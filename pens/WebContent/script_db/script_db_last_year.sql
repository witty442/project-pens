/** 07-2562 **/
alter table t_pd_receipt_his add CHEQUE_DATE date;
alter table t_order_line
add (
update_flag char(1),
remark varchar(30)
);

/** 08-2562 */
CREATE TABLE pens.c_control_thread (
	thread_name varchar(50) NOT NULL,
	status varchar(10) NOT NULL,
	PRIMARY KEY (thread_name,status)
);
INSERT INTO pens.c_control_code(class_name, method_name, ISACTIVE)
VALUES ('TestURLConnection', 'testURLConnection', 'Y');

/** 09-2562 **/
INSERT INTO pens.c_control_code(class_name, method_name, ISACTIVE)
VALUES ('ControlOrderPage', 'stepIsValid', 'Y');

/** 10-2562 **/
CREATE  TABLE pens.t_stamp_print_order (
   order_no varchar(30) not null,
	report_name varchar(150) NOT NULL,
	date varchar(50) NOT NULL,
	print_count int not null
);
INSERT INTO pens.c_control_code(class_name, method_name, ISACTIVE)
VALUES ('ClearDupDB', 'clearDupCustAddressDB', 'N');
