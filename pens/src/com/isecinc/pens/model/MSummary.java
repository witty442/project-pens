package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.NumberToolsUtil;

import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.Summary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.order.OrderProcess;
import com.isecinc.pens.report.invoicedetail.InvoiceDetailReport;
import com.isecinc.pens.web.summary.SummaryForm;

public class MSummary {

	private Logger logger = Logger.getLogger("PENS");
	
	  public List<Summary> search(Summary c,User user) throws Exception{
	    	List<Summary> summaryList = new ArrayList<Summary>();
	    	List<OrderLine> orderLines = new ArrayList<OrderLine>();
	    	Connection conn = null;
	    	try{
	    		conn = new DBCPConnectionProvider().getConnection(conn);
	    		if("DETAIL".equals(c.getType())){
	    		   summaryList = searchOrderLineDetail(c,conn,user);
	    		}else{
	    		   summaryList = searchOrderLineTotal(c,conn,user);
	    		}
	    	}catch(Exception e){
	    		logger.error(e.getMessage(),e);
	    	}finally{
	    		if(conn != null){
	    			conn.close();conn= null;
	    		}
	    	}
	    	return summaryList;
	    }

	      
	  public List<Summary> searchOrderLineTotal(Summary c,Connection conn,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<Summary> pos = new ArrayList<Summary>();
			StringBuilder sql = new StringBuilder();
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT  distinct pd.product_id,pd.code,pd.name from t_order h,t_order_line l,m_product pd ");
				sql.append("\n  where h.order_id = l.order_id  ");
				sql.append("\n  and h.doc_status ='SV' ");
				sql.append("\n  and pd.product_id = l.product_id ");

				if( !Utils.isNull(c.getProductCodeFrom()).equals("")
						&& !Utils.isNull(c.getProductCodeFrom()).equals("")){
					sql.append(" and pd.code >='"+c.getProductCodeFrom()+"' \n");
					sql.append(" and pd.code <='"+c.getProductCodeTo()+"' \n");
				}
				
				if( !Utils.isNull(c.getOrderDateFrom()).equals("")
						&&	!Utils.isNull(c.getOrderDateTo()).equals("")	){
						
					  sql.append(" and h.order_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.order_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				sql.append("\n  and h.user_id ="+user.getId());
				
				sql.append("\n  AND l.ISCANCEL='N' ");
				sql.append("\n  ORDER BY pd.code asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					Summary item = new Summary();
					
					item.setProductCode(rst.getString("code"));
					item.setProductName(rst.getString("name"));
					
					String[] product = findProductDetail(conn,c, "", rst.getString("PRODUCT_ID"),user);
					
					if(product != null && product.length >0){
					  item.setFullUOM(Utils.isNull(product[0]));
					  item.setQty(Utils.isNull(product[1]));
					}
					
					String[] productPromotion = findProductPromotionDetail(conn,c, "", rst.getString("PRODUCT_ID"),user);
					if(productPromotion != null && productPromotion.length >0){
					   item.setQtyPromotion(Utils.isNull(productPromotion[1]));
					   
					   if("".equals(item.getFullUOM())){
						   item.setFullUOM(Utils.isNull(productPromotion[0]));
					   }
					   
					}
					
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	  
	  public List<Summary> searchOrderLineDetail(Summary c,Connection conn,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<Summary> pos = new ArrayList<Summary>();
			StringBuilder sql = new StringBuilder();
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT  distinct h.order_date, pd.product_id,pd.code,pd.name from t_order h,t_order_line l,m_product pd ");
				sql.append("\n  where h.order_id = l.order_id  ");
				sql.append("\n  and h.doc_status ='SV' ");
				sql.append("\n  and pd.product_id = l.product_id ");

				if( !Utils.isNull(c.getProductCodeFrom()).equals("")
						&& !Utils.isNull(c.getProductCodeFrom()).equals("")){
					sql.append(" and pd.code >='"+c.getProductCodeFrom()+"' \n");
					sql.append(" and pd.code <='"+c.getProductCodeTo()+"' \n");
				}
				
				  if( !Utils.isNull(c.getOrderDateFrom()).equals("")
						&&	!Utils.isNull(c.getOrderDateTo()).equals("")	){
						
					sql.append(" and h.order_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					sql.append(" and h.order_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
				  sql.append("\n  and h.user_id ="+user.getId());
				
				sql.append("\n  AND l.ISCANCEL='N' ");
				sql.append("\n  ORDER BY h.order_date, pd.code asc \n");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				while (rst.next()) {
					Summary item = new Summary();
					item.setOrderDate(Utils.format(rst.getDate("order_date"),Utils.DD_MM_YYYY_WITH_SLASH));
					item.setProductCode(rst.getString("code"));
					item.setProductName(rst.getString("name"));
					
					String[] product = findProductDetail(conn,c, Utils.format(rst.getDate("order_date"),Utils.DD_MM_YYYY_WITH_SLASH), rst.getString("PRODUCT_ID"),user);
					
					if(product != null && product.length >0){
					   item.setFullUOM(Utils.isNull(product[0]));
					   item.setQty(Utils.isNull(product[1]));
					}
					
					String[] productPromotion = findProductPromotionDetail(conn,c, Utils.format(rst.getDate("order_date"),Utils.DD_MM_YYYY_WITH_SLASH), rst.getString("PRODUCT_ID"),user);
					if(productPromotion != null && productPromotion.length >0){
						 if(Utils.isNull(item.getFullUOM()).equals("")){
							item.setFullUOM(Utils.isNull(productPromotion[0]));
						 }
						item.setQtyPromotion(Utils.isNull(productPromotion[1]));
					}
					pos.add(item);
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	  
	  public String[] findProductDetail(Connection conn,Summary c,String orderDate,String productId,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			String[] r = new String[2];
			StringBuilder sql = new StringBuilder();
			try {
			
				sql.append("\n  SELECT l.product_id,l.uom_id,sum(l.qty) as qty from t_order h,t_order_line l ,m_product p ,m_uom_conversion uc");
				sql.append("\n  where h.order_id = l.order_id  ");
				sql.append("\n  and h.doc_status ='SV' ");
				sql.append("\n  and l.product_id = p.product_id  ");
				sql.append("\n  and p.uom_id = uc.uom_id ");
				sql.append("\n  and p.product_id  = uc.product_id");
						 
				if(!"".equals(orderDate))
				  sql.append(" and h.order_date = str_to_date('"+orderDate+"','%d/%m/%Y') \n");
			    sql.append(" and l.product_id ="+productId+" \n");
			    sql.append(" and l.promotion <> 'Y' \n");
			    sql.append(" and l.ISCANCEL='N' ");
			    sql.append(" and h.user_id ="+user.getId());
			    
			    if( !Utils.isNull(c.getOrderDateFrom()).equals("")
						&&	!Utils.isNull(c.getOrderDateTo()).equals("")	){
						
					  sql.append(" and h.order_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.order_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
			    
			    sql.append(" group by l.product_id,l.uom_id  \n");
			    sql.append(" order by uc.conversion_rate desc \n");
			    
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				String uom1 = "";
				String uom2 = "";
				String qty1 = "";
				String qty2 = "";
				Product product = new MProduct().find(productId);
				
				while(rst.next()){
					if(product.getUom().getId().equals(rst.getString("UOM_ID"))){
						uom1 = rst.getString("UOM_ID")+" ";
						qty1 = NumberToolsUtil.decimalFormat(rst.getDouble("qty"),NumberToolsUtil.format_current_no_disgit)+" ";
					}else{
						uom2 = " "+rst.getString("UOM_ID");
						qty2 = " "+NumberToolsUtil.decimalFormat(rst.getDouble("qty"),NumberToolsUtil.format_current_no_disgit);
					}
				}
				
				if(!"".equals(uom1) || !"".equals(uom2)){
	                r[0] = uom1 +"/"+uom2;
	                r[1] = qty1 +"/"+qty2;
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}

			return r;
		}
	  
	  public String[] findProductPromotionDetail(Connection conn,Summary c,String orderDate,String productId,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			String[] r = new String[2];
			StringBuilder sql = new StringBuilder();
			try {
				
				sql.delete(0, sql.length());
				sql.append("\n  SELECT l.product_id,l.uom_id,sum(l.qty) as qty from t_order h,t_order_line l,m_product p ,m_uom_conversion uc");
				sql.append("\n  where h.order_id = l.order_id  ");
				sql.append("\n  and h.doc_status ='SV' ");
				sql.append("\n  and l.product_id = p.product_id  ");
				sql.append("\n  and p.uom_id = uc.uom_id ");
				sql.append("\n  and p.product_id  = uc.product_id ");
				if(!"".equals(orderDate))
				  sql.append(" and h.order_date = str_to_date('"+orderDate+"','%d/%m/%Y') \n");
			    sql.append(" and l.product_id ="+productId+" \n");
			    sql.append(" and l.promotion = 'Y' \n");
			    sql.append(" and l.ISCANCEL='N' ");
			    sql.append(" and h.user_id ="+user.getId());
			    
			    if( !Utils.isNull(c.getOrderDateFrom()).equals("")
						&&	!Utils.isNull(c.getOrderDateTo()).equals("")	){
						
					  sql.append(" and h.order_date >= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateFrom(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
					  sql.append(" and h.order_date <= str_to_date('"+Utils.format(Utils.parseToBudishDate(c.getOrderDateTo(),Utils.DD_MM_YYYY_WITH_SLASH),Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y') \n");
				}
			    
			    sql.append(" group by l.product_id,l.uom_id  \n");
			    sql.append(" order by uc.conversion_rate desc \n");
				
			    logger.debug("sql:"+sql);
			    
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				String uom1 = "";
				String uom2 = "";
				String qty1 = "";
				String qty2 = "";
				
				Product product = new MProduct().find(productId);
				while(rst.next()){
					if(product.getUom().getId().equals(rst.getString("UOM_ID"))){
						uom1 = rst.getString("UOM_ID")+" ";
						qty1 = NumberToolsUtil.decimalFormat(rst.getDouble("qty"),NumberToolsUtil.format_current_no_disgit)+" ";
					}else{
						
						uom2 = " "+rst.getString("UOM_ID");
						qty2 = " "+NumberToolsUtil.decimalFormat(rst.getDouble("qty"),NumberToolsUtil.format_current_no_disgit);
					}
				}
	
				logger.debug(uom1+":"+uom2);
				if("".equals(uom1)){
					
				}
				
				if(!"".equals(uom1) || !"".equals(uom2)){
	                r[0] = uom1 +"/"+uom2;
	                r[1] = qty1 +"/"+qty2;
				}
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return r;
		}
}
