package com.isecinc.pens.web.salestarget;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.exception.DataDuplicateException;
import com.isecinc.pens.process.SequenceProcessAll;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.UserUtils;
import com.pens.util.Utils;

public class SalesTargetTTDAO {

	protected static Logger logger = Logger.getLogger("PENS");
	
	public static boolean salesRejectItem_TT(User user,String custCatNo,String salesZone
			,String brand,String period,String startDate,String rejectReason)  throws Exception {
		logger.debug("salesReject By MT");
		Connection conn = null;
        boolean r = false;	
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			SalesTargetBean h = new SalesTargetBean();
			h.setPeriod(period);
			h.setStartDate(startDate);
			h.setCustCatNo(custCatNo);
			h.setSalesZone(salesZone);
			h.setBrand(brand);
			h.setStatus(SalesTargetConstants.STATUS_REJECT);
			h.setUpdateUser(user.getUserName());
			h.setRejectReason(rejectReason);
			
			//update status REJECT IN TT Head and detail
			SalesTargetTTDAO.updateStatusRejectByTTSUPER_TT(conn, h);
			
			//update status POST in TEMP
			SalesTargetTTDAO.updateStatusHead_TEMPByTTSUPER(conn, h);
			SalesTargetTTDAO.updateStatusItem_TEMPByTTSUPER(conn, h);
			
			conn.commit();
			r = true;
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return r;
	}
	
	public static boolean salesManagerTTUnaccept(User user,String salesZone,String brand,
			String custCatNo,String period,String startDate ) throws Exception {
		logger.debug("Sales Manager Unaccept By Manager TT");
		Connection conn = null;
        boolean r = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			SalesTargetBean h = new SalesTargetBean();
			h.setSalesZone(salesZone);
			h.setBrand(brand);
			h.setCustCatNo(custCatNo);
			h.setPeriod(period);
			h.setStartDate(startDate);
			h.setStatus(SalesTargetConstants.STATUS_UN_ACCEPT);
			h.setUpdateUser(user.getUserName());
			
			h = convertCriteria(h);
			
			//update status Unaccept
			SalesTargetTTDAO.updateStatusHead_TEMPByTTSUPER(conn, h);
			SalesTargetTTDAO.updateStatusItem_TEMPByTTSUPER(conn, h);
			
			conn.commit();
			r = true;
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return r;
	}
	
	public static boolean salesManagerAccept(User user,String salesZone,String brand,
			String custCatNo,String period,String startDate )  throws Exception {
		logger.debug("Sales Manager Accept By Manager TT");
		Connection conn = null;
        boolean r = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			SalesTargetBean h = new SalesTargetBean();
			h.setSalesZone(salesZone);
			h.setBrand(brand);
			h.setCustCatNo(custCatNo);
			h.setPeriod(period);
			h.setStartDate(startDate);
			h.setStatus(SalesTargetConstants.STATUS_FINISH);
			h.setUpdateUser(user.getUserName());
			
			h = convertCriteria(h);
			
			//update status accept to finish
			SalesTargetTTDAO.updateStatusHead_TEMPByTTSUPER(conn, h);
			SalesTargetTTDAO.updateStatusItem_TEMPByTTSUPER(conn, h);
			
			//update TT for MKT
			SalesTargetTTDAO.updateStatusHead_TTByMKT(conn, h);
			conn.commit();
			r = true;
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return r;
	}
	
