package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
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
import com.pens.util.seq.SequenceProcessAll;

public class ImportAutoCNHHTempFromExcelTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
   
	/**
	 * Return :P Name|P label|P Type|default value|valid,P2...$processName,Button Name|....
	*/
	public String getParam(){
		return "dataFormFile|เลือกไฟล์|FROMFILE||VALID$Import ข้อมูล";
	}
	public String getDescription(){
		return "Import AutoCnHHTemp From File Excel";
	}
	public String getDevInfo(){
		return "PENSBI.BME_AUTOCN_HH_IMPORT_TEMP,PENSBI.BME_AUTOCN_HH_TEMP ,PENSBI.BME_AUTOCN_HH_TEMP_I,PENSBI.BME_AUTOCN_HH_TEMP_INV";
	}
	public List<BatchTaskListBean> getParamListBox(){
		List<BatchTaskListBean> listAll = new ArrayList<BatchTaskListBean>();
		return listAll;
	}
	//Show detail BatchTaskResult or no
	public boolean isDispDetail(){
		return true;
	}
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
		script +="\n   var form = document.batchTaskForm;";
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
			connMonitor = DBConnection.getInstance().getConnectionApps();

			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
			
			/** Start process **/ 
		    modelItem = process(connMonitor,conn,monitorModel,modelItem);	
			
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
			
			logger.debug("errorMsg:"+monitorModel.getErrorMsg());
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
				   logger.debug("Transaction Rollback");
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

	public static MonitorItemBean process(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		PreparedStatement ps = null;
		PreparedStatement psInv = null;
		StringBuffer sql  =new StringBuffer();
		int status = Constants.STATUS_FAIL;int dataCount=0;
		int successCount = 0;int failCount = 0;int no = 0;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Row row = null;
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 0; // row of begin data
		boolean passValid = false;String lineMsg = "";
		int colNo = 0 ;int r = 0;
		boolean foundError = false;
		String errorMsg ="";
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    
	    String storeCode  = "",materialMaster = "",groupCode = "";
	    String pensItem="",qty="",org="",subInv="",location="",unitPrice="";
	    BigDecimal id = new BigDecimal("0");
		try{
			//Get Parameter Value
			FormFile dataFile = monitorModel.getDataFile();
			
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			logger.debug("fileType:"+fileType);
			
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
			
			//Get RowNo Start for Data Table
			/** Loop Row **/
			for (r = 0; r <=sheet.getLastRowNum() ; r++) {
				row = sheet.getRow(r);
				//logger.debug("read row["+r+"]");
				
				//Check Stop Loop Col 1
				if(row !=null){
				   cellCheck = row.getCell((short) 0);
			       cellCheckValue = xslUtils.getCellValue(0, cellCheck);
			       logger.debug("check row["+r+"][0]="+cellCheckValue);
			       if(cellCheckValue != null && Utils.isNull(cellCheckValue.toString()).equalsIgnoreCase("Customer No")){
			    	   rowNo = r;
			    	   rowNo++;
			    	   break;
			       }
				}
			}//for
			
			//Get ID By next Seq
			id = SequenceProcessAll.getIns().getNextValue("AUTOCN_HHTEMP");
			
			//insert to Invoice
			sql = new StringBuffer();
			sql.append("INSERT INTO PENSBI.BME_AUTOCN_HH_TEMP_INV(ID,INVOICE_NO)VALUES(?,?) \n");
			psInv = conn.prepareStatement(sql.toString());
			
		    /** Loop Row Inv **/
			String invoiceNo= "";
			String invoiceNoAll ="";
			for (r = 1; r <(rowNo-1) ; r++) {
				int index =1;
				row = sheet.getRow(r);
				cellObjValue = xslUtils.getCellValue(colNo, row.getCell((short) 0));
				invoiceNo = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				invoiceNoAll +=invoiceNo+",";
				
				psInv.setBigDecimal(index++, id);
				psInv.setString(index++, invoiceNo);
				psInv.addBatch();
			}
			
			//validate InvoiceNo is Exist
			if(isImportExist(conn,invoiceNoAll)){
				monitorItemBean.setStatus(Constants.STATUS_FAIL);
				monitorItemBean.setErrorMsg("ข้อมูล InvoiceNo นี้เคยถูก Import ไปแล้ว ");
				monitorItemBean.setErrorCode("ImportException");
				return monitorItemBean;
			}

            //delete prev all data
			SQLHelper.excUpdate(conn, "delete from PENSBI.BME_AUTOCN_HH_IMPORT_TEMP ");
			
			//prepare insert temp import
			sql = new StringBuffer();
			sql.append("INSERT INTO PENSBI.BME_AUTOCN_HH_IMPORT_TEMP( \n");
			sql.append("ID,STORE_CODE, MATERIAL_MASTER, GROUP_CODE,PENS_ITEM, \n"); 
			sql.append("QTY, ORG,SUB_INV, LOCATION ,\n");
			sql.append("UNIT_PRICE ,CREATE_DATE, CREATE_USER, FILE_NAME) \n");//13
			sql.append(" VALUES(?,?,?,? ,?,?,?,? ,?,?,?,? ,? )");
			ps = conn.prepareStatement(sql.toString());
			
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
			
				    
				cellObjValue = xslUtils.getCellValue(colNo, row.getCell((short) 0));
				storeCode = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
				materialMaster =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 2));
				groupCode = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 3));
				pensItem = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 4));
				qty = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_number_no_disgit);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 5));
				org = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 6));
				subInv = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 7));
				location = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 8));
				unitPrice = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_current_2_disgit);
				
				//Reset Value By Line Loop
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				
				//step validate 
				
				//insert log to display
				lineMsg  = (r+1)+"|"+storeCode+"|"+materialMaster+"|"+groupCode+"|"+pensItem+"|";
				lineMsg += qty+"|"+org+"|"+subInv+"|"+location+"|"+unitPrice+"|";
				
				//logger.debug("lineMsg:"+lineMsg);
				//is valid
				int index =1;
				if(passValid){
					successCount++;
					lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
					//Insert Log
				    //insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
					
				}else{
					failCount++;
					//foundError = true; no rollback
					lineMsg += "|FAIL|"+errorMsg;
					//Insert Log
				   // insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
				}
				//add statement
				ps.setBigDecimal(index++, id);
				ps.setString(index++, Utils.isNull(storeCode));
			    ps.setString(index++, Utils.isNull(materialMaster));
			    ps.setString(index++, Utils.isNull(groupCode));
			    ps.setString(index++, Utils.isNull(pensItem));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty));
			    ps.setString(index++, Utils.isNull(org));
			    ps.setString(index++, Utils.isNull(subInv));
			    ps.setString(index++, Utils.isNull(location));
			    ps.setDouble(index++, Utils.convertStrToDouble(unitPrice));
			    ps.setDate(index++, new java.sql.Date(new Date().getTime()));
			    ps.setString(index++, monitorModel.getCreateUser());
			    ps.setString(index++, fileName);
			    ps.addBatch();
			    
	    	}//for row
		
			//Insert Column For Display Result (1:no, last column ->status,Message )default column
			lineMsg ="";
			lineMsg  = "No|Line Excel|CustomerNo|ProdCode|GroupCode|pensItem|QTY|";
			lineMsg += "org|subInv|location|ราคาออกCN ก่อนVat|";
			 
			//insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
			 
			logger.debug("result foundError:"+foundError);
			if(foundError ==false){
				status = Constants.STATUS_SUCCESS;
				//no error
				int e[] = ps.executeBatch();
				logger.debug("excute count:"+e.length);
				
				//Execute Insert invoice
				psInv.executeBatch();
				
				//sum data from temp import to table HH
				insertBME_AUTOCN_HH_TEMP(conn, id, monitorModel.getCreateUser());
				insertBME_AUTOCN_HH_TEMP_I(conn, id, monitorModel.getCreateUser());
			}
			
			logger.debug("result status:"+status);
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
					ps.close();ps = null;
				}
				if(psInv != null){
					psInv.close();psInv = null;
				}
			}catch(Exception ee){}
		}
		return monitorItemBean;	
	}
	
	public static void insertBME_AUTOCN_HH_TEMP(Connection conn,BigDecimal id,String userName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int index = 1;
		String custGroup = "";
		try {
			sql.append("\n SELECT STORE_CODE ,SUM(QTY) as TOTAL_QTY FROM PENSBI.BME_AUTOCN_HH_IMPORT_TEMP ");
			sql.append("\n WHERE ID ='"+id+"' GROUP BY ID,STORE_CODE ");
			
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery(sql.toString());
			if (rst.next()) {
				sql = new StringBuilder();
				sql.append("\n INSERT INTO PENSBI.BME_AUTOCN_HH_TEMP ");
				sql.append("\n (ID,CUST_GROUP,STORE_CODE,STATUS ,");
				sql.append("\n TOTAL_QTY,CREATE_DATE,CREATE_USER) ");
				sql.append("\n VALUES(?,?,?,?,?,?,?) ");
				
				//GET CustGroup
				custGroup = rst.getString("store_code");
				custGroup = custGroup.indexOf("-")!=-1?custGroup.substring(0,custGroup.indexOf("-")):custGroup;
				
				ps = conn.prepareStatement(sql.toString());
				ps.setBigDecimal(index++, id);
				ps.setString(index++, custGroup);
				ps.setString(index++, rst.getString("STORE_CODE"));
				ps.setString(index++, "APPROVED");
				ps.setDouble(index++, rst.getDouble("TOTAL_QTY"));
				ps.setDate(index++, new java.sql.Date(new Date().getTime()));
				ps.setString(index++, userName);
				ps.execute();
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		//return r;
	}
	public static void insertBME_AUTOCN_HH_TEMP_I(Connection conn,BigDecimal id,String userName) throws Exception {
		Statement stmt = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("\n INSERT INTO PENSBI.BME_AUTOCN_HH_TEMP_I ");
			sql.append("\n SELECT ");
			sql.append("\n "+id+" as ID");
			sql.append("\n ,(select P.inventory_item_id from apps.xxpens_om_item_mst_v P ");
			sql.append("\n  where P.segment1 = S.pens_item) as INVENTORY_ITEM_ID");
			sql.append("\n ,S.PENS_ITEM");
			sql.append("\n ,S.UNIT_PRICE ");
			sql.append("\n ,'APPROVED' as STATUS ");
			sql.append("\n ,S.ORG ");
			sql.append("\n ,S.SUB_INV ");
			sql.append("\n ,S.LOCATION ");
			sql.append("\n ,SUM(S.QTY) as QTY");
			sql.append("\n ,sysdate");
			sql.append("\n ,'"+userName+"' as create_user");
			sql.append("\n FROM PENSBI.BME_AUTOCN_HH_IMPORT_TEMP S");
			sql.append("\n WHERE ID ="+id+"");
			sql.append("\n GROUP BY");
			sql.append("\n  S.PENS_ITEM,S.UNIT_PRICE,S.ORG ");
			sql.append("\n ,S.SUB_INV ,S.LOCATION ");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			stmt.executeUpdate(sql.toString());
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {}
		}
	}
	public static boolean isImportExist(Connection conn,String invoiceNoAll) {
		ResultSet rs = null;
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		boolean exist = false;
		try{
			invoiceNoAll = invoiceNoAll.substring(0,invoiceNoAll.length()-1);
			
			sql.append("\n SELECT count(*) as c FROM PENSBI.BME_AUTOCN_HH_TEMP_INV O");
			sql.append("\n WHERE O.INVOICE_NO IN("+SQLHelper.converToTextSqlIn(invoiceNoAll)+")");
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
}
