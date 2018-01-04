/**012560 */
CREATE TABLE c_temp_location(
	lat varchar(40),
	lng varchar(40),
	create_date timestamp,
	error varchar(200)
);
/** 06/2560 **/
alter table t_stock_line modify create_date date;
alter table t_order add po_number varchar(20);
/** 06/2560/Credit **/
alter table t_stock_line
add (  qty2 decimal(15,5),
       qty3 decimal(15,5),
       sub  decimal(15,5),
       sub2 decimal(15,5),
       sub3 decimal(15,5),
       EXPIRE_DATE2 date,
       EXPIRE_DATE3 date,
       uom2 varchar(20)
);

/** 08/2560 **/
alter table t_stock 
add(back_avg_month varchar(5));

alter table t_stock_line
add(avg_order_qty int);

alter table t_stock_line
modify expire_date date ;

alter table m_customer
add (lat varchar(100),
	lng varchar(100)
	);
	
alter table m_customer
add (trip_day varchar(5),
	trip_day2 varchar(5),
	trip_day3 varchar(5)
	);
	
delete from pens.c_reference where reference_id in (2601,2602,2603);
insert into pens.c_reference values ('2601','PartyType','ร้านโชห่วย','ร้านโชห่วย' ,'P','Y');
insert into pens.c_reference values ('2602','PartyType','ร้านขายยา','ร้านขายยา','D','Y' );
insert into pens.c_reference values ('2603','PartyType','ร้านเพ็ทช็อป','ร้านเพ็ทช็อป','A','Y');
insert into pens.c_reference values ('2604','PartyType','7/11','7/11','S','Y');

insert into pens.c_reference values ('2700','CustShowTrip','ShowOnlyTrip','ShowOnlyTrip','Y','Y');

/** 10/2560 **/
alter table c_reference modify value varchar(20);
 
insert into pens.c_reference values ('2800','CreditDateFix','CreditDateFix','CreditDateFix','01/01/2560','Y');
insert into pens.c_reference values ('2801','VanDateFix','VanDateFix','VanDateFix Case PD_PAID NO','01/10/2560','Y');

CREATE TABLE pens.t_receipt_pdpaid_no (
	ORDER_NO varchar(20) NOT NULL,
	CUSTOMER_ID int(10),
	ORDER_DATE date,
	RECEIPT_AMOUNT decimal(10,2),
	PDPAID_DATE date,
	PD_PAYMENTMETHOD varchar(4),
	CREATED timestamp NOT NULL,
	CREATED_BY int(10),
	PRIMARY KEY (ORDER_NO)
);
CREATE TABLE pens.c_control_code (
	class_name varchar(50) NOT NULL,
	method_name varchar(50) NOT NULL,
	ISACTIVE char(1) ,
	PRIMARY KEY (class_name,method_name)
);
insert into pens.c_control_code values ('OrderUtils','canSaveCreditVan','Y');
insert into pens.c_control_code values ('RunUpdateSalesAppAutoServlet','startDeploySalesAppAuto','N');
alter table m_modifier_attr modify value_from decimal(12,4);
alter table m_modifier_attr modify value_to decimal(12,4);

/** 11/2560 **/
CREATE  TABLE pens.i_import_config (
	table_name varchar(50) NOT NULL,
	import_type varchar(40) NOT NULL,
	script_sql varchar(1000) NOT NULL,
	PRIMARY KEY (table_name,import_type)
);
delete from i_import_config where import_type ='pre-import-master';
insert into  i_import_config values('m_modifier','pre-import-master','delete from m_relation_modifier|delete from m_qualifier|delete from m_modifier_attr|delete from m_modifier_line|delete from m_modifier|');
insert into  i_import_config values('m_pricelist','pre-import-master','delete from m_pricelist|');
insert into  i_import_config values('m_product_category','pre-import-master','delete from m_product_category|');
insert into  i_import_config values('m_product_price','pre-import-master','delete from m_product_price|');
insert into  i_import_config values('m_sales_target_new','pre-import-master','delete  from m_sales_target_new where MONTH(TARGET_FROM) = MONTH(sysdate())   AND YEAR(TARGET_FROM) = YEAR(sysdate())|');
insert into  i_import_config values('m_uom','pre-import-master','delete from m_uom|');
insert into  i_import_config values('m_uom_class','pre-import-master','delete from m_uom_class|');
insert into  i_import_config values('m_uom_class_conversion','pre-import-master','delete from m_uom_class_conversion|');
insert into  i_import_config values('m_uom_conversion','pre-import-master','delete from m_uom_conversion|');

CREATE TABLE pens.c_control_salesapp_version (
	config_type varchar(50) NOT NULL,
	value varchar(200) NOT NULL,
	updated date,
	PRIMARY KEY (config_type)
);
insert into pens.c_control_code values ('BatchImportWorker','reimportUpdateTrans','N');

