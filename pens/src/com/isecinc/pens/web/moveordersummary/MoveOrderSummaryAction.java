package com.isecinc.pens.web.moveordersummary;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.NumberToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.MoveOrderSummary;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptCN;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.ReceiptMatch;
import com.isecinc.pens.bean.ReceiptMatchCN;
import com.isecinc.pens.bean.Summary;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ConvertUtils;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.ImportManager;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MMemberProduct;
import com.isecinc.pens.model.MMoveOrder;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptBy;
import com.isecinc.pens.model.MReceiptCN;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MReceiptMatch;
import com.isecinc.pens.model.MReceiptMatchCN;
import com.isecinc.pens.model.MSummary;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.model.MUser;
import com.isecinc.pens.web.interfaces.InterfacesForm;
import com.isecinc.pens.web.sales.OrderForm;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class MoveOrderSummaryAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		MoveOrderSummaryForm summaryForm = (MoveOrderSummaryForm) form;
		try {
			 logger.debug("prepare");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
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
		MoveOrderSummaryForm summaryForm = (MoveOrderSummaryForm) form;
		try {
			logger.debug("prepare 2");
			if("new".equalsIgnoreCase(request.getParameter("action"))){
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
		MoveOrderSummaryForm summaryForm = (MoveOrderSummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			MMoveOrder mDAO = new MMoveOrder();
			MoveOrderSummary m = summaryForm.getSummary();
			List<MoveOrderSummary> summaryList = null;
			
			if(m.getType().equals("DETAIL")){
			   summaryList = mDAO.searchMoveOrderSummaryDetail(m,user);
			}else{
			   summaryList = mDAO.searchMoveOrderSummaryDetail(m,user);
			}
			summaryForm.setResults(summaryList);
			if(summaryList != null && summaryList.size()==0){
				request.setAttribute("Message","ไม่พบข้อมูล");
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
		MoveOrderSummaryForm summaryForm = (MoveOrderSummaryForm) form;
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
	
	
	private StringBuffer genHTML(HttpServletRequest request,MoveOrderSummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		boolean isTotal = false;
		String moveOrderType = "";
		String moveOrderTypeDisp = "";
		int colspan = 0;
		try{
			//Header
			moveOrderType = form.getSummary().getMoveOrderType().equals(MMoveOrder.MOVE_ORDER_REQUISITION)?"ใบเบิกสินค้า":"ใบคืนสินค้า";
			moveOrderTypeDisp = form.getSummary().getMoveOrderType().equals(MMoveOrder.MOVE_ORDER_REQUISITION)?"เบิกสินค้าจาก PD":"คืนสินค้าเข้า PD";
			
			
			isTotal = form.getSummary().getType().equals("TOTAL")?true:false;
			colspan = form.getSummary().getType().equals("TOTAL")?9:11;
			
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>ประเภทรายงาน :"+moveOrderType+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >หน่วยรถ:"+user.getUserName()+" "+user.getName()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >วันที่เรียกข้อมูล:"+form.getSummary().getRequestDateFrom()+"  ถึงวันที่ขาย:"+form.getSummary().getRequestDateTo()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >รูปแบบการพิมพ์ :"+(isTotal?"แสดงผลรวม":"แสดงรายละเอียด")+" </td>\n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >&nbsp; </td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getResults() != null){
			    List<MoveOrderSummary> list = (List<MoveOrderSummary>)form.getResults();
			    
				h.append("<table border='1'> \n");

				h.append("<tr> \n");
				  if(!isTotal){
				    h.append("<td>เลขที่เอกสาร</td> \n");
				    h.append("<td>เลขที่ทำรายการ</td> \n");
				  }
				  h.append("<td>"+moveOrderTypeDisp+"</td> \n");
				  h.append("<td>รหัสสินค้า</td> \n");
				  h.append("<td>หน่วยนับ</td> \n");
				  h.append("<td>จำนวนเต็ม</td> \n");
				  h.append("<td>หน่วยนับเศษ</td> \n");
				  h.append("<td>จำนวนเศษ</td> \n");
				  h.append("<td>จำนวนเงิน</td> \n");
				  h.append("<td>สถานะเอกสาร</td> \n");
				  h.append("<td>สถานะการส่งข้อมูล</td> \n");
				  
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					MoveOrderSummary s = (MoveOrderSummary)list.get(i);
					h.append("<tr> \n");
					if(!isTotal){
					   h.append("<td>"+s.getRequestNumber()+"</td> \n");
					   h.append("<td>"+s.getRequestDate()+"</td> \n");
					}
					  h.append("<td>"+s.getPdCode()+"</td> \n");
					  h.append("<td>"+s.getProductCode()+"</td> \n");
					  h.append("<td>"+s.getUom1()+"</td> \n");
					  h.append("<td>"+s.getQty1()+"</td> \n");
					  h.append("<td>"+s.getUom2()+"</td> \n");
					  h.append("<td>"+s.getQty2()+"</td> \n");
					  h.append("<td>"+s.getTotalAmount()+"</td> \n");
					  h.append("<td>"+s.getStatusLabel()+"</td> \n");
					  h.append("<td>"+s.getExportedLabel()+"</td> \n");
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
		return "view";
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		MoveOrderSummaryForm summaryForm = (MoveOrderSummaryForm) form;
		try {
			 summaryForm.setSummary(new MoveOrderSummary());
			 summaryForm.setResults(null);
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
