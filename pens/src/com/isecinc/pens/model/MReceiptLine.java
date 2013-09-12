package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.NumberToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptLine;
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
		try {
			
			String sql = "select SUM(rl.PAID_AMOUNT) as PAID_AMOUNT ";
			sql += "from t_receipt_line rl, t_order o ";
			sql += "where rl.order_id = " + order.getId();
			sql += "  and rl.ORDER_ID = o.ORDER_ID ";
			sql += "  and o.DOC_STATUS = '" + Order.DOC_SAVE + "' ";
			sql += "  and rl.receipt_id in (select receipt_id from " + MReceipt.TABLE_NAME + " where doc_status = '"
					+ Receipt.DOC_SAVE + "' ) ";

			logger.debug("sql:\n"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while (rst.next()) {
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
			String sql = "select order_id,order_no,sales_order_no,ar_invoice_no, round(net_amount,2) as net_amount ";
			sql += "from t_order ";
			sql += "where 1=1 ";
			sql += "  and customer_id = " + customerId;
			sql += "  and doc_status = 'SV' ";
			sql += "  order by order_date desc, order_no desc ";

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
		try{
		   conn = new DBCPConnectionProvider().getConnection(conn);
		   return lookCreditAmt(conn,customerId);
		}catch(Exception e){
			 throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
	
	public double lookCreditAmt(Connection conn,int customerId) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		double creditAmt  =0;
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
				
				double cnAmt = cn.getTotalCreditNoteAmt(rst.getString("ar_invoice_no"));
				
				rl.setCreditAmount(calculateCreditAmount(conn,rl.getOrder())+cnAmt);
				
				rl.setPaidAmount(rl.getInvoiceAmount() - rl.getCreditAmount());
				
				if (rl.getCreditAmount() > 0) {
					creditAmt += rl.getCreditAmount();
				}
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
