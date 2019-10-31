package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.isecinc.pens.bean.ImportSummary;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.ProductBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class ImportTransactionLotusProcess extends InterfaceUtils{

	private static Logger logger = Logger.getLogger("PENS");

	public static void runProcess(User user,MonitorBean m) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		MonitorBean monitorModel = new MonitorBean();
		boolean stepBreak = false;
		boolean step2Break = false;
		boolean rollBackFlag = false;
		boolean isExc = false;
		int taskStatusInt = Constants.STATUS_SUCCESS;
		EnvProperties env = EnvProperties.getInstance();
		String patheImport = "";
		int countFileMap = 0;
		MonitorTime monitorTime = null;
		Map<String, String> batchParamMap = new HashMap<String, String>();
	    String fileName = "";
	    String fileType = "";
	    ImportDAO importDAO  = new ImportDAO();
	    String errorCode = "SuccessException";
	    String errorMsg = ExceptionHandle.ERROR_MAPPING.get(errorCode);
		try{
			/** prepare Paramenter **/
			batchParamMap = m.getBatchParamMap();
            FormFile dataFile  = m.getDataFile();
		   
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
			
			monitorModel.setTransactionId(m.getTransactionId());
			monitorModel.setMonitorId(m.getMonitorId());
			monitorModel.setTransactionType(m.getTransactionType());
		
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**Validate Duplicate File**/
			if (dataFile != null) {
				isExc = true;
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				
				/** cehck FileName duplicate **/
				boolean dup = importDAO.importLotusFileNameIsDuplicate(conn, fileName);
				if(dup){
					taskStatusInt = Constants.STATUS_FAIL;
				}
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());
			
				if(taskStatusInt == Constants.STATUS_FAIL){
					errorCode = "DuplicateImportFileException";
					errorMsg = ExceptionHandle.ERROR_MAPPING.get(errorCode);
				}
		
				if(taskStatusInt != Constants.STATUS_FAIL){
					monitorTime = new MonitorTime("Import Excel to DB");
					
					MonitorItemBean modelItem = new MonitorItemBean();
					modelItem.setMonitorId(monitorModel.getMonitorId());
					modelItem.setSource("");
					modelItem.setDestination("");
					modelItem.setTableName("Import Transaction Lotus");
					modelItem.setFileName(fileName);
					modelItem.setStatus(Constants.STATUS_START);
					modelItem.setDataCount(0);
					modelItem.setFileSize("");
					modelItem.setSubmitDate(new Date());
					modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
					dao.insertMonitorItem(connMonitor, modelItem);
					
					modelItem = importFromLotus(conn,connMonitor,modelItem,user,dataFile,fileType , fileName);
					logger.debug("Result FailCount:"+modelItem.getFailCount() );
					logger.debug("Result SuccessCount:"+modelItem.getSuccessCount() );
					if(modelItem.getFailCount() > 0){
						rollBackFlag = true;
						taskStatusInt = Constants.STATUS_FAIL;
						errorCode = "ImportErrorException";
						errorMsg = ExceptionHandle.ERROR_MAPPING.get(errorCode);
						errorMsg = errorMsg.replaceAll("\\$", fileName);
						logger.debug("errorMsg["+errorMsg+"]");
						
						modelItem.setStatus(Constants.STATUS_FAIL);
						modelItem.setErrorCode("ImportErrorException");
						modelItem.setErrorMsg(errorMsg);
						
					}else{
						modelItem.setStatus(Constants.STATUS_SUCCESS);
						rollBackFlag = false;
					}
					/**Update Status Monitor item **/
					dao.updateMonitorItem(connMonitor, modelItem);
					
					monitorTime.debugUsedTime();	
				}//if
			}//if data file
			
			logger.debug("isExc:"+isExc+" ,rollBackFlag:"+rollBackFlag);
			
			/** Check Is Excute and No error **/
			if(isExc && rollBackFlag == false){
				logger.debug("Transaction commit");
				conn.commit();
			    
				/** End process ***/
				logger.debug("Update Monitor to Success:"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				monitorModel.setErrorCode(errorCode);
				monitorModel.setErrorMsg(errorMsg);
				dao.updateMonitor(connMonitor,monitorModel);
				
				//monitorTime.debugUsedTime();
				
			}else if(isExc && rollBackFlag == true){
				logger.debug("Transaction Rolback");
				conn.rollback();
				
				logger.debug("Update Monitor to Fail :"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				monitorModel.setErrorCode(errorCode);
				monitorModel.setErrorMsg(errorMsg);
				
				dao.updateMonitor(connMonitor,monitorModel);
			}else{
				logger.debug("Transaction commit");
				conn.commit();
				
				/** End process ***/
				logger.debug("Update Monitor to Success :"+taskStatusInt);
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(countFileMap);
				monitorModel.setTransactionType(m.getTransactionType());
				monitorModel.setErrorCode(errorCode);
				monitorModel.setErrorMsg(errorMsg);
				dao.updateMonitor(connMonitor,monitorModel);
			}
			
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
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT_TRANSACTION_LOTUS);
			
			if(conn != null){
			  logger.debug("Transaction Rolback");
			  conn.rollback();
			}
		
		}finally{
		
			if(conn != null){
				conn.close();
				conn =null;
			}
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
		}
	}
	
	public static MonitorItemBean importFromLotus(Connection conn,Connection conMoni ,MonitorItemBean mi,User user,FormFile dataFile,String fileType ,String fileName) {
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
      
	    ImportDAO importDAO = new ImportDAO();
	    List<ImportSummary> successList = new ArrayList<ImportSummary>();
	    List<ImportSummary> errorList = new ArrayList<ImportSummary>();
	 
	    Map<String, ImportSummary> successMap = new HashMap<String, ImportSummary>();
	    Map<String, ImportSummary> errorMap = new HashMap<String, ImportSummary>();
	    boolean importError = false;
		try {
			if (dataFile != null) {
				StringBuffer sql = new StringBuffer("");
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
				String salesDate = "";
				String storeNo = "";
				String storeName = "";
				String description ="";
				String qty = "";
				String styleNo = "";
				String lotusItem = "";
				double netSalesIncVat = 0;
				double apAmount = 0;
				logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
	            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
	            
				for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
					row = sheet.getRow(i);
					
					/** Check Row is null **/
					Cell cellCheck = row.getCell((short) 0);
					Object cellCheckValue = xslUtils.getCellValue(0, cellCheck);
					String rowCheck = Utils.convertDoubleToStr(Utils.isDoubleNull(cellCheckValue));
					logger.debug("rowNo["+i+"]rowCheck["+rowCheck+"]");
					if("0".equals(rowCheck)){
						break;
					}
					
					for (int colNo = 0; colNo < maxColumnNo; colNo++) {
						cell = row.getCell((short) colNo);
						//logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
						
						Object cellValue = xslUtils.getCellValue(colNo, cell);
						
						if(colNo==0){
						   //VENDOR
						   String vendor = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(1,  vendor);
						   
						}else if(colNo==1){
						   //NAME
						   ps.setString(2, Utils.isNull(cellValue));
						}else if(colNo==2){
						   //AP_TYPE
						   ps.setString(3, Utils.isNull(cellValue));
						}else if(colNo==3){
						   //LEASE_VENDOR_TYPE
						   ps.setString(4, Utils.isNull(cellValue));
						}else if(colNo==4){
						  //STORE_NO
						   storeNo = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(5, storeNo);
						}else if(colNo==5){
						  //STORE_NAME
						  storeName = Utils.isNull(cellValue);
						  ps.setString(6, storeName);
						}else if(colNo==6){
						  //STYLE_NO
						   styleNo = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						   ps.setString(7, styleNo);
						}else if(colNo==7){
						   //DESCRIPTION
						   description = Utils.isNull(cellValue);
						   ps.setString(8, description);
						   lotusItem = description.substring(0,10);
						}else if(colNo==8){
						  //COL
						  String value = Utils.convertDoubleToStr(Utils.isDoubleNull(cellValue));
						  ps.setString(9, value);
						}else if(colNo==9){
						  //SIZE_TYPE
						  ps.setString(10, Utils.isNull(cellValue));
						}else if(colNo==10){
						  //SIZES
						  String value = Utils.convertDoubleStrToStr(Utils.isNull(cellValue).toString());
						  ps.setString(11,value);
						}else if(colNo==11){
						  //SALES_DATE
						  java.util.Date asOfDate =  (java.util.Date) cellValue;
						  logger.debug("Date:"+asOfDate);
						  salesDate = DateUtil.stringValue(asOfDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
						  logger.debug("salesDate:"+salesDate);
						  ps.setTimestamp(12, new java.sql.Timestamp(asOfDate.getTime()));  
						}else if(colNo==12){
						  //QTY
						  qty = ""+Utils.isDoubleNull(cellValue);
						  ps.setDouble(13, Utils.isDoubleNull(cellValue));
						}else if(colNo==13){
						  //GROSS_SALES
						  ps.setDouble(14, Utils.isDoubleNull(cellValue));
						}else if(colNo==14){
						  //RETURN_AMT
						  ps.setDouble(15, Utils.isDoubleNull(cellValue));
						}else if(colNo==15){
						  //NET_SALES_INCL_VAT
						  netSalesIncVat = Utils.isDoubleNull(cellValue);
						  ps.setDouble(16, Utils.isDoubleNull(cellValue));
						}else if(colNo==16){
						  //VAT_AMT
						  ps.setDouble(17, Utils.isDoubleNull(cellValue));
						}else if(colNo==17){
						  //NET_SALES_EXC_VAT
						  ps.setDouble(18,Utils.isDoubleNull(cellValue));
						}else if(colNo==18){
						  //GP_PERCENT
						  ps.setDouble(19,Utils.isDoubleNull(cellValue));
						}else if(colNo==19){
						  //GP_AMOUNT
						  ps.setDouble(20, Utils.isDoubleNull(cellValue));
						}else if(colNo==20){
						  //VAT_ON_GP_AMOUNT
						  ps.setDouble(21, Utils.isDoubleNull(cellValue));
						}else if(colNo==21){
						  //GP_AMOUNT_INCL_VAT
						  ps.setDouble(22, Utils.isDoubleNull(cellValue));
						}else if(colNo==22){
						  //AP_AMOUNT
						  apAmount = Utils.isDoubleNull(cellValue);
						  ps.setDouble(23,apAmount);
						}else if(colNo==23){
						  //TOTAL_VAT_AMT
						  ps.setDouble(24, Utils.isDoubleNull(cellValue));
						}else if(colNo==24){
						  //AP_AMOUNT_INCL_VAT
						  ps.setDouble(25,Utils.isDoubleNull(cellValue));
						}
						 
					}//for column
					
					 //CREATE_DATE
					 ps.setTimestamp(26, new java.sql.Timestamp(new java.util.Date().getTime()));
					 //CREATE_USER
			         ps.setString(27, user.getUserName());
			         
			         Master mStore = importDAO.getMasterMapping(conn, "Store", storeNo);
			         //New
			         ProductBean productBean = importDAO.getProductStyleMapping(conn,styleNo);
			        
			         String pensItem = "";
			         /** case Start with  'W' no check "WB7805D4BL"**/
			         if(lotusItem.startsWith("W")){
			        	  //Add Success Msg No Check PensItem
				         ImportSummary s = new ImportSummary();
				         s.setRow(i+1);
				         s.setSalesDate(salesDate);
				         s.setStoreNo(storeNo);
				         s.setStoreName(storeName);
				         s.setDescription(description);
				         s.setQty(qty);
				         s.setMessage("Success :No Validate Pens Item");
				         successMap.put(i+"", s); 
				          
				         if(mStore !=null){
					         // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(28, mStore.getPensValue());
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(29, mStore.getPensDesc());
				         }else if(mStore ==null){
				        	 // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(28, storeNo);
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(29,storeNo); 
				         }
				         if( productBean != null){
					          ps.setString(30, Utils.isNull(productBean.getProductGroup())); // PENS_GROUP VARCHAR2(30),
					          ps.setString(31, productBean.getGroupCode()); // PENS_GROUP_TYPE VARCHAR2(30),
					     }else if(productBean==null){
						     ps.setString(30, ""); // PENS_GROUP VARCHAR2(30),
					         ps.setString(31, ""); // PENS_GROUP_TYPE VARCHAR2(30),
					     } 
				         
			         }else{
				         pensItem = productBean != null?productBean.getPensItem():"";//importDAO.getItemByInterfaceValueTypeLotusCase1(conn, util.Constants.STORE_TYPE_LOTUS_ITEM, lotusItem);
	
				         /*if(Utils.isNull(pensItem).equals("")){
				        	 lotusItem = lotusItem.substring(0,6);
				        	 pensItem = importDAO.getItemByInterfaceValueTypeLotusCase2(conn, util.Constants.STORE_TYPE_LOTUS_ITEM, lotusItem);
				         }*/

				         if(mStore != null && productBean != null && !Utils.isNull(pensItem).equals("")){
				        	 //Add Success Msg
					         ImportSummary s = new ImportSummary();
					         s.setRow(i+1);
					         s.setSalesDate(salesDate);
					         s.setStoreNo(storeNo);
					         s.setStoreName(storeName);
					         s.setDescription(description);
					         s.setQty(qty);
					         s.setMessage("Success");
					         successMap.put(i+"", s);
				         }
				         
				         if(mStore !=null){
					         // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(28, mStore.getPensValue());
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(29, mStore.getPensDesc());
				         }else if(mStore ==null){
				        	 // PENS_CUST_CODE VARCHAR2(30),
					         ps.setString(28, storeNo);
					         // PENS_CUST_DESC VARCHAR2(100),O
					         ps.setString(29,storeNo); 
				         }
				         if(productBean !=null){
					          ps.setString(30, Utils.isNull(productBean.getProductGroup())); // PENS_GROUP VARCHAR2(30),
					          ps.setString(31, productBean.getGroupCode());// PENS_GROUP_TYPE VARCHAR2(30),
					     }else if(productBean ==null){
						     ps.setString(30, "");//m.getPensDesc());
					         ps.setString(31, "");//m.getPensDesc());
					     } 
				         
				         if(mStore ==null || productBean==null || Utils.isNull(pensItem).equals("")){
				        	 //Add Error Msg
					         importError = true;
					         ImportSummary s = new ImportSummary();
					         s.setRow(i+1);
					         s.setSalesDate(salesDate);
					         s.setStoreNo(storeNo);
					         s.setStoreName(storeName);
					         s.setDescription(description);
					         s.setQty(qty);
					         
					         String ms = "";
					         if(mStore ==null){
					        	 ms +="��辺������ STORE NAME";
					         }
					         if(productBean==null){
					        	 ms +="��辺������ Group";
					         }
					         if(Utils.isNull(pensItem).equals("")){
					        	 ms +="��辺������ Pens Item"; 
					         }
					         s.setMessage(ms +" � STYLE MAPPING");
					         errorMap.put(i+"", s);
				         }  
			         }
			         
			         String year = "";
			         String month ="";
			         
			         // dd/mm/yyyy
			         year = salesDate.substring(6,10);
			         month = salesDate.substring(3,5);
			         
			         // SALES_YEAR VARCHAR2(10),
			         ps.setString(32, year);
			         //SALES_MONTH VARCHAR2(10),
			         ps.setString(33, month);
			         // File_name VARCHAR2(100),
			         ps.setString(34, fileName);
			         //PENS_ITEM
			         ps.setString(35, pensItem);
			         
			         //Case LOTUS
			         //RETAIL_PRICE_BF 	= NET SALES INC. VAT (P) ��»�ա�ط�� ��� vat
			         //TOTAL_WHOLE_PRICE_BF 	= A/P AMOUNT (W) �����
			         
			         ps.setDouble(36, netSalesIncVat);
			         ps.setDouble(37, apAmount);
			         
					 ps.executeUpdate();
				}//for Row
			}
			
			if(importError){
			    importError = true;
			   /** Success List **/
				 Iterator it = successMap.keySet().iterator();
				 while(it.hasNext()){
					  String key = (String)it.next();
					  //logger.debug("key:"+key);
					  ImportSummary mm = (ImportSummary)successMap.get(key);
					  successList.add(mm);
				  }
				 
				 //Sort
				  Collections.sort(successList, new Comparator<ImportSummary>() {
					  public int compare(ImportSummary a, ImportSummary b) {
					        return a.getRow()- b.getRow();
					    }
					});
				  
				  //Insert Success
				  if(successList != null && successList.size() >0){
					  for(int r=0;r<successList.size();r++){
						  ImportSummary m = successList.get(r);
				          insertMonitorItemResult(conMoni,mi.getId(),m.getRow(),"SUCCESS",m.getRow()+","+m.getSalesDate()+","+m.getStoreNo()+","+m.getStoreName()+","+m.getDescription()+","+m.getQty()+","+m.getMessage());
					  }
				  }
				  
				  /** Error List **/
				  it = errorMap.keySet().iterator();
					 while(it.hasNext()){
						  String key = (String)it.next();
						 // logger.debug("key:"+key);
						  ImportSummary mm = (ImportSummary)errorMap.get(key);
						  errorList.add(mm);
					  }
					 
					 //Sort
					  Collections.sort(errorList, new Comparator<ImportSummary>() {
						  public int compare(ImportSummary a, ImportSummary b) {
						        return a.getRow()- b.getRow();
						    }
						});
					  
					//Insert FALI
					  if(errorList != null && errorList.size() >0){
						  for(int r=0;r<errorList.size();r++){
							  ImportSummary m = errorList.get(r);
					          insertMonitorItemResult(conMoni,mi.getId(),m.getRow(),"FAIL",m.getRow()+","+m.getSalesDate()+","+m.getStoreNo()+","+m.getStoreName()+","+m.getDescription()+","+m.getQty()+","+m.getMessage());
						  }
					  }
					 
					/** Set Count Record **/
					mi.setSuccessCount(successList.size());
				    mi.setFailCount(errorList.size());
					  
			}else{
				
				importError = false;
				 /** Success List **/
				 Iterator it = successMap.keySet().iterator();
				 while(it.hasNext()){
					  String key = (String)it.next();
					  //logger.debug("key:"+key);
					  ImportSummary mm = (ImportSummary)successMap.get(key);
					  successList.add(mm);
				  }
				 
				 //Sort
				  Collections.sort(successList, new Comparator<ImportSummary>() {
					  public int compare(ImportSummary a, ImportSummary b) {
					        return a.getRow()- b.getRow();
					    }
					});
				//Insert Success
				  if(successList != null && successList.size() >0){
					  for(int r=0;r<successList.size();r++){
						  ImportSummary m = successList.get(r);
				          insertMonitorItemResult(conMoni,mi.getId(),m.getRow(),"SUCCESS",m.getRow()+","+m.getSalesDate()+","+m.getStoreNo()+","+m.getStoreName()+","+m.getDescription()+","+m.getQty()+","+m.getMessage());
					  }
				  }
				/** Set Count Record **/
				mi.setSuccessCount(successList.size());
			    mi.setFailCount(errorList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			//request.setAttribute("Message","������������١��ͧ:"+e.toString());
			importError = true;
		} finally {
			try {
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mi;
	}
	
	
	private static void insertMonitorItemResult(Connection conn,BigDecimal monitorItemId,int no,String status,String msg) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO MONITOR_ITEM_RESULT(MONITOR_ITEM_ID, STATUS, MESSAGE,NO)VALUES(?,?,?,?) ";
			//logger.info("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, monitorItemId);
			ps.setString(++index,status);
			ps.setString(++index,msg);
			ps.setInt(++index,no);
			
			int r = ps.executeUpdate();
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
}
