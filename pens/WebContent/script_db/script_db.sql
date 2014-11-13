drop view m_sales_target_new_v;

CREATE 
    ALGORITHM = UNDEFINED 
    SQL SECURITY DEFINER
VIEW `m_sales_target_new_v` AS
    select 
        `stn`.`SALES_TARGET_ID` AS `SALES_TARGET_ID`,
        `stn`.`TARGET_HEADER_ID` AS `TARGET_HEADER_ID`,
        `stn`.`TARGET_FROM` AS `TARGET_FROM`,
        `stn`.`TARGET_TO` AS `TARGET_TO`,
        `stn`.`PRODUCT_ID` AS `PRODUCT_ID`,
        `stn`.`UOM_ID` AS `UOM_ID`,
        `stn`.`TARGET_QTY` AS `TARGET_QTY`,
        `stn`.`USER_ID` AS `USER_ID`,
        `pd`.`CODE` AS `ProductCode`
    from
        (`m_sales_target_new` `stn`
        join `m_product` `pd` ON (((`stn`.`PRODUCT_ID` = `pd`.`PRODUCT_ID`)
            and (`stn`.`UOM_ID` = `pd`.`UOM_ID`)))); 


alter table m_customer
add column(
  PRINT_TYPE varchar(5),
  PRINT_BRANCH_DESC varchar(30),
  PRINT_HEAD_BRANCH_DESC varchar(30),
  PRINT_TAX char(1) default 'N'
)