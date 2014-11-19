package com.isecinc.pens.web.pick;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Onhand;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.OnhandProcessDAO;
import com.isecinc.pens.dao.ReqPickStockDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ReqPickStockAction extends I_Action {

	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("resultsView", null);
				request.getSession().setAttribute("groupCodeMap", null);
				request.getSession().setAttribute("custGroupList", null);
				request.getSession().setAttribute("itemsBarcodeErrorMap", null);
				request.getSession().setAttribute("groupCodeErrorMap", null);
				
				aForm.setResultsSearch(null);
				ReqPickStock ad = new ReqPickStock();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				
				aForm.setBean(ad);
				
				// Balance Onhand
				Date startDate = new Date();
				//OnhandDAO.processBanlanceOnhandFromBarcode(user.getUserName());
                logger.debug("processBanlanceOnhandFromBarcode>>Total Time:"+(new Date().getTime()-startDate.getTime()));
                
			}else if("back".equals(action)){
				aForm.setBean(aForm.getBeanCriteria());
				aForm.setResultsSearch(ReqPickStockDAO.searchHead(aForm.getBean()));
			}
		} catch (Exception e) {
			request.setAttribute("Message",e.getMessage());
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
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setResultsSearch(ReqPickStockDAO.searchHead(aForm.getBean()));
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
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		try {
			aForm.setResultsSearch(null);
			ReqPickStock ad = new ReqPickStock();
			aForm.setBean(ad);
			
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
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
			conn = DBConnection.getInstance().getConnection();
			
			String process = Utils.isNull(request.getParameter("process"));
            String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
            String issueReqStatus = Utils.isNull(request.getParameter("issueReqStatus"));
            String mode = Utils.isNull(request.getParameter("mode"));
            
            if( !"save".equals(mode)){
            	request.getSession().setAttribute("results", null);
            	request.getSession().setAttribute("resultsView", null);
				request.getSession().setAttribute("groupCodeMap", null);
				request.getSession().setAttribute("itemsBarcodeErrorMap", null);
				request.getSession().setAttribute("groupCodeErrorMap", null);
				
				ReqPickStock p = new ReqPickStock();
				p.setIssueReqNo(issueReqNo);
				p.setNewSearch(true);//new search
				p.setNewReq(false);
				p.setStatus(issueReqStatus);
				p.setModeConfirm(true);
				p.setModeEdit(false);
				
				//Get Item and set data to session dataGroupCodeMapAll
				p = searchBypageCaseView(conn, p, request);
				aForm.setBean(p);
				
            }else{
				if( !"".equals(issueReqNo)){
					logger.debug("prepare edit issueReqNo:"+issueReqNo);
					request.getSession().setAttribute("results", null);
					request.getSession().setAttribute("resultsView", null);
					request.getSession().setAttribute("groupCodeMap", null);
					request.getSession().setAttribute("itemsBarcodeErrorMap", null);
					request.getSession().setAttribute("groupCodeErrorMap", null);
					
					ReqPickStock p = new ReqPickStock();
					p.setIssueReqNo(issueReqNo);
					p.setNewSearch(true);//new search
					p.setNewReq(false);
					p.setStatus(issueReqStatus);
					
					if("confirm".equalsIgnoreCase(process)){
						p.setModeConfirm(true);
						p.setModeEdit(false);
					}else{
						p.setModeConfirm(false);
						p.setModeEdit(true);
					}
					
					//Get Item and set data to session dataGroupCodeMapAll
					p = searchBypage(conn, p, request);
					aForm.setBean(p);
					
				}else{
					logger.debug("prepare new issueReqNo");
					request.getSession().setAttribute("results", null);
					request.getSession().setAttribute("resultsView", null);
					request.getSession().setAttribute("groupCodeMap", null);
					request.getSession().setAttribute("itemsBarcodeErrorMap", null);
					request.getSession().setAttribute("groupCodeErrorMap", null);
					
					ReqPickStock p = new ReqPickStock();
					p.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					p.setCanEdit(true);
					
					p.setNewReq(true);
					p.setModeConfirm(false);
					p.setModeEdit(true);
					p.setNewSearch(true);//new search
	
					//search by page 
	                p = searchBypage(conn, p, request);
					aForm.setBean(p);
				}
            }
		} catch (Exception e) {
			request.setAttribute("Message", "error:"+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}
	
	private ReqPickStock searchBypage(Connection conn,ReqPickStock p, HttpServletRequest request) {
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		int totalQtyAll = 0;
		int totalCurPageQty = 0;
		int totalQtyNotInCurPage = 0;
		boolean newSearch = p.isNewSearch();
		List<ReqPickStock> results = new ArrayList<ReqPickStock>();

		try{
			Map<String,ReqPickStock> groupCodeMap = new HashMap<String, ReqPickStock>();
			if(request.getSession().getAttribute("groupCodeMap") != null){
				groupCodeMap = (Map) request.getSession().getAttribute("groupCodeMap") ;
			}
			
			Map<String,ReqPickStock> groupCodeErrorMap = null;
			if(request.getSession().getAttribute("groupCodeErrorMap") != null){
				groupCodeErrorMap = (Map) request.getSession().getAttribute("groupCodeErrorMap") ;
			}
			
			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			logger.debug("newReq["+p.isNewReq()+"]");
			
			if(p.isNewSearch()){
				request.getSession().setAttribute("results", null);
				
				//Case newsearch Recalc page
				pageNumber = 1;
				
				//Display data cannot to save
				 if( Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)
					|| Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_POST)
					|| Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_CANCEL)
					|| p.isModeConfirm()){
					 
					 totalRow = ReqPickStockDAO.getTotalRowInStockIssueItemCaseNoEdit(conn, p);
				 }else{ 
					 totalRow = ReqPickStockDAO.getTotalRowInStockFinishGroupByGroupCode(conn,p);
				 }
				 
				totalPage = Utils.calcTotalPage(totalRow,PickConstants.REQ_PICK_PAGE_SIZE);
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);

			}else{
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}

			logger.debug("*** pageNumber["+pageNumber+"]********");
			
            //** Search Data and Display **/
			p = ReqPickStockDAO.searchReqPickStock(conn,p,false);//head only
			p.setNewSearch(newSearch);
			
			ReqPickStock pAllItem = null;
			
			if(  Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)
				 || Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_POST)
				 || Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_CANCEL)
				 || p.isModeConfirm()){
				  
				// pAllItem = ReqPickStockDAO.getGroupCodeInStockListCaseNoEdit(conn, p,pageNumber,groupCodeMap,false);//
				
			}else{
				//No refresh data get data display from session :case display error
				if(p.isNoSearch()){
				   results = (List)request.getSession().getAttribute("results");
				  
				   //Mapping to groupCodeErrorMap to show in Page
					if(groupCodeErrorMap !=null){
						for(int r=0;r<results.size();r++){
							ReqPickStock groupBean = results.get(r);
							if(groupCodeErrorMap.get(groupBean.getGroupCode()) !=null){
								groupBean.setLineItemStyle("lineError");
								Onhand groupCodeOnhand = OnhandProcessDAO.getItemInStockByGroupCode(conn,groupBean);
								String newOnhandQtyGroupCode = groupCodeOnhand!=null?groupCodeOnhand.getOnhandQty():"0";
								groupBean.setOnhandQty(newOnhandQtyGroupCode);
								//set display on screen
								results.set(r, groupBean);
								
							}
						}//for
					}
				}else{
				   pAllItem = ReqPickStockDAO.getGroupCodeInStockList(conn, p,pageNumber,groupCodeMap,groupCodeErrorMap);
				   results = pAllItem.getItems();//display normal
				}

		    }

			 if( Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)
						|| Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_POST)
						|| Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_CANCEL)
						|| p.isModeConfirm()){

					totalQtyAll =  ReqPickStockDAO.getTotalQtyInStockIssueItem(conn,p);	
			 }else{
				 if(p.isNewSearch()){
					//get Total Qty From DB
					totalQtyAll =  ReqPickStockDAO.getTotalQtyInStockIssueItem(conn,p);
				}else{
					//get totalQtyALL From Session groupCodeMap
					totalQtyAll = getTotalQtyFromGroupCodeMap(request); 
					logger.debug("totalQtyAll:"+totalQtyAll);
				} 
			 }
			
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
			
			logger.debug("totalQtyAll["+totalQtyAll+"]");
			
			p.setTotalQty(totalQtyAll);
			p.setTotalQtyNotInCurPage(totalQtyNotInCurPage);
			
			//add to session for save
			request.getSession().setAttribute("groupCodeMap", pAllItem.getGroupCodeMap());
			
			//set after first run = false
			p.setNewSearch(false);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return p;
	}

	private ReqPickStock searchBypageCaseView(Connection conn,ReqPickStock p, HttpServletRequest request) {
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		int totalQtyAll = 0;
		int totalQtyNotInCurPage = 0;
		boolean newSearch = p.isNewSearch();
		List<ReqPickStock> results = new ArrayList<ReqPickStock>();
		try{

			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			logger.debug("newReq["+p.isNewReq()+"]");
			
			if(p.isNewSearch()){
				request.getSession().setAttribute("resultsView", null);
				
				//Case newsearch Recalc page
				pageNumber = 1;

				totalRow = ReqPickStockDAO.getTotalRowInStockIssueItemCaseNoEdit(conn, p);
				totalQtyAll =  ReqPickStockDAO.getTotalQtyInStockIssueItem(conn,p);	
				
				totalPage = (totalRow/ PickConstants.REQ_PICK_PAGE_SIZE)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);

			}else{
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}

			logger.debug("*** pageNumber["+pageNumber+"]********");
			
            //** Search Data and Display **/
			p = ReqPickStockDAO.searchReqPickStock(conn,p,false);//head only
			p.setNewSearch(newSearch);
			
			ReqPickStock  pAllItem = ReqPickStockDAO.getStockIssueItemCaseNoEdit(conn, p,pageNumber,false);//
			results = pAllItem.getItems();
			
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("resultsView", results);
			} else {
				request.getSession().setAttribute("resultsView", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
			
			logger.debug("totalQtyAll["+totalQtyAll+"]");
			
			p.setTotalQty(totalQtyAll);
			p.setTotalQtyNotInCurPage(totalQtyNotInCurPage);
				
			//set after first run = false
			p.setNewSearch(false);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return p;
	}
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ReqPickStockForm f = (ReqPickStockForm) form;
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
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			//save data Prev Page to Session GroupCodeMap
			setDataToSessionGroupCodeMap(request, aForm);
			
			//set Disp nextPage
			ReqPickStock p = searchBypage(conn, aForm.getBean(), request);
			
			//setDisp result
			setDispResults(request, aForm);
			
			//logger.debug("1totalQty:"+p.getTotalQty());
			
			//set to form
			aForm.setBean(p);
			
			logger.debug("2totalQty:"+aForm.getBean().getTotalQty());
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn = null;
			}
		}
		return "search";
	}
	
	protected String searchCaseView(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			//save data Prev Page to Session GroupCodeMap
			setDataToSessionGroupCodeMap(request, aForm);
			
			//set Disp nextPage
			ReqPickStock p = searchBypageCaseView(conn, aForm.getBean(), request);
			
			//setDisp result
			setDispResults(request, aForm);
			
			//logger.debug("1totalQty:"+p.getTotalQty());
			
			//set to form
			aForm.setBean(p);
			
			logger.debug("2totalQty:"+aForm.getBean().getTotalQty());
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn = null;
			}
		}
		return "search";
	}

	private Map<String, ReqPickStock> setDataToSessionGroupCodeMap(HttpServletRequest request,ReqPickStockForm aForm){
		logger.debug("*** setDataToSessionGroupCodeMap ***");
	    User user = (User) request.getSession().getAttribute("user");
	    //Data Disp Per Page
	    List<ReqPickStock> results = (List<ReqPickStock>)request.getSession().getAttribute("results"); //data per page
		
	    //dataSaveMap All
	    Map<String, ReqPickStock> groupCodeMap = new HashMap<String, ReqPickStock>();
	    if(request.getSession().getAttribute("groupCodeMap")!= null ){
	    	groupCodeMap = (Map)request.getSession().getAttribute("groupCodeMap");
	    }
	    
		logger.debug("groupCodeMap Size:"+(groupCodeMap!=null?groupCodeMap.size():0));
		logger.debug("results Size:"+(results!=null?results.size():0));
		
		String[] qty = request.getParameterValues("qty");
		
		//add value to Results
		if(results != null && results.size() > 0 && qty != null){
			for(int i=0;i<results.size();i++){
				 ReqPickStock l = (ReqPickStock)results.get(i);
				 
				 logger.debug("GroupCode["+l.getGroupCode()+"]qty["+qty[i]+"]");
				
				 l.setQty(qty[i]);
				 l.setUpdateUser(user.getUserName());
				 l.setCreateUser(user.getUserName());
				 
				 //set data to list display
				 results.set(i, l);
				 
				//Key Map set value to Save
				 String key = l.getGroupCode();
				 ReqPickStock oldItem = groupCodeMap.get(key);
				 oldItem.setOnhandQty(l.getOnhandQty());
				 oldItem.setQty(qty[i]);
				 oldItem.setUpdateUser(user.getUserName());
				 oldItem.setCreateUser(user.getUserName());
				 
				 //check pens_items is old record
				 logger.debug("set groupCode qty:"+key+",qty:"+oldItem.getQty());
				 groupCodeMap.put(key, oldItem);
				 
			}//for
		}//if

		//data to display
		request.getSession().setAttribute("results",results);
		request.getSession().setAttribute("groupCodeMap",groupCodeMap);
		
		return groupCodeMap;
	}

	private Map<String, ReqPickStock> setDispResults(HttpServletRequest request,ReqPickStockForm aForm){
	    //Data Disp Per Page
	    List<ReqPickStock> results = (List<ReqPickStock>)request.getSession().getAttribute("results"); //data per page
		
	    //dataSaveMap All
	    Map<String, ReqPickStock> groupCodeMap = new HashMap<String, ReqPickStock>();
	    if(request.getSession().getAttribute("groupCodeMap")!= null ){
	    	groupCodeMap = (Map)request.getSession().getAttribute("groupCodeMap");
	    }
	    
		logger.debug("groupCodeMap Size:"+(groupCodeMap!=null?groupCodeMap.size():0));
		logger.debug("results Size:"+(results!=null?results.size():0));

		String[] qty = request.getParameterValues("qty");
		
		//add value to Results
		if(results != null && results.size() > 0){
			for(int i=0;i<results.size();i++){
				ReqPickStock l = (ReqPickStock)results.get(i);
				//Key Map  
				 String key = l.getGroupCode();
				 ReqPickStock groupCodeBean = groupCodeMap.get(key);
				 logger.debug("groupCodeMap.get("+key+"):"+groupCodeBean+",qty:"+groupCodeBean.getQty());
				 if(groupCodeBean != null){
				     l.setQty(groupCodeBean.getQty());
				 }
				 //set data to list disp
				 results.set(i, l);
			}//for
		}//if
	
		//data to display
		request.getSession().setAttribute("results",results);
		
		return groupCodeMap;
	}
	
	private Map<String, ReqPickStock> convertGroupCodeMapToDataSaveMapALL(HttpServletRequest request,ReqPickStockForm aForm){
	   logger.debug("*** convertGroupCodeMapToDataSaveMapALL ***");
		//dataSaveMap All
		Map<String, ReqPickStock> dataSaveMapAll = new HashMap<String, ReqPickStock>();
	    Map<String, ReqPickStock> groupCodeMap = new HashMap<String, ReqPickStock>();
	    Map<String, ReqPickStock> itemsBarcodeMap = new HashMap<String, ReqPickStock>();
	    
	    try{
		    if(request.getSession().getAttribute("groupCodeMap")!= null ){
		    	groupCodeMap = (Map)request.getSession().getAttribute("groupCodeMap");
		    	
		    	Iterator its =  groupCodeMap.keySet().iterator();
				while(its.hasNext()){
					 String key = (String)its.next();
					 ReqPickStock groupCodeBean = (ReqPickStock)groupCodeMap.get(key);
					 logger.debug("*** GroupCode["+groupCodeBean.getGroupCode()+"]onhandQty["+Utils.convertStrToInt(groupCodeBean.getQty())+"]");
					
					 if( Utils.convertStrToInt(groupCodeBean.getQty()) > 0){
						 itemsBarcodeMap = groupCodeBean.getItemsBarcodeMap();
						 Iterator itItems =  itemsBarcodeMap.keySet().iterator();
						 while(itItems.hasNext()){
							 String keyBarcode = (String)itItems.next();
							 ReqPickStock itemBean = (ReqPickStock)itemsBarcodeMap.get(keyBarcode);
							 if( Utils.convertStrToInt(itemBean.getQty()) > 0){
								 logger.debug("keyBarcode["+keyBarcode+"]qty["+itemBean.getQty()+"]mat["+itemBean.getMaterialMaster()+"]barcode["+itemBean.getBarcode()+"]");
								 //add data to save
								 dataSaveMapAll.put(keyBarcode, itemBean);
							 }
						 }//while
					 }//if
				}//if
		    }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		return dataSaveMapAll;
	}
	
	private int getTotalQtyFromGroupCodeMap(HttpServletRequest request){
        int totalAllQty = 0;
	    Map<String, ReqPickStock> groupCodeMap = new HashMap<String, ReqPickStock>();
	    try{
		    if(request.getSession().getAttribute("groupCodeMap")!= null ){
		    	groupCodeMap = (Map)request.getSession().getAttribute("groupCodeMap");
		    	
		    	Iterator its =  groupCodeMap.keySet().iterator();
				while(its.hasNext()){
					 String key = (String)its.next();
					 ReqPickStock groupCodeBean = (ReqPickStock)groupCodeMap.get(key);
					// logger.debug("*** GroupCode["+groupCodeBean.getGroupCode()+"]qty["+Utils.convertStrToInt(groupCodeBean.getQty())+"]");
					 if( Utils.convertStrToInt(groupCodeBean.getQty()) > 0){
						 totalAllQty += Utils.convertStrToInt(groupCodeBean.getQty());
					 }
				}
		    }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		return totalAllQty;
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//Set data in screen to sesssion groupCodeMap
			setDataToSessionGroupCodeMap(request,aForm);
			//Convert data to ItemByBarcode qty > 0  to save
			Map<String, ReqPickStock> dataSaveMapAll = convertGroupCodeMapToDataSaveMapALL(request, aForm);
			
			//ReqPickStock Head
			ReqPickStock h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			ReqPickStock resultProcess = ReqPickStockDAO.save(conn,h,dataSaveMapAll);
			logger.debug("result save :"+resultProcess.isResultProcess());
			
			if(resultProcess.isResultProcess()){
			    //new search
				request.setAttribute("action", "newsearch");
				h.setNewReq(false);
				//h.setNewSearch(true);
				ReqPickStock p = searchBypage(conn, h, request);
				
				logger.debug(""+p.isCanCancel());
				
				p.setCanConfirm(false);
				
				aForm.setBean(p);
				
				request.getSession().setAttribute("results", p.getItems());
			    
				conn.commit();
				
			    request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			    
			    //clear error 
			    request.getSession().removeAttribute("itemsBarcodeErrorMap");
				request.getSession().removeAttribute("groupCodeErrorMap");
			}else{
				
				conn.rollback();
				request.setAttribute("Message", "ไม่สามารถเบิกได้ตามยอดที่ต้องการ โปรดตรวจสอบยอด Onhand ใหม่");
		
				//set session,groupCodeErrorMap, itemsBarcodeErrorMap  for display Error in screen
				logger.debug("itemsBarcodeErrorMap:"+resultProcess.getItemsBarcodeErrorMap());
				logger.debug("groupCodeErrorMap:"+resultProcess.getGroupCodeErrorMap());
				
				request.getSession().setAttribute("itemsBarcodeErrorMap", resultProcess.getItemsBarcodeErrorMap());
				request.getSession().setAttribute("groupCodeErrorMap", resultProcess.getGroupCodeErrorMap());
				
				//search refresh
				h.setNewSearch(false);
				h.setNoSearch(true);
				h.setNewReq(true);
				ReqPickStock p = searchBypage(conn, h, request);
				
				request.getSession().setAttribute("results", p.getItems());
			
			}
		} catch (Exception e) {
			conn.rollback();
            logger.error(e.getMessage(),e);
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
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			h.setStatus(ReqPickStockDAO.STATUS_CANCEL);//CANCEL
			h.setUpdateUser(user.getUserName());
		
			//update DB
			ReqPickStockDAO.updateStausStockIssue(conn, h);
			ReqPickStockDAO.updateStatusStockIssueItem(conn, h);
			
			//new search
			h.setNewSearch(true);
			h.setNewReq(false);
			
			request.getSession().setAttribute("results", h.getItems());
			aForm.setBean(h);
				
			conn.commit();
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
            conn.rollback();
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward confirmAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmAction");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			h.setStatus(ReqPickStockDAO.STATUS_POST);//POST 
			h.setUpdateUser(user.getUserName());
		
			//update DB
			ReqPickStockDAO.updateStausStockIssue(conn, h);
			ReqPickStockDAO.updateStatusStockIssueItem(conn, h);
			
			conn.commit();
			
			//new search
			request.setAttribute("action", "newsearch");
			h.setNewReq(false);
			h.setNewSearch(true);
	        h = searchBypageCaseView(conn, h, request);
	        
			request.getSession().setAttribute("resultsView", h.getItems());
			
			aForm.setBean(h);
				
			request.setAttribute("Message", "ยืนยันรายการ เรียบร้อยแล้ว");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward printPostRequestReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("Search for report : " + this.getClass());
		ReqPickStockForm reportForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;
			logger.debug("fileType:"+fileType);

			ReqPickStock h = reportForm.getBean();
			if(h != null){
				logger.debug("ReqPickStock:"+h);
				//Head
				parameterMap.put("p_title", "รายงานยืนยัน การเบิกสินค้า");
				parameterMap.put("p_issueReqDate", h.getIssueReqDate());
				parameterMap.put("p_issueReqNo", h.getIssueReqNo());
				parameterMap.put("p_statusDesc", h.getStatusDesc());
				parameterMap.put("p_requestor", h.getRequestor());
				parameterMap.put("p_custGroupDesc", h.getCustGroup());
				parameterMap.put("p_needDate", h.getNeedDate());
				parameterMap.put("p_storeCode", h.getStoreCode()+"-"+h.getStoreName());
				parameterMap.put("p_subInv", h.getSubInv());
				parameterMap.put("p_storeNo", h.getStoreNo());
				parameterMap.put("p_remark", h.getRemark());
				
				//Gen Report
				String fileName = "post_request_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				List items = (List)request.getSession().getAttribute("resultsView");
				
				logger.debug("items size:"+items.size());
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, items);
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  ");
				return  mapping.findForward("prepare");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		// return null;
		return null;
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		try {
			request.getSession().setAttribute("dataSaveMapAll", null);
			request.getSession().setAttribute("dataEditMapAll", null);
			request.getSession().setAttribute("results", null);
			ReqPickStock ad = new ReqPickStock();
			//ad.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
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
