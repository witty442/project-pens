package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ShipmentLine;
import com.isecinc.pens.process.SequenceProcess;

public class MShipmentLine extends I_Model<MShipmentLine> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "t_shipment_line";
	public static String COLUMN_ID = "SHIPMENT_LINE_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID ,"SHIPMENT_ID","TOTAL_AMOUNT","VAT_AMOUNT","LINES_AMOUNT"
								,"QTY","UOM_ID","ORDER_LINE_ID","PRODUCT_ID","DESCRIPTION"
								,"CREATED_BY","UPDATED_BY"};
	
	public boolean save(ShipmentLine line, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (line.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = line.getId();
		}

		Object[] values = { id, line.getShipmentId(),line.getTotalAmt(),line.getVatAmt(),line.getLinesAmt()
							,line.getQty(),line.getUomId(),line.getOrderLineId(),line.getProductId(),line.getDescription()
							,activeUserID,activeUserID
		};
		
		if (super.save(TABLE_NAME, columns, values, line.getId(), conn)) {
			line.setId(id);
		}
		return true;
	}

}
