ALTER TABLE m_pricelist
ADD COLUMN `REGISTER_FROM_DATE` DATE AFTER `effectiveto_date`,
ADD COLUMN `REGISTER_TO_DATE` DATE AFTER `register_from_date`;



ALTER TABLE m_customer
ADD COLUMN `FIRST_DELIVERLY_DATE` DATE AFTER `register_date`;

update m_customer set first_deliverly_date = register_date;

insert into m_pricelist(pricelist_id,name,description,isactive,price_list_type,effective_date,effectiveto_date,register_from_date,register_to_date)
values(46107,'DD-Price List','Price List for Direct Delivery','Y','DD','2011-01-01',null,'2011-11-01',null);

update m_pricelist set register_from_date = '2008-01-01',register_to_date = '2011-10-31' where pricelist_id = 46106;

insert into m_product_price(pricelist_id,product_id,product_price_id,price,effective_date,effectiveto_date,isactive,uom_id)
values(46107,'19','37850','19.73001','2011-01-01',null,'Y','BOT');

insert into m_product_price(pricelist_id,product_id,product_price_id,price,effective_date,effectiveto_date,isactive,uom_id)
values(46107,'20','37851','19.73001','2011-01-01',null,'Y','BOT');

insert into m_product_price(pricelist_id,product_id,product_price_id,price,effective_date,effectiveto_date,isactive,uom_id)
values(46107,'21','37852','19.73001','2011-01-01',null,'Y','BOT');