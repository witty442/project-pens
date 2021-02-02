package com.isecinc.pens.interim.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;

import com.isecinc.pens.interim.bean.IOrderToReceipt;
import com.isecinc.pens.interim.bean.MOrderToReceipt;
import com.isecinc.pens.model.MReceipt;
import com.pens.util.UploadXLSUtil;

public class AutoCreateReceiptProcess {
	public static Logger logger = Logger.getLogger("PENS");

	private static int sheetNo = 0;

	public List<String> getInvoiceListFromExcel(FormFile dataFile)
			throws Exception {
		logger.debug("Get Invoice List From Excel");
		List<List> columnL = new ArrayList<List>();
		List<String> invoiceNoL = new ArrayList<String>();

		UploadXLSUtil reader = new UploadXLSUtil();
		columnL = reader.loadXls(dataFile, sheetNo, 1, -1, 1, 0);

		for (List columns : columnL) {
			String invoiceNo = columns.get(0).toString();
			if (invoiceNo.toString().indexOf(".") > 0)
				invoiceNo = invoiceNo.substring(0, invoiceNo.toString()
						.indexOf("."));

			invoiceNoL.add(invoiceNo);
			logger.debug("Invoice No. " + invoiceNo);
		}

		return invoiceNoL;
	}

	public int loadInvoiceNoListToTable(List<String> invoiceL, Connection conn,
			String sessionId) throws Exception {
		logger.debug("Load Invoice To Table [Total Invoice =>"
				+ invoiceL.size() + " , Session No." + sessionId + "] ");
		
		// Remove First 
		MOrderToReceipt.deleteBySessionId(conn, sessionId);
		
		int ret = 0;
		String sql = "INSERT INTO I_ORDER_TO_RECEIPT(SESSION_ID,AR_INVOICE_No,LOAD_DATE,CREATED_DATE)VALUES(?,?,CURRENT_DATE,CURRENT_DATE)";

		PreparedStatement ppstmt = conn.prepareStatement(sql);
		for (String invoiceNo : invoiceL) {
			ppstmt.setString(1, sessionId);
			ppstmt.setString(2, invoiceNo);
			ppstmt.executeUpdate();
			ret++;
		}

		return ret;
	}

	public String createReceipt(Connection conn, String sessionId) throws Exception {
		logger.debug("Start Create Receipt ");
		updateData(conn, sessionId);
		
		String whereClause = "AND SESSION_ID = ? ";
		Object[] params = {sessionId};

		List<IOrderToReceipt> orderToReceiptL = MOrderToReceipt.getOrderToReceiptList(conn, whereClause,params);
		
		int success = 0;
		int error = 0;
		
		MReceipt mReceipt = new MReceipt();
		for(IOrderToReceipt orderToReceipt : orderToReceiptL){
			String updateColumn = "RECEIPT_NO";
			try{
				String receiptNo = mReceipt.autoCreateReceipt(orderToReceipt, conn);
				//Update Receive No
				orderToReceipt.setReceiptNo(receiptNo);
				success ++;
			}
			catch(Exception ex){
				orderToReceipt.setErrMsg(ex.toString());
				updateColumn = "ERROR_MESSAGE";
				error ++;
			}

			int ret = MOrderToReceipt.update(conn,updateColumn ,orderToReceipt);
		}
		
		String sql = "UPDATE T_RECEIPT SET EXPORTED = 'Y' , INTERFACES = 'Y' WHERE RECEIPT_NO IN (SELECT RECEIPT_NO FROM I_ORDER_TO_RECEIPT WHERE SESSION_ID = '"+sessionId+"' AND RECEIPT_NO IS NOT NULL )";
		conn.setAutoCommit(true);
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		
		sql = "UPDATE T_ORDER SET PAYMENT = 'Y' WHERE ORDER_ID IN (SELECT ORDER_ID FROM I_ORDER_TO_RECEIPT WHERE SESSION_ID = '"+sessionId+"' AND ORDER_ID IS NOT NULL)";
		stmt.executeUpdate(sql);
		
		sql = "UPDATE T_ORDER_LINE SET PAYMENT = 'Y' WHERE ORDER_ID IN (SELECT ORDER_ID FROM I_ORDER_TO_RECEIPT WHERE SESSION_ID = '"+sessionId+"' AND ORDER_ID IS NOT NULL)";
		stmt.executeUpdate(sql);
		
		return "Receipt Generated Success["+success+"] ERROR["+error+"]";
	}

	private void updateData(Connection conn, String sessionId)  throws Exception{
		logger.debug("Update Data ");
		StringBuffer sql = new StringBuffer(
				"UPDATE I_ORDER_TO_RECEIPT , T_ORDER \n");
		sql.append(
				"SET I_ORDER_TO_RECEIPT.SALES_ORDER_NO = T_ORDER.SALES_ORDER_NO \n")
				.append(", I_ORDER_TO_RECEIPT.ORDER_ID = T_ORDER.ORDER_ID \n")
				.append(", I_ORDER_TO_RECEIPT.ORDER_TYPE = T_ORDER.ORDER_TYPE \n")
				.append(", I_ORDER_TO_RECEIPT.TOTAL_AMOUNT = T_ORDER.net_Amount \n")
				.append(", I_ORDER_TO_RECEIPT.CUSTOMER_ID = T_ORDER.Customer_ID \n")
				.append(", I_ORDER_TO_RECEIPT.CUSTOMER_NAME = T_ORDER.Customer_NAME \n")
				.append("WHERE I_ORDER_TO_RECEIPT.AR_INVOICE_NO = T_ORDER.AR_INVOICE_NO \n")
				.append("AND SESSION_ID = ? ");
		int ret = 0;
		try{
			
			PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
			ppstmt.setString(1, sessionId);
			ret = ppstmt.executeUpdate();
		}
		catch(Exception ex){
			throw new Exception("Cannot Update Information");
		}
		
		// Checked 
		if(ret <1 )
			throw new Exception("No Rows Was Updated");
	}
}
