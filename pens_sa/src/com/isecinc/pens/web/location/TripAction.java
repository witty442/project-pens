package com.isecinc.pens.web.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.process.SequenceProcessAll;
import com.pens.util.DBConnection;
import com.pens.util.MonitorTime;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;


public class TripAction {
 private static Logger logger = Logger.getLogger("PENS");
 public static int pageSize = 50;
 
 public static LocationForm search(ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchTrip");
		LocationForm aForm = (LocationForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnectionApps();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				//Trip Day 1 - 23 +98
				MonitorTime monitor = new MonitorTime("searchTripList");
				List<LocationBean> tripPageList = searchTripList(conn, aForm.getBean());
				request.getSession().setAttribute("tripPageList", tripPageList);
				monitor.debugUsedTime();
				
			    // Get Trip Case Search By Customer
			    if(tripPageList != null && tripPageList.size() >0){
			    	for(int i=0;i<tripPageList.size();i++){
			    		//tripBySearchSqlIn += "'"+tripPageList.get(i).getTripDay()+"',";
			    		// set default CurPage
			    		if(i==0){
			    			currPage =Integer.parseInt(tripPageList.get(i).getTripDay());
			    			break;
			    		}
			    	}
			    }
				
				aForm.setTotalRecord(tripPageList !=null?tripPageList.size():0);
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    allRec = false;
			    String currPageSqlIn = "'"+currPage+"'";
			    
			    //Case force refresh by  TripDay(Manual CurrTrip)
			    if( !Utils.isNull(request.getParameter("currPage")).equals("")){
			    	currPageSqlIn = "'"+Utils.isNull(request.getParameter("currPage"))+"'";
			    	currPage = Utils.convertStrToInt(request.getParameter("currPage"));
			    	aForm.setCurrPage(currPage);
			    }
			    monitor = new MonitorTime("searchHeadList");
				List<LocationBean> items = searchHeadList(conn,aForm.getBean(),allRec,currPageSqlIn);
				aForm.setResults(items);
				monitor.debugUsedTime();
				
				if(items.size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				   aForm.setResults(null);
				}
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    allRec = false;
			    String currPageSqlIn = "'"+currPage+"'";
			    MonitorTime monitor = new MonitorTime("searchHeadList");
				List<LocationBean> items = searchHeadList(conn,aForm.getBean(),allRec,currPageSqlIn);
				aForm.setResults(items);
				
				monitor.debugUsedTime();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return aForm;
	}
 
 public static List<LocationBean> searchTripList_bk(Connection conn,LocationBean o) throws Exception {
	 List<LocationBean> list = new ArrayList<LocationBean>();
	 for(int i=0;i<23;i++){
		 LocationBean bean = new LocationBean();
		 bean.setTripDay((i+1)+"");
		 list.add(bean);
	 }
	 return list;
 }
 
 public static List<LocationBean> searchTripList(Connection conn,LocationBean o) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Map<Integer,LocationBean> tripMap = new HashMap<Integer,LocationBean>();
		List<LocationBean> list = null;
		LocationBean c = new LocationBean();
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("\n   SELECT distinct cs.trip1,trip2,trip3");
			sql.append("\n   from apps.xxpens_ar_cust_sales_vl cs  ");
			sql.append("\n   where 1=1 ");
			sql.append("   "+genWhereCondSql(conn, "", o));
			if( !Utils.isNull(o.getTripDay()).equals("")){
				sql.append("\n   and (cs.trip1 ="+o.getTripDay()+"  ");
				sql.append("\n        or cs.trip2 ="+o.getTripDay()+" ");
				sql.append("\n        or cs.trip3 ="+o.getTripDay()+" )");
			}
			//sql.append("\n   order by trip1,trip2,trip3 asc" );
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				if(rst.getInt("trip1") != 0){
				   c = new LocationBean();
				   c.setTripDay(rst.getString("trip1"));
				   if( !Utils.isNull(o.getTripDay()).equals("")){
					   if(Utils.isNull(o.getTripDay()).equals(c.getTripDay())){
						  tripMap.put(rst.getInt("trip1"), c);
					   }
				   }else{
				       tripMap.put(rst.getInt("trip1"), c);
				   }
				}
				if(rst.getInt("trip2") != 0){
				    c = new LocationBean();
				    c.setTripDay(rst.getString("trip2"));
				    if( !Utils.isNull(o.getTripDay()).equals("")){
						if( Utils.isNull(o.getTripDay()).equals(c.getTripDay())){
						   tripMap.put(rst.getInt("trip2"), c);
						}
					 }else{
					     tripMap.put(rst.getInt("trip2"), c);
					 }
				}
				if(rst.getInt("trip3") != 0){
					c = new LocationBean();
					c.setTripDay(rst.getString("trip3"));
					if( !Utils.isNull(o.getTripDay()).equals("")){
					   if(Utils.isNull(o.getTripDay()).equals(c.getTripDay())){
						  tripMap.put(rst.getInt("trip3"), c);
					   }
				    }else{
				       tripMap.put(rst.getInt("trip3"), c);
				    }
				}
				/*//Case No set trip 
				if(rst.getInt("trip1")==0&& rst.getInt("trip2") ==0 && rst.getInt("trip3")==0){
					c = new LocationBean();
					c.setTripDay("99");
				    tripMap.put(99, c);
				}*/
			}
			
