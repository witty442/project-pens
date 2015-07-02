alter table t_order
add column(
    print_DateTime_Pick decimal(20,6),
    print_Count_Pick int,
	
    print_DateTime_Rcp decimal(20,6),
    print_Count_Rcp int
);

INSERT INTO pens.c_reference
	(REFERENCE_ID, CODE, NAME, DESCRIPTION, VALUE, ISACTIVE)
VALUES 
	(2401, 'C4_PROM_GOODS', 'C4_PROM_GOODS', 'C4_PROM_GOODS Method(1[old],2[NEW])', '2', 'Y');