/** 10/2561 **/
CREATE TABLE pens.m_barcode (
	barcode varchar(40),
	product_code varchar(20),
	material_master varchar(40),
	CREATED timestamp NOT NULL,
	CREATED_BY int(10),
	UPDATED timestamp,
	UPDATED_BY int(10),
	primary key(barcode)
);

alter table t_order
add (
  customer_bill_name varchar(200),
  address_desc varchar(100),
  ID_NO varchar(30),
  PASSPORT_NO varchar(30),
  credit_card_type varchar(10),
  credit_card_no varchar(30),
  credit_card_expire_date varchar(10)
  );
  
alter table t_receipt_by
add credit_card_no varchar(30);

update c_reference set isactive ='N' where value in('CH','TR') and code ='PaymentMethod';
insert i_import_config values('m_barcode','pre-import-master','delete from m_barcode|');

 