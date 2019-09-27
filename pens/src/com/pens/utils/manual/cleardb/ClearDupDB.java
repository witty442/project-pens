package com.pens.utils.manual.cleardb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;

public class ClearDupDB {

	
	protected static  Logger logger = Logger.getLogger("PENS");
	public static  StringBuffer msgHead = new StringBuffer("");
	public static  StringBuffer msg = new StringBuffer("");
	private static int toatlCustomer = 0;
	private static int recAddress = 0;
	private static int recContact = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		clearDupCustDB();
		clearDupCustAddressDB();
		clearDupCustContactDB();
	}
	
	//Cust dup
	public static StringBuffer clearDupCustDB(){
		Connection conn = null;
		msgHead = new StringBuffer("");
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		boolean isFoundCustIdInOrder = false;
		String customerIdMain = "";
		try{
			msgHead.append("*** Start Clear Duplicate Customer DB***************\n");
			
			sql.append("  select c.CODE  ,count(*) from m_customer c \n");
			//sql.append("  where code ='235010002' \n");
			sql.append("  group by c.CODE \n");
			sql.append("  having count(*) >1  \n");
			  
			logger.debug("sql:"+sql.toString());
			
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				toatlCustomer++;
				
				List<String> customerIdList = getCustomerIdList(conn, rs.getString("code"));
				if(customerIdList != null && customerIdList.size()>0){
					 for(int i=0;i<customerIdList.size();i++){
						 String customerId = customerIdList.get(i);
						 
						 isFoundCustIdInOrder = isFoundCustIdInOrder(conn,customerId);
						 logger.debug("customerId["+customerId+"]isFoundCustIdInOrder["+isFoundCustIdInOrder+"]");
						 if(isFoundCustIdInOrder==false){
							 logger.debug("not found in order -> delete customerCode["+rs.getString("code")+"]customerId["+customerId+"]");
							 String delCustomer =  "delete from m_customer where customer_id="+customerId;
							 logger.debug(delCustomer);
							 excUpdate(conn,delCustomer);
						 }else{
							 customerIdMain = customerId; 
						 }
					 }//for
					 
					 String sqlUpdateCustIdAddress = "update  m_address set  customer_id="+customerIdMain +" where \n";
					 sqlUpdateCustIdAddress += "customer_id in(select customer_id from m_customer where code ='"+rs.getString("code")+"') \n";
					 
					 logger.debug(sqlUpdateCustIdAddress);
					 excUpdate(conn,sqlUpdateCustIdAddress);
					 
					 String sqlUpdateCustIdContact = "update  m_contact set  customer_id="+customerIdMain +" where \n";
					 sqlUpdateCustIdContact += "customer_id in(select customer_id from m_customer where code ='"+rs.getString("code")+"') \n";
					 
					 logger.debug(sqlUpdateCustIdContact);
					 excUpdate(conn,sqlUpdateCustIdContact);
				}//if
			 }//while
			 
			msgHead.append("*** Total Customer Duplicate :["+toatlCustomer +"] ***************\n");
			
			msgHead.append(msg);
			conn.commit();
			msgHead.append("\n*** End Clear Duplicate Address DB ***************");
			
			logger.error("End Process");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				 if(ps != null){
					   ps.close();ps=null;
			     }
			     if(rs != null){
				   rs.close();rs =null;
			    }
				if(conn != null){
					conn.close();conn =null;
				}
			}catch(Exception e){}
		}
		return msgHead;
	}
	public static List<String> getCustomerIdList(Connection conn ,String customerCode){
        List<String> customerIdList = new ArrayList<String>();
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("select customer_id from m_customer where code='"+customerCode+"'");
            logger.debug("sql:"+sql.toString());
             
            ps =conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while(rs.next()){
            	String customerId = String.valueOf(rs.getInt("customer_id"));
            	customerIdList.add(customerId);
            }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			   if(ps != null){
				   ps.close();ps=null;
			   }
			   if(rs != null){
				   rs.close();rs =null;
			   }
			}catch(Exception e){}
		}
		return customerIdList;
	}
	
	public static boolean isFoundCustIdInOrder(Connection conn ,String customerId){
        boolean found = false;
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("select count(*) c from t_order where customer_id ="+customerId);
            logger.debug("sql:"+sql.toString());
             
            ps =conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            if(rs.next()){
            	if(rs.getInt("c") >0){
            		found = true;
            	}
            }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			   if(ps != null){
				   ps.close();ps=null;
			   }
			   if(rs != null){
				   rs.close();rs =null;
			   }
			}catch(Exception e){}
		}
		return found;
	}

	
	public static StringBuffer clearDupCustAddressDB(){
		Connection conn = null;
		msgHead = new StringBuffer("");
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			msgHead.append("*** Start Clear Duplicate Address DB***************\n");
			
			sql.append("  select c.CUSTOMER_ID  ,count(*) from m_customer c , m_address a \n");
			sql.append("  where a.customer_id = c.customer_id \n");
			sql.append("  group by c.CUSTOMER_ID \n");
			sql.append("  having count(*) >2  \n");
			  
			logger.debug("sql:"+sql.toString());
			
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			 while(rs.next()){
				 toatlCustomer++;
				 
				 //delete all exception last created 
				 deleteAddressDupByPurposeCaseOrderByCreateDateDesc(conn,"B",rs.getString("customer_id"));
				 deleteAddressDupByPurposeCaseOrderByCreateDateDesc(conn,"S",rs.getString("customer_id"));
				 
				 
				// Delete by ref id asc
				// deleteAddressDupByPurposeCaseOrderByRefID(conn,"B",rs.getString("customer_id"));
				// deleteAddressDupByPurposeCaseOrderByRefID(conn,"S",rs.getString("customer_id"));
				 
				 // Delete by update desc
				 //deleteAddressDupByPurposeCaseOrderByUpdated(conn,"B",rs.getString("customer_id"));
				 //deleteAddressDupByPurposeCaseOrderByUpdated(conn,"S",rs.getString("customer_id")); 
			 }
			 
			msgHead.append("*** Total Customer Duplicate :["+toatlCustomer +"] ***************\n");
			msgHead.append("*** Total delete m_address Record :["+recAddress +"] ***************");
			msgHead.append(msg);
			conn.commit();
			msgHead.append("\n*** End Clear Duplicate Address DB ***************");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				 if(ps != null){
					   ps.close();ps=null;
			     }
			     if(rs != null){
				   rs.close();rs =null;
			    }
				if(conn != null){
					conn.close();conn =null;
				}
			}catch(Exception e){}
		}
		return msgHead;
	}
	
	public static StringBuffer deleteAddressDupByPurposeCaseOrderByCreateDateDesc(Connection conn ,String purpose,String customerId){

		PreparedStatement ps =null;
		ResultSet rs = null;
		int count = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql = new StringBuffer("");
			sql.append(" select * from m_address where customer_id="+customerId+" and purpose ='"+purpose+"' order by created desc");
			
			//logger.debug("sql p:"+sql.toString());
			
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while(rs.next()){
            	count++;
            	if(count>1){
            		//delete row
            		recAddress++;
            		excUpdate(conn,"delete from m_address where customer_id="+customerId+" and purpose='"+purpose+"' and address_id ="+rs.getInt("address_id"));
            	}
            }
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			   if(ps != null){
				   ps.close();ps=null;
			   }
			   if(rs != null){
				   rs.close();rs =null;
			   }
			}catch(Exception e){}
		}
		return msg;
	}

	public static StringBuffer deleteAddressDupByPurposeCaseOrderByRefID(Connection conn ,String purpose,String customerId){

		PreparedStatement ps =null;
		ResultSet rs = null;
		int count = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql = new StringBuffer("");
			sql.append(" select * from m_address where customer_id="+customerId+" and purpose ='"+purpose+"' order by reference_id asc");
			
			logger.debug("sql p:"+sql.toString());
			
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while(rs.next()){
            	count++;
            	if(count>1){
            		//delete row
            		recAddress++;
            		excUpdate(conn,"delete from m_address where customer_id="+customerId+" and purpose='"+purpose+"' and address_id ="+rs.getInt("address_id"));
            	}
            }
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			   if(ps != null){
				   ps.close();ps=null;
			   }
			   if(rs != null){
				   rs.close();rs =null;
			   }
			}catch(Exception e){}
		}
		return msg;
	}
	
	public static StringBuffer deleteAddressDupByPurposeCaseOrderByUpdated(Connection conn ,String purpose,String customerId){

		PreparedStatement ps =null;
		ResultSet rs = null;
		int count = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql = new StringBuffer("");
			sql.append(" select * from m_address where customer_id="+customerId+" and purpose ='"+purpose+"' order by updated desc");
			
			logger.debug("sql p:"+sql.toString());
			
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while(rs.next()){
            	count++;
            	if(count>1){
            		//delete row
            		recAddress++;
            		excUpdate(conn,"delete from m_address where customer_id="+customerId+" and purpose='"+purpose+"' and address_id ="+rs.getInt("address_id"));
            	}
            }
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			   if(ps != null){
				   ps.close();ps=null;
			   }
			   if(rs != null){
				   rs.close();rs =null;
			   }
			}catch(Exception e){}
		}
		return msg;
	}
	
	public static StringBuffer clearDupCustContactDB(){
		Connection conn = null;
		msgHead = new StringBuffer("");
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			msgHead.append("*** Start Clear Duplicate Contact DB***************\n");
			
			sql.append("  select c.CUSTOMER_ID  ,count(*) from m_customer c , m_contact a \n");
			sql.append("  where a.customer_id = c.customer_id \n");
			sql.append("  group by c.CUSTOMER_ID \n");
			sql.append("  having count(*) >2  \n");
			  
			logger.debug("sql:"+sql.toString());
			
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			 while(rs.next()){
				 toatlCustomer++;
				 
				 //delete all exception last created 
				 deleteContactDupByCustomerId(conn,rs.getString("customer_id"));
				 
			 }
			 
			msgHead.append("*** Total Customer Duplicate :["+toatlCustomer +"] ***************\n");
			msgHead.append("*** Total delete m_contact Record :["+recContact +"] ***************");
			msgHead.append(msg);
			conn.commit();
			msgHead.append("\n*** End Clear Duplicate Contact DB ***************");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				 if(ps != null){
					   ps.close();ps=null;
			     }
			     if(rs != null){
				   rs.close();rs =null;
			    }
				if(conn != null){
					conn.close();conn =null;
				}
			}catch(Exception e){}
		}
		return msgHead;
	}
	
	
	public static StringBuffer deleteContactDupByCustomerId(Connection conn ,String customerId){
		PreparedStatement ps =null;
		ResultSet rs = null;
		int count = 0;
		StringBuffer sql = new StringBuffer("");
		try{
			sql = new StringBuffer("");
			sql.append(" select * from m_contact where customer_id="+customerId+" order by updated desc");
			//logger.debug("sql p:"+sql.toString());
			
            ps = conn.prepareStatement(sql.toString());
            rs = ps.executeQuery();
            while(rs.next()){
            	count++;
            	if(count>1){
            		//delete row
            		recContact++;
            		logger.debug("delete from m_contact where customer_id="+customerId+" and contact_id ="+rs.getInt("contact_id"));
            		excUpdate(conn,"delete from m_contact where customer_id="+customerId+" and contact_id ="+rs.getInt("contact_id"));
            	}
            }
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			   if(ps != null){
				   ps.close();ps=null;
			   }
			   if(rs != null){
				   rs.close();rs =null;
			   }
			}catch(Exception e){}
		}
		return msg;
	}
	
	private static boolean excUpdate(Connection conn,String sql) {
	    PreparedStatement ps =null;
        boolean result = true;
		try{  
			
			String[] sqlArr = sql.split("\\;");
			if(sqlArr != null && sqlArr.length>0){
			  //str.append("\n ------ Result ----------------------- ");
			   for(int i=0;i<sqlArr.length;i++){
				 
				 if( !Utils.isNull(sqlArr[i]).equals("")){
					 try{
						 //msg.append("\n"+sqlArr[i]+";");
						 		
					     ps = conn.prepareStatement(sqlArr[i]);
					     int recordUpdate = ps.executeUpdate();

					     msg.append("\n"+sqlArr[i]+"/*- Result:"+recordUpdate+" Record */");
					 }catch(Exception ee){
						 ee.printStackTrace();
						 msg.append("\n"+sqlArr[i]+"/*- Result ERROR:"+ee.getMessage()+"*/ "); 
						 result = false;
					 }
			     }
			   }//for
			  // str.append("\n ----------------------------------\n");
			}
		}catch(Exception e){
	      logger.error(e.getMessage(),e);
	      msg.append("/* err: \n"+e.getMessage() +"\n */");
	      result = false;
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return result;
  }
	
}
