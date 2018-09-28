package com.isecinc.pens.web.lockitem;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
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
import com.isecinc.pens.bean.LockItemOrderBean;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.LockItemOrderDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class LockItemOrderAction extends I_Action {

	public static int pageSize = 90;
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				LockItemOrderBean ad = new LockItemOrderBean();
				aForm.setBean(ad);
				
				//Get StoreCodeArr to Map Session
				request.getSession().setAttribute("StoreCodeToMap", null);
				request.getSession().setAttribute("groupStoreMapError", null);
				
				List<Master> custGroupList = new ArrayList<Master>();
				Master refP = new Master(); 
				custGroupList.add(refP);
				custGroupList.addAll(GeneralDAO.getCustGroupList(""));
				request.getSession().setAttribute("custGroupList",custGroupList);
				
			}else if("back".equals(action)){
				LockItemOrderBean oldCri = aForm.getBeanCriteria();
				
				aForm.setBean(LockItemOrderDAO.searchHead(oldCri));
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
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			LockItemOrderBean bean = LockItemOrderDAO.searchHead(aForm.getBean());
			aForm.setBean(bean);
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
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "detail";
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		LockItemOrderBean bean = new LockItemOrderBean();
		logger.debug("prepare");
		Connection conn = null;
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			conn = DBConnection.getInstance().getConnection();
			
            String groupStore = Utils.isNull(request.getParameter("groupStore"));
            String groupCode = Utils.isNull(request.getParameter("groupCode"));
            String storeCode = Utils.isNull(request.getParameter("storeCode"));
            String lockDate = Utils.isNull(request.getParameter("lockDate"));
            String action = Utils.isNull(request.getParameter("action"));
		
			logger.debug("prepare edit groupCode:"+groupCode+",groupStore="+groupStore+",storeCode="+storeCode+",action:"+action);
			logger.debug("lockDate:"+lockDate);
			
			//Clear groupStoreMapError Session
			request.getSession().setAttribute("groupStoreMapError", null);
			
			if("edit".equalsIgnoreCase(action)){
				//Find By  GroupCode and lockDate
				bean.setGroupCode(groupCode);
				bean.setLockDate(lockDate);
				List<LockItemOrderBean> items =  LockItemOrderDAO.searchHead(conn,bean).getItems();
				bean = items.get(0);
				bean.setMode("edit");
				aForm.setBean(bean);
				
				//Get StoreCodeArr to Map Session
				request.getSession().setAttribute("StoreCodeToMap", LockItemOrderDAO.setStoreCodeToMap(conn, aForm.getBean()));
				
				//init custGroupList session
				request.getSession().setAttribute("custGroupList",GeneralDAO.getCustGroupList(""));
				
			}else{
				bean.setMode("add");
				aForm.setBean(bean);
				//Get StoreCodeArr to Map Session
				request.getSession().setAttribute("StoreCodeToMap", null);
				
				//init custGroupList session and no display groupStore is set allStore 
				request.getSession().setAttribute("custGroupList",GeneralDAO.getCustGroupList(""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", "err:"+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return forward;
	}
	
	public ActionForward prepareReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareReport");
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				LockItemOrderBean ad = new LockItemOrderBean();
				
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
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			LockItemOrderBean b = aForm.getBean();
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
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		try {
			aForm.setResultsSearch(null);
			LockItemOrderBean bean = new LockItemOrderBean();
			//Can Edit
			if ( Utils.userInRole(user,new String[]{User.ADMIN,User.HRM}) ){
				bean.setCanEdit(true);
			}
			aForm.setBean(bean);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
	
	public ActionForward deleteLockItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("deleteLockItem");
		User user = (User) request.getSession().getAttribute("user");
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		String[] keyDeleteArr = null;
		String groupCode ="";
		String groupStore = "";
		String storeCode = "";
		String lockDate = "";
		Connection conn = null;
		int deleteC = 0;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			//keyDelete = mc.getGroupCode()+","+mc.getGroupStore()+","+mc.getStoreCode()+","+mc.getLockDate();
	        String[] keyDelete = request.getParameterValues("keyDelete");
	       for(int i=0;i<keyDelete.length;i++){
	    	   if( !Utils.isNull(keyDelete[i]).equals("")){
	    		   keyDeleteArr = Utils.isNull(keyDelete[i]).split("\\,");
	    		   groupCode =keyDeleteArr[0];
	    		   groupStore = keyDeleteArr[1];
	    		   storeCode = keyDeleteArr[2];
	    		   lockDate = keyDeleteArr[3];
	    		   
	    		   deleteC = LockItemOrderDAO.deleteLockItem(conn, groupCode, groupStore, storeCode, lockDate);
	    		   logger.debug("delete groupCode["+groupCode+"]groupStore["+groupStore+"]storeCode["+storeCode+"]lockDate["+lockDate+"]eff["+deleteC+"]");
	    	   }
	       }
          
	       conn.commit();
	       
	       //refresh data
	        LockItemOrderBean bean = LockItemOrderDAO.searchHead(aForm.getBean());
			aForm.setBean(bean);
			aForm.setResultsSearch(aForm.getBean().getItems());
			
			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResultsSearch(null);
			}
			
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LockItemOrderForm summaryForm = (LockItemOrderForm) form;
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
		LockItemOrderForm orderForm = (LockItemOrderForm) form;
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
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		int i= 0;
		int r= 0;
		String errorMsg = "";
		String[] errors = null;
		boolean error = false;
		boolean foundError = false;
		String storeCodeArrDB = "";
		Master m = null;
		Map<String,String> groupStoreMapError = new HashMap<String, String>();
		try {
			request.getSession().setAttribute("groupStoreMapError",null);
			
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			LockItemOrderBean h = aForm.getBean();
			
			//head 
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			String storeCodeArrStr = "";
	        String chkStore = "";
	        String[] storeCodeArray = null;
			List<Master> custGroupList = (List<Master>)request.getSession().getAttribute("custGroupList");
			
			//Set Data Before save for display Case error
			request.getSession().setAttribute("StoreCodeToMap",LockItemOrderDAO.setDataBeforeSaveStoreCodeToMap(h,custGroupList,request));
			
		   for(i=0;i<custGroupList.size();i++){
			   error = false;
			   m = custGroupList.get(i);
			   chkStore = request.getParameter("chkStore_"+m.getPensValue()); 
			   
			   //logger.debug("CustGroup["+m.getPensValue()+"]chkStore["+chkStore+"]");
			   h.setGroupStore(m.getPensValue());
			   
			   //Check Old Data DB Save By ALlStore Or SomeStore
			   storeCodeArrDB=  LockItemOrderDAO.getStoreCodeArrExist(conn, h);
			   logger.debug("**** getStoreCodeArrExist DB:"+storeCodeArrDB);
			   
			   /********** Validate Found GroupCode and groupStore is set to AllStore ****************************************/
			   if( "allStore".equalsIgnoreCase(chkStore)){
				  logger.debug("** Start Validate All Store *****");
				  LockItemOrderErrorBean errorBean  = LockItemOrderDAO.validateAllStore(conn, h,"");
				  if(errorBean != null &&  !Utils.isNull(errorBean.getErrorMsg()).equals("")){
			    	    errorMsg += errorBean.getErrorMsg();
			    	    error = true;
						foundError = true;
						
						//set Error Group Store Map
						groupStoreMapError.put(h.getGroupStore(), h.getGroupStore());
			      }
			      logger.debug("validateGroupStoreSetToAllStore error:"+error);
			  
			   /*********** Validate Store Code by parameter on Screen*******************************************************************************/
		      }else if( !Utils.isNull(request.getParameter("store_"+m.getPensValue())).equals("")){ 
		    	    /** Case Old Data is save by AllStore Must Validate **/
		    	     if(Utils.stringInStringArr("ALL", storeCodeArrDB.split("\\,"))){
		    	    	 logger.debug("** Case Old Data is save by AllStore Must Validate");
		    	    	 
		    	    	 LockItemOrderErrorBean errorBean = LockItemOrderDAO.validateAllStore(conn, h,"");
					     if(errorBean != null &&  !Utils.isNull(errorBean.getErrorMsg()).equals("")){
					    	    errorMsg += errorBean.getErrorMsg();
					    	    error = true;
								foundError = true;
								
								//set Error Group Store Map
								groupStoreMapError.put(h.getGroupStore(), h.getGroupStore());
					      }
					     logger.debug("result validateGroupStoreSetToAllStore error:"+error);
		    	     }
		    	  
		    	     if(!error){
					     storeCodeArrStr =  Utils.isNull(request.getParameter("store_"+m.getPensValue()));
					     logger.debug("** Start Validate Some Store ***** StoreCode:"+storeCodeArrStr);
					     
						 if( storeCodeArrStr.indexOf(",") ==-1){
							 storeCodeArray = new String[1];
							 storeCodeArray[0] = storeCodeArrStr;
						 }else{
						     storeCodeArray = storeCodeArrStr.split("\\,");
						 }
						 //Prepare Bean to check
						 LockItemOrderBean lockCheck = new LockItemOrderBean();
						 lockCheck.setGroupCode(h.getGroupCode());
						 lockCheck.setGroupStore(h.getGroupStore());
						 lockCheck.setLockDate(h.getLockDate());
						 lockCheck.setUnlockDate(h.getUnlockDate());
						 lockCheck.setMode(h.getMode());
						
						//logger.debug("storeCodeArray length:"+storeCodeArray.length);
				        //save by store code
						for( r=0;r<storeCodeArray.length;r++){
							error = false;
							//logger.debug("storeCode["+r+"]["+Utils.isNull(storeCodeArray[r])+"]");
							
							lockCheck.setStoreCode(storeCodeArray[r]);
				            //validate Store is Exist
							LockItemOrderErrorBean errorBean = LockItemOrderDAO.validateAllStore(conn, lockCheck,lockCheck.getStoreCode());
						     if(errorBean != null &&  !Utils.isNull(errorBean.getErrorMsg()).equals("")){
		    				    errorMsg += errorBean.getErrorMsg();
		    				    error = true;
		    				    foundError = true;
		    				    
		    				  //set Error Group Store Map
								groupStoreMapError.put(lockCheck.getGroupStore(), lockCheck.getGroupStore());
		    			    }//if
						}//for
						
						logger.debug("validateStoreNo error:"+error);
		    	    }
			   }
			   /***********************************************************************************************************/
			   
			   if(error==false){
					//Check ALL Store Found
					if( "allStore".equals(chkStore)){
						logger.debug("*** All Store *** ");
						if(LockItemOrderDAO.isDataAllStore(conn, h)){
					   	   //Clear old data save by store
						   LockItemOrderDAO.deleteModel(conn, h);
						}
						
						h.setStoreCode("ALL");
						//update or insert
						int u = LockItemOrderDAO.updateModel(conn, h);
						if(u==0){
						   LockItemOrderDAO.insertModel(conn, h);
						}
						
					}else if( !Utils.isNull(request.getParameter("store_"+m.getPensValue())).equals("")){
						 logger.debug("*** By Store *** ");
						
						if(LockItemOrderDAO.isDataAllStore(conn, h)){
							logger.debug("dataAllStore delete ");
						   	//Clear old data save by AllStore
							 LockItemOrderDAO.deleteModel(conn, h);
						}
						
						//prepare data
						 storeCodeArrStr =  Utils.isNull(request.getParameter("store_"+m.getPensValue()));
					     
						 if( storeCodeArrStr.indexOf(",") ==-1){
							 storeCodeArray = new String[1];
							 storeCodeArray[0] = storeCodeArrStr;
						 }else{
						     storeCodeArray = storeCodeArrStr.split("\\,");
						 }
						
	                    //save by store code
						for( r=0;r<storeCodeArray.length;r++){
							logger.debug("storeCode["+r+"]["+Utils.isNull(storeCodeArray[r])+"]");
							
	                        h.setStoreCode(storeCodeArray[r]);
						    //update or insert
							int u = LockItemOrderDAO.updateModel(conn, h);
							if(u==0){
							    LockItemOrderDAO.insertModel(conn, h);
							}//if
                        
						}//for
					}//if
			   }//if
		    }//for	
			  
		   logger.debug("foundError:"+foundError);
		    if(foundError){
		    	logger.debug("Conn Rollback");
		    	conn.rollback();
		    	request.setAttribute("Message", errorMsg);
		    }else{
		       logger.debug("Conn Commit");
		       conn.commit();
		       request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		       
		      //Get StoreCodeArr to Map Session
			   request.getSession().setAttribute("StoreCodeToMap", LockItemOrderDAO.setStoreCodeToMap(conn, h));
			   h.setMode("edit");
			   aForm.setBean(h);
		    }
		    //set error group Store Map
			request.getSession().setAttribute("groupStoreMapError", groupStoreMapError);
		} catch (Exception e) {
			logger.debug("Conn Rollback");
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
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		User user = (User) request.getSession().getAttribute("user");
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		try {
			aForm.setResults(new ArrayList<LockItemOrderBean>());
			
			LockItemOrderBean bean = new LockItemOrderBean();
			bean.setMode("add");
			aForm.setBean(bean);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		LockItemOrderForm aForm = (LockItemOrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		boolean found = false;
		try {
			logger.debug("PageAction:"+request.getParameter("page"));
	
			LockItemOrderBean cri = aForm.getBean();
			
			/*htmlTable = SAExportExcel.genSARewardTranReport(cri,user);
			if( !"".equals(htmlTable.toString())){
				found = true;
			}*/
			
			if(found){
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(htmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			}else{
				request.setAttribute("Message","ไม่พบข้อมูล");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
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
