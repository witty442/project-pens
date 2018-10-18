package com.isecinc.pens.inf.helper;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.TableBean;

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
			
			 if(tableBean.getTableName().equalsIgnoreCase("t_order") ){
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
					"   t.po_number ,COALESCE(t.iscash,'N') as iscash , \n"+
					"   t.customer_bill_name ,t.address_desc ,\n"+
					"   t.id_no ,t.passport_no \n"+
					"	FROM t_order t ,m_customer m	\n"+
					"	where t.CUSTOMER_ID = m.CUSTOMER_ID	\n"+
					"   and  m.user_id = "+userBean.getId()+" \n"+
					"   and  t.DOC_STATUS = 'SV' \n"+
					"   and ( t.EXPORTED  = 'N' OR t.EXPORTED  IS NULL OR TRIM(t.EXPORTED) ='') \n"+
					"   ORDER BY t.ORDER_NO \n";
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
	public static String genSqlSalesReceiptMaya(TableBean tableBean,User userBean) throws Exception {
		String sql ="	select t_receipt.receipt_id,\n"+
		"		'H'	AS	RECORD_TYPE ,	\n"+
		"		t_receipt.receipt_no	AS	RECEIPT_NO,	\n"+
		"		t_receipt.receipt_date	AS	RECEIPT_DATE,	\n"+
		"		t_receipt.order_type	AS	ORDER_TYPE,	\n"+
		"		t_receipt.customer_id	AS	CUSTOMER_ID,	\n"+
		"		m_customer.code	AS	CUSTOMER_NUMBER,	\n"+
		"		t_receipt.customer_name	AS	CUSTOMER_NAME,	\n"+
		
		"		t_order.PAYMENT_METHOD,	\n"+
		"		''	AS	BANK,	\n"+
		"		t_order.CREDIT_CARD_NO,	\n"+
		"		t_order.CREDIT_CARD_EXPIRE_DATE ,	\n"+
		"		t_order.CREDIT_CARD_TYPE,	\n"+
	
		"		t_receipt.description	AS	DESCRIPTION,\n"+
		"		(select max(value) from c_reference where code ='OrgID') AS ORG_ID,	\n"+
		"		'"+tableBean.getFileFtpNameFull()+"' AS	FILE_NAME,	\n"+
		"       t_receipt.INTERNAL_BANK , \n"+
		
		/** Optional **/
		"      t_receipt.receipt_amount AS amount \n"+
		"	   from t_receipt,t_order ,m_customer ,ad_user	\n"+
		"	   where t_receipt.CUSTOMER_ID = m_customer.CUSTOMER_ID \n"+
		"	   and t_receipt.RECEIPT_NO = t_order.ORDER_NO \n"+
		"	   and ad_user.user_id = t_receipt.user_id \n "+
		"      and t_receipt.ORDER_TYPE = '"+ExportHelper.getOrderType(userBean)+"' \n"+
		"	   and m_customer.user_id = "+userBean.getId() +" \n"+
		"      and t_receipt.DOC_STATUS = 'SV' \n"+
		"      and t_order.DOC_STATUS = 'SV' \n"+
		"      and ( t_receipt.EXPORTED  = 'N' OR t_receipt.EXPORTED  IS NULL OR TRIM(t_receipt.EXPORTED) ='')  \n";
        return sql;
	}
	
	
	
}
