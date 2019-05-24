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