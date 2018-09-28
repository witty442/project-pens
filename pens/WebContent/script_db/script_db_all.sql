
/** 01-2561 **/
alter table t_temp_import_trans
modify seq decimal(15,2);

insert into pens.c_control_code values ('OrderUtils','canPrintBillCreditCaseVan','Y');

create  table t_pd_receipt_his (
    ORDER_NO varchar(20) NOT NULL,
	CUSTOMER_ID int(10),
	ORDER_DATE date,
	RECEIPT_AMOUNT decimal(10,2),
	PDPAID_DATE date,
	PD_PAYMENTMETHOD varchar(4),
	EXPORTED char(1),
	CREATED timestamp NOT NULL,
	CREATED_BY int(10),
	PRIMARY KEY (ORDER_NO)
);
insert into pens.c_control_code values ('RunScriptDBAction','runManualScriptProcessOLD','N');
insert into pens.c_control_code values ('RunScriptDBAction','runManualScriptProcessNEW','Y');
insert into pens.c_control_code values ('OrderProcess','fillLinesShowPromotion','Y');
insert into pens.c_control_code values ('ModifierProcess','promotionalGoodProcessNew','Y');
insert into pens.c_control_code values ('OrderProcess','sumQtyProductPromotionDuplicate','Y');
/** 02-2561 **/
insert into pens.c_control_code values ('PrinterUtils','selectPrinterSmallIsOnlineCheckOnline','Y');