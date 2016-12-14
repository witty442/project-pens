package com.isecinc.pens.web.imports;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.imports.process.ImportLoadStockInitProcess;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * ImportAction Class
 * 
 * @author Witty.B
 * @version $Id: ConversionAction.java,v 1.0 19/10/2010 00:00:00
 * 
 */

public class ImportAction extends I_Action {

	
	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Interfaces Prepare Form");
		ImportForm importForm = (ImportForm) form;
		String returnText = "prepare";
		try {
			importForm.setPage(Utils.isNull(request.getParameter("page")));
			
			if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")){
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
			throw e;
		}
		
		return returnText;
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Interfaces Prepare Form without ID");
		ImportForm importForm = (ImportForm) form;
		String returnText = "prepare";
		try {
			importForm.setPage(Utils.isNull(request.getParameter("page")));
			
			if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")){
				importForm.setCountDate(null);
				importForm.setCustCode(null);
				importForm.setDataFile(null);
				importForm.setStoreCode("");
				importForm.setStoreName("");
				importForm.setImportDate("");
				
				importForm.setSummary(new OnhandSummary());
				importForm.setSummarySuccessList(null);
				importForm.setSummaryErrorList(null);
				
				importForm.setImported(false);
				importForm.setTotalSize(0);
				importForm.setSummaryPhyListErrorSize(0);
				importForm.setSummaryPhyListSuccessSize(0);
				
				importForm.setSummaryLotusErrorSize(0);
				importForm.setSummaryLotusSuccessSize(0);
				
				importForm.setSummaryBigCErrorSize(0);
				importForm.setSummaryBigCSuccessSize(0);
				
				importForm.setSummaryWacoalListErrorSize(0);
				importForm.setSummaryWacoalListSuccessSize(0);
				
				importForm.setBoxNo("");
				importForm.setSummaryReturnWacoalListErrorSize(0);
				importForm.setSummaryReturnWacoalListSuccessSize(0);
				
				importForm.setShoppingListErrorSize(0);
				importForm.setShoppingListSuccessSize(0);
				
				importForm.setSummaryKingErrorSize(0);
				importForm.setSummaryKingSuccessSize(0);
				
				importForm.setLoadStockInitListErrorSize(0);
				importForm.setLoadStockInitListSuccessSize(0);
				
				importForm.setLoadOnhandTVDirectListErrorSize(0);
				importForm.setLoadOnhandTVDirectListSuccessSize(0);
				
				importForm.setTotalQty("");
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.toString());
		}finally{
			//conn.close();
		}
		return returnText;
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Interfaces Search Current Action");
		ImportForm importForm = (ImportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String returnText = "search";
		ImportDAO dao = new ImportDAO();
		try {

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return returnText;
	}
	
	/**
	 * Import To DB
	 */
	public ActionForward importExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Import :page:"+Utils.isNull(request.getParameter("page")));
		ImportForm importForm = (ImportForm) form;
		importForm.setImported(true);
		User user = (User) request.getSession().getAttribute("user");
		ImportProcess process = new ImportProcess();
		
        if("lotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	return process.importFromLotus(mapping, importForm, request, response);
        }else  if("tops".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
        	return process.importFromTops(mapping, importForm, request, response);
        }else  if("king".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
        	return process.importFromKing(mapping, importForm, request, response);
        	
        }else if("bigc".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
            return process.importFromBigc(mapping, importForm, request, response);	
            
        }else if("master".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
            return process.importMaster(mapping, importForm, request, response);
            
        }else if("physical".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
            return process.importPhysical(mapping, importForm, request, response);  
            
        }else if("return_wacoal".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
            return process.importReturnWacoal(mapping, importForm, request, response);    
            
        }else if("onhandLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	return process.importFromWacoal(mapping, importForm, request, response);
        	
        }else if("onhandFriday".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	return process.importFridayFromWacoal(mapping, importForm, request, response);
        	
        }else if("onhandTVDirect".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	return process.importFridayFromTVDirect(mapping, importForm, request, response);
        	
        }else if("onhand7Catalog".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	return process.import7CatalogFromWacoal(mapping, importForm, request, response);
        	
        }else if("onhandOShopping".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	return process.importOShoppingFromWacoal(mapping, importForm, request, response);
        	
        }else if("LoadStockInitLotus".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
        	return ImportLoadStockInitProcess.importProcess(mapping, importForm, request, response,"Lotus");
        	
        }else if("LoadStockInitBigC".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
        	return ImportLoadStockInitProcess.importProcess(mapping, importForm, request, response,"BigC");
        	
        }else if("LoadStockInitMTT".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
        	return ImportLoadStockInitProcess.importProcess(mapping, importForm, request, response,"MTT");
        	
         }else if("filePosBME".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
        	return FilePosBMEProcess.importFilePostBME(mapping, importForm, request, response);
        
         }else if("reconcile".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	 importForm.setImported(false);
        	 ReconcileProcess pro = new ReconcileProcess();
        	return pro.run(mapping, importForm, request, response);
        	
        }else if("ftp_file_scan_barcode".equalsIgnoreCase(Utils.isNull(request.getParameter("page")))){
        	
        	/*EnvProperties env = EnvProperties.getInstance();
        	FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
        	String path = env.getProperty("path.transaction.sales.out");
        	
        	List<FTPFileBean> ftpFileBeanList = ftpManager.downloadFileFromFTP(user, path);
        	
        	boolean result = importFileScanBarcode(user, ftpFileBeanList);
        	if(result){
        		//move file to SaleOut Result
        		
        	}*/
        	
        	return mapping.findForward("success");
        }else{
        	return null;
        }
	}
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		ImportForm importForm = (ImportForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		ImportExport export = new ImportExport();
		try {
			logger.debug("PageAction:"+request.getParameter("page"));
			
			/** Onhand **/
			if("reconcile".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="Reconcile.xls";
				htmlTable = export.genReconcileHTML(request,importForm);
			}else if("filePosBME".equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
				fileName ="Pos_BME.xls";
				htmlTable = FilePosBMEProcess.genExcelHTML(request,importForm);
			}
			 //logger.debug("fileName:"+fileName);
	        //fileName = Utils.toUnicodeChar(fileName);
	        //logger.debug("fileName:"+fileName);
	        //"data.xls";
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("success");
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ImportForm tripForm = (ImportForm) form;
		try {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			request.setAttribute("type", SystemElements.ADMIN);
			request.setAttribute("searchKey", tripForm.getCriteria().getSearchKey());
			
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "re-search";
	}

	
	@Override
	protected void setNewCriteria(ActionForm form) {
		ImportForm tripForm = (ImportForm) form;
		tripForm.setCriteria(new ImportCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
