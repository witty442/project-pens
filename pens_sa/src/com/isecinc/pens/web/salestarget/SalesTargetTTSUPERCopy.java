package com.isecinc.pens.web.salestarget;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.process.SequenceProcessAll;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class SalesTargetTTSUPERCopy {

	protected static Logger logger = Logger.getLogger("PENS");
	
	public static String copyFromLastMonthByTTSUPER(User user,SalesTargetBean destBean,String pageName)  throws Exception {
		logger.debug("copyFromLastMonth");
		Connection conn = null;
        Calendar curCal = Calendar.getInstance();
        String errorCode = "";
        SalesTargetBean sourceBean = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);

			//setDate CurrentMonth 
			Date startDate = DateUtil.parse(destBean.getStartDate(), DateUtil.DD_MMM_YYYY);
			curCal.setTime(startDate);
			logger.debug("curDate Time["+curCal.getTime()+"]");
			
			//validate cur month data Exist (all status)
			destBean = SalesTargetDAO.convertCriteria(destBean);
			boolean curDataExist = false;
			/** no check because is check in before insert  **/
			//salesTargetIsExistTEMP(conn,destBean,user,"");
			
			//if(true){
			if(curDataExist ==true){
				errorCode ="DATA_CUR_EXIST_EXCEPTION";
				logger.debug("ErrorCode["+errorCode+"]");
			}else{
				//setDate prevMonth
				sourceBean = new SalesTargetBean();
				curCal.add(Calendar.MONTH, -1);
				sourceBean.setStartDate(DateUtil.stringValue(curCal.getTime(), DateUtil.DD_MMM_YYYY));
				logger.debug("prevDate Time["+curCal.getTime()+"]");
				//convert
				sourceBean.setPeriod(DateUtil.stringValue(curCal.getTime(),"MMM-yy").toUpperCase());
				
				//convert to criteria
				sourceBean = SalesTargetDAO.convertCriteria(sourceBean);
				sourceBean.setStatus(SalesTargetConstants.STATUS_FINISH);// get only status Finish
				sourceBean.setBrand(destBean.getBrand());
				sourceBean.setCustCatNo(destBean.getCustCatNo());
				sourceBean.setSalesZone(destBean.getSalesZone());
				
				logger.info("Copy from Period["+sourceBean.getPeriod()+"] to ["+destBean.getPeriod()+"]");
				
				//validate prev month data is exist (Status=Finish)
				boolean prevDataExist = salesTargetIsExistTEMP(conn,sourceBean,user,SalesTargetConstants.STATUS_FINISH);
				logger.debug("prevDataExist:"+prevDataExist);
				
				boolean foundDataMKTPost = chkAllTargetIsMKTPost(conn,destBean,user);
				logger.debug("foundDataMKTPost:"+foundDataMKTPost);
				
				if(prevDataExist == false){
					errorCode ="DATA_PREV_NOT_FOUND";
				}else if(foundDataMKTPost ==false){
					errorCode ="DATA_MKT_NOT_POST_FOUND";
				}else{
					//insert data Loop By brand
					insertDataTTSUPERByBrand(conn, sourceBean, destBean, user);
				}
			}
			
			logger.debug("ErrorCode["+errorCode+"]");
			logger.debug("Connection Commit");
			conn.commit();
		} catch (Exception e) {
			logger.debug("Connection Rollback");
			conn.rollback();
			logger.error(e.getMessage(),e);
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return errorCode;
	}
	public static boolean insertDataTTSUPERByBrand(Connection conn,SalesTargetBean sourceBean,SalesTargetBean destBean,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean h = false;
		String priceListId  ="";
		BigDecimal idNew = new BigDecimal("0");
		String keyPriceListMap = "";
		Map<String,String> priceListMap = new HashMap<String, String>();
		boolean curBrandExist = false;
		try {
			sourceBean = SalesTargetDAO.convertCriteria(sourceBean);
			
			sql.append("\n select M.id ,M.customer_category ,M.salesrep_id ,M.salesrep_code ");
			sql.append("\n ,M.customer_id ,M.sales_channel ,Z.zone,M.brand");
			sql.append("\n from PENSBI.XXPENS_BI_SALES_TARGET_TEMP M ");
			sql.append("\n ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n where M.salesrep_id = Z.salesrep_id ");
			sql.append("\n and M.division = 'B' ");//B = credit,van sales
			sql.append("\n and M.status ='"+SalesTargetConstants.STATUS_FINISH+"'");
			sql.append("\n and M.target_month = '"+Utils.isNull(sourceBean.getTargetMonth())+"'");
			sql.append("\n and M.target_quarter = '"+Utils.isNull(sourceBean.getTargetQuarter())+"'");
			sql.append("\n and M.target_year = '"+Utils.isNull(sourceBean.getTargetYear())+"'");
			
			//filter by criteria
			if( !Utils.isNull(sourceBean.getBrand()).equals("")){
				sql.append("\n and M.brand = '"+sourceBean.getBrand()+"'");
			}
			if( !Utils.isNull(sourceBean.getCustCatNo()).equals("")){
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(sourceBean.getCustCatNo())+"'");
			}
			if( !Utils.isNull(sourceBean.getSalesZone()).equals("")){
				sql.append("\n and Z.zone = '"+Utils.isNull(sourceBean.getSalesZone())+"'");
			}
			
			//filter by user login Except admin
		    if( !user.getUserName().equalsIgnoreCase("admin")){
		    	sql.append("\n  and (M.CUSTOMER_CATEGORY,Z.zone) in(");
		    	sql.append("\n    select cust_cat_no ,zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT" );
		    	sql.append("\n    where user_name = '"+Utils.isNull(user.getUserName())+"'");
		    	sql.append("\n  )");
		    }else{
		    	//admin all cat by TT
		    	sql.append("\n  and (M.CUSTOMER_CATEGORY ,Z.zone) in(");
		    	sql.append("\n    select cust_cat_no,zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT" );
		    	sql.append("\n  )");
		    }
		    //Only MKT Post 
		    sql.append("\n  and ( M.customer_category ,Z.zone ,M.brand )");
		    sql.append("\n  in( ");
		    sql.append("\n    select M.customer_category ,Z.zone,M.brand ");
			sql.append("\n  	from XXPENS_BI_SALES_TARGET_TT M ");
			sql.append("\n  	where 1=1");
			sql.append("\n  	and M.status ='"+SalesTargetConstants.STATUS_POST+"'");
			sql.append("\n  	and M.target_month = '"+Utils.isNull(destBean.getTargetMonth())+"'");
			sql.append("\n  	and M.target_quarter = '"+Utils.isNull(destBean.getTargetQuarter())+"'");
			sql.append("\n  	and M.target_year = '"+Utils.isNull(destBean.getTargetYear())+"'");
			if( !Utils.isNull(sourceBean.getBrand()).equals("")){
				sql.append("\n  	and M.brand = '"+sourceBean.getBrand()+"'");
			}
			if( !Utils.isNull(sourceBean.getCustCatNo()).equals("")){
				sql.append("\n  	and M.CUSTOMER_CATEGORY = '"+Utils.isNull(sourceBean.getCustCatNo())+"'");
			}
			if( !Utils.isNull(sourceBean.getSalesZone()).equals("")){
				sql.append("\n  	and M.zone = '"+Utils.isNull(sourceBean.getSalesZone())+"'");
			}
		    sql.append("\n  )");
		    
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				//set for get Avg Order
				destBean.setCustCatNo(Utils.isNull(rst.getString("customer_category")));
				destBean.setSalesZone(Utils.isNull(rst.getString("zone")));
				destBean.setBrand(Utils.isNull(rst.getString("brand")));
				destBean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
		
				curBrandExist = salesTargetIsExistTEMP(conn,destBean,user,"");
				logger.debug("*****Check CurBrandExist by salesrepID ********");
				logger.debug("custCatNo["+destBean.getCustCatNo()+"]");
				logger.debug("Zone["+destBean.getSalesZone()+"]");
				logger.debug("brand["+destBean.getBrand()+"]");
				logger.debug("salesrepCode["+rst.getString("salesrep_code")+"]");
				logger.debug("***** curBrandExist["+curBrandExist+"] ********");
				
				if(!curBrandExist){
					//insert head
					idNew = insertHeadTTSUPER(conn, destBean, rst.getBigDecimal("id"));
					
					keyPriceListMap = Utils.isNull(rst.getString("zone"))+"_"+Utils.isNull(rst.getString("customer_category"));
					if(priceListMap.get(keyPriceListMap) != null){
						priceListId = priceListMap.get(keyPriceListMap);
					}else{
					   //find priceListId
					    priceListId = SalesTargetTTUtils.getPriceListId(conn, Utils.isNull(rst.getString("zone")), Utils.isNull(rst.getString("customer_category")),user);
					    priceListMap.put(keyPriceListMap, priceListId);
					}
					//insert Line
				    insertLineTTSUPER(conn, priceListId, destBean, rst.getBigDecimal("id"), idNew);
				}
			}//if
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
	
	public static BigDecimal insertHeadTTSUPER(Connection conn,SalesTargetBean curBean,BigDecimal idCopy) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		BigDecimal idNew = null;
		try {
			idNew =new BigDecimal(SequenceProcessAll.getIns().getNextValue("XXPENS_BI_SALES_TARGET_TEMP"));
			if(idNew.compareTo(new BigDecimal("0")) ==0){
				idNew = new BigDecimal("1");
			}
			sql.append("\n  INSERT INTO PENSBI.XXPENS_BI_SALES_TARGET_TEMP ");
			sql.append("\n  SELECT ");
			sql.append("\n  "+idNew+" as ID, ");
			sql.append("\n  CUSTOMER_ID, ");
			sql.append("\n  CUSTOMER_GROUP, ");
			sql.append("\n  CUSTOMER_CODE, ");
			sql.append("\n  SALESREP_ID, ");
			sql.append("\n  SALESREP_CODE, ");
			sql.append("\n  '"+curBean.getTargetMonth()+"' as TARGET_MONTH, ");
			sql.append("\n  '"+curBean.getTargetQuarter()+"' as TARGET_QUARTER, ");
			sql.append("\n  '"+curBean.getTargetYear()+"' as TARGET_YEAR, ");
			sql.append("\n  CUSTOMER_CATEGORY, ");
			sql.append("\n  DIVISION, ");
			sql.append("\n  SALES_CHANNEL, ");
			sql.append("\n  BRAND, ");
			sql.append("\n  BRAND_GROUP, ");
			sql.append("\n  'Post' as STATUS, ");
			sql.append("\n  '"+curBean.getCreateUser()+"_copy' as CREATE_USER, ");
			sql.append("\n  sysdate as CREATE_DATE, ");
			sql.append("\n  '' as UPDATE_USER, ");
			sql.append("\n  null as UPDATE_DATE, ");
			sql.append("\n  '"+curBean.getPeriod()+"' as PERIOD, ");
			sql.append("\n  null as invoiced_qty ,");
			sql.append("\n  null as invoiced_amt ,");
			sql.append("\n  null as estimate_qty ,");
			sql.append("\n  null as estimate_amt ,");
			sql.append("\n  null as price,");
			sql.append("\n  '"+curBean.getSessionId()+"' as session_id");
			sql.append("\n  FROM PENSBI.XXPENS_BI_SALES_TARGET_TEMP WHERE ID="+idCopy);
		    //logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.execute();
		
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {}
		}
		return idNew;
	}
	
	public static boolean insertLineTTSUPER(Connection conn,String priceListId,SalesTargetBean curBean,BigDecimal idCopy,BigDecimal idNew) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		boolean h = false;
		try {
			sql.append("\n  INSERT INTO PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L");
			sql.append("\n  SELECT ");
			sql.append("\n  "+idNew+" as ID, ");
			sql.append("\n  LINE_ID, ");
			sql.append("\n  L.INVENTORY_ITEM_ID, ");
			sql.append("\n  INVENTORY_ITEM_CODE, ");
			sql.append("\n  TARGET_QTY, ");
			sql.append("\n  TARGET_AMOUNT, ");
			sql.append("\n  'Open' as status, ");
			sql.append("\n  '' as remark, ");
			sql.append("\n  '' as REJECT_REASON, ");
			sql.append("\n  '"+curBean.getCreateUser()+"_copy' as CREATE_USER, ");
			sql.append("\n  sysdate as CREATE_DATE, ");
			sql.append("\n  '' as UPDATE_USER, ");
			sql.append("\n  null as UPDATE_DATE, ");
			
			//19/12/2562 No get sum avg
			sql.append("\n  0 as SUM12, ");//AMT_AVG12
			sql.append("\n  0 as SUM3, ");//AMT_AVG3
			
			//NEW 21/11/2019
			/*sql.append("\n  ( SELECT MAX(V.SUM12) ");
			sql.append("\n    FROM APPS.XXPENS_BI_MST_SALES_AVG_V V,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n    WHERE V.PERIOD ='"+curBean.getPeriod()+"'");
			sql.append("\n    AND V.CUSTOMER_CATEGORY ='"+curBean.getCustCatNo()+"'");
			sql.append("\n    AND Z.ZONE ='"+curBean.getSalesZone()+"'");
			sql.append("\n    AND V.salesrep_id ="+curBean.getSalesrepId());
			sql.append("\n    AND V.salesrep_id = Z.salesrep_id");
			sql.append("\n  ) as SUM12, ");*/
			
			/*sql.append("\n  ( SELECT MAX(V.SUM3) ");
			sql.append("\n    FROM APPS.XXPENS_BI_MST_SALES_AVG_V V,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n    WHERE V.PERIOD ='"+curBean.getPeriod()+"'");
			sql.append("\n    AND V.CUSTOMER_CATEGORY ='"+curBean.getCustCatNo()+"'");
			sql.append("\n    AND Z.ZONE ='"+curBean.getSalesZone()+"'");
			sql.append("\n    AND V.salesrep_id ="+curBean.getSalesrepId());
			sql.append("\n    AND V.salesrep_id = Z.salesrep_id");
			sql.append("\n  ) as SUM3, ");*/
			
			sql.append("\n  (SELECT max(P.unit_price) from apps.xxpens_om_price_list_v P " );
			sql.append("\n   where P.INVENTORY_ITEM_ID =L.INVENTORY_ITEM_ID " );
			sql.append("\n   and P.list_header_id ="+priceListId+") as price ,");
			
			sql.append("\n  '"+curBean.getSessionId()+"' as SESSION_ID");
			sql.append("\n  FROM PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L L ");
			
			//old code
			/*sql.append("\n  LEFT OUTER JOIN ( ");
			sql.append("\n    SELECT V.INVENTORY_ITEM_ID ,V.SUM3,V.SUM12 ");
			sql.append("\n    FROM APPS.XXPENS_BI_MST_SALES_AVG_V V,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n    WHERE V.PERIOD ='"+curBean.getPeriod()+"'");
			sql.append("\n    AND V.CUSTOMER_CATEGORY ='"+curBean.getCustCatNo()+"'");
			sql.append("\n    AND Z.ZONE ='"+curBean.getSalesZone()+"'");
			sql.append("\n    AND V.salesrep_id ="+curBean.getSalesrepId());
			sql.append("\n    AND V.salesrep_id = Z.salesrep_id");
			sql.append("\n  ) P ON L.INVENTORY_ITEM_ID = P.INVENTORY_ITEM_ID  ");*/
			
			sql.append("\n  WHERE L.ID="+idCopy);
			
			//** Insert Line ITEM is Exist MKT set(Current Period) Only**/
			sql.append("\n  AND L.INVENTORY_ITEM_ID IN( ");
			sql.append("\n      select L.INVENTORY_ITEM_ID FROM");
			sql.append("\n      PENSBI.XXPENS_BI_SALES_TARGET_TT_L L ,PENSBI.XXPENS_BI_SALES_TARGET_TT M ");
			sql.append("\n      WHERE M.ID = L.ID ");
			sql.append("\n      and M.status ='"+SalesTargetConstants.STATUS_POST+"'");
			sql.append("\n      and M.target_month = '"+Utils.isNull(curBean.getTargetMonth())+"'");
			sql.append("\n      and M.target_quarter = '"+Utils.isNull(curBean.getTargetQuarter())+"'");
			sql.append("\n      and M.target_year = '"+Utils.isNull(curBean.getTargetYear())+"'");
			sql.append("\n      and M.customer_category = '"+Utils.isNull(curBean.getCustCatNo())+"'");
			sql.append("\n      and M.zone = '"+Utils.isNull(curBean.getSalesZone())+"'");
			sql.append("\n  ) ");
		   //logger.debug("sql:"+sql);
		    
			ps = conn.prepareStatement(sql.toString());
			ps.execute();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {}
		}
		return h;
	}
	
	public static boolean salesTargetIsExistTEMP(Connection conn,SalesTargetBean cri,User user,String status) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean h = false;
		try {
			cri = SalesTargetDAO.convertCriteria(cri);
			sql.append("\n  select count(*) as c");
			sql.append("\n  from XXPENS_BI_SALES_TARGET_TEMP M ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  where M.salesrep_id = Z.salesrep_id");
			sql.append("\n  and M.division = 'B' ");//B = credit,van sales
			if( !Utils.isNull(status).equals("")){
			   sql.append("\n  and M.status ='"+status+"'");
			}
			sql.append("\n  and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			sql.append("\n  and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			sql.append("\n  and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
			
			//filter by criteria
			if( !Utils.isNull(cri.getBrand()).equals("")){
				sql.append("\n  and M.brand = '"+cri.getBrand()+"'");
			}
			if( !Utils.isNull(cri.getCustCatNo()).equals("")){
				sql.append("\n  and M.CUSTOMER_CATEGORY = '"+Utils.isNull(cri.getCustCatNo())+"'");
			}
			if( !Utils.isNull(cri.getSalesZone()).equals("")){
				sql.append("\n  and Z.zone = '"+Utils.isNull(cri.getSalesZone())+"'");
			}
			
			//Case check by Exist By brand salesrepId
			if( !Utils.isNull(cri.getSalesrepId()).equals("")){
				sql.append("\n  and M.salesrep_id = "+Utils.isNull(cri.getSalesrepId())+"");
			}
			
			//filter by user login Except admin
		    if( !user.getUserName().equalsIgnoreCase("admin")){
		    	sql.append("\n  and (M.CUSTOMER_CATEGORY ,Z.zone) in(");
		    	sql.append("\n    select CUST_CAT_NO,zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT" );
		    	sql.append("\n    where user_name = '"+Utils.isNull(user.getUserName())+"'");
		    	sql.append("\n  )");
		    }else{
		    	//admin all cat
		    	sql.append("\n  and (M.CUSTOMER_CATEGORY,Z.zone) in(");
		    	sql.append("\n    select CUST_CAT_NO,zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT" );
		    	sql.append("\n  )");
		    }
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   if(rst.getInt("c") >0){
				   h = true;
			   }
			}//if
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
	
	public static boolean chkAllTargetIsMKTPost(Connection conn,SalesTargetBean cri,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean h = false;
		try {
			cri = SalesTargetDAO.convertCriteria(cri);
			sql.append("\n  select M.id from PENSBI.XXPENS_BI_SALES_TARGET_TT M ");
			sql.append("\n  where 1=1");
			sql.append("\n  and M.status ='"+SalesTargetConstants.STATUS_POST+"'");
			sql.append("\n  and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			sql.append("\n  and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			sql.append("\n  and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
			//filter by criteria
			if( !Utils.isNull(cri.getBrand()).equals("")){
				sql.append("\n   and M.brand = '"+cri.getBrand()+"'");
			}
			if( !Utils.isNull(cri.getCustCatNo()).equals("")){
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(cri.getCustCatNo())+"'");
			}
			if( !Utils.isNull(cri.getSalesZone()).equals("")){
				sql.append("\n and M.zone = '"+Utils.isNull(cri.getSalesZone())+"'");
			}
			
			//filter by user login Except admin
		    if( !user.getUserName().equalsIgnoreCase("admin")){
		    	sql.append("\n  and (M.CUSTOMER_CATEGORY ,M.zone) in(");
		    	sql.append("\n    select cust_cat_no,zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT" );
		    	sql.append("\n    where user_name = '"+Utils.isNull(user.getUserName())+"'");
		    	sql.append("\n  )");
		    }else{
		    	//admin all cat
		    	sql.append("\n  and (M.CUSTOMER_CATEGORY,M.zone) in(");
		    	sql.append("\n    select CUST_CAT_NO,zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT" );
		    	sql.append("\n  )");
		    }
		
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   if(rst.getInt("id") >0){
				   h = true;
			   }
			}//if
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

	public static boolean getBrandIsMKTPost(Connection conn,SalesTargetBean cri,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean h = false;
		try {
			cri = SalesTargetDAO.convertCriteria(cri);
			sql.append("\n  select M.id ");
			sql.append("\n  from XXPENS_BI_SALES_TARGET_TT M ");
			sql.append("\n  where 1=1");
			sql.append("\n  and M.status ='"+SalesTargetConstants.STATUS_POST+"'");
			sql.append("\n  and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			sql.append("\n  and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			sql.append("\n  and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
			sql.append("\n  and M.zone ='"+Utils.isNull(cri.getSalesZone())+"'");
			sql.append("\n  and M.brand ='"+Utils.isNull(cri.getBrand())+"'");
			
			//filter by user login Except admin
		    if( !user.getUserName().equalsIgnoreCase("admin")){
		    	sql.append("\n  and (M.CUSTOMER_CATEGORY,M.zone) in(");
		    	sql.append("\n    select cust_cat_no,zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT" );
		    	sql.append("\n    where user_name = '"+Utils.isNull(user.getUserName())+"'");
		    	sql.append("\n  )");
		    }else{
		    	//admin all cat
		    	sql.append("\n  and (M.CUSTOMER_CATEGORY,M.zone) in(");
		    	sql.append("\n    select CUST_CAT_NO,zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT" );
		    	sql.append("\n  )");
		    }
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   if(rst.getInt("id") >0){
				   h = true;
			   }
			}//if
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
}
