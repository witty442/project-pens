CREATE OR REPLACE VIEW M_Sales_Target_New_v
as SELECT stn.* , pd.Code as ProductCode
FROM M_Sales_Target_New stn
INNER JOIN M_Product pd ON (stn.Product_ID = pd.Product_ID AND stn.UOM_ID = pd.UOM_ID);

