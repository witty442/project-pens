package com.isecinc.pens.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.dao.constants.Constants;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class ReportStockWacoalLotus_SQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuilder genSQL(Connection conn,OnhandSummary c,Date initDate,Date asOfDate) throws Exception{
		StringBuilder sql = new StringBuilder();
		String storeCode = "LOTUS";
		try {
			String christAsOfDateStr = DateUtil.stringValue(asOfDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			String initDateStr ="";
			if( initDate != null){
				initDateStr = DateUtil.stringValue(initDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			sql.append("\n SELECT A.* FROM(");
			sql.append("\n SELECT M.*");
			sql.append("\n , NVL(SALE_INIT.SALE_INIT_QTY,0) AS SALE_INIT_QTY");
			sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
			sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
			sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			
			sql.append("\n ,(NVL(SALE_INIT.SALE_INIT_QTY,0) + NVL(SALE_IN.SALE_IN_QTY,0))" +
					      "-(NVL(SALE_OUT.SALE_OUT_QTY,0) + NVL(SALE_RETURN.SALE_RETURN_QTY,0)) " +
					  "\n AS ONHAND_QTY");
			
			sql.append("\n FROM(  ");
			   sql.append("\n SELECT DISTINCT AA.* FROM(");
				    sql.append("\n SELECT DISTINCT L.branch_id ");
				    sql.append("\n ,(select M.branch_name from pensbme_wacoal_store_mapping M where M.branch_id = L.branch_id) as branch_name ");
					sql.append("\n ,L.item as group_type ");
					sql.append("\n FROM  PENSBME_INISTK_WACOAL L WHERE 1=1");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.BRANCH_ID IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
				    sql.append("\n UNION ALL");
				    sql.append("\n SELECT DISTINCT L.branch_id ");
				    sql.append("\n ,(select M.branch_name from pensbme_wacoal_store_mapping M where M.branch_id = L.branch_id) as branch_name ");
					sql.append("\n ,L.item as group_type ");
					sql.append("\n FROM  PENSBME_WACOAL_SALEIN L");
					sql.append("\n WHERE  1=1 ");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.BRANCH_ID IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					sql.append("\n UNION ALL");
				    sql.append("\n SELECT DISTINCT L.branch_id ");
				    sql.append("\n ,(select M.branch_name from pensbme_wacoal_store_mapping M where M.branch_id = L.branch_id) as branch_name ");
					sql.append("\n ,substr(L.item_wacoal,0,6) as group_type ");
					sql.append("\n FROM  PENSBME_WACOAL_SALEOUT L");
					sql.append("\n WHERE 1=1 ");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.BRANCH_ID IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					sql.append("\n UNION ALL");
				    sql.append("\n SELECT DISTINCT L.branch_id");
				    sql.append("\n ,(select M.branch_name from pensbme_wacoal_store_mapping M where M.branch_id = L.branch_id) as branch_name ");
					sql.append("\n ,L.item as group_type ");
					sql.append("\n FROM  PENSBME_WACOAL_RETURN L");
					sql.append("\n WHERE  1=1 ");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.BRANCH_ID IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					sql.append("\n )AA");
					
         sql.append("\n )M ");
         sql.append("\n LEFT OUTER JOIN(	 ");
	            /** INIT  STOCK **/
                sql.append("\n SELECT L.branch_id,L.item as group_type ,NVL(SUM(CHECK_QTY),0) as SALE_INIT_QTY");
			    sql.append("\n FROM  PENSBME_INISTK_WACOAL L WHERE 1=1");
			    sql.append("\n AND L.branch_id in(select branch_id from pensbme_wacoal_store_mapping where BRANCH_NAME LIKE '"+storeCode+"%') ");
			    if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				   sql.append("\n AND L.BRANCH_ID IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			    }
				if( !Utils.isNull(initDateStr).equals("")){
				   sql.append("\n AND L.CHECK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n  GROUP BY L.branch_id,L.item  ");
				sql.append("\n )SALE_INIT ");
				sql.append("\n ON  M.branch_id = SALE_INIT.branch_id");	 	
				sql.append("\n AND M.group_type = SALE_INIT.group_type");	
				
 		   sql.append("\n LEFT OUTER JOIN(	 ");
 		        /** Sale In **/
	 		    sql.append("\n SELECT L.branch_id,L.item as group_type,NVL(SUM(QTY),0) as SALE_IN_QTY ");
			    sql.append("\n FROM  PENSBME_WACOAL_SALEIN L");
			    sql.append("\n WHERE  1=1 ");
			    if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				   sql.append("\n AND L.BRANCH_ID IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			    }
			    if(initDate != null){
					 sql.append("\n AND L.bill_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND L.bill_date  <= to_date('"+christAsOfDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND L.bill_date  <= to_date('"+christAsOfDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n  GROUP BY L.branch_id,L.item  ");
				sql.append("\n ) SALE_IN ");
				sql.append("\n ON  M.branch_id = SALE_IN.branch_id");	 	
				sql.append("\n AND M.group_type = SALE_IN.group_type");
					
			sql.append("\n LEFT OUTER JOIN( ");
				 /** Sale OUT **/
	 		    sql.append("\n SELECT L.branch_id,substr(L.item_wacoal,0,6) as group_type,NVL(SUM(QTY),0) as SALE_OUT_QTY ");
			    sql.append("\n FROM  PENSBME_WACOAL_SALEOUT L");
			    sql.append("\n WHERE  1=1 ");
			    if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				   sql.append("\n AND L.BRANCH_ID IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			    }
			    if(initDate != null){
					 sql.append("\n AND L.sales_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND L.sales_date  <= to_date('"+christAsOfDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND L.sales_date  <= to_date('"+christAsOfDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n  GROUP BY L.branch_id,substr(L.item_wacoal,0,6) ");
			sql.append("\n )SALE_OUT ");
			sql.append("\n ON  M.branch_id = SALE_OUT.branch_id");	 	
			sql.append("\n AND M.group_type = SALE_OUT.group_type");
			
			sql.append("\n LEFT OUTER JOIN ( ");
				 /** SALE_RETURN **/
	 		    sql.append("\n SELECT L.branch_id,L.item as group_type ,NVL(SUM(QTY),0) as SALE_RETURN_QTY ");
			    sql.append("\n FROM  PENSBME_WACOAL_RETURN L");
			    sql.append("\n WHERE  1=1 ");
			    if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				   sql.append("\n AND L.BRANCH_ID IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			    }
			    if(initDate != null){
					 sql.append("\n AND L.document_date_sdh  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND L.document_date_sdh  <= to_date('"+christAsOfDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND L.document_date_sdh  <= to_date('"+christAsOfDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n  GROUP BY L.branch_id,item ");
			sql.append("\n )SALE_RETURN ");
			sql.append("\n ON  M.branch_id = SALE_RETURN.branch_id");	 	
			sql.append("\n AND M.group_type = SALE_RETURN.group_type");
			sql.append("\n ) A ");
			
			if( !Utils.isNull(c.getDispHaveQty()).equals("")){
				sql.append("\n WHERE ( A.SALE_INIT_QTY <> 0");
				sql.append("\n OR A.SALE_IN_QTY <> 0");
				sql.append("\n OR A.SALE_OUT_QTY <> 0");
				sql.append("\n OR A.SALE_RETURN_QTY <> 0 )");
			}
			sql.append("\n ORDER BY A.BRANCH_ID,A.GROUP_TYPE asc ");
			
		//	logger.debug("sql:"+sql);
			
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
}
