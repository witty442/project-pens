insert c_doctype (doctype_id,name,description ,isactive)values(600,'MoveOrderReq','MoveOrderReq','Y');
insert c_doctype (doctype_id,name,description ,isactive)values(700,'MoveOrderReturn','MoveOrderTurn','Y');

create table t_move_order
(   
    request_number        varchar(30) not null,
    request_date            date not null,
    organization_id          int not null,
    sales_code      VARCHAR(20) not null, /** UserName V001 **/
    pd_code      VARCHAR(20) not null,
    description varchar(240),
    
    move_order_type varchar(20) not null,
    status varchar(2),
    print_no int,
    print_date_long decimal(20,6),
    exported char(1),
    USER_ID int(10),
    CREATED_LONG decimal(20,6),
	CREATED_BY varchar(20),
	UPDATED_LONG decimal(20,6),
	UPDATED_BY varchar(20),
	PRIMARY KEY (request_number)
)ENGINE=InnoDB DEFAULT CHARSET=tis620;  


create table t_move_order_line
(   
    request_number        varchar(30) not null, /** Generate By UserName **/
    line_number               int not null, /** Autom Increment  **/
    inventory_item_id      int not null, /** ProductCode **/
    qty     decimal(15,5)  not null,  /** calc  */
    qty1    decimal(15,5), /** CTN ctn_qty  **/
    qty2    decimal(15,5), /** PAC pcs_qty  **/
    pack    decimal(15,5)  not null,  /** calc  */
    
    uom1    VARCHAR(20), 
    uom2    VARCHAR(20),
    
    amount1     decimal(15,5), /** CTN ctn_qty  **/
    amount2    decimal(15,5), /** PAC pcs_qty  **/
    total_amount    decimal(15,5), /** amount 1+amount2**/
      
    status varchar(2),
    exported char(1),
    USER_ID int(10),
    
    CREATED_LONG decimal(20,6),
	CREATED_BY varchar(20),
	UPDATED_LONG decimal(20,6),
	UPDATED_BY varchar(20),
	PRIMARY KEY (request_number,line_number)
	
) ENGINE=InnoDB DEFAULT CHARSET=tis620;  


create table m_pd(
   pd_code    varchar(10) not null,
   sales_code    varchar(10) not null,
   pd_desc    varchar(100) ,
   PRIMARY KEY (sales_code,pd_code)
)ENGINE=InnoDB DEFAULT CHARSET=tis620;  

