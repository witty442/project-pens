package com.isecinc.pens.web.summary;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.ExcelHeader;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Summary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MSummary;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SummaryAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		SummaryForm summaryForm = (SummaryForm) form;
		try {
			 logger.debug("prepare");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 summaryForm.setResults(null);
			 }
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SummaryForm summaryForm = (SummaryForm) form;
		try {
			logger.debug("prepare 2");
			if("new".equalsIgnoreCase(request.getParameter("action"))){
				request.getSession().setAttribute("results", null);
				summaryForm.setResults(null);
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SummaryForm summaryForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			List<Summary> results = new MSummary().search(summaryForm.getSummary(),user);
			if (results != null) {
				//request.getSession().setAttribute("results", results);
				summaryForm.setResults(results);
				
				logger.debug("results:"+summaryForm.getResults());
				
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		SummaryForm summaryForm = (SummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			if(summaryForm.getResults() != null){
			   
			    StringBuffer htmlTable = genHTML(request,summaryForm,user);	 
			
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(htmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		    
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("export");
	}
	
	
	private StringBuffer genHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		boolean isTotal = false;
		try{
			//Header
			isTotal = form.getSummary().getType().equals("TOTAL")?true:false;
			
			h.append(ExcelHeader.EXCEL_HEADER);
			
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9'>รายงานการขาย By Item </td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >จากวันที่ขาย:"+form.getSummary().getOrderDateFrom()+"  ถึงวันที่ขาย:"+form.getSummary().getOrderDateTo()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >จากรหัสสินค้า:"+form.getSummary().getProductCodeFrom()+"  ถึงรหัสสินค้า:"+form.getSummary().getProductCodeTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >การแสดงผล :"+(isTotal?"ผลรวม":"รายละเอียด")+" </td>\n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >พนักงานขาย :"+user.getSourceName()+"("+user.getRole().getName()+") วันที่แสดงข้อมูล :"+Utils.format(new Date(), Utils.DD_MM_YYYY__HH_mm_ss_WITH_SLASH)+"</td>\n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >&nbsp; </td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResults() != null){
			    List<Summary> list = (List)form.getResults();
			    
				h.append("<table border='1'> \n");
				
				h.append("<tr> \n");
				  if(!isTotal){
				    h.append("<td>วันที่ขาย</td> \n");
				  }
				  h.append("<td>รหัสสินค้า</td> \n");
				  h.append("<td>ชื่อสินค้า</td> \n");
				  
				  h.append("<td colspan='2'>จำนวนขาย</td> \n");
				  h.append("<td colspan='2'>จำนวนของแถม</td> \n");
				  h.append("<td colspan='2'>หน่วย</td> \n");

				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					Summary s = (Summary)list.get(i);
					h.append("<tr> \n");
					if(!isTotal){
					   h.append("<td class='text'>"+s.getOrderDate()+"</td> \n");
					}
					  h.append("<td class='text'>"+s.getProductCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getProductName()+"</td> \n");
					  
					  if( !Utils.isNull(s.getQty()).equals("")){
						  String s1 = s.getQty().substring(0,s.getQty().indexOf("/"));
						  String s2 = s.getQty().substring(s.getQty().indexOf("/")+1);
						  h.append("<td class='num'>"+s1+"</td> \n");
						  h.append("<td class='num'>"+s2+"</td> \n");
					  }else{
						  h.append("<td>&nbsp;</td> \n");
						  h.append("<td>&nbsp;</td> \n");
					  }
					  
					  if( !Utils.isNull(s.getQtyPromotion()).equals("")){
						  String s1 = s.getQtyPromotion().substring(0,s.getQtyPromotion().indexOf("/"));
						  String s2 = s.getQtyPromotion().substring(s.getQtyPromotion().indexOf("/")+1);
						  h.append("<td class='num'>"+s1+"</td> \n");
						  h.append("<td class='num'>"+s2+"</td> \n");
					  }else{
						  h.append("<td>&nbsp;</td> \n");
						  h.append("<td>&nbsp;</td> \n");
					  }
					  
					  if( !Utils.isNull(s.getFullUOM()).equals("")){
						  String s1 = s.getFullUOM().substring(0,s.getFullUOM().indexOf("/"));
						  String s2 = s.getFullUOM().substring(s.getFullUOM().indexOf("/")+1);
						  h.append("<td class='num'>"+s1+"</td> \n");
						  h.append("<td class='num'>"+s2+"</td> \n");
					  }else{
						  h.append("<td>&nbsp;</td> \n");
						  h.append("<td>&nbsp;</td> \n");
					  }
					  
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		SummaryForm summaryForm = (SummaryForm) form;
		try {
			 request.getSession().setAttribute("results", null);
			 summaryForm.setResults(null);
		} catch (Exception e) {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		SummaryForm summaryForm = (SummaryForm) form;
		try {
			 summaryForm.setSummary(new Summary());
			 summaryForm.setResults(null);
			 request.getSession().setAttribute("results", null);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
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
