package com.isecinc.pens.model;

import java.sql.Connection;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.TaxInvoiceLine;
import com.isecinc.pens.process.SequenceProcess;

public class MTaxInvoiceLine extends I_Model<TaxInvoiceLine> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "t_taxinvoice_line";
	public static String COLUMN_ID = "TAXINVOICE_LINE_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID ,"TAXINVOICE_ID","TOTAL_AMOUNT","VAT_AMOUNT","LINES_AMOUNT"
								,"QTY","UOM_ID","PRODUCT_ID","PRICE","ORDER_LINE_ID","DESCRIPTION"
								,"CREATED_BY","UPDATED_BY"};
	
	public boolean save(TaxInvoiceLine line, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (line.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = line.getId();
		}

		Object[] values = { id, line.getTaxInvoiceId() , 
							line.getTotalAmt() , line.getVatAmt() , line.getLinesAmt() ,line.getQty() ,
							line.getUomId() , line.getProductId() , line.getPrice() , line.getOrderLineId() , line.getDescription() ,
							activeUserID,activeUserID};
		
		if (super.save(TABLE_NAME, columns, values, line.getId(), conn)) {
			line.setId(id);
		}
		return true;
	}

}
