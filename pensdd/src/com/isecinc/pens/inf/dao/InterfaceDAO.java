package com.isecinc.pens.inf.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.MonitorItemDetailBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ConvertUtils;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.SequenceHelper;
import com.isecinc.pens.inf.helper.Utils;

/**
 * @author WITTY
 *
 */
public class InterfaceDAO {

	protected static  Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public MonitorBean insertMonitor(Connection conn ,MonitorBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO monitor(" +
			" transaction_id," +
			" monitor_id," +
			" name ," +
			" type ," +
			" channel  ," +
			" sale_rep_code ," +
			" transaction_type ," +
			" submit_date ," +
			" status," +
			" create_date ," +
			" create_user ," +
			" file_count )" +
			" VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ";
			  
			logger.debug("SQL:"+sql);
			if(model.getTransactionId() ==null){
				   model.setTransactionId(new BigDecimal(SequenceHelper.getNextValue("monitor")));
			}
			if(model.getMonitorId() ==null){
				   model.setMonitorId(new BigDecimal(SequenceHelper.getNextValue("monitor_2")));
			}
			logger.debug("id:"+model.getTransactionId());

			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, model.getTransactionId());
			ps.setBigDecimal(++index, model.getMonitorId());
			ps.setString(++index, model.getName());
			ps.setString(++index, model.getType());
			ps.setString(++index, model.getChannel());
			ps.setString(++index, model.getSaleRepCode());
			ps.setString(++index, model.getTransactionType());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setInt(++index, model.getStatus());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, model.getCreateUser());
			ps.setInt(++index, model.getFileCount());
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return model;
	}
	
	public MonitorItemBean insertMonitorItem(Connection conn,MonitorItemBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO monitor_item(" +
			" id,"+
			" monitor_id ,"+
			" source ," +
			" destination ," +
			" table_name,"+
			" file_name ,"+
			" submit_date ," +
			" data_count ," +
			" status ," +
			" error_code," +
			" error_msg," +
			" group_name," +
			" file_size ," +
			" success_count)"+
			" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			  
			logger.debug("SQL:"+sql);
			logger.debug("SucessCound:"+model.getSuccessCount());
			if(model.getId() ==null){
				   model.setId(new BigDecimal(SequenceHelper.getNextValue("monitor_item")));
			}
			
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, model.getId());
			ps.setBigDecimal(++index, model.getMonitorId());
			ps.setString(++index, model.getSource());
			ps.setString(++index, model.getDestination());
			ps.setString(++index, model.getTableName());
			ps.setString(++index, model.getFileName());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setInt(++index, model.getDataCount());
			ps.setInt(++index, model.getStatus());
			ps.setString(++index, Utils.isNull(model.getErrorCode()).length()> 300?model.getErrorCode().substring(0,299):model.getErrorCode());
			ps.setString(++index, Utils.isNull(model.getErrorMsg()).length()> 300?model.getErrorMsg().substring(0,299):model.getErrorMsg());
			ps.setString(++index, model.getGroupName());
			ps.setString(++index, model.getFileSize());
			ps.setInt(++index, model.getSuccessCount());
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();
				ps = null;
			}
		}
		return model;
	}
	
	public MonitorItemBean insertMonitorItemDetail(Connection conn ,MonitorItemDetailBean[] monitorItemDetailBean ) throws Exception {
		PreparedStatement psIns = null;
		ResultSet rs =null;
		int index = 0;
		int countReceord = 0;
		try {
			logger.debug("*** insertMonitorItemDetail **********");
			
			String sql = "INSERT INTO monitor_item_detail(" +
			" MONITOR_ITEM_ID," +
			" customer_code ," +
			" customer_name ," +
			" code  ," +
			" type ," +
			" amount )" +
			" VALUES ((select max(id) from monitor_item),?,?,?,?,?) ";
			  
			logger.debug("SQL:"+sql);
			if( monitorItemDetailBean != null && monitorItemDetailBean.length >0){
				psIns = conn.prepareStatement(sql);
				for(int i=0;i<monitorItemDetailBean.length;i++){
					MonitorItemDetailBean item = monitorItemDetailBean[i];  
				    psIns.setString(++index, item.getCustomerCode());
				    psIns.setString(++index, item.getCustomerName());
				    psIns.setString(++index, item.getCode());
				    psIns.setString(++index, item.getType());
				    psIns.setDouble(++index, item.getAmount());
				    
				    index = 0;
				    psIns.addBatch();
				}
				countReceord= psIns.executeBatch().length;
				logger.debug("ins:"+countReceord);
			}

		} catch (Exception ex) {
			throw ex;
		} finally {
			if(psIns != null){
				psIns.close();psIns = null;
			}
			if(rs != null){
				rs.close();rs = null;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param conn
	 * @param monitorItemBean
	 * @param sqlSelect
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public MonitorItemDetailBean[] prepareMonitorItemDetail(Connection conn ,String sqlSelect,String type) throws Exception {
		boolean result = false;
		PreparedStatement psSelect = null;
		ResultSet rs =null;
		int index = 0;
		int countReceord = 0;
		List<MonitorItemDetailBean> itemList = new ArrayList<MonitorItemDetailBean>();
		MonitorItemDetailBean[] m = null;
		try {
			logger.debug("*** prepareMonitorItemDetail **********");
			logger.debug("SQLSelect:"+sqlSelect);			
			if( !Utils.isNull(sqlSelect).equals("")){
				psSelect = conn.prepareStatement(sqlSelect);
				rs = psSelect.executeQuery();
				while(rs.next()){
					MonitorItemDetailBean item = new MonitorItemDetailBean(); 
					logger.debug("type:"+type);
				   if(type.equalsIgnoreCase("t_order")){
					   item.setCustomerCode(rs.getString("CUSTOMER_NUMBER"));
					   item.setCustomerName(rs.getString("CUSTOMER_NAME"));
					   item.setCode(rs.getString("ORDER_NUMBER"));
					   item.setType(type);
					   item.setAmount(rs.getDouble("NET_AMOUNT"));
				   }else if(type.equalsIgnoreCase("t_order_dd")){
					   item.setCustomerCode(rs.getString("CUSTOMER_NUMBER"));
					   item.setCustomerName(rs.getString("CUSTOMER_NAME"));
					   item.setCode(rs.getString("ORDER_NUMBER"));
					   item.setType(type);
					   item.setAmount(rs.getDouble("NET_AMOUNT"));
				   }else  if(type.equalsIgnoreCase("t_visit")){
					   item.setCustomerCode(rs.getString("CUSTOMER_NUMBER"));
					   item.setCustomerName(rs.getString("CUSTOMER_NAME"));
					   item.setCode(rs.getString("code"));
					   item.setType(type);
					   item.setAmount(0);
				   }else if(type.equalsIgnoreCase("t_receipt")){
					   item.setCustomerCode(rs.getString("CUSTOMER_NUMBER"));
					   item.setCustomerName(rs.getString("CUSTOMER_NAME"));
					   item.setCode(rs.getString("RECEIPT_NO"));
					   item.setType(type);
					   item.setAmount(rs.getDouble("amount")); 
				   }else if(type.equalsIgnoreCase("t_receipt_dd")){
					   item.setCustomerCode(rs.getString("CUSTOMER_NUMBER"));
					   item.setCustomerName(rs.getString("CUSTOMER_NAME"));
					   item.setCode(rs.getString("RECEIPT_NO"));
					   item.setType(type);
					   item.setAmount(rs.getDouble("amount")); 
				   }else if(type.equalsIgnoreCase("m_customer")){
					   item.setCustomerCode(rs.getString("CUSTOMER_NUMBER"));
					   item.setCustomerName(rs.getString("CUSTOMER_NAME"));
					   item.setCode("");
					   item.setType(type);
					   item.setAmount(0); 
				   }
				   itemList.add(item);
				}
				m = new MonitorItemDetailBean[itemList.size()];
				m = itemList.toArray(m);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
		
			if(psSelect != null){
				psSelect.close();psSelect = null;
			}
			if(rs != null){
				rs.close();rs = null;
			}
		}
		return m;
	}
	/**
	 * 
	 * @param conn
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public  BigDecimal findMaxMonitorId(Connection conn,MonitorBean model) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		BigDecimal monitorId = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select max(monitor_id) as monitor_id from monitor \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				monitorId = rs.getBigDecimal("monitor_id");
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
		return monitorId;
	} 
	
	/**
	 * 
	 * @param conn
	 * @param model
	 * @return
	 * @throws Exception
	 * return 0 (start task ) , 1 (finish task)
	 */
	public  String findMonitorStatus(Connection conn,String id,String transaction_count) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String status = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select channel as batch_task_status from monitor \n");
			sql.append("where MONITOR_ID = (select min(monitor_id) from monitor where transaction_id ="+id+") \n");
			
			logger.debug("transaction_count:"+transaction_count);
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				status = rs.getString("batch_task_status");
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
		return status;
	} 
	
	/**
	 * 
	 * @param conn
	 * @param model
	 * @param status
	 * @return
	 * @throws Exception
	 * Desc : for Control Status of BatchTask borrow filed(channel = store status )
	 */
	public void updateControlStatusMonitor(Connection conn,BigDecimal transactionId,BigDecimal monitorId,String status) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE monitor SET  channel = ? WHERE MONITOR_ID = ? and transaction_id = ?";
			
			logger.debug("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setString(++index,status);
			ps.setBigDecimal(++index, monitorId);
			ps.setBigDecimal(++index, transactionId);
			
			ps.executeUpdate();
		
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	
	
	public  String findMonitorStatusBK(Connection conn,String id,String transaction_count) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String status = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select monitor_id,transaction_type,type ,status,error_code , \n (select count(*) from monitor where transaction_id ="+id+" ) as transaction_count  \n  from monitor where transaction_id ="+id+" order by monitor_id desc  \n");
			
			logger.debug("transaction_count:"+transaction_count);
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			//1) IMPORT ,1 Trans MANUAL
			//2) EXPORT ,1 Trans MANUAL
			//3) IMPORT ,3 Trans,NORMAL   
			//4) EXPORT ,2 Trans NORMAL
			if(rs.next()){
				/** New code  */
				/** Case Can't Connection FTP Server  Break All Check****/
				if(Utils.isNull(rs.getString("error_code")).equals("FTPException")){ //Case FtpException Break Task ALL
					 status = rs.getString("status");
				}else{
					if(Utils.isNull(rs.getString("TYPE")).equals("IMPORT")){
						if(transaction_count.equals(1+"") ){ //IMPORT MANUAL  Transaction_count =1
						   status = rs.getString("status");
						}else{ //IMPORT Normal  Transaction_count =3
							logger.debug("TransaType:"+Utils.isNull(rs.getString("transaction_type")));
							logger.debug("STATUS:"+Utils.isNull(rs.getString("STATUS")));
							logger.debug("TransaCount:"+rs.getString("transaction_count"));
							
							if(Utils.isNull(rs.getString("transaction_type")).equals(Constants.TRANSACTION_MASTER_TYPE) && 
									Utils.isNull(rs.getString("STATUS")).equals(Constants.STATUS_FAIL+"")){
								if(rs.getString("transaction_count").equals(2+"") ){//Update ,Master (FAIL) no run Trans = 2
									status = rs.getString("status");
								}
							}else{
								if(rs.getString("transaction_count").equals(transaction_count+"") ){//Update ,Master,Trans = 3 (ALL)
									status = rs.getString("status");
								}
							}
						}
					}else if(Utils.isNull(rs.getString("TYPE")).equals("EXPORT")){
						//EXPORT ,1 MANUAL
						//EXPORT ,2 NORMAL
						if(rs.getString("transaction_count").equals(transaction_count+"") ){//Case Success Check All Transaction
						   status = rs.getString("status");
						}
					}
				}
				
				/** Olde code **/
				//status = rs.getString("status");
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
		return status;
	} 
	
	public  MonitorBean findMonitorBean(BigDecimal monitorId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorBean  m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from monitor where 1=1  and monitor_id ="+monitorId+"\n");
	
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				 m = new MonitorBean();
				m.setTransactionId(rs.getBigDecimal("transaction_id"));
				m.setTransactionType(rs.getString("TRANSACTION_TYPE"));
				m.setMonitorId(rs.getBigDecimal("monitor_id"));
				m.setName(rs.getString("name"));
				m.setType(rs.getString("type"));
				m.setStatus(rs.getInt("status"));
				m.setCreateUser(rs.getString("create_user"));
				m.setCreateDate(rs.getDate("create_date"));
				m.setSubmitDate(rs.getTimestamp("submit_date"));
	            m.setFileCount(rs.getInt("file_count"));
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
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return m;
	} 
	
	/**
	 * 
	 * @param conn
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean[] findMonitorList(User user) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorBean[] monitorBean = null;
		List<MonitorBean> monitorList = new ArrayList<MonitorBean> ();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  \n");
			sql.append(" , (select count(*) from monitor_item where monitor_item.monitor_id = monitor.monitor_id and status = "+Constants.STATUS_SUCCESS+" )  as success_count  \n");
			sql.append(" , (select error_msg from monitor_error_mapping where monitor_error_mapping.error_code = monitor.error_code) as error_disp \n");
			sql.append(" from monitor where 1=1 \n");
			sql.append(" and  (monitor.transaction_id) in (select max(transaction_id) from monitor  where create_user like '%"+user.getUserName()+"%')  \n");
			sql.append(" order by monitor.submit_date \n");
	
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				MonitorBean m = new MonitorBean();
				m.setTransactionId(rs.getBigDecimal("transaction_id"));
				m.setTransactionType(rs.getString("TRANSACTION_TYPE"));
				m.setMonitorId(rs.getBigDecimal("monitor_id"));
				m.setName(rs.getString("name"));
				m.setType(rs.getString("type"));
				m.setStatus(rs.getInt("status"));
				m.setCreateUser(rs.getString("create_user"));
				m.setCreateDate(rs.getDate("create_date"));
				m.setSubmitDate(rs.getTimestamp("submit_date"));
	            m.setFileCount(rs.getInt("file_count"));
	            m.setSuccessCount(rs.getInt("success_count"));
	            m.setErrorMsg(rs.getString("error_disp"));
	            if(Utils.isNull(m.getErrorMsg()).equals("")){
	            	logger.debug("errorCode:"+rs.getString("error_code"));
	            	String errorMsg = Utils.isNull(ExceptionHandle.ERROR_MAPPING.get(rs.getString("error_code")));
	            	logger.debug("errorMsg:"+errorMsg);
	            	ExceptionHandle.insertErrorCode(rs.getString("error_code"),errorMsg );
	            	m.setErrorMsg(errorMsg);
	            }
				monitorList.add(m);
			}
			
			monitorBean = new MonitorBean[monitorList.size()];
			monitorBean = monitorList.toArray(monitorBean);
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return monitorBean;
	} 
	
	
	public  MonitorBean[] findMonitorDetailList(User user ,MonitorBean mc,String orderBy) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorBean[] monitorBean = null;
		List<MonitorBean> monitorList = new ArrayList<MonitorBean> ();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select monitor.transaction_id,monitor.TRANSACTION_TYPE, monitor.monitor_id \n");
			sql.append(" , monitor.name, monitor.type \n");
			sql.append(" , monitor.create_user, monitor.create_date \n");
			sql.append(" , monitor.submit_date,monitor_item.* \n");
			sql.append(" , (select error_msg from monitor_error_mapping where monitor_error_mapping.error_code = monitor_item.error_code) as error_disp \n");
			sql.append(" from monitor ,monitor_item where monitor.monitor_id = monitor_item.monitor_id  \n");
			
			if(mc.getMonitorId() != null){
			   sql.append(" and monitor.monitor_id ="+mc.getMonitorId() +"\n");
			}
			if( !Utils.isNull(mc.getUserName()).equals("")){
			   sql.append(" and monitor.create_user LIKE '%"+mc.getUserName()+"%' \n");
			}
            if( !Utils.isNull(mc.getSubmitDateFrom()).equals("")){
            	sql.append(" and date(monitor.submit_date) >= STR_TO_DATE('"+Utils.format(Utils.parseToBudishDate(mc.getSubmitDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");	
			}
            if( !Utils.isNull(mc.getSubmitDateTo()).equals("")){
            	sql.append(" and date(monitor.submit_date) <= STR_TO_DATE('"+Utils.format(Utils.parseToBudishDate(mc.getSubmitDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");	
			}
            if( !Utils.isNull(mc.getRequestTable()).equals("")){
            	sql.append(" and monitor_item.table_name LIKE '%"+mc.getRequestTable()+"%' \n");
			}
            if( !Utils.isNull(mc.getTransactionType()).equals("")){
            	sql.append(" and monitor.transaction_type LIKE '%"+mc.getTransactionType()+"%' \n");
			}
            if( !Utils.isNull(mc.getType()).equals("")){
            	sql.append(" and monitor.type LIKE '%"+mc.getType()+"%' \n");
			}
            if( mc.getStatus() != 0){
            	if(mc.getStatus() == Constants.STATUS_SUCCESS){
            	   sql.append(" and monitor.status ="+mc.getStatus()+" \n");
            	   sql.append(" and monitor_item.status ="+mc.getStatus()+" \n");
            	}else{
            	
              	    sql.append(" and monitor_item.status ="+mc.getStatus()+"  \n");
            	}
			}
            
            /** Case No Admin  Default Sales Login */
			if( !User.ADMIN.equalsIgnoreCase(user.getType())){
				 sql.append(" and monitor.create_user LIKE '%"+user.getUserName()+"%' \n");
			}
			
			if(Utils.isNull(orderBy).equals("")){
			   sql.append(" order by monitor.monitor_id ,monitor_item.id \n");
			}else{
			   sql.append(" order by "+orderBy+" \n");
			}
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				MonitorBean m = new MonitorBean();
				m.setTransactionId(rs.getBigDecimal("transaction_id"));
				m.setTransactionType(rs.getString("TRANSACTION_TYPE"));
				m.setMonitorId(rs.getBigDecimal("monitor_id"));
				m.setName(rs.getString("name"));
				m.setType(rs.getString("type"));
				m.setCreateUser(rs.getString("create_user"));
				m.setCreateDate(rs.getDate("create_date"));
				m.setSubmitDate(rs.getTimestamp("submit_date"));

				
				MonitorItemBean item = new  MonitorItemBean();
				item.setId(rs.getBigDecimal("id"));
				item.setTableName(rs.getString("table_name"));
				item.setFileName(rs.getString("file_name"));
				item.setSource(rs.getString("source"));
				item.setDestination(rs.getString("destination"));
				item.setStatus(rs.getInt("status"));
				item.setDataCount(rs.getInt("data_count"));
				item.setSuccessCount(rs.getInt("success_count"));
                item.setFileSize(rs.getString("file_size"));
				item.setErrorMsg(rs.getString("error_disp"));
				item.setErrorCode(rs.getString("error_code"));
				if( !Utils.isNull(item.getErrorCode()).equals("") && Utils.isNull(item.getErrorMsg()).equals("")){
	            	logger.debug("errorCode:"+rs.getString("error_code"));
	            	String errorMsg = Utils.isNull(ExceptionHandle.ERROR_MAPPING.get(rs.getString("error_code")));
	            	logger.debug("errorMsg:"+errorMsg);
	            	ExceptionHandle.insertErrorCode(conn,rs.getString("error_code"),errorMsg );
	            	item.setErrorMsg(errorMsg);
	            }
				m.setMonitorItemBean(item);
				
				monitorList.add(m);
			}
			
			monitorBean = new MonitorBean[monitorList.size()];
			monitorBean = monitorList.toArray(monitorBean);
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return monitorBean;
	} 
	
	public  List findMonitorDetailListTEST(User user ,MonitorBean mc,String orderBy) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorBean[] monitorBean = null;
		List<MonitorBean> monitorList = new ArrayList<MonitorBean> ();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select monitor.transaction_id,monitor.TRANSACTION_TYPE, monitor.monitor_id \n");
			sql.append(" , monitor.name, monitor.type \n");
			sql.append(" , monitor.create_user, monitor.create_date \n");
			sql.append(" , monitor.submit_date,monitor_item.* \n");
			sql.append(" , (select error_msg from monitor_error_mapping where monitor_error_mapping.error_code = monitor_item.error_code) as error_disp \n");
			sql.append(" from monitor ,monitor_item where monitor.monitor_id = monitor_item.monitor_id  \n");
			
			if(mc.getMonitorId() != null){
			   sql.append(" and monitor.monitor_id ="+mc.getMonitorId() +"\n");
			}
			if( !Utils.isNull(mc.getUserName()).equals("")){
			   sql.append(" and monitor.create_user LIKE '%"+mc.getUserName()+"%' \n");
			}
            if( !Utils.isNull(mc.getSubmitDateFrom()).equals("")){
            	sql.append(" and date(monitor.submit_date) >= STR_TO_DATE('"+Utils.format(Utils.parseToBudishDate(mc.getSubmitDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");	
			}
            if( !Utils.isNull(mc.getSubmitDateTo()).equals("")){
            	sql.append(" and date(monitor.submit_date) <= STR_TO_DATE('"+Utils.format(Utils.parseToBudishDate(mc.getSubmitDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");	
			}
            if( !Utils.isNull(mc.getRequestTable()).equals("")){
            	sql.append(" and monitor_item.table_name LIKE '%"+mc.getRequestTable()+"%' \n");
			}
            if( !Utils.isNull(mc.getTransactionType()).equals("")){
            	sql.append(" and monitor.transaction_type LIKE '%"+mc.getTransactionType()+"%' \n");
			}
            if( !Utils.isNull(mc.getType()).equals("")){
            	sql.append(" and monitor.type LIKE '%"+mc.getType()+"%' \n");
			}
            if( mc.getStatus() != 0){
            	if(mc.getStatus() == Constants.STATUS_SUCCESS){
            	   sql.append(" and monitor.status ="+mc.getStatus()+" \n");
            	   sql.append(" and monitor_item.status ="+mc.getStatus()+" \n");
            	}else{
            	
              	    sql.append(" and monitor_item.status ="+mc.getStatus()+"  \n");
            	}
			}
            
            /** Case No Admin  Default Sales Login */
			if( !User.ADMIN.equalsIgnoreCase(user.getType())){
				 sql.append(" and monitor.create_user LIKE '%"+user.getUserName()+"%' \n");
			}
			
			if(Utils.isNull(orderBy).equals("")){
			   sql.append(" order by monitor.monitor_id ,monitor_item.id \n");
			}else{
			   sql.append(" order by "+orderBy+" \n");
			}
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				MonitorBean m = new MonitorBean();
				m.setTransactionId(rs.getBigDecimal("transaction_id"));
				m.setTransactionType(rs.getString("TRANSACTION_TYPE"));
				m.setMonitorId(rs.getBigDecimal("monitor_id"));
				m.setName(rs.getString("name"));
				m.setType(rs.getString("type"));
				m.setCreateUser(rs.getString("create_user"));
				m.setCreateDate(rs.getDate("create_date"));
				m.setSubmitDate(rs.getTimestamp("submit_date"));

				
				MonitorItemBean item = new  MonitorItemBean();
				item.setId(rs.getBigDecimal("id"));
				item.setTableName(rs.getString("table_name"));
				item.setFileName(rs.getString("file_name"));
				item.setSource(rs.getString("source"));
				item.setDestination(rs.getString("destination"));
				item.setStatus(rs.getInt("status"));
				item.setDataCount(rs.getInt("data_count"));
				item.setSuccessCount(rs.getInt("success_count"));
                item.setFileSize(rs.getString("file_size"));
				item.setErrorMsg(rs.getString("error_disp"));
				
				m.setMonitorItemBean(item);
				
				monitorList.add(m);
			}
			
			//monitorBean = new MonitorBean[monitorList.size()];
			//monitorBean = monitorList.toArray(monitorBean);
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return monitorList;
	} 
	
	public  MonitorItemDetailBean[] findMonitorItemDetailBeanList(BigDecimal monitorItemId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorItemDetailBean[] itemBean = null;
		List<MonitorItemDetailBean> itemList = new ArrayList<MonitorItemDetailBean> ();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from monitor_item_detail where 1=1  and monitor_item_id ="+monitorItemId+"\n");
	
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				 MonitorItemDetailBean m = new MonitorItemDetailBean();
				 m.setId(rs.getBigDecimal("id"));
				 m.setMonitorItemId(rs.getBigDecimal("MONITOR_ITEM_ID"));
				 m.setCustomerCode(rs.getString("customer_code"));
				 m.setCustomerName(rs.getString("customer_name"));
				 m.setCode(rs.getString("code"));
				 m.setType(rs.getString("type"));
				 m.setAmount(rs.getDouble("amount"));
				 m.setAmountStr(ConvertUtils.convertToCurrencyStr(rs.getDouble("amount")));
				 itemList.add(m);
			}
			
			itemBean = new MonitorItemDetailBean[itemList.size()];
			itemBean = itemList.toArray(itemBean);
			
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return itemBean;
	} 
	
	
	
	public MonitorBean updateMonitor(Connection conn,MonitorBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;

		try {
			String sql = "UPDATE monitor SET " +
			" status = ? ,file_count =? ,error_code = ? ,error_msg =?"+
			" WHERE MONITOR_ID = ? and transaction_id =?";
			
			logger.debug("SQL:"+sql);

			logger.debug("monitor_id:"+model.getMonitorId()+",transType:"+model.getTransactionType());
		
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setInt(++index, model.getStatus());
			ps.setInt(++index, model.getFileCount());
			ps.setString(++index, Utils.isNull(model.getErrorCode()).length()> 300?model.getErrorCode().substring(0,299):model.getErrorCode());
			ps.setString(++index, Utils.isNull(model.getErrorMsg()).length()> 300?model.getErrorMsg().substring(0,299):model.getErrorMsg());
			ps.setBigDecimal(++index, model.getMonitorId());
			ps.setBigDecimal(++index, model.getTransactionId());
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			
		}
		return model;
	}
	

	public MonitorItemBean updateMonitorItem(Connection conn,MonitorItemBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;

		try {
			String sql = "UPDATE monitor_item SET " +
			"  status = ? ,error_msg = ? "+
			" WHERE ID = ?";
			
			logger.debug("SQL:"+sql);

			logger.debug("id:"+model.getId());
		
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setInt(++index, model.getStatus());
			ps.setString(++index, Utils.isNull(model.getErrorMsg()).length()> 300?model.getErrorMsg().substring(0,299):model.getErrorMsg());
			ps.setBigDecimal(++index, model.getId());
			
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			
		}
		return model;
	}
	
	/**
	 * 
	 * @param conn
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public MonitorBean updateStatusReGenToMonitorAndItem(Connection conn,MonitorBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		try {
			String sql = "UPDATE monitor SET  status = "+Constants.STATUS_REGEN+"  WHERE  transaction_id ="+model.getTransactionId();
			String sql2 = "UPDATE monitor_item SET  status = "+Constants.STATUS_REGEN+"    WHERE monitor_id in(select monitor_id from monitor where  transaction_id ="+model.getTransactionId()+")";
			
			logger.debug("SQL:"+sql);
			logger.debug("SQL2:"+sql2);
			ps = conn.prepareStatement(sql);
			ps2 = conn.prepareStatement(sql2);
			
			ps.execute();
			ps2.execute();
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			if(ps2 != null){
				ps2.close();ps2 = null;
			}
		}
		return model;
	}
	
 /**
  * mapFileNameLastImportToTableMap
  * @param configMap
  * @return
  * @throws Exception
  * 
  * Store LastFileName import for Check new import 
  */
 public  LinkedHashMap<String ,TableBean> mapFileNameLastImportToTableMap(Connection conn,LinkedHashMap<String ,TableBean> configMap,User user ,boolean importAll) throws Exception{
	PreparedStatement ps =null;
	ResultSet rs = null;
	try{
		StringBuffer sql = new StringBuffer("");
		/** CASE MASTER *****/
		sql.append(" select id,file_name,table_name from monitor_item \n");
		sql.append(" where (id,table_name) in(  \n");
		sql.append("        select max(monitor_item.id),monitor_item.table_name from monitor ,monitor_item \n");
		sql.append("        where monitor.status <> "+Constants.STATUS_FAIL+" \n");
		sql.append("        and monitor_item.status <> "+Constants.STATUS_FAIL+" \n");
		sql.append("        and monitor.monitor_id = monitor_item.monitor_id \n");
		sql.append("        and monitor.type in('"+Constants.TYPE_IMPORT+"') \n");
		sql.append("        and monitor.create_user Like '%"+user.getUserName()+"%' \n");
		sql.append("        and monitor.transaction_type ='"+Constants.TRANSACTION_MASTER_TYPE+"' \n");
		if(importAll){
		  sql.append("      and monitor_item.file_name Like '%-ALL%' \n");
		}else{
		  sql.append("      and monitor_item.file_name not Like '%-ALL%' \n");	
		}
		sql.append("        group by monitor_item.table_name) \n");
		
		sql.append("        union all \n");
		
		/** CASE TRANSACTION AND UPDATE-TRANS-SALES   Check Status Only in monitor_item *****/
		sql.append(" select id,file_name,table_name from monitor_item \n");
		sql.append(" where (id,table_name) in(  \n");
		sql.append("        select max(monitor_item.id),monitor_item.table_name from monitor ,monitor_item \n");
		sql.append("        where 1=1 \n"); 
		sql.append("        and monitor_item.status <> "+Constants.STATUS_FAIL+" \n");
		sql.append("        and monitor.monitor_id = monitor_item.monitor_id \n");
		sql.append("        and monitor.type in('"+Constants.TYPE_IMPORT+"') \n");
		sql.append("        and monitor.create_user Like '%"+user.getUserName()+"%' \n");
		sql.append("        and monitor.transaction_type in('"+Constants.TRANSACTION_UTS_TRANS_TYPE+"','"+Constants.TRANSACTION_TRANS_TYPE+"','"+Constants.TRANSACTION_WEB_MEMBER_TYPE+"') \n");
		if(importAll){
		  sql.append("      and monitor_item.file_name Like '%-ALL%' \n");
		}else{
		  sql.append("      and monitor_item.file_name not Like '%-ALL%' \n");	
		}
		sql.append("        group by monitor_item.table_name) \n");
		
	    logger.debug("SQL:"+sql);
	    
		ps = conn.prepareStatement(sql.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			String tableName = rs.getString("TABLE_NAME");
			
			Set s = configMap.keySet();
			Iterator it = s.iterator();
			for (int j = 1; it.hasNext(); j++) {
				String tableNameMap = (String) it.next();
				TableBean tableBean = (TableBean) configMap.get(tableNameMap);
				if(tableName.equalsIgnoreCase(tableNameMap)){
				   tableBean.setFileNameLastImport(rs.getString("file_name"));	
				   configMap.put(tableName, tableBean);
				   break;
				}
			}
		}//end while
		
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
	return configMap;
} 
 
 /**
  * 
  * @param configMap
  * @return
  * @throws Exception
  */
 public  TableBean getSunInvNameMap(Connection conn,TableBean tableBean ,User userBean) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;

        Map subInvMap = new HashMap();
        Map userCodeMap = new HashMap();
		try{
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" select '1' as t ,s.name as sub_inv from m_sub_inventory s where 1=1 and s.sub_inventory_id in (select sub_inventory_id from m_sales_inventory where user_id ="+userBean.getId()+") \n");
			sql.append(" union all \n");
			sql.append(" select '1' as t ,s.name as sub_inv from m_sub_inventory s where 1=1 and s.name ='"+userBean.getCode()+"' \n");
			sql.append(" union all \n");
			sql.append(" select '2' as t, s.code as sub_inv from ad_user s where s.user_id ="+userBean.getId() +"\n");
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				if(rs.getString("t").equals("1")){
				   subInvMap.put(rs.getString("sub_inv"),rs.getString("sub_inv"));
				}else{
				   userCodeMap.put(rs.getString("sub_inv"),rs.getString("sub_inv"));
				}
				
			}
			
			tableBean.setSubInvMap(subInvMap);
			tableBean.setUserCodeMap(userCodeMap);
			
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
		return tableBean;
	} 
 
 
 /**
  * 
  * @param configMap
  * @return
  *  * a.	㹡óշ���� VAN ��������� Sales 㹡��令��Ҫ��� sub Inbventory
     * b.	㹡óշ���� TT ���͡�� 2 �ó�
        i.	��� Item � �� Item Category (Sales) Segment ��� 3 �� ��Թ��Ҥ���ѧ� ����к� Sub Inventory �� �G001�
        ii.	��� Item � �� Item Category (Sales) Segment��� 3 �� ��Թ��Ҿ��������� ����к� Sub Inventory �� �G001-PRM� 
  * @throws Exception
  */
 public  String getSunInventory(Connection conn,User userBean) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String subInventory ="";
		try{
			StringBuffer sql = new StringBuffer("");
			if(User.VAN.equalsIgnoreCase(userBean.getType())){
			   // sql.append(" select '1' as t ,s.name as sub_inv from m_sub_inventory s where s.type in ('��','"+userBean.getRole().getKey()+"') \n");
			   return userBean.getCode();
			}else if(User.TT.equalsIgnoreCase(userBean.getType())){
				sql.append(" select m.PRODUCT_CATEGORY_ID ,c.SEG_ID3 ,c.SEG_VALUE3 \n");
				sql.append(" from t_order t ,m_customer m ,m_product_category c \n");
				sql.append(" where t.CUSTOMER_ID = m.CUSTOMER_ID \n");
				sql.append(" and m.PRODUCT_CATEGORY_ID = c.PRODUCT_CATEGORY_ID \n");
			}
			
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				if(Utils.isNull(rs.getString("SEG_VALUE3")).equalsIgnoreCase("�Թ��Ҥ���ѧ")){
					subInventory = "G001";
				}else if(Utils.isNull(rs.getString("SEG_VALUE3")).equalsIgnoreCase("�Թ��Ҿ��������")){
					subInventory = "G001-PRM";
				}
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
		return subInventory;
	} 
 

 /**
  * 
  * @param conn
  * @param user
  * @return
  * @throws Exception
  */
 public  List getOrderIdListByOrderNo(Connection conn,String orderNoInStr) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
	    List orderList = new ArrayList();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select order_id from t_order where 1=1 and order_no in("+orderNoInStr+") \n");
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				orderList.add(rs.getString("order_id"));
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
		return orderList;
}
 
 /**
  * 
  * @param conn
  * @param orderId
  * @return
  * @throws Exception
  */
 public  boolean isOrderLineCancelOne(Connection conn,String orderId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
	    boolean caseCancelOneFlag = false; 
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(payment) as c from t_order_line where order_id ="+orderId+" and iscancel ='Y' \n");
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") > 0){
					caseCancelOneFlag = true;
				}
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
		return caseCancelOneFlag;
} 
 
 /**
  * isOrderLineCancelAll
  * @param conn
  * @param orderId
  * @return
  * @throws Exception
  */
 public  boolean isOrderLineCancelAll(Connection conn,String orderId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
	    boolean isCancelAll = true; 
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(payment) as c from t_order_line where order_id ="+orderId+" and iscancel ='N' \n");
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") > 0){
					isCancelAll = false;
				}
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
		return isCancelAll;
} 
 /**
  * Update Order Payment Case Cancel Order
  * @param conn
  * @param orderId
  * @param paymentFlag
  * @return
  * @throws Exception
   * "Update Payment Flag ��
     (1) ��ҷ�� header payment  = 'Y' ����дѺ line ����ҡѺ   "Y" ����
     (2) ��ҷ�� header payment  = 'N' ����дѺ line ����ҡѺ   "N" ����
  */
 public  int updatePaymentInOrderLine(Connection conn,String orderId) throws Exception{
     PreparedStatement ps =null;
     int updateInt = 0;
	 try{
		StringBuffer sql = new StringBuffer("");
		sql.append(" update t_order_line set payment = (select payment from t_order where order_id ="+orderId+") where order_id ="+orderId+" \n");
	    logger.debug("SQL:"+sql.toString());
	    
		ps = conn.prepareStatement(sql.toString());
		updateInt = ps.executeUpdate();	
	}catch(Exception e){
      throw e;
	}finally{
		if(ps != null){
		   ps.close();ps = null;
		}
	}
		return updateInt;
} 
 
/**
 * updateRecalcAmountInInOrder
 * @param conn
 * @param orderId
 * @return
 * @throws Exception
 * Desc: 
 * Recalc Amount in Order Header
 */
 public  int updateRecalcAmountInOrder(Connection conn,String orderId) throws Exception{
     PreparedStatement ps =null;
     int updateInt = 0;
	 try{
		StringBuffer sql = new StringBuffer("");
		sql.append("  update t_order set \n");
		sql.append("  t_order.total_amount = (select sum(t_order_line.line_amount)-sum(t_order_line.discount) from t_order_line where t_order_line.order_id ="+orderId+" and t_order_line.iscancel='N') \n");
		sql.append(" ,t_order.vat_amount   = (select sum(t_order_line.vat_amount) from t_order_line where t_order_line.order_id ="+orderId+"  and t_order_line.iscancel='N') \n");
		sql.append(" ,t_order.net_amount   = (select sum(t_order_line.total_amount) from t_order_line where t_order_line.order_id ="+orderId+"  and t_order_line.iscancel='N') \n");
		sql.append("  where t_order.order_id = "+orderId+"  \n");
		
	    logger.debug("SQL:"+sql.toString());
	    
		ps = conn.prepareStatement(sql.toString());
		updateInt = ps.executeUpdate();	
		
	}catch(Exception e){
      throw e;
	}finally{
		if(ps != null){
		   ps.close();ps = null;
		}
	}
		return updateInt;
} 
 
 /**
  * updatePaymentFlagHeader
  * @param conn
  * @param orderId
  * @param paymentFlag
  * @return
  * @throws Exception
  */
 public  int updatePaymentInOrder(Connection conn,String orderId,String paymentFlag ) throws Exception{
     PreparedStatement ps =null;
     int updateInt = 0;
	 try{
		StringBuffer sql = new StringBuffer("");
		sql.append(" update t_order set payment = '"+paymentFlag+"'  where order_id = "+orderId+" \n");
	    logger.debug("SQL:"+sql.toString());
	    
		ps = conn.prepareStatement(sql.toString());
		updateInt = ps.executeUpdate();	
	}catch(Exception e){
      throw e;
	}finally{
		if(ps != null){
		   ps.close();ps = null;
		}
	}
		return updateInt;
} 
 
 public  int updateDocStatusInOrder(Connection conn,String orderId,String docStatus) throws Exception{
     PreparedStatement ps =null;
     int updateInt = 0;
	 try{
		StringBuffer sql = new StringBuffer("");
		sql.append(" update t_order set doc_status ='"+docStatus+"' where order_id = "+orderId+" \n");
	    logger.debug("SQL:"+sql.toString());
	    
		ps = conn.prepareStatement(sql.toString());
		updateInt = ps.executeUpdate();	
	}catch(Exception e){
      throw e;
	}finally{
		if(ps != null){
		   ps.close();ps = null;
		}
	}
		return updateInt;
}
 
 /**
  * isOrderLinePaymentAll
  * @param conn
  * @param orderId
  * @return
  * @throws Exception
  */
 public  boolean isOrderLinePaymentAll(Connection conn,String orderId) throws Exception{
	PreparedStatement ps =null;
	ResultSet rs = null;
    boolean paymentAll = false; 
	try{
		StringBuffer sql = new StringBuffer("");
		sql.append(" select count(payment) as c from t_order_line where order_id = "+orderId+" and (payment = 'N' or payment is null) \n");
	    logger.debug("SQL:"+sql.toString());
	    
		ps = conn.prepareStatement(sql.toString());
		rs = ps.executeQuery();
		if(rs.next()){
			if(rs.getInt("c") == 0){
				paymentAll = true;
			}
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
	return paymentAll;
} 

 
 /**
  * 
  * @param conn
  * @param customerId
  * @return
  * @throws Exception
  */
 public  String isSameAddress(Connection conn,String customerId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
	    PreparedStatement ps2 =null;
		ResultSet rs2 = null;
		String sameAddress = "NO"; 
		String sqlS = "";
		String ADDRESS_ID_BILL_TO = "";
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" select count(*) as c from (select count(*) from m_address sa  where sa.customer_id = "+customerId+" group by sa.line1)a \n"); 
		    logger.debug("SQL Check SameAddress:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") == 1){
					sameAddress = "SAME_ADDRESS";
				}
			}
			sqlS +=	"  SELECT 	\n";
			sqlS +=	"  concat(concat(M.CODE , '-'),A.address_id )  as ORIG_SYSTEM_ADDRESS_REF_BILL_TO,	\n";
			sqlS +=	"  A.address_id  as ADDRESS_ID_BILL_TO,	\n";
			sqlS += "  A.PURPOSE  \n";
			sqlS +=	"  FROM m_customer M ,m_address A	\n";
			sqlS +=	"  WHERE  M.customer_id = A.customer_id	\n";
			sqlS +=	"  AND  M.customer_id = "+customerId+"	\n";
			sqlS += "  AND A.PURPOSE ='B' \n";
			
			logger.debug("SQL get Address BillTO:"+sqlS.toString());
			ps2 = conn.prepareStatement(sqlS.toString());
			rs2 = ps2.executeQuery();
			if(rs2.next()){
				if(sameAddress.equalsIgnoreCase("SAME_ADDRESS")){
					ADDRESS_ID_BILL_TO = rs2.getString("ADDRESS_ID_BILL_TO");
				}else{
					ADDRESS_ID_BILL_TO = null;
				}
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
			if(ps2 != null){
			   ps2.close();ps2 = null;
			}
			if(rs2 != null){
			   rs2.close();rs2 = null;
			}
		}
		return ADDRESS_ID_BILL_TO;
	} 
 
 public  String getOrgID(Connection conn,String customerId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		String orgId ="83";//default
		try{
			sql.append(" select value from c_reference where code ='OrgID' \n"); 
		    logger.debug("SQL Check SameAddress:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				orgId = rs.getString("value");
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
		return orgId;
	} 
 /**
  * isSameAddressId
  * @param conn
  * @param customerId
  * @return
  * @throws Exception
  * RETURN STR "SAME_ADDRESS"  Case Equals
  */
 public  String getORIG_SYSTEM_ADDRESS_REF_FUNC(Connection conn,String customerId,String addressId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		PreparedStatement ps2 =null;
		ResultSet rs2 = null;
	    String sameAddress = "NO"; 
	    String sqlS = "";
	    String ORIG_SYSTEM_ADDRESS_REF = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(*) as c from (select count(*) from m_address sa  where sa.customer_id = "+customerId+" group by sa.line1)a \n"); 
		    logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") == 1){
					sameAddress = "SAME_ADDRESS";
				}
			}
			
			sqlS +=	"  SELECT "+addressId+" as ADDRESS_ID_ORG,	\n";
			sqlS +=	"  concat(concat(M.CODE , '-'), "+addressId+")  as ORIG_SYSTEM_ADDRESS_REF_NORMAL,	\n";
			sqlS += "  A.PURPOSE,  \n";
			sqlS +=	"  CASE WHEN  'SAME_ADDRESS' ='"+sameAddress+"' 	\n";
			sqlS +=	"       THEN concat(concat(M.CODE , '-'), A.ADDRESS_ID)  	\n";
			sqlS +=	"   END AS ORIG_SYSTEM_ADDRESS_REF_SAME_ADDRESS 	\n";
			sqlS +=	"   FROM m_customer M ,m_address A	\n";
			sqlS +=	"   WHERE  M.customer_id = A.customer_id	\n";
			sqlS +=	"   AND  M.customer_id = "+customerId+"	\n";
			
			ps2 = conn.prepareStatement(sqlS.toString());
			rs2 = ps2.executeQuery();
			while(rs2.next()){
				if(sameAddress.equalsIgnoreCase("SAME_ADDRESS")){
					if( Utils.isNull(rs2.getString("PURPOSE")).equals("B")){
						ORIG_SYSTEM_ADDRESS_REF = rs2.getString("ORIG_SYSTEM_ADDRESS_REF_SAME_ADDRESS");
					}
				}else{
				   ORIG_SYSTEM_ADDRESS_REF = rs2.getString("ORIG_SYSTEM_ADDRESS_REF_NORMAL");
				}
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
			if(ps2 != null){
			  ps2.close();ps = null;
			}
			if(rs2 != null){
			  rs2.close();rs = null;
			}
		}
		return ORIG_SYSTEM_ADDRESS_REF;
	} 
}
