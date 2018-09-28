package com.isecinc.pens.report.salesanalyst.helper;

import util.Utils;

import com.isecinc.pens.report.salesanalyst.SABean;

public class ExternalCondition {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static String genIncludePos1(SABean saBean) {
		String exSQL = "";
		try{
			System.out.println("includePos:"+Utils.isNull(saBean.getIncludePos()));
			
			if(Utils.isNull(saBean.getIncludePos()).equals("N")){
				
			    exSQL ="AND BRAND_GROUP IN ( SELECT BRAND_GROUP_NO FROM XXPENS_BI_MST_BRAND_GROUP WHERE (TYPE IS NULL OR TYPE ='offtake' ) ) \n";
			    
			}else if(Utils.isNull(saBean.getIncludePos()).equals("Y")){
				
				exSQL ="AND BRAND_GROUP IN ( SELECT BRAND_GROUP_NO FROM XXPENS_BI_MST_BRAND_GROUP WHERE (TYPE IS NULL OR TYPE ='pos' ) ) \n";
			   
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return exSQL;
	}

}
