package com.isecinc.pens.inf.helper;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;

public class ExportSQL {

	protected static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * Get SQL By Special Case Export
	 * @param tableBean
	 * @return
	 * @throws Exception
	 */
	public static String genSpecialSQL(TableBean tableBean,User userBean) throws Exception{
		String str = "";
		try{
			
			if(tableBean.getTableName().equalsIgnoreCase("M_CUSTOMER")){
				str = " SELECT M.CUSTOMER_ID ,M.CODE AS CUSTOMER_NUMBER ,M.NAME AS CUSTOMER_NAME \n"+
					    " ,IMAGE_FILE_NAME \n"+
						" FROM m_customer M  \n"+
						" INNER JOIN m_address A 	\n"+
						" ON M.CUSTOMER_ID = A.CUSTOMER_ID 	\n"+
						" WHERE  M.user_id = "+userBean.getId() +"\n"+
						" AND   (M.oracle_cust_id is null or M.oracle_cust_id =0) \n"+
						" AND   A.PURPOSE IS NOT NULL \n"+
					    //New Version in 072559
						" AND (M.EXPORTED  ='N' OR M.EXPORTED IS NULL OR TRIM(M.EXPORTED) ='') \n"+
						" GROUP BY M.CUSTOMER_ID ";
							
			}else if(tableBean.getTableName().equalsIgnoreCase("M_CUSTOMER_LOCATION")){
				str = " SELECT M.CUSTOMER_ID ,M.CODE AS CUSTOMER_NUMBER ,M.NAME AS CUSTOMER_NAME ,IMAGE_FILE_NAME,LOCATION \n"+
						"FROM m_customer M  \n"+
						"WHERE  M.user_id = "+userBean.getId() +"\n"+
						"AND M.IS_CHANGE='Y' \n";
						
			}else if(tableBean.getTableName().equalsIgnoreCase("t_order") ){
				str ="	select 	order_id,			\n"+
					"	'H'	AS	RECORD_TYPE	,	\n"+
					"	t.ORDER_NO	AS	ORDER_NUMBER  	,	\n"+
					"	t.ORDER_TYPE 	AS	ORDER_TYPE 	,	\n"+
					"	t.ORDER_DATE	AS	ORDER_DATE	,	\n"+
					"	t.ORDER_TIME	AS	ORDER_TIME	,	\n"+
					"	t.CUSTOMER_ID	AS	CUSTOMER_ID	,	\n"+
					"	m.CODE	AS	CUSTOMER_NUMBER	,	\n"+
					"	m.NAME	AS	CUSTOMER_NAME	,	\n"+
					
					/** OLD CODE **/
					"	t.SHIP_ADDRESS_ID	AS	SHIP_TO_SITE_USE_ID ,\n"+
					"	t.BILL_ADDRESS_ID	AS	BILL_TO_SITE_USE_ID	,\n"+
					
					/** NEW CODE **/
					/*"	CASE WHEN IFNULL((SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.SHIP_ADDRESS_ID),0) <> 0 \n"+
					"	     THEN CONCAT(CONCAT(m.code,'-') , (SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.SHIP_ADDRESS_ID)) \n"+ 
					"	     ELSE CONCAT(CONCAT(m.code,'-') , t.SHIP_ADDRESS_ID) \n"+ 
					"	END AS SHIP_TO_SITE_USE_ID, \n"+
					
					"	CASE WHEN IFNULL((SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.BILL_ADDRESS_ID),0) <> 0 \n"+
					"	     THEN CONCAT(CONCAT(m.code,'-') , (SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.BILL_ADDRESS_ID)) \n"+ 
					"	     ELSE CONCAT(CONCAT(m.code,'-') , t.BILL_ADDRESS_ID) \n"+ 
					"	END AS BILL_TO_SITE_USE_ID, \n"+*/
					
					"	t.PAYMENT_TERM	AS	PAYMENT_TERM	,	\n"+
					"	t.USER_ID	AS	SALESREP_ID	,	\n"+
					"	t.PRICELIST_ID	AS	PRICELIST_ID	,	\n"+
					"	(select max(value) from c_reference where code ='OrgID') AS ORG_ID	,	\n"+
					"	t.VAT_CODE	AS	VAT_CODE	,	\n"+
					"	t.VAT_RATE	AS	VAT_RATE 	,	\n"+
					"	t.PAYMENT_METHOD	AS	PAYMENT_METHOD	,	\n"+
					"	t.SHIPPING_DAY	AS	SHIPPING_DAY	,	\n"+
					"	t.SHIPPING_TIME	AS	SHIPPING_TIME	,	\n"+
					"	t.TOTAL_AMOUNT	AS	TOTAL_AMOUNT	,	\n"+
					"	t.VAT_AMOUNT	AS	VAT_AMOUNT	,	\n"+
					"	t.NET_AMOUNT	AS	NET_AMOUNT	,	\n"+
					"	t.PAYMENT	AS	PAYMENT	,	\n"+
					"	t.SALES_ORDER_NO	AS	SALES_ORDER_NO	,	\n"+
					"	t.AR_INVOICE_NO	AS	AR_INVOICE_NO	,	\n"+
					"	t.DOC_STATUS	AS	DOC_STATUS	,	\n"+
					"	t.ORA_BILL_ADDRESS_ID 	AS	ORA_BILL_ADDRESS_ID	,	\n"+
					"	t.ORA_SHIP_ADDRESS_ID	AS	ORA_SHIP_ADDRESS_ID	,	\n"+
					"	'"+tableBean.getFileFtpNameFull()+"'	AS	FILE_NAME ,		\n"+
					"   t.org as ORG ,t.created, t.print_datetime_pick, \n"+
					"   t.po_number ,COALESCE(t.iscash,'N') as iscash \n"+
					"	FROM t_order t ,m_customer m	\n"+
					"	where t.CUSTOMER_ID = m.CUSTOMER_ID	\n"+
					"   and  m.user_id = "+userBean.getId()+" \n"+
					
					//"   and  t.DOC_STATUS = 'SV' \n"+
					//EDIT 13/09/2562 WIT GET ALl DOC_STATUS start 01/10/2562
					//"	and t.order_date >='2019-10-01' \n"+
					//for test 
					"	and t.order_date >='2019-09-01' \n"+
					
					"   and ( t.EXPORTED  = 'N' OR t.EXPORTED  IS NULL OR TRIM(t.EXPORTED) ='') \n"+
					"   ORDER BY t.ORDER_NO \n";
			}
			else if(tableBean.getTableName().equalsIgnoreCase("t_order_rec") ){
				str ="	select 	order_id,			\n"+
					"	'H'	AS	RECORD_TYPE	,	\n"+
					"	t.ORDER_NO	AS	ORDER_NUMBER  	,	\n"+
					"	t.ORDER_TYPE 	AS	ORDER_TYPE 	,	\n"+
					"	t.ORDER_DATE	AS	ORDER_DATE	,	\n"+
					"	t.ORDER_TIME	AS	ORDER_TIME	,	\n"+
					"	t.CUSTOMER_ID	AS	CUSTOMER_ID	,	\n"+
					"	m.CODE	AS	CUSTOMER_NUMBER	,	\n"+
					"	m.NAME	AS	CUSTOMER_NAME	,	\n"+
					
					/** OLD CODE **/
					"	t.SHIP_ADDRESS_ID	AS	SHIP_TO_SITE_USE_ID ,\n"+
					"	t.BILL_ADDRESS_ID	AS	BILL_TO_SITE_USE_ID	,\n"+
					
					/** NEW CODE **/
					/*"	CASE WHEN IFNULL((SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.SHIP_ADDRESS_ID),0) <> 0 \n"+
					"	     THEN CONCAT(CONCAT(m.code,'-') , (SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.SHIP_ADDRESS_ID)) \n"+ 
					"	     ELSE CONCAT(CONCAT(m.code,'-') , t.SHIP_ADDRESS_ID) \n"+ 
					"	END AS SHIP_TO_SITE_USE_ID, \n"+
					
					"	CASE WHEN IFNULL((SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.BILL_ADDRESS_ID),0) <> 0 \n"+
					"	     THEN CONCAT(CONCAT(m.code,'-') , (SELECT A.REFERENCE_ID FROM m_address A WHERE A.ADDRESS_ID =t.BILL_ADDRESS_ID)) \n"+ 
					"	     ELSE CONCAT(CONCAT(m.code,'-') , t.BILL_ADDRESS_ID) \n"+ 
					"	END AS BILL_TO_SITE_USE_ID, \n"+*/
					
					"	t.PAYMENT_TERM	AS	PAYMENT_TERM	,	\n"+
					"	t.USER_ID	AS	SALESREP_ID	,	\n"+
					"	t.PRICELIST_ID	AS	PRICELIST_ID	,	\n"+
					"	(select max(value) from c_reference where code ='OrgID') AS ORG_ID	,	\n"+
					"	t.VAT_CODE	AS	VAT_CODE	,	\n"+
					"	t.VAT_RATE	AS	VAT_RATE 	,	\n"+
					"	t.PAYMENT_METHOD	AS	PAYMENT_METHOD	,	\n"+
					"	t.SHIPPING_DAY	AS	SHIPPING_DAY	,	\n"+
					"	t.SHIPPING_TIME	AS	SHIPPING_TIME	,	\n"+
					"	t.TOTAL_AMOUNT	AS	TOTAL_AMOUNT	,	\n"+
					"	t.VAT_AMOUNT	AS	VAT_AMOUNT	,	\n"+
					"	t.NET_AMOUNT	AS	NET_AMOUNT	,	\n"+
					"	t.PAYMENT	AS	PAYMENT	,	\n"+
					"	t.SALES_ORDER_NO	AS	SALES_ORDER_NO	,	\n"+
					"	t.AR_INVOICE_NO	AS	AR_INVOICE_NO	,	\n"+
					"	t.DOC_STATUS	AS	DOC_STATUS	,	\n"+
					"	t.ORA_BILL_ADDRESS_ID 	AS	ORA_BILL_ADDRESS_ID	,	\n"+
					"	t.ORA_SHIP_ADDRESS_ID	AS	ORA_SHIP_ADDRESS_ID	,	\n"+
					"	'"+tableBean.getFileFtpNameFull()+"'	AS	FILE_NAME ,		\n"+
	                "   t.org as ORG \n"+
					"	FROM t_order t ,m_customer m	\n"+
					"	where t.CUSTOMER_ID = m.CUSTOMER_ID	\n"+
					"   and  m.user_id = "+userBean.getId()+" \n"+
					"   and  t.DOC_STATUS = 'SV' \n"+
					"   and ( t.TEMP2_EXPORTED  = 'N' OR t.TEMP2_EXPORTED  IS NULL OR TRIM(t.EXPORTED) ='') \n"+
					"   ORDER BY t.ORDER_NO \n";
			}
			else if(tableBean.getTableName().equalsIgnoreCase("t_visit")){
				str ="	select			\n"+
					"	t_visit.visit_id	AS 	VISIT_ID,	\n"+
					/** TEXT FORMAL **/
					"	'H'	AS 	RECORD_TYPE,	\n"+
					"	t_visit.CODE	AS 	VISIT_CODE,	\n"+
					"	t_visit.CODE	AS 	CODE,	\n"+
					"	t_visit.VISIT_DATE	AS 	VISIT_DATE,	\n"+
					"	t_visit.VISIT_TIME	AS 	VISIT_TIME,	\n"+
					"	m_customer.code	AS 	CUSTOMER_ID,	\n"+
					"	t_visit.SALES_CLOSED	AS 	SALES_CLOSED,	\n"+
					"	t_visit.UNCLOSED_REASON	AS 	UNCLOSED_REASON,	\n"+
					"	t_visit.USER_ID	AS 	USER_ID,	\n"+
					"	t_visit.ISACTIVE	AS 	ISACTIVE,	\n"+
					"	t_visit.INTERFACES	AS 	INTERFACES,	\n"+
					"	m_customer.code	AS 	CUSTOMER_NUMBER,	\n"+
					"	m_customer.name	AS 	CUSTOMER_NAME ,	\n"+
					"	(select max(value) from c_reference where code ='OrgID') AS ORG_ID	,	\n"+
					"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME	\n"+
				
					"   from t_visit ,m_customer "+
					"   where t_visit.user_id ="+userBean.getId()+ " \n"+
					"   and m_customer.customer_id = t_visit.customer_id \n"+
					"   and t_visit.ISACTIVE ='Y'  \n"+
					"   and ( t_visit.EXPORTED  = 'N' OR t_visit.EXPORTED  IS NULL OR TRIM(t_visit.EXPORTED) ='') \n";
			}else if(tableBean.getTableName().equalsIgnoreCase("m_sales_inventory")){
				str ="	select 	\n"+
				"	(select name from m_sub_inventory s1 where s.sub_inventory_id = s1.sub_inventory_id)AS 	sub_inventory_id,	\n"+
				"	 s.user_id as user_id	\n"+
				"	 from m_sales_inventory	s \n";
			}else if(tableBean.getTableName().equalsIgnoreCase("m_trip")){
				str ="select distinct	\n"+
				"	m.USER_ID	\n"+
				"	from m_trip	m \n"+
				"   where ( m.EXPORTED  = 'N' OR m.EXPORTED  IS NULL OR TRIM(m.EXPORTED) ='') \n";
				
			}else if(tableBean.getTableName().equalsIgnoreCase("t_move_order")){
				str ="select \n"+
					"	'H'	AS 	RECORD_TYPE,	\n"+
					"	REQUEST_NUMBER  , \n"+
					"	REQUEST_DATE ,\n"+
					"	ORGANIZATION_ID , \n"+
					"	(CASE WHEN MOVE_ORDER_TYPE ='MoveOrderRequisition' THEN PD_CODE ELSE SALES_CODE END) AS FROM_SUBINVENTORY_CODE, \n "+
					"	(CASE WHEN MOVE_ORDER_TYPE ='MoveOrderRequisition' THEN SALES_CODE ELSE PD_CODE END) AS TO_SUBINVENTORY_CODE, \n"+
					"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME,	\n"+
					"	DESCRIPTION ,\n"+
					"	USER_ID ,\n"+
					"	( CASE WHEN MOVE_ORDER_TYPE ='MoveOrderRequisition' THEN 'T' ELSE 'R' END) AS MOVE_TYPE ,\n"+
					"	( CASE WHEN STATUS  = 'SV' THEN 'N' ELSE 'X' END) AS STATUS ,\n"+
					"	CREATED_LONG \n"+
				"	from t_move_order \n"+
				"   where ( EXPORTED  = 'N' OR EXPORTED  IS NULL OR TRIM(EXPORTED) ='') \n"+
				"   and request_date <= now() \n"+
				"   and status ='SV' \n"; 
				
	             /** WITTY Edit 02072562: new Case Export Cancel(VO) to Oracle , request_date >= 01082019 **/
				str +="union all \n";
				str +="select \n"+
						"	'H'	AS 	RECORD_TYPE,	\n"+
						"	REQUEST_NUMBER  , \n"+
						"	REQUEST_DATE ,\n"+
						"	ORGANIZATION_ID , \n"+
						"	(CASE WHEN MOVE_ORDER_TYPE ='MoveOrderRequisition' THEN PD_CODE ELSE SALES_CODE END) AS FROM_SUBINVENTORY_CODE, \n "+
						"	(CASE WHEN MOVE_ORDER_TYPE ='MoveOrderRequisition' THEN SALES_CODE ELSE PD_CODE END) AS TO_SUBINVENTORY_CODE, \n"+
						"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME,	\n"+
						"	DESCRIPTION ,\n"+
						"	USER_ID ,\n"+
						"	( CASE WHEN MOVE_ORDER_TYPE ='MoveOrderRequisition' THEN 'T' ELSE 'R' END) AS MOVE_TYPE ,\n"+
						"	( CASE WHEN STATUS  = 'SV' THEN 'N' ELSE 'X' END) AS STATUS ,\n"+
						"	CREATED_LONG \n"+
					"	from t_move_order \n"+
					"   where ( EXPORTED  = 'N' OR EXPORTED  IS NULL OR TRIM(EXPORTED) ='') \n"+
					"   and request_date <= now() \n"+
					"   and status ='VO' \n"+ 
					"   and request_date >= '2019-08-01' \n";
					
			}else if(tableBean.getTableName().equalsIgnoreCase("t_requisition_product")){
				str ="select \n"+
					"	'H'	AS 	RECORD_TYPE,	\n"+
					"	REQUEST_NUMBER  , \n"+
					"	REQUEST_DATE ,\n"+
					"	ORGANIZATION_ID , \n"+
					"	'"+userBean.getCode()+"' AS SALES_CODE , \n"+
					"	REASON_CODE, \n "+
					"	REMARK, \n"+
					"	'O' AS STATUS_O, \n"+
					"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME	\n"+
				"	from t_requisition_product \n"+
				"   where ( EXPORTED  = 'N' OR EXPORTED  IS NULL OR TRIM(EXPORTED) ='') and status ='SV' \n"+
				"   and request_date <= now() \n";
				
			}else if(tableBean.getTableName().equalsIgnoreCase("t_bill_plan")){
				str ="select \n"+
					"	'H'	AS 	RECORD_TYPE,	\n"+
					"	bill_plan_no  , \n"+
					"	bill_plan_request_date ,\n"+
					"   '"+userBean.getCode()+"' as sales_code, \n"+
					"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME,	\n"+
					"	DESCRIPTION \n"+
	
				"	from t_bill_plan \n"+
				"   where ( EXPORTED  = 'N' OR EXPORTED  IS NULL OR TRIM(EXPORTED) ='') and status ='SV' \n"+
				"   and bill_plan_request_date <= now() \n";
				
			}else if(tableBean.getTableName().equalsIgnoreCase("t_stock")){
				str ="select \n"+
					"	'H'	AS 	RECORD_TYPE,	\n"+
					"	t.REQUEST_NUMBER  , \n"+
					"	t.REQUEST_DATE ,\n"+
					"	'"+userBean.getCode()+"' as SALE_CODE ,\n"+
					"	(SELECT M.CODE FROM m_customer M where M.customer_id = t.customer_id) as CUSTOMER_CODE , \n"+
					"   t.description,\n"+
					"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME,	\n"+
					"   t.back_avg_month \n"+
				"	from t_stock t \n"+
				"   where ( t.EXPORTED  = 'N' OR t.EXPORTED  IS NULL OR TRIM(t.EXPORTED) ='') and t.status ='SV' \n";
			
			}else if(tableBean.getTableName().equalsIgnoreCase("t_pd_receipt_his")){
				str ="select \n"+
					"  'H' AS RECORD_TYPE, \n"+
					"	t.ORDER_NO  , \n"+
					"	t.ORDER_DATE ,\n"+
					"	(SELECT M.CODE FROM m_customer M where M.customer_id = t.customer_id) as CUSTOMER_CODE , \n"+
					"   t.RECEIPT_AMOUNT,\n"+
					"   t.PD_PAYMENTMETHOD, \n"+
					"   t.PDPAID_DATE, \n"+
					"   t.CREATED, \n"+
					"   t.CREATED_BY, \n"+
					"   t.CHEQUE_DATE \n"+
				"	from t_pd_receipt_his t \n"+
				"   where ( t.EXPORTED  = 'N' OR t.EXPORTED  IS NULL OR TRIM(t.EXPORTED) ='') \n";
			}else if(tableBean.getTableName().equalsIgnoreCase("t_prod_show")){
				str ="select \n"+
					"  'H' AS RECORD_TYPE, \n"+
					"	t.ORDER_NO  , \n"+
					"	t.CUSTOMER_NO ,\n"+
					"	t.DOC_DATE ,\n"+
					"   t.REMARK, \n"+
					"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME \n"+
				"	from t_prod_show t \n"+
				"   where ( t.EXPORTED  = 'N' OR t.EXPORTED  IS NULL OR TRIM(t.EXPORTED) ='') \n";
			}else if(tableBean.getTableName().equalsIgnoreCase("t_req_promotion")){
				str ="select \n"+
					"  'H' AS RECORD_TYPE, \n"+
					"  '"+userBean.getId()+"' AS SALESREP_ID, \n"+
					"	t.REQUEST_NO  , \n"+
					"	t.REQUEST_DATE ,\n"+
					"	t.PRODUCT_CATAGORY ,\n"+
					"   t.PRODUCT_TYPE, \n"+
					"   t.CUSTOMER_CODE, \n"+
					"   t.PHONE, \n"+
					"   t.NAME, \n"+
					"   t.PROMOTION_START_DATE, \n"+
					"   t.PROMOTION_END_DATE, \n"+
					"   t.PRINT_DATE, \n"+
					"   t.remark, \n"+
					"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME \n"+
				"	from t_req_promotion t \n"+
				"   where ( t.EXPORTED  = 'N' OR t.EXPORTED  IS NULL OR TRIM(t.EXPORTED) ='') \n"+
				"   and status ='SV' ";
			}else if(tableBean.getTableName().equalsIgnoreCase("t_bank_transfer")){
				str ="select \n"+
					"  'H' AS RECORD_TYPE, \n"+
					"	t.TRANSFER_TYPE  , \n"+
					"	t.TRANSFER_BANK ,\n"+
					"	t.TRANSFER_DATE , \n"+
					"	t.TRANSFER_TIME , \n"+
					"   t.AMOUNT,\n"+
					"   t.CHEQUE_NO, \n"+
					"   t.CHEQUE_DATE, \n"+
					"   t.CREATED, \n"+
					"   t.CREATED_BY, \n"+
					"   t.CREATE_DATE, \n"+
					"   t.UPDATED_BY, \n"+
					"   t.UPDATED, \n"+
					"   t.USER_ID, \n"+
					"   t.line_id , \n"+
					"	'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME \n"+
				"	from t_bank_transfer t \n"+
				"   where ( t.EXPORTED  = 'N' OR t.EXPORTED  IS NULL OR TRIM(t.EXPORTED) ='') \n";
			}
			return str;
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * Gen SQl Receipt Type User Credit
	 * @param tableBean
	 * @param userBean
	 * @return
	 */
	public static String genSqlSalesReceiptCredit(TableBean tableBean,User userBean) throws Exception {
		String sql ="	select t_receipt.receipt_id,\n"+
		"		'H'	AS	RECORD_TYPE ,	\n"+
		"		t_receipt.receipt_no	AS	RECEIPT_NO,	\n"+
		"		t_receipt.receipt_date	AS	RECEIPT_DATE,	\n"+
		"		t_receipt.order_type	AS	ORDER_TYPE,	\n"+
		"		t_receipt.customer_id	AS	CUSTOMER_ID,	\n"+
		"		m_customer.code	AS	CUSTOMER_NUMBER,	\n"+
		"		t_receipt.customer_name	AS	CUSTOMER_NAME,	\n"+
		
		/** Don't use ****/
		"		''	AS	PAYMENT_METHOD,	\n"+
		"		''	AS	BANK,	\n"+
		"		''	AS	CHEQUE_NO,	\n"+
		"		null	AS	CHEQUE_DATE,	\n"+
		"		''	AS	CREDIT_CARD_TYPE,	\n"+
		/** Don't use ****/
		
		"		t_receipt.description	AS	DESCRIPTION,	\n"+
		"		(select max(value) from c_reference where code ='OrgID') AS ORG_ID,	\n"+
		"		'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME,		\n"+
		"       IF(AD_USER.PD_PAID='Y','CASH',t_receipt.INTERNAL_BANK) AS INTERNAL_BANK , \n"+
		/** Optional **/
		"      t_receipt.receipt_amount AS amount \n"+
		"	   from t_receipt ,m_customer ,ad_user			\n"+
		"	   where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID 				\n"+
		"	   and ad_user.user_id = t_receipt.user_id \n "+
		"      and t_receipt.ORDER_TYPE = '"+ExportHelper.getOrderType(userBean)+"' \n"+
		"	   and m_customer.user_id = "+userBean.getId() +" \n"+
		"      and  t_receipt.DOC_STATUS = 'SV' \n"+
		"      and ( t_receipt.TEMP2_EXPORTED  = 'N' OR t_receipt.TEMP2_EXPORTED  IS NULL OR TRIM(t_receipt.EXPORTED) ='')     \n";
        return sql;
	}
	
	
	/** 
	 * genSqlCountCaseReceiptLockBoxPayment
	 * @param conn
	 * @param tableBean
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public  static String genSqlCountCaseReceiptLockBoxPayment(TableBean tableBean,User userBean) throws Exception{
		String sqlSelect = "" +
		"	SELECT		\n"+
		"	H.receipt_no as receipt_no,		\n"+
		"	H.code as customer_number,		\n"+
		"	H.name as customer_name,		\n"+
		"	H.receipt_amount as amount      \n"+
		"	FROM(		\n"+
		"		select	\n"+
		"		 r.RECEIPT_DATE	\n"+
		"		,r.receipt_no	\n"+
		"		,m.code	\n"+
		"		,m.name	\n"+
		"		,r.receipt_amount	\n"+
		"		,r.RECEIPT_ID as receipt_id	\n"+
		"		from t_receipt r , m_customer m , t_receipt_by rb	\n"+
		"		where 1=1	\n"+
		"		and r.CUSTOMER_ID = m.CUSTOMER_ID	\n"+
		"		and r.RECEIPT_ID = rb.RECEIPT_ID	\n"+
		"		and r.DOC_STATUS = 'SV'	\n"+
		"		and r.ORDER_TYPE = '"+ExportHelper.getOrderType(userBean)+"'	\n"+
		"		and r.USER_ID ="+userBean.getId()+"	\n"+
		"		and ( r.EXPORTED  = 'N' OR r.EXPORTED  IS NULL OR TRIM(r.EXPORTED) ='' ) \n"+
		"       and  r.DOC_STATUS = 'SV' \n"+
		"		group by r.receipt_id, r.receipt_date,m.code	\n"+
		"	  ) H		\n"+
		"		INNER JOIN	\n"+
		"	 (	\n"+
		"		  select rl.receipt_id as receipt_id	\n"+
		"	      from  t_receipt_line rl, t_receipt_match rm, t_receipt_by rb		\n"+
		"	      where  1=1		\n"+
		"         and rl.RECEIPT_LINE_ID = rm.RECEIPT_LINE_ID	\n"+
		"         and rm.RECEIPT_BY_ID = rb.RECEIPT_BY_ID 	\n"+
		" 	 )D	\n"+
		"	ON H.receipt_id = D.receipt_id  	\n";
		return sqlSelect;
	}
	
	/**
	 * Gen SQl Receipt Type User Van
	 * @param tableBean
	 * @param userBean
	 * @return
	 */
	public static String genSqlSalesReceiptVan(TableBean tableBean,User userBean) throws Exception {
		String sql ="	select t_receipt.receipt_id,\n"+
		"		'H'	AS	RECORD_TYPE ,	\n"+
		"		t_receipt.receipt_no	AS	RECEIPT_NO,	\n"+
		"		t_receipt.receipt_date	AS	RECEIPT_DATE,	\n"+
		"		t_receipt.order_type	AS	ORDER_TYPE,	\n"+
		"		t_receipt.customer_id	AS	CUSTOMER_ID,	\n"+
		"		m_customer.code	AS	CUSTOMER_NUMBER,	\n"+
		"		t_receipt.customer_name	AS	CUSTOMER_NAME,	\n"+
		
		/** Don't use ****/
		"		''	AS	PAYMENT_METHOD,	\n"+
		"		''	AS	BANK,	\n"+
		"		''	AS	CHEQUE_NO,	\n"+
		"		null	AS	CHEQUE_DATE,	\n"+
		"		''	AS	CREDIT_CARD_TYPE,	\n"+
		/** Don't use ****/
		
		"		t_receipt.description	AS	DESCRIPTION,	\n"+
		"		(select max(value) from c_reference where code ='OrgID') AS ORG_ID,	\n"+
		"		'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME,		\n"+
		
		"     (CASE WHEN t_order.van_payment_method ='LOV ' THEN t_receipt.INTERNAL_BANK \n" +
		"       ELSE \n"+
		"        (IF(AD_USER.PD_PAID='Y','CASH',t_receipt.INTERNAL_BANK) ) \n"+
		"      END )AS INTERNAL_BANK , \n"+
		
		/** Optional **/
		"      t_receipt.receipt_amount AS amount \n"+
		
		"	   from t_receipt ,t_order ,m_customer ,ad_user	\n"+
		"	   where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID \n"+
		"	   and t_order.order_no = t_receipt.receipt_no \n "+
		"	   and ad_user.user_id = t_receipt.user_id \n "+
		"      and t_receipt.ORDER_TYPE = '"+ExportHelper.getOrderType(userBean)+"' \n"+
		"	   and m_customer.user_id = "+userBean.getId() +" \n"+
		"      and t_receipt.DOC_STATUS = 'SV' \n"+
		"      and ( t_receipt.EXPORTED  = 'N' OR t_receipt.EXPORTED  IS NULL OR TRIM(t_receipt.EXPORTED) ='')     \n"+
		//Add new issue  Case van No export if internal_bank is null 16/01/2019
		"      and t_receipt.INTERNAL_BANK is not null and t_receipt.INTERNAL_BANK <> '' \n";
        return sql;
	}
	
	
	
}
