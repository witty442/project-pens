package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import meter.MonitorTime;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.InterfaceHelper;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

public class ExportBillICC extends InterfaceUtils{
	private static Logger logger = Logger.getLogger("PENS");
	public static String PATH_CONTROL ="inf-config/table-mapping-export-icc/";
	public static String FILE_CONTROL_NAME ="control_export.csv";
	
	public static void runProcess(User user,MonitorBean m) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap<String,TableBean> initConfigMap = new LinkedHashMap<String,TableBean>();
		ExportICCProcess exICCProcess = new ExportICCProcess();
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		boolean rollBackFlag = false;
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		EnvProperties env = EnvProperties.getInstance();
		int countFileMap = 0;
		MonitorTime monitorTime = null;
		Map<String, String> batchParamMap = new HashMap<String, String>();
		try{
			/** prepare Paramenter **/
			batchParamMap = m.getBatchParamMap();
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			monitorModel.setTransactionId(m.getTransactionId());
			monitorModel.setMonitorId(m.getMonitorId());
			monitorModel.setTransactionType(m.getTransactionType());
			
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.icc.ip.server"), env.getProperty("ftp.icc.username"), env.getProperty("ftp.icc.password"));
			ftpManager.canConnectFTPServer();
			
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			
			monitorTime = new MonitorTime("initImportConfig");
			InterfaceHelper.initExportConfigICC(PATH_CONTROL,FILE_CONTROL_NAME,initConfigMap,conn,user,batchParamMap);
			monitorTime.debugUsedTime();
			
			/**Validate Duplicate File**/
			taskStatusInt = exICCProcess.validateFileExport(connMonitor, monitorModel, initConfigMap, batchParamMap);
			if(taskStatusInt == Constants.STATUS_FAIL){
				monitorModel.setErrorCode("DuplicateExportFileException");
				monitorModel.setErrorMsg("ไม่สามารถ Export file นี้ได้เนื่องจาก มีการ Exportไปแล้ว");
			}
			
			/** Validate Oracle invoice no **/
			if(taskStatusInt != Constants.STATUS_FAIL){
				taskStatusInt = exICCProcess.validateOrcaleInvoice(connMonitor, monitorModel, initConfigMap, batchParamMap);
				if(taskStatusInt == Constants.STATUS_FAIL){
					monitorModel.setErrorCode("OracleInvoiceNoEmtpyException");
					monitorModel.setErrorMsg("ไม่สามารถ Export file นี้ได้เนื่องจาก ยังไม่มีการบันทึก oracle invoice no");
				}
			}
			
			monitorTime = new MonitorTime("Export Text by Table");
			
			if(taskStatusInt != Constants.STATUS_FAIL){
				Set s = initConfigMap.keySet();
				Iterator it = s.iterator();
				for (int i = 1; it.hasNext(); i++) { //for 1
					
					String tableName = (String) it.next();
					TableBean tableBean = (TableBean) initConfigMap.get(tableName);
					logger.debug("Export TableName:"+tableBean.getTableName());
	        
					/** insert to monitor_item **/
					logger.debug("Insert Monitor Item  TableName:"+tableBean.getTableName());
					MonitorItemBean modelItem = new MonitorItemBean();
					modelItem.setMonitorId(monitorModel.getMonitorId());
					modelItem.setSource(tableBean.getSource());
					modelItem.setDestination(tableBean.getDestination());
					modelItem.setTableName(tableBean.getTableName());
					modelItem.setFileName(tableBean.getFileFtpName());
					modelItem.setStatus(Constants.STATUS_START);
					modelItem.setSubmitDate(new Date());
					
					tableBean = exICCProcess.exportBillICC(conn, tableBean, user,batchParamMap);
					
					//Export To FTP 
					if( !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){
					 	logger.debug("Step Upload ALL File To FTP Server");
						ftpManager.uploadAllFileToFTP_OPT2_BY_FILE(env.getProperty("path.icc.hisher.export.iccbill"),tableBean.getFileFtpName(), tableBean.getDataStrExport());
					     
					     //Update Exported ='Y' in BME_ORDER
						if("PENSBME_ICC_HEAD".equalsIgnoreCase(tableName)){
					       logger.debug("Update Exported ='Y' in PENSBME_ICC_HEAD");
					        int updateCount = updateIccHeadExportFlag(conn,user.getUserName(),batchParamMap);
					        logger.debug("updateCount:"+updateCount);
						}
						modelItem.setStatus(Constants.STATUS_SUCCESS);
						
						/** Update Monitor Item To Success **/
						modelItem.setDataCount(tableBean.getExportCount());
						modelItem.setSuccessCount(tableBean.getExportCount());
						modelItem.setErrorMsg("");
						modelItem.setErrorCode("");
						
					}else{
						taskStatusInt = Constants.STATUS_FAIL;
						modelItem.setStatus(taskStatusInt);
						modelItem.setErrorMsg("ไม่พบข้อมูลที่จะ Generate");
						modelItem.setErrorCode("DataNotFoundException");
						
						monitorModel.setErrorMsg("ไม่พบข้อมูลที่จะ Generate");
		            	monitorModel.setErrorCode("DataNotFoundException");
					}
					
					isExc = true;
					modelItem = dao.insertMonitorItem(connMonitor,modelItem);
					
				}//for 2
				monitorTime.debugUsedTime();	
			}//if
			
			logger.debug("isExc:"+isExc+" ,rollBackFlag:"+rollBackFlag);
		
			logger.debug("Transaction commit");
			conn.commit();
				
			/** End process ***/
			logger.debug("Update Monitor to Success :"+taskStatusInt);
			monitorModel.setStatus(taskStatusInt);
			monitorModel.setFileCount(countFileMap);
			monitorModel.setTransactionType(m.getTransactionType());
				
			dao.updateMonitor(connMonitor,monitorModel);

		}catch(Exception e){
			logger.error(e.getMessage(),e);
			
			/** End process ***/
			logger.debug("Update Monitor to Fail ");
			monitorModel.setStatus(Constants.STATUS_FAIL);
			monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
			monitorModel.setFileCount(countFileMap);
			monitorModel.setTransactionType(m.getTransactionType());
			monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
			
			dao.updateMonitorCaseError(connMonitor,monitorModel);

			//clear Task running for next run
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_EXPORT_BILL_ICC);
			
			if(conn != null){
			  logger.debug("Transaction Rolback");
			  conn.rollback();
			}
		
		}finally{
			if(initConfigMap != null){
				initConfigMap.clear();
				initConfigMap = null;
			}
			if(conn != null){
				conn.setAutoCommit(true);
				conn.close();
				conn =null;
			}
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
			logger.debug("initConfigMap:"+initConfigMap);
			logger.debug("conn:"+conn);
		}
	}
	
	private static int  updateIccHeadExportFlag(Connection conn,String userName,Map<String, String> batchParamMap) throws Exception {
		PreparedStatement ps = null;
		int updateCount = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			String billDateStr  = batchParamMap.get("TRANS_DATE").replaceAll("/", "");
			
			/** Set Data For Update InterfacesFlag **/
			sql.append("\n UPDATE PENSBME_ICC_HEAD O  ");
			sql.append("\n SET interface_icc ='Y' ,update_user ='"+userName+"' ,update_date = sysdate");
			sql.append("\n where 1=1 ");
			sql.append("\n and O.bill_date = '"+billDateStr+"'");
			sql.append("\n and (O.interface_icc = 'N' or O.interface_icc is null)	 ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			updateCount = ps.executeUpdate();
			
			return updateCount;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	
}
