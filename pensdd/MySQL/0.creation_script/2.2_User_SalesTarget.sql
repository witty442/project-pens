
DROP INDEX XIF2M_SALES_TARGET_PRODUCT ON M_SALES_TARGET_PRODUCT
;



DROP INDEX XIF3M_SALES_TARGET_PRODUCT ON M_SALES_TARGET_PRODUCT
;



DROP INDEX XPKM_SALES_TARGET_PRODUCT ON M_SALES_TARGET_PRODUCT
;



DROP INDEX XIF1M_SALES_TARGET ON M_SALES_TARGET
;



DROP INDEX XPKM_SALES_TARGET ON M_SALES_TARGET
;



DROP INDEX XPKAD_USER ON AD_USER
;




DROP TABLE M_SALES_TARGET_PRODUCT
;




DROP TABLE M_SALES_TARGET
;




DROP TABLE AD_USER
;



CREATE TABLE AD_USER
(
	USER_ID               INTEGER NOT NULL,
	CATEGORY              VARCHAR(50) NULL,
	ORGANIZATION          VARCHAR(50) NULL,
	START_DATE            DATE NULL,
	END_DATE              DATE NULL,
	NAME                  VARCHAR(100) NOT NULL,
	SOURCE_NAME           VARCHAR(100) NULL,
	ID_CARD_NO            VARCHAR(20) NULL,
	USER_NAME             VARCHAR(20) NOT NULL,
	PASSWORD              VARCHAR(30) NOT NULL,
	ROLE                  CHAR(5) NOT NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y',
	CODE                  VARCHAR(20) NULL,
	UPDATED               TIMESTAMP NULL,
	UPDATED_BY            INTEGER NULL
)
;



CREATE UNIQUE INDEX XPKAD_USER ON AD_USER
(
	USER_ID
)
;



ALTER TABLE AD_USER
	ADD  PRIMARY KEY (USER_ID)
;



CREATE TABLE M_SALES_TARGET
(
	SALES_TARGET_ID       INTEGER NOT NULL,
	MONTH                 CHAR(3) NULL,
	TARGET_AMOUNT         DECIMAL(15,2) NULL,
	USER_ID               INTEGER NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y',
	YEAR                  INTEGER NULL
)
;



CREATE UNIQUE INDEX XPKM_SALES_TARGET ON M_SALES_TARGET
(
	SALES_TARGET_ID
)
;



ALTER TABLE M_SALES_TARGET
	ADD  PRIMARY KEY (SALES_TARGET_ID)
;



CREATE INDEX XIF1M_SALES_TARGET ON M_SALES_TARGET
(
	USER_ID
)
;



CREATE TABLE M_SALES_TARGET_PRODUCT
(
	PRODUCT_ID            INTEGER NULL,
	SALES_TARGET_ID       INTEGER NULL,
	SALES_TARGET_PRODUCT_ID  INTEGER NOT NULL,
	TARGET_AMOUNT         DECIMAL(15,2) NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y'
)
;



CREATE UNIQUE INDEX XPKM_SALES_TARGET_PRODUCT ON M_SALES_TARGET_PRODUCT
(
	SALES_TARGET_PRODUCT_ID
)
;



ALTER TABLE M_SALES_TARGET_PRODUCT
	ADD  PRIMARY KEY (SALES_TARGET_PRODUCT_ID)
;



CREATE INDEX XIF2M_SALES_TARGET_PRODUCT ON M_SALES_TARGET_PRODUCT
(
	SALES_TARGET_ID
)
;



CREATE INDEX XIF3M_SALES_TARGET_PRODUCT ON M_SALES_TARGET_PRODUCT
(
	PRODUCT_ID
)
;



ALTER TABLE M_SALES_TARGET
	ADD FOREIGN KEY R_20 (USER_ID) REFERENCES AD_USER(USER_ID)
;



ALTER TABLE M_SALES_TARGET_PRODUCT
	ADD FOREIGN KEY R_8 (SALES_TARGET_ID) REFERENCES M_SALES_TARGET(SALES_TARGET_ID)
;


ALTER TABLE M_SALES_TARGET_PRODUCT
	ADD FOREIGN KEY R_21 (PRODUCT_ID) REFERENCES M_PRODUCT(PRODUCT_ID)
;


