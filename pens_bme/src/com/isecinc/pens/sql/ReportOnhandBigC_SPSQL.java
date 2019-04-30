package com.isecinc.pens.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class ReportOnhandBigC_SPSQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuilder genSQL(Connection conn,OnhandSummary c,Date initDate,String summaryType) throws Exception{
		StringBuilder sql = new StringBuilder();
		String storeCode = Constants.STORE_TYPE_BIGC_CODE;
		try {
			String christSalesDateStr ="";
			if( !Utils.isNull(c.getSalesDate()).equals("")){
				Date d = Utils.parse(c.getSalesDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				christSalesDateStr = Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH);
			}
			String initDateStr ="";
			if( initDate != null){
				initDateStr = Utils.stringValue(initDate, Utils.DD_MM_YYYY_WITH_SLASH);
			}
			
			if("GroupCode".equalsIgnoreCase(summaryType)){
				sql.append("\n SELECT A.store_code,A.store_name ,A.sub_inv ,A.group_type ");
				sql.append("\n ,SUM(A.INIT_SALE_QTY) as INIT_SALE_QTY");
				sql.append("\n ,SUM(A.TRANS_IN_QTY) AS TRANS_IN_QTY");
				sql.append("\n ,SUM(A.SALE_OUT_QTY) AS SALE_OUT_QTY");
				sql.append("\n ,SUM(A.SALE_RETURN_QTY) AS SALE_RETURN_QTY");
				sql.append("\n ,SUM(A.SALE_ADJUST_QTY) AS SALE_ADJUST_QTY");
				sql.append("\n ,SUM(A.ONHAND_QTY) AS ONHAND_QTY");
				sql.append("\n FROM(");
			}else{
			    sql.append("\n SELECT A.* FROM(");
			}
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
			   sql.append("\n SELECT DISTINCT AA.* FROM(");
				    sql.append("\n SELECT DISTINCT ");
				    sql.append("\n L.store_code");
				    sql.append("\n ,(select X.pens_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value=L.store_code and reference_code = 'Store') as store_name ");
				    sql.append("\n ,(select X.interface_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value=L.store_code and reference_code = 'SubInv') as sub_inv ");
					sql.append("\n ,M.pens_item, L.group_code as group_type ");
					sql.append("\n FROM  PENSBME_ORDER L");
					sql.append("\n ,( ");
					sql.append("\n   select pens_value as pens_item,");
					sql.append("\n   interface_value as material_master,interface_desc as barcode ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
					sql.append("\n   )M ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND L.barcode = M.barcode  ");
					sql.append("\n AND L.STORE_CODE LIKE '"+storeCode+"%'");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND M.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND M.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
				    sql.append("\n UNION ALL");
				    
				    sql.append("\n SELECT L.PENS_CUST_CODE AS STORE_CODE");
				    sql.append("\n ,(select X.pens_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value=L.PENS_CUST_CODE and reference_code = 'Store') as store_name ");
					sql.append("\n ,(select X.interface_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value=L.PENS_CUST_CODE and reference_code = 'SubInv') as sub_inv ");
					sql.append("\n ,L.PENS_ITEM,L.PENS_GROUP_TYPE as group_type");
					sql.append("\n FROM  PENSBME_SALES_FROM_BIGC  L");
					sql.append("\n WHERE 1=1   ");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n UNION ALL");
					
					sql.append("\n SELECT DISTINCT");
					sql.append("\n L.cust_no as store_code");
					sql.append("\n ,(select X.pens_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value=L.cust_no and reference_code = 'Store') as store_name ");
					sql.append("\n ,(select X.interface_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value=L.cust_no and reference_code = 'SubInv') as sub_inv ");
 				    sql.append("\n ,L.PENS_ITEM,L.GROUP_CODE as group_type");
					sql.append("\n FROM PENSBME_BIGC_INIT_STK H,PENSBME_BIGC_ONHAND_INIT_STK L");
					sql.append("\n WHERE 1=1 ");
					sql.append("\n AND H.cust_no = L.cust_no  ");
					sql.append("\n AND L.CUST_NO LIKE '"+storeCode+"%'");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.CUST_NO IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.GROUP_CODE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n UNION ALL");
					
					sql.append("\n SELECT DISTINCT ");
					sql.append("\n M.CUSTOMER_CODE as store_code");
					sql.append("\n ,(select X.pens_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value=M.CUSTOMER_CODE and reference_code = 'Store') as store_name ");
					sql.append("\n ,(select X.interface_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value=M.CUSTOMER_CODE and reference_code = 'SubInv') as sub_inv ");
					sql.append("\n ,P.inventory_item_code as pens_item, substr(P.inventory_item_desc,0,6) as group_type ");
					sql.append("\n  FROM XXPENS_BI_SALES_ANALYSIS_V V   ");
					sql.append("\n ,XXPENS_BI_MST_CUSTOMER C  ");
					sql.append("\n ,XXPENS_BI_MST_ITEM P  ");
					sql.append("\n ,( ");
					sql.append("\n   select pens_value as pens_item,");
					sql.append("\n   interface_value as material_master,interface_desc as barcode ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
					sql.append("\n  )MI ");
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as customer_code, interface_value as cust_no,pens_desc as customer_desc from ");
					sql.append("\n   PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE  ( pens_value like  '"+storeCode+"%' )" );
					sql.append("\n   AND M.reference_code ='Store' ");
			        sql.append("\n  ) M ");
			  
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
					sql.append("\n AND V.customer_id = C.customer_id ");
					sql.append("\n AND M.cust_no = C.customer_code  ");
					sql.append("\n AND P.inventory_item_code = MI.pens_item  ");
					
					sql.append("\n AND V.Customer_id IS NOT NULL   ");
					sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
					sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
					
					if(initDate != null){
						 sql.append("\n AND V.invoice_date >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					sql.append("\n AND M.CUSTOMER_CODE LIKE '"+storeCode+"%'");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND M.customer_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND P.inventory_item_code >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND P.inventory_item_code <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND substr(P.inventory_item_desc,0,6) IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
						
					sql.append("\n UNION ALL  ");
					
					sql.append("\n SELECT DISTINCT ");
					sql.append("\n M.store_code");
					sql.append("\n ,(select X.pens_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value = M.store_code and reference_code = 'Store') as store_name ");
					sql.append("\n ,(select X.interface_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value = M.store_code and reference_code = 'SubInv') as sub_inv ");
					sql.append("\n ,I.pens_item, I.group_code as group_type");
					sql.append("\n FROM PENSBME_PICK_STOCK M ,PENSBME_PICK_STOCK_I I ");
					sql.append("\n ,( ");
					sql.append("\n   select pens_value as pens_item,");
					sql.append("\n   interface_value as material_master,interface_desc as barcode ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
					sql.append("\n  )MI ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND M.issue_req_no = I.issue_req_no ");
					sql.append("\n AND I.pens_item = MI.pens_item   ");
					sql.append("\n AND I.material_master = MI.material_master");
					sql.append("\n AND M.issue_req_status ='"+PickConstants.STATUS_ISSUED+"'");
					
					if(initDate != null){
						 sql.append("\n AND M.ISSUE_REQ_DATE  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					sql.append("\n AND M.STORE_CODE LIKE '"+storeCode+"%'");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND M.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					
					sql.append("\n UNION ALL  ");
					
					sql.append("\n SELECT  DISTINCT");
					sql.append("\n M.CUSTOMER_NO as store_code");
					sql.append("\n ,(select X.pens_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value = M.CUSTOMER_NO and reference_code = 'Store') as store_name ");
					sql.append("\n ,(select X.interface_desc from PENSBME_MST_REFERENCE X WHERE X.pens_value = M.CUSTOMER_NO and reference_code = 'SubInv') as sub_inv ");
					sql.append("\n ,I.pens_item, I.group_code as group_type ");
					sql.append("\n FROM PENSBME_STOCK_ISSUE M ,PENSBI.PENSBME_STOCK_ISSUE_ITEM I ");
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
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND M.customer_no IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					
					sql.append("\n )AA");
					
         sql.append("\n )M ");
         sql.append("\n LEFT OUTER JOIN(	 ");
	            /** INIT MTT STOCK **/
      		    sql.append("\n SELECT ");
				sql.append("\n L.CUST_NO as STORE_CODE,L.PENS_ITEM, L.GROUP_CODE as group_type,SUM(QTY) AS INIT_SALE_QTY ");
				sql.append("\n FROM PENSBME_BIGC_INIT_STK H,PENSBME_BIGC_ONHAND_INIT_STK L");
				sql.append("\n WHERE 1=1 ");
				sql.append("\n and H.cust_no = L.cust_no  ");
				sql.append("\n and H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
				if( !Utils.isNull(initDateStr).equals("")){
				   sql.append("\n AND H.COUNT_STK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n AND L.CUST_NO LIKE '"+storeCode+"%'");
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n AND L.CUST_NO IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND L.GROUP_CODE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n  GROUP BY L.CUST_NO,L.PENS_ITEM,L.GROUP_CODE");
				sql.append("\n )INIT_MTT ");
				sql.append("\n ON  M.pens_item = INIT_MTT.pens_item ");	 
				sql.append("\n AND M.STORE_CODE = INIT_MTT.STORE_CODE ");		
				sql.append("\n AND M.group_type = INIT_MTT.group_type ");	
				
 		   sql.append("\n LEFT OUTER JOIN(	 ");
 				sql.append("\n SELECT ");
				sql.append("\n L.PENS_CUST_CODE AS STORE_CODE,L.PENS_ITEM,L.PENS_GROUP_TYPE as group_type,NVL(SUM(L.QTY),0) AS SALE_OUT_QTY ");
				sql.append("\n FROM  PENSBME_SALES_FROM_BIGC  L");
				sql.append("\n WHERE 1=1 ");
		
				if(initDate != null){
					 sql.append("\n AND L.sales_date  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND L.sales_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND L.sales_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n AND L.PENS_CUST_CODE LIKE '"+storeCode+"%'");
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n AND L.PENS_CUST_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND L.PENS_GROUP_TYPE IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n  GROUP BY L.PENS_CUST_CODE,L.PENS_ITEM, L.PENS_GROUP_TYPE  ");
				
				sql.append("\n ) SALE_OUT ");
				sql.append("\n ON  M.pens_item = SALE_OUT.pens_item ");	 
				sql.append("\n AND M.group_type = SALE_OUT.group_type ");
				sql.append("\n AND M.STORE_CODE = SALE_OUT.STORE_CODE ");	
					
			sql.append("\n LEFT OUTER JOIN( ");
			    sql.append("\n SELECT  ");
			    sql.append("\n A.STORE_CODE,A.pens_item, A.group_type,NVL(SUM(A.SALE_IN_QTY),0)  as TRANS_IN_QTY ");
			    sql.append("\n FROM (  ");
						sql.append("\n SELECT  ");
						sql.append("\n M.STORE_CODE,MI.pens_item, M.group_code as group_type,");
						sql.append("\n NVL(SUM(QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM PENSBME_ORDER M ");
						sql.append("\n ,( ");
						sql.append("\n   select pens_value as pens_item,");
						sql.append("\n   interface_value as material_master,interface_desc as barcode ");
						sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
						sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
						sql.append("\n  )MI ");
						sql.append("\n WHERE MI.BARCODE = M.BARCODE  ");
						
						if(initDate != null){
							 sql.append("\n AND M.order_date  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND M.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND M.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						sql.append("\n AND M.STORE_CODE LIKE '"+storeCode+"%'");
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND M.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND MI.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND MI.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND M.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY M.STORE_CODE,MI.pens_item,M.group_code ");
						
						sql.append("\n UNION ALL  ");
						
						sql.append("\n SELECT  ");
						sql.append("\n M.STORE_CODE,I.pens_item, I.group_code as group_type, ");
						sql.append("\n NVL(COUNT(*),0)  as SALE_IN_QTY ");
						sql.append("\n FROM PENSBME_PICK_STOCK M ,PENSBME_PICK_STOCK_I I ");
						sql.append("\n ,( ");
						sql.append("\n   select pens_value as pens_item,");
						sql.append("\n   interface_value as material_master,interface_desc as barcode ");
						sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
						sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
						sql.append("\n  )MI ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND M.issue_req_no = I.issue_req_no ");
						sql.append("\n AND I.pens_item = MI.pens_item   ");
						sql.append("\n AND I.material_master = MI.material_master");
						sql.append("\n AND M.issue_req_status ='"+PickConstants.STATUS_ISSUED+"'");
						
						if(initDate != null){
							 sql.append("\n AND M.ISSUE_REQ_DATE  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
						sql.append("\n AND M.STORE_CODE LIKE '"+storeCode+"%'");
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND M.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY M.STORE_CODE,I.pens_item,I.group_code");
						
						sql.append("\n UNION ALL  ");
						
						sql.append("\n SELECT  ");
						sql.append("\n M.CUSTOMER_NO AS STORE_CODE,I.pens_item, I.group_code as group_type, ");
						sql.append("\n NVL(SUM(I.ISSUE_QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM PENSBME_STOCK_ISSUE M ,PENSBI.PENSBME_STOCK_ISSUE_ITEM I ");
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
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND M.customer_no IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
						}
						sql.append("\n GROUP BY M.CUSTOMER_NO,I.pens_item,I.group_code");
						
			  sql.append("\n ) A ");
			  sql.append("\n GROUP BY A.STORE_CODE,A.pens_item, A.group_type");
			sql.append("\n )TRANS_IN ");
			
			sql.append("\n ON  M.pens_item = TRANS_IN.pens_item ");
			sql.append("\n AND M.group_type  = TRANS_IN.group_type ");
			sql.append("\n AND M.STORE_CODE = TRANS_IN.STORE_CODE ");	
			sql.append("\n LEFT OUTER JOIN ( ");
					sql.append("\n SELECT J.STORE_CODE,I.pens_item, I.group_code as group_type,");
					sql.append("\n NVL(COUNT(*),0) as SALE_RETURN_QTY ");
					sql.append("\n  FROM PENSBME_PICK_JOB J   ");
					sql.append("\n ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
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
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND I.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n GROUP BY J.STORE_CODE,I.pens_item ,I.group_code");

			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON  M.pens_item = SALE_RETURN.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
			sql.append("\n  AND M.STORE_CODE = SALE_RETURN.STORE_CODE ");	
			
			sql.append("\n LEFT OUTER JOIN ( ");
			    sql.append("\n SELECT J.STORE_CODE, J.item_adjust as pens_item,J.item_adjust_desc as group_type ");
				sql.append("\n ,NVL(SUM(J.ITEM_ADJUST_QTY),0) as SALE_ADJUST_QTY ");
				sql.append("\n FROM PENSBME_ADJUST_SALES J ");
				
				sql.append("\n WHERE 1=1   ");
				sql.append("\n AND J.STATUS <> '"+PickConstants.STATUS_CANCEL+"' ");
				
				if(initDate != null){
					 sql.append("\n AND J.transaction_date  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND J.transaction_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND J.transaction_date   <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				sql.append("\n AND J.STORE_CODE LIKE '"+storeCode+"%'");
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				    sql.append("\n AND J.store_code IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND J.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND J.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND J.item_adjust_desc IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
				}
				sql.append("\n GROUP BY J.STORE_CODE,J.item_adjust , J.item_adjust_desc ");

			sql.append("\n )SALE_ADJUST ");
			sql.append("\n  ON  M.pens_item = SALE_ADJUST.pens_item ");
			sql.append("\n  AND M.group_type = SALE_ADJUST.group_type ");
			sql.append("\n  AND M.STORE_CODE = SALE_ADJUST.STORE_CODE ");	
	
			sql.append("\n ) A ");
			if( !Utils.isNull(c.getDispHaveQty()).equals("")){
				sql.append("\n WHERE ( A.INIT_SALE_QTY <> 0");
				sql.append("\n OR A.TRANS_IN_QTY <> 0");
				sql.append("\n OR A.SALE_OUT_QTY <> 0");
				sql.append("\n OR A.SALE_RETURN_QTY <> 0");
				sql.append("\n OR A.SALE_ADJUST_QTY <> 0 )");
			}
			
			if("GroupCode".equalsIgnoreCase(summaryType)){
				sql.append("\n GROUP BY A.store_code,A.store_name ,A.sub_inv ,A.group_type ");
			}
			sql.append("\n ORDER BY A.STORE_CODE,A.group_type asc ");
			
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
