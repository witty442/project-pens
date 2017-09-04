/**012560 */
CREATE TABLE c_temp_location(
	lat varchar(40),
	lng varchar(40),
	create_date timestamp,
	error varchar(200)
);
/** 06/2560 **/
alter table t_stock_line modify create_date date;
alter table t_order add po_number varchar(20);
/** 06/2560/Credit **/
alter table t_stock_line
add (  qty2 decimal(15,5),
       qty3 decimal(15,5),
       sub  decimal(15,5),
       sub2 decimal(15,5),
       sub3 decimal(15,5),
       EXPIRE_DATE2 date,
       EXPIRE_DATE3 date,
       uom2 varchar(20)
);


/** 08/2560 **/
alter table t_stock 
add(back_avg_month varchar(5));

alter table t_stock_line
add(avg_order_qty int);

alter table t_stock_line
modify expire_date date ;

alter table m_customer
add (lat varchar(100),
	lng varchar(100)
	);
	
alter table m_customer
add (trip_day varchar(5),
	trip_day2 varchar(5),
	trip_day3 varchar(5)
	);
	
delete from pens.c_reference where reference_id in (2601,2602,2603);
insert into pens.c_reference values ('2601','PartyType','ร้านโชห่วย','ร้านโชห่วย' ,'P','Y');
insert into pens.c_reference values ('2602','PartyType','ร้านขายยา','ร้านขายยา','D','Y' );
insert into pens.c_reference values ('2603','PartyType','ร้านเพ็ทช็อป','ร้านเพ็ทช็อป','A','Y');
insert into pens.c_reference values ('2604','PartyType','7/11','7/11','S','Y');

insert into pens.c_reference values ('2700','CustShowTrip','ShowOnlyTrip','ShowOnlyTrip','Y','Y');
