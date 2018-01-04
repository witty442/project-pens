package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;
import util.NumberToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;

public class MReceiptSummary {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	public static String amtDebug = "";
	
	public static double lookCreditAmtByCustomerId(int customerId) throws Exception {
		Connection conn = null;
		String creditDateFix = "";
		try{
			 //Get CreditDateFix FROM C_REFERENCE
			 References refConfigCreditDateFix = InitialReferences.getReferenesByOne(InitialReferences.CREDIT_DATE_FIX,InitialReferences.CREDIT_DATE_FIX);
			 creditDateFix = refConfigCreditDateFix!=null?refConfigCreditDateFix.getKey():"";
			 logger.debug("creditDateFix:"+creditDateFix);
			 
		     conn = new DBCPConnectionProvider().getConnection(conn);
		     return lookCreditAmtByCustomerId(conn,customerId,creditDateFix);
		}catch(Exception e){
			 throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
	
	public static double lookCreditAmtByCustomerId(Connection conn,int customerId ,String creditDateFix) throws Exception {
        double totalCreditAmt = 0;
        String orderType  ="CR";
		try{
			User user = new MUser().getActiveUserName();
			orderType = Utils.isNull(user.getOrderType().getKey());
			logger.debug("orderType:"+orderType);
			
			// CrediAmount order and receipt line
			totalCreditAmt = lookUpByOrderAR(conn,customerId ,orderType);
			//sum CN
			totalCreditAmt += lookUpCNForReceipt(conn,customerId); 
			
			//FileUtil.writeFile("d:/temp/temp2.csv", amtDebug);
			
        }catch(Exception e){
        	logger.error(e.getMessage(),e);
        }
		return totalCreditAmt;
	}
	
	private static double lookUpByOrderAR(Connection conn,int customerId, String orderType)
			throws Exception {
		double creditAmt = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		MCreditNote creditNote = new MCreditNote();
		MAdjust adjust = new MAdjust();
		Order order = null;
		double creditAmtTemp = 0;
		double cnAmt = 0;
		double adjustInvoiceAmt = 0;
		try{
			References refConfigCreditDateFix = InitialReferences.getReferenesByOne(InitialReferences.CREDIT_DATE_FIX,InitialReferences.CREDIT_DATE_FIX);
			String  creditDateFix = refConfigCreditDateFix!=null?refConfigCreditDateFix.getKey():"";
			logger.debug("creditDateFix:"+creditDateFix);
			String dateCheck = "";
			if( !"".equalsIgnoreCase(creditDateFix)){
				java.util.Date d = Utils.parse(creditDateFix, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateCheck = "str_to_date('"+Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y')" ;
			}
		
			String whereCause = " select order_id, round(net_amount,2) as net_amount,ar_invoice_no from t_order where 1=1 \n";
			whereCause += "  and ar_invoice_no is not null  \n";
			whereCause += "  and ar_invoice_no <> ''  \n";
			whereCause += "  and order_type = '" + orderType + "'  \n";
			whereCause += "  and customer_id = " + customerId +" \n";
			whereCause += "  and doc_status = 'SV'  \n";
			/** Wit Edit 02/10/2017 Case Show Order_date< config date **/
			whereCause += "  and order_date > "+dateCheck +" \n";
			
			//Test
			//whereCause += "  and ar_invoice_no = '21600101966'  \n";
			whereCause += "  order by Ar_invoice_no asc  \n";
			
			logger.debug("sql lookUpByOrderAR \n "+whereCause);
			
			ps = conn.prepareStatement(whereCause);
			rs = ps.executeQuery();
			
			while(rs.next()){
				order = new Order();
				order.setId(rs.getInt("order_id"));
				order.setNetAmount(rs.getDouble("net_amount"));
				order.setArInvoiceNo(rs.getString("ar_invoice_no"));

				cnAmt = creditNote.getTotalCreditNoteAmt(conn,order.getArInvoiceNo());
				adjustInvoiceAmt = adjust.getTotalAdjustAmtInvoice(conn,order.getArInvoiceNo());  
				
				logger.debug("netAmt:"+order.getNetAmount());
				logger.debug("cnAmt:"+cnAmt);
				logger.debug("adjustInvoiceAmt:"+adjustInvoiceAmt);
				
				creditAmtTemp  = new MReceiptLine().calculateCreditAmount(conn,order)+cnAmt+adjustInvoiceAmt;
				creditAmtTemp = NumberToolsUtil.round(creditAmtTemp,2,BigDecimal.ROUND_HALF_UP);

				//debug
			
				if(creditAmtTemp != 0.01 && creditAmtTemp != 0 && creditAmtTemp != -0.01){
					logger.debug("creditAmtTemp:"+creditAmtTemp);
				    amtDebug += "INVOICE,"+rs.getString("ar_invoice_no")+","+order.getOpenAmt()+"\n";
				}
				
				creditAmt += creditAmtTemp;
			}
			//logger.debug("Invoice CreditAmt:"+creditAmt);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ps.close();
			rs.close();
		}
		return creditAmt;
    }
	
	private static double lookUpCNForReceipt(Connection conn,int customerId)  {
		double creditAmt = 0;
		double creditAmtTemp = 0;
		String whereCause = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		CreditNote cn = null;
		try {
			whereCause += "\n  SELECT  CREDIT_NOTE_ID,CREDIT_NOTE_NO ,DOCUMENT_DATE";
			whereCause += "\n  ,AR_INVOICE_NO , ACTIVE";
			whereCause += "\n  , (  COALESCE(c.TOTAL_AMOUNT,0) ";
			//New code cn = cn +adjust 
			whereCause += "\n     + COALESCE((SELECT sum(adjust_amount) from t_adjust ad ";
			whereCause += "\n                 where ad.ar_invoice_no = c.credit_note_no),0) ";
			whereCause += "\n    ) as TOTAL_AMOUNT ";
			whereCause += "\n  FROM T_CREDIT_NOTE c WHERE 1=1";
			whereCause += "\n  AND ACTIVE = 'Y' ";
			whereCause += "\n  AND DOC_STATUS = 'SV' ";
			whereCause += "\n  AND CREDIT_NOTE_ID NOT IN(SELECT CREDIT_NOTE_ID FROM t_receipt_cn rcn, t_receipt rc ";
			whereCause += "\n  WHERE rc.receipt_id = rcn.receipt_id AND rc.Doc_Status = 'SV') ";
			whereCause += "\n AND (AR_Invoice_No Is Null OR TRIM(AR_INVOICE_NO) = '' ) "; 
			whereCause += "\n  AND CUSTOMER_ID ="+customerId+" ";
		    
			//debug
			//whereCause += "\n AND ar_invoice_no = '21600101966'  \n";
			
			whereCause += "\n ORDER BY CREDIT_NOTE_NO ";
			
			logger.debug("whereCause:"+whereCause);
			
			ps = conn.prepareStatement(whereCause);
		    rs = ps.executeQuery();
		    while(rs.next()){
		    	cn = new CreditNote();
		    	cn.setId(rs.getInt("CREDIT_NOTE_ID"));
		    	cn.setTotalAmount(rs.getDouble("total_amount"));
		    	creditAmtTemp = new MReceiptCN().calculateCNCreditAmount(cn);
				
				//logger.debug("creditAmt:"+creditAmtTemp);
				creditAmt += creditAmtTemp;
				
				 logger.debug("CN creditAmtTemp:"+creditAmtTemp);
				 
				//debug
				if(creditAmtTemp != 0  && creditAmtTemp != -0.01)
				amtDebug += "CN,"+rs.getString("CREDIT_NOTE_NO")+","+creditAmtTemp+"\n";
		    }
		    
		   // logger.debug("CN CreditAmt:"+creditAmt);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			try{
				ps.close();
				rs.close();
			}catch(Exception ee){}
		}
		return creditAmt;
	}
	
	@Deprecated
	public static double lookCreditAmtByCustomerId_BK(Connection conn,int customerId ,String creditDateFix) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		double creditAmt  =0;
		String dateCheck = "";
		try {
			if( !"".equalsIgnoreCase(creditDateFix)){
				Date d = Utils.parse(creditDateFix, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateCheck = "str_to_date('"+Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y')" ;
			}
			StringBuffer sql = new StringBuffer("");
			
			sql.append(" SELECT SUM(credit_amount_temp) as credit_amount FROM( \n");
			sql.append(" SELECT  \n");
			sql.append("   B.customer_id \n");
			sql.append(" , B.NET_AMOUNT \n");
			sql.append(" , B.PAID_AMOUNT \n");
			sql.append(" , B.CN_AMOUNT  \n");
			sql.append(" , (CASE \n");
			sql.append("     WHEN COALESCE(credit_amount_temp ,0) > 0 THEN  \n");
			sql.append("          COALESCE(credit_amount_temp ,0) \n");
			sql.append("     ELSE 0 END ) as credit_amount_temp \n");
			sql.append(" FROM( \n");
			sql.append("  SELECT M.customer_id \n");
			sql.append(" ,COALESCE(M.NET_AMOUNT,0) as NET_AMOUNT  \n");
			sql.append(" ,COALESCE(R.PAID_AMOUNT,0) as PAID_AMOUNT \n");
			sql.append(" ,COALESCE(CN.CN_AMOUNT,0) as CN_AMOUNT \n");
			sql.append(" ,COALESCE(adj_inv.ADJUST_INV_AMOUNT,0) as ADJUST_INV_AMOUNT \n");
			sql.append(" ,COALESCE(  "
				     	+ "( COALESCE(M.NET_AMOUNT,0)   "
					    + "  - COALESCE(R.PAID_AMOUNT,0) ) "
					    + "  + COALESCE(CN.CN_AMOUNT,0) "
					    + "  + COALESCE(adj_inv.ADJUST_INV_AMOUNT,0)  "
					    + ") as credit_amount_temp \n");
			sql.append(" FROM( \n");
			sql.append(" 	select customer_id,COALESCE(sum(round(net_amount,2)),0) as net_amount \n");
			sql.append(" 	from t_order  \n");
			sql.append(" 	where customer_id =  "+customerId+" \n");
			sql.append(" 	and doc_status = 'SV'  \n");
			//Edit 02/10/2560 sum 
			if( !Utils.isNull(dateCheck).equals("")){
				sql.append(" 	and order_date > "+dateCheck+"  \n");
			}
			sql.append(" 	GROUP BY customer_id \n");
			sql.append(" ) M LEFT OUTER JOIN	 \n");
			sql.append(" ( \n");
			sql.append(" 	select customer_id,COALESCE(SUM(rl.PAID_AMOUNT),0) as PAID_AMOUNT   \n");
			sql.append(" 	from t_receipt_line rl, t_order o  \n");
			sql.append(" 	where o.customer_id = "+customerId+" \n");
			sql.append(" 	and rl.ORDER_ID = o.ORDER_ID  \n");
			sql.append("	and o.DOC_STATUS = 'SV' \n");
			sql.append(" 	and rl.receipt_id in (select receipt_id from t_receipt where doc_status = 'SV' ) \n");
			//Edit 02/10/2560 sum 
			if( !Utils.isNull(dateCheck).equals("")){
				sql.append(" 	and o.order_date > "+dateCheck+"  \n");
			}
			sql.append(" 	GROUP BY o.customer_id \n");
			sql.append(" )R ON R.customer_id  = M.customer_id \n");
			
			/*sql.append(" ( \n");
			sql.append(" 	select customer_id,COALESCE(SUM(rl.PAID_AMOUNT),0) as PAID_CN_AMOUNT   \n");
			sql.append(" 	from t_receipt_cn rcn ,t_receipt r, t_order o  \n");
			sql.append(" 	where o.customer_id = "+customerId+" \n");
			sql.append(" 	and r.ORDER_ID = o.ORDER_ID  \n");
			sql.append(" 	and rcn.RECEIPT_ID = o.RECEIPT_ID  \n");
			sql.append("	and o.DOC_STATUS = 'SV' \n");
			sql.append(" 	and r.receipt_id in (select receipt_id from t_receipt where doc_status = 'SV' ) \n");
			if( !Utils.isNull(dateCheck).equals("")){
				sql.append(" 	and o.order_date > "+dateCheck+"  \n");
			}
			sql.append(" 	GROUP BY o.customer_id \n");
			sql.append(" )RCN ON RCN.customer_id  = M.customer_id \n");*/
			
			sql.append(" LEFT OUTER JOIN \n");
			sql.append(" ( \n");
			sql.append("   select  customer_id \n");
			sql.append("   ,(  COALESCE(SUM(c.TOTAL_AMOUNT),0)  \n");
			sql.append("     + COALESCE((SELECT SUM(adjust_amount) from t_adjust ad  \n");
			sql.append("                 where ad.ar_invoice_no = c.credit_note_no),0)  \n");
			sql.append("    ) as cn_amount  \n");
			sql.append(" 	from t_credit_note c where ACTIVE = 'Y'  \n");
			sql.append(" 	AND DOC_STATUS = 'SV'  \n");
			sql.append(" 	AND customer_id =  "+customerId+" \n");
			sql.append(" 	GROUP BY customer_id \n");
		    sql.append("  )CN  ON M.customer_id = CN.customer_id \n");
		    
		    sql.append(" LEFT OUTER JOIN \n");
		    sql.append(" ( \n");
		    sql.append(" 	SELECT o.customer_id,COALESCE(sum(adjust_amount),0)  as adjust_inv_amount  \n");
		    sql.append(" 	FROM t_order o, t_adjust a   \n");
		    sql.append(" 	WHERE o.ar_invoice_no = a.ar_invoice_no  \n");
		    sql.append(" 	AND o.doc_status = 'SV'  \n");
		    sql.append(" 	AND o.customer_id =  "+customerId+" \n");
		    sql.append(" 	GROUP BY o.customer_id  \n");
		    sql.append("    )adj_inv ON M.customer_id = adj_inv.customer_id  \n");
			sql.append(" )B	 \n");
			sql.append(")A	 \n");
			
			logger.debug("sql:"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				
			   creditAmt = rst.getDouble("credit_amount");
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}

		}
		return creditAmt;
	}
	
}
