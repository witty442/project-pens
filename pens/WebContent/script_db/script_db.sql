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
/** 07/2560 **/
INSERT INTO `c_reference` (`REFERENCE_ID`,`CODE`,`NAME`,`DESCRIPTION`,`VALUE`,`ISACTIVE`) VALUES (2601,'PartyType','บุคคลทั่วไป','Personal','P','Y');
INSERT INTO `c_reference` (`REFERENCE_ID`,`CODE`,`NAME`,`DESCRIPTION`,`VALUE`,`ISACTIVE`) VALUES (2602,'PartyType','องค์กร/ร้านค้า','Organization','O','Y');
INSERT INTO `c_reference` (`REFERENCE_ID`,`CODE`,`NAME`,`DESCRIPTION`,`VALUE`,`ISACTIVE`) VALUES (2603,'PartyType','7-11','7-11','S','Y');
