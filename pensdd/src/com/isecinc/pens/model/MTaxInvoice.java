package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.TaxInvoice;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.document.DocumentSequenceProcess;
import com.isecinc.pens.process.document.ReceiptDocumentProcess;
import com.isecinc.pens.process.document.ShipmentDocumentProcess;
import com.isecinc.pens.process.document.TaxInvoiceDocumentProcess;

public class MTaxInvoice extends I_Model<TaxInvoice> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "t_taxinvoice";
	public static String COLUMN_ID = "TAXINVOICE_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID ,"TAXINVOICE_NO","TAXINVOICE_DATE"
								,"TOTAL_AMOUNT","VAT_AMOUNT","LINES_AMOUNT"
								,"TAXINVOICE_STATUS","ORDER_ID","DESCRIPTION"
								,"CREATED_BY","UPDATED_BY","TRIP_NO"};
	
	public boolean save(TaxInvoice invoice , boolean isShipment, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (invoice.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			
			String prefix = "1";
			if (ConvertNullUtil.convertToString(invoice.getTaxInvoiceNo()).trim().length() == 0){
				String taxInvoiceNo = "";
				Date shippingDate = Utils.parse(invoice.getTaxInvoiceDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
				
				if(isShipment)
					taxInvoiceNo =  "2"+new TaxInvoiceDocumentProcess().getNextDocumentNo(shippingDate,activeUserID,DocumentSequenceProcess.SHIPMENTTAXINVOICE_NUMNER, conn,invoice.getTaxInvoiceDateYear(),invoice.getTaxInvoiceDateMonth());
				else
					taxInvoiceNo =  "4"+new TaxInvoiceDocumentProcess().getNextDocumentNo(shippingDate,activeUserID,DocumentSequenceProcess.RECEIPTTAXINVOICE_NUMNER, conn,invoice.getTaxInvoiceDateYear(),invoice.getTaxInvoiceDateMonth());
				
				invoice.setTaxInvoiceNo(taxInvoiceNo);
			}
		} else {
			id = invoice.getId();
		}

		Object[] values = { id,invoice.getTaxInvoiceNo(),DateToolsUtil.convertToTimeStamp(invoice.getTaxInvoiceDate())
						  , invoice.getTotalAmt() , invoice.getVatAmt() , invoice.getLinesAmt()
						  , invoice.getTaxInvoiceStatus() , invoice.getOrderId() , invoice.getDescription()
						  , activeUserID,activeUserID,invoice.getTripNo()
		};
		
		if (super.save(TABLE_NAME, columns, values, invoice.getId(), conn)) {
			invoice.setId(id);
		}
		return true;
	}
	
	public TaxInvoice getTaxInvoice(int taxInvoiceId , Connection conn) throws Exception {
		return getTaxInvoice(conn,taxInvoiceId,0,0);
	}
	
	public TaxInvoice getTaxInvoice(Connection conn,int orderId,int tripNo) throws Exception {
		return getTaxInvoice(conn,0,orderId,tripNo);
	}
	
	public TaxInvoice getTaxInvoice(Connection conn,int taxInvoiceId ,int orderId,int tripNo) throws Exception {
		TaxInvoice taxinvoice = null;
		PreparedStatement ppstmt = null;
		ResultSet rset = null;
		try{
			String sql = "SELECT * FROM t_taxinvoice WHERE  taxinvoice_status = 'SV'";
			if(taxInvoiceId != 0){
				sql +=" and taxinvoice_id ="+taxInvoiceId;
			}
			if(orderId != 0){
				sql +=" and order_id ="+orderId;
			}
			if(tripNo != 0){
				sql +=" and trip_no ="+tripNo;
			}
			logger.debug("sql:"+sql);
			
			ppstmt = conn.prepareStatement(sql);
			
			rset = ppstmt.executeQuery();
			if(rset.next()){
				taxinvoice = new TaxInvoice();
				taxinvoice.setId(rset.getInt("taxinvoice_id"));
				taxinvoice.setLinesAmt(rset.getDouble("lines_amount"));
				taxinvoice.setTotalAmt(rset.getDouble("total_amount"));
				taxinvoice.setVatAmt(rset.getDouble("vat_amount"));
				taxinvoice.setTaxInvoiceNo(rset.getString("taxinvoice_no"));
				taxinvoice.setDescription(rset.getString("description"));
				taxinvoice.setOrderId(rset.getInt("order_id"));
				taxinvoice.setTripNo(rset.getInt("trip_no"));
				taxinvoice.setTaxInvoiceDate(DateToolsUtil.convertToString(rset.getTimestamp("taxinvoice_date")));
				taxinvoice.setTaxInvoiceStatus(rset.getString("taxinvoice_status"));
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ppstmt != null){
				ppstmt.close();ppstmt=null;
			}
			if(rset != null){
				rset.close();rset=null;
			}
		}
		return taxinvoice;
	}
	
	public List<TaxInvoice> getTaxInvoiceIsNotGenReceipt(Connection conn,int orderId) throws Exception {
		TaxInvoice taxinvoice = null;
		PreparedStatement ppstmt = null;
		ResultSet rset = null;
		List<TaxInvoice> taxInvoiceNotGenreceiptList = new ArrayList<TaxInvoice>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT * from t_taxinvoice where order_id ="+orderId+" \n");
			sql.append(" and taxinvoice_id not in( select taxinvoice_id from t_receipt t ,t_receipt_line l  where t.receipt_id = l.receipt_id and order_id ="+orderId+") \n");
			logger.debug("sql:"+sql);
			
			ppstmt = conn.prepareStatement(sql.toString());
			
			rset = ppstmt.executeQuery();
			while(rset.next()){
				taxinvoice = new TaxInvoice();
				taxinvoice.setId(rset.getInt("taxinvoice_id"));
				taxinvoice.setLinesAmt(rset.getDouble("lines_amount"));
				taxinvoice.setTotalAmt(rset.getDouble("total_amount"));
				taxinvoice.setVatAmt(rset.getDouble("vat_amount"));
				taxinvoice.setTaxInvoiceNo(rset.getString("taxinvoice_no"));
				taxinvoice.setDescription(rset.getString("description"));
				taxinvoice.setOrderId(rset.getInt("order_id"));
				taxinvoice.setTripNo(rset.getInt("trip_no"));
				taxinvoice.setTaxInvoiceDate(DateToolsUtil.convertToString(rset.getTimestamp("taxinvoice_date")));
				taxinvoice.setTaxInvoiceStatus(rset.getString("taxinvoice_status"));
				
				taxInvoiceNotGenreceiptList.add(taxinvoice);
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ppstmt != null){
				ppstmt.close();ppstmt=null;
			}
			if(rset != null){
				rset.close();rset=null;
			}
		}
		return taxInvoiceNotGenreceiptList;
	}
	
	public List<References> getTaxPeriods() throws Exception {
		List<References> referenceL = new ArrayList<References>();
		
		Connection conn = null;
		try{
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			String sql = "select distinct DATE_FORMAT(th.taxinvoice_date,'%Y%m') as code, month(th.taxinvoice_date) as month, year(th.taxinvoice_date) as year  from t_taxinvoice th ";
			PreparedStatement ppstmt = conn.prepareStatement(sql);
			
			ResultSet rset = ppstmt.executeQuery();
			while(rset.next()){
				String code = rset.getString("code");
				int month = rset.getInt("month")-1;
				int year = rset.getInt("year")+543;
				String monthTh = DateToolsUtil.getMonthOfNum(month);
				References reference = new References(code, code, monthTh+" "+year);
				referenceL.add(reference);
			}
		}
		catch(Exception ex){
			conn.close();
			throw ex;
		}
		finally{
			conn.close();
		}

		return referenceL;
	}
}
