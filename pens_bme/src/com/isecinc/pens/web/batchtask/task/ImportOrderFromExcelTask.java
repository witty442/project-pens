package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;

import meter.MonitorTime;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.upload.FormFile;

import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;

public class ImportOrderFromExcelTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
	private static String PARAM_AS_OF_DATE = "AS_OF_DATE";
   
	/*public void run(MonitorBean monitorModel){
		logger.debug("TaskName:"+monitorModel.getName());
		logger.debug("transactionId:"+monitorModel.getTransactionId());
	}*/
	
	/**
	 * Return :Param Name|Param label|Param Type|default value|validate$Button Name
	 */
	public String getParam(){
		return "AS_OF_DATE|วันที่เปิด Order Big-C จากโรงงาน Wacoal|DATE|SYSDATE|VALID,DATA_FILE|เลือกไฟล์|FROMFILE||VALID$Import ข้อมูล";
	}
	
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
		script +="\n var form = document.batchTaskForm;";
		script +="\n var extension = '';";
		script +="\n var startFileName = '';";
		script +="\n  if(form.dataFile.value.indexOf('.') > 0){";
		script +="\n    extension = form.dataFile.value.substring(form.dataFile.value.lastIndexOf('.') + 1).toLowerCase();";
		script +="\n   //alert(extension);";
		script +="\n  }";
		script +="\n  if(form.dataFile.value.indexOf('_') > 0){";
		script +="\n    var pathFileName = form.dataFile.value;";
		script +="\n    //alert(pathFileName +','+pathFileName.lastIndexOf('\\'));";
		script +="\n    startFileName = pathFileName.substring(pathFileName.lastIndexOf('\\')+1,pathFileName.indexOf('_')).toLowerCase();";
		script +="\n    //alert(startFileName);";
		script +="\n  }";
		
		script +="\n  if(form.dataFile.value != '' && (extension == 'xls' || extension == 'xlsx') ){";
		script +="\n  }else{";
		script +="\n    alert('กรุณาเลือกไฟล์นามสกุล  xls หรือ  xlsx ');";
		script +="\n    return false;";
		script +="\n  }";
		script +="\n  return true";
		script +="\n }";
		script +="\n </script>";
		
		return script;
	}
	
	public  MonitorBean run(MonitorBean monitorModel){
		Connection conn = null;
		Connection connMonitor = null;
		BatchTaskDAO dao = new BatchTaskDAO();
		EnvProperties env = EnvProperties.getInstance();
		MonitorTime monitorTime = null;
		try{
			//logger.debug("RealPath:"+request.getRealPath(""));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();

			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item BigC PO ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			
			//Validate Duplication
			if(isImportExist(conn,monitorModel)){
				 modelItem.setStatus(Constants.STATUS_FAIL);
				 modelItem.setErrorMsg("ได้เคย Import ข้อมูลวันที่ระบุ ไปก่อนหน้านี้แล้ว");
				 modelItem.setErrorCode("ImportException");
			}else{
				//Process Generate 
				modelItem = runProcess(conn,monitorModel,modelItem);
				/** validate data **/
	            if(modelItem.getFailCount() ==0 && modelItem.getSuccessCount()==0 ){
	 			    modelItem.setErrorMsg("ไม่พบข้อมูลการเปิด Order Big-C ในวันที่ระบุ");
	 			    modelItem.setErrorCode("DataNotFoundException");
	            }else{
	            	modelItem.setErrorCode("SuccessException"); 
	            }
			}
			/**debug TimeUse **/
			monitorTime.debugUsedTime();
			
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			if(modelItem.getStatus()== Constants.STATUS_SUCCESS){
			    logger.debug("Transaction commit");
			    conn.commit();
			}else{
				logger.debug("Transaction Rolback");
				conn.rollback();
			}
			
			/** Update status Head Monitor */
            monitorModel.setErrorMsg(modelItem.getErrorMsg());
            monitorModel.setErrorCode(modelItem.getErrorCode());
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			/** Update Monitor **/
			dao.updateMonitor(connMonitor,monitorModel);

		}catch(Exception e){
			try{
				logger.error(e.getMessage(),e);
				
				/** End process ***/
				logger.debug("Update Monitor to Fail ");
				monitorModel.setStatus(Constants.STATUS_FAIL);
				monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
				monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
				
				dao.updateMonitorCaseError(connMonitor,monitorModel);
	
				//clear Task running for next run
				dao.updateControlMonitor(new BigDecimal(0),monitorModel.getName());
				
				if(conn != null){
				   logger.debug("Transaction Rolback");
				   conn.rollback();
				}
			}catch(Exception ee){}
		}finally{
		   try{
				if(conn != null){
					conn.setAutoCommit(true);
					conn.close();
					conn =null;
				}
				if(connMonitor != null){
					connMonitor.close();
					connMonitor=null;
				}
		   }catch(Exception ee){}
		}
		return monitorModel;
	}
	
	public static MonitorItemBean prepareMonitorItemBean(MonitorBean monitorModel) throws Exception{
		MonitorItemBean modelItem = new MonitorItemBean();
		modelItem.setMonitorId(monitorModel.getMonitorId());
		modelItem.setSource("SALES");
		modelItem.setDestination("ORACLE");
		modelItem.setStatus(Constants.STATUS_START);
		modelItem.setSubmitDate(new Date());
		modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
		
		return modelItem;
	}
	
	public static boolean isImportExist(Connection conn,MonitorBean monitorModel) {
		ResultSet rs = null;
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		boolean exist = false;
		Map<String, String> batchParamMap = monitorModel.getBatchParamMap();
		try{
			String asOfDate =Utils.stringValue(Utils.parse(batchParamMap.get(PARAM_AS_OF_DATE),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th),Utils.DD_MM_YYYY_WITH_SLASH) ;

			sql.append("\n SELECT count(*) as c");
			sql.append("\n FROM PENSBME_ORDER O");
			sql.append("\n WHERE O.ORDER_DATE  = TO_DATE('"+asOfDate+"','dd/mm/yyyy')");
			sql.append("\n AND ( O.ORDER_LOT_NO IS  NOT NULL OR ORDER_LOT_NO <> '')");
			sql.append("\n AND O.ORDER_LOT_NO IN(  ");
			sql.append("\n   SELECT ORDER_NUMBER FROM XXPENS_PO_ORDER_IMPORT_MST ");
			sql.append("\n  ) ");
			
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());

			rs = ps.executeQuery();
			if(rs.next()){
			   if(rs.getInt("c") >0){
				   exist = true;
			   }
			}//while	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception e){}
		}
		return exist;	
	}
	
	public static MonitorItemBean runProcess(Connection conn,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		ResultSet rs = null;
		PreparedStatement ps =null;
		PreparedStatement psIns =null;
		StringBuffer sql = new StringBuffer("");
		int status = Constants.STATUS_FAIL;
		boolean foundData = false;
		int successCount = 0;
		int failCount = 0;
		int index = 0;
		BigDecimal headId = null;
		Map<String, String> batchParamMap = monitorModel.getBatchParamMap();
		try{
			FormFile dataFile = monitorModel.getDataFile();
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
			int sheetNo = 0; // xls sheet no. or name
			int rowNo = 4; // row of begin data
			int maxColumnNo = 26; // max column of data per row
			
			Workbook wb1 = null;
			XSSFWorkbook wb2 = null;
			Sheet sheet = null;
			
			if("xls".equalsIgnoreCase(fileType)){
			   wb1 = new HSSFWorkbook(dataFile.getInputStream());//97-2003
			   sheet = wb1.getSheetAt(sheetNo);
			   logger.debug("number of sheet: " + wb1.getNumberOfSheets());
			}else{
			   OPCPackage pkg = OPCPackage.open(dataFile.getInputStream());
			   wb2 = new XSSFWorkbook(pkg);
			   sheet = wb2.getSheetAt(sheetNo);
			   logger.debug("number of sheet: " + wb2.getNumberOfSheets());
			}
			
			Row row = null;
			Cell cell = null;
		
	
			if(foundData){
				status = Constants.STATUS_SUCCESS;
			}
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);//successCount);
			monitorItemBean.setFailCount(failCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception e){}
		}
		return monitorItemBean;	
	}
	
	public static int insertLine(Connection conn,BigDecimal headId, String orderLotNo) throws Exception{
		ResultSet rs = null;
		PreparedStatement ps =null;
		PreparedStatement psIns =null;
		StringBuffer sql = new StringBuffer("");
		int successCount = 0;
		int index = 0;
		int lineId = 0;	
		try{
			String sqlIn = " insert into XXPENS_PO_ORDER_IMPORT_DT \n";
			sqlIn +=" ( created_by, creation_date,last_updated_by, last_update_date,last_update_login, \n";
			sqlIn +="   header_id,line_id,item_no , qty,group_code )\n ";
			sqlIn += " values(?,?,?,? ,?,?,?,? ,?,?)";
			psIns = conn.prepareStatement(sqlIn);
			
			sql.append("\n SELECT  ");
			sql.append("\n O.ORDER_LOT_NO,O.ITEM ,O.GROUP_CODE");
			sql.append("\n ,NVL(SUM(O.QTY),0) as QTY");
			sql.append("\n FROM PENSBME_ORDER O");
			sql.append("\n WHERE O.ORDER_LOT_NO ='"+orderLotNo+"'");
			sql.append("\n GROUP BY O.ORDER_LOT_NO,O.ITEM,O.GROUP_CODE ");
			sql.append("\n ORDER BY O.GROUP_CODE ,O.ITEM");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				index = 0;
				lineId++;
				
				psIns.setInt(++index, 2408);
				psIns.setTimestamp(++index,new java.sql.Timestamp(new Date().getTime()));
				psIns.setInt(++index, 2408);
				psIns.setTimestamp(++index,new java.sql.Timestamp(new Date().getTime()));
				psIns.setDouble(++index, -1);
				
				psIns.setBigDecimal(++index, headId);//headId
				psIns.setInt(++index, lineId);//lineId
				psIns.setString(++index, Utils.isNull(rs.getString("item")));
				psIns.setInt(++index, rs.getInt("qty"));
				psIns.setString(++index, Utils.isNull(rs.getString("group_code")));
				
				psIns.executeUpdate();
				successCount++;
			}//while

		}catch(Exception e){
		  throw e;
		}finally{
			try{
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception e){}
		}
		return successCount;	
	}
}
