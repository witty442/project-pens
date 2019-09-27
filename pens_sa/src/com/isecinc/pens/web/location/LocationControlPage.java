package com.isecinc.pens.web.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.PopupBean;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class LocationControlPage {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public  List<References> initYearList(Connection conn,String sort) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
        List<References> yearList = new ArrayList<References>();
		try{
			sql = "select distinct to_char(checkin_date,'yyyy') as order_year  from xxpens_om_trip_checkin_temp ORDER BY to_char(checkin_date,'yyyy') "+sort;
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				yearList.add(new References(rs.getString("ORDER_YEAR"), (rs.getInt("ORDER_YEAR")+543)+""));
			}
			
		}catch(Exception e){
			 throw e;
		}finally{
			
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return yearList;
	}
	
	public static List<PopupBean> searchCustCatNoListModel(Connection conn){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct C.cust_cat_no,C.cust_cat_desc from XXPENS_BI_MST_CUST_CAT C  ");
			sql.append("\n  where 1=1 and c.cust_cat_desc is not null");
		    //Show Only Credit Van Sales
			sql.append("\n  and C.cust_cat_no in('ORDER - CREDIT SALES','ORDER - VAN SALES')");
		
			sql.append("\n  ORDER BY C.cust_cat_no asc \n");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setCustCatNo(Utils.isNull(rst.getString("cust_cat_desc")));
				item.setCustCatDesc(Utils.isNull(rst.getString("cust_cat_desc")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	public static List<PopupBean> searchSalesChannelListModel(Connection conn){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.sales_channel_no ,S.sales_channel_desc from PENSBI.XXPENS_BI_MST_SALES_CHANNEL S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and sales_channel_no in('0','1','2','3','4') ");
			sql.append("\n  ORDER BY S.sales_channel_no asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesChannelNo(Utils.isNull(rst.getString("sales_channel_no")));
				item.setSalesChannelDesc(Utils.isNull(rst.getString("sales_channel_desc")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public static List<PopupBean> searchSalesrepListAll(String salesChannelNo,String custCatNo,String salesZone) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return searchSalesrepListAll(conn, salesChannelNo, custCatNo,salesZone);
		}catch(Exception e){
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static List<PopupBean> searchSalesrepListAll(Connection conn,String salesChannelNo,String custCatNo,String salesZone){
		PopupBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupBean> pos = new ArrayList<PopupBean>();
		try{
			sql.append("\n SELECT distinct S.code as salesrep_code,S.salesrep_id ");
			sql.append("\n from xxpens_salesreps_v S ");
			sql.append("\n where 1=1 ");
			sql.append("\n and attribute10 ='Y' ");
			if( !Utils.isNull(custCatNo).equals("")){
				sql.append("\n  and S.sales_channel = '"+custCatNo+"' ");
				sql.append("\n  and S.code not like 'C%' ");
				sql.append("\n  and S.code not like 'SN%' ");
			}else{
				sql.append("\n  and (S.sales_channel = 'S' OR S.sales_channel = 'C' ) ");
				sql.append("\n  and S.code not like 'C%' ");
				sql.append("\n  and S.code not like 'SN%' ");
			}
			if( !Utils.isNull(salesChannelNo).equals("")){
				sql.append("\n  and S.region='"+Utils.isNull(salesChannelNo)+"'");
			}
			if( !Utils.isNull(salesZone).equals("")){
			    sql.append("\n  and S.code in(");
			    sql.append("\n    select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
			    sql.append("\n    where zone = "+Utils.isNull(salesZone) );
			    sql.append("\n  )");
			}
			sql.append("\n  ORDER BY S.code asc ");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				 
				 //salesrepCode +=Utils.isNull(rst.getString("salesrep_code"))+",";
				 //salesrepId +=Utils.isNull(rst.getString("salesrep_id"))+",";
				 bean = new PopupBean();
				 bean.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				 bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				
				 if(Utils.isNull(bean.getSalesrepCode()).length() >=4){
				   pos.add(bean);
				 }
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	public static List<PopupBean> searchSalesZoneListModel(Connection conn){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.zone,S.zone_name from PENSBI.XXPENS_BI_MST_SALES_ZONE S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and zone in('0','1','2','3','4') ");
			sql.append("\n  ORDER BY S.zone asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesZone(Utils.isNull(rst.getString("zone")));
				item.setSalesZoneDesc(Utils.isNull(rst.getString("zone_name")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	public static List<PopupBean> searchProvinceList(Connection conn,String provinceName){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT * from PENSBI.m_province ");
			sql.append("\n  where 1=1  ");
			if( !Utils.isNull(provinceName).equals("")){
				sql.append("\n  and name ='"+provinceName+"'");
			}
			sql.append("\n  ORDER BY name asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setProvince(Utils.isNull(rst.getString("province_id")));
				item.setProvinceName(Utils.isNull(rst.getString("name")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public static String getProvinceName(Connection conn,String provinceId){
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String provincename = "";
		try{
			sql.append("\n  SELECT * from pensbi.m_province ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and province_id ='"+provinceId+"'");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			if (rst.next()) {
				provincename = Utils.isNull(rst.getString("name"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return provincename;
	}
	
	public static String getDistrictName(Connection conn,String districtId){
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String provincename = "";
		try{
			sql.append("\n  SELECT * from pensbi.m_district ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and district_id ='"+districtId+"'");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			if (rst.next()) {
				provincename = Utils.isNull(rst.getString("name"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return provincename;
	}
	
	public static List<PopupBean> searchDistrictList(String provinceId) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchDistrictListModel(conn,provinceId);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
			  conn.close();
			}
		}
	}
	public static List<PopupBean> searchDistrictListModel(Connection conn,String provinceId){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT d.name , d.district_id from PENSBI.m_district d,PENSBI.m_map_province p ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and d.province_id = p.province_id  ");
			if( !Utils.isNull(provinceId).equals("")){
				sql.append("\n  and d.province_id ='"+provinceId+"'");
			}
			sql.append("\n  ORDER BY d.name asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setDistrict(Utils.isNull(rst.getString("district_id")));
				item.setDistrictName(Utils.isNull(rst.getString("name")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
}
