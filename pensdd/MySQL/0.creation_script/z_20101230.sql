CREATE TABLE `c_lockbox` (
  `LOCKBOX_ID` int(11) NOT NULL,
  `LOCKBOX_NAME` varchar(20) default NULL,
  `REFERENCE_ID` int(11) default NULL,
  `BANK_ORIGINATION_NUMBER` int(11) default NULL,
  PRIMARY KEY  (`LOCKBOX_ID`),
  UNIQUE KEY `XPKC_LOCKBOX` (`LOCKBOX_ID`),
  KEY `XIF1C_LOCKBOX` (`REFERENCE_ID`),
  CONSTRAINT `c_lockbox_ibfk_1` FOREIGN KEY (`REFERENCE_ID`) REFERENCES `c_reference` (`REFERENCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

INSERT INTO `c_lockbox` (`LOCKBOX_ID`,`LOCKBOX_NAME`,`REFERENCE_ID`,`BANK_ORIGINATION_NUMBER`) VALUES 
 (1,'LCK01',2101,123),
 (2,'LCK02',2102,123);