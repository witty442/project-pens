package com.pens.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SQLHelper {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuffer genFilterByStoreType(Connection conn,String storeType,String columnName) throws Exception{
		StringBuffer sqlCond = new StringBuffer("");
		String sqlSelect = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int no = 0;
		try{
			sqlSelect  ="select pens_value from PENSBI.PENSBME_MST_REFERENCE where reference_code ='Customer' ";
			sqlSelect +="and pens_desc3 ='"+storeType+"'";
			
			ps = conn.prepareStatement(sqlSelect);
			rs = ps.executeQuery();
			sqlCond.append("\n  AND ( ");
			while(rs.next()){
				if(no==0){
				   sqlCond.append("    "+columnName+" like  '"+Utils.isNull(rs.getString("pens_value"))+"%' \n");
				}else{
				   sqlCond.append("\t\t\t OR "+columnName+" like  '"+Utils.isNull(rs.getString("pens_value"))+"%' \n");
				}
				no++;
			}
			sqlCond.append("      ) \n");
		    return sqlCond;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
	}
	
	public static StringBuffer genFilterByStoreTypeEquals(Connection conn,String storeType,String columnName) throws Exception{
		StringBuffer sqlCond = new StringBuffer("");
		String sqlSelect = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int no = 0;
		try{
			sqlSelect  ="select pens_value from PENSBI.PENSBME_MST_REFERENCE where reference_code ='Customer' ";
			sqlSelect +="and pens_desc3 ='"+storeType+"'";
			
			ps = conn.prepareStatement(sqlSelect);
			rs = ps.executeQuery();
			sqlCond.append("\n  AND ( ");
			while(rs.next()){
				if(no==0){
				   sqlCond.append("    "+columnName+" =  '"+Utils.isNull(rs.getString("pens_value"))+"' \n");
				}else{
				   sqlCond.append("\t\t\t OR "+columnName+" =  '"+Utils.isNull(rs.getString("pens_value"))+"' \n");
				}
				no++;
			}
			sqlCond.append("      ) \n");
		    return sqlCond;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps !=null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
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
			str.append("<table align='center' border='1' cellpadding='3' cellspacing='1' class='result'> \n");
			str.append("<tr>");  
			for(int i=1;i<=columnCount;i++){
				    //System.out.println("["+i+"]"+rsm.getColumnName(i));
				    str.append("<th> \n");
	            	str.append(rsm.getColumnName(i));
	            	str.append("</th>");
			}
			str.append("</tr> \n"); 
			 
			//Gen Detail
			while(rs.next()){
				str.append("<tr> \n");  
				for(int i=1;i<=columnCount;i++){
					  str.append("<td class='text'>");
		              str.append(Utils.isNull(rs.getString(rsm.getColumnName(i))));
		              str.append("</td>");
				}
			    str.append("</tr> ");  
			}
		   str.append("</table> \n");
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
	
	public static int excUpdateOneSql(Connection conn,String sqlOne) {
	    PreparedStatement ps =null;
	    int recordUpdate = 0;
		try{  
		    ps = conn.prepareStatement(sqlOne);
			recordUpdate = ps.executeUpdate();
		}catch(Exception e){
	      logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return recordUpdate;
  }

	 public static String converToTextSqlIn(String value){
			
			List<String> valuesText = new ArrayList<String>() ;
			String[] values = value.split("[,]");
			
			for(String text : values){
				valuesText.add("'"+text+"'");
			}
			
			return StringUtils.join(valuesText, ","); 
		}
}
