--References
DELETE FROM c_reference;

Insert into C_REFERENCE(REFERENCE_ID, CODE, NAME, VALUE, ISACTIVE) Values
(1, 'OrgID', 'OrgID', '81', 'Y');

Insert into C_REFERENCE(REFERENCE_ID, CODE, NAME, VALUE, ISACTIVE) Values
(101, 'CustomerType', 'Modern Trade', 'MT', 'N'),
(102, 'CustomerType', 'Credit', 'CT', 'Y'),
(103, 'CustomerType', 'Cash', 'CV', 'Y'),
(104, 'CustomerType', 'Direct Delivery', 'DD', 'Y'),
 
(201, 'Territory', '��ا෾��ҹ��', '0', 'Y'),
(202, 'Territory', '�Ҥ��ҧ', '1', 'Y'),
(203, 'Territory', '�Ҥ���ҹ', '3', 'Y'),
(204, 'Territory', '�Ҥ�˹��', '2', 'Y'),
(205, 'Territory', '�Ҥ��', '4', 'Y'),
 
(301, 'SalesGroup', 'Modern Trade', 'M', 'N'),
(302, 'SalesGroup', 'Credit', 'S', 'Y'),
(303, 'SalesGroup', 'Cash', 'C', 'Y'),
(304, 'SalesGroup', 'Direct Delivery', 'D', 'Y'),
 
(401, 'OrderType', 'Modern Trade', 'MT', 'N'),
(402, 'OrderType', 'Credit', 'CR', 'Y'),
(403, 'OrderType', 'Cash', 'CS', 'Y'),
(404, 'OrderType', 'Direct Delivery', 'DD', 'Y'),
 

(501, 'Shipment', '���Թ��ҷ����ҹ/��ҹ', '1', 'Y'),
(502, 'Shipment', '���Ѻ�Թ����ͧ (��ǹ�Ѻ�ͧ)', '2', 'Y'),
 

(601, 'MemberType', '��Ҫԡ 3 ��͹', '3', 'Y'),
(602, 'MemberType', '��Ҫԡ 6 ��͹', '6', 'Y'),
(603, 'MemberType', '��Ҫԡ 9 ��͹', '9', 'Y'),
(604, 'MemberType', '��Ҫԡ 12 ��͹', '12', 'Y'),
 

(701, 'MemberStatus', 'Regular', 'R', 'Y'),
(702, 'MemberStatus', 'Silver', 'S', 'Y'),
(703, 'MemberStatus', 'Gold', 'G', 'Y'),
(704, 'MemberStatus', 'Diamond', 'D', 'Y'),
(705, 'MemberStatus', 'Platinum', 'P', 'Y'),
 
 
(800, 'Role', 'Administrator', 'AD', 'Y'),
(801, 'Role', 'Credit Sales', 'TT', 'Y'),
(802, 'Role', 'Van Sales', 'VAN', 'Y'),
(803, 'Role', 'Direct Delivery', 'DD', 'Y'),
 

(901, 'PaymentTerm', '15 �ѹ', '15', 'Y'),
(902, 'PaymentTerm', '30 �ѹ', '30', 'Y'),
(903, 'PaymentTerm', '45 �ѹ', '45', 'Y'),
(904, 'PaymentTerm', 'Immediate', 'IM', 'Y'),
 

(1001, 'VatCode', '0%', '0', 'Y'),
(1002, 'VatCode', '7%', '7', 'Y'),
 

(1101, 'PaymentMethod', '�Թʴ', 'CS', 'Y'),
(1102, 'PaymentMethod', '��', 'CH', 'Y'),
(1103, 'PaymentMethod', '�ѵ��ôԵ', 'CR', 'Y'),
 

(1201, 'UnclosedReason', '��ҹ��һԴ', '1', 'Y'),
(1202, 'UnclosedReason', '�Թ����ʵ�͡�ըӹǹ�ҡ', '2', 'Y'),
(1203, 'UnclosedReason', '�Թ������� (�ѧ������ѡ�Թ���)', '3', 'Y'),
 

