select GROUP_CONCAT(DISTINCT column_name SEPARATOR ',')
from (
SELECT CONCAT('"',UPPER(column_name),'"') as column_name , data_type
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'penstest'
AND TABLE_NAME = 't_shipment'
) a;


select concat('private ',a.data_type,' ',a.column_name,';') as attr_class
from (
SELECT case col.column_key when 'PRI' then 'id'
        else replace(col.column_name,'_',' ') end as column_name
      ,case
        when col.data_type='int' then 'int'
        when col.data_type='varchar' then 'String'
        when col.data_type='date' then 'String'
        when col.data_type='decimal' then 'double'
        when col.data_type='timestamp' then 'String'
        else 'String'
       end as data_type
FROM INFORMATION_SCHEMA.COLUMNS col
WHERE TABLE_SCHEMA = 'penstest'
AND TABLE_NAME = 't_taxinvoice_line'
) a
;



