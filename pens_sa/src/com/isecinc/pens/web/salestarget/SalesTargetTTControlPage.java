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

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class SalesTargetTTControlPage {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void prepareSearchMKT(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetTTUtils.initPeriod(conn));
			//init salesChannelList

			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetTTUtils.searchSalesChannelTTListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			custCatNoList.add(item);
			custCatNoList.addAll(SalesTargetTTUtils.searchCustCatNoMTListModel(conn, ""));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static void prepareSearchMKT_TT(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetTTUtils.initPeriod(conn));
			//init salesChannelList

			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			salesZoneList.add(item);
			
			List<PopupBean> salesZoneList_s = SalesTargetTTUtils.searchSalesZoneTTListModel(conn,user, "");
			salesZoneList.addAll(salesZoneList_s);
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			custCatNoList.add(item);
			custCatNoList.addAll(SalesTargetTTUtils.searchCustCatNoTTListModel(conn));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetTTUtils.searchSalesChannelTTListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static void prepareSearchTTSUPER(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			logger.debug("prepareSearchTTSUPER");
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetTTUtils.initPeriodTT(conn));

			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			List<PopupBean> salesZoneList_s = SalesTargetTTUtils.searchSalesZoneTTListModel(conn,user, "");
			if(user.getUserName().equalsIgnoreCase("admin") || salesZoneList_s.size() >2){
				PopupBean item = new PopupBean();
				item.setSalesZone("");
				item.setSalesZoneDesc("");
				salesZoneList.add(item);
				salesZoneList.addAll(salesZoneList_s);
			}else{
				salesZoneList.addAll(salesZoneList_s);
			}
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			custCatNoList.add(item);
			custCatNoList.addAll(SalesTargetTTUtils.searchCustCatNoTTListModel(conn));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void prepareSearchTTMGR(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetTTUtils.initPeriodTT(conn));
			//init salesChannelList

			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			List<PopupBean> salesZoneList_s = SalesTargetTTUtils.searchSalesZoneTTListModel(conn,user, "");
			if(user.getUserName().equalsIgnoreCase("admin") || salesZoneList_s.size() >2){
				PopupBean item = new PopupBean();
				item.setSalesZone("");
				item.setSalesZoneDesc("");
				salesZoneList.add(item);
				salesZoneList.addAll(salesZoneList_s);
			}else{
				salesZoneList.addAll(salesZoneList_s);
			}
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			custCatNoList.add(item);
			custCatNoList.addAll(SalesTargetTTUtils.searchCustCatNoTTListModel(conn));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetTTUtils.searchSalesChannelTTListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void prepareSearchTTADMIN(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetTTUtils.initPeriodTT(conn));
			//init salesChannelList

			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			List<PopupBean> salesZoneList_s = SalesTargetTTUtils.searchSalesZoneTTListModel(conn,user, "");
			if(user.getUserName().equalsIgnoreCase("admin") || salesZoneList_s.size() >2){
				PopupBean item = new PopupBean();
				item.setSalesZone("");
				item.setSalesZoneDesc("");
				salesZoneList.add(item);
				salesZoneList.addAll(salesZoneList_s);
			}else{
				salesZoneList.addAll(salesZoneList_s);
			}
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			custCatNoList.add(item);
			custCatNoList.addAll(SalesTargetTTUtils.searchCustCatNoTTListModel(conn));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetTTUtils.searchSalesChannelTTListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}

	public static void prepareSearchMTADMIN(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetTTUtils.initPeriod(conn));
			//init salesChannelList

			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetTTUtils.searchSalesChannelTTListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
			
			//Cust Cat No List
			//add Blank Row
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			custCatNoList.add(item);
			custCatNoList.addAll(SalesTargetTTUtils.searchCustCatNoMTListModel(conn, ""));
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	/**
	 * prepareDetailMKT_TT
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String prepareDetailMKT_TT(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detailTTMKT";
		SalesTargetForm aForm = (SalesTargetForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			logger.debug("prepareDetailMKT_TT");
			//Create Connection
			conn = DBConnection.getInstance().getConnection();
			
			//clear old session for check dup item in page
			request.getSession().setAttribute("ITEM_IN_PAGE",null);
			
            String salesZone = Utils.isNull(request.getParameter("salesZone"));
            String salesZoneDesc = Utils.isNull(request.getParameter("salesZoneDesc"));
            String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
         
            logger.debug("salesZone:"+salesZone);
            logger.debug("salesZoneDesc:"+salesZoneDesc);
            logger.debug("custCatNo:"+custCatNo);
            
            //prepare data form session search
        	SalesTargetBean sales = aForm.getBean();
        	
			sales.setSalesZone(salesZone);
			sales.setSalesZoneDesc(salesZoneDesc);
			sales.setCustCatNo(custCatNo);
			sales.setId(0);//reset from new search only
			
			//check data Exist
			boolean getItems = true;
			sales = SalesTargetTTDAO.searchSalesTargetTT(conn,sales,getItems,user,aForm.getPageName());
			//get PriceListId
			sales.setPriceListId(SalesTargetTTUtils.getPriceListId(conn, sales.getSalesZone(), sales.getCustCatNo(),user));
			logger.debug("pricelistId:"+sales.getPriceListId());
			
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
				  sales.setBrandName(SalesTargetTTUtils.getBrandName(conn, sales.getBrand()));
				}
				sales.setBrandGroup(SalesTargetTTUtils.getBrandGroup(conn,sales.getBrand()));
				//sales.setDivision(SalesTargetTTUtils.getDivision(conn,sales.getSalesChannelNo()));
				
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
	 * prepareDetailTTSUPER_TT
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String prepareDetailTTSUPER_TT(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detailTTSUPER";
		SalesTargetForm aForm = (SalesTargetForm) form;
		String pageName = aForm.getPageName();
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			logger.debug("prepareDetailTTSUPER_TT");
			
			//Create Connection
			conn = DBConnection.getInstance().getConnection();
			
            String salesZone = Utils.isNull(request.getParameter("salesZone"));
            String salesZoneDesc = Utils.isNull(request.getParameter("salesZoneDesc"));
            String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
            String brand = Utils.isNull(request.getParameter("brand"));
            String brandName = Utils.isNull(request.getParameter("brandName"));
            
            //prepare data form session search
        	SalesTargetBean sales = aForm.getBean();
        	
			sales.setSalesZone(salesZone);
			sales.setSalesZoneDesc(salesZoneDesc);
			sales.setCustCatNo(custCatNo);
			sales.setBrand(brand);
			sales.setBrandName(brandName);
			//BRAND_GROUP
			//DIVISION
			//SALES_CHANNEL
			sales.setBrandGroup(SalesTargetTTUtils.getBrandGroup(conn,sales.getBrand()));
			
			SalesTargetBean chkBean =SalesTargetTTDAO.searchSalesTargetTT(conn, sales, false, user, pageName);
			//check status
			if( chkBean != null 
				&& ( Utils.isNull(chkBean.getStatus()).equals(SalesTargetConstants.STATUS_POST)
					|| Utils.isNull(chkBean.getStatus()).equals(SalesTargetConstants.STATUS_UN_ACCEPT)) 
					) {
			  sales.setCanSet(true);
			  sales.setCanAccept(true);
			}
			aForm.setBean(sales);
			
			logger.debug("canSet:"+aForm.getBean().isCanSet());
			
			//Get target total by product from MKT page(XXPENS_BI_SALES_TARGET_TT)
			List<SalesTargetBean> productMKTList = SalesTargetTTDAO.searchSalesTargetProductListTTSUPER(conn, sales, user);

			//Get target save By Sales(XXPENS_BI_SALES_TARGET_TEMP) to MAP
			//KEY store_code+itemCode
			List<Map> dataList = SalesTargetTTDAO.searchSalesTargetTempToMap(conn, aForm.getBean(), user);
			Map<String, String> rowMap = null;
			Map<String, SalesTargetBean> dataMap = null;
			if(dataList != null && dataList.size()>0){
			   rowMap = dataList.get(0);
			   dataMap = dataList.get(1);
			}
			//Get SalesList
			List<SalesTargetBean> salesrepList = SalesTargetTTDAO.searchSalesrepListByTTSUPER(conn, sales, user,rowMap);
				
			request.getSession().setAttribute("productMKTList", productMKTList);
			request.getSession().setAttribute("salesrepList", salesrepList);
			request.getSession().setAttribute("dataMap", dataMap);
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
	 * prepareDetailTTSUPER_TT
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String prepareDetailTTMGR_TT(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detailTTMGR";
		SalesTargetForm aForm = (SalesTargetForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			logger.debug("prepareDetailMKT");
			//Create Connection
			conn = DBConnection.getInstance().getConnection();
			
            String salesZone = Utils.isNull(request.getParameter("salesZone"));
            String salesZoneDesc = Utils.isNull(request.getParameter("salesZoneDesc"));
            String custCatNo = Utils.isNull(request.getParameter("custCatNo"));
            String brand = Utils.isNull(request.getParameter("brand"));
            String brandName = Utils.isNull(request.getParameter("brandName"));
            
            //prepare data form session search
        	SalesTargetBean sales = aForm.getBean();
        	
			sales.setSalesZone(salesZone);
			sales.setSalesZoneDesc(salesZoneDesc);
			sales.setCustCatNo(custCatNo);
			sales.setBrand(brand);
			sales.setBrandName(brandName);
			//BRAND_GROUP
			//DIVISION
			//SALES_CHANNEL
			sales.setBrandGroup(SalesTargetTTUtils.getBrandGroup(conn,sales.getBrand()));
			
			//set status
			aForm.setBean(sales);
			
			//Get target total by product from MKT page(XXPENS_BI_SALES_TARGET_TT)
			List<SalesTargetBean> productMKTList = SalesTargetTTDAO.searchSalesTargetProductListTTSUPER(conn, sales, user);

			//Get target save By Sales(XXPENS_BI_SALES_TARGET_TEMP) to MAP
			//KEY store_code+itemCode
			List<Map> dataList = SalesTargetTTDAO.searchSalesTargetTempToMap(conn, aForm.getBean(), user);
			Map<String, String> rowMap = null;
			Map<String, SalesTargetBean> dataMap = null;
			if(dataList != null && dataList.size()>0){
			   rowMap = dataList.get(0);
			   dataMap = dataList.get(1);
			}
			//Get SalesList
			List<SalesTargetBean> salesrepList = SalesTargetTTDAO.searchSalesrepListByTTMGR(conn, sales, user,rowMap);
				
			request.getSession().setAttribute("productMKTList", productMKTList);
			request.getSession().setAttribute("salesrepList", salesrepList);
			request.getSession().setAttribute("dataMap", dataMap);
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
	
	public static void prepareSearchReportSalesTargetTT(HttpServletRequest request,Connection conn,User user,String pageName){
		PopupBean item = null;
		List<PopupBean> dataList = null;
		logger.debug("prepareSearchReportSalesTargetTT");
		try{
			
			//init periodList
			request.getSession().setAttribute("PERIOD_LIST", SalesTargetTTUtils.initPeriodTT(conn));
			
			/**
			 * 1. ประเภทขาย / ภาคตามสายดูแล / พนักงานขาย		
				2. ภาคตามสายดูแล / พนักงานขาย / แบรนด์ 		
				3. ภาคตามสายดูแล / พนักงานขาย / แบรนด์ / SKU		
				4. พนักงานขาย / แบรนด์ 		
				5. พนักงานขาย / แบรนด์ / SKU		
				6. ประเภทขาย		
				7. ภาคตามสายดูแล		
				8. พนักงานขาย		
				9. แบรนด์		
				10. SKU		
			 */
			//SALES_CHANNEL_LIST
			dataList = new ArrayList<PopupBean>();
			dataList.add(new PopupBean("reportType","ประเภทขาย / ภาคตามสายดูแล / พนักงานขาย	","CUSTOMER_CATEGORY,SALES_ZONE,SALESREP_CODE"));
			dataList.add(new PopupBean("reportType","ภาคตามสายดูแล / พนักงานขาย / แบรนด์ ","SALES_ZONE,SALESREP_CODE,BRAND"));
			dataList.add(new PopupBean("reportType","ภาคตามสายดูแล / พนักงานขาย / แบรนด์ / SKU","SALES_ZONE,SALESREP_CODE,BRAND,INVENTORY_ITEM_CODE"));
			dataList.add(new PopupBean("reportType","พนักงานขาย / แบรนด์ ","SALESREP_CODE,BRAND"));
			dataList.add(new PopupBean("reportType","พนักงานขาย / แบรนด์ / SKU","SALESREP_CODE,BRAND,INVENTORY_ITEM_CODE"));
			dataList.add(new PopupBean("reportType","ประเภทขาย","CUSTOMER_CATEGORY"));
			dataList.add(new PopupBean("reportType","ภาคตามสายดูแล","SALES_ZONE"));
			dataList.add(new PopupBean("reportType","พนักงานขาย","SALESREP_CODE"));
			dataList.add(new PopupBean("reportType","แบรนด์","BRAND"));
			dataList.add(new PopupBean("reportType","SKU","INVENTORY_ITEM_CODE"));
			request.getSession().setAttribute("REPORT_TYPE_LIST",dataList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesChannelList_s = SalesTargetTTUtils.searchSalesChannelTTListModel(conn, user);
			dataList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",dataList);
			
			//CUST_CAT_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			dataList.add(item);
			
			List<PopupBean> custCatList_s = SalesTargetTTUtils.searchCustCatNoTTListModel(conn,user, "");
			dataList.addAll(custCatList_s);
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",dataList);
		
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesrepCode("");
			item.setSalesrepId("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = SalesTargetTTUtils.searchSalesrepTTListAll(conn,user);
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
			
			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			List<PopupBean> salesZoneList_s = SalesTargetTTUtils.searchSalesZoneTTListModel(conn,user, "");
			if(user.getUserName().equalsIgnoreCase("admin") || salesZoneList_s.size() >2){
				item = new PopupBean();
				item.setSalesZone("");
				item.setSalesZoneDesc("");
				salesZoneList.add(item);
				salesZoneList.addAll(salesZoneList_s);
			}else{
				salesZoneList.addAll(salesZoneList_s);
			}
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
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
		}
		return cri;
	}
}
