package com.isecinc.pens.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class ReportOnhandBigC_ASOF_SQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuilder genSQL(Connection conn,OnhandSummary c,User user) throws Exception{
		StringBuilder sql = new StringBuilder();
		String cutOffDate  ="";
		logger.debug("***ReportOnhandBigC_ASOF_SQL***");
		try {
			//get cutoffDate of BigC
			cutOffDate = ControlConstantsDB.getOnhandDateAsOfControl(conn,ControlConstantsDB.TYPE_ONHAND_DATE_BIGC_AS_OF);
		
			//prepare parameter
			String christSalesDateStr ="";
			if( !Utils.isNull(c.getSalesDate()).equals("")){
				Date d = DateUtil.parse(c.getSalesDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = DateUtil.stringValue(d, DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			sql.append("\n SELECT A.* FROM(");
			sql.append("\n  SELECT ALL_IN.customer_code,ALL_IN.group_type,ALL_IN.pens_item ,ALL_IN.SUB_INV");
			sql.append("\n  ,( SELECT S.pens_desc from PENSBME_MST_REFERENCE  S  ");
			sql.append("\n     WHERE reference_code ='Store' and S.pens_value = ALL_IN.customer_code) as customer_name");
			
			//Get column init case not found in bastransact
			sql.append("\n ,INIT.customer_code as customer_code_init ,INIT.customer_name as customer_name_init");
			sql.append("\n ,INIT.group_type as group_type_init ,INIT.pens_item as pens_item_init ,INIT.SUB_INV as sub_inv_init");
			
			sql.append("\n , NVL(INIT.INIT_QTY,0) AS INIT_QTY");
			sql.append("\n , NVL(ALL_IN.TRANS_IN_QTY,0) AS TRANS_IN_QTY");	
			sql.append("\n , NVL(ALL_IN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			sql.append("\n , NVL(ALL_IN.ADJUST_QTY,0) AS ADJUST_QTY");
			sql.append("\n , NVL(ALL_IN.SALEOUT_SHORT_QTY,0) AS SALEOUT_SHORT_QTY");
			
			sql.append("\n ,(  ( NVL(INIT.INIT_QTY,0) + NVL(ALL_IN.TRANS_IN_QTY,0)) + NVL(ALL_IN.ADJUST_QTY,0) "
					    + "  - (NVL((ALL_IN.SALE_RETURN_QTY*-1),0)+ NVL((ALL_IN.SALEOUT_SHORT_QTY*-1),0) )"
					    + " ) ONHAND_QTY");
			
			sql.append("\n FROM(  ");
			    sql.append("\n SELECT ");
			    sql.append("\n M.customer_code,MI.group_type ,P.item_code as pens_item ,M.sub_inv");
			    sql.append("\n ,sum(case when P.Transaction_Type_name in('PO Receipt' ,'Subinventory Transfer' ,'BAS Transfer Stock' )");
				sql.append("\n    and P.TRANSACTION_QUANTITY > 0 ");
				sql.append("\n    then P.TRANSACTION_QUANTITY else 0 end)  as TRANS_IN_QTY");
			            
			    sql.append("\n ,sum(case when P.Transaction_Type_name in('Subinventory Transfer' ,'BAS Transfer Stock' )");
			    sql.append("\n      and P.TRANSACTION_QUANTITY < 0 ");
			    sql.append("\n      then P.TRANSACTION_QUANTITY else 0 end)  as SALE_RETURN_QTY ");      
			 
			    sql.append("\n ,sum(case when P.Transaction_Type_name in('Bme Adjust Items Issue','Bme Adjust Items Receipt' )"); 
			    sql.append("\n      then P.TRANSACTION_QUANTITY else 0 end)  as ADJUST_QTY ");   
			            
			    sql.append("\n ,sum(case when P.Transaction_Type_name ='Sales order issue'");
			    sql.append("\n     then P.TRANSACTION_QUANTITY else 0 end)  as SALEOUT_SHORT_QTY");
			    
				sql.append("\n from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
				sql.append("\n ,(SELECT PENS_VALUE as customer_code ");
				sql.append("\n  , INTERFACE_DESC as sub_inv from PENSBME_MST_REFERENCE  ");
				sql.append("\n    WHERE reference_code ='SubInv' ");
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				    sql.append("\n AND pens_value ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
				}
				sql.append("\n) M ");
				sql.append("\n ,( SELECT DISTINCT M.PENS_VALUE as pens_item ,PENS_DESC2 as group_type  ");
				sql.append("\n    from PENSBME_MST_REFERENCE M  ");
				sql.append("\n    WHERE M.reference_code ='BigCitem' ");
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND M.PENS_DESC2 IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n  ) MI ");
				sql.append("\n where M.sub_inv = p.subinventory_code ");
				sql.append("\n and (    P.Transaction_Type_name = 'PO Receipt'");
				sql.append("\n       OR P.Transaction_Type_name = 'Subinventory Transfer'");
				sql.append("\n       OR P.Transaction_Type_name = 'BAS Transfer Stock' ");
				sql.append("\n       OR P.Transaction_Type_name = 'Bme Adjust Items Issue' ");
				sql.append("\n       OR P.Transaction_Type_name = 'Bme Adjust Items Receipt' ");
				sql.append("\n       OR P.Transaction_Type_name ='Sales order issue' ");
				sql.append("\n     ) ");
				sql.append("\n and P.item_code = MI.pens_item  ");
				if( !Utils.isNull(c.getSalesDate()).equals("")){
					sql.append("\n AND P.transaction_date >= to_date('"+cutOffDate+"','ddmmyyyy')  ");
	                sql.append("\n AND P.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				    sql.append("\n AND M.customer_code ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND P.item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND P.item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND MI.group_type IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n GROUP BY M.customer_code,MI.group_type,p.item_code ,M.sub_inv ");
		   sql.append("\n ) ALL_IN ");
			sql.append("\n FULL OUTER JOIN( ");
			sql.append("\n /** INIT  Qty  <= 01/03/2018 **/ ");
				        sql.append("\n SELECT ");
						sql.append("\n  M.customer_code ,MI.group_type ,P.item_code as pens_item ,M.sub_inv ");
						sql.append("\n  ,( SELECT S.pens_desc from PENSBME_MST_REFERENCE  S  ");
						sql.append("\n     WHERE reference_code ='Store' and S.pens_value = M.customer_code) as customer_name ");
						sql.append("\n ,NVL(SUM(P.BEGIN_EA_QTY),0)  as INIT_QTY ");
						sql.append("\n from PENSBI.XXPENS_INV_BASBEGIN_V P ");
						sql.append("\n ,(SELECT PENS_VALUE as customer_code ");
						sql.append("\n  , INTERFACE_DESC as sub_inv from PENSBME_MST_REFERENCE  ");
						sql.append("\n   WHERE reference_code ='SubInv' ");
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND pens_value ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
						}
						sql.append("\n) M ");
						sql.append("\n ,( SELECT DISTINCT M.PENS_VALUE as pens_item ,PENS_DESC2 as group_type  ");
						sql.append("\n    from PENSBME_MST_REFERENCE M  ");
						sql.append("\n    WHERE M.reference_code ='BigCitem' ");
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND M.PENS_DESC2 IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n  ) MI ");
						sql.append("\n where M.sub_inv = p.subinventory_code ");
						sql.append("\n AND P.item_code = MI.pens_item ");
				
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND M.customer_code ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND P.item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND P.item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND MI.group_type IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY M.customer_code ,MI.group_type,P.item_code ,M.sub_inv");
				sql.append("\n ) INIT ");
				sql.append("\n ON  ALL_IN.Customer_code = INIT.Customer_code ");
				sql.append("\n AND ALL_IN.group_type  = INIT.group_type ");
				sql.append("\n AND ALL_IN.pens_item = INIT.pens_item ");
			sql.append("\n ) A ");
			if( !"".equalsIgnoreCase(c.getDispHaveQty())){
				sql.append("\n WHERE (A.INIT_QTY <> 0 ");
				sql.append("\n OR A.TRANS_IN_QTY <> 0 ");
				sql.append("\n OR A.SALEOUT_SHORT_QTY <> 0 ");
				sql.append("\n OR A.SALE_RETURN_QTY <> 0 ");
				sql.append("\n OR A.ADJUST_QTY <> 0 ");
				sql.append("\n OR A.ONHAND_QTY <> 0 )");
			}
			sql.append("\n ORDER BY A.customer_code,A.group_type,A.pens_item asc ");
			
			//logger.debug("sql:"+sql);
			
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
}