			//Sort
			list = new ArrayList<LocationBean>(tripMap.values());
			Collections.sort(list, LocationBean.Comparators.TRIP_ASC);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return list;
	}
	
 public static List<LocationBean> searchHeadList(Connection conn,LocationBean o,boolean allRec ,String tripBySearchSqlIn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		LocationBean h = null;
		List<LocationBean> items = new ArrayList<LocationBean>();
		try {
			sql.append("\n   SELECT ");
			sql.append("\n    cs.account_number ,cs.party_name ");
			sql.append("\n   ,cs.address,cs.primary_salesrep_id ,cs.trip1,cs.trip2,cs.trip3");
			sql.append("\n   from apps.xxpens_ar_cust_sales_vl cs  ");
			sql.append("\n   where 1=1 ");
			if( !"".equals(tripBySearchSqlIn)){
				sql.append("\n   and (   cs.trip1 ="+tripBySearchSqlIn+" ");
				sql.append("\n        or cs.trip2 ="+tripBySearchSqlIn+" ");
				sql.append("\n        or cs.trip3 ="+tripBySearchSqlIn+" )");
			}
			sql.append("   "+genWhereCondSql(conn, "", o));
			if( !Utils.isNull(o.getTripDay()).equals("")){
				sql.append("\n   and (   cs.trip1 ="+o.getTripDay()+" ");
				sql.append("\n        or cs.trip2 ="+o.getTripDay()+" ");
				sql.append("\n        or cs.trip3 ="+o.getTripDay()+" )");
			}
			sql.append("\n order by  cs.account_number asc ");
			logger.debug("sql :"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new LocationBean();
			   h.setCustomerCode(Utils.isNull(rst.getString("ACCOUNT_NUMBER")));
			   h.setCustomerName(Utils.isNull(rst.getString("party_name")));
			   h.setAddress(Utils.isNull(rst.getString("address")));
			   h.setSalesrepCode(Utils.isNull(rst.getString("primary_salesrep_id")));
			   h.setTripDay(Utils.isNull(rst.getString("trip1")));
			   h.setTripDay2(Utils.isNull(rst.getString("trip2")));
			   h.setTripDay3(Utils.isNull(rst.getString("trip3")));
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
 public static List<LocationBean> searchExportReportList(Connection conn,LocationBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		LocationBean h = null;
		List<LocationBean> items = new ArrayList<LocationBean>();
		Map<String,String> chkDupCustCodeMap = new HashMap<String,String>();
		String tripDayAll = "";
		try {
			sql.append("\n SELECT A.*");
			sql.append("\n ,T2.trip_day2 ,T3.trip_day3 ");
			sql.append("\n  FROM( ");
			sql.append("\n   SELECT to_number(cs.trip1) as trip_day ,c.account_number ,c.party_name ,cs.address,s.salesrep_id");
			sql.append("\n   ,cs.amphur,cs.province ");
			sql.append("\n   FROM apps.xxpens_ar_cust_sales_vl cs ");
			sql.append("\n   ,xxpens_ar_customer_all_v c");
			sql.append("\n   ,xxpens_salesreps_v s");
			sql.append("\n   WHERE cs.cust_account_id = c.cust_account_id ");
			sql.append("\n   and cs.primary_salesrep_id = s.salesrep_id ");
			sql.append("   "+genWhereCondSql(conn, "", o));
			if( !Utils.isNull(o.getTripDay()).equals("")){
				sql.append("\n   and cs.trip1 ="+o.getTripDay()+" ");
			}
			sql.append("\n ) A LEFT OUTER JOIN  ");
			sql.append("\n ( ");
			sql.append("\n   SELECT to_number(cs.trip2) as trip_day2 ,c.account_number ,c.party_name ,cs.address,s.salesrep_id");
			sql.append("\n   ,cs.amphur,cs.province ");
			sql.append("\n   FROM apps.xxpens_ar_cust_sales_vl cs ");
			sql.append("\n   ,xxpens_ar_customer_all_v c");
			sql.append("\n   ,xxpens_salesreps_v s");
			sql.append("\n   WHERE cs.cust_account_id = c.cust_account_id ");
			sql.append("\n   and cs.primary_salesrep_id = s.salesrep_id ");
			sql.append("   "+genWhereCondSql(conn, "", o));
			if( !Utils.isNull(o.getTripDay()).equals("")){
				sql.append("\n   and cs.trip2 ="+o.getTripDay()+" ");
			}
			sql.append("\n ) T2 ON A.account_number = T2.account_number ");
			sql.append("\n LEFT OUTER JOIN");
			sql.append("\n ( ");
			sql.append("\n   SELECT to_number(cs.trip3) as trip_day3 ,c.account_number ,c.party_name ,cs.address,s.salesrep_id");
			sql.append("\n   ,cs.amphur,cs.province ");
			sql.append("\n   FROM apps.xxpens_ar_cust_sales_vl cs ");
			sql.append("\n   ,xxpens_ar_customer_all_v c");
			sql.append("\n   ,xxpens_salesreps_v s");
			sql.append("\n   WHERE cs.cust_account_id = c.cust_account_id ");
			sql.append("\n   and cs.primary_salesrep_id = s.salesrep_id ");
			sql.append("   "+genWhereCondSql(conn, "", o));
			if( !Utils.isNull(o.getTripDay()).equals("")){
				sql.append("\n   and cs.trip3 ="+o.getTripDay()+" ");
			}
			sql.append("\n )T3 ON A.account_number = T3.account_number ");
			
			sql.append("\n order by  A.trip_day ,A.account_number asc ");
			
			logger.debug("sql :"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new LocationBean();
			   tripDayAll = "";
			   if( !Utils.isNull(rst.getString("trip_day")).equals("")){
				   tripDayAll = Utils.isNull(rst.getString("trip_day"));
			   }
			   if( !Utils.isNull(rst.getString("trip_day2")).equals("")){
				   tripDayAll += ","+ Utils.isNull(rst.getString("trip_day2"));
			   }
			   if( !Utils.isNull(rst.getString("trip_day3")).equals("")){
				   tripDayAll += ","+ Utils.isNull(rst.getString("trip_day3"));
			   }
			   h.setTripDay(tripDayAll);
			   h.setCustomerCode(Utils.isNull(rst.getString("ACCOUNT_NUMBER")));
			   h.setCustomerName(Utils.isNull(rst.getString("party_name")));
			   h.setAddress(Utils.isNull(rst.getString("address")));
			   h.setSalesrepCode(Utils.isNull(rst.getString("salesrep_id")));
			   h.setDistrict(Utils.isNull(rst.getString("amphur")));
			   h.setProvince(Utils.isNull(rst.getString("province")));
			   if(chkDupCustCodeMap.get(h.getCustomerCode()) ==null){
			     items.add(h);
			   }
			   chkDupCustCodeMap.put(h.getCustomerCode(), h.getCustomerCode());
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				chkDupCustCodeMap.clear();
			} catch (Exception e) {}
		}
		return items;
	}

 public static LocationBean searchCustomerTripDetail(LocationBean o )throws Exception {
	 Connection conn = null;
	 try{
		 conn = DBConnection.getInstance().getConnectionApps();
		 return searchCustomerTripDetail(conn, o);
	 }catch(Exception e){
		 throw e;
	 }finally{
		 if(conn != null){
			 conn.close();
		 }
	 }
 }
 
 public static LocationBean searchCustomerTripDetail(Connection conn,LocationBean o )throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		LocationBean h = null;
		SalesrepBean salesrepBean = null;
		try {
			sql.append("\n   SELECT ");
			sql.append("\n   cs.account_number ,cs.party_name ,cs.address,cs.primary_salesrep_id,cs.trip1,cs.trip2,cs.trip3");
			sql.append("\n   ,cs.party_site_id,cs.cust_account_id ,cs.code");
			sql.append("\n   FROM apps.xxpens_ar_cust_sales_vl cs ");
			sql.append("\n   WHERE 1=1");
			sql.append("\n   and cs.account_number ='"+o.getCustomerCode()+"'");	
			logger.debug("sql:\n"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   h = new LocationBean();
			   h.setCustomerCode(Utils.isNull(rst.getString("ACCOUNT_NUMBER")));
			   h.setCustomerName(Utils.isNull(rst.getString("party_name")));
			   h.setAddress(Utils.isNull(rst.getString("address")));
			   h.setSalesrepId(Utils.isNull(rst.getString("primary_salesrep_id")));
			   h.setSalesrepCode(Utils.isNull(rst.getString("code")));
			  
			   //Get Salesrep name
			   salesrepBean = SalesrepDAO.getSalesrepBeanById(conn, h.getSalesrepId());
			   h.setSalesrepName(Utils.isNull(salesrepBean.getSalesrepFullName()));
			   
			   h.setTripDay(Utils.isNull(rst.getString("trip1")));
			   h.setTripDay2(Utils.isNull(rst.getString("trip2")));
			   h.setTripDay3(Utils.isNull(rst.getString("trip3")));
			   h.setCustAccountId(rst.getLong("party_site_id"));
			   h.setPartySiteId(rst.getLong("party_site_id"));
			   
			   h.setTripDayDB(Utils.isNull(rst.getString("trip1")));
			   h.setTripDayDB2(Utils.isNull(rst.getString("trip2")));
			   h.setTripDayDB3(Utils.isNull(rst.getString("trip3")));
			}//
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
 public static LocationBean searchCustomerNoTripDetail(LocationBean o )throws Exception {
	 Connection conn = null;
	 try{
		 conn = DBConnection.getInstance().getConnectionApps();
		 return searchCustomerNoTripDetail(conn, o);
	 }catch(Exception e){
		 throw e;
	 }finally{
		 if(conn != null){
			 conn.close();
		 }
	 }
 }
 
 public static LocationBean searchCustomerNoTripDetail(Connection conn,LocationBean o )throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		LocationBean h = null;
		SalesrepBean salesrepBean =null;
		try {
			sql.append("\n   SELECT ");
			sql.append("\n   cs.account_number ,cs.party_name ,cs.address,cs.primary_salesrep_id,cs.trip1,cs.trip2,cs.trip3");
			sql.append("\n   ,cs.party_site_id, cs.cust_account_id ,cs.code");
			sql.append("\n   FROM apps.xxpens_ar_cust_sales_vl cs ");
			sql.append("\n   WHERE 1=1");
			sql.append("\n   and cs.account_number ='"+o.getCustomerCode()+"'");	
			sql.append("\n   and cs.primary_salesrep_id ="+o.getSalesrepId()+"");	
			sql.append("\n   and cs.trip1 is null and cs.trip2 is null and cs.trip3 is null ");
			logger.debug("sql:\n"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   h = new LocationBean();
			   h.setCustomerCode(Utils.isNull(rst.getString("ACCOUNT_NUMBER")));
			   h.setCustomerName(Utils.isNull(rst.getString("party_name")));
			   h.setAddress(Utils.isNull(rst.getString("address")));
			   h.setSalesrepId(Utils.isNull(rst.getString("primary_salesrep_id")));
			   h.setSalesrepCode(Utils.isNull(rst.getString("code")));
			   
			   //Get Salesrep name
			   salesrepBean = SalesrepDAO.getSalesrepBeanById(conn, h.getSalesrepId());
			   h.setSalesrepName(Utils.isNull(salesrepBean.getSalesrepFullName()));
			   
			   h.setTripDay(Utils.isNull(rst.getString("trip1")));
			   h.setTripDay2(Utils.isNull(rst.getString("trip2")));
			   h.setTripDay3(Utils.isNull(rst.getString("trip3")));
			   h.setCustAccountId(rst.getLong("party_site_id"));
			   h.setPartySiteId(rst.getLong("party_site_id"));
			   
			   h.setTripDayDB(Utils.isNull(rst.getString("trip1")));
			   h.setTripDayDB2(Utils.isNull(rst.getString("trip2")));
			   h.setTripDayDB3(Utils.isNull(rst.getString("trip3")));
			}//
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
 
 public static LocationBean searchCustTripDetailTemp(Connection conn,LocationBean o )throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		LocationBean h = null;
		try {
			sql.append("\n SELECT account_number ,trip1,trip2,trip3");
			sql.append("\n FROM PENSBI.XXPENS_BI_CUST_TRIP_TEMP ");
			sql.append("\n WHERE account_number ='"+o.getCustomerCode()+"'");
			logger.debug("sql:\n"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   h = new LocationBean();
			   h.setCustomerCode(Utils.isNull(rst.getString("ACCOUNT_NUMBER")));
			   //h.setCustomerName(Utils.isNull(rst.getString("party_name")));
			   //h.setAddress(Utils.isNull(rst.getString("address")));
			   h.setTripDay(Utils.isNull(rst.getString("trip1")));
			   h.setTripDay2(Utils.isNull(rst.getString("trip2")));
			   h.setTripDay3(Utils.isNull(rst.getString("trip3")));
			}//while
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
 private static StringBuffer genWhereCondSql(Connection conn,String schema_name,LocationBean c){
	 StringBuffer sql = new StringBuffer();
	 String province = "";
	 String district = "";
	 try {
		 //filter only have trip
		/* sql.append("\n   and (   (cs.trip1 >=1 and cs.trip1 <=23) ");
		 sql.append("\n        or (cs.trip2 >=1 and cs.trip2 <=23)");
		 sql.append("\n        or (cs.trip3 >=1 and cs.trip3 <=23) )");*/
			
		if( !Utils.isNull(c.getProvince()).equals("")){
			province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getProvince()));
		}
		if( !Utils.isNull(c.getDistrict()).equals("")){
			district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getDistrict()));
		}
		if( !Utils.isNull(c.getCustomerCode()).equals("")){
			sql.append("\n and cs"+schema_name+".account_number ='"+c.getCustomerCode()+"' ");
		}
		if( !Utils.isNull(c.getCustomerName()).equals("")){
			sql.append("\n and cs"+schema_name+".party_name LIKE '%"+c.getCustomerName()+"%' ");
		}
		if( !Utils.isNull(c.getCustCatNo()).equals("")){
			sql.append("\n and cs"+schema_name+".sales_channel = '"+Utils.isNull(c.getCustCatNo())+"' ");
		}
		if( !Utils.isNull(c.getSalesChannelNo()).equals("")){
			sql.append("\n and cs"+schema_name+".region = '"+Utils.isNull(c.getSalesChannelNo())+"' ");
		}
		if( !Utils.isNull(c.getSalesrepCode()).equals("")){
			sql.append("\n and cs"+schema_name+".primary_salesrep_id = "+Utils.isNull(c.getSalesrepCode())+"");
		}
		if( !Utils.isNull(province).equals("")){
			sql.append("\n and cs"+schema_name+".province = '"+province+"' ");
		}
		if( !Utils.isNull(district).equals("")){
			sql.append("\n and cs"+schema_name+".amphur = '"+district+"' ");
		}

		if( !Utils.isNull(c.getSalesZone()).equals("")){
		    sql.append("\n  and cs"+schema_name+".primary_salesrep_id in(");
		    sql.append("\n    select salesrep_id from pensbi.XXPENS_BI_MST_SALES_ZONE ");
		    sql.append("\n    where zone = "+Utils.isNull(c.getSalesZone()) );
		    sql.append("\n  )");
		}
		
	/*	if( !Utils.isNull(c.getCustomerType()).equals("")){
			if( Utils.isNull(c.getCustomerType()).equals("P")){
		        //sql.append("\n and ( cs"+schema_name+".customer_class_code IN('"+c.getCustomerType()+"','') or cs"+schema_name+".customer_class_code is null) ");
		        sql.append("\n and ( cs"+schema_name+".customer_class_code ='"+c.getCustomerType()+"' ");
		        sql.append("\n     or cs"+schema_name+".customer_class_code ='' ");
		        sql.append("\n     or cs"+schema_name+".customer_class_code is null ) ");
			}else{
			    sql.append("\n and cs"+schema_name+".customer_class_code ='"+c.getCustomerType()+"' ");
			}
		}else{
			//CustType
			 sql.append("\n and (  cs"+schema_name+".customer_class_code ='P' ");
			 sql.append("\n     or cs"+schema_name+".customer_class_code ='B' ");
		     sql.append("\n     or cs"+schema_name+".customer_class_code ='' ");
		     sql.append("\n     or cs"+schema_name+".customer_class_code is null ) ");
			
		}*/
	 }catch(Exception e){
		 e.printStackTrace();
	 }
	return sql;
 }
 
 public static String updateCustTrip(LocationBean o) throws Exception{
	 Connection conn = null;
	 String msg = "success";
	 try{
		 conn =DBConnection.getInstance().getConnectionApps();
		 conn.setAutoCommit(false);
		 
		 //update trip master
		 updateCustTripMasterProc(conn, o);
		 
		 //insertCustTripChangeHis 
		 insertCustTripChangeHis(conn, o);
		  
		 //test 
		/* if(true){
			 throw new Exception("Error Wit test");
		 }*/
		 conn.commit();
	 }catch(Exception e){
		 conn.rollback();
		 msg = "fail|"+e.getMessage();
	 }finally{
		 if(conn != null){
			 conn.close();
		 }
	 }
	 return msg;
 }
 
 
 public static void insertCustTripChangeHis(Connection conn,LocationBean o) throws Exception{
	PreparedStatement ps = null;
	int c =1;
	logger.debug("insertCustTripChangeHis customerCode["+o.getCustomerCode()+"]");
	long id = 0;
	try{
		id = SequenceProcessAll.getNextValue("XXPENS_BI_CUST_TRIP_CHANGE_HIS");
		
		StringBuffer sql = new StringBuffer("");
		sql.append(" INSERT INTO PENSBI.XXPENS_BI_CUST_TRIP_CHANGE_HIS \n");
		sql.append(" (ID,ACCOUNT_NUMBER,TRIP1_TO,TRIP2_TO,TRIP3_TO, CREATE_USER, CREATE_DATE,TRIP1_FROM, TRIP2_FROM, TRIP3_FROM)  \n");
	    sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?) \n");
		
		ps = conn.prepareStatement(sql.toString());
		ps.setLong(c++, id);
		ps.setString(c++, Utils.isNull(o.getCustomerCode()));
		ps.setString(c++,Utils.isNull(o.getTripDay()));
		ps.setString(c++,Utils.isNull(o.getTripDay2()));
		ps.setString(c++,Utils.isNull(o.getTripDay3()));
		ps.setString(c++, o.getCreateUser());
		ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
		ps.setString(c++,Utils.isNull(o.getTripDayDB()));
		ps.setString(c++,Utils.isNull(o.getTripDayDB2()));
		ps.setString(c++,Utils.isNull(o.getTripDayDB3()));

		ps.executeUpdate();
	}catch(Exception e){
		throw e;
	}finally{
		if(ps != null){
			ps.close();ps=null;
		}
	}
}

public static void updateCustTripMasterProc(Connection conn,LocationBean o) throws Exception{
	PreparedStatement ps = null;
	logger.debug("updateCustTripMasterProc cust_account_id["+o.getCustAccountId()+"]");
	logger.debug("trip1:"+o.getTripDay()+",trip2:"+o.getTripDay2()+",trip3:"+o.getTripDay3());
	int  c = 1;
	try{
		StringBuffer sql = new StringBuffer("");
		sql.append("{ call xxpens_om_trip_pkg.update_trip(?,?,?,?,?) } \n");
		
		/*xxpens_om_trip_pkg.update_trip(p_cust_id       in number,
                p_party_site_id in number,
                p_trip1         in varchar2,
                p_trip2         in varchar2,
                p_trip3         in varchar2)*/
                
		ps = conn.prepareCall(sql.toString());
		
		ps.setLong(c++,o.getCustAccountId());
		ps.setLong(c++,o.getPartySiteId());
		if( !Utils.isNull(o.getTripDay()).equals("")){
		   ps.setString(c++,Utils.isNull(o.getTripDay()));
		}else{
		   ps.setNull(c++, java.sql.Types.NULL);
		}
		if( !Utils.isNull(o.getTripDay2()).equals("")){
		   ps.setString(c++,Utils.isNull(o.getTripDay2()));
		}else{
		   ps.setNull(c++, java.sql.Types.NULL);
		}
		if( !Utils.isNull(o.getTripDay3()).equals("")){
		   ps.setString(c++,Utils.isNull(o.getTripDay3()));
		}else{
		   ps.setNull(c++, java.sql.Types.NULL);
		}
		ps.executeUpdate();
		
	}catch(Exception e){
		throw e;
	}finally{
		if(ps != null){
			ps.close();ps=null;
		}
	}
}

 public static StringBuffer exportToExcel(LocationBean bean) throws Exception{
		StringBuffer h = new StringBuffer("");
		String colspan ="6";
		Connection conn =null;
		try{
			//search all
			conn = DBConnection.getInstance().getConnectionApps();
			List<LocationBean> itemsList = searchExportReportList(conn,bean);
			
			logger.debug("salesrepCode:"+bean.getSalesrepCode());
			
			SalesrepBean salesrepBean = SalesrepDAO.getSalesrepBeanById(conn, bean.getSalesrepCode());
			
			h.append(ExcelHeader.EXCEL_HEADER);
			//Header
			h.append("<table border='1'> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"'><b>ข้อมูล Trip</b></td> \n");
			h.append(" </tr> \n");
			h.append(" <tr> \n");
			h.append("  <td align='left' colspan='"+colspan+"'><b>พนักงานขาย "+salesrepBean.getCode()+"-"+salesrepBean.getSalesrepFullName()+"</b></td> \n");
			h.append(" </tr> \n");
			h.append("</table> \n");

			if(itemsList != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>Trip/จุด</th> \n");
				  /*h.append("<th>No</th> \n");*/
				  h.append("<th>หมายเลขลูกค้า</th> \n");
				  h.append("<th>ชื่อ</th> \n");
				  h.append("<th>ที่อยู่</th> \n");
				  h.append("<th>อำเภอ</th> \n");
				  h.append("<th>จังหวัด</th> \n");
				h.append("</tr> \n");
			
				for(int i=0;i<itemsList.size();i++){
					LocationBean s = (LocationBean)itemsList.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+Utils.isNull(s.getTripDay())+"</td> \n");
					  /*h.append("<td class='text'>"+(i+1)+"</td> \n");*/
					  h.append("<td class='text'>"+s.getCustomerCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getCustomerName()+"</td> \n");
					  h.append("<td class='text'>"+s.getAddress()+"</td> \n");
					  h.append("<td class='text'>"+s.getDistrict()+"</td> \n");
					  h.append("<td class='text'>"+s.getProvince()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return h;
	}
}
