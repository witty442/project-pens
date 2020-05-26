package com.isecinc.pens.web.salestarget;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcessAll;

public class SalesTargetCopy {
	
   /**MT ONLY **/
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static String copyFromLastMonthMT(User user,SalesTargetBean destBean,String pageName)  throws Exception {
		logger.debug("copyFromLastMonth MT");
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
			boolean curDataExist = salesTargetIsExist(conn,destBean,user,"");
			if(curDataExist ==true){
				errorCode ="DATA_CUR_EXIST_EXCEPTION";
				logger.debug("ErrorCode["+errorCode+"]");
			}else{
				//setData prevMonth
				sourceBean = new SalesTargetBean();
				curCal.add(Calendar.MONTH, -1);
				sourceBean.setStartDate(DateUtil.stringValue(curCal.getTime(), DateUtil.DD_MMM_YYYY));
				logger.debug("prevDate Time["+curCal.getTime()+"]");
				//convert
				sourceBean.setPeriod(DateUtil.stringValue(curCal.getTime(),"MMM-yy").toUpperCase());
				sourceBean.setBrand(destBean.getBrand());
				sourceBean.setSalesChannelNo(destBean.getSalesChannelNo());
				sourceBean.setCustCatNo(destBean.getCustCatNo());
				sourceBean = SalesTargetDAO.convertCriteria(sourceBean);
				sourceBean.setStatus(SalesTargetConstants.STATUS_FINISH);// get only status Finish
				
				logger.info("Copy from Period["+sourceBean.getPeriod()+"] to ["+destBean.getPeriod()+"]");
				
				//validate prev month data is exist (Status=Finish)
				boolean prevDataExist = salesTargetIsExist(conn,sourceBean,user,SalesTargetConstants.STATUS_FINISH);
				
				if(prevDataExist == false){
					errorCode ="DATA_PREV_NOT_FOUND";
				}else{
					//insert data Loop By brand
					insertDataByBrand(conn, sourceBean, destBean, user);
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
	
	public static boolean insertDataByBrand(Connection conn,SalesTargetBean sourceBean,SalesTargetBean destBean,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean h = false;
		String priceListId  ="";
		BigDecimal idNew = new BigDecimal("0");
		try {
			sourceBean = SalesTargetDAO.convertCriteria(sourceBean);
			
			sql.append("\n select id ,customer_category ,salesrep_id ");
			sql.append("\n ,customer_id ,sales_channel ");
			sql.append("\n from XXPENS_BI_SALES_TARGET_TEMP M where 1=1 ");
			sql.append("\n and division <> 'B' ");//B = credit,van sales
			sql.append("\n and M.status ='"+SalesTargetConstants.STATUS_FINISH+"'");
			sql.append("\n and M.brand = '"+sourceBean.getBrand()+"'");
			sql.append("\n and M.target_month = '"+Utils.isNull(sourceBean.getTargetMonth())+"'");
			sql.append("\n and M.target_quarter = '"+Utils.isNull(sourceBean.getTargetQuarter())+"'");
			sql.append("\n and M.target_year = '"+Utils.isNull(sourceBean.getTargetYear())+"'");
			sql.append("\n and M.sales_channel = '"+Utils.isNull(sourceBean.getSalesChannelNo())+"'");
			sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(sourceBean.getCustCatNo())+"'");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
				//insert head
				idNew = insertHead(conn, destBean, rst.getBigDecimal("id"));
				//set for get Avg Order
				destBean.setCustCatNo(Utils.isNull(rst.getString("customer_category")));
				destBean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				destBean.setCustomerId(Utils.isNull(rst.getString("customer_id")));
				
				//find priceListId
				priceListId = SalesTargetUtils.getPriceListId(conn,Utils.isNull(rst.getString("sales_channel")),Utils.isNull(rst.getString("customer_category")));
				
				//insert Line
			    insertLine(conn, priceListId, destBean, rst.getBigDecimal("id"), idNew);
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
	
	public static BigDecimal insertHead(Connection conn,SalesTargetBean curBean,BigDecimal idCopy) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		BigDecimal idNew = null;
		try {
			idNew =SequenceProcessAll.getIns().getNextValue("XXPENS_BI_SALES_TARGET_TEMP");
			if(idNew.compareTo(new BigDecimal("0")) ==0){
				idNew = new BigDecimal("1");
			}
			sql.append("\n  INSERT INTO XXPENS_BI_SALES_TARGET_TEMP ");
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
			sql.append("\n  'Open' as STATUS, "); 
			sql.append("\n  '"+curBean.getCreateUser()+"_copy' as CREATE_USER, ");
			sql.append("\n  sysdate as CREATE_DATE, ");
			sql.append("\n  '' as UPDATE_USER, ");
			sql.append("\n  null as UPDATE_DATE, ");
			sql.append("\n  '"+curBean.getPeriod()+"' as PERIOD ,");
			sql.append("\n  null as invoiced_qty ,");
			sql.append("\n  null as invoiced_amt ,");
			sql.append("\n  null as estimate_qty ,");
			sql.append("\n  null as estimate_amt ,");
			sql.append("\n  null as price,");
			sql.append("\n  '"+curBean.getSessionId()+"' as session_id ,");
			sql.append("\n  0 as user_input_id ");
			sql.append("\n  FROM XXPENS_BI_SALES_TARGET_TEMP WHERE ID="+idCopy);
		   // logger.debug("sql:"+sql);
			
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
	
	public static boolean insertLine(Connection conn,String priceListId,SalesTargetBean curBean,BigDecimal idCopy,BigDecimal idNew) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		boolean h = false;
		try {
			sql.append("\n  INSERT INTO XXPENS_BI_SALES_TARGET_TEMP_L");
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
			//OLD
			//sql.append("\n  p.SUM12, ");//AMT_AVG12
			//sql.append("\n  p.SUM3, ");//AMT_AVG3
			
			//NEW 21/11/2019
			sql.append("\n  ( SELECT MAX(V.SUM12) ");
			sql.append("\n    FROM APPS.XXPENS_BI_MST_SALES_AVG_V V,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n    WHERE V.PERIOD ='"+curBean.getPeriod()+"'");
			sql.append("\n    AND V.CUSTOMER_CATEGORY ='"+curBean.getCustCatNo()+"'");
			sql.append("\n    AND Z.ZONE ='"+curBean.getSalesZone()+"'");
			sql.append("\n    AND V.salesrep_id ="+curBean.getSalesrepId());
			sql.append("\n    AND V.salesrep_id = Z.salesrep_id");
			sql.append("\n  ) as SUM12, ");

			sql.append("\n  ( SELECT MAX(V.SUM3) ");
			sql.append("\n    FROM APPS.XXPENS_BI_MST_SALES_AVG_V V,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n    WHERE V.PERIOD ='"+curBean.getPeriod()+"'");
			sql.append("\n    AND V.CUSTOMER_CATEGORY ='"+curBean.getCustCatNo()+"'");
			sql.append("\n    AND Z.ZONE ='"+curBean.getSalesZone()+"'");
			sql.append("\n    AND V.salesrep_id ="+curBean.getSalesrepId());
			sql.append("\n    AND V.salesrep_id = Z.salesrep_id");
			sql.append("\n  ) as SUM3, ");
			
			//old version
			/*sql.append("\n  ( SELECT max(P.price) from xxpens_bi_mst_price_list P " );
			sql.append("\n    where P.product_id =L.INVENTORY_ITEM_ID " );
			sql.append("\n    and P.primary_uom_code ='Y' " );
			sql.append("\n    and P.pricelist_id ="+priceListId+") as price ");//Price
*/			
			//new version 06/2019
			sql.append("\n  (SELECT max(P.unit_price) from apps.xxpens_om_price_list_v P " );
			sql.append("\n   where P.INVENTORY_ITEM_ID = L.INVENTORY_ITEM_ID " );
			//witty edit:04/02/2020 (get only active )
			sql.append("\n   and P.end_date_active is null ");
			sql.append("\n   and P.list_header_id in("+ SQLHelper.converToTextSqlIn(priceListId)+")) as price ,");
			sql.append("\n  '"+curBean.getSessionId()+"' as session_id ,");
			sql.append("\n  0 as user_input_id");
			sql.append("\n  FROM XXPENS_BI_SALES_TARGET_TEMP_L L ");

			sql.append("\n  WHERE L.ID="+idCopy);
		    logger.debug("sql:"+sql);
		    
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
	
	public static boolean salesTargetIsExist(Connection conn,SalesTargetBean cri,User user,String status) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean h = false;
		try {
			cri = SalesTargetDAO.convertCriteria(cri);
			sql.append("\n  select sum(item_count) as item_count from(");
			sql.append("\n   select M.id ");
			sql.append("\n   ,(select count(*) from XXPENS_BI_SALES_TARGET_TEMP L ");
			sql.append("\n     where L.id= M.id) as item_count");
			sql.append("\n   from XXPENS_BI_SALES_TARGET_TEMP  M ");
			sql.append("\n   where 1=1 ");
			sql.append("\n   and division <> 'B' ");//B = credit,van sales
			if( !Utils.isNull(status).equals("")){
			  sql.append("\n   and M.status ='"+status+"'");
			}
			if( !Utils.isNull(cri.getBrand()).equals("")){
			   sql.append("\n   and M.brand = '"+cri.getBrand()+"'");
			}
			if( !Utils.isNull(cri.getTargetMonth()).equals("")){
				sql.append("\n  and M.target_month = '"+Utils.isNull(cri.getTargetMonth())+"'");
			}
			if( !Utils.isNull(cri.getTargetQuarter()).equals("")){
				sql.append("\n  and M.target_quarter = '"+Utils.isNull(cri.getTargetQuarter())+"'");
			}
			if( !Utils.isNull(cri.getTargetYear()).equals("")){
				sql.append("\n  and M.target_year = '"+Utils.isNull(cri.getTargetYear())+"'");
			}
			if( !Utils.isNull(cri.getSalesChannelNo()).equals("")){
				sql.append("\n and M.sales_channel = '"+Utils.isNull(cri.getSalesChannelNo())+"'");
			}
			if( !Utils.isNull(cri.getCustCatNo()).equals("")){
				sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(cri.getCustCatNo())+"'");
			}
			sql.append("\n ) A group by A.id ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   if(rst.getInt("item_count") >0){
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
