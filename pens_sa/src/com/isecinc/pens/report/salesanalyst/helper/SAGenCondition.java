package com.isecinc.pens.report.salesanalyst.helper;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.salesanalyst.SABean;
import com.isecinc.pens.report.salesanalyst.SAInitial;
import com.pens.util.ControlCode;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class SAGenCondition {
	
	protected static  Logger logger = Logger.getLogger("PENS");
	SAUtils reportU = new SAUtils();
    boolean getOldVersion = true;
	public static void main(String[] a){
		try{
			genWhereCondSQLCaseByYEAR("INVOICE","'2017','2015'");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
   /** WIT Say:Wait for Case Month year difference Not work 21/06/2019 **/
	public static String genWhereCondSQLCaseByMonthYearMain(String tab,String schema,String type,String colGroupName){
		 String sql = "";
		 boolean getNewVersion = ControlCode.canExecuteMethod("SAGenCondition", "genWhereCondSQLCaseByMonthYearNewVersion");
		 if("ORDER".equalsIgnoreCase(type)){
			 if( getNewVersion){
				 //new version 2 gen month to date
				 String[] dateReturnArr =  genDateFromCondMonthYear(colGroupName);
				 String minDateStr  = dateReturnArr[0];
				 String maxDateStr = dateReturnArr[dateReturnArr.length-1];
				 sql  = tab+" AND sales_order_date >= to_date('"+minDateStr+"','yyyymmdd') \n";
				 sql += tab+" AND sales_order_date <= to_date('"+maxDateStr+"','yyyymmdd') \n";
				 
			 }else{
				 //orginal version
				 sql = tab+" AND sales_order_year || sales_order_month  IN("+colGroupName+")";
			 }
		 }else{
			 if(getNewVersion){
				//new version 2 gen month to date
				 String[] dateReturnArr =  genDateFromCondMonthYear(colGroupName);
				 String minDateStr  = dateReturnArr[0];
				 String maxDateStr = dateReturnArr[dateReturnArr.length-1];
				 sql  = tab+" AND INVOICE_DATE >= to_date('"+minDateStr+"','yyyymmdd') \n";
				 sql += tab+" AND INVOICE_DATE <= to_date('"+maxDateStr+"','yyyymmdd') \n";
			 }else{
				//orginal version
				 sql = tab+" AND invoice_YEAR || invoice_month  IN("+colGroupName+")";
			 }
		 }
		 return sql;
	 }
	
	 public static String genWhereCondSQLCaseByMonthYear(String tab,String schema,String type,String colGroupName){
		 String sql = "";
		 boolean getNewVersion = ControlCode.canExecuteMethod("SAGenCondition", "genWhereCondSQLCaseByMonthYearNewVersion");
		 if("ORDER".equalsIgnoreCase(type)){
			 if(getNewVersion){
				 //new vision 2 gen month to date
				 String[] dateReturnArr =  genDateFromCondMonthYear(colGroupName);
				 String minDateStr  = dateReturnArr[0];
				 String maxDateStr = dateReturnArr[dateReturnArr.length-1];
				 sql  = tab+" AND sales_order_date >= to_date('"+minDateStr+"','yyyymmdd') \n";
				 sql += tab+" AND sales_order_date <= to_date('"+maxDateStr+"','yyyymmdd') \n";
			 }else{
				 //orginal version
				 sql = tab+" AND sales_order_year || sales_order_month  IN("+colGroupName+")"; 
			 }
		 }else{
			 if(getNewVersion){
				 //new version 2 gen month to date
				 String[] dateReturnArr =  genDateFromCondMonthYear(colGroupName);
				 String minDateStr  = dateReturnArr[0];
				 String maxDateStr = dateReturnArr[dateReturnArr.length-1];
				 sql  = tab+" AND INVOICE_DATE >= to_date('"+minDateStr+"','yyyymmdd') \n";
				 sql += tab+" AND INVOICE_DATE <= to_date('"+maxDateStr+"','yyyymmdd') \n";
			 }else{
				//orginal version
				 sql = tab+" AND invoice_year || invoice_month  IN("+colGroupName+")";
			 }
		 }
		 return sql;
	 }
	 
     /**
      * String[2] 
      * @param allMonthYear
      * @return String[0]= minDate ,String[1]=maxDate
      */
     public static String[] genDateFromCondMonthYear(String allYYYYMM) {
    	
	    String[] dateReturnArr = null;
	    int[] dateReturnArrSort = null;
	    String yyyymmdd = "";
    	Date date = null;
    	int indexArr = 0;
    	try{
    		logger.debug("allYYYYMM:"+allYYYYMM);
    		allYYYYMM = allYYYYMM.replaceAll("'", "");
    		
	    	if(allYYYYMM.indexOf(",") != -1){
		    	String[] allYYYYMM_ARR = allYYYYMM.split("\\,");
		    	 dateReturnArr = new String[allYYYYMM_ARR.length*2];
		    	 dateReturnArrSort = new int[allYYYYMM_ARR.length*2];
		    	 
		    	if(allYYYYMM_ARR.length >0){
		          for(int i=0;i<=allYYYYMM_ARR.length-1;i++){
		        	  //minDateOfMonth
		        	  yyyymmdd = allYYYYMM_ARR[i]+"01";
		        	  dateReturnArrSort[indexArr] = Integer.parseInt(yyyymmdd);
		        	  dateReturnArr[indexArr] = yyyymmdd;indexArr++;
		        	  logger.debug("minDateOfMonth:"+yyyymmdd);
		        	  
		        	  //maxDateOfMonth
		        	  date = DateUtil.parse(yyyymmdd, "yyyyMMdd");
		        	  yyyymmdd = allYYYYMM_ARR[i]+DateUtil.getMaxDayOfMonth(date);
		        	  dateReturnArrSort[indexArr] = Integer.parseInt(yyyymmdd);
		        	  dateReturnArr[indexArr] = yyyymmdd;indexArr++;
		        	  
		        	  logger.debug("maxDateOfMonth:"+yyyymmdd);
		          }//for
		          
		          //Sort Array of Mast min to max
		          Arrays.sort(dateReturnArrSort);
		          
		    	}//if
	    	}else{
	    		dateReturnArr = new String[2];
	    		
	    		//minDateOfMonth
	        	yyyymmdd = allYYYYMM+"01";
	        	dateReturnArr[indexArr] = yyyymmdd;indexArr++;
	        	logger.debug("minDateOfMonth:"+yyyymmdd);
	        	  
	        	//maxDateOfMonth
	        	date = DateUtil.parse(yyyymmdd, "yyyyMMdd");
	        	yyyymmdd = allYYYYMM+DateUtil.getMaxDayOfMonth(date);
	        	dateReturnArr[indexArr] = yyyymmdd;indexArr++;
	        	  
	        	logger.debug("maxDateOfMonth:"+yyyymmdd);
	    	}
    	}catch(Exception e){
    		logger.error(e.getMessage(),e);
    	}
	    return dateReturnArr;
	 }
     
     public static String genWhereCondSQLCaseByQuarterYearMain(String tab,String schema,String type,String colGroupName){
    	 String sql = "";
    	 boolean getNewVersion = ControlCode.canExecuteMethod("SAGenCondition", "genWhereCondSQLCaseByMonthYearNewVersion");
    	 if("ORDER".equalsIgnoreCase(type)){
    		 if(getNewVersion){
    			 //new vision 2 gen month to date
				 String[] dateReturnArr =  genDateFromCondQuarterYear(colGroupName);
				 String minDateStr  = dateReturnArr[0];
				 String maxDateStr = dateReturnArr[dateReturnArr.length-1];
				 sql  = tab+" AND sales_order_date >= to_date('"+minDateStr+"','yyyymmdd') \n";
				 sql += tab+" AND sales_order_date <= to_date('"+maxDateStr+"','yyyymmdd') \n";
    		 }else{
    			 //orginal version
			    sql = tab+" AND sales_order_year||sales_order_quarter IN ("+colGroupName+")";
    		 }
		 }else{
			 if(getNewVersion){
				 //new version 2 gen month to date
				 String[] dateReturnArr =  genDateFromCondQuarterYear(colGroupName);
				 String minDateStr  = dateReturnArr[0];
				 String maxDateStr = dateReturnArr[dateReturnArr.length-1];
				 sql  = tab+" AND INVOICE_DATE >= to_date('"+minDateStr+"','yyyymmdd') \n";
				 sql += tab+" AND INVOICE_DATE <= to_date('"+maxDateStr+"','yyyymmdd') \n";
			 }else{
				//orginal version
				 sql = tab+" AND invoice_year||invoice_quarter IN ("+colGroupName+")";  
			 }
		 }//if
    	 return sql;
	 }
	
     public static String genWhereCondSQLCaseByQuarterYear(String tab,String schema,String type,String colGroupName){
    	 String sql = "";
    	 boolean getNewVersion = ControlCode.canExecuteMethod("SAGenCondition", "genWhereCondSQLCaseByMonthYearNewVersion");
    	 if("ORDER".equalsIgnoreCase(type)){
    		 if(getNewVersion){
    			 //new vision 2 gen month to date
				 String[] dateReturnArr =  genDateFromCondQuarterYear(colGroupName);
				 String minDateStr  = dateReturnArr[0];
				 String maxDateStr = dateReturnArr[dateReturnArr.length-1];
				 sql  = tab+" AND sales_order_date >= to_date('"+minDateStr+"','yyyymmdd') \n";
				 sql += tab+" AND sales_order_date <= to_date('"+maxDateStr+"','yyyymmdd') \n";
    		 }else{
    			 //orginal version
			     sql = tab+" AND sales_order_year||sales_order_quarter IN ("+colGroupName+")";
    		 }
		 }else{
			 if(getNewVersion){
				 //new version 2 gen month to date
				 String[] dateReturnArr =  genDateFromCondQuarterYear(colGroupName);
				 String minDateStr  = dateReturnArr[0];
				 String maxDateStr = dateReturnArr[dateReturnArr.length-1];
				 sql  = tab+" AND INVOICE_DATE >= to_date('"+minDateStr+"','yyyymmdd') \n";
				 sql += tab+" AND INVOICE_DATE <= to_date('"+maxDateStr+"','yyyymmdd') \n";
			 }else{
				//orginal version
				 sql = tab+" AND invoice_year||invoice_quarter IN ("+colGroupName+")"; 
			 }
		 }//if
    	 return sql;
	 }
     
     
     /**
      * String[2] 
      * @param allYYYYQQ
      * @return String[0]= minDate ,String[1]=maxDate
      */
     public static String[] genDateFromCondQuarterYear(String allYYYYQQ) {
    	
	    String[] dateReturnArr = null;
	    int[] dateReturnArrSort = null;
	    String yyyymmdd = "";
    	Date date = null;
    	int indexArr = 0;
    	String yyyy = "";
    	String[] mm = null;
    	String qq="";
    	try{
    		logger.debug("allYYYYQQ:"+allYYYYQQ);
    		allYYYYQQ = allYYYYQQ.replaceAll("'", "");
    		
	    	if(allYYYYQQ.indexOf(",") != -1){
		    	String[] allYYYYQQ_ARR = allYYYYQQ.split("\\,");
		    	 dateReturnArr = new String[allYYYYQQ_ARR.length*2];
		    	 dateReturnArrSort = new int[allYYYYQQ_ARR.length*2];
		    	 
		    	if(allYYYYQQ_ARR.length >0){
		          for(int i=0;i<=allYYYYQQ_ARR.length-1;i++){
		        	  //get month from Quarter
			    	  yyyy = allYYYYQQ_ARR[i].substring(0,4);
			    	  qq = allYYYYQQ_ARR[i].substring(4,5);
			    	  mm = getMonthFromQuarter(qq);
			    		
		        	  //minDateOfMonth
		        	  yyyymmdd =yyyy+mm[0]+"01";
		        	  dateReturnArrSort[indexArr] = Integer.parseInt(yyyymmdd);
		        	  dateReturnArr[indexArr] = yyyymmdd;indexArr++;
		        	  logger.debug("minDateOfMonth:"+yyyymmdd);
		        	  
		        	  //maxDateOfMonth
		        	  date = DateUtil.parse(yyyy+mm[1]+"01", "yyyyMMdd");
		        	  yyyymmdd = yyyy+mm[1]+DateUtil.getMaxDayOfMonth(date);
		        	  dateReturnArrSort[indexArr] = Integer.parseInt(yyyymmdd);
		        	  dateReturnArr[indexArr] = yyyymmdd;indexArr++;
		        	  
		        	  logger.debug("maxDateOfMonth:"+yyyymmdd);
		          }//for
		          
		          //Sort Array of Mast min to max
		          Arrays.sort(dateReturnArrSort);
		          
		    	}//if
	    	}else{
	    		dateReturnArr = new String[2];
	    		//get month from Quarter
	    		yyyy = allYYYYQQ.substring(0,4);
	    		qq = allYYYYQQ.substring(4,5);
	    		mm = getMonthFromQuarter(qq);
	    		
	    		//logger.debug("mm[0]"+mm[0]);
	    		//logger.debug("mm[1]"+mm[1]);
	    		
	    		//minDateOfMonth
	        	yyyymmdd = yyyy+mm[0]+"01";
	        	dateReturnArr[indexArr] = yyyymmdd;indexArr++;
	        	logger.debug("minDateOfMonth:"+yyyymmdd);
	        	  
	        	//maxDateOfMonth
	        	date = DateUtil.parse(yyyy+mm[1]+"01", "yyyyMMdd");
	        	yyyymmdd = yyyy+mm[1]+DateUtil.getMaxDayOfMonth(date);
	        	dateReturnArr[indexArr] = yyyymmdd;indexArr++;
	        	  
	        	logger.debug("maxDateOfMonth:"+yyyymmdd);
	    	}
    	}catch(Exception e){
    		logger.error(e.getMessage(),e);
    	}
	    return dateReturnArr;
	 }
     
     /**
      * Get Month By Quarter
      * @param quarter
      * @return string[0] = minMonthOfQyarter ,String[1] maxMonthOfQuater
      */
     public static String[] getMonthFromQuarter(String quarter){
    	 String[] mm = new String[2];
    	 if(quarter.equals("1")){
    		 mm[0] = "01";
    		 mm[1] = "03";
    	 }else  if(quarter.equals("2")){
    		 mm[0] = "04";
    		 mm[1] = "06";
    	 }else  if(quarter.equals("3")){
    		 mm[0] = "07";
    		 mm[1] = "09";
    	 }else  if(quarter.equals("4")){
    		 mm[0] = "10";
    		 mm[1] = "12";
    	 }
    	 return mm;
     }
     
     
	 public static String genWhereCondSQLCaseByYEAR(String typeColumn,String allYear){
	    	String sql = "";
	    	logger.debug("allYear:"+allYear);
	    	if(allYear.indexOf(",") != -1){
		    	String[] yearAllArr = allYear.split("\\,");
		    	logger.debug("yearAllArr length:"+yearAllArr.length);
		    	//sort year asc
		    	if(yearAllArr.length >0){
		    	  int[] yearAllArrInt = new int[yearAllArr.length];
		          for(int i=0;i<yearAllArr.length;i++){
		        	  yearAllArrInt[i] = Utils.convertStrToInt(yearAllArr[i].replaceAll("'", ""));
		        	  logger.debug("yearAllArrInt["+i+"]["+yearAllArrInt[i]+"]");
		          }
		          Arrays.sort(yearAllArrInt);
		          
		         /* for (int i = 0; i < yearAllArrInt.length; i++) {
		        	   System.out.println(yearAllArrInt[i]);
		        	 };
		        	 */
		        	 
		        	if("INVOICE".equalsIgnoreCase(typeColumn)){
		 	    		sql +="\t AND INVOICE_DATE >=TO_DATE('0101"+yearAllArrInt[0]+"','ddmmyyyy') \n"; //MinYear
		 	    		sql +="\t\t AND INVOICE_DATE <=TO_DATE('3112"+yearAllArrInt[yearAllArrInt.length-1]+"','ddmmyyyy') \n"; //MinYear
		 	    	}else{
		 	    		sql +="\t AND ORDERED_DATE >=TO_DATE('0101"+yearAllArrInt[0]+"','ddmmyyyy') \n"; //MinYear
		 	    		sql +="\t\t AND ORDERED_DATE <=TO_DATE('3112"+yearAllArrInt[yearAllArrInt.length-1]+"','ddmmyyyy') \n"; //MinYear
		 	    	}
		    	}
	    	}else{
	    		allYear = allYear.replaceAll("'", "");
	    		if("INVOICE".equalsIgnoreCase(typeColumn)){
	 	    		sql +="\t AND INVOICE_DATE >=TO_DATE('0101"+allYear+"','ddmmyyyy') \n"; //MinYear
	 	    		sql +="\t\t AND INVOICE_DATE <=TO_DATE('3112"+allYear+"','ddmmyyyy') \n"; //MinYear
	 	    	}else{
	 	    		sql +="\t AND ORDERED_DATE >=TO_DATE('0101"+allYear+"','ddmmyyyy') \n"; //MinYear
	 	    		sql +="\t\t AND ORDERED_DATE <=TO_DATE('3112"+allYear+"','ddmmyyyy') \n"; //MinYear
	 	    	}
	    	}
	    	return sql;
	   }
	 
    public static String genGroupBySQL(String groupBy){
    	String groupBySQl = groupBy;
    	if("SUBBRAND".equalsIgnoreCase(groupBy)){
    		groupBySQl +=",INVENTORY_ITEM_ID"; 
    	}
    	return groupBySQl;
    }
	
	public  String genSqlWhereCondition(SABean salesBean) throws Exception{
		return genSqlWhereCondition(salesBean,"");
	}
	
	//aliasSub =SS. (select * from table)SS
	public  String genSqlWhereCondition(SABean salesBean,String aliasSub) throws Exception{
		String sql = "";
		
		//  change  "0" to "-1" for fix condition data
		if( !"-1".equals(Utils.isNull(salesBean.getCondName1())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue1()))){
			if(Utils.isNull(salesBean.getCondName1()).equalsIgnoreCase("invoice_date")|| Utils.isNull(salesBean.getCondName1()).equalsIgnoreCase("SALES_ORDER_DATE")){
				logger.debug("CondValue1:"+salesBean.getCondValue1());
				Date date = DateUtil.parseToBudishDate(salesBean.getCondValue1(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				sql +=" and  "+aliasSub+salesBean.getCondName1()+" = to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n";
			
			}else if(Utils.isNull(salesBean.getCondName1()).equalsIgnoreCase("BrandXX")){
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName1()))){
					sql +="and ( ";
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName1())+" IN ("+ SAUtils.converToText(salesBean.getCondName1(), Utils.isNull(salesBean.getCondValue1()))+") \n ";
					sql +="     or \n";
					// Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName1())+" IN ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no in ("+ SAUtils.converToText(salesBean.getCondName1(), Utils.isNull(salesBean.getCondValue1()))+") ) \n ";
					sql +=" ) \n";
				}else{
					sql +="and ( ";
				    sql +="   "+aliasSub+ Utils.isNull(salesBean.getCondName1())+"='"+Utils.isNull(salesBean.getCondValue1())+"' \n ";
				    sql +="     or \n";
				   // Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName1())+" = ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no = '"+ SAUtils.converToText(salesBean.getCondName1(), Utils.isNull(salesBean.getCondValue1()))+"' ) \n ";
					sql +=" ) \n";
				}
			}else if(Utils.isNull(salesBean.getCondName1()).equalsIgnoreCase("SUBBRAND")){
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName1()))){
			       sql +="AND  INVENTORY_ITEM_ID IN ( select INVENTORY_ITEM_ID from XXPENS_BI_MST_SUBBRAND where subbrand_no in ("+ SAUtils.converToText(salesBean.getCondName1(), Utils.isNull(salesBean.getCondValue1()))+"))\n ";
				}
			}else{
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName1()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName1())+" IN ("+ SAUtils.converToText(salesBean.getCondName1(), Utils.isNull(salesBean.getCondValue1()))+") \n ";
				}else if(SAUtils.isColumnNumberType(Utils.isNull(salesBean.getCondName1()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName1())+"="+Utils.isNull(salesBean.getCondValue1())+" \n ";
				}else{
				    sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName1())+"='"+Utils.isNull(salesBean.getCondValue1())+"' \n ";
				}
			}
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName2())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue2()))){
			if(Utils.isNull(salesBean.getCondName2()).equalsIgnoreCase("invoice_date") || Utils.isNull(salesBean.getCondName2()).equalsIgnoreCase("SALES_ORDER_DATE")){
				Date date = DateUtil.parseToBudishDate(salesBean.getCondValue2(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				sql +=" and  "+aliasSub+salesBean.getCondName1()+" = to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n";
			
			}else if(Utils.isNull(salesBean.getCondName2()).equalsIgnoreCase("BrandXX")){
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName2()))){
					sql +="and ( ";
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName2())+" IN ("+ SAUtils.converToText(salesBean.getCondName2(), Utils.isNull(salesBean.getCondValue2()))+") \n ";
					sql +="     or \n";
					// Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName2())+" IN ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no in ("+ SAUtils.converToText(salesBean.getCondName2(), Utils.isNull(salesBean.getCondValue2()))+") ) \n ";
					sql +=" ) \n";
				}else{
					sql +="and ( ";
				    sql +="   "+aliasSub+ Utils.isNull(salesBean.getCondName2())+"='"+Utils.isNull(salesBean.getCondValue2())+"' \n ";
				    sql +="     or \n";
				   // Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName2())+" = ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no = '"+ SAUtils.converToText(salesBean.getCondName2(), Utils.isNull(salesBean.getCondValue2()))+"' ) \n ";
					sql +=" ) \n";
				}
			}else if(Utils.isNull(salesBean.getCondName2()).equalsIgnoreCase("SUBBRAND")){
			     //sql +="AND  INVENTORY_ITEM_ID IN ( select INVENTORY_ITEM_ID from XXPENS_BI_MST_SUBBRAND where subbrand_no = '"+ Utils.isNull(salesBean.getCondValue2())+"' ) \n ";
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName2()))){
				       sql +="AND  INVENTORY_ITEM_ID IN ( select INVENTORY_ITEM_ID from XXPENS_BI_MST_SUBBRAND where subbrand_no in ("+ SAUtils.converToText(salesBean.getCondName2(), Utils.isNull(salesBean.getCondValue2()))+")) \n ";
				}
			}else{
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName2()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName2())+" IN ("+SAUtils.converToText(salesBean.getCondName2(), Utils.isNull(salesBean.getCondValue2()))+") \n ";
				}
				else if(SAUtils.isColumnNumberType(Utils.isNull(salesBean.getCondName2()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName2())+"="+Utils.isNull(salesBean.getCondValue2())+" \n ";
				}else{
				    sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName2())+"='"+Utils.isNull(salesBean.getCondValue2())+"' \n ";
				}
			}
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName3())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue3()))){
			if(Utils.isNull(salesBean.getCondName3()).equalsIgnoreCase("invoice_date") || Utils.isNull(salesBean.getCondName3()).equalsIgnoreCase("SALES_ORDER_DATE")){
				Date date = DateUtil.parseToBudishDate(salesBean.getCondValue2(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				sql +=" and  "+aliasSub+salesBean.getCondName1()+" = to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n";
			
			}else if(Utils.isNull(salesBean.getCondName3()).equalsIgnoreCase("BrandXX")){
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName3()))){
					sql +="and ( ";
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName3())+" IN ("+ SAUtils.converToText(salesBean.getCondName3(), Utils.isNull(salesBean.getCondValue3()))+") \n ";
					sql +="     or \n";
					// Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName3())+" IN ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no in ("+ SAUtils.converToText(salesBean.getCondName3(), Utils.isNull(salesBean.getCondValue3()))+") ) \n ";
					sql +=" ) \n";
				}else{
					sql +="and ( ";
				    sql +="   "+aliasSub+ Utils.isNull(salesBean.getCondName3())+"='"+Utils.isNull(salesBean.getCondValue3())+"' \n ";
				    sql +="     or \n";
				   // Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName3())+" = ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no = '"+ SAUtils.converToText(salesBean.getCondName3(), Utils.isNull(salesBean.getCondValue3()))+"' ) \n ";
					sql +=" ) \n";
				}
			}else if(Utils.isNull(salesBean.getCondName3()).equalsIgnoreCase("SUBBRAND")){
			     //sql +="AND  INVENTORY_ITEM_ID IN ( select INVENTORY_ITEM_ID from XXPENS_BI_MST_SUBBRAND where subbrand_no = '"+ Utils.isNull(salesBean.getCondValue3())+"' ) \n ";
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName3()))){
				      sql +="AND  INVENTORY_ITEM_ID IN ( select INVENTORY_ITEM_ID from XXPENS_BI_MST_SUBBRAND where subbrand_no in ("+ SAUtils.converToText(salesBean.getCondName3(), Utils.isNull(salesBean.getCondValue3()))+")) \n ";
				}
			}else{
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName3()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName3())+" IN ("+SAUtils.converToText(salesBean.getCondName3(), Utils.isNull(salesBean.getCondValue3()))+") \n ";
				}
				else if(SAUtils.isColumnNumberType(Utils.isNull(salesBean.getCondName3()))){
					sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName3())+"="+Utils.isNull(salesBean.getCondValue3())+" \n ";
				}else{
				   sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName3())+"='"+Utils.isNull(salesBean.getCondValue3())+"' \n ";
				}
			}
		}
		if( !"-1".equals(Utils.isNull(salesBean.getCondName4())) &&  !"-1".equals(Utils.isNull(salesBean.getCondValue4()))){
			if(Utils.isNull(salesBean.getCondName4()).equalsIgnoreCase("invoice_date")|| Utils.isNull(salesBean.getCondName4()).equalsIgnoreCase("SALES_ORDER_DATE")){
				Date date = DateUtil.parseToBudishDate(salesBean.getCondValue2(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				sql +=" and  "+aliasSub+salesBean.getCondName1()+" = to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n";
			
			}else if(Utils.isNull(salesBean.getCondName4()).equalsIgnoreCase("BrandXX")){
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName4()))){
					sql +="and ( ";
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName4())+" IN ("+ SAUtils.converToText(salesBean.getCondName4(), Utils.isNull(salesBean.getCondValue4()))+") \n ";
					sql +="     or \n";
					// Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName4())+" IN ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no in ("+ SAUtils.converToText(salesBean.getCondName4(), Utils.isNull(salesBean.getCondValue4()))+") ) \n ";
					sql +=" ) \n";
				}else{
					sql +="and ( ";
				    sql +="   "+aliasSub+ Utils.isNull(salesBean.getCondName4())+"='"+Utils.isNull(salesBean.getCondValue4())+"' \n ";
				    sql +="     or \n";
				   // Brand group
					sql +="    "+aliasSub+ Utils.isNull(salesBean.getCondName4())+" = ( select brand_no from XXPENS_BI_MST_BRAND_GROUP where brand_group_no = '"+ SAUtils.converToText(salesBean.getCondName4(), Utils.isNull(salesBean.getCondValue4()))+"' ) \n ";
					sql +=" ) \n";
				}
			}else if(Utils.isNull(salesBean.getCondName3()).equalsIgnoreCase("SUBBRAND")){
			    // sql +="AND  INVENTORY_ITEM_ID IN ( select INVENTORY_ITEM_ID from XXPENS_BI_MST_SUBBRAND where subbrand_no = '"+ Utils.isNull(salesBean.getCondValue3())+"' ) \n ";
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName4()))){
				      sql +="AND  INVENTORY_ITEM_ID IN ( select INVENTORY_ITEM_ID from XXPENS_BI_MST_SUBBRAND where subbrand_no in ("+ SAUtils.converToText(salesBean.getCondName4(), Utils.isNull(salesBean.getCondValue4()))+")) \n ";
				}
			}else{
				if(SAInitial.MULTI_SELECTION_LIST.contains(Utils.isNull(salesBean.getCondName4()))){
					sql +="and "+ Utils.isNull(salesBean.getCondName4())+" IN ("+SAUtils.converToText(salesBean.getCondName4(), Utils.isNull(salesBean.getCondValue4()))+") \n ";
				}
				else if(SAUtils.isColumnNumberType(Utils.isNull(salesBean.getCondName4()))){
				   sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName4())+"="+Utils.isNull(salesBean.getCondValue4())+" \n ";
				}else{
				   sql +="and "+aliasSub+ Utils.isNull(salesBean.getCondName4())+"='"+Utils.isNull(salesBean.getCondValue4())+"' \n ";
				}
			}
		}
		return sql;
	}
	
	
	public  String genSQLGetDesc(String condType)throws Exception{
		String sql = "";
		try{
			if("inventory_item_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.INVENTORY_ITEM_DESC as DESC_ from XXPENS_BI_MST_ITEM M WHERE M.INVENTORY_ITEM_ID = S.INVENTORY_ITEM_ID )";
			
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql = "(select M.cust_cat_desc as desc_ from XXPENS_BI_MST_CUST_CAT M WHERE M.cust_cat_no = S.Customer_Category)";
			
			}else if("Division".equalsIgnoreCase(condType)){
				sql = "(select M.div_desc as desc_ from XXPENS_BI_MST_DIVISION M WHERE M.div_no = S.division)";
			
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql = "(select M.salesrep_desc desc_ from XXPENS_BI_MST_SALESREP M  WHERE M.salesrep_id= S.salesrep_id)";
			
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql = "(select M.sales_channel_desc as desc_ from XXPENS_BI_MST_SALES_CHANNEL M WHERE  M.sales_channel_no= S.sales_channel)";
			
			}else if("Customer_Group".equalsIgnoreCase(condType)){
				sql = "(select M.cust_group_desc as desc_ from XXPENS_BI_MST_CUST_GROUP M WHERE M.cust_group_no= S.customer_group)";
			
			}else if("Customer_id".equalsIgnoreCase(condType)){
				sql = " (SELECT max(desc_) FROM (SELECT M.customer_desc desc_, M.customer_id  from XXPENS_BI_MST_CUSTOMER M ORDER BY m.create_date DESC ) WHERE customer_id = S.customer_id ) ";
			
			}else if("Brand_XX".equalsIgnoreCase(condType)){
				
				sql = "(case when (select M.brand_desc as desc_ from XXPENS_BI_MST_BRAND M WHERE M.brand_no = S.brand) is null ";
				sql += "     then (select max(M.brand_group_desc) as desc_ from XXPENS_BI_MST_BRAND_GROUP M WHERE M.brand_no = S.brand) end )";
				
			}else if("Brand".equalsIgnoreCase(condType)){
				sql = "(select M.brand_desc as desc_ from XXPENS_BI_MST_BRAND M WHERE M.brand_no = S.brand)";
				
			}else if("Brand_Group".equalsIgnoreCase(condType)){
				sql = "(select max(M.brand_group_desc) as desc_ from XXPENS_BI_MST_BRAND_GROUP M WHERE M.brand_group_no = S.brand_group)";	
				
			}else if("SUBBRAND".equalsIgnoreCase(condType)){
				sql = "(select max(M.subbrand_desc) as desc_ from XXPENS_BI_MST_SUBBRAND M WHERE M.INVENTORY_ITEM_ID = S.INVENTORY_ITEM_ID )";	
			
			}else if("SALES_ZONE".equalsIgnoreCase(condType)){
				sql = "(select max(M.ZONE_NAME) as desc_ from XXPENS_BI_MST_SALES_ZONE M WHERE M.ZONE = S.SALES_ZONE )";	
			
			}else if("Invoice_Date".equalsIgnoreCase(condType)){
				sql = "(select M.Invoice_Date as desc_ from XXPENS_BI_MST_INVOICE_DATE M WHERE M.INVOICE_DATE = S.INVOICE_DATE)";
			
			}else if("SALES_ORDER_DATE".equalsIgnoreCase(condType)){
				sql = "(select M.ORDER_DATE as desc_ from XXPENS_BI_MST_ORDER_DATE M WHERE M.ORDER_DATE = S.SALES_ORDER_DATE)";
			
			}else if("Province".equalsIgnoreCase(condType)){
				sql = "(select M.PROVINCE as desc_ from XXPENS_BI_MST_PROVINCE M WHERE M.PROVINCE = S.PROVINCE)";
			
			}else if("AMPHOR".equalsIgnoreCase(condType)){
				sql = "(select M.AMPHOR as desc_ from XXPENS_BI_MST_AMPHOR M WHERE M.AMPHOR =S.AMPHOR)";
			
			}else if("TAMBOL".equalsIgnoreCase(condType)){
				sql = "(select M.TAMBOL as desc_ from XXPENS_BI_MST_TAMBOL M WHERE M.TAMBOL =S.TAMBOL)";
			
			}else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
				sql = "(select  distinct M.SALES_ORDER_NO as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.SALES_ORDER_NO =S.SALES_ORDER_NO)";
			
			}else if("INVOICE_NO".equalsIgnoreCase(condType)){
				sql = "(select  distinct M.INVOICE_NO as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.INVOICE_NO =S.INVOICE_NO)";
			
			}else if("IR_AMT".equalsIgnoreCase(condType)){
			   sql = "(select  distinct M.INVOICED_AMT-M.RETURNED_AMT as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.INVOICE_NO =S.INVOICE_NO)";
			
			}else if("BILL_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "(SELECT M.CUSTOMER_BILL_TO_ADDRESS as DESC_ from XXPENS_BI_MST_CUST_BILL_ADDR M WHERE M.BILL_TO_SITE_USE_ID = S.BILL_TO_SITE_USE_ID )";
			
			}else if("SHIP_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "(SELECT M.CUSTOMER_SHIP_TO_ADDRESS as DESC_ from XXPENS_BI_MST_CUST_SHIP_ADDR M WHERE M.SHIP_TO_SITE_USE_ID = S.SHIP_TO_SITE_USE_ID )";
			
			}else if("Organization_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.ORGANIZATION_CODE as DESC_ from XXPENS_BI_MST_ORGANIZATION M WHERE M.Organization_id = S.Organization_id )";
			
			}else if("Order_type_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.order_type_name|| ' (' || M.order_type_cat || ')' as DESC_ from XXPENS_BI_MST_ORDER_TYPE M WHERE M.Order_type_id = S.Order_type_id )";
			
			}else if("CUSTOMER_CLASS_CODE".equalsIgnoreCase(condType)){
				sql = "(SELECT DESCRIPTION FROM apps.xxpens_ar_cust_class_v M WHERE M.code = S.CUSTOMER_CLASS_CODE)";
			}
			
			
			sql +=" AS "+condType+"_DESC , \n";
			
			logger.debug("SqlGetDesc:"+sql);
			return sql;
		}catch(Exception e){
		   throw e;
		}finally{
		
		}
	}
	
	public  String genSQLGetCode(String condType)throws Exception{
		String sql = "";
		try{
			if("inventory_item_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.INVENTORY_ITEM_CODE as DESC_ from XXPENS_BI_MST_ITEM M WHERE M.INVENTORY_ITEM_ID = S.INVENTORY_ITEM_ID )";
			
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql = "(select M.cust_cat_no as desc_ from XXPENS_BI_MST_CUST_CAT M WHERE M.cust_cat_no = S.Customer_Category)";
			
			}else if("Division".equalsIgnoreCase(condType)){
				sql = "(select M.div_no as desc_ from XXPENS_BI_MST_DIVISION M WHERE M.div_no = S.division)";
			
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql = "(select M.salesrep_code desc_ from XXPENS_BI_MST_SALESREP M  WHERE M.salesrep_id= S.salesrep_id)";
			
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql = "(select M.sales_channel_no as desc_ from XXPENS_BI_MST_SALES_CHANNEL M WHERE  M.sales_channel_no= S.sales_channel)";
			
			}else if("Customer_Group".equalsIgnoreCase(condType)){
				sql = "(select M.cust_group_no as desc_ from XXPENS_BI_MST_CUST_GROUP M WHERE M.cust_group_no= S.customer_group)";
			
			}else if("Customer_id".equalsIgnoreCase(condType)){
				sql = " (SELECT max(desc_) FROM (SELECT M.customer_code desc_, M.customer_id  from XXPENS_BI_MST_CUSTOMER M ORDER BY m.create_date DESC ) WHERE customer_id = S.customer_id) ";
			
			}else if("BrandXX".equalsIgnoreCase(condType)){
				
				sql = "( case when (select M.brand_no as desc_ from XXPENS_BI_MST_BRAND M WHERE M.brand_no = S.brand) is null";
				sql += "       then (select max(M.brand_group_no) as desc_ from XXPENS_BI_MST_BRAND_GROUP M WHERE M.brand_no = S.brand) end)";
				
			}else if("Brand".equalsIgnoreCase(condType)){
				sql = "(select M.brand_no as desc_ from XXPENS_BI_MST_BRAND M WHERE M.brand_no = S.brand)";
				
			}else if("Brand_Group".equalsIgnoreCase(condType)){
				sql = "(select max(M.brand_group_no) as desc_ from XXPENS_BI_MST_BRAND_GROUP M WHERE M.brand_group_no = S.brand_group)";	
				
			}else if("SUBBRAND".equalsIgnoreCase(condType)){
				sql = "(select max(M.subbrand_no) as desc_ from XXPENS_BI_MST_SUBBRAND M WHERE M.INVENTORY_ITEM_ID = S.INVENTORY_ITEM_ID)";	
			
			}else if("SALES_ZONE".equalsIgnoreCase(condType)){
				sql = "(select max(M.ZONE) as desc_ from XXPENS_BI_MST_SALES_ZONE M WHERE M.ZONE = S.SALES_ZONE )";	
			
			}else if("Invoice_Date".equalsIgnoreCase(condType)){
				sql = "(select M.Invoice_Date as desc_ from XXPENS_BI_MST_INVOICE_DATE M WHERE M.INVOICE_DATE = S.INVOICE_DATE)";
			
			}else if("SALES_ORDER_DATE".equalsIgnoreCase(condType)){
				sql = "(select M.ORDER_DATE as desc_ from XXPENS_BI_MST_ORDER_DATE M WHERE M.ORDER_DATE = S.SALES_ORDER_DATE)";
			
			}else if("Province".equalsIgnoreCase(condType)){
				sql = "(select M.PROVINCE as desc_ from XXPENS_BI_MST_PROVINCE M WHERE M.PROVINCE = S.PROVINCE)";
			
			}else if("AMPHOR".equalsIgnoreCase(condType)){
				sql = "(select M.AMPHOR as desc_ from XXPENS_BI_MST_AMPHOR M WHERE M.AMPHOR = S.AMPHOR)";
			
			}else if("TAMBOL".equalsIgnoreCase(condType)){
				sql = "(select M.TAMBOL as desc_ from XXPENS_BI_MST_TAMBOL M WHERE M.TAMBOL = S.TAMBOL)";
			
			}else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
				sql = "(select distinct M.SALES_ORDER_NO as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.SALES_ORDER_NO =S.SALES_ORDER_NO)";
			
			}else if("INVOICE_NO".equalsIgnoreCase(condType)){
				sql = "(select distinct  M.INVOICE_NO as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.INVOICE_NO =S.INVOICE_NO)";
			
			}else if("IR_AMT".equalsIgnoreCase(condType)){
				sql = "(select  distinct M.INVOICED_AMT-M.RETURNED_AMT as desc_ from XXPENS_BI_SALES_ANALYSIS M WHERE M.INVOICE_NO =S.INVOICE_NO)";
			
			}else if("BILL_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "(SELECT M.CUSTOMER_BILL_TO_ADDRESS as DESC_ from XXPENS_BI_MST_CUST_BILL_ADDR M WHERE M.BILL_TO_SITE_USE_ID = S.BILL_TO_SITE_USE_ID )";
			
			}else if("SHIP_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "(SELECT M.CUSTOMER_SHIP_TO_ADDRESS as DESC_ from XXPENS_BI_MST_CUST_SHIP_ADDR M WHERE M.SHIP_TO_SITE_USE_ID = S.SHIP_TO_SITE_USE_ID )";
			
			}else if("Organization_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.ORGANIZATION_CODE as DESC_ from XXPENS_BI_MST_ORGANIZATION M WHERE M.Organization_id = S.Organization_id )";
			
			}else if("Order_type_id".equalsIgnoreCase(condType)){
				sql = "(SELECT M.order_type_id as DESC_ from XXPENS_BI_MST_ORDER_TYPE M WHERE M.Order_type_id = S.Order_type_id )";
			
			}else if("CUSTOMER_CLASS_CODE".equalsIgnoreCase(condType)){
				sql = "CUSTOMER_CLASS_CODE";
			}
			
			sql +=" AS "+condType+"_CODE , \n";
			
			logger.debug("SqlGetDesc:"+sql);
			return sql;
		}catch(Exception e){
		   throw e;
		}finally{
		
		}
	}
	

	/**
	 * 
	 * @param type  ORDER || INVOICE
	 * @param salesBean
	 * @return 
	 * @throws Exception
	 */
	
	public StringBuffer genSqlWhereCondByGroup(SABean salesBean,String type,String alias,String colGroupName) throws Exception{
		StringBuffer sql = new StringBuffer("");

		/** Where Condition By Group  **/
		if(SAInitial.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
		    sql.append(" AND (1=1 AND "+alias+"invoice_year||"+alias+"invoice_month = '"+colGroupName+"' ) \n");
		    //sql.append(" AND invoice_year IN('"+salesBean.getYear()+"') \n");
		}else if(SAInitial.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
			sql.append(" AND (1=1 AND "+alias+"invoice_quarter = '"+colGroupName+"' \n");
			sql.append(" AND "+alias+"invoice_year IN('"+salesBean.getYear()+"') ) \n");
		}else if(SAInitial.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
			sql.append(" AND (1=1 AND "+alias+"invoice_year = '"+colGroupName+"' ) \n");
		}else{
			 Date date = DateUtil.parseToBudishDate(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH);
			 sql.append(" AND (1=1 AND  "+alias+"INVOICE_DATE = to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy') ) \n");
		}
		return sql;
	}
	
	public  String genSubSQLByType(Connection conn,User user ,SABean salesBean,String type,String groupBy,String colGroupName) throws Exception{
		StringBuffer sql = new StringBuffer("");
		
		//sql.append("/*** genSubSQLByType **/ \n");
		
		if("ORDER".equalsIgnoreCase(type)){
			sql.append("\t\t"+" SELECT \n");
			sql.append("\t\t"+ genSQLSelectColumn(salesBean,type,groupBy,colGroupName)+"\n");
			sql.append("\t\t"+" '1' AS A \n");
			sql.append("\t\t"+" FROM "+SAInitial.TABLE_VIEW+" V \n");
			sql.append("\t\t"+" WHERE 1=1 \n ");
			sql.append("\t\t"+" AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
			/** Condition Filter **/
			sql.append(genSqlWhereCondition(salesBean));
			
			/** Where Condition By Group  **/
			if(SAInitial.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
			    //sql.append(""+" AND sales_order_month = '"+colGroupName.substring(4,6)+"' \n");
			    //sql.append("\t\t"+" AND sales_order_year = '"+colGroupName.substring(0,4)+"' \n");
			    
			    sql.append(SAGenCondition.genWhereCondSQLCaseByMonthYear("\t\t","","ORDER","'"+colGroupName+"'"));
			    
			}else if(SAInitial.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				//sql.append(""+" AND sales_order_quarter = '"+colGroupName.substring(4,5)+"' \n");
				//sql.append("\t\t"+" AND sales_order_year = '"+colGroupName.substring(0,4)+"' \n");
				
				sql.append(SAGenCondition.genWhereCondSQLCaseByQuarterYear("\t\t","","ORDER","'"+colGroupName+"'"));
				
			}else if(SAInitial.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				//sql.append(""+" AND sales_order_year = '"+colGroupName+"' \n");
				sql.append("\t"+SAGenCondition.genWhereCondSQLCaseByYEAR("ORDER",colGroupName));
			}else{
				Date date = DateUtil.parseToBudishDate(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo() , DateUtil.DD_MM_YYYY_WITH_SLASH);
				sql.append("\t\t"+"AND  SALES_ORDER_DATE = to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n"); 
			}
			
			//Include Pos or not
			//sql.append(ExternalCondition.genIncludePos(salesBean));
		}else{
			sql.append("\t\t"+ "SELECT \n");
			sql.append("\t\t"+ genSQLSelectColumn(salesBean,type,groupBy,colGroupName));
			sql.append("\t\t"+" '1' AS A \n");
			sql.append("\t\t"+" FROM "+SAInitial.TABLE_VIEW+" V \n");
			sql.append("\t\t"+" WHERE 1=1 \n ");
			sql.append("\t\t"+" AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
			/** Condition Filter **/
			sql.append("\t\t"+  genSqlWhereCondition(salesBean));
			
			/** Where Condition By Group  **/
			if(SAInitial.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
			   // sql.append(""+" AND invoice_month = '"+colGroupName.substring(4,6)+"' \n");
			   // sql.append("\t\t"+" AND invoice_year = '"+colGroupName.substring(0,4)+"' \n");
				
				 sql.append(SAGenCondition.genWhereCondSQLCaseByMonthYear("\t\t","","INVOICE","'"+colGroupName+"'"));
			}else if(SAInitial.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				//sql.append(""+" AND invoice_quarter = '"+colGroupName.substring(4,5)+"' \n");
				//sql.append("\t\t"+" AND invoice_year = '"+colGroupName.substring(0,4)+"' \n");
				
				 sql.append(SAGenCondition.genWhereCondSQLCaseByQuarterYear("\t\t","","INVOICE","'"+colGroupName+"'"));
				 
			}else if(SAInitial.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				//sql.append(""+" AND invoice_year = '"+colGroupName+"' \n");
				sql.append("\t"+SAGenCondition.genWhereCondSQLCaseByYEAR("INVOICE",colGroupName));
			}else{
				Date date = DateUtil.parseToBudishDate(!StringUtils.isEmpty(salesBean.getDay())?salesBean.getDay():salesBean.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				sql.append("\t\t"+" AND  INVOICE_DATE = to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
			}
			
			//Include Pos or not
			//sql.append(ExternalCondition.genIncludePos(salesBean));
		}
		
		/** Filter Displsy Data By User **/
		sql.append("\t\t"+SecurityHelper.genWhereSqlFilterByUser(conn,user, null,"V."));
	
		return sql.toString();
	}
	
	/**
	 * 
	 * @param salesBean
	 * @param groupBy
	 * @param colGroupName
	 * @return
	 * @throws Exception
	 * Desc: gen sql count customer 
	 */
	public  StringBuffer genSqlCountCustomerNoDup(SABean salesBean,String groupBy,String colGroupName,String sqlNotInCaseCall) throws Exception{
		StringBuffer ss = new StringBuffer("");
		
		ss.append("\n NVL( ");
		ss.append("\n  ( SELECT COUNT(DISTINCT c.c_id) as c_id FROM( ");
		ss.append("\n \t  SELECT DISTINCT c.customer_id as c_id ,c."+groupBy+" from "+SAInitial.TABLE_VIEW+" c " );
		ss.append("\n \t      WHERE 1=1 " );
		ss.append("      \t "+genSqlWhereCondition(salesBean,"c."));
		ss.append("      \t "+genSqlWhereCondByGroup(salesBean,"INVOICE","c.",colGroupName));
		
		//Case No Count Promotion Product (invoiced_amt = 0)
		ss.append("      \t AND c.invoiced_amt <> 0");
		
		if( !Utils.isNull(sqlNotInCaseCall).equals(""))
		   ss.append("       \n "+sqlNotInCaseCall);
		ss.append("\n   ) c ");
		ss.append("\n   WHERE c."+groupBy+" = SS."+groupBy );
		ss.append("\n ) ");
		ss.append(",0) \n" );
		
		return ss;
	}
	  
	public  StringBuffer genSqlCountCustomer(SABean salesBean,String groupBy,String colGroupName) throws Exception{
		StringBuffer ss = new StringBuffer("");
		
		ss.append("\n NVL( ");
		ss.append("\n  ( SELECT COUNT(DISTINCT c.c_id) as c_id FROM( ");
		ss.append("\n \t  SELECT DISTINCT c.customer_id as c_id,c."+groupBy+" from "+SAInitial.TABLE_VIEW+" c " );
		ss.append("\n \t      WHERE 1=1 " );
		ss.append("      \t "+genSqlWhereCondition(salesBean,"c."));
		ss.append("      \t "+genSqlWhereCondByGroup(salesBean,"INVOICE","c.",colGroupName));
		
		//Case No Count Promotion Product (invoiced_amt = 0)
		ss.append("      \t AND c.invoiced_amt <> 0");
				
		ss.append("\n   ) c ");
		ss.append("\n   WHERE c."+groupBy+" = SS."+groupBy );
		ss.append("\n ) ");
		ss.append(",0) \n" );
		
		return ss;
	}
	
	public  StringBuffer genSqlMinusCustomerId(SABean salesBean,List<StringBuffer> sqlNotInCaseCallAllList) throws Exception{
		StringBuffer ss = new StringBuffer("");
		//ss.append("\n      AND (c.customer_id,"+salesBean.getGroupBy()+") NOT IN ( " );
		ss.append("\n      AND NOT EXISTS ( " );
		ss.append("\n      SELECT DISTINCT A.c_id as c_id ,A."+salesBean.getGroupBy()+" FROM( " );
		
		ss.append("\n      SELECT 0 as c_id,NULL as "+salesBean.getGroupBy()+"  FROM DUAL ");
		for(int i=0;i<sqlNotInCaseCallAllList.size();i++){
			StringBuffer condNotInAll =(StringBuffer)sqlNotInCaseCallAllList.get(i);
			ss.append("\n      UNION ALL ");
			ss.append("\n      SELECT distinct c.customer_id as c_id ,c."+salesBean.getGroupBy()+" from "+SAInitial.TABLE_VIEW+" c " );
			ss.append("\n      WHERE 1=1 " );
			ss.append("\n      "+condNotInAll );
			ss.append("\n      "+genSqlWhereCondition(salesBean,"c."));
	     }
		ss.append("\n     )A " );
		ss.append("\n     WHERE A.c_id = c.customer_id AND c."+salesBean.getGroupBy()+"= A."+salesBean.getGroupBy());
		ss.append("\n   ) " );
		return ss;
	}

	/**
	 * genTopSQLSelectColumn
	 * @param salesBean
	 * @param groupBy
	 * @param colGroupName
	 * @return String[2]  0->columnTop ,1->columnALL
	 * @throws Exception
	 * 
	 */
	
	public  String[] genTopSQLSelectColumn(SABean salesBean,String groupBy,String colGroupName) throws Exception{
		return genTopSQLSelectColumn(salesBean, groupBy, colGroupName,"");
	}
	
	/**
	 * genTopSQLSelectColumn
	 * @param salesBean
	 * @param groupBy
	 * @param colGroupName
	 * @return String[2]  0->columnTop ,1->columnALL
	 * @throws Exception
	 * 
	 */
	public  String[] genTopSQLSelectColumn(SABean salesBean,String groupBy,String colGroupName,String sqlNotIncaseCall) throws Exception{
		StringBuffer columnAll = new StringBuffer("");
		StringBuffer columnTop = new StringBuffer("");
		String result[] = new String[2];
		String columnName1="";
		String columnName2="";
		String columnName3="";
		String columnName4="";
		
		String subSelect1 ="";
		String subSelect2 ="";
		String subSelect3 ="";
		String subSelect4 ="";
		
		logger.debug("genTopSQLSelectColumn:groupBy["+groupBy+"]:colGroupName["+colGroupName+"]");
		
		colGroupName = reportU.getShortColName(colGroupName);
			
		columnTop.append(genGroupBySQL(groupBy) +", ");			
		columnAll.append(groupBy +", ");;
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			
			if("CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1()))){
				subSelect1 = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				columnName1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName;	

				/** Gen Column Top **/
				columnTop.append( subSelect1+" as "+columnName1+",  ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName1+", ");		
	
			}else if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1()))){
				subSelect1 = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				columnName1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName;	
				
				String subSelectNoDup = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				String columnNameNoDup = SAInitial.NO_DUP_PREFIX+salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName;
				
				/** Gen Column Top **/
				columnTop.append( subSelect1+" as "+columnName1+",  ");
				columnTop.append( subSelectNoDup+" as "+columnNameNoDup+", ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName1+",  ");
				columnAll.append( columnNameNoDup+", ");
				
			}else{
				subSelect1 = "NVL(sum("+salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName+"),0)" ;
				columnName1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect1+" as "+columnName1+",  ");
				columnAll.append( columnName1+",  ");
			}
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
					
			if("CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2()))){
				subSelect2 = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				columnName2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName;
				
				/** Gen Column Top **/
				columnTop.append( subSelect2+" as "+columnName2+",  ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName2+",  ");
							
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
					columnTop.append("(CASE WHEN "+subSelect1+" <> 0");
					columnTop.append(" THEN ("+ subSelect2 +"/"+subSelect1 +")*100 ");
					columnTop.append(" ELSE 0 END )  as PER1_"+colGroupName +",   ");
				
					columnAll.append(" PER1_"+colGroupName+", ");;
				}
				
			}else if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2()))){
				subSelect2 = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				columnName2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName;	
				
				String subSelectNoDup = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				String columnNameNoDup = SAInitial.NO_DUP_PREFIX+salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName;
					
				/** Gen Column Top **/
				columnTop.append( subSelect2+" as "+columnName2+",  ");
				columnTop.append( subSelectNoDup+" as "+columnNameNoDup+", ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName2+",  ");
				columnAll.append( columnNameNoDup+",  ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
					columnTop.append("(CASE WHEN "+subSelect1+" <> 0");
					columnTop.append(" THEN ("+ subSelect2 +"/"+subSelect1 +")*100  ");
					columnTop.append(" ELSE 0 END )  as PER1_"+colGroupName +",   ");
				
					columnAll.append(" PER1_"+colGroupName+", ");;
				}
				
			}else{
				subSelect2 = "NVL(sum("+salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName+"),0)" ;
				columnName2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect2+" as "+columnName2+",  ");
				columnAll.append( columnName2+",  ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp1()))){
					columnTop.append("(CASE WHEN "+subSelect1+" <> 0");
					columnTop.append(" THEN ("+ subSelect2 +"/"+subSelect1 +")*100  ");
					columnTop.append(" ELSE 0 END )  as PER1_"+colGroupName +",   ");
					
					columnAll.append(" PER1_"+colGroupName+", \n");;
				}
			}
	   }
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
			
			if("CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3()))){
				subSelect3 = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				columnName3 = salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName;

				/** Gen Column Top **/
				columnTop.append( subSelect3+" as "+columnName3+",  ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName3+", ");
				
			}else if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3()))){
				subSelect3 = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				columnName3 = salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName;
				
				String subSelectNoDup = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
			    String columnNameNoDup = SAInitial.NO_DUP_PREFIX+salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName;
					
				/** Gen Column ALL **/
				columnTop.append( subSelect3+" as "+columnName3+",  ");
				columnTop.append( subSelectNoDup+" as "+columnNameNoDup+",  ");
				
				/** Gen Column ALL **/
				columnAll.append( columnName3+",  ");
				columnAll.append( columnNameNoDup+",  ");
				
			}else{
				subSelect3 = "NVL(sum("+salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName+"),0)" ;
				columnName3 = salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect3+" as "+columnName3+",  ");
				columnAll.append( columnName3+",  ");
			}
	   }
		
       if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
			
			if("CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4()))){
				subSelect4 = genSqlCountCustomerNoDup(salesBean, groupBy, colGroupName,sqlNotIncaseCall).toString();
				columnName4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName;
					
				/** Gen Column Top **/
				columnTop.append( subSelect4+" as "+columnName4+", ");

				/** Gen Column ALL **/
				columnAll.append( columnName4+",  ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp2()))){
					columnTop.append("(CASE WHEN "+subSelect3+" <> 0");
					columnTop.append(" THEN ("+ subSelect4 +"/"+subSelect3 +")*100  ");
					columnTop.append(" ELSE 0 END )  as PER2_"+colGroupName +",   ");
				
					columnAll.append(" PER2_"+colGroupName+", ");;
				}
				
			}else if("CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4()))){
				subSelect4 = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				columnName4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName;
				
				String subSelectNoDup = genSqlCountCustomer(salesBean, groupBy, colGroupName).toString();
				String columnNameNoDup = SAInitial.NO_DUP_PREFIX+salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect4+" as "+columnName4+",  ");
				columnTop.append( subSelectNoDup+" as "+columnNameNoDup+",  ");
				

				/** Gen Column ALL **/
				columnAll.append( columnName4+",  ");
				columnAll.append( columnNameNoDup+",  ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp2()))){
					columnTop.append("(CASE WHEN "+subSelect3+" <> 0");
					columnTop.append(" THEN ("+ subSelect4 +"/"+subSelect3 +")*100  ");
					columnTop.append(" ELSE 0 END )  as PER2_"+colGroupName +",  ");
				
					columnAll.append(" PER2_"+colGroupName+", ");;
				}	
			}else{
				subSelect4 = "NVL(sum("+salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName+"),0)" ;
				columnName4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName;
				
				/** Gen Column ALL **/
				columnTop.append( subSelect4+" as "+columnName4+",  ");
				columnAll.append( columnName4+",  ");
				
				//PER
				if( !"0".equals(Utils.isNull(salesBean.getCompareDisp2()))){
					columnTop.append("(CASE WHEN "+subSelect3+" <> 0");
					columnTop.append(" THEN ("+ subSelect4 +"/"+subSelect3 +")*100  ");
					columnTop.append(" ELSE 0 END )  as PER2_"+colGroupName +",   ");
				
					columnAll.append(" PER2_"+colGroupName+", ");;
				}
			}
	   }
     
		result[0] = columnTop.toString();
		result[1] = columnAll.toString();
		
		return result;
	}

	/**
	 * 
	 * @param salesBean
	 * @param type
	 * @param groupBy
	 * @param colGroupName
	 * @return
	 * @throws Exception
	 */
	public  String genSQLSelectColumn(SABean salesBean,String type ,String groupBy,String colGroupName) throws Exception{
		String sql ="";
		String colName1 = "";String colName2 = "";
		String colName3 = "";String colName4 = "";
		String colAlias1 = "";String colAlias2 = "";
		String colAlias3 = "";String colAlias4 = "";
		
		colGroupName = reportU.getShortColName(colGroupName);
		
		if("ORDER".equalsIgnoreCase(type)){
			if(colGroupName.equalsIgnoreCase("INVOICE_DATE")){
				sql +="SALES_ORDER_DATE as "+groupBy+",  ";
			}else{
		        sql += groupBy +" as "+groupBy+",  ";
		        if("SUBBRAND".equalsIgnoreCase(groupBy)){
		        	sql +="INVENTORY_ITEM_ID,"; 
		    	}
			}
		}else{
			sql +=groupBy +" as "+groupBy+",  ";	
			if("SUBBRAND".equalsIgnoreCase(groupBy)){
	        	sql +="INVENTORY_ITEM_ID,"; 
	    	}
		}
		
		// IR_AMT = M.INVOICED_AMT-M.RETURNED_AMT
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp1()))){
			if( !"CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1())) && 
				!"CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp1()))
			    ){
				  colAlias1 = salesBean.getColNameDisp1()+"_"+salesBean.getColNameUnit1()+"_1_"+colGroupName ;
				  colName1 = reportU.genColumnName(salesBean.getColNameDisp1(),salesBean.getColNameUnit1());
				  if("ORDER".equalsIgnoreCase(type) && SAInitial.getInstance().COLUMN_ORDER_MAP.get(salesBean.getColNameDisp1()) != null){
				     sql +=colName1 +" as "+colAlias1+",  ";
				  }else if("INVOICE".equalsIgnoreCase(type) && SAInitial.getInstance().COLUMN_INVOICE_MAP.get(salesBean.getColNameDisp1()) != null){
				     sql +=colName1 +" as "+colAlias1+",  ";
				  }else{
				   sql +=" 0 as "+colAlias1+", ";
				  }
			}
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp2()))){
	        if( !"CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2())) && 
	            !"CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp2()))	
	        		){
				colAlias2 = salesBean.getColNameDisp2()+"_"+salesBean.getColNameUnit2()+"_2_"+colGroupName  ;
				colName2 = reportU.genColumnName(salesBean.getColNameDisp2(),salesBean.getColNameUnit2());
				if("ORDER".equalsIgnoreCase(type) && SAInitial.getInstance().COLUMN_ORDER_MAP.get(salesBean.getColNameDisp2()) != null){
				   sql +=colName2 +" as "+colAlias2+",  ";
				}else if("INVOICE".equalsIgnoreCase(type) && SAInitial.getInstance().COLUMN_INVOICE_MAP.get(salesBean.getColNameDisp2()) != null){
				   sql +=colName2 +" as "+colAlias2+",  ";
				}else{
				   sql +=" 0 as "+colAlias2+",  ";
				}
			}
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp3()))){
	        if( !"CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3())) && 
	        	!"CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp3()))	
	        		){
			
			    colAlias3 =  salesBean.getColNameDisp3()+"_"+salesBean.getColNameUnit3()+"_3_"+colGroupName  ;
				colName3 = reportU.genColumnName(salesBean.getColNameDisp3(),salesBean.getColNameUnit3());
				if("ORDER".equalsIgnoreCase(type) && SAInitial.getInstance().COLUMN_ORDER_MAP.get(salesBean.getColNameDisp3()) != null){
				   sql +=colName3 +" as "+colAlias3+",  ";
				}else if("INVOICE".equalsIgnoreCase(type) && SAInitial.getInstance().COLUMN_INVOICE_MAP.get(salesBean.getColNameDisp3()) != null){
				   sql +=colName3 +" as "+colAlias3+",  ";
				}else{
				   sql +=" 0 as "+colAlias3+",  ";
				}
			}
		}
		
		if( !"0".equals(Utils.isNull(salesBean.getColNameDisp4()))){
	        if( !"CALL".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4())) &&
	        	!"CALL_NEW".equalsIgnoreCase(Utils.isNull(salesBean.getColNameDisp4()))	
	        		){
				colAlias4 = salesBean.getColNameDisp4()+"_"+salesBean.getColNameUnit4()+"_4_"+colGroupName ;
				colName4 = reportU.genColumnName(salesBean.getColNameDisp4(),salesBean.getColNameUnit4());
				if("ORDER".equalsIgnoreCase(type) && SAInitial.getInstance().COLUMN_ORDER_MAP.get(salesBean.getColNameDisp4()) != null){
				   sql +=colName4 +" as "+colAlias4+",  ";
				}else if("INVOICE".equalsIgnoreCase(type) && SAInitial.getInstance().COLUMN_INVOICE_MAP.get(salesBean.getColNameDisp4()) != null){
				   sql +=colName4 +" as "+colAlias4+",  ";
				}else{
				   sql +=" 0 as "+colAlias4+",  ";
				}
			}
		}
		
		return sql;
	}

}
