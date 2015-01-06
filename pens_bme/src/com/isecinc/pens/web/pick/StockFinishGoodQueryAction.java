package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
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
import com.isecinc.pens.bean.StockQuery;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.StockQueryDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockFinishGoodQueryAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		StockFinishGoodQueryForm aForm = (StockFinishGoodQueryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equalsIgnoreCase(action)){
				aForm.setBean(new StockQuery());
			}else{
				
			}
		
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StockFinishGoodQueryForm summaryForm = (StockFinishGoodQueryForm) form;
		try {
			logger.debug("prepare 2");
			
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
		StockFinishGoodQueryForm aForm = (StockFinishGoodQueryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			List<StockQuery> dataList = null;
			StockQuery b = aForm.getBean();
			if("Detail".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				b =  StockQueryDAO.searchSummaryFinishGoodByDetail(aForm.getBean());
				dataList = b.getItems();
				
			}else if("SummaryByPensItem".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				b = StockQueryDAO.searchSummaryFinishGoodByPensItem(aForm.getBean());
				dataList = b.getItems();
			}
			
			if(dataList != null && dataList.size() >0){
				aForm.setResults(dataList);
			}else{
				aForm.setResults(null);
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
			aForm.setBean(b);
			
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
		StockFinishGoodQueryForm aForm = (StockFinishGoodQueryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
            e.printStackTrace();
			return "prepare";
		} finally {
		}
		return "search";
	}
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		StockFinishGoodQueryForm aForm = (StockFinishGoodQueryForm) form;
		try {
			aForm.setResults(new ArrayList<StockQuery>());
			
			StockQuery ad = new StockQuery();
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}

	
	/**
	 * Print Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportExcel : ");
		StockFinishGoodQueryForm reportForm = (StockFinishGoodQueryForm) form;
		try {
	         if(reportForm.getResults() == null || (reportForm.getResults() != null && reportForm.getResults().size() ==0)){
	        	 request.setAttribute("Message" ,"ไม่พบข้อมูล");
	         }else{
			    StringBuffer htmlTable = genHTML(request,reportForm);	 
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(htmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
	         }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		// return null;
		return null;
	}
	
	private StringBuffer genHTML(HttpServletRequest request,StockFinishGoodQueryForm form){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			StockQuery b = form.getBean();
			
			int colSpan = 0;
			String title = "";
			if("Detail".equalsIgnoreCase(form.getBean().getSummaryType())){
				colSpan = 6;
				title = "Stock Finish Goods Query By Detail";
			}else if("SummaryByPensItem".equalsIgnoreCase(form.getBean().getSummaryType())){
				colSpan = 4;
				title = "Stock Finish Goods Query By Pens Item";
			}
			
			h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append("<td align='left' colspan='"+colSpan+"'>"+title+" </td> \n");
				h.append("</tr> \n");
				
				h.append("<tr> \n");
				h.append("<td align='left' colspan='"+colSpan+"' >From Pens Item:"+form.getBean().getPensItemFrom()+"  To Pens Item:"+form.getBean().getPensItemTo()+"</td> \n");
				h.append("</tr> \n");
	
				h.append("<tr> \n");
				h.append("<td align='left' colspan='"+colSpan+"' >From Group Code:"+form.getBean().getGroupCodeFrom()+" To Group Code:"+form.getBean().getGroupCodeTo()+"</td> \n");
				h.append("</tr> \n");
				
				h.append("<tr> \n");
				h.append("<td align='left' colspan='"+colSpan+"' >สถานะ :"+PickConstants.getStatusDesc(form.getBean().getStatus())+" </td>\n");
				h.append("</tr> \n");

			h.append("</table> \n");

			if(form.getResults() != null){
			    List<StockQuery> dataList = form.getResults();
				h.append("<table border='1'> \n");
				h.append("<tr> \n");

				if("Detail".equalsIgnoreCase(form.getBean().getSummaryType())){
					 h.append("<td>Wacoal Mat.</td> \n");
					 h.append("<td>Group Code</td> \n");
					 h.append("<td>Pens Item</td> \n");
					 h.append("<td>Barcode</td> \n");
					 h.append("<td>จำนวน</td> \n");
					 h.append("<td>Status</td> \n");

				}else if("SummaryByPensItem".equalsIgnoreCase(form.getBean().getSummaryType())){
					h.append("<td>Pens Item</td> \n");
					h.append("<td>Group Code.</td> \n");
					h.append("<td>จำนวน</td> \n");
					h.append("<td>Status</td> \n");
				}

				h.append("</tr> \n");
				
				for(int i=0;i<dataList.size();i++){
					StockQuery s = (StockQuery)dataList.get(i);
					h.append("<tr> \n");
					if("Detail".equalsIgnoreCase(form.getBean().getSummaryType())){
					   h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					   h.append("<td>"+s.getGroupCode()+"</td> \n");
					   h.append("<td>"+s.getPensItem()+"</td> \n");
					   h.append("<td>"+s.getBarcode()+"&nbsp;</td> \n");
					   h.append("<td>"+s.getOnhandQty()+"</td> \n");
					   h.append("<td>"+s.getStatusDesc()+"</td> \n");
	
					}else if("SummaryByPensItem".equalsIgnoreCase(form.getBean().getSummaryType())){
					   h.append("<td>"+s.getPensItem()+"</td> \n");
					   h.append("<td>"+s.getGroupCode()+"</td> \n");
					   h.append("<td>"+s.getOnhandQty()+"</td> \n");
					   h.append("<td>"+s.getStatusDesc()+"</td> \n");
					}
					h.append("</tr>");
				}//for 
				
				if("SummaryByPensItem".equalsIgnoreCase(form.getBean().getSummaryType())){
					 h.append("<td></td> \n");
					 h.append("<td>Total QTY</td> \n");
					 h.append("<td>"+b.getTotalQty()+"</td> \n");
					 h.append("<td></td> \n");
				}else{
					 h.append("<td></td> \n");
					 h.append("<td></td> \n");
					 h.append("<td></td> \n");
					 h.append("<td>Total QTY</td> \n");
					 h.append("<td>"+b.getTotalQty()+"</td> \n");
					 h.append("<td></td> \n");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
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
