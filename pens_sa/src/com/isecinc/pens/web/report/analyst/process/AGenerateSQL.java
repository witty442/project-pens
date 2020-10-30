package com.isecinc.pens.web.report.analyst.process;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.report.analyst.bean.ABean;
import com.isecinc.pens.web.report.analyst.bean.ConfigBean;
import com.isecinc.pens.web.report.analyst.bean.CriteriaBean;
import com.isecinc.pens.web.report.analyst.helper.AGenCondition;
import com.isecinc.pens.web.report.analyst.helper.AUtils;
import com.isecinc.pens.web.report.analyst.helper.SecurityHelper;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Debug;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class AGenerateSQL {
	public static  Debug debug = new Debug(true,Debug.level_0);//debug all
	//public static  Debug debug = new Debug(true,Debug.level_1);//debug some by user
    
	public static AUtils reportU = new AUtils();
	/** Logger */
	protected Logger logger = Logger.getLogger("PENS");
	public static AInitial aInit = null;
	
	public AGenerateSQL(AInitial aaInit){
		aInit = aaInit;
	}
	
	/**
	 * genMainSql
	 * @param conn
	 * @param user
	 * @param salesBean
	 * @param colGroupList
	 * @param all
	 * @return  sql StringBuffer
	 * @throws Exception
	 */
	public  StringBuffer genMainSql(Connection conn,User user,ABean salesBean
			,List<ConfigBean> colGroupList,CriteriaBean criBean ) throws Exception{
        AGenCondition aCond = new AGenCondition(aInit);
		StringBuffer sql = new StringBuffer("");
		
		logger.debug("TypeSearch :"+salesBean.getTypeSearch() +"ColumnGroupList Size:"+colGroupList.size());
		
		if(AConstants.TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch()) && colGroupList.size() ==1 ){
			
			sql = genSqlTypeDay(conn, user, salesBean, colGroupList, criBean, aCond);
			
		}else {
			//Month ,quarter,year
			sql = genSqlTypeMonth(conn, user, salesBean, colGroupList, criBean, aCond);
			
		}
		
		//logger.debug("*************** Generate SQL ******************** \n");
		//logger.debug(sql.toString()+"\n******************************** \n");
		if(logger.isDebugEnabled()){
			FileUtil.writeFile("d://dev_temp/temp/sql.sql", sql.toString(),"TIS-620");
		}
		return sql;
	}
	
	/**
	 * Type Day
	 * @param conn
	 * @param user
	 * @param salesBean
	 * @param colGroupList
	 * @param criBean
	 * @param aCond
	 * @return
	 * @throws Exception
	 */
	public  StringBuffer genSqlTypeDay(Connection conn,User user,ABean salesBean
			,List<ConfigBean> colGroupList,CriteriaBean criBean ,AGenCondition aCond) throws Exception{
		StringBuffer sql = new StringBuffer("");
		//GEN SUB SQL  Separate By Order and Invoice
        boolean isColumnOrder = reportU.isColumnDispOrder(salesBean);
        boolean isColumnInvoice = reportU.isColumnDispInvoice(salesBean);
        
		String[] topColumn = aCond.genTopSQLSelectColumn(salesBean,salesBean.getGroupBy(),salesBean.getGroupBy());
		String sqlTopColumn = topColumn[0];
		String sqlAllColumn = topColumn[1];
		
		sql.append("SELECT  * FROM ( \n");
		sql.append("\t"+  "SELECT  DISTINCT "+sqlAllColumn+" \n");
		sql.append("\t"+   aCond.genSQLGetDesc(salesBean.getGroupBy())+"");
		sql.append("\t"+   aCond.genSQLGetCode(salesBean.getGroupBy())+"");
		sql.append("\t"+   " '1' as a \n");
		sql.append("\t"+"FROM ( \n");
		sql.append("\t"+"SELECT \n ");
		/** Gen Select Column By Disp Column **/
		sql.append("\t"+  sqlTopColumn +"\n");
		
		sql.append("\t"+  " '1' as a \n");
        sql.append("\t"+  " FROM ( \n");
        
        if(isColumnOrder){
		    sql.append(aCond.genSubSQLByType(conn,user,salesBean, "ORDER", salesBean.getGroupBy(), salesBean.getGroupBy())+"\n");
        }
        if(isColumnOrder && isColumnInvoice){
        	sql.append("\t"+" UNION ALL \n");
        }
        if(isColumnInvoice){
		    sql.append(aCond.genSubSQLByType(conn,user,salesBean, "INVOICE", salesBean.getGroupBy(), salesBean.getGroupBy())+"\n");
        }
		sql.append("\t"+"  )SS \n");
		sql.append("\t "+"GROUP BY "+AGenCondition.genGroupBySQL(salesBean.getGroupBy()) +"\n");
		sql.append("\t"+ ")S  \n ");
		sql.append(")V  \n ");
		
		/** Order BY */
		String order_type = salesBean.getOrder_type(); // DESC / ASC 
		String order_by_name=salesBean.getOrder_by_name();
		String order_by_name2="";
		
		if (!order_by_name.equalsIgnoreCase("")){
			order_by_name2=order_by_name;
      	}else {
			order_by_name2=salesBean.getGroupBy()+"_CODE ";
		}

		sql.append("ORDER BY V."+order_by_name2+" "+order_type+" \n"); 
		
		return sql;
	}
	
	
	/**
	 * Gen SQL Type Month
	 * @param conn
	 * @param user
	 * @param salesBean
	 * @param colGroupList
	 * @param criBean
	 * @param aCond
	 * @return
	 * @throws Exception
	 */
	public  StringBuffer genSqlTypeMonth(Connection conn,User user,ABean salesBean
			,List<ConfigBean> colGroupList,CriteriaBean criBean ,AGenCondition aCond) throws Exception{
		StringBuffer sql = new StringBuffer("");
		
		sql.append("SELECT A.* FROM ( \n");
		sql.append("\t SELECT * FROM( \n");
		
		boolean isColumnOrder = reportU.isColumnDispOrder(salesBean); 
		boolean isColumnInvoice = reportU.isColumnDispInvoice(salesBean);
		 
		// Sub SQl Order
		if(isColumnOrder){
			sql.append("\t SELECT DISTINCT  \n");
			//EDIT 24/04/2563
			sql.append("\t"+ genSQLNA(Utils.isNull(salesBean.getGroupBy())) +", \n ");
			
			//Get desc By Code for display
			sql.append("\t"+ aCond.genSQLGetCode(salesBean.getGroupBy()));
			sql.append("\t"+ aCond.genSQLGetDesc(salesBean.getGroupBy()));
			
			sql.append("\t"+"'1' AS A \n");
			sql.append("\t"+"FROM "+aInit.TABLE_VIEW+" S \n");
			sql.append("\t"+"WHERE 1=1 \n ");
			sql.append("\t"+"AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
			/** Condition Filter **/
			sql.append("\t"+aCond.genSqlWhereCondition(salesBean));
			/** Filter Display Data By User **/
			sql.append("\t"+SecurityHelper.genWhereSqlFilterByUser(conn,user, null,"S."));
			
			//ALL MAIN SQL
			if(AConstants.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
			    //sql.append("\t"+" AND sales_order_month IN("+criBean.getMonth()+") \n");
			    //sql.append("\t"+" AND sales_order_year IN("+criBean.getYear()+") \n");
			    sql.append(AGenCondition.genWhereCondSQLCaseByMonthYearMain("\t","","ORDER",criBean.getAllCond()));
			    
			    sql.append("\t"+" GROUP BY  "+AGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
			}else if(AConstants.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				//sql.append("\t"+" AND sales_order_quarter IN("+criBean.getQuarter()+") \n");
				//sql.append("\t"+" AND sales_order_year IN("+criBean.getYear()+") \n");
				 
				sql.append(AGenCondition.genWhereCondSQLCaseByQuarterYearMain("\t","","ORDER",criBean.getAllCond()));
				 
				sql.append("\t"+" GROUP BY  "+AGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
			}else if(AConstants.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				//sql.append("\t"+" AND sales_order_year IN("+all+") \n");
				
				sql.append(""+AGenCondition.genWhereCondSQLCaseByYEAR("ORDER",criBean.getAllCond()));
				sql.append("\t"+" GROUP BY  "+AGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
			}
			else {
				Date date = DateUtil.parseToBudishDate(salesBean.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				Date dateTo = DateUtil.parseToBudishDate(salesBean.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				// 
				if(date != null && dateTo != null){
					sql.append("\t"+" AND  SALES_ORDER_DATE >= to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
					sql.append("\t"+" AND  SALES_ORDER_DATE <= to_date('"+DateUtil.stringValue(dateTo, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
					sql.append("\t"+" GROUP BY  "+AGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}
			}
		}
		
		if(isColumnOrder && isColumnInvoice){
        	sql.append("\t UNION \n");
        }
		// Sub SQl Invoice
		if(isColumnInvoice){
			sql.append("\t"+"SELECT DISTINCT \n");
			//EDIT 24/04/2563
			sql.append("\t"+ genSQLNA(Utils.isNull(salesBean.getGroupBy())) +", \n ");
			
			sql.append("\t"+ aCond.genSQLGetCode(salesBean.getGroupBy()));
			sql.append("\t"+ aCond.genSQLGetDesc(salesBean.getGroupBy()));
			sql.append("\t"+"'1' AS A \n");
			sql.append("\t"+"FROM "+aInit.TABLE_VIEW+" S \n");
			sql.append("\t"+"WHERE 1=1 \n ");
			sql.append("\t"+"AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
			/** Condition Filter **/
			sql.append("\t"+aCond.genSqlWhereCondition(salesBean));
			
			/** Filter Display Data By User **/
			sql.append("\t"+SecurityHelper.genWhereSqlFilterByUser(conn,user, null,"S."));
			
			//ALL MAIN SQL
			if(AConstants.TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
			    //sql.append("\t"+" AND invoice_month IN("+criBean.getMonth()+") \n");
			    //sql.append("\t"+" AND invoice_year IN("+criBean.getYear()+") \n");
				 
				sql.append(AGenCondition.genWhereCondSQLCaseByMonthYearMain("\t","","INVOICE",criBean.getAllCond()));
				 
			    sql.append("\t"+" GROUP BY  "+AGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
			}else if(AConstants.TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
				//sql.append("\t"+" AND invoice_quarter IN("+criBean.getQuarter()+") \n");
				//sql.append("\t"+" AND invoice_year IN("+criBean.getYear()+") \n");
				
				sql.append(AGenCondition.genWhereCondSQLCaseByQuarterYearMain("\t","","INVOICE",criBean.getAllCond()));
				 
				sql.append("\t"+" GROUP BY  "+AGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
			}else if(AConstants.TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
				//sql.append("\t"+" AND invoice_year IN("+all+") \n");
				
				sql.append(""+AGenCondition.genWhereCondSQLCaseByYEAR("INVOICE",criBean.getAllCond()));
				sql.append("\t"+" GROUP BY "+AGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
			}
			else {
				Date date = DateUtil.parseToBudishDate(salesBean.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				Date dateTo = DateUtil.parseToBudishDate(salesBean.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH);
				// 
				if(date != null && dateTo != null){
					sql.append("\t"+" AND  INVOICE_DATE >= to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
					sql.append("\t"+" AND  INVOICE_DATE <= to_date('"+DateUtil.stringValue(dateTo, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
					sql.append("\t"+" GROUP BY  "+AGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}
			}
		}
		sql.append("\t ) S \n");

		Date startDate = null ;
		String paramDay = salesBean.getDay(); // Temp Parameter Using for Set Back Date Parameter
		if( !Utils.isNull(salesBean.getDay()).equals("")){
			logger.debug("salesBean.getDay():"+salesBean.getDay());
			startDate = DateUtil.convertStringToDate(salesBean.getDay());
		}
		
		/** Generate By Condition From TO dayFrom-DateTo or Month 11-Month 12 or Quarter1-Quarter4 **/
		StringBuffer sqlNotInCaseCall = new StringBuffer("");
		List<StringBuffer> sqlNotInCaseCallAllList = new ArrayList<StringBuffer>();
		
		for(int i=0;i<colGroupList.size();i++){
			ConfigBean configGroupBean = (ConfigBean)colGroupList.get(i);
			String colGroupName = configGroupBean.getName(); //01,02
			
			if(startDate != null && i != 0){
				startDate = DateUtils.addDays(startDate, 1); 
				salesBean.setDay(DateUtil.convertToString(startDate));
			}
			
			/** Gen SQL not in Case CALL **/
			if(i==0){
				sqlNotInCaseCallAllList.add(aCond.genSqlWhereCondByGroup(salesBean,"INVOICE","c.",colGroupName));
			}else{
			    sqlNotInCaseCall = aCond.genSqlMinusCustomerId(salesBean, sqlNotInCaseCallAllList);
			    
			    sqlNotInCaseCallAllList.add(aCond.genSqlWhereCondByGroup(salesBean,"INVOICE","c.",colGroupName));	
			}
			
			//logger.debug("SQL NOT IN["+i+" ********************** \n"+sqlNotInCaseCall.toString()+"\n***************************");
			
			sql.append("LEFT OUTER JOIN \n");
			
			/** Gen Sql By Group  **/
			sql.append(" (  \n");
			String[] topColumn = aCond.genTopSQLSelectColumn(salesBean,salesBean.getGroupBy(),colGroupName,sqlNotInCaseCall.toString());
			String sqlTopColumn = topColumn[0];
			String sqlAllColumn = topColumn[1];
			
			sql.append("SELECT  "+sqlAllColumn+" \n");
			sql.append(  " '1' as a \n");
			sql.append("FROM ( \n");
			sql.append(" SELECT  \n "+sqlTopColumn);
			sql.append(  " '1' as a \n");
            sql.append(  " FROM ( \n");
            	 
			//GEN SUB SQL  Separate By Order and Invoice
            if(isColumnOrder){
			   sql.append(aCond.genSubSQLByType(conn,user,salesBean, "ORDER", salesBean.getGroupBy(), colGroupName));
            }
            if(isColumnOrder && isColumnInvoice){
               sql.append(" UNION ALL \n");
            }
            if(isColumnInvoice){
			   sql.append(aCond.genSubSQLByType(conn,user,salesBean, "INVOICE", salesBean.getGroupBy(),colGroupName));
            }
			sql.append(   "     )SS \n");
			sql.append(" GROUP BY "+salesBean.getGroupBy() +"\n");
			sql.append("  )SS  \n ");
			
			sql.append(" )M_"+colGroupName +" \n");
			sql.append("ON S."+Utils.isNull(salesBean.getGroupBy())+"=M_"+colGroupName+"."+Utils.isNull(salesBean.getGroupBy()) +"\n");
			
		}//for
		
		if(startDate != null)
			salesBean.setDay(paramDay);
		
		sql.append(") A \n");
		
		/** Order BY */
		String order_type =salesBean.getOrder_type(); // DESC / ASC modify by tutiya 
		String order_by_name=salesBean.getOrder_by_name();
		String order_by_name2="";
		String whenNullValue ="";
		
		if (!order_by_name.equalsIgnoreCase("")){
			order_by_name2="A."+order_by_name;
			whenNullValue = "0";
      	}else {
			order_by_name2="A."+salesBean.getGroupBy()+"_CODE ";
			
			if("Invoice_Date".equals(salesBean.getGroupBy()) || "SALES_ORDER_DATE".equals(salesBean.getGroupBy())){
				whenNullValue = "Sysdate";
			}
			else{
				whenNullValue = "null";
			}
		}
		sql.append("ORDER BY COALESCE("+order_by_name2+","+whenNullValue+") "+order_type+" \n"); // modify by tutiya

		return sql;
	}
	
	public static String genSQLNA(String columnName){
		String sqlR = columnName;
		
		//No Edit case column is null
		/*if( columnName.equalsIgnoreCase("Customer_id")
	     || columnName.equalsIgnoreCase("Organization_id")
	     || columnName.equalsIgnoreCase("Order_type_id")
	     || columnName.equalsIgnoreCase("inventory_item_id")
	     || columnName.equalsIgnoreCase("Salesrep_id")
	     || columnName.equalsIgnoreCase("SHIP_TO_SITE_USE_ID")
	     || columnName.equalsIgnoreCase("BILL_TO_SITE_USE_ID")
	    ){
			sqlR = "NVL("+columnName+","+COLUMN_NUMBER_NA+") as "+columnName;
		}else{
			sqlR = "NVL("+columnName+",'"+COLUMN_TEXT_NA+"') as "+columnName;
		}*/
		
		return sqlR;
	}
}
