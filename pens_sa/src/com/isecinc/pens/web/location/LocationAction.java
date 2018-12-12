package com.isecinc.pens.web.location;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBConnection;
import util.Utils;

import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.CConstantsBean;
import com.isecinc.pens.bean.User;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class LocationAction extends I_Action {

	public static int pageSize = 90;
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		LocationForm aForm = (LocationForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean foundData = false;
		String pageName = aForm.getPageName();
		String action = Utils.isNull(request.getParameter("action")); 
		try {
			logger.debug("search Head :pageName["+pageName+"]");
	
			if(foundData==false){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   request.getSession().setAttribute("RESULTS",null);
			   aForm.getBean().setItemsList(null);
			}
			logger.debug("pageName:"+aForm.getPageName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		LocationForm aForm = (LocationForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName"));
		String forwardPage = "";
		try{
			if("".equals(pageName)){
				pageName = aForm.getPageName();
			}
			logger.debug("pageName:"+pageName);
			if("spider".equalsIgnoreCase(pageName)){
				if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")){
					/** Init Data ***/
				    LocationInitial.getInstance().initSession(request);
				    LocationBean bean = new LocationBean();
				    //default check display
				    bean.setCustCatNo("C");//VanSale
				    bean.setDispAllNoOrder("true");
				    bean.setDispAllOrder("true");
				    bean.setDispAllVisit("true");
				    //for test 
				    /*if(logger.isDebugEnabled()){
					    bean.setDay("03/07/2561");
					    bean.setCustCatNo("C");
					    bean.setSalesChannelNo("2");
					    bean.setSalesrepCode("100021084");//V201
				    }*/
				    aForm.setBean(bean);
				    aForm.setPageName(pageName);
				    request.getSession().removeAttribute("CUST_LOC_LIST");
				    request.getSession().removeAttribute("RESULTS");
				    
				    forwardPage="spider";
				 }
			}else if("monitorSpider".equalsIgnoreCase(pageName)){
				logger.debug("action:"+Utils.isNull(request.getParameter("action")));
				if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")){
					
					/** Init Data ***/
				    LocationInitial.getInstance().initSessionMonitorSpider(request);
				    
				    LocationBean bean =new LocationBean();
				    bean.setCustCatNo("C");//VanSale
				    //for test 
				    /*if(logger.isDebugEnabled()){
					    bean.setDay("04/07/2561");
					    bean.setCustCatNo("C");
					    bean.setSalesChannelNo("2");
					    bean.setSalesrepCode("100021084");//V201
					   // bean.setSalesrepCode("100021079");//V101
				    }*/
				    aForm.setBean(bean);
				    aForm.setPageName(pageName);
				    request.getSession().removeAttribute("CUST_LOC_LIST");
				    request.getSession().removeAttribute("RESULTS");
				    /** Clear Session Detail Page */
					request.getSession().removeAttribute("RESULTS_DETAIL");
					
				    forwardPage="monitorSpider";
				 }else{
					forwardPage="monitorSpider";
					/** Clear Session Detail Page */
					request.getSession().removeAttribute("RESULTS_DETAIL");
				 }
			}else if("trip".equalsIgnoreCase(pageName)){
				logger.debug("action:"+Utils.isNull(request.getParameter("action")));
				if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")){
					/** Init Data ***/
				    LocationInitial.getInstance().initSessionTrip(request);
				    
				    LocationBean bean =new LocationBean();
				    bean.setCustCatNo("C");//VanSale
				    //fortest
				    bean.setSalesrepCode("100033105");
				    
				    aForm.setBean(bean);
				    aForm.setPageName(pageName);
				    aForm.setResults(null);
				    forwardPage="tripSearch";
				 }else{
					forwardPage="tripSearch";
				 }
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return forwardPage;
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		LocationForm aForm = (LocationForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "detail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	/**
	 * Prepare with ID
	*/
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	*/
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LocationForm aForm = (LocationForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String forwardPage = "";
		try {
			logger.debug("search pageName:"+aForm.getPageName());
			
			if("spider".equalsIgnoreCase(aForm.getPageName())){
				forwardPage ="spider";
				List<LocationBean> allList = new ArrayList<LocationBean>();
				request.getSession().removeAttribute("CUST_LOC_LIST");
				request.getSession().removeAttribute("RESULTS");
				
				LocationBean bean = aForm.getBean();
				if("MAP".equalsIgnoreCase(bean.getDispType())){
					//search Check in Location 3 Type NoOrder,Order,Visit
					if(   !"".equalsIgnoreCase(Utils.isNull(bean.getDispAllOrder())) 
					   || !"".equalsIgnoreCase(Utils.isNull(bean.getDispAllVisit()))
					   || !"".equalsIgnoreCase(Utils.isNull(bean.getDispAllNoOrder()))){
						
					    allList.addAll(LocationDAO.searchCustomerCheckInMapList(bean));
					    
					    logger.debug("LocationList Size:"+allList.size());
					}else{
						//search Customer Location only 2 Type P,B ,null
						allList.addAll(LocationDAO.searchCustomerLocationList(bean));
						
						logger.debug("LocationList Size:"+allList.size());
					}
					if(allList != null && allList.size() >0){
					    request.getSession().setAttribute("CUST_LOC_LIST", allList);
					}else {
						request.setAttribute("Message", "ไม่พบข้อมูล");
					}
	
				}else if("DATA".equalsIgnoreCase(bean.getDispType())){
					boolean excel = false;
					StringBuffer html = LocationReport.searchCustomerCheckInDataList(bean,excel);
					if(html.length() >0){
				       request.getSession().setAttribute("RESULTS", html);
					}else{
					   request.setAttribute("Message", "ไม่พบข้อมูล");
					}
				}// oracle
			}else if("monitorSpider".equalsIgnoreCase(aForm.getPageName())){
				forwardPage ="monitorSpider";
				String path = request.getContextPath();
				
				//get constants config all by ref_code
				Map<String, CConstantsBean> constantsMap = (Map<String, CConstantsBean>)request.getSession().getAttribute("CONSTANTS_MAP");
				
				LocationBean bean = aForm.getBean();
				boolean excel = false;
				StringBuffer html = MonitorSpiderReport.searchMonitorReport(path,bean,excel,constantsMap);
				if(html.length() >0){
			       request.getSession().setAttribute("RESULTS", html);
				}else{
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				
			}else if("trip".equalsIgnoreCase(aForm.getPageName())){
				forwardPage ="tripSearch";
				 String action = Utils.isNull(request.getParameter("action"));
				 if("back".equalsIgnoreCase(action)){
					 aForm.setBean(aForm.getBeanCriteria());
				 }
				 
				 aForm = TripAction.search(aForm,request,response);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}finally{}
		return forwardPage;
	}
	
	public ActionForward exportReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("exportReport : ");
		User user = (User) request.getSession().getAttribute("user");
		LocationForm aForm = (LocationForm) form;
		StringBuffer resultHtmlTable = null;
		String pageName = aForm.getPageName();
		try {	
			if("spider".equalsIgnoreCase(aForm.getPageName())){
				boolean excel = true;
				resultHtmlTable = LocationReport.searchCustomerCheckInDataList(aForm.getBean(),excel);
			    			
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(resultHtmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			    
			}else if("monitorSpider".equalsIgnoreCase(aForm.getPageName())){
				boolean excel = true;
                String path = request.getContextPath();
				LocationBean bean = aForm.getBean();
				
				//get constants config all by ref_code
				Map<String, CConstantsBean> constantsMap = (Map<String, CConstantsBean>)request.getSession().getAttribute("CONSTANTS_MAP");
				if(request.getSession().getAttribute("RESULTS") != null){
					resultHtmlTable = (StringBuffer)request.getSession().getAttribute("RESULTS");
				}else{
				    resultHtmlTable = MonitorSpiderReport.searchMonitorReport(path,bean,excel,constantsMap);
				}
				if(resultHtmlTable.length() >0){
			       request.getSession().setAttribute("RESULTS", resultHtmlTable);
				}else{
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
			    			
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(resultHtmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	
	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("viewDetail : ");
		LocationForm aForm = (LocationForm) form;
		StringBuffer result= null;
		try {	
			String detailType = Utils.isNull(request.getParameter("detailType"));
			String salesrepId = Utils.isNull(request.getParameter("salesrepId"));
			String tripDate = Utils.isNull(request.getParameter("tripDate"));
			logger.debug("detailType:"+detailType);
			aForm.setDetailType(detailType);
			
			//Criteria Link
			LocationBean bean = new LocationBean();
			bean.setSalesrepCode(salesrepId);
			bean.setDay(tripDate);
			
			//Criteria from Screen Search
			bean.setCustCatNo(aForm.getBean().getCustCatNo());
			bean.setSalesChannelNo(aForm.getBean().getSalesChannelNo());
			bean.setProvince(aForm.getBean().getProvince());
			bean.setDistrict(aForm.getBean().getDistrict());
			bean.setCustomerCode(aForm.getBean().getCustomerCode());
			bean.setCustomerType(aForm.getBean().getCustomerType());
			
			if("CustNotEqualTrip".equalsIgnoreCase(detailType)){
				result = MonitorSpiderReport.searchCustNoEqualsTrip(bean, false);
				logger.debug("result size:"+result.length());
				if(result.length()>0){
					request.getSession().setAttribute("RESULTS_DETAIL", result);
				}else{
					request.getSession().setAttribute("Message","ไม่พบข้อมูล");
				}
			}else if("CustNotEqualMasLocation".equalsIgnoreCase(detailType)){
				result = MonitorSpiderReport.searchCustNotEqualMstLocDetail(bean, false);
				//logger.debug("result size:"+result.length());
				if(result.length()>0){
					request.getSession().setAttribute("RESULTS_DETAIL", result);
				}else{
					request.getSession().setAttribute("Message","ไม่พบข้อมูล");
				}
			}else if("CustDetail".equalsIgnoreCase(detailType)){
				result = MonitorSpiderReport.searchCustDetail(bean, false);
				//logger.debug("result size:"+result.length());
				if(result.length()>0){
					request.getSession().setAttribute("RESULTS_DETAIL", result);
				}else{
					request.getSession().setAttribute("Message","ไม่พบข้อมูล");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("monitorSpiderDetail");
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
	
	
}
