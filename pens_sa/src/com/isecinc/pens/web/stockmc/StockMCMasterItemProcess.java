package com.isecinc.pens.web.stockmc;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * StockMCMasterItemProcess 
 * 
 * @author WITTY
 * 
 */
public class StockMCMasterItemProcess  extends I_Action{

	
	public static int pageSize = StockMCAction.pageSize;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="";
		try {
			String action = Utils.isNull(request.getParameter("action"));  

			forward = "searchMasterItem";
			if("new".equals(action)){
				//clear session
				request.getSession().removeAttribute("stock_mc_codes");
				
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResults(null);
				//prepare bean
				StockMCBean bean = new StockMCBean();
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
				
			}else if("back".equals(action)){
				//clear session 
				aForm.setResults(null);
				//prepare bean
				StockMCBean bean = new StockMCBean();
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
			}
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward(forward);
	}
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		StockMCForm aForm = (StockMCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(StockMCMasterItemDAO.searchStockMCMasterItemListTotalRec(conn,aForm.getBean()));
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
			   
				List<StockMCBean> items = StockMCMasterItemDAO.searchStockMCMasterItemList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResults(items);
				
				if(items.size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				   aForm.setResults(null);
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
			    List<StockMCBean> items = StockMCMasterItemDAO.searchStockMCMasterItemList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResults(items);
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("searchMasterItem");
	}
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = aForm.getPageName();
		
		return "detail";
	}

	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("viewDetail : ");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		StockMCMasterItemDAO dao = new StockMCMasterItemDAO();
		try {
			//save criteria
			aForm.setBeanCriteria(aForm.getBean());
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			String customerCode = Utils.isNull(request.getParameter("customerCode"));
			String barcode = Utils.isNull(request.getParameter("barcode"));
			String action = Utils.isNull(request.getParameter("action"));
			
			logger.debug("action:"+action);
			logger.debug("customerCode:"+customerCode);
			logger.debug("barcode:"+barcode);
			
			if("add".equalsIgnoreCase(action)){
				StockMCBean bean = new StockMCBean();
				aForm.setBean(bean);
				aForm.setMode("add");
			}else if("edit".equalsIgnoreCase(action)){
				StockMCBean bean = new StockMCBean();
				bean.setCustomerCode(customerCode);
				bean.setBarcode(barcode);
				bean = dao.searchStockMCMasterItem(conn, bean);
				if(bean != null){
					bean.setCanEdit(true);
					aForm.setBean(bean);
					aForm.setResults(bean.getItems());
				}
				aForm.setMode("edit");
			}else if("view".equalsIgnoreCase(action)){
				StockMCBean bean = new StockMCBean();
				bean.setCustomerCode(customerCode);
				bean.setBarcode(barcode);
				bean = dao.searchStockMCMasterItem(conn, bean);
				if(bean != null){
					aForm.setBean(bean);
				}
				aForm.setMode("view");
			}
	
			// save token
			saveToken(request);			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("detailMasterItem");
	}
	public ActionForward loadItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("loadItem : ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = aForm.getPageName();
		Connection conn = null;
		try {
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			 aForm.setMode("edit");
			 aForm.setResults(StockMCDAO.getProductMCItemList(conn,aForm.getBean().getCustomerCode(),"",0));
			
			// save token
			saveToken(request);			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward clearForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("clearForm  ");
		StockMCForm aForm = (StockMCForm) form;
		try {
			StockMCBean bean = new StockMCBean();
			bean.setStockDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			bean.setCanEdit(true);
			aForm.setBean(bean);
			aForm.setResults(null);
			aForm.setMode("add");
				
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
		}
		return mapping.findForward("detail");
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockMCForm stockForm = (StockMCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		StockMCMasterItemDAO dao = new StockMCMasterItemDAO();
		try {
			logger.debug("save-->");

			// check Token
			if (!isTokenValid(request)) {
				logger.debug("Token invalid");
				//stockForm.getResults().clear();
				return "new";
			}
			
			StockMCBean m = stockForm.getBean();
			m.setCreateUser(user.getUserName());
			m.setUpdateUser(user.getUserName());
		
			//Save to DB
			m = dao.save(m);
			
			//search 
			m = dao.searchStockMCMasterItem(m,true);
			m.setCanEdit(true);
			stockForm.setBean(m);
	
			request.setAttribute("Message","บันทึกข้อมูลเรียบร้อย");
		
			// save token
			saveToken(request);
		} catch (Exception e) {
			e.printStackTrace();
			return "new";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "detailMasterItem";
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		StockMCForm aForm = (StockMCForm) form;
		StringBuffer resultTable = null;
		String pageName = aForm.getPageName();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			//String idSelected =Utils.isNull(request.getSession().getAttribute("stock_mc_codes"));
			
			List<StockMCBean> items = StockMCMasterItemDAO.searchStockMCMasterItemList(conn, aForm.getBean(), true, 1, pageSize);
		    if(items!= null && items.size() >0){
		    	resultTable = StockMCExport.genExportStockMCMasterItemReport(items);
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(resultTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
		   }else{
			  request.setAttribute("Message","ไม่พบข้อมูล");
		   }
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockMCForm orderForm = (StockMCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{}
		return "search";
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
