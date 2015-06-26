alter table pensbme_barcode_scan modify Status varchar(2);

alter table scan.pensbme_barcode_scan_item
 add Whole_Price_BF decimal(11,2)