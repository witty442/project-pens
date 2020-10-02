package com.isecinc.pens.inf.manager.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;

public class UpdateBankReceiptCaseVANProcess {
	
	public static Logger logger = Logger.getLogger("PENS");
	
	//Case Van PDPAID ='N' AND MONEY_TO_PENS ='Y  
	//update bank(last transfer cash) in t_receipt_by and payment_method ='CS' (CASH) ONLY
	public static int process(User userBean){
		Connection conn = null;
        int i = 0;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			updateBankReceipt(conn,userBean);
			
			updateBankReceiptBy(conn,userBean);
			
			conn.commit();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			if(conn != null){
				conn.close();conn= null;
			}
			}catch(Exception eee){}
		}
		return i;
	}
	public static int updateBankReceipt(Connection conn,User userBean) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
        int i = 0;
		try{
			sql.append("update t_receipt \n");
			sql.append("set INTERNAL_BANK = (");
			sql.append("  select transfer_bank from t_bank_transfer where line_id in( \n");
			sql.append("     select max(line_id) from t_bank_transfer \n");
			sql.append("     where (exported ='N' or exported is null OR TRIM(EXPORTED) ='') \n");
			sql.append("     and transfer_type ='CS' ) \n");
			sql.append(" ) \n");
			sql.append("where doc_status='SV' and (exported ='N' OR exported is null OR TRIM(EXPORTED) ='') \n");
			sql.append("and receipt_id in(  \n");
			sql.append("  select receipt_id from t_receipt_by ");
			sql.append("  where t_receipt_by.receipt_id = t_receipt.receipt_id \n");
			sql.append("  and payment_method ='CS' )  \n");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			i = ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
			if(ps != null){
				ps.close();ps= null;
			}
			}catch(Exception eee){}
		}
		return i;
	}
	
	public static int updateBankReceiptBy(Connection conn,User userBean) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
        int i = 0;
		try{
			sql.append("update t_receipt_by \n");
			sql.append("set BANK = (select transfer_bank from t_bank_transfer where line_id in( \n");
			sql.append("  select max(line_id) from t_bank_transfer \n");
			sql.append("  where (exported ='N' or exported is null) and transfer_type ='CS' ) ) \n");
			sql.append("where payment_method ='CS' \n");
			sql.append("and receipt_id in(select receipt_id from t_receipt \n");
			sql.append("       where doc_status='SV' and exported ='N' OR exported is null) \n");
			
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			i = ps.executeUpdate();
			return i;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps= null;
			}
			
		}
	}
}
