/** 07-2562 **/
alter table t_order_line
add (
update_flag char(1),
remark varchar(30)
);