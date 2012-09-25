package com.isecinc.pens.report.salesanalyst;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.SAGenCondition;
import com.isecinc.pens.report.salesanalyst.helper.SAUtils;
import com.isecinc.pens.report.salesanalyst.helper.SecurityHelper;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

public class SAProcess {
   
	/**
	 * WITTY
	 */
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public   static Map<String,String> MONTH_MAP = new HashMap<String,String>();
	public   static Map<String,String>  QUARTER_MAP = new HashMap<String,String> ();
	public   static Map<String,String>  GROUP_BY_MAP = new HashMap<String,String> ();
	public   static Map<String,String>  DISP_COL_MAP = new HashMap<String,String> ();
	public   static Map<String,String>  UNIT_MAP = new HashMap<String,String> ();
	public   static Map<String,String>  COMPARE_MAP = new HashMap<String,String> ();
	public   static Map<String,String>  COLUMN_ORDER_MAP = new HashMap<String,String> ();
	public   static Map<String,String>  COLUMN_INVOICE_MAP = new HashMap<String,String> ();
	
	// Add Summary Type
	public   static Map<String,String>  SUMMARY_TYPE_MAP = new HashMap<String,String> ();
	public	 static String SUMMARY_TYPE_NONE = "";
	public	 static String SUMMARY_TYPE_SUM = "SUM";
	public	 static String SUMMARY_TYPE_AVG = "AVG";
	
	public static final String TYPE_SEARCH_DAY = "DAY";
	public static final String TYPE_SEARCH_MONTH = "MONTH";
	public static final String TYPE_SEARCH_QUARTER = "QUARTER";
	public static final String TYPE_SEARCH_YEAR = "YEAR";
	public static final String TABLE_VIEW = "XXPENS_BI_SALES_ANALYSIS_V";
	
	//For Prefix ColumnName CALL NO DUP 
	public static final String NO_DUP_PREFIX = "ND_";
	
	public static List<String> MULTI_SELECTION_LIST = new ArrayList<String>();
	SAGenCondition saGen = new SAGenCondition();
	SAUtils saU = new SAUtils();
	private static SAProcess salesAnalystProcess;
	
	public static SAProcess getInstance(){
		
		if(salesAnalystProcess == null){
			salesAnalystProcess = new SAProcess();
			return salesAnalystProcess;
		}
		return salesAnalystProcess;
	}