(1301, 'RoleTT', 'CustomerType', 'CT', 'Y'),
(1302, 'RoleTT', 'OrderType', 'CR', 'Y'),
(1303, 'RoleTT', 'SalesGroup', 'S', 'Y'),
 
 
(1401, 'RoleVAN', 'CustomerType', 'CV', 'Y'),
(1402, 'RoleVAN', 'OrderType', 'CS', 'Y'),
(1403, 'RoleVAN', 'SalesGroup', 'C', 'Y'),
 
 
(1501, 'RoleDD', 'CustomerType', 'DD', 'Y'),
(1502, 'RoleDD', 'OrderType', 'DD', 'Y'),
(1503, 'RoleDD', 'SalesGroup', 'D', 'Y'),
 
(1600, 'QtyDeliver', '7', '7', 'Y'),
(1601, 'QtyDeliver', '15', '15', 'Y'),
(1602, 'QtyDeliver', '30', '30', 'Y'),
(1603, 'QtyDeliver', '45', '45', 'Y'),


(1700, 'RoundDeliver', '7 �ѹ', '7', 'Y'),
(1701, 'RoundDeliver', '15 �ѹ', '15', 'Y'),
(1702, 'RoundDeliver', '30 �ѹ', '30', 'Y'),


(1800, 'DocRun', '����ա�ùѺ����', '', 'Y'),
(1801, 'DocRun', '�����͹', 'MM', 'Y'),
(1802, 'DocRun', '�����', 'YY', 'Y'),


(1901, 'Bank', '��Ҥ���¾ҳԪ���', '01', 'Y'),
(1902, 'Bank', '��Ҥ�á�ا෾', '02', 'Y'),
(1903, 'Bank', '��Ҥ�á�ԡ���', '03', 'Y'),
(1904, 'Bank', '��Ҥ�á�ا��', '04', 'Y'),
(1905, 'Bank', '��Ҥ�÷�����', '05', 'Y'),
(1906, 'Bank', '��Ҥ������Թ', '06', 'Y'),
(1907, 'Bank', '��Ҥ����������觻������', '07', 'Y'),


(2001, 'DeliveryGroup', 'A', 'A', 'Y'),
(2002, 'DeliveryGroup', 'B', 'B', 'Y'),
(2003, 'DeliveryGroup', 'C', 'C', 'Y'),
(2004, 'DeliveryGroup', 'D', 'D', 'Y'),
(2005, 'DeliveryGroup', 'E', 'E', 'Y'),
(2006, 'DeliveryGroup', 'F', 'F', 'Y'),
(2007, 'DeliveryGroup', 'G', 'G', 'Y'),
(2008, 'DeliveryGroup', 'H', 'H', 'Y'),
(2009, 'DeliveryGroup', 'I', 'I', 'Y'),
(2010, 'DeliveryGroup', 'J', 'J', 'Y'),
(2011, 'DeliveryGroup', 'K', 'K', 'Y'),
(2012, 'DeliveryGroup', 'L', 'L', 'Y'),
(2013, 'DeliveryGroup', 'M', 'M', 'Y'),
(2014, 'DeliveryGroup', 'N', 'N', 'Y'),
(2015, 'DeliveryGroup', 'O', 'O', 'Y'),
(2016, 'DeliveryGroup', 'P', 'P', 'Y'),
(2017, 'DeliveryGroup', 'Q', 'Q', 'Y'),
(2018, 'DeliveryGroup', 'R', 'R', 'Y'),
(2019, 'DeliveryGroup', 'S', 'S', 'Y'),
(2020, 'DeliveryGroup', 'T', 'T', 'Y'),
(2021, 'DeliveryGroup', 'U', 'U', 'Y'),
(2022, 'DeliveryGroup', 'V', 'V', 'Y'),
(2023, 'DeliveryGroup', 'W', 'W', 'Y'),
(2024, 'DeliveryGroup', 'X', 'X', 'Y'),
(2025, 'DeliveryGroup', 'Y', 'Y', 'Y'),
(2026, 'DeliveryGroup', 'Z', 'Z', 'Y'),
 
