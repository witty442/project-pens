alter table t_order
add column(
    print_DateTime_Pick decimal(20,6),
    print_Count_Pick int,
	
    print_DateTime_Rcp decimal(20,6),
    print_Count_Rcp int
);

INSERT INTO pens.c_reference
	(REFERENCE_ID, CODE, NAME, DESCRIPTION, VALUE, ISACTIVE)
VALUES 
	(2401, 'C4_PROM_GOODS', 'C4_PROM_GOODS', 'C4_PROM_GOODS Method(1[old],2[NEW])', '2', 'Y');
	
create table pens.m_product_special(
 group_code varchar(30) not null,
 code varchar(30) not null,
 create_date timestamp,
 PRIMARY KEY (code)
);

CREATE TABLE pens.t_stock (
	request_number varchar(30) NOT NULL,
	CUSTOMER_ID int(10) NOT NULL,
	request_date date NOT NULL,
	description varchar(240),
	status varchar(2),
	exported char(1),
	USER_ID int(10),
	CREATED date,
	CREATED_BY varchar(20),
	UPDATED date,
	UPDATED_BY varchar(20),
	PRIMARY KEY (request_number)
);

CREATE TABLE pens.t_stock_line (
	request_number varchar(30) NOT NULL,
	line_number int(10) NOT NULL,
	inventory_item_id int(10) NOT NULL,
	CREATE_DATE date NOT NULL,
	EXPIRE_DATE date NOT NULL,
	qty decimal(15,5),
	uom varchar(20),
	amount decimal(15,5),
	status varchar(2),
	exported char(1),
	USER_ID int(10),
	
	CREATED date,
	CREATED_BY varchar(20),
	UPDATED date,
	UPDATED_BY varchar(20),
	PRIMARY KEY (request_number,line_number)
);
insert into c_doctype values(222,'Stock','Stock','Y');
