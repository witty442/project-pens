package com.isecinc.pens.inf.manager.process.imports;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptFunction.ReceiptFunctionBean;
import com.isecinc.pens.model.MCreditNote;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.web.receipt.ReceiptForm;

public class ImportReceiptHelper {

	protected static  Logger logger = Logger.getLogger("PENS");
	
	public static boolean isReceiptCancelAll(Connection conn,String receiptNo,String filName) throws Exception{
		PreparedStatement  ps = null;
		ResultSet rs =null;
		StringBuffer sql = new StringBuffer();
		boolean cancelAll = true;
		try{
			sql.append("\n select count(*) as c from t_temp_import_trans");
			sql.append("\n where file_name ='"+filName+"'");
			sql.append("\n and  receipt_no ='"+receiptNo+"'");
			sql.append("\n and  line_str like 'H%'");
			sql.append("\n and  doc_status ='SV' ");
			
			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
			  if(rs.getInt("c") >0){
				  cancelAll = false;
			  }
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
		return cancelAll;
	}
	
	public static void recalcReceiptHead(Map<String, String> receiptNoMap) {
		Connection conn = null;
		logger.debug("#start recalcReceiptHead# ");
		try{
			if( !receiptNoMap.isEmpty()){
			  conn = DBConnection.getInstance().getConnection();
			  Iterator its = receiptNoMap.keySet().iterator();
	  		  while(its.hasNext()){
	  			  String  receiptNo =(String)its.next();
	  			  logger.debug("ReCalc Sum(paid_amount) receipt_line to apply_amount in t_receipt ReceiptNo["+receiptNo+"]");
	  			  if( !Utils.isNull(receiptNo).equals("")){
	  				  
	    		      // Re-Calculate ReceiptLine amount and update to t_receipt
	    		      int receiptId = new MReceipt().getReceiptId(conn, receiptNo);
		    		  Receipt receiptUpdate = reCalculateApplyAmount(conn,receiptId);
	    		    	
		    		  updateApplyAmountInReceiptHead(conn,receiptUpdate);
		    		  
	  			   }//if
	  		  }//for
			}//if
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		  try{
			 if(conn != null){
			    conn.close();
			 }
		  }catch(Exception ee){}
		}
	}
	
	public  static int updatePaidAmountRecepitLine(Connection conn,ReceiptFunctionBean o) throws Exception{
	     PreparedStatement ps =null;
	     int updateInt = 0;
		 try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" update t_receipt_line  \n");
			sql.append(" set paid_amount = \n");
			
			sql.append(" (select  COALESCE(sum(paid_amount),0) \n");
			sql.append("   from t_receipt_match  \n");
			sql.append("   where receipt_id = "+o.getReceiptId()+" \n");
			sql.append("   and receipt_line_id = "+o.getReceiptLineId()+" )\n");
			
            sql.append(", remain_amount = \n");
			sql.append("  credit_amount - (select  COALESCE(sum(paid_amount),0) \n");
			sql.append("   from t_receipt_match  \n");
			sql.append("   where receipt_id = "+o.getReceiptId()+" \n");
			sql.append("   and receipt_line_id = "+o.getReceiptLineId()+" )\n");
			
			sql.append(" where receipt_id = "+o.getReceiptId()+" \n");
			sql.append(" and receipt_line_id = "+o.getReceiptLineId()+" \n");
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			updateInt = ps.executeUpdate();	
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
		}
		return updateInt;
	}
	
	public  static int updateApplyAmountInReceiptHead(Connection conn,Receipt o) throws Exception{
	     PreparedStatement ps =null;
	     int updateInt = 0;
		 try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" update t_receipt set apply_amount ="+o.getApplyAmount());
			sql.append(" where receipt_id = "+o.getId()+" \n");
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			updateInt = ps.executeUpdate();	
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
		}
		return updateInt;
	}
	
	public static Receipt reCalculateApplyAmount(Connection conn,int receiptId) throws Exception{
		try{
			Receipt[] receipts = new MReceipt().search(conn," and receipt_id ="+receiptId+" \n");
			if(receipts != null && receipts.length >0){
				Receipt receiptUpdate = receipts[0];
			    List<ReceiptLine> receiptLines = new MReceiptLine().lookUp(conn,receiptUpdate.getId());
			    List<CreditNote> cnLines = new MCreditNote().lookUpByReceiptId(conn,receiptUpdate.getId());
			    
			    //recalculate Head Amount
			    return reCalculateApplyAmount(receiptUpdate,receiptLines,cnLines);
			}
			return null;
	    }catch(Exception e){
	    	throw e;
	    }
	}
	
	public static Receipt reCalculateApplyAmount(Receipt receipt, List<ReceiptLine> receiptLines, List<CreditNote> cnLines) {
		BigDecimal paidAmount = new BigDecimal("0");
		BigDecimal totalApplyAmount = new BigDecimal("0");
		try{
			//Sum ReceiptLine 
			for (ReceiptLine l : receiptLines) {
				paidAmount = new BigDecimal(l.getPaidAmount()).setScale(2,BigDecimal.ROUND_HALF_UP);
				totalApplyAmount = totalApplyAmount.add(paidAmount );
			}
			
			//Sum subtract cn(negative)
			for (CreditNote l : cnLines) {
				paidAmount = new BigDecimal(l.getTotalAmount()).setScale(2,BigDecimal.ROUND_HALF_UP);
				totalApplyAmount = totalApplyAmount.add(paidAmount );
			}
			
			totalApplyAmount = totalApplyAmount.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			receipt.setApplyAmount(totalApplyAmount.doubleValue());
			
		}catch(Exception e){
		   logger.debug(e.getMessage(),e);
		}finally{

		}
		return receipt;
	}

}
