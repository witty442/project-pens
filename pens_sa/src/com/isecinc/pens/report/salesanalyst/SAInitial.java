package com.isecinc.pens.report.salesanalyst;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.salesanalyst.helper.SAGenCondition;
import com.isecinc.pens.report.salesanalyst.helper.SAUtils;
import com.isecinc.pens.report.salesanalyst.helper.SecurityHelper;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

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
	public	 static String SUMMARY_TYPE_SUM_CONTRIBUTE = "SUM_CONTRIBUTE";
	public	 static String SUMMARY_TYPE_AVG = "AVG";
	public	 static String SUMMARY_TYPE_PERCENT = "PERCENT";
	
	public static final String TYPE_SEARCH_DAY = "DAY";
	public static final String TYPE_SEARCH_MONTH = "MONTH";
	public static final String TYPE_SEARCH_QUARTER = "QUARTER";
	public static final String TYPE_SEARCH_YEAR = "YEAR";
	public static final String TABLE_VIEW = "XXPENS_BI_SALES_ANALYSIS_V";
	
	public static final String COLUMN_TEXT_NA ="N/A";
	public static final String COLUMN_NUMBER_NA ="0";
	
	//For Prefix ColumnName CALL NO DUP 
	public static final String NO_DUP_PREFIX = "ND_";
	
	public static final int MAX_SHOW_CUSTOMER = 500;
	
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
		logger.debug("Initail Param Sales Analysis");
		
		MULTI_SELECTION_LIST.add("inventory_item_id");
		MULTI_SELECTION_LIST.add("Brand");
		MULTI_SELECTION_LIST.add("Customer_id");
		MULTI_SELECTION_LIST.add("Sales_Channel");
		MULTI_SELECTION_LIST.add("Salesrep_id");
		MULTI_SELECTION_LIST.add("Brand_Group");
		MULTI_SELECTION_LIST.add("Organization_id");
		MULTI_SELECTION_LIST.add("Order_type_id");
		MULTI_SELECTION_LIST.add("SUBBRAND");
		MULTI_SELECTION_LIST.add("Division");
		MULTI_SELECTION_LIST.add("SALES_ZONE");
		MULTI_SELECTION_LIST.add("CUSTOMER_CLASS_CODE");
		
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
		COLUMN_INVOICE_MAP.put("CALL_NEW","CALL_NEW");
		
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
		DISP_COL_MAP.put("CALL_NEW", "CALL NEW");/**Sum Customer no dup in prev criteria **/
		DISP_COL_MAP.put("CALL", "CALL");/**Sum Customer no dup in criteria**/
		
		/** Unit Column  **/
		UNIT_MAP.put("AMT","บาท");
		UNIT_MAP.put("QTY","หีบ");
		UNIT_MAP.put("QTY_S","หีบพิเศษ");
		
		/** Compare Column  **/
		COMPARE_MAP.put("PERCENT","%");
		
		/** Summary Type*/
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_NONE , "ไม่เลือก");
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_SUM , "ยอดรวม");
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_AVG , "ค่าเฉลี่ย");
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_PERCENT , "เปอร์เซ็นต์");
		SUMMARY_TYPE_MAP.put(SUMMARY_TYPE_SUM_CONTRIBUTE , "ยอดรวม , % Contribute");
	}
	
	private String maxOrderedDate = "";
	private String maxOrderedTime ="";
	
	public  void initSession(HttpServletRequest requestWeb,User user) {
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
			r = new References("-1","ไม่เลือก");
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
			dispColumnList.add(new References("CALL_NEW", "CALL NEW"));/**Sum Customer ID  no dup in prev criteria**/
			dispColumnList.add(new References("CALL", "CALL"));/**Sum Customer ID  **/
			
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
			/*Iterator sumIter = SUMMARY_TYPE_MAP.keySet().iterator();
			while(sumIter.hasNext()){
				String key = (String)sumIter.next();
				r = new References(key,Utils.isNull(SUMMARY_TYPE_MAP.get(key)));
				summaryTypeList.add(r);
			}
			// Collections.sort(summaryTypeList, new SummaryTypeComparator());
			*/
			summaryTypeList.add(new References("","ไม่เลือก"));
			summaryTypeList.add(new References(SUMMARY_TYPE_SUM,"ยอดรวม"));
			summaryTypeList.add(new References(SUMMARY_TYPE_AVG,"ค่าเฉลี่ย"));
			summaryTypeList.add(new References(SUMMARY_TYPE_PERCENT,"เปอร์เซ็นต์"));
			summaryTypeList.add(new References(SUMMARY_TYPE_SUM_CONTRIBUTE,"ยอดรวม , % Contribute"));
			
			session.setAttribute("summaryTypeList", summaryTypeList);
			
			
			/** init quarter **/
			List<References> profileList = new ArrayList<References>();
			r = new References("0","ไม่เลือก");
			profileList.add(r);
			/*
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
			session.setAttribute("profileList", profileList);*/
			
			//new Code
			profileList.addAll(initProfileList(conn, user.getId()));
			session.setAttribute("profileList", profileList);
			
			
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
			sql = "SELECT DISTINCT ORDER_YEAR from XXPENS_BI_MST_ORDER_DATE ORDER BY ORDER_YEAR "+sort;
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
	
	public static List<References> initProfileList(Connection conn,int userId) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
        List<References> profileList = new ArrayList<References>();
        String profileName ="";
        int maxProfileId= 0;
		try{
			sql = "SELECT * from pensbi.c_user_profile where user_id ="+userId +" and report_name ='SalesAnalyst' order by profile_id asc ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				if( !Utils.isNull(rs.getString("profile_name")).equals("")){
				    profileName = Utils.isNull(rs.getString("profile_name"));
				}else{
					profileName = "Profile " +rs.getString("profile_id");
				}
				profileList.add(new References(rs.getString("profile_id"), profileName));
				maxProfileId = rs.getInt("profile_id");
			}
			//add to show 5 profile
			int diff = 5-profileList.size();
			for(int i=0;i<diff;i++){
				maxProfileId++;
				profileList.add(new References(String.valueOf(maxProfileId), "Profile "+maxProfileId));
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
		return profileList;
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
	public  StringBuffer genMainSql(Connection conn,User user,SABean salesBean
			,List<ConfigBean> colGroupList,CriteriaBean criBean ) throws Exception{
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
			sql.append("\t"+  "SELECT  DISTINCT "+sqlAllColumn+" \n");
			sql.append("\t"+   saGen.genSQLGetDesc(salesBean.getGroupBy())+"");
			sql.append("\t"+   saGen.genSQLGetCode(salesBean.getGroupBy())+"");
			sql.append("\t"+   " '1' as a \n");
			sql.append("\t"+"FROM ( \n");
			sql.append("\t"+"SELECT \n ");
			/** Gen Select Column By Disp Column **/
			sql.append("\t"+  sqlTopColumn +"\n");
			
			sql.append("\t"+  " '1' as a \n");
            sql.append("\t"+  " FROM ( \n");
            
            if(isColumnOrder){
			    sql.append(saGen.genSubSQLByType(conn,user,salesBean, "ORDER", salesBean.getGroupBy(), salesBean.getGroupBy())+"\n");
            }
            if(isColumnOrder && isColumnInvoice){
            	sql.append("\t"+" UNION ALL \n");
            }
            if(isColumnInvoice){
			    sql.append(saGen.genSubSQLByType(conn,user,salesBean, "INVOICE", salesBean.getGroupBy(), salesBean.getGroupBy())+"\n");
            }
			sql.append("\t"+"  )SS \n");
			sql.append("\t "+"GROUP BY "+SAGenCondition.genGroupBySQL(salesBean.getGroupBy()) +"\n");
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
			
		}else {
			
			sql.append("SELECT A.* FROM ( \n");
			sql.append("\t SELECT * FROM( \n");
			
			isColumnOrder = saU.isColumnDispOrder(salesBean); 
			isColumnInvoice = saU.isColumnDispInvoice(salesBean);
			 
			// Sub SQl Order
			if(isColumnOrder){
				sql.append("\t SELECT DISTINCT  \n");
				//EDIT 24/04/2563
				sql.append("\t"+ genSQLNA(Utils.isNull(salesBean.getGroupBy())) +", \n ");
				
				sql.append("\t"+ saGen.genSQLGetDesc(salesBean.getGroupBy()));
				sql.append("\t"+ saGen.genSQLGetCode(salesBean.getGroupBy()));
				sql.append("\t"+"'1' AS A \n");
				sql.append("\t"+"FROM "+TABLE_VIEW+" S \n");
				sql.append("\t"+"WHERE 1=1 \n ");
				sql.append("\t"+"AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
				/** Condition Filter **/
				sql.append("\t"+saGen.genSqlWhereCondition(salesBean));
				/** Filter Displsy Data By User **/
				sql.append("\t"+SecurityHelper.genWhereSqlFilterByUser(conn,user, null,"S."));
				
				//ALL MAIN SQL
				if(TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				    //sql.append("\t"+" AND sales_order_month IN("+criBean.getMonth()+") \n");
				    //sql.append("\t"+" AND sales_order_year IN("+criBean.getYear()+") \n");
				    sql.append(SAGenCondition.genWhereCondSQLCaseByMonthYearMain("\t","","ORDER",criBean.getAllCond()));
				    
				    sql.append("\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}else if(TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
					//sql.append("\t"+" AND sales_order_quarter IN("+criBean.getQuarter()+") \n");
					//sql.append("\t"+" AND sales_order_year IN("+criBean.getYear()+") \n");
					 
					sql.append(SAGenCondition.genWhereCondSQLCaseByQuarterYearMain("\t","","ORDER",criBean.getAllCond()));
					 
					sql.append("\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}else if(TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
					//sql.append("\t"+" AND sales_order_year IN("+all+") \n");
					
					sql.append(""+SAGenCondition.genWhereCondSQLCaseByYEAR("ORDER",criBean.getAllCond()));
					sql.append("\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}
				else {
					Date date = DateUtil.parseToBudishDate(salesBean.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH);
					Date dateTo = DateUtil.parseToBudishDate(salesBean.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH);
					// 
					if(date != null && dateTo != null){
						sql.append("\t"+" AND  SALES_ORDER_DATE >= to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("\t"+" AND  SALES_ORDER_DATE <= to_date('"+DateUtil.stringValue(dateTo, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
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
				
				sql.append("\t"+ saGen.genSQLGetDesc(salesBean.getGroupBy()));
				sql.append("\t"+ saGen.genSQLGetCode(salesBean.getGroupBy()));
				sql.append("\t"+"'1' AS A \n");
				sql.append("\t"+"FROM "+TABLE_VIEW+" S \n");
				sql.append("\t"+"WHERE 1=1 \n ");
				sql.append("\t"+"AND "+salesBean.getGroupBy()+" IS NOT NULL \n");
				/** Condition Filter **/
				sql.append("\t"+saGen.genSqlWhereCondition(salesBean));
				
				/** Filter Displsy Data By User **/
				sql.append("\t"+SecurityHelper.genWhereSqlFilterByUser(conn,user, null,"S."));
				
				//ALL MAIN SQL
				if(TYPE_SEARCH_MONTH.equalsIgnoreCase(salesBean.getTypeSearch())){
				    //sql.append("\t"+" AND invoice_month IN("+criBean.getMonth()+") \n");
				    //sql.append("\t"+" AND invoice_year IN("+criBean.getYear()+") \n");
					 
					sql.append(SAGenCondition.genWhereCondSQLCaseByMonthYearMain("\t","","INVOICE",criBean.getAllCond()));
					 
				    sql.append("\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}else if(TYPE_SEARCH_QUARTER.equalsIgnoreCase(salesBean.getTypeSearch())){
					//sql.append("\t"+" AND invoice_quarter IN("+criBean.getQuarter()+") \n");
					//sql.append("\t"+" AND invoice_year IN("+criBean.getYear()+") \n");
					
					sql.append(SAGenCondition.genWhereCondSQLCaseByQuarterYearMain("\t","","INVOICE",criBean.getAllCond()));
					 
					sql.append("\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}else if(TYPE_SEARCH_YEAR.equalsIgnoreCase(salesBean.getTypeSearch())){
					//sql.append("\t"+" AND invoice_year IN("+all+") \n");
					
					sql.append(""+SAGenCondition.genWhereCondSQLCaseByYEAR("INVOICE",criBean.getAllCond()));
					sql.append("\t"+" GROUP BY "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
				}
				else {
					Date date = DateUtil.parseToBudishDate(salesBean.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH);
					Date dateTo = DateUtil.parseToBudishDate(salesBean.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH);
					// 
					if(date != null && dateTo != null){
						sql.append("\t"+" AND  INVOICE_DATE >= to_date('"+DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("\t"+" AND  INVOICE_DATE <= to_date('"+DateUtil.stringValue(dateTo, DateUtil.DD_MM_YYYY_WITH_SLASH, Locale.US)+"','dd/mm/yyyy')  \n");
						sql.append("\t"+" GROUP BY  "+SAGenCondition.genGroupBySQL(Utils.isNull(salesBean.getGroupBy())) +" \n ");
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
			
			/** Gen By Condition From TO dayFrom-DateTo or Month 11-Month 12 or Quarter1-Quarter4 **/
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
		
		//logger.debug("*************** Generate SQL ******************** \n"+sql.toString()+"\n*****************************************************\n");
		if(logger.isDebugEnabled()){
			FileUtil.writeFile("d://dev_temp/temp/sql.sql", sql.toString(),"TIS-620");
		}
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
	
			}else if("Sales_zone".equalsIgnoreCase(condType)){
				sql  = "SELECT  DISTINCT ZONE AS CODE ,ZONE_NAME AS DESC1 FROM XXPENS_BI_MST_SALES_ZONE WHERE 1=1 \n";
				if( !Utils.isNull(code).equals("")){
					sql +=" and ZONE like '%"+code+"%' \n";
				}
				if( !Utils.isNull(desc).equals("")){
					sql +=" and ZONE_NAME like '%"+desc+"%' \n";
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
