package com.isecinc.pens.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.ConvertNullUtil;

import com.isecinc.pens.bean.ProductBarcodeBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.sales.bean.Basket;
import com.isecinc.pens.web.sales.bean.ProductCatalog;

public class ProductBarcodeDAO {
	private static Logger logger = Logger.getLogger("PENS");
	

	 public static ProductBarcodeBean  getProductCodeByBarcode(String barcode,String priceListId,User user,String inputQty) throws Exception {
		 ProductCatalog itemProd = null;
		 ProductBarcodeBean productBarcode = null;
		 String json ="";
		 ProductCatalog productCatalog = null;
		 Connection conn = null;
		 try{
			 //Init Connection
			 conn = DBConnection.getInstance().getConnection();
			 
			 productBarcode = ProductBarcodeDAO.getProductCodeByBarcodeModel(conn,barcode);
			 
			 if(  !Utils.isNull(productBarcode.getProductCode()).equals("") ){
				itemProd = ProductBarcodeDAO.getProductCatalogByProductCode(conn,productBarcode.getProductCode(),priceListId,user);
	            //logger.debug("itemProd:"+itemProd);
				
	            //Case Not found m_product.uom_id <> m_price.uom_id (bme not sell CTN )
				if(itemProd==null){
					itemProd = ProductBarcodeDAO.getProductCatalogByProductCodeCaseUOMNotEquals(conn,productBarcode.getProductCode(),priceListId,user);	
				}
			    if(itemProd != null){
			    	System.out.println("found productCode:"+itemProd.getProductCode());
			 
			    	List<ProductCatalog> itemList = new ArrayList<ProductCatalog>();
			    	
			    	productCatalog = new ProductCatalog();
			    	productCatalog.setProductId(itemProd.getProductId());
			    	productCatalog.setProductCode(itemProd.getProductCode());
			    	productCatalog.setProductName(itemProd.getProductName());
			    	productCatalog.setUom1(itemProd.getUom1());
			    	productCatalog.setUom2(itemProd.getUom2());
			    	productCatalog.setProductNonBme(itemProd.getProductNonBme());
			    	
			    	//productCatalog.setPrice1(itemProd.getPrice1());
			    	//New Edit 20/11/2018 (price*vat 7%)
			    	if(itemProd.getPrice1()==0){
			    	   productCatalog.setPrice1(0);
			    	}else{
			    	   /** Case ProductNonBme not include vat **/
			    	   if(itemProd.getProductNonBme().equals("N")){
			    	      //calc price include vat 
			    	      productCatalog.setPrice1(calePriceIncludeVat(itemProd.getPrice1()));
			    	   }else{
			    		  productCatalog.setPrice1(itemProd.getPrice1());
			    	   }
			    	   logger.debug("price1:"+productCatalog.getPrice1());
			    	}
			    	
			    	logger.debug("productCode["+productCatalog.getProductCode()+"]price1["+productCatalog.getPrice1()+"]price2["+productCatalog.getPrice2()+"]");
			    	logger.debug("uom1["+productCatalog.getUom1()+"]uom2["+productCatalog.getUom2()+"]");
			    	
			    	/** Case witty:03/09/2562 :Case Product Premium default inputQty = uom2 allway */
			    	if(productCatalog.getPrice1() == 0){//Product premium price==0     
			    		//OLD CODE
			    		/*productCatalog.setPrice2(0);
				    	productCatalog.setTaxable(itemProd.getTaxable());
				    	productCatalog.setQty1(0);
				    	productCatalog.setQty2(Integer.parseInt(inputQty));//get from screen input
				    	//calc from qty1*price1
				    	Double lineAmt = itemProd.getQty2()*itemProd.getPrice1();
				    	productCatalog.setLineNetAmt(lineAmt);*/
				    	
				    	//edit 16/12/2562
				    	if( !Utils.isNull(productCatalog.getUom1()).equals("")
				    		&& !Utils.isNull(productCatalog.getUom2()).equals("") ){
				    		logger.debug("Case1: CTN/BAG");
				    		//Case CTN/BAG
				    		productCatalog.setPrice2(0);
					    	productCatalog.setTaxable(itemProd.getTaxable());
					    	productCatalog.setQty1(0);
					    	productCatalog.setQty2(Integer.parseInt(inputQty));//get from screen input
					    	//calc from qty1*price1
					    	Double lineAmt = itemProd.getQty2()*itemProd.getPrice1();
					    	productCatalog.setLineNetAmt(lineAmt);
					    	
				    	}else{
				    		//case EA/
				    		logger.debug("Case2: EA/");
				    		productCatalog.setPrice2(0);
					    	productCatalog.setTaxable(itemProd.getTaxable());
					    	productCatalog.setQty1(Integer.parseInt(inputQty));
					    	productCatalog.setQty2(0);//get from screen input
					    	//calc from qty1*price1
					    	Double lineAmt = itemProd.getQty1()*itemProd.getPrice1();
					    	productCatalog.setLineNetAmt(lineAmt);
				    	}
				    	
			    	
			    	}else{
				    	productCatalog.setPrice2(0);
				    	productCatalog.setTaxable(itemProd.getTaxable());
				    	productCatalog.setQty1(Integer.parseInt(inputQty));//get  from screen input
				    	productCatalog.setQty2(0);
				    	//calc from qty1*price1
				    	Double lineAmt = itemProd.getQty1()*itemProd.getPrice1();
				    	productCatalog.setLineNetAmt(lineAmt);
			    	}
			    	
			    	itemList.add(productCatalog);
			    	//for test
			    	productBarcode.setProductCatalog(productCatalog);
			    	/****** Add ProductToSO **********************/
			    	Basket basket = new Basket(itemList);	
			    	if(basket != null ){
			    		json = new String(basket.getJSON().toString());
			    		//System.out.println("json:\n"+json);
			    		
			    		productBarcode.setJson(json);
			    	 } 	    
			    }//if itemProd found
			}//if 
		 }catch(Exception e){
			 //e.printStackTrace();
			 logger.error(e.getMessage(),e);
		 }finally{
			 if(conn != null){
				 conn.close();conn=null;
			 }
		 }
		 return productBarcode;
	 }
	 public static ProductBarcodeBean  getProductCodeByBarcodeModel(Connection conn,String barcode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			ProductBarcodeBean p = null;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n SELECT b.* ");
				sql.append("\n from m_barcode b");
				sql.append("\n where b.barcode ='"+barcode+"' \n");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					p = new ProductBarcodeBean();
					p.setBarcode(Utils.isNull(rst.getString("barcode")));
					p.setProductCode(Utils.isNull(rst.getString("product_code")));
					p.setMaterialMaster(Utils.isNull(rst.getString("material_master")));
				}//if

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return p;
		}
	
		public static ProductCatalog getProductCatalogByProductCode(Connection conn,String productCode,String pricelistId ,User u) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			ProductCatalog catalog = null;
			StringBuffer sql = new StringBuffer("");
			
			sql.append("\n SELECT A.* ");
			sql.append("\n ,(SELECT N.product_code from m_product_non_bme n where n.product_code=A.product_code) as product_non_bme ");
			sql.append("\n FROM( ");
			sql.append("\n  SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE ");
			sql.append("\n  , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
			sql.append("\n  ,(CASE WHEN st.product_id  <> '' THEN '0' ELSE '1' end )as target_sort ");
			sql.append("\n  ,(CASE WHEN st.product_id  <> '' THEN 'Y' ELSE '' end )as target ");
			sql.append("\n  ,pd.taxable ");
			sql.append("\n  FROM M_Product pd ");
			sql.append("\n  INNER JOIN M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
			sql.append("\n  LEFT JOIN m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
			sql.append("\n  LEFT OUTER JOIN m_sales_target_new st ON  st.Product_ID = pp1.Product_ID AND DATE_FORMAT(st.target_from, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m')");
			
			sql.append("\n  WHERE pp1.ISACTIVE = 'Y' AND pd.CODE ='"+productCode+"' AND pp1.PRICELIST_ID = "+pricelistId+" ");
			sql.append("\n  AND ( ");
			sql.append("\n    pp1.UOM_ID IN ( ");
			sql.append("\n      SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
			sql.append("\n     ) ");
			sql.append("\n     OR");
			sql.append("\n     pp2.UOM_ID IN ( ");
			sql.append("\n        SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
			sql.append("\n      ) ");
			sql.append("\n   )");
			
	        sql.append("\n  AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"') ");
	        sql.append("\n )A");
	        sql.append("\n WHERE A.product_id not in(");
	 	    sql.append("\n select M.product_id from M_product_center C ,M_product M where M.code = C.code");
	 	    sql.append("\n ) ");
	        sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");
			
	        logger.debug("sql:"+sql);
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
					catalog.setProductNonBme(!Utils.isNull(rst.getString("product_non_bme")).equals("")?"Y":"N");
					logger.debug("productname:"+catalog.getProductName());
				}
			} catch (Exception e) {
				throw e;
			} finally {
			}
			return catalog;
		}
        /**
         * //Case Not found m_product.uom_id <> m_price.uom_id
         *  ex, 883001 ECW50G01WH ���ʷӤ������Ҵ���������� ��٫ͧ��	ECW50G01WH ���ʷӤ������Ҵ���������� ��٫ͧ��	CTN	Y	233130	Y
         * @param conn
         * @param productCode
         * @param pricelistId
         * @param u
         * @return
         * @throws Exception
         */
		public static ProductCatalog getProductCatalogByProductCodeCaseUOMNotEquals(Connection conn,String productCode,String pricelistId ,User u) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			ProductCatalog catalog = new ProductCatalog();
			StringBuffer sql = new StringBuffer("");
			
			sql.append("\n SELECT A.* ");
			sql.append("\n ,(SELECT N.product_code from m_product_non_bme n where n.product_code=A.product_code) as product_non_bme ");
			sql.append("\n FROM( ");
			sql.append("\n  SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE ");
			sql.append("\n  , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp1.PRICE as PRICE2 , '' as UOM2 ");
			sql.append("\n  ,(CASE WHEN st.product_id  <> '' THEN '0' ELSE '1' end )as target_sort ");
			sql.append("\n  ,(CASE WHEN st.product_id  <> '' THEN 'Y' ELSE '' end )as target ");
			sql.append("\n  ,pd.taxable ");
			sql.append("\n  FROM M_Product pd ");
			sql.append("\n  INNER JOIN M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID ");
			sql.append("\n  LEFT OUTER JOIN m_sales_target_new st ON  st.Product_ID = pp1.Product_ID AND DATE_FORMAT(st.target_from, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m')");
			
			sql.append("\n  WHERE pp1.ISACTIVE = 'Y' AND pd.CODE ='"+productCode+"'");
			sql.append("\n  AND pp1.PRICELIST_ID = "+pricelistId);
			sql.append("\n  AND pd.uom_id ='CTN'");//Case product uomId=CTN
			
			sql.append("\n  AND ( ");
			sql.append("\n    pp1.UOM_ID IN ( ");
			sql.append("\n      SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
			sql.append("\n     ) ");
			sql.append("\n   )");
			
	        sql.append("\n   AND pd.CODE NOT IN ( ");
	        sql.append("\n     SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"'");
	        sql.append("\n   ) ");
	        sql.append("\n )A");
	        sql.append("\n WHERE A.product_id not in(");
	 	    sql.append("\n   select M.product_id from M_product_center C ,M_product M where M.code = C.code");
	 	    sql.append("\n ) ");
	        sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");
			
	        logger.debug("sql:"+sql);
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
					catalog.setProductNonBme(!Utils.isNull(rst.getString("product_non_bme")).equals("")?"Y":"N");
					logger.debug("productname:"+catalog.getProductName());
				}
			} catch (Exception e) {
				throw e;
			} finally {
			}
			return catalog;
		}
		
		private static double calePriceIncludeVat(double price1) throws Exception{
			try{
				logger.debug("Price:"+price1);
				BigDecimal priceBig = new BigDecimal(price1);
		    	BigDecimal vatBig = new BigDecimal(0.07);
		    	vatBig = priceBig.multiply(vatBig).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	logger.debug("vatBig:"+vatBig);
		    	
		    	BigDecimal priceIncludeVat = priceBig.add(vatBig).setScale(2,BigDecimal.ROUND_HALF_UP);
		    	logger.debug("before priceIncludeVat:"+priceIncludeVat.doubleValue());
		    	
		    	priceIncludeVat= priceIncludeVat.setScale(0,BigDecimal.ROUND_HALF_UP);
		    	logger.debug("result priceIncludeVat:"+priceIncludeVat.doubleValue());
		    	
		    	return priceIncludeVat.doubleValue();
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
}
