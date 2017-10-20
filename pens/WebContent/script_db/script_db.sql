
/** 10/2560 **/
alter table c_reference modify value varchar(20);
 
insert into pens.c_reference values ('2800','CreditDateFix','CreditDateFix','CreditDateFix','01/01/2560','Y');
insert into pens.c_reference values ('2801','VanDateFix','VanDateFix','VanDateFix Case PD_PAID NO','01/10/2560','Y');

CREATE TABLE pens.t_receipt_pdpaid_no (
	ORDER_NO varchar(20) NOT NULL,
	CUSTOMER_ID int(10),
	ORDER_DATE date,
	RECEIPT_AMOUNT decimal(10,2),
	PDPAID_DATE date,
	PD_PAYMENTMETHOD varchar(4),
	CREATED timestamp NOT NULL,
	CREATED_BY int(10),
	PRIMARY KEY (ORDER_NO)
);
CREATE TABLE pens.c_control_code (
	class_name varchar(50) NOT NULL,
	method_name varchar(50) NOT NULL,
	ISACTIVE char(1) ,
	PRIMARY KEY (class_name,method_name)
);
insert into pens.c_control_code values ('OrderUtils','canSaveCreditVan','Y');
insert into pens.c_control_code values ('RunUpdateSalesAppAutoServlet','startDeploySalesAppAuto','N');
