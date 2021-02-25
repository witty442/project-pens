package com.isecinc.pens.web.transfer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.TransferBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MTransfer;
import com.isecinc.pens.web.externalprocess.ProcessAfterAction;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class TransferAction extends I_Action {

	public static int pageSize = 50;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		TransferForm stockForm = (TransferForm) form;
		Connection conn = null;
		try {
			 //init connection 
			 conn = DBConnection.getInstance().getConnection();
			
			 logger.debug("action:"+request.getParameter("action"));
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				TransferBean mCri = new TransferBean();
				stockForm.setResults(null);
				stockForm.setBean(mCri);
				
				//init sesion List
				request.getSession().setAttribute("TRANSFER_BANK_VAN", InitialReferences.getReferenceListByCode(conn,InitialReferences.TRANSFER_BANK_VAN));
				
				//Set cri session for Back page
				request.getSession().setAttribute("criteria_stock_2",mCri);
				
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				 stockForm.setBean(stockForm.getBeanCriteria());
				 search(stockForm,request,response); 
			 }
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			conn.close();
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare without ID
	 */
	
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		TransferForm stockForm = (TransferForm) form;
		String action = Utils.isNull(request.getParameter("action"));
		User user = (User) request.getSession(true).getAttribute("user");
		MTransfer mDAO = new MTransfer();
		Connection conn = null;
		Date currentDate = DateUtil.parse(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH), DateUtil.DD_MM_YYYY_WITH_SLASH);
		try {
			 logger.debug("prepare action:"+action);
			 String createDate = Utils.isNull(request.getParameter("createDate"));
			 conn = DBConnection.getInstance().getConnection();
			 
			 if("new".equalsIgnoreCase(action)){
				TransferBean mCri = new TransferBean();
				mCri.setCreateDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				mCri.setUserId(user.getId()+"");
				mCri.setCanEdit(true);
				
				//default 2 rows blank
				List<TransferBean> blankList = new ArrayList<TransferBean>();
				TransferBean rowBean = new TransferBean();
				rowBean.setCanEdit(true);
				//blankList.add(rowBean);
				//blankList.add(rowBean);
				
				//check exist wait
				List<TransferBean> transferList = mDAO.searchTransferList(conn,mCri,true,0,0);
				if(transferList != null && transferList.size() >0){
					transferList.addAll(blankList);
					stockForm.setLines(transferList);
				}else{
					stockForm.setLines(blankList);
				}
				
				//stockForm.setLines(blankList);
				stockForm.setBean(mCri);
				
			 }else if("edit".equalsIgnoreCase(action)){
				TransferBean mCri = new TransferBean();
				mCri.setCreateDate(createDate);
				mCri.setUserId(user.getId()+"");
				List<TransferBean> transferList = mDAO.searchTransferList(conn,mCri,true,0,0);
				
				//compare createDate >= currentDate the canEdit
				Date createDateValid = DateUtil.parse(mCri.getCreateDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				if( createDateValid.equals(currentDate) || createDateValid.after(currentDate)){
				   mCri.setCanEdit(true);
				}
				stockForm.setBean(mCri);
				stockForm.setLines(transferList);
				
             }else if("view".equalsIgnoreCase(action)){
            	 TransferBean mCri = new TransferBean();
				 mCri.setCreateDate(createDate);
				 mCri.setUserId(user.getId()+"");
				 List<TransferBean> transferList = mDAO.searchTransferList(conn,mCri,true,0,0);
				 
				//compare createDate >= currentDate the canEdit
				 Date createDateValid = DateUtil.parse(mCri.getCreateDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				 if( createDateValid.equals(currentDate) || createDateValid.after(currentDate)){
				    mCri.setCanEdit(true);
				 }
				 stockForm.setBean(mCri);
				 stockForm.setLines(transferList);
			 }
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn =null;
			}
		}
		return forward;
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		TransferForm stockForm = (TransferForm) form;
		try {
			logger.debug("prepare 2:"+request.getParameter("action"));
			
			 // save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}
	
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("search");
		TransferForm aForm = (TransferForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		MTransfer mDAO = new MTransfer();
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//Search head no where by create_date
			aForm.getBean().setCreateDate("");
			aForm.getBean().setUserId(user.getId()+"");
			
			conn = DBConnection.getInstance().getConnection();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(mDAO.searchTotalRecTransferList(conn, aForm.getBean()));
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
				List<TransferBean> items = mDAO.searchTransferList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResults(items);
				
				if(items.size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				   aForm.setResults(null);
				}else{
					//currPage ==totalPage ->get Summary
					if(currPage==aForm.getTotalPage()){
					   aForm.setSummary(mDAO.searchSummaryTransfer(conn, aForm.getBean()));
					}
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
				List<TransferBean> items = mDAO.searchTransferList(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResults(items);
				
				//currPage ==totalPage ->get Summary
				if(currPage==aForm.getTotalPage()){
					aForm.setSummary(mDAO.searchSummaryTransfer(conn, aForm.getBean()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return "search";
}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		TransferForm stockForm = (TransferForm) form;
		MTransfer mDAO = new MTransfer();
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		TransferBean item = null;
		int i=0;
		List<TransferBean> items = new ArrayList<TransferBean>();
		String lineIdDelete = "";
		Connection conn = null;
		try {
			logger.debug("save-->");
			conn =DBConnection.getInstance().getConnection();
			
			// check Token
			if (!isTokenValid(request)) {
				logger.debug("Token invalid");
				stockForm.getLines().clear();
				return "new";
			}
			
			TransferBean m =stockForm.getBean();
			m.setUserId(user.getId()+"");
			 
			List<TransferBean> itemList = new ArrayList<TransferBean>();
			String[] keyDatas = request.getParameterValues("keyData");
			String[] lineId = request.getParameterValues("lineId");
			String[] transferType = request.getParameterValues("transferType");
			String[] transferBank = request.getParameterValues("transferBank");
			String[] transferDate = request.getParameterValues("transferDate");
			String[] transferTime = request.getParameterValues("transferTime");
			String[] amounts = request.getParameterValues("amount");
			String[] chequeNo = request.getParameterValues("chequeNo");
			String[] chequeDate = request.getParameterValues("chequeDate");
			
			String[] row_can_edit = request.getParameterValues("row_can_edit");
			
			
	            
			logger.debug("keyDatas length:"+keyDatas.length);
			logger.debug("transferType length:"+transferType.length);
			
			for(i=0;i<keyDatas.length;i++){
				logger.debug("keyData:"+keyDatas[i]);
				if( !Utils.isNull(transferType[i]).equals("") 
					&& !Utils.isNull(keyDatas[i]).equals("CANCEL") ){
					
				   item = new TransferBean();
				   item.setLineId(Utils.convertStrToInt(lineId[i]));
				   item.setUserId(user.getId()+"");
				   item.setCreatedBy(user.getUserName());
				   item.setUpdateBy(user.getUserName());
				   item.setCreateDate(m.getCreateDate());
				   item.setTransferType(transferType[i]);
				   item.setTransferBank(transferBank[i]);
				   item.setTransferDate(transferDate[i]);
				   
				   /** append 0 to time 4:40 to 04:40**/
			       /*String hh = transferTime[i].split(":")[0];
			       hh = hh.length()==1?"0"+hh:hh;
			       String MM = transferTime[i].split(":")[1];
			       MM = MM.length()==1?"0"+MM:MM;*/
				   
				   item.setTransferTime(transferTime[i]);
				   
				   item.setAmount(amounts[i]);
				   item.setChequeNo(chequeNo[i]);
				   item.setChequeDate(chequeDate[i]);
				   
				   if(Utils.isNull(row_can_edit[i]).equals("true")){
					   item.setCanEdit(true);
				   }
				   
				   items.add(item);
				}else if (Utils.isNull(keyDatas[i]).equals("CANCEL")) {
					lineIdDelete += lineId[i]+",";
				}
			}
			m.setItems(items);
			m.setLineIdDelete(lineIdDelete);
			
			mDAO.save(user,m);
	
			List<TransferBean> itemSearchList = mDAO.searchTransferList(conn,m,true,0,0);
			if(itemSearchList != null && itemSearchList.size() >0){
				m = itemSearchList.get(0);//for check action control
			}
			stockForm.setLines(itemSearchList);
			
			request.setAttribute("Message",InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc() );
			
			 //set Btn Display
			if("Y".equalsIgnoreCase(m.getExported())){
			   stockForm.getBean().setCanEdit(false);
			}else{
			   stockForm.getBean().setCanEdit(true);
			}
			// save token
			saveToken(request);
			
			/** 
			* Process run after this action 
			* get sql manual script from 'c_after_action_sql' 
			* and run script by action name 
			**/ 
			ProcessAfterAction.processAfterAction(ProcessAfterAction.SAVE_TRANS_MONEY,stockForm.getBean().getCreateDate());

		} catch (Exception e) {
			request.setAttribute("Message",InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc() );
			logger.error(e.getMessage(),e);
			return "detail";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "detail";
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		TransferForm stockForm = (TransferForm) form;
		try {
			//Clear Parameter 
			/* stockForm.getBean().setRequestDateFrom(null);
			 stockForm.getBean().setRequestDateTo(null);
			 stockForm.setResults(null);
			 stockForm.setLines(null);
			 */
			 //Clear Criteria
			 request.getSession().setAttribute("criteria_",null);
			 
		} catch (Exception e) {
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
