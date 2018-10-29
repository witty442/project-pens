package com.isecinc.pens.web.maya.sub;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.sql.ReportSizeColorLotus_SQL;
import com.isecinc.pens.web.maya.MayaBean;
import com.isecinc.pens.web.maya.MayaForm;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class MayaSaleOutAction {
 private static Logger logger = Logger.getLogger("PENS");
	
 public static MayaForm search(HttpServletRequest request, MayaForm f,User user) throws Exception{
	   Statement stmt = null;
		ResultSet rst = null;
		List<MayaBean> pos = new ArrayList<MayaBean>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			sql = genSQL(conn,f);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				MayaBean item = new MayaBean();
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
				item.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
				
				if("S".equalsIgnoreCase(Utils.isNull(rst.getString("promotion")))){
				  item.setFreeItem("YES");
				}else{
				  item.setFreeItem("");
				}
				pos.add(item);
			}//while

			if(pos != null && pos.size() >0){
				f.setResults(pos);
				//request.getSession().setAttribute("summary" ,item);
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
 
 public static StringBuffer exportToExcel(HttpServletRequest request, MayaForm form,User user,List<MayaBean> list){
		StringBuffer h = new StringBuffer("");
		String colspan ="12";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			if("GroupCode".equalsIgnoreCase(form.getSummaryType())){
				colspan ="11";
			}
			//Header
			h.append("<table border='1'> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"'>รายงานรายละเอียดการขาย ที่ MAYA Shop</td> \n");
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
				  h.append("<th>Total Line Amount</th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					MayaBean s = (MayaBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getOrderDate()+"</td> \n");
					  h.append("<td class='text'>"+s.getOrderNo()+"</td> \n");
					  h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStyle()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getQty()+"</td> \n");
					  h.append("<td class='text'>"+s.getFreeItem()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getUnitPrice()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getLineAmount()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getDiscount()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getVatAmount()+"</td> \n");
					  h.append("<td class='num_currency'>"+s.getTotalAmount()+"</td> \n");
					h.append("</tr>");
				}
				/** Summary **/
				MayaBean s = (MayaBean)request.getSession().getAttribute("summary");
				h.append("<tr> \n");
				 
				 /* if("PensItem".equalsIgnoreCase(form.getSummaryType())){
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }else{
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
				  h.append("<td></td> \n");
				  h.append("<td class='currency_bold'>"+bStart+s.getOnhandAmt()+bEnd+"</td> \n");
				h.append("</tr>");*/
				
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
 
 
 public static StringBuilder genSQL(Connection conn,MayaForm f) throws Exception{
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
			
			sql.append("\n FROM XXPENS_OM_SHOP_ORDER_MST M,XXPENS_OM_SHOP_ORDER_DT D ");
			sql.append("\n ,(" );
			sql.append("\n   SELECT I.inventory_item_id as product_id");
			sql.append("\n   ,MP.PENS_VALUE as PENS_ITEM ");
			sql.append("\n   ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
			sql.append("\n   ,MP.INTERFACE_DESC as BARCODE ");
			sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE MP ,XXPENS_OM_ITEM_MST_V I");
			sql.append("\n   WHERE reference_code ='"+Constants.STORE_TYPE_7CATALOG_ITEM+"'");
			sql.append("\n   AND MP.pens_value =I.segment1 ");
			sql.append("\n ) MP ");
			sql.append("\n WHERE M.ORDER_NUMBER = D.ORDER_NUMBER ");
			sql.append("\n AND D.product_id = MP.product_id ");
			sql.append("\n AND M.DOC_STATUS ='SV' ");
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
