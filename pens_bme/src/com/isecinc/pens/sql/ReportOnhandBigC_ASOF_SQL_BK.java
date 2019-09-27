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

public class ReportOnhandBigC_ASOF_SQL_BK {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuilder genSQL(Connection conn,OnhandSummary c,User user) throws Exception{
		StringBuilder sql = new StringBuilder();
		String cutOffDate  ="";
		logger.debug("***ReportOnhandBigC_ASOF_SQL***");
		try {
			//get cutoffDate of BigC
			cutOffDate = ControlConstantsDB.getOnhandDateAsOfControl(conn,ControlConstantsDB.TYPE_ONHAND_DATE_BIGC_AS_OF);
			//onhandDateAsOfConfig = Utils.parse(onhandDateAsOfConfigStr,Utils.DD_MM_YYYY_WITHOUT_SLASH);
			
			//prepare parameter
			String christSalesDateStr ="";
			if( !Utils.isNull(c.getSalesDate()).equals("")){
				Date d = DateUtil.parse(c.getSalesDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = DateUtil.stringValue(d, DateUtil.DD_MM_YYYY_WITH_SLASH);
			}
			sql.append("\n SELECT A.* FROM(");
			sql.append("\n SELECT M.* ");
			sql.append("\n , NVL(INIT.INIT_QTY,0) AS INIT_QTY");
			sql.append("\n , NVL(TRANS_IN.TRANS_IN_QTY,0) AS TRANS_IN_QTY");	
			//sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
			sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			sql.append("\n , NVL(ADJUST.ADJUST_QTY,0) AS ADJUST_QTY");
			sql.append("\n , NVL(SALEOUT_SHORT.SALEOUT_SHORT_QTY,0) AS SALEOUT_SHORT_QTY");
			
			sql.append("\n ,(  ( NVL(INIT.INIT_QTY,0) + NVL(TRANS_IN.TRANS_IN_QTY,0)) + NVL(ADJUST.ADJUST_QTY,0) "
					    + "  - (NVL((SALE_RETURN.SALE_RETURN_QTY*-1),0)+ NVL((SALEOUT_SHORT.SALEOUT_SHORT_QTY*-1),0) )"
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
				sql.append("\n GROUP BY M.customer_code,P.subinventory_code,MI.group_type,P.item_code  ");
				
				sql.append("\n UNION ");
				
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
					}
					sql.append("\n GROUP BY M.customer_code,P.subinventory_code,MI.group_type,P.item_code  ");
				
		   sql.append("\n ) M ");
		    //sql.append("\n LEFT OUTER JOIN(	 ");
			//sql.append("\n /** Sale Out **/ ");
			/*		sql.append("\n SELECT ");
					sql.append("\n L.PENS_CUST_CODE as customer_code,L.PENS_CUST_DESC as customer_desc ");
					sql.append("\n ,L.PENS_GROUP_TYPE as group_type,M.pens_value as pens_item,NVL(SUM(QTY),0) AS SALE_OUT_QTY ");
					sql.append("\n FROM PENSBI.PENSBME_SALES_FROM_BIGC L ");
					sql.append("\n,( ");
					sql.append("\n   select distinct pens_value,pens_desc2 from ");
					sql.append("\n   PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE M.reference_code ='BigCitem' ");
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND M.PENS_DESC2 IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n ) M ");
					sql.append("\n WHERE 1=1 ");
					sql.append("\n AND M.pens_desc2 = L.pens_group_type ");
							 
					if( !Utils.isNull(c.getSalesDate()).equals("")){
						//Date Cut Off
						sql.append("\n AND L.sales_date >= to_date('"+cutOffDate+"','ddmmyyyy')  ");
						
		                sql.append("\n AND L.sales_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.PENS_CUST_CODE ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+"");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND M.pens_value >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND M.pens_value <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n  GROUP BY ");
					sql.append("\n  L.PENS_CUST_CODE,L.PENS_CUST_DESC,L.PENS_GROUP_TYPE,M.pens_value ");
					sql.append("\n )SALE_OUT ");
					sql.append("\n ON  M.Customer_code = SALE_OUT.Customer_code ");	 
					sql.append("\n AND M.group_type = SALE_OUT.group_type ");
					sql.append("\n AND M.pens_item = SALE_OUT.pens_item ");
		    */
			sql.append("\n LEFT OUTER JOIN( ");
			sql.append("\n /** INIT  Qty  <= 01/03/2018 **/ ");
				    sql.append("\n SELECT ");
					sql.append("\n  A.customer_code, A.group_type ,A.sub_inv ,A.pens_item ");
					sql.append("\n, NVL(SUM(A.INIT_QTY),0)  as INIT_QTY ");
					sql.append("\n FROM( ");
				        sql.append("\n SELECT ");
						sql.append("\n  M.customer_code ,MI.group_type ,P.item_code as pens_item, P.subinventory_code as sub_inv ");
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
						
						//bigC Only 020049
						//sql.append("\n AND M.customer_code LIKE '020049%'");
						
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
						sql.append("\n GROUP BY M.customer_code,P.subinventory_code ,MI.group_type,P.item_code ");
					sql.append("\n) A ");	
					sql.append("\n group by A.customer_code ,A.sub_inv, A.group_type ,A.pens_item ");
				sql.append("\n ) INIT ");
				sql.append("\n ON  M.Customer_code = INIT.Customer_code ");
				sql.append("\n AND M.group_type  = INIT.group_type ");
				sql.append("\n AND M.pens_item = INIT.pens_item ");
				
			sql.append("\n LEFT OUTER JOIN( ");
			
			    /** Trans In **/
			    sql.append("\n SELECT ");
				sql.append("\n  A.customer_code, A.group_type ,A.sub_inv,A.pens_item, NVL(SUM(A.TRANS_IN_QTY),0)  as TRANS_IN_QTY  ");
				sql.append("\n FROM( ");
			        sql.append("\n SELECT ");
					sql.append("\n M.customer_code,MI.group_type ,P.subinventory_code as sub_inv ,P.item_code as pens_item");
					sql.append("\n,NVL(SUM(P.TRANSACTION_QUANTITY),0)  as TRANS_IN_QTY ");
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
					sql.append("\n and P.Transaction_Type_name = 'PO Receipt' ");
					sql.append("\n and P.TRANSACTION_QUANTITY > 0  ");
					sql.append("\n and P.item_code = MI.pens_item  ");
					//bigC Only 020049
					//sql.append("\n AND C.customer_code LIKE '020049%'");
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
					sql.append("\n GROUP BY M.customer_code,P.subinventory_code,MI.group_type,p.item_code  ");
					
					sql.append("\n UNION ALL ");
					
					sql.append("\n SELECT ");
					sql.append("\n M.customer_code ,MI.group_type ,P.subinventory_code as sub_inv,P.item_code as pens_item ");
					sql.append("\n ,NVL(SUM(P.TRANSACTION_QUANTITY),0)  as TRANS_IN_QTY ");
					sql.append("\n from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
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
					sql.append("\n and P.Transaction_Type_name = 'Subinventory Transfer' ");
					sql.append("\n and P.TRANSACTION_QUANTITY > 0  ");
					sql.append("\n and P.item_code = MI.pens_item  ");
					//bigC Only 020049
					//sql.append("\n AND C.customer_code LIKE '020049%'");
					if( !Utils.isNull(c.getSalesDate()).equals("")){
						sql.append("\n AND P.transaction_date >= to_date('"+cutOffDate+"','ddmmyyyy')  ");
		                sql.append("\n AND P.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND M.customer_code ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+"");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND P.item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND P.item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND MI.group_type IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n GROUP BY M.customer_code,P.subinventory_code,MI.group_type,P.item_code ");
					
		            sql.append("\n UNION ALL ");
					
					sql.append("\n SELECT ");
					sql.append("\n  M.customer_code ,MI.group_type ,P.subinventory_code as sub_inv,P.item_code as pens_item  ");
					sql.append("\n ,NVL(SUM(P.TRANSACTION_QUANTITY),0)  as TRANS_IN_QTY ");
					sql.append("\n from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
					sql.append("\n ,(SELECT PENS_VALUE as customer_code ");
					sql.append("\n  , INTERFACE_DESC as sub_inv from PENSBME_MST_REFERENCE  ");
					sql.append("\n    WHERE reference_code ='SubInv' ");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND pens_value ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
					}
					sql.append("\n ) M ");
					sql.append("\n ,( SELECT DISTINCT M.PENS_VALUE as pens_item ,PENS_DESC2 as group_type  ");
					sql.append("\n    from PENSBME_MST_REFERENCE M  ");
					sql.append("\n    WHERE M.reference_code ='BigCitem' ");
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND M.PENS_DESC2 IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n  ) MI ");
					sql.append("\n where M.sub_inv = p.subinventory_code ");
					sql.append("\n and P.Transaction_Type_name = 'BAS Transfer Stock' ");
					sql.append("\n and P.TRANSACTION_QUANTITY > 0  ");
					sql.append("\n and P.item_code = MI.pens_item  ");
					//bigC Only 020049
					//sql.append("\n AND C.customer_code LIKE '020049%'");
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
						sql.append("\n AND MI.group_type ="+Utils.converToTextSqlIn(c.getGroup())+" ");
					}
					sql.append("\n GROUP BY M.customer_code,P.subinventory_code ,MI.group_type,P.item_code ");
				sql.append("\n) A ");	
				sql.append("\n  group by A.customer_code,A.sub_inv , A.group_type ,A.pens_item ");
			sql.append("\n ) TRANS_IN ");
			sql.append("\n ON  M.Customer_code = TRANS_IN.Customer_code ");
			sql.append("\n AND M.group_type  = TRANS_IN.group_type ");
			sql.append("\n AND M.pens_item = TRANS_IN.pens_item ");
			
			sql.append("\n LEFT OUTER JOIN ( ");
			
			sql.append("\n /** Return QTY *****/ ");
				    sql.append("\n SELECT ");
					sql.append("\n  A.customer_code , A.group_type ,A.sub_inv,A.pens_item  ");
					sql.append("\n, NVL(SUM(A.TRANS_IN_QTY),0)  as SALE_RETURN_QTY ");
					sql.append("\n FROM( ");
						sql.append("\n SELECT ");
						sql.append("\n M.customer_code ,MI.group_type ,P.subinventory_code as sub_inv,P.item_code as pens_item  ");
						sql.append("\n,NVL(SUM(P.TRANSACTION_QUANTITY),0)  as TRANS_IN_QTY ");
						sql.append("\n from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
						sql.append("\n,(SELECT PENS_VALUE as customer_code ");
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
						sql.append("\n and P.Transaction_Type_name = 'Subinventory Transfer' ");
						sql.append("\n and P.TRANSACTION_QUANTITY < 0  ");
						sql.append("\n and P.item_code = MI.pens_item  ");
						//bigC Only 020049
						//sql.append("\n AND C.customer_code LIKE '020049%'");
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
							sql.append("\n AND MI.group_type ="+Utils.converToTextSqlIn(c.getGroup())+" ");
						}
						sql.append("\n GROUP BY M.customer_code,P.subinventory_code,MI.group_type,P.item_code ");
						
		                sql.append("\n UNION ALL ");
						
						sql.append("\n SELECT ");
						sql.append("\n M.customer_code,MI.group_type ,P.subinventory_code as sub_inv,P.item_code as pens_item  ");
						sql.append("\n,NVL(SUM(P.TRANSACTION_QUANTITY),0)  as TRANS_IN_QTY ");
						sql.append("\n from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
						sql.append("\n,(SELECT PENS_VALUE as customer_code ");
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
						sql.append("\n and P.Transaction_Type_name = 'BAS Transfer Stock' ");
						sql.append("\n and P.TRANSACTION_QUANTITY < 0  ");
						sql.append("\n and P.item_code = MI.pens_item  ");
						//bigC Only 020049
						//sql.append("\n AND C.customer_code LIKE '020049%'");
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
						sql.append("\n GROUP BY M.customer_code,P.subinventory_code,MI.group_type,P.item_code  ");
					sql.append("\n) A ");
					sql.append("\n group by A.customer_code, A.sub_inv , A.group_type ,A.pens_item  ");
					
			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON  M.Customer_code = SALE_RETURN.Customer_code ");
			sql.append("\n  AND M.group_type = SALE_RETURN.group_type ");
			sql.append("\n  AND M.pens_item = SALE_RETURN.pens_item ");
			
			sql.append("\n LEFT OUTER JOIN (");
			sql.append("\n  /** ADJUST **/ ");
			sql.append("\n 	SELECT ");
			sql.append("\n 	M.customer_code ,MI.group_type ,P.subinventory_code as sub_inv,P.item_code as pens_item  ");
			sql.append("\n	,NVL(SUM(P.TRANSACTION_QUANTITY),0)  as ADJUST_QTY ");
			sql.append("\n 	from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
			sql.append("\n	,( SELECT PENS_VALUE as customer_code ");
			sql.append("\n    ,INTERFACE_DESC as sub_inv from PENSBME_MST_REFERENCE  ");
			sql.append("\n     WHERE reference_code ='SubInv' ");
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
			    sql.append("\n 	 AND pens_value ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
			}
			sql.append("\n	  ) M ");
			sql.append("\n  ,( SELECT DISTINCT M.PENS_VALUE as pens_item ,PENS_DESC2 as group_type  ");
			sql.append("\n     from PENSBME_MST_REFERENCE M  ");
			sql.append("\n     WHERE M.reference_code ='BigCitem' ");
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n  AND M.PENS_DESC2 IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
			}
			sql.append("\n   ) MI ");
			sql.append("\n 	where M.sub_inv = p.subinventory_code ");
			sql.append("\n 	and P.Transaction_Type_name in('Bme Adjust Items Issue','Bme Adjust Items Receipt') ");
			sql.append("\n 	and P.item_code = MI.pens_item  ");
			if( !Utils.isNull(c.getSalesDate()).equals("")){
				sql.append("\n AND P.transaction_date >= to_date('"+cutOffDate+"','ddmmyyyy')  ");
                sql.append("\n 	AND P.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
			    sql.append("\n 	AND M.customer_code ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n 	AND P.item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n 	AND P.item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n 	AND MI.group_type ="+Utils.converToTextSqlIn(c.getGroup())+" ");
			}
			sql.append("\n 	GROUP BY M.customer_code,P.subinventory_code,MI.group_type,P.item_code ");
			sql.append("\n ) ADJUST ");
			sql.append("\n  ON  M.Customer_code = ADJUST.Customer_code ");
			sql.append("\n  AND M.group_type = ADJUST.group_type ");
			sql.append("\n  AND M.pens_item = ADJUST.pens_item ");
			
			sql.append("\n LEFT OUTER JOIN (");
			sql.append("\n  /** SALEOUT_+STOCK_SHORT **/ ");
			sql.append("\n 	SELECT ");
			sql.append("\n 	M.customer_code ,MI.group_type ,P.subinventory_code as sub_inv,P.item_code as pens_item  ");
			sql.append("\n	,NVL(SUM(P.TRANSACTION_QUANTITY),0)  as SALEOUT_SHORT_QTY ");
			sql.append("\n 	from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
			sql.append("\n	,( SELECT PENS_VALUE as customer_code ");
			sql.append("\n    ,INTERFACE_DESC as sub_inv from PENSBME_MST_REFERENCE  ");
			sql.append("\n     WHERE reference_code ='SubInv' ");
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
			    sql.append("\n 	 AND pens_value ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
			}
			sql.append("\n	  ) M ");
			sql.append("\n  ,( SELECT DISTINCT M.PENS_VALUE as pens_item ,PENS_DESC2 as group_type  ");
			sql.append("\n     from PENSBME_MST_REFERENCE M  ");
			sql.append("\n     WHERE M.reference_code ='BigCitem' ");
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n  AND M.PENS_DESC2 IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
			}
			sql.append("\n   ) MI ");
			sql.append("\n 	where M.sub_inv = p.subinventory_code ");
			sql.append("\n 	and P.Transaction_Type_name ='Sales order issue' ");
			sql.append("\n 	and P.item_code = MI.pens_item  ");
			if( !Utils.isNull(c.getSalesDate()).equals("")){
				sql.append("\n  AND P.transaction_date >= to_date('"+cutOffDate+"','ddmmyyyy')  ");
                sql.append("\n 	AND P.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
			    sql.append("\n 	AND M.customer_code ="+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+" ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n 	AND P.item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n 	AND P.item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n 	AND MI.group_type ="+Utils.converToTextSqlIn(c.getGroup())+" ");
			}
			sql.append("\n 	GROUP BY M.customer_code,P.subinventory_code,MI.group_type,P.item_code ");
			sql.append("\n ) SALEOUT_SHORT ");
			sql.append("\n  ON  M.Customer_code = SALEOUT_SHORT.Customer_code ");
			sql.append("\n  AND M.group_type = SALEOUT_SHORT.group_type ");
			sql.append("\n  AND M.pens_item = SALEOUT_SHORT.pens_item ");
			sql.append("\n ) A ");
			if( !"".equalsIgnoreCase(c.getDispHaveQty())){
				sql.append("\n WHERE (A.INIT_QTY <> 0 ");
				sql.append("\n OR A.TRANS_IN_QTY <> 0 ");
				sql.append("\n OR A.SALEOUT_SHORT_QTY <> 0 ");
				sql.append("\n OR A.SALE_RETURN_QTY <> 0 ");
				sql.append("\n OR A.ADJUST_QTY <> 0 ");
				/*sql.append("\n OR A.SHORT_QTY <> 0 ");*/
				sql.append("\n OR A.ONHAND_QTY <> 0 )");
			}
			sql.append("\n ORDER BY A.customer_code,A.group_type,A.pens_item asc ");
			
			//logger.debug("sql:"+sql);
			
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
