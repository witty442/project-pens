package com.isecinc.pens.web.buds.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.StockOnhandBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class StockOnhandDAO {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");


	public static StockOnhandBean searchStockOnhandReport(Connection conn,StockOnhandBean o ,boolean excel) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockOnhandBean item = null;
		StringBuffer html = null;
		int r = 0;
		try {
			
			sql.append("\n  SELECT p.brand,p.segment1,p.description");
			sql.append("\n  ,sb.subbrand_no,sb.subbrand_desc");
			sql.append("\n  ,(select brand_desc from XXPENS_BI_MST_BRAND b where b.brand_no = p.brand  ) as brand_desc ");
			sql.append("\n  ,t.primary_uom_code,t.second_uom_code");
			sql.append("\n  ,t.onhand_pri_qty,t.onhand_sec_qty");
			sql.append("\n  ,t.reservation_pri_qty ,t.reservation_sec_qty");
			sql.append("\n  ,t.available_pri_qty,t.available_sec_qty");
			sql.append("\n  FROM APPS.xxpens_inv_onhand_z00_v t ,apps.xxpens_om_item_mst_v p");
			sql.append("\n  ,PENSBI.XXPENS_BI_MST_SUBBRAND  sb ");
			sql.append("\n  WHERE t.subinventory_code ='Z001' ");
			sql.append("\n  and t.inventory_item_id = p.inventory_item_id ");
			sql.append("\n  and sb.inventory_item_id = p.inventory_item_id ");
			
			if( !Utils.isNull(o.getBrand()).equals("")){
				sql.append("\n and p.brand in("+SQLHelper.converToTextSqlIn(o.getBrand())+")");
			}
			if( !Utils.isNull(o.getSubBrand()).equals("")){
				sql.append("\n and sb.subbrand_no in("+SQLHelper.converToTextSqlIn(o.getSubBrand())+")");
			}
			if( !Utils.isNull(o.getProductCode()).equals("")){
				sql.append("\n and p.segment1 in("+SQLHelper.converToTextSqlIn(o.getProductCode())+")");
			}
			sql.append("\n order by p.brand,sb.subbrand_no,p.segment1 ");
            
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();
			while(rst.next()) {
			   r++;
			   item = new StockOnhandBean();
			   if(r==1){
				   //gen Header Table
				   html = new StringBuffer("");
				   html.append(genHeadTable(o, excel));
			   }
			   item.setBrand(Utils.isNull(rst.getString("brand")));
			   item.setBrandName(Utils.isNull(rst.getString("brand_desc")));
			   item.setSubBrand(Utils.isNull(rst.getString("subbrand_no")));
			   item.setSubBrandName(Utils.isNull(rst.getString("subbrand_desc")));
			   item.setProductCode(Utils.isNull(rst.getString("segment1")));
			   item.setProductName(Utils.isNull(rst.getString("description")));
			   item.setUom1(Utils.isNull(rst.getString("primary_uom_code")));
			   item.setUom2(Utils.isNull(rst.getString("second_uom_code")));
			   
			   //WAIT
			   item.setPriQtyOnhand(String.valueOf(new Double(rst.getDouble("onhand_pri_qty")).intValue()));
			   item.setSecQtyOnhand(String.valueOf(new Double(rst.getDouble("onhand_sec_qty")).intValue()));
			   item.setPriQtyReserve(String.valueOf(new Double(rst.getDouble("reservation_pri_qty")).intValue()));
			   item.setSecQtyReserve(String.valueOf(new Double(rst.getDouble("reservation_sec_qty")).intValue()));
			   item.setPriQtyAvailable(String.valueOf(new Double(rst.getDouble("Available_pri_qty")).intValue()));
			   item.setSecQtyAvailable(String.valueOf(new Double(rst.getDouble("Available_sec_qty")).intValue()));

			   html.append(genRowTable(o, excel, item, r));
		
			}//while
			
			if(r>0){
				html.append("</tbody> \n");
				html.append("</table> \n");
			}
			//set Result 
			o.setDataStrBuffer(html);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
	return o;
}
	
	private static StringBuffer genHeadTable(StockOnhandBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		String width="100%",border="0";
		String columnHeadClass ="";
		if(excel){
			border="1";
			h.append(ExcelHeader.EXCEL_HEADER);
			columnHeadClass = "colum_head_hilight";
		}
		h.append("<table id='tblProduct' align='center' border='"+border+"' width='"+width+"' cellpadding='3' cellspacing='1' class='table table-condensed table-striped'> \n");
		h.append("<thead><tr> \n");
		h.append("<th rowspan='2' class='"+columnHeadClass+"'>Brand</th> \n");
		h.append("<th rowspan='2' class='"+columnHeadClass+"'>Sub Brand</th> \n");
		h.append("<th rowspan='2' class='"+columnHeadClass+"'>SKU</th> \n");
		h.append("<th rowspan='2' class='"+columnHeadClass+"'>ÀπË«¬‡µÁ¡</th> \n");
		h.append("<th rowspan='2' class='"+columnHeadClass+"'>ÀπË«¬‡»…</th> \n");
		h.append("<th colspan='2' class='"+columnHeadClass+"'>¬Õ¥ µÍÕ°</th> \n");
		h.append("<th colspan='2' class='"+columnHeadClass+"'>¬Õ¥®Õß</th> \n");
		h.append("<th colspan='2' class='"+columnHeadClass+"'>¬Õ¥„™È‰¥È</th> \n");
		h.append("</tr><tr> \n");
		h.append("<th class='"+columnHeadClass+"'>‡µÁ¡</th> \n");
		h.append("<th class='"+columnHeadClass+"'>‡»…</th> \n");
		h.append("<th class='"+columnHeadClass+"'>‡µÁ¡</th> \n");
		h.append("<th class='"+columnHeadClass+"'>‡»…</th> \n");
		h.append("<th class='"+columnHeadClass+"'>‡µÁ¡</th> \n");
		h.append("<th class='"+columnHeadClass+"'>‡»…</th> \n");
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
	private static StringBuffer genRowTable(StockOnhandBean head,boolean excel,StockOnhandBean item,int lineNo) throws Exception{
		StringBuffer h = new StringBuffer("");
		char singleQuote ='"';
		String trClass =lineNo%2==0?"lineE":"lineO";
		String className ="";
		String classNameCenter ="td_text_center";
		String classNameNumber = "td_number";
		if(excel){
			className = "text";
			classNameCenter ="text_center";
			classNameNumber = "num_currency";
		}
		h.append("<tr class='"+trClass+"'> \n");
		h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getBrand()+"-"+item.getBrandName()+"</td> \n");
        h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getSubBrand()+"-"+item.getSubBrandName()+"</td> \n");
		h.append("<td class='"+className+"' width='15%'>"+item.getProductCode()+"/"+item.getProductName()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='3%'>"+item.getUom1()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='5%'>"+item.getUom2()+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='5%'>"+item.getPriQtyOnhand()+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='5%'>"+item.getSecQtyOnhand()+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='5%'>"+item.getPriQtyReserve()+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='5%'>"+item.getSecQtyReserve()+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='5%'><font color='blue'><b>"+item.getPriQtyAvailable()+"</b></font></td> \n");
		h.append("<td class='"+classNameNumber+"' width='5%'><font color='blue'><b>"+item.getSecQtyAvailable()+"</b></font></td> \n");
		return h;
	}
	
}