(9801, 'DocStatus', '�ѹ�֡', 'SV', 'Y'),
(9802, 'DocStatus', '¡��ԡ', 'VO', 'Y'),

(9901, 'Active', '��ҹ', 'Y', 'Y'),
(9902, 'Active', '�����ҹ', 'N', 'Y'),
(9903, 'Active', '¡��ԡ', 'C', 'Y');


Insert into C_REFERENCE(REFERENCE_ID, CODE, NAME, VALUE, ISACTIVE) Values
(2101, 'InternalBank', 'BBL-�ҢҶ���Ѫ��-����¡�Ҹػ�д�ɰ�  195-4-75402-2', '001', 'Y');
Insert into C_REFERENCE(REFERENCE_ID, CODE, NAME, VALUE, ISACTIVE) Values
(2102, 'InternalBank', 'SCB-�Ң��Ҹػ�д�ɰ�  068-2-81805-7', '002', 'Y');
Insert into C_REFERENCE(REFERENCE_ID, CODE, NAME, VALUE, ISACTIVE) Values
(2103, 'InternalBank', 'UOB  �Ң��Ѫ��-�Ҹػ�д�ɰ�  089-2-45038-8', '003', 'Y');


--Config
DELETE FROM c_sysconfig;
INSERT INTO c_sysconfig(SYSCONFIG_ID, NAME, VALUE) VALUES
(1, 'AlertPeriod', '7'),
(2, 'QtyDeliver', '15'),
(3, 'RoundDeliver', '15'),
(4, 'MemberType', '3');

--DocSeq
DELETE FROM c_doctype;
INSERT INTO c_doctype(DOCTYPE_ID, NAME, DESCRIPTION, ISACTIVE) VALUES 
(100, 'CustomerNo', 'Customer No', 'Y'),
(200, 'MemberNo', 'Member No', 'Y'),
(300, 'OrderNo', 'Order No', 'Y'),
(400, 'ReceiptNo', 'Receipt No', 'Y'),
(500, 'VisitNo', 'Visit No', 'Y');


   
--Message
DELETE FROM c_message;
INSERT INTO c_message(MESSAGE_CODE, DESCRIPTION_TH, DESCRIPTION_EN) VALUES 
('I0001', '�ѹ�֡���������º��������', 'Save success.'),
('I0002', '��辺�����ŷ���ͧ���', 'Record(s) not found.'),
('I0003', '������Է��������ҹ ��سҵԴ��ͼ������к�', 'No access privilege, Please contact your administrator.'),
('F0001', '�������ö�ѹ�֡��������', 'Save fail.'),
('F0002', '�բ�ͼԴ��Ҵ', 'An Error Occured.'),
('F0003', '���ʢ����� ���� �Ţ����͡��ë�� ��سҵ���Ţ����͡������� ', 'Duplicate Document No.'),
('F0004', '����� Price List �����ҹ������Ѻ�ѹ���', 'No Price List for today');

