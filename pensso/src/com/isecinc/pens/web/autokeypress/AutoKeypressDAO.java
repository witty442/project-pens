package com.isecinc.pens.web.autokeypress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MUOMConversion;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.sales.bean.ProductCatalog;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.Utils;

public class AutoKeypressDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static PopupBean searchProduct(Connection conn,PopupBean c) throws Exception {
		return searchProductModel(conn, c);
	}
	public static PopupBean searchProduct(PopupBean c) throws Exception {
		Connection conn = null;
		try{
		    conn = DBConnectionApps.getInstance().getConnection();
		    return searchProductModel(conn, c);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
	public static PopupBean searchProductModel(Connection conn,PopupBean c) throws Exception {
		PopupBean item = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		int pricelistId = 0;
		try {
			pricelistId = new MPriceList().getCurrentPriceList("CR").getId();
			
			sql.append("\n SELECT A.* FROM( ");
			sql.append(" \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
			sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN '0' ELSE '1' end )as target_sort ");
			sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN 'Y' ELSE '' end )as target ");
			sql.append("\n  ,pd.taxable ");
			sql.append("\n FROM PENSSO.M_Product pd ");
			sql.append("\n INNER JOIN PENSSO.M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
			sql.append("\n LEFT JOIN PENSSO.m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID ");
			sql.append("\n AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
			sql.append("\n LEFT OUTER JOIN PENSSO.m_sales_target_new st ON  st.Product_ID = pp1.Product_ID ");
			sql.append("\n AND TO_CHAR(st.target_from, 'YYYYMM')  = TO_CHAR(sysdate, 'YYYYMM')");
			
			sql.append("\n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE = '"+c.getCodeSearch()+"' AND pp1.PRICELIST_ID = "+pricelistId+" ");
			sql.append("\n AND ( ");
			sql.append("\n    pp1.UOM_ID IN ( ");
			sql.append("\n      SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
			sql.append("\n     ) ");
			sql.append("\n     OR");
			sql.append("\n     pp2.UOM_ID IN ( ");
			sql.append("\n        SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
			sql.append("\n      ) ");
			sql.append("\n   )");
	        sql.append("\n  AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM PENSSO.M_PRODUCT_UNUSED ) ");
	        sql.append("\n )A");
	        sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");
			
	        //logger.debug("sql:"+sql);
	        
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				item = new PopupBean();
				item.setProductId(Utils.isNull(rs.getString("product_id")));
				item.setCode(Utils.isNull(rs.getString("product_code")));
				item.setDesc(Utils.isNull(rs.getString("product_name")));
				item.setUom1(Utils.isNull(rs.getString("uom1")));
				item.setUom2(Utils.isNull(rs.getString("uom2")));
				//item.setPrice(p.getPrice1()+"/"+p.getPrice2());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {}
		}
		return item;
	}
	public static ProductCatalog getProduct(User u ,String productCode,String pricelistId) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		String stockArr[] = null;
		//
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM", Locale.US);
		String today = df.format(new Date());
		
		ProductCatalog catalog = null;
		StringBuffer sql = new StringBuffer("");
		sql.append("\n SELECT A.* FROM( ");
		sql.append(" \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN '0' ELSE '1' end )as target_sort ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN 'Y' ELSE '' end )as target ");
		sql.append("\n  ,pd.taxable ");
		sql.append("\n FROM PENSSO.M_Product pd ");
		sql.append("\n INNER JOIN PENSSO.M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
		sql.append("\n LEFT JOIN PENSSO.m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID "
				+ "AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
		sql.append("\n LEFT OUTER JOIN PENSSO.m_sales_target_new st ON  st.Product_ID = pp1.Product_ID "
				+ "AND TO_CHAR(st.target_from, 'YYYYMM')  = TO_CHAR(sysdate, 'YYYYMM')");
		
		sql.append("\n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE ='"+productCode+"' AND pp1.PRICELIST_ID = "+pricelistId+" ");
		sql.append("\n AND ( ");
		sql.append("\n    pp1.UOM_ID IN ( ");
		sql.append("\n      SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
		sql.append("\n     ) ");
		sql.append("\n     OR");
		sql.append("\n     pp2.UOM_ID IN ( ");
		sql.append("\n        SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
		sql.append("\n      ) ");
		sql.append("\n   )");
		
        sql.append("\n  AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM PENSSO.M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"') ");
        sql.append("\n )A");
    	sql.append("\n WHERE A.product_id not in(");
        sql.append("\n select M.product_id from PENSSO.M_product_center C ,PENSSO.M_product M where M.code = C.code");
        sql.append("\n ) ");
        sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");
		
        logger.debug("sql:"+sql);
        
		conn = DBConnection.getInstance().getConnection();
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				catalog = new ProductCatalog();
				catalog.setTarget(rst.getString("target"));
				catalog.setProductId(rst.getInt("PRODUCT_ID"));
				catalog.setProductName(rst.getString("PRODUCT_NAME"));
				catalog.setProductCode( rst.getString("PRODUCT_CODE"));
				catalog.setPrice1(rst.getDouble("PRICE1"));
				catalog.setPrice2(rst.getDouble("PRICE2"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				catalog.setTaxable(ConvertNullUtil.convertToString(rst.getString("TAXABLE")));
				
				UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(conn,catalog.getProductId(), "CTN");//default to CTN
			    UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(conn,catalog.getProductId(), catalog.getUom2());

			    if(uc1 != null){
			    	catalog.setUom1ConvRate(Utils.decimalFormat(uc1.getConversionRate(),Utils.format_number_no_disgit));
			    }
			    if(uc2 != null){
			    	catalog.setUom2ConvRate(Utils.decimalFormat(uc2.getConversionRate(),Utils.format_number_no_disgit));
			    }
			    
				/*stockArr = CheckStockOnhandProcess.checkStockItemProc(catalog.getProductCode(),catalog.getProductId()+"", catalog.getUom1(), catalog.getUom2());
				if(stockArr != null ){
					//logger.debug("stockArr[0]:"+stockArr[0]+",stockArr[1]:"+stockArr[1]);
					catalog.setStockQty(Utils.convertStrToDouble(stockArr[0]));
					catalog.setStockQty1(Utils.convertStrToInt(stockArr[1]));
					catalog.setStockQty2(Utils.convertStrToInt(stockArr[2]));
				}*/
				
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
		return catalog;
	}
	
	
}
