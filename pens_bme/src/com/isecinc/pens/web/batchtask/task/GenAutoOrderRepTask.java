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

import oracle.jpub.runtime.Util;

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

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.TaskStoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.process.OrderKeyBean;
import com.isecinc.pens.process.OrderNoGenerate;
import com.isecinc.pens.web.batchtask.BatchTask;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskInterface;
import com.isecinc.pens.web.batchtask.BatchTaskListBean;
import com.isecinc.pens.web.batchtask.subtask.GenStockOnhandRepTempLotusSubTask;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.meter.MonitorTime;
import com.pens.util.seq.SequenceProcess;

public class GenAutoOrderRepTask extends BatchTask implements BatchTaskInterface{
	public static Logger logger = Logger.getLogger("PENS");
	public static String PARAM_ORDER_DATE ="ORDER_DATE";
	public static String PARAM_STORE_CODE ="STORE_CODE";
	
	/*public void run(MonitorBean monitorModel){
		logger.debug("TaskName:"+monitorModel.getName());
		logger.debug("transactionId:"+monitorModel.getTransactionId());
	}*/
	
	/**
	 * Return :Param Name|Param label|Param Type|default value|validate$Button Name
	 */

	public String[] getParam(){
		String[] param = new String[1];
		param[0] = "temp|temp|TEXT||VALID";
		return param;
	}
	public List<BatchTaskListBean> getParamListBox(){
		return null;
	}
	public String getButtonName(){
		return "No BTN Is Run From Popup";
	}
	
