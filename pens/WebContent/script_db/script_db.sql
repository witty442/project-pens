
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