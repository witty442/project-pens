ALTER TABLE m_qualifier 
  CHANGE VALUE_FROM VALUE_FROM VARCHAR(50), 
  CHANGE VALUE_TO VALUE_TO VARCHAR(50);
ALTER TABLE m_address
 ADD REFERENCE_ID INT(11) DEFAULT 0;
ALTER TABLE m_contact
 ADD REFERENCE_ID INT(11) DEFAULT 0;
 
 drop table m_trip;
CREATE TABLE `m_trip` (
  `TRIP_ID` int(11) NOT NULL default '0',
  `YEAR` char(4) default NULL,
  `MONTH` char(2) default NULL,
  `DAY` char(2) default NULL,
  `CUSTOMER_ID` int(11) default NULL,
  `USER_ID` int(11) default NULL,
  `CREATED` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `CREATED_BY` int(11) default NULL,
  `UPDATED` timestamp NULL default NULL,
  `UPDATED_BY` int(11) default NULL,
  `LINE_NO` int(11) default NULL,
  PRIMARY KEY  (`TRIP_ID`),
  UNIQUE KEY `XPKT_TRIP` (`TRIP_ID`),
  KEY `XIF1T_TRIP` (`CUSTOMER_ID`),
  KEY `XIF2T_TRIP` (`USER_ID`),
  CONSTRAINT `m_trip_ibfk_1` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `m_customer` (`CUSTOMER_ID`),
  CONSTRAINT `m_trip_ibfk_2` FOREIGN KEY (`USER_ID`) REFERENCES `ad_user` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

alter table t_order_line add column PROMOTION_FROM varchar(200) DEFAULT null;
Insert into c_reference(REFERENCE_ID, CODE, NAME, VALUE, ISACTIVE) Values
(1, 'OrgID', 'OrgID', '81', 'Y');