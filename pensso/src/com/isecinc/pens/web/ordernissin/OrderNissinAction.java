package com.isecinc.pens.web.ordernissin;

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

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.CustomerNissin;
import com.isecinc.pens.bean.OrderNissin;
import com.isecinc.pens.bean.OrderNissinLine;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomerNissin;
import com.isecinc.pens.model.MOrderNissin;
import com.isecinc.pens.web.customernissin.CustomerNissinCriteria;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.UserUtils;

/**
 * Order Nissin Action
 * 
 * @author WITTY :25/11/2020
 * 
 */
public class OrderNissinAction extends I_Action {
	public static int pageSize = 60;
	
	public ActionForward prepareSearchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearchHead");
		OrderNissinForm aForm = (OrderNissinForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				OrderNissin bean =  new OrderNissin();
				bean.setOrderDateFrom(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				
				aForm.setBean(bean);
				
				//save criteria
				request.getSession().removeAttribute("_ordersearch_criteria");
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		//forward to mobile by check after login
		//return mapping.findForward(Utils.isNull(request.getParameter("mobile")).equals("true")?"searchMobile":"search");
		
		return mapping.findForward("search");
	}
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		OrderNissinForm aForm = (OrderNissinForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			String fromPage = Utils.isNull(request.getParameter("fromPage"));
			logger.debug("action:"+action);
			logger.debug("fromPage:"+fromPage);
			logger.debug("shortCustId:"+Utils.isNull(request.getParameter("shortCustId")));
			
			conn = DBConnectionApps.getInstance().getConnection();
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				
				if("customerNissinDetail".equalsIgnoreCase(fromPage)){
					String customerId = Utils.isNull(request.getParameter("shortCustId"));
					OrderNissin orderCri = new OrderNissin();
					orderCri.setCustomerNis(new MCustomerNissin().findOpt(conn, Utils.convertStrToLong(customerId)));
					aForm.setBean(orderCri);
					
					//case  back
					if("back".equalsIgnoreCase(action)){
						aForm.setBean((OrderNissin)request.getSession().getAttribute("_ordersearch_criteria"));
					}
				}else{
					//case  back
					if("back".equalsIgnoreCase(action)){
						aForm.setBean((OrderNissin)request.getSession().getAttribute("_ordersearch_criteria"));
					}
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				aForm.setPageSize(pageSize);
				aForm.getBean().setUser(user);
				
				//get Total Record
				aForm.setTotalRecord(MOrderNissin.searchTotalHead(conn,user,aForm.getBean()));
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
			    
			    //save criteria
				request.getSession().setAttribute("_ordersearch_criteria", aForm.getBean());
				
				//get Items Show by Page Size
				
				OrderNissin orderBean = MOrderNissin.searchHead(conn,user,aForm.getBean(),allRec,currPage,pageSize);
				if(orderBean.getItemsList().size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				aForm.setResults(orderBean.getItemsList());
				aForm.setBean(orderBean);
				
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
				aForm.getBean().setUser(user);
				OrderNissin orderBean = MOrderNissin.searchHead(conn,user,aForm.getBean(),allRec,currPage,pageSize);
				if(orderBean.getItemsList().size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
				aForm.setResults(orderBean.getItemsList());
				aForm.setBean(orderBean);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward(Utils.isNull(request.getParameter("mobile")).equals("true")?"searchMobile":"search");
	}
	
	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		
		OrderNissinForm orderNissinForm = (OrderNissinForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String customerId = Utils.isNull(request.getParameter("customerId"));
			String orderId = Utils.isNull(request.getParameter("orderId"));
			String fromPage = Utils.isNull(request.getParameter("fromPage"));
			String action = Utils.isNull(request.getParameter("action"));
			
			logger.debug("viewDetail action["+action+"]customerId["+customerId+"]orderId["+orderId+"]");
			logger.debug("fromPage["+fromPage+"]");
			
			if( !customerId.equalsIgnoreCase("") && action.equalsIgnoreCase("new")){
				// new order by custId
				CustomerNissin custNissin = new MCustomerNissin().findOpt(Utils.convertStrToInt(customerId));
				
				OrderNissin order = new OrderNissin();
				order.setCanEdit(true);
				order.setCustomerNis(custNissin);
				order.setOrderDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				
				orderNissinForm.setBean(order);
				
			    //save criteria
				CustomerNissinCriteria custCri = new CustomerNissinCriteria();
				custCri.setCustomer(custNissin);
				request.getSession().setAttribute("_cust_criteria", custCri);
			}else{
				//Case Nissin user view Order detail
				if(action.equalsIgnoreCase("view")){
					//view order detail by CustomerId
					OrderNissin orderCri = new OrderNissin();
					orderCri.setCustomerId(Utils.convertStrToInt(customerId));
					
					orderCri.setUser(user);
					//search order detail by orderId
					OrderNissin order = MOrderNissin.searchOrderNissin(orderCri);
					order.setCanEdit(false);
					orderNissinForm.setBean(order);
					orderNissinForm.setLines(order.getLinesList());
					
				}else{
					//view  detail by orderID
					OrderNissin orderCri = new OrderNissin();
					orderCri.setId(orderId);
					
					orderCri.setUser(user);
					//search order detail by orderId
					OrderNissin order = MOrderNissin.searchOrderNissin(orderCri);
					
					//Case PENS user No insert SalesrepCode  get from custSales By Province
					//get SalesrepCode by Customer
					if(    UserUtils.userInRole("ROLE_ALL",user,new String[]{User.ADMIN,User.NIS_PENS})
						&& Utils.isNull(order.getSalesrepCode()).equals("")){
						List<SalesrepBean> salesrepCodeList = MOrderNissin.getSalesCodeByCustomerNisDetail(order.getCustomerNis());
						order.setSalesrepCodeList(salesrepCodeList);
						if(salesrepCodeList != null && salesrepCodeList.size() >0){
							// found 1 sales set salesrepCode
							// found more 1 show listBox to select
							if(salesrepCodeList.size() ==1){
								SalesrepBean ss = salesrepCodeList.get(0);
								order.setSalesrepId(ss.getSalesrepId());
								order.setSalesrepCode(ss.getCode());
							}
						}
					}
	
					orderNissinForm.setBean(order);
					orderNissinForm.setLines(order.getLinesList());
				}
			}
			
			//for back from order detail
			orderNissinForm.setFromPage(fromPage);
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward(Utils.isNull(request.getParameter("mobile")).equals("true")?"detailMobile":"detail");
	}

	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		OrderNissinForm aForm = (OrderNissinForm) form;
		StringBuffer html = null;
		boolean excel = false;
		try {
			 logger.debug("exportToExcel :"+request.getParameter("action"));

			 excel = true;
			 html = OrderNissinReport.genReportToExcel(request, aForm, excel);
			 request.getSession().setAttribute("RESULTS",html);
			 
		     if(html ==null){
			    request.setAttribute("Message","ไม่พบข้อมูล");
		     }else{
		        //Export To Excel
			    java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(html.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
		     }
			 
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return mapping.findForward("search");
	}
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		OrderNissinForm orderNissinForm = (OrderNissinForm) form;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 
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
		OrderNissinForm orderNissinForm = (OrderNissinForm) form;
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
	
	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		OrderNissinForm mForm = (OrderNissinForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		OrderNissinForm aForm = (OrderNissinForm) form;
		String orderId = "";
		String msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
		User user = (User) request.getSession(true).getAttribute("user");
		int i= 0;
		try {
			logger.debug("save action");
			conn = new DBConnectionApps().getConnection();
			
			orderId = aForm.getBean().getId();

			// check Token
			if (!isTokenValid(request)) {
				CustomerNissin customer = new MCustomerNissin().find(conn,String.valueOf(aForm.getBean().getCustomerId()));
			
				aForm.getBean().setCustomerId(customer.getId());
				aForm.getBean().setCustomerNis(customer);
					
				aForm.getLines().clear();
				return "detail";
			}

			OrderNissin order = aForm.getBean();

			// set interfaces & payment & docstatus
			order.setInterfaces("N");
			order.setExported("N");
			order.setDocStatus("OPEN");
			order.setUser(user);

			// Begin Transaction
			conn.setAutoCommit(false);

			logger.debug("customerNis.id:"+order.getCustomerNis().getId());
			// Save Order
			if( Utils.isNull(order.getId()).equals("") || Utils.isNull(order.getId()).equals("0")){
				CustomerNissin customer = new MCustomerNissin().find(conn,String.valueOf(order.getCustomerNis().getId()));
				order.setNisCreateUser(customer.getCreatedBy());
				order = new MOrderNissin().insertOrderNissin(conn, order);
			}else{
				new MOrderNissin().updateOrderNissin(conn, order);
			}

			// Delete Line (chk delete by user)
			if (ConvertNullUtil.convertToString(aForm.getDeletedId()).trim().length() > 0){
				new MOrderNissin().deleteOrderNissinLine(conn,aForm.getDeletedId().substring(1).trim());
			}
			
			// Save Lines all new
			for (OrderNissinLine line :aForm.getLines() ) {
				//OrderNissinLine line = (OrderNissinLine) aForm.getLines().get(i);
				line.setLineNo(i++);
				
				line.setOrderId(Utils.convertStrToLong(order.getId()));
				line.setExported("N");
				line.setInterfaces("N");
				
				//Save Line to DB
				if(line.getId()==0){
					new MOrderNissin().insertOrderNissinLine(conn, order,line);
				}else{
					new MOrderNissin().updateOrderNissinLine(conn, order,line);
				}
			}
			msg = "บันทึกข้อมูลเรียบร้อยแล้ว";
			
			// Commit Transaction
			conn.commit();
			
			// set msg save success
			request.setAttribute("Message",msg );
			
			/** Manage Mode (add,edit,view) **/
			aForm.setMode("edit");
			
			/** search update display */
			order = MOrderNissin.searchOrderNissin(order);
			aForm.setBean(order);
			aForm.setLines(order.getLinesList());

			// save token
			saveToken(request);
			
		} catch (Exception e) {
			aForm.getBean().setId(orderId);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			e.printStackTrace();
			return "detail";
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "detail";
	}
	/**
	 * Save
	 */
	public ActionForward saveByPens(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		Connection conn = null;
		OrderNissinForm aForm = (OrderNissinForm) form;
		String orderId = "";
		String msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
		User user = (User) request.getSession(true).getAttribute("user");
		int i= 0;
		try {
			logger.debug("save by pens action");
			conn = new DBConnectionApps().getConnection();
			
			orderId = aForm.getBean().getId();

			// check Token
			if (!isTokenValid(request)) {
				return mapping.findForward("detail");
			}

			OrderNissin order = aForm.getBean();
			order.setUser(user);
			
			// Begin Transaction
			conn.setAutoCommit(false);

			if(UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS_PENS})){
				logger.debug("salesrepCode:"+order.getSalesrepCode());
				logger.debug("customerId:"+order.getOraCustomerId());
			   //Get salesrepId by SalesrepCode
			   if(Utils.isNull(order.getSalesrepId()).equals("")){
			      order.setSalesrepId(SalesrepDAO.getSalesrepBeanByCode(order.getSalesrepCode()).getSalesrepId());
			   }
			   //Case DoctStatus = 'ACCEPT'
			   if( !order.getDocStatus().equalsIgnoreCase("COMPLETE")){
				   //can pending status <> COMPLETE
			      String pendingStatus = Utils.isNull(order.getPendingStatus());
			      if( !pendingStatus.equals("")){
			         order.setDocStatus(pendingStatus);
			      }else{ 
			    	 if( !Utils.isNull(order.getInvoiceNo()).equals("")){
			    		 order.setDocStatus("COMPLETE");
			    		 order.setCompleteActionUser(user.getUserName());
			    	 }//if
			      }//if
			   }//if
				
			   //update 
			   new MOrderNissin().updateOrderNissinByPens(conn, order);
			   
			}else if(user.getType().equalsIgnoreCase(User.VAN)){
			   //Update orderNo (4 digit)
			   new MOrderNissin().updateOrderNissinBySalesPens(conn, order);
			}
		
			msg = "บันทึกข้อมูลเรียบร้อยแล้ว";
			
			// Commit Transaction
			conn.commit();
			
			// set msg save success
			request.setAttribute("Message",msg );
			
			/** Manage Mode (add,edit,view) **/
			aForm.setMode("edit");
			
			/** search update display */
			OrderNissin orderSearch = MOrderNissin.searchOrderNissin(order);
			aForm.setBean(orderSearch);
			aForm.setLines(orderSearch.getLinesList());
			
			// save token
			saveToken(request);
			
		} catch (Exception e) {
			aForm.getBean().setId(orderId);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			e.printStackTrace();
			return mapping.findForward("detail");
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
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
