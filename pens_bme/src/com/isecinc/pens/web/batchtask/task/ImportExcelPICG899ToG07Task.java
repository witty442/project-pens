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

public class ImportExcelPICG899ToG07Task extends BatchTask implements BatchTaskInterface{
	
	public static String STORE_CODE_P  ="STORE_CODE";
	public static String SUB_INV_P  ="SUB_INV";
	
	/**
	 * Return :P Name|P label|P Type|default value|valid,P2...$processName,Button Name|....
	*/
	public String[] getParam(){
		String[] param = new String[1];
		param[0] = "dataFormFile|เลือกไฟล์|FROMFILE||VALID";
		return param;
	}
	public List<BatchTaskListBean> getParamListBox(){
		List<BatchTaskListBean> listAll = new ArrayList<BatchTaskListBean>();
		return listAll;
	}
	public String getButtonName(){
		return "Import ข้อมูล";
	}
	public String getDescription(){
		return "Import Excel PIC G899 To G07 From File Excel";
	}
	public String getDevInfo(){
		return "apps.XXPENS_INV_EXCEL_BILLT_MST,apps.XXPENS_INV_EXCEL_BILLT_DT";
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
			
			/** Step validate **/
			//Validate Duplication
			if(isImportExist(conn,monitorModel.getDataFile().getFileName())){
				modelItem.setStatus(Constants.STATUS_FAIL);
				modelItem.setErrorMsg("ไฟล์ :"+monitorModel.getDataFile().getFileName()+" ได้เคย Importไปแล้ว");
				modelItem.setErrorCode("ImportException");
			}else{
			    /** Start process **/ 
		        modelItem = process(connMonitor,conn,monitorModel,modelItem);	
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
			
			/** Update status Head Monitor From Monitor Item Status*/
            monitorModel.setErrorMsg(modelItem.getErrorMsg());
            monitorModel.setErrorCode(modelItem.getErrorCode());
			monitorModel.setStatus(modelItem.getStatus());
			monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			monitorModel.setType("IMPORT");
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
		StringBuffer sql  =new StringBuffer();
		int status = Constants.STATUS_FAIL;int dataCount=0;
		int successCount = 0;int failCount = 0;int no = 0;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Row row = null;
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 1; // row of begin data
		boolean passValid = false;String lineMsg = "";
		int colNo = 0 ;int r = 0;
		boolean foundError = false;
		String errorMsg ="";
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    
	    String docDate  = "",refDoc = "",fromOrg = "",fromSubInv="";
	    String toOrg="",toSubInv="" ,item="",qty="";
	    BigDecimal id = new BigDecimal("0");
		try{
			//Get Parameter Value
			String storeCode = monitorModel.getBatchParamMap().get(STORE_CODE_P);
			String subInv = monitorModel.getBatchParamMap().get(SUB_INV_P);
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
            //delete prev all data
			SQLHelper.excUpdate(conn, "delete from PENSBI.BME_INV_EXCEL_BILLT_TEMP ");
			
			//prepare insert temp import
			sql = new StringBuffer();
			sql.append("INSERT INTO PENSBI.BME_INV_EXCEL_BILLT_TEMP( \n");
			sql.append("TRNSACTION_DATE,TRNSACTION_SOURCE_NAME, FROM_ORG, FROM_SUBINV,\n"); 
			sql.append("TO_ORG,TO_SUBINV ,ITEM, QTY,\n");
			sql.append("CREATE_DATE, CREATE_USER, FILE_NAME) \n");//11
			sql.append(" VALUES(?,?,?,? ,?,?,?,? ,?,?,?)");
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
				docDate = ExcelHelper.getCellValue(cellObjValue,"DATE","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
				refDoc =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 2));
				fromOrg =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 3));
				fromSubInv = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 4));
				toOrg = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 5));
				toSubInv = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 6));
				item = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 7));
				qty = ExcelHelper.getCellValue(cellObjValue,"NUMBER",Utils.format_number_no_disgit);
				
				//Reset Value By Line Loop
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				
				//step validate 
				if(isRefDocExist(conn, refDoc)){
					passValid = false;
					foundError = true;
					errorMsg = "อ้างอิงใบโอน ใบนี้ เคย Import ไปแล้ว";
				}
				if( !subInv.equalsIgnoreCase(toSubInv)){
					errorMsg +=" ,ข้อมูล to Sub Inv ไม่ตรงกับ SubInv ที่ระบุในหน้าจอ";
					passValid = false;
					foundError = true;
				}
				
				//Qty ==0 is error
				if(Utils.convertToInt(qty) <=0){
					errorMsg +=" ,ข้อมูล Qty ต้องมากกว่า 0 ";
					passValid = false;
					foundError = true;
				}
				//insert log to display
				lineMsg  = (r+1)+"|"+docDate+"|"+refDoc+"|"+fromOrg+"|"+fromSubInv+"|";
				lineMsg += toOrg+"|"+toSubInv+"|"+item+"|"+qty;
				
				//logger.debug("lineMsg:"+lineMsg);
				//is valid
				int index =1;
				if(passValid){
					successCount++;
					lineMsg += "|SUCCESS|บันทึกข้อมูลเรียบร้อย";
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
					
				}else{
					failCount++;
					//foundError = true; no rollback
					lineMsg += "|FAIL|"+errorMsg;
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
				}
				
				//add statement
				ps.setDate(index++, new java.sql.Date(DateUtil.parse(docDate,DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
				ps.setString(index++, Utils.isNull(refDoc));
				ps.setString(index++, Utils.isNull(fromOrg));
			    ps.setString(index++, Utils.isNull(fromSubInv));
			    ps.setString(index++, Utils.isNull(toOrg));
			    ps.setString(index++, Utils.isNull(toSubInv));
			    ps.setString(index++, Utils.isNull(item));
			    ps.setDouble(index++, Utils.convertStrToDouble(qty));
			    ps.setDate(index++, new java.sql.Date(new Date().getTime()));
			    ps.setString(index++, monitorModel.getCreateUser());
			    ps.setString(index++, fileName);
			    ps.addBatch();
			    
	    	}//for row
		
			//Insert Column For Display Result (1:no, last column ->status,Message )default column
			lineMsg ="";
			lineMsg  = "No|Line Excel|วันที่ทำเอกสาร|อ้างอิงใบโอน|From Org|From SubInv|To Org|To SubInv|";
			lineMsg += "Item|Qty|Status|Message";
			insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
			 
			logger.debug("result foundError:"+foundError);
			if(foundError ==false){
				status = Constants.STATUS_SUCCESS;
				//no error
				int e[] = ps.executeBatch();
				logger.debug("excute count:"+e.length);
				
				//sum data from temp import to table 
				insertXXPENS_INV_EXCEL_BILLT_MST(conn, monitorModel.getCreateUser());
				
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
			}catch(Exception ee){}
		}
		return monitorItemBean;	
	}
	
	public static void insertXXPENS_INV_EXCEL_BILLT_MST(Connection conn,String userName) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement psIns = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int index = 1;
		BigDecimal id = new BigDecimal("0");
		try {
			//query
			sql.append("\n SELECT TRNSACTION_DATE,TRNSACTION_SOURCE_NAME,FROM_ORG,FROM_SUBINV,TO_ORG ,TO_SUBINV,FILE_NAME ");
			sql.append("\n ,SYSDATE");
			sql.append("\n FROM PENSBI.BME_INV_EXCEL_BILLT_TEMP ");
			sql.append("\n WHERE 1=1");
			sql.append("\n GROUP BY TRNSACTION_DATE,TRNSACTION_SOURCE_NAME,FROM_ORG,FROM_SUBINV,TO_ORG ,TO_SUBINV,FILE_NAME");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery(sql.toString());
			
			//insert 
			sql = new StringBuilder();
			sql.append("\n INSERT INTO apps.XXPENS_INV_EXCEL_BILLT_MST ");
			sql.append("\n (HEADER_ID,TRANSACTION_DATE,TRANSACTION_SOURCE_NAME,FROM_ORG,FROM_SUBINV,TO_ORG ,TO_SUBINV,");
			sql.append("\n FILE_NAME,CREATION_DATE) ");
			sql.append("\n VALUES(?,?,?,?,?,?,?,?,?) ");//8
			psIns = conn.prepareStatement(sql.toString());
			
			//Loop Query
			while (rst.next()) {
				id = SequenceProcessAll.getIns().getNextValue("apps.XXPENS_INV_EXCEL_BILLT_MST");
			
				psIns.setBigDecimal(index++, id);
				psIns.setDate(index++, rst.getDate("TRNSACTION_DATE"));
				psIns.setString(index++, rst.getString("TRNSACTION_SOURCE_NAME"));
				psIns.setString(index++, rst.getString("FROM_ORG"));
				psIns.setString(index++, rst.getString("FROM_SUBINV"));
				psIns.setString(index++, rst.getString("TO_ORG"));
				psIns.setString(index++, rst.getString("TO_SUBINV"));
				psIns.setString(index++, rst.getString("FILE_NAME"));
				psIns.setTimestamp(index++, rst.getTimestamp("SYSDATE"));
				psIns.execute();
				
				index =1;//reset index
				//insert Line
				insertXXPENS_INV_EXCEL_BILLT_DT(conn, id,rst,userName);
			}//if
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				psIns.close();
			} catch (Exception e) {}
		}
		//return r;
	}
	public static void insertXXPENS_INV_EXCEL_BILLT_DT(Connection conn,BigDecimal id,ResultSet rs ,String userName) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement psIns = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int index = 1;
		int lineId =0;
		try { 
			//query
			sql.append("\n SELECT ITEM");
			sql.append("\n ,(select P.inventory_item_id from apps.xxpens_om_item_mst_v P ");
			sql.append("\n  where P.segment1 = S.item) as INVENTORY_ITEM_ID");
			sql.append("\n ,SYSDATE");
			sql.append("\n ,SUM(QTY) AS QTY ");
			sql.append("\n FROM PENSBI.BME_INV_EXCEL_BILLT_TEMP S ");
			sql.append("\n WHERE TRNSACTION_DATE  = TO_DATE('"+DateUtil.stringValue(rs.getDate("TRNSACTION_DATE"), "dd/MM/yyyy")+"','dd/mm/yyyy')");
			sql.append("\n AND TRNSACTION_SOURCE_NAME ='"+rs.getString("TRNSACTION_SOURCE_NAME")+"'");
			sql.append("\n AND FROM_ORG ='"+rs.getString("FROM_ORG")+"'");
			sql.append("\n AND FROM_SUBINV ='"+rs.getString("FROM_SUBINV")+"'");
			sql.append("\n AND TO_ORG ='"+rs.getString("TO_ORG")+"'");
			sql.append("\n AND TO_SUBINV ='"+rs.getString("TO_SUBINV")+"'");
			sql.append("\n AND FILE_NAME ='"+rs.getString("FILE_NAME")+"'");
			sql.append("\n GROUP BY ITEM");
			
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			
			//Insert
			sql = new StringBuilder();
			sql.append("\n INSERT INTO apps.XXPENS_INV_EXCEL_BILLT_DT ");
			sql.append("\n (HEADER_ID,LINE_ID,INVENTORY_ITEM_ID,PENS_ITEM,QTY,CREATION_DATE) ");
			sql.append("\n VALUES(?,?,?,?,?,?) ");
			psIns = conn.prepareStatement(sql.toString());
			
			//loop query
			while (rst.next()) {
				lineId++;
				
				psIns.setBigDecimal(index++, id);
				psIns.setInt(index++, lineId);
				psIns.setString(index++, rst.getString("INVENTORY_ITEM_ID"));
				psIns.setString(index++, rst.getString("ITEM"));
				psIns.setString(index++, rst.getString("QTY"));
				psIns.setTimestamp(index++, rst.getTimestamp("SYSDATE"));
				psIns.execute();
				
				index =1;//reset index
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				psIns.close();
			} catch (Exception e) {}
		}
	}
	
	public static boolean isImportExist(Connection conn,String fileName) {
		ResultSet rs = null;
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		boolean exist = false;
		try{
			sql.append("\n SELECT count(*) as c");
			sql.append("\n FROM apps.XXPENS_INV_EXCEL_BILLT_MST ");
			sql.append("\n WHERE FILE_NAME ='"+fileName+"'");
			
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
			   if(rs.getInt("c") >0){
				   exist = true;
			   }
			}//while	
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
		return exist;	
	}
	
	public static boolean isRefDocExist(Connection conn,String refDoc) {
		ResultSet rs = null;
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		boolean exist = false;
		try{
			sql.append("\n SELECT count(*) as c");
			sql.append("\n FROM apps.XXPENS_INV_EXCEL_BILLT_MST ");
			sql.append("\n WHERE TRANSACTION_SOURCE_NAME ='"+refDoc+"'");
			
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
			   if(rs.getInt("c") >0){
				   exist = true;
			   }
			}//while	
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
		return exist;	
	}
}
