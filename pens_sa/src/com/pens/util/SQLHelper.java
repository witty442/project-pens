package com.pens.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pens.util.excel.ExcelHeader;

public class SQLHelper {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static String genStrCondChkNull(String value,String schema){
		String sql = "";
		if( !Utils.isNull(value).equals("")){
			sql = "\n "+schema +" '"+Utils.isNull(value)+"'";
		}
		return sql;	
	}
	
	public static String genStrCond(String value,String schema){
		return "\n "+schema +" '"+Utils.isNull(value)+"'";	
	}
	
	public static String genStrArrCondChkNull(String value,String schema){
		String sql = "";
		if( !Utils.isNull(value).equals("") &&  !Utils.isNull(value).equals("ALL")){
			sql = "\n "+schema +" ("+converToTextSqlIn(value)+")";
		}
		return sql;	
	}
	
	public static String genStrArrCond(String value,String schema){
		return "\n "+schema +" ("+converToTextSqlIn(value)+")";
	}
	
	public static String genNumCondChkNull(String value,String schema){
		String sql = "";
		if( !Utils.isNull(value).equals("")){
			sql = "\n "+schema +" "+Utils.isNull(value)+"";
		}
		return sql;	
	}
	
	public static String genNumCond(String value,String schema){
		return "\n "+schema +" "+Utils.isNull(value)+"";	
	}
	
	public static String genTHDateCondChkNull(String value,String format,String schema) throws Exception{
		String sql = "";
		if( !Utils.isNull(value).equals("")){
			Date d = DateUtil.parse(value, format,Utils.local_th);
			String dateStr = DateUtil.stringValue(d, format);//en format
			sql = "\n and "+schema +" to_date('"+dateStr+"','"+format+"')";
		}
		return sql;	
	}
	
	public static String genTHDateCond(String value,String format,String schema) throws Exception{
		String sql = "";
		Date d = DateUtil.parse(value, format,Utils.local_th);
		String dateStr = DateUtil.stringValue(d, format);//en format
		sql = "\n and "+schema +" to_date('"+dateStr+"','"+format+"')";
		return sql;
	}
	
   //input 19,12,11 = '19','12','11'
   public static String converToTextSqlIn(String value){
		List<String> valuesText = new ArrayList<String>() ;
		String[] values = value.split("[,]");
		
		for(String text : values){
			valuesText.add("'"+text+"'");
		}
		
		return StringUtils.join(valuesText, ","); 
	}
   public static String excQuery(String sql) {
	    PreparedStatement ps =null;
		ResultSet rs = null;
       Connection conn = null;
       ResultSetMetaData rsm = null;
       int columnCount = 0;
       StringBuffer str = new StringBuffer("");
		try{
			System.out.println("sql:"+sql);   
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			rsm = rs.getMetaData();
			columnCount = rsm.getColumnCount();
			
			//getColumnHeader 
			str.append(ExcelHeader.EXCEL_HEADER);
			str.append("<table align='center' border='1' cellpadding='3' cellspacing='1' class='result'>");
			str.append("<tr>");  
			for(int i=1;i<=columnCount;i++){
			    //System.out.println("["+i+"]"+rsm.getColumnName(i));
			    str.append("<th>");
            	str.append(rsm.getColumnName(i));
            	str.append("</th>");
			 }
			 str.append("</tr>"); 
			 
			 //Gen Detail
			 while(rs.next()){
				 str.append("<tr>");  
				 for(int i=1;i<=columnCount;i++){
					    str.append("<td class='lineE'>");
		            	str.append(Utils.isNull(rs.getString(rsm.getColumnName(i))));
		            	str.append("</td>");
				   }
				 str.append("</tr>");  
			 }
			
			str.append("</table>");
			
		}catch(Exception e){
	      e.printStackTrace();
	      str.append("ERROR: \n"+e.getMessage());
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
				if(rs != null){
				   rs.close();rs = null;
				}
				
				if(conn != null){
					conn.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return str.toString();
 }
	public static String excQueryApps(String sql) {
	    PreparedStatement ps =null;
		ResultSet rs = null;
       Connection conn = null;
       ResultSetMetaData rsm = null;
       int columnCount = 0;
       StringBuffer str = new StringBuffer("");
		try{
			System.out.println("sql:"+sql);   
			conn = DBConnection.getInstance().getConnectionApps();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			rsm = rs.getMetaData();
			columnCount = rsm.getColumnCount();
			
			//getColumnHeader 
			 str.append("<table align='center' border='1' cellpadding='3' cellspacing='1' class='result'>");
			 str.append("<tr>");  
			 for(int i=1;i<=columnCount;i++){
				    //System.out.println("["+i+"]"+rsm.getColumnName(i));
				    str.append("<th>");
	            	str.append(rsm.getColumnName(i));
	            	str.append("</th>");
			   }
			 str.append("</tr>"); 
			 
			 //Gen Detail
			 while(rs.next()){
				 str.append("<tr>");  
				 for(int i=1;i<=columnCount;i++){
					    str.append("<td class='lineE'>");
		            	str.append(Utils.isNull(rs.getString(rsm.getColumnName(i))));
		            	str.append("</td>");
				   }
				 str.append("</tr>");  
			 }
			
			str.append("</table>");
			
		}catch(Exception e){
	      e.printStackTrace();
	      str.append("ERROR: \n"+e.getMessage());
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
				if(rs != null){
				   rs.close();rs = null;
				}
				
				if(conn != null){
					conn.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return str.toString();
 }
	
  public static String excUpdate(String sql) {
		Connection conn = null;
		try{
		    conn = DBConnection.getInstance().getConnection();
		    return excUpdate(conn, sql);
		}catch(Exception e){
		      e.printStackTrace();
			}finally{
				try{
					if(conn != null){
						conn.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		return "";
	}
	
	public static String excUpdate(Connection conn,String sql) {
	   PreparedStatement ps =null;
       StringBuffer str = new StringBuffer("");
		try{  
			String[] sqlArr = sql.split("\\;");
			if(sqlArr != null && sqlArr.length>0){
			   for(int i=0;i<sqlArr.length;i++){
				 
				 if( !Utils.isNull(sqlArr[i]).equals("")){
				     ps = conn.prepareStatement(sqlArr[i]);
				     int recordUpdate = ps.executeUpdate();
				     str.append("<br>["+i+"] SQL Execute  :"+sqlArr[i]);
				     str.append("<br>- Result Effect:"+recordUpdate+"");
			     }
			   }
			}
		}catch(Exception e){
	      e.printStackTrace();
	      str.append("ERROR: \n"+e.getMessage());
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return str.toString();
 }
}