	public static SalesTargetBean searchTargetHeadByMKT_TT(SalesTargetBean o,User user,String pageName ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		SalesTargetBean h = null;
		List<SalesTargetBean> items = new ArrayList<SalesTargetBean>();
		int totalTargetQty = 0;
		double totalTargetAmount = 0;
		double totalOrderAmt12Month = 0;
		double totalOrderAmt3Month = 0;
		try {
			//convert Criteria
			o = convertCriteria(o);
			
			sql.append("\n select A.cust_cat_no ,A.cust_cat_desc ,A.zone ,A.zone_name ,A.status ");
			sql.append("\n ,SUM(A.total_target_qty) as  total_target_qty ,SUM(A.total_target_amount) as total_target_amount ");
			sql.append("\n ,SUM(A.amt_avg12) as amt_avg12 ,SUM(A.amt_avg3) as amt_avg3 ");
			sql.append("\n FROM (  ");
			sql.append("\n   select M.cust_cat_no ,M.cust_cat_desc ,M.zone ,M.zone_name ,T.status ");
			sql.append("\n   ,total_target_qty ,total_target_amount ");
			sql.append("\n   ,amt_avg12 ,amt_avg3 ");
			sql.append("\n   FROM (  ");
			sql.append("\n     select distinct");
			sql.append("\n     M.cust_cat_no ,M.cust_cat_desc  ");
			sql.append("\n    ,M.zone ,M.zone_name  ");
			sql.append("\n     from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT M ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n     where M.zone = Z.zone ");
			if( !Utils.isNull(o.getSalesZone()).equals("")){
				sql.append("\n     and M.zone = '"+Utils.isNull(o.getSalesZone())+"'");
			}
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n     and M.cust_cat_no = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			sql.append("\n ) M LEFT OUTER JOIN ");
			sql.append("\n ( ");
			sql.append("\n  select M.CUSTOMER_CATEGORY,M.ZONE,M.brand");
			sql.append("\n  ,(select nvl(sum(amt_avg12),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as amt_avg12 ");
			sql.append("\n  ,(select nvl(sum(amt_avg3),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as amt_avg3 ");
			sql.append("\n  ,(select nvl(sum(target_qty),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_qty ");
			sql.append("\n  ,(select nvl(sum(target_amount),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_amount ");
			sql.append("\n  , M.status ");
			sql.append("\n  from PENSBI.XXPENS_BI_SALES_TARGET_TT M  ");
			sql.append("\n  where 1=1 ");
			if( !Utils.isNull(o.getSalesZone()).equals("")){
				sql.append("\n and M.zone = '"+Utils.isNull(o.getSalesZone())+"'");
			}
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			if( !Utils.isNull(o.getTargetMonth()).equals("")){
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
			}
			if( !Utils.isNull(o.getTargetQuarter()).equals("")){
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
			}
			if( !Utils.isNull(o.getTargetYear()).equals("")){
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
			}
			if( !Utils.isNull(o.getBrand()).equals("")){
				sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
			}
			sql.append("\n )T ON ");
			sql.append("\n  M.ZONE = T.ZONE AND T.CUSTOMER_CATEGORY = M.cust_cat_no ");
			sql.append("\n )A ");
			sql.append("\n GROUP BY A.cust_cat_no ,A.cust_cat_desc ,A.zone ,A.zone_name ,A.status ");
			sql.append("\n ORDER BY A.cust_cat_no,A.zone asc ");

			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new SalesTargetBean();
			   h.setCustCatNo(rst.getString("cust_cat_no"));  
			   h.setCustCatDesc(rst.getString("cust_cat_desc")); 
			   h.setSalesZone(Utils.isNull(rst.getString("zone"))); 
			   h.setSalesZoneDesc(Utils.isNull(rst.getString("zone_name"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setTargetQty(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_QTY"), Utils.format_current_no_disgit,""));
			   h.setTargetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_AMOUNT"), Utils.format_current_2_disgit,""));
			
			   //get Total Qty
			   totalTargetQty = totalTargetQty+ rst.getInt("TOTAL_TARGET_QTY");
			   totalTargetAmount = totalTargetAmount+ rst.getDouble("TOTAL_TARGET_AMOUNT");
			   totalOrderAmt12Month = totalOrderAmt12Month+ rst.getDouble("amt_avg12");
			   totalOrderAmt3Month = totalOrderAmt3Month+rst.getDouble("amt_avg3");
			   
			   //set Access Action
			   h = SalesTargetTTUtils.setAccess(h,user,pageName);
			
			   items.add(h);
			}//while

			//set Result 
			o.setItems(items);
			o.setTotalTargetQty(Utils.decimalFormat(totalTargetQty, Utils.format_current_no_disgit));
			o.setTotalTargetAmount(Utils.decimalFormat(totalTargetAmount, Utils.format_current_2_disgit));
			o.setTotalOrderAmt12Month(Utils.decimalFormat(totalOrderAmt12Month, Utils.format_current_2_disgit));
			o.setTotalOrderAmt3Month(Utils.decimalFormat(totalOrderAmt3Month, Utils.format_current_2_disgit));
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	
	public static SalesTargetBean searchTargetHeadByTTSUPER_TT(SalesTargetBean o,User user,String pageName ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		SalesTargetBean h = null;
		List<SalesTargetBean> items = new ArrayList<SalesTargetBean>();
		int totalTargetQty = 0;
		double totalTargetAmount = 0;
		int rowId = 0;
		try {
			//convert Criteria
			o = convertCriteria(o);

			sql.append("\n  select M.CUSTOMER_CATEGORY,M.ZONE,M.brand,M.status as status_tt ");
			sql.append("\n  ,(select max(zone_name) from PENSBI.XXPENS_BI_MST_SALES_ZONE B where B.zone = M.zone) as zone_name ");
			sql.append("\n  ,(select max(cust_cat_desc) from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT B where B.cust_cat_no = M.CUSTOMER_CATEGORY) as CUSTOMER_CATEGORY_DESC ");
			sql.append("\n  ,(select brand_desc from PENSBI.XXPENS_BI_MST_BRAND B where B.brand_no = M.brand) as brand_name ");
			sql.append("\n  ,(select nvl(sum(target_qty),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_qty ");
			sql.append("\n  ,(select nvl(sum(target_amount),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_amount ");
			
			sql.append("\n  ,(select nvl(sum(target_qty),0) ");
			sql.append("\n    from PENSBI.XXPENS_BI_SALES_TARGET_TEMP H ,PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L L ");
			sql.append("\n    ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n    where H.id=L.id and H.salesrep_id = Z.salesrep_id");
			sql.append("\n    and H.period = M.period and H.brand = M.brand ");
			sql.append("\n    and H.CUSTOMER_CATEGORY = M.CUSTOMER_CATEGORY ");
			sql.append("\n    and Z.zone = M.zone");
			sql.append("\n    and H.target_month = M.target_month");
			sql.append("\n    and H.target_quarter = M.target_quarter");
			sql.append("\n    and H.target_year = M.target_year");
			sql.append("\n  ) as total_sales_target_qty ");
			
			sql.append("\n  , M.status as tt_status ,M.reject_reason ");
			sql.append("\n  from PENSBI.XXPENS_BI_SALES_TARGET_TT M ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and M.status <>'"+SalesTargetConstants.STATUS_OPEN+"'");
			if( !Utils.isNull(o.getSalesZone()).equals("")){
				sql.append("\n and M.zone = '"+Utils.isNull(o.getSalesZone())+"'");
			}
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			if( !Utils.isNull(o.getTargetMonth()).equals("")){
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
			}
			if( !Utils.isNull(o.getTargetQuarter()).equals("")){
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
			}
			if( !Utils.isNull(o.getTargetYear()).equals("")){
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
			}
			if( !Utils.isNull(o.getBrand()).equals("")){
				sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
			}
			//filter user login
			if( !Utils.isNull(user.getUserName()).equalsIgnoreCase("admin")){
				sql.append("\n and M.zone in( ");
				sql.append("\n  select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT");
				sql.append("\n  where user_name = '"+user.getUserName()+"'");
				sql.append("\n ) ");
			}
			
			sql.append("\n ORDER BY M.CUSTOMER_CATEGORY,M.zone,M.brand asc ");

			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   rowId++;
			   h = new SalesTargetBean();
			   h.setRowId(rowId);
			   h.setCustCatNo(rst.getString("CUSTOMER_CATEGORY"));  
			   h.setCustCatDesc(rst.getString("CUSTOMER_CATEGORY_DESC")); 
			   h.setSalesZone(rst.getString("zone"));
			   h.setSalesZoneDesc(rst.getString("zone_name"));
			   h.setBrand(rst.getString("brand"));  
			   h.setBrandName(rst.getString("brand_name"));
			   
			  
			   h.setRejectReason(Utils.isNull(rst.getString("reject_reason")));
			   h.setTargetQty(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_QTY"), Utils.format_current_no_disgit,""));
			   h.setTargetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_AMOUNT"), Utils.format_current_2_disgit,""));
			
			   //get status from temp //more 1 status
			   h.setPeriod(o.getPeriod());
			   h.setTargetMonth(o.getTargetMonth());
			   h.setTargetQuarter(o.getTargetQuarter());
			   h.setTargetYear(o.getTargetYear());
			   
			   h.setStatus(findStatusTEMPByTTSUPER(conn,h,rst.getString("status_tt")));
			   if( Utils.isNull(h.getStatus()).equals("") ){
				  h.setStatus(Utils.isNull(rst.getString("tt_status")));
			   }
			   if(rst.getInt("total_sales_target_qty") <= 0){
				   h.setCanCopy(true);
			   }
			   
			   //get Total Qty
			   totalTargetQty = totalTargetQty+ rst.getInt("TOTAL_TARGET_QTY");
			   totalTargetAmount = totalTargetAmount+ rst.getDouble("TOTAL_TARGET_AMOUNT");
			
			   //set Access Action
			   h = SalesTargetTTUtils.setAccess(h,user,pageName);
			   /*logger.debug("canSet:"+h.isCanSet());
			   logger.debug("canAccept:"+h.isCanAccept());
			   logger.debug("canReject:"+h.isCanReject());*/
			   items.add(h);
			}//while

			//set Result 
			o.setItems(items);
			o.setTotalTargetQty(Utils.decimalFormat(totalTargetQty, Utils.format_current_no_disgit));
			o.setTotalTargetAmount(Utils.decimalFormat(totalTargetAmount, Utils.format_current_2_disgit));
		
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	public static SalesTargetBean searchTargetHeadByTTMGR_TT(SalesTargetBean o,User user,String pageName ) throws Exception {
		return searchTargetHeadByTTMGR_TTypeBrand(o, user, pageName);
	}
	
	public static SalesTargetBean searchTargetHeadByTTMGR_TTypeBrand(SalesTargetBean o,User user,String pageName ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		SalesTargetBean h = null;
		List<SalesTargetBean> items = new ArrayList<SalesTargetBean>();
		int totalTargetQty = 0;
		double totalTargetAmount = 0;
		int totalSalesTargetQty = 0;
		double totalSalesTargetAmount = 0;
		boolean canFinish = false;
		int rowId=0;
		try {
			//convert Criteria
			o = convertCriteria(o);

			sql.append("\n  select M.CUSTOMER_CATEGORY,M.ZONE,M.brand,M.status as status_tt ");
			sql.append("\n  ,(select max(zone_name) from PENSBI.XXPENS_BI_MST_SALES_ZONE B where B.zone = M.zone) as zone_name ");
			sql.append("\n  ,(select max(cust_cat_desc) from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT B where B.cust_cat_no = M.CUSTOMER_CATEGORY) as CUSTOMER_CATEGORY_DESC ");
			sql.append("\n  ,(select brand_desc from PENSBI.XXPENS_BI_MST_BRAND B where B.brand_no = M.brand) as brand_name ");
			
			sql.append("\n  ,(select nvl(sum(target_qty),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_qty ");
			sql.append("\n  ,(select nvl(sum(target_amount),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_amount ");
			
			sql.append("\n  ,(select nvl(sum(target_qty),0) ");
			sql.append("\n    from PENSBI.XXPENS_BI_SALES_TARGET_TEMP H ,PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L L ");
			sql.append("\n    ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n    where H.id=L.id and H.salesrep_id = Z.salesrep_id");
			sql.append("\n    and H.period = M.period and H.brand = M.brand ");
			sql.append("\n    and H.CUSTOMER_CATEGORY = M.CUSTOMER_CATEGORY ");
			sql.append("\n    and Z.zone = M.zone");
			sql.append("\n    and H.target_month = M.target_month");
			sql.append("\n    and H.target_quarter = M.target_quarter");
			sql.append("\n    and H.target_year = M.target_year");
			sql.append("\n  ) as total_sales_target_qty ");
			
			sql.append("\n  ,(select nvl(sum(target_amount),0) ");
			sql.append("\n    from PENSBI.XXPENS_BI_SALES_TARGET_TEMP H ,PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L L ");
			sql.append("\n    ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n    where H.id=L.id and H.salesrep_id = Z.salesrep_id");
			sql.append("\n    and H.period = M.period and H.brand = M.brand ");
			sql.append("\n    and H.CUSTOMER_CATEGORY = M.CUSTOMER_CATEGORY ");
			sql.append("\n    and Z.zone = M.zone");
			sql.append("\n    and H.target_month = M.target_month");
			sql.append("\n    and H.target_quarter = M.target_quarter");
			sql.append("\n    and H.target_year = M.target_year");
			sql.append("\n  ) as total_sales_target_amount ");
			
			sql.append("\n  from PENSBI.XXPENS_BI_SALES_TARGET_TT M ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and m.status <> '"+SalesTargetConstants.STATUS_OPEN+"'");
			if( !Utils.isNull(o.getSalesZone()).equals("")){
				sql.append("\n and M.zone = '"+Utils.isNull(o.getSalesZone())+"'");
			}
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			if( !Utils.isNull(o.getTargetMonth()).equals("")){
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
			}
			if( !Utils.isNull(o.getTargetQuarter()).equals("")){
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
			}
			if( !Utils.isNull(o.getTargetYear()).equals("")){
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
			}
			if( !Utils.isNull(o.getBrand()).equals("")){
				sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
			}
			//filter user login
			if( !Utils.isNull(user.getUserName()).equalsIgnoreCase("admin")){
				sql.append("\n and M.zone in( ");
				sql.append("\n  select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT");
				sql.append("\n  where user_name = '"+user.getUserName()+"'");
				sql.append("\n ) ");
			}
			sql.append("\n ORDER BY M.CUSTOMER_CATEGORY,M.zone,M.brand asc ");

			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   rowId++;
			   h = new SalesTargetBean();
			   h.setRowId(rowId);
			   h.setPeriod(o.getPeriod());
			   h.setStartDate(o.getStartDate());
			   h.setCustCatNo(rst.getString("CUSTOMER_CATEGORY"));  
			   h.setCustCatDesc(rst.getString("CUSTOMER_CATEGORY_DESC")); 
			   h.setSalesZone(rst.getString("zone"));
			   h.setSalesZoneDesc(rst.getString("zone_name"));
			   h.setBrand(rst.getString("brand"));  
			   h.setBrandName(rst.getString("brand_name"));
			   h.setTargetQty(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_QTY"), Utils.format_current_no_disgit,""));
			   h.setTargetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_AMOUNT"), Utils.format_current_2_disgit,""));
			
			   h.setSalesTargetQty(Utils.decimalFormat(rst.getDouble("TOTAL_SALES_TARGET_QTY"), Utils.format_current_no_disgit,""));
			   h.setSalesTargetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_SALES_TARGET_AMOUNT"), Utils.format_current_2_disgit,""));
			
			   //get status from temp //more 1 status
			   h.setTargetMonth(o.getTargetMonth());
			   h.setTargetQuarter(o.getTargetQuarter());
			   h.setTargetYear(o.getTargetYear());
			   
			   h.setStatus(findStatusTEMPByTTSUPER(conn,h,rst.getString("status_tt")));
			  
			   //get Total Qty MKT
			   totalTargetQty += rst.getInt("TOTAL_TARGET_QTY");
			   totalTargetAmount += rst.getDouble("TOTAL_TARGET_AMOUNT");
			
			   //Total By TTSUPER
			   totalSalesTargetQty += rst.getInt("TOTAL_SALES_TARGET_QTY");
			   totalSalesTargetAmount += rst.getDouble("TOTAL_SALES_TARGET_AMOUNT");
			  
			   //set Access Action
			   h = SalesTargetTTUtils.setAccess(h,user,pageName);
			  // logger.debug("canUnAccept:"+h.isCanUnAccept());
			  // logger.debug("canFinish:"+h.isCanFinish());
			   
			   items.add(h);
			   canFinish = true;//show alway
			}//while

			//logger.debug("totalSalesTargetQty:"+totalSalesTargetQty);
			//logger.debug("totalSalesTargetAmount:"+totalSalesTargetAmount);
			
			//set Result 
			o.setItems(items);
			o.setTotalTargetQty(Utils.decimalFormat(totalTargetQty, Utils.format_current_no_disgit));
			o.setTotalTargetAmount(Utils.decimalFormat(totalTargetAmount, Utils.format_current_2_disgit));
			o.setTotalSalesTargetQty(Utils.decimalFormat(totalSalesTargetQty, Utils.format_current_no_disgit));
			o.setTotalSalesTargetAmount(Utils.decimalFormat(totalSalesTargetAmount, Utils.format_current_2_disgit));
			o.setCanFinish(canFinish);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	
	public static SalesTargetBean searchTargetHeadByTTADMIN_TT(SalesTargetBean o,User user,String pageName ) throws Exception {
		return searchTargetHeadByTTADMIN_TTypeBrand(o, user, pageName);
	}
	
	public static SalesTargetBean searchTargetHeadByTTADMIN_TTypeBrand(SalesTargetBean o,User user,String pageName ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		SalesTargetBean h = null;
		List<SalesTargetBean> items = new ArrayList<SalesTargetBean>();
		int rowId=0;
		try {
			//convert Criteria
			o = convertCriteria(o);
			sql.append("\n SELECT A.BRAND ,A.brand_name ");
			sql.append("\n ,SUM(total_target_qty) as total_target_qty  ");
			sql.append("\n ,SUM(total_target_amount) as total_target_amount  ");
			sql.append("\n FROM(  ");
			sql.append("\n  select M.CUSTOMER_CATEGORY,M.ZONE,M.brand,M.status as status_tt ");
			sql.append("\n  ,(select max(zone_name) from PENSBI.XXPENS_BI_MST_SALES_ZONE B where B.zone = M.zone) as zone_name ");
			sql.append("\n  ,(select max(cust_cat_desc) from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT B where B.cust_cat_no = M.CUSTOMER_CATEGORY) as CUSTOMER_CATEGORY_DESC ");
			sql.append("\n  ,(select brand_desc from PENSBI.XXPENS_BI_MST_BRAND B where B.brand_no = M.brand) as brand_name ");
			
			sql.append("\n  ,(select nvl(sum(target_qty),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_qty ");
			sql.append("\n  ,(select nvl(sum(target_amount),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_amount ");
			
			sql.append("\n  from PENSBI.XXPENS_BI_SALES_TARGET_TT M ");
			sql.append("\n  where 1=1 ");
			if( !Utils.isNull(o.getSalesZone()).equals("")){
				sql.append("\n and M.zone = '"+Utils.isNull(o.getSalesZone())+"'");
			}
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			if( !Utils.isNull(o.getTargetMonth()).equals("")){
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
			}
			if( !Utils.isNull(o.getTargetQuarter()).equals("")){
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
			}
			if( !Utils.isNull(o.getTargetYear()).equals("")){
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
			}
			if( !Utils.isNull(o.getBrand()).equals("")){
				sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
			}
			//filter user login
			if( !Utils.isNull(user.getUserName()).equalsIgnoreCase("admin")){
				sql.append("\n and M.zone in( ");
				sql.append("\n  select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT");
				sql.append("\n  where user_name = '"+user.getUserName()+"'");
				sql.append("\n ) ");
			}
			sql.append("\n )A ");
			sql.append("\n GROUP BY A.brand ,A.brand_name ");
			sql.append("\n ORDER BY A.brand asc ");

			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   rowId++;
			   h = new SalesTargetBean();
			   h.setRowId(rowId);
			   h.setPeriod(o.getPeriod());
			   h.setStartDate(o.getStartDate());
			
			   h.setBrand(rst.getString("brand"));  
			   h.setBrandName(rst.getString("brand_name"));
			   h.setTargetQty(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_QTY"), Utils.format_current_no_disgit,""));
			   h.setTargetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_AMOUNT"), Utils.format_current_2_disgit,""));
			
			   //get status from temp //more 1 status
			   h.setTargetMonth(o.getTargetMonth());
			   h.setTargetQuarter(o.getTargetQuarter());
			   h.setTargetYear(o.getTargetYear());
			   
			   h.setCustCatNo(o.getCustCatNo());
			   h.setSalesZone(o.getSalesZone());
			   h.setStatus(findStatusTEMPByTTADMIN(conn,h));
			  
			   items.add(h);
			}//while
			//set Result 
			o.setItems(items);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	
	public static SalesTargetBean searchSalesTargetTT(Connection conn,SalesTargetBean cri,boolean getItems,User user,String pageName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		SalesTargetBean h = null;
		try {
			cri = convertCriteria(cri);
			
			sql.append("\n   select M.id ,M.status as status_tt ");
			sql.append("\n  ,M.BRAND_GROUP,M.PERIOD ,Z.ZONE,Z.ZONE_NAME");
			sql.append("\n  ,M.status as status_tt ,M.brand ,M.CUSTOMER_CATEGORY ");
			sql.append("\n  ,(select brand_desc from PENSBI.XXPENS_BI_MST_BRAND B where B.brand_no = M.brand) as brand_name ");
			sql.append("\n  ,(select nvl(sum(amt_avg12),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as amt_avg12 ");
			sql.append("\n  ,(select nvl(sum(amt_avg3),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as amt_avg3 ");	
			sql.append("\n  ,(select nvl(sum(target_qty),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_qty ");
			sql.append("\n  ,(select nvl(sum(target_amount),0) from PENSBI.XXPENS_BI_SALES_TARGET_TT_L L where L.id=M.id) as total_target_amount ");
			
			sql.append("\n  ,target_month,target_quarter ,target_year ");
			sql.append("\n  from PENSBI.XXPENS_BI_SALES_TARGET_TT M ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  where M.zone = Z.zone ");
			if(cri.getId() !=0)
		       sql.append("\n  and M.id = "+cri.getId()+"");
			
			if( !Utils.isNull(cri.getCustCatNo()).equals(""))
				   sql.append("\n  and M.CUSTOMER_CATEGORY = '"+cri.getCustCatNo()+"'");
			
			if( !Utils.isNull(cri.getSalesZone()).equals(""))
				   sql.append("\n  and M.zone = '"+cri.getSalesZone()+"'");
			
			if( !Utils.isNull(cri.getBrand()).equals(""))
			   sql.append("\n  and M.brand = '"+cri.getBrand()+"'");
			
			if( !Utils.isNull(cri.getTargetMonth()).equals("")){
				sql.append("\n and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			}
			if( !Utils.isNull(cri.getTargetQuarter()).equals("")){
				sql.append("\n and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			}
			if( !Utils.isNull(cri.getTargetYear()).equals("")){
				sql.append("\n and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
			}
			//sql.append("\n  ORDER BY M. ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   h = new SalesTargetBean();
			   h.setId(rst.getLong("ID"));
			  
			   h.setSalesZone(Utils.isNull(rst.getString("zone"))); 
			   h.setSalesZoneDesc(Utils.isNull(rst.getString("zone_name"))); 
			   h.setBrand(Utils.isNull(rst.getString("brand"))); 
			   h.setBrandGroup(Utils.isNull(rst.getString("brand_group"))); 
			   h.setBrandName(Utils.isNull(rst.getString("brand_name"))); 
			   h.setCustCatNo(Utils.isNull(rst.getString("CUSTOMER_CATEGORY")));
			   
			   h.setTotalTargetQty(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_QTY"), Utils.format_current_no_disgit));
			   h.setTotalTargetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_AMOUNT"), Utils.format_current_2_disgit));
			   h.setTotalOrderAmt12Month(Utils.decimalFormat(rst.getDouble("AMT_AVG12"), Utils.format_current_2_disgit));
			   h.setTotalOrderAmt3Month(Utils.decimalFormat(rst.getDouble("AMT_AVG3"), Utils.format_current_2_disgit));
			 
			   h.setTargetMonth(Utils.isNull(rst.getString("target_month")));
			   h.setTargetQuarter(Utils.isNull(rst.getString("target_quarter")));
			   h.setTargetYear(Utils.isNull(rst.getString("target_year")));
			   
			   h.setPeriod(rst.getString("period"));
			   
			   h.setStatus(findStatusTEMPByTTSUPER(conn,h,rst.getString("status_tt")));
			   //get period
			   SalesTargetBean period = SalesTargetTTUtils.getPeriodList(conn,h.getPeriod()).get(0);//get Period View
			   if(period != null){
				   h.setStartDate(period.getStartDate());
				   h.setEndDate(period.getEndDate());
			   }
			   h.setPeriodDesc(cri.getPeriodDesc());
			   
			   //set can access
			   h = SalesTargetTTUtils.setAccess(h,user,pageName);
			   
			   //getItems
			   if(getItems)
			     h.setItems(searchSalesTargetDetailTT(conn, h.getId(),user,pageName));
			}//while
			
			if(h==null){
				h = cri;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return h;
	}
	
	public static List<SalesTargetBean> searchSalesTargetDetailTT(Connection conn,long id,User user,String pageName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<SalesTargetBean> items = new ArrayList<SalesTargetBean>();
		SalesTargetBean h = null;
		int rowId= 0;
		try {
			sql.append("\n  select");
			sql.append("\n  M.ID,M.LINE_ID,M.STATUS");
			sql.append("\n  ,M.INVENTORY_ITEM_ID  ");
			sql.append("\n  ,B.INVENTORY_ITEM_CODE ");
			sql.append("\n  ,B.INVENTORY_ITEM_DESC ");
			sql.append("\n  ,M.target_qty ");
			sql.append("\n  ,M.target_amount");
			sql.append("\n  ,M.remark ,M.reject_reason");
			sql.append("\n  ,M.AMT_AVG12,M.AMT_AVG3 ,M.PRICE");
			sql.append("\n  from XXPENS_BI_SALES_TARGET_TT_L M ,XXPENS_BI_MST_ITEM B");
			sql.append("\n  WHERE 1=1");
			sql.append("\n  AND B.INVENTORY_ITEM_ID = M.INVENTORY_ITEM_ID");
		    sql.append("\n  and M.ID = '"+id+"'");
			sql.append("\n  ORDER BY M.LINE_ID ASC ");

			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new SalesTargetBean();
			   rowId++;
			   h.setRowId(rowId);
			   h.setId(rst.getLong("ID"));
			   h.setLineId(rst.getLong("LINE_ID"));
			   h.setItemCode(rst.getString("INVENTORY_ITEM_CODE"));
			   h.setItemId(rst.getString("INVENTORY_ITEM_ID"));  
			   h.setItemName(Utils.isNull(rst.getString("INVENTORY_ITEM_DESC"))); 
			   h.setStatus(Utils.isNull(rst.getString("STATUS"))); 
			   h.setRemark(Utils.isNull(rst.getString("REMARK"))); 
			   h.setRejectReason(Utils.isNull(rst.getString("reject_reason"))); 
			   
			   h.setTargetQty(Utils.decimalFormat(rst.getDouble("TARGET_QTY"), Utils.format_current_no_disgit)); 
			   h.setTargetAmount(Utils.decimalFormat(rst.getDouble("TARGET_AMOUNT"), Utils.format_current_2_disgit)); 
			   
			   h.setOrderAmt12Month(Utils.decimalFormat(rst.getDouble("AMT_AVG12"), Utils.format_current_2_disgit)); 
			   h.setOrderAmt3Month(Utils.decimalFormat(rst.getDouble("AMT_AVG3"), Utils.format_current_2_disgit));  
			   h.setPrice(Utils.decimalFormat(rst.getDouble("PRICE"), Utils.format_current_2_disgit)); 
			   
			   //set can access
			   h = SalesTargetTTUtils.setAccess(h,user,pageName);
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
	
	public static List<SalesTargetBean> searchSalesTargetProductListTTSUPER(Connection conn,SalesTargetBean cri,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<SalesTargetBean> dataList = new ArrayList<SalesTargetBean>();
		SalesTargetBean h = null;
		try {
			cri = convertCriteria(cri);
			
			sql.append("\n  select D.INVENTORY_ITEM_CODE,D.INVENTORY_ITEM_ID ,D.target_qty,D.TARGET_AMOUNT");
			sql.append("\n  ,D.PRICE,D.AMT_AVG3,D.AMT_AVG12 ");
			sql.append("\n  ,(select INVENTORY_ITEM_desc from PENSBI.XXPENS_BI_MST_ITEM B ");
			sql.append("\n   where B.INVENTORY_ITEM_CODE = D.INVENTORY_ITEM_CODE) as INVENTORY_ITEM_DESC ");
            sql.append("\n  from PENSBI.XXPENS_BI_SALES_TARGET_TT M ,PENSBI.XXPENS_BI_SALES_TARGET_TT_L D");
			sql.append("\n  where D.id = M.id ");
			if( !Utils.isNull(cri.getCustCatNo()).equals(""))
				sql.append("\n  and M.CUSTOMER_CATEGORY = '"+cri.getCustCatNo()+"'");
			
			if( !Utils.isNull(cri.getSalesZone()).equals(""))
				sql.append("\n  and M.ZONE = '"+cri.getSalesZone()+"'");
			
			if( !Utils.isNull(cri.getBrand()).equals(""))
			   sql.append("\n  and M.brand = '"+cri.getBrand()+"'");
			
			if( !Utils.isNull(cri.getTargetMonth()).equals("")){
				sql.append("\n and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			}
			if( !Utils.isNull(cri.getTargetQuarter()).equals("")){
				sql.append("\n and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			}
			if( !Utils.isNull(cri.getTargetYear()).equals("")){
				sql.append("\n and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
			}
			sql.append("\n  ORDER BY D.INVENTORY_ITEM_CODE ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new SalesTargetBean();
			   h.setItemId(rst.getString("INVENTORY_ITEM_ID"));
			   h.setItemCode(rst.getString("INVENTORY_ITEM_CODE"));
			   h.setItemName(rst.getString("INVENTORY_ITEM_DESC"));
			   h.setPrice(Utils.decimalFormat(rst.getDouble("price"), Utils.format_current_2_disgit));
			   h.setTotalTargetQty(Utils.decimalFormat(rst.getDouble("TARGET_QTY"), Utils.format_current_no_disgit));
			   h.setTotalTargetAmount(Utils.decimalFormat(rst.getDouble("TARGET_AMOUNT"), Utils.format_current_2_disgit));
			   h.setTotalOrderAmt3Month(Utils.decimalFormat(rst.getDouble("AMT_AVG3"), Utils.format_current_2_disgit));
			   h.setTotalOrderAmt12Month(Utils.decimalFormat(rst.getDouble("AMT_AVG12"), Utils.format_current_2_disgit));

			   dataList.add(h);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return dataList;
	}
	
	//XXPENS_BI_SALES_TARGET_TEMP
	public static  List<Map> searchSalesTargetTempToMap(Connection conn,SalesTargetBean cri,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Map<String,SalesTargetBean> dataMap = new HashMap<String, SalesTargetBean>();
		Map<String,String> rowMap = new HashMap<String, String>();
		SalesTargetBean h = null;
		String keyMap = "";
		List<Map> dataList = new ArrayList<Map>();
		try {
			cri = convertCriteria(cri);
			
			sql.append("\n  select M.SALESREP_CODE ,D.ID ,D.LINE_ID");
			sql.append("\n  ,D.INVENTORY_ITEM_CODE ,D.target_qty,D.TARGET_AMOUNT");
			sql.append("\n  ,(select INVENTORY_ITEM_desc from PENSBI.XXPENS_BI_MST_ITEM B ");
			sql.append("\n    where B.INVENTORY_ITEM_CODE = D.INVENTORY_ITEM_CODE) as INVENTORY_ITEM_DESC ");
			sql.append("\n  from PENSBI.XXPENS_BI_SALES_TARGET_TEMP M ");
			sql.append("\n  ,PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L D");
			sql.append("\n  ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n  where D.id = M.id ");
			sql.append("\n  and M.salesrep_code = Z.salesrep_code ");
			sql.append("\n  and M.DIVISION ='B' ");//Van and Credit only
			if( !Utils.isNull(cri.getCustCatNo()).equals(""))
				sql.append("\n  and M.CUSTOMER_CATEGORY = '"+cri.getCustCatNo()+"'");
			
			if( !Utils.isNull(cri.getSalesZone()).equals(""))
				sql.append("\n  and Z.ZONE = '"+cri.getSalesZone()+"'");
			
			if( !Utils.isNull(cri.getBrand()).equals(""))
			   sql.append("\n  and M.brand = '"+cri.getBrand()+"'");
			
			if( !Utils.isNull(cri.getTargetMonth()).equals("")){
				sql.append("\n and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			}
			if( !Utils.isNull(cri.getTargetQuarter()).equals("")){
				sql.append("\n and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			}
			if( !Utils.isNull(cri.getTargetYear()).equals("")){
				sql.append("\n and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
			}
			sql.append("\n  ORDER BY D.INVENTORY_ITEM_CODE ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new SalesTargetBean();
			   h.setId(rst.getLong("id"));
			   h.setLineId(rst.getLong("line_id"));
			   h.setSalesrepCode(rst.getString("salesrep_code"));
			   h.setItemCode(rst.getString("INVENTORY_ITEM_CODE"));
			   h.setItemName(rst.getString("INVENTORY_ITEM_DESC"));
			   if(rst.getDouble("TARGET_QTY") != 0){
			       h.setTargetQty(Utils.decimalFormat(rst.getDouble("TARGET_QTY"), Utils.format_number_no_disgit));
			       h.setTargetAmount(Utils.decimalFormat(rst.getDouble("TARGET_AMOUNT"), Utils.format_current_2_disgit));
			   }else{
				   h.setTargetQty("");
				   h.setTargetAmount("");
			   }
			   keyMap ="target_qty_"+h.getItemCode()+"_"+h.getSalesrepCode();
               
			   dataMap.put(keyMap, h);
			   
			   //set id row map by salesrep
			   rowMap.put(h.getSalesrepCode(), String.valueOf(h.getId()));
			  
			}//while
			dataList.add(rowMap);
			dataList.add(dataMap);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return dataList;
	}
	
	public static List<SalesTargetBean> searchSalesrepListByTTSUPER(Connection conn,SalesTargetBean cri,User user,Map<String, String> rowMap) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<SalesTargetBean> dataList = new ArrayList<SalesTargetBean>();
		SalesTargetBean h = null;
		String totalAmountByBrand = "";
		try {
			cri = convertCriteria(cri);
			
			sql.append("\n  select M.code,M.salesrep_full_name,M.salesrep_id ,M.region ");
			sql.append("\n  ,( select max(cust_cat_desc) from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT S");
			sql.append("\n    where S.cust_cat_no = '"+cri.getCustCatNo()+"') as cust_cat_desc");
			sql.append("\n  from apps.xxpens_salesreps_v M ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  where M.salesrep_id = Z.salesrep_id and M.isactive ='Y'");
			
			if( Utils.isNull(cri.getCustCatNo()).equalsIgnoreCase("ORDER - VAN SALES")){
				sql.append("\n  and M.sales_channel = 'C'");
			}else if( Utils.isNull(cri.getCustCatNo()).equalsIgnoreCase("ORDER - CREDIT SALES")){
				sql.append("\n  and M.sales_channel = 'S'");
			}
			if(!Utils.isNull(cri.getSalesZone()).equals("")){
				sql.append("\n  and Z.zone = '"+cri.getSalesZone()+"'");
			}
			
			sql.append("\n  ORDER BY M.code ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   totalAmountByBrand = "";
			   h = new SalesTargetBean();
			   h.setSalesrepId(rst.getString("salesrep_id"));
			   h.setSalesrepCode(rst.getString("code"));
			   h.setSalesrepName(rst.getString("salesrep_full_name"));
			   h.setSalesChannelNo(rst.getString("region"));
			   h.setCustCatNo(cri.getCustCatNo());
			   h.setCustCatDesc(rst.getString("cust_cat_desc"));
			   
			   //logger.debug("salesrepCode["+h.getSalesrepCode()+"]id["++"]");
			   if(rowMap.get(h.getSalesrepCode()) != null){
				   h.setId(Utils.convertStrToLong(rowMap.get(h.getSalesrepCode()), 0));
			   }
			   //getTotalAmountBrandBySales
			   h.setPeriod(cri.getPeriod());
			   h.setTargetMonth(cri.getTargetMonth());
			   h.setTargetQuarter(cri.getTargetQuarter());
			   h.setTargetYear(cri.getTargetYear());
			   h.setSalesZone(cri.getSalesZone());
			   
			   totalAmountByBrand =getTotalAmountBrandBySales(conn, h,cri.getBrand());
			   h.setTotalAmountBrandBySale("");
			   //logger.debug("totalAmountByBrand["+totalAmountByBrand+"]");
			   if( !totalAmountByBrand.equals("0.00")){
			      h.setTotalAmountBrandBySale(totalAmountByBrand);
			   }
			  // logger.debug("totalAmountByBrand["+h.getTotalAmountBrandBySale()+"]");
			   dataList.add(h);
			}//while
			
			if(h==null){
				h = cri;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return dataList;
	}
	public static List<SalesTargetBean> searchSalesrepListByTTMGR(Connection conn,SalesTargetBean cri,User user,Map<String, String> rowMap) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<SalesTargetBean> dataList = new ArrayList<SalesTargetBean>();
		SalesTargetBean h = null;
		String totalAmountByBrand = "";
		try {
			cri = convertCriteria(cri);
			
			sql.append("\n  select M.code,M.salesrep_full_name,M.salesrep_id ,M.region ");
			sql.append("\n  ,( select max(cust_cat_desc) from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT S");
			sql.append("\n    where S.cust_cat_no = '"+cri.getCustCatNo()+"') as cust_cat_desc");
			sql.append("\n  from apps.xxpens_salesreps_v M ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  where M.salesrep_id = Z.salesrep_id and M.isactive ='Y'");
			sql.append("\n  and M.salesrep_full_name not like '%ยกเลิก%'");
			
			if( Utils.isNull(cri.getCustCatNo()).equalsIgnoreCase("ORDER - VAN SALES")){
				sql.append("\n  and M.sales_channel = 'C'");
			}else if( Utils.isNull(cri.getCustCatNo()).equalsIgnoreCase("ORDER - CREDIT SALES")){
				sql.append("\n  and M.sales_channel = 'S'");
			}
			if(!Utils.isNull(cri.getSalesZone()).equals("")){
				sql.append("\n  and Z.zone = '"+cri.getSalesZone()+"'");
			}
			
			sql.append("\n  ORDER BY M.code ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   totalAmountByBrand = "";
			   h = new SalesTargetBean();
			   h.setSalesrepId(rst.getString("salesrep_id"));
			   h.setSalesrepCode(rst.getString("code"));
			   h.setSalesrepName(rst.getString("salesrep_full_name"));
			   h.setSalesChannelNo(rst.getString("region"));
			   h.setCustCatNo(cri.getCustCatNo());
			   h.setCustCatDesc(rst.getString("cust_cat_desc"));
			   
			   //logger.debug("salesrepCode["+h.getSalesrepCode()+"]id["++"]");
			   if(rowMap.get(h.getSalesrepCode()) != null){
				   h.setId(Utils.convertStrToLong(rowMap.get(h.getSalesrepCode()), 0));
			   }
			   //getTotalAmountBrandBySales
			   h.setPeriod(cri.getPeriod());
			   h.setTargetMonth(cri.getTargetMonth());
			   h.setTargetQuarter(cri.getTargetQuarter());
			   h.setTargetYear(cri.getTargetYear());
			   h.setSalesZone(cri.getSalesZone());
			   
			   totalAmountByBrand = getTotalAmountBrandBySales(conn, h,"");
			   h.setTotalAmountBrandBySale("");
			   if( !totalAmountByBrand.equals("0.00")){
			      h.setTotalAmountBrandBySale(totalAmountByBrand);
			   }
			   dataList.add(h);
			}//while
			
			if(h==null){
				h = cri;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return dataList;
	}
	public static  String getTotalAmountBrandBySales(Connection conn,SalesTargetBean cri,String notInBrand) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String totalAmountBrandBySale = "";
		try {
			sql.append("\n  select NVL(SUM(D.TARGET_AMOUNT),0) as TARGET_AMOUNT");
			sql.append("\n  from PENSBI.XXPENS_BI_SALES_TARGET_TEMP M");
			sql.append("\n  ,PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L D");
			sql.append("\n  ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  where M.salesrep_id= Z.salesrep_id ");
			sql.append("\n  and M.id = D.id ");
			sql.append("\n  and M.CUSTOMER_CATEGORY = '"+cri.getCustCatNo()+"'");
			sql.append("\n  and M.salesrep_id = "+cri.getSalesrepId()+"");
		    sql.append("\n  and Z.zone = '"+cri.getSalesZone()+"'");
			sql.append("\n  and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			sql.append("\n  and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
		    sql.append("\n  and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
		    
		    if( !Utils.isNull(notInBrand).equals("")){
			    sql.append("\n  and M.brand <> '"+notInBrand+"'");
			}
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   totalAmountBrandBySale= Utils.decimalFormat(rst.getDouble("TARGET_AMOUNT"), Utils.format_current_2_disgit);
			}//while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalAmountBrandBySale;
	}
	
	public static String getAllStatusInItem(Connection conn,long id,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String allStatus = "";
		try {
			sql.append("\n  select DISTINCT M.STATUS");
			sql.append("\n  from XXPENS_BI_SALES_TARGET_TT_L M ");
			sql.append("\n  WHERE 1=1");
		    sql.append("\n  and M.ID = '"+id+"'");
		    sql.append("\n  order by status ");
		    
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				allStatus +=Utils.isNull(rst.getString("STATUS"))+"/"; 
			}//while
			if(allStatus.length() >0){
				allStatus = allStatus.substring(0,allStatus.length()-1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return allStatus;
	}
		
	public static SalesTargetBean convertCriteria(SalesTargetBean o) throws Exception{
		logger.debug("startDate:"+o.getStartDate());
		Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MMM_YYYY);
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		//set Month
		String month =String.valueOf(c.get(Calendar.MONTH)+1);
		o.setTargetMonth(month.length()==1?"0"+month:month);
        //Set Year
		o.setTargetYear(c.get(Calendar.YEAR)+"");
		//set quarter
		if(Integer.parseInt(month) >= 1&& Integer.parseInt(month) <=3){
			o.setTargetQuarter("1");
		}else if(Integer.parseInt(month) >= 4&& Integer.parseInt(month) <=6){
			o.setTargetQuarter("2");
		}else if(Integer.parseInt(month) >= 7&& Integer.parseInt(month) <=9){
			o.setTargetQuarter("3");
		}else{
			o.setTargetQuarter("4");
		}
		
		return o;
	}
	
	/**
	 * Save data By Role User
	 * @param h
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static SalesTargetBean save(Connection conn,SalesTargetBean h,User user) throws Exception{
		try{
			if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.MKT,User.ADMIN}) ){
				h = saveModelByMKT_TT(conn,h);
			}
			return h;
		}catch(Exception e){
			throw e;
		}finally{
		}
	}
	
	/** delete product in XXPENS_BI_SALES_TARGET_TEMP_L where product_id not in(XXPENS_BI_SALES_TARGET_TT_L) */
	public static int deleteProductByMKTDel(Connection conn,SalesTargetBean cri,User user){
		int maxLineId = 0;
		Statement stmt = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n delete from PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L D");
			sql.append("\n where D.id in(");
			sql.append("\n  select M.id from PENSBI.XXPENS_BI_SALES_TARGET_TEMP M ");
			sql.append("\n  ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  where M.salesrep_id = Z.salesrep_id");
			sql.append("\n  and M.division ='B'");//van and credit
			sql.append("\n  and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			sql.append("\n  and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			sql.append("\n  and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
            sql.append("\n  and M.brand = '"+cri.getBrand()+"'");
			sql.append("\n  and M.CUSTOMER_CATEGORY = '"+Utils.isNull(cri.getCustCatNo())+"'");
			sql.append("\n  and Z.zone = '"+Utils.isNull(cri.getSalesZone())+"'");
			sql.append("\n ) ");
			// item_code not in TT_L 
			sql.append("\n and D.inventory_item_code not in (");
			sql.append("\n   select DD.inventory_item_code from PENSBI.XXPENS_BI_SALES_TARGET_TT_L DD");
			sql.append("\n   where DD.id in(");
			sql.append("\n     select M.id from PENSBI.XXPENS_BI_SALES_TARGET_TT M ");
			sql.append("\n     where M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			sql.append("\n     and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			sql.append("\n     and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
            sql.append("\n     and M.brand = '"+cri.getBrand()+"'");
			sql.append("\n     and M.CUSTOMER_CATEGORY = '"+Utils.isNull(cri.getCustCatNo())+"'");
			sql.append("\n     and M.zone = '"+Utils.isNull(cri.getSalesZone())+"'");
			sql.append("\n    ) ");
			sql.append("\n ) ");
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			
			int r = stmt.executeUpdate(sql.toString());
			logger.debug("total rec del:"+r);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {}
		}
	 return maxLineId;
	}
	
	public static SalesTargetBean saveModelByMKT_TT(Connection conn,SalesTargetBean h) throws Exception{
        logger.debug("saveModelByMKT id["+h.getId()+"]");
        int lineId = 0;
		try{
			//check documentNo
			if(h.getId()==0){
				//Gen Next ID Sequence
				Integer id =SequenceProcessAll.getIns().getNextValue("XXPENS_BI_SALES_TARGET_TT");
				if(id==0){
					id = 1;
				}
				h.setId(id);
				
				logger.debug("**** Start Insert New ID:"+h.getId()+"****");
				//save Head
				insertHeadByMKT_TT(conn, h);
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   SalesTargetBean l = (SalesTargetBean)h.getItems().get(i);
					 //  logger.debug("itemCode["+l.getItemCode()+"]status["+l.getStatus()+"]");
					   if( !l.getStatus().equals("DELETE")){
						   l.setId(h.getId());
						   l.setUserInputId(h.getUserInputId());
						   lineId++;
						   l.setLineId(lineId);
						   insertItemByMKT_TT(conn, l);
					   }
				   }
				}
			}else{
				logger.debug("****Start Update ID:"+h.getId()+"****");
				//Get MaxLine ID 
				int maxLineId = getMaxLineTTID(conn, h);
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   SalesTargetBean l = (SalesTargetBean)h.getItems().get(i);
					   l.setId(h.getId());
					   l.setUserInputId(h.getUserInputId());
					   logger.debug("Line ID:"+l.getLineId());
					   if(l.getLineId() ==0){
	                        //get next line id =(max_line_id+1);
						    maxLineId++;
						    l.setLineId(maxLineId);
						    logger.debug("Insert new LineID:"+l.getLineId());
							insertItemByMKT_TT(conn, l);
					   }else{
                           if(l.getStatus().equals("DELETE")){
                        	   logger.debug("DELETE LineID:"+l.getLineId());
							   //delete item
                        	   deleteItemByMKT_TT(conn, l);
						   }else{
							   logger.debug("UPDATE LineID:"+l.getLineId());
						      //update Item by LineId
						      updateItemByMKT_TT(conn,l);
						   }//if
					   }//if
				   }//for
				}//if
			}//if
			return h;
		}catch(Exception e){
		  throw e;
		}finally{
		}
	}
	
	//XXPENS_BI_SALES_TARGET_TEMP
	/** Save By TTSUPER (OLD) **/
	public static boolean saveModelByTTSUPER_TT(Connection conn,List<SalesTargetBean> salesrepDataSaveList) throws Exception{
        logger.debug("saveModelByTTSUPER_TT ");
        SalesTargetBean h = null;
        int lineId = 0;
		try{
			for(int r=0;r<salesrepDataSaveList.size();r++){
				h = salesrepDataSaveList.get(r);
				
				//check id is Exist
				if(h.getId()==0 ){
					//convert to target month year quarter
					h = convertCriteria(h);
					long idHeadExist = getIdTargetTempHead(conn, h);
				    logger.debug("idHeadExist:"+idHeadExist);
				    if(idHeadExist != 0){
				       h.setId(idHeadExist);
				    }
				}
				logger.debug("h.id="+h.getId());
				
				if(h.getId()==0 ){
					//Gen Next ID Sequence
					Integer id =SequenceProcessAll.getIns().getNextValue("XXPENS_BI_SALES_TARGET_TEMP");
					if(id==0){
						id = 1;
					}
					h.setId(id);
					
					logger.debug("**** Start Insert New ID:"+h.getId()+"****");
					//convert to target month year quarter
					h = convertCriteria(h);
					
					//check duplicate all case (TTSUPPER duplicate no reason )
					/*if(isTargetTempHeadDup(conn, h)){
						throw new DataDuplicateException("DataDuplicate :please check");
					}*/
					
					//save Head
					insertXXPENS_BI_SALES_TARGET_TEMP_ByTTSUPER(conn, h);
					//save line
					if(h.getItems() != null && h.getItems().size()>0){
					   for(int i=0;i<h.getItems().size();i++){
						   SalesTargetBean l = (SalesTargetBean)h.getItems().get(i);
						 //  logger.debug("itemCode["+l.getItemCode()+"]status["+l.getStatus()+"]");
						   if( !Utils.isNull(l.getStatus()).equals("DELETE")){
							   l.setId(h.getId());
							   l.setUserInputId(h.getUserInputId());
							   lineId++;
							   l.setLineId(lineId);
							   insertXXPENS_BI_SALES_TARGET_TEMP_L_ByTTSUPER(conn, l);
						   }
					   }
					}
				}else{
					logger.debug("****Start Update ID:"+h.getId()+"****");
					//save line
					if(h.getItems() != null && h.getItems().size()>0){
					   //Get MaxLine ID 
					   int maxLineId = getMaxLineIDByTTSUPER(conn, h);
						
					   for(int i=0;i<h.getItems().size();i++){
						   SalesTargetBean l = (SalesTargetBean)h.getItems().get(i);
						   l.setId(h.getId());
						   l.setUserInputId(h.getUserInputId());
						   logger.debug("Line ID:"+l.getLineId());
						   if(l.getLineId() ==0){
		                        //get next line id =(max_line_id+1);
							    maxLineId++;
							    l.setLineId(maxLineId);
							    logger.debug("Insert new LineID:"+l.getLineId());
							    insertXXPENS_BI_SALES_TARGET_TEMP_L_ByTTSUPER(conn, l);
						   }else{
								logger.debug("UPDATE LineID:"+l.getLineId());
							    //update Item by LineId
							    updateXXPENS_BI_SALES_TARGET_TEMP_L_ByTTSUPER(conn,l);
						   }//if
					   }//for
					}//if
				}//if
			}
			return true;
		}catch(Exception e){
		  throw e;
		}finally{
		}
	}
	
	/** Save By TTSUPER (NEW) :21/11/2019**/
	public static boolean saveModelByTTSUPER_TT_BK(Connection conn,List<SalesTargetBean> salesrepDataSaveList) throws Exception{
        logger.debug("saveModelByTTSUPER_TT ");
        SalesTargetBean h = null;
        int lineId = 0;
		try{
			for(int r=0;r<salesrepDataSaveList.size();r++){
				h = salesrepDataSaveList.get(r);
				//check documentNo
				if(h.getId()==0){
					//Gen Next ID Sequence
					Integer id =SequenceProcessAll.getIns().getNextValue("XXPENS_BI_SALES_TARGET_TEMP");
					if(id==0){
						id = 1;
					}
					h.setId(id);
					
					logger.debug("**** Start Insert New ID:"+h.getId()+"****");
					//convert to target month year quarter
					h = convertCriteria(h);
					
					//save Head
					insertXXPENS_BI_SALES_TARGET_TEMP_ByTTSUPER(conn, h);
					//save line
					if(h.getItems() != null && h.getItems().size()>0){
					   for(int i=0;i<h.getItems().size();i++){
						   SalesTargetBean l = (SalesTargetBean)h.getItems().get(i);
						 //  logger.debug("itemCode["+l.getItemCode()+"]status["+l.getStatus()+"]");
						   if( !Utils.isNull(l.getStatus()).equals("DELETE")){
							   l.setId(h.getId());
							   lineId++;
							   l.setLineId(lineId);
							   insertXXPENS_BI_SALES_TARGET_TEMP_L_ByTTSUPER(conn, l);
						   }
					   }
					}
				}else{
					logger.debug("****Start Update ID:"+h.getId()+"****");
					
					//update Head (update_date,update_user)
					updateXXPENS_BI_SALES_TARGET_TEMP_ByTTSUPER(conn, h);
					
					//delete all line by head_id
					deleteXXPENS_BI_SALES_TARGET_TEMP_L_ByTTSUPER(conn, h.getId());
					
					//save line new all
					if(h.getItems() != null && h.getItems().size()>0){
					   for(int i=0;i<h.getItems().size();i++){
						   SalesTargetBean l = (SalesTargetBean)h.getItems().get(i);
						   //logger.debug("itemCode["+l.getItemCode()+"]status["+l.getStatus()+"]");
						   if( !Utils.isNull(l.getStatus()).equals("DELETE")){
							   l.setId(h.getId());
							   lineId++;
							   l.setLineId(lineId);
							   insertXXPENS_BI_SALES_TARGET_TEMP_L_ByTTSUPER(conn, l);
						   }//if
					   }//for
					}//if
				}//if
			}
			return true;
		}catch(Exception e){
		  throw e;
		}finally{
		}
	}
	
	/**
	 * check SalesTargetTemp is Duplicate
	 * @param conn
	 * @param cri
	 * @return
	 */
	public static long getIdTargetTempHead(Connection conn,SalesTargetBean cri){
		long id = 0;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n select distinct id ");
			sql.append("\n from PENSBI.XXPENS_BI_SALES_TARGET_TEMP M ");
			sql.append("\n where 1=1");
			sql.append("\n and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			sql.append("\n and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			sql.append("\n and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
			sql.append("\n and M.division = '"+Utils.isNull(cri.getDivision())+"' ");//B = credit,van sales
			sql.append("\n and M.brand = '"+cri.getBrand()+"'");
			sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(cri.getCustCatNo())+"'");
			sql.append("\n and M.salesrep_id = "+Utils.isNull(cri.getSalesrepId())+"");
			
			//sql.append("\n and M.sales_channel = '"+Utils.isNull(cri.getSalesChannelNo())+"'");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				id= rst.getLong("id");
			}//if
			logger.debug("ID_TargetTempHeadDup -->>"+id);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return id;
	}
	
	/**
	 * getMaxLineID
	 * @param conn
	 * @param o
	 * @return maxLineID
	 */
	public static int getMaxLineTTID(Connection conn,SalesTargetBean o){
		int maxLineId = 0;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n SELECT MAX(M.LINE_ID) AS MAX_LINE_ID from XXPENS_BI_SALES_TARGET_TT_L M  ");
			sql.append("\n where 1=1  ");
			sql.append("\n and ID ="+o.getId()+"");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				maxLineId = rst.getInt("MAX_LINE_ID");
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return maxLineId;
	}
	 
	public static int getMaxLineIDByTTSUPER(Connection conn,SalesTargetBean o){
		int maxLineId = 0;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n SELECT MAX(M.LINE_ID) AS MAX_LINE_ID from XXPENS_BI_SALES_TARGET_TEMP_L M  ");
			sql.append("\n where 1=1  ");
			sql.append("\n and ID ="+o.getId()+"");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				maxLineId = rst.getInt("MAX_LINE_ID");
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return maxLineId;
	}
	
	public static String findStatusTEMPByTTSUPER(Connection conn,SalesTargetBean o,String status_tt){
		String status = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Map<String, String> mapStatus = new HashMap<String, String>();
		try{
			sql.append("\n SELECT STATUS from PENSBI.XXPENS_BI_SALES_TARGET_TEMP M  ");
			sql.append("\n ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n where 1=1  ");
			sql.append("\n and m.salesrep_id = Z.salesrep_id  ");
			sql.append("\n and M.period = '"+Utils.isNull(o.getPeriod())+"'");
			sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
			sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
			sql.append("\n and Z.zone= '"+Utils.isNull(o.getSalesZone())+"'");
			sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
			sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
			sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
			
			//logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				if(mapStatus.get(rst.getString("STATUS")) ==null){
				   status += rst.getString("STATUS")+"/";
				}
				mapStatus.put(rst.getString("STATUS"), rst.getString("STATUS"));
			}//while
			if(status.length()>0){
				status = status.substring(0,status.length()-1);
			}
			if(Utils.isNull(status).equals("")){
				status = Utils.isNull(status_tt);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return status;
	}
	public static String findStatusTEMPByTTADMIN(Connection conn,SalesTargetBean o){
		String status = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Map<String, String> mapStatus = new HashMap<String, String>();
		try{
			sql.append("\n SELECT distinct STATUS ");
			sql.append("\n from PENSBI.XXPENS_BI_SALES_TARGET_TT M ");
			sql.append("\n where 1=1  ");
			sql.append("\n and M.period = '"+Utils.isNull(o.getPeriod())+"'");
			sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
			sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
			sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
			if(!"".equals(Utils.isNull(o.getCustCatNo()))){
			  sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			if(!"".equals(Utils.isNull(o.getBrand()))){
			  sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
			}
			if(!"".equals(Utils.isNull(o.getSalesZone()))){
			  sql.append("\n and M.zone= '"+Utils.isNull(o.getSalesZone())+"'");
			}
			sql.append("\n UNION ");
			
			sql.append("\n SELECT distinct STATUS ");
			sql.append("\n from PENSBI.XXPENS_BI_SALES_TARGET_TEMP M ");
			sql.append("\n ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z"); 
			sql.append("\n where 1=1  ");
			sql.append("\n and M.salesrep_id = Z.salesrep_id  ");
			sql.append("\n and M.division ='B' ");// Van And Credit
			sql.append("\n and M.period = '"+Utils.isNull(o.getPeriod())+"'");
			sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
			sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
			sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
			if(!"".equals(Utils.isNull(o.getCustCatNo()))){
			  sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			if(!"".equals(Utils.isNull(o.getBrand()))){
			  sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
			}
			if(!"".equals(Utils.isNull(o.getSalesZone()))){
			  sql.append("\n and Z.zone= '"+Utils.isNull(o.getSalesZone())+"'");
			}
			 
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				if(mapStatus.get(rst.getString("STATUS")) ==null){
				   status += rst.getString("STATUS")+"/";
				}
				mapStatus.put(rst.getString("STATUS"), rst.getString("STATUS"));
			}//while
			if(status.length()>0){
				status = status.substring(0,status.length()-1);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return status;
	}
	 public static void insertHeadByMKT_TT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			logger.debug("insertHeadByMKT_TT ID["+o.getId()+"]");
			logger.debug("customerId:"+o.getCustomerId());
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO XXPENS_BI_SALES_TARGET_TT \n");
				sql.append(" (ID, TARGET_MONTH,  \n");
				sql.append("  TARGET_QUARTER, TARGET_YEAR, CUSTOMER_CATEGORY, ZONE, BRAND, \n");
				sql.append("  BRAND_GROUP, STATUS, CREATE_USER, CREATE_DATE,PERIOD,USER_INPUT_ID)  \n");
			    sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setLong(c++, o.getId());
				ps.setString(c++, o.getTargetMonth());
				ps.setString(c++, o.getTargetQuarter());
				ps.setString(c++, o.getTargetYear());
				ps.setString(c++, o.getCustCatNo());
				ps.setString(c++, o.getSalesZone());
				ps.setString(c++, o.getBrand());
				ps.setString(c++, o.getBrandGroup());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getCreateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getPeriod());
				ps.setLong(c++, o.getUserInputId());
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
		public static void updateStatusHead_TEMPByTTSUPER(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("UpdateStatusHeadByMKT_TT ID["+o.getId()+"]status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
		        //convert Criteria
                o = convertCriteria(o);
                
				//XXPENS_BI_SALES_TARGET_TEMP
				sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP M SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE 1=1  \n" );
			    sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				sql.append("\n and M.salesrep_id in( ");
				sql.append("\n   select salesrep_id from PENSBI.XXPENS_BI_MST_SALES_ZONE");
				sql.append("\n   where zone= '"+Utils.isNull(o.getSalesZone())+"'");
				sql.append("\n )");
				sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");

				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		public static void updateStatusHead_TEMPByTTADMIN(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusHead_TEMPByTTADMIN status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
		        //convert Criteria
                o = convertCriteria(o);
                
				//XXPENS_BI_SALES_TARGET_TEMP
				sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP M SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE 1=1  \n" );
				sql.append("\n and M.division ='B' ");//Van And Credit
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
				if(!"".equals(Utils.isNull(o.getCustCatNo()))){
					sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				}
				if(!"".equals(Utils.isNull(o.getBrand()))){
					sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				}
				if(!"".equals(Utils.isNull(o.getSalesZone()))){
					sql.append("\n and M.salesrep_id in( ");
				    sql.append("\n   select salesrep_id from PENSBI.XXPENS_BI_MST_SALES_ZONE");
					sql.append("\n   where zone= '"+Utils.isNull(o.getSalesZone())+"'");
				    sql.append("\n )");
				}
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		public static void updateStatusItem_TEMPByTTSUPER(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("UpdateStatusItemByMKT ID["+o.getId()+"] status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
				//convert Criteria
                o = convertCriteria(o);
                
				//XXPENS_BI_SALES_TARGET_TEMP_L
				sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP_L  SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE ID IN( \n" );
				sql.append("    SELECT ID FROM  XXPENS_BI_SALES_TARGET_TEMP M \n");
				sql.append("    WHERE 1=1  " );
				sql.append("\n  and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				sql.append("\n  and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				sql.append("\n  and M.salesrep_id in( ");
				sql.append("\n     select salesrep_id from PENSBI.XXPENS_BI_MST_SALES_ZONE");
				sql.append("\n     where zone= '"+Utils.isNull(o.getSalesZone())+"'");
				sql.append("\n   )");
				sql.append("\n  and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n  and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n  and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
				sql.append(" )  \n" );
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		public static void updateStatusItem_TEMPByTTADMIN(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusItem_TEMPByTTADMIN status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
				//convert Criteria
                o = convertCriteria(o);
                
				//XXPENS_BI_SALES_TARGET_TEMP_L
				sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP_L  SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE ID IN( \n" );
				sql.append("    SELECT ID FROM  XXPENS_BI_SALES_TARGET_TEMP M \n");
				sql.append("    WHERE 1=1  " );
				sql.append("\n  and M.division ='B' ");//Van And Credit
				sql.append("\n  and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n  and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n  and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");

				if(!"".equals(Utils.isNull(o.getCustCatNo()))){
					sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				}
				if(!"".equals(Utils.isNull(o.getBrand()))){
					sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				}
				if(!"".equals(Utils.isNull(o.getSalesZone()))){
					sql.append("\n and M.salesrep_id in( ");
				    sql.append("\n   select salesrep_id from PENSBI.XXPENS_BI_MST_SALES_ZONE");
					sql.append("\n   where zone= '"+Utils.isNull(o.getSalesZone())+"'");
				    sql.append("\n )");
				}
				sql.append(" )  \n" );
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		public static void updateStatusHead_TEMPByTTMGR(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusHead_TEMPByTTMGR status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
				o = convertCriteria(o);
				//XXPENS_BI_SALES_TARGET_TEMP
			
				sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP M SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE 1=1  \n" );
			    sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				sql.append("\n and M.salesrep_id in( ");
				sql.append("\n   select salesrep_id from PENSBI.XXPENS_BI_MST_SALES_ZONE");
				sql.append("\n   where zone= '"+Utils.isNull(o.getSalesZone())+"'");
				sql.append("\n )");
				sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");

				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
		public static void updateStatusItem_TEMPByTTMGR(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusItem_TEMPByTTMGR status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
				//XXPENS_BI_SALES_TARGET_TEMP_L
				sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP_L  SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE ID IN( \n" );
				sql.append("    SELECT ID FROM  XXPENS_BI_SALES_TARGET_TEMP M \n");
				sql.append("    WHERE 1=1  " );
				sql.append("\n  and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				sql.append("\n  and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				sql.append("\n  and M.salesrep_id in( ");
				sql.append("\n     select salesrep_id from PENSBI.XXPENS_BI_MST_SALES_ZONE");
				sql.append("\n     where zone= '"+Utils.isNull(o.getSalesZone())+"'");
				sql.append("\n   )");
				sql.append("\n  and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n  and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n  and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
				sql.append(" )  \n" );
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
		public static void updateStatusHead_TTByMKT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusHead_TTByMKT ID["+o.getId()+"]status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
		        //convert Criteria
                o = convertCriteria(o);
                
				//XXPENS_BI_SALES_TARGET_TT
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TT M SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE 1=1  \n" );
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				sql.append("\n and M.zone= '"+Utils.isNull(o.getSalesZone())+"'");
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		public static void updateStatusHead_TTByAdmin(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusHead_TTByAdmin status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
		        //convert Criteria
                o = convertCriteria(o);
                
				//XXPENS_BI_SALES_TARGET_TT
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TT M SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE 1=1  \n" );
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
				
				if(!"".equals(Utils.isNull(o.getCustCatNo()))){
				  sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				}
				if(!"".equals(Utils.isNull(o.getBrand()))){
				  sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				}
				if(!"".equals(Utils.isNull(o.getSalesZone()))){
				  sql.append("\n and M.zone= '"+Utils.isNull(o.getSalesZone())+"'");
				}
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		public static void updateStatusItem_TTByMKT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("UpdateStatusItemByMKT ID["+o.getId()+"] status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
				//XXPENS_BI_SALES_TARGET_TT_L
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TT_L SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE ID IN( \n" );
				sql.append("  SELECT ID FROM  XXPENS_BI_SALES_TARGET_TT M \n");
				sql.append("  WHERE 1=1  \n" );
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				sql.append("\n and M.zone= '"+Utils.isNull(o.getSalesZone())+"'");
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
				
				sql.append(" )  \n" );
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
		public static void updateStatusItem_TTByAdmin(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("UpdateStatusItemByTTADMIN  status["+o.getStatus()+"]");
			int  c = 1;
			StringBuffer sql = new StringBuffer("");
			try{
				//XXPENS_BI_SALES_TARGET_TT_L
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TT_L SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE ID IN( \n" );
				sql.append("  SELECT ID FROM  XXPENS_BI_SALES_TARGET_TT M \n");
				sql.append("  WHERE 1=1  \n" );
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
				
				if(!"".equals(Utils.isNull(o.getCustCatNo()))){
					sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				}
				if(!"".equals(Utils.isNull(o.getBrand()))){
					sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				}
				if(!"".equals(Utils.isNull(o.getSalesZone()))){
					sql.append("\n and M.zone= '"+Utils.isNull(o.getSalesZone())+"'");
				}
				sql.append(" )  \n" );
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		/**
		 * TO TABLE XXPENS_BI_SALES_TARGET_TEMP
		 * @param conn
		 * @param o
		 * @throws Exception
		 */
		 public static void insertXXPENS_BI_SALES_TARGET_TEMP_ByTTSUPER(Connection conn,SalesTargetBean o) throws Exception{
				PreparedStatement ps = null;
				int c =1;
				logger.debug("insertXXPENS_BI_SALES_TARGET_TEMPByTTSUPER ID["+o.getId()+"]");
				logger.debug("customerId:"+o.getCustomerId());
				try{
					StringBuffer sql = new StringBuffer("");
					sql.append(" INSERT INTO XXPENS_BI_SALES_TARGET_TEMP \n");
					sql.append(" (ID, CUSTOMER_ID, CUSTOMER_CODE,  \n");
					sql.append("  SALESREP_ID, SALESREP_CODE, TARGET_MONTH,  \n");
					sql.append("  TARGET_QUARTER, TARGET_YEAR, CUSTOMER_CATEGORY,  \n");
					sql.append("  DIVISION, SALES_CHANNEL, BRAND, \n");
					sql.append("  BRAND_GROUP, STATUS, CREATE_USER, CREATE_DATE,PERIOD,CUSTOMER_GROUP,SESSION_ID,USER_INPUT_ID)  \n");
				    sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
					
					ps = conn.prepareStatement(sql.toString());
					
					ps.setLong(c++, o.getId());
					ps.setBigDecimal(c++, new BigDecimal("0"));
					ps.setString(c++,"");
					ps.setBigDecimal(c++, new BigDecimal(o.getSalesrepId()));
					ps.setString(c++, o.getSalesrepCode());
					ps.setString(c++, o.getTargetMonth());
					ps.setString(c++, o.getTargetQuarter());
					ps.setString(c++, o.getTargetYear());
					ps.setString(c++, o.getCustCatNo());
					ps.setString(c++, o.getDivision());
					ps.setString(c++, o.getSalesChannelNo());
					ps.setString(c++, o.getBrand());
					ps.setString(c++, o.getBrandGroup());
					ps.setString(c++, o.getStatus());
					ps.setString(c++, o.getCreateUser());
					ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
					ps.setString(c++, o.getPeriod());
					ps.setString(c++, o.getCustomerGroup());
					ps.setString(c++, o.getSessionId());
					ps.setLong(c++, o.getUserInputId());
					ps.executeUpdate();
					
				}catch(Exception e){
					throw e;
				}finally{
					if(ps != null){
						ps.close();ps=null;
					}
				}
			}
		 
		 public static void updateXXPENS_BI_SALES_TARGET_TEMP_ByTTSUPER(Connection conn,SalesTargetBean o) throws Exception{
				PreparedStatement ps = null;
				int c =1;
				logger.debug("insertXXPENS_BI_SALES_TARGET_TEMPByTTSUPER ID["+o.getId()+"]");
				logger.debug("customerId:"+o.getCustomerId());
				try{
					StringBuffer sql = new StringBuffer("");
					sql.append(" UPDATE PENSBI.XXPENS_BI_SALES_TARGET_TEMP \n");
					sql.append(" SET UPDATE_DATE =? ,UPDATE_USER =? ,SESSION_ID =?  \n");
				    sql.append(" WHERE ID = ? \n");
					
					ps = conn.prepareStatement(sql.toString());
					
				
					ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
					ps.setString(c++, o.getCreateUser());
					ps.setString(c++, o.getSessionId());
					ps.setLong(c++, o.getId());
					ps.executeUpdate();
					
				}catch(Exception e){
					throw e;
				}finally{
					if(ps != null){
						ps.close();ps=null;
					}
				}
			}
		 
		 public static void insertXXPENS_BI_SALES_TARGET_TEMP_L_ByTTSUPER(Connection conn,SalesTargetBean o) throws Exception{
				PreparedStatement ps = null;
				int c =1;
				logger.debug("insertXXPENS_BI_SALES_TARGET_TEMP_LByTTSUPER ID["+o.getId()+"] LINE_ID["+o.getLineId()+"]itemId["+o.getItemId()+"]");
				try{
				
					StringBuffer sql = new StringBuffer("");
					sql.append(" INSERT INTO XXPENS_BI_SALES_TARGET_TEMP_L \n");
					sql.append(" (ID,LINE_ID, INVENTORY_ITEM_ID, INVENTORY_ITEM_CODE,\n");
					sql.append(" TARGET_QTY, TARGET_AMOUNT, STATUS,  \n");
					sql.append(" REMARK, REJECT_REASON, CREATE_USER, CREATE_DATE,AMT_AVG12,AMT_AVG3,PRICE,SESSION_ID,USER_INPUT_ID)  \n");
				    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?) \n");
					
					ps = conn.prepareStatement(sql.toString());
					
					ps.setLong(c++, o.getId());
					ps.setLong(c++, o.getLineId());
					ps.setBigDecimal(c++, new BigDecimal(o.getItemId()));
					ps.setString(c++, o.getItemCode());
					ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetQty())));
					ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetAmount())));
					ps.setString(c++, o.getStatus());
					ps.setString(c++, o.getRemark());
					ps.setString(c++, o.getRejectReason());
					ps.setString(c++, o.getCreateUser());
					ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
					ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getOrderAmt12Month())));
					ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getOrderAmt3Month())));
					ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getPrice())));
					ps.setString(c++, o.getSessionId());
					ps.setLong(c++, o.getUserInputId());
					ps.executeUpdate();
				}catch(Exception e){
					throw e;
				}finally{
					if(ps != null){
						ps.close();ps=null;
					}
				}
			}
		 public static void updateXXPENS_BI_SALES_TARGET_TEMP_L_ByTTSUPER(Connection conn,SalesTargetBean o) throws Exception{
				PreparedStatement ps = null;
				int c =1;
				//logger.debug("updateItemByMKT ID["+o.getId()+"] LINE_ID["+o.getLineId()+"]");
				try{
				
					StringBuffer sql = new StringBuffer("");
					sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP_L \n");
					sql.append(" SET TARGET_QTY =?, TARGET_AMOUNT =?, REMARK =?  \n");
					sql.append(" ,UPDATE_USER =?, UPDATE_DATE=? ,SESSION_ID =? ,USER_INPUT_ID =?  \n");
				    sql.append(" WHERE ID =? AND LINE_ID = ?\n");
					
					ps = conn.prepareStatement(sql.toString());
					
					ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetQty())));
					ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetAmount())));
					ps.setString(c++, o.getRemark());
					ps.setString(c++, o.getCreateUser());
					ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
					ps.setString(c++, o.getSessionId());
					ps.setLong(c++, o.getUserInputId());
					
					ps.setLong(c++, o.getId());
					ps.setLong(c++, o.getLineId());

					ps.executeUpdate();
					
				}catch(Exception e){
					throw e;
				}finally{
					if(ps != null){
						ps.close();ps=null;
					}
				}
			}
		 
		 public static void deleteXXPENS_BI_SALES_TARGET_TEMP_L_ByTTSUPER(Connection conn,long id) throws Exception{
				PreparedStatement ps = null;
				int c =1;
				//logger.debug("updateItemByMKT ID["+o.getId()+"] LINE_ID["+o.getLineId()+"]");
				try{
					StringBuffer sql = new StringBuffer("");
					sql.append(" DELETE FROM PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L \n");
				    sql.append(" WHERE ID =? \n");
					ps = conn.prepareStatement(sql.toString());
					ps.setLong(c++, id);

					ps.executeUpdate();
					
				}catch(Exception e){
					throw e;
				}finally{
					if(ps != null){
						ps.close();ps=null;
					}
				}
			}
		public static void updateStatusRejectByTTSUPER_TT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusRejectItemByTTSUPER_TT PERIOD["+o.getPeriod()+"] status["+o.getStatus()+"]rejectReason["+o.getRejectReason()+"]");
			int  c = 1;
			try{
				o = convertCriteria(o);
				//head
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TT M SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ? ,REJECT_REASON = ?   \n");
				sql.append(" WHERE 1=1 ");
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				sql.append("\n and M.zone = '"+Utils.isNull(o.getSalesZone())+"'");
				sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
			
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getRejectReason());
				ps.executeUpdate();
				
				//Item
				sql = new StringBuffer("");
				c=1;
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TT_L M SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?  ,REJECT_REASON = ?  \n");
				sql.append(" WHERE ID IN ( ");
				sql.append("    select id from XXPENS_BI_SALES_TARGET_TT M");
				sql.append("\n  WHERE M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
				sql.append("\n  and M.brand = '"+Utils.isNull(o.getBrand())+"'");
				sql.append("\n  and M.zone = '"+Utils.isNull(o.getSalesZone())+"'");
				sql.append("\n  and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
				sql.append("\n  and M.target_quarter = '"+Utils.isNull(o.getTargetQuarter())+"'");
				sql.append("\n  and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
				sql.append("\n )");
				
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getRejectReason());
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
		
	 public static void insertItemByMKT_TT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			logger.debug("insertItemByMKT ID["+o.getId()+"] LINE_ID["+o.getLineId()+"]");
			try{
			
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO XXPENS_BI_SALES_TARGET_TT_L \n");
				sql.append(" (ID,LINE_ID, INVENTORY_ITEM_ID, INVENTORY_ITEM_CODE,\n");
				sql.append(" TARGET_QTY, TARGET_AMOUNT, STATUS,  \n");
				sql.append(" REMARK, REJECT_REASON, CREATE_USER, CREATE_DATE,AMT_AVG12,AMT_AVG3,PRICE,USER_INPUT_ID)  \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setLong(c++, o.getId());
				ps.setLong(c++, o.getLineId());
				ps.setBigDecimal(c++, new BigDecimal(o.getItemId()));
				ps.setString(c++, o.getItemCode());
				ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetQty())));
				ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetAmount())));
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getRemark());
				ps.setString(c++, o.getRejectReason());
				ps.setString(c++, o.getCreateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getOrderAmt12Month())));
				ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getOrderAmt3Month())));
				ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getPrice())));
				ps.setLong(c++, o.getUserInputId());
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static void updateItemByMKT_TT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			//logger.debug("updateItemByMKT ID["+o.getId()+"] LINE_ID["+o.getLineId()+"]");
			try{
			
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TT_L \n");
				sql.append(" SET TARGET_QTY =?, TARGET_AMOUNT =?, REMARK =?  \n");
				sql.append(" ,UPDATE_USER =?, UPDATE_DATE=? ,USER_INPUT_ID = ? \n");
			    sql.append(" WHERE ID =? AND LINE_ID = ?\n");
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetQty())));
				ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetAmount())));
				ps.setString(c++, o.getRemark());
				ps.setString(c++, o.getCreateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setLong(c++, o.getUserInputId());
				
				ps.setLong(c++, o.getId());
				ps.setLong(c++, o.getLineId());

				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 public static void deleteAllByMKT_TT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			StringBuffer sql = new StringBuffer("");
			logger.debug("deleteAllByMKT_TT ID["+o.getId()+"]");
			try{
				sql.append(" DELETE XXPENS_BI_SALES_TARGET_TT WHERE ID ="+o.getId()+" ");
				logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				ps.executeUpdate();
				
				sql = new StringBuffer("");
				sql.append(" DELETE XXPENS_BI_SALES_TARGET_TT_L WHERE ID ="+o.getId()+" ");
				logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 public static void deleteItemByMKT_TT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			logger.debug("deleteItemByMKT ID["+o.getId()+"] LINE_ID["+o.getLineId()+"]");
			try{
			
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE XXPENS_BI_SALES_TARGET_TT_L \n");
			    sql.append(" WHERE ID =? AND LINE_ID = ?\n");
				
				ps = conn.prepareStatement(sql.toString());
				ps.setLong(c++, o.getId());
				ps.setLong(c++, o.getLineId());

				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static void updateStatusHeadByManual(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateStatusHeadByManual(admin) status["+o.getStatus()+"]");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TT M SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE 1=1  \n" );
				sql.append(" and M.salesrep_code = '"+o.getSalesrepCode()+"' \n" );
				sql.append(" and M.customer_code = '"+o.getCustomerCode()+"' \n" );
				sql.append(" and M.brand = '"+o.getBrand()+"' \n" );
				sql.append(" and M.target_month = '"+o.getTargetMonth()+"' \n" );
				sql.append(" and M.target_quarter = '"+o.getTargetQuarter()+"' \n" );
				sql.append(" and M.target_year = '"+o.getTargetYear()+"' \n" );

		        logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
}
