package com.isecinc.pens.web.adjuststock;

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
import com.isecinc.pens.bean.AdjustStockSA;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.AdjustStockDAO;
import com.isecinc.pens.dao.AdjustStockSADAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class AdjustStockSAAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		AdjustStockSAForm aForm = (AdjustStockSAForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				AdjustStockSA ad = new AdjustStockSA();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				
				aForm.setAdjustStockSA(ad);
			}else if("back".equals(action)){
				aForm.setAdjustStockSA(aForm.getAdjustStockSACriteria());
				aForm.setResultsSearch(AdjustStockSADAO.searchHead(aForm.getAdjustStockSA()));
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		AdjustStockSAForm aForm = (AdjustStockSAForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setResultsSearch(AdjustStockSADAO.searchHead(aForm.getAdjustStockSA()));
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
		return mapping.findForward("search2");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		AdjustStockSAForm aForm = (AdjustStockSAForm) form;
		try {
			aForm.setResultsSearch(new ArrayList<AdjustStockSA>());
			aForm.setAdjustStockSA(new AdjustStockSA());
			aForm.setVerify(false);
			
			AdjustStockSA ad = new AdjustStockSA();
			//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setAdjustStockSA(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear2");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		AdjustStockSAForm aForm = (AdjustStockSAForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setAdjustStockSACriteria(aForm.getAdjustStockSA());
			
            String documentNo = Utils.isNull(request.getParameter("documentNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
            
			if( !"".equals(documentNo)){
				logger.debug("prepare edit documentNo:"+documentNo);
				AdjustStockSA c = new AdjustStockSA();
				c.setDocumentNo(documentNo);
				AdjustStockSA aS = AdjustStockSADAO.searchDetail(c,"order by seq_no asc");
				
				aForm.setResults(aS.getItems());
				aForm.setAdjustStockSA(aS);
				aForm.setVerify(true);
				
				aForm.setMode(mode);//Mode Edit
				
			}else{
				logger.debug("prepare new documentNo");
				AdjustStockSA ad = new AdjustStockSA();
				ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				
				aForm.setAdjustStockSA(ad);
				aForm.setVerify(true);
				aForm.setMode(mode);//Mode Add new
				
				//add Row blank 1 row
				AdjustStockSA lastAd = new AdjustStockSA();
				lastAd.setSeqNo(0);
			
				List<AdjustStockSA> results = new ArrayList<AdjustStockSA>();
                results.add(createNewAdjustStock(lastAd));
               
				aForm.setResults(results);
			}
			
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AdjustStockSAForm summaryForm = (AdjustStockSAForm) form;
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
		AdjustStockSAForm orderForm = (AdjustStockSAForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			/*AdjustStockSA aS = AdjustStockSADAO.searchDetail(orderForm.getAdjustStockSA(),"order by seq_no asc");
			orderForm.setResults(aS.getItems());
			
			if(Utils.isNull(aS.getStoreName()).equals("")){
				msg = "ไม่สามารถทำรายการได้  ไม่พบข้อมูลร้านค้า";
				orderForm.setVerify(false);
			}else{
				orderForm.setVerify(true);
			}
			orderForm.setAdjustStockSA(aS);
			
			request.setAttribute("Message", msg);*/
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
		AdjustStockSAForm aForm = (AdjustStockSAForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
            //Set Data from screen to List
			List<AdjustStockSA> results = setDataToResults(request,aForm);
			AdjustStockSA h = aForm.getAdjustStockSA();
			h.setItems(results);
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			AdjustStockSADAO.save(h);
			
			conn.commit();
			
			//Search 
			AdjustStockSA aS = AdjustStockSADAO.searchDetail(aForm.getAdjustStockSA(),"order by seq_no asc");
			aForm.setAdjustStockSA(aS);
			aForm.setResults(aS.getItems());
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "search";
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		AdjustStockSAForm aForm = (AdjustStockSAForm) form;
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			AdjustStockSA h = aForm.getAdjustStockSA();
			h.setStatus(AdjustStockSADAO.STATUS_CANCEL);
			h.setStatusDesc(AdjustStockDAO.getStatusDesc(h.getStatus()));
			
			//update
			AdjustStockSADAO.updateStatus(conn,h);
			
			//Search 
			AdjustStockSA aS = AdjustStockSADAO.searchDetail(h,"order by seq_no asc");
			aForm.setAdjustStockSA(aS);
			aForm.setResults(aS.getItems());
			
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
				   conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}

	public ActionForward addRow(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("addRow");
		AdjustStockSAForm aForm = (AdjustStockSAForm) form;
		try {
			List<AdjustStockSA> results = aForm.getResults();
			logger.debug("Result Size:"+(results!=null?results.size():0));
			
			if(results != null && results.size() > 0){
				//add data screen to sessin result
				results = setDataToResults(request,aForm);
				
				AdjustStockSA lastAd = (AdjustStockSA)results.get(results.size()-1);
				
				results.add(createNewAdjustStock(lastAd));
				
				aForm.setResults(results);
			}else{
				//new 
				AdjustStockSA lastAd = new AdjustStockSA();
				lastAd.setSeqNo(0);
				
				results = new ArrayList<AdjustStockSA>();
                results.add(createNewAdjustStock(lastAd));
				
				aForm.setResults(results);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("addRow");
	}
	
	public ActionForward removeRow(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("removeRow");
		AdjustStockSAForm aForm = (AdjustStockSAForm) form;
		List<AdjustStockSA> newResults = new ArrayList<AdjustStockSA>();
		try {
			List<AdjustStockSA> results = setDataToResults(request,aForm);
			//for check 
			String[] linechk = request.getParameterValues("linechk");
			
			int seqNo = 0;
			if(results != null && results.size() > 0){
				for(int i=0;i<results.size();i++){
				   AdjustStockSA a = (AdjustStockSA)results.get(i);
				   boolean e = false;
				   for(int n=0;n<linechk.length;n++){
					   if( Utils.isNull(linechk[n]).equals(String.valueOf(a.getSeqNo()))){
						  e = true;
					   }
					   logger.debug("compare seqNo:["+a.getSeqNo()+"]:["+linechk[n]+"]e["+e+"]");
				   }//for 2
				   
				   logger.debug("result seqNo:"+a.getSeqNo()+":"+e);
				   if(e==false){
					   seqNo++;
					   a.setSeqNo(seqNo);
					   newResults.add(a);
				   }
				 
				}//for 1
				
				aForm.setResults(newResults);
			}//for
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("addRow");
	}
	
	private List<AdjustStockSA> setDataToResults(HttpServletRequest request,AdjustStockSAForm aForm){
		List<AdjustStockSA> results = aForm.getResults();
		logger.debug("Result Size:"+(results!=null?results.size():0));
		//String[] linechk = request.getParameterValues("linechk");
		String[] itemAdjust = request.getParameterValues("itemAdjust");
		String[] groupCode = request.getParameterValues("groupCode");
		String[] itemAdjustUom = request.getParameterValues("itemAdjustUom");
		String[] itemAdjustQty = request.getParameterValues("itemAdjustQty");
		
		//logger.debug("linechk:"+linechk.length);
		//logger.debug("itemAdjust:"+itemAdjust != null?itemAdjust.length:0);
		
		//add value to Results
		if(results != null && results.size() > 0){
			for(int i=0;i<results.size();i++){
				 AdjustStockSA l = (AdjustStockSA)results.get(i);
				 
				 l.setItemAdjust(Utils.isNull(itemAdjust[i]));
				 l.setGroupCode(Utils.isNull(groupCode[i]));
				 l.setItemAdjustUom(Utils.isNull(itemAdjustUom[i]));
				 l.setItemAdjustQty(itemAdjustQty[i]); 
				
				 results.set(i, l);
			}
		}
		return results;
	}
	
	private AdjustStockSA createNewAdjustStock(AdjustStockSA lastAd){
		AdjustStockSA aNew = new AdjustStockSA();
		aNew.setSeqNo(lastAd.getSeqNo()+1);
	
		return aNew;
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		AdjustStockSAForm aForm = (AdjustStockSAForm) form;
		try {
			AdjustStockSA ad = new AdjustStockSA();
			ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setAdjustStockSA(ad);
			
			//add Row blank 1 row
			AdjustStockSA lastAd = new AdjustStockSA();
			lastAd.setSeqNo(0);
		
			List<AdjustStockSA> results = new ArrayList<AdjustStockSA>();
            results.add(createNewAdjustStock(lastAd));
           
			aForm.setResults(results);
			
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
