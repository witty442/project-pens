package com.isecinc.pens.web.batchtask.subtask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class GenStockOnhandRepTempBigCSubTask {
	public static Logger logger = Logger.getLogger("PENS");
	public static String PARAM_ASOF_DATE ="AS_OF_DATE";
	public static String PARAM_STORE_CODE ="STORE_CODE";
	
	
	public static MonitorItemBean processStockOnhandRepTempBigC(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean,String backSalesDay) throws Exception{
		int status = Constants.STATUS_FAIL;
		int successCount = 0;int dataCount=0;int failCount = 0;
		int r = 0;
		boolean foundError = false;boolean passValid = false;String lineMsg = "";String errorMsg = "";
		PreparedStatement ps =null,psIns=null,psDel=null;
		ResultSet rst = null;
		int index = 0;
		StringBuffer sql = new StringBuffer("");
		try{
		    //get parameter
			String asOfDate = monitorModel.getBatchParamMap().get(PARAM_ASOF_DATE);
			String storeCode = monitorModel.getBatchParamMap().get(PARAM_STORE_CODE);
			
			logger.debug("asOfDate:"+asOfDate);
			logger.debug("storeCode:"+storeCode);
			
			psDel = conn.prepareStatement("delete from PENSBI.BME_TEMP_ONHAND_REP where store_code ='"+storeCode+"'");
			r = psDel.executeUpdate();
			logger.debug("delete from PENSBI.BME_TEMP_ONHAND_REP where store_code ='"+storeCode+"':result["+r+"]");
			
			//gen sql
			ReportAllBean cri = new ReportAllBean();
			cri.setAsOfDate(asOfDate);
			cri.setPensCustCodeFrom(storeCode);
			Date initDate = new SummaryDAO().searchInitDateBigC(conn,cri.getPensCustCodeFrom());
			
			sql = genSQL(conn,cri,initDate,backSalesDay);
			
			//sql insert 
			String sqlIns  = "INSERT INTO PENSBI.BME_TEMP_ONHAND_REP";
			       sqlIns +="(STORE_CODE, GROUP_CODE, PENS_ITEM, SHOP_ONHAND_QTY,SALES_QTY, CREATE_USER, CREATE_DATE) ";
			       sqlIns +=" VALUES(?, ?, ?, ?, ?, ?,?)";
			psIns = conn.prepareStatement(sqlIns.toString());
					
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
				psIns.setString(++index, rst.getString("store_code"));
				psIns.setString(++index, rst.getString("group_type"));
				psIns.setString(++index, rst.getString("pens_item"));
				psIns.setDouble(++index, rst.getDouble("onhand_qty"));
				psIns.setDouble(++index, rst.getDouble("sales_qty"));
				psIns.setString(++index, monitorModel.getCreateUser());
				psIns.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
				
				psIns.addBatch();
				index = 0;
			}
		
			if(foundError ==false){
				logger.info("Excute Batch");
				psIns.executeBatch();
				status = Constants.STATUS_SUCCESS;
			}
			
			monitorItemBean.setStatus(status);
			monitorItemBean.setSuccessCount(successCount);//successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setDataCount(dataCount);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ps.close();ps =null;
			psIns.close();psIns =null;
			psDel.close();psDel=null;
		}
		return monitorItemBean;	
	}
	
	public static StringBuffer genSQL(Connection conn,ReportAllBean c,Date initDate,String backSalesDay) throws Exception{
		StringBuffer sql = new StringBuffer();
		String storeCode =Constants.STORE_TYPE_BIGC_CODE;
		try {
			String christSalesDateStr ="";
			if( !Utils.isNull(c.getAsOfDate()).equals("")){
				Date d = DateUtil.parse(c.getAsOfDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = DateUtil.stringValue(d, DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			String initDateStr ="";
			if( initDate != null){
				initDateStr = DateUtil.stringValue(initDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			
			  //Max SalesDate of Lotus
			String maxSalesDate = getMaxSalesDateBigCTemp(conn, c.getPensCustCodeFrom());
		    if(Utils.isNull(maxSalesDate).equals("")){
		    	//sysdate
		    	maxSalesDate = DateUtil.stringValue(new Date(), "dd/MM/yyyy");
		    }
		    
			sql.append("\n SELECT A.* ");
			
			//Get Sales Qty backSalesDay(config)
			sql.append("\n,(SELECT NVL(SUM(QTY),0)");
			sql.append("\n  FROM PENSBI.PENSBME_SALES_FROM_BIGC_TEMP S ");
			sql.append("\n  WHERE S.PENS_ITEM = A.PENS_ITEM ");
			sql.append("\n  AND S.PENS_CUST_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+")");
			sql.append("\n  AND S.sales_date > (to_date('"+maxSalesDate+"','dd/MM/yyyy') - "+backSalesDay+") ");
			sql.append("\n  AND S.sales_date <= to_date('"+maxSalesDate+"','dd/MM/yyyy')" );
			sql.append("\n ) as sales_qty \n");
			
			
			sql.append("\n FROM(");
			
			sql.append("\n SELECT M.*");
			sql.append("\n , NVL(INIT_MTT.INIT_SALE_QTY,0) AS INIT_SALE_QTY");
			sql.append("\n , NVL(TRANS_IN.TRANS_IN_QTY,0) AS TRANS_IN_QTY");
			sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
			sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			sql.append("\n , NVL(SALE_ADJUST.SALE_ADJUST_QTY,0) AS SALE_ADJUST_QTY");
			
			sql.append("\n ,(NVL(INIT_MTT.INIT_SALE_QTY,0) + NVL(TRANS_IN.TRANS_IN_QTY,0))" +
					"-(NVL(SALE_OUT.SALE_OUT_QTY,0) + NVL(SALE_RETURN.SALE_RETURN_QTY,0) + NVL(SALE_ADJUST.SALE_ADJUST_QTY,0)) " +
					" ONHAND_QTY");
			
			sql.append("\n FROM(  ");
			//List Main Product from BME_CONFIG_REP
			sql.append("\n\t  SELECT RP.STORE_CODE ");
			sql.append("\n\t  ,RP.MATERIAL_MASTER as group_type ");
			sql.append("\n\t  ,M.PENS_VALUE as pens_item");
			sql.append("\n\t  from PENSBI.BME_CONFIG_REP RP,PENSBI.PENSBME_MST_REFERENCE M");
			sql.append("\n\t  where RP.MATERIAL_MASTER = M.interface_value");
			sql.append("\n\t  and M.reference_code = 'LotusItem'");
			sql.append("\n\t  and RP.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			       	
            sql.append("\n )M ");
            sql.append("\n LEFT OUTER JOIN(	 ");
	        /** INIT MTT STOCK **/
   		    sql.append("\n SELECT ");
				sql.append("\n L.CUST_NO as STORE_CODE,L.PENS_ITEM, L.GROUP_CODE as group_type,");
				sql.append("\n SUM(QTY) AS INIT_SALE_QTY ");
				sql.append("\n FROM PENSBI.PENSBME_BIGC_INIT_STK H,PENSBI.PENSBME_BIGC_ONHAND_INIT_STK L");
				sql.append("\n WHERE 1=1 ");
				sql.append("\n and H.cust_no = L.cust_no  ");
				sql.append("\n and H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
				if( !Utils.isNull(initDateStr).equals("")){
				   sql.append("\n AND H.COUNT_STK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n AND L.CUST_NO LIKE '"+storeCode+"%'");
				sql.append("\n AND L.CUST_NO IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				//filter Mat in BME_CONFIG_REP
				sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND L.GROUP_CODE"));
				
				sql.append("\n  GROUP BY L.CUST_NO,L.PENS_ITEM,L.GROUP_CODE ");
				sql.append("\n )INIT_MTT ");
				sql.append("\n ON  M.pens_item = INIT_MTT.pens_item ");	 
				sql.append("\n AND M.STORE_CODE = INIT_MTT.STORE_CODE ");		
				sql.append("\n AND M.group_type = INIT_MTT.group_type ");		
				
		   sql.append("\n LEFT OUTER JOIN(	 ");
		        /** NEW CODE :21/08/2562 **/
	 		    sql.append("\n SELECT L.PENS_CUST_CODE AS STORE_CODE,L.PENS_ITEM, ");
			    sql.append("\n L.PENS_GROUP_TYPE as group_type, ");
				sql.append("\n NVL(SUM(L.QTY),0) AS SALE_OUT_QTY ");
				sql.append("\n FROM PENSBI.PENSBME_SALES_FROM_BIGC_TEMP L");
				sql.append("\n WHERE 1=1 ");
				sql.append("\n AND L.PENS_CUST_CODE LIKE '"+storeCode+"%'");
				if(initDate != null){
					 sql.append("\n AND L.sales_date  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND L.sales_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND L.sales_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n AND L.PENS_CUST_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				
				//filter Mat in BME_CONFIG_REP
				sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND L.PENS_GROUP_TYPE"));
				
				sql.append("\n  GROUP BY L.PENS_CUST_CODE,L.PENS_ITEM,L.PENS_GROUP_TYPE ");
				sql.append("\n ) SALE_OUT ");
				
				sql.append("\n ON  M.pens_item = SALE_OUT.pens_item ");	 
				sql.append("\n AND M.group_type = SALE_OUT.group_type ");
				sql.append("\n AND M.STORE_CODE = SALE_OUT.STORE_CODE ");	
					
			sql.append("\n LEFT OUTER JOIN( ");
			    sql.append("\n SELECT  ");
			    sql.append("\n A.STORE_CODE,A.pens_item, A.group_type,NVL(SUM(A.SALE_IN_QTY),0)  as TRANS_IN_QTY ");
			    sql.append("\n FROM (  ");
						sql.append("\n SELECT  ");
						sql.append("\n M.STORE_CODE,M.ITEM as pens_item, M.group_code as group_type,");
						sql.append("\n NVL(SUM(QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM PENSBI.PENSBME_ORDER M ");
						sql.append("\n WHERE 1=1  ");
						sql.append("\n AND M.STORE_CODE LIKE '"+storeCode+"%'");
						if(initDate != null){
							 sql.append("\n AND M.order_date  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND M.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND M.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
					    sql.append("\n AND M.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					    
					    //filter Mat in BME_CONFIG_REP
						sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND M.group_code"));
						
						sql.append("\n AND M.STORE_CODE LIKE '"+storeCode+"%'");
						sql.append("\n GROUP BY M.STORE_CODE,M.item,M.group_code ");
						
						sql.append("\n UNION ALL  ");
						
						sql.append("\n SELECT  ");
						sql.append("\n M.STORE_CODE,I.pens_item, I.group_code as group_type, ");
						sql.append("\n NVL(COUNT(*),0)  as SALE_IN_QTY ");
						sql.append("\n FROM PENSBI.PENSBME_PICK_STOCK M ,PENSBI.PENSBME_PICK_STOCK_I I ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND M.issue_req_no = I.issue_req_no ");
						sql.append("\n AND M.issue_req_status ='"+PickConstants.STATUS_ISSUED+"'");
						sql.append("\n AND M.STORE_CODE LIKE '"+storeCode+"%'");
						if(initDate != null){
							 sql.append("\n AND M.ISSUE_REQ_DATE  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						sql.append("\n AND M.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						//filter Mat in BME_CONFIG_REP
						sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND I.group_code"));
						
						sql.append("\n AND M.STORE_CODE LIKE '"+storeCode+"%'");
						sql.append("\n GROUP BY M.STORE_CODE,I.pens_item,I.group_code");
						
						sql.append("\n UNION ALL  ");
						
						sql.append("\n SELECT  ");
						sql.append("\n M.CUSTOMER_NO AS STORE_CODE,I.pens_item, I.group_code as group_type,");
						sql.append("\n NVL(SUM(I.ISSUE_QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM PENSBI.PENSBME_STOCK_ISSUE M ,PENSBI.PENSBME_STOCK_ISSUE_ITEM I ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND M.issue_req_no = I.issue_req_no  ");
						sql.append("\n AND M.status ='"+PickConstants.STATUS_ISSUED+"'");
						if(initDate != null){
							 sql.append("\n AND M.ISSUE_REQ_DATE  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						sql.append("\n AND M.CUSTOMER_NO LIKE '"+storeCode+"%'");
						sql.append("\n AND M.customer_no IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						//filter Mat in BME_CONFIG_REP
						sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND I.group_code"));
						
						sql.append("\n AND M.CUSTOMER_NO LIKE '"+storeCode+"%'");
						sql.append("\n GROUP BY M.CUSTOMER_NO,I.pens_item,I.group_code ");
						
			  sql.append("\n ) A ");
			  sql.append("\n GROUP BY A.STORE_CODE,A.pens_item, A.group_type");
			sql.append("\n )TRANS_IN ");
			
			sql.append("\n ON  M.pens_item = TRANS_IN.pens_item ");
			sql.append("\n AND M.group_type  = TRANS_IN.group_type ");
			sql.append("\n AND M.STORE_CODE = TRANS_IN.STORE_CODE ");	
			
			sql.append("\n LEFT OUTER JOIN ( ");
					sql.append("\n SELECT J.STORE_CODE,I.pens_item,I.group_code as group_type,");
					sql.append("\n NVL(COUNT(*),0) as SALE_RETURN_QTY ");
					sql.append("\n  FROM PENSBI.PENSBME_PICK_JOB J   ");
					sql.append("\n ,PENSBI.PENSBME_PICK_BARCODE B ,PENSBI.PENSBME_PICK_BARCODE_ITEM I  ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND J.job_id = B.job_id  ");
					sql.append("\n AND B.job_id = I.job_id ");
					sql.append("\n AND B.box_no = I.box_no ");
					sql.append("\n AND J.status = '"+PickConstants.STATUS_CLOSE+"' ");
					sql.append("\n AND ( I.STATUS  <> '"+PickConstants.STATUS_CANCEL+"' AND I.STATUS  <> '"+PickConstants.STATUS_OPEN+"' )");
					
					if(initDate != null){
						 sql.append("\n AND J.close_date  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND J.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND J.close_date   <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					sql.append("\n AND J.STORE_CODE LIKE '"+storeCode+"%'");
					sql.append("\n AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					//filter Mat in BME_CONFIG_REP
					sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND I.group_code"));
					
					sql.append("\n AND J.STORE_CODE LIKE '"+storeCode+"%'");
					sql.append("\n GROUP BY J.STORE_CODE,I.pens_item ,I.group_code ");

			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON  M.pens_item = SALE_RETURN.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
			sql.append("\n  AND M.STORE_CODE = SALE_RETURN.STORE_CODE ");	
			
			sql.append("\n LEFT OUTER JOIN ( ");
			    sql.append("\n SELECT J.STORE_CODE, J.item_adjust as pens_item,J.item_adjust_desc as group_type ");
				sql.append("\n ,NVL(SUM(J.ITEM_ADJUST_QTY),0) as SALE_ADJUST_QTY ");
				sql.append("\n FROM PENSBI.PENSBME_ADJUST_SALES J ");
				sql.append("\n WHERE 1=1   ");
				sql.append("\n AND J.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
				
				if(initDate != null){
					 sql.append("\n AND J.transaction_date  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND J.transaction_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND J.transaction_date   <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n AND J.STORE_CODE LIKE '"+storeCode+"%'");
				sql.append("\n AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				//filter Mat in BME_CONFIG_REP
				sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND J.item_adjust_desc"));
				sql.append("\n AND J.STORE_CODE LIKE '"+storeCode+"%'");
				sql.append("\n GROUP BY J.STORE_CODE,J.item_adjust , J.item_adjust_desc ");

			sql.append("\n )SALE_ADJUST ");
			sql.append("\n  ON  M.pens_item = SALE_ADJUST.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_ADJUST.group_type ");
			sql.append("\n  AND M.STORE_CODE = SALE_ADJUST.STORE_CODE ");	
	
			sql.append("\n ) A ");
			sql.append("\n ORDER BY A.store_code ,A.group_type ,A.pens_item asc ");

			//debug write sql to file
			if(logger.isDebugEnabled()){
			   FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
			}
			//logger.debug("sql:"+sql);

		} catch (Exception e) {
			throw e;
		} finally {
		
		}
		return sql;
}

 public static String genWhereFilterConfigRep(String storeCode,String colName){
	String sql = colName+" IN(SELECT MATERIAL_MASTER FROM PENSBI.BME_CONFIG_REP WHERE STORE_CODE ='"+storeCode+"')";
	return sql;
 }
 public static String getMaxSalesDateBigCTemp(Connection conn,String storeCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String maxSalesDate = "";
		try {
			sql.append("\n select distinct max(sales_date) as sales_date ");
			sql.append("\n FROM PENSBI.PENSBME_SALES_FROM_BIGC_TEMP WHERE  PENS_CUST_CODE ='"+storeCode+"'");
		
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				maxSalesDate = DateUtil.stringValue(rst.getDate("sales_date"),DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return maxSalesDate;
	} 
}
