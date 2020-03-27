package com.isecinc.pens.web.reportall.page;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.StoreDAO;
import com.isecinc.pens.dao.SummaryDAO;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.isecinc.pens.web.batchtask.BatchTaskDAO;
import com.isecinc.pens.web.batchtask.BatchTaskForm;
import com.isecinc.pens.web.batchtask.task.ExportReportOnhandLotusTask;
import com.isecinc.pens.web.importall.ImportAllForm;
import com.isecinc.pens.web.reportall.ReportAllBean;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.sql.ReportOnhandAsOfRobinsonSQL;
import com.isecinc.pens.web.reportall.sql.ReportOnhandLotusSQL;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ReportOnhandAsOfRobinsonAction extends I_Action {

	
	/**
	 * Prepare without ID
	 */
	public String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "reportAll";
		ReportAllForm reportAllForm = (ReportAllForm) form;
		ReportAllBean bean = new ReportAllBean();
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 request.getSession().removeAttribute("BATCH_TASK_RESULT");
				 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
				 reportAllForm.setResults(null);
				
				 bean = new ReportAllBean();
				 bean.setDispHaveQty("true");
				 
				//for test
				 //bean.setSalesDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				// bean.setPensCustCodeFrom("100002-01,100002-02");
				 
				 /** get parameter from Gen AutoOrder Page **/
				 if( !Utils.isNull(request.getParameter("storeCode")).equals("")){
					 bean.setPensCustCodeFrom(Utils.isNull(request.getParameter("storeCode")));
					 bean.setPensCustNameFrom(GeneralDAO.getStoreName(bean.getPensCustCodeFrom()));
				 }
				 if( !Utils.isNull(request.getParameter("orderDate")).equals("")){
					 bean.setSalesDate(Utils.isNull(request.getParameter("orderDate")));
				 }
				 bean.setCurrentDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				 
				 reportAllForm.setBean(bean);
				 reportAllForm.setPageName(Utils.isNull(request.getParameter("pageName")));
				 
			 }
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
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "reports";
	}

	/**
	 * Search
	 */
	public String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReportAllForm aForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		//prepare parameter
		ReportAllBean c = aForm.getBean();
		List<StoreBean> storeList = null;
		boolean pass = true;
		try {
			logger.debug("Search page["+Utils.isNull(aForm.getPageName())+"]");

			 /** Case StoreCode =ALL Export To Excel  OR storeCode >1**/
			if(Utils.isNull(aForm.getBean().getPensCustCodeFrom()).equals("ALL")
			    || Utils.isNull(aForm.getBean().getPensCustCodeFrom()).split("\\,").length >1
			    //for test (1001,1002)
			  ){  
				logger.info("Export All Store To Excel ");
				//Submit Run Batch
				//Prepare Parameter to BatchTask
				Map<String, String> batchParaMap = new HashMap<String, String>();
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_AS_OF_DATE, aForm.getBean().getSalesDate());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_STORE_CODE, aForm.getBean().getPensCustCodeFrom());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_PENS_ITEM_FROM, aForm.getBean().getPensItemFrom());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_PENS_ITEM_TO, aForm.getBean().getPensItemTo());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_GROUP, aForm.getBean().getGroup());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_SUMMARY_TYPE, aForm.getBean().getSummaryType());
				
				//logger.debug("storeCode:"+aForm.getBean().getPensCustCodeFrom());
				//logger.debug("asOfDate:"+aForm.getBean().getSalesDate());
				
				request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
				request.setAttribute("BATCH_TASK_NAME",BatchTaskConstants.EXPORT_REPORT_ONHAND_ROBINSON);//set to popup page to BatchTask
			}else{
				 String queryStr= request.getQueryString();
				 if(queryStr.indexOf("d-") != -1){
				 	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
				 	System.out.println("queryStr:"+queryStr);
				 }
				 
				//Case link page in display no search again
				logger.debug("currentPage:"+request.getParameter(queryStr));
				if(request.getParameter(queryStr) != null){
					//logger.debug("results:"+summaryForm.getResults());
					ImportDAO importDAO = new ImportDAO();
					Master m = importDAO.getStoreName("Store", aForm.getBean().getPensCustCodeFrom());
					if(m != null)
					   aForm.getBean().setPensCustNameFrom(m.getPensDesc());
				}else{
					//set for display by page
					request.getSession().setAttribute("summary" ,null);
					request.getSession().setAttribute("BATCH_TASK_RESULT",null);//Clear BatchTask Result
	
					//Init Connection
					conn = DBConnection.getInstance().getConnection();
					
					//One StoreCode
					//Get By Store
					storeList =  new ArrayList<StoreBean>();
					StoreBean storeBean = new StoreBean();
					storeBean.setStoreCode(c.getPensCustCodeFrom());
					storeList.add(storeBean);
					
					//Validate Initial Date
					Date asOfDate = DateUtil.parse(c.getSalesDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					Date initDate = new SummaryDAO().searchInitDateMTT(conn,c.getPensCustCodeFrom());
					
					logger.debug("initDate:"+initDate);
					logger.debug("asOfDate:"+asOfDate);

					if(initDate !=null){
						if(asOfDate.before(initDate)){
							aForm.setResults(null);
							request.setAttribute("Message", "วันที่ as of ต้องมากกว่าเท่ากับวันที่นับสต๊อกตั้งต้น");
							pass = false;
						}
					}
					if(pass){
						List<ReportAllBean> results = null;
						ReportAllBean summary = searchOnhandAsOfRobinson(conn,aForm.getBean(),initDate,user,aForm.getBean().getSummaryType());
						results = summary != null?summary.getItemsList():null;
						
						if (results != null  && results.size() >0) {
							request.getSession().setAttribute("summary" ,summary.getSummary());
							aForm.setResults(results);
							aForm.getBean().setInitDate(DateUtil.stringValue(initDate,DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
							
							ImportDAO importDAO = new ImportDAO();
							Master m = importDAO.getStoreName("Store", aForm.getBean().getPensCustCodeFrom());
							if(m != null)
								aForm.getBean().setPensCustNameFrom(m.getPensDesc());
														
						}else {
							aForm.setResults(null);
							request.setAttribute("Message", "ไม่พบข่อมูล");
						}
					}//if	
				}//if
			}//if 
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "reportAll";
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		ReportAllForm aForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		try {
			logger.debug("PageAction:"+aForm.getPageName());
			if(Utils.isNull(aForm.getBean().getPensCustCodeFrom()).equals("ALL")
				|| Utils.isNull(aForm.getBean().getPensCustCodeFrom()).split("\\,").length >1
					//|| "020047-1".equals(aForm.getBean().getPensCustCodeFrom()) //for test
			  ){
				logger.info("Export All Store To Excel ");
				//Submit Run Batch
				//Prepare Parameter to BatchTask
				Map<String, String> batchParaMap = new HashMap<String, String>();
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_AS_OF_DATE, aForm.getBean().getSalesDate());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_STORE_CODE, aForm.getBean().getPensCustCodeFrom());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_PENS_ITEM_FROM, aForm.getBean().getPensItemFrom());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_PENS_ITEM_TO, aForm.getBean().getPensItemTo());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_GROUP, aForm.getBean().getGroup());
				batchParaMap.put(ExportReportOnhandLotusTask.PARAM_SUMMARY_TYPE, aForm.getBean().getSummaryType());
				
				logger.debug("storeCode:"+aForm.getBean().getPensCustCodeFrom());
				logger.debug("asOfDate:"+aForm.getBean().getSalesDate());
				
				request.getSession().setAttribute("BATCH_PARAM_MAP",batchParaMap);
				request.setAttribute("BATCH_TASK_NAME",BatchTaskConstants.EXPORT_REPORT_ONHAND_ROBINSON);//set to popup page to BatchTask
			}else{
				 fileName ="Stock_Onhand_Robins_AsOf_"+aForm.getBean().getSummaryType()+".xls";
			    
			    if(aForm.getResults() != null && aForm.getResults().size() > 0){
			    	ReportAllBean summary = (ReportAllBean)request.getSession().getAttribute("summary");
					htmlTable = genOnhandRobinsonHTML(request,aForm,user,aForm.getResults(),summary);	
					
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
					request.setAttribute("Message", "ไม่พบข้อมูล");
				 }
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
	
	
	/** For batch popup after Task success**/
	/** For display BatchTask Result **/
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		ReportAllForm aForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("searchBatch :pageName["+pageName+"]");
	         aForm.setResults(null);
	         
			 BatchTaskForm batchTaskForm = (BatchTaskForm)request.getSession().getAttribute("batchTaskForm");
			 logger.debug("batchTaskForm result size:"+batchTaskForm.getResults().length);
			 
			 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
			 
			 logger.debug("batchName:"+batchTaskForm.getResults()[0].getName());
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{	
		}
		return mapping.findForward("reportAll");
	}
	public ActionForward downloadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ReportAllForm aForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String fileName = ""; 
		String pathFile = "";
		EnvProperties env = EnvProperties.getInstance();
		try {
			logger.debug("downloadFile :fileName["+fileName+"]");

			 pathFile = FileUtil.getRootPathTemp(env);
	    	 fileName = Utils.isNull(request.getParameter("fileName"));
	    	 if( !Utils.isNull(fileName).equals("")){
	    		 pathFile +=fileName; 
		    	 logger.debug("pathFile:"+pathFile);
		    	  
	    		//read file from temp file
				 byte[] bytes = FileUtil.readFileToByte(new FileInputStream(pathFile));
				 
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				response.setContentType("application/excel");
				
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.write(bytes, 0, bytes.length);
				servletOutputStream.flush();
				servletOutputStream.close();
	    	 }
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			
		}
		return mapping.findForward("reportAll");
	}
	

	public static ReportAllBean searchOnhandAsOfRobinson(Connection conn,ReportAllBean c,Date initDate,User user,String summaryType) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			List<ReportAllBean> pos = new ArrayList<ReportAllBean>();
			StringBuffer sql = new StringBuffer();
			double initSaleQty = 0;
		    double saleInQtyTemp = 0;
		    double saleReturnQtyTemp = 0;
		    double saleOutQtyTemp = 0;
		    double adjustQtyTemp = 0;
		    double stockShortQtyTemp = 0;
		    double onhandQtyTemp = 0;
			try {
				sql = ReportOnhandAsOfRobinsonSQL.genSQL(conn, c, initDate,summaryType);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					ReportAllBean item = new ReportAllBean();
					
					item.setStoreCode(rst.getString("customer_code"));
					item.setCustNo(rst.getString("cust_no"));
					item.setStoreName(rst.getString("customer_desc"));
					if(!"GroupCode".equalsIgnoreCase(summaryType)){	
					  item.setPensItem(rst.getString("pens_item"));
					}
					item.setGroup(rst.getString("group_type"));
					item.setInitSaleQty(Utils.decimalFormat(rst.getDouble("init_sale_qty"),Utils.format_current_no_disgit));
					item.setSaleInQty(Utils.decimalFormat(rst.getDouble("sale_in_qty"),Utils.format_current_no_disgit));
					item.setSaleReturnQty(Utils.decimalFormat(rst.getDouble("sale_return_qty"),Utils.format_current_no_disgit));
					item.setSaleOutQty(Utils.decimalFormat(rst.getDouble("sale_out_qty"),Utils.format_current_no_disgit));
					item.setAdjustQty(Utils.decimalFormat(rst.getDouble("adjust_qty"),Utils.format_current_no_disgit));
					item.setStockShortQty(Utils.decimalFormat(rst.getDouble("STOCK_SHORT_QTY"),Utils.format_current_no_disgit));
					item.setOnhandQty(Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit));
					
					initSaleQty += Utils.convertStrToDouble(item.getInitSaleQty());
					saleInQtyTemp += Utils.convertStrToDouble(item.getSaleInQty());
					saleReturnQtyTemp +=Utils.convertStrToDouble(item.getSaleReturnQty());
					saleOutQtyTemp +=Utils.convertStrToDouble(item.getSaleOutQty());
					adjustQtyTemp += rst.getDouble("adjust_qty");
					stockShortQtyTemp += rst.getDouble("STOCK_SHORT_QTY");
					onhandQtyTemp +=Utils.convertStrToDouble(item.getOnhandQty());
				
					pos.add(item);
				}//while
				
				//Summary
				ReportAllBean item = new ReportAllBean();
				item.setStoreCode("");
				if("PensItem".equalsIgnoreCase(summaryType)){
				   item.setPensItem("");
				}
				item.setGroup("");
				item.setInitSaleQty(Utils.decimalFormat(initSaleQty,Utils.format_current_no_disgit));
				item.setSaleInQty(Utils.decimalFormat(saleInQtyTemp,Utils.format_current_no_disgit));
				item.setSaleReturnQty(Utils.decimalFormat(saleReturnQtyTemp,Utils.format_current_no_disgit));
				item.setSaleOutQty(Utils.decimalFormat(saleOutQtyTemp,Utils.format_current_no_disgit));
				item.setAdjustQty(Utils.decimalFormat(adjustQtyTemp,Utils.format_current_no_disgit));
				item.setStockShortQty(Utils.decimalFormat(stockShortQtyTemp,Utils.format_current_no_disgit));
				item.setOnhandQty(Utils.decimalFormat(onhandQtyTemp,Utils.format_current_no_disgit));
				
				c.setSummary(item);
				c.setItemsList(pos);
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return c;
	    }
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//Connection conn = null;
		//SummaryForm summaryForm = (SummaryForm) form;
		try {
			 
		} catch (Exception e) {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		try {
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("reportAll");
	}
	public ActionForward searchBatchForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatchForm");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			reportAllForm.setResults(null);
	        String batchTaskName = Utils.isNull(request.getParameter("batchTaskName"));
			BatchTaskForm batchTaskForm = new BatchTaskDAO().searchBatchLastRun(user, batchTaskName);
			if(batchTaskForm.getResults() != null && batchTaskForm.getResults().length >0){
				 logger.debug("batchTaskForm result size:"+batchTaskForm.getResults().length);
				 
				 request.getSession().setAttribute("BATCH_TASK_RESULT",batchTaskForm);
				 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
				 
				 logger.debug("batchName:"+batchTaskForm.getResults()[0].getName());
			 }else{
				 request.setAttribute("Message", "ไม่พบข้อมูล");
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{	
		}
		return mapping.findForward("reportAll");
	}
	public ActionForward clearBatchForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clearBatchForm");
		ReportAllForm reportAllForm = (ReportAllForm) form;
		try {
			 request.getSession().removeAttribute("BATCH_TASK_RESULT");
			 request.getSession().removeAttribute("batchTaskForm");//clear session BatchTaskForm
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{	
		}
		return mapping.findForward("reportAll");
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
	
	public StringBuffer genOnhandRobinsonHTML(HttpServletRequest request,ReportAllForm aForm,User user
			,List<ReportAllBean> list,ReportAllBean summary){
		StringBuffer h = new StringBuffer("");
		String page = aForm.getPageName();
		String colspan ="10";
		String bStart = "";
		String bEnd = "";
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			if("GroupCode".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				colspan ="11";
			}
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"'>รายงาน B'me Stock on-hand at Lotus(As Of)</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >จากวันที่ขาย:"+aForm.getBean().getSalesDate()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >รหัสร้านค้า:"+aForm.getBean().getPensCustCodeFrom()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Pens Item From:"+aForm.getBean().getPensItemFrom()+"  Pens Item To:"+aForm.getBean().getPensItemTo()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colspan+"' >Group:"+aForm.getBean().getGroup()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

			if(list != null){
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				  h.append("<th>รหัสร้านค้า</th> \n");
				  h.append("<th>ชื่อร้านค้า</th> \n");
				  if("PensItem".equalsIgnoreCase(aForm.getBean().getSummaryType())){
				    h.append("<th>PensItem</th> \n");
				  }
				  h.append("<th>Group</th> \n");
				  h.append("<th>Sale In Qty</th> \n");
				  h.append("<th>Sale Return Qty </th> \n");
				  h.append("<th>Sales Out Qty </th> \n");
				  h.append("<th>Adjust </th> \n");
				  h.append("<th>Stock short </th> \n");
				  h.append("<th>Onhand Qty </th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					ReportAllBean s = (ReportAllBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getStoreCode()+"</td> \n");
					  h.append("<td class='text'>"+s.getStoreName()+"</td> \n");
					  if("PensItem".equalsIgnoreCase(aForm.getBean().getSummaryType())){
					     h.append("<td class='text'>"+s.getPensItem()+"</td> \n");
					  }
					  h.append("<td class='text'>"+s.getGroup()+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleInQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleReturnQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getSaleOutQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getAdjustQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getStockShortQty()+bEnd+"</td> \n");
					  h.append("<td class='num_currency'>"+bStart+s.getOnhandQty()+bEnd+"</td> \n");
					h.append("</tr>");
					
					logger.debug("onhandQty:"+s.getOnhandQty());
				}
				/** Summary **/
				bStart ="<b>";
				bEnd ="</b>";
				
				h.append("<tr> \n");
				 
				  if("PensItem".equalsIgnoreCase(aForm.getBean().getSummaryType())){
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }else{
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;</td> \n");
					  h.append("<td>&nbsp;<b>รวม</b></td> \n");
				  }
				  h.append("<td class='num_currency_bold'>"+bStart+summary.getSaleInQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+summary.getSaleReturnQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+summary.getSaleOutQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+summary.getAdjustQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+summary.getStockShortQty()+bEnd+"</td> \n");
				  h.append("<td class='num_currency_bold'>"+bStart+summary.getOnhandQty()+bEnd+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
}
