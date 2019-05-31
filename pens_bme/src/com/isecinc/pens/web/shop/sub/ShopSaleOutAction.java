package com.isecinc.pens.web.shop.sub;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.shop.ShopBean;
import com.isecinc.pens.web.shop.ShopForm;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class ShopSaleOutAction {
 private static Logger logger = Logger.getLogger("PENS");
	
 public static ShopForm search(HttpServletRequest request, ShopForm f,User user) throws Exception{
	   Statement stmt = null;
		ResultSet rst = null;
		List<ShopBean> pos = new ArrayList<ShopBean>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		double totalQty = 0;
		double totalLineAmount = 0;
		double totalDiscount = 0;
		double totalVatAmount = 0;
		double totalAmountInVat = 0;
		double totalAmountExVat = 0;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			sql = genSQL(conn,f);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				ShopBean item = new ShopBean();
				item.setOrderDate(Utils.stringValue(rst.getDate("order_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				item.setOrderNo(Utils.isNull(rst.getString("ORDER_NUMBER")));
				item.setPensItem(Utils.isNull(rst.getString("pens_item")));
				item.setBarcode(rst.getString("barcode"));
				item.setStyle(rst.getString("material_master"));
				item.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_no_disgit));
				item.setUnitPrice(Utils.decimalFormat(rst.getDouble("price"),Utils.format_current_2_disgit));
				item.setLineAmount(Utils.decimalFormat(rst.getDouble("line_amount"),Utils.format_current_2_disgit));
				item.setDiscount(Utils.decimalFormat(rst.getDouble("discount"),Utils.format_current_2_disgit));
				item.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
				//TotalAmount (incVat)
				item.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
				item.setTotalAmountExVat(Utils.decimalFormat(rst.getDouble("total_amount_ex_vat"),Utils.format_current_2_disgit));
				
				if("S".equalsIgnoreCase(Utils.isNull(rst.getString("promotion")))){
				  item.setFreeItem("YES");
				}else{
				  item.setFreeItem("");
				}
				pos.add(item);
				
				//summary
				totalQty +=rst.getDouble("qty");
				totalLineAmount += rst.getDouble("line_amount");
				totalDiscount += rst.getDouble("discount");
				totalVatAmount += rst.getDouble("vat_amount");
				totalAmountInVat += rst.getDouble("total_amount");
				totalAmountExVat += rst.getDouble("total_amount_ex_vat");
			}//while
		
			if(pos != null && pos.size() >0){
				f.setResults(pos);
				
				//add summary
				ShopBean summary = new ShopBean();
				summary.setQty(Utils.decimalFormat(totalQty,Utils.format_current_no_disgit));
				summary.setLineAmount(Utils.decimalFormat(totalLineAmount,Utils.format_current_2_disgit));
				summary.setDiscount(Utils.decimalFormat(totalDiscount,Utils.format_current_2_disgit));
				summary.setVatAmount(Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit));
				summary.setTotalAmount(Utils.decimalFormat(totalAmountInVat,Utils.format_current_2_disgit));
				summary.setTotalAmountExVat(Utils.decimalFormat(totalAmountExVat,Utils.format_current_2_disgit));
				
				request.getSession().setAttribute("summary" ,summary);
			}else{
				f.setResults(null);
				request.getSession().setAttribute("summary" ,null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return f;
    }
 
 public static StringBuffer exportToExcel(HttpServletRequest request, ShopForm form,User user,List<ShopBean> list){
		StringBuffer h = new StringBuffer("");
		String colspan ="13";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan ="11";
			}
			//Header
			h.append("<table border='1'> \n");
			h.append(" <tr> \n");
			if (Utils.isNull(form.getBean().getCustGroup()).equals(PickConstants.STORE_TYPE_PENSHOP_CODE)){
			  h.append("  <td align='left' colspan='"+colspan+"'>รายงานรายละเอียดการขาย ที่ MAYA Shop</td> \n");
			}else if (Utils.isNull(form.getBean().getCustGroup()).equals(PickConstants.STORE_TYPE_TERMINAL_CODE)){
			  h.append("  <td align='left' colspan='"+colspan+"'>รายงานรายละเอียดการขาย ที่ Terminal Shop</td> \n");
			}
			h.append(" </tr> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+form.getBean().getStartDate()+"-"+form.getBean().getEndDate()+"</td> \n");
			h.append(" </tr> \n");
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>Sales Date</th> \n");
				  h.append("<th>Order No</th> \n");
				  h.append("<th>Pens Item</th> \n");
				  h.append("<th>Barcode</th> \n");
				  h.append("<th>Style</th> \n");
				  h.append("<th>Qty </th> \n");
				  h.append("<th>Free Item</th> \n");
				  h.append("<th>Unit Price</th> \n");
				  h.append("<th>Line Amount</th> \n");
				  h.append("<th>Discount</th> \n");
				  h.append("<th>Vat Amount</th> \n");
				  h.append("<th>Total Line Amount(In. Vat)</th> \n");
				  h.append("<th>Total Line Amount(Ex. Vat)</th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					ShopBean s = (ShopBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getOrderDate()+"</td> \n");
					  h.append("<td class='text'>"+s.getOrderNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStyle()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getQty()+"</td> \n");
					  h.append("<td class='text'>"+s.getFreeItem()+"</td> \n");
					  h.append("<td class='currency'>"+s.getUnitPrice()+"</td> \n");
					  h.append("<td class='currency'>"+s.getLineAmount()+"</td> \n");
					  h.append("<td class='currency'>"+s.getDiscount()+"</td> \n");
					  h.append("<td class='currency'>"+s.getVatAmount()+"</td> \n");
					  h.append("<td class='currency'>"+s.getTotalAmount()+"</td> \n");
					  h.append("<td class='currency'>"+s.getTotalAmountExVat()+"</td> \n");
					h.append("</tr>");
				}
				
				/** Summary **/
				ShopBean s = (ShopBean)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
					h.append("<td>&nbsp;</td> \n");
					h.append("<td>&nbsp;</td> \n");
					h.append("<td>&nbsp;</td> \n");
					h.append("<td>&nbsp;</td> \n");
					h.append("<td>&nbsp;<b>รวม</b></td> \n");
					h.append("<td class='num_currency_bold'>"+s.getQty()+"</td> \n");
					h.append("<td>&nbsp;</td> \n");
					h.append("<td>&nbsp;</td> \n");
					h.append("<td class='currency_bold'>"+s.getLineAmount()+"</td> \n");
					h.append("<td class='currency_bold'>"+s.getDiscount()+"</td> \n");
					h.append("<td class='currency_bold'>"+s.getVatAmount()+"</td> \n");
					h.append("<td class='currency_bold'>"+s.getTotalAmount()+"</td> \n");
					h.append("<td class='currency_bold'>"+s.getTotalAmountExVat()+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
 
 public static StringBuilder genSQL(Connection conn,ShopForm f) throws Exception{
		StringBuilder sql = new StringBuilder();
		Date dateTemp = null;
		String dateStr ="";
		try {
			sql.append("\n SELECT M.ORDER_DATE ,M.ORDER_NUMBER");
			sql.append("\n ,MP.PENS_ITEM,MP.MATERIAL_MASTER,MP.BARCODE");
			sql.append("\n ,D.PROMOTION ,D.PRICE ");
			sql.append("\n ,SUM(D.ORDERED_QUANTITY) AS QTY");
			sql.append("\n ,SUM(D.LINE_AMOUNT) AS LINE_AMOUNT");
			sql.append("\n ,SUM(D.DISCOUNT) AS DISCOUNT");
			sql.append("\n ,SUM(D.VAT_AMOUNT) AS VAT_AMOUNT");
			sql.append("\n ,SUM(D.TOTAL_AMOUNT) AS TOTAL_AMOUNT");
			sql.append("\n ,NVL(SUM(D.TOTAL_AMOUNT)/1.07,0)  AS TOTAL_AMOUNT_EX_VAT ");
			sql.append("\n FROM XXPENS_OM_SHOP_ORDER_MST M,XXPENS_OM_SHOP_ORDER_DT D ");
			sql.append("\n ,(" );
			sql.append("\n   SELECT DISTINCT I.inventory_item_id as product_id");
			sql.append("\n   ,MP.PENS_VALUE as PENS_ITEM ");
			sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
			sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
			sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ,XXPENS_OM_ITEM_MST_V I");
			sql.append("\n   WHERE reference_code in('"+Constants.STORE_TYPE_7CATALOG_ITEM+"','"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
			sql.append("\n   AND MP.pens_desc6 in ('MAYA' , 'TM21') ");
			sql.append("\n   AND MP.pens_value =I.segment1 ");
			sql.append("\n ) MP ");
			sql.append("\n WHERE M.ORDER_NUMBER = D.ORDER_NUMBER ");
			sql.append("\n AND D.product_id = MP.product_id ");
			sql.append("\n AND M.DOC_STATUS ='SV' ");
			sql.append("\n AND M.CUSTOMER_NUMBER ='"+f.getBean().getCustGroup()+"' ");
			
			if( !Utils.isNull(f.getBean().getStartDate()).equals("") && !Utils.isNull(f.getBean().getEndDate()).equals("") ){
				dateTemp = Utils.parse(f.getBean().getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateStr = Utils.stringValue(dateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n AND ORDER_DATE >= to_date('"+dateStr+"','dd/mm/yyyy')");
				
				dateTemp = Utils.parse(f.getBean().getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateStr = Utils.stringValue(dateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n AND ORDER_DATE <= to_date('"+dateStr+"','dd/mm/yyyy')");
				
			}else if( !Utils.isNull(f.getBean().getStartDate()).equals("") && Utils.isNull(f.getBean().getEndDate()).equals("") ){
				dateTemp = Utils.parse(f.getBean().getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				dateStr = Utils.stringValue(dateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n AND ORDER_DATE = to_date('"+dateStr+"','dd/mm/yyyy')");
			}
			sql.append("\n GROUP BY M.ORDER_DATE ,M.ORDER_NUMBER");
			sql.append("\n ,MP.PENS_ITEM,MP.MATERIAL_MASTER,MP.BARCODE");
			sql.append("\n ,D.PROMOTION ,D.PRICE ");
			
			sql.append("\n ORDER BY M.ORDER_DATE ,M.ORDER_NUMBER ASC");
			
			logger.debug("sql:"+sql);
		} catch (Exception e) {
			throw e;
		} finally {
		
		}
		return sql;
	}

	
}
