/** 04-2562 **/
alter table t_order_line add MODIFIER_LINE_ID int;

CREATE TABLE pens_shop.c_config (
	user_id int  NOT NULL,
	customer_code varchar(20) not null,
	pricelist_id int not null,
	PRIMARY KEY (user_id)
);

/** 05-2562 **/
alter table c_config add qualifier varchar(60);

/** 06-2563 **/
CREATE TABLE pens_shop.m_product_non_bme (
  product_code varchar(20) NOT NULL,
  PRIMARY KEY (product_code)
);
insert into m_product_non_bme values('883001');
insert into m_product_non_bme values('883002');
insert into m_product_non_bme values('883003');
insert into m_product_non_bme values('771185');
insert into m_product_non_bme values('481623');
insert into m_product_non_bme values('709484');
alter table t_order_line add price_af_discount decimal(15,5);
