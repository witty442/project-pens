package com.isecinc.pens.web.prodshow;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.ProdShowBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ProdShowDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MProductCategory;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ProdShowAction extends I_Action {
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		ProdShowForm aForm = (ProdShowForm) form;
		Connection conn = null;
		EnvProperties env = EnvProperties.getInstance();
		try {
			/*//clear image file on temp all
			String imageLocalTempPath = env.getProperty("path.image.prodshow.local")+"temp/";//"D:/SalesApp/Images-prodshow/";
			//delete file all in Dir
			FileUtil.deleteFileAllInDir(imageLocalTempPath);
			//create Dir case no exist
			FileUtil.createDir(imageLocalTempPath);
			*/
			 logger.debug("prepare action:"+request.getParameter("action"));
			 User user = (User) request.getSession(true).getAttribute("user");
			 
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 conn  = DBConnection.getInstance().getConnection();
				 
				 String orderNo = Utils.isNull(request.getParameter("orderNo"));
				 Order order = new MOrder().findByWhereCond(conn,"where order_no='"+orderNo+"'");
				 if(order!= null){
					 ProdShowBean bean = new ProdShowBean();
					 bean.setOrderNo(orderNo);
					 
					 //get Customer Detail
					 Customer cus = new MCustomer().findByWhereCond(conn, " where customer_id ="+order.getCustomerId());
					 if(cus != null){
					    bean.setCustomerCode(cus.getCode());
					    bean.setCustomerName(cus.getName());
					 }
					 //get ProdShow Data
					 ProdShowBean prodShowBean = null;// ProdShowDAO.searchProdShow(conn, orderNo, false);
					 if(prodShowBean !=null){
					    if("Y".equalsIgnoreCase(prodShowBean.getExport())){
						  bean.setMode("view");
						  bean.setCanSave(false);
					    }else{
					       bean.setCanSave(true);
					       bean.setMode("edit");
					    }
					 }else{
						bean.setCanSave(true);
						bean.setDocDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
						bean.setMode("add"); 
					 }
					 aForm.setBean(bean);
				 }
					
				//List All Brand
				MProductCategory mProductCat = new MProductCategory();
				List<References> brandList = mProductCat.lookUpBrandAllListNew(user);
				List<References> brandAllList = new ArrayList<References>();
				References ref = new References("", "", "");
				brandAllList.add(ref);
				brandAllList.addAll(brandList);
				request.getSession().setAttribute("brandList", brandAllList); 
				
			 }else if("back".equalsIgnoreCase(request.getParameter("action"))){
				
			 }
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn !=null){
				conn.close();
			}
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ProdShowForm f = (ProdShowForm) form;
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
		ProdShowForm mForm = (ProdShowForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
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
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ProdShowForm f = (ProdShowForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int i= 0;
		List<ProdShowBean> itemList = new ArrayList<ProdShowBean>();
		EnvProperties env = EnvProperties.getInstance();
		try {
			logger.debug("save-->");
			
			// check Token
			if (!isTokenValid(request)) {
				logger.debug("Token invalid");
				return "new";
			}
	
			ProdShowBean m = f.getBean();
			m.setCreatedBy(user.getId()+"");
			m.setUpdatedBy(user.getId()+"");

		    //Create Path upload
			String imageLocalPath = env.getProperty("path.image.prodshow.local");//"D:/SalesApp/Images-prodshow/";
			FileUtil.createDir(imageLocalPath);//Create HomePath
			
			logger.debug("get Parameter File ");
			// Check that we have a file upload request
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			logger.debug("isMultipart:"+isMultipart);
			
			try {
				// Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();

				// Configure a repository (to ensure a secure temp location is used)
				ServletContext servletContext = request.getSession().getServletContext();
				File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
				factory.setRepository(repository);

				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);
		        List<FileItem> items = upload.parseRequest(request);
		        logger.debug("items size:"+items.size());
		        
		        for (FileItem item : items) {
		        	logger.debug("isFormField:"+item.isFormField());
		            if (item.isFormField()) {
		                // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
		                String fieldName = item.getFieldName();
		                String fieldValue = item.getString();
		                // ... (do your job here)
		                
		                logger.debug("fieldName:"+fieldName);
		                logger.debug("fieldValue:"+fieldValue);
		            } else {
		                // Process form file field (input type="file").
		                String fieldName = item.getFieldName();
		                String fileName = FilenameUtils.getName(item.getName());
		                InputStream fileContent = item.getInputStream();
		                // ... (do your job here)
		                
		                logger.debug("fieldName:"+fieldName);
		                logger.debug("fileName:"+fileName);
		                logger.debug("fileContent:"+fileContent);
		            }//if
		        }//for
		        
		    } catch (FileUploadException e) {
		        throw new ServletException("Cannot parse multipart request.", e);
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
			
			//get parameter
			/*String[] brands = request.getParameterValues("brand");
			String[] status = request.getParameterValues("status");
			String[] fileName = request.getParameterValues("fileName");
			String[] filePath = request.getParameterValues("filePath");
			for(i=0;i<brands.length;i++){
				if( !Utils.isNull(status[i]).equalsIgnoreCase("CANCEL")){
					ProdShowBean item = new ProdShowBean();
					item.setBrand(Utils.isNull(brands[i]));
					
					itemList.add(item);
				};
			}*/
			//save to DB
			
			if("".equals(m.getOrderNo())){
				//Fail
				msg = InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc();
			}else{
				//Success
				msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
			}

			request.setAttribute("Message",msg );
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			return "new";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "prepare";
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ProdShowForm f = (ProdShowForm) form;
		try {
			//Clear Parameter 
		
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepare");
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
