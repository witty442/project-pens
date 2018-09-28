package com.isecinc.pens.interim.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class MOrderToReceipt {
	
	public static List<IOrderToReceipt> getOrderToReceiptList(Connection conn , String whereClause,Object[] params) throws Exception{
		List<IOrderToReceipt> retL = new ArrayList<IOrderToReceipt>();
		
		StringBuffer sql = new StringBuffer("SELECT * FROM I_ORDER_TO_RECEIPT WHERE 1=1 ");
		if(!StringUtils.isEmpty(whereClause))
			sql.append(whereClause);
		
		PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
		
		if(!StringUtils.isEmpty(whereClause)){
			int paramIdx = 0;
			for(Object obj:params){
				paramIdx++;
				
				if(obj instanceof String)
					ppstmt.setString(paramIdx, (String)obj);
				else if(obj instanceof Integer)
					ppstmt.setInt(paramIdx, (Integer)obj);
				else if(obj instanceof Double)
					ppstmt.setDouble(paramIdx, (Double)obj);
				else if(obj instanceof Timestamp)
					ppstmt.setTimestamp(paramIdx, (Timestamp)obj);
				else
					ppstmt.setObject(paramIdx, obj);

			}
		}
			
		ResultSet rs = ppstmt.executeQuery();
		while(rs.next()){
			retL.add(new IOrderToReceipt(rs));
		}
		
		rs =null;
		ppstmt = null;
		
		return retL;
	}
	
	public static int update(Connection conn ,String column,IOrderToReceipt tOrder) throws Exception{
		int ret = 0;

		String sql = "UPDATE I_ORDER_TO_RECEIPT SET "+column+" = ? WHERE SESSION_ID = ? AND AR_INVOICE_NO = ? ";
		
		PreparedStatement ppstmt = conn.prepareStatement(sql);
		
		String updateValue = tOrder.getReceiptNo();
		if("ERROR_MESSAGE".equalsIgnoreCase(column))
			updateValue = tOrder.getErrMsg();
		
		ppstmt.setString(1, updateValue);
		ppstmt.setString(2, tOrder.getSessionId());
		ppstmt.setString(3,tOrder.getArInvoiceNo());
		
		ret = ppstmt.executeUpdate();
		
		System.out.println("UPDATE "+column+" : "+updateValue+" Invoice No. "+tOrder.getArInvoiceNo());
		
		ppstmt = null;

		return ret;
	}
	
	public static int deleteBySessionId(Connection conn , String sessionId) throws Exception{
		int ret = 0;
		String sql = "DELETE FROM I_ORDER_TO_RECEIPT WHERE SESSION_ID = ? ";
		
		PreparedStatement ppstmt = conn.prepareStatement(sql);
		ppstmt.setString(1, sessionId);
		ret = ppstmt.executeUpdate();

		ppstmt = null;
		
		return ret;
	}
}
