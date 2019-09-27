package com.isecinc.pens.web.sa;

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

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.SADamageBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SADamageDAO;
import com.isecinc.pens.dao.SATranDAO;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
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
public class SADamageAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				SADamageBean ad = new SADamageBean();
				//can Edit
				if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
					ad.setCanEdit(true);
				}
				aForm.setBean(ad);
			}else if("back".equals(action)){
				SADamageBean oldCri = aForm.getBeanCriteria();
				SADamageBean bean = SADamageDAO.searchHead(oldCri,"",false,"");
				//can Edit
				if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
					bean.setCanEdit(true);
				}
				aForm.setBean(bean);
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setBean(SADamageDAO.searchHead(aForm.getBean(),"",false,""));
			aForm.setResultsSearch(aForm.getBean().getItems());
			
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
		return mapping.findForward("search");
	}
	
	public ActionForward prepareNoDamageSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareNoDamageSearch");
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				SADamageBean ad = new SADamageBean();
				
				//can Edit
				if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
					ad.setCanEdit(true);
				}
				aForm.setBean(ad);
			}else if("back".equals(action)){
				SADamageBean oldCri = aForm.getBeanCriteria();
				SADamageBean bean = SADamageDAO.searchHead(oldCri,"",false,"noDamage");
				//can Edit
				if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
					bean.setCanEdit(true);
				}
				aForm.setBean(bean);
				aForm.setResultsSearch(aForm.getBean().getItems());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("noDamageSearch");
	}
	
	public ActionForward searchNoDamage(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchNoDamage");
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setBean(SADamageDAO.searchHead(aForm.getBean(),"",false,"noDamage"));
			aForm.setResultsSearch(aForm.getBean().getItems());
			
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
		return mapping.findForward("noDamageSearch");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		SADamageBean bean = new SADamageBean();
		logger.debug("prepare");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
            String empId = Utils.isNull(request.getParameter("empId"));
            String type = Utils.isNull(request.getParameter("type"));
            String invRefwal = Utils.isNull(request.getParameter("invRefwal"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit empId:"+empId+",action:"+action);
			if("add".equalsIgnoreCase(action)){
				//init default value
				bean.setTranDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				bean.setType("BME");
			}else{
				SADamageBean c = new SADamageBean();
				c.setEmpId(empId);
				c.setType(type);
				c.setInvRefwal(invRefwal);
				bean = SADamageDAO.searchHead(c,"edit",true,"").getItems().get(0);
			
			}
			
			//can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
				bean.setCanEdit(true);
			}
			
			logger.debug("canEdit:"+bean.isCanEdit());
			
			aForm.setBean(bean);
			aForm.setMode(action);//Mode Edit ,Add
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			
		}
		return forward;
	}
	
	public ActionForward prepareNoDamage(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareNoDamage");
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		SADamageBean bean = new SADamageBean();
		String mode = "";
		try {
			//save old criteria Case link from damageSearch 
			aForm.setBeanCriteria(aForm.getBean());
			
            String empId = Utils.isNull(request.getParameter("empId"));
            String type = Utils.isNull(request.getParameter("type"));
            String invRefwal = Utils.isNull(request.getParameter("invRefwal"));
            String action = Utils.isNull(request.getParameter("action"));
            
			if("add".equals(action)){
				logger.debug("add");
				//init default value
				bean = new SADamageBean();
				bean.setInvoiceDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				bean.setTranDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				bean.setType("BME"); //default case new add
				mode ="add";
			}else{
				SADamageBean c = new SADamageBean();
				c.setEmpId(empId);
				c.setType(type);
				c.setInvRefwal(invRefwal);
				bean = SADamageDAO.searchHead(c,"edit",true,"noDamage").getItems().get(0);
			    mode ="edit";
			}
			
			//can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
				bean.setCanEdit(true);
			}
			
			aForm.setBean(bean);
			aForm.setMode(mode);//Mode Edit ,Add
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("noDamage");
	}
	
	public ActionForward prepareReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareReport");
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				SADamageBean ad = new SADamageBean();
				
				aForm.setBean(ad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward searchReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchReport");
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			SADamageBean b = aForm.getBean();
			//aForm.setBean(MCBeanDAO.searchHead(aForm.getBean()));
			//aForm.setResultsSearch(aForm.getBean().getItems());
			
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
		return mapping.findForward("detail");
	}
	
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		User user = (User) request.getSession().getAttribute("user");
		SADamageForm aForm = (SADamageForm) form;
		try {
			aForm.setResultsSearch(null);
			SADamageBean bean = new SADamageBean();
			//can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
				bean.setCanEdit(true);
			}
			aForm.setBean(bean);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	public ActionForward clearNoDamageSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		User user = (User) request.getSession().getAttribute("user");
		SADamageForm aForm = (SADamageForm) form;
		try {
			aForm.setResultsSearch(null);
			SADamageBean bean = new SADamageBean();
			//can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
				bean.setCanEdit(true);
			}
			aForm.setBean(bean);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("noDamageSearch");
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SADamageForm summaryForm = (SADamageForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "detail";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SADamageForm orderForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		
			request.setAttribute("Message", msg);
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
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int r=  0;
		String idDelete = "";
		int lineIdNew = 0;
		boolean isFoundReward = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SADamageBean h = aForm.getBean();
			logger.debug("mode:"+aForm.getMode());
			//head 
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Get Data Table Item
			String[] payType = request.getParameterValues("payType");
			String[] payDate = request.getParameterValues("payDate");
			String[] payAmt = request.getParameterValues("payAmt");
			String[] status = request.getParameterValues("status");
			String[] lineId = request.getParameterValues("lineId");
			String[] ids = request.getParameterValues("ids");
			
			//Add data  to List
			List<SADamageBean> itemList = new ArrayList<SADamageBean>();
			for(int i=0;i<payType.length;i++){
				logger.debug("payType["+i+"]["+Utils.isNull(payType[i])+"]");
				logger.debug("status["+i+"]["+Utils.isNull(status[i])+"]");
				if( !Utils.isNull(payType[i]).equals("") && !Utils.isNull(status[i]).equalsIgnoreCase("AB")){
					SADamageBean item = new SADamageBean();
			
					//set key
					item.setCreateUser(h.getCreateUser());
					item.setUpdateUser(h.getUpdateUser());
					item.setEmpId(h.getEmpId());
					item.setType(h.getType());
					item.setInvRefwal(h.getInvRefwal());
					
					item.setLineId(Utils.isNull(lineId[i]));
					item.setPayType(Utils.isNull(payType[i]));
					item.setPayDate(Utils.isNull(payDate[i]));
					item.setPayAmt(Utils.isNull(payAmt[i]));
					
					item.setId(Utils.isNull(ids[i]));
					
					itemList.add(item);
				}else if( Utils.isNull(status[i]).equalsIgnoreCase("AB")){
					//Delete lineIdDelete
					idDelete += Utils.isNull(ids[i])+",";
				}
			}//for
			
			//set Date to Bean case Show Error
			h.setItems(itemList);
			aForm.setBean(h);
			
			//Validate StaffId duplicate
			if("add".equalsIgnoreCase((aForm.getMode()))){
				logger.debug("insert:");
	            
				//Validate Duplicate
				boolean dup = SADamageDAO.isDuplicateInvRefWal(conn, h.getInvRefwal());
				if(dup){
					request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้  เนื่องจาก invoice No / Ref Wacoal ข้อมูลซ้ำ");
					return "detail";
				}
				
				//Validate Duplicate PK
				dup = SADamageDAO.isDuplicateDamageHeadPK(conn, h.getEmpId(), h.getType(), h.getInvRefwal());
				if(dup){
					request.setAttribute("Message", "Primary key Duplicate");
					return "detail";
				}
				
			    h = SADamageDAO.insertHeadModel(conn, h);
			  
			    //Insert Item Trans 
			    int id= 0;
			    if(itemList != null && itemList.size()>0){
					for(int i=0;i<itemList.size();i++){
						SADamageBean item = itemList.get(i);
					    id++;
					    item.setId(id+"");
						r +=SADamageDAO.insertItemModel(conn, item);
						//check is found reward
						if(item.getPayType().equalsIgnoreCase("2. หักค่าเฝ้าตู้")){
							isFoundReward = true;
						}
						
					}
				    logger.debug("total insert record :"+r);
			    }
				
			}else{
				logger.debug("update:");
				 SADamageDAO.updateHeadModel(conn, h);
				 
				  //delete item Trans status ='AB'
				 if(idDelete != null && idDelete.length() >0){
					 idDelete = idDelete.substring(0,idDelete.length()-1);
				 
				    r = SADamageDAO.deleteItemModelById(conn, h,idDelete);  
				    logger.debug("result delete:"+r);
				 }
				 
				 //Get MaxId from getMaxIdFromDanageTran
				 int maxId = SADamageDAO.getMaxIdFromDanageTran(conn, h.getEmpId(), h.getType(), h.getInvRefwal());
				 
				 //Insert or update Item Trans
			    if(itemList != null && itemList.size()>0){
					for(int i=0;i<itemList.size();i++){
						lineIdNew++;
						SADamageBean item = itemList.get(i);
						item.setLineId(lineIdNew+"");
						
						if( !"".equals(item.getId())){
							int u = SADamageDAO.updateItemModelById(conn, item);
							r +=u;
						}else{
							maxId++;
							item.setId(maxId+"");
							r +=SADamageDAO.insertItemModel(conn, item);
						}
						//check is found reward
						if(item.getPayType().equalsIgnoreCase("2. หักค่าเฝ้าตู้")){
							isFoundReward = true;
						}
					}
				    logger.debug("total insert record :"+r);
			    } 
			}
	        //update sa_reward_tran used row
			if(isFoundReward){
				SATranDAO.updateFlagUsed(conn,h,"Y");
			}else{
				SATranDAO.updateFlagUsed(conn,h,"N");
			}
			
			//Search Again
			String mode ="edit";
			SADamageBean criPK = new SADamageBean();
			criPK.setEmpId(h.getEmpId());
			criPK.setType(h.getType());
			criPK.setInvRefwal(h.getInvRefwal());
			
			SADamageBean bean = SADamageDAO.searchHead(conn,mode,criPK,true,"").getItems().get(0);
			//Can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
				bean.setCanEdit(true);
			}
		    aForm.setBean(bean);
		    aForm.setMode(mode);
		    
		    conn.commit();
		    request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "detail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	/**
	 * Save
	 */
	public ActionForward saveNoDamage(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		Connection conn = null;
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int r=  0;
		String idDelete = "";
		int lineIdNew = 0;
		boolean isFoundReward = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SADamageBean h = aForm.getBean();
			logger.debug("mode:"+aForm.getMode());
			//head 
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Get Data Table Item
			String[] payType = request.getParameterValues("payType");
			String[] payDate = request.getParameterValues("payDate");
			String[] payAmt = request.getParameterValues("payAmt");
			String[] status = request.getParameterValues("status");
			String[] lineId = request.getParameterValues("lineId");
			String[] ids = request.getParameterValues("ids");
			
			//Add data  to List
			List<SADamageBean> itemList = new ArrayList<SADamageBean>();
			for(int i=0;i<payType.length;i++){
				logger.debug("payType["+i+"]["+Utils.isNull(payType[i])+"]");
				logger.debug("status["+i+"]["+Utils.isNull(status[i])+"]");
				if( !Utils.isNull(payType[i]).equals("") && !Utils.isNull(status[i]).equalsIgnoreCase("AB")){
					SADamageBean item = new SADamageBean();
			
					//set key
					item.setCreateUser(h.getCreateUser());
					item.setUpdateUser(h.getUpdateUser());
					item.setEmpId(h.getEmpId());
					item.setType(h.getType());
					item.setInvRefwal(h.getInvRefwal());
					
					item.setLineId(Utils.isNull(lineId[i]));
					item.setPayType(Utils.isNull(payType[i]));
					item.setPayDate(Utils.isNull(payDate[i]));
					item.setPayAmt("0");
					
					item.setId(Utils.isNull(ids[i]));
					
					itemList.add(item);
				}else if( Utils.isNull(status[i]).equalsIgnoreCase("AB")){
					//Delete lineIdDelete
					idDelete += Utils.isNull(ids[i])+",";
				}
			}//for
			
			//set Date to Bean case Show Error
			h.setItems(itemList);
			aForm.setBean(h);
			
			//Validate StaffId duplicate
			if("add".equalsIgnoreCase((aForm.getMode()))){
				logger.debug("insert:");
				//Gen Dummy InvRef
	            h.setInvRefwal(SADamageDAO.genDummyInvRefWal(h.getEmpId(), h.getTranDate()));
				
				//Validate Duplicate PK
				boolean dup = SADamageDAO.isDuplicateDamageHeadPK(conn, h.getEmpId(), h.getType(), h.getInvRefwal());
				if(dup){
					request.setAttribute("Message", "Primary key Duplicate");
					return mapping.findForward("noDamage");
				}
				
			    h = SADamageDAO.insertHeadModel(conn, h);
			  
			    //Insert Item Trans 
			    int id= 0;
			    if(itemList != null && itemList.size()>0){
					for(int i=0;i<itemList.size();i++){
						SADamageBean item = itemList.get(i);
					    id++;
					    item.setId(id+"");
					    item.setInvRefwal(h.getInvRefwal());
						r += SADamageDAO.insertItemModel(conn, item);
						//check is found reward
						if(item.getPayType().equalsIgnoreCase("2. หักค่าเฝ้าตู้")){
							isFoundReward = true;
						}
						
					}
				    logger.debug("total insert record :"+r);
			    }
				
			}else{
				logger.debug("update:");
				 SADamageDAO.updateHeadModel(conn, h);
				 
				  //delete item Trans status ='AB'
				 if(idDelete != null && idDelete.length() >0){
					 idDelete = idDelete.substring(0,idDelete.length()-1);
				 
				    r = SADamageDAO.deleteItemModelById(conn, h,idDelete);  
				    logger.debug("result delete:"+r);
				 }
				 
				 //Get MaxId from getMaxIdFromDanageTran
				 int maxId = SADamageDAO.getMaxIdFromDanageTran(conn, h.getEmpId(), h.getType(), h.getInvRefwal());
				 
				 //Insert or update Item Trans
			    if(itemList != null && itemList.size()>0){
					for(int i=0;i<itemList.size();i++){
						lineIdNew++;
						SADamageBean item = itemList.get(i);
						item.setLineId(lineIdNew+"");
						
						if( !"".equals(item.getId())){
							int u = SADamageDAO.updateItemModelById(conn, item);
							r +=u;
						}else{
							maxId++;
							item.setId(maxId+"");
							r +=SADamageDAO.insertItemModel(conn, item);
						}
						//check is found reward
						if(item.getPayType().equalsIgnoreCase("2. หักค่าเฝ้าตู้")){
							isFoundReward = true;
						}
					}
				    logger.debug("total insert record :"+r);
			    } 
			}
	        //update sa_reward_tran used row
			if(isFoundReward){
				SATranDAO.updateFlagUsed(conn,h,"Y");
			}else{
				SATranDAO.updateFlagUsed(conn,h,"N");
			}
			
			//Search Again
			String mode ="edit";
			SADamageBean criPK = new SADamageBean();
			criPK.setEmpId(h.getEmpId());
			criPK.setType(h.getType());
			criPK.setInvRefwal(h.getInvRefwal());
			
			SADamageBean bean = SADamageDAO.searchHead(conn,mode,criPK,true,"noDamage").getItems().get(0);
			//Can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
				bean.setCanEdit(true);
			}
		    aForm.setBean(bean);
		    aForm.setMode(mode);
		    
		    conn.commit();
		    request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return mapping.findForward("noDamage");
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("noDamage");
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResults(new ArrayList<SADamageBean>());
			
			SADamageBean bean = new SADamageBean();
			bean.setTranDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			bean.setType("BME");
			//can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
				bean.setCanEdit(true);
			}
			aForm.setBean(bean);
			
			aForm.setMode("add");
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward clearNoDamage(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		SADamageForm aForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			aForm.setResults(new ArrayList<SADamageBean>());
			
			SADamageBean bean = new SADamageBean();
			bean.setTranDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			bean.setType("BME");
			//can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.SALE}) ){
				bean.setCanEdit(true);
			}
			aForm.setBean(bean);
			
			aForm.setMode("add");
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("noDamage");
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
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("Search for report : " + this.getClass());
		SADamageForm reportForm = (SADamageForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;//request.getParameter("fileType");
			logger.debug("fileType:"+fileType);
			
			SADamageBean h = null;// MCBeanDAO.searchReport(reportForm.getBean());
			if(h != null){
				//Head
				//parameterMap.put("p_boxno", h.getBoxNo());
				//parameterMap.put("p_jobname", h.getJobId()+"-"+h.getName());
				//parameterMap.put("p_remark", Utils.isNull(h.getRemark()));
	
				//Gen Report
				String fileName = "boxno_pdf_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, h.getItems());
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  พิมพ์รายการที่มีสถานะเป็น CLOSE เท่านั้น");
				return  mapping.findForward("prepare");
			}
		} catch (Exception e) {
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		// return null;
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
	
	
}
