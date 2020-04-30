package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.upload.FormFile;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.ProductBean;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskDispBean;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHelper;
import com.pens.util.meter.MonitorTime;

public class ImportExcelSalesOutLotusTask extends BatchTask implements BatchTaskInterface{
	
	
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
		return "Load file Excel Sales-Out Lotus ";
	}
	public String getDevInfo(){
		return "PENSBI.PENSBME_SALES_FROM_LOTUS";
	}
	
	//Show detail BatchTaskResult or no
	public BatchTaskDispBean getBatchDisp(){
		BatchTaskDispBean dispBean = new BatchTaskDispBean();
		dispBean.setDispDetail(true);
		dispBean.setDispRecordFailHead(true);
		dispBean.setDispRecordFailDetail(true);
		dispBean.setDispRecordSuccessHead(true);
		dispBean.setDispRecordSuccessDetail(true);
		return dispBean;
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
			monitorModel.setFileName(monitorModel.getDataFile().getFileName());
            monitorModel.setThName("Load Sales-Out Lotus");
			
			/** Step validate **/
			
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
		ImportDAO importDAO = new ImportDAO();
		String errorMsg ="";
		Cell cellCheck = null;
	    Object cellCheckValue = null;
	    Object cellObjValue = "";
	    int seq = 0;
	    String vendor = "",name="",storeNo = "",storeName="",ITEM_TPNA ="",styleNo="",description="";
	    String salesDate = "",qty="",sizes="",sizeType="";
	    double netSalesExcVat=0,netSalesInclVat=0,vatRate=0 ,gpPercent =0,gpAmount=0;
		double vatAmount = 0,vatOnGpAmount=0,gpAmountInclVat=0;
	    String PENS_CUST_CODE ="",PENS_CUST_DESC="",PENS_GROUP="",PENS_GROUP_TYPE="",pensItem ="";
	
	    try{
			//Get Parameter Value
			FormFile dataFile = monitorModel.getDataFile();
			
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			logger.debug("fileType:"+fileType);
			
			
			/** validate FileName duplicate (exist db import ) **/
			boolean dup = importDAO.importLotusFileNameIsDuplicate(conn, fileName);
			if(dup){
				monitorItemBean.setStatus(Constants.STATUS_FAIL);
				monitorItemBean.setSuccessCount(0);
				monitorItemBean.setFailCount(0);
				monitorItemBean.setDataCount(0);
				monitorItemBean.setErrorMsg(ExceptionHandle.ERROR_MAPPING.get("DuplicateImportFileException"));
				return monitorItemBean;
			}
			
			if("xls".equalsIgnoreCase(fileType)){
			   wb1 = new HSSFWorkbook(dataFile.getInputStream());//97-2003
			   sheet = wb1.getSheetAt(sheetNo);
			   logger.debug("number of sheet: " + wb1.getNumberOfSheets());
			}else if("xlsx".equalsIgnoreCase(fileType)){
			   OPCPackage pkg = OPCPackage.open(dataFile.getInputStream());
			   wb2 = new XSSFWorkbook(pkg);
			   sheet = wb2.getSheetAt(sheetNo);
			   logger.debug("number of sheet: " + wb2.getNumberOfSheets());
			}else{
				//Dev Test
				XSSFEventBasedExcelExtractor ext = null;
				try {
					///OPCPackage pkg = OPCPackage.open(dataFile.getInputStream());
				    ext = new XSSFEventBasedExcelExtractor("D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\TRANS_LOTUS(SaleOut)\\New Format Sales-Out LOTUS_test.xlsb");
				    logger.debug("***********XLSB*****************************");
				    logger.debug(ext.getText());
				    logger.debug("***********XLSB*****************************");
				} catch (Exception ex) {
				    System.out.println(ex.getMessage());
				}
				
			}
     
			//prepare insert temp import
			sql = new StringBuffer();
			sql.append(" INSERT INTO PENSBME_SALES_FROM_LOTUS( \n");
			sql.append(" VENDOR, NAME, AP_TYPE, LEASE_VENDOR_TYPE,  \n");
			sql.append(" STORE_NO, STORE_NAME, STYLE_NO,  \n");
			sql.append(" DESCRIPTION, COL, SIZE_TYPE,  \n");
			sql.append(" SIZES, SALES_DATE, QTY,  \n");
			sql.append(" GROSS_SALES, RETURN_AMT, NET_SALES_INCL_VAT,  \n");
			sql.append(" VAT_AMT, NET_SALES_EXC_VAT, GP_PERCENT,  \n");
			sql.append(" GP_AMOUNT, VAT_ON_GP_AMOUNT, GP_AMOUNT_INCL_VAT,  \n");
			sql.append(" AP_AMOUNT, TOTAL_VAT_AMT, AP_AMOUNT_INCL_VAT, \n");
			sql.append(" CREATE_DATE, CREATE_USER ,PENS_CUST_CODE, \n");
			sql.append(" PENS_CUST_DESC ,PENS_GROUP ,PENS_GROUP_TYPE, \n");
			sql.append(" SALES_YEAR ,SALES_MONTH ,File_name,PENS_ITEM ,RETAIL_PRICE_BF,TOTAL_WHOLE_PRICE_BF ) \n");
			sql.append(" VALUES( ?,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,? ,?,?,?,? ,?,?)");

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
				vendor = ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 1));
				name =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 3));
				storeNo =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 4));
				storeName =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 5));
				ITEM_TPNA =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				styleNo = ITEM_TPNA.substring(2,9);
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 6));
				description =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 9));
				salesDate =ExcelHelper.getCellValue(cellObjValue,"DATE","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 11));
				qty = ExcelHelper.getCellValue(cellObjValue,"NUMBER","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 13));
				sizes =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 14));
				sizeType =ExcelHelper.getCellValue(cellObjValue,"STRING","");
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 15));
				netSalesExcVat = Utils.convertStrToDouble(ExcelHelper.getCellValue(cellObjValue,"NUMBER",""));
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 18));
				vatRate = Utils.convertStrToDouble(ExcelHelper.getCellValue(cellObjValue,"NUMBER",""));
		        vatAmount = (netSalesExcVat*(vatRate/100));
				netSalesInclVat = netSalesExcVat +vatAmount;
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 16));
				gpPercent = Utils.convertStrToDouble(ExcelHelper.getCellValue(cellObjValue,"NUMBER",""));
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 17));
				gpAmount = Utils.convertStrToDouble(ExcelHelper.getCellValue(cellObjValue,"NUMBER",""));
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 19));
				vatOnGpAmount = Utils.convertStrToDouble(ExcelHelper.getCellValue(cellObjValue,"NUMBER",""));
				
				cellObjValue = xslUtils.getCellValue(colNo,  row.getCell((short) 20));
				gpAmountInclVat = Utils.convertStrToDouble(ExcelHelper.getCellValue(cellObjValue,"NUMBER",""));
				
				//Reset Value By Line Loop
				PENS_CUST_CODE = "";
				PENS_CUST_DESC = "";
				PENS_GROUP = "";
				PENS_GROUP_TYPE = "";
				pensItem = "";
				lineMsg = "";
				errorMsg = "";
				passValid = true;
				dataCount++;
				seq++;
				
				logger.debug("StyleNo:"+styleNo);
				/** step validate item  **/
				Master mStore = importDAO.getMasterMapping(conn, "Store", storeNo);
			    ProductBean productBean = importDAO.getProductStyleMapping(conn,styleNo);
			    
			    if(productBean != null && productBean.getGroupCode().startsWith("W")){
					if(mStore !=null){
						PENS_CUST_CODE = mStore.getPensValue();
						PENS_CUST_DESC =mStore.getPensDesc();
					}else if(mStore ==null){
						PENS_CUST_CODE = storeNo;
						PENS_CUST_DESC = storeNo;
					}
					if(productBean != null){
						PENS_GROUP = Utils.isNull(productBean.getProductGroup());
						PENS_GROUP_TYPE = productBean.getGroupCode();
					}
			    }else{
			    	pensItem = productBean != null?productBean.getPensItem():"";
			    	if(mStore !=null){
						PENS_CUST_CODE = mStore.getPensValue();
						PENS_CUST_DESC =mStore.getPensDesc();
					}else if(mStore ==null){
						PENS_CUST_CODE = storeNo;
						PENS_CUST_DESC = storeNo;
					}
					if(productBean != null){
						PENS_GROUP = Utils.isNull(productBean.getProductGroup());
						PENS_GROUP_TYPE = productBean.getGroupCode();
					}
					
				     if(mStore ==null){
				    	errorMsg +="ไม่พบข้อมูล STORE NAME";
				    	passValid = false;
						foundError = true;
				     }
				     if(productBean==null){
				        errorMsg +="ไม่พบข้อมูล Group";
				        passValid = false;
						foundError = true;
				     }
			         if(Utils.isNull(pensItem).equals("")){
			        	 errorMsg +="ไม่พบข้อมูล Pens Item"; 
			        	 passValid = false;
						 foundError = true;
			         }   
			    }
			    
				//insert item log to display
				lineMsg  = (r+1)+"|"+vendor+"|"+name+"|"+storeNo+"|"+storeName+"|"+ITEM_TPNA+"|"+description+"|"+salesDate+"|"+qty;
				
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
					//foundError = true; no roll back
					lineMsg += "|FAIL|"+errorMsg;
					//Insert Log
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
				}
				
				//add statement
				ps.setString(index++, Utils.isNull(vendor));
				ps.setString(index++, Utils.isNull(name));
				ps.setString(index++, Utils.isNull(""));//AP_TYPE
				ps.setString(index++, Utils.isNull(""));//LEASE VENDOR TYPE
				ps.setString(index++, Utils.isNull(storeNo));
				ps.setString(index++, Utils.isNull(storeName));
				ps.setString(index++, Utils.isNull(styleNo));
				ps.setString(index++, Utils.isNull(description));
				ps.setString(index++, Utils.isNull(""));//COL
				ps.setString(index++, Utils.isNull(sizeType));
				ps.setString(index++, Utils.isNull(sizes));
				ps.setDate(index++, new java.sql.Date(DateUtil.parse(salesDate,DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
				ps.setDouble(index++, Utils.convertStrToDouble(qty));
				ps.setDouble(index++, 0);//Gross_sales
				ps.setDouble(index++, 0);//return_amt
				
				ps.setDouble(index++, netSalesInclVat);//NET_SALES_INCL_VAT
				ps.setDouble(index++, vatAmount);//VAT_AMT
				ps.setDouble(index++, netSalesExcVat);//NET_SALES_EXC_VAT
				
				ps.setDouble(index++, gpPercent);
				//OLD Case 1
				ps.setDouble(index++, 0);//gpAmount
				ps.setDouble(index++, 0);//vatOnGpAmount
				ps.setDouble(index++, 0);//gpAmountInclVat
				
				//new Edit 07/04/2020:Case1
				ps.setDouble(index++, gpAmount);//ap_amount (GROSS SALES EXCL VAT Excel:column:15)
				ps.setDouble(index++, vatOnGpAmount);//TOTAL_VAT_AMT (VAT AMOUNT Excel:19)
				ps.setDouble(index++, gpAmountInclVat);//AP_AMOUNT_INCL_VAT(AMOUNT PAYABLE INCL VAT Excel:20)
				
				ps.setDate(index++, new java.sql.Date(new Date().getTime()));
				ps.setString(index++, monitorModel.getCreateUser());
				
				ps.setString(index++, Utils.isNull(PENS_CUST_CODE));
				ps.setString(index++, Utils.isNull(PENS_CUST_DESC));
				ps.setString(index++, Utils.isNull(PENS_GROUP));
				ps.setString(index++, Utils.isNull(PENS_GROUP_TYPE));
			
		        
		         ps.setString(index++, salesDate.substring(6,10)); // SALES_YEAR VARCHAR2(10)
		         ps.setString(index++, salesDate.substring(3,5));  //SALES_MONTH VARCHAR2(10)
		         ps.setString(index++, fileName);// File_name VARCHAR2(100)
		         ps.setString(index++, pensItem); //PENS_ITEM
		         
		         //Case LOTUS
		         ps.setDouble(index++, netSalesInclVat); //RETAIL_PRICE_BF 	= NET SALES INC. VAT (P) ขายปลีกสุทธิ รวม vat
		         ps.setDouble(index++, 0); //TOTAL_WHOLE_PRICE_BF 	= A/P AMOUNT (W) ขายส่ง
		         
			    ps.addBatch();
	    	}//for row
		
			//Insert Column For Display Result (1:no, last column ->status,Message )default column
			lineMsg ="";
			lineMsg  = "No|Line Excel|Supplier|SupplierName|Store|StoreName|ITEM(TPNB)|Description|TransDate|Qty|";
			lineMsg += "Status|Message";
			insertMonitorItemColumnHeadTableResult(connMonitor,monitorItemBean.getId(),lineMsg);
			 
			logger.debug("result foundError:"+foundError);
			if(foundError ==false){
				status = Constants.STATUS_SUCCESS;
				//no error
				int e[] = ps.executeBatch();
				logger.debug("excute count:"+e.length);
			}
			
			logger.debug("result status:"+status);
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
			monitorItemBean.setErrorMsg(errorMsg);
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

}
