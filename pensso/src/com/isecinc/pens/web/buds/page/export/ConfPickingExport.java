package com.isecinc.pens.web.buds.page.export;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MProduct;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class ConfPickingExport {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	
	public static ConfPickingBean searchSalesReport(ConfPickingBean o,boolean excel,User user){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer html = null;
		ConfPickingBean item = null;
		ConfPickingBean itemNext = null;
		int r = 0;
		int countOrderBySale = 0;
		double totalAmount = 0;
		double totalVatAmount = 0;
		double totalNetAmount = 0;
		List<ConfPickingBean> itemsList = new ArrayList<ConfPickingBean>();
		try{
			//create connection
			conn = DBConnectionApps.getInstance().getConnection();
			sql = genSQLSalesReport(o);
	
			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString(),"TIS-620");
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				item = new ConfPickingBean();
				item.setPickingNo(Utils.isNull(rst.getString("picking_no")));
				item.setOrderNo(Utils.isNull(rst.getString("order_no")));
				item.setOrderDate(DateUtil.stringValue(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				item.setPoNumber(Utils.isNull(rst.getString("po_number")));
				item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
				item.setCustomerName(Utils.isNull(rst.getString("customer_name")));
				item.setAlternateName(Utils.isNull(rst.getString("alternate_name")));
                item.setMobile(Utils.isNull(rst.getString("telephone")));
                item.setAmphur(Utils.isNull(rst.getString("province"))+"/"+Utils.isNull(rst.getString("amphur")));
                
                item.setTotalAmountD(rst.getDouble("total_amount"));
                item.setVatAmountD(rst.getDouble("vat_amount"));
                item.setNetAmountD(rst.getDouble("net_amount"));
				itemsList.add(item)	;
			}//while
			
			 //gen Head Table
			 html = new StringBuffer("");
			 html.append(ExcelHeader.EXCEL_HEADER);
			 html.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			 html.append("<tr><td colspan='10'><b> Sales Order Summary</b></td> </tr>\n");
			 html.append("</table>\n");
			
			 html.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='table table-condensed table-striped'> \n");
			 html.append("<thead><tr> \n");
			 html.append("<th>Picking List No.</th> \n");
			 html.append("<th>Order No</th> \n");
			 html.append("<th>Order Date</th> \n");
			 html.append("<th>Customer PO</th> \n");
			 html.append("<th>Sales Code</th> \n");
			 html.append("<th>Customer No</th> \n");
			 html.append("<th>Customer Name</th> \n");
			 html.append("<th>Reference Cust No.</th> \n");
			 html.append("<th>Contact Info.</th> \n");
			 html.append("<th>อำเภอ / จังหวัด</th> \n");
			 html.append("</tr></thead> \n");
			 html.append("<tbody> \n");
			//display 
			 for(int i=0;i<itemsList.size();i++){
				 itemNext = null;
				 r++;
				 item = itemsList.get(i);
				 if(i!= itemsList.size()-1){
					itemNext = itemsList.get(i+1);
				 }
				
			    countOrderBySale++;
			    totalAmount += item.getTotalAmountD();
			    totalVatAmount += item.getVatAmountD();
			    totalNetAmount += item.getNetAmountD();
	
			  //Gen Row Table
				html.append("<tr class=''> \n");
				html.append("<td class='text' width='6%'>"+item.getPickingNo()+"</td> \n");
				html.append("<td class='text' width='6%'>"+item.getOrderNo()+"</td> \n");
				html.append("<td class='text' width='5%'>"+item.getOrderDate()+"</td> \n");
				html.append("<td class='text' width='7%'>"+item.getPoNumber()+"</td> \n");//
				html.append("<td class='text' width='3%'>"+item.getSalesrepCode()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getCustomerCode()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getCustomerName()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getAlternateName()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getMobile()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getAmphur()+"</td> \n");
				html.append("</tr> \n");
				//gen summary by SalesCode
				if(itemNext== null ||
					(itemNext != null && !item.getSalesrepCode().equalsIgnoreCase(itemNext.getSalesrepCode())) ){
					html.append("<tr class='colum_head'> \n");
					html.append("<td class='text' colspan='6' align='right'>Total By Sales :"+Utils.isNull(item.getSalesrepCode())+"</td> \n");
					html.append("<td class='text' align='center'>"+countOrderBySale+" ใบ </td> \n");
					html.append("<td class='text' align='center'>"+Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit)+"</td>\n");
					html.append("<td class='text' align='center'>"+Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit)+" </td>\n");
					html.append("<td class='text' align='center'>"+Utils.decimalFormat(totalNetAmount,Utils.format_current_2_disgit)+"</td> \n");
					html.append("</tr> \n");
					
					//reset by Sales
					totalAmount = 0;
					totalVatAmount =0;
					totalNetAmount = 0;
				}
			 }//for
			 
			//Check Execute Found data
			if(r>0){
			   html.append("<tbody> \n");
			   html.append("</table>");
			}

		   o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	private static StringBuffer genSQLSalesReport(ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n  SELECT M.*");
		sql.append("\n  FROM (");
		sql.append("\n    /** SALES_APP **/ ");
		sql.append("\n    SELECT t.picking_no, t.ORDER_NO ,t.ORDER_DATE,t.po_number ");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = t.user_id) as salesrep_code");
		sql.append("\n    ,a.alternate_name ,a.telephone");
		sql.append("\n    ,t.total_amount ,t.vat_amount,t.NET_AMOUNT");
		sql.append("\n    FROM pensso.t_order t ");
		sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
		sql.append("\n    WHERE t.customer_id = c.customer_id ");
		sql.append("\n    AND c.customer_id = a.customer_id ");
		sql.append("\n    AND t.ship_address_id = a.address_id ");
		sql.append("\n    AND a.purpose = 'S' ");
		sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
		
		sql.append("\n    UNION ALL ");
		
		sql.append("\n    /** EDI **/ ");
		sql.append("\n    SELECT t.picking_no, t.CUST_PO_NUMBER as ORDER_NO ,t.ORDERED_DATE as ORDER_DATE,t.CUST_PO_NUMBER as po_number");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from apps.xxpens_salesreps_v ad where ad.salesrep_id = t.salesrep_id) as salesrep_code");
		sql.append("\n    ,a.alternate_name ,a.telephone");
		sql.append("\n    ,sum(l.LINE_AMOUNT) as total_amount ,SUM((l.LINE_AMOUNT *0.07)) as vat_amount,SUM((l.LINE_AMOUNT+(l.LINE_AMOUNT *0.07))) as NET_AMOUNT");
		sql.append("\n    FROM pensso.T_EDI t ,pensso.T_EDI_LINE l");
		sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
		sql.append("\n    WHERE t.HEADER_ID = l.HEADER_ID ");
		sql.append("\n    AND t.customer_id = c.customer_id ");
		sql.append("\n    AND c.customer_id = a.customer_id ");
		sql.append("\n    AND t.ship_to_site_use_id = a.address_id ");
		sql.append("\n    AND a.purpose = 'S' ");
		sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
		sql.append("\n    GROUP BY t.picking_no, t.CUST_PO_NUMBER ,t.ORDERED_DATE  ");
		sql.append("\n    ,c.code ,c.name ,a.district_id,a.province_id");
		sql.append("\n    ,t.salesrep_id ,a.alternate_name ,a.telephone");
		sql.append("\n )M ");
		sql.append("\n where 1=1");
		sql.append("\n and M.PICKING_NO ='"+o.getPickingNo()+"'");
		sql.append("\n ORDER BY M.salesrep_code,M.customer_number");
		
		return sql;
	}
	public static ConfPickingBean searchSalesDetailReport(ConfPickingBean o,boolean excel,User user){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer html = new StringBuffer("");
		ConfPickingBean item = null;
		ConfPickingBean itemNext = null;
		int r = 0;
		double totalQty = 0,totalQty2 = 0;
		int countOrderByOrder = 0;
		double totalAmount = 0;
		double totalVatAmount = 0;
		double totalNetAmount = 0;
		List<ConfPickingBean> itemsList = new ArrayList<ConfPickingBean>();
		Product p = null;
		String orderNoTemp = "";
		Map<String, ConfPickingBean> productMap = new HashMap<String, ConfPickingBean>();
		try{
			//create connection
			conn = DBConnectionApps.getInstance().getConnection();
			
			//Gen Header
			sql = genSQLSalesDetailHeadReport(o);
			logger.debug("sql:"+sql);
			
			//gen Head Table
			 
			 html.append(ExcelHeader.EXCEL_HEADER);
			 html.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1'> \n");
			 html.append(" <tr><td colspan='6'><b>Summary Sales Order(สรุปยอดใบสั่งขาย)</b></td> </tr>\n");
			 html.append("<tr> \n");
			 html.append("  <td class='colum_head_hilight'>Picking List No.</td><td colspan='5' class='text'>"+o.getPickingNo()+"</td> \n");
			 html.append("</tr> \n");
			 html.append("<tr> \n");
			 html.append(" <td class='colum_head_hilight' rowspan='2' align='center'>SKU</td> \n");
			 html.append(" <td class='colum_head_hilight' rowspan='2' align='center'>Description</td> \n");
			 html.append(" <td class='colum_head_hilight' colspan='2' align='center'>หน่วย</td> \n");
			 html.append(" <td class='colum_head_hilight' colspan='2' align='center'>จำนวน</td> \n");
			 html.append("</tr> \n");
			 html.append("<tr> \n");
			 html.append(" <td class='colum_head_hilight' align='center'>เต็ม</td> \n");
			 html.append(" <td class='colum_head_hilight' align='center'>เศษ</td> \n");
			 html.append(" <td class='colum_head_hilight' align='center'>เต็ม</td> \n");
			 html.append(" <td class='colum_head_hilight' align='center'>เศษ</td> \n");
			 html.append("</tr> \n");
			 stmt = conn.createStatement();
			 rst = stmt.executeQuery(sql.toString());
			 while (rst.next()) {
				 p =new Product();
				 p.setCode(Utils.isNull(rst.getString("product_code")));
				 p = MProduct.getProductInfo(conn, p);
				 
				 item = new ConfPickingBean();
				 item.setProductCode(Utils.isNull(rst.getString("product_code")));
				 item.setProductName(Utils.isNull(rst.getString("product_name")));
				 item.setUom1(p.getUom1());
				 item.setUom2(p.getUom2());
				 
				 if(Utils.isNull(p.getUom1()).equalsIgnoreCase(Utils.isNull(rst.getString("uom_id")))){
				    item.setQty1(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_number_no_disgit));
				 }else{
					item.setQty1("");
				 }
				 if(Utils.isNull(p.getUom2()).equalsIgnoreCase(Utils.isNull(rst.getString("uom_id")))){
					 item.setQty2(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_number_no_disgit));
				 }else{
					 item.setQty2("");
				 }
				 if(productMap.get(item.getProductCode()) !=null){
					 ConfPickingBean oldItem = productMap.get(item.getProductCode());
					 if( Utils.isNull(oldItem.getQty1()).equals("")){
						 oldItem.setQty1(item.getQty1());
					 }
					 if( Utils.isNull(oldItem.getQty2()).equals("")){
						 oldItem.setQty2(item.getQty2());
					 }
				     productMap.put(item.getProductCode(), oldItem);
				 }else{
					 productMap.put(item.getProductCode(), item);
				 }
			 }
			 
			 //convert Map to List
			 List<ConfPickingBean> headList = new ArrayList(productMap.values());
			 Collections.sort(headList, ConfPickingBean.Comparators.PRODUCT_CODE_ASC);
			 
			 for(int i=0;i<headList.size();i++){
				 item = headList.get(i);
				 html.append("<tr> \n");
				 html.append(" <td class='text_center' align='center'>"+item.getProductCode()+"</td> \n");
				 html.append(" <td class='text'>"+item.getProductName()+"</td> \n");
				 html.append(" <td class='text_center'  align='center'>"+item.getUom1()+"</td> \n");
				 html.append(" <td class='text_center' align='center'>"+item.getUom2()+"</td> \n");
				 html.append(" <td class='num'>"+item.getQty1()+"</td> \n");
				 html.append(" <td class='num'>"+item.getQty2()+"</td> \n");
				 html.append("</tr> \n");
				 totalQty  += Utils.convertStrToDouble(item.getQty1());
				 totalQty2 += Utils.convertStrToDouble(item.getQty2());
			 }//for
			 
			 //total
			 html.append("<tr> \n");
			 html.append(" <td></td> \n");
			 html.append(" <td></td> \n");
			 html.append(" <td></td> \n");
			 html.append(" <td></td> \n");
			 html.append(" <td class='num_bold'>"+Utils.decimalFormat(totalQty,Utils.format_number_no_disgit)+"</td> \n");
			 html.append(" <td class='num_bold'>"+Utils.decimalFormat(totalQty2,Utils.format_number_no_disgit)+"</td> \n");
			 html.append("</tr> \n");
			 html.append("</table>\n");
			 
			 /**************************************************************************/
			//Get Detail 
			sql = genSQLSalesDetailReport(o);
	
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				item = new ConfPickingBean();
				item.setPickingNo(Utils.isNull(rst.getString("picking_no")));
				item.setOrderNo(Utils.isNull(rst.getString("order_no")));
				item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
				item.setCustomerName(Utils.isNull(rst.getString("customer_name")));
				item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				item.setAddressShipTo(getLineAddress("S", rst));
				item.setAlternateName(Utils.isNull(rst.getString("s_alternate_name")));
				item.setProductCode(Utils.isNull(rst.getString("product_code")));
				item.setProductName(Utils.isNull(rst.getString("product_name")));
                item.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_number_no_disgit));
                item.setUom(Utils.isNull(rst.getString("uom_id")));
                item.setUnitStandardPrice(Utils.decimalFormat(rst.getDouble("UNIT_STANDARD_PRICE"),Utils.format_current_2_disgit));
                item.setUnitSellingPrice(Utils.decimalFormat(rst.getDouble("UNIT_SELLING_PRICE"),Utils.format_current_2_disgit));
                
                item.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
                item.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
                item.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
                
                item.setTotalAmountD(rst.getDouble("total_amount"));
                item.setVatAmountD(rst.getDouble("vat_amount"));
                item.setNetAmountD(rst.getDouble("net_amount"));
				itemsList.add(item)	;
			}//while
			
			 //gen Head Table
			 html.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			 html.append("<tr><td colspan='16' class='colum_head_hilight'><b> Sales Order Information</b></td> </tr>\n");
			 html.append("</table>\n");
			
			 html.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' > \n");
			 html.append("<tr> \n");
			 html.append("<th class='colum_head_hilight'>Picking No.</th> \n");
			 html.append("<th class='colum_head_hilight'>Order No</th> \n");
			 html.append("<th class='colum_head_hilight'>Customer No</th> \n");
			 html.append("<th class='colum_head_hilight'>Customer Name</th> \n");
			 html.append("<th class='colum_head_hilight'>Sales Person</th> \n");
			 html.append("<th class='colum_head_hilight'>Ship-To</th> \n");
			 html.append("<th class='colum_head_hilight'>Ref Cust(เดิม)</th> \n");
			 html.append("<th class='colum_head_hilight'>SKU</th> \n");
			 html.append("<th class='colum_head_hilight'>SKU Name</th> \n");
			 html.append("<th class='colum_head_hilight'>QTY</th> \n");
			 html.append("<th class='colum_head_hilight'>UOM</th> \n");
			 html.append("<th class='colum_head_hilight'>List Price</th> \n");
			 html.append("<th class='colum_head_hilight'>Selling Price</th> \n");
			 html.append("<th class='colum_head_hilight'>Subtotal</th> \n");
			 html.append("<th class='colum_head_hilight'>Tax</th> \n");
			 html.append("<th class='colum_head_hilight'>Total</th> \n");
			 html.append("</tr></thead> \n");
			//display 
			 for(int i=0;i<itemsList.size();i++){
				 itemNext = null;
				 r++;
				 item = itemsList.get(i);
				 if(i!= itemsList.size()-1){
					itemNext = itemsList.get(i+1);
				 }
				
				countOrderByOrder++;
			    totalAmount += item.getTotalAmountD();
			    totalVatAmount += item.getVatAmountD();
			    totalNetAmount += item.getNetAmountD();
	
			  //Gen Row Table
				html.append("<tr class=''> \n");
				if(orderNoTemp.equals("")){
					html.append("<td class='text' width='6%'>"+item.getPickingNo()+"</td> \n");
					html.append("<td class='text' width='6%'>"+item.getOrderNo()+"</td> \n");
					html.append("<td class='text' width='10%'>"+item.getCustomerCode()+"</td> \n");
					html.append("<td class='text' width='10%'>"+item.getCustomerName()+"</td> \n");
					html.append("<td class='text' width='3%'>"+item.getSalesrepCode()+"</td> \n");
					html.append("<td class='text' width='10%'>"+item.getAddressShipTo()+"</td> \n");
					html.append("<td class='text' width='10%'>"+item.getAlternateName()+"</td> \n");
				}else{
					html.append("<td class='text' width='6%'></td> \n");
					html.append("<td class='text' width='6%'></td> \n");
					html.append("<td class='text' width='10%'></td> \n");
					html.append("<td class='text' width='10%'></td> \n");
					html.append("<td class='text' width='3%'></td> \n");
					html.append("<td class='text' width='10%'></td> \n");
					html.append("<td class='text' width='10%'></td> \n");
				}
				html.append("<td class='text' width='10%'>"+item.getProductCode()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getProductName()+"</td> \n");
				html.append("<td class='num' width='10%'>"+item.getQty()+"</td> \n");
				html.append("<td class='text' width='10%'>"+item.getUom()+"</td> \n");
				html.append("<td class='currency' width='10%'>"+item.getUnitStandardPrice()+"</td> \n");
				html.append("<td class='currency' width='10%'>"+item.getUnitSellingPrice()+"</td> \n");
				html.append("<td class='currency' width='10%'>"+item.getTotalAmount()+"</td> \n");
				html.append("<td class='currency' width='10%'>"+item.getVatAmount()+"</td> \n");
				html.append("<td class='currency' width='10%'>"+item.getNetAmount()+"</td> \n");
				html.append("</tr> \n");
				//gen summary by OrderNo
				if(itemNext== null ||
					(itemNext != null && !item.getOrderNo().equalsIgnoreCase(itemNext.getOrderNo())) ){
					html.append("<tr class='colum_head'> \n");
					html.append("<td class='text' colspan='13' align='right'>Total :</td> \n");
					html.append("<td class='currency_bold' align='center'>"+Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit)+"</td>\n");
					html.append("<td class='currency_bold' align='center'>"+Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit)+" </td>\n");
					html.append("<td class='currency_bold' align='center'>"+Utils.decimalFormat(totalNetAmount,Utils.format_current_2_disgit)+"</td> \n");
					html.append("</tr> \n");
					
					//reset by Sales
					totalAmount = 0;
					totalVatAmount =0;
					totalNetAmount = 0;
					orderNoTemp = "";
				}else{
					orderNoTemp = item.getOrderNo();
				}
			 }//for
			 
			//Check Execute Found data
			if(r>0){
			   html.append("</table>");
			}

		   o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	private static StringBuffer genSQLSalesDetailHeadReport(ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n  SELECT M.*");
		sql.append("\n  FROM (");
		sql.append("\n    /** SALES_APP **/ ");
		sql.append("\n    SELECT p.code as product_code , p.name as product_name ,l.uom_id");
		sql.append("\n    ,nvl(sum(l.qty),0) as qty");
		sql.append("\n    FROM pensso.t_order t ,pensso.t_order_line l");
		sql.append("\n    ,pensso.m_product p ");
		sql.append("\n    WHERE t.order_id = l.order_id ");
		sql.append("\n    AND l.product_id = p.product_id");
		sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
		sql.append("\n    group by p.code,p.name,l.uom_id ");
		sql.append("\n )M ");
		sql.append("\n where 1=1");
		sql.append("\n ORDER BY M.product_code");
		
		return sql;
	}
	private static StringBuffer genSQLSalesDetailReport(ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n  SELECT M.*");
		sql.append("\n  FROM (");
		sql.append("\n    /** SALES_APP **/ ");
		sql.append("\n    SELECT t.picking_no ,t.ORDER_NO ,t.ORDER_DATE ");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,s.code as salesrep_code ,s.name as salesrep_name");
		sql.append("\n    ,ship_address.line1 as s_line1,ship_address.line2 as s_line2,ship_address.line3 as s_line3");
		sql.append("\n    ,ship_address.amphur as s_amphur,ship_address.province as s_province");
		sql.append("\n    ,ship_address.postal_code as s_postal_code ,ship_address.alternate_name as s_alternate_name ");

		sql.append("\n    ,p.code as product_code ,p.name as product_name ");
		sql.append("\n    ,l.qty ,l.uom_id ,l.price as UNIT_STANDARD_PRICE ");
		sql.append("\n    ,(l.price-(l.discount/l.qty)) as UNIT_SELLING_PRICE ");
		sql.append("\n    ,l.total_amount ,l.vat_amount,(l.total_amount+l.vat_amount)as NET_AMOUNT");
		
		sql.append("\n    FROM pensso.t_order t ,pensso.t_order_line l");
		sql.append("\n    ,pensso.m_customer c,pensso.ad_user s ,pensso.m_product p");
		sql.append("\n    ,(select a.customer_id,a.address_id,a.line1,a.line2,a.line3 ,a.postal_code,a.alternate_name ");
		sql.append("\n      ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur ");
		sql.append("\n      ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province ");
		sql.append("\n      from pensso.m_address a where a.purpose ='S'");
		sql.append("\n    ) ship_address");
	
		sql.append("\n    WHERE t.order_id = l.order_id");
		sql.append("\n    and t.customer_id = c.customer_id ");
		sql.append("\n    AND t.user_id = s.user_id ");
		sql.append("\n    AND l.product_id = p.product_id ");
		
		sql.append("\n    AND c.customer_id = ship_address.customer_id ");
		sql.append("\n    AND t.ship_address_id = ship_address.address_id ");
		
		sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
		
		//sql.append("\n    UNION ALL ");
		
		//sql.append("\n    /** EDI **/ ");
		
		sql.append("\n )M ");
		sql.append("\n where 1=1");
		sql.append("\n ORDER BY M.order_no,M.customer_number,M.product_code");
		
		return sql;
	}
	public static StringBuffer genPickingListReport(ConfPickingBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		int lineNo=0;
		String className ="";
		String classNameCenter ="td_text_center";
		String classNameNumber = "td_number";
		double sumSubQtyByProduct =0,sumPriQtyByProduct=0;
		double sumSubQtyBySubBrand =0,sumPriQtyBySubBrand=0;
		ConfPickingBean itemNext = null;
		String subBrandTemp = "";
		try{
			if(excel){
				className = "text";
				classNameCenter ="text";
				classNameNumber = "num_currency";
			}
			//Gen Header
			h.append(genHeadPickingListReport(head, excel));
			
			for(int i=0;i<head.getItemsList().size();i++){
				lineNo++;
				ConfPickingBean item = head.getItemsList().get(i);
				itemNext = null;
				if(i != head.getItemsList().size()-1){
					itemNext = head.getItemsList().get(i+1);
				}
				
				//Gen Summary BY SubBrand Header
				if(subBrandTemp.equalsIgnoreCase("")){
				   h.append("<tr class='colum_head'> \n");
				   h.append("<td colspan='12' width='100%' align='left'>"+item.getSubBrandName()+"</td> \n");
				   h.append("</tr> \n");
			
				}//if
				
				//gen Row Excel
				h.append(genRowPickingListReport(head, excel, item, lineNo));
				
				 //summary by product
				 sumSubQtyByProduct += item.getSubQty();
				 sumPriQtyByProduct += item.getPriQty();
				  
				 //summary by SubBrand
				 sumSubQtyBySubBrand += item.getSubQty();
				 sumPriQtyBySubBrand += item.getPriQty();
				  
				//Gen Summary BY Product Footer
				if(itemNext == null || !item.getProductCode().equalsIgnoreCase(itemNext.getProductCode())){
				   h.append("<tr> \n");
				   h.append("<td colspan='11' width='78%' align='right' class='colum_head'>รวมจำนวน Order </td> \n");
				   h.append("<td class='num_bold' width='7%' align='right'>"+Utils.decimalFormat(sumSubQtyByProduct,Utils.format_current_no_disgit)+" "+item.getUom2()+"</td> \n");
				   h.append("<td class='num_bold_underline' width='7%' align='right'>"+Utils.decimalFormat(sumPriQtyByProduct,Utils.format_current_2_disgit)+" "+item.getUom1()+"</td> \n");
				   h.append("</tr> \n");
				  
				   //reset 
				   sumSubQtyByProduct =0;
				   sumPriQtyByProduct=0;
				}//if
				
				//Gen Summary By SubBrand
				if(itemNext == null || !item.getSubBrand().equalsIgnoreCase(itemNext.getSubBrand())){
				   h.append("<tr > \n");
				   h.append("<td colspan='11' width='78%' align='right' class='colum_head_underline'>ยอดรวมกลุ่ม :"+item.getSubBrandName()+" </td> \n");
				   h.append("<td class='num_bold_underline' width='7%' align='right'>"+Utils.decimalFormat(sumSubQtyBySubBrand,Utils.format_current_no_disgit)+" "+item.getUom2()+"</td> \n");
				   h.append("<td class='num_bold_underline' width='7%' align='right'>"+Utils.decimalFormat(sumPriQtyBySubBrand,Utils.format_current_2_disgit)+" "+item.getUom1()+"</td> \n");
				   h.append("</tr> \n");
				  
				   //reset 
				   sumSubQtyBySubBrand =0;
				   sumPriQtyBySubBrand=0;
				  //for check temp
				   subBrandTemp = "";
				}else{
				   //for check temp
				   subBrandTemp = item.getSubBrand();
				}//if
			}//for
			return h;
		}catch(Exception e){
		  throw e;	
		}
	}
	private static StringBuffer genHeadPickingListReport(ConfPickingBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		String width="100%",border="0";
		if(excel){
			border="1";
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='"+border+"' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			h.append("<tr><td colspan='13' align='center' class='report_head'>Picking List</font></td> </tr>\n");
			h.append("<tr><td colspan='13' class='colum_head'>Picking No:"+head.getPickingNo()+"</td></tr>\n");
			h.append("<tr><td colspan='13' class='colum_head'>วันที่:"+head.getTransactionDate()+"</td></tr>\n");
			h.append("</table>\n");
		}
		
		h.append("<table id='tblProduct' align='center' border='"+border+"' width='"+width+"' cellpadding='3' cellspacing='1' class='table table-condensed table-striped'> \n");
		h.append("<thead><tr> \n");
		h.append("<th rowspan='2'>รหัสสินค้า / ชื่อสินค้า</th> \n");
		h.append("<th rowspan='2'>วันที่ Order</th> \n");
		h.append("<th rowspan='2'>เลขที่ Order</th> \n");
		h.append("<th rowspan='2'>รหัสร้านค้า</th> \n");
		h.append("<th rowspan='2'>ชื่อร้านค้า</th> \n");
		h.append("<th rowspan='2'>Reference Cust No.</th> \n");
		h.append("<th rowspan='2'>อำเภอ / จังหวัด</th> \n");
		h.append("<th rowspan='2'>จำนวนที่ Order</th> \n");
		h.append("<th rowspan='2'>หน่วยที่ Order</th> \n");
		h.append("<th colspan='2'>หน่วยย่อย</th> \n");
		h.append("<th rowspan='2'>ยอดหน่วยย่อย</th> \n");
		h.append("<th rowspan='2'>ยอดจ่ายหน่วยใหญ่</th> \n");
		h.append("</tr> \n");
		h.append("<tr> \n");
		h.append("<th>บรรจุ</th> \n");
		h.append("<th>หน่วย</th> \n");
		h.append("</tr></thead> \n");
		h.append("<tbody> \n");
		return h;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @param ROWVALUE_MAP
	 * @param ROWDESC_MAP
	 * @param o
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genRowPickingListReport(ConfPickingBean head,boolean excel,ConfPickingBean item,int lineNo) throws Exception{
		StringBuffer h = new StringBuffer("");
		String trClass =lineNo%2==0?"lineE":"lineO";
		String className ="";
		String classNameCenter ="td_text_center";
		String classNameNumber = "td_number";
		if(excel){
			className = "text";
			classNameCenter ="text";
			classNameNumber = "num_currency";
		}
		h.append("<tr class='"+trClass+"'> \n");
		h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getProductCode()+"-"+item.getProductName()+"</td> \n");
        h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getOrderDate()+"</td> \n");
    	h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getOrderNo()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getCustomerCode()+"</td> \n");
		h.append("<td class='"+className+"' width='15%'>"+item.getCustomerName()+"</td> \n");
		h.append("<td class='"+className+"' width='15%'>"+item.getAlternateName()+"</td> \n");
		h.append("<td class='"+className+"' width='15%'>"+item.getAmphur()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='7%'>"+item.getQty()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='7%'>"+item.getUom()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='7%'>"+item.getUom2Contain()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='7%'>"+item.getUom2()+"</td> \n");
		h.append("<td class='num_currency' width='7%'>"+item.getSubQty()+"</td> \n");
		h.append("<td class='currency' width='7%'>"+item.getPriQty()+"</td> \n");
		h.append("</tr> \n");
		return h;
	}
	public static String getLineAddress( String purpose,ResultSet rs) throws Exception{
		String lineString;
		lineString = "";
		if( !Utils.isNull(rs.getString(purpose+"_alternate_name")).equals("")){
		   lineString = "("+Utils.isNull(rs.getString(purpose+"_alternate_name"))+") ";
		}
		lineString += Utils.isNull(rs.getString(purpose+"_line1")) + " ";
		lineString += Utils.isNull(rs.getString(purpose+"_line2")) + " ";
		if ("กรุงเทพฯ".equalsIgnoreCase(rs.getString(purpose+"_province"))
				|| "กรุงเทพมหานคร".equalsIgnoreCase(rs.getString(purpose+"_province"))) {
			//lineString += "แขวง";
			lineString += (rs.getString(purpose+"_line3")) + " ";
			//lineString += "เขต";
			lineString += (rs.getString(purpose+"_amphur")) + " ";
			lineString += "";
		} else {
			lineString += "ตำบล";
			lineString += (rs.getString(purpose+"_line3")) + " ";
			lineString += "อำเภอ";
			lineString += (rs.getString(purpose+"_amphur")) + " ";
			lineString += "จังหวัด";
		}
		lineString += (rs.getString(purpose+"_province")) + " ";
		lineString += Utils.isNull(rs.getString(purpose+"_postal_code"));
		return lineString;
	}
}
