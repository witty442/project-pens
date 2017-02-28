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
import com.isecinc.pens.inf.bean.MonitorItemResultBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.ExceptionHandle;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ConvertUtils;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

/**
 * @author WITTY
 *
 */
public class InterfaceDAO extends InterfaceUtils{

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
	public  String findMonitorStatus(Connection conn,String id) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String status = "";
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select channel as batch_task_status from monitor \n");
			sql.append("where MONITOR_ID = (select min(monitor_id) from monitor where transaction_id ="+id+") \n");
			
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
    
    public static void clearTaskControlMonitorAll() {
    	Connection conn = null;
    	PreparedStatement ps = null;
    	try {
            conn = DBConnection.getInstance().getConnection();
	       	String sql = "UPDATE c_monitor SET  transaction_id = 0";
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
    
    public void updateControlMonitor(Connection conn,BigDecimal transactionId,String type) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE c_monitor SET  transaction_id = ? WHERE action = ?";
			//logger.info("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, transactionId);
			ps.setString(++index,type);
			
			int r = ps.executeUpdate();
			if(r==0){
				//insert 
				sql = "insert into c_monitor(action,transaction_id)values(?,?)";
				index = 0;
				ps = conn.prepareStatement(sql);
				ps.setString(++index,type);
				ps.setBigDecimal(++index, transactionId);
				ps.executeUpdate();
				
			}
		
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	public  String findControlMonitor(String action) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		String status = "";
		Connection conn = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from c_monitor where action ='"+action+"' \n");
			
		    logger.debug("SQL:"+sql.toString());
		    conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				status = rs.getString("transaction_id");
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
	
	public  MonitorBean[] findMonitorListNew(User user,String type) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		MonitorBean[] monitorBean = null;
		List<MonitorBean> monitorList = new ArrayList<MonitorBean> ();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select monitor.*  \n");
			sql.append(" , (select count(*) from monitor_item where monitor_item.monitor_id = monitor.monitor_id and status = "+Constants.STATUS_SUCCESS+" )  as success_count  \n");
			sql.append(" , (select max(id) from monitor_item where monitor_item.monitor_id = monitor.monitor_id)  as monitor_item_id  \n");
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
				m.setMonitorItemId(rs.getBigDecimal("monitor_item_id"));
				m.setTransactionType(rs.getString("TRANSACTION_TYPE"));
				m.setMonitorId(rs.getBigDecimal("monitor_id"));
				m.setName(rs.getString("name"));
				m.setType(rs.getString("type"));
				m.setStatus(rs.getInt("status"));
				m.setStatusDesc(getStatDesc(rs.getInt("status")));
				m.setCreateUser(rs.getString("create_user"));
				m.setCreateDate(rs.getDate("create_date"));
				m.setSubmitDate(rs.getTimestamp("submit_date"));
	            m.setFileCount(rs.getInt("file_count"));
	            m.setSuccessCount(rs.getInt("success_count"));
	            if( !Utils.isNull(rs.getString("error_msg")).equals("")){
	               m.setErrorMsg(rs.getString("error_msg"));
	            }else{
	               m.setErrorMsg(rs.getString("error_disp"));
	            }
	            
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
	
	public  MonitorItemBean findMonitorItemBean(User user, MonitorBean mc) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		MonitorItemBean item = new  MonitorItemBean();
		Connection conn = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *   \n");
			sql.append(" from monitor_item  \n");
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
			sql.append(" from monitor_item where 1=1 \n");
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
			sql.append(" select * from monitor_item_result where 1=1 \n");
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
	
	public  List<MonitorItemResultBean> findMonitorItemResultListByTypeMessage(Connection conn,BigDecimal monitorIItemId,String typeMessage) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<MonitorItemResultBean> resultItemList = new ArrayList<MonitorItemResultBean> ();
		int row = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select * from monitor_item_result where 1=1 \n");
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

			logger.debug("monitor_id:"+model.getMonitorId()+",transId:"+model.getTransactionId());
		
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
	
	
	public MonitorBean updateMonitorCaseError(Connection conn,MonitorBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;

		try {
			String sql = "UPDATE monitor SET " +
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
			String sql = "UPDATE monitor_item SET " +
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
		sql.append(" select monitor_item.id, \n");
		sql.append(" monitor_item.file_name, \n");
		sql.append(" monitor_item.table_name  \n");
		sql.append(" from monitor_item \n");
		sql.append(" inner join   \n");
		sql.append("   ( select max(monitor_item.id) as id,monitor_item.table_name from monitor ,monitor_item \n");
		sql.append("    where monitor.status <> "+Constants.STATUS_FAIL+" \n");
		sql.append("    and monitor_item.status <> "+Constants.STATUS_FAIL+" \n");
		sql.append("    and monitor.monitor_id = monitor_item.monitor_id \n");
		sql.append("    and monitor.type ='"+Constants.TYPE_IMPORT+"' \n");
		sql.append("    and monitor.create_user Like '%"+user.getUserName()+"%' \n");
		sql.append("    and monitor.transaction_type ='"+Constants.TRANSACTION_MASTER_TYPE+"' \n");
		if(importAll){
		   sql.append("      and monitor_item.file_name Like '%-ALL%' \n");
		}else{
		   sql.append("      and monitor_item.file_name not Like '%-ALL%' \n");	
		}
		sql.append("        group by monitor_item.table_name \n");
		sql.append("        ) s  \n");
		sql.append(" ON s.id = monitor_item.id and s.table_name = monitor_item.table_name \n");
		
		sql.append(" union all \n");
		
		/** CASE TRANSACTION AND UPDATE-TRANS-SALES   Check Status Only in monitor_item *****/
		sql.append(" select monitor_item.id, \n");
		sql.append(" monitor_item.file_name, \n");
		sql.append(" monitor_item.table_name  \n");
		sql.append(" from monitor_item \n");
		sql.append(" inner join   \n");
		sql.append("  ( select max(monitor_item.id) as id,monitor_item.table_name from monitor ,monitor_item \n");
		sql.append("    where 1=1 \n"); 
		sql.append("    and monitor_item.status <> "+Constants.STATUS_FAIL+" \n");
		sql.append("    and monitor.monitor_id = monitor_item.monitor_id \n");
		sql.append("    and monitor.type ='"+Constants.TYPE_IMPORT+"' \n");
		sql.append("    and monitor.create_user Like '%"+user.getUserName()+"%' \n");
		sql.append("    and monitor.transaction_type in('"+Constants.TRANSACTION_UTS_TRANS_TYPE+"','"+Constants.TRANSACTION_TRANS_TYPE+"','"+Constants.TRANSACTION_WEB_MEMBER_TYPE+"') \n");
		if(importAll){
		   sql.append("     and monitor_item.file_name Like '%-ALL%' \n");
		}else{
		   sql.append("     and monitor_item.file_name not Like '%-ALL%' \n");	
		}
		sql.append("        group by monitor_item.table_name \n");
		sql.append("  ) s  \n");
		sql.append(" ON s.id = monitor_item.id and s.table_name = monitor_item.table_name \n");
		
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

 
}
