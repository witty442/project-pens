package com.isecinc.pens.web.stockmc;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockMCAction extends I_Action {

	public static int pageSize = 60;
	public static String pageSTockMC = "STOCKMC";
	public static String pageSTockMCQuery = "STOCKMCQuery";
	public static String pageMasItemStockMC = "MasterItemStockMC";
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="";
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				aForm.setPageName(pageName);
				return new StockMCProcess().prepareSearch(mapping, aForm, request, response);
			}else if(pageSTockMCQuery.equalsIgnoreCase(pageName)){
				aForm.setPageName(pageName);
				return new StockMCQueryProcess().prepareSearch(mapping, aForm, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				aForm.setPageName(pageName);
				return new StockMCMasterItemProcess().prepareSearch(mapping, aForm, request, response);
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
		String msg = "";
		try {
			String action = Utils.isNull(request.getParameter("action"));
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			logger.debug("pageName:"+pageName);
			logger.debug("action:"+action);
			
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().searchHead(mapping, form, request, response);
			}else if(pageSTockMCQuery.equalsIgnoreCase(pageName)){
				return new StockMCQueryProcess().searchHead(mapping, form, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				return new StockMCMasterItemProcess().searchHead(mapping, form, request, response);
			}
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
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
		User user = (User) request.getSession().getAttribute("user");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		
		return "detail";
	}

	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("viewDetail");
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			logger.debug("pageName:"+pageName);

			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().viewDetail(mapping, form, request, response);
				
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				return new StockMCMasterItemProcess().viewDetail(mapping, form, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			
		}
		return mapping.findForward("detail");
	}
	public ActionForward viewDetailMobile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("viewDetail : ");
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			logger.debug("pageName:"+pageName);

			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().viewDetailMobile(mapping, form, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			
		}
		return mapping.findForward("detail");
	}
	public ActionForward loadItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("loadItem : ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().loadItem(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			
		}
		return mapping.findForward("detail");
	}
	public ActionForward loadItemMobile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("loadItem : ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().loadItemMobile(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			
		}
		return mapping.findForward("detail");
	}
	public ActionForward clearForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("clearForm  ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().clearForm(mapping, form, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				
			}	
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward viewSearchDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("viewSearchDetail : ");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		StockMCDAO dao = new StockMCDAO();
		try {
			//save criteria
			//aForm.setBeanCriteria(aForm.getBean());
			request.getSession().setAttribute("criteria_", aForm.getBean());
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			String headerId = Utils.isNull(request.getParameter("headerId"));
			String action = Utils.isNull(request.getParameter("action"));
			
			logger.debug("action:"+action);
			logger.debug("headerId:"+headerId);
			
			StockMCBean bean = new StockMCBean();
			bean.setId(Utils.convertStrToInt(headerId));
			bean = dao.searchStockMC(conn, bean, true);
			if(bean != null){
				aForm.setBean(bean);
				aForm.setResults(bean.getItems());
			}
			
			aForm.setPageName(aForm.getPageName());
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
		return mapping.findForward("detailMobile");
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String msg = "";
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		
		try {
			logger.debug("save-->");
           
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				 return new StockMCProcess().save(aForm, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				return new StockMCMasterItemProcess().save(aForm, request, response);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return "new";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().exportToExcel(mapping, aForm, request, response);
			}else if(pageSTockMCQuery.equalsIgnoreCase(pageName)){
		       return new StockMCQueryProcess().exportToExcel(mapping, aForm, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				return new StockMCMasterItemProcess().exportToExcel(mapping, form, request, response);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
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
	
	public ActionForward stockMCMStep1(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("stockMCMStep1");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="stockMCMStep1";
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String pageName = Utils.isNull(request.getParameter("pageName")); 
	
			if("new".equals(action)){
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
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
	
	/** choose customerCode ,storeCode **/
	public ActionForward stockMCMStep2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("stockMCMStep2");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="stockMCMStep2";
		StockMCBean bean = null;
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			//prepare bean
			if("new".equalsIgnoreCase(action)){
			   bean = new StockMCBean();
			   bean.setStockDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			}else if("back".equalsIgnoreCase(action)){
			   bean = aForm.getBean();
			}
			
			/////for test //////////////// 
			/*bean.setCustomerCode("020014");
			bean.setCustomerName("Lotus");
			bean.setStoreCode("5001");
			bean.setStoreName("«Õ¤Í¹Êá¤ÇÃì");*/
			//////////////////////////////
			
			aForm.setBean(bean);
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
	/**
	 * find CustomerCode ,StoreCode 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	/** choose brand **/
	public ActionForward stockMCMStep3(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("stockMCMStep3");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="stockMCMStep3";
		StockMCBean bean = null;
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			bean = aForm.getBean();
			
			//for test
			//bean.setBrand("501");
			//bean.setBrandName("Nissin Cup");
	
			//aForm.setBean(bean);
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
	
	/** Find Product By Brand **/
	public ActionForward stockMCMStep4(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("stockMCMStep4");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="stockMCMStep4";
		StockMCBean bean = null;
		StockMCBean existBean = null;
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			if("back".equalsIgnoreCase(action)){
				//clear insert form session case no save
			   bean = aForm.getBean();
			   bean = clearInputFormStep5(bean);
			}else{
			   bean = aForm.getBean();
			}
			//check stockDate,customerCode ,StoreCode is Exist
		    existBean = new StockMCDAO().searchStockMC(bean, true);
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			//search product by brand and check product IsExist 
			List<StockMCBean> productList = StockMCDAO.getProductMCItemList(conn,bean.getCustomerCode(),bean.getBrand(),0);
			List<StockMCBean> productAllList = new ArrayList<StockMCBean>();
			
			logger.debug("ExistBean:"+existBean);
			
            if(existBean != null){
            	logger.debug("Exist itemsList Size:"+existBean.getItems());
            	
            	bean = existBean;
            	bean.setBrand(aForm.getBean().getBrand());
            	bean.setBrandName(aForm.getBean().getBrandName());
            	
            	List<StockMCBean> productExistList = existBean.getItems();
            	//map productCode exist 
            	long lineId = 0;
            	for(int i=0;i<productList.size();i++){
            		lineId = 0;
            		StockMCBean p = productList.get(i);
            		for(int j=0;j<productExistList.size();j++){
            			StockMCBean e = productExistList.get(j);
            			if(p.getProductCode().equals(e.getProductCode())){
            				lineId = e.getLineId();
            				break;
            			}
            		}//for 2
            		
            		if(lineId !=0){
        				p.setLineId(lineId);
        				productAllList.add(p);
        			}else{
        				productAllList.add(p);
        			}
            	}//for 1
			}else{
				//not found in db
				productAllList.addAll(productList);
			}
			
			aForm.setResults(productAllList);
			//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
			
			String imageFileName  = bean.getStockDate().replaceAll("\\/", "")+"-"+bean.getCustomerCode();
			       imageFileName +="-"+bean.getStoreCode()+"-"+bean.getBrand()+".jpg";
	
		    String path = EnvProperties.getInstance().getProperty("path.stockmc.photo");
	
		    if(FileUtil.isFileExist(path+imageFileName)){
			   bean.setImageFileName(imageFileName);
		    }
			
			aForm.setBean(bean);
			
			//clear imageFile in from session (for user choose file only) 
			aForm.setImageFile(null);
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
	
	/** List Product By Brand ,customerCode,storeCode **/
	public ActionForward stockMCMStep5(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("stockMCMStep5");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="stockMCMStep5";
		StockMCBean headerBean = null;
		try {
			//clear imageFile in from session (for user choose file only) 
			aForm.setImageFile(null);
			
			String fromPage = Utils.isNull(request.getParameter("fromPage")); 
			
			String lineId = Utils.isNull(request.getParameter("selectLineId"));
			String productCode = Utils.isNull(request.getParameter("selectProductCode"));
			String productName = Utils.isNull(request.getParameter("selectProductName"));
			String barcode = Utils.isNull(request.getParameter("selectBarcode"));
			
			logger.debug("fromPage["+fromPage+"]");
			logger.debug("lineId["+lineId+"]");
			logger.debug("brand["+Utils.isNull(request.getParameter("selectBrand"))+"]");
			logger.debug("brandName["+Utils.isNull(request.getParameter("selectBrandName"))+"]");
			
			if("stockMCMSearchDetail".equalsIgnoreCase(fromPage)){
				headerBean = new StockMCBean();
				headerBean.setId(Utils.convertStrToInt(request.getParameter("selectHeaderId")));
				
				//get detail header by headerId
				headerBean = new StockMCDAO().searchStockMC(headerBean, false);
				headerBean.setBrand(Utils.isNull(request.getParameter("selectBrand")));
				headerBean.setBrandName(Utils.isNull(request.getParameter("selectBrandName")));
				
				logger.debug("headerId["+headerBean.getId()+"]canEdit["+headerBean.isCanEdit()+"]");
			}else{
				logger.debug("aForm.getBean()["+aForm.getBean()+"]");
			    headerBean = aForm.getBean();
			    //get header detail
			    headerBean = new StockMCDAO().searchStockMC(headerBean, false);
			    headerBean = headerBean==null?aForm.getBean():headerBean;
			    
			    headerBean.setBrand(aForm.getBean().getBrand());
			    headerBean.setBrandName(aForm.getBean().getBrandName());
			}
			
			logger.debug("headerBean["+headerBean+"]");
			
			headerBean.setLineId(Utils.convertStrToLong(lineId,0));
			headerBean.setProductCode(productCode);
			headerBean.setProductName(productName);
			headerBean.setBarcode(barcode);
			
			logger.debug("product["+headerBean.getProductCode()+"]lineId["+headerBean.getLineId()+"]");
			//check lineId is Exist
			if(headerBean.getLineId() !=0){
				//edit
				//load display old data
				StockMCBean itemBean = new StockMCDAO().searchStockMCDetail(String.valueOf(headerBean.getId()), productCode);
				
				//set header bean
				itemBean.setId(headerBean.getId());
				itemBean.setStockDate(headerBean.getStockDate());
				itemBean.setCustomerCode(headerBean.getCustomerCode());
				itemBean.setCustomerName(headerBean.getCustomerName());
				itemBean.setStoreCode(headerBean.getStoreCode());
				itemBean.setStoreName(headerBean.getStoreName());
				itemBean.setBrand(headerBean.getBrand());
				itemBean.setBrandName(headerBean.getBrandName());
				itemBean.setCanEdit(headerBean.isCanEdit());
				
				aForm.setBean(itemBean);
			}else{
				//new
				//Get product Detail
				StockMCBean productInfo = StockMCDAO.getProductMCItemInfo(headerBean.getCustomerCode(),headerBean.getProductCode());
				if(productInfo != null){
					headerBean.setProductName(productInfo.getProductName());
					headerBean.setProductPackSize(productInfo.getProductPackSize());
					headerBean.setProductAge(productInfo.getProductAge());
					headerBean.setRetailPriceBF(productInfo.getRetailPriceBF());
					headerBean.setBarcode(productInfo.getBarcode());
					headerBean.setBrand(productInfo.getBrand());
					headerBean.setMasterLegQty(productInfo.getMasterLegQty());
					headerBean.setUom(productInfo.getUom());
					headerBean.setCanEdit(true);//case insert new
				}
				
				aForm.setBean(headerBean);
			}
			
			String imageFileName  = aForm.getBean().getStockDate().replaceAll("\\/", "")+"-"+aForm.getBean().getCustomerCode();
		           imageFileName +="-"+aForm.getBean().getStoreCode()+"-"+aForm.getBean().getBrand()+".jpg";
		    String path = EnvProperties.getInstance().getProperty("path.stockmc.photo");
		    if(FileUtil.isFileExist(path+imageFileName)){
		       aForm.getBean().setImageFileName(imageFileName);
			}
		  
			aForm.setFromPage(fromPage);
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
	
	public ActionForward saveImage(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("saveImage");
		StockMCForm aForm = (StockMCForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String forward =Utils.isNull(request.getParameter("forward")); ;
		StockMCBean bean = null;
		FileOutputStream outputStream = null;
		FormFile file = null;
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			bean = aForm.getBean();
			
			file = aForm.getImageFile();
			if(file != null && file.getFileSize() >0){
				//String typeImageFile = file.getFileName().substring(file.getFileName().indexOf(".")+1,file.getFileName().length());
				String path = EnvProperties.getInstance().getProperty("path.stockmc.photo");
				String imageFileName  = bean.getStockDate().replaceAll("\\/", "")+"-"+bean.getCustomerCode();
					   imageFileName +="-"+bean.getStoreCode()+"-"+bean.getBrand()+".jpg";
			    
				outputStream = new FileOutputStream(new File(path+imageFileName));
				outputStream.write(file.getFileData());
				
				//set imageFileName
				bean.setImageFileName(imageFileName);
			}
			aForm.setBean(bean);
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if (outputStream != null) {
			    outputStream.close();
			 }
		}
		return mapping.findForward(forward);
	}
	public ActionForward saveByItemStockMC(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("saveByItemStockMC");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="stockMCMStep4";
		FileOutputStream outputStream = null;
		try {
			StockMCBean headerBean = aForm.getBean();
		    headerBean.setCreateUser(user.getUserName());
		    headerBean.setMcName(user.getName());
		    
			//check lineId is Exist
			if(headerBean.getLineId() !=0){
				//update 
				
				//load display old data
				StockMCBean itemBean = new StockMCDAO().searchStockMCDetail(String.valueOf(headerBean.getId()), headerBean.getProductCode());
				//set header bean
				itemBean.setCustomerCode(headerBean.getCustomerCode());
				itemBean.setCustomerName(headerBean.getCustomerName());
				itemBean.setStoreCode(headerBean.getStoreCode());
				itemBean.setStoreName(headerBean.getStoreName());
				itemBean.setBrand(headerBean.getBrand());
				itemBean.setBrandName(headerBean.getBrandName());
				itemBean.setCreateUser(headerBean.getCreateUser());
				itemBean.setMcName(user.getName());
				 
				 //set parameter
				itemBean.setLegQty(Utils.isNull(request.getParameter("legQty")));
				itemBean.setInStoreQty(Utils.isNull(request.getParameter("inStoreQty")));
				itemBean.setBackendQty(Utils.isNull(request.getParameter("backendQty")));
				itemBean.setUom(Utils.isNull(request.getParameter("uom")));
				itemBean.setFrontendQty1(Utils.isNull(request.getParameter("frontendQty1")));
				itemBean.setUom1(Utils.isNull(request.getParameter("uom1")));
				itemBean.setExpireDate1(Utils.isNull(request.getParameter("expireDate1")));
				itemBean.setFrontendQty2(Utils.isNull(request.getParameter("frontendQty2")));
				itemBean.setUom2(Utils.isNull(request.getParameter("uom2")));
				itemBean.setExpireDate2(Utils.isNull(request.getParameter("expireDate2")));
				itemBean.setFrontendQty3(Utils.isNull(request.getParameter("frontendQty3")));
				itemBean.setUom3(Utils.isNull(request.getParameter("uom3")));
				itemBean.setExpireDate3(Utils.isNull(request.getParameter("expireDate3")));
				
				itemBean.setItemCheck(headerBean.getItemCheck());
				itemBean.setReasonNId(Utils.isNull(request.getParameter("reasonNId")));
				itemBean.setDateInStore(Utils.isNull(request.getParameter("dateInStore")));
				itemBean.setDateInStoreQty(Utils.isNull(request.getParameter("dateInStoreQty")));
				itemBean.setReasonDId(Utils.isNull(request.getParameter("reasonDId")));
				
				itemBean.setNote(Utils.isNull(request.getParameter("note")));
				
				//Get product Detail
				StockMCBean productInfo = StockMCDAO.getProductMCItemInfo(itemBean.getCustomerCode(),itemBean.getProductCode());
				if(productInfo != null){
					itemBean.setProductName(productInfo.getProductName());
					itemBean.setProductPackSize(productInfo.getProductPackSize());
					itemBean.setProductAge(productInfo.getProductAge());
					itemBean.setRetailPriceBF(productInfo.getRetailPriceBF());
					itemBean.setBarcode(productInfo.getBarcode());
					itemBean.setBrand(productInfo.getBrand());
					itemBean.setMasterLegQty(productInfo.getMasterLegQty());
				}
				//save Mobile By Product
				itemBean = new StockMCDAO().saveByProduct(itemBean);
				
				aForm.setBean(itemBean);
			}else{
				//insert new
				 //set parameter
			    headerBean.setLegQty(Utils.isNull(request.getParameter("legQty")));
			    headerBean.setInStoreQty(Utils.isNull(request.getParameter("inStoreQty")));
			    headerBean.setBackendQty(Utils.isNull(request.getParameter("backendQty")));
			    headerBean.setUom(Utils.isNull(request.getParameter("uom")));
			    headerBean.setFrontendQty1(Utils.isNull(request.getParameter("frontendQty1")));
			    headerBean.setUom1(Utils.isNull(request.getParameter("uom1")));
			    headerBean.setExpireDate1(Utils.isNull(request.getParameter("expireDate1")));
			    headerBean.setFrontendQty2(Utils.isNull(request.getParameter("frontendQty2")));
			    headerBean.setUom2(Utils.isNull(request.getParameter("uom2")));
			    headerBean.setExpireDate2(Utils.isNull(request.getParameter("expireDate2")));
			    headerBean.setFrontendQty3(Utils.isNull(request.getParameter("frontendQty3")));
			    headerBean.setUom3(Utils.isNull(request.getParameter("uom3")));
			    headerBean.setExpireDate3(Utils.isNull(request.getParameter("expireDate3")));
			    
			    headerBean.setReasonNId(Utils.isNull(request.getParameter("reasonNId")));
			    headerBean.setDateInStore(Utils.isNull(request.getParameter("dateInStore")));
				headerBean.setDateInStoreQty(Utils.isNull(request.getParameter("dateInStoreQty")));
				headerBean.setReasonDId(Utils.isNull(request.getParameter("reasonDId")));
				headerBean.setNote(Utils.isNull(request.getParameter("note")));
				//Get product Detail
				StockMCBean productInfo = StockMCDAO.getProductMCItemInfo(headerBean.getCustomerCode(),headerBean.getProductCode());
				if(productInfo != null){
					headerBean.setProductName(productInfo.getProductName());
					headerBean.setProductPackSize(productInfo.getProductPackSize());
					headerBean.setProductAge(productInfo.getProductAge());
					headerBean.setRetailPriceBF(productInfo.getRetailPriceBF());
					headerBean.setBarcode(productInfo.getBarcode());
				}
				//save Mobile By Product
				headerBean = new StockMCDAO().saveByProduct(headerBean);
				
			}
	
			//save image
			FormFile file = aForm.getImageFile();
			logger.debug("imageFile:"+file +","+file.getFileSize());
			if(file != null && file.getFileSize() >0){
				//String typeImageFile = file.getFileName().substring(file.getFileName().indexOf(".")+1,file.getFileName().length());
				String path = EnvProperties.getInstance().getProperty("path.stockmc.photo");
				String imageFileName  = headerBean.getStockDate().replaceAll("\\/", "")+"-"+headerBean.getCustomerCode();
					   imageFileName +="-"+headerBean.getStoreCode()+"-"+headerBean.getBrand()+".jpg"; //save default type file to jpg all time
			    
				outputStream = new FileOutputStream(new File(path+imageFileName));
				outputStream.write(file.getFileData());
			}
			//goto Step 4 (list product by brand)
			stockMCMStep4(mapping, aForm, request, response);
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
			if (outputStream != null) {
			    outputStream.close();
			 }
		}
		return mapping.findForward(forward);
	}
	
	public static StockMCBean clearInputFormStep5(StockMCBean line){
		line.setItemCheck("");
	
		//clear Y
		line.setLegQty("");
		line.setInStoreQty("");
		line.setBackendQty("");
		line.setUom("");
		line.setFrontendQty1("");
		line.setUom1("");
		line.setExpireDate1("");
		line.setFrontendQty2("");
		line.setUom2("");
		line.setExpireDate2("");
		line.setFrontendQty3("");
		line.setUom3("");
		line.setExpireDate3("");
		
		 //clear N
		line.setReasonNId("");
		line.setDateInStore("");
		line.setDateInStoreQty("");
		
		//clear D
		line.setReasonDId("");
		
		return line;
	}
}
