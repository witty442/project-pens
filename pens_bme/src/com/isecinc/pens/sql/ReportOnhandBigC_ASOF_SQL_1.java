package com.isecinc.pens.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class ReportOnhandBigC_ASOF_SQL_1 {
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
				Date d = Utils.parse(c.getSalesDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH);
			}
			sql.append("\n SELECT A.* FROM(");
			sql.append("\n SELECT M.* ");
			sql.append("\n , NVL(INIT.INIT_QTY,0) AS INIT_QTY");
			sql.append("\n , NVL(ALL_IN.TRANS_IN_QTY,0) AS TRANS_IN_QTY");	
			sql.append("\n , NVL(ALL_IN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			sql.append("\n , NVL(ALL_IN.ADJUST_QTY,0) AS ADJUST_QTY");
			sql.append("\n , NVL(ALL_IN.SALEOUT_SHORT_QTY,0) AS SALEOUT_SHORT_QTY");
			
			sql.append("\n ,(  ( NVL(INIT.INIT_QTY,0) + NVL(ALL_IN.TRANS_IN_QTY,0)) + NVL(ALL_IN.ADJUST_QTY,0) "
					    + "  - (NVL((ALL_IN.SALE_RETURN_QTY*-1),0)+ NVL((ALL_IN.SALEOUT_SHORT_QTY*-1),0) )"
					    + " ) ONHAND_QTY");
			
			sql.append("\n FROM(  ");
			    //INIT ALL DATA
			    sql.append("\n  SELECT ");
				sql.append("\n  M.customer_code, M.customer_name, MI.group_type  ");
				sql.append("\n ,P.subinventory_code as sub_inv ,P.item_code as pens_item ");
				sql.append("\n  from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
				sql.append("\n ,( SELECT M.PENS_VALUE as customer_code ,M.INTERFACE_DESC as sub_inv   ");
				sql.append("\n     ,( SELECT S.pens_desc from PENSBME_MST_REFERENCE  S  ");
				sql.append("\n        WHERE reference_code ='Store' and S.pens_value = M.pens_value ) as customer_name   ");
				sql.append("\n    from PENSBME_MST_REFERENCE M  ");
				sql.append("\n    WHERE M.reference_code ='SubInv' ");
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				    sql.append("\n AND pens_value ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
				}
				sql.append("\n  ) M ");
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
				sql.append("\n       OR P.Transaction_Type_name = 'Bme Adjust Items Receipt'"); 
				sql.append("\n       OR P.Transaction_Type_name = 'Sales order issue' ");
				sql.append("\n ) ");
				sql.append("\n and MI.pens_item = p.item_code ");
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
				
				/*sql.append("\n UNION ");
				//BASBEGIN INIT Item All
				sql.append("\n  SELECT ");
				sql.append("\n  M.customer_code, M.customer_name, MI.group_type  ");
				sql.append("\n ,P.subinventory_code as sub_inv ,P.item_code as pens_item ");
				sql.append("\n  from PENSBI.XXPENS_INV_BASBEGIN_V P ");
				sql.append("\n ,( SELECT M.PENS_VALUE as customer_code ,M.INTERFACE_DESC as sub_inv   ");
				sql.append("\n     ,( SELECT S.pens_desc from PENSBME_MST_REFERENCE  S  ");
				sql.append("\n        WHERE reference_code ='Store' and S.pens_value = M.pens_value ) as customer_name   ");
				sql.append("\n    from PENSBME_MST_REFERENCE M  ");
				sql.append("\n    WHERE M.reference_code ='SubInv' ");
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				    sql.append("\n AND pens_value ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
				}
				sql.append("\n  ) M ");
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
				}*/
		   sql.append("\n ) M ");
			sql.append("\n LEFT OUTER JOIN( ");
			sql.append("\n /** INIT  Qty  <= 01/03/2018 **/ ");
				        sql.append("\n SELECT ");
						sql.append("\n  M.customer_code ,MI.group_type ,P.item_code as pens_item ");
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
						sql.append("\n GROUP BY M.customer_code ,MI.group_type,P.item_code ");
				sql.append("\n ) INIT ");
				sql.append("\n ON  M.Customer_code = INIT.Customer_code ");
				sql.append("\n AND M.group_type  = INIT.group_type ");
				sql.append("\n AND M.pens_item = INIT.pens_item ");
				
			sql.append("\n LEFT OUTER JOIN( ");
			
			    /** ALL BASTRANSACT **/
			    sql.append("\n SELECT A.customer_code, A.group_type ,A.pens_item  ");
				sql.append("\n ,A.TRANS_IN_QTY ,A.SALE_RETURN_QTY,A.ADJUST_QTY,A.SALEOUT_SHORT_QTY ");
				sql.append("\n FROM( ");
				    sql.append("\n SELECT ");
				    sql.append("\n M.customer_code,MI.group_type ,P.item_code as pens_item");
						
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
					sql.append("\n GROUP BY M.customer_code,MI.group_type,p.item_code  ");
			sql.append("\n   ) A ");	
			sql.append("\n ) ALL_IN ");
			sql.append("\n ON  M.Customer_code = ALL_IN.Customer_code ");
			sql.append("\n AND M.group_type  = ALL_IN.group_type ");
			sql.append("\n AND M.pens_item = ALL_IN.pens_item ");
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
