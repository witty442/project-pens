package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.upload.FormFile;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.TaskStoreBean;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;
import com.pens.util.meter.MonitorTime;

public class ImportSalesOutBigCFromTextTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
   
	/*public void run(MonitorBean monitorModel){
		logger.debug("TaskName:"+monitorModel.getName());
		logger.debug("transactionId:"+monitorModel.getTransactionId());
	}*/
	
	/**
	 * Return :Param Name|Param label|Param Type|default value|validate$Button Name
	 */
	public String getParam(){
		return "dataFormFile|เลือกไฟล์|FROMFILE||VALID$Import ข้อมูล";
	}
	public String getDescription(){
		String desc = "ใช้ File นามสกุล .txt ที่ Load มาจาก EDI Web ใหม่ มาทำการ Upload <br/>";
		       desc +="(เป็นข้อมูลใช้เช็คยอดขายรายวัน ไม่ได้นำไปใช้ออกรายงานสต๊อกใดๆ) <br/>";
		return desc;
	}
	public String getDevInfo(){
		return "";
	}
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
		script +="\n   var form = document.batchTaskForm;";
	    script +="\n   var extension = '';";
		script +="\n   var startFileName = '';";
		script +="\n    if(form.dataFormFile.value.indexOf('.') > 0){";
		script +="\n       extension = form.dataFormFile.value.substring(form.dataFormFile.value.lastIndexOf('.') + 1).toLowerCase();";
		script +="\n    }";
		script +="\n    if(form.dataFormFile.value.indexOf('_') > 0){";
		script +="\n       var pathFileName = form.dataFormFile.value;";
		script +="\n       startFileName = pathFileName.substring(pathFileName.lastIndexOf('\\\\')+1,pathFileName.indexOf('_')).toLowerCase();";
		script +="\n    }";
		script +="\n    if(form.dataFormFile.value != '' && (extension == 'txt' || extension == 'txt') ){";
		script +="\n    }else{";
		script +="\n       alert('กรุณาเลือกไฟล์นามสกุล  txt  ');";
		script +="\n       return false;";
		script +="\n    }";
		script +="\n    return true";
		script +="\n }";
		script +="\n </script>";
		return script;
	}
	
	public  MonitorBean run(MonitorBean monitorModel){
		Connection conn = null;
		Connection connMonitor = null;
		BatchTaskDAO dao = new BatchTaskDAO();
		MonitorTime monitorTime = null;
		try{
			//logger.debug("RealPath:"+request.getRealPath(""));
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();

			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item ImportSalesOutBigCFromTextTask ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			
			/** Validate FileName is imported**/
			boolean isFileNameExist = isFileNameExist(connMonitor, monitorModel.getDataFile().getFileName());
			logger.debug("isFileNameExist["+monitorModel.getDataFile().getFileName()+"]:"+isFileNameExist);
			if(isFileNameExist){
				modelItem.setStatus(Constants.STATUS_FAIL);
				modelItem.setErrorMsg("มีการ Import File นี้ไปแล้ว ไม่สามารถ import ได้อีก");
				modelItem.setErrorCode("DuplicateImportFileException");
			}else{
			   /** Start process **/ 
			   modelItem = runProcess(connMonitor,conn,monitorModel,modelItem);
			}
			
			/** validate process Return**/
			
			/**debug TimeUse **/
			monitorTime.debugUsedTime();
			
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			if(modelItem.getStatus()== Constants.STATUS_SUCCESS){
			    logger.debug("Transaction commit");
			    conn.commit();
			}else{
				logger.debug("Transaction Rollback");
				conn.rollback();
			}
			
			/** Update status Head Monitor From Monitor Item Status*/
            monitorModel.setErrorMsg(modelItem.getErrorMsg());
            monitorModel.setErrorCode(modelItem.getErrorCode());
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			
			/** Update Status Monitor **/
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

	public static MonitorItemBean runProcess(Connection connMonitor ,Connection conn,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		int status = Constants.STATUS_FAIL;
		int successCount = 0;int dataCount=0;int failCount = 0;
		int r = 0;
		boolean foundError = false;boolean passValid = false;String lineMsg = "";String errorMsg = "";
		PreparedStatement ps =null;
		String lineTemp = "";
		String[] lineTempArry = null;
		String[] lineHead = new String[7];
		String[] lineStrArry = null;
		ImportDAO importDAO = new ImportDAO();
		Master mStore = null;
		String pensItem = "";
		Master mGroupType =null;Master mGroup = null;
		int index = 1;
		try{
			FormFile dataFile = monitorModel.getDataFile();
			String fileName = dataFile.getFileName();
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_SALES_FROM_BIGC_TEMP( \n");
			sql.append(" VENDOR, NAME, BARCODE,  \n");
			sql.append(" STYLE_NO, DESCRIPTION, STORE_NO,  \n");
			sql.append(" STORE_NAME, SALES_DATE, GP_PERCENT,   \n");
			sql.append(" QTY, WHOLE_PRICE_BF, RETAIL_PRICE_BF,CREATE_DATE, CREATE_USER,  \n");
			sql.append(" PENS_CUST_CODE, PENS_CUST_DESC,PENS_ITEM,    \n");
			sql.append(" SALES_YEAR ,SALES_MONTH,PENS_GROUP,PENS_GROUP_TYPE ,  \n");
			sql.append(" FILE_NAME,TOTAL_WHOLE_PRICE_BF)  \n");
			sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?) \n");
			ps = conn.prepareStatement(sql.toString());
			
			//Convert InputStream to String
			String dataString = IOUtils.toString(dataFile.getInputStream(), "UTF-8"); 
			//logger.debug("dataString:"+dataString);
			String[] dataArray = dataString.split("\\r?\\n");
			logger.debug("dataArray length["+dataArray.length+"]");
			//Mapping lineStrArray to List
			for (r = 0; r < dataArray.length ; r++) {
				lineTemp  = dataArray[r];
				if( !Utils.isNull(lineTemp).equals("") && !lineTemp.startsWith("T") ){
					if(lineTemp.startsWith("H")){
						logger.debug("lineTemp "+lineTemp);
						lineTempArry = lineTemp.split("\\|");
						lineHead[0] = lineTempArry[6]; //vendor
						lineHead[1] = lineTempArry[7]; //storeNo
						lineHead[2] = lineTempArry[2]; //salesDate
						lineHead[3] = ""+(r+1); //line head text
						
						//validate
						mStore = importDAO.getMasterMapping(conn, "Store", lineHead[1]);//by storeNo
						if(mStore != null){
						   lineHead[4] =Utils.isNull(mStore.getInterfaceDesc());//storeName 
						   lineHead[5] =Utils.isNull(mStore.getPensValue());  // PENS_CUST_CODE 
						   lineHead[6] =Utils.isNull(mStore.getPensDesc());  // PENS_CUST_DESC
						}else{
						   lineHead[4] = "";
						   lineHead[5] = "";
						   lineHead[6] = "";
						}
					}else if(lineTemp.startsWith("D")){
						logger.debug("lineTemp "+lineTemp);
						lineTempArry = lineTemp.split("\\|");
						
						lineStrArry = new String[11];
						lineStrArry[0] = Utils.isNull(lineHead[0]);//VENDOR
						lineStrArry[1] = "PENS";//NAME
						lineStrArry[2] = Utils.isNull(lineTempArry[2]);//BARCODE
						lineStrArry[3] = Utils.isNull(lineTempArry[3]);//STYLE_NO
						lineStrArry[4] = Utils.isNull(lineTempArry[4]);//DESCRIPTION
						lineStrArry[5] = Utils.isNull(lineHead[1]);//STORE_NO
						lineStrArry[6] = Utils.isNull(lineHead[2]);//SalesDate
						lineStrArry[7] = "0";//GP_PERCENT
						lineStrArry[8] = Utils.isNull(lineTempArry[7]);//QTY
						lineStrArry[9] = "0";//WHOLE_PRICE_BF
						lineStrArry[10] = Utils.isNull(lineTempArry[5]);//RETAIL_PRICE_BF
						
					    //find pensItem
					    pensItem = importDAO.getItemByBarcodeTypeBigC(conn,"BigCitem", lineStrArry[2]);//by barcode
					  
					    //Reset Value By Line Loop
						lineMsg = "";
						errorMsg = "";
						passValid = true;
						dataCount++;

						lineMsg =  lineHead[3]+"|"+(r+1)+"|"+lineStrArry[0]+"|"+lineStrArry[1]+"|"+lineStrArry[2]+"|"+lineStrArry[3]+"|"+lineStrArry[4]+"|";
						lineMsg += lineStrArry[5]+"|"+lineStrArry[6]+"|"+lineStrArry[7]+"|"+lineStrArry[8]+"|"+lineStrArry[9]+"|";
						lineMsg += lineStrArry[10]+"|";
						
						//validate StoreName is not found error
						if(Utils.isNull( lineHead[4]).equals("")){
							errorMsg ="ไม่พบข้อมูล Store Name";
							passValid  =false;
							lineMsg += "|";
						}else{
							lineMsg += Utils.isNull( lineHead[4])+"|";
						}
						//validate pensItem is not found error
						if(Utils.isNull(pensItem).equals("")){
							errorMsg +=",ไม่พบข้อมูล pensItem";
							passValid  =false;
							lineMsg += "|";
						}else{
							lineMsg += Utils.isNull(pensItem)+"|";
						}

						logger.debug("lineMsg:"+lineMsg);
						//is valid
						if(passValid){
						   dataCount++;
						   successCount++;
						   lineMsg +="SUCCESS";
						  //Insert Log
						   insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
							
						   //set prepare statement
						   ps.setString(index++, lineStrArry[0]);//VENDOR
						   ps.setString(index++, lineStrArry[1]);//NAME
						   ps.setString(index++, lineStrArry[2]);//BARCODE
						   ps.setString(index++, lineStrArry[3]);//STYLE_NO
						   ps.setString(index++, lineStrArry[4]);//DESCRIPTION
						   ps.setString(index++, lineStrArry[5]);//STORE_NO
						   ps.setString(index++, Utils.isNull( lineHead[4]));//STORE_NAME
						   ps.setDate(index++, new java.sql.Date(DateUtil.parse(lineStrArry[6],DateUtil.YYYY_MM_DD_WITHOUT_SLASH).getTime()));//SALES_DATE
						   ps.setDouble(index++, Utils.convertStrToDouble(lineStrArry[7]));//GP_PERCENT
						   ps.setInt(index++, Utils.convertStrToInt(lineStrArry[8]));//QTY
						   ps.setDouble(index++, Utils.convertStrToDouble(lineStrArry[9]));//WHOLE_PRICE_BF
						   ps.setDouble(index++, Utils.convertStrToDouble(lineStrArry[10]));//RETAIL_PRICE_BF
						   ps.setTimestamp(index++, new java.sql.Timestamp(new java.util.Date().getTime())); //CREATE_DATE
					       ps.setString(index++, monitorModel.getCreateUser()); //CREATE_USER
					       ps.setString(index++, Utils.isNull( lineHead[5]));  // PENS_CUST_CODE VARCHAR2(30)
						   ps.setString(index++, Utils.isNull( lineHead[6]));  // PENS_CUST_DESC VARCHAR2(100)
					       ps.setString(index++, Utils.isNull(pensItem)); //Pens_item
					       ps.setString(index++, lineStrArry[6].substring(0,4)); // SALES_YEAR VARCHAR2(10)
					       ps.setString(index++, lineStrArry[6].substring(4,6)); //SALES_MONTH VARCHAR2(10)
					          
					       mGroupType = importDAO.getMasterByBarcodeTypeBigC(conn, lineStrArry[2]);
					       logger.debug("mGroupType:"+mGroupType);
					       mGroup = mGroupType!=null? importDAO.getMasterMapping(conn, "Group", Utils.isNull(mGroupType.getPensDesc2())) :null;
					 
					       ps.setString(index++, mGroup!=null?Utils.isNull(mGroup.getPensValue()):"");//PENS_GROUP
					       ps.setString(index++, mGroupType!=null?Utils.isNull(mGroupType.getPensDesc2()):"");//PENS_GROUP_TYPE
					       ps.setString(index++, fileName);// File_name VARCHAR2(100),
					       ps.setDouble(index++, Utils.convertStrToInt(lineStrArry[8]) *Utils.convertStrToDouble(lineStrArry[9]));//TOTAL_WHOLE_PRICE_BF =QTY * WHOLE_PRICE_BF
					       
					       ps.addBatch();
					       
					       index=1;
						}else{
							dataCount++;
							failCount++;
							foundError = true;
							lineMsg +=errorMsg;
							
							//Insert Log
						    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
						}//if
						
					}//if(D) line Str
				}//if (T)

    	   }//for row
			
			//Insert Column For Display Result
			 lineMsg ="";
			 lineMsg += "No|Line(H)|Line(D)|VENDOR|NAME|BARCODE|STYLE_NO|DESCRIPTION|STORE_NO|";
			 lineMsg +=	"SALES_DATE|GP_PERCENT|QTY|WHOLE_PRICE_BF|RETAIL_PRICE_BF|STORE_NAME|PENS_ITEM|Message";
			 
			 insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
			 
			if(foundError ==false){
				ps.executeBatch();
				status = Constants.STATUS_SUCCESS;
			}
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);//successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return monitorItemBean;	
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

	public static boolean isFileNameExist(Connection conn,String fileName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean isExist = false;
		try {
			sql.append(" select count(*) as c from PENSBI.PENSBME_SALES_FROM_BIGC_TEMP WHERE  lower(file_name) ='"+Utils.isNull(fileName).toLowerCase()+"' \n");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			  if(rst.getInt("c") >0){
				  isExist = true;
			  }
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return isExist;
	}
	
	public static void debug(Map<String,Order> orderSaveMap){
		//debug
		Iterator its = orderSaveMap.keySet().iterator();
		while(its.hasNext()){
		   String key = (String)its.next();
		   Order o = orderSaveMap.get(key); 
		   logger.debug("DEBUG StoreCode["+key+"]orderNo["+o.getQty()+"]");
		}
	}
}
