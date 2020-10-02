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
	
	/** For Get Product Info Page SalesOrder ***/
	public static PopupBean searchProduct(Connection conn,PopupBean c,int pricelistId) throws Exception {
		return searchProductModel(conn, c, pricelistId);
	}
	public static PopupBean searchProduct(PopupBean c,int pricelistId) throws Exception {
		Connection conn = null;
		try{
		    conn = DBConnectionApps.getInstance().getConnection();
		    return searchProductModel(conn, c,pricelistId);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
	public static PopupBean searchProductModel(Connection conn,PopupBean c,int pricelistId) throws Exception {
		PopupBean item = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		try {
			if(pricelistId==0){
			   pricelistId = new MPriceList().getCurrentPriceList(conn,"CR").getId();
			}
			
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
			
	        logger.debug("sql:"+sql);
	        
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
	
	/** For Get Product Info Page STock Onhand **/
	public static PopupBean searchProductStockOnhand(Connection conn,PopupBean c,int pricelistId) throws Exception {
		return searchProductModel(conn, c, pricelistId);
	}
	public static PopupBean searchProductStockOnhand(PopupBean c,int pricelistId) throws Exception {
		Connection conn = null;
		try{
		    conn = DBConnectionApps.getInstance().getConnection();
		    return searchProductStockOnhandModel(conn, c,pricelistId);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
	public static PopupBean searchProductStockOnhandModel(Connection conn,PopupBean c,int pricelistId) throws Exception {
		PopupBean item = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		try {
			if(pricelistId==0){
			   pricelistId = new MPriceList().getCurrentPriceList(conn,"CR").getId();
			}
			
			sql.append("\n SELECT A.* FROM( ");
			sql.append("\n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 ");
			sql.append("\n ,pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
			sql.append("\n ,pd.taxable ");
			sql.append("\n FROM PENSSO.M_Product pd ");
			sql.append("\n INNER JOIN PENSSO.M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
			sql.append("\n LEFT JOIN PENSSO.m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID ");
			sql.append("\n AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
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
	        sql.append("\n )A");
	        sql.append("\n ORDER BY A.PRODUCT_CODE ");
			
	        logger.debug("sql:"+sql);
	        
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
	
}
