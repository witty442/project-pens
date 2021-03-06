package com.isecinc.pens.web.sa;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.SAEmpBean;
import com.isecinc.pens.bean.SATranBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SAEmpDAO;
import com.isecinc.pens.dao.SATranDAO;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

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
				//Can Edit
				if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
					ad.setCanEdit(true);
				}
				aForm.setBean(ad);
			}else if("back".equals(action)){
				SATranBean oldCri = aForm.getBeanCriteria();
				
				aForm.setBean(SATranDAO.searchHead(oldCri,""));
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
			SATranBean bean = SATranDAO.searchHead(aForm.getBean(),"");
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
				bean.setCanEdit(true);
			}
			aForm.setBean(bean);
			aForm.setResultsSearch(aForm.getBean().getItems());
			
			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "��辺������");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
            String type = Utils.isNull(request.getParameter("type"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit empId:"+empId+",action:"+action+"type:"+type);
			
			//init default value
			SAEmpBean b= SAEmpDAO.getEmp(empId);
			bean.setEmpId(empId);
			bean.setName(b.getName());
			bean.setSurname(b.getSurName());
	        bean.setPayDate(payDate);
	        bean.setType(type);
			
			//init data yearMonth old and new
	        SATranBean re = SATranDAO.initYearMonth(bean.getEmpId(),bean.getPayDate(),bean.getType());
			bean.setCountStockDate(re.getCountStockDate());
	        List<SATranBean> items = re.getItems();
	        
	        if(items != null){
			    bean.setItems(items);
			  //Can Edit
				if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
					bean.setCanEdit(true);
				}
				if("view".equalsIgnoreCase(action)){
					bean.setCanEdit(false);
				}
				aForm.setBean(bean);
				aForm.setMode(action);//Mode Edit ,Add
	        }else{
	        	bean.setItems(null);
	        	//Can Edit
				if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
					bean.setCanEdit(true);
				}
				if("view".equalsIgnoreCase(action)){
					bean.setCanEdit(false);
				}
				
				aForm.setBean(bean);
				aForm.setMode(action);//Mode Edit ,Add
				
	        	request.setAttribute("Message","����ա���кآ����� �ѹ����������� �����ҵ��");
	        	return "detail";
	        }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
			   request.setAttribute("Message", "��辺������");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return mapping.findForward("detail");
	}
	
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		User user = (User) request.getSession().getAttribute("user");
		SATranForm aForm = (SATranForm) form;
		try {
			aForm.setResultsSearch(null);
			SATranBean bean = new SATranBean();
			//Can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
				bean.setCanEdit(true);
			}
			aForm.setBean(bean);

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
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
			String[] amt = request.getParameterValues("amt");
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
					item.setType(h.getType());
					item.setPayDate(h.getPayDate());
					item.setCountStockDate(h.getCountStockDate());
					
					item.setYearMonth(yearMonth[i]);
					item.setAmt(Utils.isNull(amt[i]));
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
		    request.setAttribute("Message", "�ѹ�֡���������º��������");
		    
			//Search Again
		    SATranBean re = SATranDAO.initYearMonth(h.getEmpId(),h.getPayDate(),h.getType());
			h.setItems(re.getItems());
			h.setCountStockDate(re.getCountStockDate());
			//Can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
				h.setCanEdit(true);
			}
		    aForm.setBean(h);
		    aForm.setMode("edit");
		    
		} catch (Exception e) {
			conn.rollback();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
		User user = (User) request.getSession().getAttribute("user");
		SATranForm aForm = (SATranForm) form;
		try {
			aForm.setResults(new ArrayList<SATranBean>());
			
			SATranBean bean = new SATranBean();
			bean.setType("BME");
			//Can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
				bean.setCanEdit(true);
			}
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
				request.setAttribute("Message","��辺������");
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
