package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.GenCNBean;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.ProductBean;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.sun.org.apache.xpath.internal.FoundIndex;

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
		double totalQty = 0;
		boolean foundError = false;
		try {
			sql.append("\n SELECT s.invoice_date ");
			sql.append("\n  ,(select g.customer_code from PENSBI.XXPENS_BI_MST_CUSTOMER g ");
			sql.append("\n   where g.customer_id = s.customer_id)as customer_code  ");
			sql.append("\n  ,(select g.customer_desc from PENSBI.XXPENS_BI_MST_CUSTOMER g ");
			sql.append("\n   where g.customer_id = s.customer_id)as customer_desc ");
			sql.append("\n  ,s.invoice_no,i.segment1 as pens_item,i.inventory_item_id ");
			sql.append("\n  ,NVL(sum(s.returned_qty),0) as qty ");
			sql.append("\n  FROM XXPENS_BI_SALES_ANALYSIS S,xxpens_om_item_mst_v i ");
			sql.append("\n  WHERE 1=1 ");
			sql.append("\n  and S.inventory_item_id = i.inventory_item_id ");
			sql.append("\n  and S.INVOICE_NO ='"+o.getCnNo()+"' ");
			sql.append("\n  group by s.invoice_date,s.customer_id,s.invoice_date,s.invoice_no,i.segment1,i.inventory_item_id ");
            sql.append("\n  ORDER BY i.segment1 asc");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new GenCNBean();
			   h.setLineId(no);
			   h.setInvoiceDate(DateUtil.stringValue(rst.getDate("invoice_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setCnNo(Utils.isNull(rst.getString("invoice_no")));
			   h.setStoreCode(Utils.isNull(rst.getString("customer_code")));
			   h.setStoreName(Utils.isNull(rst.getString("customer_desc")));
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setInventoryItemId(Utils.isNull(rst.getString("inventory_item_id")));
			   h.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_5_digit));
 
			   //Get Item FROM BMELOCKED
			   Barcode b = searchProductByPensItemInBMELocked(conn, h.getPensItem());
			   if(b !=null){
				   h.setBarcode(b.getBarcode());
				   h.setMaterialMaster(b.getMaterialMaster());
				   h.setGroupCode(Utils.isNull(b.getGroupCode()));
				   h.setWholePriceBF(b.getWholePriceBF());
				   h.setRetailPriceBF(b.getRetailPriceBF());
				   b.setMaterialMaster(b.getMaterialMaster());
			   }
			   //Validate GroupCode is null foundError = true
			   if(Utils.isNull(h.getGroupCode()).equals("")){
				   foundError = true;
			   }
			   items.add(h);
			   
			   totalQty += Utils.convertStrToDouble(h.getQty());
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
			o.setFoundError(foundError);
			
			if(items != null && items.size() >0){
				if(foundError==false){
			      o.setCanEdit(true);
				}
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
	public static Barcode searchProductByPensItemInBMELocked(Connection conn,String pensItem) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode b = null;
		try {
			sql.append("\n  select BARCODE ,MATERIAL_MASTER,GROUP_ITEM ,PENS_ITEM,WHOLE_PRICE_BF,RETAIL_PRICE_BF ");
			sql.append("\n  from pensbi.PENSBME_ONHAND_BME_LOCKED M   ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and M.pens_item ='"+pensItem+"'");
            sql.append("\n  order by BARCODE desc");
         
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				b = new Barcode();
				b.setBarcode(rst.getString("barcode"));
				b.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
				b.setGroupCode(rst.getString("group_item"));
				b.setPensItem(Utils.isNull(rst.getString("PENS_ITEM")));
				b.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
				b.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			}//if

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	return b;
}
	 public static Barcode searchProductByPensItemModelByStep_BK(Connection conn,String pensItem) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder step1 = new StringBuilder();
			StringBuilder step2 = new StringBuilder();
			Barcode b = null;
			try {
				//SELECT pens_desc2  GROUP_CODE FROM  PENSBI.PENSBME_MST_REFERENCE where reference_code = 'LotusItem' and pens_desc3 = :PENS Item
				//Step 1
				step1.append("\n  select pens_desc2 as GROUP_CODE ");
				step1.append("\n  from PENSBME_MST_REFERENCE M  ");
				step1.append("\n  where  reference_code = 'LotusItem'");
				step1.append("\n  and pens_desc3 ='"+pensItem+"'");
	         
				logger.debug("step1:"+step1);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(step1.toString());
				
				if (rst.next()) {
					b = new Barcode();
					b.setGroupCode(rst.getString("group_code"));
					b.setPensItem(pensItem);
				}else{
					//step 2
					step2.append("\n  select pens_desc2 as GROUP_CODE ");
					step2.append("\n  from PENSBME_MST_REFERENCE M  ");
					step2.append("\n  where  reference_code = 'LotusItem'");
					step2.append("\n  and pens_value ='"+pensItem+"'");
					logger.debug("step2:"+step2);
					
					stmt = conn.createStatement();
					rst = stmt.executeQuery(step2.toString());
					if (rst.next()) {
						b = new Barcode();
						b.setGroupCode(rst.getString("group_code"));
						b.setPensItem(pensItem);
					}
				}

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				
				} catch (Exception e) {}
			}
			return b;
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
