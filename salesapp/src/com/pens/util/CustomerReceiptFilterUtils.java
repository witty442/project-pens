package com.pens.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;


public class CustomerReceiptFilterUtils {

	public static Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Filter Can Receipt Cheque
	///	String canReceiptCheque = ReceiptFilterUtils.canReceiptCheque(customerCode);

	}
	
	/**
	 * 
	 * @param customerId
	 * @return
	 * :for van 
	 */
	public  static String canReceiptCheque(Connection conn,long customerId){
		String canFlag = "N";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select count(*) as x from m_customer c, c_customer_receipt_cheque r \n" +
					" where c.customer_id ="+customerId+" and (r.isactive ='Y' or r.isactive is null or trim(r.isactive) ='') \n"+
					" and r.customer_code = c.code \n";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("x") > 0){
					canFlag = "Y";
				}
			}
			
			logger.debug("canFlag:"+canFlag);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				
				if(rs !=null){
				   rs.close();rs=null;
				}
				if(ps !=null ){
				  ps.close();ps=null;
				}
			}catch(Exception e){
				
			}
		}
		return canFlag;
	}
	
	public  static String canReceiptCredit(Connection conn,long customerId){
		String canFlag = "N";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select count(*) as x from m_customer c, c_customer_receipt_credit r \n" +
					" where c.customer_id ="+customerId+" and (r.isactive ='Y' or r.isactive is null or trim(r.isactive) ='') \n"+
					" and r.customer_code = c.code \n";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("x") > 0){
					canFlag = "Y";
				}
			}
			
			logger.debug("canFlag:"+canFlag);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs !=null){
				   rs.close();rs=null;
				}
				if(ps !=null ){
				  ps.close();ps=null;
				}
			}catch(Exception e){
				
			}
		}
		return canFlag;
	}
	
	public  static String canAirpay(Connection conn,int customerId){
		String canFlag = "N";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select airpay_flag from m_customer c \n" +
					" where c.customer_id ="+customerId+" \n";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				if(Utils.isNull(rs.getString("airpay_flag")).equals("Y")){
					canFlag = "Y";
				}
			}
			
			logger.debug("canFlag:"+canFlag);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(rs !=null){
				   rs.close();rs=null;
				}
				if(ps !=null ){
				  ps.close();ps=null;
				}
			}catch(Exception e){
				
			}
		}
		return canFlag;
	}

}
