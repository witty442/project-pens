package com.pens.util.manual.cleardb;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;

public class ClearDB {

	
	protected static  Logger logger = Logger.getLogger("PENS");
	public static  StringBuffer msg = new StringBuffer("");
	public static StringBuffer sql = new StringBuffer("");
	
	public static String date = "2014-01-01";// <2014
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static StringBuffer clearDB(){
		Connection conn = null;
		msg = new StringBuffer("");
		try{
			msg.append("*** Start runScript ***************");
			
			sql.append(" /**  update m_customer no order in 6 month **/ \n");
			sql.append("update m_customer set isactive ='N'  where customer_id in( \n");
			sql.append("		select a.customer_id from( \n");
			sql.append("			  select  customer_id from m_customer where customer_id not in(\n");
			sql.append("			      select customer_id from t_order  where  order_date >=  '2013-10-01'  \n");
			sql.append("		  	 )\n");
			sql.append("		  )a\n");
			sql.append(");\n");
			
			
			sql.append(" /**  t_receipt_match  line **/ \n");
			sql.append(" delete from t_receipt_match where receipt_line_id in (   \n");
			sql.append("     select receipt_line_id from t_receipt_line where receipt_id in(  \n");
			sql.append("        select receipt_id from t_receipt where   receipt_date <  '"+date+"'  \n");
			sql.append("     )   \n");
			sql.append("  ) ;   \n");
			
			sql.append(" /**  t_receipt_match head **/ \n");
			sql.append("  delete from t_receipt_match where receipt_id in (   \n");
			sql.append("       select receipt_id from t_receipt where   receipt_date <  '"+date+"'  \n");
			sql.append("  ) ;   \n");
			
			sql.append("/*** t_receipt_by  line **/ \n");
			sql.append(" delete from t_receipt_by where receipt_by_id in( \n");
			sql.append("    select receipt_by_id from t_receipt_match where receipt_line_id in ( \n");
			sql.append("     select receipt_line_id from t_receipt_line where receipt_id in( \n");
			sql.append("        select receipt_id from t_receipt   where  receipt_date <  '"+date+"' \n");
			sql.append("     ) \n");
			sql.append("   ) \n");
			sql.append(" ) ; \n");
			
			
			sql.append(" /** t_receipt_match_cn **/ \n");
			sql.append(" delete from t_receipt_match_cn where receipt_id in (  \n");
			sql.append("    select receipt_id  from t_receipt where 1=1 and receipt_date <  '"+date+"' \n");
			sql.append(") ; \n");
			
			
			sql.append("/*** t_receipt_by  head **/ \n");
			sql.append(" delete from t_receipt_by where receipt_id in( \n");
			sql.append("    select receipt_id from t_receipt   where  receipt_date <  '"+date+"' \n");
			sql.append(" ) ; \n");
			
			
			sql.append("/** t_receipt_cn  **/ \n");
			sql.append(" delete from t_receipt_cn where receipt_id in(  \n");
			sql.append("   select a.receipt_id from ( \n");
			sql.append("      select receipt_id from t_receipt  where  receipt_date <  '"+date+"'  \n"); 
			sql.append("    )a \n");
			sql.append(" ); \n");
		   
			sql.append("/** t_receipt_line ***/   \n"); 
			sql.append(" delete from t_receipt_line where receipt_id in(  \n"); 
			sql.append("   select receipt_id from t_receipt where  receipt_date <  '"+date+"'  \n"); 
			sql.append(" );  \n"); 
			
			sql.append("/** t_receipt ***/   \n"); 
			sql.append(" delete from t_receipt where receipt_id in(  \n"); 
			sql.append("   select a.receipt_id from ( \n");
			sql.append("      select receipt_id from t_receipt  where  receipt_date <  '"+date+"'  \n"); 
			sql.append("    )a \n"); 
			sql.append(" );  \n"); 
			 
			
			sql.append("/** t_order_line **/ \n");
			sql.append("delete  from t_order_line where order_id in ( \n");
			sql.append("  select order_id from t_order  where order_date <  '"+date+"' \n");
			sql.append(" ) ; \n");
			
			sql.append("/** t_order **/ \n");
			sql.append("delete  from t_order where order_id in ( \n");
			sql.append("   select a.order_id from ( \n");
			sql.append("      select order_id from t_order  where  order_date <  '"+date+"'  \n"); 
			sql.append("    )a \n");
			sql.append(" ) ; \n");
			
			sql.append("/** t_move_order_line **/ \n");
			sql.append("delete  from t_move_order_line where request_number in ( \n");
			sql.append("  select request_number from t_move_order \n");
			sql.append("  where request_date <  '"+date+"'  \n");
			sql.append(" ) ; \n");
			
			sql.append("/** t_move_order **/ \n");
			sql.append("delete  from t_move_order where request_number in ( \n");
			sql.append("  select a.request_number from( \n");
			sql.append("     select request_number from t_move_order \n");
			sql.append("     where request_date <  '"+date+"'  \n");
			sql.append("   )a \n");
			sql.append(" ) ; \n");
			
			sql.append("/** Clear  m_address  duplicate **/  \n");
			sql.append(" delete from m_address where address_id in( \n");
			sql.append("    select a.address_id  from ( \n");
			sql.append("  	  	 select address_id from m_address where created_by ='0' and customer_id in( \n");
			sql.append("		      select customer_id  from m_address where 1=1 \n");
			sql.append("		      group by customer_id \n");
			sql.append("		      having count(*) >2 \n");
			sql.append("		   ) \n");
			sql.append("   )a \n");
			sql.append(" ); \n");
			  
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
		   //run script split by ";"
			boolean result = excUpdate(conn,sql.toString());
			msg.append("\n Result :"+result);
			if(result){
				conn.commit();
				msg.append("\n Connection commit");
			}else{
				conn.rollback();
				msg.append("\n Connection Rollback");
			}
			
			msg.append("\n *** End Clear DB ***************");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();conn =null;
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
