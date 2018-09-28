/** 05/2561 **/
alter table t_order add van_payment_method varchar(15);
insert into pens.c_reference values ('2900','VanPaymentMethod','à§Ô¹Ê´','à§Ô¹Ê´ ','CASH','Y');
insert into pens.c_reference values ('2901','VanPaymentMethod','à§Ô¹àª×èÍ','à§Ô¹àª×èÍ','CREDIT','Y');

/** 06/2561 **/
insert into pens.c_reference values ('3000','ProdShowFileSize','¢¹Ò´ä¿Åì  Prod Show upload(KB)','¢¹Ò´ä¿Åì  Prod Show upload(KB)','2000','Y');

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
/** 09/2561 **/
alter table t_order add is_promotion_special char(1);
alter table t_order_line add is_promotion_special char(1);