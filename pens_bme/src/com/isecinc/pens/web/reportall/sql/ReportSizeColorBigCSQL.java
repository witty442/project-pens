package com.isecinc.pens.web.reportall.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class ReportSizeColorBigCSQL {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static StringBuilder genSQL(Connection conn,ReportAllBean c,Date initDate) throws Exception{
			StringBuilder sql = new StringBuilder();
			String storeCode = Constants.STORE_TYPE_BIGC_CODE;
			try {
				String christSalesDateStr ="";
				if( !Utils.isNull(c.getSalesDate()).equals("")){
					Date d = DateUtil.parse(c.getSalesDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					christSalesDateStr = DateUtil.stringValue(d, DateUtil.DD_MM_YYYY_WITH_SLASH);
				}
				String initDateStr ="";
				if( initDate != null){
					initDateStr = DateUtil.stringValue(initDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				}
				
				sql.append("\n SELECT A.* FROM(");
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
					    sql.append("\n  L.pens_cust_code as store_code");
					    sql.append("\n ,L.pens_cust_desc as store_name ");
					    sql.append("\n ,(select X.interface_desc from PENSBI.PENSBME_MST_REFERENCE X WHERE X.pens_value=L.pens_cust_code and reference_code = 'SubInv') as sub_inv ");
						sql.append("\n ,M.pens_item, L.pens_group_type as group_type, M.material_master,L.barcode ");
						sql.append("\n FROM  PENSBI.PENSBME_SALES_FROM_BIGC_TEMP L");
						sql.append("\n ,( ");
						sql.append("\n   select distinct pens_value as pens_item,");
						sql.append("\n   interface_value as material_master,interface_desc as barcode ");
						sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
						sql.append("\n   WHERE REFERENCE_CODE IN('"+Constants.STORE_TYPE_LOTUS_ITEM+"')");
						sql.append("\n   )M ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND L.barcode = M.barcode  ");
						sql.append("\n AND L.pens_cust_code LIKE '"+storeCode+"%'");
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND L.pens_cust_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND M.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND M.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals(""))
							sql.append("\n AND L.pens_group_type LIKE '"+c.getGroup()+"%' ");
							
					    sql.append("\n UNION ALL");
						
						sql.append("\n SELECT DISTINCT");
						sql.append("\n L.cust_no as store_code");
						sql.append("\n ,(select X.pens_desc from PENSBI.PENSBME_MST_REFERENCE X WHERE X.pens_value=L.cust_no and reference_code = 'Store') as store_name ");
						sql.append("\n ,(select X.interface_desc from PENSBI.PENSBME_MST_REFERENCE X WHERE X.pens_value=L.cust_no and reference_code = 'SubInv') as sub_inv ");
					    sql.append("\n ,L.PENS_ITEM,L.GROUP_CODE as group_type, L.material_master,L.barcode ");
						sql.append("\n FROM PENSBI.PENSBME_BIGC_INIT_STK H,PENSBI.PENSBME_BIGC_ONHAND_INIT_STK L");
						sql.append("\n WHERE 1=1 ");
						sql.append("\n AND H.cust_no = L.cust_no  ");
						sql.append("\n AND L.CUST_NO LIKE '"+storeCode+"%'");
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND L.CUST_NO IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND L.GROUP_CODE LIKE '"+c.getGroup()+"%' ");
						}
						sql.append("\n UNION ALL");
						
						sql.append("\n /** PENSBI.XXPENS_INV_BASTRANSACT_V **/");
						sql.append("\n SELECT ");
						sql.append("\n  M.customer_code as store_code ");
						sql.append("\n ,(select X.pens_desc from PENSBI.PENSBME_MST_REFERENCE X ");
						sql.append("\n   WHERE X.pens_value = M.customer_code and reference_code = 'Store') as store_name ");
						sql.append("\n ,M.sub_inv ");
					    sql.append("\n ,P.item_code as pens_item ,MI.group_type ,MI.material_master ,MI.barcode ");
						sql.append("\n from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
						sql.append("\n ,(SELECT PENS_VALUE as customer_code,INTERFACE_DESC as sub_inv ");
						sql.append("\n   from PENSBI.PENSBME_MST_REFERENCE "); 
						sql.append("\n   WHERE reference_code ='SubInv' ");
						sql.append("\n ) M ");
						sql.append("\n ,( ");
						sql.append("\n   select distinct pens_value as pens_item,PENS_DESC2 as group_type ");
						sql.append("\n   ,interface_value as material_master,interface_desc as barcode ");
						sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE ");
						sql.append("\n   WHERE REFERENCE_CODE IN('"+Constants.STORE_TYPE_LOTUS_ITEM+"')" );
						sql.append("\n  )MI ");
						sql.append("\n where M.sub_inv = P.subinventory_code ");
						sql.append("\n and (  P.Transaction_Type_name = 'PO Receipt'");
						sql.append("\n     OR P.Transaction_Type_name = 'Subinventory Transfer'");
						sql.append("\n     OR P.Transaction_Type_name = 'BAS Transfer Stock' ");
						sql.append("\n )"); 
						sql.append("\n and P.item_code = MI.pens_item  ");
						sql.append("\n AND M.customer_code LIKE '"+storeCode+"%'");
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND M.customer_code  IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND MI.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND MI.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND MI.GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
						}
						sql.append("\n UNION ALL");
		
						sql.append("\n /** PREMIUM Transfer From FridayItem **/");
						sql.append("\n SELECT ");
						sql.append("\n  M.customer_code as store_code ");
						sql.append("\n ,(select X.pens_desc from PENSBI.PENSBME_MST_REFERENCE X ");
						sql.append("\n   WHERE X.pens_value = M.customer_code and reference_code = 'Store') as store_name ");
						sql.append("\n ,M.sub_inv ");
					    sql.append("\n ,P.item_code as pens_item ,MI.group_type ,MI.material_master ,MI.barcode ");
						sql.append("\n from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
						sql.append("\n ,(SELECT PENS_VALUE as customer_code,INTERFACE_DESC as sub_inv ");
						sql.append("\n   from PENSBI.PENSBME_MST_REFERENCE "); 
						sql.append("\n   WHERE reference_code ='SubInv' ");
						sql.append("\n ) M ");
						sql.append("\n ,( ");
						sql.append("\n   select distinct pens_value as pens_item,PENS_DESC2 as group_type ");
						sql.append("\n   ,interface_value as material_master,interface_desc as barcode ");
						sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE ");
						sql.append("\n   WHERE REFERENCE_CODE IN('"+Constants.STORE_TYPE_FRIDAY_ITEM+"')" );
						sql.append("\n  )MI ");
						sql.append("\n where M.sub_inv = P.subinventory_code ");
						sql.append("\n and P.Transaction_Type_name = 'BAS Transfer Stock'"); 
						sql.append("\n and P.TRANSACTION_QUANTITY > 0 ");
						sql.append("\n and P.item_code = MI.pens_item  ");
						sql.append("\n AND M.customer_code LIKE '"+storeCode+"%'");
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
							sql.append("\n AND M.customer_code  IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND MI.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND MI.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND MI.GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
						}
						sql.append("\n )AA");
						
	      sql.append("\n )M ");
	      sql.append("\n LEFT OUTER JOIN(	 ");
	      sql.append("\n /****** INIT_IN *******/   ");
	   		    sql.append("\n SELECT ");
					sql.append("\n L.CUST_NO as STORE_CODE,L.PENS_ITEM, L.GROUP_CODE as group_type, L.material_master,L.barcode,");
					sql.append("\n SUM(QTY) AS INIT_SALE_QTY ");
					sql.append("\n FROM PENSBI.PENSBME_BIGC_INIT_STK H, PENSBI.PENSBME_BIGC_ONHAND_INIT_STK L");
					sql.append("\n WHERE 1=1 ");
					sql.append("\n and H.cust_no = L.cust_no  ");
					sql.append("\n and H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
					if( !Utils.isNull(initDateStr).equals("")){
					   sql.append("\n AND H.COUNT_STK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					}
					sql.append("\n AND L.CUST_NO LIKE '"+storeCode+"%'");
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.CUST_NO IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.GROUP_CODE LIKE '"+c.getGroup()+"%' ");
					}
					sql.append("\n  GROUP BY L.CUST_NO,L.PENS_ITEM,L.GROUP_CODE, L.material_master,L.barcode ");
					sql.append("\n )INIT_MTT ");
					sql.append("\n ON  M.pens_item = INIT_MTT.pens_item ");	 
					sql.append("\n AND M.STORE_CODE = INIT_MTT.STORE_CODE ");		
					sql.append("\n AND M.group_type = INIT_MTT.group_type ");	
					sql.append("\n AND M.material_master = INIT_MTT.material_master ");
					sql.append("\n AND M.barcode = INIT_MTT.barcode ");	
					
			   sql.append("\n LEFT OUTER JOIN(	 ");
			   sql.append("\n /****** SALES_OUT *******/   ");
			       
			        /** NEW CODE :21/08/2562 **/
		 		    sql.append("\n SELECT L.PENS_CUST_CODE AS STORE_CODE,L.PENS_ITEM, ");
				    sql.append("\n L.PENS_GROUP_TYPE as group_type, M.material_master,L.barcode , ");
					sql.append("\n NVL(SUM(L.QTY),0) AS SALE_OUT_QTY ");
					sql.append("\n FROM PENSBI.PENSBME_SALES_FROM_BIGC_TEMP L");
					sql.append("\n ,( ");
					sql.append("\n   select pens_value as pens_item,");
					sql.append("\n   interface_value as material_master,interface_desc as barcode ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
					sql.append("\n   )M ");
					sql.append("\n WHERE L.barcode = M.barcode  ");
					sql.append("\n AND L.PENS_CUST_CODE LIKE '"+storeCode+"%'");
					if(initDate != null){
						 sql.append("\n AND L.sales_date  >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND L.sales_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND L.sales_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.PENS_CUST_CODE IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.PENS_GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
					}
					sql.append("\n  GROUP BY L.PENS_CUST_CODE,L.PENS_ITEM,");
					sql.append("\n  L.PENS_GROUP_TYPE , M.material_master,L.barcode ");
					sql.append("\n ) SALE_OUT ");
					
					sql.append("\n ON  M.pens_item = SALE_OUT.pens_item ");	 
					sql.append("\n AND M.group_type = SALE_OUT.group_type ");
					sql.append("\n AND M.STORE_CODE = SALE_OUT.STORE_CODE ");	
					sql.append("\n AND M.material_master = SALE_OUT.material_master ");
					sql.append("\n AND M.barcode = SALE_OUT.barcode ");
						
				sql.append("\n LEFT OUTER JOIN( ");
				sql.append("\n /****** TRANS_IN *******/   ");
				sql.append("\n  SELECT ");
				sql.append("\n  M.customer_code as store_code ");
			    sql.append("\n ,P.item_code as pens_item ,MI.group_type ,MI.material_master ,MI.barcode ");
			    sql.append("\n ,NVL(SUM(P.TRANSACTION_QUANTITY),0) as TRANS_IN_QTY");
				sql.append("\n from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
				sql.append("\n ,(SELECT PENS_VALUE as customer_code,INTERFACE_DESC as sub_inv ");
				sql.append("\n   from PENSBI.PENSBME_MST_REFERENCE "); 
				sql.append("\n   WHERE reference_code ='SubInv' ");
				sql.append("\n ) M ");
				sql.append("\n ,( ");
				sql.append("\n   select distinct pens_value as pens_item,PENS_DESC2 as group_type ");
				sql.append("\n   ,interface_value as material_master,interface_desc as barcode ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE ");
				sql.append("\n   WHERE REFERENCE_CODE IN('"+Constants.STORE_TYPE_LOTUS_ITEM+"')" );
				sql.append("\n  )MI ");
				sql.append("\n where M.sub_inv = p.subinventory_code ");
				sql.append("\n and (  P.Transaction_Type_name = 'PO Receipt'");
				sql.append("\n     OR P.Transaction_Type_name = 'Subinventory Transfer'");
				sql.append("\n     OR P.Transaction_Type_name = 'BAS Transfer Stock' ");
				sql.append("\n )"); 
				sql.append("\n and P.TRANSACTION_QUANTITY > 0 ");
				sql.append("\n and P.item_code = MI.pens_item  ");
				sql.append("\n AND M.customer_code LIKE '"+storeCode+"%'");
				if(initDate != null){
					 sql.append("\n AND P.transaction_date >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND P.transaction_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND P.transaction_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n AND M.customer_code  IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND MI.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND MI.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND MI.GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
				}
				sql.append("\n GROUP BY M.customer_code,P.item_code,MI.group_type,MI.material_master ,MI.barcode");
				
				sql.append("\n  UNION  ");
				
				sql.append("\n  /** Premium From FridayItem **/ ");
			    sql.append("\n  SELECT ");
				sql.append("\n  M.customer_code as store_code ");
			    sql.append("\n ,P.item_code as pens_item ,MI.group_type ,MI.material_master ,MI.barcode ");
			    sql.append("\n ,NVL(SUM(P.TRANSACTION_QUANTITY),0) as TRANS_IN_QTY");
				sql.append("\n from PENSBI.XXPENS_INV_BASTRANSACT_V P ");
				sql.append("\n ,(SELECT PENS_VALUE as customer_code,INTERFACE_DESC as sub_inv ");
				sql.append("\n   from PENSBI.PENSBME_MST_REFERENCE "); 
				sql.append("\n   WHERE reference_code ='SubInv' ");
				sql.append("\n ) M ");
				sql.append("\n ,( ");
				sql.append("\n   select distinct pens_value as pens_item,PENS_DESC2 as group_type ");
				sql.append("\n   ,interface_value as material_master,interface_desc as barcode ");
				sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE ");
				sql.append("\n   WHERE REFERENCE_CODE IN('"+Constants.STORE_TYPE_FRIDAY_ITEM+"')" );
				sql.append("\n  )MI ");
				sql.append("\n where M.sub_inv = p.subinventory_code ");
				sql.append("\n and P.Transaction_Type_name = 'BAS Transfer Stock' "); 
				sql.append("\n and P.TRANSACTION_QUANTITY > 0 ");
				sql.append("\n and P.item_code = MI.pens_item  ");
				sql.append("\n AND M.customer_code LIKE '"+storeCode+"%'");
				if(initDate != null){
					 sql.append("\n AND P.transaction_date >= to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND P.transaction_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND P.transaction_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					sql.append("\n AND M.customer_code  IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
				}
				if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
					sql.append("\n AND MI.PENS_ITEM >='"+Utils.isNull(c.getPensItemFrom())+"' ");
					sql.append("\n AND MI.PENS_ITEM <='"+Utils.isNull(c.getPensItemTo())+"' ");
				}
				if( !Utils.isNull(c.getGroup()).equals("")){
					sql.append("\n AND MI.GROUP_TYPE LIKE '"+c.getGroup()+"%' ");
				}
				sql.append("\n GROUP BY M.customer_code,P.item_code,MI.group_type,MI.material_master ,MI.barcode");
				
				sql.append("\n )TRANS_IN ");
				
				sql.append("\n ON  M.pens_item = TRANS_IN.pens_item ");
				sql.append("\n AND M.group_type  = TRANS_IN.group_type ");
				sql.append("\n AND M.STORE_CODE = TRANS_IN.STORE_CODE ");	
				sql.append("\n AND M.material_master = TRANS_IN.material_master");
				sql.append("\n AND M.barcode = TRANS_IN.barcode ");
				
				sql.append("\n LEFT OUTER JOIN ( ");
				sql.append("\n /****** SALES_RETURN *******/   ");
						sql.append("\n SELECT J.STORE_CODE,I.pens_item, ");
						sql.append("\n I.group_code as group_type,I.material_master,I.barcode ,");
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
						if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						    sql.append("\n AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
						}
						if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
							sql.append("\n AND I.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
							sql.append("\n AND I.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
						}
						if( !Utils.isNull(c.getGroup()).equals("")){
							sql.append("\n AND I.group_code LIKE '"+c.getGroup()+"%' ");
						}
						sql.append("\n AND J.STORE_CODE LIKE '"+storeCode+"%'");
						sql.append("\n GROUP BY J.STORE_CODE,I.pens_item ,I.group_code ,I.material_master,I.barcode ");

				sql.append("\n )SALE_RETURN ");
				sql.append("\n  ON  M.pens_item = SALE_RETURN.pens_item ");
				sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
				sql.append("\n  AND M.STORE_CODE = SALE_RETURN.STORE_CODE ");	
				sql.append("\n  AND M.material_master = SALE_RETURN.material_master");
				sql.append("\n  AND M.barcode = SALE_RETURN.barcode ");
				
				sql.append("\n LEFT OUTER JOIN ( ");
				sql.append("\n /****** ADJUST *******/   ");
				    sql.append("\n SELECT J.STORE_CODE, J.item_adjust as pens_item,J.item_adjust_desc as group_type ");
					sql.append("\n ,( ");
					sql.append("\n   select max(interface_value) as material_master ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
					sql.append("\n   and M.pens_value = J.item_adjust");
					sql.append("\n  ) as material_master");
					sql.append("\n ,( ");
					sql.append("\n   select max(interface_desc) as barcode ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
					sql.append("\n   and M.pens_value = J.item_adjust");
					sql.append("\n  ) as barcode");
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
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
					    sql.append("\n AND J.store_code IN("+SQLHelper.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND J.item_adjust >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND J.item_adjust <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND J.item_adjust_desc LIKE '"+c.getGroup()+"%' ");
					}
					sql.append("\n AND J.STORE_CODE LIKE '"+storeCode+"%'");
					sql.append("\n GROUP BY J.STORE_CODE,J.item_adjust , J.item_adjust_desc ");

				sql.append("\n )SALE_ADJUST ");
				sql.append("\n  ON  M.pens_item = SALE_ADJUST.pens_item ");
				sql.append("\n  AND M.group_type   = SALE_ADJUST.group_type ");
				sql.append("\n  AND M.STORE_CODE = SALE_ADJUST.STORE_CODE ");	
				sql.append("\n  AND M.material_master = SALE_ADJUST.material_master ");
				sql.append("\n  AND M.barcode = SALE_ADJUST.barcode ");
		
				sql.append("\n ) A ");
				if( !Utils.isNull(c.getDispHaveQty()).equals("")){
					sql.append("\n WHERE ( A.INIT_SALE_QTY <> 0");
					sql.append("\n OR A.TRANS_IN_QTY <> 0");
					sql.append("\n OR A.SALE_OUT_QTY <> 0");
					sql.append("\n OR A.SALE_RETURN_QTY <> 0");
					sql.append("\n OR A.SALE_ADJUST_QTY <> 0 )");
				}
				sql.append("\n ORDER BY A.STORE_CODE,A.group_type,A.MATERIAL_MASTER,A.BARCODE asc ");
				
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
