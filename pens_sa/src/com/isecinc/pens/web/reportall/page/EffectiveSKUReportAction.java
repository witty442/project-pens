package com.isecinc.pens.web.reportall.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.CustomerBean;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.CustomerCatDAO;
import com.isecinc.pens.dao.CustomerDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.SalesrepChannelDAO;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.dao.SalesrepZoneDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.boxno.BoxNoBean;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.bean.EffectiveSKUBean;
import com.isecinc.pens.web.reportall.page.dao.BoxNoNissinReportDAO;
import com.isecinc.pens.web.reportall.page.dao.StockExpireDetailDAO_DESCRICATE;
import com.isecinc.pens.web.stock.StockBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class EffectiveSKUReportAction extends I_Action {
	
	public static Logger logger = Logger.getLogger("PENS");
	public static int pageSize = 90;
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		ReportAllForm aForm = (ReportAllForm) form;
		String pageName = aForm.getPageName();
		Connection conn = null;
		String yyyymm = "";
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			pageName = Utils.isNull(request.getParameter("pageName"));
			logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
			
			if("new".equals(action)){
				conn = DBConnection.getInstance().getConnectionApps();
				
				pageName = Utils.isNull(request.getParameter("pageName"));
				request.getSession().setAttribute("reportAllForm_RESULTS",null);
				
				ReportAllBean bean = new ReportAllBean();
				EffectiveSKUBean effectiveSKUBean  =new EffectiveSKUBean();

				//default back 6 month
				String yearMonthChkDefault = "";
				Calendar cal =Calendar.getInstance();
				for(int i=0;i<6;i++){
					yyyymm =  DateUtil.stringValue(cal.getTime(),"yyyyMM").toUpperCase();
					//Month -1
					cal.add(Calendar.MONTH, -1);
					yearMonthChkDefault +="'"+yyyymm+"',";
				}
				yearMonthChkDefault = yearMonthChkDefault.substring(0,yearMonthChkDefault.length()-1);
				effectiveSKUBean.setYearMonthChk(yearMonthChkDefault);
				
				//for test
				//effectiveSKUBean.setSalesrepCode("S001");
				//effectiveSKUBean.setYearMonthChk("'202006'");
	
				bean.setEffectiveSKUBean(effectiveSKUBean);
				aForm.setBean(bean);
				
				prepareSearch(request, conn, user, pageName);
				
			}else if("back".equals(action)){
				pageName = aForm.getPageName();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return "reportAll";
	}

	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("searchHead");
		ReportAllForm aForm = (ReportAllForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		String action = Utils.isNull(request.getParameter("action")); 
		boolean excel = false;
		try {
			logger.debug("search Head :pageName["+pageName+"]");
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			String screenWidth = "";
			if(request.getSession().getAttribute("screenWidth") != null){ 
				screenWidth = (String)request.getSession().getAttribute("screenWidth");
			}
		
			//prepare parameter YYYYMM
			String yearMonthChkArr[] = request.getParameterValues("yearMonthChk");
			StringBuilder yearMonthSQL = new StringBuilder();
		    for (String n : yearMonthChkArr) {
		    	yearMonthSQL.append("'").append(n.replace("'", "\\'")).append("',");
		    }
		    yearMonthSQL.deleteCharAt(yearMonthSQL.length() - 1);
			logger.debug("yearMonthSQL:"+yearMonthSQL.toString());
			
			aForm.getBean().getEffectiveSKUBean().setYearMonthChk(yearMonthSQL.toString());
			
			EffectiveSKUBean result = searchReportModel(screenWidth, "", aForm.getBean().getEffectiveSKUBean(), user, excel);
			
			//logger.debug("dataHTMLStr:"+stockResult.getDataStrBuffer());
			if(result.getDataStrBuffer() != null){
				 request.setAttribute("reportAllForm_RESULTS",result.getDataStrBuffer());
				 result.setDataStrBuffer(null);//clear data session
			}else{
				 request.setAttribute("Message", "ไม่พบข้อมูล");
				 request.setAttribute("reportAllForm_RESULTS",null);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return "reportAll";
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("export : ");
		User user = (User) request.getSession().getAttribute("user");
		ReportAllForm aForm = (ReportAllForm) form;
		Connection conn = null;
		String action = "";
		String forward ="reportAll";
		EffectiveSKUBean beanResult = new EffectiveSKUBean();
		try {
			action = Utils.isNull(request.getParameter("action"));
			conn = DBConnection.getInstance().getConnectionApps();
			
			//prepare parameter YYYYMM
			String yearMonthChkArr[] = request.getParameterValues("yearMonthChk");
			StringBuilder yearMonthSQL = new StringBuilder();
		    for (String n : yearMonthChkArr) {
		    	yearMonthSQL.append("'").append(n.replace("'", "\\'")).append("',");
		    }
		    yearMonthSQL.deleteCharAt(yearMonthSQL.length() - 1);
			logger.debug("yearMonthSQL:"+yearMonthSQL.toString());
			
			aForm.getBean().getEffectiveSKUBean().setYearMonthChk(yearMonthSQL.toString());
			String screenWidth = "";
			if(request.getSession().getAttribute("screenWidth") != null){ 
				screenWidth = (String)request.getSession().getAttribute("screenWidth");
			}
			if(request.getAttribute("reportAllForm_RESULTS") != null){
				 beanResult.setDataStrBuffer((StringBuffer)request.getAttribute("reportAllForm_RESULTS"));
			}else{
				 beanResult =searchReportModel(screenWidth, "", aForm.getBean().getEffectiveSKUBean(), user, true);
			}
			if(beanResult != null && beanResult.getDataStrBuffer() != null){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(beanResult.getDataStrBuffer().toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			    beanResult.setDataStrBuffer(null);//clear data session
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				if(conn != null){
				   conn.close();
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward(forward);
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "detail";
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * Set new Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {

	}
	public static void prepareSearch(HttpServletRequest request,Connection conn,User user,String pageName){
		PopupBean item = null;
		List<PopupBean> dataList = null;
		try{
			//Cust Cat No List
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			List<PopupBean> dataTempList = CustomerCatDAO.searchCustomerCatListModel(conn, "ORDER - CREDIT SALES",user,"ROLE_CR_STOCK");
			if(dataTempList != null &&dataTempList.size() ==1){
			  dataList.addAll(dataTempList);
			}else{
			  dataList.add(item);
			  dataList.addAll(dataTempList);
			}
			request.getSession().setAttribute("CUST_CAT_LIST",dataList);
			
			//init Month back 12 
			request.getSession().setAttribute("MONTH_LIST", initMonthList());
			
			//SALESZONE_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			dataList.add(item);
			
			List<PopupBean> salesZoneList =SalesrepZoneDAO.searchSalesrepZoneListModel(conn,user,"ROLE_CR_STOCK");
			dataList.addAll(salesZoneList);
			request.getSession().setAttribute("SALES_ZONE_LIST",dataList);
			
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = SalesrepDAO.searchSalesrepListAll(conn, pageName, "", "", "", user, "ROLE_CR_STOCK") ;
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static List<PopupBean> initMonthList(){
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		String periodValue = "";
		PopupBean item = new PopupBean();
		try{
			for(int i=0;i<12;i++){
				item = new PopupBean();
				periodValue =  DateUtil.stringValue(cal.getTime(),"yyyyMM").toUpperCase();
				periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy",DateUtil.local_th).toUpperCase();
				item.setKeyName(periodName);
				item.setValue(periodValue);
				monthYearList.add(item);
				
				//Month -1
				cal.add(Calendar.MONTH, -1);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	 return monthYearList;
	}

	public static EffectiveSKUBean searchReportModel(String screenWidth,String contextPath,EffectiveSKUBean o,User user,boolean excel){
		EffectiveSKUBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer html = null;
		int r = 0;
		Map<String, String> brandMap = new HashMap<String, String>();
		Map<String, EffectiveSKUBean> salesCustomerMap = new HashMap<String, EffectiveSKUBean>();
		Map<String, EffectiveSKUBean> dataMAP = new HashMap<String, EffectiveSKUBean>();
		String keyDataMap = "";
		try{
			//create connection
			conn = DBConnection.getInstance().getConnection();
			
			//check user login is map cust sales TT
			boolean isUserMapCustSalesTT = GeneralDAO.isUserMapCustSalesTT(user);
			
          	//Sales Analyst
			sql.append("\n SELECT ");
			sql.append("\n S.salesrep_code,C.CUSTOMER_CODE,");
			sql.append("\n C.CUSTOMER_DESC ,P.brand ,");
			sql.append("\n COUNT(distinct M.inventory_item_id) as qty");
			sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V M,");
			sql.append("\n APPS.xxpens_om_item_mst_v P,");
			sql.append("\n PENSBI.XXPENS_BI_MST_SALESREP S,");
			sql.append("\n PENSBI.XXPENS_BI_MST_CUSTOMER C ,");
			sql.append("\n PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n WHERE 1=1 ");
			sql.append("\n AND M.inventory_item_id = P.inventory_item_id");
			sql.append("\n AND M.customer_id = C.customer_id");
			sql.append("\n AND M.salesrep_id = S.salesrep_id");
			sql.append("\n AND M.salesrep_id = Z.salesrep_id");
			sql.append("\n AND TO_CHAR(M.INVOICE_DATE,'YYYYMM') IN ("+o.getYearMonthChk()+")");//start init date check
			
			//debug 
			//sql.append("\n AND M.inventory_item_id  = 665170");
			
			//SalesZone By User Login
			if(isUserMapCustSalesTT){
				sql.append("\n and Z.zone in( ");
				sql.append("\n   select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT");
				sql.append("\n   where user_name ='"+user.getUserName()+"'");
				sql.append("\n ) ");
			}
			
			sql.append("\n AND Z.zone in('0','1','2','3','4') ");

			if( !Utils.isNull(o.getSalesZone()).equals("")){
				sql.append("\n and Z.zone ='"+o.getSalesZone()+"'");
			}
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n and M.customer_category ='ORDER - "+o.getCustCatNo().toUpperCase()+"'");
			}
			if( !Utils.isNull(o.getBrand()).equals("")){
				sql.append("\n and P.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")");
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("") ){
				sql.append("\n and C.customer_code in("+SQLHelper.converToTextSqlIn(o.getCustomerCode())+")");
			}
			if( !Utils.isNull(o.getSalesrepCode()).equals("") ){
				sql.append("\n and Z.salesrep_code ='"+Utils.isNull(o.getSalesrepCode())+"'");
			}
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
			    sql.append("\n and Z.salesrep_code = '"+user.getUserName().toUpperCase()+"'");
			}
			sql.append("\n GROUP BY S.salesrep_code,C.CUSTOMER_CODE,");
			sql.append("\n C.CUSTOMER_DESC ,P.brand ");

			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString());
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new EffectiveSKUBean();
			
			  //set bean
			  item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
			  item.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			  item.setCustomerName(Utils.isNull(rst.getString("customer_desc")));
			  item.setBrand(Utils.isNull(rst.getString("brand")));
			  item.setQty(Utils.decimalFormat(rst.getDouble("qty"), Utils.format_current_no_disgit));
			  
			  //set for display
			  brandMap.put(Utils.isNull(rst.getString("brand")), Utils.isNull(rst.getString("brand")));
			  salesCustomerMap.put(Utils.isNull(rst.getString("salesrep_code"))+"_"+Utils.isNull(rst.getString("customer_code")), item);
			  
			  //set data Map  key= sales+_+customer+_+brand
			  keyDataMap = Utils.isNull(rst.getString("salesrep_code"))+"_"+Utils.isNull(rst.getString("customer_code"))+"_"+Utils.isNull(rst.getString("brand"));
			  
			  dataMAP.put(keyDataMap, item);
			}//while
			
			/*** Sort column and Row Map  ***/
			//Sort productCode asc to List
			List<EffectiveSKUBean> brandList = new ArrayList<EffectiveSKUBean>();
			Map<String, String> brandMapSortMap = new TreeMap<String, String>(brandMap);
			Iterator its = brandMapSortMap.keySet().iterator();
			EffectiveSKUBean b =null;
			while(its.hasNext()){
				String key = (String)its.next();
				String value = brandMapSortMap.get(key);
				//logger.debug("no["+r+"]key["+key+"]value["+value+"]");
				b = new EffectiveSKUBean();
				b.setBrand(key);
				brandList.add(b);
			}//while

			//Sort customerSales asc To List
			List<EffectiveSKUBean> salesCustomerList = new ArrayList<EffectiveSKUBean>();
			Map<String, EffectiveSKUBean> salesCustomerSortMap = new TreeMap<String, EffectiveSKUBean>(salesCustomerMap);
			its = salesCustomerSortMap.keySet().iterator();
			while(its.hasNext()){
				String key = (String)its.next();
				EffectiveSKUBean value = salesCustomerSortMap.get(key);
				//logger.debug("no["+r+"]key["+key+"]value["+value+"]");
				b = new EffectiveSKUBean();
				b.setSalesrepCode(value.getSalesrepCode());
				b.setCustomerCode(value.getCustomerCode());
				b.setCustomerName(value.getCustomerName());
				salesCustomerList.add(b);
			}//while
			
			//Gen Html Table Show
			if(dataMAP != null && !dataMAP.isEmpty()){
			   html = new StringBuffer("");
				   
			   //Gen Header Table
			   html.append(genHeadTable(screenWidth,contextPath, o, excel, brandList,salesCustomerList));
			   
			   //Gen Row Table
			   html.append(genRowTable(contextPath, o, excel, dataMAP, brandList, salesCustomerList));
			  
			   html.append("</table>");
			   html.append("</div>");
			}//if
			
			o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genHeadTable(String screenWidth, String contextPath,EffectiveSKUBean head,boolean excel
			,List<EffectiveSKUBean> brandList
			,List<EffectiveSKUBean> customerList) throws Exception{
		StringBuffer hAll = new StringBuffer("");
		StringBuffer h = new StringBuffer("");
		StringBuffer headReport = new StringBuffer("");
		int colSpan = 0;
		String tdWidth = "";
		
		//find CustomerName
		CustomerBean criBean = new CustomerBean();
		criBean.setCustomerCode(head.getCustomerCode());
		CustomerBean reBean = CustomerDAO.findCustomer(criBean);
		if(reBean != null){
			head.setCustomerName(reBean.getCustomerName());
		}
		
		h.append("<div style='height:500px;width:"+screenWidth+"px;' \n>");//NEW CODE
		h.append("<table id='tblProduct' class='table table-condensed table-striped' border='1'> \n");
		
		h.append("<thead> \n");
		h.append("<tr> \n");
		h.append(" <th width='5%' >Sales</th> \n");
		h.append(" <th width='2%' >Seq</th> \n");
		h.append(" <th width='7%' >Cust No</th> \n");
		h.append(" <th width='10%''>Cust Name</th> \n");
		//Loop By Brand
		for(int i=0;i<brandList.size();i++){
			colSpan++;
			EffectiveSKUBean item = brandList.get(i);
			if( !excel){}
			h.append("<th width='"+tdWidth+"%' class='td_bg_lineH'>"+item.getBrand()+"</th> \n");
		}
		h.append(" <th width='5%' >รวมจำนวนแบรนด์</th> \n");
		h.append("</tr> \n");
		h.append("</thead> \n");
		
		colSpan = brandList.size()+5;
		logger.debug("colSpan:"+colSpan);
		if(excel){
			headReport.append(ExcelHeader.EXCEL_HEADER);
			headReport.append("<style> \n");
			headReport.append("</style> \n");
			
			//'202006','202005'
			logger.debug("yearMonthChk:"+head.getYearMonthChk());
			String monthYearDisp = "";
			String yearMonthChkArr[] = head.getYearMonthChk().split("\\,");
			for(int i=0;i<yearMonthChkArr.length;i++){
				String dateTemp = yearMonthChkArr[i].replaceAll("\\'", "");
				dateTemp = dateTemp+"01";
				logger.debug("dateTemp:"+dateTemp);
				Date dateObj = DateUtil.parse(dateTemp, "yyyyMMdd");
				monthYearDisp +=DateUtil.stringValue(dateObj, "MMM-yy",DateUtil.local_th)+" ,";
			}
			if(monthYearDisp.length()>0){
				monthYearDisp = monthYearDisp.substring(0,monthYearDisp.length()-1);
			}
			
			headReport.append("<table id='tblProduct' align='center' border='1' cellpadding='1' cellspacing='1' > \n");
			headReport.append("<tr> \n");
			headReport.append(" <td colspan="+colSpan+"><b>จำนวน SKU ที่ขายแต่ร้านค้า ช่วงเดือน:&nbsp;&nbsp;"+monthYearDisp+"</b> </td>");
			headReport.append("</tr> \n");
			headReport.append("<tr> \n");
			headReport.append(" <td colspan="+colSpan+"><b> &nbsp;&nbsp;</b> </td>");
			headReport.append("</tr> \n");
			headReport.append("</table> \n");
			
			hAll.append(headReport);
			hAll.append(h);
		}else{
		   hAll.append(h);
		}
		return hAll;
	}
	
	private static StringBuffer genRowTable(String contextPath,EffectiveSKUBean head,boolean excel
			,Map<String, EffectiveSKUBean> dataMAP
			,List<EffectiveSKUBean> brandList
			,List<EffectiveSKUBean> salesCustomerList) throws Exception{
		StringBuffer h = new StringBuffer("");
		String key = "";
		EffectiveSKUBean itemRow = null;
		EffectiveSKUBean itemCol = null;
		EffectiveSKUBean itemRowColValue = null;
		Map<String,Double> sumColumnMap = new HashMap<String,Double>();
		double sumRow = 0,sumCol =0;
		int seqByCust = 0;
		Map<String,String> salesDup = new HashMap<String, String>();
		String classText ="td_text";
		String classTextCenter ="td_text_center";
		String classTextCenterBold ="td_text_center_bold";
		String classNumber ="td_number";
		String classNumberBold = "td_number_bold";
		int columnWidth = 0;
		if(brandList.size()<=10){
			columnWidth  =5;
		}
		if(excel){
			classText ="text";
			classTextCenter ="text";
			classTextCenterBold="text_center_bold";
			classNumber ="number";
			classNumberBold = "num_currency_bold";
		}
		
		h.append("<tbody> \n");
		for(int r=0;r<salesCustomerList.size();r++){
			itemRow = salesCustomerList.get(r);
			
			//reset seq by sale+custCode
			if(salesDup.get(itemRow.getSalesrepCode())==null){
				
				//summary row by sales
				if( (salesDup.get(itemRow.getSalesrepCode())==null && r != 0) ){
					//Summary Column
					h.append("<tr> \n");
					h.append("<td colspan='4' class='"+classTextCenterBold+"'>รวมจำนวนร้านค้าที่ขาย</td> \n");
					for(int c=0;c<brandList.size();c++){
						itemCol = brandList.get(c);
						if(sumColumnMap.get(itemCol.getBrand()) != null){
						  h.append(" <td class='"+classNumberBold+"' width='"+columnWidth+"%'>"+Utils.decimalFormat(sumColumnMap.get(itemCol.getBrand()), Utils.format_current_no_disgit)+"</td> \n");
						}else{
						  h.append(" <td class='"+classNumberBold+"' width='"+columnWidth+"%'>"+Utils.decimalFormat(0, Utils.format_current_no_disgit)+"</td> \n");
						}
					}//for
					h.append("<td class='"+classNumberBold+"' width='"+columnWidth+"%'></td>\n");
					h.append("</tr> \n");
					
					//reset value for loop
					if(r !=salesCustomerList.size()-1){
					  sumColumnMap.clear();
					  // logger.debug("SumColumnMap clear["+sumColumnMap+"]");
					}//if
				}//if
				
				h.append("<tr> \n");
				//row by Salesrep+customer
				seqByCust =0;
				h.append(" <td class='"+classTextCenter+"'>"+itemRow.getSalesrepCode()+"</td> \n");
			}else{
				h.append("<tr> \n");
				h.append(" <td class='"+classTextCenter+"'></td> \n");
			}
			
			seqByCust++;
			h.append(" <td class='"+classTextCenter+"'>"+seqByCust+"</td> \n");
			h.append(" <td class='"+classTextCenter+"'>"+itemRow.getCustomerCode()+"</td> \n");
			h.append(" <td class='"+classText+"'>"+itemRow.getCustomerName()+"</td> \n");
			
			for(int c=0;c<brandList.size();c++){
				itemCol = brandList.get(c);
				//1.Column Type S Stock
				key=  itemRow.getSalesrepCode()+"_"+itemRow.getCustomerCode()+"_"+itemCol.getBrand();
				itemRowColValue = dataMAP.get(key);
				//logger.debug("Get dataMAP["+key+"]value["+(itemRowColValue!=null?itemRowColValue.getQty():0)+"]");
				
				if(itemRowColValue != null){
				    h.append(" <td  class='"+classNumber+"' width='"+columnWidth+"%'>"+Utils.isNull(itemRowColValue.getQty())+"</td> \n");
				   
				   //SumRow
	                sumRow += 1;
	                //sumByColumn
	                sumCol = 1;
	                if(sumColumnMap.get(itemCol.getBrand()) != null){
	                	sumColumnMap.put(itemCol.getBrand(), sumColumnMap.get(itemCol.getBrand()).doubleValue()+sumCol);
	                }else{
	                	sumColumnMap.put(itemCol.getBrand(),sumCol);
	                }
				}else{
				   h.append(" <td  class='"+classNumber+"' width='"+columnWidth+"%'></td> \n");
				}//if
			}//for 2
			
			//sumRow
			h.append(" <td class='"+classNumberBold+"'>"+Utils.decimalFormat(sumRow, Utils.format_current_no_disgit)+"</td> \n");
			
			//reset value for loop
			sumRow= 0;
			salesDup.put(itemRow.getSalesrepCode(),itemRow.getSalesrepCode());
			h.append("</tr> \n");
		}//for 1
		
		//Summary Column Last Row
		h.append("<tr> \n");
		h.append("<td colspan='4' class='"+classTextCenterBold+"'>รวมจำนวนร้านค้าที่ขาย</td> \n");
		for(int c=0;c<brandList.size();c++){
			itemCol = brandList.get(c);
			if(sumColumnMap.get(itemCol.getBrand()) != null){
			  h.append(" <td class='"+classNumberBold+"' width='"+columnWidth+"%'>"+Utils.decimalFormat(sumColumnMap.get(itemCol.getBrand()), Utils.format_current_no_disgit)+"</td> \n");
			}else{
			  h.append(" <td class='"+classNumberBold+"' width='"+columnWidth+"%'>"+Utils.decimalFormat(0, Utils.format_current_no_disgit)+"</td> \n");
			}
		}//for
		h.append("<td class='"+classNumberBold+"' width='"+columnWidth+"%'></td> \n");
		h.append("</tr> \n");
		
		h.append("</tbody> \n");
		return h;
	}
}
