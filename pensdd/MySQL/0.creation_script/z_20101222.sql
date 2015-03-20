ALTER TABLE m_district ADD CODE varchar(2) DEFAULT NULL;

CREATE TABLE `c_customer_sequence` (
  `CUSTOMER_SEQUENCE_ID` int(11) NOT NULL,
  `TERRITORY` varchar(2) NOT NULL,
  `PROVINCE` varchar(2) NOT NULL,
  `DISTRICT` varchar(2) NOT NULL,
  `CURRENT_NEXT` int(11) default NULL,
  `UPDATED_BY` int(11) default NULL,
  `UPDATED` timestamp NULL default NULL,
  PRIMARY KEY  (`CUSTOMER_SEQUENCE_ID`),
  UNIQUE KEY `XPKC_CUSTOMER_SEQUENCE` (`CUSTOMER_SEQUENCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

Insert into c_reference(REFERENCE_ID, CODE, NAME, VALUE, ISACTIVE) Values
(1600, 'QtyDeliver', '7', '7', 'Y'),
(1700, 'RoundDeliver', '7 วัน', '7', 'Y');

Insert into c_reference(REFERENCE_ID, CODE, NAME, VALUE, ISACTIVE) Values
(2101, 'InternalBank', 'ธ.กรุงเทพ สาขาสาธุประดิษฐ์  195-4-75402-2', '001', 'Y');
