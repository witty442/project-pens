package com.isecinc.pens.web.summary;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.DiffStockSummary;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.PhysicalSummary;
import com.isecinc.pens.bean.TransactionSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

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
				 summaryForm.setOnhandSummaryResults(null);
				 OnhandSummary oh = new OnhandSummary();
				 summaryForm.setOnhandSummary(oh);
				 summaryForm.setOnhandSummaryResults(null);
				 summaryForm.setOnhandSummaryLotusResults(null);
				 
				 summaryForm.setLotusSummaryResults(null);
				 summaryForm.setBigcSummaryResults(null);
				 summaryForm.setTopsSummaryResults(null);
				 summaryForm.setTransactionSummary(new TransactionSummary());
				 
				 summaryForm.setPhysicalSummaryResults(null);
				 summaryForm.setPhysicalSummary(new PhysicalSummary());
				 
				 summaryForm.setDiffStockSummaryLists(null);
				 summaryForm.setDiffStockSummary(new DiffStockSummary());
				 
				 summaryForm.setOnhandBigCResults(null);
				 
				 summaryForm.setOnhandSummaryMTTResults(null);
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
				 summaryForm.setOnhandSummaryResults(null);
				 OnhandSummary oh = new OnhandSummary();
				 summaryForm.setOnhandSummary(oh);
				 summaryForm.setOnhandSummaryResults(null);
				 summaryForm.setOnhandSummaryLotusResults(null);
				 
				 summaryForm.setLotusSummaryResults(null);
				 summaryForm.setBigcSummaryResults(null);
				 summaryForm.setTopsSummaryResults(null);
				 summaryForm.setTransactionSummary(new TransactionSummary());
				 
				 summaryForm.setPhysicalSummaryResults(null);
				 summaryForm.setPhysicalSummary(new PhysicalSummary());
				 
				 summaryForm.setDiffStockSummaryLists(null);
				 summaryForm.setDiffStockSummary(new DiffStockSummary());
				 
				 summaryForm.setOnhandSummaryLotusPeriodResults(null);
				 summaryForm.setOnhandBigCResults(null);
				 
				 summaryForm.setOnhandSummaryMTTResults(null);
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
		logger.debug("page["+Utils.isNull(request.getParameter("page"))+"]");
		try {
			if("onhand".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				List<OnhandSummary> results = new SummaryDAO().search(summaryForm.getOnhandSummary(),user);
				if (results != null  && results.size() >0) {
					summaryForm.setOnhandSummaryResults(results);
					
					OnhandSummary cc = (OnhandSummary)results.get(0);
					summaryForm.getOnhandSummary().setAsOfDate(cc.getAsOfDate());
					summaryForm.getOnhandSummary().setFileName(cc.getFileName());
					
					//logger.debug("results:"+summaryForm.getOnhandSummaryResults());
					
				} else {
					summaryForm.setOnhandSummaryResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
				
			}else if("onhandLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				List<OnhandSummary> results = new SummaryDAO().searchOnhandLotus(summaryForm.getOnhandSummary(),user);
				if (results != null  && results.size() >0) {
					summaryForm.setOnhandSummaryLotusResults(results);
					
					OnhandSummary cc = (OnhandSummary)results.get(0);
					
					//logger.debug("results:"+summaryForm.getOnhandSummaryLotusResults());
					ImportDAO importDAO = new ImportDAO();
					Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
					if(m != null)
					  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
					
				} else {
					summaryForm.setOnhandSummaryLotusResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
			}else if("bmeTrans".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				List<OnhandSummary> results = new SummaryDAO().searchBmeTrans(summaryForm.getOnhandSummary(),user);
				if (results != null  && results.size() >0) {
					summaryForm.setOnhandSummaryBmeTransResults(results);
					
					//logger.debug("results:"+summaryForm.getOnhandSummaryLotusResults());
					ImportDAO importDAO = new ImportDAO();
					Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
					if(m != null)
					  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
					
				} else {
					summaryForm.setOnhandSummaryBmeTransResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
				
			}else if("onhandMTT".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				//Validate Initial Date
				Date asOfDate = Utils.parse(summaryForm.getOnhandSummary().getSalesDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				Date initDate = new SummaryDAO().searchInitDateMTT(summaryForm.getOnhandSummary().getPensCustCodeFrom());
				
				logger.debug("initDate:"+initDate);
				logger.debug("asOfDate:"+asOfDate);
				
				boolean pass = true;
				if(initDate !=null){
					if(asOfDate.before(initDate)){
						summaryForm.setOnhandSummaryMTTResults(null);
						request.setAttribute("Message", "วันที่ as of ต้องมากกว่าเท่ากับวันที่นับสต๊อกตั้งต้น");
						pass = false;
					}
				}
				if(pass){
					List<OnhandSummary> results = new SummaryDAO().searchOnhandMTT(summaryForm.getOnhandSummary(),initDate,user);
					if (results != null  && results.size() >0) {
						summaryForm.setOnhandSummaryMTTResults(results);
						
						ImportDAO importDAO = new ImportDAO();
						Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
						if(m != null)
						  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
						
					} else {
						summaryForm.setOnhandSummaryMTTResults(null);
						request.setAttribute("Message", "ไม่พบข่อมูล");
					}
				}
			}else if("onhandBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				List<OnhandSummary> results = new SummaryDAO().searchOnhandBigC(summaryForm.getOnhandSummary(),user);
				if (results != null  && results.size() >0) {
					summaryForm.setOnhandBigCResults(results);
					
					OnhandSummary cc = (OnhandSummary)results.get(0);
					
					//logger.debug("results:"+summaryForm.getOnhandBigCResults());
					ImportDAO importDAO = new ImportDAO();
					Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
					if(m != null)
					  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
					
				} else {
					summaryForm.setOnhandSummaryLotusResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
				
			}else if("onhandLotusPeriod".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				List<OnhandSummary> results = new SummaryDAO().searchOnhandLotusPeriod(summaryForm.getOnhandSummary(),user);
				if (results != null  && results.size() >0) {
					summaryForm.setOnhandSummaryLotusPeriodResults(results);
					
					OnhandSummary cc = (OnhandSummary)results.get(0);
					
					//logger.debug("results:"+summaryForm.getOnhandSummaryLotusPeriodResults());
					ImportDAO importDAO = new ImportDAO();
					Master m = importDAO.getStoreName("Store", summaryForm.getOnhandSummary().getPensCustCodeFrom());
					if(m != null)
					  summaryForm.getOnhandSummary().setPensCustNameFrom(m.getPensDesc());
					
				} else {
					summaryForm.setOnhandSummaryLotusResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
			}else if("physical".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				List<PhysicalSummary> results = new SummaryDAO().search(summaryForm.getPhysicalSummary(),user);
				if (results != null && results.size() >0) {
					summaryForm.setPhysicalSummaryResults(results);
					
					PhysicalSummary cc = (PhysicalSummary)results.get(0);
					summaryForm.getOnhandSummary().setFileName(cc.getFileName());
					
					//logger.debug("results:"+summaryForm.getPhysicalSummaryResults());
					
				} else {
					summaryForm.setPhysicalSummaryResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
					
			}else if("lotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				List<TransactionSummary> results = new SummaryDAO().search(summaryForm.getTransactionSummary(),user,"lotus");
				if (results != null  && results.size() >0) {
					summaryForm.setLotusSummaryResults(results);
					//logger.debug("results:"+summaryForm.getLotusSummaryResults());
					
				} else {
					summaryForm.setLotusSummaryResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
			}else if("bigc".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				List<TransactionSummary> results = new SummaryDAO().search(summaryForm.getTransactionSummary(),user,"bigc");
				if (results != null  && results.size() >0) {
					summaryForm.setBigcSummaryResults(results);
					//logger.debug("results:"+summaryForm.getBigcSummaryResults());
					
				} else {
					summaryForm.setBigcSummaryResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}	
			}else if("tops".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				
				List<TransactionSummary> results = new SummaryDAO().search(summaryForm.getTransactionSummary(),user,"tops");
				if (results != null  && results.size() >0) {
					summaryForm.setTopsSummaryResults(results);
					//logger.debug("results:"+summaryForm.getTopsSummaryResults());
					
				} else {
					summaryForm.setTopsSummaryResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}	
			}else if("diff_stock".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				List<DiffStockSummary> results = new SummaryDAO().searchDiffStock(summaryForm.getDiffStockSummary(),user);
				if (results != null  && results.size() >0) {
					summaryForm.setDiffStockSummaryLists(results);
					//logger.debug("results:"+summaryForm.getDiffStockSummaryLists());
					
				} else {
					summaryForm.setDiffStockSummaryLists(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
				
			}else if("sumByGroupCode".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				
				List<TransactionSummary> results = new SummaryDAO().searchByGroupCode(summaryForm.getTransactionSummary(),user,"lotus");
				if (results != null  && results.size() >0) {
					summaryForm.setLotusSummaryResults(results);
					//logger.debug("results:"+summaryForm.getLotusSummaryResults());
					
				} else {
					summaryForm.setLotusSummaryResults(null);
					request.setAttribute("Message", "ไม่พบข่อมูล");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			
			logger.debug("PageAction:"+request.getParameter("page"));
			
			/** Onhand **/
			if("onhand".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="ReportStockonhandBme.xls";
				if(summaryForm.getOnhandSummaryResults() != null && summaryForm.getOnhandSummaryResults().size() > 0){
					htmlTable = genOnhandHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("onhandLotusPeriod".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="ReportBmeStockon-handatLotus(Period for Acc).xls";
			     if(summaryForm.getOnhandSummaryLotusResults() != null && summaryForm.getOnhandSummaryLotusResults().size() > 0){
					htmlTable = genOnhandLotusPeriodHTML(request,summaryForm,user,"lotus");	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("onhandLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock on-hand at Lotus(As of).xls";
			     if(summaryForm.getOnhandSummaryLotusResults() != null && summaryForm.getOnhandSummaryLotusResults().size() > 0){
					htmlTable = genOnhandLotusHTML(Utils.isNull(request.getParameter("page")),request,summaryForm,user,summaryForm.getOnhandSummaryLotusResults());	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("bmeTrans".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Transaction Bme.xls";
			     if(summaryForm.getOnhandSummaryBmeTransResults() != null && summaryForm.getOnhandSummaryBmeTransResults().size() > 0){
					htmlTable = genOnhandLotusHTML(Utils.isNull(request.getParameter("page")),request,summaryForm,user,summaryForm.getOnhandSummaryBmeTransResults());	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("onhandMTT".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				 fileName ="Report Bme Stock on-hand MTT.xls";
			     if(summaryForm.getOnhandSummaryMTTResults() != null && summaryForm.getOnhandSummaryMTTResults().size() > 0){
					htmlTable = genOnhandMTTHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("onhandBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="Report Bme Stock on-hand at BigC(As of).xls";
				if(summaryForm.getOnhandBigCResults() != null && summaryForm.getOnhandBigCResults().size() > 0){
					htmlTable = genOnhandBigCHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("lotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName="ReportSalesDetailBmeLOTUS.xls";
				if(summaryForm.getLotusSummaryResults() != null && summaryForm.getLotusSummaryResults().size() > 0){
					htmlTable = genLotusHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("bigc".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="ReportSalesDetailBmeBigC.xls";
				if(summaryForm.getBigcSummaryResults() != null && summaryForm.getBigcSummaryResults().size() > 0){
					htmlTable = genBigCHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("tops".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName="ReportSalesDetailBmeTops.xls";
				if(summaryForm.getTopsSummaryResults() != null && summaryForm.getTopsSummaryResults().size() > 0){
					htmlTable = genTopsHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("physical".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				if(summaryForm.getPhysicalSummaryResults() != null && summaryForm.getPhysicalSummaryResults().size() > 0){
					htmlTable = genPhysicalHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}else if("diff_stock".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				if(summaryForm.getDiffStockSummaryLists() != null && summaryForm.getDiffStockSummaryLists().size() > 0){
					htmlTable = genDiffStockHTML(request,summaryForm,user);	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}
			
	        logger.debug("fileName:"+fileName);
	        //fileName = Utils.toUnicodeChar(fileName);
	        logger.debug("fileName:"+fileName);
	        		//"data.xls";
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("export");
	}
	
	
	private StringBuffer genOnhandHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9'>รายงานข้อมูล  Stock Onhand B'ME</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >จากรหัสสินค้า:"+form.getOnhandSummary().getItemCodeFrom()+"  ถึงรหัสสินค้า:"+form.getOnhandSummary().getItemCodeTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >File Name:"+form.getOnhandSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getOnhandSummaryResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getOnhandSummaryResults();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>Item</td> \n");
				  h.append("<td>Description</td> \n");
				  h.append("<td>OnHand</td> \n");
				  h.append("<td>ราคาขายส่งก่อน VAT </td> \n");
				  h.append("<td>ราคาขายปลีกก่อน VAT </td> \n");
				  h.append("<td>Pens Item</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>Material Master</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>&nbsp;"+s.getGroup()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getItem()+"</td> \n");
					  h.append("<td>"+s.getItemDesc()+"</td> \n");
					  h.append("<td>"+s.getOnhandQty()+"</td> \n");
					  h.append("<td>"+s.getWholePriceBF()+"</td> \n");
					  h.append("<td>"+s.getRetailPriceBF()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getPensItem()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getBarcode()+"</td> \n");
					  h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genOnhandLotusHTML(String page,HttpServletRequest request,SummaryForm form,User user,List<OnhandSummary> list){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			if(form.getOnhandSummaryBmeTransResults() != null){
			    h.append("<td align='left' colspan='9'>รายงาน  Transaction B'me </td> \n");
			}else{
				h.append("<td align='left' colspan='9'>รายงาน B'me Stock on-hand at Lotus(As Of)</td> \n");
			}
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			if("bmeTrans".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				h.append("<td align='left' colspan='9' >จากวันที่:"+form.getOnhandSummary().getAsOfDateFrom()+"ถึง"+form.getOnhandSummary().getAsOfDateTo() +"</td> \n");
			}else{
				h.append("<td align='left' colspan='9' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			}
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสสาขา</td> \n");
				  h.append("<td>PensItem</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>Sale In Qty</td> \n");
				  h.append("<td>Sale Return Qty </td> \n");
				  h.append("<td>Sales Out Qty </td> \n");
				  h.append("<td>Adjust </td> \n");
				  h.append("<td>Stock short </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				 
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>&nbsp;"+s.getStoreCode()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getPensItem()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td>"+s.getAdjustQty()+"</td> \n");
					  h.append("<td>"+s.getStockShortQty()+"</td> \n");
					  h.append("<td>"+s.getOnhandQty()+"</td> \n");
					  
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genOnhandMTTHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		String a= "@";
		try{
			h.append("<style> \n");
			h.append(" .num { \n");
			h.append("  mso-number-format:General; \n");
			h.append(" } \n");
			h.append(" .text{ \n");
			h.append("   mso-number-format:'"+a+"'; \n");
			h.append(" } \n");
			h.append("</style> \n");
			
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='10'>รายงาน B'me Stock on-hand MTT</td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='10' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='10' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='10' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='10' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getOnhandSummaryMTTResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getOnhandSummaryMTTResults();
			 	    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสร้านค้า(Bme)</td> \n");
				  h.append("<td>CustNo(Oracle)</td> \n");
				  h.append("<td>ชื่อร้านค้า</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>PensItem</td> \n");
				  h.append("<td>Initial Stock</td> \n");
				  h.append("<td>Sale In Qty</td> \n");
				  h.append("<td>Sale Out Qty</td> \n");
				  h.append("<td>Return Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getCustNo()+"</td> \n");
					  h.append("<td>"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td>"+s.getPensItem()+"</td> \n");
					  h.append("<td class='num'>"+s.getInitSaleQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td class='num'>"+s.getOnhandQty()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genOnhandBigCHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
		    h.append("<td align='left' colspan='9'>รายงาน B'me Stock on-hand at BigC(As Of) </td> \n");
			
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >จากวันที่ขาย:"+form.getOnhandSummary().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='9' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getOnhandBigCResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getOnhandBigCResults();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสสาขา</td> \n");
				  h.append("<td>Sub Inv</td> \n");
				  h.append("<td>ชื่อสาขา</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>Pens Item</td> \n");
				  h.append("<td>Trans In Qty</td> \n");
				  h.append("<td>Return Qty </td> \n");
				  h.append("<td>Sales Out Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				 
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>&nbsp;"+s.getStoreCode()+"</td> \n");
					  h.append("<td>"+s.getSubInv()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getPensItem()+"</td> \n");
					  h.append("<td>"+s.getTransInQty()+"</td> \n");
					  h.append("<td>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td>"+s.getOnhandQty()+"</td> \n");
					  
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genOnhandLotusPeriodHTML(HttpServletRequest request,SummaryForm form,User user,String storeType){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			if("lotus".equalsIgnoreCase(storeType)){
			   h.append("<td align='left' colspan='7'>รายงาน B'me Stock on-hand at Lotus(As Of)</td> \n");
			}else{
			  h.append("<td align='left' colspan='7'>รายงาน B'me Stock on-hand at BigC(As Of) </td> \n");
			}
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >จากวันที่ขาย:"+form.getOnhandSummary().getAsOfDateFrom()+" ถึงวันที่ขาย:"+form.getOnhandSummary().getAsOfDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >รหัสร้านค้า:"+form.getOnhandSummary().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >Pens Item From:"+form.getOnhandSummary().getPensItemFrom()+"  Pens Item To:"+form.getOnhandSummary().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >Group:"+form.getOnhandSummary().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getOnhandSummaryLotusResults() != null){
			    List<OnhandSummary> list = (List<OnhandSummary>)form.getOnhandSummaryLotusResults();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสสาขา</td> \n");
				  h.append("<td>ชื่อสาขา</td> \n");
				  h.append("<td>Group</td> \n");
				  h.append("<td>Sale In Qty</td> \n");
				  h.append("<td>Sale Return Qty </td> \n");
				  h.append("<td>Sales Out Qty </td> \n");
				  h.append("<td>Onhand Qty </td> \n");
				 
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					OnhandSummary s = (OnhandSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>&nbsp;"+s.getStoreCode()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getStoreName()+"</td> \n");
					  h.append("<td>"+s.getGroup()+"</td> \n");
					  h.append("<td>"+s.getSaleInQty()+"</td> \n");
					  h.append("<td>"+s.getSaleReturnQty()+"</td> \n");
					  h.append("<td>"+s.getSaleOutQty()+"</td> \n");
					  h.append("<td>"+s.getOnhandQty()+"</td> \n");
					  
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genPhysicalHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7'>รายงานข้อมูลการตรวจนับสต๊อก</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >จาก วันที่นับสต็อก:"+form.getPhysicalSummary().getCountDateFrom()+"  ถึง วันที่นับสต็อก:"+form.getPhysicalSummary().getCountDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >จาก รหัสร้านค้า:"+form.getPhysicalSummary().getPensCustCodeFrom()+"  ถึง รหัสร้านค้า:"+form.getPhysicalSummary().getPensCustCodeTo()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='7' >File Name:"+form.getPhysicalSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getPhysicalSummaryResults() != null){
			    List<PhysicalSummary> list = (List<PhysicalSummary>)form.getPhysicalSummaryResults();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>Item</td> \n");
				  h.append("<td>Barcode</td> \n");
				  h.append("<td>Cust Code</td> \n");
				  h.append("<td>Cust Name</td> \n");
				  h.append("<td>Count Date </td> \n");
				  h.append("<td>File Name </td> \n");
				  h.append("<td>Create Date</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					PhysicalSummary s = (PhysicalSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>&nbsp;"+s.getItem()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getBarcode()+" </td> \n");
					  h.append("<td>"+s.getPensCustCode()+"</td> \n");
					  h.append("<td>"+s.getPensCustName()+"</td> \n");
					  h.append("<td>"+s.getCountDate()+"</td> \n");
					  h.append("<td>"+s.getFileName()+"</td> \n");
					  h.append("<td>"+s.getCreateDate()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genDiffStockHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='8'>รายงานข้อมูล  Different Stock B'ME</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='8' > รหัสร้านค้า:"+form.getDiffStockSummary().getPensCustCodeFrom()+"-"+form.getDiffStockSummary().getPensCustNameFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='8' > As Of Date:"+form.getDiffStockSummary().getAsOfDate()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='8' >"+("true".equalsIgnoreCase(form.getDiffStockSummary().getHaveQty())?"Have Qty only":"")+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getDiffStockSummaryLists() != null){
			    List<DiffStockSummary> list = (List<DiffStockSummary>)form.getDiffStockSummaryLists();
			    
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<td>รหัสร้านค้า</td> \n");
				  h.append("<td>Item</td> \n");
				  h.append("<td>Description</td> \n");
				  h.append("<td>Order Consign</td> \n");
				  h.append("<td>Order From Lotus</td> \n");
				  h.append("<td>Data From Physical</td> \n");
				  h.append("<td>Adjust</td> \n");
				  h.append("<td>Diff</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					DiffStockSummary s = (DiffStockSummary)list.get(i);
					h.append("<tr> \n");
					  h.append("<td>"+s.getCustCode()+"</td> \n");
					  h.append("<td>&nbsp;"+s.getItem()+"</td> \n");
					  h.append("<td>"+s.getDescription()+"</td> \n");
					  h.append("<td>"+s.getOrderConsign()+"</td> \n");
					  h.append("<td>"+s.getOrderFromLotus()+"</td> \n");
					  h.append("<td>"+s.getDataFromPhysical()+"</td> \n");
					  h.append("<td>"+s.getAdjust()+"</td> \n");
					  h.append("<td>"+s.getDiff()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genLotusHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='33'>รายงานข้อมูลรายละเอียดขาย B'ME จาก LOTUS</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='33' >จากวันที่ขาย:"+form.getTransactionSummary().getSalesDateFrom()+"  ถึง วันที่ขาย:"+form.getTransactionSummary().getSalesDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='33' >จากรหัสร้านค้า:"+form.getTransactionSummary().getPensCustCodeFrom()+"  ถึงรหัสร้านค้า:"+form.getTransactionSummary().getPensCustCodeTo()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='33' >File Name:"+form.getTransactionSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getLotusSummaryResults() != null){
			    List<TransactionSummary> list = (List<TransactionSummary>)form.getLotusSummaryResults();
			    
				h.append("<table border='1'> \n");
				h.append("<thead>\n");
				h.append("<tr> \n");
					h.append("<th>Sales Date</th>\n");
					h.append("<th>Pens Cust Code</th>\n");
					h.append("<th>Pens Cust Desc</th> \n");
					h.append("<th>Store No</th> \n");
					h.append("<th>Store Name </th> \n");
					h.append("<th>STYLE NO</th>  \n");
					h.append("<th>DESCRIPTION</th> \n");
					h.append("<th>QTY</th> \n");
					h.append("<th>Pens Group</th>\n");
					h.append("<th>Pens Group Type</th> \n");
					h.append("<th>Sales Year</th>\n");
					h.append("<th>Sales Month</th>  \n");
					h.append("<th>file Name</th>\n");
					h.append("<th>Vendor</th> \n");
					h.append("<th>Name</th>\n");
					h.append("<th>AP Type</th> \n");
					h.append("<th>LEASE VENDOR TYPE</th>\n");
					h.append("<th>COL</th> \n");
					h.append("<th>Size Type</th> \n");
					h.append("<th>SIZE</th> \n");
					h.append("<th>GROSS SALES</th> \n");
					h.append("<th>RETURN AMT</th>\n");
					h.append("<th>NET SALES INCL VAT</th> \n");
					h.append("<th>VAT AMT</th> \n");
					h.append("<th>NET SALES EXC VAT</th> \n");
					h.append("<th>GP AMOUNT</th>  \n");
					h.append("<th>VAT ON GP AMOUNT</th> \n");
					h.append("<th>GP AMOUNT INCL VAT</th> \n");
					h.append("<th>AP AMOUNT</th> \n");
					h.append("<th>TOTAL VAT AMT</th> \n");
					h.append("<th>AP AMOUNT INCL VAT</th> \n");
					h.append("<th>Create date</th>  \n");
					h.append("<th>Create by</th>\n");
				h.append("</tr> \n");
				h.append("</thead> \n");
				
				for(int i=0;i<list.size();i++){
					TransactionSummary s = (TransactionSummary)list.get(i);
					h.append("<tr> \n");
						h.append("<td>"+Utils.isNull(s.getSalesDate())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustCode())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustDesc())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getStoreNo())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getStoreName())+" </td> \n");
						h.append("<td>"+Utils.isNull(s.getStyleNo())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getDescription())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getQty())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getPensGroup())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensGroupType())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getSalesYear())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getSalesMonth())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getFileName())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getVendor())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getName())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getApType())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getLeaseVendorType())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getCol())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getSizeType())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getSizes())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGrossSales())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getReturnAmt())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getNetSalesInclVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getVatAmt())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getNetSalesExcVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGpAmount())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getVatOnGpAmount())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGpAmountInclVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getApAmount())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getTotalVatAmt())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getApAmountInclVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getCreateDate())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getCreateUser())+"</td>\n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genTopsHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='31'>รายงานข้อมูลรายละเอียดขาย B'ME จาก  Tops</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='31' >จากวันที่ขาย:"+form.getTransactionSummary().getSalesDateFrom()+"  ถึง วันที่ขาย:"+form.getTransactionSummary().getSalesDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='31' >รหัสร้านค้า:"+form.getTransactionSummary().getPensCustCodeFrom());
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='31' >File Name:"+form.getTransactionSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getTopsSummaryResults() != null){
			    List<TransactionSummary> list = (List<TransactionSummary>)form.getTopsSummaryResults();
			    
				h.append("<table border='1'> \n");
				h.append("<thead>\n");
				h.append("<tr> \n");
					h.append("<th>Sales Date</th>\n");
					h.append("<th>Pens Cust Code</th>\n");
					h.append("<th>Pens Cust Desc</th> \n");
					h.append("<th>Pens Group</th>\n");
					h.append("<th>Pens Group Type</th> \n");
					h.append("<th>Pens Item</th> \n");
					h.append("<th>QTY</th> \n");
					h.append("<th>Item</th> \n");
					h.append("<th>Item Desc</th> \n");
					h.append("<th>Branch Name</th> \n");
					h.append("<th>Group No</th> \n");
					h.append("<th>Group Name</th> \n");
					h.append("<th>DEPT</th> \n");
					h.append("<th>Dept Name</th> \n");
					h.append("<th>Unit Cost</th> \n");
					h.append("<th>Retail Price</th> \n");
					h.append("<th>GP PERCENT</th> \n");
					h.append("<th>NET SALES INCL VA </th> \n");
					h.append("<th>NET SALES EXC VAT </th> \n");
					h.append("<th>GP AMOUNT </th> \n");
					h.append("<th>GROSS SALES </th> \n");
					h.append("<th>Discount </th> \n");
					h.append("<th>CUS RETURN </th> \n");
					h.append("<th>DISCOUNT CUS RETURN </th> \n");
					h.append("<th>NET CUS RETURN </th> \n");
					h.append("<th>COGS </th> \n");
					h.append("<th>Sales Year</th>\n");
					h.append("<th>Sales Month</th>  \n");
					h.append("<th>file Name</th>\n");
					h.append("<th>Create date</th>  \n");
					h.append("<th>Create by</th>\n");
				
				h.append("</tr> \n");
				h.append("</thead> \n");
				
				for(int i=0;i<list.size();i++){
					TransactionSummary s = (TransactionSummary)list.get(i);
					h.append("<tr> \n");
						h.append("<td>"+Utils.isNull(s.getSalesDate())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustCode())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustDesc())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getPensGroup())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensGroupType())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getPensItem())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getQty())+"</td> \n");
						
						h.append("<td>"+Utils.isNull(s.getItem())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getItemDesc())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getBranchName())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGroupNo())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGroupName())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getDept())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getDeptName())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getUnitCost())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getRetailPrice())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGpPercent())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getNetSalesInclVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getNetSalesExcVat())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGpAmount())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getGrossSales())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getDiscount())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getCusReturn())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getDiscountCusReturn())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getNetCusReturn())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getCogs())+"</td> \n");
						
						h.append("<td>"+Utils.isNull(s.getSalesYear())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getSalesMonth())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getFileName())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getCreateDate())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getCreateUser())+"</td>\n");
					h.append("</tr>");
				}
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genBigCHTML(HttpServletRequest request,SummaryForm form,User user){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='33'>รายงานข้อมูลรายละเอียดขาย B'ME จาก BIGC</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='33' >จากวันที่ขาย:"+form.getTransactionSummary().getSalesDateFrom()+"  ถึง วันที่ขาย:"+form.getTransactionSummary().getSalesDateTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='33' >จากรหัสร้านค้า:"+form.getTransactionSummary().getPensCustCodeFrom()+"  ถึงรหัสร้านค้า:"+form.getTransactionSummary().getPensCustCodeTo()+"</td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='33' >File Name:"+form.getTransactionSummary().getFileName()+"</td>\n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(form.getBigcSummaryResults() != null){
			    List<TransactionSummary> list = (List<TransactionSummary>)form.getBigcSummaryResults();
			    
				h.append("<table border='1'> \n");
				h.append("<thead>\n");
				h.append("<tr> \n");
					h.append("<th>Sales Date</th>\n");
					h.append("<th>Pens Cust Code</th>\n");
					h.append("<th>Pens Cust Desc</th> \n");
					h.append("<th>Store No</th> \n");
					h.append("<th>Store Name </th> \n");
					h.append("<th>STYLE NO</th>  \n");
					h.append("<th>DESCRIPTION</th> \n");
					h.append("<th>QTY</th> \n");
					h.append("<th>WHOLE PRICE BF</th> \n");
					h.append("<th>Retail PRICE BF</th> \n");
					h.append("<th>TOTAL WHOLE PRICE BF</th> \n");
					h.append("<th>Pens Group</th>\n");
					h.append("<th>Pens Group Type</th> \n");
					h.append("<th>Sales Year</th>\n");
					h.append("<th>Sales Month</th>  \n");
					h.append("<th>file Name</th>\n");
					h.append("<th>Vendor</th> \n");
					h.append("<th>Name</th>\n");
					
					h.append("<th>GP PERCENT</th>  \n");
					h.append("<th>Create date</th>  \n");
					h.append("<th>Create by</th>\n");
				h.append("</tr> \n");
				h.append("</thead> \n");
				
				for(int i=0;i<list.size();i++){
					TransactionSummary s = (TransactionSummary)list.get(i);
					h.append("<tr> \n");
						h.append("<td>"+Utils.isNull(s.getSalesDate())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustCode())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensCustDesc())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getStoreNo())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getStoreName())+" </td> \n");
						h.append("<td>"+Utils.isNull(s.getStyleNo())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getDescription())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getQty())+"</td> \n");
						h.append("<td>&nbsp;"+Utils.isNull(s.getWholePriceBF())+"</td> \n");
						h.append("<td>&nbsp;"+Utils.isNull(s.getRetailPriceBF())+"</td> \n");
						h.append("<td>&nbsp;"+Utils.isNull(s.getTotalWholePriceBF())+"</td> \n");

						h.append("<td>"+Utils.isNull(s.getPensGroup())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getPensGroupType())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getSalesYear())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getSalesMonth())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getFileName())+"</td>\n");
						h.append("<td>"+Utils.isNull(s.getVendor())+"</td> \n");
						h.append("<td>"+Utils.isNull(s.getName())+"</td>\n");
					
						h.append("<td>"+Utils.isNull(s.getGpPercent())+"</td> \n");
						
						h.append("<td>"+Utils.isNull(s.getCreateDate())+"</td>  \n");
						h.append("<td>"+Utils.isNull(s.getCreateUser())+"</td>\n");
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
		//Connection conn = null;
		//SummaryForm summaryForm = (SummaryForm) form;
		try {
			 
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
			 request.getSession().setAttribute("results", null);
			 
			 summaryForm.setOnhandSummaryResults(null);
			 OnhandSummary oh = new OnhandSummary();
			 summaryForm.setOnhandSummary(oh);
			 summaryForm.setOnhandSummaryResults(null);
			 summaryForm.setOnhandSummaryLotusResults(null);
			 
			 summaryForm.setLotusSummaryResults(null);
			 summaryForm.setBigcSummaryResults(null);
			 summaryForm.setTopsSummaryResults(null);
			 summaryForm.setTransactionSummary(new TransactionSummary());
			 
			 summaryForm.setPhysicalSummaryResults(null);
			 summaryForm.setPhysicalSummary(new PhysicalSummary());
			 
			 summaryForm.setDiffStockSummaryLists(null);
			 summaryForm.setDiffStockSummary(new DiffStockSummary());
			 
			 summaryForm.setOnhandSummaryLotusPeriodResults(null);
			 
			 summaryForm.setOnhandBigCResults(null);
			 	
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
