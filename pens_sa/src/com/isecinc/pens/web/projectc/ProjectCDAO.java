package com.isecinc.pens.web.projectc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcessAll;

public class ProjectCDAO {
	protected static Logger logger = Logger.getLogger("PENS");

	public static List<ProjectCBean> searchBranchList(Connection conn,ProjectCBean o,boolean allRec,int currPage,int pageSize,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ProjectCBean h = null;
		List<ProjectCBean> items = new ArrayList<ProjectCBean>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select ");
			sql.append("\n  H.* ");
			sql.append("\n from apps.xxpens_ar_cust_sales_all M,apps.xxpens_ar_customer_all_v C");
			sql.append("\n ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ,PENSBI.PENSBME_TT_BRANCH H ");
			sql.append("\n where M.cust_account_id = C.cust_account_id ");
			sql.append("\n and M.primary_salesrep_id = Z.salesrep_id ");
			sql.append("\n and H.oracle_cust_no = C.account_number ");
			sql.append("\n and M.code like 'S%' ");//Credit Sales Only
			sql.append("\n and Z.zone in('0','1','2','3','4') ");
			 //GenWhereSQL
			sql.append(" "+genBranchListWhereCondSql(conn,o,user));
		
			sql.append("\n    ORDER BY H.oracle_cust_no asc ");
			sql.append("\n   )A ");
        	// get record start to end 
            if( !allRec){
        	  sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
        	sql.append("\n )M  ");
			if( !allRec){
			   sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
			}
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ProjectCBean();
			   h.setStoreCode(Utils.isNull(rst.getString("oracle_cust_no")));
			   h.setStoreName(Utils.isNull(rst.getString("oracle_cust_name")));
			   h.setBranchId(Utils.isNull(rst.getString("branch_id")));
			   h.setBranchName(Utils.isNull(rst.getString("branch_name")));
			   h.setBranchSize(Utils.isNull(rst.getString("branch_size")));
			   h.setAddress(Utils.isNull(rst.getString("address")));  
			   h.setAmphor(Utils.isNull(rst.getString("amphor"))); 
			   h.setProvince(Utils.isNull(rst.getString("province"))); 
			   h.setStoreLat(Utils.isNull(rst.getString("latitude"))); 
			   h.setStoreLong(Utils.isNull(rst.getString("longitude"))); 
			   //check is found check stock
			   h.setFoundCheck(isFoundCheck(conn, h));
			   items.add(h);
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	public static boolean isFoundCheck(Connection conn,ProjectCBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
	    boolean foundCheck = false;
		try {
			sql.append("\n select count(*) as c  ");
			sql.append("\n FROM PENSBI.PENSBME_TT_PROJECTC H ,PENSBI.PENSBME_TT_BRANCH B");
			sql.append("\n WHERE B.oracle_cust_no = H.oracle_cust_no");
			sql.append("\n and B.branch_id = H.branch_id");
			sql.append("\n and H.oracle_cust_no ='"+o.getStoreCode()+"'");
			sql.append("\n and H.branch_id ='"+o.getBranchId()+"'");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				if(rst.getInt("c")>0){
					foundCheck = true;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return foundCheck;
	}
	
	public static int searchCheckStockListTotalRec(Connection conn,ProjectCBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c  ");
			sql.append("\n FROM PENSBI.PENSBME_TT_PROJECTC H ,PENSBI.PENSBME_TT_BRANCH B");
			sql.append("\n WHERE B.oracle_cust_no = H.oracle_cust_no");
			sql.append("\n and B.branch_id = H.branch_id");
			 //GenWhereSQL
			sql.append(" "+genCheckStockWhereCondSql(conn,o));
		
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
				totalRec = rst.getInt("c");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRec;
	}
	public static List<ProjectCBean> searchCheckStockList(Connection conn,ProjectCBean o,boolean allRec,int currPage,int pageSize,boolean getItems ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ProjectCBean h = null;
		List<ProjectCBean> items = new ArrayList<ProjectCBean>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select ");
			sql.append("\n  H.* ,B.branch_name,B.address,B.amphor,B.province ");
			sql.append("\n  ,B.latitude,B.longitude ,B.branch_size ");
			sql.append("\n  FROM PENSBI.PENSBME_TT_PROJECTC H ,PENSBI.PENSBME_TT_BRANCH B");
			sql.append("\n  WHERE B.oracle_cust_no = H.oracle_cust_no");
			sql.append("\n  and B.branch_id = H.branch_id");
			 //GenWhereSQL
			sql.append(" "+genCheckStockWhereCondSql(conn,o));
		
			sql.append("\n    ORDER BY H.check_date desc ");
			sql.append("\n   )A ");
        	// get record start to end 
            if( !allRec){
        	  sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
        	sql.append("\n )M  ");
			if( !allRec){
			   sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
			}
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ProjectCBean();
			   h.setId(rst.getString("id"));
			   h.setStoreCode(Utils.isNull(rst.getString("oracle_cust_no")));
			   h.setBranchId(Utils.isNull(rst.getString("branch_id")));
			   h.setBranchName(Utils.isNull(rst.getString("branch_name")));
			   h.setBranchSize(Utils.isNull(rst.getString("branch_size")));
			   h.setAddress(rst.getString("address"));  
			   h.setAmphor(Utils.isNull(rst.getString("amphor"))); 
			   h.setProvince(Utils.isNull(rst.getString("province"))); 
			   h.setStoreLat(Utils.isNull(rst.getString("latitude"))); 
			   h.setStoreLong(Utils.isNull(rst.getString("longitude"))); 
			   h.setCheckUser(Utils.isNull(rst.getString("check_user")));
			   h.setCheckDate(DateUtil.stringValue(rst.getDate("check_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setChkLatitude(Utils.isNull(rst.getString("check_latitude")));
			   h.setChkLongitude(Utils.isNull(rst.getString("check_longitude")));
			   h.setRemark(Utils.isNull(rst.getString("remark")));
			   if(getItems){
				   //get checkStock Detail List
				    h.setItems(searchCheckStockDetailList(conn, h));
				    
				   //get Image List
				    h.setImageBean(searchCheckStockImageList(conn, h));
			   }
			   items.add(h);
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	
	public static ProjectCBean searchCheckStockListCaseNew(Connection conn,ProjectCBean o) throws Exception {
		try {
			 //get checkStock From Sales Analysis
			 o.setItems(searchCheckStockDetailListFromSA(conn, o));
			 
			 o.setImageBean(searchCheckStockImageList(conn, o));
		} catch (Exception e) {
			throw e;
		} finally {
		}
		return o;
	}
	public static List<ProjectCBean> searchCheckStockDetailList(Connection conn,ProjectCBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ProjectCBean> items = null;
		ProjectCBean item = null;
		try {
			sql.append("\n select * FROM ");
			sql.append("\n PENSBI.PENSBME_TT_PROJECTC_I H");
			sql.append("\n WHERE ID ="+o.getId());
			sql.append("\n ORDER BY PRODUCT_CODE ASC ");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				if(items ==null){
					items = new ArrayList<ProjectCBean>();
				}
				item = new ProjectCBean();
				item.setProductCode(Utils.isNull(rst.getString("product_code")));
				item.setProductName(Utils.isNull(rst.getString("product_name")));
				item.setFound(Utils.isNull(rst.getString("found")));
				item.setLeg(Utils.isNull(rst.getString("leg")));
				item.setLineRemark(Utils.isNull(rst.getString("remark")));
				
				items.add(item);
			}//while
			
			//Case New get Product from SalesAnalysis back 6 month
			if(items ==null){
				items = searchCheckStockDetailListFromSA(conn, o);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	
	public static ProjectCImageBean searchCheckStockImageList(Connection conn,ProjectCBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ProjectCImageBean item = new ProjectCImageBean();
		try {
			logger.debug("searchCheckStockImageList id:"+o.getId());
			if( !Utils.isNull(o.getId()).equals("")){
				sql.append("\n select * FROM ");
				sql.append("\n PENSBI.PENSBME_TT_PROJECTC_IMAGE H");
				sql.append("\n WHERE ID ="+o.getId());
				sql.append("\n ORDER BY IMAGE_ID asc ");
				logger.debug("sql:"+sql);
	
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				while(rst.next()) {
					//10 pic
					if(rst.getString("image_id").equals("1")){
					   item.setImageId("1");
					   item.setImageNameDB1(Utils.isNull(rst.getString("image_name")));
					}else if(rst.getString("image_id").equals("2")){
						item.setImageId("2");
						item.setImageNameDB2(Utils.isNull(rst.getString("image_name")));
					}else if(rst.getString("image_id").equals("3")){
						item.setImageId("3");
						item.setImageNameDB3(Utils.isNull(rst.getString("image_name")));
					}else if(rst.getString("image_id").equals("4")){
						item.setImageId("4");
						item.setImageNameDB4(Utils.isNull(rst.getString("image_name")));
					}else if(rst.getString("image_id").equals("5")){
						item.setImageId("5");
						item.setImageNameDB5(Utils.isNull(rst.getString("image_name")));
					}else if(rst.getString("image_id").equals("6")){
						item.setImageId("6");
						item.setImageNameDB6(Utils.isNull(rst.getString("image_name")));
						item.setImageId("7");
					}else if(rst.getString("image_id").equals("7")){
						item.setImageNameDB7(Utils.isNull(rst.getString("image_name")));
					}else if(rst.getString("image_id").equals("8")){
						item.setImageId("8");
						item.setImageNameDB8(Utils.isNull(rst.getString("image_name")));
					}else if(rst.getString("image_id").equals("9")){
						item.setImageId("9");
						item.setImageNameDB9(Utils.isNull(rst.getString("image_name")));
					}else if(rst.getString("image_id").equals("10")){
						item.setImageId("10");
						item.setImageNameDB10(Utils.isNull(rst.getString("image_name")));
					}
					
				}//while
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return item;
	}
	/**
	 * Get Product from Sales Analysis last 6 month by Customer Code
	 * @param conn
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static List<ProjectCBean> searchCheckStockDetailListFromSA(Connection conn,ProjectCBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ProjectCBean> items = null;
		ProjectCBean item = null;
		try {
			//Get back 6 month
			
			sql.append("\n SELECT distinct");
			sql.append("\n P.inventory_item_code ,P.inventory_item_desc");
			sql.append("\n ,V.customer_id ,C.customer_code");
			sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V V");
			sql.append("\n ,PENSBI.XXPENS_BI_MST_ITEM P");
			sql.append("\n ,PENSBI.XXPENS_BI_MST_CUSTOMER C");
			sql.append("\n WHERE P.inventory_item_id= V.inventory_item_id");
			sql.append("\n and V.customer_id = C.customer_id");
			sql.append("\n and to_char(V.invoice_date,'YYYYMM') >= to_char(ADD_MONTHS(SYSDATE,-6),'YYYYMM') ");
			sql.append("\n and to_char(V.invoice_date,'YYYYMM') <= to_char(SYSDATE,'YYYYMM') ");
			sql.append("\n and C.customer_code ='"+o.getStoreCode()+"'");
			sql.append("\n ORDER BY P.inventory_item_code asc ");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				if(items ==null){
					items = new ArrayList<ProjectCBean>();
				}
				item = new ProjectCBean();
				item.setProductCode(Utils.isNull(rst.getString("inventory_item_code")));
				item.setProductName(Utils.isNull(rst.getString("inventory_item_desc")));
				item.setFound("");
				item.setLeg("");
				item.setLineRemark("");
				
				items.add(item);
			}//while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	public static int updateLocationBranch(ProjectCBean bean) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		int update =0;
		int index =0;
		Connection conn = null;
		logger.debug("updateLocationBranch");
		try {
			sql.append("\n update PENSBI.PENSBME_TT_BRANCH");
			sql.append("\n set latitude =? ,longitude =?");
			sql.append("\n ,update_date =?,update_user =?");
			sql.append("\n where oracle_cust_no =? and branch_id = ?");
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnectionApps();
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, bean.getStoreLat());
			ps.setString(++index, bean.getStoreLong());
			ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(++index, bean.getUserName());
			ps.setString(++index, bean.getStoreCode());
			ps.setString(++index, bean.getBranchId());
			update = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return update;
	}
	public static int updateCheckStockHead(Connection conn,ProjectCBean bean) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		int update =0;
		int index =0;
		logger.debug("updateCheckStockHead");
		try {
			sql.append("\n update PENSBI.PENSBME_TT_PROJECTC");
			sql.append("\n set check_user = ? ,check_latitude =? ,check_longitude =?");
			sql.append("\n , remark =?,update_date =?,update_user =?");
			sql.append("\n where ID =?");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, bean.getCheckUser());
			ps.setString(++index, bean.getChkLatitude());
			ps.setString(++index, bean.getChkLongitude());
			ps.setString(++index, bean.getRemark());
			ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(++index, bean.getUserName());
			ps.setString(++index, bean.getId());
			
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
	public static ProjectCBean insertCheckStockHead(Connection conn,ProjectCBean bean) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		int update =0;
		int index =0;
		logger.debug("insertCheckStockHead");
		try {
			sql.append("\n INSERT INTO PENSBI.PENSBME_TT_PROJECTC(");
			sql.append("\n ID,oracle_cust_no ,branch_id");
			sql.append("\n ,check_date,check_user,remark");
			sql.append("\n ,check_latitude,check_longitude,create_date,create_user)");
			sql.append("\n values(?,?,?,?,?,?,?,?,?,?)");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			long id = SequenceProcessAll.getIns().getNextValue("PENSBME_TT_PROJECTC");
			bean.setId(id+"");
			
			ps.setLong(++index, id);
			ps.setString(++index, bean.getStoreCode());
			ps.setString(++index, bean.getBranchId());
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(bean.getCheckDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setString(++index, bean.getCheckUser());
			ps.setString(++index, bean.getRemark());
			ps.setString(++index, bean.getChkLatitude());
			ps.setString(++index, bean.getChkLongitude());
			ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(++index, bean.getUserName());
			
			update = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {}
		}
		return bean;
	}
	public static int updateCheckStockProductItem(Connection conn,ProjectCBean bean) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		int update =0;
		int index =0;
		//logger.debug("updateCheckStockHead");
		try {
			sql.append("\n update PENSBI.PENSBME_TT_PROJECTC_I");
			sql.append("\n set found = ? ,leg =? ");
			sql.append("\n , remark =?,update_date =?,update_user =?");
			sql.append("\n where ID =? and product_code =? ");
			//logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, !Utils.isNull(bean.getFound()).equals("")?"Y":"N");
			ps.setString(++index, bean.getLeg());
			ps.setString(++index, bean.getLineRemark());
			ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(++index, bean.getUserName());
			ps.setString(++index, bean.getId());
			ps.setString(++index, bean.getProductCode());
			
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
	public static ProjectCBean insertCheckStockProductItem(Connection conn,ProjectCBean bean) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		int update =0;
		int index =0;
		//logger.debug("insertCheckStockHead");
		try {
			sql.append("\n INSERT INTO PENSBI.PENSBME_TT_PROJECTC_I(");
			sql.append("\n ID,product_code ,product_name");
			sql.append("\n ,found,leg,remark,create_date,create_user)");
			sql.append("\n values(?,?,?,?,?,?,?,?)");
			//logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());

			ps.setLong(++index, Utils.convertStrToLong(bean.getId()));
			ps.setString(++index, bean.getProductCode());
			ps.setString(++index, bean.getProductName());
			ps.setString(++index, !Utils.isNull(bean.getFound()).equals("")?"Y":"N");
			ps.setString(++index, bean.getLeg());
			ps.setString(++index, bean.getLineRemark());
			ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(++index, bean.getUserName());
			
			update = ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {}
		}
		return bean;
	}
	 public static boolean isIdExistCheckStockProductItem(Connection conn,String id,String productCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			boolean exist = false;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT count(*) as c from PENSBI.PENSBME_TT_PROJECTC_I M");
				sql.append("\n  where id = "+id+" and product_code ='"+productCode+"'");
				//logger.debug("sql:"+sql);
				
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
	 
	 public static boolean isIdExistCheckStockImageItem(Connection conn,String id,String imageId) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			boolean exist = false;
			StringBuilder sql = new StringBuilder();
			try {
				sql.append("\n  SELECT count(*) as c from PENSBI.PENSBME_TT_PROJECTC_IMAGE M");
				sql.append("\n  where 1=1 and id = "+id+" and image_id ="+imageId);
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
	public static int updateCheckStockImageItem(Connection conn,ProjectCBean bean,ProjectCImageBean imageBean) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		int update =0;
		int index =0;
		logger.debug("updateCheckStockHead");
		try {
			sql.append("\n update PENSBI.PENSBME_TT_PROJECTC_IMAGE");
			sql.append("\n set image_name =? ,update_date =?,update_user =?");
			sql.append("\n where ID =? and image_id = ?");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, imageBean.getImageName());
			ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(++index, bean.getUserName());
			ps.setString(++index, bean.getId());
			ps.setString(++index, imageBean.getImageId());
			
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
	
	public static int deleteCheckStockImageItem(Connection conn,String id,String imageId) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		int update =0;
		int index =0;
		logger.debug("updateCheckStockHead");
		try {
			sql.append("\n delete from PENSBI.PENSBME_TT_PROJECTC_IMAGE");;
			sql.append("\n where ID ="+id+" and image_id = "+imageId);
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
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
	public static int insertCheckStockImageItem(Connection conn,ProjectCBean bean,ProjectCImageBean imageBean) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		int update =0;
		int index =0;
		logger.debug("insertCheckStockHead");
		try {
			sql.append("\n INSERT INTO PENSBI.PENSBME_TT_PROJECTC_IMAGE(");
			sql.append("\n ID,image_id ,image_name");
			sql.append("\n ,create_date,create_user)");
			sql.append("\n values(?,?,?,?,?)");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, bean.getId());
			ps.setString(++index, imageBean.getImageId());
			ps.setString(++index, imageBean.getImageName());
			ps.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(++index, bean.getUserName());
			
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
	
	public static StringBuffer genBranchListWhereCondSql(Connection conn,ProjectCBean o,User user) throws Exception{
		StringBuffer sql = new StringBuffer("");

		if( !Utils.isNull(o.getStoreCode()).equals("")){
			sql.append("\n and H.oracle_cust_no = '"+Utils.isNull(o.getStoreCode())+"'");
		}
		/** filter sales by user login **/
		/** check user is mapping customer tt (manager,TTSupper)**/
		if(GeneralDAO.isUserMapCustSalesTT(user)){
			if( !Utils.isNull(user.getUserName()).equals("")){
				sql.append("\n and Z.zone in( ");
				sql.append("\n   select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT ");
				sql.append("\n   where user_name ='"+(user.getUserName())+"'");
				sql.append("\n ) ");
			}
		}else if( !user.getUserName().equalsIgnoreCase("admin")){
			//is CreditSales Key
			sql.append("\n and Z.salesrep_code ='"+user.getUserName().toUpperCase()+"'");
		}
		return sql;
	}
	public static StringBuffer genCheckStockWhereCondSql(Connection conn,ProjectCBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		logger.debug("ProjectCBean:"+o);
		if( !Utils.isNull(o.getId()).equals("")){
			sql.append("\n and H.id = '"+Utils.isNull(o.getId())+"'");
		}
		if( !Utils.isNull(o.getStoreCode()).equals("")){
			sql.append("\n and H.oracle_cust_no = '"+Utils.isNull(o.getStoreCode())+"'");
		}
		if( !Utils.isNull(o.getBranchId()).equals("")){
			sql.append("\n and H.branch_id = '"+Utils.isNull(o.getBranchId())+"'");
		}
		if( !Utils.isNull(o.getCheckDate()).equals("")){
			Date date = DateUtil.parse(o.getCheckDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			sql.append("\n and H.check_date = to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
		}
		return sql;
	}
}
