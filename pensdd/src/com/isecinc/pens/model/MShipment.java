package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Shipment;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.document.ShipmentDocumentProcess;

public class MShipment extends I_Model<MShipment>{

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "t_shipment";
	public static String COLUMN_ID = "SHIPMENT_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID ,"SHIPMENT_NO","SHIPMENT_DATE"
								,"TOTAL_AMOUNT","VAT_AMOUNT","LINES_AMOUNT","SHIPMENT_STATUS"
								,"ORDER_ID","TAXINVOICE_ID","DESCRIPTION"
								,"CREATED_BY","UPDATED_BY"};
	
	public boolean save(Shipment shipment, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (shipment.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			
			if(shipment.getTaxInvoiceId() <= 0){
				String prefix = "1";
				if (ConvertNullUtil.convertToString(shipment.getShipmentNo()).trim().length() == 0){
					
					Date shippingDate = Utils.parse(shipment.getShipmentDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
					
					shipment.setShipmentNo(prefix+new ShipmentDocumentProcess().getNextDocumentNo( shippingDate,
									activeUserID,shipment.getShipmentDateYear(),shipment.getShipmentDateMonth(),conn));
				}
			}
		} else {
			id = shipment.getId();
		}

		Object[] values = { id,shipment.getShipmentNo(),DateToolsUtil.convertToTimeStamp(shipment.getShipmentDate())
							,shipment.getTotalAmt(),shipment.getVatAmt(),shipment.getLineAmt()
							,shipment.getShipmentStats(),shipment.getOrderId(),(shipment.getTaxInvoiceId() == 0?null:shipment.getTaxInvoiceId())
							,shipment.getDescription(),activeUserID,activeUserID
		};
		
		if (super.save(TABLE_NAME, columns, values, shipment.getId(), conn)) {
			shipment.setId(id);
		}
		return true;
	}
	
	
	public Shipment findShipmentByOrderLineId(int lineId,Connection conn) throws Exception{
		StringBuffer sql = new StringBuffer("SELECT sh.* FROM t_shipment sh , t_shipment_line sl WHERE sh.shipment_id = sl.shipment_id and sl.order_line_id = ? ");
		
		PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
		ppstmt.setInt(1,lineId);
		
		ResultSet rset = ppstmt.executeQuery();
		
		Shipment shipment = null;
		if(rset.next()){
			shipment =  new Shipment();
			
			shipment.setId(rset.getInt("shipment_id"));
			shipment.setLineAmt(rset.getDouble("lines_amount"));
			shipment.setTotalAmt(rset.getDouble("total_amount"));
			shipment.setVatAmt(rset.getDouble("vat_amount"));
			shipment.setShipmentNo(rset.getString("shipment_no"));
			shipment.setDescription(rset.getString("description"));
			shipment.setOrderId(rset.getInt("order_id"));
			shipment.setShipmentDate(DateToolsUtil.convertToString(rset.getTimestamp("shipment_date")));
			shipment.setShipmentStats(rset.getString("shipment_status"));
			shipment.setTaxInvoiceId(rset.getInt("taxinvoice_id"));
		}
		
		return shipment;
	}
	
	public static void main(String[] args){
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			InitialReferences init = new InitialReferences();
			init.init(conn);
			Shipment shipment = new Shipment();
			shipment.setLineAmt(200d);
			shipment.setVatAmt(14d);
			shipment.setTotalAmt(214d);
			shipment.setShipmentDate(util.DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
			shipment.setOrderId(3072);
			shipment.setShipmentStats("SV");
			
			MShipment m_shipment = new MShipment();
			m_shipment.save(shipment, 100000072, conn);
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {}
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
	}
}