ALTER TABLE `ad_user` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `CATEGORY` CATEGORY VARCHAR(50) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `ORGANIZATION` ORGANIZATION VARCHAR(50) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `NAME` NAME VARCHAR(100) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `SOURCE_NAME` SOURCE_NAME VARCHAR(100) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `ID_CARD_NO` ID_CARD_NO VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `USER_NAME` USER_NAME VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `PASSWORD` PASSWORD VARCHAR(30) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `CODE` CODE VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci;
  
ALTER TABLE `m_sales_target` CHARACTER SET tis620 COLLATE tis620_thai_ci;
ALTER TABLE `m_sales_target` DROP FOREIGN KEY m_sales_target_ibfk_1;
ALTER TABLE `m_sales_target` ADD CONSTRAINT m_sales_target_ibfk_1 FOREIGN KEY (USER_ID) REFERENCES ad_user (USER_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE `m_sales_target_product` CHARACTER SET tis620 COLLATE tis620_thai_ci;

ALTER TABLE `m_sales_target_product` DROP FOREIGN KEY m_sales_target_product_ibfk_2,
  DROP FOREIGN KEY m_sales_target_product_ibfk_1;
ALTER TABLE `m_sales_target_product` ADD CONSTRAINT m_sales_target_product_ibfk_2 FOREIGN KEY (PRODUCT_ID) REFERENCES m_product (PRODUCT_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_sales_target_product_ibfk_1 FOREIGN KEY (SALES_TARGET_ID) REFERENCES m_sales_target (SALES_TARGET_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE `ad_user` CHANGE `ROLE` ROLE CHAR(5) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL,
  CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `m_sales_target` CHANGE `MONTH` MONTH CHAR(3) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `m_sales_target` DROP FOREIGN KEY m_sales_target_ibfk_1;
ALTER TABLE `m_sales_target` ADD CONSTRAINT m_sales_target_ibfk_1 FOREIGN KEY (USER_ID) REFERENCES ad_user (USER_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE `m_sales_target_product` CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `m_sales_target_product` DROP FOREIGN KEY m_sales_target_product_ibfk_1,
  DROP FOREIGN KEY m_sales_target_product_ibfk_2;
ALTER TABLE `m_sales_target_product` ADD CONSTRAINT m_sales_target_product_ibfk_1 FOREIGN KEY (SALES_TARGET_ID) REFERENCES m_sales_target (SALES_TARGET_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_sales_target_product_ibfk_2 FOREIGN KEY (PRODUCT_ID) REFERENCES m_product (PRODUCT_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;

  
  
  
  

CREATE TABLE M_SALES_TARGET_NEW
(
	SALES_TARGET_ID       INTEGER NOT NULL,
	TARGET_FROM           DATE NOT NULL,
	TARGET_TO             DATE NULL,
	PRODUCT_ID            INTEGER NULL,
	UOM_ID                VARCHAR(20) NULL,
	TARGET_QTY            DECIMAL(10,2) NULL,
	USER_ID               INTEGER NULL
)
;



CREATE UNIQUE INDEX XPKM_SALES_TARGET_NEW ON M_SALES_TARGET_NEW
(
	SALES_TARGET_ID
)
;



ALTER TABLE M_SALES_TARGET_NEW
	ADD  PRIMARY KEY (SALES_TARGET_ID)
;



CREATE INDEX XIF1M_SALES_TARGET_NEW ON M_SALES_TARGET_NEW
(
	PRODUCT_ID
)
;



CREATE INDEX XIF2M_SALES_TARGET_NEW ON M_SALES_TARGET_NEW
(
	UOM_ID
)
;



CREATE INDEX XIF3M_SALES_TARGET_NEW ON M_SALES_TARGET_NEW
(
	USER_ID
)
;



ALTER TABLE M_SALES_TARGET_NEW
	ADD FOREIGN KEY R_96 (PRODUCT_ID) REFERENCES M_PRODUCT(PRODUCT_ID)
;


ALTER TABLE M_SALES_TARGET_NEW
	ADD FOREIGN KEY R_97 (UOM_ID) REFERENCES M_UOM(UOM_ID)
;


ALTER TABLE M_SALES_TARGET_NEW
	ADD FOREIGN KEY R_98 (USER_ID) REFERENCES AD_USER(USER_ID)
;

alter table m_sales_target_new add column TARGET_HEADER_ID INT NOT NULL AFTER SALES_TARGET_ID;
