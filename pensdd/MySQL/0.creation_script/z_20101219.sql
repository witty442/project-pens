--Receipt By Line Off
ALTER TABLE t_receipt_by ADD COLUMN LINE_OFF CHAR(1) NOT NULL DEFAULT 'N' ;

ALTER TABLE t_receipt_cn add COLUMN RECEIPT_AMOUNT decimal(10,2) default 0;
ALTER TABLE t_receipt_cn add COLUMN PAID_AMOUNT decimal(10,2) default 0;
ALTER TABLE t_receipt_cn add COLUMN REMAIN_AMOUNT decimal(10,2) default 0;

--Receipt Match CN
CREATE TABLE `t_receipt_match_cn` (
  `RECEIPT_MATCH_CN_ID` int(11) NOT NULL,
  `RECEIPT_BY_ID` int(11) default NULL,
  `RECEIPT_CN_ID` int(11) default NULL,
  `PAID_AMOUNT` decimal(10,2) default NULL,
  `CREATED` datetime default NULL,
  `CREATED_BY` int(11) default NULL,
  `UPDATED` datetime default NULL,
  `UPDATED_BY` int(11) default NULL,
  `RECEIPT_ID` int(11) default NULL,
  PRIMARY KEY  (`RECEIPT_MATCH_CN_ID`),
  UNIQUE KEY `XPKT_RECEIPT_MATCH_CN` (`RECEIPT_MATCH_CN_ID`),
  KEY `XIF2T_RECEIPT_MATCH_CN` (`RECEIPT_BY_ID`),
  KEY `XIF3T_RECEIPT_MATCH_CN` (`RECEIPT_CN_ID`),
  KEY `XIF4T_RECEIPT_MATCH_CN` (`RECEIPT_ID`),
  CONSTRAINT `t_receipt_match_cn_ibfk_3` FOREIGN KEY (`RECEIPT_ID`) REFERENCES `t_receipt` (`RECEIPT_ID`),
  CONSTRAINT `t_receipt_match_cn_ibfk_1` FOREIGN KEY (`RECEIPT_BY_ID`) REFERENCES `t_receipt_by` (`RECEIPT_BY_ID`),
  CONSTRAINT `t_receipt_match_cn_ibfk_2` FOREIGN KEY (`RECEIPT_CN_ID`) REFERENCES `t_receipt_cn` (`RECEIPT_CN_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;


alter table t_receipt_cn drop column receipt_amount;
alter table t_receipt_cn add column credit_amount decimal(10,2) default 0;
ALTER TABLE t_receipt_by CHANGE CREDIT_CARD_TYPE CREDIT_CARD_TYPE VARCHAR(30);
ALTER TABLE t_receipt_by CHANGE LINE_OFF WRITE_OFF CHAR(1) NOT NULL DEFAULT 'N';
alter table t_receipt_by drop column line_off;
ALTER TABLE t_receipt ADD INTERNAL_BANK VARCHAR(5) NULL DEFAULT NULL;