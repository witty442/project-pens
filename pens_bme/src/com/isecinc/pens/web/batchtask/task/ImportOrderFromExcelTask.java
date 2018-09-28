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
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.TaskStoreBean;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;
import com.pens.util.meter.MonitorTime;

public class ImportOrderFromExcelTask extends BatchTask implements BatchTaskInterface{
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
			connMonitor = DBConnection.getInstance().getConnection();

			/** Set Transaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/**debug TimeUse **/
			monitorTime  = new MonitorTime(monitorModel.getName());   
			
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item ImportOrderFromExcelTask ");
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			
			/** Validate Order is Exist **/
			boolean isOrderTodayExist = isOrderToDayExist(conn);
			if(isOrderTodayExist){
				modelItem.setStatus(Constants.STATUS_FAIL);
				modelItem.setErrorMsg("มีการ key order ไปแล้ว ไม่สามารถ import ได้");
				modelItem.setErrorCode("OrderExistException");
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

	public static MonitorItemBean runProcess(Connection connMonitor ,Connection conn,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) {
		int status = Constants.STATUS_FAIL;
		int successCount = 0;
		int failCount = 0;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		Workbook wb1 = null;
		XSSFWorkbook wb2 = null;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;
		String sheetName ="Orders";//Fix 
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 4; // row of begin data
		int maxColumnNo = 5; // max column of data per row
		//Map<String, String> batchParamMap = monitorModel.getBatchParamMap();
		int totalQtyInRow = 0;
		int onhandQty = 0;
		Order orderTemp = null;
		Order orderOnhandMap = null;
		boolean nextStep = false;
		String lineMsg = "";
		Date orderDate = new Date();// to currentDate
		int colNo = 0 ;
		int r = 0;
		boolean foundError = false;
		String storeType ="";
		Map<String,Order> orderSaveMap = new HashMap<String, Order>();
		try{
			FormFile dataFile = monitorModel.getDataFile();
			String fileName = dataFile.getFileName();
			String fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
			if("xls".equalsIgnoreCase(fileType)){
			   wb1 = new HSSFWorkbook(dataFile.getInputStream());//97-2003
			   sheet = wb1.getSheet(sheetName);
			   logger.debug("number of sheet: " + wb1.getNumberOfSheets());
			}else{
			   OPCPackage pkg = OPCPackage.open(dataFile.getInputStream());
			   wb2 = new XSSFWorkbook(pkg);
			   sheet = wb2.getSheet(sheetName);
			   logger.debug("number of sheet: " + wb2.getNumberOfSheets());
			}
			
			/** Get Max Column StoreCode and init **/ 
			TaskStoreBean task = getStoreListByMaxColumn(sheet);
			Map<String,String> columnNoMapStoreCode = task.getStoreMap();
			maxColumnNo += columnNoMapStoreCode.size();
		    logger.debug("MaxRowNum:"+sheet.getLastRowNum());
		    
		    /** Validate StoreCode is Exist **/
		    if( !task.isValid()){ //Fail
		    	Map<String,String> columnNoMapStoreCodeError = task.getStoreErrorMap();
		    	String storeCodeError = "";
		    	Iterator its_1 = columnNoMapStoreCodeError.keySet().iterator();
				while(its_1.hasNext()){
					 storeCodeError += columnNoMapStoreCodeError.get(its_1.next())+",";
				}//while
				  
				monitorItemBean.setStatus(Constants.STATUS_FAIL);
				monitorItemBean.setSuccessCount(0);//successCount);
				monitorItemBean.setFailCount(0);
				monitorItemBean.setErrorCode("StoreCodeNotFoundException");
				monitorItemBean.setErrorMsg("ไม่พบข้อมูลร้านค้า "+storeCodeError);
				
				return monitorItemBean;
		    }
		    
			/** Check File Excel is Friday,Oshopping 7-Catalog,TV-Direct,(Bigc lotus) OR Other**/
		    String storeCodeColumnCheck = "";
		    Iterator<String> its_1 = columnNoMapStoreCode.keySet().iterator();
		    while(its_1.hasNext()){
		    	storeCodeColumnCheck = columnNoMapStoreCode.get(its_1.next());
		    	break;
		    } 
		    if(storeCodeColumnCheck.startsWith("020052")){//Friday
		    	storeType ="FRIDAY";
		    }else  if(storeCodeColumnCheck.startsWith("020074")){//TVDIRECT
		    	storeType ="TVDIRECT";
		    }else  if(storeCodeColumnCheck.startsWith("020066")){//OSHOPPING
		    	storeType ="OSHOPPING";
		    }else  if(storeCodeColumnCheck.startsWith("020070")){//7CATALOG
		    	storeType ="7CATALOG";
		    }else{
		    	//BigC Lotus ,Etc
		    	storeType ="ALL";
		    }
		    logger.debug("storeCodeColumnCheck:"+storeCodeColumnCheck +",StoreType["+storeType+"]");
		    
			/** Get OrderNoInDB key by StoreCode   **/
			Map<String,OrderKeyBean> orderNoMap = getOrderNoMap(conn,orderDate);
			//debug
			//debug(orderNoMap);
			
			/** Load Onhand By StoreCode By **/
			Map<String,Order> onhandMap = loadOnhandToMap(conn,storeType);
			
		    Cell cellCheck = null;
		    Object cellCheckValue = null;
		    Object cellObjValue = "";
		    String mat = "";
		
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
					nextStep = false;
					lineMsg = "";
					orderSaveMap = new HashMap<String, Order>();
				}else{
					logger.debug("Error :rowCheck Not Found");
				    break;	
				}
				
			    /** Loop Column **/
				for (colNo = 0; colNo < maxColumnNo; colNo++) {
				//	logger.debug("read row["+r+"]colNo["+colNo+"]");
					cell = row.getCell((short) colNo);
					
					/*if(cell == null){
					  logger.debug("last column :"+colNo);
					  break;
					}*/	
					
					cellObjValue = xslUtils.getCellValue(colNo, cell);
					//logger.debug("row["+i+"]col["+colNo+"]Type["+cell.getCellType()+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
					
					if(colNo ==0){
						mat =cellObjValue.toString();
					}

					/** Loop insert By Store code **/ 
					if(colNo >= 5){
						logger.debug("row["+r+"]col["+colNo+"]value["+cellObjValue+"]");
						if(cellObjValue != null){
						  totalQtyInRow += Utils.convertStrToInt(Utils.convertDoubleStrToStr(cellObjValue.toString()));
						}
						String storeCode = columnNoMapStoreCode.get(""+colNo);
						orderTemp = new Order();
						orderTemp.setMaterialMaster(mat);
						orderTemp.setRowExcel(r+1);
						orderTemp.setStoreCode(storeCode);
						orderTemp.setStoreType(storeCode.substring(0,storeCode.indexOf("-")));
						if(cellObjValue != null){
						   orderTemp.setQty(""+Utils.convertStrToInt(Utils.convertDoubleStrToStr(cellObjValue.toString())));
						}else{
						   orderTemp.setQty("0");
						}
						logger.debug("read 1 StoreCode["+storeCode+"]");
						
						orderSaveMap.put(storeCode, orderTemp);
						logger.debug("read 2 StoreCode["+orderSaveMap.get(storeCode).getStoreCode()+"]");
						
					}//if col >=5	
				}//for colNo
				
				logger.debug("** Start Save Mat["+orderTemp.getMaterialMaster()+"]size["+orderSaveMap.size()+"]");
			
				/**  Validate And Save By Row **/
				if(orderSaveMap != null && !orderSaveMap.isEmpty()){
					Iterator<String> itsSave = orderSaveMap.keySet().iterator();
					Order orderSave = null;
					int c = 0;
					while(itsSave.hasNext()){
						String key = (String)itsSave.next();
						//logger.debug("Save Key:"+key);
						orderSave = orderSaveMap.get(key);
						//logger.debug("Save StoreCode:"+orderSave.getStoreCode());
						
						//Add Msg by Row InExcel
						if(c==0){
							lineMsg = orderSave.getRowExcel()+"|"+orderSave.getMaterialMaster()+"|";
						 }
							
						/** Get Detail From OnhandMap for validate **/
						orderOnhandMap = onhandMap.get(orderSave.getMaterialMaster());
						orderSave.setOrderDate(Utils.stringValue(orderDate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
						
						if(orderOnhandMap != null){
							orderSave.setWholePriceBF(orderOnhandMap.getWholePriceBF());
							orderSave.setRetailPriceBF(orderOnhandMap.getRetailPriceBF());
							orderSave.setItem(orderOnhandMap.getItem());
							orderSave.setGroupCode(orderOnhandMap.getGroupCode());
							orderSave.setBarcode(orderOnhandMap.getBarcode());
							orderSave.setExported("N");
							orderSave.setBillType("N");	
							orderSave.setValidFrom("00000000");
							orderSave.setValidTo("00000000");
							orderSave.setCreateUser(monitorModel.getCreateUser());
							
							onhandQty = Utils.convertStrToInt(orderOnhandMap.getOnhandQty());

							/** add line msg **/
							lineMsg += totalQtyInRow+"|";
							/** validate Total Qty vs Onhand Qty **/
							if(totalQtyInRow >onhandQty){
								failCount++;//count row error
								foundError = true;
								if(c==0){
									/** insert row error **/
									logger.debug("Error :TotalQTY["+totalQtyInRow+"] > Onhand Qty["+onhandQty+"]");
									lineMsg += "FAIL|TotalQTY["+totalQtyInRow+"] > Onhand Qty["+onhandQty+"]";
									insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
								}
								nextStep = false;
								break;
							}
						}else{
							/** Not found onhand **/
							failCount++;//count row error
							foundError = true;
							if(c==0){
								/** insert row error **/
								logger.debug("Error Not found "+orderSave.getMaterialMaster()+" in Onhand");
								lineMsg += totalQtyInRow+"|FAIL|ไม่พบข้อมูล "+orderSave.getMaterialMaster()+" ใน Onhand";
								insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
							}
						}
						
						//insert to db
						if(foundError == false){
							String orderNo = "";
	                    	String barOnBox = "";
	                    	OrderKeyBean keyBean = null;
	                    	/** Check Case Order is Exist or not **/
	                    	if(orderNoMap.get(orderSave.getStoreCode()) ==null){
	                    		/** Gen New OrderNoKey **/
	                    		logger.debug("Gen New OrderNoKey["+orderSave.getStoreCode()+"]");
	                    	    orderNo = OrderNoGenerate.genOrderNoKEY(orderDate, orderSave.getStoreCode());
	                    	    keyBean= new OrderKeyBean(orderNo,barOnBox);
	                    	    
	                    	    orderNoMap.put(orderSave.getStoreCode(), keyBean);
	                    	}else{
	                    		logger.debug("Get Old OrderNoKey["+orderSave.getStoreCode()+"]");
	                    		keyBean = orderNoMap.get(orderSave.getStoreCode());
	                    		orderNo = Utils.isNull(keyBean.getOrderNo());
	                    	}
	   
	                    	logger.debug("Insert OrderNo:"+orderNo+",BarOnBox:"+barOnBox);
	                    	orderSave.setCreateUser(monitorModel.getCreateUser());
	                    	orderSave.setOrderNo(orderNo);
	                    	orderSave.setBarOnBox(barOnBox);
	                    	
	                        /** insert Order To DB Qty == 0 no Save **/
	                    	if(Utils.convertStrToInt(orderSave.getQty(), 0) > 0){
	                    	   OrderDAO.saveOrder(conn, orderSave);
	                    	}
	                    	if(c==0){
		                    	lineMsg += "SUCCESS|บันทึกข้อมูลเรียบร้อย";
		                 	    logger.debug("lineMsg: \n"+lineMsg);
		     				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
		     				    successCount++;//count row success
	                    	}
	     			
						}//if error ==false
					    c++;
					}//while
				}//if check saveOrderMap is not null
				
				//reset TotalQtyInRow 
				totalQtyInRow = 0;
				orderSaveMap = new HashMap<String, Order>();
				
	    	}//for row
		
			if(foundError ==false){
				status = Constants.STATUS_SUCCESS;
			}
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);//successCount);
			monitorItemBean.setFailCount(failCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return monitorItemBean;	
	}
	
	/** return Map (ColNo,StoreCode)
	 * 
	 * @param sheet
	 * @return
	 */
	public static TaskStoreBean getStoreListByMaxColumn(Sheet sheet) {
		TaskStoreBean task = new TaskStoreBean();
		Row row = null;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		boolean nextFound = true;
		int columnNoCount = 5;
		int rowNoCheck = 1;
		Cell cellCheck = null;
		Object cellCheckValue = null;
		String rowCheck = "";
		Map<String,String>  storeColumnErrorNoMap = new HashMap<String, String>();
		Map<String,String>  storeColumnNoMap = new HashMap<String, String>();
		boolean valid = true;
		try{
			while(nextFound) {
				row = sheet.getRow(rowNoCheck);
			    cellCheck = row.getCell((short) columnNoCount);
			    cellCheckValue = xslUtils.getCellValue(columnNoCount, cellCheck);
			    if(cellCheckValue != null){
				    rowCheck = Utils.isNull(cellCheckValue.toString());
				   // logger.debug("colNo["+columnNoCount+"]cellCheckValue["+cellCheckValue+"]rowCheck["+rowCheck+"]");
				    if(rowCheck.equals("")){
				    	nextFound = false;
				    }else{
				    	//validate StoreCode
				    	String storeName = GeneralDAO.getStoreName(rowCheck);
				    	if( "".equals(storeName)){
				    		valid = false;
				    		storeColumnErrorNoMap.put(String.valueOf(columnNoCount),rowCheck);
				    	}else{
				    		//logger.debug("StoreCode:"+rowCheck);
				    		storeColumnNoMap.put(String.valueOf(columnNoCount),rowCheck);
				    	}
				    	
				    }
				    columnNoCount++;
			    }else{
			    	nextFound = false;
			    }
			}//while
			
			task.setValid(valid);
			task.setStoreMap(storeColumnNoMap);
			task.setStoreErrorMap(storeColumnErrorNoMap);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return task;	
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

	public static Map<String, Order> loadOnhandToMap(Connection conn,String pageName) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String, Order> onhandMap = new HashMap<String, Order>();
		Order order = null;
		String tableName = "";
		String storeTypeItemCode = "";
		try{
			if("OSHOPPING".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_OSHOPPING";
				storeTypeItemCode = com.isecinc.pens.dao.constants.Constants.STORE_TYPE_OSHOPPING_ITEM;
			}else if("7CATALOG".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_7CATALOG";
				storeTypeItemCode = com.isecinc.pens.dao.constants.Constants.STORE_TYPE_7CATALOG_ITEM;
			}else if("TVDIRECT".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_TVDIRECT";
				storeTypeItemCode = com.isecinc.pens.dao.constants.Constants.STORE_TYPE_TVD_ITEM;
			}else if("FRIDAY".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_FRIDAY";
				storeTypeItemCode = com.isecinc.pens.dao.constants.Constants.STORE_TYPE_FRIDAY_ITEM;
			}else{
				tableName = "PENSBME_ONHAND_BME";
				storeTypeItemCode = com.isecinc.pens.dao.constants.Constants.STORE_TYPE_LOTUS_ITEM;
			}
			
			StringBuffer sql = new StringBuffer("");
			sql.append("\n select material_master,onhand_qty,whole_price_bf");
			sql.append("\n , retail_price_bf,group_item ,barcode");
			sql.append("\n ,(SELECT max(m.pens_value) from PENSBME_MST_REFERENCE m   ");
			sql.append("\n   where m.reference_code ='"+storeTypeItemCode+"' and m.interface_desc = h.barcode) as item_oracle ");
			sql.append("\n from "+tableName+" h where onhand_qty <> 0 and status <> 'ERROR'  ");
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
	
			rs = ps.executeQuery();
			while(rs.next()){
				order = new Order();
				order.setMaterialMaster(Utils.isNull(rs.getString("material_master")));
				order.setWholePriceBF(Utils.isNull(rs.getDouble("whole_price_bf")));
				order.setRetailPriceBF(Utils.isNull(rs.getDouble("retail_price_bf")));
				order.setOnhandQty(rs.getInt("onhand_qty")+"");
				order.setItem(Utils.isNull(rs.getString("item_oracle")));
				order.setGroupCode(Utils.isNull(rs.getString("group_item")));
				order.setBarcode(Utils.isNull(rs.getString("barcode")));
				onhandMap.put(rs.getString("material_master"),order);
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
		return onhandMap;
	} 
	
	public static Map<String, OrderKeyBean> getOrderNoMap(Connection conn,Date orderDate) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String, OrderKeyBean> orderNoMapByStore = new HashMap<String, OrderKeyBean>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select order_no,bar_on_box,store_code \n"
					+ "from PENSBME_ORDER \n"
					+ "WHERE trunc(order_date) =to_date('"+Utils.stringValue(orderDate, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy') \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				OrderKeyBean orderKey= new OrderKeyBean(rs.getString("order_no"),rs.getString("bar_on_box"));
				orderNoMapByStore.put(rs.getString("store_code"),orderKey );
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
		return orderNoMapByStore;
	} 
	
	public static boolean isOrderToDayExist(Connection conn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean isExist = false;
		try {
			String orderDateStr = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n SELECT count(*) as c FROM PENSBME_ORDER ");
			sql.append("\n WHERE trunc(order_date) =to_date('"+orderDateStr+"','dd/mm/yyyy')");
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
