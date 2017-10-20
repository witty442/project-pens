package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.NumberToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.SequenceProcess;

/**
 * Receipt Line Model
 * 
 * @author atiz.b
 * @version $Id: MReceiptLine.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MReceiptLine extends I_Model<ReceiptLine> {

	private static final long serialVersionUID = -5576062597577498894L;
	public static String TABLE_NAME = "t_receipt_line";
	public static String COLUMN_ID = "RECEIPT_LINE_ID";

	private String[] columns = { COLUMN_ID, "LINE_NO", "RECEIPT_ID", "AR_INVOICE_NO", "SALES_ORDER_NO",
			"INVOICE_AMOUNT", "CREDIT_AMOUNT", "PAID_AMOUNT", "REMAIN_AMOUNT", "ORDER_ID", "CREATED_BY", "UPDATED_BY",
			"DESCRIPTION", "ORDER_LINE_ID", /** "COMPLETE" **/
	};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ReceiptLine find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ReceiptLine.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public ReceiptLine[] search(String whereCause) throws Exception {
		List<ReceiptLine> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ReceiptLine.class);
		if (pos.size() == 0) return null;
		ReceiptLine[] array = new ReceiptLine[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param receiptLine
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(ReceiptLine line, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (line.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = line.getId();
		}
		int lineId = 0;
		if (line.getOrderLine() != null) {
			lineId = line.getOrderLine().getId();
		}
		Object[] values = { id, line.getLineNo(), line.getReceiptId(), line.getArInvoiceNo(), line.getSalesOrderNo(),
				line.getInvoiceAmount(), line.getCreditAmount(), line.getPaidAmount(), line.getRemainAmount(),
				line.getOrder().getId(), activeUserID, activeUserID,
				ConvertNullUtil.convertToString(line.getDescription()).trim(), lineId == 0 ? null : lineId,
		/** line.getComplete() **/
		};
		if (super.save(TABLE_NAME, columns, values, line.getId(), conn)) {
			line.setId(id);
		}
		return true;
	}

	/**
	 * Lookup
	 * 
	 * @param receiptId
	 * @return
	 */
	public List<ReceiptLine> lookUp(int receiptId) {
		List<ReceiptLine> pos = new ArrayList<ReceiptLine>();
		try {
			String whereCause = " AND RECEIPT_ID = " + receiptId + " ORDER BY LINE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ReceiptLine.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	
	public double calculateCreditAmount(Order order) throws Exception {
		Connection conn = null;
		try{
			conn = new DBCPConnectionProvider().getConnection(conn);
			return calculateCreditAmount(conn, order);
		}catch(Exception e){
			throw e;
		}finally{
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
	/**
	 * Calculate Credit Amount
	 * 
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public double calculateCreditAmount(Connection conn,Order order) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		double creditAmt = NumberToolsUtil.round(order.getNetAmount(), 2, BigDecimal.ROUND_HALF_UP);
		double paidAmt = 0;
		logger.debug("Start creditAmt :"+creditAmt);
		try {
			
			String sql = "\n select SUM(rl.PAID_AMOUNT) as PAID_AMOUNT ";
			sql += "\n  from t_receipt_line rl, t_order o ";
			sql += "\n  where rl.order_id = " + order.getId();
			sql += "\n  and rl.ORDER_ID = o.ORDER_ID ";
			sql += "\n  and o.DOC_STATUS = '" + Order.DOC_SAVE + "' ";
			sql += "\n  and rl.receipt_id in (select receipt_id from " + MReceipt.TABLE_NAME + " where doc_status = '"
					+ Receipt.DOC_SAVE + "' ) ";

			logger.debug("sql:\n"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while (rst.next()) {
				//logger.debug("-Paid_Amount :"+rst.getDouble("PAID_AMOUNT"));
				creditAmt -= rst.getDouble("PAID_AMOUNT");
				paidAmt += rst.getDouble("PAID_AMOUNT");
			}

			order.setCreditAmount(creditAmt );
			order.setPaidAmount(paidAmt);
			order.setRemainAmount(creditAmt - paidAmt);

		} catch (Exception e) {
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

	/**
	 * Delete
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String deleteId, Connection conn) throws Exception {
		return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
	}

	/**
	 * Look UP Outstanding
	 * 
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public List<ReceiptLine> lookUpOutstanding(int customerId) throws Exception {
		List<ReceiptLine> pos = new ArrayList<ReceiptLine>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			References refConfigCreditDateFix = InitialReferences.getReferenesByOne(InitialReferences.CREDIT_DATE_FIX,InitialReferences.CREDIT_DATE_FIX);
			String  creditDateFix = refConfigCreditDateFix!=null?refConfigCreditDateFix.getKey():"";
			String dateCheck = "";
			if( !"".equalsIgnoreCase(creditDateFix)){
				java.util.Date d = Utils.parse(creditDateFix, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateCheck = "str_to_date('"+Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y')" ;
			}
			
			String sql = "\n select order_id,order_no,sales_order_no,ar_invoice_no, round(net_amount,2) as net_amount ";
			sql += "\n from t_order ";
			sql += "\n where 1=1 ";
			sql += "\n  and customer_id = " + customerId;
			sql += "\n  and doc_status = 'SV' ";
			sql += "\n  and order_date > "+dateCheck;
			sql += "\n  order by order_date desc, order_no desc ";

			logger.debug("sql:"+sql);
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			ReceiptLine rl;
			MCreditNote cn = new MCreditNote();
			while (rst.next()) {
				rl = new ReceiptLine();

				rl.setOrder(new MOrder().find(rst.getString("order_id")));
				rl.setInvoiceAmount(rst.getDouble("NET_AMOUNT"));
				
				double cnAmt = cn.getTotalCreditNoteAmt(rst.getString("ar_invoice_no"));
				
				rl.setCreditAmount(calculateCreditAmount(rl.getOrder())+cnAmt);
				
				rl.setPaidAmount(rl.getInvoiceAmount() - rl.getCreditAmount());
				
				if (rl.getCreditAmount() > 0) 
					pos.add(rl);
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
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}
	
	
	public double lookCreditAmt(int customerId) throws Exception {
		Connection conn = null;
		String creditDateFix = "";
		try{
			 //Get CreditDateFix FROM C_REFERENCE
			 References refConfigCreditDateFix = InitialReferences.getReferenesByOne(InitialReferences.CREDIT_DATE_FIX,InitialReferences.CREDIT_DATE_FIX);
			 creditDateFix = refConfigCreditDateFix!=null?refConfigCreditDateFix.getKey():"";
			 logger.debug("creditDateFix:"+creditDateFix);
			 
		     conn = new DBCPConnectionProvider().getConnection(conn);
		     return lookCreditAmt(conn,customerId,creditDateFix);
		}catch(Exception e){
			 throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
	
	public double lookCreditAmt(Connection conn,int customerId ,String creditDateFix) throws Exception {
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
			sql.append("   B.order_id \n");
			sql.append(" , B.NET_AMOUNT \n");
			sql.append(" , B.PAID_AMOUNT \n");
			sql.append(" , B.CN_AMOUNT  \n");
			sql.append(" , (CASE \n");
			sql.append("     WHEN COALESCE(credit_amount_temp ,0) > 0 THEN  \n");
			sql.append("          COALESCE(credit_amount_temp ,0) \n");
			sql.append("     ELSE 0 END ) as credit_amount_temp \n");
			sql.append(" FROM( \n");
			sql.append(" SELECT M.order_id \n");
			sql.append(" ,COALESCE(M.NET_AMOUNT,0) as NET_AMOUNT  \n");
			sql.append(" ,COALESCE(R.PAID_AMOUNT,0) as PAID_AMOUNT \n");
			sql.append(" ,COALESCE(C.CN_AMOUNT,0) as  CN_AMOUNT \n");
			sql.append(" ,COALESCE(  ( COALESCE(M.NET_AMOUNT,0) -  COALESCE(R.PAID_AMOUNT,0) ) + COALESCE(C.CN_AMOUNT,0) ) as credit_amount_temp \n");
			sql.append(" FROM( \n");
				sql.append(" select order_id,ar_invoice_no, COALESCE(sum(round(net_amount,2)),0) as net_amount \n");
				sql.append(" from t_order  \n");
				sql.append(" where 1=1  \n");
				sql.append(" and customer_id =  "+customerId+" \n");
				sql.append(" and doc_status = 'SV'  \n");
				//Edit 02/10/2560 sum 
				if( !Utils.isNull(dateCheck).equals("")){
					sql.append(" and order_date > "+dateCheck+"  \n");
				}
				sql.append(" GROUP BY  order_id,ar_invoice_no \n");
			sql.append(" ) M LEFT OUTER JOIN	 \n");
			sql.append(" ( \n");
				sql.append(" select o.order_id ,o.ar_invoice_no,COALESCE(SUM(rl.PAID_AMOUNT),0) as PAID_AMOUNT   \n");
				sql.append(" from t_receipt_line rl, t_order o  \n");
				sql.append(" where 1=1 \n");
				sql.append(" and o.customer_id = "+customerId+" \n");
				sql.append(" and rl.ORDER_ID = o.ORDER_ID  \n");
				sql.append(" and o.DOC_STATUS = 'SV' \n");
				sql.append(" and rl.receipt_id in (select receipt_id from t_receipt where doc_status = 'SV' ) \n");
				//Edit 02/10/2560 sum 
				if( !Utils.isNull(dateCheck).equals("")){
					sql.append(" and o.order_date > "+dateCheck+"  \n");
				}
				sql.append(" GROUP BY o.order_id,o.ar_invoice_no \n");
			sql.append(" )R ON R.order_id  = M.order_id AND M.ar_invoice_no = R.ar_invoice_no  \n");
			
			sql.append(" LEFT OUTER JOIN \n");
			sql.append(" ( \n");
				sql.append(" select  ar_invoice_no,COALESCE(sum(total_amount),0)  as cn_amount  \n");
				sql.append(" from t_credit_note where 1=1  \n");
				sql.append(" AND ACTIVE = 'Y'  \n");
				sql.append(" AND ar_invoice_no in( \n");
				sql.append("     select ar_invoice_no from t_credit_note where customer_id = "+customerId+" \n");
				sql.append("    ) GROUP BY ar_invoice_no \n");
		   sql.append("  )C   ON M.ar_invoice_no = C.ar_invoice_no \n");
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
	
	public double lookCreditAmtBK1(Connection conn,int customerId) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		double creditAmt  =0;
		double totalcnAmt = 0;
		try {
			String sql = "select order_id,order_no,sales_order_no,ar_invoice_no, round(net_amount,2) as net_amount ";
			sql += "from t_order ";
			sql += "where 1=1 ";
			sql += "  and customer_id = " + customerId;
			sql += "  and doc_status = 'SV' ";
			sql += "  order by order_date desc, order_no desc ";

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			ReceiptLine rl;
			MCreditNote cn = new MCreditNote();
			while (rst.next()) {
				rl = new ReceiptLine();

				rl.setOrder(new MOrder().find(rst.getString("order_id")));
				rl.setInvoiceAmount(rst.getDouble("NET_AMOUNT"));
				
				//Get CN
				double cnAmt = cn.getTotalCreditNoteAmt(rst.getString("ar_invoice_no"));
				//logger.debug("cnAmt:"+cnAmt);
				totalcnAmt += cnAmt;
				//Calc CreditAmount 
				//rl.setCreditAmount(calculateCreditAmount(conn,rl.getOrder())+cnAmt);
				
				//Cale PaidAmount
				//rl.setPaidAmount(rl.getInvoiceAmount() - rl.getCreditAmount());
				
				if (rl.getCreditAmount() > 0) {
					creditAmt += rl.getCreditAmount();
				}
			}
			
			logger.debug("totalCnAmt:"+totalcnAmt);

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
	
	
	/**
	 * 
	 * @param whereClause
	 * @return
	 * @throws Exception
	 */
	public List<ReceiptLine> findByCondition(String whereClause) throws Exception {
		List<ReceiptLine> pos = new ArrayList<ReceiptLine>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String sql = "select * from t_receipt_line ";
			sql += " where 1=1 ";
			sql +=whereClause;

			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			ReceiptLine rl;
			while (rst.next()) {
				rl = new ReceiptLine();
				rl.setId(rst.getInt("receipt_line_id"));
				rl.setReceiptId(rst.getInt("receipt_id"));

				pos.add(rl);
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
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}
}
