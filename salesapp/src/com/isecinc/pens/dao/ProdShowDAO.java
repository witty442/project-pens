package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ProdShowBean;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class ProdShowDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static ProdShowBean searchProdShow(Connection conn,String orderNo,boolean getItems) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			ProdShowBean bean = null;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT M.* ");
				sql.append("\n  ,(select name from m_customer c where c.code = M.customer_no) as customer_name ");
				sql.append("\n  from t_prod_show M");
				sql.append("\n  where 1=1 and order_no = '"+orderNo+"'");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					bean = new ProdShowBean();
					bean.setOrderNo(Utils.isNull(rst.getString("order_no")));
					bean.setCustomerCode(Utils.isNull(rst.getString("customer_no")));
					bean.setCustomerName(Utils.isNull(rst.getString("customer_name")));
					bean.setDocDate(DateUtil.stringValue(rst.getDate("doc_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					bean.setRemark(Utils.isNull(rst.getString("remark")));
					bean.setExport(Utils.isNull(rst.getString("exported")));
					if( !"Y".equalsIgnoreCase(bean.getExport())){
					   bean.setMode("edit");
					   bean.setCanSave(true);
					}else{
					   bean.setMode("view");
					   bean.setCanSave(false);
					}
					if(getItems){
                    	bean.setItems(searchProdShowItems(conn, orderNo));
                    }
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return bean;
		}
	 public static List<ProdShowBean> searchProdShowList(Connection conn,String customerCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			ProdShowBean bean = null;
			StringBuilder sql = new StringBuilder();
			List<ProdShowBean> itemList = new ArrayList<ProdShowBean>();
			try {
				sql.append("\n  SELECT M.* ");
				sql.append("\n  ,(select name from m_customer c where c.code = M.customer_no) as customer_name ");
				sql.append("\n  from t_prod_show M");
				sql.append("\n  where 1=1 and customer_no = '"+customerCode+"'");
				sql.append("\n  order by M.order_no desc");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					bean = new ProdShowBean();
					bean.setOrderNo(Utils.isNull(rst.getString("order_no")));
					bean.setCustomerCode(Utils.isNull(rst.getString("customer_no")));
					bean.setCustomerName(Utils.isNull(rst.getString("customer_name")));
					bean.setDocDate(DateUtil.stringValue(rst.getDate("doc_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					bean.setRemark(Utils.isNull(rst.getString("remark")));
					bean.setExport(Utils.isNull(rst.getString("exported")));
					itemList.add(bean);
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return itemList;
		}
	 public static List<ProdShowBean> searchProdShowItems(Connection conn,String orderNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<ProdShowBean> pos = new ArrayList<ProdShowBean>();
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT M.* ,P.brand_name from t_prod_show_line M");
				sql.append("\n  ,( ");
				sql.append("\n  select A.* from (");
				sql.append("\n   select DISTINCT TRIM(SUBSTRING_INDEX(name,'-',1)) as brand_name ,seg_value1 as brand_code ");
				sql.append("\n   from m_product_category where NAME NOT IN('Default','ว่าง')");
				sql.append("\n   AND ISACTIVE = 'Y'");
				sql.append("\n   AND PRODUCT_CATEGORY_ID IN (SELECT DISTINCT PRODUCT_CATEGORY_ID FROM M_PRODUCT WHERE ISACTIVE = 'Y') ");
				sql.append("\n   AND seg_value1 <> '000' ");
				sql.append("\n   AND seg_value1 NOT IN (SELECT c.CODE FROM M_CATALOG c WHERE c.ISEXCLUDE ='Y') ");
				sql.append("\n   AND PRODUCT_CATEGORY_ID NOT IN ");
				sql.append("\n   (SELECT  p2.PRODUCT_CATEGORY_ID  ");
				sql.append("\n    FROM M_PRODUCT p1 , M_PRODUCT_CATEGORY p2 , M_PRODUCT_UNUSED p3  ");
				sql.append("\n    WHERE p1.code = p3.code  and p3.type ='VAN'");
				sql.append("\n    AND p1.PRODUCT_CATEGORY_ID = p2.PRODUCT_CATEGORY_ID  ) "); 
				sql.append("\n  )A ");
				sql.append("\n  WHERE A.brand_name <> '' and A.brand_name is not null ");
				sql.append("\n )P ");
				sql.append("\n  where 1=1 and order_no = '"+orderNo+"'");
				sql.append("\n  and p.brand_code = M.brand ");
				sql.append("\n  ORDER BY id asc \n");
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					ProdShowBean item = new ProdShowBean();
					item.setId(rst.getInt("id"));
					item.setOrderNo(Utils.isNull(rst.getString("order_no")));
					item.setBrand(Utils.isNull(rst.getString("brand")));
					item.setBrandName(Utils.isNull(rst.getString("brand_name")));
					item.setInputFileNameDBPic1(Utils.isNull(rst.getString("pic1")));
					item.setInputFileNameDBPic2(Utils.isNull(rst.getString("pic2")));
					item.setInputFileNameDBPic3(Utils.isNull(rst.getString("pic3")));
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
	 
	 public static int getMaxIdProdShowLine(Connection conn,String orderNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			int maxId = 0;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT max(id) as c from t_prod_show_line M");
				sql.append("\n  where 1=1 and order_no = '"+orderNo+"'");
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					maxId =rst.getInt("c") ;
					
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return maxId;
		}
	 public static boolean isIdExistProdShowLine(Connection conn,String orderNo,long id) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			boolean exist = false;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT count(*) as c from t_prod_show_line M");
				sql.append("\n  where 1=1 and order_no = '"+orderNo+"' and id ="+id);
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					if(rst.getInt("c") >0){
						exist = true;
					}
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return exist;
		}
	 public static int insertProdShow(Connection conn,ProdShowBean bean) throws Exception {
			PreparedStatement ps = null;
			StringBuilder sql = new StringBuilder();
			int update =0;
			int index =0;
			logger.debug("insertProdShow");
			try {
				sql.append("\n insert into t_prod_show");
				sql.append("\n (Order_no,Customer_no ,doc_date,remark,created,created_by)");
				sql.append("\n values(?,?,?,?,?,?) \n");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				ps.setString(++index, bean.getOrderNo());
				ps.setString(++index, bean.getCustomerCode());
				ps.setDate(++index, new java.sql.Date(DateUtil.parse(bean.getDocDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
				ps.setString(++index, bean.getRemark());
				ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
				ps.setString(++index, bean.getCreatedBy());
				
				update = ps.executeUpdate();
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					ps.close();
				} catch (Exception e) {}
			}
			return update;
		}
	 public static int updateProdShow(Connection conn,ProdShowBean bean) throws Exception {
			PreparedStatement ps = null;
			StringBuilder sql = new StringBuilder();
			int update =0;
			int index =0;
			logger.debug("updateProdShow");
			try {
				sql.append("\n update t_prod_show");
				sql.append("\n set remark =?,updated =?,updated_by =?");
				sql.append("\n where order_no =?");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(++index, bean.getRemark());
				ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
				ps.setString(++index, bean.getUpdatedBy());
				ps.setString(++index, bean.getOrderNo());
				
				update = ps.executeUpdate();
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					ps.close();
				} catch (Exception e) {}
			}
			return update;
		}
	 
	 public static int insertProdShowLine(Connection conn,ProdShowBean bean) throws Exception {
			PreparedStatement ps = null;
			StringBuilder sql = new StringBuilder();
			int update =0;
			int index =0;
			logger.debug("insertProdShowLine");
			try {
				sql.append("\n insert into t_prod_show_line");
				sql.append("\n (id,Order_no,brand ,pic1,pic2,pic3,created,created_by)");
				sql.append("\n values(?,?,?,?,?,?,?,?) \n");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				ps.setLong(++index, bean.getId());
				ps.setString(++index, bean.getOrderNo());
				ps.setString(++index, bean.getBrand());
				ps.setString(++index, bean.getInputFileNamePic1());
				ps.setString(++index, bean.getInputFileNamePic2());
				ps.setString(++index, bean.getInputFileNamePic3());
				ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
				ps.setString(++index, bean.getCreatedBy());
				
				update = ps.executeUpdate();
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					ps.close();
				} catch (Exception e) {}
			}
			return update;
		}
	 
	 public static int updateProdShowLine(Connection conn,ProdShowBean bean) throws Exception {
			PreparedStatement ps = null;
			StringBuilder sql = new StringBuilder();
			int update =0;
			int index =0;
			logger.debug("**updateProdShowLine**");
			try {
				logger.debug("order_no:"+bean.getOrderNo());
				logger.debug("id:"+bean.getId());
				
				sql.append("\n update t_prod_show_line");
				sql.append("\n set brand=? ,pic1=?,pic2=?,pic3=?,updated =?,updated_by =?");
				sql.append("\n where order_no =? and id =? \n");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
			
				ps.setString(++index, bean.getBrand());
				ps.setString(++index, bean.getInputFileNamePic1());
				ps.setString(++index, bean.getInputFileNamePic2());
				ps.setString(++index, bean.getInputFileNamePic3());
				ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
				ps.setString(++index, bean.getUpdatedBy());
				
				ps.setString(++index, bean.getOrderNo());
				ps.setLong(++index, bean.getId());
				update = ps.executeUpdate();
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					ps.close();
				} catch (Exception e) {}
			}
			return update;
		}
	 
	 public static int deleteProdShowLine(Connection conn,ProdShowBean bean) throws Exception {
			PreparedStatement ps = null;
			StringBuilder sql = new StringBuilder();
			int update =0;
			int index =0;
			logger.debug("**deleteProdShowLine**");
			try {
				logger.debug("order_no:"+bean.getOrderNo());
				logger.debug("id:"+bean.getId());
				
				sql.append("\n delete from t_prod_show_line");
				sql.append("\n where order_no =? and id =? \n");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				ps.setString(++index, bean.getOrderNo());
				ps.setLong(++index, bean.getId());
				update = ps.executeUpdate();
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					ps.close();
				} catch (Exception e) {}
			}
			return update;
		}
}
