package com.pens.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.bean.StoreBean;

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
}
