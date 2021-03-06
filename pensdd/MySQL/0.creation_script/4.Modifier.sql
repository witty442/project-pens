

DROP TABLE M_RELATION_MODIFIER
;




DROP TABLE M_MODIFIER_ATTR
;




DROP TABLE M_MODIFIER_LINE
;




DROP TABLE M_MODIFIER
;



CREATE TABLE M_MODIFIER
(
	MODIFIER_ID           INTEGER NOT NULL,
	TYPE                  VARCHAR(10) NULL,
	CODE                  VARCHAR(20) NULL,
	NAME                  VARCHAR(100) NULL,
	DESCRIPTION           VARCHAR(255) NULL,
	START_DATE            DATE NULL,
	END_DATE              DATE NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y',
	ISAUTOMATIC           CHAR(1) NOT NULL DEFAULT 'Y'
)
;



CREATE UNIQUE INDEX XPKM_MODIFIEER ON M_MODIFIER
(
	MODIFIER_ID
)
;



ALTER TABLE M_MODIFIER
	ADD  PRIMARY KEY (MODIFIER_ID)
;



CREATE TABLE M_MODIFIER_ATTR
(
	MODIFIER_ATTR_ID      INTEGER NOT NULL,
	MODIFIER_LINE_ID      INTEGER NULL,
	PRODUCT_ATTRIBUTE     VARCHAR(30) NULL,
	PRODUCT_ATTRIBUTE_VALUE  VARCHAR(30) NULL,
	PRODUCT_UOM_ID        VARCHAR(20) NULL,
	ISEXCLUDE             CHAR(1) NULL DEFAULT 'N',
	VALUE_FROM            DECIMAL(10,2) NULL,
	VALUE_TO              DECIMAL(10,2) NULL,
	OPERATOR              VARCHAR(20) NULL
)
;



CREATE UNIQUE INDEX XPKM_MODIFIER_ATTR ON M_MODIFIER_ATTR
(
	MODIFIER_ATTR_ID
)
;



ALTER TABLE M_MODIFIER_ATTR
	ADD  PRIMARY KEY (MODIFIER_ATTR_ID)
;



CREATE INDEX XIF1M_MODIFIER_ATTR ON M_MODIFIER_ATTR
(
	MODIFIER_LINE_ID
)
;



CREATE INDEX XIF2M_MODIFIER_ATTR ON M_MODIFIER_ATTR
(
	PRODUCT_UOM_ID
)
;



CREATE TABLE M_MODIFIER_LINE
(
	MODIFIER_LINE_ID      INTEGER NOT NULL,
	MODIFIER_ID           INTEGER NULL,
	TYPE                  VARCHAR(10) NULL,
	START_DATE            DATE NULL,
	END_DATE              DATE NULL,
	LEVEL                 VARCHAR(20) NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y',
	ISAUTOMATIC           CHAR(1) NOT NULL DEFAULT 'Y',
	BREAK_TYPE            VARCHAR(20) NULL,
	APPLICATION_METHOD    VARCHAR(20) NULL,
	VALUE                 DECIMAL(10,2) NULL,
	PRECEDENCE            INTEGER NULL,
	PRICELIST_ID          INTEGER NULL,
	BENEFIT_QTY           DECIMAL(10,2) NULL,
	VOLUME_TYPE           VARCHAR(20) NULL,
	BENEFIT_UOM_ID        VARCHAR(20) NULL,
	VALUE_FROM            DECIMAL(10,2) NULL,
	VALUE_TO              DECIMAL(10,2) NULL
)
;



CREATE UNIQUE INDEX XPKM_MODIFIER_LINE ON M_MODIFIER_LINE
(
	MODIFIER_LINE_ID
)
;



ALTER TABLE M_MODIFIER_LINE
	ADD  PRIMARY KEY (MODIFIER_LINE_ID)
;



CREATE INDEX XIF1M_MODIFIER_LINE ON M_MODIFIER_LINE
(
	MODIFIER_ID
)
;



CREATE INDEX XIF2M_MODIFIER_LINE ON M_MODIFIER_LINE
(
	PRICELIST_ID
)
;



CREATE INDEX XIF4M_MODIFIER_LINE ON M_MODIFIER_LINE
(
	BENEFIT_UOM_ID
)
;