	public String getDescription(){
		String desc = "Gen Stock Auto Order REP(Lotus,BigC) <br/>";
		return desc;
	}
	public String getDevInfo(){
		return "PENSBI.BME_ORDER_REP";
	}
	public boolean isDispDetail(){
		return true;
	}
	public String getValidateScript(){
		String script ="";
		script +="<script>";
		script +="\n function validate(){";
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
			MonitorItemBean modelItem = prepareMonitorItemBean(monitorModel);
			monitorModel.setTransactionType(Constants.TRANSACTION_BME_TYPE);
			
			/** Validate StoreCode is generated**/
			String storeCode = monitorModel.getBatchParamMap().get(PARAM_STORE_CODE);
			String orderDate = monitorModel.getBatchParamMap().get(PARAM_ORDER_DATE);
			logger.info("Start Gen StoreCode["+storeCode+"]OrderDate["+orderDate+"]...");
			
			/** Start process **/ 
			modelItem = process(connMonitor, connMonitor, monitorModel, modelItem);

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
			monitorModel.setThName("Gen Order àµ×ÁàµçÁ");
			monitorModel.setType("GEN");
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

	public static boolean isStoreGenerated(Connection conn,String storeCode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean isExist = false;
		try {
			sql.append(" select count(*) as c from PENSBI.BME_TEMP_ONHAND_REP \n");
			sql.append(" WHERE STORE_CODE ='"+Utils.isNull(storeCode)+"' \n");
			sql.append(" AND TRUNC(CREATE_DATE)  = TRUNC(SYSDATE)");
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

	public static MonitorItemBean process(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean) throws Exception{
		int status = Constants.STATUS_FAIL;
		int successCount = 0;int dataCount=0;int failCount = 0;
		int r = 0;
		boolean foundError = false;boolean passValid = false;String lineMsg = "";String errorMsg = "";
		PreparedStatement ps =null,psIns=null;
		ResultSet rst = null;
		int index = 0;
		StringBuffer sql = new StringBuffer("");
		double salesQty=0,shopOnhandQty=0,wacoalOnhandQty=0;
		double orderQty=0;
		double salesQtyPerDay= 0;
		double stockByCoverDay = 0;
		double recommandQty =0,recommandCalcQty =0;
		int maxQtyConfig = 0,minQtyConfig=0;
		Map<String, String> matMinMaxMap = new HashMap<String, String>();
		String[] minMaxArr = null;
		try{
		    //get parameter config 
			String orderDate = monitorModel.getBatchParamMap().get(PARAM_ORDER_DATE);
			String storeCode = monitorModel.getBatchParamMap().get(PARAM_STORE_CODE);
			
			Date orderDateObj = DateUtil.parse(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String orderDateStr = DateUtil.stringValue(orderDateObj, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			int coverDay = Utils.convertToInt(getConfig(conn, storeCode,"replenishment_coverday"));
		    int backSalesDay = Utils.convertToInt(getConfig(conn, storeCode,"replenishment_backsales"));
			matMinMaxMap = getMatMinMaxMapConfig(conn, "replenishment_minmax",storeCode);
			
			//gen sql
			sql.append("\n SELECT R.* ");
			sql.append("\n ,(W.ONHAND_QTY - ");
			//subtract BME ORDER OrderDate =Sysdate
			sql.append("\n  ( SELECT NVL(SUM(O.QTY),0) FROM PENSBI.PENSBME_ORDER O ");
			sql.append("\n    WHERE O.ORDER_DATE = TO_DATE('"+orderDateStr+"','dd/mm/yyyy') ");
			sql.append("\n    AND R.PENS_ITEM = O.ITEM ) ");
			sql.append("\n  )AS WACOAL_ONHAND_QTY ");
			sql.append("\n from PENSBI.BME_TEMP_ONHAND_REP R ,PENSBI.PENSBME_ONHAND_BME W ");
			sql.append("\n WHERE R.PENS_ITEM = W.PENS_ITEM ");
			sql.append("\n AND R.store_code ='"+storeCode+"' ");
			
			//for test 
			//sql.append("\n and R.group_code ='ME1D59X2NN' \n");
			
			logger.debug("sql \n"+sql.toString());
			
			//sql insert 
			String sqlIns  = "INSERT INTO PENSBI.BME_ORDER_REP";
			       sqlIns +="(  STORE_CODE,ORDER_DATE,STORE_TYPE, PENS_ITEM, GROUP_CODE,ORDER_QTY";
			       sqlIns +=" , BACKSALES_DAY,DAY_COVER,SALES_QTY_PER_DAY,STOCK_BY_COVER_DAY,RECOMMAND_CALC_QTY";
			       sqlIns += ", RECOMMAND_QTY, SHOP_ONHAND_QTY,SALES_QTY, CREATE_USER, CREATE_DATE,MIN_QTY,MAX_QTY) ";
			       sqlIns +=" VALUES(?, ?, ?, ?, ?,?, ?,?,?,?,?,?,?,?,?,?,?,?)";
			psIns = conn.prepareStatement(sqlIns.toString());
					
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
				
				//Calc order qty
				minQtyConfig = 0;
				maxQtyConfig = 0;
				salesQty=0;
				shopOnhandQty=0;//on shop stock
				wacoalOnhandQty=0;
				orderQty =0;
				salesQtyPerDay = 0;//salesQty/backsalesDay
				stockByCoverDay =0;//salesQtyPerDay*coverDay
				recommandQty = 0;//stockByCoverDay-onhandQty(on shop stock)
				
				salesQty = rst.getInt("sales_qty");
				shopOnhandQty = rst.getInt("SHOP_ONHAND_QTY");//(on shop stock)
				wacoalOnhandQty = rst.getInt("WACOAL_ONHAND_QTY");
				salesQtyPerDay = (salesQty/backSalesDay);
				stockByCoverDay = (salesQtyPerDay*coverDay);
				recommandCalcQty = stockByCoverDay-shopOnhandQty;
				
				//Get MinMaxBy GroupCode(ME1085)
				logger.debug("groupCode:"+rst.getString("group_code").substring(0, 6));
				if(matMinMaxMap.get(rst.getString("group_code").substring(0, 6))!= null){
					minMaxArr = matMinMaxMap.get(rst.getString("group_code").substring(0, 6)).split("\\|");
					if(minMaxArr != null){
					   minQtyConfig = Utils.convertStrToInt(minMaxArr[0]);
					   maxQtyConfig = Utils.convertStrToInt(minMaxArr[1]);
					}
				}//if minMaxMap
				logger.debug("GroupCode["+rst.getString("group_code")+"]recommandCalcQty["+recommandCalcQty+"]");
				logger.debug("salesQty["+salesQty+"]wacoalOnhandQty["+wacoalOnhandQty+"]");
				logger.debug("min["+minQtyConfig+"]max["+maxQtyConfig+"]");
				if(salesQty==0){//Case 1
					orderQty = 0;
					recommandQty = 0;
				}else{
					if(wacoalOnhandQty != 0){
						if(recommandCalcQty > maxQtyConfig && maxQtyConfig != 0){//case 2
							if(maxQtyConfig > wacoalOnhandQty){ 
								orderQty = wacoalOnhandQty;
								recommandQty = wacoalOnhandQty;
							}else{
								orderQty = maxQtyConfig;
								recommandQty = maxQtyConfig;
							}
						}else if( recommandCalcQty < minQtyConfig && minQtyConfig != 0){//case 3
							orderQty = 0;
							recommandQty = 0;
						}else{
							if(recommandCalcQty > wacoalOnhandQty){ //case4 
								orderQty = wacoalOnhandQty;
								recommandQty = wacoalOnhandQty;
							}else{
							    orderQty = recommandCalcQty;//normal
							    recommandQty = recommandCalcQty;//normal
							}
						}//if
					}else{
						//wacoal onhand Qty =0
						orderQty = 0;
						recommandQty = 0;
					}//if
				}//if
				
				//insert ps
				psIns.setString(++index, storeCode);
				psIns.setDate(++index,new java.sql.Date(DateUtil.parse(orderDate, DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
				psIns.setString(++index, storeCode.substring(0,5));
				psIns.setString(++index, rst.getString("pens_item"));
				psIns.setString(++index, rst.getString("group_code"));
				psIns.setDouble(++index, orderQty);
				//optional for display
				psIns.setDouble(++index, backSalesDay);
				psIns.setDouble(++index, coverDay);
				psIns.setDouble(++index, salesQtyPerDay);
				psIns.setDouble(++index, stockByCoverDay);
				psIns.setDouble(++index, recommandCalcQty);
				psIns.setDouble(++index, recommandQty);
				psIns.setDouble(++index, shopOnhandQty);
				psIns.setDouble(++index, salesQty);
				psIns.setString(++index, monitorModel.getCreateUser());
				psIns.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
				psIns.setDouble(++index, minQtyConfig);
				psIns.setDouble(++index, maxQtyConfig);
				psIns.addBatch();
				index = 0;
			}
	
			if(foundError ==false){
				logger.info("Execute Batch");
				psIns.executeBatch();
				status = Constants.STATUS_SUCCESS;
			}
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);//successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			psIns.close();psIns=null;
			ps.close();ps=null;
		}
		return monitorItemBean;	
	}
	
	public static String getConfig(Connection conn,String storeCode,String refCode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String backSalesDay = "";
		try {
			sql.append(" select interface_value from PENSBI.PENSBME_MST_REFERENCE \n");
			sql.append(" WHERE reference_code = '"+refCode+"' \n");
			sql.append(" AND '"+Utils.isNull(storeCode)+"' LIKE PENS_VALUE||'%' \n");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				backSalesDay = rst.getString("interface_value");
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return backSalesDay;
	}
	public static Map<String, String> getMatMinMaxMapConfig(Connection conn,String refCode,String storeCode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String minMax = "";
		Map<String, String> matMinMaxMap = new HashMap<String, String>();
		try {
			//Get Priority By Store For Get MinMax
			String priorityByStore = getStorePriorityConfig(conn ,"replenishment_priority", storeCode);
			
			sql.append(" select pens_value as group_code,pens_desc as min ,pens_desc2 as max \n");
			sql.append(" from PENSBI.PENSBME_MST_REFERENCE \n");
			sql.append(" WHERE reference_code = '"+refCode+"' \n");
			sql.append(" and  interface_value ='"+priorityByStore+"'");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				minMax = rst.getString("min")+"|"+rst.getString("max");
				matMinMaxMap.put(rst.getString("group_code"), minMax);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return matMinMaxMap;
	}
	public static String getStorePriorityConfig(Connection conn,String refCode,String storeCode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String priority = "";
		try {
			sql.append(" select interface_value \n");
			sql.append(" from PENSBI.PENSBME_MST_REFERENCE \n");
			sql.append(" WHERE reference_code = '"+refCode+"' \n");
			sql.append(" and pens_value ='"+storeCode+"'");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				priority = rst.getString("interface_value");
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return priority;
	}
}