	static {
		logger.debug("Initail Param Sales Analisis");
		
		MULTI_SELECTION_LIST.add("inventory_item_id");
		MULTI_SELECTION_LIST.add("Brand");
		MULTI_SELECTION_LIST.add("Customer_id");
		MULTI_SELECTION_LIST.add("Sales_Channel");
		MULTI_SELECTION_LIST.add("Salesrep_id");
		
		/** Column Of ORDER **/
		COLUMN_ORDER_MAP.put("ORDERED","ORDERED");
		COLUMN_ORDER_MAP.put("Backordered","Backordered");
		COLUMN_ORDER_MAP.put("Outstanding","Outstanding");
		
		/** Column Of INVOICE **/
		COLUMN_INVOICE_MAP.put("TARGET","TARGET");
		COLUMN_INVOICE_MAP.put("INVOICED","INVOICED");
		COLUMN_INVOICE_MAP.put("Returned","Returned");
		COLUMN_INVOICE_MAP.put("Discount","Discount");
		COLUMN_INVOICE_MAP.put("Promotion","Promotion");
		COLUMN_INVOICE_MAP.put("IR","IR");
		COLUMN_INVOICE_MAP.put("NETAMT","NETAMT");
		COLUMN_INVOICE_MAP.put("CALL","CALL");
		
		/** QUARTER MAP ****/
		QUARTER_MAP.put("1", "ไตรมาส 1");
		QUARTER_MAP.put("2", "ไตรมาส 2");
		QUARTER_MAP.put("3", "ไตรมาส 3");
		QUARTER_MAP.put("4", "ไตรมาส 4");
		
		/** Display Column  **/
		DISP_COL_MAP.put("TARGET","เป้าหมาย");
		DISP_COL_MAP.put("INVOICED","ยอดขาย");
		DISP_COL_MAP.put("ORDERED","ยอดสั่งซื้อ");
		DISP_COL_MAP.put("Returned","รับคืน");
		DISP_COL_MAP.put("Discount","ส่วนลด");
		DISP_COL_MAP.put("Promotion","ของแถม");
		DISP_COL_MAP.put("Backordered","สินค้าค้างส่ง ไม่มีสินค้า");
		DISP_COL_MAP.put("Outstanding","สินค้าค้างส่ง มีสินค้า ");
		DISP_COL_MAP.put("IR","ยอดขาย-คืน");/** INVOICED_AMT-RETURNED_AMT as IR_AMT **/
		DISP_COL_MAP.put("NETAMT","ยอดขาย-คืน-ส่วนลด");/** INVOICED_AMT-RETURNED_AMT-DISCOUNT_AMT as NETAMT **/
		DISP_COL_MAP.put("CALL", "CALL");/**Sum Customer  **/
		
		/** Unit Column  **/
		UNIT_MAP.put("AMT","บาท");
		UNIT_MAP.put("QTY","หีบ");
		
		/** Copmare Column  **/
		COMPARE_MAP.put("PERCENT","%");
		
		/** Summary Type*/
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_NONE , "ไม่เลือก");
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_SUM , "ยอดรวม");
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_AVG , "ค่าเฉลี่ย");
	}
	
	private String maxOrderedDate = "";
	private String maxOrderedTime ="";
	
	public  void initSession(HttpServletRequest requestWeb) {
		References r = null;
		HttpSession session = requestWeb.getSession(true);
		Connection conn = null;
		int i = 0;
		try{
			logger.debug("Initail Session ");
			conn = DBConnection.getInstance().getConnection();
			
			try{
				if(GROUP_BY_MAP.isEmpty()){
					List<References>groupByList = SecurityHelper.filterDisplayColumnByUser(conn);
					/** init Variable Map GroupByMap for Use **/
					if(groupByList != null && groupByList.size() > 0){
						for(i=0;i<groupByList.size();i++){
							References ref = (References)groupByList.get(i);
					        GROUP_BY_MAP.put(ref.getKey(),ref.getName());
						}
					}
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
			/** init TypeSearch ***/
			List<References> typeSearchList = new ArrayList<References>();
			r = new References("DAY","วัน");
			typeSearchList.add(r);
			r = new References("MONTH","เดือน");
			typeSearchList.add(r);
			r = new References("QUARTER","ไตรมาส");
			typeSearchList.add(r);
			r = new References("YEAR","ปี");
			typeSearchList.add(r);
			session.setAttribute("typeSearchList", typeSearchList);
			
			/** init year **/
			
			List<References> yearL = initYearList(conn);
			session.setAttribute("yearList", yearL);
			
			
			for(References year:yearL){
				String yearKey = year.getKey();
				String yearShow = year.getName();
				
				MONTH_MAP.put(yearKey+"01", "ม.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"02", "ก.พ. "+yearShow);
				MONTH_MAP.put(yearKey+"03", "มี.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"04", "เม.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"05", "พ.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"06", "มิ.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"07", "ก.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"08", "ส.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"09", "ก.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"10", "ต.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"11", "พ.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"12", "ธ.ค. "+yearShow);
			}
			
			/** init quarter **/
			List<References> quarterList = new ArrayList<References>();
			r = new References("0","ไม่เลือก");
			quarterList.add(r);
			r = new References("ไตรมาส 1","ไตรมาส 1");
			quarterList.add(r);
			r = new References("ไตรมาส 2","ไตรมาส 2");
			quarterList.add(r);
			r = new References("ไตรมาส 3","ไตรมาส 3");
			quarterList.add(r);
			r = new References("ไตรมาส 4","ไตรมาส 4");
			quarterList.add(r);
			session.setAttribute("quarterList", quarterList);
			
			
			/** init day **/
			List<References> dayList = new ArrayList<References>();
			for(i=1;i<=31;i++){
			   r = new References(""+i,""+i);
			   dayList.add(r);
			}
			session.setAttribute("dayList", dayList);

			/** init Group BY  **/
			List<References> groupByList = new ArrayList<References>();
			groupByList = SecurityHelper.filterDisplayColumnByUser(conn);
			session.setAttribute("groupByList", groupByList);
			
			/** init Variable Map GroupByMap for Use **/
			if(groupByList != null && groupByList.size() > 0){
				for(i=0;i<groupByList.size();i++){
					References ref = (References)groupByList.get(i);
			        GROUP_BY_MAP.put(ref.getKey(),ref.getName());
				}
			}
			
			/** init ConditionList **/
			List<References> conditionList = new ArrayList<References>();
			r = new References("-1","ไม่เลือก"); // modify by Tutiya R. 7 Feb 2011
			conditionList.add(r);
			conditionList.addAll(groupByList);
			session.setAttribute("conditionList", conditionList);
			
			/** Init operation List **/
			List<References> operationList = new ArrayList<References>();
			r = new References("=","=");
			operationList.add(r);
			r = new References("<>","<>");
			operationList.add(r);
			session.setAttribute("operationList", operationList);
			
			/** Init values List **/
			List<References> valuesList = new ArrayList<References>();
			
			r = new References("0","ไม่เลือก"); 
			
			valuesList.add(r);
			session.setAttribute("valuesList1", valuesList);
			session.setAttribute("valuesList2", valuesList);
			session.setAttribute("valuesList3", valuesList);
			session.setAttribute("valuesList4", valuesList);
			
			/** Init Display Column List **/
			List<References> dispColumnList = new ArrayList<References>();
			dispColumnList.add(new References("0","ไม่เลือก"));
			dispColumnList.add(new References("NETAMT","ยอดขาย-คืน-ส่วนลด"));/** INVOICED_AMT-RETURNED_AMT-DISCOUNT_AMT as NETAMT **/
			dispColumnList.add(new References("INVOICED","ยอดขาย"));
			dispColumnList.add(new References("Backordered","สินค้าค้างส่ง ไม่มีสินค้า"));
			dispColumnList.add(new References("Promotion","ของแถม"));
			dispColumnList.add(new References("TARGET","เป้าหมาย"));
			dispColumnList.add(new References("Returned","รับคืน"));
			dispColumnList.add(new References("IR","ยอดขาย-คืน"));/** INVOICED_AMT-RETURNED_AMT as IR_AMT **/
			dispColumnList.add(new References("ORDERED","ยอดสั่งซื้อ"));
			dispColumnList.add(new References("Discount","ส่วนลด"));
			dispColumnList.add(new References("Outstanding","สินค้าค้างส่ง มีสินค้า "));
			dispColumnList.add(new References("CALL", "CALL"));/**Sum Customer ID  **/
			session.setAttribute("dispColumnList", dispColumnList);
			
			
			/** Init unit Column List **/
			List<References> unitColumnList = new ArrayList<References>();
			Iterator itU = UNIT_MAP.keySet().iterator();
			while(itU.hasNext()){
				String key = (String)itU.next();
				r = new References(key,Utils.isNull(UNIT_MAP.get(key)));
				unitColumnList.add(r);
			}
			session.setAttribute("unitColumnList", unitColumnList);
			
			/** Init Comapre Column List **/
			List<References> compareColumnList = new ArrayList<References>();
			r = new References("0","ไม่เลือก");
			compareColumnList.add(r);
			Iterator itP = COMPARE_MAP.keySet().iterator();
			while(itP.hasNext()){
				String key = (String)itP.next();
				r = new References(key,Utils.isNull(COMPARE_MAP.get(key)));
				compareColumnList.add(r);
			}
			session.setAttribute("compareColumnList", compareColumnList);

			// Find Max Date Invoice Date
			setMaxOrderedDateTime(conn);
			session.setAttribute("maxOrderedDate", this.maxOrderedDate);
			session.setAttribute("maxOrderedTime", this.maxOrderedTime);
			
			/** Init Summary Type List **/
			List<References> summaryTypeList = new ArrayList<References>();
			Iterator sumIter = SUMMARY_TYPE_MAP.keySet().iterator();
			while(sumIter.hasNext()){
				String key = (String)sumIter.next();
				r = new References(key,Utils.isNull(SUMMARY_TYPE_MAP.get(key)));
				summaryTypeList.add(r);
			}
			session.setAttribute("summaryTypeList", summaryTypeList);
			
			//Remove Session RESULT
			session.setAttribute("RESULT", null);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			 	DBConnection.getInstance().closeConn(conn, null, null);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public void setMaxOrderedDateTime(Connection conn) throws Exception {
		String sql = "select to_char(max(start_date),'dd/mm/yyyy') as max_ordered_date ,to_char(max(start_date),'hh24:mi:ss') as max_ordered_time from xxpens_bi_running_status";
		Statement stmt = null;
		ResultSet rset = null;
		
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sql);
		
		while(rset.next()){
			this.maxOrderedDate = rset.getString(1);
			this.maxOrderedTime = rset.getString(2);
		}
	}

	public String getMaxOrderedDate() {
		return maxOrderedDate;
	}

	public String getMaxOrderedTime() {
		return maxOrderedTime;
	}

	public  List<References> initYearList(Connection conn) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
        List<References> yearList = new ArrayList<References>();
		try{
			sql = "SELECT DISTINCT ORDER_YEAR from XXPENS_BI_MST_ORDER_DATE ORDER BY ORDER_YEAR DESC";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				yearList.add(new References(rs.getString("ORDER_YEAR"), (rs.getInt("ORDER_YEAR")+543)+""));
			}
			
		}catch(Exception e){
			 throw e;
		}finally{
			
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return yearList;
	}
	
	/*** Gen SQL  ***/
	public  StringBuffer genMainSql(Connection conn,User user,SABean salesBean,List colGroupList,String all) throws Exception{
        boolean isColumnOrder = false;
        boolean isColumnInvoice = false;
        
		StringBuffer sql = new StringBuffer("");
		logger.debug("TypeSearch :"+salesBean.getTypeSearch() +"ColumnGroupList Size:"+colGroupList.size());
		
		if(TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch()) && colGroupList.size() ==1 ){
			
			//GEN SUB SQL  Separate By Order and Invoice
            isColumnOrder = saU.isColumnDispOrder(salesBean);
            isColumnInvoice = saU.isColumnDispInvoice(salesBean);
            
			String[] topColumn = saGen.genTopSQLSelectColumn(salesBean,salesBean.getGroupBy(),salesBean.getGroupBy());
			String sqlTopColumn = topColumn[0];
			String sqlAllColumn = topColumn[1];
			
			sql.append("SELECT  * FROM ( \n");
			sql.append("SELECT  "+sqlAllColumn+" \n");
			sql.append(  saGen.genSQLGetDesc(salesBean.getGroupBy()));
			sql.append(  saGen.genSQLGetCode(salesBean.getGroupBy()));
			sql.append(  " '1' as a \n");
			sql.append("FROM ( \n");
			sql.append(" SELECT \n ");
			/** Gen Select Column By Disp Column **/
			sql.append(  sqlTopColumn);
			
			sql.append(  " '1' as a \n");
            sql.append(  " FROM ( \n");
            
            if(isColumnOrder){
			    sql.append(saGen.genSubSQLByType(conn,user,salesBean, "ORDER", salesBean.getGroupBy(), salesBean.getGroupBy()));
            }
            if(isColumnOrder && isColumnInvoice){
            	sql.append(" UNION ALL \n");
            }
            if(isColumnInvoice){
			   sql.append(saGen.genSubSQLByType(conn,user,salesBean, "INVOICE", salesBean.getGroupBy(), salesBean.getGroupBy()));
            }
			sql.append(   "     )SS \n");
			sql.append(" GROUP BY "+salesBean.getGroupBy() +"\n");
			sql.append("  )S  \n ");
			sql.append(")V  \n ");
			
			/** Order BY */
			String order_type =salesBean.getOrder_type(); // DESC / ASC modify by tutiya 
			String order_by_name=salesBean.getOrder_by_name();
			String order_by_name2="";
			
			if (!order_by_name.equalsIgnoreCase("")){
				order_by_name2=order_by_name;
	      	}else {
				order_by_name2=salesBean.getGroupBy()+"_CODE ";
			}

			sql.append("ORDER BY V."+order_by_name2+" "+order_type+" \n"); // modify by tutiya
			
		}else {
			
			sql.append("SELECT A.* FROM ( \n");
			sql.append(" SELECT * FROM( \n");
			
			isColumnOrder = saU.isColumnDispOrder(salesBean); 
			isColumnInvoice = saU.isColumnDispInvoice(salesBean);
			 
			// Sub SQl Order
			if(isColumnOrder){
				sql.append(" SELECT  \n");
				sql.append(     Utils.isNull(salesBean.getGroupBy()) +", \n ");
				sql.append(     saGen.genSQLGetDesc(salesBean.getGroupBy()));
				sql.append(     saGen.genSQLGetCode(salesBean.getGroupBy()));
				sql.append("    '1' AS A \n");
				sql.append("    FROM "+TABLE_VIEW+" S \n");
				sql.append("    WHERE 1=1 \n ");
				sql.append("    AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
				/** Condition Filter **/
				sql.append(saGen.genSqlWhereCondition(salesBean));
				/** Filter Displsy Data By User **/
				//sql.append(SecurityHelper.genWhereSqlFilterByUser(conn,user, salesBean.getGroupBy()));
				
				//ALL MAIN SQL
				if(TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				    sql.append(" AND sales_order_year||sales_order_month IN("+all+") \n");
				    //sql.append(" AND sales_order_year IN('"+salesBean.getYear()+"') \n");
				    sql.append("group by  "+Utils.isNull(salesBean.getGroupBy()) +" \n ");
				}else if(TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
					sql.append(" AND sales_order_quarter IN("+all+") \n");
					sql.append(" AND sales_order_year IN('"+salesBean.getYear()+"') \n");
					sql.append("group by  "+Utils.isNull(salesBean.getGroupBy()) +" \n ");
				}else if(TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
					sql.append(" AND sales_order_year IN("+all+") \n");
					sql.append("group by  "+Utils.isNull(salesBean.getGroupBy()) +" \n ");
				}
				else {
					Date date = Utils.parseToBudishDate(salesBean.getDay(), Utils.DD_MM_YYYY_WITH_SLASH);
					Date dateTo = Utils.parseToBudishDate(salesBean.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH);
					// 
					if(date != null && dateTo != null){
						sql.append("AND  SALES_ORDER_DATE >= to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("AND  SALES_ORDER_DATE <= to_date('"+Utils.stringValue(dateTo, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("group by  "+Utils.isNull(salesBean.getGroupBy()) +" \n ");
					}
				}
			}
			
			if(isColumnOrder && isColumnInvoice){
            	sql.append(" UNION \n");
            }
			// Sub SQl Invoice
			if(isColumnInvoice){
				sql.append("    SELECT  \n");
				sql.append(     Utils.isNull(salesBean.getGroupBy()) +", \n ");
				sql.append(     saGen.genSQLGetDesc(salesBean.getGroupBy()));
				sql.append(     saGen.genSQLGetCode(salesBean.getGroupBy()));
				sql.append("    '1' AS A \n");
				sql.append("    FROM "+TABLE_VIEW+" S \n");
				sql.append("    WHERE 1=1 \n ");
				sql.append("    AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
				/** Condition Filter **/
				sql.append(saGen.genSqlWhereCondition(salesBean));
				
				/** Filter Displsy Data By User **/
				//sql.append(SecurityHelper.genWhereSqlFilterByUser(conn,user, salesBean.getGroupBy()));
				
				//ALL MAIN SQL
				if(TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				    sql.append(" AND invoice_year||invoice_month IN("+all+") \n");
				    //sql.append(" AND invoice_year IN('"+salesBean.getYear()+"') \n");
				    sql.append("group by  "+Utils.isNull(salesBean.getGroupBy()) +" \n ");
				}else if(TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
					sql.append(" AND invoice_quarter IN("+all+") \n");
					sql.append(" AND invoice_year IN('"+salesBean.getYear()+"') \n");
					sql.append("group by  "+Utils.isNull(salesBean.getGroupBy()) +" \n ");
				}else if(TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
					sql.append(" AND invoice_year IN("+all+") \n");
					sql.append("group by  "+Utils.isNull(salesBean.getGroupBy()) +" \n ");
				}
				else {
					Date date = Utils.parseToBudishDate(salesBean.getDay(), Utils.DD_MM_YYYY_WITH_SLASH);
					Date dateTo = Utils.parseToBudishDate(salesBean.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH);
					// 
					if(date != null && dateTo != null){
						sql.append("AND  INVOICE_DATE >= to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("AND  INVOICE_DATE <= to_date('"+Utils.stringValue(dateTo, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("group by  "+Utils.isNull(salesBean.getGroupBy()) +" \n ");
					}
				}
			}
			sql.append(" ) S \n");

			Date startDate = null ;
			String paramDay = salesBean.getDay(); // Temp Parameter Using for Set Back Date Parameter
			if(salesBean.getDay() != null){
				startDate = DateToolsUtil.convertStringToDate(salesBean.getDay());
			}
			
			
			/** Gen By Conditon From TO dayFrom-DateTo or Month 11-Month 12 or Quarter1-Quarter4 **/
			StringBuffer sqlNotInCaseCall = new StringBuffer("");
			List<StringBuffer> sqlNotInCaseCallAllList = new ArrayList<StringBuffer>();
			
			for(int i=0;i<colGroupList.size();i++){
				ConfigBean configGroupBean = (ConfigBean)colGroupList.get(i);
				String colGroupName = configGroupBean.getName(); //01,02
				
				if(startDate != null && i != 0){
					startDate = DateUtils.addDays(startDate, 1); 
					salesBean.setDay(DateToolsUtil.convertToString(startDate));
				}
				
				/** Gen SQL not in Case CALL **/
				if(i==0){
					sqlNotInCaseCallAllList.add(saGen.genSqlWhereCondByGroup(salesBean,"INVOICE","c.",colGroupName));
				}else{
				    sqlNotInCaseCall = saGen.genSqlMinusCustomerId(salesBean, sqlNotInCaseCallAllList);
				    
				    sqlNotInCaseCallAllList.add(saGen.genSqlWhereCondByGroup(salesBean,"INVOICE","c.",colGroupName));	
				}
				
				//logger.debug("SQL NOT IN["+i+" ********************** \n"+sqlNotInCaseCall.toString()+"\n***************************");
				
				sql.append("LEFT OUTER JOIN \n");
				
				/** Gen Sql By Group  **/
				sql.append(" (  \n");
				String[] topColumn = saGen.genTopSQLSelectColumn(salesBean,salesBean.getGroupBy(),colGroupName,sqlNotInCaseCall.toString());
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
				   sql.append(saGen.genSubSQLByType(conn,user,salesBean, "ORDER", salesBean.getGroupBy(), colGroupName));
	            }
	            if(isColumnOrder && isColumnInvoice){
	               sql.append(" UNION ALL \n");
	            }
	            if(isColumnInvoice){
				   sql.append(saGen.genSubSQLByType(conn,user,salesBean, "INVOICE", salesBean.getGroupBy(),colGroupName));
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
		}
		
		logger.debug("*************** Generate SQL ******************** \n"+sql.toString()+"\n*****************************************************\n");
		return sql;
	}
	
	public  List<References> getConditionValueList4Role(HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getConditionValueListModel4Role(conn,request,condType, null,null);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public  List<References> getConditionValueList(Connection conn,HttpServletRequest request,String condType)throws Exception{
		return getConditionValueListModel(conn,request,condType, null,null);
	}
	
	public  List<References> getConditionValueList(HttpServletRequest request,String condType)throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getConditionValueListModel(conn,request,condType, null,null);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public  List<References> getConditionValueList(Connection conn,HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		return getConditionValueListModel(conn,request,condType, code,desc);
	}
	
	public  List<References> getConditionValueList(HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getConditionValueListModel(conn,request,condType, code,desc);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public  List<References> getConditionValueListModel(Connection conn,HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<References> returnList = new ArrayList<References>();
		try{
			logger.debug("condType:"+condType);
			logger.debug("code:"+code);
			logger.debug("desc:"+desc);
			
			if("inventory_item_id".equalsIgnoreCase(condType)){
				sql = "SELECT INVENTORY_ITEM_ID,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC from XXPENS_BI_MST_ITEM where INVENTORY_ITEM_CODE is not null \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and INVENTORY_ITEM_CODE IN ("+SAUtils.converToText("INVENTORY_ITEM_CODE", code) +") \n"; 
					}
					else{
						sql += " and INVENTORY_ITEM_CODE like '"+code+"%' \n"; // modify by tutiya
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and INVENTORY_ITEM_DESC LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				//sql += SecurityHelper.genWhereSqlFilterByUser(request, "inventory_item_id");
				
				sql += "order by INVENTORY_ITEM_CODE \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("INVENTORY_ITEM_ID"),rs.getString("INVENTORY_ITEM_CODE") ,rs.getString("INVENTORY_ITEM_DESC")));
				}
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql = "select cust_cat_no,cust_cat_desc from XXPENS_BI_MST_CUST_CAT where cust_cat_no is not null \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and cust_cat_no IN ("+SAUtils.converToText("Customer_Category", code) +") \n"; 
					}else{
					    sql += " and cust_cat_no like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and cust_cat_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				//sql += SecurityHelper.genWhereSqlFilterByUser(request, "Customer_Category");
				
				sql += "order by cust_cat_no \n";
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("cust_cat_no"),rs.getString("cust_cat_no"),rs.getString("cust_cat_desc")));
				}
			}else if("Division".equalsIgnoreCase(condType)){
				sql = "select div_no,div_desc from XXPENS_BI_MST_DIVISION where div_no is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and div_no like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and div_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Division");
				
				sql += "order by div_no \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("div_no"),rs.getString("div_no"),rs.getString("div_desc")));
				}	
				
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql = "select salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and Salesrep_code in ("+SAUtils.converToText("Salesrep_id", code) +") \n"; 
					}else{
					    sql += " and Salesrep_code like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and salesrep_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Salesrep_id");
				
				sql += "order by Salesrep_code \n";
				
				logger.debug("SQL:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("salesrep_id"),rs.getString("salesrep_code"),rs.getString("salesrep_desc")));
				}	
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql = "select sales_channel_no,sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL where sales_channel_no is not null \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and sales_channel_no IN ("+SAUtils.converToText("Sales_Channel", code) +") \n"; 
					}else{
					    sql += " and sales_channel_no like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and sales_channel_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Sales_Channel");

				sql += "order by sales_channel_no \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("sales_channel_no"),rs.getString("sales_channel_no"),rs.getString("sales_channel_desc")));
				}	
			}else if("Customer_Group".equalsIgnoreCase(condType)){
				sql = "select cust_group_no,cust_group_desc from XXPENS_BI_MST_CUST_GROUP where cust_group_no is not null ";
				if(!Utils.isNull(code).equals("")){
					sql += " and cust_group_no like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and cust_group_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Customer_Group");
				
				sql += "order by cust_group_no \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("cust_group_no"),rs.getString("cust_group_no"),rs.getString("cust_group_desc")));
				}
			// Record Over display 
			}else if("Customer_id".equalsIgnoreCase(condType)){
				sql = "select customer_id,customer_code,customer_desc from XXPENS_BI_MST_CUSTOMER where customer_code is not null \n";	 
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and customer_code in ("+SAUtils.converToText("customer_code", code) +") \n";
					}
					else{
						sql += " and customer_code like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and customer_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Customer_id");

				sql += "order by customer_code \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("customer_id"),rs.getString("customer_code"),rs.getString("customer_code")+"-"+rs.getString("customer_desc")));
				}
			}else if("Brand".equalsIgnoreCase(condType)){
				sql = "select brand_no,brand_desc from XXPENS_BI_MST_BRAND where brand_no is not null \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and brand_no in ("+SAUtils.converToText("Brand", code) +") \n";
					}
					else{
						sql += " and brand_no like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and brand_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Brand");
				
				sql += "order by brand_no \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("brand_no"),rs.getString("brand_no"),rs.getString("brand_desc")));
				}
			}else if("Invoice_Date".equalsIgnoreCase(condType)){
				sql = "select invoice_date from XXPENS_BI_MST_INVOICE_DATE where invoice_date is not null ";
				if(!Utils.isNull(code).equals("")){
					Date date = Utils.parseToBudishDate(code, Utils.DD_MM_YYYY_WITH_SLASH);
					String chrisDateStr = Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
					sql += " and invoice_date = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
				}
				if(!Utils.isNull(desc).equals("")){
					Date date = Utils.parseToBudishDate(code, Utils.DD_MM_YYYY_WITH_SLASH);
					String chrisDateStr = Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
					sql += " and invoice_date = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
				}
				sql +="order by invoice_date \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					String dateStr = Utils.stringValue(rs.getDate("invoice_date",Calendar.getInstance(Locale.US)), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
					returnList.add(new References(dateStr,dateStr,dateStr));
				}
			}else if("SALES_ORDER_DATE".equalsIgnoreCase(condType)){
				sql = "select ORDER_DATE from XXPENS_BI_MST_ORDER_DATE where ORDER_DATE is not null ";
				if(!Utils.isNull(code).equals("")){
					Date date = Utils.parseToBudishDate(code, Utils.DD_MM_YYYY_WITH_SLASH);
					String chrisDateStr = Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
					sql += " and ORDER_DATE = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
				}
				if(!Utils.isNull(desc).equals("")){
					Date date = Utils.parseToBudishDate(code, Utils.DD_MM_YYYY_WITH_SLASH);
					String chrisDateStr = Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
					sql += " and ORDER_DATE = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
				}
				sql +="order by ORDER_DATE \n";
				
				logger.debug("SQL:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					String dateStr = Utils.stringValue(rs.getDate("ORDER_DATE",Calendar.getInstance(Locale.US)), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
					returnList.add(new References(dateStr,dateStr,dateStr));
				}
			}else if("Province".equalsIgnoreCase(condType)){
				sql = "select * from XXPENS_BI_MST_PROVINCE where province is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and province like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and province LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Province");
				
				sql += "order by province \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("province"),rs.getString("province"),rs.getString("province")));
				}
			}else if("AMPHOR".equalsIgnoreCase(condType)){
				sql = "select * from XXPENS_BI_MST_AMPHOR where amphor is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and amphor like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and amphor LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "AMPHOR");

				sql += "order by amphor \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("amphor"),rs.getString("amphor"),rs.getString("amphor")));
				}
			}else if("TAMBOL".equalsIgnoreCase(condType)){
				sql = "select * from XXPENS_BI_MST_TAMBOL WHERE tambol is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and tambol like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and tambol LIKE '%"+desc+"%' \n";
				}
				sql += "order by tambol \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("tambol"),rs.getString("tambol"),rs.getString("tambol")));
				}
			}
			else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
				sql = "select  DISTINCT  SALES_ORDER_NO from XXPENS_BI_SALES_ANALYSIS WHERE SALES_ORDER_NO is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and SALES_ORDER_NO like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and SALES_ORDER_NO LIKE '%"+desc+"%' \n";
				}
				sql += "order by SALES_ORDER_NO \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO")));
				}
			}
			
			else if("INVOICE_NO".equalsIgnoreCase(condType)){
				sql = "select  DISTINCT  INVOICE_NO from XXPENS_BI_SALES_ANALYSIS WHERE INVOICE_NO is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and INVOICE_NO like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and INVOICE_NO LIKE '%"+desc+"%' \n";
				}
				sql += "order by INVOICE_NO \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO")));
				}
			}

			// Over Display  display 100 record
			else if("SHIP_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "select  * from XXPENS_BI_MST_CUST_SHIP_ADDR WHERE CUSTOMER_SHIP_TO_ADDRESS is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and CUSTOMER_SHIP_TO_ADDRESS like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and CUSTOMER_SHIP_TO_ADDRESS LIKE '%"+desc+"%' \n";
				}
				sql +=" and rownum <= 100 \n";
				sql +=" order by CUSTOMER_SHIP_TO_ADDRESS \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("CUSTOMER_SHIP_TO_ADDRESS")));
				}
			}
			// Over Display  display 100 record
			else if("BILL_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "select * from XXPENS_BI_MST_CUST_BILL_ADDR WHERE CUSTOMER_BILL_TO_ADDRESS is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and CUSTOMER_BILL_TO_ADDRESS like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and CUSTOMER_BILL_TO_ADDRESS LIKE '%"+desc+"%' \n";
				}
				sql +=" and rownum <= 100 \n";
				sql +=" order by CUSTOMER_BILL_TO_ADDRESS \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("CUSTOMER_BILL_TO_ADDRESS")));
				}
			}
			
			logger.debug("SQL:"+sql);
		}catch(Exception e){
		   throw e;
		}finally{
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return returnList;
	}

	private  List<References> getConditionValueListModel4Role(Connection conn,HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<References> returnList = new ArrayList<References>();
		try{
			
			if("ALL".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
			}else if("inventory_item_id".equalsIgnoreCase(condType)){
				sql  = "SELECT  CODE ,TH_NAME FROM ad_reference where GROUP_NAME ='SKU' \n";
				sql += "order by ORDER_INDEX \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				while(rs.next()){
					returnList.add(new References(rs.getString("CODE"),rs.getString("CODE") ,rs.getString("TH_NAME")));
				}
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql  = "SELECT  CODE ,TH_NAME FROM ad_reference where GROUP_NAME ='CUST_CATE' \n";
				sql += "order by ORDER_INDEX \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				while(rs.next()){
					returnList.add(new References(rs.getString("CODE"),rs.getString("CODE") ,rs.getString("TH_NAME")));
				}
			}else if("Division".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
				
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql  = "SELECT  CODE ,TH_NAME FROM ad_reference where GROUP_NAME ='CUST_CATE' \n";
				sql += "order by ORDER_INDEX \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				while(rs.next()){
					returnList.add(new References(rs.getString("CODE"),rs.getString("CODE") ,rs.getString("TH_NAME")));
				}
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql  = "SELECT  DISTINCT SALES_CHANNEL_NO AS CODE ,SALES_CHANNEL_DESC AS TH_NAME FROM XXPENS_BI_MST_SALES_CHANNEL \n";
				sql += " union all \n";
				sql += "SELECT  CODE ,TH_NAME  FROM ad_reference where GROUP_NAME ='CUST_CATE' and code ='ALL' \n";
				sql += "order by  CODE \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				while(rs.next()){
					returnList.add(new References(rs.getString("CODE"),rs.getString("CODE") ,rs.getString("TH_NAME")));
				}
			}else if("Customer_Group".equalsIgnoreCase(condType)){
				sql  = "SELECT  CODE ,TH_NAME FROM ad_reference where GROUP_NAME ='CUST_CATE' \n";
				sql += "order by ORDER_INDEX \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				while(rs.next()){
					returnList.add(new References(rs.getString("CODE"),rs.getString("CODE") ,rs.getString("TH_NAME")));
				}
			// Record Over display 
			}else if("Customer_id".equalsIgnoreCase(condType)){
				sql  = "SELECT  CODE ,TH_NAME FROM ad_reference where GROUP_NAME ='CUST_CATE' \n";
				sql += "order by ORDER_INDEX \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				while(rs.next()){
					returnList.add(new References(rs.getString("CODE"),rs.getString("CODE") ,rs.getString("TH_NAME")));
				}
			}else if("Brand".equalsIgnoreCase(condType)){
				sql  = " select  brand_no as CODE,brand_desc as TH_NAME from XXPENS_BI_MST_BRAND where brand_no is not null \n";
				sql += " union all \n";
				sql += " SELECT  CODE ,TH_NAME  FROM ad_reference where GROUP_NAME ='CUST_CATE' and code ='ALL' \n";
				sql += " order by CODE \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					returnList.add(new References(rs.getString("code"),rs.getString("code"),rs.getString("TH_NAME")));
				}
			}else if("Invoice_Date".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
			}else if("SALES_ORDER_DATE".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
			}else if("Province".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
			}else if("AMPHOR".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
			}else if("TAMBOL".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
			}else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
			}else if("INVOICE_NO".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
			}
			
			logger.debug("SQL:"+sql);
		}catch(Exception e){
			logger.debug("SQL:"+sql);
		   throw e;
		}finally{
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return returnList;
	}
	
	public  List<References> getConditionValueListByParent(String condType,String code ,String desc,ConditionFilterBean filterBean)throws Exception{
			
		String sql = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<References> returnList = new ArrayList<References>();
		try{
			//logger.debug("panrentType1:"+panrentType1+",parentCode1:"+parentCode1);
			
				conn = DBConnection.getInstance().getConnection();
				
				//แบรนด์ --> สินค้า (SKU) OK
				if("inventory_item_id".equalsIgnoreCase(condType)){ // edit by tutiya r.
                    
					if(filterBean.getCurrCondNo().equals("2")){
						if ("Brand".equals(filterBean.getCondType1())){
							sql = "SELECT distinct u.INVENTORY_ITEM_ID,u.INVENTORY_ITEM_CODE,u.INVENTORY_ITEM_DESC "+
							" from XXPENS_BI_MST_ITEM u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.INVENTORY_ITEM_CODE is not null and t1.inventory_item_id=u.inventory_item_id \n"+ 
							" and t1.brand IN ("+SAUtils.converToText("Brand", filterBean.getCondCode1()) + ") \n";
						} else {
						    sql = "SELECT distinct INVENTORY_ITEM_ID,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC from XXPENS_BI_MST_ITEM where INVENTORY_ITEM_CODE is not null \n";
						}
					}else{
						sql = "SELECT distinct INVENTORY_ITEM_ID,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC from XXPENS_BI_MST_ITEM where INVENTORY_ITEM_CODE is not null \n";
					}
					
					if(!Utils.isNull(code).equals("")){
						sql += " and INVENTORY_ITEM_CODE like '"+code+"%' \n"; // modify by tutiya
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and INVENTORY_ITEM_DESC LIKE '%"+desc+"%' \n";
					}
					sql += "order by INVENTORY_ITEM_CODE \n";
					
					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("INVENTORY_ITEM_ID"),rs.getString("INVENTORY_ITEM_CODE") ,rs.getString("INVENTORY_ITEM_DESC")));
					}
					

					/*
					ประเภทขาย Customer_Category   + ภาคตามพนักงานขายย Sales_Channel ==>  พนักงานขายย Salesrep_id
					ดิวิชั่น   Division              + ภาคตามพนักงานขาย Sales_Channel  ==>  พนักงานขาย Salesrep_id	
					ภาคตามพนักงานขาย Sales_Channel  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
					ภาคตามพนักงานขาย Sales_Channel  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_id
					*/
				}else if("Salesrep_id".equalsIgnoreCase(condType)){
					
					//ดิวิชั่น  > พนักงานขาย OK
					if(filterBean.getCurrCondNo().equals("2")){
						//Cond 1
						 if (filterBean.getCondType1().equalsIgnoreCase("Division")){
							sql = "select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc "+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id=u.salesrep_id \n"+ 
							" and t1.division in ("+SAUtils.converToText("Division", filterBean.getCondCode1()) + ") \n";
							
						}else {
							sql = "select distinct salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
						}	
					
					 /*
						ประเภทขาย Customer_Category   + ภาคตามพนักงานขายย Sales_Channel ==>  พนักงานขายย Salesrep_id
						ดิวิชั่น   Division              + ภาคตามพนักงานขาย Sales_Channel  ==>  พนักงานขาย Salesrep_id	
						ภาคตามพนักงานขาย Sales_Channel  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
						ภาคตามพนักงานขาย Sales_Channel  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_id
					 */
					}else if(filterBean.getCurrCondNo().equals("3")){
						
						//ประเภทขาย Customer_Category   + ภาคตามพนักงานขายย Sales_Channel ==>  พนักงานขายย Salesrep_id
						if (filterBean.getCondType1().equalsIgnoreCase("Customer_Category") && filterBean.getCondType2().equalsIgnoreCase("Sales_Channel")){
							sql = "select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc "+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.Customer_Category in ("+SAUtils.converToText("Customer_Category", filterBean.getCondCode1()) + ")  \n"+
							" and t1.Sales_Channel in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ")  \n";
						
							//ดิวิชั่น   Division           + ภาคตามพนักงานขาย Sales_Channel  ==>  พนักงานขาย Salesrep_id	
						}else if (filterBean.getCondType1().equalsIgnoreCase("Division") && filterBean.getCondType2().equalsIgnoreCase("Sales_Channel")){
							sql = "select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc "+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.division in ("+SAUtils.converToText("Division", filterBean.getCondCode1()) + ") \n"+
							" and t1.Sales_Channel in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ") \n";
							
						//ภาคตามพนักงานขาย Sales_Channel  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id	
						}else if (filterBean.getCondType1().equalsIgnoreCase("Sales_Channel") && filterBean.getCondType2().equalsIgnoreCase("Customer_Category")){
							sql = "select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc "+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.Sales_Channel in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ") \n"+
							" and t1.Customer_Category in ("+SAUtils.converToText("Customer_Category", filterBean.getCondCode2()) + ")  \n";
						
						//ภาคตามพนักงานขาย Sales_Channel  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_i
						}else if (filterBean.getCondType1().equalsIgnoreCase("Sales_Channel") && filterBean.getCondType2().equalsIgnoreCase("Division")){
							sql = "select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc "+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.Sales_Channel in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ") \n"+
							" and t1.division in ("+SAUtils.converToText("Division", filterBean.getCondCode2()) + ")  \n";
						}else {
							sql = "select distinct salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
						}
					
					}else {
						sql = "select distinct salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
					}
					
					if(!Utils.isNull(code).equals("")){
						sql += " and salesrep_code like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and salesrep_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by salesrep_code \n";
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("salesrep_id"),rs.getString("salesrep_code"),rs.getString("salesrep_desc")));
					}	
					
				//กลุ่มร้านค้า(Customer_Group) --> ร้านค้า(Customer_id)
				}else if("Customer_id".equalsIgnoreCase(condType)){
					
					sql = "select customer_id,customer_code,customer_desc from XXPENS_BI_MST_CUSTOMER where customer_code is not null \n";	
					
					if(!Utils.isNull(code).equals("")){
						sql += " and customer_code like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and customer_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by customer_code \n";
					
					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("customer_id"),rs.getString("customer_code"),rs.getString("customer_code")+"-"+rs.getString("customer_desc")));
					}
				}else if("Sales_Channel".equalsIgnoreCase(condType)){
			
					sql = "select sales_channel_no,sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL where sales_channel_no is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						sql += " and sales_channel_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and sales_channel_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by sales_channel_no \n";
					
					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("sales_channel_no"),rs.getString("sales_channel_no"),rs.getString("sales_channel_desc")));
					}	
					
				}else if("Customer_Category".equalsIgnoreCase(condType)){
					sql = "select cust_cat_no,cust_cat_desc from XXPENS_BI_MST_CUST_CAT where cust_cat_no is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and cust_cat_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and cust_cat_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by cust_cat_no \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("cust_cat_no"),rs.getString("cust_cat_no"),rs.getString("cust_cat_desc")));
					}
				}else if("Division".equalsIgnoreCase(condType)){
					sql = "select div_no,div_desc from XXPENS_BI_MST_DIVISION where div_no is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and div_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and div_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by div_no \n";
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("div_no"),rs.getString("div_no"),rs.getString("div_desc")));
					}	

				}else if("Customer_Group".equalsIgnoreCase(condType)){
					sql = "select cust_group_no,cust_group_desc from XXPENS_BI_MST_CUST_GROUP where cust_group_no is not null ";
					if(!Utils.isNull(code).equals("")){
						sql += " and cust_group_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and cust_group_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by cust_group_no \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("cust_group_no"),rs.getString("cust_group_no"),rs.getString("cust_group_desc")));
					}
				
				}else if("Brand".equalsIgnoreCase(condType)){
					sql = "select brand_no,brand_desc from XXPENS_BI_MST_BRAND where brand_no is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and brand_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and brand_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by brand_no \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("brand_no"),rs.getString("brand_no"),rs.getString("brand_desc")));
					}
				}else if("Invoice_Date".equalsIgnoreCase(condType)){
					sql = "select invoice_date from XXPENS_BI_MST_INVOICE_DATE where invoice_date is not null ";
					if(!Utils.isNull(code).equals("")){
						Date date = Utils.parseToBudishDate(code, Utils.DD_MM_YYYY_WITH_SLASH);
						String chrisDateStr = Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql += " and invoice_date = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
					}
					if(!Utils.isNull(desc).equals("")){
						Date date = Utils.parseToBudishDate(code, Utils.DD_MM_YYYY_WITH_SLASH);
						String chrisDateStr = Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql += " and invoice_date = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
					}
					sql +="order by invoice_date \n";
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						String dateStr = Utils.stringValue(rs.getDate("invoice_date",Calendar.getInstance(Locale.US)), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
						returnList.add(new References(dateStr,dateStr,dateStr));
					}
				}else if("SALES_ORDER_DATE".equalsIgnoreCase(condType)){
					sql = "select ORDER_DATE from XXPENS_BI_MST_ORDER_DATE where ORDER_DATE is not null ";
					if(!Utils.isNull(code).equals("")){
						Date date = Utils.parseToBudishDate(code, Utils.DD_MM_YYYY_WITH_SLASH);
						String chrisDateStr = Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql += " and ORDER_DATE = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
					}
					if(!Utils.isNull(desc).equals("")){
						Date date = Utils.parseToBudishDate(code, Utils.DD_MM_YYYY_WITH_SLASH);
						String chrisDateStr = Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql += " and ORDER_DATE = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
					}
					sql +="order by ORDER_DATE \n";
					
					logger.debug("SQL:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						String dateStr = Utils.stringValue(rs.getDate("ORDER_DATE",Calendar.getInstance(Locale.US)), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
						returnList.add(new References(dateStr,dateStr,dateStr));
					}
				}else if("Province".equalsIgnoreCase(condType)){
					sql = "select * from XXPENS_BI_MST_PROVINCE where province is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and province like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and province LIKE '%"+desc+"%' \n";
					}
					sql += "order by province \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("province"),rs.getString("province"),rs.getString("province")));
					}
				}else if("AMPHOR".equalsIgnoreCase(condType)){
					sql = "select * from XXPENS_BI_MST_AMPHOR where amphor is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and amphor like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and amphor LIKE '%"+desc+"%' \n";
					}
					sql += "order by amphor \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("amphor"),rs.getString("amphor"),rs.getString("amphor")));
					}
				}else if("TAMBOL".equalsIgnoreCase(condType)){
					sql = "select * from XXPENS_BI_MST_TAMBOL WHERE tambol is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and tambol like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and tambol LIKE '%"+desc+"%' \n";
					}
					sql += "order by tambol \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("tambol"),rs.getString("tambol"),rs.getString("tambol")));
					}
				}
				
				else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
					sql = "select  DISTINCT  SALES_ORDER_NO from XXPENS_BI_SALES_ANALYSIS WHERE SALES_ORDER_NO is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and SALES_ORDER_NO like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and SALES_ORDER_NO LIKE '%"+desc+"%' \n";
					}
					sql += "order by SALES_ORDER_NO \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO")));
					}
				}
				
				else if("INVOICE_NO".equalsIgnoreCase(condType)){
					sql = "select  DISTINCT  INVOICE_NO from XXPENS_BI_SALES_ANALYSIS WHERE INVOICE_NO is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and INVOICE_NO like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and INVOICE_NO LIKE '%"+desc+"%' \n";
					}
					sql += "order by INVOICE_NO \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO")));
					}
				}
				
				/** Over Display  show 100 receord **/
				else if("SHIP_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
					sql = "select  * from XXPENS_BI_MST_CUST_SHIP_ADDR WHERE CUSTOMER_SHIP_TO_ADDRESS is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and CUSTOMER_SHIP_TO_ADDRESS like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and CUSTOMER_SHIP_TO_ADDRESS LIKE '%"+desc+"%' \n";
					}
					sql += " and rownum <= 100 \n";
					sql += "order by CUSTOMER_SHIP_TO_ADDRESS \n";
					ps = conn.prepareStatement(sql);
					while(rs.next()){
						returnList.add(new References(rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("CUSTOMER_SHIP_TO_ADDRESS")));
					}
				}
				/** Over Display  show 100 receord **/
				else if("BILL_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
					sql = "select  * from XXPENS_BI_MST_CUST_BILL_ADDR WHERE CUSTOMER_BILL_TO_ADDRESS is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and CUSTOMER_BILL_TO_ADDRESS like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and CUSTOMER_BILL_TO_ADDRESS LIKE '%"+desc+"%' \n";
					}
					sql += " and rownum <= 100 \n";
					sql += "order by CUSTOMER_BILL_TO_ADDRESS \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						returnList.add(new References(rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("CUSTOMER_BILL_TO_ADDRESS")));
					}
				}
				logger.debug("SQL:"+sql);
				
				
			}catch(Exception e){
			   throw e;
			}finally{
				if(conn != null){
					conn.close();conn=null;
				}
				if(ps != null){
				   ps.close();ps= null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}
			return returnList;
	}
	
	
	public  String getDesc(String condType,String code)throws Exception{
		String sql = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs= null;
		String desc = "";
		boolean exc = true;
		try{
			conn =DBConnection.getInstance().getConnection();
			if("inventory_item_id".equalsIgnoreCase(condType)){
				sql = "SELECT INVENTORY_ITEM_ID,(INVENTORY_ITEM_CODE|| '-' || INVENTORY_ITEM_DESC) as DESC_ from XXPENS_BI_MST_ITEM WHERE INVENTORY_ITEM_ID ='"+code+"'";
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql = "select cust_cat_no,cust_cat_desc as desc_ from XXPENS_BI_MST_CUST_CAT WHERE cust_cat_no ='"+code+"'";
			}else if("Division".equalsIgnoreCase(condType)){
				sql = "select div_no,div_desc as desc_ from XXPENS_BI_MST_DIVISION WHERE div_no ='"+code+"'";
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql = "select salesrep_id,(salesrep_code || '-' || salesrep_code) desc_ from XXPENS_BI_MST_SALESREP  WHERE salesrep_id='"+code+"'";
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql = "select sales_channel_no,sales_channel_desc as desc_ from XXPENS_BI_MST_SALES_CHANNEL ='"+code+"'";
			}else if("Customer_Group".equalsIgnoreCase(condType)){
				sql = "select cust_group_no,cust_group_desc as desc_ from XXPENS_BI_MST_CUST_GROUP WHERE cust_group_no='"+code+"'";
			}else if("Customer_id".equalsIgnoreCase(condType)){
				sql = "select customer_id,(customer_code || '-' ||customer_desc) desc_ from XXPENS_BI_MST_CUSTOMER WHERE customer_id ='"+code+"'";
			}else if("Brand".equalsIgnoreCase(condType)){
				sql = "select brand_no,brand_desc as desc_ from XXPENS_BI_MST_BRAND WHERE brand_no ='"+code+"'";
			}else if("Invoice_Date".equalsIgnoreCase(condType)){
				desc = code;
				exc = false;
			}else if("Province".equalsIgnoreCase(condType)){
				desc = code;
				exc = false;
			}else if("AMPHOR".equalsIgnoreCase(condType)){
				desc = code;
				exc = false;
			}else if("TAMBOL".equalsIgnoreCase(condType)){
				desc = code;
				exc = false;
			}else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
				desc = code;
				exc = false;
			}else if("INVOICE_NO".equalsIgnoreCase(condType)){
				desc = code;
				exc = false;
			}
			
			
			if(exc){
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					desc = rs.getString("desc_");
				}
			}
			if("".equals(desc)){
				desc = code;
			}
			return desc;
		}catch(Exception e){
		   throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
	}

}