CREATE TABLE M_RELATION_MODIFIER
(
	RELATION_MODIFIER_ID  INTEGER NOT NULL,
	MODIFIER_TYPE         VARCHAR(20) NULL,
	MODIFIER_LINE_FROM_ID  INTEGER NULL,
	MODIFIER_LINE_TO_ID   INTEGER NULL
)
;



CREATE UNIQUE INDEX XPKM_RELATION_MODIFIER ON M_RELATION_MODIFIER
(
	RELATION_MODIFIER_ID
)
;



ALTER TABLE M_RELATION_MODIFIER
	ADD  PRIMARY KEY (RELATION_MODIFIER_ID)
;



CREATE INDEX XIF1M_RELATION_MODIFIER ON M_RELATION_MODIFIER
(
	MODIFIER_LINE_FROM_ID
)
;



CREATE INDEX XIF2M_RELATION_MODIFIER ON M_RELATION_MODIFIER
(
	MODIFIER_LINE_TO_ID
)
;



ALTER TABLE M_MODIFIER_ATTR
	ADD FOREIGN KEY R_66 (MODIFIER_LINE_ID) REFERENCES M_MODIFIER_LINE(MODIFIER_LINE_ID)
;


ALTER TABLE M_MODIFIER_ATTR
	ADD FOREIGN KEY R_67 (PRODUCT_UOM_ID) REFERENCES M_UOM(UOM_ID)
;



ALTER TABLE M_MODIFIER_LINE
	ADD FOREIGN KEY R_55 (MODIFIER_ID) REFERENCES M_MODIFIER(MODIFIER_ID)
;


ALTER TABLE M_MODIFIER_LINE
	ADD FOREIGN KEY R_56 (PRICELIST_ID) REFERENCES M_PRICELIST(PRICELIST_ID)
;


ALTER TABLE M_MODIFIER_LINE
	ADD FOREIGN KEY R_65 (BENEFIT_UOM_ID) REFERENCES M_UOM(UOM_ID)
;



ALTER TABLE M_RELATION_MODIFIER
	ADD FOREIGN KEY R_62 (MODIFIER_LINE_FROM_ID) REFERENCES M_MODIFIER_LINE(MODIFIER_LINE_ID)
;


ALTER TABLE M_RELATION_MODIFIER
	ADD FOREIGN KEY R_64 (MODIFIER_LINE_TO_ID) REFERENCES M_MODIFIER_LINE(MODIFIER_LINE_ID)
;

ALTER TABLE M_MODIFIER_ATTR ADD COLUMN MODIFIER_ID  INTEGER;

CREATE INDEX XIF3M_MODIFIER_ATTR ON M_MODIFIER_ATTR
(
	MODIFIER_ID
);


ALTER TABLE `m_modifier` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `TYPE` TYPE VARCHAR(10) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `CODE` CODE VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `NAME` NAME VARCHAR(100) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `DESCRIPTION` DESCRIPTION VARCHAR(255) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `ISACTIVE` ISACTIVE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y',
  CHANGE `ISAUTOMATIC` ISAUTOMATIC CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci NOT NULL DEFAULT 'Y';
ALTER TABLE `m_modifier_attr` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `PRODUCT_ATTRIBUTE` PRODUCT_ATTRIBUTE VARCHAR(30) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `PRODUCT_ATTRIBUTE_VALUE` PRODUCT_ATTRIBUTE_VALUE VARCHAR(30) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `ISEXCLUDE` ISEXCLUDE CHAR(1) CHARACTER SET tis620 COLLATE tis620_thai_ci DEFAULT 'N',
  CHANGE `OPERATOR` OPERATOR VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci;
ALTER TABLE `m_modifier_attr` DROP FOREIGN KEY m_modifier_attr_ibfk_2,
  DROP FOREIGN KEY m_modifier_attr_ibfk_1;