alter TABLE t_credit_note add doc_status varchar(5);
update t_credit_note set doc_status ='SV' where (doc_status ='' or doc_status is null);

CREATE  TABLE pens.t_adjust (
	adjust_id bigint NOT NULL,
	ar_invoice_no varchar(20) NOT NULL,
	adjust_date date not null,
	adjust_type varchar(20) NOT NULL,
	adjust_amount decimal(10,2),
	reason varchar(100) ,
	comment varchar(100) ,
	UPDATED timestamp,
	UPDATED_BY int(10),
	CREATED_BY int(10),
	CREATED timestamp NOT NULL,
	PRIMARY KEY (adjust_id)
);
CREATE   TABLE pens.t_temp_import_trans (
    file_name varchar(100) NOT NULL,
	table_name varchar(50) NOT NULL,
	import_type varchar(30) NOT NULL,
	key_no varchar(100) not null,
	line_str varchar(1000) not null,
	AMOUNT decimal(10,2),
	receipt_no varchar(100) ,
	seq bigint not null,
	error_msg varchar(1000) not null,
	created timestamp
);
CREATE TABLE pens.t_receipt_his (
    TRANS_ID int(10) NOT NULL,
	RECEIPT_ID int(10) NOT NULL,
	RECEIPT_NO varchar(20) NOT NULL,
	RECEIPT_DATE date,
	ORDER_TYPE varchar(5),
	CUSTOMER_ID int(10),
	CUSTOMER_NAME varchar(200),
	PAYMENT_METHOD varchar(5),
	BANK varchar(100),
	CHEQUE_NO varchar(20),
	CHEQUE_DATE date,
	RECEIPT_AMOUNT decimal(10,2),
	INTERFACES char(1) NOT NULL,
	DOC_STATUS char(5),
	USER_ID int(10),
	CREATED timestamp NOT NULL,
	CREATED_BY int(10),
	UPDATED datetime,
	UPDATED_BY int(10),
	CREDIT_CARD_TYPE varchar(10),
	DESCRIPTION varchar(200),
	PREPAID char(1) NOT NULL,
	APPLY_AMOUNT decimal(10,2),
	EXPORTED char(1) NOT NULL,
	INTERNAL_BANK varchar(5),
	ISPDPAID varchar(1),
	PDPAID_DATE date,
	PD_PAYMENTMETHOD varchar(4),
	TEMP2_EXPORTED varchar(1),
	PRIMARY KEY (TRANS_ID,RECEIPT_ID)
);
CREATE TABLE pens.t_receipt_line_his (
   TRANS_ID int(10) NOT NULL,
	RECEIPT_LINE_ID int(10) NOT NULL,
	LINE_NO int(10),
	RECEIPT_ID int(10),
	AR_INVOICE_NO varchar(20),
	SALES_ORDER_NO varchar(20),
	INVOICE_AMOUNT decimal(10,2),
	CREDIT_AMOUNT decimal(10,2),
	PAID_AMOUNT decimal(10,2),
	REMAIN_AMOUNT decimal(10,2),
	ORDER_ID int(10),
	ORDER_LINE_ID int(10),
	UPDATED datetime,
	UPDATED_BY int(10),
	CREATED_BY int(10),
	CREATED timestamp NOT NULL,
	DESCRIPTION varchar(200),
	PRIMARY KEY (TRANS_ID,RECEIPT_LINE_ID)
);
CREATE TABLE pens.t_receipt_match_his (
   TRANS_ID int(10) NOT NULL,
	RECEIPT_MATCH_ID int(10) NOT NULL,
	RECEIPT_BY_ID int(10),
	RECEIPT_LINE_ID int(10),
	PAID_AMOUNT decimal(15,5),
	CREATED timestamp NOT NULL,
	CREATED_BY int(10),
	UPDATED datetime,
	UPDATED_BY int(10),
	RECEIPT_ID int(10),
	PRIMARY KEY (TRANS_ID,RECEIPT_MATCH_ID)
);

CREATE TABLE pens.t_receipt_cn_his (
   TRANS_ID int(10) NOT NULL,
	RECEIPT_CN_ID int(10) NOT NULL,
	CREDIT_NOTE_ID int(10) NOT NULL,
	RECEIPT_ID int(10) NOT NULL,
	CREATED timestamp NOT NULL,
	CREATED_BY int(10) NOT NULL,
	UPDATED timestamp,
	UPDATED_BY int(10),
	PAID_AMOUNT decimal(10,2),
	REMAIN_AMOUNT decimal(10,2),
	credit_amount decimal(10,2),
	PRIMARY KEY (TRANS_ID,RECEIPT_CN_ID)
);

