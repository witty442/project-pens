package com.isecinc.pens.web.report.analyst.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
import com.isecinc.pens.web.report.analyst.bean.ABean;
import com.isecinc.pens.web.report.analyst.bean.ConfigBean;
import com.isecinc.pens.web.report.analyst.bean.CriteriaBean;
import com.isecinc.pens.web.report.analyst.helper.AGenCondition;
import com.isecinc.pens.web.report.analyst.helper.AUtils;
import com.isecinc.pens.web.report.analyst.helper.SecurityHelper;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class AInitial {
   
	/**
	 * WITTY
	 */
	protected static  Logger logger = Logger.getLogger("PENS");
	public String TABLE_VIEW = "";
	
	public List<String> MULTI_SELECTION_LIST = new ArrayList<String>();

	AUtils saU = new AUtils();
	private String maxOrderedDate = "";
	private String maxOrderedTime ="";
	
	public  Map<String,String>  MONTH_MAP = new HashMap<String,String>();
	public  Map<String,String>  QUARTER_MAP = new HashMap<String,String> ();
	public  Map<String,String>  GROUP_BY_MAP = new HashMap<String,String> ();
	public  Map<String,String>  DISP_COL_MAP = new HashMap<String,String> ();
	public  Map<String,String>  UNIT_MAP = new HashMap<String,String> ();
	public  Map<String,String>  COMPARE_MAP = new HashMap<String,String> ();
	public  Map<String,String>  COLUMN_ORDER_MAP = new HashMap<String,String> ();
	public  Map<String,String>  COLUMN_INVOICE_MAP = new HashMap<String,String> ();
	
	// Add Summary Type
	public   Map<String,String>  SUMMARY_TYPE_MAP = new HashMap<String,String> ();
	
	public AInitial getAInit(HttpServletRequest request){
		AInitial aInitial = null;
		String reportName = Utils.isNull(request.getParameter("reportName"));
		logger.debug("AInitial.reportName:"+reportName);
		if(request.getSession().getAttribute("PARAM_INIT_"+reportName) != null){
			aInitial = (AInitial)request.getSession().getAttribute("PARAM_INIT_"+reportName);
		}else{
            aInitial = new AInitial();
            aInitial.initSession(request);
            request.getSession().setAttribute("PARAM_INIT_"+reportName,aInitial);
		}
		return aInitial;
	}

	public  void initSession(HttpServletRequest requestWeb) {
		String reportName = Utils.isNull(requestWeb.getParameter("reportName"));
		
		if("ProjectCAnalyst".equalsIgnoreCase(reportName)){
			TABLE_VIEW = "PENSBI.XXPENS_BI_PROJECTC_ANALYSIS_V";
			initSessionProjectCAnalyst(requestWeb);
		}else{
			TABLE_VIEW = "PENSBI.XXPENS_BI_SALES_ANALYSIS_V";
			initSessionAnalyst(requestWeb);
		}
	}
	public  void initSessionAnalyst(HttpServletRequest requestWeb) {
		References r = null;
		HttpSession session = requestWeb.getSession(true);
		Connection conn = null;
		int i = 0;
		String reportName = Utils.isNull(requestWeb.getParameter("reportName"));
		User user = (User) session.getAttribute("user");
		try{
			logger.debug("Initail Session ");
		
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
			SUMMARY_TYPE_MAP.put(AConstants.SUMMARY_TYPE_NONE , "ไม่เลือก");
			SUMMARY_TYPE_MAP.put(AConstants.SUMMARY_TYPE_SUM , "ยอดรวม");
			SUMMARY_TYPE_MAP.put(AConstants.SUMMARY_TYPE_AVG , "ค่าเฉลี่ย");
			SUMMARY_TYPE_MAP.put(AConstants.SUMMARY_TYPE_PERCENT , "เปอร์เซ็นต์");
			SUMMARY_TYPE_MAP.put(AConstants.SUMMARY_TYPE_SUM_CONTRIBUTE , "ยอดรวม , % Contribute");
		
			
			conn = DBConnection.getInstance().getConnection();
			
			try{
				if(GROUP_BY_MAP.isEmpty()){
					List<References>groupByList = SecurityHelper.filterDisplayColumnByUser(conn,reportName);
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
			groupByList = SecurityHelper.filterDisplayColumnByUser(conn,reportName);
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
			summaryTypeList.add(new References("","ไม่เลือก"));
			summaryTypeList.add(new References(AConstants.SUMMARY_TYPE_SUM,"ยอดรวม"));
			summaryTypeList.add(new References(AConstants.SUMMARY_TYPE_AVG,"ค่าเฉลี่ย"));
			summaryTypeList.add(new References(AConstants.SUMMARY_TYPE_PERCENT,"เปอร์เซ็นต์"));
			summaryTypeList.add(new References(AConstants.SUMMARY_TYPE_SUM_CONTRIBUTE,"ยอดรวม , % Contribute"));
			
			session.setAttribute("summaryTypeList", summaryTypeList);
			
			
			/** init quarter **/
			List<References> profileList = new ArrayList<References>();
			r = new References("0","ไม่เลือก");
			profileList.add(r);
			//new Code
			profileList.addAll(initProfileList(conn, user.getId(),"SalesAnalyst"));
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
	public  void initSessionProjectCAnalyst(HttpServletRequest requestWeb) {
		References r = null;
		HttpSession session = requestWeb.getSession(true);
		Connection conn = null;
		int i = 0;
		String reportName = Utils.isNull(requestWeb.getParameter("reportName"));
		User user = (User) session.getAttribute("user");
		try{
			logger.debug("Initail Session ");
			conn = DBConnection.getInstance().getConnection();
			
	        logger.debug("Initail Param ProjectC Analysis");
	        logger.debug("reportName:"+reportName);
			
			MULTI_SELECTION_LIST.add("INVENTORY_ITEM_ID");
			MULTI_SELECTION_LIST.add("BRAND");
			MULTI_SELECTION_LIST.add("CUSTOMER_ID");
			MULTI_SELECTION_LIST.add("Sales_Channel");
			MULTI_SELECTION_LIST.add("SALESREP_ID");
			MULTI_SELECTION_LIST.add("BRAND_GROUP");
			MULTI_SELECTION_LIST.add("Organization_id");
			MULTI_SELECTION_LIST.add("Order_type_id");
			MULTI_SELECTION_LIST.add("SUBBRAND");
			MULTI_SELECTION_LIST.add("Division");
			MULTI_SELECTION_LIST.add("SALES_ZONE");
			MULTI_SELECTION_LIST.add("CUSTOMER_CLASS_CODE");
			MULTI_SELECTION_LIST.add("CUSTOMER_GROUP_TT");
			
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
			//DISP_COL_MAP.put("Discount","ส่วนลด");
			//DISP_COL_MAP.put("Promotion","ของแถม");
			//DISP_COL_MAP.put("Backordered","สินค้าค้างส่ง ไม่มีสินค้า");
			//DISP_COL_MAP.put("Outstanding","สินค้าค้างส่ง มีสินค้า ");
			DISP_COL_MAP.put("IR","ยอดขาย-คืน");/** INVOICED_AMT-RETURNED_AMT as IR_AMT **/
			//DISP_COL_MAP.put("NETAMT","ยอดขาย-คืน-ส่วนลด");/** INVOICED_AMT-RETURNED_AMT-DISCOUNT_AMT as NETAMT **/
			//DISP_COL_MAP.put("CALL_NEW", "CALL NEW");/**Sum Customer no dup in prev criteria **/
			//DISP_COL_MAP.put("CALL", "CALL");/**Sum Customer no dup in criteria**/
			
			/** Unit Column  **/
			UNIT_MAP.put("AMT","บาท");
			UNIT_MAP.put("QTY","หีบ");
			UNIT_MAP.put("QTY_S","หีบพิเศษ");
			
			/** Compare Column  **/
			COMPARE_MAP.put("PERCENT","%");
			
			/** Summary Type*/
			SUMMARY_TYPE_MAP.put(AConstants.SUMMARY_TYPE_NONE , "ไม่เลือก");
			SUMMARY_TYPE_MAP.put(AConstants.SUMMARY_TYPE_SUM , "ยอดรวม");
			SUMMARY_TYPE_MAP.put(AConstants.SUMMARY_TYPE_AVG , "ค่าเฉลี่ย");
			SUMMARY_TYPE_MAP.put(AConstants.SUMMARY_TYPE_PERCENT , "เปอร์เซ็นต์");
			
			try{
				if(GROUP_BY_MAP.isEmpty()){
					List<References>groupByList = SecurityHelper.filterDisplayColumnByUser(conn,reportName);
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
			groupByList = SecurityHelper.filterDisplayColumnByUser(conn,reportName);
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
			//dispColumnList.add(new References("NETAMT","ยอดขาย-คืน-ส่วนลด"));/** INVOICED_AMT-RETURNED_AMT-DISCOUNT_AMT as NETAMT **/
			//dispColumnList.add(new References("INVOICED","ยอดขาย"));
			//dispColumnList.add(new References("Backordered","สินค้าค้างส่ง ไม่มีสินค้า"));
			//dispColumnList.add(new References("Promotion","ของแถม"));
			dispColumnList.add(new References("TARGET","เป้าหมาย"));
			//dispColumnList.add(new References("Returned","รับคืน"));
			dispColumnList.add(new References("IR","ยอดขาย-คืน"));/** INVOICED_AMT-RETURNED_AMT as IR_AMT **/
			//dispColumnList.add(new References("ORDERED","ยอดสั่งซื้อ"));
			//dispColumnList.add(new References("Discount","ส่วนลด"));
			//dispColumnList.add(new References("Outstanding","สินค้าค้างส่ง มีสินค้า "));
			//dispColumnList.add(new References("CALL_NEW", "CALL NEW"));/**Sum Customer ID  no dup in prev criteria**/
			//dispColumnList.add(new References("CALL", "CALL"));/**Sum Customer ID  **/
			
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
			summaryTypeList.add(new References("","ไม่เลือก"));
			summaryTypeList.add(new References(AConstants.SUMMARY_TYPE_SUM,"ยอดรวม"));
			summaryTypeList.add(new References(AConstants.SUMMARY_TYPE_AVG,"ค่าเฉลี่ย"));
			summaryTypeList.add(new References(AConstants.SUMMARY_TYPE_PERCENT,"เปอร์เซ็นต์"));
			
			session.setAttribute("summaryTypeList", summaryTypeList);
			
			
			/** init quarter **/
			List<References> profileList = new ArrayList<References>();
			r = new References("0","ไม่เลือก");
			profileList.add(r);

			//new Code
			profileList.addAll(initProfileList(conn, user.getId(),reportName));
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
	
	public static List<References> initProfileList(Connection conn,int userId,String reportName) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
        List<References> profileList = new ArrayList<References>();
        String profileName ="";
        int maxProfileId= 0;
		try{
			sql = "\n SELECT * from pensbi.c_user_profile where user_id ="+userId  ;
			if( !Utils.isNull(reportName).equals("")){ //reportName is null :reportName SalesAnalyst
				sql += "\n and report_name = '"+reportName+"'";
			}
			sql += "\n order by profile_id asc ";
			
			logger.debug("sql:"+sql);
			
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
				returnList.add(new References("ALL","ALL",AConstants.MSG_ALL_TH));
				
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
				
				//returnList.add(new References("ALL","ALL",AConstants.MSG_ALL_TH));
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
				
				//returnList.add(new References("ALL","ALL",AConstants.MSG_ALL_TH));
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
				
				//returnList.add(new References("ALL","ALL",AConstants.MSG_ALL_TH));
				while(rs.next()){
					returnList.add(new References(rs.getString("code"),rs.getString("code") ,rs.getString("DESC1")));
				}
				
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql  = "SELECT SALESREP_ID as ID, SALESREP_CODE as CODE, SALESREP_DESC as DESC1, CREATE_DATE FROM PENSBI.XXPENS_BI_MST_SALESREP WHERE 1=1 \n";
				if( !Utils.isNull(code).equals("")){
					sql +=" and SALESREP_ID like '%"+code+"%' \n";
				}
				if( !Utils.isNull(desc).equals("")){
					sql +=" and SALESREP_DESC like '%"+desc+"%' \n";
				}
				sql += "order by SALESREP_CODE  \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				//returnList.add(new References("ALL","ALL",AConstants.MSG_ALL_TH));
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
				
				//returnList.add(new References("ALL","ALL",AConstants.MSG_ALL_TH));
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
				
				//returnList.add(new References("ALL","ALL",AConstants.MSG_ALL_TH));
				while(rs.next()){
					returnList.add(new References(rs.getString("CODE"),rs.getString("CODE") ,rs.getString("DESC1")));
				}
	
			}else if("Customer_id".equalsIgnoreCase(condType)){
				sql  = "SELECT CUSTOMER_ID as ID, CUSTOMER_CODE as CODE, CUSTOMER_DESC as DESC1, CREATE_DATE ";
				sql +=" FROM PENSBI.XXPENS_BI_MST_CUSTOMER WHERE 1=1 \n";
				if( !Utils.isNull(code).equals("")){
					sql +=" and CUSTOMER_ID = '"+code+"' \n";
				}
				if( !Utils.isNull(desc).equals("")){
					sql +=" and CUSTOMER_DESC like '%"+desc+"%' \n";
				}
				sql += "order by CUSTOMER_CODE  \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				//returnList.add(new References("ALL","ALL",AConstants.MSG_ALL_TH));
				while(rs.next()){
					returnList.add(new References(rs.getString("ID"),rs.getString("CODE") ,rs.getString("DESC1")));
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
				sql += "sales_channel_no in ("+AUtils.converToText("Sales_Channel", code) +")";
				
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
