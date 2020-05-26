package com.isecinc.pens.web.reportall.page;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.CustomerCatDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.SalesrepChannelDAO;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.dao.SalesrepZoneDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.bean.StockReturn;
import com.isecinc.pens.web.reportall.bean.StockReturnLine;
import com.isecinc.pens.web.reportall.page.dao.StockReturnDAO;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockConstants;
import com.isecinc.pens.web.stock.StockControlPage;
import com.pens.util.BeanParameter;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockReturnAction extends I_Action {
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
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			pageName = Utils.isNull(request.getParameter("pageName"));
			logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
			
			if("new".equals(action)){
				pageName = Utils.isNull(request.getParameter("pageName"));
				request.getSession().setAttribute("stockForm_RESULTS",null);
				
				ReportAllBean bean = new ReportAllBean();
				StockBean stockBean  =new StockBean();
				stockBean.setEndDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				stockBean.setStartDate(DateUtil.stringValue(DateUtil.getBackDate(new Date(),-30), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				bean.setStockBean(stockBean);
				
				aForm.setBean(bean);
				
				conn = DBConnection.getInstance().getConnectionApps();
				prepareSearchStockCreditReturn(request, conn, user, pageName);
				
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
		int currPage = 1;
		boolean allRec = false;
		boolean excel = false;
		try {
			logger.debug("search Head :pageName["+pageName+"]");
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(StockReturnDAO.searchStockReturnListTotalRec(conn,aForm.getBean().getStockBean(),user));
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			   
				StockBean stockResult = StockReturnDAO.searchStockReturnList(conn,user,aForm,allRec,currPage,pageSize,excel);
				//logger.debug("dataHTMLStr:"+stockResult.getDataStrBuffer());
				if(stockResult.getDataStrBuffer() != null){
					 request.setAttribute("reportAllForm_RESULTS",stockResult.getDataStrBuffer());
				}else{
					 request.setAttribute("Message", "ไม่พบข้อมูล");
					 request.setAttribute("reportAllForm_RESULTS",null);
				}
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    StockBean stockResult = StockReturnDAO.searchStockReturnList(conn,user,aForm,allRec,currPage,pageSize,excel);
				if(stockResult.getDataStrBuffer() != null){
					 request.setAttribute("reportAllForm_RESULTS",stockResult.getDataStrBuffer());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	
	public ActionForward exportReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportReport : ");
		User user = (User) request.getSession().getAttribute("user");
		ReportAllForm aForm = (ReportAllForm) form;
		StringBuffer resultHtmlTable =null;
		String pageName = aForm.getPageName();
		Connection conn = null;
		String action = "";
		String popup = "";
		String forward ="reportAll";
		try {
			action = Utils.isNull(request.getParameter("action"));
			popup = Utils.isNull(request.getParameter("popup"));
			if(popup.equalsIgnoreCase("false")){
				forward ="search";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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
	
	 public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
			logger.debug("printReport");
			ReportAllForm f = (ReportAllForm) form;
			User user = (User) request.getSession().getAttribute("user");
			Connection conn = null;
			StockReturn p  =null;
			String fileNameExport  = "";
			try { 
				ReportUtilServlet reportServlet = new ReportUtilServlet();
				HashMap<String,Object> parameterMap = new HashMap<String,Object>();
				ServletContext context = request.getSession().getServletContext();
		
				String fileName = "stock_return_mainreport";
	            String fileJasper = BeanParameter.getReportPath() + fileName;
	          
	            //init connection 
				conn = DBConnection.getInstance().getConnectionApps();
				
				//head
				String logopath =   context.getRealPath("/images2/pens_logo_fit.jpg");//
				logger.debug("requestNumber:"+Utils.isNull(request.getParameter("requestNo")));
				logger.debug("reportType:"+Utils.isNull(request.getParameter("reportType")));
				
				//get parameter
				f.getBean().getStockBean().setRequestNo(Utils.isNull(request.getParameter("requestNo")));
	        	//f.getBean().setUserId(user.getId()+"");
	        	  
	            fileNameExport = "StockRe_"+f.getBean().getStockBean().getRequestNo()+".pdf";
	            	
	            //get head detail
	            p = new StockReturnDAO().searchStockReturnReport(conn,f.getBean().getStockBean(), user);
				
	            logger.debug("request_number: "+p.getRequestNumber());
	            //detail
				List<StockReturnLine> mResultList = new StockReturnDAO().searchStockReturnLineReport(conn,user, f.getBean().getStockBean());
				//fix row detail =12
				if(mResultList != null && mResultList.size() >0){
					int diff = 12-mResultList.size();
					int no = mResultList.size();
				    for(int i=0;i<diff;i++){
				    	StockReturnLine line = new StockReturnLine();
				    	no++;
				    	line.setNo(no); 
				    	mResultList.add(line);
					}
				}
				
				//subReport1
				HashMap<String,Object> subParameterMap1 = new HashMap<String,Object>();
				subParameterMap1.put("reportName","ใบอนุมัติให้คืนคลัง PENS รอทำลาย");
				subParameterMap1.put("reportName2","(ต้นฉบับ)");  	
				subParameterMap1.put("reportName3","(สำหรับบัญชีลูกหนี้)");  	
				subParameterMap1.put("pens_logo_fit",logopath);
				subParameterMap1.put("request_date",p.getRequestDate());
				subParameterMap1.put("request_number",p.getRequestNumber());
				subParameterMap1.put("customer_code",p.getCustomerCode());
				subParameterMap1.put("customer_name",p.getCustomerName());
				subParameterMap1.put("description",p.getDescription());
				subParameterMap1.put("totalAllAmount",p.getTotalAllAmount());
				subParameterMap1.put("totalAllVatAmount",p.getTotalAllVatAmount());
				subParameterMap1.put("totalAllNonVatAmount",p.getTotalAllNonVatAmount());
				subParameterMap1.put("sales_code",user.getCode());
				subParameterMap1.put("sales_name",user.getName());
				subParameterMap1.put("printDate",Utils.isNull(p.getPrintDate()));
				subParameterMap1.put("userPrint",user.getName());
				subParameterMap1.put("address",p.getAddress1()+" "+p.getAddress2());
				
				//subReport2
				HashMap<String,Object> subParameterMap2 = new HashMap<String,Object>();
				subParameterMap2.put("reportName","ใบอนุมัติให้คืนคลัง PENS รอทำลาย");
				subParameterMap2.put("reportName2","(สำเนา)");  
				subParameterMap2.put("reportName3","(สำหรับคลังสินค้า)");  	
				subParameterMap2.put("pens_logo_fit",logopath);
				subParameterMap2.put("request_date",p.getRequestDate());
				subParameterMap2.put("request_number",p.getRequestNumber());
				subParameterMap2.put("customer_code",p.getCustomerCode());
				subParameterMap2.put("customer_name",p.getCustomerName());
				subParameterMap2.put("description",p.getDescription());
				subParameterMap2.put("totalAllAmount",p.getTotalAllAmount());
				subParameterMap2.put("totalAllVatAmount",p.getTotalAllVatAmount());
				subParameterMap2.put("totalAllNonVatAmount",p.getTotalAllNonVatAmount());
				subParameterMap2.put("sales_code",user.getCode());
				subParameterMap2.put("sales_name",user.getName());
				subParameterMap2.put("printDate",Utils.isNull(p.getPrintDate()));
				subParameterMap2.put("userPrint",user.getName());
				subParameterMap2.put("address",p.getAddress1()+" "+p.getAddress2());
				
				//mainReport
				parameterMap.put("parameter_subreport",parameterMap);
	            parameterMap.put("found_data_subreport","found");
	            parameterMap.put("subDataList", mResultList);
	            parameterMap.put("SUBREPORT_DIR",BeanParameter.getReportPath());
	            parameterMap.put("parameter_subreport1",subParameterMap1);
	            parameterMap.put("parameter_subreport2",subParameterMap2);
	            
				if(mResultList != null && mResultList.size()>0){
					//set for display report
					List<StockReturnLine> showList = new ArrayList<StockReturnLine>();
					showList.add(new StockReturnLine());
					
					reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName,showList ,fileNameExport);
				    
				}else{
					request.setAttribute("Message","Data not found");
				}
				// save token
				saveToken(request);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}finally{
				conn.close();conn=null;
			}
			return null;
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
	public static void prepareSearchStockCreditReturn(HttpServletRequest request,Connection conn,User user,String pageName){
		PopupBean item = null;
		List<PopupBean> dataList = null;
		try{

			//Cust Cat No List
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			
			List<PopupBean> dataTempList = CustomerCatDAO.searchCustomerCatListModel(conn, "ORDER - CREDIT SALES",user,"ROLE_CR_STOCK");
			if(dataTempList != null &&dataTempList.size() ==1){
			  dataList.addAll(dataTempList);
			}else{
			  dataList.add(item);
			  dataList.addAll(dataTempList);
			}
			request.getSession().setAttribute("CUST_CAT_LIST",dataList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesChannelList_s =SalesrepChannelDAO.searchSalesrepChannelListModel(conn,user,"ROLE_CR_STOCK");
			dataList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",dataList);
			
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
			
			List<PopupBean> salesrepList_s = SalesrepDAO.searchSalesrepListAll(conn,"StockReturn","","CREDIT SALES","",user,"ROLE_CR_STOCK");
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
}
