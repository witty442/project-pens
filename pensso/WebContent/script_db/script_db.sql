/** 05-2563 **/
alter table pens.t_stock_return_line add reason varchar(100);
insert into pens.c_reference values ( 3201 , 'ReasonReturn' , 'Expired Product' ,'สินค้าหมดอายุ' , 'EXPIRED PRODUCT' , 'Y' , 'Y' );
insert into pens.c_reference values ( 3202 , 'ReasonReturn' , 'Damaged Product' ,'สินค้าชำรุด' , 'DAMAGED PRODUCT' , 'Y' , 'Y' );
/** 05-2563.1 **/
CREATE TABLE pens.t_stock_discount (
	request_number varchar(30) NOT NULL,
	CUSTOMER_ID int(10) NOT NULL,
	request_date date NOT NULL,
	back_date date NOT NULL,
	description varchar(300),
	line_amount decimal(15,5),
	vat_amount decimal(15,5),
	net_amount decimal(15,5),
	vat_rate varchar(5),
	status varchar(2),
	exported char(1),
	USER_ID int(10),
	CREATED timestamp NOT NULL,
	CREATED_BY varchar(20),
	UPDATED timestamp,
	UPDATED_BY varchar(20),
	PRIMARY KEY (request_number)
);
CREATE TABLE pens.t_stock_discount_line (
	request_number varchar(30) NOT NULL,
	line_number int(10) NOT NULL,
	inventory_item_id int(10) ,
	product_name varchar(80) ,
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
	line_amount decimal(15,5),
	vat_amount decimal(15,5),
	net_amount decimal(15,5),
	status varchar(2),
	exported char(1),
	USER_ID int(10),
	CREATED timestamp NOT NULL,
	CREATED_BY varchar(20),
	UPDATED timestamp,
	UPDATED_BY varchar(20),
	PRIMARY KEY (request_number,line_number)
);
CREATE TABLE pens.t_stock_discount_init (
	CUSTOMER_CODE varchar(30) NOT NULL,
	ar_invoice_no varchar(30) NOT NULL,
	USER_ID int(10),
	PRIMARY KEY (CUSTOMER_CODE,ar_invoice_no)
);
insert into pens.c_reference values ( 2503 , 'backDateInvStkDis' , 'backDateInvStkDis' ,'Back Date Stock Discount(Month) ' , '8' , 'Y' , 'N' );
insert into pens.c_reference values ( 2504 , 'vatRateInvStkDis' , 'vatRateInvStkDis' ,'Vat Rate Stock Discount ' , '1.5' , 'Y' , 'N' );

INSERT INTO pens.c_doctype VALUES (555, 'StockDiscount', 'StockDiscount', 'Y');

/** 05-2563.2 **/
alter table t_stock_return_line
add expire_date date

/** 06-2563 **/
CREATE TABLE pens.c_after_action_sql (
	action_name varchar(50) NOT NULL,
	sql_action varchar(200) ,
	create_date date,
	PRIMARY KEY (action_name)
);
/** 07-2563 **/
alter table m_customer add cust_group varchar(40);
/** 08-2563 **/

