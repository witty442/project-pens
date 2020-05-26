package com.isecinc.pens.web.reportall.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.isecinc.pens.dao.SalesrepChannelDAO;
import com.isecinc.pens.dao.SalesrepZoneDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.boxno.BoxNoBean;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.bean.StockReturn;
import com.isecinc.pens.web.reportall.bean.StockReturnLine;
import com.isecinc.pens.web.reportall.page.dao.BoxNoNissinReportDAO;
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
public class BoxNoNissinReportAction extends I_Action {
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
				request.getSession().setAttribute("reportAllForm_RESULTS",null);
				
				ReportAllBean bean = new ReportAllBean();
				BoxNoBean stockBean  =new BoxNoBean();
				bean.setBoxNoBean(stockBean);
				aForm.setBean(bean);
				
				conn = DBConnection.getInstance().getConnectionApps();
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
				aForm.setTotalRecord(BoxNoNissinReportDAO.searchTotalRecBoxNoList(conn,user,aForm.getBean().getBoxNoBean()));
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
				BoxNoBean beanResult = BoxNoNissinReportDAO.searchBoxNoList(conn,user,aForm,allRec,currPage,pageSize,excel);
				//logger.debug("dataHTMLStr:"+stockResult.getDataStrBuffer());
				if(beanResult != null && beanResult.getDataStrBuffer() != null){
					 aForm.getBean().getBoxNoBean().setDataStrBuffer(beanResult.getDataStrBuffer());
				}else{
					 request.setAttribute("Message", "ไม่พบข้อมูล");
					 aForm.getBean().getBoxNoBean().setDataStrBuffer(null);
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
			    BoxNoBean beanResult = BoxNoNissinReportDAO.searchBoxNoList(conn,user,aForm,allRec,currPage,pageSize,excel);
				if(beanResult != null && beanResult.getDataStrBuffer() != null){
					aForm.getBean().getBoxNoBean().setDataStrBuffer(beanResult.getDataStrBuffer());
				}
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
		try {
			action = Utils.isNull(request.getParameter("action"));
			conn = DBConnection.getInstance().getConnectionApps();
			
			BoxNoBean beanResult = BoxNoNissinReportDAO.searchBoxNoList(conn,user,aForm,true,0,pageSize,true);
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
	
 public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printReport");
		String reportName = Utils.isNull(request.getParameter("reportName"));
		logger.debug("reportName:"+reportName);
		if("controlBoxNoReport".equalsIgnoreCase(reportName)){
			return printReportControlBoxNo(mapping, form, request, response);
		}else if("controlBoxNoByZoneReport".equalsIgnoreCase(reportName)){
			return printReportControlBoxNoByZone(mapping, form, request, response);
		}
		return null;
 }
	 
 public ActionForward printReportControlBoxNo(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printReportControlBoxNo");
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		BoxNoBean p  =null;
		String fileNameExport  = "ReturnNissin.pdf";
		String fileName = "control_boxno_report";
        String fileJasper = BeanParameter.getReportPath() + fileName;
		try { 
			ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,Object> parameterMap = new HashMap<String,Object>();

            //init connection 
			conn = DBConnection.getInstance().getConnectionApps();
	        p = new BoxNoBean();
	        p.setPeriod(Utils.isNull(request.getParameter("period")));
	        p.setPdCode(Utils.isNull(request.getParameter("pdCode")));

			p = BoxNoNissinReportDAO.searchBoxNoReportList(conn, user, p);
			List<BoxNoBean> itemList = p.getItemsList();
			if(itemList != null && itemList.size() >0){
				//save print,count and gen DocNo
				p = BoxNoNissinReportDAO.savePrintControlBoxNo(conn,user,p);
				
				//set parameter report
				parameterMap.put("printDate",DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_HH_MM_SS_WITH_SLASH,DateUtil.local_th));
				parameterMap.put("userName",user.getUserName());
				parameterMap.put("pdCode",p.getPdCode() +" "+BoxNoNissinReportDAO.getPdDesc(conn, "S", p.getPdCode()));
				parameterMap.put("period",p.getPeriod());
				parameterMap.put("docNo",p.getDocNo());
				parameterMap.put("sumTotalBox",p.getTotalBox());
				
				reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName,itemList ,fileNameExport);
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("reportAll");
	}
	
	 public ActionForward printReportControlBoxNoByZone(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printReportControlBoxNoByZone");
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		BoxNoBean p  =null;
		String fileNameExport  = "ReturnNissinByZone.pdf";
		String fileName = "control_boxno_byzone_report";
        String fileJasper = BeanParameter.getReportPath() + fileName;
	    try { 
			ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,Object> parameterMap = new HashMap<String,Object>();

            //init connection 
			conn = DBConnection.getInstance().getConnectionApps();
	        p = new BoxNoBean();
	        p.setPeriod(Utils.isNull(request.getParameter("period")));
	        p.setSalesZone(Utils.isNull(request.getParameter("salesZone")));

			p = BoxNoNissinReportDAO.searchBoxNoByZoneReportList(conn, user, p);
		
			List<BoxNoBean> itemList = p.getItemsList();
			logger.debug("size:"+itemList.size());
			if(itemList != null && itemList.size() >0){
				//set parameter report
				parameterMap.put("printDate",DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_HH_MM_SS_WITH_SLASH,DateUtil.local_th));
				parameterMap.put("userName",user.getUserName());
				parameterMap.put("period",p.getPeriod());
				parameterMap.put("zone",SalesrepZoneDAO.getSalesZoneDesc(conn,p.getSalesZone()));
				parameterMap.put("sumTotalBox",p.getTotalBox());
				
				reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName,itemList ,fileNameExport);
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("reportAll");
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
			//init periodList
			request.getSession().setAttribute("PERIOD_LIST", initPeriod());
			 
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
		
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static List<PopupBean> initPeriod(){
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		String periodValue = "";
		PopupBean item = new PopupBean();
		try{
			//blank
			item.setKeyName("");
			item.setValue("");
			monthYearList.add(item);
			
			for(int i=0;i<=1;i++){
				item = new PopupBean();
				periodValue =  DateUtil.stringValue(cal.getTime(),"MM/yyyy",DateUtil.local_th).toUpperCase();
				periodName =  DateUtil.stringValue(cal.getTime(),"MMMMM-yyyy",DateUtil.local_th).toUpperCase();
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
	
}
