
DROP INDEX XIF1M_DISTRICT ON M_DISTRICT
;



DROP INDEX XPKM_DISTRICT ON M_DISTRICT
;



DROP INDEX XPKM_PROVINCE ON M_PROVINCE
;



DROP INDEX XIF1C_DOCTYPE_SEQUENCE ON C_DOCTYPE_SEQUENCE
;



DROP INDEX XPKC_DOCTYPE_SEQUENCE ON C_DOCTYPE_SEQUENCE
;



DROP INDEX XPKC_DOCTYPE ON C_DOCTYPE
;



DROP INDEX XIF1M_PRODUCT_PRICE ON M_PRODUCT_PRICE
;



DROP INDEX XIF2M_PRODUCT_PRICE ON M_PRODUCT_PRICE
;



DROP INDEX XPKM_PRODUCT_PRICE ON M_PRODUCT_PRICE
;



DROP INDEX XIF1M_PRODUCT ON M_PRODUCT
;



DROP INDEX XIF2M_PRODUCT ON M_PRODUCT
;



DROP INDEX XPKM_PRODUCT ON M_PRODUCT
;



DROP INDEX XPKM_PRODUCT_CATEGORY ON M_PRODUCT_CATEGORY
;



DROP INDEX XPKM_UOM ON M_UOM
;



DROP INDEX XPKM_PRICELIST ON M_PRICELIST
;



DROP INDEX XPKC_REFERENCE ON C_REFERENCE
;




DROP TABLE M_DISTRICT
;




DROP TABLE M_PROVINCE
;




DROP TABLE C_DOCTYPE_SEQUENCE
;




DROP TABLE C_DOCTYPE
;




DROP TABLE M_PRODUCT_PRICE
;




DROP TABLE M_PRODUCT
;




DROP TABLE M_PRODUCT_CATEGORY
;




DROP TABLE M_UOM
;




DROP TABLE M_PRICELIST
;




DROP TABLE C_REFERENCE
;



CREATE TABLE C_DOCTYPE
(
	DOCTYPE_ID            INTEGER NOT NULL,
	NAME                  VARCHAR(50) NOT NULL,
	DESCRIPTION           VARCHAR(255) NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y'
)
;



CREATE UNIQUE INDEX XPKC_DOCTYPE ON C_DOCTYPE
(
	DOCTYPE_ID
)
;



ALTER TABLE C_DOCTYPE
	ADD  PRIMARY KEY (DOCTYPE_ID)
;



CREATE TABLE C_DOCTYPE_SEQUENCE
(
	ORDER_TYPE            CHAR(2) NOT NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y',
	DOCTYPE_SEQUENCE_ID   INTEGER NOT NULL,
	DOCTYPE_ID            INTEGER NOT NULL,
	START_NO              INTEGER NULL,
	CURRENT_NEXT          INTEGER NULL,
	CURRENT_YEAR          INTEGER NULL,
	CURRENT_MONTH         INTEGER NULL,
	SALES_CODE            VARCHAR(20) NULL
)
;



CREATE UNIQUE INDEX XPKC_DOCTYPE_SEQUENCE ON C_DOCTYPE_SEQUENCE
(
	DOCTYPE_SEQUENCE_ID
)
;



ALTER TABLE C_DOCTYPE_SEQUENCE
	ADD  PRIMARY KEY (DOCTYPE_SEQUENCE_ID)
;



CREATE INDEX XIF1C_DOCTYPE_SEQUENCE ON C_DOCTYPE_SEQUENCE
(
	DOCTYPE_ID
)
;



CREATE TABLE C_REFERENCE
(
	REFERENCE_ID          INTEGER NOT NULL,
	CODE                  VARCHAR(5) NOT NULL,
	NAME                  VARCHAR(50) NOT NULL,
	DESCRIPTION           VARCHAR(255) NULL,
	VALUE                  VARCHAR(5) NOT NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y'
)
;

ALTER TABLE `c_reference` CHANGE `CODE` CODE VARCHAR(20) NOT NULL;

CREATE UNIQUE INDEX XPKC_REFERENCE ON C_REFERENCE
(
	REFERENCE_ID
)
;



ALTER TABLE C_REFERENCE
	ADD  PRIMARY KEY (REFERENCE_ID)
;



CREATE TABLE M_DISTRICT
(
	DISTRICT_ID           INTEGER NOT NULL,
	NAME                  VARCHAR(20) NOT NULL,
	PROVINCE_ID           INTEGER NULL
)
;



