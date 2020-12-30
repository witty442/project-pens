package com.isecinc.pens.web.reportall;

public class ReportAllSpecialUtils {

 /**
  * Case  (saleNalysis qty =CTN )  :(BME System Qty =EA)
  * Report in BME System Convert (SA) CTN to EA
  *  1: 481623 :IPCM (mask) 1(CTN)*120(EA)
  */

	public static StringBuffer genSQLSumSpecialProduct(String columnName,String sumName){
		StringBuffer sql = new StringBuffer("");
		sql.append("\n /** Special Case :convert CTN(SA) TO EA(BME) **/");
		sql.append("\n NVL(SUM( ");
		sql.append("\n       CASE WHEN P.inventory_item_code ='481623' THEN NVL("+columnName+",0)*120");
		sql.append("\n            ELSE "+columnName+" END");
		sql.append("\n ) ,0)  as "+sumName);
		
		return sql;
	}
}