ALTER TABLE `m_modifier_attr` ADD CONSTRAINT m_modifier_attr_ibfk_2 FOREIGN KEY (PRODUCT_UOM_ID) REFERENCES m_uom (UOM_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_modifier_attr_ibfk_1 FOREIGN KEY (MODIFIER_LINE_ID) REFERENCES m_modifier_line (MODIFIER_LINE_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE `m_modifier_line` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `TYPE` TYPE VARCHAR(10) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `LEVEL` LEVEL VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `BREAK_TYPE` BREAK_TYPE VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `APPLICATION_METHOD` APPLICATION_METHOD VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `VOLUME_TYPE` VOLUME_TYPE VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci;
ALTER TABLE `m_modifier_line` DROP FOREIGN KEY m_modifier_line_ibfk_3,
  DROP FOREIGN KEY m_modifier_line_ibfk_1,
  DROP FOREIGN KEY m_modifier_line_ibfk_2;
ALTER TABLE `m_modifier_line` ADD CONSTRAINT m_modifier_line_ibfk_3 FOREIGN KEY (BENEFIT_UOM_ID) REFERENCES m_uom (UOM_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_modifier_line_ibfk_1 FOREIGN KEY (MODIFIER_ID) REFERENCES m_modifier (MODIFIER_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_modifier_line_ibfk_2 FOREIGN KEY (PRICELIST_ID) REFERENCES m_pricelist (PRICELIST_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE `m_relation_modifier` CHARACTER SET tis620 COLLATE tis620_thai_ci,
  CHANGE `MODIFIER_TYPE` MODIFIER_TYPE VARCHAR(20) CHARACTER SET tis620 COLLATE tis620_thai_ci;
ALTER TABLE `m_relation_modifier` DROP FOREIGN KEY m_relation_modifier_ibfk_2,
  DROP FOREIGN KEY m_relation_modifier_ibfk_1;
ALTER TABLE `m_relation_modifier` ADD CONSTRAINT m_relation_modifier_ibfk_2 FOREIGN KEY (MODIFIER_LINE_TO_ID) REFERENCES m_modifier_line (MODIFIER_LINE_ID) ON UPDATE RESTRICT ON DELETE RESTRICT,
  ADD CONSTRAINT m_relation_modifier_ibfk_1 FOREIGN KEY (MODIFIER_LINE_FROM_ID) REFERENCES m_modifier_line (MODIFIER_LINE_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;


ALTER TABLE M_MODIFIER ADD COLUMN ORDER_TYPE  VARCHAR(5) DEFAULT NULL;
ALTER TABLE M_MODIFIER_LINE ADD COLUMN ORDER_TYPE  VARCHAR(5) DEFAULT NULL;


CREATE TABLE M_QUALIFIER
(
	QUALIFIER_ID          INTEGER NOT NULL,
	QUALIFIER_CONTEXT     VARCHAR(100) NULL,
	QUALIFIER_TYPE        VARCHAR(100) NULL,
	QUALIFIER_VALUE       VARCHAR(100) NULL,
	OPERATOR              VARCHAR(20) NULL,
	ISEXCLUDE             CHAR(1) NULL DEFAULT 'N',
	START_DATE            DATE NULL,
	END_DATE              DATE NULL,
	MODIFIER_ID           INTEGER NULL,
	MODIFIER_LINE_ID      INTEGER NULL,
	ISACTIVE              CHAR(1) NOT NULL DEFAULT 'Y',
	VALUE_FROM            DECIMAL(10,2) NULL,
	VALUE_TO              DECIMAL(10,2) NULL
)ENGINE=InnoDB DEFAULT CHARSET=tis620;



CREATE UNIQUE INDEX XPKM_QULIFIERS ON M_QUALIFIER
(
	QUALIFIER_ID
)
;



ALTER TABLE M_QUALIFIER
	ADD  PRIMARY KEY (QUALIFIER_ID)
;



CREATE INDEX XIF1M_QULIFIERS ON M_QUALIFIER
(
	MODIFIER_ID
)
;



CREATE INDEX XIF2M_QULIFIERS ON M_QUALIFIER
(
	MODIFIER_LINE_ID
)
;



ALTER TABLE M_QUALIFIER
	ADD FOREIGN KEY R_85 (MODIFIER_ID) REFERENCES M_MODIFIER(MODIFIER_ID)
;


ALTER TABLE M_QUALIFIER
	ADD FOREIGN KEY R_86 (MODIFIER_LINE_ID) REFERENCES M_MODIFIER_LINE(MODIFIER_LINE_ID)
;


ALTER TABLE m_modifier_line ADD COLUMN DESCRIPTION  VARCHAR(2000) DEFAULT NULL;
