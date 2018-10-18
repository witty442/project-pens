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
  address_desc varchar(255),
  ID_NO varchar(30),
  PASSPORT_NO varchar(30),
  credit_card_type varchar(10),
  credit_card_no varchar(30),
  credit_card_expire_date varchar(10)
  );
  
alter table t_order
modify address_desc varchar(255);

alter table t_order_line
add selling_price decimal(15,5);

alter table t_receipt_by
add credit_card_no varchar(30);

update c_reference set isactive ='N' where value in('CH','TR') and code ='PaymentMethod';
insert i_import_config values('m_barcode','pre-import-master','delete from m_barcode|');

alter table c_reference
add is_load char(1);

alter table c_reference
modify value varchar(255);
INSERT INTO pens_shop.c_reference
	(REFERENCE_ID, CODE, name, DESCRIPTION, value, ISACTIVE, is_load)
VALUES 
	(4000, 'ADDRESS_MAYA', 'ADDRESS_MAYA', 'ADDRESS_MAYA', 'เลขที่ 5, 5/5 ห้อง 210/2 ชั้นที่ 2 ถ.ซุปเปอร์ไฮเวย์ ต.ช้างเผือก อ.เมืองเชียงใหม่ เชียงใหม่ 50300 สาขาที่ 5', 'Y', 'N');
alter table t_receipt_by modify creditcard_expired char(10);