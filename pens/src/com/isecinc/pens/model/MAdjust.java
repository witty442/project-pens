package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Adjust;
import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.inf.helper.DBConnection;

/**
 * Credit Note Model
 * 
 * @author atiz.b
 * @version $Id: MCreditNote.java,v 1.0 15/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MAdjust extends I_Model<Adjust> {

	private static final long serialVersionUID = -4202991323191680273L;
	public static String TABLE_NAME = "t_adjust";
	public static String COLUMN_ID = "ADJUST_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Adjust find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Adjust.class);
	}
	public double getTotalAdjustAmtInvoice(String arInvoiceNo){
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return getTotalAdjustAmtInvoiceModel(conn, arInvoiceNo);
	}
	
	public double getTotalAdjustAmtInvoice(Connection conn,String arInvoiceNo){
		return getTotalAdjustAmtInvoiceModel(conn, arInvoiceNo);
	}
	
	public double getTotalAdjustAmtInvoiceModel(Connection conn,String arInvoiceNo){
		double totalAdjustAmt = 0;
		String whereCause = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			whereCause += "\n  SELECT COALESCE(SUM(ADJUST_AMOUNT),0) as ADJUST_AMOUNT ";
			whereCause += "\n  FROM T_ADJUST c WHERE 1=1";
			whereCause += "\n AND AR_INVOICE_NO ='"+arInvoiceNo+"'";
			//logger.debug("whereCause:"+whereCause);
			ps = conn.prepareStatement(whereCause);
		    rs =ps.executeQuery();
		    
		    if(rs.next()){
		    	totalAdjustAmt = rs.getDouble("ADJUST_AMOUNT");
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			try{
				ps.close();
				rs.close();
			}catch(Exception ee){}
		}
		return totalAdjustAmt;
	}
}
