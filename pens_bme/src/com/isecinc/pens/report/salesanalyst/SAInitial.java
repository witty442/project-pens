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
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.salesanalyst.SAGenCondition;
import com.isecinc.pens.web.salesanalyst.SAUtils;
import com.isecinc.pens.web.salesanalyst.SecurityHelper;


public class SAInitial {
   
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
	public	 static String SUMMARY_TYPE_PERCENT = "PERCENT";
	
	public static final String TYPE_SEARCH_DAY = "DAY";
	public static final String TYPE_SEARCH_MONTH = "MONTH";
	public static final String TYPE_SEARCH_QUARTER = "QUARTER";
	public static final String TYPE_SEARCH_YEAR = "YEAR";
	public static final String TABLE_VIEW = "xxpens_bi_salesout_analysis_v";
	
	//For Prefix ColumnName CALL NO DUP 
	public static final String NO_DUP_PREFIX = "ND_";
	
	public static List<String> MULTI_SELECTION_LIST = new ArrayList<String>();
	SAGenCondition saGen = new SAGenCondition();
	SAUtils saU = new SAUtils();
	private static SAInitial salesAnalystProcess;
	
	public static SAInitial getInstance(){
		
		if(salesAnalystProcess == null){
			salesAnalystProcess = new SAInitial();
			return salesAnalystProcess;
		}
		return salesAnalystProcess;
	}

