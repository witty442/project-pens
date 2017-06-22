/**012560 */
CREATE TABLE c_temp_location(
	lat varchar(40),
	lng varchar(40),
	create_date timestamp,
	error varchar(200)
)
/** 06/2560 **/
alter table t_stock_line
modify create_date date;

alter table t_order 
add po_number varchar(20);