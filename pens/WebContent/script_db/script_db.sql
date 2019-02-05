
/** 11/2561 **/
alter table t_order_line add selling_price decimal(15,5);

/** 01/2562 **/
alter table ad_user add money_to_pens char(1);

alter table pens.t_receipt_pdpaid_no add CHEQUE_DATE date;
alter table pens.t_pd_receipt_his add CHEQUE_DATE date;

alter table c_reference add is_load char(1);
update c_reference set is_load ='Y';

CREATE TABLE pens.t_bank_transfer (
   line_id int(10) NOT NULL,
   USER_ID int(10) not null,
   create_date date NOT NULL,
	transfer_date date NOT NULL,
	transfer_type varchar(10) NOT NULL,
	transfer_bank varchar(10) NOT NULL,
	transfer_time varchar(10) NOT NULL,
	amount decimal(15,5),
	cheque_no varchar(30) ,
	cheque_date date ,
	status varchar(2),
	exported char(1),
	CREATED timestamp NOT NULL,
	CREATED_BY varchar(20),
	UPDATED datetime ,
	UPDATED_BY varchar(20),
	PRIMARY KEY (line_id)
);
INSERT INTO pens.c_reference(REFERENCE_ID, CODE, name, DESCRIPTION, value, ISACTIVE) VALUES (3101, 'TransferBankVAN', 'ธนาคารไทยพาณิชย์', 'ธนาคารไทยพาณิชย์-สาขาสาธุประดิษฐ์ 068-2-81805-7', '002', 'Y');
INSERT INTO pens.c_reference(REFERENCE_ID, CODE, name, DESCRIPTION, value, ISACTIVE) VALUES (3102, 'TransferBankVAN', 'ธนาคารกสิกรไทย', 'ธนาคารกสิกรไทย-สาขาท่าเรือสาธุประดิษฐ์ 048-2-51789-9', '005', 'Y');
INSERT INTO pens.c_reference(REFERENCE_ID, CODE, name, DESCRIPTION, value, ISACTIVE) VALUES (3103, 'TransferBankVAN', 'ธนาคาร กรุงไทย จำกัด (มหาชน)', 'ธนาคาร กรุงไทย จำกัด (มหาชน)-สาขาสาธุประดิษฐ์ 083-0-32922-6', '009', 'Y');
INSERT INTO pens.c_reference(REFERENCE_ID, CODE, name, DESCRIPTION, value, ISACTIVE) VALUES (3104, 'TransferBankVAN', 'ธนาคารกรุงเทพ', 'ธนาคารกรุงเทพ-สาขาถนนรัชดา-สี่แยกสาธุประดิษฐ์ 195-4-75402-2', '001', 'Y');

/** Case Credit add old table van **/
insert into pens.c_reference values ('3000','ProdShowFileSize','ขนาดไฟล์  Prod Show upload(KB)','ขนาดไฟล์  Prod Show upload(KB)','2000','Y');
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
INSERT INTO pens.c_doctype(DOCTYPE_ID, NAME, DESCRIPTION, ISACTIVE)
VALUES (333, 'ProdShow', 'ProdShow', 'Y');