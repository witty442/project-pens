
alter table m_customer
add column(
   airpay_flag char(1)
);

insert c_reference 
(REFERENCE_ID, CODE, NAME, DESCRIPTION, VALUE, ISACTIVE)
values
(1105,'PaymentMethod','ชำระผ่านแอร์เพย์','','AP','Y');

INSERT INTO pens.c_reference
	(REFERENCE_ID, CODE, NAME, DESCRIPTION, VALUE, ISACTIVE)
VALUES 
	(2402, 'CALC_C4', 'CALC_C4', 'CALC_C4 Method(1[old],2[NEW])', '2', 'Y');
	
INSERT INTO pens.c_reference
	(REFERENCE_ID, CODE, NAME, DESCRIPTION, VALUE, ISACTIVE)
VALUES 
	(2403, 'CALC_TARGET', 'CALC_TARGET', 'CALC_TARGET Method(1[old],2[NEW])', '2', 'Y');
	
alter table m_customer
add column(
   location varchar(200)
   ,image_file_name varchar(100)
);

alter table m_customer
add column(
   is_change char(1)
);