/*!40000 ALTER TABLE `m_delivery_route` DISABLE KEYS */;
DELETE FROM m_delivery_route;
INSERT INTO `m_delivery_route` (`DELIVERY_ROUTE_ID`,`DAYS`,`DISTRICT_ID`) VALUES 
 (1,'Mon',120),
 (2,'Mon',129),
 (3,'Mon',156),
 (4,'Mon',132),
 (5,'Mon',126),
 (6,'Mon',121),
 (7,'Mon',146),
 (8,'Mon',115),
 (9,'Mon',142),
 (10,'Mon',133),
 (11,'Mon',119),
 (12,'Mon',123),
 (13,'Mon',124),
 (14,'Mon',122),
 (15,'Mon',111),
 (16,'Tue',155),
 (17,'Tue',116),
 (18,'Tue',158),
 (19,'Tue',114),
 (20,'Tue',127),
 (21,'Tue',159),
 (22,'Tue',117),
 (23,'Tue',152),
 (24,'Tue',113),
 (25,'Tue',149),
 (26,'Tue',148),
 (27,'Tue',125),
 (28,'Tue',135),
 (29,'Wed',140),
 (30,'Wed',151),
 (31,'Wed',137),
 (32,'Wed',131),
 (33,'Wed',112),
 (34,'Wed',143),
 (35,'Wed',157),
 (36,'Wed',147),
 (37,'Thu',136),
 (38,'Thu',110),
 (39,'Thu',150),
 (40,'Thu',139),
 (41,'Thu',145),
 (42,'Thu',154),
 (43,'Thu',138),
 (44,'Thu',141),
 (45,'Thu',153),
 (46,'Thu',118),
 (47,'Thu',134),
 (48,'Thu',128),
 (49,'Thu',144),
 (50,'Fri',452),
 (51,'Fri',448),
 (52,'Fri',449),
 (53,'Fri',450),
 (54,'Fri',451),
 (55,'Fri',447),
 (56,'Fri',130),
 (57,'Fri',507),
 (58,'Fri',505),
 (59,'Fri',509),
 (60,'Fri',506),
 (61,'Fri',510),
 (62,'Fri',508),
 (63,'Fri',511),
 (64,'Fri',842),
 (65,'Fri',838),
 (66,'Fri',837),
 (67,'Fri',840),
 (68,'Fri',839),
 (69,'Fri',841),
 (70,'Sat',452),
 (71,'Sat',451),
 (72,'Sat',450),
 (73,'Sat',129),
 (74,'Sat',135),
 (75,'Sat',149),
 (76,'Sat',148),
 (77,'Sat',125),
 (78,'Sat',122),
 (79,'Sat',158),
 (80,'Sat',134),
 (81,'Sat',151),
 (82,'Sat',144),
 (83,'Sat',154),
 (84,'Sat',127),
 (85,'Sat',116),
 (86,'Sat',842),
 (87,'Sat',131),
 (88,'Sat',153),
 (89,'Sat',133),
 (90,'Sat',126),
 (91,'Sat',132),
 (92,'Sat',115),
 (93,'Sat',357);
/*!40000 ALTER TABLE `m_delivery_route` ENABLE KEYS */;

delete from m_delivery_group;
INSERT INTO `m_delivery_group` (`REFERENCE_ID`,`DISTRICT_ID`) VALUES 
 (2001,120),
 (2001,129),
 (2001,156),
 (2001,132),
 (2001,126),
 (2001,121),
 (2001,146),
 (2001,115),
 (2001,142),
 (2001,133),
 (2001,119),
 (2001,123),
 (2001,124),
 (2001,122),
 (2001,111),
 (2002,155),
 (2002,116),
 (2002,158),
 (2002,114),
 (2002,127),
 (2002,159),
 (2002,117),
 (2002,152),
 (2002,113),
 (2002,149),
 (2002,148),
 (2002,125),
 (2002,135),
 (2003,140),
 (2003,151),
 (2003,137),
 (2003,131),
 (2003,112),
 (2003,143),
 (2003,157),
 (2003,147),
 (2004,136),
 (2004,110),
 (2004,150),
 (2004,139),
 (2004,145),
 (2004,154),
 (2004,138),
 (2004,141),
 (2004,153),
 (2004,118),
 (2004,134),
 (2004,128),
 (2004,144),
 (2005,452),
 (2005,448),
 (2005,449),
 (2005,450),
 (2005,451),
 (2005,447),
 (2005,130),
 (2005,507),
 (2005,505),
 (2005,509),
 (2005,506),
 (2005,510),
 (2005,508),
 (2005,511),
 (2005,842),
 (2005,838),
 (2005,837),
 (2005,840),
 (2005,839),
 (2005,841);
 
 --User
delete from ad_user;
INSERT into AD_USER(USER_ID, NAME, USER_NAME, PASSWORD, ROLE, ISACTIVE, CODE) VALUES
(1, 'Administrator', 'admin', '1234', 'AD', 'Y', 'ADMIN'),
(2, '����͹� ����', 'tt01', '1234', 'TT', 'Y', 'TT001'),
(3, '��������Ѳ�� ��ѧ����С��', 'van01', '1234', 'VAN', 'Y', 'VAN001'),
(4, '���͸�� �ح���', 'dd01', '1234', 'DD', 'Y', 'DD001');