CREATE UNIQUE INDEX XPKM_DISTRICT ON M_DISTRICT
(
	DISTRICT_ID
)
;



ALTER TABLE M_DISTRICT
	ADD  PRIMARY KEY (DISTRICT_ID)
;



CREATE INDEX XIF1M_DISTRICT ON M_DISTRICT
(
	PROVINCE_ID
)
;



CREATE TABLE M_PRICELIST
(
	PRICELIST_ID          INTEGER NULL,
	NAME                  VARCHAR(50) NOT NULL,
	DESCRIPTION           VARCHAR(255) NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y',
	PRICE_LIST_TYPE       CHAR(5) NULL,
	EFFECTIVE_DATE        DATE NULL,
	EFFECTIVETO_DATE      DATE NULL
)
;



CREATE UNIQUE INDEX XPKM_PRICELIST ON M_PRICELIST
(
	PRICELIST_ID
)
;



ALTER TABLE M_PRICELIST
	ADD  PRIMARY KEY (PRICELIST_ID)
;



CREATE TABLE M_PRODUCT
(
	PRODUCT_ID            INTEGER NULL,
	CODE                  VARCHAR(20) NOT NULL,
	NAME                  VARCHAR(50) NOT NULL,
	DESCRIPTION           VARCHAR(255) NULL,
	UOM_ID                VARCHAR(20) NOT NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y',
	PRODUCT_CATEGORY_ID   INTEGER NULL
)
;



CREATE UNIQUE INDEX XPKM_PRODUCT ON M_PRODUCT
(
	PRODUCT_ID
)
;



ALTER TABLE M_PRODUCT
	ADD  PRIMARY KEY (PRODUCT_ID)
;



CREATE INDEX XIF1M_PRODUCT ON M_PRODUCT
(
	UOM_ID
)
;



CREATE INDEX XIF2M_PRODUCT ON M_PRODUCT
(
	PRODUCT_CATEGORY_ID
)
;



CREATE TABLE M_PRODUCT_CATEGORY
(
	PRODUCT_CATEGORY_ID   INTEGER NOT NULL,
	NAME                  VARCHAR(50) NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y'
)
;



CREATE UNIQUE INDEX XPKM_PRODUCT_CATEGORY ON M_PRODUCT_CATEGORY
(
	PRODUCT_CATEGORY_ID
)
;



ALTER TABLE M_PRODUCT_CATEGORY
	ADD  PRIMARY KEY (PRODUCT_CATEGORY_ID)
;



CREATE TABLE M_PRODUCT_PRICE
(
	PRICELIST_ID          INTEGER NULL,
	PRODUCT_ID            INTEGER NULL,
	PRODUCT_PRICE_ID      INTEGER NOT NULL,
	PRICE                 DECIMAL(10,2) NOT NULL DEFAULT 0,
	EFFECTIVE_DATE        DATE NULL,
	EFFECTIVETO_DATE      DATE NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y',
	UOM_ID                VARCHAR(20) NULL
)
;



CREATE UNIQUE INDEX XPKM_PRODUCT_PRICE ON M_PRODUCT_PRICE
(
	PRODUCT_PRICE_ID
)
;



ALTER TABLE M_PRODUCT_PRICE
	ADD  PRIMARY KEY (PRODUCT_PRICE_ID)
;



CREATE INDEX XIF1M_PRODUCT_PRICE ON M_PRODUCT_PRICE
(
	PRICELIST_ID
)
;



CREATE INDEX XIF2M_PRODUCT_PRICE ON M_PRODUCT_PRICE
(
	PRODUCT_ID
)
;



CREATE TABLE M_PROVINCE
(
	PROVINCE_ID           INTEGER NOT NULL,
	NAME                  VARCHAR(20) NOT NULL
)
;



CREATE UNIQUE INDEX XPKM_PROVINCE ON M_PROVINCE
(
	PROVINCE_ID
)
;



ALTER TABLE M_PROVINCE
	ADD  PRIMARY KEY (PROVINCE_ID)
;



CREATE TABLE M_UOM
(
	UOM_ID                VARCHAR(20) NOT NULL,
	CODE                  VARCHAR(20) NULL,
	NAME                  VARCHAR(50) NOT NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y'
)
;



CREATE UNIQUE INDEX XPKM_UOM ON M_UOM
(
	UOM_ID
)
;



ALTER TABLE M_UOM
	ADD  PRIMARY KEY (UOM_ID)
;



