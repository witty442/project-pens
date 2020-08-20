package com.isecinc.pens.web.buds.page;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.StockReturn;
import com.isecinc.pens.bean.StockReturnLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MStockReturn;
import com.isecinc.pens.web.buds.BudsAllBean;
import com.isecinc.pens.web.buds.BudsAllForm;
import com.isecinc.pens.web.buds.page.export.ControlPickingExport;
import com.isecinc.pens.web.stockreturn.StockReturnForm;
import com.pens.util.BeanParameter;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ControlPickingAction extends I_Action {

	public static int pageSize = 60;
	
	public ActionForward prepareSearchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearchHead");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		BudsAllBean bean = new BudsAllBean();
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				bean.setConfPickingBean(new ConfPickingBean());
				aForm.setBean(bean);
			 }
			 aForm.setPageName(Utils.isNull(request.getParameter("pageName")));
			 aForm.setSubPageName(Utils.isNull(request.getParameter("subPageName")));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnectionApps.getInstance().getConnection();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.getBean().setConfPickingBean((ConfPickingBean)request.getSession().getAttribute("_criteria"));
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				aForm.setPageSize(pageSize);
				
				//get Total Record
				aForm.setTotalRecord(ControlPickingDAO.searchTotalHead(conn,aForm.getBean().getConfPickingBean()));
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
				ConfPickingBean confPickingBean = ControlPickingDAO.searchHead(conn,aForm.getBean().getConfPickingBean(),false,allRec,currPage,pageSize);
				if(confPickingBean.getItemsList().size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				aForm.getBean().setConfPickingBean(confPickingBean);
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
			    ConfPickingBean confPickingBean = ControlPickingDAO.searchHead(conn,aForm.getBean().getConfPickingBean(),false,allRec,currPage,pageSize);
				if(confPickingBean.getItemsList().size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				aForm.getBean().setConfPickingBean(confPickingBean);
				
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("budsAll");
	}
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "budsAll";
		BudsAllForm aForm = (BudsAllForm) form;
		BudsAllBean bean = new BudsAllBean();
		Connection conn = null;
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"]subPageName["+request.getParameter("subPageName")+"]");
			 logger.debug("action["+request.getParameter("action")+"]");
			 conn = DBConnectionApps.getInstance().getConnection();
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().removeAttribute("_criteria");
				 
			    if("ControlPicking".equalsIgnoreCase(Utils.isNull(request.getParameter("subPageName"))) ){
				   bean.setConfPickingBean(new ConfPickingBean());
				   aForm.setBean(bean);
			    }
			 }
			 aForm.setPageName(Utils.isNull(request.getParameter("pageName")));
			 aForm.setSubPageName(Utils.isNull(request.getParameter("subPageName")));
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			conn.close();
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "";
	}

	/**
	 * Search
	 */
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//set for display by page
			request.getSession().setAttribute("summary" ,null);
			budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "budsAll";
	}
	
	public ActionForward exportControlPicking(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportControlPicking");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ConfPickingBean bean = null;
		try {
			logger.debug("pickingNo:"+Utils.isNull(request.getParameter("pickingNo")));
			logger.debug("summaryType:"+Utils.isNull(request.getParameter("summaryType")));
			//set parameter
			budsAllForm.getBean().getConfPickingBean().setPickingNo(Utils.isNull(request.getParameter("pickingNo")));
			budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			
			//search and export
			if(Utils.isNull(request.getParameter("summaryType")).equalsIgnoreCase("summary")){
			    bean = ControlPickingExport.searchControlPickingReportTypeSummary(budsAllForm.getBean().getConfPickingBean(), true, user);
			}else{
				bean = ControlPickingExport.searchControlPickingReportTypeDetail(budsAllForm.getBean().getConfPickingBean(), true, user);
			}
			
			if(bean.getDataStrBuffer() != null &&  bean.getDataStrBuffer().length() >0){
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(bean.getDataStrBuffer().toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			    
			    budsAllForm.getBean().getConfPickingBean().setDataStrBuffer(null);
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
	
  public ActionForward printControlPickingReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printControlPickingReport");
		BudsAllForm aForm = (BudsAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		String fileNameExport  = "ControlPickingReport.pdf";
		try { 
			ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,Object> parameterMap = new HashMap<String,Object>();
			ServletContext context = request.getSession().getServletContext();
	
			String fileName = "control_picking_report";
            String fileJasper = BeanParameter.getReportPath() + fileName;
          
            //init connection 
			conn = DBConnectionApps.getInstance().getConnection();
			
			//head
			String logopath =   context.getRealPath("/images/pens_logo_fit.jpg");
			logger.debug("reportType:"+Utils.isNull(request.getParameter("reportType")));
		
            //detail
			List<ConfPickingBean> resultList = ControlPickingDAO.searchPickingReport(aForm.getBean().getConfPickingBean(), user);
			logger.debug("resultList :"+resultList.size());
			
		   //set main parameter
            parameterMap.put("pickingNo",aForm.getBean().getConfPickingBean().getPickingNo());
            parameterMap.put("transactionDate",aForm.getBean().getConfPickingBean().getTransactionDate());
            parameterMap.put("userPrint",user.getName());
          
            reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName,resultList ,fileNameExport);
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn !=null){
				conn.close();conn= null;
			}
		}
		return null;
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			 
		} catch (Exception e) {
			request.setAttribute("Message",e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
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
