package com.isecinc.pens.summary.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import meter.MonitorTime;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BMECControlDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.sql.ReportEndDateLotusSQL;

public class GenerateReportEndDateLotus {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static MonitorBean process(MonitorBean monitorModel,User user,HttpServletRequest request) throws Exception{
		Connection conn = null;
		Connection connMonitor = null;
		InterfaceDAO dao = new InterfaceDAO();
		Map<String, Object> batchParamMap = new HashMap<String, Object>();
		MonitorTime monitorTime = null;
		int taskStatusInt = Constants.STATUS_START;
		try{
			
			/** prepare Paramenter **/
			batchParamMap = monitorModel.getBatchParamMapObj();
			OnhandSummary summary = (OnhandSummary)batchParamMap.get("ONHAND_SUMMARY");
			logger.debug("customerCode:"+summary.getPensCustCodeFrom());
			logger.debug("salesdate:"+summary.getSalesDate());
			
			/** Connection Monitor */
			connMonitor = DBConnection.getInstance().getConnection();
				
			/** Set Trasaction no Auto Commit **/
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
		
			/** insert to monitor_item **/
			logger.debug("Insert Monitor Item  TableName:Genreate Stock End Date Lotus");
			MonitorItemBean modelItem = new MonitorItemBean();
			modelItem.setMonitorId(monitorModel.getMonitorId());
			modelItem.setSource("");
			modelItem.setDestination("");
			modelItem.setTableName("Genreate Stock Report End Date Lotus");
			modelItem.setFileName("");
			modelItem.setStatus(Constants.STATUS_START);
			modelItem.setDataCount(0);
			modelItem.setFileSize("");
			modelItem.setSubmitDate(new Date());
			modelItem.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
			
			monitorTime  = new MonitorTime("Generate Stock Report End Date Lotus");   
			
			//Validate BME_ORDER EXPORTED 
			/*String re[] = BMECControlDAO.canGenEndDateLotus(conn,summary.getPensCustCodeFrom(),summary.getSalesDate());
			if("false".equals(re[0])){
				taskStatusInt = Constants.STATUS_FAIL;
			    modelItem.setErrorMsg("กรุณาตรวจสอบวันที่จะ End date ต้องมากกว่าการ End date ครั้งก่อนหน้านี้");
				modelItem.setErrorCode("SalesDateException");
			}*/
			
			if(taskStatusInt == Constants.STATUS_START){
				//Process Generate 
				modelItem = generateReportEndDateLotus(conn,modelItem,summary, user);
				
				monitorTime.debugUsedTime();
			
				//Insert Monitor Item
				modelItem = dao.insertMonitorItem(connMonitor,modelItem);
				
				logger.debug("Transaction commit");
				taskStatusInt = Constants.STATUS_SUCCESS;
				conn.commit();
				
				/** Update Head Monitor **/
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			}else{
				/** Update Head Monitor **/
				monitorModel.setStatus(taskStatusInt);
				monitorModel.setErrorCode(modelItem.getErrorCode());
				monitorModel.setErrorMsg(modelItem.getErrorMsg());
				monitorModel.setFileCount(modelItem.getSuccessCount()>0?1:0);
			}
	
			/** Update Monitor **/
			dao.updateMonitor(connMonitor,monitorModel);

		}catch(Exception e){
			logger.error(e.getMessage(),e);
			
			/** End process ***/
			logger.debug("Update Monitor to Fail ");
			monitorModel.setStatus(Constants.STATUS_FAIL);
			monitorModel.setBatchTaskStatus(Constants.STATUS_SUCCESS);//Thread batchTask end process
			monitorModel.setTransactionType(monitorModel.getTransactionType());
			monitorModel.setErrorCode(ExceptionHandle.getExceptionCode(e));
			
			dao.updateMonitorCaseError(connMonitor,monitorModel);

			//clear Task running for next run
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_GEN_STOCK_ENDDATE_LOTUS);
			
			if(conn != null){
			  logger.debug("Transaction Rolback");
			  conn.rollback();
			}
		
		}finally{
		
			if(conn != null){
				conn.setAutoCommit(true);
				conn.close();
				conn =null;
			}
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
		}
		return monitorModel;
	}
	
	public static MonitorItemBean generateReportEndDateLotus(Connection conn,MonitorItemBean monitorItemBean,OnhandSummary cri,User user) throws Exception{
		PreparedStatement ps = null;
		PreparedStatement psIns = null;
		PreparedStatement psDel = null;
	    ResultSet rst = null;
	    StringBuilder sql = new StringBuilder();
	    int result =0;
	    int c =1;
		try{
			Date asofDate = Utils.parse(cri.getSalesDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String asOfDateStr = Utils.stringValue(asofDate, Utils.DD_MM_YYYY_WITH_SLASH);
	
			//delete old month by yearMonth
			String sqlDel = "delete from PENSBME_ENDDATE_STOCK_TEMP where store_code ='"+cri.getPensCustCodeFrom()+"'";
			logger.debug("sqlDel:"+sqlDel);
			psDel = conn.prepareStatement(sqlDel);
			psDel.execute();
			
			//gen sql
			sql = ReportEndDateLotusSQL.genSQL(conn, cri, user, "GroupCode","GenReportEndDate");
			
			//sql insert 
			String sqlIns = "INSERT INTO PENSBME_ENDDATE_STOCK_TEMP" +
					"( STORE_CODE, ENDING_DATE, GROUP_CODE" +
					", SALE_IN_QTY, SALE_OUT_QTY, SALE_RETURN_QTY" +
					", ADJUST_QTY, SHORT_QTY, ENDING_QTY" +
					", CREATE_USER, CREATE_DATE ,Begining_qty) "+
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			psIns = conn.prepareStatement(sqlIns.toString());
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
				
				psIns.setString(1, rst.getString("customer_code"));
				psIns.setDate(2,new java.sql.Date(asofDate.getTime()));
				psIns.setString(3, rst.getString("group_type"));
				psIns.setDouble(4, rst.getDouble("sale_in_qty"));
				psIns.setDouble(5, rst.getDouble("sale_out_qty"));
				psIns.setDouble(6, rst.getDouble("sale_return_qty"));
				psIns.setDouble(7, rst.getDouble("ADJUST_QTY"));
				psIns.setDouble(8, rst.getDouble("STOCK_SHORT_QTY"));
				psIns.setDouble(9, rst.getDouble("onhand_qty"));
				psIns.setString(10, user.getUserName());
				psIns.setTimestamp(11, new java.sql.Timestamp(new Date().getTime()));
				psIns.setDouble(12, rst.getDouble("BEGINING_qty"));
				
				psIns.executeUpdate();
			}
			result = Constants.STATUS_SUCCESS;
		
			monitorItemBean.setStatus(result);
		}catch(Exception e){
			throw e;
		}finally{
			if(rst != null)rst.close();
			if(ps != null) ps.close();
			if(psIns != null)psIns.close();
		}
		return monitorItemBean;	
	}
	
	
	
	 public static String getEndDateStock(String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String r = "";
			try {
				//Date asofDateTemp = Utils.parse(asOfdate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				//String christAsOfDateStr = Utils.stringValue(asofDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n select distinct max(ending_date) as max_ending_date FROM PENSBME_ENDDATE_STOCK WHERE 1=1 ");
				sql.append("\n and store_code ='"+storeCode+"'");
			
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()){
					r = Utils.stringValue(rst.getDate("max_ending_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				}
				
				return r;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			
		}

}
