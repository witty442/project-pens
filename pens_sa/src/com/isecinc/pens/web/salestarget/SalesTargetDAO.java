package com.isecinc.pens.web.salestarget;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.process.SequenceProcessAll;
import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

public class SalesTargetDAO {

	protected static Logger logger = Logger.getLogger("PENS");
	
	public static boolean salesRejectItem(User user,long id,long lineId,String rejectReason)  throws Exception {
		logger.debug("salesReject By MT");
		Connection conn = null;
        boolean r = false;	
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			SalesTargetBean h = new SalesTargetBean();
			h.setId(id);
			h.setLineId(lineId);
			h.setStatus(SalesTargetConstants.STATUS_REJECT);
			h.setUpdateUser(user.getUserName());
			h.setRejectReason(rejectReason);
			
			//update status REJECT IN Item
			SalesTargetDAO.updateStatusRejectItemByMKT(conn, h);
			
			//check status in item to head (OPEN/REJECT)
			String allStatusItem = getAllStatusInItem(conn,id,user);
			h.setStatus(allStatusItem);
			SalesTargetDAO.updateStatusHeadByMKT(conn, h);
			
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
	
	public static boolean salesManagerUnaccept(User user,long id)  throws Exception {
		logger.debug("Sales Manager Unaccept By Manager MT");
		Connection conn = null;
        boolean r = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			SalesTargetBean h = new SalesTargetBean();
			h.setId(id);
			h.setStatus(SalesTargetConstants.STATUS_UN_ACCEPT);
			h.setUpdateUser(user.getUserName());
			
			//update status Unaccept
			SalesTargetDAO.updateStatusHeadByMKT(conn, h);
			SalesTargetDAO.updateStatusItemByMKTByID(conn, h);
			
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
	
	public static boolean salesManagerAccept(User user,long id)  throws Exception {
		logger.debug("Sales Manager Accept By Manager MT");
		Connection conn = null;
        boolean r = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			SalesTargetBean h = new SalesTargetBean();
			h.setId(id);
			h.setStatus(SalesTargetConstants.STATUS_FINISH);
			h.setUpdateUser(user.getUserName());
			
			//update status Unaccept
			SalesTargetDAO.updateStatusHeadByMKT(conn, h);
			SalesTargetDAO.updateStatusItemByMKTByID(conn, h);
			
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
	
	public static SalesTargetBean searchTargetHeadByMKT(SalesTargetBean o,User user,String pageName ) throws Exception {
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
			
			sql.append("\n select M.*  ,T.total_target_qty ,T.total_target_amount ,T.status,T.amt_avg12 ,T.amt_avg3 ");
			sql.append("\n ,(select customer_id from XXPENS_BI_MST_CUSTOMER S WHERE S.customer_code = M.customer_code) as customer_id  ");
			sql.append("\n ,(select salesrep_id from XXPENS_BI_MST_SALESREP S WHERE S.salesrep_code = M.salesrep_code) as salesrep_id  ");
			sql.append("\n ,(select division from XXPENS_BI_MST_SALES_CHANNEL S WHERE S.sales_channel_no = T.SALES_CHANNEL) as division  ");
			sql.append("\n ,(select brand_group_no from XXPENS_BI_MST_BRAND_GROUP S WHERE S.brand_no = T.brand) as brand_group  ");
			
			sql.append("\n FROM (  ");
			sql.append("\n   select distinct");
			sql.append("\n   M.salesrep_code,M.customer_code   ");
			sql.append("\n  ,M.short_name  ");
			sql.append("\n  ,M.SALES_CHANNEL_NO");
			sql.append("\n   from XXPENS_BI_MST_CUST_SALES M ,XXPENS_BI_MST_CUST_CAT_MAP CM");
			sql.append("\n   where 1=1 ");
			sql.append("\n   AND M.sales_channel_no = CM.sales_channel_no");
			sql.append("\n   AND M.cust_cat_no = CM.cust_cat_no");
			if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
				sql.append("\n and M.sales_channel_no = '"+Utils.isNull(o.getSalesChannelNo())+"'");
			}
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n and M.cust_cat_no = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			sql.append("\n ) M LEFT OUTER JOIN ");
			sql.append("\n ( ");
			sql.append("\n  select M.salesrep_code,M.salesrep_id");
			sql.append("\n  , M.customer_code ,M.customer_id ,M.SALES_CHANNEL ,M.brand");
			sql.append("\n  ,(select max(C.short_name) from XXPENS_BI_MST_CUST_SALES C where C.customer_code = M.customer_code  ) as short_name  ");
			sql.append("\n  ,(select nvl(sum(amt_avg12),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as amt_avg12 ");
			sql.append("\n  ,(select nvl(sum(amt_avg3),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as amt_avg3 ");
			sql.append("\n  ,(select nvl(sum(target_qty),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as total_target_qty ");
			sql.append("\n  ,(select nvl(sum(target_amount),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as total_target_amount ");
			sql.append("\n  , M.status ");
			sql.append("\n  from XXPENS_BI_SALES_TARGET_TEMP M  ");
			sql.append("\n  where 1=1 ");
			if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
				sql.append("\n and M.sales_channel = '"+Utils.isNull(o.getSalesChannelNo())+"'");
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
			sql.append("\n)T ON ");
			sql.append("\n M.salesrep_code = T.salesrep_code AND M.customer_code = T.customer_code ");
			sql.append("\n ORDER BY M.salesrep_code,M.customer_code ");

			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new SalesTargetBean();
			   h.setSalesrepCode(rst.getString("salesrep_code"));
			   h.setSalesrepId(rst.getString("salesrep_id"));
			   h.setCustomerCode(rst.getString("customer_code"));  
			   h.setCustomerId(rst.getString("customer_id")); 
			   h.setCustomerName(Utils.isNull(rst.getString("short_name"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setTargetQty(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_QTY"), Utils.format_current_no_disgit,""));
			   h.setTargetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_AMOUNT"), Utils.format_current_2_disgit,""));
			   h.setDivision(Utils.isNull(rst.getString("division")));
			   h.setBrandGroup(Utils.isNull(rst.getString("brand_group")));
			   h.setSalesChannelNo(Utils.isNull(rst.getString("SALES_CHANNEL_NO")));
			   //get Total Qty
			   totalTargetQty = totalTargetQty+ rst.getInt("TOTAL_TARGET_QTY");
			   totalTargetAmount = totalTargetAmount+ rst.getDouble("TOTAL_TARGET_AMOUNT");
			   totalOrderAmt12Month = totalOrderAmt12Month+ rst.getDouble("amt_avg12");
			   totalOrderAmt3Month = totalOrderAmt3Month+rst.getDouble("amt_avg3");
			   
			   //set Access Action
			   h = SalesTargetUtils.setAccess(h,user,pageName);
			
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
	
	/**
	 * Role MT Access
	 * @param o
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static SalesTargetBean searchTargetHeadByMT(SalesTargetBean o,User user ,String pageName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		SalesTargetBean h = null;
		List<SalesTargetBean> items = new ArrayList<SalesTargetBean>();
		int totalTargetQty = 0;
		double totalTargetAmount = 0;
		double totalOrderAmt12Month =0;
		double totalOrderAmt3Month =0;
		boolean canFinish = false;
		int rowId = 0;
		try {
			//convert Criteria
			o = convertCriteria(o);
			
			sql.append("\n select M.*");
			sql.append("\n FROM (  ");
			sql.append("\n   select M.* ");
			sql.append("\n  ,(select max(C.short_name) from XXPENS_BI_MST_CUST_SALES C where C.customer_code = M.customer_code  ) as short_name  ");
			sql.append("\n  ,(select brand_desc from XXPENS_BI_MST_BRAND S WHERE S.brand_no = M.brand) as brand_name  ");
			sql.append("\n  ,(select nvl(sum(amt_avg12),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as amt_avg12 ");
			sql.append("\n  ,(select nvl(sum(amt_avg3),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as amt_avg3 ");
			sql.append("\n  ,(select nvl(sum(target_qty),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as total_target_qty ");
			sql.append("\n  ,(select nvl(sum(target_amount),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as total_target_amount ");;
			sql.append("\n  from XXPENS_BI_SALES_TARGET_TEMP M  ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and M.status <>'"+SalesTargetConstants.STATUS_OPEN+"'");
			if(SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){
				sql.append("\n  and (M.salesrep_code,M.customer_code) in( ");
				sql.append("\n    select A.salesrep_code,A.customer_code from XXPENS_BI_MST_CUST_SALES A " );
				sql.append("\n    WHERE 1=1 " );
				if( !"admin".equalsIgnoreCase(user.getUserName()))
				    sql.append("\n    and A.user_name = '"+Utils.isNull(user.getUserName())+"'");
				sql.append("\n  ) ");
			}else{
				if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
					sql.append("\n and M.sales_channel = '"+Utils.isNull(o.getSalesChannelNo())+"'");
				}
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("")){
				sql.append("\n and M.customer_code = '"+Utils.isNull(o.getCustomerCode())+"'");
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
			sql.append("\n)M");
			sql.append("\n ORDER BY M.salesrep_code,M.customer_code,M.brand ");

			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   rowId++;
			   h = new SalesTargetBean();
			   h.setId(rst.getLong("id"));
			   h.setRowId(rowId);
			   h.setSalesrepCode(rst.getString("salesrep_code"));
			   h.setSalesrepId(rst.getString("salesrep_id"));
			   h.setCustomerCode(rst.getString("customer_code"));  
			   h.setCustomerId(rst.getString("customer_id")); 
			   h.setCustomerName(Utils.isNull(rst.getString("short_name"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setDivision(Utils.isNull(rst.getString("division")));
			   h.setBrandGroup(Utils.isNull(rst.getString("brand_group")));
			   h.setBrand(Utils.isNull(rst.getString("brand"))); 
			   h.setBrandName(Utils.isNull(rst.getString("brand_name"))); 
			   h.setSalesChannelNo(Utils.isNull(rst.getString("SALES_CHANNEL")));
			   
			   h.setTargetQty(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_QTY"), Utils.format_current_no_disgit,""));
			   h.setTargetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_AMOUNT"), Utils.format_current_2_disgit,""));
	
			   //get Total Qty
			   totalTargetQty = totalTargetQty+ rst.getInt("TOTAL_TARGET_QTY");
			   totalTargetAmount = totalTargetAmount+ rst.getDouble("TOTAL_TARGET_AMOUNT");
			   totalOrderAmt12Month += rst.getDouble("amt_avg12");
			   totalOrderAmt3Month +=rst.getDouble("amt_avg3");
			   
			   //set Access Action
			   h = SalesTargetUtils.setAccess(h,user,pageName);
			
			   items.add(h);
			   
			   //Check can Finish All Search
			   if( !SalesTargetConstants.STATUS_FINISH.equalsIgnoreCase(h.getStatus())){
				 if( SalesTargetConstants.STATUS_ACCEPT.equalsIgnoreCase(h.getStatus())){
				   canFinish = true;
				 }
			   }
			}//while

			//set Result 
			o.setItems(items);
			o.setTotalTargetQty(Utils.decimalFormat(totalTargetQty, Utils.format_current_no_disgit));
			o.setTotalTargetAmount(Utils.decimalFormat(totalTargetAmount, Utils.format_current_2_disgit));
			o.setTotalOrderAmt12Month(Utils.decimalFormat(totalOrderAmt12Month, Utils.format_current_2_disgit));
			o.setTotalOrderAmt3Month(Utils.decimalFormat(totalOrderAmt3Month, Utils.format_current_2_disgit));
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
	
	public static SalesTargetBean searchSalesTarget(Connection conn,SalesTargetBean cri,boolean getItems,User user,String pageName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		SalesTargetBean h = null;
		try {
			cri = convertCriteria(cri);
			
			sql.append("\n   select M.id ");
			sql.append("\n  ,M.salesrep_code ,M.salesrep_id ");
			sql.append("\n  ,M.customer_code ,M.customer_id ,M.division,M.BRAND_GROUP,M.PERIOD");
			sql.append("\n  ,(select max(C.short_name) from XXPENS_BI_MST_CUST_SALES C where C.customer_code = M.customer_code  ) as short_name  ");
			sql.append("\n  ,M.status ,M.brand ,M.SALES_CHANNEL,M.DIVISION ,M.CUSTOMER_CATEGORY ");
			sql.append("\n  ,(select max(C.sales_channel_desc) from XXPENS_BI_MST_SALES_CHANNEL C where C.SALES_CHANNEL_NO = M.SALES_CHANNEL  ) as sales_channel_name  ");
			sql.append("\n  ,(select brand_desc from XXPENS_BI_MST_BRAND B where B.brand_no = M.brand) as brand_name ");
			sql.append("\n  ,(select nvl(sum(amt_avg12),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as amt_avg12 ");
			sql.append("\n  ,(select nvl(sum(amt_avg3),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as amt_avg3 ");
		
			sql.append("\n  ,(select nvl(sum(target_qty),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as total_target_qty ");
			sql.append("\n  ,(select nvl(sum(target_amount),0) from XXPENS_BI_SALES_TARGET_TEMP_L L where L.id=M.id) as total_target_amount ");
			sql.append("\n  ,(select customer_group from xxpens_ar_cust_group_v L where L.cust_account_id =M.customer_id) as customer_group ");
			sql.append("\n  ,target_month,target_quarter ,target_year ");
			sql.append("\n  from XXPENS_BI_SALES_TARGET_TEMP  M ");
			sql.append("\n  where 1=1 ");
			if(cri.getId() !=0)
		       sql.append("\n  and M.id = "+cri.getId()+"");
			if( !Utils.isNull(cri.getSalesrepCode()).equals(""))
			   sql.append("\n  and M.salesrep_code = '"+cri.getSalesrepCode()+"'");
			
			if( !Utils.isNull(cri.getCustomerCode()).equals(""))
			   sql.append("\n  and M.customer_code = '"+cri.getCustomerCode()+"'");
			
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
			sql.append("\n  ORDER BY M.salesrep_code,M.customer_code ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   h = new SalesTargetBean();
			   h.setId(rst.getLong("ID"));
			   h.setSalesrepCode(rst.getString("salesrep_code"));
			   h.setSalesrepId(rst.getString("salesrep_id"));
			   h.setCustomerCode(rst.getString("customer_code")); 
			   h.setCustomerId(rst.getString("customer_id"));  
			   h.setCustomerGroup(rst.getString("customer_group")); 
			   h.setCustomerName(Utils.isNull(rst.getString("short_name"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setBrand(Utils.isNull(rst.getString("brand"))); 
			   h.setBrandGroup(Utils.isNull(rst.getString("brand_group"))); 
			   h.setBrandName(Utils.isNull(rst.getString("brand_name"))); 
			   h.setSalesChannelNo(Utils.isNull(rst.getString("SALES_CHANNEL"))); 
			   h.setSalesChannelName(Utils.isNull(rst.getString("SALES_CHANNEL_NAME"))); 
			   h.setDivision(Utils.isNull(rst.getString("division"))); 
			   h.setCustCatNo(Utils.isNull(rst.getString("CUSTOMER_CATEGORY")));
			   h.setTotalTargetQty(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_QTY"), Utils.format_current_no_disgit));
			   h.setTotalTargetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_TARGET_AMOUNT"), Utils.format_current_2_disgit));
			   h.setTotalOrderAmt12Month(Utils.decimalFormat(rst.getDouble("AMT_AVG12"), Utils.format_current_2_disgit));
			   h.setTotalOrderAmt3Month(Utils.decimalFormat(rst.getDouble("AMT_AVG3"), Utils.format_current_2_disgit));
			   h.setTargetMonth(Utils.isNull(rst.getString("target_month")));
			   h.setTargetQuarter(Utils.isNull(rst.getString("target_quarter")));
			   h.setTargetYear(Utils.isNull(rst.getString("target_year")));
			   
			   h.setPeriod(rst.getString("period"));
			   //get period
			   SalesTargetBean period = SalesTargetUtils.getPeriodList(conn,h.getPeriod()).get(0);//get Period View
			   if(period != null){
				   h.setStartDate(period.getStartDate());
				   h.setEndDate(period.getEndDate());
			   }
			   h.setPeriodDesc(cri.getPeriodDesc());
			   
			   //set can access
			   h = SalesTargetUtils.setAccess(h,user,pageName);
			   
			   //getItems
			   if(getItems)
			     h.setItems(searchSalesTargetDetail(conn, h.getId(),user,pageName));
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
	
	public static List<SalesTargetBean> searchSalesTargetDetail(Connection conn,long id,User user,String pageName) throws Exception {
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
			sql.append("\n  from XXPENS_BI_SALES_TARGET_TEMP_L M ,XXPENS_BI_MST_ITEM B");
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
			   h = SalesTargetUtils.setAccess(h,user,pageName);
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
	
	public static String getAllStatusInItem(Connection conn,long id,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String allStatus = "";
		try {
			sql.append("\n  select DISTINCT M.STATUS");
			sql.append("\n  from XXPENS_BI_SALES_TARGET_TEMP_L M ");
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
		//logger.debug("startDate:"+o.getStartDate());
		Date startDate = Utils.parse(o.getStartDate(), Utils.DD_MMM_YYYY);
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
	public static SalesTargetBean save(SalesTargetBean h,User user) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			if ( Utils.userInRole(user,new String[]{User.MKT,User.ADMIN}) ){
				h = saveModelByMKT(conn,h);
			}
			conn.commit();
			
			return h;
		}catch(Exception e){
			conn.rollback();
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	public static SalesTargetBean saveModelByMKT(Connection conn,SalesTargetBean h) throws Exception{
        logger.debug("saveModelByMKT id["+h.getId()+"]");
        int lineId = 0;
		try{
			//check documentNo
			if(h.getId()==0){
				//Gen Next ID Sequence
				Integer id =SequenceProcessAll.getNextValue("XXPENS_BI_SALES_TARGET_TEMP");
				if(id==0){
					id = 1;
				}
				h.setId(id);
				
				logger.debug("**** Start Insert New ID:"+h.getId()+"****");
				//save Head
				insertHeadByMKT(conn, h);
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   SalesTargetBean l = (SalesTargetBean)h.getItems().get(i);
					 //  logger.debug("itemCode["+l.getItemCode()+"]status["+l.getStatus()+"]");
					   if( !l.getStatus().equals("DELETE")){
						   l.setId(h.getId());
						   lineId++;
						   l.setLineId(lineId);
						   insertItemByMKT(conn, l);
					   }
				   }
				}
			}else{
				logger.debug("****Start Update ID:"+h.getId()+"****");
				//Get MaxLine ID 
				int maxLineId = getMaxLineID(conn, h);
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   SalesTargetBean l = (SalesTargetBean)h.getItems().get(i);
					   l.setId(h.getId());
					   logger.debug("Line ID:"+l.getLineId());
					   if(l.getLineId() ==0){
	                        //get next line id =(max_line_id+1);
						    maxLineId++;
						    l.setLineId(maxLineId);
						    logger.debug("Insert new LineID:"+l.getLineId());
							insertItemByMKT(conn, l);
					   }else{
                           if(l.getStatus().equals("DELETE")){
                        	   logger.debug("DELETE LineID:"+l.getLineId());
							   //delete item
                        	   deleteItemByMKT(conn, l);
						   }else{
							   logger.debug("UPDATE LineID:"+l.getLineId());
						      //update Item by LineId
						      updateItemByMKT(conn,l);
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
	
	/**
	 * getMaxLineID
	 * @param conn
	 * @param o
	 * @return maxLineID
	 */
	public static int getMaxLineID(Connection conn,SalesTargetBean o){
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
	 
	 public static void insertHeadByMKT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			logger.debug("insertHeadByMKT ID["+o.getId()+"]");
			logger.debug("customerId:"+o.getCustomerId());
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO XXPENS_BI_SALES_TARGET_TEMP \n");
				sql.append(" (ID, CUSTOMER_ID, CUSTOMER_CODE,  \n");
				sql.append("  SALESREP_ID, SALESREP_CODE, TARGET_MONTH,  \n");
				sql.append("  TARGET_QUARTER, TARGET_YEAR, CUSTOMER_CATEGORY,  \n");
				sql.append("  DIVISION, SALES_CHANNEL, BRAND, \n");
				sql.append("  BRAND_GROUP, STATUS, CREATE_USER, CREATE_DATE,PERIOD,CUSTOMER_GROUP)  \n");
			    sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setLong(c++, o.getId());
				ps.setBigDecimal(c++, new BigDecimal(o.getCustomerId()));
				ps.setString(c++,o.getCustomerCode());
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
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
		public static void updateStatusHeadByMKT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("UpdateStatusHeadByMKT ID["+o.getId()+"]status["+o.getStatus()+"]");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE ID = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
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
		
		public static void updateStatusRejectItemByMKT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("UpdateStatusItemByMKT ID["+o.getId()+"] status["+o.getStatus()+"]rejectReason["+o.getRejectReason()+"]");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP_L SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ? ,REJECT_REASON = ?   \n");
				sql.append(" WHERE ID = ? AND line_id = ? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getRejectReason());
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
		public static void updateStatusItemByMKTByID(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("UpdateStatusItemByMKT ID["+o.getId()+"] status["+o.getStatus()+"]");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP_L SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE ID = ?\n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
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
	
	 public static void insertItemByMKT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			logger.debug("insertItemByMKT ID["+o.getId()+"] LINE_ID["+o.getLineId()+"]");
			try{
			
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO XXPENS_BI_SALES_TARGET_TEMP_L \n");
				sql.append(" (ID,LINE_ID, INVENTORY_ITEM_ID, INVENTORY_ITEM_CODE,\n");
				sql.append(" TARGET_QTY, TARGET_AMOUNT, STATUS,  \n");
				sql.append(" REMARK, REJECT_REASON, CREATE_USER, CREATE_DATE,AMT_AVG12,AMT_AVG3,PRICE)  \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?) \n");
				
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

				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static void updateItemByMKT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			//logger.debug("updateItemByMKT ID["+o.getId()+"] LINE_ID["+o.getLineId()+"]");
			try{
			
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE XXPENS_BI_SALES_TARGET_TEMP_L \n");
				sql.append(" SET TARGET_QTY =?, TARGET_AMOUNT =?, REMARK =?  \n");
				sql.append(" ,UPDATE_USER =?, UPDATE_DATE=?  \n");
			    sql.append(" WHERE ID =? AND LINE_ID = ?\n");
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetQty())));
				ps.setBigDecimal(c++, new BigDecimal(Utils.convertStrToDouble(o.getTargetAmount())));
				ps.setString(c++, o.getRemark());
				ps.setString(c++, o.getCreateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
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
	 public static void deleteAllByMKT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			logger.debug("deleteAllByMKT ID["+o.getId()+"]");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE XXPENS_BI_SALES_TARGET_TEMP \n");
			    sql.append(" WHERE ID =? \n");
				ps = conn.prepareStatement(sql.toString());
				ps.setLong(c++, o.getId());
				ps.executeUpdate();
				
				sql = new StringBuffer("");
				c=1;
				sql.append(" DELETE XXPENS_BI_SALES_TARGET_TEMP_L \n");
			    sql.append(" WHERE ID =? \n");
				ps = conn.prepareStatement(sql.toString());
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
	 public static void deleteItemByMKT(Connection conn,SalesTargetBean o) throws Exception{
			PreparedStatement ps = null;
			int c =1;
			logger.debug("deleteItemByMKT ID["+o.getId()+"] LINE_ID["+o.getLineId()+"]");
			try{
			
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE XXPENS_BI_SALES_TARGET_TEMP_L \n");
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
		
}
