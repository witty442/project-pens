package com.isecinc.pens.web.promotion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.DBConnection;
import util.Utils;

import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.dao.SalesrepDAO;

public class PromotionDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static int searchPromotionListTotalRec(Connection conn,PromotionBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c FROM(");
			sql.append("\n  SELECT S.* FROM (");
			sql.append("\n    select substr(H.request_no,2,1) as region ,H.request_date ,H.request_no ");
			sql.append("\n    ,H.salesrep_id,H.customer_number");
			sql.append("\n    ,substr(H.request_no,1,4) as sales_code");
			sql.append("\n    ,H.product_category as brand");
			sql.append("\n    from xxpens_om_req_promotion_mst H");
			sql.append("\n    ,xxpens_ar_customer_all_v C");
			sql.append("\n    WHERE C.account_number = H.customer_number");
			sql.append("\n  )S WHERE 1=1");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			sql.append("\n )A ");
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
	public static List<PromotionBean> searchPromotionList(Connection conn,PromotionBean o,boolean allRec,int currPage,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PromotionBean h = null;
		List<PromotionBean> items = new ArrayList<PromotionBean>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select S.* FROM(");
			sql.append("\n  select ");
			sql.append("\n  substr(H.request_no,2,1) as region ");
			sql.append("\n  ,(select B.sales_channel_desc from PENSBI.XXPENS_BI_MST_SALES_CHANNEL B ");
			sql.append("\n    where B.sales_channel_no=substr(H.request_no,2,1)) as region_name ");
			sql.append("\n  ,substr(H.request_no,1,4) as sales_code");
			sql.append("\n  ,(select B.salesrep_desc from PENSBI.XXPENS_BI_MST_SALESREP B ");
			sql.append("\n    where B.salesrep_code=substr(H.request_no,1,4)) as salesrep_full_name ");
			sql.append("\n  ,H.customer_number ,C.party_name ,H.product_type,H.request_date");
			sql.append("\n  ,H.request_no");
			
			//sql.append("\n  , substr(REGEXP_REPLACE(D.product_code,'[^0-9]+',''),1,3) as brand");
			//sql.append("\n  ,(select B.brand_desc from PENSBI.XXPENS_BI_MST_BRAND B ");
			//sql.append("\n    where B.brand_no =substr(REGEXP_REPLACE(D.product_code,'[^0-9]+',''),1,3) )as brand_name ");
			
			sql.append("\n  , H.product_category as brand");
			sql.append("\n  ,(select B.brand_desc from PENSBI.XXPENS_BI_MST_BRAND B ");
			sql.append("\n    where B.brand_no = H.product_category ) as brand_name ");
			
			sql.append("\n  FROM xxpens_om_req_promotion_mst H");
			sql.append("\n  ,xxpens_ar_customer_all_v C");
			sql.append("\n  WHERE C.account_number = H.customer_number");
			sql.append("\n )S WHERE 1=1");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
		
			sql.append("\n    ORDER BY S.sales_code,S.request_date,S.request_no asc ");
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
			   h = new PromotionBean();
			   h.setSalesChannelName(Utils.isNull(rst.getString("region_name")));
			   h.setSalesrepCode(Utils.isNull(rst.getString("sales_code")));
			   h.setSalesrepName(Utils.isNull(rst.getString("salesrep_full_name")));
			   h.setCustomerCode(rst.getString("customer_number"));  
			   h.setCustomerName(Utils.isNull(rst.getString("party_name"))); 
			   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
			   h.setRequestDate(Utils.stringValue(rst.getDate("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setBrand(Utils.isNull(rst.getString("brand"))); 
			   h.setBrandName(Utils.isNull(rst.getString("brand_name"))); 
			   h.setProductType(Utils.isNull(rst.getString("product_type")));
			   h.setProductTypeDesc(getProductTypeDesc(Utils.isNull(rst.getString("product_type"))));
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
	
	public static StringBuffer genWhereCondSql(Connection conn,PromotionBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");

		if( !Utils.isNull(o.getCustCatNo()).equals("")){
			sql.append("\n and S.request_no Like 'S%'");
		}
		if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
			sql.append("\n and S.region= '"+Utils.isNull(o.getSalesChannelNo())+"'");
		}
		if( !Utils.isNull(o.getSalesrepCode()).equals("")){
			sql.append("\n and S.sales_code = '"+Utils.isNull(o.getSalesrepCode())+"'");
		}
		if( !Utils.isNull(o.getBrand()).equals("")){
			sql.append("\n and S.brand  = '"+Utils.isNull(o.getBrand())+"'");
		}
		if( !Utils.isNull(o.getCustomerCode()).equals("")){
			sql.append("\n and S.customer_number = '"+Utils.isNull(o.getCustomerCode())+"'");
		}
		if( !Utils.isNull(o.getSalesZone()).equals("")){
			sql.append("\n and S.sales_code in( ");
			sql.append("\n  select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
			sql.append("\n  where zone = '"+Utils.isNull(o.getSalesZone())+"'");
			sql.append("\n )");
		}
		
		if( !Utils.isNull(o.getStartDate()).equalsIgnoreCase("")){
			Date startDate = Utils.parse(o.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String startDateStr = Utils.stringValue(startDate, Utils.DD_MM_YYYY_WITH_SLASH);
			
			Date endDate = Utils.parse(o.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String endDateStr = Utils.stringValue(endDate, Utils.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and S.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
			sql.append("\n and S.request_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
		}
		return sql;
	}
	public static List<RequestPromotion> searchReqPromotionList(PromotionBean mCriteria,boolean getItem) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<RequestPromotion> list = new ArrayList<RequestPromotion>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		int no = 0;
		SalesrepBean salesrepBean = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			sql.append("\n SELECT " );
			sql.append("\n  h.request_no,h.request_date,h.product_category ");
			sql.append("\n ,(select B.brand_desc from PENSBI.XXPENS_BI_MST_BRAND B ");
			sql.append("\n   where B.brand_no = h.product_category) as product_category_name ");
			sql.append("\n ,h.product_type,h.customer_number,h.phone");
			sql.append("\n ,h.request_name,h.comments as remark, h.printed_date,h.salesrep_id ");
			sql.append("\n ,h.start_active_date as promotion_start_date,h.end_active_date as promotion_end_date ");
			sql.append("\n ,(select p.salesrep_desc from PENSBI.XXPENS_BI_MST_SALESREP p where p.salesrep_id = h.salesrep_id) as sales_desc ");
			sql.append("\n ,(select p.customer_desc from PENSBI.XXPENS_BI_MST_CUSTOMER p where p.customer_code = h.customer_number) as customer_name ");
			sql.append("\n ,(select p.brand_desc from PENSBI.XXPENS_BI_MST_BRAND p where p.brand_no = h.product_category) as product_catagory_name ");

			sql.append("\n  from xxpens_om_req_promotion_mst h ");
			sql.append("\n  where 1=1 ");
			if(!Utils.isNull(mCriteria.getRequestNo()).equals("")){
			  sql.append("\n  and  h.request_no ='"+mCriteria.getRequestNo()+"'");
			}
			sql.append("\n  ORDER BY h.request_no desc \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  no++;
			  RequestPromotion m = new RequestPromotion();
			  m.setNo(no+"");
			  m.setRequestNo(rst.getString("request_no"));
			  m.setRequestDate(Utils.stringValue(rst.getDate("request_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setProductCatagory(Utils.isNull(rst.getString("product_category")));
			  m.setProductCatagoryDesc(Utils.isNull(rst.getString("product_catagory_name")));
			  m.setProductType(Utils.isNull(rst.getString("product_type")));
			  m.setProductTypeDesc(getProductTypeDesc(Utils.isNull(rst.getString("product_type"))));
			  m.setName(Utils.isNull(rst.getString("request_name")));
			  m.setPhone(Utils.isNull(rst.getString("phone")));
			  m.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
			  m.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			  m.setUserId(Utils.isNull(rst.getString("salesrep_id")));
			  m.setPromotionStartDate(Utils.stringValue(rst.getDate("promotion_start_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setPromotionEndDate(Utils.stringValue(rst.getDate("promotion_end_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  m.setRemark(Utils.isNull(rst.getString("remark")));
	
			  //Get User SalesRep Description
			  salesrepBean = SalesrepDAO.getSalesrepBeanById(conn, m.getUserId());
			  if(salesrepBean != null){
				  m.setTerritory(salesrepBean.getRegionName());
				  m.setSalesCode(salesrepBean.getCode());
				  m.setSalesName(salesrepBean.getSalesrepFullName());
			  }
			  if(rst.getDate("printed_date") != null)
			     m.setPrintDate(Utils.stringValue(rst.getTimestamp("printed_date"),Utils.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH,Utils.local_th));
			  
			  if(getItem){
				  //get Cost Line
				  m.setCostLineList(getCostLineList(conn,mCriteria.getRequestNo()));
				  
				//file table to 5 row
				 if(m.getCostLineList() != null && m.getCostLineList().size() >0 && m.getCostLineList().size() <5){
					  RequestPromotionCost lastCostLine = m.getCostLineList().get(m.getCostLineList().size()-1);
					  
					  int diffRow = 5 - m.getCostLineList().size();
					  int lastLineNo = lastCostLine.getLineNo();
					  for(int r=0;r<diffRow;r++){
						  lastLineNo++;
						  RequestPromotionCost newCostLine = new RequestPromotionCost();
						  newCostLine.setLineNo(lastLineNo);
						  newCostLine.setCostDetail("");
						  newCostLine.setCostAmount(null);
						  
						  m.getCostLineList().add(newCostLine);
					  }//for
				  }else if(m.getCostLineList().size() ==0){
					  int diffRow = 5;
					  int lastLineNo = 0;
					  for(int r=0;r<diffRow;r++){
						  lastLineNo++;
						  RequestPromotionCost newCostLine = new RequestPromotionCost();
						  newCostLine.setLineNo(lastLineNo);
						  newCostLine.setCostDetail("");
						  newCostLine.setCostAmount(null);
						  
						  m.getCostLineList().add(newCostLine);
					  }//for
				  }//if
				  
				  //get product Line
				  m.setPromotionLineList(getPromotionLineList(conn,mCriteria.getRequestNo()));
				  //file table to 7 row
				  if(m.getPromotionLineList() != null && m.getPromotionLineList().size()> 0 && m.getPromotionLineList().size() < 7 ){
					  RequestPromotionLine lastCostLine = m.getPromotionLineList().get(m.getPromotionLineList().size()-1);
					  
					  int diffRow = 7 - m.getPromotionLineList().size();
					  int lastLineNo = lastCostLine.getLineNo();
					  for(int r=0;r<diffRow;r++){
						  lastLineNo++;
						  RequestPromotionLine newCostLine = new RequestPromotionLine();
						  newCostLine.setLineNo(lastLineNo);
						  newCostLine.setProductCode("");
						  newCostLine.setProductName(" \n");
						  newCostLine.setNewCtn(null);
						  
						  m.getPromotionLineList().add(newCostLine);
					  }//for
				  }else if(m.getPromotionLineList() ==null || (m.getPromotionLineList() != null & m.getPromotionLineList().size()==0)){
					  int diffRow = 7;
					  int lastLineNo = 0;
					  for(int r=0;r<diffRow;r++){
						  lastLineNo++;
						  RequestPromotionLine newCostLine = new RequestPromotionLine();
						  newCostLine.setLineNo(lastLineNo);
						  newCostLine.setProductCode("");
						  newCostLine.setProductName(" \n");
						  newCostLine.setNewCtn(null);
						  
						  m.getPromotionLineList().add(newCostLine);
					  }//for
				  }
			  }//if get Item
			  list.add(m);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				if(conn != null){
				   conn.close();conn=null;
				}
			} catch (Exception e) {}
		}
		return list;
   }
	 public static List<RequestPromotionLine> getPromotionLineList(Connection conn,String requestNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			RequestPromotionLine line = null;
			List<RequestPromotionLine> productLineList = new ArrayList<RequestPromotionLine>();
			try {
				sql.append("\n  SELECT l.* " );
				sql.append("\n ,(select inventory_item_desc from PENSBI.XXPENS_BI_MST_ITEM M ");
				sql.append("\n   where M.inventory_item_code = l.product_code ) as product_name ");
                sql.append("\n from xxpens_om_req_promotion_dt1 l ");
				sql.append("\n where l.request_no ='"+requestNo+"'  order by line_number asc ");
			
			    logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
				    line = new RequestPromotionLine();
				    line.setLineNo(rst.getInt("line_number"));
					line.setProductCode(rst.getString("product_code"));
					line.setNewCtn(rst.getBigDecimal("new_ctn"));
					line.setNewAmount(rst.getBigDecimal("new_amount"));
					line.setStockCtn(rst.getBigDecimal("stock_ctn"));
					line.setStockQty(rst.getBigDecimal("stock_qty"));
					
					line.setBorrowCtn(rst.getBigDecimal("loan_ctn"));
					line.setBorrowQty(rst.getBigDecimal("loan_qty"));
					line.setBorrowAmount(rst.getBigDecimal("loan_amount"));
					line.setProductName(rst.getString("product_name"));
				    productLineList.add(line);
				}//while
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return productLineList;
		}
	 
	 public static List<RequestPromotionCost> getCostLineList(Connection conn,String requestNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			RequestPromotionCost line = null;
			List<RequestPromotionCost> productLineList = new ArrayList<RequestPromotionCost>();
			try {
				sql.append("\n  SELECT l.* " );
                sql.append("\n  from xxpens_om_req_promotion_dt2 l  ");
				sql.append("\n  where  l.request_no ='"+requestNo+"' order by line_number asc ");
			    logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
				    line = new RequestPromotionCost();
				    line.setLineNo(rst.getInt("line_number"));
					line.setCostDetail(rst.getString("cost_details"));
					line.setCostAmount(rst.getBigDecimal("cost_amount"));

				  productLineList.add(line);
				}//while
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return productLineList;
		}
	 
	 private static String getProductTypeDesc(String productType){
		 String desc ="";
		 if("C".equalsIgnoreCase(productType)){
			 desc ="‡§≈’¬√Ï µÍÕ°";
		 }else if("P".equalsIgnoreCase(productType)){
			 desc ="‚ª√‚¡™—Ëπ";
		 }
		 return desc;
	 }
}
