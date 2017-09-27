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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;

import util.DBCPConnectionProvider;
import util.UploadXLSUtil;

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
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;

public class ImportOrderFromExcelTask_BK extends BatchTask implements BatchTaskInterface{
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

			/** Set Trasaction no Auto Commit **/
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
		int sheetNo = 0; // xls sheet no. or name
		int rowNo = 4; // row of begin data
		int maxColumnNo = 5; // max column of data per row
		Map<String, String> batchParamMap = monitorModel.getBatchParamMap();
		int totalQty = 0;
		int onhandQty = 0;
		Order order = null;
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
			   sheet = wb1.getSheetAt(sheetNo);
			   logger.debug("number of sheet: " + wb1.getNumberOfSheets());
			}else{
			   OPCPackage pkg = OPCPackage.open(dataFile.getInputStream());
			   wb2 = new XSSFWorkbook(pkg);
			   sheet = wb2.getSheetAt(sheetNo);
			   logger.debug("number of sheet: " + wb2.getNumberOfSheets());
			}
			
			/** Get Max Column StoreCode and init **/ 
			TaskStoreBean task = getStoreListByMaxColumn(sheet);
			Map<String,String> columnNoMapStoreCode = task.getStoreMap();
			maxColumnNo += columnNoMapStoreCode.size();
		    logger.debug("MaxRowNum:"+sheet.getLastRowNum());
		    
		    /** validate StoreCode is Exist **/
		    if( !task.isValid()){ //Fail
		    	Map<String,String> columnNoMapStoreCodeError = task.getStoreErrorMap();
		    	String storeCodeError = "";
		    	 Iterator its_1 = columnNoMapStoreCodeError.keySet().iterator();
				  while(its_1.hasNext()){
					  storeCodeError += columnNoMapStoreCodeError.get(its_1.next())+",";
					// lineMsg +="|FAIL|ไม่พบข้อมูลร้านค้า "+storeCodeError+" ";
				     // insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
				  }
				  
				monitorItemBean.setStatus(Constants.STATUS_FAIL);
				monitorItemBean.setSuccessCount(0);//successCount);
				monitorItemBean.setFailCount(0);
				monitorItemBean.setErrorCode("StoreCodeNotFoundException");
				monitorItemBean.setErrorMsg("ไม่พบข้อมูลร้านค้า "+storeCodeError);
				
				return monitorItemBean;
		    }
		    
			/** Check File Excel is Friday,Oshopping 7-Catalog,TV-Direct,(Bigc lotus) OR Other**/
		    String storeCodeColumnCheck = "";
		    Iterator its_1 = columnNoMapStoreCode.keySet().iterator();
		    while(its_1.hasNext()){
		    	storeCodeColumnCheck = columnNoMapStoreCode.get(its_1.next());
		    	break;
		    }
		    logger.debug("storeCodeColumnCheck:"+storeCodeColumnCheck);
		    
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
			
			/** Get OrderNoInDB key by StoreCode   **/
			Map<String,OrderKeyBean> orderNoMap = getOrderNoMap(conn,orderDate);
			//debug
			/*Iterator its = orderNoMap.keySet().iterator();
			while(its.hasNext()){
			   String key = (String)its.next();
			   OrderKeyBean o =orderNoMap.get(key); 
			   logger.debug("StoreCode["+key+"]orderNo["+o.getOrderNo()+"]");
			}*/
			
			/** Load Onhand By StoreCode By **/
			Map<String,Order> onhandMap = loadOnhandToMap(conn,storeType);
			
		    Cell cellCheck = null;
		    Object cellCheckValue = null;
		    Object cellObjValue = "";
		    
		    /** Loop Row **/
			for (r = rowNo; r <=sheet.getLastRowNum() ; r++) {
				row = sheet.getRow(r);
				logger.debug("read row["+r+"]");
				
				//Check Stop Loop Col 1
				if(row !=null){
				   cellCheck = row.getCell((short) 0);
			       cellCheckValue = xslUtils.getCellValue(0, cellCheck);
			       if(cellCheckValue != null){
				      logger.debug("RowCheck[c,r][0,"+r+"]value["+Utils.isNull(cellCheckValue.toString())+"]");
			       }
				}else{
				   cellCheckValue = null;
				}
				
				if( cellCheckValue != null && !Utils.isNull(cellCheckValue.toString()).equals("")){
					//Clear Value By Row
					order = new Order();
					nextStep = false;
					lineMsg = "";
				}else{
					logger.debug("Error :rowCheck Not Found");
				    break;	
				}
				
			    /** Loop Column **/
				for (colNo = 0; colNo < maxColumnNo; colNo++) {
					logger.debug("read row["+r+"]colNo["+colNo+"]");
					cell = row.getCell((short) colNo);
					
					/*if(cell == null){
					  logger.debug("last column :"+colNo);
					  break;
					}*/	
					
					cellObjValue = xslUtils.getCellValue(colNo, cell);
					//logger.debug("row["+i+"]col["+colNo+"]Type["+cell.getCellType()+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
					
					if(colNo ==0){
						order.setMaterialMaster(cellObjValue.toString());
						lineMsg += (r+1)+"|"+order.getMaterialMaster()+"|";
					}
					/** validate total qty in row **/
					if(colNo ==4){
						totalQty = Utils.convertStrToInt(Utils.convertDoubleStrToStr(cellObjValue.toString()));
						/** Get Detail From OnhandMap for validate **/
						orderOnhandMap = onhandMap.get(order.getMaterialMaster());
						order.setOrderDate(Utils.stringValue(orderDate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
						if(orderOnhandMap != null){
							order.setWholePriceBF(orderOnhandMap.getWholePriceBF());
							order.setRetailPriceBF(orderOnhandMap.getRetailPriceBF());
							order.setItem(orderOnhandMap.getItem());
							order.setGroupCode(orderOnhandMap.getGroupCode());
							order.setStoreType(util.Constants.STORE_TYPE_BIGC_CODE);
							order.setBarcode(orderOnhandMap.getBarcode());
							order.setExported("N");
							order.setBillType("N");	
							order.setValidFrom("00000000");
							order.setValidTo("00000000");
                            order.setCreateUser(monitorModel.getCreateUser());
							
							onhandQty = Utils.convertStrToInt(orderOnhandMap.getOnhandQty());
							
							/** add line msg **/
							lineMsg += totalQty+"|";
							/** validate Total Qty vs Onhand Qty **/
							if(totalQty >onhandQty){
								failCount++;//count row error
								foundError = true;
								/** insert row error **/
								logger.debug("Error :TotalQTY["+totalQty+"] > Onhand Qty["+onhandQty+"]");
								lineMsg += "FAIL|TotalQTY["+totalQty+"] > Onhand Qty["+onhandQty+"]";
								insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
								nextStep = false;
								break;
							}else{
								nextStep = true;
							}
						}else{
							/** Not found onhand **/
							failCount++;//count row error
							foundError = true;
							nextStep = false;
							/** insert row error **/
							logger.debug("Error Not found "+order.getMaterialMaster()+" in Onhand");
							lineMsg += totalQty+"|FAIL|ไม่พบข้อมูล "+order.getMaterialMaster()+" ใน Onhand";
							insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"FAIL",lineMsg);
							break;
						}
					}//if col 4
					
					/** Loop insert By Store code **/ 
					if(nextStep ==true && colNo >= 5){
						String storeCode = columnNoMapStoreCode.get(""+colNo);
						logger.debug("storeCode:"+storeCode);
						
						order.setStoreCode(storeCode);
						order.setQty(Utils.convertDoubleStrToStr(cellObjValue.toString()));
						
						String orderNo = "";
                    	String barOnBox = "";
                    	OrderKeyBean keyBean = null;
                    	/** Check Case Order is Exist or not **/
                    	if(orderNoMap.get(order.getStoreCode()) ==null){
                    		/** Gen New OrderNoKey **/
                    		logger.debug("Gen New OrderNoKey["+order.getStoreCode()+"]");
                    	    orderNo = OrderNoGenerate.genOrderNoKEY(conn, orderDate, order.getStoreCode());
                    	    keyBean= new OrderKeyBean(orderNo,barOnBox);
                    	    
                    	    orderNoMap.put(order.getStoreCode(), keyBean);
                    	}else{
                    		logger.debug("Get Old OrderNoKey["+order.getStoreCode()+"]");
                    		keyBean = orderNoMap.get(order.getStoreCode());
                    		orderNo = Utils.isNull(keyBean.getOrderNo());
                    	}
   
                    	logger.debug("Insert OrderNo:"+orderNo+",BarOnBox:"+barOnBox);
                    	order.setCreateUser(monitorModel.getCreateUser());
                    	order.setOrderNo(orderNo);
                    	order.setBarOnBox(barOnBox);
                    	
                        /** insert Order To DB **/
                    	OrderDAO.saveOrder(conn, order);
					}//if col >=5	
				}//for colNo
				
				/** Insert Success Log **/
				if(foundError ==false){
            	    lineMsg += "SUCCESS|บันทึกข้อมูลเรียบร้อย";
            	    logger.debug("lineMsg: \n"+lineMsg);
				    insertMonitorItemResult(connMonitor,monitorItemBean.getId(),r,"SUCCESS",lineMsg);
				    successCount++;//count row success
				}else{
					
				}
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
				    logger.debug("colNo["+columnNoCount+"]cellCheckValue["+cellCheckValue+"]rowCheck["+rowCheck+"]");
				    if(rowCheck.equals("")){
				    	nextFound = false;
				    }else{
				    	//validate StoreCode
				    	String storeName = GeneralDAO.getStoreName(rowCheck);
				    	if( "".equals(storeName)){
				    		valid = false;
				    		storeColumnErrorNoMap.put(String.valueOf(columnNoCount),rowCheck);
				    	}else{
				    		logger.debug("StoreCode:"+rowCheck);
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
				storeTypeItemCode = util.Constants.STORE_TYPE_OSHOPPING_ITEM;
			}else if("7CATALOG".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_7CATALOG";
				storeTypeItemCode = util.Constants.STORE_TYPE_7CATALOG_ITEM;
			}else if("TVDIRECT".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_TVDIRECT";
				storeTypeItemCode = util.Constants.STORE_TYPE_TVD_ITEM;
			}else if("FRIDAY".equalsIgnoreCase(Utils.isNull(pageName)) ){
				tableName = "PENSBME_ONHAND_BME_FRIDAY";
				storeTypeItemCode = util.Constants.STORE_TYPE_FRIDAY_ITEM;
			}else{
				tableName = "PENSBME_ONHAND_BME";
				storeTypeItemCode = util.Constants.STORE_TYPE_LOTUS_ITEM;
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
			
			conn = DBConnection.getInstance().getConnection();
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
	
	
}
