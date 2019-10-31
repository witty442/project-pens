package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.upload.FormFile;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.dao.MasterDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class ImportBarcodeMasterFromExcelTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
	private static String PARAM_DATA_TYPE = "dataType";
   
	/**
	 * Return :P Name|P label|P Type|default value|valid,P2...$Button Name
	*/
	public String getParam(){
		return "dataType|ระบุประเภทกลุ่ม Barcode|LIST||VALID,dataFormFile|เลือกไฟล์|FROMFILE||VALID$Import ข้อมูล";
	}
	public String getDescription(){
		return "Import File Excel";
	}
	public String getDevInfo(){
		return "PENSBI.PENSBME_MST_REFERENCE";
	}
	public boolean isDispDetail(){
		return true;
	}
	public List<BatchTaskListBean> getParamListBox(){
		List<BatchTaskListBean> listAll = new ArrayList<BatchTaskListBean>();
		
		//LIST 1
		BatchTaskListBean listHeadBean = new BatchTaskListBean();
		List<BatchTaskListBean> listBoxData = new ArrayList<BatchTaskListBean>();
		listBoxData.add(new BatchTaskListBean("",""));
		listBoxData.add(new BatchTaskListBean("LotusItem","LotusItem"));
		listBoxData.add(new BatchTaskListBean("FridayItem","FridayItem"));
		
		listHeadBean.setListBoxName("LIST");
		listHeadBean.setListBoxData(listBoxData);
		listAll.add(listHeadBean);
		
		//LIST 2
		/*listHeadBean = new BatchTaskListBean();
		listBoxData = new ArrayList<BatchTaskListBean>();
		listBoxData.add(new BatchTaskListBean("",""));
		listBoxData.add(new BatchTaskListBean("xxx","xxx"));
		listBoxData.add(new BatchTaskListBean("yyy","yyy"));
		
		listHeadBean.setListBoxName("LIST_2");
		listHeadBean.setListBoxData(listBoxData);
		listAll.add(listHeadBean);*/
		
		return listAll;
	}
	
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
		script +="\n   var form = document.batchTaskForm;";
		script +="\n   if(form.dataType.value ==''){";
		script +="\n     alert('กรุณา ระบุประเภทกลุ่ม Barcode');";
		script +="\n     form.dataType.focus();";
		script +="\n     return false;";
		script +="\n   }";
	    script +="\n   var extension = '';";
		script +="\n   var startFileName = '';";
		script +="\n   if(form.dataFormFile.value.indexOf('.') > 0){";
		script +="\n       extension = form.dataFormFile.value.substring(form.dataFormFile.value.lastIndexOf('.') + 1).toLowerCase();";
		script +="\n   }";
		script +="\n   if(form.dataFormFile.value.indexOf('_') > 0){";
		script +="\n       var pathFileName = form.dataFormFile.value;";
		script +="\n       startFileName = pathFileName.substring(pathFileName.lastIndexOf('\\\\')+1,pathFileName.indexOf('_')).toLowerCase();";
		script +="\n   }";
		script +="\n   if(form.dataFormFile.value != '' && (extension == 'xls' || extension == 'xlsx') ){";
		script +="\n   }else{";
		script +="\n       alert('กรุณาเลือกไฟล์นามสกุล  xls หรือ  xlsx ');";
		script +="\n       return false;";
		script +="\n   }";
	
		script +="\n   return true";
		script +="\n }";
	
		script +="\n </script>";
		return script;
	}
	
	/** Run Process **/
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
			logger.debug("Insert Monitor Item ImportOrderFromExcelTask ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			
			/** Start process **/ 
			modelItem = runProcess(connMonitor,conn,monitorModel,modelItem);
			
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

	public static MonitorItemBean runProcess(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		int status = Constants.STATUS_FAIL;int dataCount=0;
		int successCount = 0;int failCount = 0;int no = 0;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		MasterDAO masterDAO = new MasterDAO(); 
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		int maxColumnNo = 5; // max column of data per row
		boolean passValid = false;String lineMsg = "";
		int colNo = 0 ;int r = 0;
		boolean foundError = false;
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    String groupType = "";
	    String pensItem = "";
	    String mat = "";
	    String barcode = "";
	    String groupCode = "";
		try{
			//Get Parameter Value
			String dataType =Utils.isNull(monitorModel.getBatchParamMap().get(PARAM_DATA_TYPE)) ;
			FormFile dataFile = monitorModel.getDataFile();
			
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
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

		    /** Validate dataType != data col DataType */
			//row = sheet.getRow(1);
			//cellCheck = row.getCell((short) 0);
		    //cellCheckValue = xslUtils.getCellValue(0, cellCheck);
		    
		    /** Loop Row **/
			for (r = rowNo; r <=sheet.getLastRowNum() ; r++) {
				row = sheet.getRow(r);
				//logger.debug("read row["+r+"]");
				
				//Check Stop Loop Col 1
				if(row !=null){
				   cellCheck = row.getCell((short) 0);
			       cellCheckValue = xslUtils.getCellValue(0, cellCheck);
			       if(cellCheckValue != null){
				      //logger.debug("RowCheck[c,r][0,"+r+"]value["+Utils.isNull(cellCheckValue.toString())+"]");
			       }
				}else{
				   cellCheckValue = null;
				}
				
				if( cellCheckValue != null && !Utils.isNull(cellCheckValue.toString()).equals("")){
					//Clear Value By Row
					passValid = false;
					lineMsg = "";
				}else{
					logger.debug("Break :rowCheck Not Found");
				    break;	
				}
				
			    /** Loop Column **/
				for (colNo = 0; colNo < maxColumnNo; colNo++) {
				//	logger.debug("read row["+r+"]colNo["+colNo+"]");
					cell = row.getCell((short) colNo);
					
					if(cell == null){
					  logger.debug("last column :"+colNo);
					  break;
					}
					
					cellObjValue = xslUtils.getCellValue(colNo, cell);
					//logger.debug("row["+i+"]col["+colNo+"]Type["+cell.getCellType()+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
					
					if(colNo ==0){
						groupType =cellObjValue.toString();
					}
					if(colNo ==1){
						 pensItem = Utils.convertDoubleToStr(Utils.isDoubleNull(cellObjValue));
					}
					if(colNo ==2){
						mat =cellObjValue.toString();
					}
					if(colNo ==3){
						barcode = Utils.convertDoubleToStr(Utils.isDoubleNull(cellObjValue));
					}
					if(colNo ==4){
						groupCode =cellObjValue.toString();
					}
					
				}//for colNo
				
				//Reset Value By Line Loop
				lineMsg = "";
				passValid = true;
				dataCount++;
				
				lineMsg = (r+1)+"|"+groupType+"|"+pensItem+"|"+mat+"|"+barcode+"|"+groupCode+"|";
				
				/** Validate Group Type **/
				logger.debug("dataType:"+dataType);
			  
			    if( !dataType.equals(String.valueOf(groupType)))  { 
			    	lineMsg += "เลือกกลุ่ม Barcode ไม่ตรงกับ Column A ในไฟล์ กรุณาตรวจสอบใหม่ .</br>" ;
					passValid = false;
				}
			    
				/** validate dataType,pensItem **/
				if( isGroupCodeValid(conn, dataType, pensItem,groupCode)==false){
					//FAIL
					lineMsg +=" รหัส PENS นี้ เคยใช้กับ Group อื่นไปแล้ว กรุณาตรวจสอบใหม่ .</br>"; 
					passValid = false;
				}
				
				/** validate dataType,pensItem,mat **/
				if(isMatExist(conn, dataType, pensItem,mat)){
					lineMsg +=" Material No. ซ้ำ กรุณาตรวจสอบ .</br>"; 
					passValid = false;
				}
				
				/** validate dataType,pensItem,barcode **/
				if(isBarcodeExist(conn, dataType, pensItem,barcode)){
					lineMsg +=" Barcode. ซ้ำ กรุณาตรวจสอบ .</br>"; 
					passValid = false;
				}

				logger.debug("lineMsg:"+lineMsg);
				//is valid
				if(passValid){
					successCount++;
					lineMsg +="บันทึกสำเร็จ";
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
					
				    //insert master table
					masterDAO.save(conn,"add",groupType, mat, barcode, pensItem, "",groupCode, monitorModel.getCreateUser(),"","","");
					//masterDAO.save(conn,"add",groupType, mat, barcode, pensItem, "",groupCode, "XXX","","","");
				}else{
					failCount++;
					foundError = true;
					
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
				}
				
	    	}//for row
		
			logger.debug("result foundError:"+foundError);
			if(foundError ==false){
				status = Constants.STATUS_SUCCESS;
			}
			logger.debug("result status:"+status);
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);
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
		modelItem.setSource("FILE");
		modelItem.setDestination("ORACLE");
		modelItem.setStatus(Constants.STATUS_START);
		modelItem.setSubmitDate(new Date());
		modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
		
		return modelItem;
	}

	 public static boolean isGroupCodeValid(Connection conn,String dataType,String pensItem,String groupCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean r = true;
			try {
				sql.append("\n select max(pens_desc2) as group_code_db FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE reference_code = '"+dataType+"' ");
				sql.append("\n AND pens_value ='"+pensItem+"' \n");
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					if( !Utils.isNull(rst.getString("group_code_db")).equals("")){
						if(Utils.isNull(rst.getString("group_code_db")).equals(groupCode)){
							r = true;
						}else{
							r = false;
						}
				    }
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return r;
		}
	 public static boolean isBarcodeExist(Connection conn,String dataType,String pensItem,String barcode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean r = false;
			try {
				sql.append("\n select count(*) as c FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE reference_code = '"+dataType+"' ");
				sql.append("\n AND pens_value ='"+pensItem+"' and interface_desc ='"+barcode+"' \n");
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					if(rst.getInt("c") >0){
						r = true;
					}
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return r;
		}
	 public static boolean isMatExist(Connection conn,String dataType,String pensItem,String mat) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean r = false;
			try {
				sql.append("\n select count(*) as c FROM ");
				sql.append("\n PENSBI.PENSBME_MST_REFERENCE WHERE reference_code = '"+dataType+"' ");
				sql.append("\n AND pens_value ='"+pensItem+"' and interface_value ='"+mat+"' \n");
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					if(rst.getInt("c") >0){
						r = true;
					}
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return r;
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
