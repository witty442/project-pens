
/** 03/2562 **/
INSERT INTO pens.c_doctype VALUES (444, 'StockReturn', 'StockReturn', 'Y');
insert into pens.c_control_code values ('ClearDupDB','clearDupCustDB','N');
insert into pens.c_control_code values ('SessionUtils','clearSessionUnusedFormModel','Y');

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