ALTER TABLE C_DOCTYPE_SEQUENCE
	ADD FOREIGN KEY R_25 (DOCTYPE_ID) REFERENCES C_DOCTYPE(DOCTYPE_ID)
;



ALTER TABLE M_DISTRICT
	ADD FOREIGN KEY R_8 (PROVINCE_ID) REFERENCES M_PROVINCE(PROVINCE_ID)
;



ALTER TABLE M_PRODUCT
	ADD FOREIGN KEY R_1 (UOM_ID) REFERENCES M_UOM(UOM_ID)
;


ALTER TABLE M_PRODUCT
	ADD FOREIGN KEY R_2 (PRODUCT_CATEGORY_ID) REFERENCES M_PRODUCT_CATEGORY(PRODUCT_CATEGORY_ID)
;



ALTER TABLE M_PRODUCT_PRICE
	ADD FOREIGN KEY R_4 (PRICELIST_ID) REFERENCES M_PRICELIST(PRICELIST_ID)
;


ALTER TABLE M_PRODUCT_PRICE
	ADD FOREIGN KEY R_24 (PRODUCT_ID) REFERENCES M_PRODUCT(PRODUCT_ID)
;


CREATE TABLE C_SYSCONFIG
(
	SYSCONFIG_ID          INTEGER NULL,
	NAME                  VARCHAR(30) NULL,
	VALUE                 VARCHAR(5) NULL,
	UPDATED               TIMESTAMP NULL,
	UPDATED_BY            INTEGER NULL
)
;



ALTER TABLE C_SYSCONFIG
	ADD  PRIMARY KEY (SYSCONFIG_ID)
;

DROP TABLE c_sequence;

