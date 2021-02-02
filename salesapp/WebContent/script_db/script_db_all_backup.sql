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

delete from pens.c_reference where reference_id in (2601,2602,2603,2604);
insert into pens.c_reference values ('2601','PartyType','ร้านโชห่วย','ร้านโชห่วย' ,'P','Y','Y');
insert into pens.c_reference values ('2602','PartyType','ร้านธงฟ้า','ร้านธงฟ้า','B','Y','Y' );
insert into pens.c_reference values ('2603','PartyType','ร้านเพ็ทช็อป','ร้านเพ็ทช็อป','A','N','Y');
insert into pens.c_reference values ('2604','PartyType','7/11','7/11','S','N','Y');