	static {
		logger.debug("Initail Param Sales Analisis");
		
		MULTI_SELECTION_LIST.add("Item");
		MULTI_SELECTION_LIST.add("Group");
		MULTI_SELECTION_LIST.add("Store");
		MULTI_SELECTION_LIST.add("Style");
		

		/** Column Of ORDER **/
		COLUMN_ORDER_MAP.put("QTY","QTY");
		COLUMN_ORDER_MAP.put("WHOLE_PRICE_BF","WHOLE_PRICE_BF");
		COLUMN_ORDER_MAP.put("RETAIL_PRICE_BF","RETAIL_PRICE_BF");
		
		/** Unit Column  **/
		UNIT_MAP.put("AMT","บาท");
		//UNIT_MAP.put("QTY","หีบ");
		//UNIT_MAP.put("QTY_S","หีบพิเศษ");
		
		/** Copmare Column  **/
		COMPARE_MAP.put("PERCENT","%");
		
		/** Summary Type*/
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_NONE , "ไม่เลือก");
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_SUM , "ยอดรวม");
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_AVG , "ค่าเฉลี่ย");
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_PERCENT , "เปอร์เซ็นต์");
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
			
			List<References> yearL = initYearList(conn," DESC");
			session.setAttribute("yearList", yearL);
			
			List<References> yearLSAC = initYearList(conn," ASC");
			session.setAttribute("yearListASC", yearLSAC);
			
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
				
				/** QUARTER MAP ****/
				QUARTER_MAP.put(yearKey+"1", "ไตรมาส 1/"+yearShow);
				QUARTER_MAP.put(yearKey+"2", "ไตรมาส 2/"+yearShow);
				QUARTER_MAP.put(yearKey+"3", "ไตรมาส 3/"+yearShow);
				QUARTER_MAP.put(yearKey+"4", "ไตรมาส 4/"+yearShow);
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
			//OLD CODE
			/*groupByList = SecurityHelper.filterDisplayColumnByUser(conn);
			session.setAttribute("groupByList", groupByList);
			*/
			//Group By 
			groupByList.add(new References("disp_1","Style"));
			groupByList.add(new References("disp_2","StoreNo+StoreName"));
			groupByList.add(new References("disp_3","StoreNo+StoreName+Style"));
			groupByList.add(new References("disp_4","SalesDate+StoreNo+StoreName"));
			groupByList.add(new References("disp_5","SalesDate+StoreNo+StoreName+Style"));
			groupByList.add(new References("disp_6","SalesDate+StoreNo+StoreName+Style+รหัสสินค้า"));
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
			conditionList.add(new References("-1","ไม่เลือก"));
			conditionList.add(new References("Customer","กลุ่มร้านค้า"));
			conditionList.add(new References("Store","ร้านค้า"));
			conditionList.add(new References("Style","Style"));
			conditionList.add(new References("Group","Group"));
			conditionList.add(new References("Item","รหัสสินค้า"));
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
			/** OLD Config **/
			dispColumnList.add(new References("0","ไม่เลือก"));
			dispColumnList.add(new References("QTY","จำนวนชิ้น"));/** INVOICED_AMT-RETURNED_AMT-DISCOUNT_AMT as NETAMT **/
			dispColumnList.add(new References("WHOLE_PRICE_BF","จำนวนเงิน(ขายส่ง) Exc.VAT"));
			dispColumnList.add(new References("RETAIL_PRICE_BF","จำนวนเงิน(ขายปลีกสุทธิ) Inc.VAT"));	
			session.setAttribute("dispColumnList", dispColumnList);
			
			/** Init unit Column List **/
			List<References> unitColumnList = new ArrayList<References>();
			r = new References("AMT",Utils.isNull(UNIT_MAP.get("AMT")));
			unitColumnList.add(r);
			r = new References("QTY",Utils.isNull(UNIT_MAP.get("QTY")));
			unitColumnList.add(r);
			r = new References("QTY_S",Utils.isNull(UNIT_MAP.get("QTY_S")));
			unitColumnList.add(r);
			
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
			
			
			/** init quarter **/
			/*List<References> profileList = new ArrayList<References>();
			r = new References("0","ไม่เลือก");
			profileList.add(r);
			r = new References("1","Profile 1");
			profileList.add(r);
			r = new References("2","Profile 2");
			profileList.add(r);
			r = new References("3","Profile 3");
			profileList.add(r);
			r = new References("4","Profile 4");
			profileList.add(r);
			r = new References("5","Profile 5");
			profileList.add(r);
			session.setAttribute("profileList", profileList);
			*/
			
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

	public  List<References> initYearList(Connection conn,String sort) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
        List<References> yearList = new ArrayList<References>();
		try{
			sql  = "SELECT A.* FROM ( \n";
			sql += "  SELECT DISTINCT SALES_YEAR from PENSBME_SALES_FROM_LOTUS  \n";
			sql += "  UNION \n";
			sql += "  SELECT DISTINCT SALES_YEAR from PENSBME_SALES_FROM_BIGC  \n";
			sql += ") A ORDER BY A.SALES_YEAR "+sort+"\n";
			
			logger.debug("sql:\n"+sql.toString());
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				yearList.add(new References(rs.getString("SALES_YEAR"), (rs.getInt("SALES_YEAR")+543)+""));
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
	public  StringBuffer genMainSql(Connection conn,User user,SABean salesBean,List<ConfigBean> colGroupList,String all) throws Exception{
		StringBuffer sql = new StringBuffer("");
		logger.debug("TypeSearch :"+salesBean.getTypeSearch() +"ColumnGroupList Size:"+colGroupList.size());
		
		if(TYPE_SEARCH_DAY.equalsIgnoreCase(salesBean.getTypeSearch()) && colGroupList.size() ==1 ){
			logger.debug("One Day ");
           
			String[] topColumn = saGen.genTopSQLSelectColumn(salesBean,salesBean.getGroupBy(),salesBean.getGroupBy());
			String sqlTopColumn = topColumn[0];
			String sqlAllColumn = topColumn[1];
			
			sql.append("\t"+  "SELECT  DISTINCT "+sqlAllColumn+" '1' as a \n");
			sql.append("\t"+"   FROM ( \n");
			sql.append(            saGen.genSubSQLByType(conn,user,salesBean, salesBean.getGroupBy(), salesBean.getGroupBy())+"\n");
			sql.append("\t\t\t     GROUP BY "+SAGenCondition.genGroupBySQL(salesBean.getGroupBy()) +"\n");
			sql.append("\t"+ ")S  \n ");
		
			/** Order BY */
			
			
		}else {
			logger.debug("More Day ");
			
			sql.append("SELECT A.* FROM ( \n");
			sql.append("\t SELECT * FROM( \n");
			
				sql.append("\t\t SELECT DISTINCT  \n");
				sql.append("\t\t"+ SAGenCondition.genColumnSQL(salesBean.getGroupBy()) +" \n ");
				sql.append("\t\t ,"+ SAGenCondition.genColumnCodeSQL(salesBean.getGroupBy()) +" \n ");
				sql.append("\t\t"+"FROM "+TABLE_VIEW+" S \n");
				sql.append("\t\t"+"WHERE 1=1 \n ");
				/** Condition Filter **/
				sql.append(""+saGen.genSqlWhereCondition(salesBean));
				/** Filter Displsy Data By User **/
				//sql.append("\t"+SecurityHelper.genWhereSqlFilterByUser(conn,user, null,"S."));
				
				//ALL MAIN SQL
				if(TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				    sql.append("\t\t"+" AND sales_year||sales_month IN("+all+") \n");
				    //sql.append(" AND sales_order_year IN('"+salesBean.getYear()+"') \n");
				    sql.append("\t\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}else if(TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
					sql.append("\t\t"+" AND sales_year||sales_quarter IN("+all+") \n");
					sql.append("\t\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}else if(TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
					sql.append("\t\t"+" AND sales_year IN("+all+") \n");
					sql.append("\t\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}
				else {
					Date date = Utils.parseToBudishDate(salesBean.getDay(), Utils.DD_MM_YYYY_WITH_SLASH);
					Date dateTo = Utils.parseToBudishDate(salesBean.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH);
					// 
					if(date != null && dateTo != null){
						sql.append("\t\t"+" AND  SALES_DATE >= to_date('"+Utils.stringValue(date, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("\t\t"+" AND  SALES_DATE <= to_date('"+Utils.stringValue(dateTo, Utils.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("\t\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
					}
				}
				
			sql.append("\t ) S \n");

			Date startDate = null ;
			String paramDay = salesBean.getDay(); // Temp Parameter Using for Set Back Date Parameter
			if(salesBean.getDay() != null){
				startDate = DateToolsUtil.convertStringToDate(salesBean.getDay());
			}
			
			for(int i=0;i<colGroupList.size();i++){
				ConfigBean configGroupBean = (ConfigBean)colGroupList.get(i);
				String colGroupName = configGroupBean.getName(); //01,02
				
				if(startDate != null && i != 0){
					startDate = DateUtils.addDays(startDate, 1); 
					salesBean.setDay(DateToolsUtil.convertToString(startDate));
				}
	
				sql.append("LEFT OUTER JOIN \n");
				
				/** Gen Sql By Group  **/
				sql.append(" (  \n");
				String[] topColumn = saGen.genTopSQLSelectColumn(salesBean,salesBean.getGroupBy(),colGroupName);
				String sqlTopColumn = topColumn[0];
				String sqlAllColumn = topColumn[1];
				
				sql.append("\t SELECT  "+sqlAllColumn+"  '1' as a \n");
				sql.append("\t FROM ( \n");
				sql.append("\t\t\t"+ saGen.genSubSQLByType(conn,user,salesBean, salesBean.getGroupBy(), colGroupName));
				sql.append("\t\t\t GROUP BY "+SAGenCondition.genColumnSQL(salesBean.getGroupBy())+"\n");
				sql.append("\t )SS  \n ");
				
				sql.append(" )M_"+colGroupName +" \n");
				//sql.append("ON S."+Utils.isNull(salesBean.getGroupBy())+"=M_"+colGroupName+"."+Utils.isNull(salesBean.getGroupBy()) +"\n");
				sql.append(SAGenCondition.genLeftJoinSQL(salesBean.getGroupBy(),colGroupName));
				
			}//for
			
			if(startDate != null)
				salesBean.setDay(paramDay);
			
			sql.append(") A \n");
			sql.append("ORDER BY \n"); 
			
			String order_type = salesBean.getOrder_type(); // DESC / ASC 
			String[] codesArr = SAGenCondition.genColumnAll(salesBean.getGroupBy()).split("\\,");
	    	if(codesArr.length==1){
	    		codesArr = new String[1];
	    		codesArr[0] = SAGenCondition.genColumnAll(salesBean.getGroupBy());
	    	}
	    	 
	    	 for(int i=0;i<codesArr.length;i++){
	    		 
	    		 if(order_type.equalsIgnoreCase("desc")){
	    			sql.append("COALESCE("+codesArr[i]+"_code,null) desc"); 
	    		 }else{
	    		    sql.append("COALESCE("+codesArr[i]+"_code,null) "); 
	    	     }
	    		 if(i != codesArr.length-1){
	    			 sql.append(",");
	    		 }
	    	}
		}
		
		logger.debug("*************** Generate SQL ******************** \n"+sql.toString()+"\n*****************************************************\n");
		return sql;
	}
	
	public  List<References> getConditionValueList4Role(String condType,String code ,String desc)throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getConditionValueListModel4Role(conn,condType, code,desc);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public  List<References> getConditionValueList4Role(Connection conn ,String condType,String code ,String desc)throws Exception{
		try{
			return getConditionValueListModel4Role(conn,condType, code,desc);
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
	public  List<DisplayBean> getConditionValueList(Connection conn,HttpServletRequest request,String condType)throws Exception{
		return getConditionValueListModel(conn,request,condType, null,null);
	}
	
	public  List<DisplayBean> getConditionValueList(HttpServletRequest request,String condType)throws Exception{
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
	
	public  List<DisplayBean> getConditionValueList(Connection conn,HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		return getConditionValueListModel(conn,request,condType, code,desc);
	}
	
	public  List<DisplayBean> getConditionValueList(HttpServletRequest request,String condType,String code ,String desc)throws Exception{
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
	
	public  List<DisplayBean> getConditionValueListModel(Connection conn,HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		String sql = "";
		int no = 0;
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<DisplayBean> returnList = new ArrayList<DisplayBean>();
		User user = (User) request.getSession().getAttribute("user");
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
						sql += " and INVENTORY_ITEM_CODE like '"+code+"%' \n"; 
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and INVENTORY_ITEM_DESC LIKE '%"+desc+"%' \n";
				}
				
				sql += "order by INVENTORY_ITEM_CODE \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("INVENTORY_ITEM_ID"),rs.getString("INVENTORY_ITEM_CODE") ,rs.getString("INVENTORY_ITEM_DESC")));
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
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Customer_Category","");
				
				sql += "order by cust_cat_no \n";
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("cust_cat_no"),rs.getString("cust_cat_no"),rs.getString("cust_cat_desc")));
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
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Division","");
				
				sql += "order by div_no \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("div_no"),rs.getString("div_no"),rs.getString("div_desc")));
				}	
				
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql  = "select salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
				sql += " and salesrep_code not like 'C%'  \n";
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
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Salesrep_id","");
				
				
				sql += "order by Salesrep_code \n";
				
				logger.debug("SQL:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("salesrep_id"),rs.getString("salesrep_code"),rs.getString("salesrep_desc")));
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
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Sales_Channel","");

				sql += "order by sales_channel_no \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("sales_channel_no"),rs.getString("sales_channel_no"),rs.getString("sales_channel_desc")));
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
					no++;
					returnList.add(new DisplayBean(no,rs.getString("cust_group_no"),rs.getString("cust_group_no"),rs.getString("cust_group_desc")));
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
					no++;
					returnList.add(new DisplayBean(no,rs.getString("customer_id"),rs.getString("customer_code"),rs.getString("customer_code")+"-"+rs.getString("customer_desc")));
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
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Brand","");
				
				sql += "order by brand_no \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("brand_no"),rs.getString("brand_no"),rs.getString("brand_desc")));
				}
				
			}else if("Brand_Group".equalsIgnoreCase(condType)){
		
                sql = "select distinct brand_group_no,brand_group_desc  from XXPENS_BI_MST_BRAND_GROUP where brand_group_no is not null \n";
				
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and brand_group_no in ("+SAUtils.converToText("Brand", code) +") \n";
					}
					else{
						sql += " and brand_group_no like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and brand_group_desc LIKE '%"+desc+"%' \n";
				}
				
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Brand");
				
				sql += "order by brand_group_no \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("brand_group_no"),rs.getString("brand_group_no"),rs.getString("brand_group_desc")));
				}
				
			 }else if("SUBBRAND".equalsIgnoreCase(condType)){
					
	                sql = "select distinct subbrand_no,subbrand_desc  from XXPENS_BI_MST_SUBBRAND where subbrand_no is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						if(code.indexOf(",") > -1){
							sql += " and subbrand_no in ("+SAUtils.converToText("SubBrand", code) +") \n";
						}
						else{
							sql += " and subbrand_no like '"+code+"%' \n";
						}
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and subbrand_desc LIKE '%"+desc+"%' \n";
					}
					
					/** filter by user **/
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "subbrand","");
					
					sql += "order by subbrand_no \n";
					
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("subbrand_no"),rs.getString("subbrand_no"),rs.getString("subbrand_desc")));
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
					no++;
					String dateStr = Utils.stringValue(rs.getDate("invoice_date",Calendar.getInstance(Locale.US)), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
					
					returnList.add(new DisplayBean(no,dateStr,dateStr,dateStr));
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
					no++;
					String dateStr = Utils.stringValue(rs.getDate("ORDER_DATE",Calendar.getInstance(Locale.US)), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
					returnList.add(new DisplayBean(no,dateStr,dateStr,dateStr));
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
					no++;
					returnList.add(new DisplayBean(no,rs.getString("province"),rs.getString("province"),rs.getString("province")));
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
					no++;
					returnList.add(new DisplayBean(no,rs.getString("amphor"),rs.getString("amphor"),rs.getString("amphor")));
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
					no++;
					returnList.add(new DisplayBean(no,rs.getString("tambol"),rs.getString("tambol"),rs.getString("tambol")));
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
					no++;
					returnList.add(new DisplayBean(no,rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO")));
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
					no++;
					returnList.add(new DisplayBean(no,rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO")));
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
					no++;
					returnList.add(new DisplayBean(no,rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("CUSTOMER_SHIP_TO_ADDRESS")));
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
					no++;
					returnList.add(new DisplayBean(no,rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("CUSTOMER_BILL_TO_ADDRESS")));
				}
			}
			//Organization 
			else if("Organization_id".equalsIgnoreCase(condType)){
				sql = "select  * from XXPENS_BI_MST_ORGANIZATION WHERE Organization_code is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and Organization_code like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and Organization_code LIKE '%"+desc+"%' \n";
				}
				sql += "order by Organization_code \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("Organization_id"),rs.getString("Organization_code"),rs.getString("Organization_code")));
				}
			}
			
			//Order Type
			else if("Order_type_id".equalsIgnoreCase(condType)){
				sql = "select  * from XXPENS_BI_MST_ORDER_TYPE WHERE Order_type_id is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and order_type_id like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and order_type_name LIKE '%"+desc+"%' \n";
				}
				sql += "order by order_type_name \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("Order_type_id"),rs.getString("Order_type_id"),rs.getString("Order_type_name")+"-"+rs.getString("Order_type_cat")));
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

	private  List<References> getConditionValueListModel4Role(Connection conn,String condType,String code ,String desc)throws Exception{
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<References> returnList = new ArrayList<References>();
		try{
			
			if("ALL".equalsIgnoreCase(condType)){
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
				
			}else if("Brand".equalsIgnoreCase(condType)){
				sql  = " select  brand_no as CODE,brand_desc as DESC1 from XXPENS_BI_MST_BRAND where brand_no is not null ";
				if( !Utils.isNull(code).equals("")){
					sql +=" and brand_no like '%"+code+"%' \n";
				}
				if( !Utils.isNull(desc).equals("")){
					sql +=" and brand_desc like '%"+desc+"%' \n";
				}
				sql += " order by brand_no \n";
				logger.debug(sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
				while(rs.next()){
					returnList.add(new References(rs.getString("code"),rs.getString("code"),rs.getString("DESC1")));
				}
		
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql  = "SELECT CUST_CAT_NO as CODE, CUST_CAT_DESC as DESC1,CREATE_DATE FROM XXPENS_BI_MST_CUST_CAT where 1=1  ";
				if( !Utils.isNull(code).equals("")){
					sql +=" and CUST_CAT_NO like '%"+code+"%' \n";
				}
				if( !Utils.isNull(desc).equals("")){
					sql +=" and CUST_CAT_DESC like '%"+desc+"%' \n";
				}
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
				while(rs.next()){
					returnList.add(new References(rs.getString("CODE"),rs.getString("CODE") ,rs.getString("DESC1")));
				}
			}else if("Division".equalsIgnoreCase(condType)){
				sql  = " SELECT DIV_NO as CODE ,DIV_DESC as DESC1 ,CREATE_DATE FROM PENSBI.XXPENS_BI_MST_DIVISION WHERE 1=1 ";
				if( !Utils.isNull(code).equals("")){
					sql +=" and DIV_NO like '"+code+"%' \n";
				}
				if( !Utils.isNull(desc).equals("")){
					sql +=" and DIV_DESC like '%"+desc+"%' \n";
				}
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
				while(rs.next()){
					returnList.add(new References(rs.getString("code"),rs.getString("code") ,rs.getString("DESC1")));
				}
				
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql  = "SELECT SALESREP_ID as ID, SALESREP_CODE as CODE, SALESREP_DESC as DESC1, CREATE_DATE FROM PENSBI.XXPENS_BI_MST_SALESREP WHERE 1=1 \n";
				if( !Utils.isNull(code).equals("")){
					sql +=" and SALESREP_CODE like '%"+code+"%' \n";
				}
				if( !Utils.isNull(desc).equals("")){
					sql +=" and SALESREP_DESC like '%"+desc+"%' \n";
				}
				sql += "order by SALESREP_CODE  \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
				while(rs.next()){
					returnList.add(new References(rs.getString("ID"),rs.getString("CODE") ,rs.getString("DESC1")));
				}
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql  = "SELECT  DISTINCT SALES_CHANNEL_NO AS CODE ,SALES_CHANNEL_DESC AS DESC1 FROM XXPENS_BI_MST_SALES_CHANNEL WHERE 1=1 \n";
				if( !Utils.isNull(code).equals("")){
					sql +=" and SALES_CHANNEL_NO like '%"+code+"%' \n";
				}
				if( !Utils.isNull(desc).equals("")){
					sql +=" and SALES_CHANNEL_DESC like '%"+desc+"%' \n";
				}
				sql += "order by  CODE \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				returnList.add(new References("ALL","ALL",SAConstants.MSG_ALL_TH));
				while(rs.next()){
					returnList.add(new References(rs.getString("CODE"),rs.getString("CODE") ,rs.getString("DESC1")));
				}
	
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
	
	public  List<DisplayBean> getConditionValueListByParent(User user,String condType,String code ,String desc,ConditionFilterBean filterBean)throws Exception{
			
		String sql = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<DisplayBean> returnList = new ArrayList<DisplayBean>();
		int no = 0;
		boolean debug = true;
		try{
			//logger.debug("panrentType1:"+panrentType1+",parentCode1:"+parentCode1);
			
				conn = DBConnection.getInstance().getConnection();
				
				//แบรนด์ --> สินค้า (SKU) OK
				//Brand_Group->Brand->Sku
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
					}else if(filterBean.getCurrCondNo().equals("3")){
						if (filterBean.getCondType1().equalsIgnoreCase("Brand_Group") && filterBean.getCondType2().equalsIgnoreCase("Brand")){
								sql = " \n SELECT distinct u.INVENTORY_ITEM_ID,u.INVENTORY_ITEM_CODE,u.INVENTORY_ITEM_DESC "+
								" from XXPENS_BI_MST_ITEM u, xxpens_bi_sales_analysis_v t1 \n"+
								" where u.INVENTORY_ITEM_CODE is not null and t1.inventory_item_id=u.inventory_item_id \n"+ 
								" and t1.brand in (  \n"+
							    "   select brand_no from XXPENS_BI_MST_BRAND where brand_no is not null \n"+
								"   and brand_no in ( \n" +
										" select distinct brand_no  from XXPENS_BI_MST_BRAND_GROUP where brand_group_no is not null \n" +
										"   and brand_group_no in ("+SAUtils.converToText("Brand_Group", filterBean.getCondCode1())+") \n"+
										" ) \n "+
							   "   and brand_no IN ("+SAUtils.converToText("Brand", filterBean.getCondCode2()) + ") \n"+
								"  ) \n";

						} else {
							 sql = "SELECT distinct INVENTORY_ITEM_ID,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC from XXPENS_BI_MST_ITEM where INVENTORY_ITEM_CODE is not null \n";
						}
					}else{
						sql = "SELECT distinct INVENTORY_ITEM_ID,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC from XXPENS_BI_MST_ITEM where INVENTORY_ITEM_CODE is not null \n";
					}
					
					if(!Utils.isNull(code).equals("")){
						sql += " and INVENTORY_ITEM_CODE like '"+code+"%' \n"; 
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and INVENTORY_ITEM_DESC LIKE '%"+desc+"%' \n";
					}
					sql += "order by INVENTORY_ITEM_CODE \n";
					
					
					if(debug)logger.debug("sql:"+sql);
					
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("INVENTORY_ITEM_ID"),rs.getString("INVENTORY_ITEM_CODE") ,rs.getString("INVENTORY_ITEM_DESC")));
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
							sql += " and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
							//ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
						}else if (filterBean.getCondType1().equalsIgnoreCase("Customer_Category") ){
							sql = "select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc "+
							      " from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							      " where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							      " and t1.Customer_Category in ("+SAUtils.converToText("Customer_Category", filterBean.getCondCode1()) + ")  \n";
							sql += " and u.salesrep_code like '"+SAUtils.getCustomerCatagoryFlag(filterBean.getCondCode1())+"%'  \n";
							sql += " and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
						}else {
							sql = "select distinct salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
							sql += " and salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","");
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
							" and t1.Sales_Channel in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ")  \n"+
							" and substr(u.salesrep_code,2,1) in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ")  \n"+
							" and u.salesrep_code like '"+SAUtils.getCustomerCatagoryFlag(filterBean.getCondCode1())+"%'  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
							//ดิวิชั่น   Division           + ภาคตามพนักงานขาย Sales_Channel  ==>  พนักงานขาย Salesrep_id	
						}else if (filterBean.getCondType1().equalsIgnoreCase("Division") && filterBean.getCondType2().equalsIgnoreCase("Sales_Channel")){
							sql = "select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc "+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.division in ("+SAUtils.converToText("Division", filterBean.getCondCode1()) + ") \n"+
							" and t1.Sales_Channel in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ") \n"+
							" and substr(u.salesrep_code,2,1) in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ")  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
						//ภาคตามพนักงานขาย Sales_Channel  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id	
						}else if (filterBean.getCondType1().equalsIgnoreCase("Sales_Channel") && filterBean.getCondType2().equalsIgnoreCase("Customer_Category")){
							sql = "select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc "+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.Sales_Channel in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ") \n"+
							" and t1.Customer_Category in ("+SAUtils.converToText("Customer_Category", filterBean.getCondCode2()) + ")  \n"+
							" and substr(u.salesrep_code,2,1) in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ")  \n"+
							" and u.salesrep_code like '"+SAUtils.getCustomerCatagoryFlag(filterBean.getCondCode2())+"%'  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
						
						//ภาคตามพนักงานขาย Sales_Channel  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_i
						}else if (filterBean.getCondType1().equalsIgnoreCase("Sales_Channel") && filterBean.getCondType2().equalsIgnoreCase("Division")){
							sql = "select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc "+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.Sales_Channel in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ") \n"+
							" and t1.division in ("+SAUtils.converToText("Division", filterBean.getCondCode2()) + ")  \n"+
							" and substr(u.salesrep_code,2,1) in ("+SAUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ")  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
						}else {
							sql = "select distinct salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
							sql +=" and salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","");
						}
					
					}else {
						sql = " select distinct salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
						sql +=" and salesrep_code not like 'C%'  \n";
						//Filter By Role User
						sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
					}
					
					if(!Utils.isNull(code).equals("")){
						sql += " and salesrep_code like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and salesrep_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by salesrep_code \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("salesrep_id"),rs.getString("salesrep_code"),rs.getString("salesrep_desc")));
					}	
					
				//กลุ่มร้านค้า(Customer_Group) --> ร้านค้า(Customer_id)
				}else if("Customer_id".equalsIgnoreCase(condType)){
					if(filterBean.getCurrCondNo().equals("2")){
						//Cond 1
						 if (filterBean.getCondType1().equalsIgnoreCase("Customer_Group")){
							sql = "select distinct u.customer_id,u.customer_code,u.customer_desc "+
							      " from XXPENS_BI_MST_CUSTOMER u, xxpens_bi_sales_analysis_v t1 \n"+
							      " where u.customer_id is not null and t1.customer_id=u.customer_id \n"+ 
							      " and t1.customer_group in ("+SAUtils.converToText("Customer_Group", filterBean.getCondCode1()) + ") \n";
					
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Customer_id","u.");
						 }else{
							sql = "select u.customer_id,u.customer_code,u.customer_desc from XXPENS_BI_MST_CUSTOMER u where u.customer_code is not null \n";	
						 }
					}else{
                        sql = "select u.customer_id,u.customer_code,u.customer_desc from XXPENS_BI_MST_CUSTOMER u where u.customer_code is not null \n";	
					}
					
					if(!Utils.isNull(code).equals("")){
						sql += " and u.customer_code like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and u.customer_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by u.customer_code \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("customer_id"),rs.getString("customer_code"),rs.getString("customer_code")+"-"+rs.getString("customer_desc")));
					}
				}else if("Sales_Channel".equalsIgnoreCase(condType)){
			
					sql = "select sales_channel_no,sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL where sales_channel_no is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						sql += " and sales_channel_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and sales_channel_desc LIKE '%"+desc+"%' \n";
					}
					
					//Filter By Role User
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Sales_Channel","");
					
					sql += "order by sales_channel_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("sales_channel_no"),rs.getString("sales_channel_no"),rs.getString("sales_channel_desc")));
					}	
					
				}else if("Customer_Category".equalsIgnoreCase(condType)){
					sql = "select cust_cat_no,cust_cat_desc from XXPENS_BI_MST_CUST_CAT where cust_cat_no is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and cust_cat_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and cust_cat_desc LIKE '%"+desc+"%' \n";
					}
					//Filter By Role User
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Customer_Category","");
					
					sql += "order by cust_cat_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("cust_cat_no"),rs.getString("cust_cat_no"),rs.getString("cust_cat_desc")));
					}
				}else if("Division".equalsIgnoreCase(condType)){
					sql = "select div_no,div_desc from XXPENS_BI_MST_DIVISION where div_no is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and div_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and div_desc LIKE '%"+desc+"%' \n";
					}
					//Filter By Role User
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Division","");
					
					sql += "order by div_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("div_no"),rs.getString("div_no"),rs.getString("div_desc")));
					}	

				//กลุ่มร้านค้า
				}else if("Customer_Group".equalsIgnoreCase(condType)){
					sql = "select cust_group_no,cust_group_desc from XXPENS_BI_MST_CUST_GROUP where cust_group_no is not null ";
					if(!Utils.isNull(code).equals("")){
						sql += " and cust_group_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and cust_group_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by cust_group_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("cust_group_no"),rs.getString("cust_group_no"),rs.getString("cust_group_desc")));
					}
				
				}else if("Brand".equalsIgnoreCase(condType)){
					if(filterBean.getCurrCondNo().equals("2")){
						//Cond 1
						 if (filterBean.getCondType1().equalsIgnoreCase("Brand_Group")){
							
							sql = "select brand_no,brand_desc from XXPENS_BI_MST_BRAND where brand_no is not null \n";
							sql += " and brand_no in ( \n" +
									" select distinct brand_no  from XXPENS_BI_MST_BRAND_GROUP where brand_group_no is not null \n" +
									"   and brand_group_no in ("+SAUtils.converToText("Brand_Group", filterBean.getCondCode1())+") \n"+
									" ) \n ";
						 }else{
							 sql = "select brand_no,brand_desc from XXPENS_BI_MST_BRAND where brand_no is not null \n";
						 }
					}else{
						sql = "select brand_no,brand_desc from XXPENS_BI_MST_BRAND where brand_no is not null \n";
					}
					
					//Condition
					if(!Utils.isNull(code).equals("")){
						sql += " and brand_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and brand_desc LIKE '%"+desc+"%' \n";
					}
					//Filter By Role User
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Brand","");
					sql += "order by brand_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("brand_no"),rs.getString("brand_no"),rs.getString("brand_desc")));
					}
					
				}else if("Brand_Group".equalsIgnoreCase(condType)){
					
	                sql = "select distinct brand_group_no,brand_group_desc  from XXPENS_BI_MST_BRAND_GROUP where brand_group_no is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						if(code.indexOf(",") > -1){
							sql += " and brand_group_no in ("+SAUtils.converToText("Brand", code) +") \n";
						}
						else{
							sql += " and brand_group_no like '"+code+"%' \n";
						}
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and brand_group_desc LIKE '%"+desc+"%' \n";
					}
					
					/** filter by user **/
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Brand","");
					
					sql += "order by brand_group_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("brand_group_no"),rs.getString("brand_group_no"),rs.getString("brand_group_desc")));
					}
	            }else if("SUBBRAND".equalsIgnoreCase(condType)){
					
	                sql = "select distinct subbrand_no,subbrand_desc  from XXPENS_BI_MST_SUBBRANDwhere subbrand_no is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						if(code.indexOf(",") > -1){
							sql += " and subbrand_no in ("+SAUtils.converToText("SubBrand", code) +") \n";
						}
						else{
							sql += " and subbrand_no like '"+code+"%' \n";
						}
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and subbrand_desc LIKE '%"+desc+"%' \n";
					}
					
					/** filter by user **/
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Brand","");
					
					sql += "order by subbrand_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("subbrand_no"),rs.getString("subbrand_no"),rs.getString("subbrand_desc")));
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
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						String dateStr = Utils.stringValue(rs.getDate("invoice_date",Calendar.getInstance(Locale.US)), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
						returnList.add(new DisplayBean(no,dateStr,dateStr,dateStr));
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
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						String dateStr = Utils.stringValue(rs.getDate("ORDER_DATE",Calendar.getInstance(Locale.US)), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
						returnList.add(new DisplayBean(no,dateStr,dateStr,dateStr));
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
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("province"),rs.getString("province"),rs.getString("province")));
					}
				}else if("AMPHOR".equalsIgnoreCase(condType)){
					
					if(filterBean.getCurrCondNo().equals("2")){
						//Cond 1
						 if (filterBean.getCondType1().equalsIgnoreCase("Province")){
							sql = "select distinct u.* "+
							      " from XXPENS_BI_MST_AMPHOR u, xxpens_bi_sales_analysis_v t1 \n"+
							      " where u.amphor is not null and t1.amphor = u.amphor \n"+ 
							      " and t1.province in ("+SAUtils.converToText("Province", filterBean.getCondCode1()) + ") \n";
					
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "AMPHOR","u.");
						 }else{
							 sql = "select u.* from XXPENS_BI_MST_AMPHOR u where u.amphor is not null \n";
						 }
					}else{
						sql = "select u.* from XXPENS_BI_MST_AMPHOR u where u.amphor is not null \n";	
					}

					if(!Utils.isNull(code).equals("")){
						sql += " and u.amphor like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and u.amphor LIKE '%"+desc+"%' \n";
					}
					sql += "order by u.amphor \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("amphor"),rs.getString("amphor"),rs.getString("amphor")));
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
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("tambol"),rs.getString("tambol"),rs.getString("tambol")));
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
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO")));
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
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO")));
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
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("CUSTOMER_SHIP_TO_ADDRESS")));
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
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("CUSTOMER_BILL_TO_ADDRESS")));
					}
				}
				
				//Organization 
				else if("Organization_id".equalsIgnoreCase(condType)){
					sql = "select  * from XXPENS_BI_MST_ORGANIZATION WHERE Organization_code is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and Organization_code like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and Organization_code LIKE '%"+desc+"%' \n";
					}
					sql += "order by Organization_code \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("Organization_id"),rs.getString("Organization_code"),rs.getString("Organization_code")));
					}
				}
				
				//Order Type
				else if("Order_type_id".equalsIgnoreCase(condType)){
					sql = "select  * from XXPENS_BI_MST_ORDER_TYPE WHERE Order_type_id is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and order_type_id like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and order_type_name LIKE '%"+desc+"%' \n";
					}
					sql += "order by order_type_name \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("Order_type_id"),rs.getString("Order_type_id"),rs.getString("Order_type_name")+"-"+rs.getString("Order_type_cat")));
					}
				}
	
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
	
	public static String getDesc(Connection conn,String condType,String code)throws Exception{
		return getDescModel(conn,condType, code);
	}
	
	public  String getDescX(String condType,String code)throws Exception{
		Connection conn = null;
		try{
			conn =DBConnection.getInstance().getConnection();
			return getDescModel(conn,condType, code);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	
	private  static String getDescModel(Connection conn,String condType,String code)throws Exception{
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs= null;
		String desc = "";
		boolean exc = true;
		try{
			logger.debug("condType["+condType+"]");
			
			if("inventory_item_id".equalsIgnoreCase(condType)){
				sql = "SELECT INVENTORY_ITEM_ID,(INVENTORY_ITEM_CODE|| '-' || INVENTORY_ITEM_DESC) as DESC_ from XXPENS_BI_MST_ITEM WHERE INVENTORY_ITEM_ID ='"+code+"'";
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql = "select cust_cat_no,cust_cat_desc as desc_ from XXPENS_BI_MST_CUST_CAT WHERE cust_cat_no ='"+code+"'";
			}else if("Division".equalsIgnoreCase(condType)){
				sql = "select div_no,div_desc as desc_ from XXPENS_BI_MST_DIVISION WHERE div_no ='"+code+"'";
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql = "select salesrep_id,(salesrep_code || '-' || salesrep_code) desc_ from XXPENS_BI_MST_SALESREP  WHERE salesrep_id='"+code+"'";
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql  = "select sales_channel_no,sales_channel_desc as desc_ from XXPENS_BI_MST_SALES_CHANNEL where ";
				sql += "sales_channel_no in ("+SAUtils.converToText("Sales_Channel", code) +")";
				
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
				
				while(rs.next()){
					desc += rs.getString("desc_")+",";
				}
				
				if( !Utils.isNull(desc).equals("") && Utils.isNull(desc).indexOf(",") != -1){
					desc = desc.substring(0,Utils.isNull(desc).length()-1);
				}
			}
			if("".equals(desc)){
				desc = code;
			}
			return desc;
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
	}

}