CREATE TABLE pens.t_receipt_match_cn_his (
  TRANS_ID int(10) NOT NULL,
	RECEIPT_MATCH_CN_ID int(10) NOT NULL,
	RECEIPT_BY_ID int(10),
	RECEIPT_CN_ID int(10),
	PAID_AMOUNT decimal(10,2),
	CREATED datetime,
	CREATED_BY int(10),
	UPDATED datetime,
	UPDATED_BY int(10),
	RECEIPT_ID int(10),
	PRIMARY KEY (TRANS_ID,RECEIPT_MATCH_CN_ID)
);

CREATE TABLE pens.t_receipt_by_his (
   TRANS_ID int(10) NOT NULL,
	RECEIPT_BY_ID int(10) NOT NULL,
	PAYMENT_METHOD varchar(5),
	BANK varchar(100),
	CHEQUE_NO varchar(20),
	CHEQUE_DATE date,
	RECEIPT_AMOUNT decimal(10,2),
	CREDIT_CARD_TYPE varchar(30),
	PAID_AMOUNT decimal(10,2),
	REMAIN_AMOUNT decimal(10,2),
	RECEIPT_ID int(10) NOT NULL,
	CREATED_BY int(10),
	UPDATED datetime,
	UPDATED_BY int(10),
	CREATED timestamp NOT NULL,
	SEED_ID varchar(30),
	CREDITCARD_EXPIRED varchar(5),
	WRITE_OFF char(1) NOT NULL,
	receive_Cash_Date date,
	PRIMARY KEY (TRANS_ID,RECEIPT_BY_ID)
);

delete from c_reference where code='PaymentMethod';
insert into c_reference values (1101,'PaymentMethod','เงินสด','เงินสด','CS','Y');
insert into c_reference values (1102,'PaymentMethod','เช็ค','เช็ค','CH','Y');
insert into c_reference values (1103,'PaymentMethod','Bank Transfer','เงินโอน','TR','Y');
insert into c_reference values (1104,'PaymentMethod','บัตรเครดิต','บัตรเครดิต','CR','Y');
insert into c_reference values (1105,'PaymentMethod','ชำระผ่าน PD','ชำระผ่าน PD','PD','N');
insert into c_reference values (1106,'PaymentMethod','ชำระผ่านแอร์เพย์','ชำระผ่านแอร์เพย์','AP','N');

alter table c_reference modify name varchar(255);
delete from c_reference where code='TransferBank';
insert into pens.c_reference values (2302 ,'TransferBank','ธนาคารไทยพาณิชย์|สาขาสาธุประดิษฐ์|068-2-81805-7' , 'ธนาคารไทยพาณิชย์-สาขาสาธุประดิษฐ์ 068-2-81805-7','002','Y');					
insert into pens.c_reference values (2303 ,'TransferBank','ธนาคารกสิกรไทย|สาขาท่าเรือสาธุประดิษฐ์|048-2-51789-9' , 'ธนาคารกสิกรไทย-สาขาท่าเรือสาธุประดิษฐ์ 048-2-51789-9','005','Y');					
insert into pens.c_reference values (2304 ,'TransferBank','ธนาคาร กรุงไทย จำกัด (มหาชน)|สาขาสาธุประดิษฐ์|083-0-32922-6' , 'ธนาคาร กรุงไทย จำกัด (มหาชน)-สาขาสาธุประดิษฐ์ 083-0-32922-6','009','Y');					
insert into pens.c_reference values (2305 ,'TransferBank','ธนาคารกรุงเทพ|สาขาถนนรัชดา-สี่แยกสาธุประดิษฐ์|195-4-75514-4' , 'ธนาคารกรุงเทพ-สาขาถนนรัชดา-สี่แยกสาธุประดิษฐ์ 195-4-75514-4','006','Y');					
insert into pens.c_reference values (2306 ,'TransferBank','ธนาคารกรุงเทพ|สาขาถนนรัชดา-สี่แยกสาธุประดิษฐ์|195-4-75402-2' , 'ธนาคารกรุงเทพ-สาขาถนนรัชดา-สี่แยกสาธุประดิษฐ์ 195-4-75402-2','001','Y');					

alter table t_receipt_by add receive_Cash_Date date;
alter table t_receipt_by_his add receive_Cash_Date date;
alter table t_temp_import_trans add doc_status varchar(5);

insert into pens.c_lockbox values (3,'LCK03',2304,123);	
insert into pens.c_lockbox values (4,'LCK04',2306,123);	
insert into pens.c_lockbox values (5,'LCK05',2303,123);				
insert into pens.c_lockbox values (6,'LCK06',2305,123);		
insert into pens.c_lockbox values (7,'LCK07',2302,123);	

alter table m_product add taxable char(1);
alter table t_order add total_amount_non_vat decimal(15,2);
alter table t_order_line add taxable char(1);
update t_order_line set taxable ='Y' where (taxable ='' or taxable is null);

alter table t_receipt_line add import_trans_id bigint;