CREATE TABLE `c_sequence` (
  `Sequence_ID` int(11) NOT NULL,
  `Name` varchar(20) NOT NULL,
  `Active` char(1) NOT NULL default 'Y',
  `StartNo` decimal(10,0) NOT NULL default '0',
  `NextValue` decimal(10,0) NOT NULL default '0',
  PRIMARY KEY  (`Sequence_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=tis620;

ALTER TABLE C_DOCTYPE_SEQUENCE ADD COLUMN UPDATED  TIMESTAMP
;

ALTER TABLE C_DOCTYPE_SEQUENCE ADD COLUMN UPDATED_BY  INTEGER
;

ALTER TABLE `c_sequence` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `Sequence_ID` Sequence_ID INT(11) AUTO_INCREMENT NOT NULL,
  CHANGE `Name` Name VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `Active` Active CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';


ALTER TABLE `c_doctype` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `NAME` NAME VARCHAR(50) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `DESCRIPTION` DESCRIPTION VARCHAR(255) CHARACTER SET tis620 COLLATE tis620_thai_ci;

ALTER TABLE `c_doctype_sequence` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `SALES_CODE` SALES_CODE VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci;
ALTER TABLE `c_doctype_sequence` DROP FOREIGN KEY c_doctype_sequence_ibfk_1;
ALTER TABLE `c_doctype_sequence` ADD CONSTRAINT c_doctype_sequence_ibfk_1 FOREIGN KEY (DOCTYPE_ID) REFERENCES c_doctype (DOCTYPE_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE `m_district` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `NAME` NAME VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL;
ALTER TABLE `m_district` DROP FOREIGN KEY m_district_ibfk_1;
ALTER TABLE `m_district` ADD CONSTRAINT m_district_ibfk_1 FOREIGN KEY (PROVINCE_ID) REFERENCES m_province (PROVINCE_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE `m_pricelist` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `NAME` NAME VARCHAR(50) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `DESCRIPTION` DESCRIPTION VARCHAR(255) CHARACTER SET tis620 COLLATE tis620_thai_ci;

ALTER TABLE `m_product` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `CODE` CODE VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `NAME` NAME VARCHAR(50) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `DESCRIPTION` DESCRIPTION VARCHAR(255) CHARACTER SET tis620 COLLATE tis620_thai_ci;
ALTER TABLE `m_product` DROP FOREIGN KEY m_product_ibfk_2,
  DROP FOREIGN KEY m_product_ibfk_1;
ALTER TABLE `m_product` ADD CONSTRAINT m_product_ibfk_2 FOREIGN KEY (PRODUCT_CATEGORY_ID) REFERENCES m_product_category (PRODUCT_CATEGORY_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_product_ibfk_1 FOREIGN KEY (UOM_ID) REFERENCES m_uom (UOM_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE `m_product_category` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `NAME` NAME VARCHAR(50) CHARACTER SET tis620 COLLATE tis620_thai_ci;

ALTER TABLE `m_product_price` CHARACTER SET tis620 COLLATE tis620_thai_ci;
ALTER TABLE `m_product_price` DROP FOREIGN KEY m_product_price_ibfk_2,
  DROP FOREIGN KEY m_product_price_ibfk_1;
ALTER TABLE `m_product_price` ADD CONSTRAINT m_product_price_ibfk_2 FOREIGN KEY (PRODUCT_ID) REFERENCES m_product (PRODUCT_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_product_price_ibfk_1 FOREIGN KEY (PRICELIST_ID) REFERENCES m_pricelist (PRICELIST_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE `m_province` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `NAME` NAME VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL;
	
ALTER TABLE `m_uom` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `CODE` CODE VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `NAME` NAME VARCHAR(50) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL;

ALTER TABLE `c_reference` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `CODE` CODE VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `NAME` NAME VARCHAR(50) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `DESCRIPTION` DESCRIPTION VARCHAR(255) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `VALUE` VALUE VARCHAR(5) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL;
ALTER TABLE `c_sysconfig` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `NAME` NAME VARCHAR(30) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `VALUE` VALUE VARCHAR(5) CHARACTER SET tis620 COLLATE tis620_thai_ci;
ALTER TABLE `c_doctype` CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `c_doctype_sequence` CHANGE `ORDER_TYPE` ORDER_TYPE CHAR(5) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `c_doctype_sequence` DROP FOREIGN KEY c_doctype_sequence_ibfk_1;
ALTER TABLE `c_doctype_sequence` ADD CONSTRAINT c_doctype_sequence_ibfk_1 FOREIGN KEY (DOCTYPE_ID) REFERENCES c_doctype (DOCTYPE_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE `c_reference` CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `m_pricelist` CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y',
  CHANGE `PRICE_LIST_TYPE` PRICE_LIST_TYPE CHAR(5) CHARACTER SET tis620 COLLATE tis620_thai_ci;
ALTER TABLE `m_product` CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `m_product` DROP FOREIGN KEY m_product_ibfk_1,
  DROP FOREIGN KEY m_product_ibfk_2;
ALTER TABLE `m_product` ADD CONSTRAINT m_product_ibfk_1 FOREIGN KEY (UOM_ID) REFERENCES m_uom (UOM_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_product_ibfk_2 FOREIGN KEY (PRODUCT_CATEGORY_ID) REFERENCES m_product_category (PRODUCT_CATEGORY_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE `m_product_category` CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `m_product_price` CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `m_product_price` DROP FOREIGN KEY m_product_price_ibfk_1,
  DROP FOREIGN KEY m_product_price_ibfk_2;
ALTER TABLE `m_product_price` ADD CONSTRAINT m_product_price_ibfk_1 FOREIGN KEY (PRICELIST_ID) REFERENCES m_pricelist (PRICELIST_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_product_price_ibfk_2 FOREIGN KEY (PRODUCT_ID) REFERENCES m_product (PRODUCT_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE `m_uom` CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `m_province` CHANGE `NAME` NAME VARCHAR(50) NOT NULL;
ALTER TABLE `m_district` CHANGE `NAME` NAME VARCHAR(50) NOT NULL;







DROP TABLE C_MESSAGE
;



CREATE TABLE C_MESSAGE
(
	MESSAGE_CODE          VARCHAR(5) NOT NULL,
	DESCRIPTION_TH        VARCHAR(255) NULL,
	DESCRIPTION_EN        VARCHAR(255) NULL
)
;



CREATE UNIQUE INDEX XPKC_MESSAGE ON C_MESSAGE
(
	MESSAGE_CODE
)
;



ALTER TABLE C_MESSAGE
	ADD  PRIMARY KEY (MESSAGE_CODE)
;


ALTER TABLE `c_message` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `MESSAGE_CODE` MESSAGE_CODE VARCHAR(5) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `DESCRIPTION_TH` DESCRIPTION_TH VARCHAR(255) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `DESCRIPTION_EN` DESCRIPTION_EN VARCHAR(255) CHARACTER SET tis620 COLLATE tis620_thai_ci;


  
CREATE TABLE M_MAP_PROVINCE
(
	REFERENCE_ID          INTEGER NOT NULL,
	PROVINCE_ID           INTEGER NOT NULL
)
;



ALTER TABLE M_MAP_PROVINCE
	ADD  PRIMARY KEY (REFERENCE_ID,PROVINCE_ID)
;



ALTER TABLE M_MAP_PROVINCE
	ADD FOREIGN KEY R_70 (REFERENCE_ID) REFERENCES C_REFERENCE(REFERENCE_ID)
;


ALTER TABLE M_MAP_PROVINCE
	ADD FOREIGN KEY R_71 (PROVINCE_ID) REFERENCES M_PROVINCE(PROVINCE_ID)
;



ALTER TABLE m_product_category ADD COLUMN SEG_ID1  INTEGER DEFAULT NULL;
ALTER TABLE m_product_category ADD COLUMN SEG_VALUE1  VARCHAR(100) DEFAULT NULL;
ALTER TABLE m_product_category ADD COLUMN SEG_ID2  INTEGER DEFAULT NULL;
ALTER TABLE m_product_category ADD COLUMN SEG_VALUE2  VARCHAR(100) DEFAULT NULL;
ALTER TABLE m_product_category ADD COLUMN SEG_ID3  INTEGER DEFAULT NULL;
ALTER TABLE m_product_category ADD COLUMN SEG_VALUE3  VARCHAR(100) DEFAULT NULL;
ALTER TABLE m_product_category ADD COLUMN SEG_ID4  INTEGER DEFAULT NULL;
ALTER TABLE m_product_category ADD COLUMN SEG_VALUE4  VARCHAR(100) DEFAULT NULL;
ALTER TABLE m_product_category ADD COLUMN SEG_ID5  INTEGER DEFAULT NULL;
ALTER TABLE m_product_category ADD COLUMN SEG_VALUE5  VARCHAR(100) DEFAULT NULL;



DROP TABLE C_TRX_HISTORY;

CREATE TABLE C_TRX_HISTORY
(
	TRX_HIST_ID           INTEGER NOT NULL,
	TRX_MODULE            VARCHAR(50) NOT NULL,
	TRX_TYPE              VARCHAR(10) NOT NULL,
	USER_ID               INTEGER NOT NULL,
	TRX_DATE              TIMESTAMP NOT NULL,
	RECORD_ID             INTEGER NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=tis620;




ALTER TABLE C_TRX_HISTORY
	ADD  PRIMARY KEY (TRX_HIST_ID)
;



ALTER TABLE C_TRX_HISTORY
	ADD FOREIGN KEY R_87 (USER_ID) REFERENCES AD_USER(USER_ID)
;


ALTER TABLE `m_product_category` CHANGE `NAME` NAME VARCHAR(200) DEFAULT NULL;


CREATE TABLE `m_delivery_route` (
  `DELIVERY_ROUTE_ID` int(11) NOT NULL auto_increment,
  `DAYS` varchar(5) NOT NULL,
  `DISTRICT_ID` int(11) NOT NULL,
  PRIMARY KEY  (`DELIVERY_ROUTE_ID`),
  UNIQUE KEY `XPKM_DELIVERY_ROUTE` (`DELIVERY_ROUTE_ID`),
  KEY `XIF1M_DELIVERY_ROUTE` (`DISTRICT_ID`),
  CONSTRAINT `m_delivery_route_ibfk_1` FOREIGN KEY (`DISTRICT_ID`) REFERENCES `m_district` (`DISTRICT_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=tis620;



ALTER TABLE m_product CHANGE NAME NAME VARCHAR(200) NOT NULL;


CREATE TABLE M_DELIVERY_GROUP
(
	REFERENCE_ID          INTEGER NOT NULL,
	DISTRICT_ID           INTEGER NOT NULL
)
;



CREATE UNIQUE INDEX XPKM_DELIVERY_GROUP ON M_DELIVERY_GROUP
(
	DISTRICT_ID,
	REFERENCE_ID
)
;



ALTER TABLE M_DELIVERY_GROUP
	ADD  PRIMARY KEY (DISTRICT_ID,REFERENCE_ID)
;



CREATE INDEX XIF1M_DELIVERY_GROUP ON M_DELIVERY_GROUP
(
	REFERENCE_ID
)
;



CREATE INDEX XIF2M_DELIVERY_GROUP ON M_DELIVERY_GROUP
(
	DISTRICT_ID
)
;



ALTER TABLE M_DELIVERY_GROUP
	ADD FOREIGN KEY R_94 (REFERENCE_ID) REFERENCES C_REFERENCE(REFERENCE_ID)
;


ALTER TABLE M_DELIVERY_GROUP
	ADD FOREIGN KEY R_95 (DISTRICT_ID) REFERENCES M_DISTRICT(DISTRICT_ID)
;

