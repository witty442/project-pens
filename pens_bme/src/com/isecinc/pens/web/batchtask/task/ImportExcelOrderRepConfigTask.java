package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
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
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskDispBean;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHelper;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class ImportExcelOrderRepConfigTask extends BatchTask implements BatchTaskInterface{
   
	/*public void run(MonitorBean monitorModel){
		logger.debug("TaskName:"+monitorModel.getName());
		logger.debug("transactionId:"+monitorModel.getTransactionId());
	}*/
	
	/**
	 * Return :Param Name|Param label|Param Type|default value|validate$Button Name
	 */
	/*public String getParam(){
		return "dataFormFile|เลือกไฟล์|FROMFILE||VALID$Import ข้อมูล";
	}*/
	public String[] getParam(){
		String[] param = new String[1];
		param[0] = "dataFormFile|เลือกไฟล์|FROMFILE||VALID";
		return param;
	}
	public List<BatchTaskListBean> getParamListBox(){
		return null;
	}
	public String getButtonName(){
		return "Import ข้อมูล";
	}
	public String getDescription(){
		return "Import File From Excel (ORDER REP CONFIG)";
	}
	public String getDevInfo(){
		return "PENSBI.BME_CONFIG_REP";
	}
	public BatchTaskDispBean getBatchDisp(){
		BatchTaskDispBean dispBean = new BatchTaskDispBean();
		dispBean.setDispDetail(true);
		dispBean.setDispRecordFailHead(true);
		dispBean.setDispRecordFailDetail(true);
		dispBean.setDispRecordSuccessHead(true);
		return dispBean;
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
		script +="\n    if(form.dataFormFile.value != '' && (extension == 'xls' || extension == 'xlsx') ){";
		script +="\n    }else{";
		script +="\n       alert('กรุณาเลือกไฟล์นามสกุล  xls หรือ  xlsx ');";
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
			connMonitor = DBConnection.getInstance().getConnectionApps();

			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnectionApps();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item ImportExcelOrderRepConfigTask ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			
			/** Validate FileName is imported**/
			
			/** Start process **/ 
		    modelItem = runProcess(connMonitor,conn,monitorModel,modelItem);
			
			/**debug TimeUse **/
			monitorTime.debugUsedTime();
			
			//Insert Monitor Item
			modelItem = dao.insertMonitorItem(connMonitor,modelItem);
			
			if(modelItem.getStatus()== Constants.STATUS_SUCCESS){
			    logger.debug("Transaction Commit");
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
			monitorModel.setType("IMPORT");
			monitorModel.setThName("Import ข้อมูล Replenishment Config");
			monitorModel.setFileName(monitorModel.getDataFile().getFileName());
			
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
		UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    int dataCount=0,successCount = 0,failCount = 0;
	    String lineMsg = "";
	    String errorMsg = "";
	    boolean importAllError = false;
	    boolean importError = false;
	    int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		int maxColumnNo = 2; // max column of data per row
		int colNo = 0 ,batchExeCount = 100;
		boolean passValid = false;
		Row row = null;
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Cell cellCheck = null;
		boolean hardBreak = false;
		Object cellCheckValue = null;
		Object cellObjValue = "";
		List<String> failList = new ArrayList<String>();
		List<String> successList = new ArrayList<String>();
		String columnCheck1 ="",columnCheck2 ="";
		String storeCode  = "",materialMaster = "";
		Map<String,String> storeMapValid = new HashMap<String, String>();
		Map<String,String> matMapValid = new HashMap<String, String>();
		try {
			FormFile dataFile = monitorModel.getDataFile();
			String custGroup = monitorModel.getBatchParamMap().get("custGroup");
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
			if (dataFile != null) {

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
				
				//validate
				//Check Column to Check
				if( sheet.getRow(0) !=null){
				   cellCheck =  sheet.getRow(0).getCell((short) 0);
				   columnCheck1 = Utils.isNull(xslUtils.getCellValue(0, cellCheck));
				   
				   cellCheck =  sheet.getRow(0).getCell((short) 1);
				   columnCheck2 = Utils.isNull(xslUtils.getCellValue(1, cellCheck));
				}
				logger.debug("columnCheck1:"+columnCheck1+",columnCheck2:"+columnCheck2);
				if(!columnCheck1.equalsIgnoreCase("รหัสร้านค้า") || !columnCheck2.equalsIgnoreCase("MaterialMaster")){
					logger.debug("Error");
		    		 //error
		    		// request.setAttribute("Message", "ข้อมูลไฟล์ ไม่ตรง Format ที่กำหนดไว้");
		    		// return ImportAllForm;
					monitorItemBean.setStatus(-1);
					monitorItemBean.setErrorCode("InvalidDataException");
					monitorItemBean.setErrorMsg("ข้อมูลไฟล์ ไม่ตรง Format ที่กำหนดไว้");
					return monitorItemBean;
		    	 }
				
				 //delete prev all data
				SQLHelper.excUpdate(conn, "delete from PENSBI.BME_CONFIG_REP where store_code like '"+custGroup+"%'");
				
				//xxpens_inv_vanmisc_temp
				StringBuffer sql = new StringBuffer();
				sql.append("INSERT INTO PENSBI.BME_CONFIG_REP \n");
				sql.append("(STORE_CODE, MATERIAL_MASTER,CREATE_DATE, CREATE_USER) VALUES(?,?,?,? ) \n");
				ps = conn.prepareStatement(sql.toString());
	
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            cellCheck =null;
				for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
					row = sheet.getRow(i);
					logger.debug("read row["+i+"]");
					
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
				
					cellObjValue = xslUtils.getCellValue(colNo, row.getCell((short) 0));
					storeCode = ExcelHelper.getCellValue(cellObjValue,"STRING","");
					
					cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
					materialMaster =ExcelHelper.getCellValue(cellObjValue,"STRING","");
					
					//Reset Value By Line Loop
					lineMsg = "";
					errorMsg = "";
					passValid = true;
					dataCount++;
					
					//add Head Table Display
					if(dataCount==1){
					   lineMsg = "Line No Excel|รหัสร้านค้า|Material Master|Status|Message";
					   failList.add(lineMsg);
					   successList.add(lineMsg);
					   lineMsg = "";
					}
					
					//Add Detail LineMsg
					lineMsg = (i+1)+"|"+storeCode+"|"+materialMaster;
					
					//step validate 
					//0:custGroup in screen
					if(storeCode.indexOf(custGroup) ==-1){
						errorMsg = "ข้อมูลกลุ่มร้านค้าที่เลือก ไม่ตรงกับรหัสร้านค้าใน Excel ,";
						passValid = false;
						hardBreak = true;
					}
					//0:check duplicate
					
					
					//1:validate StoreCode
					if(storeMapValid.get(storeCode) ==null){ //valid only new storeCode
						if( !isStoreCodeValid(conn, storeCode)){
							errorMsg = "ไม่พบข้อมูล รหัสสาขานี้ในระบบ ,";
							passValid = false;
						}else{
							storeMapValid.put(storeCode, storeCode);
						}
					}
					//2:validate MasterilMaster
					if(matMapValid.get(materialMaster) ==null){//valid only new mat
						if( !isMaterialMasterValid(conn, materialMaster)){
							errorMsg += "ไม่พบข้อมูล MaterialMaster นี้ในระบบ ";
							passValid = false;
						}else{
							matMapValid.put(materialMaster, materialMaster);
						}
					}
					
					//logger.debug("lineMsg:"+lineMsg);
					
					//is valid
					int index =1;
					if(!passValid){
						failCount++;
						lineMsg += "|FAIL|"+errorMsg;
						//failList.add(lineMsg);
						insertMonitorItemResult(connMonitor,monitorItemBean.getId(),i,"FAIL",lineMsg);
					}else{
						//add statement
						ps.setString(index++, Utils.isNull(storeCode));
						ps.setString(index++, Utils.isNull(materialMaster));
					    ps.setTimestamp(index++, new java.sql.Timestamp(new Date().getTime()));
					    ps.setString(index++, monitorModel.getUserName());
					    try{
					       ps.executeUpdate();
					       ps.clearParameters();
					       
					       //insert record success
					       successCount++;
						   lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
						   insertMonitorItemResult(connMonitor,monitorItemBean.getId(),i,"SUCCESS",lineMsg);
					    }catch(SQLIntegrityConstraintViolationException sid){
					       //insert record fail
					       failCount++;
						   lineMsg += "|FAIL|ข้อมูลซ้ำ";
						   insertMonitorItemResult(connMonitor,monitorItemBean.getId(),i,"FAIL",lineMsg);
					       logger.error("Duplicate:storeCode["+storeCode+"]materialMaster["+materialMaster+"]");
					    }
					}//if
				    //Case Hard Break 
					if(hardBreak){
						break;
					}

				}//for Row
				
				//Insert Column For Display Result
				lineMsg ="";
				lineMsg += "No|Line Excel|StoreCode|Material Master|Status|Message";
				insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
				 
				//Execute Batch Statement
				if(importAllError==false){
				   //int[] e = ps.executeBatch();
				   //logger.debug("excute count:"+e.length);
				}
			}//if
			
			logger.debug("importAllError:"+importAllError);
			
			/** Set Count Record **/
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setStatus(Constants.STATUS_SUCCESS);
			if(importAllError){
				monitorItemBean.setStatus(Constants.STATUS_FAIL);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			     
			} catch (Exception e2) {}
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
			sql.append("\n SELECT count(*) as c FROM APPS.xxpens_inv_vanmisc_temp");
			sql.append("\n WHERE file_name ='"+fileName+"'");
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
	 public static boolean isStoreCodeValid(Connection conn,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean valid = false;
			try {
				sql.append("\n  SELECT count(*) as c from PENSBI.PENSBME_MST_REFERENCE M");
				sql.append("\n  where M.reference_code ='Store' ");
				sql.append("\n  and M.pens_desc4 ='N' ");
				sql.append("\n  and M.pens_value ='"+storeCode+"'");
				
				//logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					if(rst.getInt("c") >0){
						valid = true;
					}
				}//if
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					
				} catch (Exception e) {}
			}
			return valid;
		}
		 public static boolean isMaterialMasterValid(Connection conn,String materialMaster) throws Exception {
				Statement stmt = null;
				ResultSet rst = null;
				StringBuilder sql = new StringBuilder();
				boolean valid = false;
				try {
					sql.append("\n  SELECT count(*) as c from PENSBI.PENSBME_MST_REFERENCE M");
					sql.append("\n  where M.reference_code ='LotusItem' ");
					sql.append("\n  and M.interface_value ='"+materialMaster+"'");
					
					//logger.debug("sql:"+sql);
					stmt = conn.createStatement();
					rst = stmt.executeQuery(sql.toString());
					if (rst.next()) {
						if(rst.getInt("c") >0){
							valid = true;
						}
					}//if
				} catch (Exception e) {
					throw e;
				} finally {
					try {
						rst.close();
						stmt.close();
						
					} catch (Exception e) {}
				}
				return valid;
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
