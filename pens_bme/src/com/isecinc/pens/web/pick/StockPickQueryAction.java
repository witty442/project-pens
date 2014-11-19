package com.isecinc.pens.web.pick;

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
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.JobDAO;
import com.isecinc.pens.dao.StockPickQueryDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockPickQueryAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		StockPickQueryForm aForm = (StockPickQueryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equalsIgnoreCase(action)){
				aForm.setBean(new Barcode());
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
		StockPickQueryForm summaryForm = (StockPickQueryForm) form;
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
		StockPickQueryForm aForm = (StockPickQueryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			List<Barcode> dataList = null;
			Barcode b = aForm.getBean();
			if("Detail".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				dataList = StockPickQueryDAO.searchSummaryByDetail(aForm.getBean());
			}else if("SummaryByBox".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				b = StockPickQueryDAO.searchSummaryByBoxNo(aForm.getBean());
				dataList= b.getItems();
			}else if("SummaryByPensItem".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				b = StockPickQueryDAO.searchSummaryByPensItem(aForm.getBean());
				dataList= b.getItems();
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
		StockPickQueryForm aForm = (StockPickQueryForm) form;
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
		StockPickQueryForm aForm = (StockPickQueryForm) form;
		try {
			aForm.setResults(new ArrayList<Barcode>());
			
			Barcode ad = new Barcode();
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			ad.setCanEdit(true);
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
		StockPickQueryForm reportForm = (StockPickQueryForm) form;
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
	
	private StringBuffer genHTML(HttpServletRequest request,StockPickQueryForm form){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			Barcode b = form.getBean();
			
			int colSpan = 0;
			String title = "";
			if("Detail".equalsIgnoreCase(form.getBean().getSummaryType())){
				colSpan = 9;
				title = "Stock Pick Query By Detail";
			}else if("SummaryByBox".equalsIgnoreCase(form.getBean().getSummaryType())){
				colSpan = 3;
				title = "Stock Pick Query By Box";
			}else if("SummaryByPensItem".equalsIgnoreCase(form.getBean().getSummaryType())){
				colSpan = 3;
				title = "Stock Pick Query By Pens Item";
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
				h.append("<td align='left' colspan='"+colSpan+"' >สถานะ :"+form.getBean().getStatus()+" </td>\n");
				h.append("</tr> \n");

			h.append("</table> \n");

			if(form.getResults() != null){
			    List<Barcode> dataList = form.getResults();
				h.append("<table border='1'> \n");
				h.append("<tr> \n");

				if("Detail".equalsIgnoreCase(form.getBean().getSummaryType())){
					 h.append("<td>เลขที่กล่อง</td> \n");
					 h.append("<td>Wacoal Mat.</td> \n");
					 h.append("<td>Group Code</td> \n");
					 h.append("<td>Pens Item</td> \n");
					 h.append("<td>Barcode</td> \n");
					 h.append("<td>Job Id</td> \n");
					 h.append("<td>รับคืนจาก</td> \n");
					 h.append("<td>หมายเหตุ</td> \n");
					 h.append("<td>Status</td> \n");
				}else if("SummaryByBox".equalsIgnoreCase(form.getBean().getSummaryType())){
					
					h.append("<td>เลขที่กล่อง</td> \n");
					h.append("<td>รับคืนจาก.</td> \n");
					h.append("<td>จำนวน</td> \n");
					
				}else if("SummaryByPensItem".equalsIgnoreCase(form.getBean().getSummaryType())){
					h.append("<td>Pens Item</td> \n");
					h.append("<td>Group Code.</td> \n");
					h.append("<td>จำนวน</td> \n");
				}

				h.append("</tr> \n");
				
				for(int i=0;i<dataList.size();i++){
					Barcode s = (Barcode)dataList.get(i);
					h.append("<tr> \n");
					if("Detail".equalsIgnoreCase(form.getBean().getSummaryType())){
					   h.append("<td>"+s.getBoxNo()+"&nbsp;</td> \n");
					   h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					   h.append("<td>"+s.getGroupCode()+"</td> \n");
					   h.append("<td>"+s.getPensItem()+"</td> \n");
					   h.append("<td>"+s.getBarcode()+"&nbsp;</td> \n");
					   h.append("<td>"+s.getJobId()+"&nbsp;</td> \n");
					   h.append("<td>"+s.getName()+"</td> \n");
					   h.append("<td>"+s.getRemark()+"</td> \n");
					   h.append("<td>"+s.getStatusDesc()+"</td> \n");
					}else if("SummaryByBox".equalsIgnoreCase(form.getBean().getSummaryType())){
					   h.append("<td>"+s.getBoxNo()+"&nbsp;</td> \n");
					   h.append("<td>"+s.getName()+"</td> \n");
					   h.append("<td>"+s.getQty()+"</td> \n");
					}else if("SummaryByPensItem".equalsIgnoreCase(form.getBean().getSummaryType())){
					   h.append("<td>"+s.getPensItem()+"</td> \n");
					   h.append("<td>"+s.getGroupCode()+"</td> \n");
					   h.append("<td>"+s.getQty()+"</td> \n");
					}
					h.append("</tr>");
				}//for 
				 if("SummaryByBox".equalsIgnoreCase(form.getBean().getSummaryType())){
					  h.append("<td></td> \n");
					  h.append("<td>Total QTY</td> \n");
					  h.append("<td>"+b.getTotalQty()+"</td> \n");
				}else if("SummaryByPensItem".equalsIgnoreCase(form.getBean().getSummaryType())){
					 h.append("<td></td> \n");
					 h.append("<td>Total QTY</td> \n");
					 h.append("<td>"+b.getTotalQty()+"</td> \n");
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
