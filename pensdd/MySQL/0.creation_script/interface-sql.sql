DROP TABLE monitor;

CREATE TABLE monitor (
  TRANSACTION_ID bigint(20) NOT NULL ,
  MONITOR_ID bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(50) ,
  type varchar(20) ,
  channel varchar(20) ,
  sale_rep_code varchar(20),
  transaction_type varchar(20) ,
  submit_date timestamp NULL DEFAULT NULL,
  status int(11) ,
  create_date timestamp NULL DEFAULT NULL,
  create_user varchar(30) ,
  update_date timestamp NULL DEFAULT NULL,
  update_user varchar(30) ,
  file_count int,
  error_code varchar(300) ,
  error_msg varchar(300) ,
  PRIMARY KEY (MONITOR_ID)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

DROP TABLE monitor_item ;

CREATE TABLE monitor_item (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  MONITOR_ID bigint(20) ,
  group_name varchar(100),
  table_name varchar(50) ,
  file_name varchar(50) ,
  file_size varchar(50),
  submit_date timestamp NULL DEFAULT NULL,
  status int(11) ,
  data_count int(11) ,
  success_count int(11) ,
  error_code varchar(300) ,
  error_msg varchar(300) ,
  source varchar(50) ,
  destination varchar(50) ,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

DROP TABLE monitor_item_detail;

CREATE TABLE monitor_item_detail (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  MONITOR_ITEM_ID bigint(20) ,
  customer_code varchar(30),
  customer_name varchar(100),
  code varchar(30) ,/**  ORDER_NO,RECEIPT_NO ,VISIT_ID**/
  type varchar(30) ,/** ORDER ,RECEIPT ,VISIT **/
  amount decimal(10,2),
  line_code varchar(30) ,/**  ORDER_NO,RECEIPT_NO ,VISIT_ID**/
  line_type varchar(30) ,/** ORDER ,RECEIPT ,VISIT **/
  line_amount decimal(10,2),
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

DROP TABLE monitor_error_mapping;

CREATE TABLE monitor_error_mapping
( 
ERROR_ID bigint(20) NOT NULL AUTO_INCREMENT,
ERROR_CODE VARCHAR(300),
ERROR_MSG VARCHAR(300),
PRIMARY KEY (ERROR_ID)
)DEFAULT CHARSET=tis620;


delete from monitor_error_mapping;

insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('UNKNOW','UNKNOW');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('NOUPDATE','ไม่สามารถ Update ข้อมูลได้เนื่องจากหา Key ไม่พบ ');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('MySQLIntegrityConstraintViolationException','ไม่สามารถบันทึกลงฐานข้อมูลนี้ เนื่องจาก Key ที่เกี่ยวข้องไม่มี');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('FTPException','ไม่สามารถติดต่อ FTP Server ได้');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('NullPointerException','มีข้อมูลเป็น NULL ');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('DataTruncationException','ข้อมูลที่นำเข้ามามีขนาดมากกว่า ขนาดของ Field ที่กำหนดไว้');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('MasterException','ข้อมูลพื้นฐานบางส่วนมีปัญหา กรุณาตรวจสอบและ Import  ใหม่อีกครั้ง');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('FindCustomerException','ไม่พบข้อมูล Customer ID ');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('FindProductException','ไม่พบข้อมูล Product ID ');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('FindUOMException','ไม่พบข้อมูล UOM ID ');
insert into monitor_error_mapping(ERROR_CODE,ERROR_MSG)values('FindUserException','ไม่พบข้อมูล User ID ');




