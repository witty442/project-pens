package com.isecinc.pens.web.salestarget;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import util.DBConnection;
import util.Utils;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;

public class SalesTargetControlPage {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void prepareSearchMKT(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetUtils.initPeriod(conn));
			//init salesChannelList

			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetUtils.searchSalesChannelListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			custCatNoList.add(item);
			custCatNoList.addAll(SalesTargetUtils.searchCustCatNoMTListModel(conn, ""));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void prepareSearchMT(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetUtils.searchSalesChannelListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			custCatNoList.add(item);
			custCatNoList.addAll(SalesTargetUtils.searchCustCatNoMTListModel(conn, ""));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
			
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetUtils.initPeriod(conn));
			
			//init salesChannelList
			List<PopupBean> pos = new ArrayList<PopupBean>();
			PopupBean p = new PopupBean();
			p.setCustomerCode("");
			p.setCustomerName("");
			pos.add(p);
			pos.addAll(SalesTargetUtils.searchCustomerListByUserName(conn, user));
			request.getSession().setAttribute("CUSTOMER_LIST",pos);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/** Manager Sales **/
	public static void prepareSearchMTMGR(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetUtils.searchSalesChannelListModelByUserName(conn, user.getUserName());
			if(salesChannelList_s!= null && salesChannelList_s.size()>1){
			   salesChannelList.addAll(salesChannelList_s);
			   request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList_s);
			}else{
			   request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList_s);
			}
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			custCatNoList.add(item);
			custCatNoList.addAll(SalesTargetUtils.searchCustCatNoMTListModel(conn, ""));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
					
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetUtils.initPeriod(conn));
			//init salesChannelList
			List<PopupBean> pos = new ArrayList<PopupBean>();
			PopupBean p = new PopupBean();
			p.setCustomerCode("");
			p.setCustomerName("");
			pos.add(p);
			pos.addAll(SalesTargetUtils.searchCustomerListByUserName(conn, user));
			request.getSession().setAttribute("CUSTOMER_LIST",pos);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static void prepareSearchReportSalesTarget(HttpServletRequest request,Connection conn,User user
			,String pageName){
		prepareSearchReportSalesTargetMT(request, conn, user, pageName);
	}
	public static void prepareSearchReportSalesTargetMT(HttpServletRequest request,Connection conn,User user
			,String pageName){
		PopupBean item = null;
		List<PopupBean> dataList = null;
		try{
			//init periodList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetUtils.initPeriod(conn));
			
			//SALES_CHANNEL_LIST
			dataList = new ArrayList<PopupBean>();
			dataList.add(new PopupBean("reportType","ประเภทขาย","CUSTOMER_CATEGORY"));
			dataList.add(new PopupBean("reportType","ภาคการขาย","SALES_CHANNEL"));
			dataList.add(new PopupBean("reportType","พนักงานขาย","SALESREP_CODE"));
			dataList.add(new PopupBean("reportType","แบรนด์","BRAND"));
			dataList.add(new PopupBean("reportType","ร้านค้า","CUSTOMER_CODE"));
			dataList.add(new PopupBean("reportType","SKU","INVENTORY_ITEM_CODE"));
			dataList.add(new PopupBean("reportType","ภาคการขาย / พนักงานขาย / แบรนด์ ","SALES_CHANNEL,SALESREP_CODE,BRAND"));
			dataList.add(new PopupBean("reportType","ภาคการขาย / พนักงานขาย / แบรนด์ / SKU","SALES_CHANNEL,SALESREP_CODE,BRAND,INVENTORY_ITEM_CODE"));
			dataList.add(new PopupBean("reportType","พนักงานขาย / แบรนด์ ","SALESREP_CODE,BRAND"));
			dataList.add(new PopupBean("reportType","พนักงานขาย / แบรนด์ / SKU","SALESREP_CODE,BRAND,INVENTORY_ITEM_CODE"));
			dataList.add(new PopupBean("reportType","พนักงานขาย /รหัสร้านค้า / แบรนด์ ","SALESREP_CODE,CUSTOMER_CODE,BRAND"));
			dataList.add(new PopupBean("reportType","พนักงานขาย /รหัสร้านค้า / แบรนด์  / SKU","SALESREP_CODE,CUSTOMER_CODE,BRAND,INVENTORY_ITEM_CODE"));
			
			request.getSession().setAttribute("REPORT_TYPE_LIST",dataList);
			
			//Cust Cat No List
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			dataList.add(item);
			dataList.addAll(SalesTargetUtils.searchCustCatNoMTListModel(conn, ""));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",dataList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetUtils.searchSalesChannelListModel(conn, user);
			dataList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",dataList);
		
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = SalesTargetUtils.searchSalesrepMTListAll(conn);
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
			//status list
			dataList = new ArrayList<PopupBean>();
			dataList.add(new PopupBean("status","",""));
			dataList.add(new PopupBean("status","",SalesTargetConstants.STATUS_FINISH));
			dataList.add(new PopupBean("status","",SalesTargetConstants.STATUS_ACCEPT));
			dataList.add(new PopupBean("status","",SalesTargetConstants.STATUS_UN_ACCEPT));
			dataList.add(new PopupBean("status","",SalesTargetConstants.STATUS_POST));
			dataList.add(new PopupBean("status","",SalesTargetConstants.STATUS_REJECT));
			dataList.add(new PopupBean("status","",SalesTargetConstants.STATUS_OPEN));
			request.getSession().setAttribute("STATUS_LIST",dataList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			salesZoneList.add(item);
			
			List<PopupBean> salesZoneList_s = SalesTargetUtils.searchSalesZoneMTListModel(conn, "");
			salesZoneList.addAll(salesZoneList_s);
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void prepareSearchReportSalesTargetAll(HttpServletRequest request,Connection conn,User user,String pageName){
		PopupBean item = null;
		List<PopupBean> dataList = null;
		try{
			//init periodList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetUtils.initPeriod(conn));
			
			//Report type LIST
			dataList = new ArrayList<PopupBean>();
			dataList.add(new PopupBean("reportType","Summary","Summary"));
			dataList.add(new PopupBean("reportType","Detail","Detail"));
			request.getSession().setAttribute("REPORT_TYPE_LIST",dataList);
			
			//Disp type LIST
			dataList = new ArrayList<PopupBean>();
			dataList.add(new PopupBean("reportType","Summary","Summary"));
			dataList.add(new PopupBean("reportType","Detail","Detail"));
			request.getSession().setAttribute("DISP_TYPE_LIST",dataList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetUtils.searchSalesChannelListModel(conn, user);
			dataList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",dataList);
			
			//CUST_CAT_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> custCatList_s = SalesTargetUtils.searchCustCatNoMTListModel(conn, "");
			dataList.addAll(custCatList_s);
			request.getSession().setAttribute("CUST_CAT_LIST",dataList);
		
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = SalesTargetUtils.searchSalesrepListByUserName(conn, user);
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * prepareDetailMKT
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String prepareDetailMKT(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		SalesTargetForm aForm = (SalesTargetForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			logger.debug("prepareDetailMKT");
			//Create Connection
			conn = DBConnection.getInstance().getConnection();
			//clear old session for check dup item in page
			request.getSession().setAttribute("ITEM_IN_PAGE",null);
			
            String salesrepCode = Utils.isNull(request.getParameter("salesrepCode"));
            String customerCode = Utils.isNull(request.getParameter("customerCode"));
            String customerId = Utils.isNull(request.getParameter("customerId"));
            String customerName = Utils.isNull(request.getParameter("customerName"));
            String salesrepId = Utils.isNull(request.getParameter("salesrepId"));
            String salesChannelNo = Utils.isNull(request.getParameter("salesChannelNo"));
            
            //prepare data form session search
        	SalesTargetBean sales = aForm.getBean();
        	sales.setSalesrepCode(salesrepCode);
			sales.setCustomerCode(customerCode);
			sales.setCustomerId(customerId);
			sales.setCustomerName(customerName);
			sales.setSalesrepId(salesrepId);
			sales.setSalesChannelNo(salesChannelNo);
			sales.setId(0);//reset from new search only
			
			//check data Exist
			boolean getItems = true;
			sales = SalesTargetDAO.searchSalesTarget(conn,sales,getItems,user,aForm.getPageName());
			//get PriceListId
			sales.setPriceListId(SalesTargetUtils.getPriceListId(conn, sales.getSalesChannelNo(), sales.getCustCatNo()));
			
			if( sales.getId() != 0){
				logger.debug("prepare edit Id:"+sales.getId() );
				aForm.setResults(sales.getItems());
				aForm.setBean(sales);
				
				//init itemCode in session for check sup
				if(sales.getItems() != null && sales.getItems().size() >0){
					 Map<String,String> map = new HashMap<String,String>();
					 for(int i=0;i<sales.getItems().size();i++){
						 SalesTargetBean item = sales.getItems().get(i);
						 map.put(item.getItemCode(), item.getItemCode());
					 }
			    	 request.getSession().setAttribute("ITEM_IN_PAGE",map);
				}
			}else{
				logger.debug("prepare new ");
				if(Utils.isNull(sales.getBrandName()).equals("")){
				  sales.setBrandName(SalesTargetUtils.getBrandName(conn, sales.getBrand()));
				}
				sales.setBrandGroup(SalesTargetUtils.getBrandGroup(conn,sales.getBrand()));
				sales.setDivision(SalesTargetUtils.getDivision(conn,sales.getSalesChannelNo()));
				sales.setCustomerGroup(SalesTargetUtils.getCustomerGroup(conn, sales.getCustomerId()));
				//get CustomerName
				sales.setCustomerName(SalesTargetUtils.getCustName(conn, sales.getCustomerCode()));
				 
				sales.setCanSet(true);
				sales.setItems(null);
				aForm.setResults(new ArrayList<SalesTargetBean>());
				aForm.setBean(sales);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return forward;
	}
	
	
	/**
	 * prepareDetailMTSales
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String prepareDetailMTSales(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		SalesTargetForm aForm = (SalesTargetForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			logger.debug("prepareDetailMTSales");
			//Create Connection
			conn = DBConnection.getInstance().getConnection();
			//clear old session for check dup item in page
			request.getSession().setAttribute("ITEM_IN_PAGE",null);
			
			long id = Utils.convertStrToLong(request.getParameter("ids"),0);
            String salesrepCode = Utils.isNull(request.getParameter("salesrepCode"));
            String customerCode = Utils.isNull(request.getParameter("customerCode"));
            String customerId = Utils.isNull(request.getParameter("customerId"));
            String customerName = Utils.isNull(request.getParameter("customerName"));
            String salesrepId = Utils.isNull(request.getParameter("salesrepId"));
            String salesChannelNo = Utils.isNull(request.getParameter("salesChannelNo"));
            
            //prepare data form session search
        	SalesTargetBean sales = aForm.getBean();
        	sales.setId(id);
        	sales.setSalesrepCode(salesrepCode);
			sales.setCustomerCode(customerCode);
			sales.setCustomerId(customerId);
			sales.setCustomerName(customerName);
			sales.setSalesrepId(salesrepId);
			sales.setSalesChannelNo(salesChannelNo);
			
			//check data Exist
			boolean getItems = true;
			sales = SalesTargetDAO.searchSalesTarget(conn,sales,getItems,user,aForm.getPageName());
			//get PriceListId
			//sales.setPriceListId(SalesTargetUtils.getPriceListId(conn, sales.getSalesChannelNo(), sales.getCustCatNo()));
			
			logger.debug("prepare edit Id:"+sales.getId() );
			aForm.setResults(sales.getItems());
			aForm.setBean(sales);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}
	
	public static String prepareDetailMTMGR(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		SalesTargetForm aForm = (SalesTargetForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//Create Connection
			conn = DBConnection.getInstance().getConnection();
			
            long id = Utils.convertStrToLong(request.getParameter("ids"),0);
            
            //prepare data form session search
        	SalesTargetBean sales = aForm.getBean();
        	sales.setId(id);
    
			//check data Exist
			boolean getItems = true;
			sales = SalesTargetDAO.searchSalesTarget(conn,sales,getItems,user,aForm.getPageName());
			
			logger.debug("prepareDetailMTMGR Id:"+sales.getId() );
			aForm.setResults(sales.getItems());
			aForm.setBean(sales);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}
	
	/**
	 * convertBeanToBeanCriteria
	 * @param pageName
	 * @param bean
	 * @return
	 */
	public static SalesTargetBean convertToCriteriaBean(String pageName,SalesTargetBean bean){
		SalesTargetBean cri = new SalesTargetBean();
		if(SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){
			cri.setPeriod(bean.getPeriod());
			cri.setPeriodDesc(bean.getPeriodDesc());
			cri.setBrand(bean.getBrand());
			cri.setBrandName(bean.getBrandName());
			cri.setSalesChannelNo(bean.getSalesChannelNo());
			cri.setCustCatNo(bean.getCustCatNo());
			cri.setStartDate(bean.getStartDate());
			cri.setEndDate(bean.getEndDate());
		}else if(SalesTargetConstants.PAGE_MTSALES.equalsIgnoreCase(pageName)){
			cri.setPeriod(bean.getPeriod());
			cri.setPeriodDesc(bean.getPeriodDesc());
			cri.setBrand(bean.getBrand());
			cri.setBrandName(bean.getBrandName());
			cri.setSalesrepCode(bean.getSalesrepCode());
			cri.setSalesrepId(bean.getSalesrepId());
			cri.setCustomerCode(bean.getCustomerCode());
			cri.setStartDate(bean.getStartDate());
			cri.setEndDate(bean.getEndDate());
		}else if(SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){
			cri.setPeriod(bean.getPeriod());
			cri.setPeriodDesc(bean.getPeriodDesc());
			cri.setBrand(bean.getBrand());
			cri.setBrandName(bean.getBrandName());
			cri.setStartDate(bean.getStartDate());
			cri.setEndDate(bean.getEndDate());
			cri.setSalesChannelNo(bean.getSalesChannelNo());
		}else if(SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName)){
			cri.setPeriod(bean.getPeriod());
			cri.setPeriodDesc(bean.getPeriodDesc());
			cri.setBrand(bean.getBrand());
			cri.setBrandName(bean.getBrandName());
			cri.setSalesZone(bean.getSalesZone());
			cri.setCustCatNo(bean.getCustCatNo());
			cri.setStartDate(bean.getStartDate());
			cri.setEndDate(bean.getEndDate());
		}else if(SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName)){
			cri.setPeriod(bean.getPeriod());
			cri.setPeriodDesc(bean.getPeriodDesc());
			cri.setBrand(bean.getBrand());
			cri.setBrandName(bean.getBrandName());
			cri.setSalesZone(bean.getSalesZone());
			cri.setCustCatNo(bean.getCustCatNo());
			cri.setStartDate(bean.getStartDate());
			cri.setEndDate(bean.getEndDate());
		}else if(SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName)){
			cri.setPeriod(bean.getPeriod());
			cri.setPeriodDesc(bean.getPeriodDesc());
			cri.setSalesZone(bean.getSalesZone());
			cri.setCustCatNo(bean.getCustCatNo());
			cri.setCustCatNo(bean.getCustCatNo());
			cri.setStartDate(bean.getStartDate());
			cri.setEndDate(bean.getEndDate());
		}
		return cri;
	}
}
