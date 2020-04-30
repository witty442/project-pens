package com.isecinc.pens.web.boxno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.bean.StockReturn;
import com.isecinc.pens.web.reportall.bean.StockReturnLine;
import com.isecinc.pens.web.reportall.page.dao.StockReturnDAO;
import com.isecinc.pens.web.salestarget.SalesTargetBean;
import com.isecinc.pens.web.stock.StockBean;
import com.pens.util.BeanParameter;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class BoxNoAction extends I_Action {

	private int pageSize =50;
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		BoxNoForm boxNoForm = (BoxNoForm) form;
		Connection conn = null;
		try {
			 logger.debug("prepare :"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 
			 conn = DBConnection.getInstance().getConnectionApps();
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 BoxNoBean b = new BoxNoBean();
				 boxNoForm.setBean(b);
				 boxNoForm.setResults(null);
				 
				 request.getSession().setAttribute("criteria_",null);
				 
				//init periodList
				 request.getSession().setAttribute("PERIOD_LIST", initPeriod());
				 //init PD By User Login
				 request.getSession().setAttribute("PD_LIST", initPdList(conn,user));	
				 
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				 if(request.getSession().getAttribute("criteria_") != null){
					 BoxNoBean b = (BoxNoBean)request.getSession().getAttribute("criteria_");
					 boxNoForm.setBean(b);
					 search(boxNoForm,request,response);
				 }
			 }
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn = null;
			}
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BoxNoForm boxNoForm = (BoxNoForm) form;
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}
	
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("search");
		BoxNoForm aForm = (BoxNoForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String action = Utils.isNull(request.getParameter("action")); 
		int currPage = 1;
		boolean allRec = false;
		boolean excel = false;
		BoxNoDAO boxNoDAO = new BoxNoDAO();
		try {
			logger.debug("search action:"+action);
			
			//Save Criteria Search
			request.getSession().setAttribute("criteria_", aForm.getBean());
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					//aForm.setBean(aForm.getCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(boxNoDAO.searchTotalRecBoxNoList(conn,user,aForm));
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
				BoxNoBean boxNoBean = boxNoDAO.searchBoxNoList(conn,user,aForm,allRec,currPage,pageSize,excel);
				//logger.debug("dataHTMLStr:"+boxNoBean.getDataStrBuffer());
				if(boxNoBean.getDataStrBuffer() != null){
					request.setAttribute("boxNoForm_RESULTS",boxNoBean.getDataStrBuffer());
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					request.setAttribute("boxNoForm_RESULTS",null);
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
			    
			    BoxNoBean boxNoBean = boxNoDAO.searchBoxNoList(conn,user,aForm,allRec,currPage,pageSize,excel);
				//logger.debug("dataHTMLStr:"+stockResult.getDataStrBuffer());
				if(boxNoBean.getDataStrBuffer() != null){
					 request.setAttribute("boxNoForm_RESULTS",boxNoBean.getDataStrBuffer());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return "search";
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		BoxNoForm boxNoForm = (BoxNoForm) form;
		try {
			//Clear Parameter
			 boxNoForm.setBean(new BoxNoBean());
			 boxNoForm.setResults(null);
			 
			 //Clear Criteria
			 request.getSession().setAttribute("criteria_",null);
			 
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	public ActionForward prepareDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareDetail");
		BoxNoForm aForm = (BoxNoForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			String action = Utils.isNull(request.getParameter("action"));
			BoxNoDAO mDAO = new BoxNoDAO();
			if("new".equalsIgnoreCase(action)){
				BoxNoBean bean = new BoxNoBean();
				bean.setCanEdit(true);
				aForm.setMode("add");
				aForm.setBean(bean);
			}else if("edit".equalsIgnoreCase(action)){
				BoxNoBean cri = new BoxNoBean();
				cri.setSalesrepCode(Utils.isNull(request.getParameter("salesrepCode")));
				cri.setPeriod(Utils.isNull(request.getParameter("period")));
				cri.setPdCode(Utils.isNull(request.getParameter("pdCode")));
				cri.setPdCodeKey(Utils.isNull(request.getParameter("pdCode")));//for Case update
				
				BoxNoBean bean = mDAO.searchBoxNo(conn, user, cri);
				aForm.setMode("edit");
				aForm.setBean(bean);
			}else if("view".equalsIgnoreCase(action)){
				BoxNoBean cri = new BoxNoBean();
				cri.setSalesrepCode(Utils.isNull(request.getParameter("salesrepCode")));
				cri.setPeriod(Utils.isNull(request.getParameter("period")));
				cri.setPdCode(Utils.isNull(request.getParameter("pdCode")));
				cri.setPdCodeKey(Utils.isNull(request.getParameter("pdCode")));//for Case update
				
				BoxNoBean bean = mDAO.searchBoxNo(conn, user, cri);
				bean.setCanEdit(false);
				aForm.setMode("view");
				aForm.setBean(bean);
			}
			
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("detail");
	}
	
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		BoxNoForm aForm = (BoxNoForm) form;
		BoxNoDAO dao = new BoxNoDAO();
		Connection conn= null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			conn.setAutoCommit(false);
			
			logger.debug("save");
			BoxNoBean bean = aForm.getBean();
			bean.setSalesrepId(""+user.getId());
			bean.setSalesrepCode(user.getCode());
			bean.setSalesrepName(user.getSalesrepFullName());
			bean.setCreateUser(user.getUserName());
			bean.setUpdateUser(user.getUserName());
			
			logger.debug("PdCodeKey:"+bean.getPdCodeKey());
			
			//Case New PDCodeKey is null set pdCodeKey(for update) = pdCode
			if(Utils.isNull(bean.getPdCodeKey()).equals("")){
			   bean.setPdCodeKey(bean.getPdCode());
			}
			dao.saveBoxNo(conn,user, bean);
			
			conn.commit();
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อย");
			
			//search 
			//set new PDCodeKey = pdCode after save
			bean.setPdCodeKey(bean.getPdCode());
			
			bean = dao.searchBoxNo(conn, user, bean);
			aForm.setMode("edit");
			aForm.setBean(bean);
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			return "detail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn = null;
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}


	public ActionForward printBoxNoReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("printBoxNoReport");
		BoxNoForm aForm = (BoxNoForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		BoxNoBean p  =null;
		String fileNameExport  = "";
		BoxNoDAO mDAO = new BoxNoDAO();
		try { 
			String actionDB = Utils.isNull(request.getParameter("actionDB"));
			if("saveDB".equalsIgnoreCase(actionDB)){
				
				conn = DBConnection.getInstance().getConnectionApps();
				conn.setAutoCommit(false);
				
				//Save Before Print
				BoxNoBean bean = aForm.getBean();
				bean.setSalesrepId(""+user.getId());
				bean.setSalesrepCode(user.getCode());
				bean.setSalesrepName(user.getSalesrepFullName());
				bean.setCreateUser(user.getUserName());
				bean.setUpdateUser(user.getUserName());
				
				//Case New PDCodeKey is null set pdCodeKey = pdCode
				if(Utils.isNull(bean.getPdCodeKey()).equals("")){
					bean.setPdCodeKey(bean.getPdCode());
				}
				
				mDAO.saveBoxNo(conn,user, bean);
				request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อย");
				
				conn.commit();
				
				//set new PDCodeKey = pdCode after save
				bean.setPdCodeKey(bean.getPdCode());
			}
			
			//Start Report
			ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,Object> parameterMap = new HashMap<String,Object>();
			ServletContext context = request.getSession().getServletContext();
	
			String fileName = "box_no_pdf_report";
            String fileJasper = BeanParameter.getReportPath() + fileName;
          
            //init connection 
			conn = DBConnection.getInstance().getConnectionApps();
		
            //get head detail
			String period = Utils.isNull(request.getParameter("period"));
			String pdCode = Utils.isNull(request.getParameter("pdCode"));
			String salesrepCode = Utils.isNull(request.getParameter("salesrepCode"));
			BoxNoBean cri = new BoxNoBean();
			cri.setPeriod(period);
			cri.setPdCode(pdCode);
			cri.setPdCodeKey(pdCode);
			cri.setSalesrepCode(salesrepCode);
            p = mDAO.searchBoxNo(conn, user, cri);
			
            int totalBox = Utils.convertStrToInt(p.getTotalBox());
            List<BoxNoBean> dataList = new ArrayList<BoxNoBean>();
            BoxNoBean rowBean = new BoxNoBean();
			for(int i=1;i<=totalBox;i++){
			   rowBean = new BoxNoBean();
			   rowBean.setReportName("สินค้าชำรุด/เสียหาย ของเดือน : "+p.getPeriod());
			   rowBean.setPdCode("PD : "+p.getPdCode()+" "+p.getPdDesc());
			 //  rowBean.setPdDesc();
			   rowBean.setSalesrepCode("พนักงานขาย : "+p.getSalesrepCode() +" "+p.getSalesrepName());
			  // rowBean.setSalesrepName();
			   rowBean.setTotalBox("กล่องที่  "+i+"/"+totalBox);
			   dataList.add(rowBean);
			}
			logger.debug("dataList size:"+dataList.size());
			reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName,dataList ,fileNameExport);

			//update printDate
			mDAO.updatePrintDate(conn, cri);
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return null;
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
	public  List<PopupBean> initPeriod(){
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		String periodValue = "";
		PopupBean item = new PopupBean();
		try{
			//blank
			item.setKeyName("");
			item.setValue("");
			monthYearList.add(item);
			
			for(int i=0;i<=1;i++){
				item = new PopupBean();
				periodValue =  DateUtil.stringValue(cal.getTime(),"MM/yyyy",DateUtil.local_th).toUpperCase();
				periodName =  DateUtil.stringValue(cal.getTime(),"MMMMM-yyyy",DateUtil.local_th).toUpperCase();
				item.setKeyName(periodName);
				item.setValue(periodValue);
				monthYearList.add(item);
				
				//Month -1
				cal.add(Calendar.MONTH, -1);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	 return monthYearList;
	}
	public  List<PopupBean> initPdList(Connection conn,User user) throws Exception{
		List<PopupBean> pdList = new ArrayList<PopupBean>();
		PopupBean item = new PopupBean();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try{
			sql = " select s.subinventory,(sn.description ||'('|| sn.attribute6 ||')') as description";
			sql +=" from apps.xxpens_inv_subinv_access s ,apps.mtl_secondary_inventories sn \n";
			sql +=" where s.subinventory = sn.secondary_inventory_name \n";
			sql += "and s.subinventory like 'P%' \n";
			sql +=" and s.code ='"+user.getUserName().toUpperCase()+"' \n";
			
			logger.debug("sql:"+sql);
	        ps = conn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        //blank
	    	item = new PopupBean();
			item.setKeyName("เลือก");
			item.setValue("");
			pdList.add(item);
	        while(rs.next()){
				item = new PopupBean();
				item.setKeyName(Utils.isNull(rs.getString("subinventory"))+"-"+Utils.isNull(rs.getString("description")));
				item.setValue(Utils.isNull(rs.getString("subinventory")));
				pdList.add(item);
	        }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			ps.close();
			rs.close();
		}
	 return pdList;
	}
	
}
