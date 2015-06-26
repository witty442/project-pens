package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class GenCNDAO extends PickConstants{
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	
	public static GenCNBean searchHead(GenCNBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		GenCNBean h = null;
		List<GenCNBean> items = new ArrayList<GenCNBean>();
		int no=1;
		int totalQty = 0;
		try {
			sql.append("\n SELECT s.invoice_date, ");
			sql.append("\n  (select g.customer_code from PENSBI.XXPENS_BI_MST_CUSTOMER g ");
			sql.append("\n   where g.customer_id = s.customer_id)as customer_code,  ");
			sql.append("\n  (select g.customer_desc from PENSBI.XXPENS_BI_MST_CUSTOMER g ");
			sql.append("\n   where g.customer_id = s.customer_id)as customer_desc, ");
			sql.append("\n  s.invoice_no, ");
			sql.append("\n  i.inventory_item_code as pens_item, ");
			sql.append("\n  NVL(sum(s.returned_qty),0) as qty ");
			sql.append("\n  FROM XXPENS_BI_SALES_ANALYSIS S,XXPENS_BI_MST_ITEM i ");
			sql.append("\n  WHERE 1=1 ");
			sql.append("\n  and S.inventory_item_id = i.inventory_item_id ");
			sql.append("\n  and S.INVOICE_NO ='"+o.getCnNo()+"' ");
			sql.append("\n  group by s.invoice_date,s.customer_id,s.invoice_date,s.invoice_no,i.inventory_item_code");
            sql.append("\n  ORDER BY i.inventory_item_code asc");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new GenCNBean();
			   h.setLineId(no);
			   h.setInvoiceDate(Utils.stringValue(rst.getDate("invoice_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setCnNo(Utils.isNull(rst.getString("invoice_no")));
			   h.setStoreCode(Utils.isNull(rst.getString("customer_code")));
			   h.setStoreName(Utils.isNull(rst.getString("customer_desc")));
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setQty(Utils.isNull(rst.getString("qty")));
 
			   //Get Item 
			   Barcode b = GeneralDAO.searchProductByPensItemModelBMELocked(conn, h.getPensItem());
			   if(b !=null){
				   h.setBarcode(b.getBarcode());
				   h.setMaterialMaster(b.getMaterialMaster());
				   h.setGroupCode(Utils.isNull(b.getGroupCode()));
				   h.setWholePriceBF(b.getWholePriceBF());
				   h.setRetailPriceBF(b.getRetailPriceBF());
			   }
			   items.add(h);
			   
			   totalQty += Utils.convertStrToInt(h.getQty());
			   no++;
			}//while
			logger.debug("totalQty:"+totalQty);
            
			if(h!=null){
			  o.setStoreCode(h.getStoreCode());
			  o.setStoreName(h.getStoreName());
			  o.setInvoiceDate(h.getInvoiceDate());
			}
			o.setTotalQty(Utils.convertToNumberStr(totalQty));
			o.setItems(items);
			
			if(items != null && items.size() >0){
			  o.setCanEdit(true);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	
	public static boolean isLoaded(GenCNBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		boolean isLoaded = false;
	
		try {
			sql.append("\n SELECT count(*) as c FROM PENSBME_PICK_BARCODE ");
			sql.append("\n WHERE REMARK ='"+o.getCnNo()+"' ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			  if(rst.getInt("c") >0){
				  isLoaded = true;
			  }
			  
			}//while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return isLoaded;
	}
	
}
