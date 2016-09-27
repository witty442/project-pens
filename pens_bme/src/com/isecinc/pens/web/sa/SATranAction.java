package com.isecinc.pens.web.sa;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.SAEmpBean;
import com.isecinc.pens.bean.SAReportBean;
import com.isecinc.pens.bean.SATranBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SAEmpDAO;
import com.isecinc.pens.dao.SATranDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SATranAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		SATranForm aForm = (SATranForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				SATranBean ad = new SATranBean();
				
				aForm.setBean(ad);
			}else if("back".equals(action)){
				SATranBean oldCri = aForm.getBeanCriteria();
				
				aForm.setBean(SATranDAO.searchHead(oldCri,""));
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		SATranForm aForm = (SATranForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setBean(SATranDAO.searchHead(aForm.getBean(),""));
			aForm.setResultsSearch(aForm.getBean().getItems());
			
			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
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
		String forward = "detail";
		SATranForm aForm = (SATranForm) form;
		User user = (User) request.getSession().getAttribute("user");
		SATranBean bean = new SATranBean();
		logger.debug("prepare");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String empId = Utils.isNull(request.getParameter("empId"));
            String payDate = Utils.isNull(request.getParameter("payDate"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit empId:"+empId+",action:"+action);
			
			//init default value
			SAEmpBean b= SAEmpDAO.getEmp(empId);
			bean.setEmpId(empId);
			bean.setName(b.getName());
			bean.setSurname(b.getSurName());
	        bean.setPayDate(payDate);
			
			//init data yearMonth old and new
	        List<SATranBean> items = SATranDAO.initYearMonth(bean.getEmpId(),bean.getPayDate());
	        if(items != null){
			    bean.setItems(items);
			    bean.setCanEdit(true);
				
				aForm.setBean(bean);
				aForm.setMode(action);//Mode Edit ,Add
	        }else{
	        	bean.setItems(null);
	        	bean.setCanEdit(true);
				
				aForm.setBean(bean);
				aForm.setMode(action);//Mode Edit ,Add
	        	request.setAttribute("Message","ไม่มีการระบุข้อมูล วันที่เริ่มให้ ค่าเฝ้าตู้");
	        	return "detail";
	        }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			
		}
		return forward;
	}
	
	public ActionForward prepareReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareReport");
		SATranForm aForm = (SATranForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				SATranBean ad = new SATranBean();
				
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward searchReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchReport");
		SATranForm aForm = (SATranForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			SATranBean b = aForm.getBean();
			//aForm.setBean(MCBeanDAO.searchHead(aForm.getBean()));
			//aForm.setResultsSearch(aForm.getBean().getItems());
			
			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("detail");
	}
	
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		SATranForm aForm = (SATranForm) form;
		try {
			aForm.setResultsSearch(null);
			aForm.setBean(new SATranBean());

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SATranForm summaryForm = (SATranForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "detail";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SATranForm orderForm = (SATranForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		SATranForm aForm = (SATranForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int r=  0;
		int lineId = 0;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SATranBean h = aForm.getBean();
			logger.debug("mode:"+aForm.getMode());
			//head 
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Get Data Table Item
			String[] yearMonth = request.getParameterValues("yearMonth");
			String[] bmeAmt = request.getParameterValues("bmeAmt");
			String[] wacoalAmt = request.getParameterValues("wacoalAmt");
			String[] canSave = request.getParameterValues("canSave");
			String[] status = request.getParameterValues("status");
			
			//Add data  to List
			List<SATranBean> itemList = new ArrayList<SATranBean>();
			for(int i=0;i<yearMonth.length;i++){
				logger.debug("yearMonth["+i+"]["+Utils.isNull(yearMonth[i])+"]");
				logger.debug("canSave["+i+"]["+Utils.isNull(canSave[i])+"]");
				logger.debug("status["+i+"]["+Utils.isNull(status[i])+"]");
				
				if( Utils.isNull(canSave[i]).equalsIgnoreCase("canSave") 
					|| Utils.isNull(status[i]).equalsIgnoreCase("DB") ){
					SATranBean item = new SATranBean();
				
					//set key
					item.setEmpId(h.getEmpId());
					item.setPayDate(h.getPayDate());
					
					item.setYearMonth(yearMonth[i]);
					item.setBmeAmt(Utils.isNull(bmeAmt[i]));
					item.setWacoalAmt(Utils.isNull(wacoalAmt[i]));
					item.setCreateUser(h.getCreateUser());
					item.setUpdateUser(h.getUpdateUser());
					
					//itemList.add(item);
					
					if( Utils.isNull(canSave[i]).equals("canSave")){
					    //update or insert
						int u = SATranDAO.updateModel(conn, item);
						if(u==0){
						   SATranDAO.insertModel(conn, item);
						}
					}else{
						//delete
						if( Utils.isNull(canSave[i]).equals("")){
						  SATranDAO.deleteModel(conn, item);
						}
					}
				}//if
			}//for
			
			//set Date to Bean case Show Error
			//h.setItems(itemList);
			//aForm.setBean(h);

		    conn.commit();
		    request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		    
			//Search Again
			h.setItems(SATranDAO.initYearMonth(h.getEmpId(),h.getPayDate()));
			h.setCanEdit(true);
		    aForm.setBean(h);
		    aForm.setMode("edit");
		    
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
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		SATranForm aForm = (SATranForm) form;
		try {
			aForm.setResults(new ArrayList<SATranBean>());
			
			SATranBean bean = new SATranBean();
			bean.setType("BME");
			bean.setCanEdit(true);
			aForm.setBean(bean);
			
			aForm.setMode("add");
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		SATranForm aForm = (SATranForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		boolean found = false;
		try {
			
			logger.debug("PageAction:"+request.getParameter("page"));
			/** Onhand **/
	
			SATranBean cri = aForm.getBean();
			
			htmlTable = SAExportExcel.genSARewardTranReport(cri,user);
			if( !"".equals(htmlTable.toString())){
				found = true;
			}
			
			if(found){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(htmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
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
