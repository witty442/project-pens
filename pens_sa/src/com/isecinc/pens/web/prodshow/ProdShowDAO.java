package com.isecinc.pens.web.prodshow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class ProdShowDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static int searchProdShowListTotalRec(Connection conn,ProdShowBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c FROM ");
			sql.append("\n  XXPENS_OM_PRODSHOW_MST H");
			sql.append("\n ,XXPENS_OM_PRODSHOW_DT D"); 
			sql.append("\n ,xxpens_ar_customer_all_v C");
			sql.append("\n WHERE H.order_number =D.order_number");
			sql.append("\n AND C.account_number = H.customer_number");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
		
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
	public static List<ProdShowBean> searchProdShowList(Connection conn,ProdShowBean o,boolean allRec,int currPage,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ProdShowBean h = null;
		List<ProdShowBean> items = new ArrayList<ProdShowBean>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select ");
			sql.append("\n  substr(H.order_number,2,1) ");
			sql.append("\n ,(select B.sales_channel_desc from PENSBI.XXPENS_BI_MST_SALES_CHANNEL B ");
			sql.append("\n   where B.sales_channel_no=substr(H.order_number,2,1)) as region_name ");
			sql.append("\n ,substr(H.order_number,1,4) as sales_code");
			sql.append("\n ,(select B.salesrep_desc from PENSBI.XXPENS_BI_MST_SALESREP B ");
			sql.append("\n   where B.salesrep_code=substr(H.order_number,1,4)) as salesrep_full_name ");
			sql.append("\n ,H.customer_number ,C.party_name");
			sql.append("\n ,H.order_number,D.brand");
			sql.append("\n ,D.pic1,D.pic2,D.pic3");
			sql.append("\n ,(select B.brand_desc from PENSBI.XXPENS_BI_MST_BRAND B where B.brand_no=D.brand) as brand_name ");
			sql.append("\n ,H.show_date ");
			sql.append("\n FROM XXPENS_OM_PRODSHOW_MST H");
			sql.append("\n ,XXPENS_OM_PRODSHOW_DT D"); 
			sql.append("\n ,xxpens_ar_customer_all_v C");
			sql.append("\n WHERE H.order_number =D.order_number");
			sql.append("\n AND C.account_number = H.customer_number");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
		
			sql.append("\n    ORDER BY substr(H.order_number,2,1) ,substr(H.order_number,1,4),H.customer_number asc ");
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
			   h = new ProdShowBean();
			   h.setSalesChannelName(Utils.isNull(rst.getString("region_name")));
			   h.setSalesrepCode(Utils.isNull(rst.getString("sales_code")));
			   h.setSalesrepName(Utils.isNull(rst.getString("salesrep_full_name")));
			   h.setCustomerCode(rst.getString("customer_number"));  
			   h.setCustomerName(Utils.isNull(rst.getString("party_name"))); 
			   h.setOrderNo(Utils.isNull(rst.getString("order_number"))); 
			   h.setBrand(Utils.isNull(rst.getString("brand"))); 
			   h.setBrandName(Utils.isNull(rst.getString("brand_name"))); 
			   h.setPic1(Utils.isNull(rst.getString("pic1"))); 
			   h.setPic2(Utils.isNull(rst.getString("pic2"))); 
			   h.setPic3(Utils.isNull(rst.getString("pic3"))); 
			   h.setShowDate(DateUtil.stringValueChkNull(rst.getDate("show_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
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
	
	public static StringBuffer genWhereCondSql(Connection conn,ProdShowBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");

		if( !Utils.isNull(o.getCustCatNo()).equals("")){
			sql.append("\n and H.order_number Like '"+Utils.isNull(o.getCustCatNo())+"%'");
		}
		if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
			sql.append("\n and substr(H.order_number,2,1) = '"+Utils.isNull(o.getSalesChannelNo())+"'");
		}
		if( !Utils.isNull(o.getSalesrepCode()).equals("")){
			sql.append("\n and substr(H.order_number,1,4) = '"+Utils.isNull(o.getSalesrepCode())+"'");
		}
		if( !Utils.isNull(o.getBrand()).equals("")){
			sql.append("\n and D.brand = '"+Utils.isNull(o.getBrand())+"'");
		}
		if( !Utils.isNull(o.getCustomerCode()).equals("")){
			sql.append("\n and H.customer_number = '"+Utils.isNull(o.getCustomerCode())+"'");
		}
		if( !Utils.isNull(o.getSalesZone()).equals("")){
			sql.append("\n  and substr(H.order_number,1,4) in(");
			sql.append("\n    select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
			sql.append("\n    where zone = "+Utils.isNull(o.getSalesZone()) );
			sql.append("\n  )");
		}
		
		if( !Utils.isNull(o.getStartDate()).equalsIgnoreCase("") && !Utils.isNull(o.getEndDate()).equalsIgnoreCase("")){
			Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and H.show_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
			sql.append("\n and H.show_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
		}else if(!Utils.isNull(o.getStartDate()).equalsIgnoreCase("")){
			Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and H.show_date = to_date('"+startDateStr+"','dd/mm/yyyy')");
		}else if(!Utils.isNull(o.getEndDate()).equalsIgnoreCase("")){
			Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and H.show_date = to_date('"+endDateStr+"','dd/mm/yyyy')");
		}
		return sql;
	}
}
