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
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.pens.util.Constants;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class GenStockOnhandRepTempLotusSubTask {
	public static Logger logger = Logger.getLogger("PENS");
	public static String PARAM_ASOF_DATE ="AS_OF_DATE";
	public static String PARAM_STORE_CODE ="STORE_CODE";
	
	public static MonitorItemBean processStockOnhandRepTempLotus(Connection connMonitor ,Connection conn
			,MonitorBean monitorModel ,MonitorItemBean monitorItemBean,String backSalesDay) throws Exception{
		int status = Constants.STATUS_FAIL;
		int successCount = 0;int dataCount=0;int failCount = 0;
		int r = 0;
		boolean foundError = false;boolean passValid = false;
		String lineMsg = "";String errorMsg = "";
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
			
			sql = genSQLStockOnhandRepTempLotus(conn,cri,backSalesDay);
			//sql insert 
			String sqlIns  = "INSERT INTO PENSBI.BME_TEMP_ONHAND_REP";
			       sqlIns +="(STORE_CODE, GROUP_CODE, PENS_ITEM, SHOP_ONHAND_QTY,SALES_QTY, CREATE_USER, CREATE_DATE) ";
			       sqlIns +=" VALUES(?, ?, ?, ?, ?, ?,?)";
			psIns = conn.prepareStatement(sqlIns.toString());
					
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
				psIns.setString(++index, rst.getString("customer_code"));
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
	
	
 public static StringBuffer genSQLStockOnhandRepTempLotus(Connection conn,ReportAllBean c,String backSalesDay) throws Exception{
	StringBuffer sql = new StringBuffer();
	//Date asofDate = null;
	BMEControlBean control = null;
	String maxSalesDate = "";
	try {
		control = calcDueDateLotus(conn,c.getPensCustCodeFrom(),c.getAsOfDate());
		
        //Max SalesDate of Lotus
		maxSalesDate = getMaxSalesDateLotus(conn, c.getPensCustCodeFrom());
	    if(Utils.isNull(maxSalesDate).equals("")){
	    	//sysdate
	    	maxSalesDate = DateUtil.stringValue(new Date(), "dd/MM/yyyy");
	    }
	    
		sql.append("\n SELECT A.* " );
		
		//Get Sales Qty backSalesDay(config)
		sql.append("\n,(SELECT NVL(SUM(QTY),0)");
		sql.append("\n  FROM PENSBI.PENSBME_SALES_FROM_LOTUS S ");
		sql.append("\n  WHERE S.PENS_ITEM = A.PENS_ITEM ");
		sql.append("\n  AND S.PENS_CUST_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+")");
		sql.append("\n  AND S.sales_date > (to_date('"+maxSalesDate+"','dd/MM/yyyy') - "+backSalesDay+") ");
		sql.append("\n  AND S.sales_date <= to_date('"+maxSalesDate+"','dd/MM/yyyy')  ");
		sql.append("\n ) as sales_qty \n");
		
		sql.append("\n FROM( ");
		sql.append("\n SELECT A.customer_code ,A.group_type ,A.pens_item");
		sql.append("\n ,SUM(A.BEGINING_QTY) as BEGINING_QTY");
		sql.append("\n ,SUM(A.SALE_IN_QTY) AS SALE_IN_QTY");
		sql.append("\n ,SUM(A.SALE_OUT_QTY) AS SALE_OUT_QTY");
		sql.append("\n ,SUM(A.SALE_RETURN_QTY) AS SALE_RETURN_QTY");
		sql.append("\n ,SUM(A.ADJUST_QTY) AS ADJUST_QTY");
		sql.append("\n ,SUM(A.STOCK_SHORT_QTY) AS STOCK_SHORT_QTY");
		sql.append("\n ,SUM(A.ONHAND_QTY) AS ONHAND_QTY");
		sql.append("\n FROM(");
			sql.append("\n SELECT M.*");
			sql.append("\n , NVL(ENDING.BEGINING_QTY,0) as BEGINING_QTY ");
			sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
			sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
			sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			sql.append("\n , NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) as ADJUST_QTY ");
			sql.append("\n , NVL(STOCK_SHORT.STOCK_SHORT_QTY,0) as STOCK_SHORT_QTY ");
			
			sql.append("\n , NVL(ENDING.BEGINING_QTY,0)+ (NVL(SALE_IN.SALE_IN_QTY,0) " + //beginQty + SaleIn
							"\n -(" +
							"\n    NVL(SALE_OUT.SALE_OUT_QTY,0)  " +//SaleOut
							"\n  + NVL(SALE_RETURN.SALE_RETURN_QTY,0) " +//Return
							"\n  )" +
							"\n  + ( NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) )" + //Adjust
							"\n  + NVL(STOCK_SHORT.STOCK_SHORT_QTY,0) " +//Short
					   "\n  ) ONHAND_QTY");
			
	sql.append("\n FROM(  ");
	//List Main Product from BME_CONFIG_REP
	sql.append("\n\t\t SELECT RP.STORE_CODE as customer_code ");
	sql.append("\n\t\t ,RP.MATERIAL_MASTER as group_type ");
	sql.append("\n\t\t ,M.PENS_VALUE as pens_item");
	sql.append("\n\t\t from PENSBI.BME_CONFIG_REP RP,PENSBI.PENSBME_MST_REFERENCE M");
	sql.append("\n\t\t where RP.MATERIAL_MASTER = M.interface_value");
	sql.append("\n\t\t and M.reference_code = 'LotusItem'");
	sql.append("\n\t\t and RP.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
	//For test
	//sql.append("\n\t\t and M.PENS_VALUE ='871208'");
	
  sql.append("\n )M ");
  sql.append("\n LEFT OUTER JOIN(	 ");
  sql.append("\n /******************* BEGINING ****************************************/ ");
		    sql.append("\n\t SELECT L.STORE_CODE as customer_code,L.PENS_ITEM, ");
			sql.append("\n\t L.group_code as group_type, NVL(SUM(Ending_qty),0) AS BEGINING_QTY ");
			sql.append("\n\t FROM PENSBI.PENSBME_ENDDATE_STOCK L");
			sql.append("\n\t WHERE 1=1 ");
			//NOT IN pensbme_group_unuse_lotus
			sql.append("\n\t AND L.group_code NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
			sql.append("\n\t AND L.ENDING_DATE  = to_date('"+control.getStartDate()+"','dd/mm/yyyy')  ");
			sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			
			//filter Mat in BME_CONFIG_REP
			sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND L.GROUP_CODE"));
			
			sql.append("\n\t  GROUP BY L.STORE_CODE,L.PENS_ITEM, L.group_code ");
		sql.append("\n ) ENDING ");
		sql.append("\n ON  M.customer_code = ENDING.customer_code ");
		sql.append("\n AND M.pens_item = ENDING.pens_item ");	 
		sql.append("\n AND M.group_type = ENDING.group_type ");
				
	 sql.append("\n LEFT OUTER JOIN(	 ");
	 sql.append("\n/******************* SALE OUT ****************************************/");
			sql.append("\n\t SELECT L.PENS_CUST_CODE as customer_code,L.PENS_ITEM, ");
			sql.append("\n\t L.pens_group_type as group_type, ");
			sql.append("\n\t NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
			sql.append("\n\t FROM PENSBI.PENSBME_SALES_FROM_LOTUS L ");
			sql.append("\n\t WHERE 1=1 ");
			//NOT IN pensbme_group_unuse_lotus
			sql.append("\n\t AND L.pens_group_type NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
			sql.append(genWhereCondDateLotus(control,"L.sales_date"));
			sql.append("\n\t AND L.PENS_CUST_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			
			//filter Mat in BME_CONFIG_REP
			sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND L.PENS_GROUP_TYPE"));
			
			sql.append("\n\t  GROUP BY L.PENS_CUST_CODE,L.PENS_ITEM,L.pens_group_type ");
			sql.append("\n ) SALE_OUT ");
			sql.append("\n ON  M.customer_code = SALE_OUT.customer_code ");
			sql.append("\n AND M.pens_item = SALE_OUT.pens_item ");	 
			sql.append("\n AND M.group_type = SALE_OUT.group_type ");
					
			sql.append("\n LEFT OUTER JOIN( ");
			sql.append("\n/******************* SALE IN ****************************************/");
			   sql.append("\n\t\t SELECT MM.* FROM( ");
		        sql.append("\n\t\t SELECT  ");
				sql.append("\n\t\t  C.customer_code ,P.inventory_item_code as pens_item");
				sql.append("\n\t\t ,MP.MATERIAL_MASTER as group_type  ");
				sql.append("\n\t\t ,NVL(SUM(INVOICED_QTY),0)  as SALE_IN_QTY ");
				sql.append("\n\t\t FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V V   ");
				sql.append("\n\t\t ,PENSBI.XXPENS_BI_MST_CUSTOMER C  ");
				sql.append("\n\t\t ,PENSBI.XXPENS_BI_MST_ITEM P  ");
				sql.append("\n\t\t ,PENSBI.PENSBME_STYLE_MAPPING MP ");
				sql.append("\n\t\t WHERE 1=1   ");
				sql.append("\n\t\t AND V.inventory_item_id = P.inventory_item_id  ");
				sql.append("\n\t\t AND V.customer_id = C.customer_id  ");
				sql.append("\n\t\t AND P.inventory_item_code = MP.pens_item");
				sql.append("\n\t\t AND V.Customer_id IS NOT NULL   ");
				sql.append("\n\t\t AND V.inventory_item_id IS NOT NULL  ");
				//Lotus Only 020047
				sql.append("\n\t\t AND C.customer_code LIKE '020047-%'");
				sql.append(genWhereCondDateLotus(control,"V.invoice_date"));
				sql.append("\n\t\t AND C.customer_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				
				//filter Mat in BME_CONFIG_REP
				sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND MP.MATERIAL_MASTER"));
				
				sql.append("\n\t GROUP BY C.customer_code,P.inventory_item_code,MP.MATERIAL_MASTER");
				//NOT IN pensbme_group_unuse_lotus
				sql.append("\n\t )MM WHERE MM.GROUP_TYPE NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
			sql.append("\n )SALE_IN ");
			sql.append("\n ON  M.customer_code = SALE_IN.customer_code ");
			sql.append("\n AND M.pens_item = SALE_IN.pens_item ");
			sql.append("\n AND M.group_type  = SALE_IN.group_type ");
			sql.append("\n LEFT OUTER JOIN ( ");
			
			sql.append("\n /******************* SALE RETURN ****************************************/");
				sql.append("\n\t SELECT J.store_code as customer_code,I.pens_item,  ");
				sql.append("\n\t I.group_code as group_type, ");
				sql.append("\n\t COUNT(*) as SALE_RETURN_QTY ");
				sql.append("\n\t FROM PENSBI.PENSBME_PICK_JOB J,PENSBI.PENSBME_PICK_BARCODE B ");
				sql.append("\n\t ,PENSBI.PENSBME_PICK_BARCODE_ITEM I ");
				sql.append("\n\t WHERE 1=1   ");
				sql.append("\n\t AND J.job_id = B.job_id  ");
				sql.append("\n\t AND B.job_id = I.job_id ");
				sql.append("\n\t AND B.box_no = I.box_no ");
				sql.append("\n\t AND I.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
				//NOT IN pensbme_group_unuse_lotus
				sql.append("\n\t AND I.group_code NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
				//Lotus Only 020047
				sql.append("\n\t AND J.cust_group = '020047'");
				sql.append(genWhereCondDateLotus(control,"J.close_date"));
				sql.append("\n\t AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				
				//filter Mat in BME_CONFIG_REP
				sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND I.GROUP_CODE"));
				
				sql.append("\n\t GROUP BY J.store_code ,I.pens_item ,I.group_code ");
			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON  M.customer_code = SALE_RETURN.customer_code ");
			sql.append("\n  AND M.pens_item = SALE_RETURN.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
			
			sql.append("\n LEFT OUTER JOIN(	 ");
			sql.append("\n /******************* STOCK_ISSUE ****************************************/");
			 	sql.append("\n\t SELECT ");
			 	sql.append("\n\t L.store_code as customer_code,L.item_issue as pens_item,L.item_issue_desc as group_type, ");
			 	sql.append("\n\t (NVL(SUM(ITEM_ISSUE_QTY),0)*-1) AS ISSUE_QTY ");
			 	sql.append("\n\t FROM PENSBI.PENSBME_ADJUST_INVENTORY L ");
			 	sql.append("\n\t WHERE 1=1 " );
			 	//NOT IN pensbme_group_unuse_lotus
				sql.append("\n\t AND L.item_issue_desc NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
				sql.append("\n\t AND L.STORE_CODE LIKE '020047-%'");
			 	// L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
			 	sql.append(genWhereCondDateLotus(control,"L.transaction_date"));
			 	sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			 
			 	//filter Mat in BME_CONFIG_REP
				sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND L.ITEM_ISSUE_DESC"));
				
			 	sql.append("\n\t  GROUP BY L.STORE_CODE,L.item_issue, L.item_issue_desc ");
			sql.append("\n )STOCK_ISSUE ");
			sql.append("\n ON  M.customer_code = STOCK_ISSUE.customer_code ");
			sql.append("\n AND M.pens_item = STOCK_ISSUE.pens_item ");	 
			sql.append("\n AND M.group_type = STOCK_ISSUE.group_type ");
			
			//Stock Receipt
			sql.append("\n LEFT OUTER JOIN(	 ");
			sql.append("\n /******************* STOCK RECEIPT ****************************************/");
			  sql.append("\n\t SELECT ");
			  sql.append("\n\t L.STORE_CODE as customer_code,L.item_receipt as pens_item,L.item_receipt_desc as group_type, ");
			  sql.append("\n\t NVL(SUM(ITEM_RECEIPT_QTY),0) AS RECEIPT_QTY ");
			  sql.append("\n\t FROM PENSBI.PENSBME_ADJUST_INVENTORY L");
			  sql.append("\n\t WHERE 1=1 ");
			 //NOT IN pensbme_group_unuse_lotus
			  sql.append("\n\t AND L.item_receipt_desc NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
			  sql.append("\n\t AND L.STORE_CODE LIKE '020047-%'");
			  //L.status ='"+AdjustStockDAO.STATUS_INTERFACED+"'");	 
			  sql.append(genWhereCondDateLotus(control,"L.transaction_date"));
			  sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			
			  //filter Mat in BME_CONFIG_REP
			  sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND L.ITEM_RECEIPT_DESC"));
				
			  sql.append("\n\t  GROUP BY  L.STORE_CODE,L.item_receipt,L.item_receipt_desc");
			sql.append("\n )STOCK_RECEIPT ");
			sql.append("\n ON  M.customer_code = STOCK_RECEIPT.customer_code and M.pens_item = STOCK_RECEIPT.pens_item ");	 
			sql.append("\n AND M.group_type = STOCK_RECEIPT.group_type ");
			
			//STOCK_SHORT
			sql.append("\n LEFT OUTER JOIN(	 ");
			sql.append("\n /******************* STOCK_SHORT ****************************************/");
			  sql.append("\n\t SELECT ");
			  sql.append("\n\t L.STORE_CODE as customer_code,L.item_adjust as pens_item,L.item_adjust_desc as group_type, ");
			  sql.append("\n\t NVL(SUM(ITEM_ADJUST_QTY),0) AS STOCK_SHORT_QTY ");
			  sql.append("\n\t FROM PENSBI.PENSBME_ADJUST_SALES L ");
			  sql.append("\n\t WHERE 1=1 ");	 
			 //NOT IN pensbme_group_unuse_lotus
			  sql.append("\n\t AND L.item_adjust_desc NOT IN(select group_code from PENSBI.pensbme_group_unuse_lotus)");
			  sql.append("\n\t AND L.STORE_CODE LIKE '020047-%'");
			  sql.append(genWhereCondDateLotus(control,"L.transaction_date"));
			  sql.append("\n\t AND L.STORE_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			  
			  //filter Mat in BME_CONFIG_REP
			  sql.append(genWhereFilterConfigRep(c.getPensCustCodeFrom(),"\n\t AND L.ITEM_ADJUST_DESC"));
				
			  sql.append("\n\t  GROUP BY L.STORE_CODE,L.item_adjust,L.item_adjust_desc ");
			sql.append("\n )STOCK_SHORT ");
			sql.append("\n ON  M.customer_code = STOCK_SHORT.customer_code and M.pens_item = STOCK_SHORT.pens_item ");	 
			sql.append("\n AND M.group_type = STOCK_SHORT.group_type ");
			
			sql.append("\n ) A ");
			sql.append("\n GROUP BY A.customer_code ,A.group_type ,A.pens_item");
			sql.append("\n ORDER BY A.customer_code ,A.group_type ,A.pens_item asc ");
			sql.append("\n ) A ");
			
			//debug write sql to file
			if(logger.isDebugEnabled()){
			   FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString());
			}

		} catch (Exception e) {
			throw e;
		} finally {
		
		}
		return sql;
 }
 public static BMEControlBean calcDueDateLotus(Connection conn,String storeCode,String asOfdate) throws Exception {
	BMEControlBean bean = new BMEControlBean();
    String maxEndDate = "";
	try {
			//Budish to ChristDate
			Date asofDateTemp = DateUtil.parse(asOfdate, DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String christAsOfDateStr = DateUtil.stringValue(asofDateTemp, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			//Get Max End Date By StoreCode and asOfdate
			maxEndDate =  getMaxEndDateByStoreCodeLotus(conn,storeCode);
			logger.debug("calcDueDate Lotus ReportEndDate");
			logger.debug("asOfDate:"+asOfdate);
			logger.debug("maxEndDate:"+maxEndDate);
			
			if ( !"".equals(Utils.isNull(maxEndDate))){
				bean.setStartDate(maxEndDate);
				bean.setEndDate(christAsOfDateStr);
			}else{
				bean.setYearMonth("");
				bean.setStartDate(christAsOfDateStr);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
			
			} catch (Exception e) {}
		}
		return bean;
	}
 public static String getMaxEndDateByStoreCodeLotus(Connection conn,String storeCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String yearMonth = "";
		try {
			sql.append("\n select distinct max(ending_date) as ending_date ");
			sql.append("\n FROM PENSBI.PENSBME_ENDDATE_STOCK WHERE store_code ='"+storeCode+"'");
		
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				yearMonth = DateUtil.stringValue(rst.getDate("ending_date"),DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return yearMonth;
	} 
 
 public static String getMaxSalesDateLotus(Connection conn,String storeCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String maxSalesDate = "";
		try {
			sql.append("\n select distinct max(sales_date) as sales_date ");
			sql.append("\n FROM PENSBI.PENSBME_SALES_FROM_LOTUS WHERE  PENS_CUST_CODE ='"+storeCode+"'");
		
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
 
	public static String genWhereCondDateLotus(BMEControlBean bean ,String symnoname){
		  String whereSQL = "";
	      if( !"".equals(Utils.isNull(bean.getStartDate())) && !"".equals(Utils.isNull(bean.getEndDate()))){
	    	  whereSQL  ="\n\t AND "+ symnoname +" > to_date('"+bean.getStartDate()+"','dd/mm/yyyy')";
	    	  whereSQL +="\n\t AND "+ symnoname +"<= to_date('"+bean.getEndDate()+"','dd/mm/yyyy')";
	      }else{
		      whereSQL ="\n\t AND "+ symnoname +"<= to_date('"+bean.getStartDate()+"','dd/mm/yyyy')";
	      }
		  return whereSQL;
	  }
	 
	public static String genWhereFilterConfigRep(String storeCode,String colName){
		String sql = colName+" IN(SELECT MATERIAL_MASTER FROM PENSBI.BME_CONFIG_REP WHERE STORE_CODE ='"+storeCode+"')";
		return sql;
	}

}
