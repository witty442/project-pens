package com.isecinc.pens.web.billplan;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.BillPlan;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MBillPlan;
import com.isecinc.pens.web.moveorder.MoveOrderForm;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class BillPlanAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		BillPlanForm billPlanForm = (BillPlanForm) form;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 BillPlan b = new BillPlan();
				 b.setNoBillPlan("true");
				 billPlanForm.setBillPlan(b);
				 billPlanForm.setResults(null);
				 billPlanForm.setLines(null);
				 
				 request.getSession().setAttribute("criteria_",null);
				 
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				 BillPlan b = (BillPlan)request.getSession().getAttribute("criteria_");
				 billPlanForm.getCriteria().setBillPlan(b);
				 search(billPlanForm,request,response);
			 }
			// save token
			saveToken(request);
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
		BillPlanForm billPlanForm = (BillPlanForm) form;
		try {
			 logger.debug("prepare 2:"+request.getParameter("action"));
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 BillPlan b = new BillPlan();
				 b.setNoBillPlan("true");
				 billPlanForm.setBillPlan(b);
				 billPlanForm.setResults(null);
				 billPlanForm.setLines(null);
				 
				 request.getSession().setAttribute("criteria_",null);
			 }
			
			 // save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		BillPlanForm billPlanForm = (BillPlanForm) form;
		try {
			//Clear Parameter
			 billPlanForm.setBillPlan(new BillPlan());
			 billPlanForm.setResults(null);
			 billPlanForm.setLines(null);
			 
			 //Clear Criteria
			 request.getSession().setAttribute("criteria_",null);
			 
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}
	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BillPlanForm mForm = (BillPlanForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			logger.debug("noBillPlan:"+mForm.getCriteria().getBillPlan().getNoBillPlan());
			
			MBillPlan mDAO = new MBillPlan();
			List<BillPlan> billPlanList = mDAO.searchBillPlanList(mForm.getCriteria().getBillPlan(),user);
			mForm.setResults(billPlanList);
			if(billPlanList != null && billPlanList.size()==0){
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
			
			//save Criteria to Session
			request.getSession().setAttribute("criteria_", mForm.getCriteria().getBillPlan());
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("view");
		BillPlanForm mForm = (BillPlanForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String billPlanNo = Utils.isNull(request.getParameter("billPlanNo"));
			MBillPlan mDAO = new MBillPlan();
			BillPlan mCriteria = new BillPlan();
			mCriteria.setBillPlanNo(billPlanNo);
			//Search
			mCriteria = mDAO.searchBillPlan(mCriteria, user);
			if(Utils.isNull(mCriteria.getBillPlanRequestDate()).equals("")){
			    String currentDate = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			    mCriteria.setBillPlanRequestDate(currentDate);
			}
			mForm.setBillPlan(mCriteria);
			mForm.setLines(mForm.getBillPlan().getBillPlanLineList());
			
			if("".equalsIgnoreCase(Utils.isNull(mCriteria.getPdDesc()))){
				request.setAttribute("Message","ข้อมูล PD:"+mCriteria.getPdCode()+" ไม่ตรงกับข้อมูล PD ของ:"+mCriteria.getSalesCode() +" ที่ระบบได้ตั้งไว้  ไม่สามารถทำรายการต่อได้");
			}
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("view");
	}
	
	public ActionForward confirmBillPlan(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("view");
		BillPlanForm mForm = (BillPlanForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			MBillPlan mDAO = new MBillPlan();
			//Confirm Bill Plan
			BillPlan b = mForm.getCriteria().getBillPlan();
			b.setStatus(MBillPlan.STATUS_SAVE);//Confirm SV
			//Check RequestDate
			b = checkRequestDate(b);
			//Update 
			mDAO.updateBillPlan(b);
			//Search Show
			mForm.setBillPlan(mDAO.searchBillPlan(mForm.getCriteria().getBillPlan(), user));
			mForm.setLines(mForm.getBillPlan().getBillPlanLineList());
			
			request.setAttribute("Message","ยืนยันรับสินค้าเรียบร้อยแล้ว");
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("view");
	}
	
	//Validate requestDate Case diff day(month end date - request date) = 2  set request date = 01/nextMonth/nextYear
	private BillPlan checkRequestDate(BillPlan head) {
		try{
			List<References> refList = InitialReferences.getReferenes().get(InitialReferences.BILLPLAN);
			logger.info("Integer.parseInt(refList.get(0).getKey()):"+Integer.parseInt(refList.get(0).getKey()));
			int diffDayToEndConfig = refList != null? Integer.parseInt(refList.get(0).getKey())-1:1;
			 
            Calendar currentDate = Calendar.getInstance();
			
			Date requestDateObj = Utils.parse(head.getBillPlanRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
			Calendar requestDate = Calendar.getInstance();
			requestDate.setTime(requestDateObj);
			int dayInMonthOfRequestDate = requestDate.get(Calendar.DATE);
			
			Calendar monthEndDate = Calendar.getInstance();
			int lastDayInMonthOfCurrentDate = monthEndDate.getActualMaximum(Calendar.DATE);
			
			//diff day 
			int diffDayRequestDateToMonthEndDate = lastDayInMonthOfCurrentDate - dayInMonthOfRequestDate;
			
			System.out.println("currentDate:"+currentDate);
			System.out.println("diffDayToEndConfig:"+diffDayToEndConfig);
			System.out.println("requestDate:"+requestDate.getTime());
			System.out.println("monthEndDate:"+monthEndDate.getTime());
			System.out.println("lastDayInMonthOfCurrentDate:"+lastDayInMonthOfCurrentDate);
			System.out.println("dayInMonthOfRequestDate:"+dayInMonthOfRequestDate);
			
			System.out.println("diffDayRequestDateToMonthEndDate:"+diffDayRequestDateToMonthEndDate);
			
			//int diffDay <=1 
			if(diffDayRequestDateToMonthEndDate <= diffDayToEndConfig){
				System.out.println("set request Date to 01/nextMonth/nextYear");
				currentDate.add(Calendar.MONTH, 1);//next Month or NextYear 
				currentDate.set(Calendar.DATE, 1);//set to 01/xx/xxxx
				
				String requestDateStr = Utils.stringValue(currentDate.getTime(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				head.setBillPlanRequestDate(requestDateStr);
				System.out.println("requestDate :"+head.getBillPlanRequestDate());
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return head;
	}
	
	public ActionForward cancelBillPlan(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("view");
		BillPlanForm mForm = (BillPlanForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			MBillPlan mDAO = new MBillPlan();
			//Cancel Bill Plan
			BillPlan b = mForm.getCriteria().getBillPlan();
			b.setStatus(MBillPlan.STATUS_VOID);//Cancel VO
			b.setBillPlanRequestDate(null);
			//Update
			mDAO.updateBillPlan(b);
            //Search to Show
			mForm.setBillPlan(mDAO.searchBillPlan(mForm.getCriteria().getBillPlan(), user));
			mForm.setLines(mForm.getBillPlan().getBillPlanLineList());
			
			request.setAttribute("Message","ยกเลิกการยืนยันรับสินค้าเรียบร้อยแล้ว");
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("view");
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			logger.debug("save-->");

		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			return "new";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "preview";
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
