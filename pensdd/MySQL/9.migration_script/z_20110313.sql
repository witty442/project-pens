alter table m_contact add column phone_sub1 varchar(10) default null;
alter table m_contact add column phone_sub2 varchar(10) default null;

/** add Fixed issue Filter cridt_note by cust **/
alter table t_credit_note add column `CUSTOMER_ID` int(11) DEFAULT NULL;