insert into m_pd values ('P001','V001','P001  PD บ. ไอ.ซี.ซี อินเตอร์เนชั่นแนล จำกัด (มหาชน) ');
insert into m_pd values ('P001','V002','P001  PD บ. ไอ.ซี.ซี อินเตอร์เนชั่นแนล จำกัด (มหาชน) ');
insert into m_pd values ('P001','V003','P001  PD บ. ไอ.ซี.ซี อินเตอร์เนชั่นแนล จำกัด (มหาชน) ');
insert into m_pd values ('P001','V004','P001  PD บ. ไอ.ซี.ซี อินเตอร์เนชั่นแนล จำกัด (มหาชน) ');
insert into m_pd values ('P001','V005','P001  PD บ. ไอ.ซี.ซี อินเตอร์เนชั่นแนล จำกัด (มหาชน) ');
insert into m_pd values ('P109','V101','P109  PD มนตรี มโนสุดประสิทธิ์                       ');
insert into m_pd values ('P107','V102','P107  PD ห้างหุ้นส่วนจำกัด โชคสมบูรณ์พูนผล           ');
insert into m_pd values ('P106','V103','P106  PD อาภานนท์ เทรดดิ้ง                           ');
insert into m_pd values ('P102','V104','P102  PD คุณเล็ก                                     ');
insert into m_pd values ('P101','V105','P101  PD น.ส.ฐิตาภา อำไพโรจน์วงศ์                    ');
insert into m_pd values ('P207','V201','P207  PD นายบุญทรง ตรีวัฒนาวงศ์                      ');
insert into m_pd values ('P208','V201','P208  PD นายกิตติทัต รัตนไพบูลย์เจริญ                ');
insert into m_pd values ('P203','V202','P203  PD นายชาญชัย พิพัฒนานิมิตร                     ');
insert into m_pd values ('P205','V202','P205  PD นายสุรพงษ์ ณรงค์ตะณุพล                      ');
insert into m_pd values ('P206','V202','P206  PD นายสุรพงษ์ ณรงค์ตะณุพล                      ');
insert into m_pd values ('P204','V203','P204  PD นายภาคภูมิ บุนนาค                           ');
insert into m_pd values ('P209','V203','P209  PD นายสมนึก อมรเสถียรพงศ์                      ');
insert into m_pd values ('P202','V204','P202  PD คณะบุคคล เอสอาร์ ดี.ซ๊.                     ');
insert into m_pd values ('P201','V205','P201  PD นายวิจิตร ตันเวทติยานนท์                    ');
insert into m_pd values ('P201','V206','P201  PD นายวิจิตร ตันเวทติยานนท์                    ');
insert into m_pd values ('P311','V301','P311  PD ห้างหุ้นส่วนจำกัด ยู.พี.เค.กรุ๊ป (2011)     ');
insert into m_pd values ('P306','V302','P306  PD ลิ้มเซียวจิว                                ');
insert into m_pd values ('P310','V302','P310  PD ศิริพร                                      ');
insert into m_pd values ('P305','V303','P305  PD คณะบุคคลตั้งแสงไทย                          ');
insert into m_pd values ('P309','V304','P309  PD กัญจนันท์                                   ');
insert into m_pd values ('P307','V305','P307  PD จิวเอี่ยมฮวด                                ');
insert into m_pd values ('P312','V305','P312  PD นิตยา เอื้อพงศธร                            ');
insert into m_pd values ('P301','V306','P301  PD อึ้งเซ้งเฮง (1994)                          ');
insert into m_pd values ('P303','V307','P303  PD สมิต                                        ');
insert into m_pd values ('P306','V307','P306  PD ลิ้มเซียวจิว                                ');
insert into m_pd values ('P404','V401','P404  PD คุณประดิษฐ์ จันทร์ขจร                       ');
insert into m_pd values ('P401','V402','P401  PD คุณชัยณรงค์ เมธีภัทรกุล                     ');
insert into m_pd values ('P405','V402','P405  PD คุณวิศาล สว่างสมุทร                         ');
insert into m_pd values ('P403','V403','P403  PD บจก. หาดใหญ่โกวย่งฮั้ว                      ');
insert into m_pd values ('P410','V404','P410  PD สำอาง ภู่ชำนาญกิจ                           ');
insert into m_pd values ('P406','V405','P406  PD คุณอมรรัตน์ เดชธรากุล                       ');
insert into m_pd values ('P407','V405','P407  PD คุณสุฤทธิ์ ตันติวิบูลย์ชัย                  ');
insert into m_pd values ('P409','V405','P409  PD ธนอร อนุกูลกาญจน์                           ');
insert into m_pd values ('P301','V308','P301  PD อึ้งเซ้งเฮง (1994)                          ');
insert into m_pd values ('P304','V308','P304  PD ณัฐวุฒิ                                     ');
insert into m_pd values ('P001','V051','P001  PD บ. ไอ.ซี.ซี อินเตอร์เนชั่นแนล จำกัด (มหาชน) ');
insert into m_pd values ('P002','V052','P002  PD อ้อมน้อย                                    ');
insert into m_pd values ('P002','V053','P002  PD อ้อมน้อย                                    ');
insert into m_pd values ('P002','V054','P002  PD อ้อมน้อย                                    ');
insert into m_pd values ('P002','V055','P002  PD อ้อมน้อย                                    ');
insert into m_pd values ('P108','V106','P108  PD กาญจนา ไกรลพ                                ');
insert into m_pd values ('P110','V106','P110  PD ธิติมา                                      ');
insert into m_pd values ('P210','V207','P210  PD นายธนพล เตชะแพร่พิบูรณ์                     ');
insert into m_pd values ('P103','V107','P103  PD ศุภเนตร บูรณากาญจน์                         ');
insert into m_pd values ('P104','V108','P104  PD ลัดดา ไทยกรณ์                               ');
insert into m_pd values ('P105','V108','P105  PD ล้อฮงไถ่                                    ');
insert into m_pd values ('P308','V309','P308  PD เกียรติวิทยะ                                ');
insert into m_pd values ('P311','V309','P311  PD ห้างหุ้นส่วนจำกัด ยู.พี.เค.กรุ๊ป (2011)     ');
insert into m_pd values ('P302','V310','P302  PD ตั้งรุ่งโรจน์                               ');
insert into m_pd values ('P411','V406','P411  PD ศรีสมัย                                     ');
insert into m_pd values ('P001','V012','P001  PD บ. ไอ.ซี.ซี อินเตอร์เนชั่นแนล จำกัด (มหาชน) ');

