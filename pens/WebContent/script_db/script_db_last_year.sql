
/** 01-2561 **/
alter table t_temp_import_trans
modify seq decimal(15,2);

alter table t_order_line add selling_price decimal(15,5);

insert into pens.c_control_code values ('OrderUtils','canPrintBillCreditCaseVan','Y');

create  table t_pd_receipt_his (
    ORDER_NO varchar(20) NOT NULL,
	CUSTOMER_ID int(10),
	ORDER_DATE date,
	RECEIPT_AMOUNT decimal(10,2),
	PDPAID_DATE date,
	PD_PAYMENTMETHOD varchar(4),
	EXPORTED char(1),
	CREATED timestamp NOT NULL,
	CREATED_BY int(10),
	PRIMARY KEY (ORDER_NO)
);
insert into pens.c_control_code values ('RunScriptDBAction','runManualScriptProcessOLD','N');
insert into pens.c_control_code values ('RunScriptDBAction','runManualScriptProcessNEW','Y');
insert into pens.c_control_code values ('OrderProcess','fillLinesShowPromotion','Y');
insert into pens.c_control_code values ('ModifierProcess','promotionalGoodProcessNew','Y');
insert into pens.c_control_code values ('OrderProcess','sumQtyProductPromotionDuplicate','Y');
/** 02-2561 **/
insert into pens.c_control_code values ('PrinterUtils','selectPrinterSmallIsOnlineCheckOnline','Y');
/** 05/2561 **/
alter table t_order add van_payment_method varchar(15);
insert into pens.c_reference values ('2900','VanPaymentMethod','à§Ô¹Ê´','à§Ô¹Ê´ ','CASH','Y','Y');
insert into pens.c_reference values ('2901','VanPaymentMethod','à§Ô¹àª×èÍ','à§Ô¹àª×èÍ','CREDIT','Y','Y');

/** 06/2561 **/
insert into pens.c_reference values ('3000','ProdShowFileSize','¢¹Ò´ä¿Åì  Prod Show upload(KB)','¢¹Ò´ä¿Åì  Prod Show upload(KB)','2000','Y','Y');

create table t_prod_show(
  Order_no varchar(30)not null,
  Customer_no varchar(30)not null,	
  doc_date	date,
  remark varchar(100),
  Exported	char(1),
  CREATED timestamp NOT NULL,
  CREATED_BY int(10),
  UPDATED datetime ,
  UPDATED_BY int(10),
  PRIMARY KEY (order_no)
);
	
create  table t_prod_show_line(
 Order_no varchar(30)not null,
  id int not null,
  brand varchar(10)not null,	
  pic1 varchar(100)not null,
  pic2 varchar(100),
  pic3 varchar(100),
  CREATED timestamp NOT NULL,
  CREATED_BY int(10),
  UPDATED datetime ,
  UPDATED_BY int(10),
  PRIMARY KEY (order_no,id)
);

/** 06/2561 Credit Version 2 **/
alter table t_req_promotion add exported char(1);
insert into pens.c_control_code values ('UpdateSalesProcess','ImportReceiptFunction2','Y');
alter table pens.t_temp_import_trans modify seq decimal(15,3);
CREATE TABLE pens.t_temp_import_trans_err (
	file_name varchar(100) NOT NULL,
	table_name varchar(50) NOT NULL,
	import_type varchar(30) NOT NULL,
	key_no varchar(100) NOT NULL,
	line_str varchar(1000) NOT NULL,
	AMOUNT decimal(10,2),
	receipt_no varchar(100),
	seq decimal(15,3),
	error_msg varchar(1000) NOT NULL,
	created timestamp NOT NULL,
	doc_status varchar(5)
);

/** 03/2562 **/
INSERT INTO pens.c_doctype VALUES (444, 'StockReturn', 'StockReturn', 'Y');
insert into pens.c_control_code values ('ClearDupDB','clearDupCustDB','N');
insert into pens.c_control_code values ('SessionUtils','clearSessionUnusedFormModel','Y');
alter table c_reference add is_load char(1);

INSERT INTO c_reference(REFERENCE_ID, CODE, name, DESCRIPTION, value, ISACTIVE, is_load) 
VALUES (2502, 'backDateInvStkReturn', 'backDateInvoiceStockReturn', 'backDateInvoiceStockReturn', '12', 'Y', 'N');
CREATE TABLE pens.t_stock_return_init (
	CUSTOMER_CODE varchar(30) NOT NULL,
	ar_invoice_no varchar(30) NOT NULL,
	inventory_item_id int(10) NOT NULL,
	pri_qty decimal(15,5),
	USER_ID int(10),
	PRIMARY KEY (CUSTOMER_CODE,ar_invoice_no,inventory_item_id)
);
CREATE TABLE pens.t_stock_return (
	request_number varchar(30) NOT NULL,
	CUSTOMER_ID int(10) NOT NULL,
	request_date date NOT NULL,
	back_date date NOT NULL,
	description varchar(300),
	total_nonvat_amount decimal(15,5),
	total_vat_amount decimal(15,5),
	total_amount decimal(15,5),
	status varchar(2),
	exported char(1),
	USER_ID int(10),
	CREATED timestamp not null,
	CREATED_BY varchar(20),
	UPDATED timestamp NULL DEFAULT NULL,
	UPDATED_BY varchar(20),
	PRIMARY KEY (request_number)
);
CREATE TABLE pens.t_stock_return_line (
	request_number varchar(30) NOT NULL,
	line_number int(10) NOT NULL,
	inventory_item_id int(10) NOT NULL,
	ar_invoice_no varchar(30) NOT NULL,
	
	pri_qty decimal(15,5),
	uom1_qty decimal(15,5),
	uom2_qty decimal(15,5),
	
	uom2 varchar(20),
	uom1_pac decimal(15,5),
	uom2_pac decimal(15,5),
	uom1_price decimal(15,5),
	
	uom1_Conv_Rate decimal(15,5),
	uom2_Conv_Rate decimal(15,5),
	
	discount decimal(15,5),
	total_amount decimal(15,5),
	status varchar(2),
	exported char(1),
	USER_ID int(10),
	CREATED timestamp not null,
	CREATED_BY varchar(20),
	UPDATED timestamp NULL DEFAULT NULL,
	UPDATED_BY varchar(20),
	PRIMARY KEY (request_number,line_number)
);
alter table ad_user add money_to_pens char(1);
CREATE TABLE pens.t_bank_transfer (
	line_id int(10) NOT NULL,
	USER_ID int(10) NOT NULL,
	create_date date NOT NULL,
	transfer_date date NOT NULL,
	transfer_type varchar(10) NOT NULL,
	transfer_bank varchar(10) NOT NULL,
	transfer_time varchar(10) NOT NULL,
	amount decimal(15,5),
	cheque_no varchar(30),
	cheque_date date,
	status varchar(2),
	exported char(1),
	CREATED timestamp NOT NULL,
	CREATED_BY varchar(20),
	UPDATED datetime,
	UPDATED_BY varchar(20),
	PRIMARY KEY (line_id)
);