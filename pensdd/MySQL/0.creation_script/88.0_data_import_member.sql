CREATE TABLE `i_customer` (
  `ICUSTOMER_ID` int(11) NOT NULL,
  `CODE` varchar(20) default NULL,
  `NAME` varchar(100) default NULL,
  `NAME2` varchar(100) default NULL,
  `CUSTOMER_TYPE` varchar(5) default 'DD',
  `PARTY_TYPE` char(1) default 'O',
  `TERRITORY` varchar(5) default NULL,
  `PERSON_ID_NO` varchar(20) default NULL,
  `EMAIL` varchar(100) default NULL,
  `BIRTHDAY` date default NULL,
  `OCCUPATION` varchar(100) default NULL,
  `MONHTLY_INCOME` decimal(10,2) default NULL,
  `CHOLESTEROL` decimal(10,0) default NULL,
  `MEMBER_LEVEL` varchar(5) default NULL,
  `MEMBER_TYPE` varchar(5) default NULL,
  
  `REGISTER_DATE` date default NULL,
  `EXPIRED_DATE` date default NULL,
  `AGE_MONTH` int(11) NOT NULL default 0,

  `CREDIT_CHECK` char(1) default 'N',
  `CREDIT_LIMIT` decimal(10,2) default NULL,
  `PAYMENT_TERM` char(5) default NULL,
  `PAYMENT_METHOD` varchar(5) default NULL,
  `VAT_CODE` char(5) default NULL,
  `SHIPPING_DATE` varchar(5) default NULL,
  `SHIPPING_TIME` varchar(5) default NULL,
  `DELIVERY_GROUP` varchar(5) default NULL,
  `ROUND_TRIP` varchar(5) default NULL,
  `ORDER_AMOUNT_PERIOD` varchar(5) default NULL,
  
  `ISVIP` char(1) default 'N',
  
  `USER_ID` int(11) default NULL,
  `INTERFACES` char(1) NOT NULL default 'N',
  `EXPORTED` char(1) default 'N',
  
  `ISACTIVE` char(1) NOT NULL default 'Y',
  `CREATED` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `CREATED_BY` int(11) default NULL,
  `UPDATED` timestamp NULL default NULL,
  `UPDATED_BY` int(11) default NULL,
  
  PRIMARY KEY  (`ICUSTOMER_ID`),
  UNIQUE KEY `XPKI_CUSTOMER` (`ICUSTOMER_ID`),
  KEY `XIF2I_CUSTOMER` (`USER_ID`),
  CONSTRAINT `i_customer_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `ad_user` (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

CREATE TABLE `i_address` (
  `IADDRESS_ID` int(11) NOT NULL,
  `LINE1` varchar(100) NOT NULL,
  `LINE2` varchar(100) NOT NULL,
  `LINE3` varchar(100) NOT NULL,
  `LINE4` varchar(100) NOT NULL,
  `DISTRICT_ID` int(11) default NULL,
  `PROVINCE_ID` int(11) default NULL,
  `PROVINCE_NAME` varchar(50) default NULL,
  `POSTAL_CODE` varchar(5) NOT NULL,
  `COUNTRY` varchar(20) default NULL,
  `PURPOSE` char(2) default NULL,
  `ICUSTOMER_ID` int(11) default NULL,
  `ISACTIVE` char(1) NOT NULL default 'Y',
  `CREATED` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `CREATED_BY` int(11) default NULL,
  `UPDATED` timestamp NULL default NULL,
  `UPDATED_BY` int(11) default NULL,
  PRIMARY KEY  (`IADDRESS_ID`),
  UNIQUE KEY `XPKI_ADDRESS` (`IADDRESS_ID`),
  KEY `XIF1I_ADDRESS` (`ICUSTOMER_ID`),
  KEY `XIF2I_ADDRESS` (`PROVINCE_ID`),
  KEY `XIF3I_ADDRESS` (`DISTRICT_ID`),
  CONSTRAINT `i_address_ibfk_1` FOREIGN KEY (`ICUSTOMER_ID`) REFERENCES `i_customer` (`ICUSTOMER_ID`),
  CONSTRAINT `i_address_ibfk_2` FOREIGN KEY (`PROVINCE_ID`) REFERENCES `m_province` (`PROVINCE_ID`),
  CONSTRAINT `i_address_ibfk_3` FOREIGN KEY (`DISTRICT_ID`) REFERENCES `m_district` (`DISTRICT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

CREATE TABLE `i_contact` (
  `ICONTACT_ID` int(11) NOT NULL,
  `ICUSTOMER_ID` int(11) default NULL,
  `CONTACT_TO` varchar(100) default NULL,
  `RELATION` varchar(100) default NULL,
  `PHONE` varchar(100) default NULL,
  `PHONE2` varchar(100) default NULL,
  `MOBILE` varchar(100) default NULL,
  `MOBILE2` varchar(100) default NULL,
  `FAX` varchar(100) default NULL,
  `ISACTIVE` char(1) NOT NULL default 'Y',
  `CREATED` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `CREATED_BY` int(11) default NULL,
  `UPDATED` timestamp NULL default NULL,
  `UPDATED_BY` int(11) default NULL,
  PRIMARY KEY  (`ICONTACT_ID`),
  UNIQUE KEY `XPKI_CONTACT` (`ICONTACT_ID`),
  KEY `XIF1I_CONTACT` (`ICUSTOMER_ID`),
  CONSTRAINT `i_contact_ibfk_1` FOREIGN KEY (`ICUSTOMER_ID`) REFERENCES `i_customer` (`ICUSTOMER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

CREATE TABLE `i_member_product` (
  `IMEMBER_PRODUCT_ID` int(18) NOT NULL,
  `ICUSTOMER_ID` int(11) default NULL,
  `PRODUCT_ID` int(11) default NULL,
  `ORDER_QTY` int(11) default NULL,
  `UOM_ID` varchar(20) default NULL,
  `CREATED` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `CREATED_BY` int(11) default NULL,
  `UPDATED` timestamp  NULL default NULL,
  `UPDATED_BY` int(11) default NULL,
  PRIMARY KEY  (`IMEMBER_PRODUCT_ID`),
  UNIQUE KEY `XPKI_MEMBER_PRODUCT` (`IMEMBER_PRODUCT_ID`),
  KEY `XIF1I_MEMBER_PRODUCT` (`PRODUCT_ID`),
  KEY `XIF2I_MEMBER_PRODUCT` (`UOM_ID`),
  KEY `XIF3I_MEMBER_PRODUCT` (`ICUSTOMER_ID`),
  CONSTRAINT `i_member_product_ibfk_1` FOREIGN KEY (`PRODUCT_ID`) REFERENCES `m_product` (`PRODUCT_ID`),
  CONSTRAINT `i_member_product_ibfk_2` FOREIGN KEY (`UOM_ID`) REFERENCES `m_uom` (`UOM_ID`),
  CONSTRAINT `i_member_product_ibfk_3` FOREIGN KEY (`ICUSTOMER_ID`) REFERENCES `i_customer` (`ICUSTOMER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

alter table i_customer  add column IMPORTED char(1) not null default 'N';
alter table i_customer  add column IMPORTED_DETAIL varchar(500) default null;

alter table i_address  add column IMPORTED char(1) not null default 'N';
alter table i_address  add column IMPORTED_DETAIL varchar(500) default null;

alter table i_contact  add column IMPORTED char(1) not null default 'N';
alter table i_contact  add column IMPORTED_DETAIL varchar(500) default null;

alter table i_member_product  add column IMPORTED char(1) not null default 'N';
alter table i_member_product add column IMPORTED_DETAIL varchar(500) default null;