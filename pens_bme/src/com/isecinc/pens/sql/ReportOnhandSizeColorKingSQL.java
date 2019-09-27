package com.isecinc.pens.sql;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class ReportOnhandSizeColorKingSQL {
	private static Logger logger = Logger.getLogger("PENS");
	static String storeType ="DUTYFREE";
	
	public static StringBuilder genSQL(Connection conn,OnhandSummary c,User user,Date initDate) throws Exception{
		StringBuilder sql = new StringBuilder();
		try {
	        		
			//prepare parameter
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
			sql.append("\n , NVL(SALE_IN.SALE_IN_QTY,0) AS SALE_IN_QTY");
			sql.append("\n , NVL(SALE_OUT.SALE_OUT_QTY,0) AS SALE_OUT_QTY");
			sql.append("\n , NVL(STOCK_ISSUE.ISSUE_QTY,0)+NVL(STOCK_RECEIPT.RECEIPT_QTY,0) as ADJUST_QTY ");
			sql.append("\n , NVL(SALE_RETURN.SALE_RETURN_QTY,0) AS SALE_RETURN_QTY");
			
			//sql.append("\n , (NVL(INIT_MTT.INIT_SALE_QTY,0) + NVL(SALE_IN.SALE_IN_QTY,0)) - (NVL(SALE_OUT.SALE_OUT_QTY,0)+NVL(SALE_RETURN.SALE_RETURN_QTY,0)) ONHAND_QTY");
			
			sql.append("\n , ( (NVL(INIT_MTT.INIT_SALE_QTY,0) + NVL(SALE_IN.SALE_IN_QTY,0)) ");//Init+SalesIn
			sql.append("\n    - ( NVL(SALE_OUT.SALE_OUT_QTY,0)");//SalesOut
			sql.append("\n      + NVL(SALE_RETURN.SALE_RETURN_QTY,0) )");//RETURN
			sql.append("\n    + ( NVL(STOCK_ISSUE.ISSUE_QTY,0) + NVL(STOCK_RECEIPT.RECEIPT_QTY,0) )");//Adjust
			sql.append("\n    ) ONHAND_QTY");
			
			sql.append("\n FROM(  ");
			   sql.append("\n SELECT DISTINCT AA.* FROM(");
				    sql.append("\n SELECT DISTINCT ");
					sql.append("\n M.pens_item, L.group_code as group_type, M.material_master,L.barcode ");
					sql.append("\n FROM PENSBME_ORDER L");
					sql.append("\n ,( ");
					sql.append("\n   select pens_value as pens_item,");
					sql.append("\n   interface_value as material_master,interface_desc as barcode ");
					sql.append("\n   FROM PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
					sql.append("\n   )M ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND L.barcode = M.barcode  ");
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND M.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND M.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.group_code IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					//Filter StoreCode By StoreType Equals
					sql.append(SQLHelper.genFilterByStoreTypeEquals(conn, storeType, "L.STORE_TYPE"));
					
				    sql.append("\n UNION ");
				    
					sql.append("\n SELECT DISTINCT ");
					sql.append("\n L.pens_item, L.group_item as group_type, L.material_master,L.barcode ");
					sql.append("\n FROM  PENSBME_ONHAND_BME_LOCKED L ");
					sql.append("\n WHERE 1=1   ");
					
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.pens_item >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.pens_item <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.group_item IN("+Utils.converToTextSqlIn(c.getGroup())+") ");
					}
					sql.append("\n UNION ");
					
					sql.append("\n SELECT ");
  				    sql.append("\n DISTINCT L.PENS_ITEM,L.GROUP_CODE as group_type, L.material_master,L.barcode ");
					sql.append("\n FROM PENSBI.PENSBME_MTT_INIT_STK H,PENSBME_MTT_ONHAND_INIT_STK L");
					sql.append("\n WHERE 1=1 ");
					sql.append("\n AND H.cust_no = L.cust_no  ");
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
					//Filter StoreCode By StoreType Like
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.CUST_NO"));
					
					sql.append("\n UNION ");
					
					sql.append("\n SELECT DISTINCT ");
					sql.append("\n  P.inventory_item_code as pens_item, substr(P.inventory_item_desc,0,6) as group_type ");
					sql.append("\n ,MI.material_master,MI.barcode ");
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
					sql.append("\n   WHERE M.reference_code ='Store' ");
					//Gen Filter StoreCode By StoreType
					sql.append(SQLHelper.genFilterByStoreType(conn, "DUTYFREE", "pens_value"));
			        sql.append("\n  ) M ");
			  
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND V.inventory_item_id = P.inventory_item_id  ");
					sql.append("\n AND V.customer_id = C.customer_id ");
					sql.append("\n AND M.cust_no = C.customer_code  ");
					sql.append("\n AND P.inventory_item_code = MI.pens_item  ");
					sql.append("\n AND V.Customer_id IS NOT NULL   ");
					sql.append("\n AND V.inventory_item_id IS NOT NULL  ");
					//sql.append("\n AND P.inventory_item_desc LIKE 'ME%' ");
					
					if(initDate != null){
						 sql.append("\n AND V.invoice_date > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND V.invoice_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					
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
						
                    sql.append("\n UNION");
					
					/** Adjust issue  **/
					sql.append("\n SELECT DISTINCT L.item_issue as pens_item,L.item_issue_desc as group_type");
					sql.append("\n ,MI.material_master ,MI.barcode ");
					sql.append("\n FROM PENSBME_ADJUST_INVENTORY L " );
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as pens_item,");
					sql.append("\n   interface_value as material_master,interface_desc as barcode ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
					sql.append("\n  )MI ");
					sql.append("\n WHERE L.item_issue = MI.pens_item   ");

					if( !Utils.isNull(c.getSalesDate()).equals("")){
		                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.item_issue_desc LIKE '"+c.getGroup()+"%' ");
					}
					//Filter StoreCode By StoreType Like
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.STORE_CODE"));
					
                    sql.append("\n UNION");
					
					/** Adjust receipt **/
					sql.append("\n SELECT DISTINCT L.item_receipt as pens_item ,L.item_receipt_desc as group_type");
					sql.append("\n ,MI.material_master ,MI.barcode ");
					sql.append("\n FROM PENSBME_ADJUST_INVENTORY L " );
					sql.append("\n ,( ");
					sql.append("\n   select distinct pens_value as pens_item,");
					sql.append("\n   interface_value as material_master,interface_desc as barcode ");
					sql.append("\n   FROM PENSBI.PENSBME_MST_REFERENCE M ");
					sql.append("\n   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
					sql.append("\n  )MI ");
					sql.append("\n WHERE L.item_receipt = MI.pens_item   ");
					
					if( !Utils.isNull(c.getSalesDate()).equals("")){
		                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
						sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
					}
					if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
						sql.append("\n AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
						sql.append("\n AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
					}
					if( !Utils.isNull(c.getGroup()).equals("")){
						sql.append("\n AND L.item_receipt_desc LIKE '"+c.getGroup()+"%' ");
					}
					//Filter StoreCode By StoreType Like
					sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.STORE_CODE"));
			        
					sql.append("\n )AA");
					
          sql.append("\n )M ");
          sql.append("\n LEFT OUTER JOIN(	 ");
	            /**** INIT MTT STOCK ******/
      		    sql.append("\n SELECT ");
				sql.append("\n L.PENS_ITEM, L.GROUP_CODE as group_type, L.material_master,L.barcode,");
				sql.append("\n SUM(QTY) AS INIT_SALE_QTY ");
				sql.append("\n FROM PENSBME_MTT_INIT_STK H,PENSBME_MTT_ONHAND_INIT_STK L");
				sql.append("\n WHERE 1=1 ");
				sql.append("\n and H.cust_no = L.cust_no  ");
				sql.append("\n and H.COUNT_STK_DATE = L.COUNT_STK_DATE  ");
				
				if( !Utils.isNull(initDateStr).equals("")){
					 sql.append("\n AND H.COUNT_STK_DATE  = to_date('"+initDateStr+"','dd/mm/yyyy')  ");
				}
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
				//Filter StoreCode By StoreType Like
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.CUST_NO"));
				
				sql.append("\n  GROUP BY L.PENS_ITEM,L.GROUP_CODE, L.material_master,L.barcode ");
				sql.append("\n )INIT_MTT ");
				sql.append("\n ON  M.pens_item = INIT_MTT.pens_item ");	 
				sql.append("\n AND M.group_type = INIT_MTT.group_type ");		
				sql.append("\n AND M.material_master = INIT_MTT.material_master AND M.barcode = INIT_MTT.barcode ");	
			
			/***** Sale Out ***************************/
  		   sql.append("\n LEFT OUTER JOIN(	 ");
  				sql.append("\n SELECT L.PENS_ITEM, ");
			    sql.append("\n L.GROUP_CODE as group_type, L.material_master,L.barcode , ");
				sql.append("\n NVL(COUNT(*),0) AS SALE_OUT_QTY ");
				sql.append("\n FROM PENSBME_SALES_OUT L");
				sql.append("\n WHERE 1=1 ");
				sql.append("\n AND L.status <> '"+PickConstants.STATUS_CANCEL+"'");
				if(initDate != null){
					 sql.append("\n AND L.sale_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
					 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}else{
					 sql.append("\n AND L.sale_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
				}
				
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
				//Filter StoreCode By StoreType Like
				sql.append(SQLHelper.genFilterByStoreType(conn, storeType, "L.CUST_NO"));
				
				sql.append("\n  GROUP BY L.PENS_ITEM, L.GROUP_CODE , L.material_master,L.barcode ");
				sql.append("\n ) SALE_OUT ");
				
				sql.append("\n ON  M.pens_item = SALE_OUT.pens_item ");	 
				sql.append("\n AND M.group_type = SALE_OUT.group_type ");
				sql.append("\n AND M.material_master = SALE_OUT.material_master AND M.barcode = SALE_OUT.barcode ");
			
					/******** Sale In **********************************/
			sql.append("\n LEFT OUTER JOIN( ");
			    sql.append("\n SELECT  ");
			    sql.append("\n A.pens_item, A.group_type,A.material_master,A.barcode,NVL(SUM(A.SALE_IN_QTY),0)  as SALE_IN_QTY ");
			    sql.append("\n FROM (  ");
						sql.append("\n SELECT  ");
						sql.append("\n MI.pens_item, M.group_code as group_type,MI.material_master,MI.barcode,");
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
							 sql.append("\n AND M.order_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND M.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND M.order_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
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
						//Filter StoreCode By StoreType Equals
						sql.append(SQLHelper.genFilterByStoreTypeEquals(conn, storeType, "M.STORE_TYPE"));
						
						sql.append("\n GROUP BY MI.pens_item,M.group_code,MI.material_master,MI.barcode ");
						
						sql.append("\n UNION ALL  ");
						
						sql.append("\n SELECT  ");
						sql.append("\n I.pens_item, I.group_code as group_type,I.material_master,MI.barcode, ");
						sql.append("\n NVL(COUNT(*),0)  as SALE_IN_QTY ");
						sql.append("\n FROM PENSBME_PICK_STOCK M ,PENSBI.PENSBME_PICK_STOCK_I I ");
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
							 sql.append("\n AND M.ISSUE_REQ_DATE  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
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
						//Filter StoreCode By StoreType Equals
						sql.append(SQLHelper.genFilterByStoreTypeEquals(conn, storeType, "M.CUST_GROUP"));
						
						sql.append("\n GROUP BY I.pens_item,I.group_code,I.material_master,MI.barcode ");
						
						sql.append("\n UNION ALL  ");
						
						sql.append("\n SELECT  ");
						sql.append("\n I.pens_item, I.group_code as group_type,I.material_master,I.barcode, ");
						sql.append("\n NVL(SUM(I.ISSUE_QTY),0)  as SALE_IN_QTY ");
						sql.append("\n FROM PENSBME_STOCK_ISSUE M ,PENSBI.PENSBME_STOCK_ISSUE_ITEM I ");
						sql.append("\n WHERE 1=1   ");
						sql.append("\n AND M.issue_req_no = I.issue_req_no  ");
						sql.append("\n AND M.status ='"+PickConstants.STATUS_ISSUED+"'");
						
						if(initDate != null){
							 sql.append("\n AND M.ISSUE_REQ_DATE  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}else{
							 sql.append("\n AND M.ISSUE_REQ_DATE  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
						}
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
						//Filter StoreCode By StoreType Equals
						sql.append(SQLHelper.genFilterByStoreTypeEquals(conn, storeType, "M.CUST_GROUP"));
						
						sql.append("\n GROUP BY I.pens_item,I.group_code,I.material_master,I.barcode ");
						
			  sql.append("\n ) A ");
			  sql.append("\n GROUP BY A.pens_item, A.group_type,A.material_master,A.barcode");
			sql.append("\n )SALE_IN ");
			
			sql.append("\n ON  M.pens_item = SALE_IN.pens_item ");
			sql.append("\n AND M.group_type  = SALE_IN.group_type ");
			sql.append("\n AND M.material_master = SALE_IN.material_master AND M.barcode = SALE_IN.barcode ");
			
			/************* SALE_RETURN ************************/
			sql.append("\n LEFT OUTER JOIN ( ");
					sql.append("\n SELECT I.pens_item, ");
					sql.append("\n I.group_code as group_type,I.material_master,I.barcode ,");
					sql.append("\n NVL(COUNT(*),0) as SALE_RETURN_QTY ");
					sql.append("\n  FROM PENSBME_PICK_JOB J   ");
					sql.append("\n ,PENSBME_PICK_BARCODE B ,PENSBME_PICK_BARCODE_ITEM I  ");
					sql.append("\n WHERE 1=1   ");
					sql.append("\n AND J.job_id = B.job_id  ");
					sql.append("\n AND B.job_id = I.job_id ");
					sql.append("\n AND B.box_no = I.box_no ");
					sql.append("\n AND I.STATUS IN( '"+PickConstants.STATUS_RETURN+"' )");
					
					if(initDate != null){
						 sql.append("\n AND J.close_date  > to_date('"+initDateStr+"','dd/mm/yyyy')  ");
						 sql.append("\n AND J.close_date  <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}else{
						 sql.append("\n AND J.close_date   <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
					}
					
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
					//Filter StoreCode By StoreType Equals
					sql.append(SQLHelper.genFilterByStoreTypeEquals(conn, storeType, "J.CUST_GROUP"));
					
					sql.append("\n GROUP BY I.pens_item ,I.group_code ,I.material_master,I.barcode ");

			sql.append("\n )SALE_RETURN ");
			sql.append("\n  ON  M.pens_item = SALE_RETURN.pens_item ");
			sql.append("\n  AND M.group_type   = SALE_RETURN.group_type ");
			sql.append("\n  AND M.material_master = SALE_RETURN.material_master AND M.barcode = SALE_RETURN.barcode ");
			
			/******** Stock Issue(adjust_inventory) **********************************/
			sql.append("\n LEFT OUTER JOIN(	 ");
			sql.append("\n\t SELECT L.item_issue as pens_item,L.item_issue_desc as group_type");
			sql.append("\n\t ,MI.material_master ,MI.barcode ");
			sql.append("\n\t ,(NVL(SUM(ITEM_ISSUE_QTY),0)*-1) AS ISSUE_QTY ");
			sql.append("\n\t FROM PENSBME_ADJUST_INVENTORY L " );
			sql.append("\n\t ,( ");
			sql.append("\n\t   select distinct pens_value as pens_item,");
			sql.append("\n\t   interface_value as material_master,interface_desc as barcode ");
			sql.append("\n\t   FROM PENSBI.PENSBME_MST_REFERENCE M ");
			sql.append("\n\t   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
			sql.append("\n\t  )MI ");
			sql.append("\n\t WHERE L.item_issue = MI.pens_item  ");
			
			if( !Utils.isNull(c.getSalesDate()).equals("")){
                sql.append("\n\t AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				sql.append("\n\t AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n\t AND L.item_issue >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n\t AND L.item_issue <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n\t AND L.item_issue_desc LIKE '"+c.getGroup()+"%' ");
			}
			//Gen Filter StoreCode By StoreType
			sql.append(SQLHelper.genFilterByStoreType(conn, "DUTYFREE", "L.STORE_CODE"));
	        
			sql.append("\n  GROUP BY L.item_issue, L.item_issue_desc,MI.material_master ,MI.barcode  ");
			sql.append("\n )STOCK_ISSUE ");
			sql.append("\n ON  M.pens_item = STOCK_ISSUE.pens_item AND M.group_type = STOCK_ISSUE.group_type ");
			sql.append("\n AND M.material_master = STOCK_ISSUE.material_master AND M.barcode = STOCK_ISSUE.barcode ");
			
			/*********** Stock Receipt(adjust_inventory) ******************************/
			sql.append("\n LEFT OUTER JOIN(	 ");
			sql.append("\n SELECT L.item_receipt as pens_item,L.item_receipt_desc as group_type ");
			sql.append("\n ,MI.material_master ,MI.barcode ");
			sql.append("\n ,NVL(SUM(ITEM_RECEIPT_QTY),0) AS RECEIPT_QTY ");
			sql.append("\n FROM PENSBME_ADJUST_INVENTORY L  ");
			sql.append("\n\t ,( ");
			sql.append("\n\t   select distinct pens_value as pens_item,");
			sql.append("\n\t   interface_value as material_master,interface_desc as barcode ");
			sql.append("\n\t   FROM PENSBI.PENSBME_MST_REFERENCE M ");
			sql.append("\n\t   WHERE REFERENCE_CODE ='"+Constants.STORE_TYPE_LOTUS_ITEM+"' " );
			sql.append("\n\t  )MI ");
			sql.append("\n\t WHERE L.item_receipt = MI.pens_item  ");
			
			if( !Utils.isNull(c.getSalesDate()).equals("")){
                sql.append("\n AND L.transaction_date <= to_date('"+christSalesDateStr+"','dd/mm/yyyy')  ");
			}
			if( !Utils.isNull(c.getPensCustCodeFrom()).equals("") && !Utils.isNull(c.getPensCustCodeFrom()).equals("ALL")){
				sql.append("\n AND L.STORE_CODE IN("+Utils.converToTextSqlIn(c.getPensCustCodeFrom())+") ");
			}
			if( !Utils.isNull(c.getPensItemFrom()).equals("") && !Utils.isNull(c.getPensItemTo()).equals("")){
				sql.append("\n AND L.item_receipt >='"+Utils.isNull(c.getPensItemFrom())+"' ");
				sql.append("\n AND L.item_receipt <='"+Utils.isNull(c.getPensItemTo())+"' ");
			}
			if( !Utils.isNull(c.getGroup()).equals("")){
				sql.append("\n AND L.item_receipt_desc LIKE '"+c.getGroup()+"%' ");
			}
			//Gen Filter StoreCode By StoreType
			sql.append(SQLHelper.genFilterByStoreType(conn, "DUTYFREE", "L.STORE_CODE"));
			sql.append("\n  GROUP BY ");
			sql.append("\n  L.item_receipt, L.item_receipt_desc ,MI.material_master ,MI.barcode ");
			sql.append("\n )STOCK_RECEIPT ");
			sql.append("\n ON  M.pens_item = STOCK_RECEIPT.pens_item AND M.group_type = STOCK_RECEIPT.group_type ");
			sql.append("\n AND M.material_master = STOCK_RECEIPT.material_master AND M.barcode = STOCK_RECEIPT.barcode ");
			
			sql.append("\n ) A ");
			if( !c.getDispHaveQty().equals("")){
				sql.append("\n WHERE (  A.INIT_SALE_QTY <> 0 OR A.SALE_IN_QTY <> 0 ");
				sql.append("\n       OR A.SALE_OUT_QTY <> 0 OR A.SALE_RETURN_QTY <> 0 ");
				sql.append("\n       OR A.ADJUST_QTY <> 0 OR A.ONHAND_QTY <> 0)");
			}
			sql.append("\n ORDER BY A.group_type,A.MATERIAL_MASTER,A.BARCODE asc ");

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
