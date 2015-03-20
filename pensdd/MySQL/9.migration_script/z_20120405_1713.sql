DROP TABLE IF EXISTS t_shipment;
create table t_shipment
(shipment_id      int(11)  primary key
,shipment_no      varchar(22)  NOT NULL
,shipment_date    date        NOT NULL
,total_amount     decimal(15,5) default 0
,vat_amount       decimal(15,5) default 0
,lines_amount     decimal(15,5) default 0
,shipment_status  varchar(2)  NOT NULL
,order_id         int(11)     NOT NULL
,taxinvoice_id    int(11)
,description      varchar(225)
,created          timestamp  default current_timestamp
,created_by       int(11)
,updated          timestamp
,updated_by       int(11)
);

DROP TABLE IF EXISTS t_shipment_line;
create table t_shipment_line
(shipment_line_id  int(11)  primary key
,shipment_id        int(11) not null
,total_amount      decimal(15,5) default 0
,vat_amount        decimal(15,5) default 0
,lines_amount      decimal(15,5) default 0
,qty               int(11) default 0
,uom_id             varchar(10)
,order_line_id     int(11)
,product_id        int(11)
,description       varchar(225)
,created           timestamp  default current_timestamp
,created_by        int(11)
,updated           timestamp
,updated_by        int(11)
)ENGINE=InnoDB DEFAULT CHARSET=tis620;


DROP TABLE IF EXISTS t_taxinvoice;
create table t_taxinvoice
(taxinvoice_id      int(11)  primary key
,taxinvoice_no      varchar(22)  NOT NULL
,taxinvoice_date    date         NOT NULL
,total_amount       decimal(15,5) default 0
,vat_amount         decimal(15,5) default 0
,lines_amount       decimal(15,5) default 0
,taxinvoice_status  varchar(2) NOT NULL
,order_id         int(11)      NOT NULL
,description      varchar(225)
,created          timestamp  default current_timestamp
,created_by       int(11)
,updated          timestamp
,updated_by       int(11)
)ENGINE=InnoDB DEFAULT CHARSET=tis620;

DROP TABLE IF EXISTS t_taxinvoice_line;
create table t_taxinvoice_line
(taxinvoice_line_id  int(11)  primary key
,taxinvoice_id        int(11)  not null
,total_amount      decimal(15,5) default 0
,vat_amount        decimal(15,5) default 0
,lines_amount      decimal(15,5) default 0
,qty               int(11) default 0
,uom_id            varchar(10) not null
,product_id        int(11)
,price             decimal(15,5) default 0
,order_line_id     int(11)
,description       varchar(225)
,created           timestamp  default current_timestamp
,created_by        int(11)
,updated           timestamp
,updated_by        int(11)
)ENGINE=InnoDB DEFAULT CHARSET=tis620;

insert into c_sequence values(27,'t_shipment','Y',1,1);
insert into c_sequence values(28,'t_shipment_line','Y',1,1);
insert into c_sequence values(29,'t_taxinvoice','Y',1,1);
insert into c_sequence values(30,'t_taxinvoice_line','Y',1,1);

insert into c_doctype values(600,'ShipmentNo','ShipmentNo','Y');

insert into c_doctype values(700,'ShipmentTaxInvoiceNo','ShipmentTaxInvoiceNo','Y');

insert into c_doctype values(800,'ReceiptTaxInvoiceNo','ReceiptTaxInvoiceNo','Y');

insert into c_doctype_sequence values ('MM','Y',4,600,1,1,2555,4,'',current_timestamp,1);
insert into c_doctype_sequence values ('MM','Y',5,700,1,1,2555,4,'',current_timestamp,1);
insert into c_doctype_sequence values ('MM','Y',6,800,1,1,2555,4,'',current_timestamp,1);

alter table t_receipt add column taxinvoice_id int;
