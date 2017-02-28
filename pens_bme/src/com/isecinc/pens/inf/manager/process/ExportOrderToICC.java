package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import meter.MonitorTime;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.ColumnBean;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.InterfaceHelper;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

public class ExportOrderToICC extends InterfaceUtils{
	private static Logger logger = Logger.getLogger("PENS");
	public static String PATH_CONTROL ="inf-config/table-mapping-export-icc/";
	public static String FILE_CONTROL_NAME ="control_export.csv";
	public final static String PARAM_TRANS_DATE = "TRANS_DATE";//Budish Date
	public final static String PARAM_PRODUCT_TYPE = "PRODUCT_TYPE";
	
	public static void runProcess(User user,MonitorBean m) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		LinkedHashMap<String,TableBean> initConfigMap = new LinkedHashMap<String,TableBean>();
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
			taskStatusInt = validateFileExport(connMonitor, monitorModel, initConfigMap, batchParamMap);
			if(taskStatusInt == Constants.STATUS_FAIL){
				monitorModel.setErrorCode("DuplicateExportFileException");
				monitorModel.setErrorMsg("ไม่สามารถ Export file นี้ได้เนื่องจาก มีการ Exportไปแล้ว");
			}
			
			/** Validate Oracle invoice no **/
			if(taskStatusInt != Constants.STATUS_FAIL){
				taskStatusInt = validateOrcaleInvoice(connMonitor, monitorModel, initConfigMap, batchParamMap);
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
					
					tableBean = exportBillICC(conn, tableBean, user,batchParamMap);
					
					//Export To FTP 
					if( !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){
					 	logger.debug("Step Upload ALL File To FTP Server");        
					 	String pathFull = env.getProperty("path.icc.hisher.export.iccbill")+"/"+tableBean.getFileFtpName();
						ftpManager.uploadAllFileToFTP_OPT2_BY_FILE(env.getProperty("path.icc.hisher.export.iccbill"),pathFull, tableBean.getDataStrExport());
					     
						//Backup file to DD Server
						String ddServerPath =env.getProperty("path.backup.icc.hisher.export.iccbill")+"/"+tableBean.getFileFtpName();
					    logger.debug("Backup Text File:"+ddServerPath);
					    FileUtil.writeFile(ddServerPath, tableBean.getDataStrExport(), "TIS-620");
					    
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
						modelItem.setTotalQty(tableBean.getTotalQty());
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
	/**
	 * importToDB
	 * @param conn
	 * @param initConfigMap
	 * @param tableBean
	 * @param dataTextLineArr
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	
	public static TableBean exportBillICC(Connection conn,TableBean tableBean,User userBean,Map<String, String> batchParamMap) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer dataAll = new StringBuffer("");
		StringBuffer dataAppend = new StringBuffer("");
        int i = 0;
        int totalRows = 0;
        int totalQty = 0;
        String lineStr = "";
		try{
            logger.debug("Select:"+tableBean.getPrepareSqlSelect());
			ps = conn.prepareStatement(tableBean.getPrepareSqlSelect());
			rs = ps.executeQuery();
			while(rs.next()){
				totalRows++;
				lineStr = "";
				//Add Order Header
				for(i=0;i<tableBean.getColumnBeanList().size();i++){
					ColumnBean colBean = (ColumnBean)tableBean.getColumnBeanList().get(i);
					
					lineStr += ExportHelper.covertToFormatExport(colBean,rs);
					
				}//for
				/** Add New Line **/
				logger.debug("Line length:"+lineStr.length());
				lineStr += Constants.newLine;//new line
				dataAppend.append(lineStr);
				
				//Sum Total qty
				if("PENSBME_ICC_DLYR".equalsIgnoreCase(tableBean.getTableName())){
					totalQty += rs.getInt("qty");
				}
			}//while
			
			logger.debug("totalRows:"+totalRows);
			//Add Total record
			if(totalRows >0){
			   dataAll.append(InterfaceUtils.appendNumLeft(String.valueOf(totalRows),"0",10)+"\n");
			   dataAll.append(dataAppend);
			}
			
			tableBean.setExportCount(totalRows);
			tableBean.setDataStrExport(dataAll);
			tableBean.setTotalQty(totalQty);
			return tableBean;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			if(rs != null){
				rs.close();rs= null;
			}
		}
	}
	
	public static int validateFileExport(Connection connMonitor,MonitorBean monitorModel,LinkedHashMap<String,TableBean> controlTableMap,Map<String, String> batchParamMap) {
		String fileNameFixByDate = "";
		InterfaceDAO dao = new InterfaceDAO();
		int taskStatusInt = Constants.STATUS_SUCCESS;
		try{
			String billDateStr  = batchParamMap.get(PARAM_TRANS_DATE).replaceAll("/", "");
			String productType  = batchParamMap.get(PARAM_PRODUCT_TYPE);
			
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			for (int j = 1; it.hasNext(); j++) {
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(tableName);
				fileNameFixByDate = InterfaceUtils.genExportNameICC(productType,tableName, batchParamMap);
				
				MonitorItemBean modelItem = new MonitorItemBean();
				modelItem.setMonitorId(monitorModel.getMonitorId());
				modelItem.setSource(tableBean.getSource());
				modelItem.setDestination(tableBean.getDestination());
				modelItem.setTableName(tableBean.getTableName());
				modelItem.setFileName(fileNameFixByDate);
				modelItem.setDataCount(0);
				modelItem.setFileSize("");
				modelItem.setSubmitDate(new Date());
				
				if(isExportByTransDate(connMonitor, tableName, billDateStr)){
					modelItem.setStatus(Constants.STATUS_FAIL);
					modelItem.setSuccessCount(0);
					modelItem.setErrorMsg("ไม่สามารถ Export file นี้ได้เนื่องจาก มีการ Exportไปแล้ว ");
					modelItem.setErrorCode("DuplicateExportFileException");
					
					dao.insertMonitorItem(connMonitor,modelItem);
					
					taskStatusInt = Constants.STATUS_FAIL;
				}

			}
		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return taskStatusInt;
	}
	
	public static boolean isExportByTransDate(Connection conn,String tableName,String transDate) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean exist = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(*) as c from pensbme_icc_head where bill_date ='"+transDate+"' and interface_icc ='Y' \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") >0){
				   exist = true;
				}
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return exist;
	} 
	
	public static int validateOrcaleInvoice(Connection connMonitor,MonitorBean monitorModel,LinkedHashMap<String,TableBean> controlTableMap,Map<String, String> batchParamMap) {
		String fileNameFixByDate = "";
		InterfaceDAO dao = new InterfaceDAO();
		int taskStatusInt = Constants.STATUS_SUCCESS;
		try{
			String billDateStr  = batchParamMap.get(PARAM_TRANS_DATE).replaceAll("/", "");
			String productType  = batchParamMap.get(PARAM_PRODUCT_TYPE);
			
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			for (int j = 1; it.hasNext(); j++) {
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(tableName);
				fileNameFixByDate = InterfaceUtils.genExportNameICC(productType,tableName, batchParamMap);
				
				MonitorItemBean modelItem = new MonitorItemBean();
				modelItem.setMonitorId(monitorModel.getMonitorId());
				modelItem.setSource(tableBean.getSource());
				modelItem.setDestination(tableBean.getDestination());
				modelItem.setTableName(tableBean.getTableName());
				modelItem.setFileName(fileNameFixByDate);
				modelItem.setDataCount(0);
				modelItem.setFileSize("");
				modelItem.setSubmitDate(new Date());
				
				if(isFoundOracleInvoiceNoEmty(connMonitor, tableName, billDateStr)){
					modelItem.setStatus(Constants.STATUS_FAIL);
					modelItem.setSuccessCount(0);
					modelItem.setErrorMsg("ไม่สามารถ Export file นี้ได้เนื่องจาก ยังไม่มีการบันทึก oracle invoice no");
					modelItem.setErrorCode("OracleInvoiceNoEmtpyException");
					
					dao.insertMonitorItem(connMonitor,modelItem);
					
					taskStatusInt = Constants.STATUS_FAIL;
				}

			}
		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return taskStatusInt;
	}
	
	public static boolean isFoundOracleInvoiceNoEmty(Connection conn,String tableName,String transDate) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean exist = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(*) as c from pensbme_icc_head where bill_date ='"+transDate+"' and (oracle_invoice_no is null or trim(oracle_invoice_no) ='') \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") >0){
				   exist = true;
				}
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return exist;
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
