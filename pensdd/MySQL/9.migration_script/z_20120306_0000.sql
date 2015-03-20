-- ADD COLUMN FOR SHIPMENT COMMENT
alter table t_order_line add shipment_comment varchar(255);

alter table t_order_line modify column cf_ship_date timestamp null ;
alter table t_order_line modify column cf_receipt_date timestamp null ;

update t_order_line
set cf_ship_date=null
where cf_ship_date = '0000-00-00 00:00:00';

update t_order_line
set cf_receipt_date=null
where cf_receipt_date = '0000-00-00 00:00:00';
