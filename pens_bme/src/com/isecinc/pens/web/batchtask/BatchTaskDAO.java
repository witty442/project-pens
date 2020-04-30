package com.isecinc.pens.web.batchtask;

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

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.MonitorItemDetailBean;
import com.isecinc.pens.bean.MonitorItemResultBean;
import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.pens.util.Constants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

/**
 * @author WITTY
 *
 */
public class BatchTaskDAO {

	protected static  Logger logger = Logger.getLogger("PENS");
	
	public  BatchTaskForm searchBatchLastRun(User user,String pageName){
		BatchTaskForm batchTaskForm = new BatchTaskForm();
		try{
			/** Set Condition Search **/
			MonitorBean[] monitors = findMonitorListNew(user,pageName);
			
			logger.debug("monitors Size:"+monitors.length);
			if (monitors != null && monitors.length > 0) {
				
				//Search interfaceResult (monitorItem)
				MonitorItemBean monitorItemBean = findMonitorItemBean(user,monitors[0]);
				batchTaskForm.setMonitorItem(monitorItemBean);
	
				// Head Monitor 
				batchTaskForm.setResults(monitors);
				
				//Get BatschTask Info
				BatchTaskInfo taskInfo = new BatchTaskInfo();
				taskInfo.setTaskName(pageName);
				taskInfo.setDispBean(new BatchTaskAction().getBatchDispByTaskname(pageName));
				batchTaskForm.setTaskInfo(taskInfo);
			} else {
				batchTaskForm.setResults(null);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return batchTaskForm;
	}
	public MonitorBean insertMonitor(Connection conn ,MonitorBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO PENSBI.monitor(" +
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
				 model.setTransactionId(new BigDecimal(SequenceProcess.getNextValue("monitor")));
			}
			if(model.getMonitorId() ==null){
				 model.setMonitorId(new BigDecimal(SequenceProcess.getNextValue("monitor_2")));
			}
			logger.debug("Transaction id:"+model.getTransactionId());

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
			String sql = "INSERT INTO PENSBI.monitor_item(" +
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
			" success_count," +
			" fail_count," +
			" total_qty)"+
			" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			  
			logger.debug("SQL:"+sql);
			logger.debug("SucessCound:"+model.getSuccessCount());
			if(model.getId() ==null){
				model.setId(new BigDecimal(SequenceProcess.getNextValue("monitor_item")));
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
			ps.setInt(++index, model.getFailCount());
			ps.setInt(++index, model.getTotalQty());
			
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
			
			String sql = "INSERT INTO PENSBI.monitor_item_detail(" +
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
					
					logger.info("customeName:"+item.getCustomerName());
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
				   if(type.equalsIgnoreCase("pensbme_barcode_scan")){
					   item.setCustomerCode(rs.getString("doc_no"));
					   item.setCustomerName(rs.getString("cust_no"));
					   item.setCode(rs.getString("doc_no"));
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
			sql.append(" select max(monitor_id) as monitor_id from PENSBI.monitor \n");
			
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
	public  String findMonitorStatus(Connection conn,String id) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String status = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select channel as batch_task_status from PENSBI.monitor \n");
			sql.append("where MONITOR_ID = (select min(monitor_id) from PENSBI.monitor where transaction_id ="+id+") \n");
			
		    logger.info("SQL:"+sql.toString());
		    
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
    
    public static void clearTaskControlMonitorAll() {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	try {
            conn = DBConnection.getInstance().getConnection();
	       	String sql = "UPDATE PENSBI.c_monitor SET  transaction_id = 0";
			ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            
        } catch (Exception e) {
        	e.printStackTrace();
        }finally{
        	try{
	        	if(conn != null){
	        		conn.close();conn=null;
	        	}
	        	if(ps != null){
					ps.close();ps = null;
				}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	
        }
    }
    
    /** c_monitor **/
    public void updateControlMonitor(BigDecimal transactionId,String type) {
    	Connection conn = null;
    	try {
           InterfaceDAO dao = new InterfaceDAO();
           conn = DBConnection.getInstance().getConnection();
           dao.updateControlMonitor(conn,transactionId,type);
           
        } catch (Exception e) {
        	e.printStackTrace();
        }finally{
        	try{
	        	if(conn != null){
	        		conn.close();conn=null;
	        	}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
    }
    
    
    /** check BATCH_TASK_CONTROL 
     *  TaskName is concurrent or no
     *  TaskName no set default no concurrent
     * **/
	public  boolean canRunBatchTask(String action) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String status = "";
		Connection conn = null;
		StringBuffer sql = new StringBuffer("");
		boolean isConcurrent = false;
		boolean canRunBatch =false;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			 
			/** Step 1 Check Task is run concurrent or no */
			sql.append(" select name,IS_CONCURRENT from PENSBI.BATCH_TASK_CONTROL where name ='"+action+"' \n");
			logger.debug("SQL checkTaskISConcurrent:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(Utils.isNull(rs.getString("IS_CONCURRENT")).equals("Y")){
					isConcurrent = true;
				}
			}
			logger.info("TaskName:"+action+" isConcurrent:"+isConcurrent);
			
			/** Step 2 check control monitor run Task success */
			/** 1) isConcurrent is true no check control monitor return true*/
			/** 2) noConcurrent check c_monitor **/
			if( !isConcurrent){
				sql = new StringBuffer();
				sql.append(" select * from PENSBI.c_monitor where action ='"+action+"' \n");
			    logger.debug("SQL cMonitor:"+sql.toString());
			  
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					status = rs.getString("transaction_id");
					if(Utils.isNull(status).equals("") ||  Utils.isNull(status).equals("0")){
					    canRunBatch = true;
					}
				}
			}else{
				/** Case run concurrent no check c_monitor **/
				canRunBatch = true;
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
			   conn.close();conn = null;
			}
		}
		return canRunBatch;
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
			String sql = "UPDATE PENSBI.monitor SET  channel = ? WHERE MONITOR_ID = ? and transaction_id = ?";
			
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
	
	public  MonitorBean findMonitorBean(BigDecimal monitorId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorBean  m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from PENSBI.monitor where 1=1  and monitor_id ="+monitorId+"\n");
	
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
	
	public  MonitorBean[] findMonitorListNew(User user,String name) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorBean[] monitorBean = null;
		List<MonitorBean> monitorList = new ArrayList<MonitorBean> ();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select monitor.*  \n");
			sql.append(" , (select count(*) from pensbi.monitor_item where monitor_item.monitor_id = monitor.monitor_id and status = "+Constants.STATUS_SUCCESS+" )  as success_count  \n");
			sql.append(" , (select max(id) from pensbi.monitor_item where monitor_item.monitor_id = monitor.monitor_id)  as monitor_item_id  \n");
			sql.append(" , (select error_msg from pensbi.monitor_error_mapping where monitor_error_mapping.error_code = monitor.error_code) as error_disp \n");
			sql.append(" from pensbi.monitor  \n");
			sql.append(" inner join  \n");
			sql.append(" ( select max(transaction_id) as transaction_id  \n");
			sql.append("   from pensbi.monitor  \n");
			sql.append("   where create_user like '%"+user.getUserName()+"%' \n");
			sql.append("   and name ='"+name+"'\n");
			sql.append("  ) s  \n");
			sql.append("  on  monitor.transaction_id = s.transaction_id \n");
			if( !Utils.isNull(name).equals("")){
			   sql.append(" where name ='"+name+"'\n");
			}
			sql.append("  order by monitor.submit_date \n");
	
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				MonitorBean m = new MonitorBean();
				m.setTransactionId(rs.getBigDecimal("transaction_id"));
				m.setMonitorItemId(rs.getBigDecimal("monitor_item_id"));
				m.setTransactionType(rs.getString("TRANSACTION_TYPE"));
				m.setMonitorId(rs.getBigDecimal("monitor_id"));
				m.setName(rs.getString("name"));
				m.setThName(rs.getString("th_name"));
				m.setType(rs.getString("type"));
				m.setStatus(rs.getInt("status"));
				m.setStatusDesc(getStatDesc(rs.getInt("status")));
				m.setCreateUser(rs.getString("create_user"));
				m.setCreateDate(rs.getDate("create_date"));
				m.setSubmitDate(rs.getTimestamp("submit_date"));
				m.setSubmitDateDisp(DateUtil.stringValue(rs.getTimestamp("submit_date"),DateUtil.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,Utils.local_th));
	            m.setFileCount(rs.getInt("file_count"));
	            m.setFileName(rs.getString("file_name"));
	            m.setSuccessCount(rs.getInt("success_count"));
	            if( !Utils.isNull(rs.getString("error_msg")).equals("")){
	               m.setErrorMsg(rs.getString("error_msg"));
	            }else{
	               m.setErrorMsg(rs.getString("error_disp"));
	            }
	        	m.setUpdateDateDisp(DateUtil.stringValue(rs.getTimestamp("update_date"),DateUtil.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,Utils.local_th));
	            if(Utils.isNull(m.getErrorMsg()).equals("")){
	            	logger.debug("errorCode:"+rs.getString("error_code"));
	            	String errorMsg = Utils.isNull(ExceptionHandle.ERROR_MAPPING.get(rs.getString("error_code")));
	            	logger.debug("errorMsg:"+errorMsg);
	            	 if( !Utils.isNull(errorMsg).equals("")){
	            	    ExceptionHandle.insertErrorCode(rs.getString("error_code"),errorMsg );
	            	    m.setErrorMsg(errorMsg);
	            	 }
	            }
	            //
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
	
	/**
	 * 
	 * @param conn
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public  MonitorBean[] findMonitorList(User user,String type) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorBean[] monitorBean = null;
		List<MonitorBean> monitorList = new ArrayList<MonitorBean> ();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select monitor.*  \n");
			sql.append(" , (select count(*) from monitor_item where monitor_item.monitor_id = monitor.monitor_id and status = "+Constants.STATUS_SUCCESS+" )  as success_count  \n");
			sql.append(" , (select error_msg from monitor_error_mapping where monitor_error_mapping.error_code = monitor.error_code) as error_disp \n");
			sql.append(" from monitor  \n");
			sql.append(" inner join  \n");
			sql.append(" ( select max(transaction_id) as transaction_id  \n");
			sql.append("   from monitor  \n");
			sql.append("   where create_user like '%"+user.getUserName()+"%' \n");
			sql.append("   and type ='"+type+"'\n");
			sql.append("  ) s  \n");
			sql.append("  on  monitor.transaction_id = s.transaction_id \n");
			if( !Utils.isNull(type).equals("")){
			   sql.append(" where type ='"+type+"'\n");
			}
			sql.append("  order by monitor.submit_date \n");
	
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
	            	 if( !Utils.isNull(errorMsg).equals("")){
	            	    ExceptionHandle.insertErrorCode(rs.getString("error_code"),errorMsg );
	            	    m.setErrorMsg(errorMsg);
	            	 }
	            }
	            //
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
            	sql.append(" and date(monitor.submit_date) >= STR_TO_DATE('"+DateUtil.format(DateUtil.parseToBudishDate(mc.getSubmitDateFrom(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");	
			}
            if( !Utils.isNull(mc.getSubmitDateTo()).equals("")){
            	sql.append(" and date(monitor.submit_date) <= STR_TO_DATE('"+DateUtil.format(DateUtil.parseToBudishDate(mc.getSubmitDateTo(),DateUtil.DD_MM_YYYY_WITH_SLASH),DateUtil.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");	
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
	
	public  MonitorItemBean findMonitorItemBean(User user, MonitorBean mc) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		MonitorItemBean item = new  MonitorItemBean();
		Connection conn = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *   \n");
			sql.append(" from PENSBI.monitor_item  \n");
			sql.append(" where 1=1 \n");
			sql.append(" and monitor_id ="+mc.getMonitorId() +"\n");

		    logger.debug("SQL:"+sql.toString());
            conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				item.setId(rs.getBigDecimal("id"));
				item.setTableName(rs.getString("table_name"));
				item.setFileName(rs.getString("file_name"));
				item.setSource(rs.getString("source"));
				item.setDestination(rs.getString("destination"));
				item.setStatus(rs.getInt("status"));
				item.setSuccessCount(rs.getInt("success_count"));
				item.setFailCount(rs.getInt("fail_count"));
				item.setErrorCode(rs.getString("error_code"));
				item.setErrorMsg(rs.getString("error_msg"));
				
				//ColumnHeadList
				item.setColumnHeadStrArr(findMonitorItemColumnHeadResultList(conn,item.getId()));
				
			    //SuccessList
				item.setSuccessList(findMonitorItemResultList(conn,item.getId(),"SUCCESS"));
				//FailList
				item.setFailList(findMonitorItemResultList(conn,item.getId(),"FAIL"));
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
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return item;
	} 
	
	
	public  MonitorItemBean findMonitorItemBeanByPK(User user, String id) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		MonitorItemBean item = new  MonitorItemBean();
		Connection conn = null;
	
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select monitor_item.* \n");
			sql.append(" from PENSBI.monitor_item where 1=1 \n");
			sql.append(" and id ="+id +"\n");

		    logger.debug("SQL:"+sql.toString());
            conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				item.setId(rs.getBigDecimal("id"));
				item.setTableName(rs.getString("table_name"));
				item.setFileName(rs.getString("file_name"));
				item.setSource(rs.getString("source"));
				item.setDestination(rs.getString("destination"));
				item.setStatus(rs.getInt("status"));
				item.setSuccessCount(rs.getInt("success_count"));
				item.setFailCount(rs.getInt("fail_count"));
				item.setErrorCode(rs.getString("error_code"));
				item.setErrorMsg(rs.getString("error_msg"));
				
			    //SuccessList
				item.setSuccessList(findMonitorItemResultList(conn,item.getId(),"SUCCESS"));
				//FailList
				item.setFailList(findMonitorItemResultList(conn,item.getId(),"FAIL"));
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
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return item;
	} 
	
	public  List<MonitorItemBean> findMonitorItemList(User user, MonitorBean mc) throws Exception{
		return findMonitorItemList(user,mc,"");
	}
	
	public  List<MonitorItemBean> findMonitorItemList(User user, MonitorBean mc,String typeImport) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		MonitorItemBean item = null;
		Connection conn = null;
		List<MonitorItemBean> monitorItemList = null;
		int row = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select monitor_item.* \n");
			sql.append(",(select max(transaction_id) from monitor where monitor.monitor_id =monitor_item.monitor_id)as transaction_id \n");
			sql.append(" from monitor_item where 1=1 \n");
			sql.append(" and monitor_id ="+mc.getMonitorId() +"\n");

		    logger.debug("SQL:"+sql.toString());
            conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				row++;
				if(row==1){
					monitorItemList = new ArrayList<MonitorItemBean>();
				}
				item = new  MonitorItemBean();
				item.setRow(row);
				item.setTransactionId(rs.getBigDecimal("transaction_id"));
				item.setId(rs.getBigDecimal("id"));
				item.setMonitorId(rs.getBigDecimal("monitor_id"));
				item.setTableName(rs.getString("table_name"));
				item.setFileName(rs.getString("file_name"));
				item.setSource(rs.getString("source"));
				item.setDestination(rs.getString("destination"));
				item.setStatus(rs.getInt("status"));
				item.setStatusDesc(getStatDesc(item.getStatus()));
				item.setDataCount(rs.getInt("data_count"));
				item.setSuccessCount(rs.getInt("success_count"));
				item.setFailCount(rs.getInt("fail_count"));
				item.setErrorCode(rs.getString("error_code"));
				item.setErrorMsg(ExceptionHandle.ERROR_MAPPING.get(item.getErrorCode()));
				if(Utils.isNull(item.getErrorMsg()).equals("")){
				   item.setErrorMsg(rs.getString("error_msg"));
				}
				item.setTotalQty(rs.getInt("total_qty"));
				
				if(typeImport.equalsIgnoreCase(Constants.TYPE_IMPORT_WACOAL_SALESIN_RETURN)){
				   //SalesInList
				   item.setSalesInList(findMonitorItemResultListByTypeMessage(conn,item.getId(),"salein"));
				   //ReturnList
				   item.setReturnList(findMonitorItemResultListByTypeMessage(conn,item.getId(),"GR"));
				}
				monitorItemList.add(item);
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
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return monitorItemList;
	} 
	
	public  List<MonitorItemResultBean> findMonitorItemResultList(Connection conn ,BigDecimal monitorIItemId,String statusDesc) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<MonitorItemResultBean> resultItemList = new ArrayList<MonitorItemResultBean> ();
		int row = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from PENSBI.monitor_item_result where 1=1 \n");
			sql.append(" and monitor_item_id ="+monitorIItemId +"\n");
			sql.append(" and status ='"+statusDesc+"' \n");
			sql.append(" order by no asc ");
		    logger.debug("SQL:"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				MonitorItemResultBean item = new  MonitorItemResultBean();
				row++;
				item.setRow(row);
				item.setMonitorItemId(rs.getBigDecimal("monitor_item_id"));
				item.setStatus(rs.getString("status"));
				item.setMsg(rs.getString("message"));
				resultItemList.add(item);
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
		return resultItemList;
	} 
	
	public String findMonitorItemColumnHeadResultList(Connection conn ,BigDecimal monitorIItemId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String columnHeadTable = "";
		int row = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from PENSBI.MONITOR_ITEM_COLUMN_RESULT where 1=1 \n");
			sql.append(" and monitor_item_id ="+monitorIItemId +"\n");
		    logger.debug("SQL:"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				columnHeadTable = Utils.isNull(rs.getString("COLUMN_HEAD"));
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
		return columnHeadTable;
	} 
	public  List<MonitorItemResultBean> findMonitorItemResultListByTypeMessage(Connection conn,BigDecimal monitorIItemId,String typeMessage) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<MonitorItemResultBean> resultItemList = new ArrayList<MonitorItemResultBean> ();
		int row = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from PENSBI.monitor_item_result where 1=1 \n");
			sql.append(" and monitor_item_id ="+monitorIItemId +"\n");
			sql.append(" and monitor_item_id in(select id from monitor_item where id ="+monitorIItemId+" )");
			sql.append(" and message like'"+typeMessage+"%' \n");
			sql.append(" order by no asc ");
		    logger.debug("SQL:"+sql.toString());

			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				MonitorItemResultBean item = new  MonitorItemResultBean();
				row++;
				item.setRow(row);
				item.setMonitorItemId(rs.getBigDecimal("monitor_item_id"));
				item.setStatus(rs.getString("status"));
				item.setMsg(rs.getString("message"));
				resultItemList.add(item);
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
		return resultItemList;
	} 
	
	
	public  MonitorItemDetailBean[] findMonitorItemDetailBeanList(BigDecimal monitorItemId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorItemDetailBean[] itemBean = null;
		List<MonitorItemDetailBean> itemList = new ArrayList<MonitorItemDetailBean> ();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from PENSBI.monitor_item_detail where 1=1  and monitor_item_id ="+monitorItemId+"\n");
	
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
				 m.setAmountStr(Utils.convertToCurrencyStr(rs.getDouble("amount")));
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
	
	
	public MonitorBean updateMonitor(MonitorBean model) throws Exception {
		Connection conn = null;
		try{
			conn =DBConnection.getInstance().getConnectionApps();
			model = updateMonitor(conn, model);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return model;
	}
	public MonitorBean updateMonitor(Connection conn,MonitorBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;

		try {
			String sql = "UPDATE PENSBI.monitor SET " +
			" status = ? ,file_count =? ,error_code = ? ,error_msg =? ,transaction_type = ? "+
			" ,file_name = ? ,type =? ,th_name = ? ,update_date =? "+
			" WHERE MONITOR_ID = ? and transaction_id =?";
			
			logger.debug("SQL:"+sql);

			logger.debug("monitor_id:"+model.getMonitorId()+",transId:"+model.getTransactionId());
		
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setInt(++index, model.getStatus());
			ps.setInt(++index, model.getFileCount());
			ps.setString(++index, Utils.isNull(model.getErrorCode()).length()> 300?model.getErrorCode().substring(0,299):model.getErrorCode());
			ps.setString(++index, Utils.isNull(model.getErrorMsg()).length()> 300?model.getErrorMsg().substring(0,299):model.getErrorMsg());
			ps.setString(++index, Utils.isNull(model.getTransactionType()));
			ps.setString(++index, Utils.isNull(model.getFileName()));
			ps.setString(++index, Utils.isNull(model.getType()));
			ps.setString(++index, Utils.isNull(model.getThName()));
			if(model.getStatus()==-1 || model.getStatus()==1){
				ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			}else{
				ps.setTimestamp(++index,null);
			}
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
	
	public MonitorBean updateMonitorCaseError(Connection conn,MonitorBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;

		try {
			String sql = "UPDATE PENSBI.monitor SET " +
			" status = ? ,channel = ? ,file_count =? ,error_code = ? ,error_msg =?"+
			" WHERE MONITOR_ID = ? and transaction_id =?";
			
			logger.debug("SQL:"+sql);

			logger.debug("monitor_id:"+model.getMonitorId()+",transType:"+model.getTransactionType());
		
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setInt(++index, model.getStatus());
			ps.setInt(++index, model.getBatchTaskStatus());
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
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE PENSBI.monitor_item SET " +
			"  status = ? ,error_msg = ? ,success_count =? ,fail_count=?"+
			" WHERE ID = ?";
			
			logger.debug("SQL:"+sql +"\nSuccessCount["+model.getSuccessCount()+"]");

			logger.debug("id:"+model.getId());
		
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setInt(++index, model.getStatus());
			ps.setString(++index, Utils.isNull(model.getErrorMsg()).length()> 300?model.getErrorMsg().substring(0,299):model.getErrorMsg());
		
			ps.setInt(++index, model.getSuccessCount());
			ps.setInt(++index, model.getFailCount());
			ps.setBigDecimal(++index, model.getId());
			
			int ch = ps.executeUpdate();
		
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
			String sql = "UPDATE PENSBI.monitor SET  status = "+Constants.STATUS_REGEN+"  WHERE  transaction_id ="+model.getTransactionId();
			String sql2 = "UPDATE PENSBI.monitor_item SET  status = "+Constants.STATUS_REGEN+"  WHERE monitor_id in(select monitor_id from monitor where  transaction_id ="+model.getTransactionId()+")";
			
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
	
	public  static String getStatDesc(int status){
		String statusDesc ="";
		if(Constants.STATUS_START==status){
			statusDesc = "Start";
		}else 	if(Constants.STATUS_SUCCESS==status){
			statusDesc = "SUCCESS";
		}else 	if(Constants.STATUS_FAIL==status){
			statusDesc = "FAIL";
		}
		return statusDesc;
	}

}
