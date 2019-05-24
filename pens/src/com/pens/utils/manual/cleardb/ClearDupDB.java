package com.pens.utils.manual.cleardb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
	private static int rec = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static StringBuffer clearDupCustDB(){
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
			msgHead.append("*** Total delete m_address Record :["+rec +"] ***************");
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
            		rec++;
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
            		rec++;
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
            		rec++;
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
