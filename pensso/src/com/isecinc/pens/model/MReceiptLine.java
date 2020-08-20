package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.NumberToolsUtil;
import com.pens.util.seq.SequenceProcess;

/**
 * Receipt Line Model
 * 
 * @author atiz.b
 * @version $Id: MReceiptLine.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MReceiptLine extends I_Model<ReceiptLine> {

	private static final long serialVersionUID = -5576062597577498894L;
	public static String TABLE_NAME = "pensso.t_receipt_line";
	public static String COLUMN_ID = "RECEIPT_LINE_ID";

	private String[] columns = { COLUMN_ID, "LINE_NO", "RECEIPT_ID", "AR_INVOICE_NO", "SALES_ORDER_NO",
			"INVOICE_AMOUNT", "CREDIT_AMOUNT", "PAID_AMOUNT", "REMAIN_AMOUNT", "INVOICE_ID", "CREATED_BY", "UPDATED_BY",
			"DESCRIPTION"
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
		long id = 0;
		if (line.getId() ==0) {
			id = SequenceProcess.getNextValue("t_receipt_line").longValue();
		} else {
			id = line.getId();
		}
		
		Object[] values = { id, line.getLineNo(), line.getReceiptId(), line.getArInvoiceNo(), line.getSalesOrderNo(),
				line.getInvoiceAmount(), line.getCreditAmount(), line.getPaidAmount(), line.getRemainAmount(),
				line.getOrder().getInvoiceId(), activeUserID, activeUserID,
				ConvertNullUtil.convertToString(line.getDescription()).trim(),
		/** line.getComplete() **/
		};
		if (super.save(TABLE_NAME, columns, values, line.getId(), conn)) {
			line.setId(id);
		}
		return true;
	}

	public boolean saveCaseImportReceipt(ReceiptLine line, int activeUserID, Connection conn) throws Exception {
		 String[] columnsSaveImport = { COLUMN_ID, "LINE_NO", "RECEIPT_ID", "AR_INVOICE_NO", "SALES_ORDER_NO",
				"INVOICE_AMOUNT", "CREDIT_AMOUNT", "PAID_AMOUNT", "REMAIN_AMOUNT", "INVOICE_ID", "CREATED_BY", "UPDATED_BY",
				"DESCRIPTION", "IMPORT_TRANS_ID"};
		 long id =0;
		if (line.getId() ==0) {
			id = SequenceProcess.getNextValue(TABLE_NAME).longValue();
		} else {
			id = line.getId();
		}
		
		Object[] values = { id, line.getLineNo(), line.getReceiptId(), line.getArInvoiceNo(), line.getSalesOrderNo(),
				line.getInvoiceAmount(), line.getCreditAmount(), line.getPaidAmount(), line.getRemainAmount(),
				line.getOrder().getId(), activeUserID, activeUserID,
				ConvertNullUtil.convertToString(line.getDescription()).trim(),
		        line.getImportTransId()
		};
		if (super.save(TABLE_NAME, columnsSaveImport, values, line.getId(), conn)) {
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
	public List<ReceiptLine> lookUp(long receiptId) {
		List<ReceiptLine> pos = new ArrayList<ReceiptLine>();
		try {
			String whereCause = "\n AND RECEIPT_ID = " + receiptId + "";
			whereCause += "\n  ORDER BY LINE_NO ";
			
			//logger.debug("whereCause:"+whereCause);
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ReceiptLine.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
	public List<ReceiptLine> lookUp(Connection conn,long receiptId) {
		List<ReceiptLine> pos = new ArrayList<ReceiptLine>();
		try {
			String whereCause = "\n AND RECEIPT_ID = " + receiptId + "";
			whereCause += "\n  ORDER BY LINE_NO ";
			
			//logger.debug("whereCause:"+whereCause);
			pos = super.search(conn,TABLE_NAME, COLUMN_ID, whereCause, ReceiptLine.class);
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
		//logger.debug("Start creditAmt :"+creditAmt+", ar_invoice_no:"+order.getArInvoiceNo());
		try {
			String sql = "\n select COALESCE(SUM(rl.PAID_AMOUNT),0) as PAID_AMOUNT ";
			sql += "\n  from pensso.t_receipt_line rl, pensso.t_invoice o ";
			
			sql += "\n  where rl.invoice_id = " + order.getInvoiceId();
			sql += "\n  and rl.INVOICE_ID = o.INVOICE_ID ";
			sql += "\n  and rl.receipt_id in ("
					+ "   select receipt_id "
					+ "   from " + MReceipt.TABLE_NAME 
					+"    where doc_status = '"+Receipt.DOC_SAVE + "' ) ";

			logger.debug("sql:\n"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while (rst.next()) {
				//logger.debug("-Paid_Amount :"+rst.getDouble("PAID_AMOUNT"));
				creditAmt -= (rst.getDouble("PAID_AMOUNT"));
				paidAmt += (rst.getDouble("PAID_AMOUNT"));
			}
			order.setCreditAmount(creditAmt);
			order.setPaidAmount(paidAmt);
			order.setRemainAmount(creditAmt - paidAmt);

			logger.debug("paidAmount :"+order.getPaidAmount());
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

	public List<ReceiptLine> lookUpOutstanding(int customerId,User user) throws Exception {
		List<ReceiptLine> receiptLine  = new ArrayList<ReceiptLine>();
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			
			//List from Order
			receiptLine = lookUpByOrderAR(conn,customerId, user);
			
			//List from CN
			receiptLine.addAll(lookUpCNForReceipt(conn,customerId, user));
			
			return receiptLine;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			conn.close();
		}
	}
	
	public List<ReceiptLine> lookUpCNForReceipt(Connection conn,int customerId,User user) throws Exception {
		List<ReceiptLine> receiptLine  = new ArrayList<ReceiptLine>();
		ReceiptLine rl;
		try{
			List<CreditNote> cns = new ArrayList<CreditNote>(); 
			cns = new MCreditNote().lookUpForReceipt(conn,user.getId(),"",customerId); 
			for(CreditNote cn : cns){
				cn.setCreditAmount(new MReceiptCN().calculateCNCreditAmount(conn,cn));
				rl = new ReceiptLine();
				rl.setArInvoiceNo(cn.getArInvoiceNo());
				rl.setInvoiceAmount(0);
				rl.setCreditAmount(cn.getCreditAmount());
				rl.setPaidAmount(0);
				Order order = new Order();
				order.setArInvoiceNo(cn.getCreditNoteNo());
				rl.setOrder(order);
				if(rl.getCreditAmount() != 0 && rl.getCreditAmount() != 0.01 && rl.getCreditAmount() != -0.01){
				   receiptLine.add(rl);
				}
			}
			return receiptLine;
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
	/**
	 * Look UP Outstanding
	 * 
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public List<ReceiptLine> lookUpByOrderAR(Connection conn,int customerId,User user) throws Exception {
		List<ReceiptLine> pos = new ArrayList<ReceiptLine>();
		Statement stmt = null;
		ResultSet rst = null;
		ReceiptLine rl;
		MCreditNote cnDAO = new MCreditNote();
		MAdjust adjustDAO = new MAdjust();
		double cnAmt = 0;
		double adjustInvoiceAmt = 0;
		try {
			References refConfigCreditDateFix = InitialReferences.getReferenesByOne(InitialReferences.CREDIT_DATE_FIX,InitialReferences.CREDIT_DATE_FIX);
			String  creditDateFix = refConfigCreditDateFix!=null?refConfigCreditDateFix.getKey():"";
			String dateCheck = "";
			if( !"".equalsIgnoreCase(creditDateFix)){
				java.util.Date d = Utils.parse(creditDateFix, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateCheck = "to_date('"+Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy')" ;
			}
			
			String sql = "\n select order_id,order_no,sales_order_no,ar_invoice_no, round(net_amount,2) as net_amount ";
			sql += "\n from t_order ";
			sql += "\n where 1=1 ";
			sql += "\n  and customer_id = " + customerId;
			sql += "\n  and ar_invoice_no is not null  ";
			sql += "\n  and ar_invoice_no <> ''  ";
			sql += "\n  and order_type = '" + user.getOrderType().getKey() + "'  ";
			sql += "\n  and doc_status = 'SV' ";
			sql += "\n  and order_date > "+dateCheck;
			//test
			//sql += "\n and ar_invoice_no ='21600302007'";
			
			sql += "\n  order by order_date desc, order_no desc ";
			//Debug
			//sql += "\n  order by ar_invoice_no ";
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			
			while (rst.next()) {
				rl = new ReceiptLine();

				rl.setOrder(new MOrder().find(conn,rst.getString("order_id")));
				rl.setInvoiceAmount(rst.getDouble("NET_AMOUNT"));
				
				//CN = CN+adjustCN (t_credit_note.credit_note_no = t_adjust.ar_invoice_no)
				cnAmt = cnDAO.getTotalCreditNoteAmt(conn,rst.getString("ar_invoice_no"));

				//AdjustInvoice (t_adjust.ar_invoice_no = t_order.ar_invoice_no)
				adjustInvoiceAmt = adjustDAO.getTotalAdjustAmtInvoice(conn,rst.getString("ar_invoice_no"));
				
				/******************(sum(receipt_line))*****************+(cn)***+(adjust)****/
				rl.setCreditAmount(calculateCreditAmount(rl.getOrder())+cnAmt+adjustInvoiceAmt);
				rl.setCreditAmount(NumberToolsUtil.round(rl.getCreditAmount(),2,BigDecimal.ROUND_HALF_UP));
				
				rl.setPaidAmount(rl.getInvoiceAmount() - rl.getCreditAmount());
				
				if (rl.getCreditAmount() != 0.01 && rl.getCreditAmount() != 0  && rl.getCreditAmount() != -0.01) 
					pos.add(rl);
			}
			return pos;
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
				rl.setId(rst.getLong("receipt_line_id"));
				rl.setReceiptId(rst.getLong("receipt_id"));

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
