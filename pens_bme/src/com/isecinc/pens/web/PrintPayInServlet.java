package com.isecinc.pens.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.PayBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.PayDAO;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.init.InitialParameter;
import com.isecinc.pens.model.MUser;
import com.isecinc.pens.web.pay.PayForm;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;


public class PrintPayInServlet extends HttpServlet {
	private static final long serialVersionUID = 7711018056252168383L;

	private Logger logger = Logger.getLogger("PENS");

	public void init() throws ServletException {
		logger.debug("Initial PENS...");
		Connection conn = null;
		try {
			//logger.info("Init ");
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(conn != null){
					conn.close();
				}
			} catch (Exception e) {}
		}
	}
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		logger.debug("DoPost ");
		try{
			String docNo = Utils.isNull(request.getParameter("docNo"));
			String userName = Utils.isNull(request.getParameter("userName"));
			User user = new MUser().findByUserName(userName);
			
			//Choose Printer By User Case Printer default offline
			String printerName = Utils.isNull(request.getParameter("printerName"));
			if( !Utils.isNull(printerName).equals("")){
				logger.info("Printer Choose by user:"+printerName);
				user.setPrinterName(printerName);
			}
			
			request.getSession().setAttribute("user", user);
			
			logger.info("PrintPay User["+user.getUserName()+"]docNo["+docNo+"]printer["+user.getPrinterName()+"]");
			printReport(request, response);
			//logger.info(" PrintPay User["+user.getUserName()+"]docNo["+docNo+"]printer["+user.getPrinterName()+"]");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		logger.debug("DoGet ");
		printReport(request, response);
	}
	
	public void printReport( HttpServletRequest request,HttpServletResponse response) {
		
		logger.debug("Search for report ");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null; 
		try {
			conn = DBConnection.getInstance().getConnection();
			String fileType = SystemElements.PRINTER;
			
			//Search Again
			String docNo = Utils.isNull(request.getParameter("docNo"));
			PayBean cri = new PayBean();
			cri.setDocNo(docNo);
			
			//PayBean h = PayDAO.searchHead(conn,cri,true).getItems().get(0);
			 PayBean h = PayDAO.searchHead(conn,cri,true,false,1,30).getItems().get(0);
			logger.debug("result:"+h.getDocNo());
			
			if(h != null){
				//Head
				if("CH".equalsIgnoreCase(h.getPaymethod())){
				   parameterMap.put("cheque", h.getPaymethod());
				}else{
				   parameterMap.put("cash", h.getPaymethod());
				}
				// 22/10/2558
			    String dd = h.getDocDate().substring(0,2);
			    String MM = h.getDocDate().substring(3,5);
			    String yyyy = h.getDocDate().substring(6,10);
			    
				parameterMap.put("docDateDD", Utils.isNull(dd));
				parameterMap.put("docDateMM", Utils.isNull(MM));
				parameterMap.put("docDateYYYY", Utils.isNull(yyyy));
				
				parameterMap.put("payInName", Utils.isNull(h.getPayToName()));
				parameterMap.put("deptName", Utils.isNull(h.getDeptName()));
				parameterMap.put("sectionName", Utils.isNull(h.getSectionName()));
				parameterMap.put("totalAmountLetter", Utils.isNull(h.getTotalAmountLetter()));
				parameterMap.put("createUser", Utils.isNull(h.getCreateUser()));
				
				logger.debug("totalAmountLetter["+Utils.isNull(h.getTotalAmountLetter())+"]");
				
				String[] aa = Utils.isNull(h.getTotalAmount()).split("\\.");
				parameterMap.put("totalAmount", Utils.isNull(aa[0]));
				if(aa.length > 1){
					parameterMap.put("totalAmount2Digit", Utils.isNull(aa[1]));
				}else{
					parameterMap.put("totalAmount2Digit", "");
				}
				
				//*********************DR***************************************//
				parameterMap.put("DR_AC_NO", Utils.isNull(h.getDR_AC_NO()));
				parameterMap.put("DR_DESC", Utils.isNull(h.getDR_DESC()));
				
				String[] temp = Utils.isNull(h.getDR_AMOUNT()).split("\\.");
				parameterMap.put("DR_AMOUNT", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("DR_AMOUNT_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("DR_AMOUNT_DIGIT", "");
				}
				
				temp = Utils.isNull(h.getDR_INPUT_TAX_AMOUNT()).split("\\.");
				parameterMap.put("DR_INPUT_TAX_AMOUNT", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("DR_INPUT_TAX_AMOUNT_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("DR_INPUT_TAX_AMOUNT_DIGIT", "");
				}
				temp = Utils.isNull(h.getDR_TOTAL()).split("\\.");
				parameterMap.put("DR_TOTAL", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("DR_TOTAL_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("DR_TOTAL_DIGIT", "");
				}
				//********************CR*************************************//
				parameterMap.put("CR_AC_NO", Utils.isNull(h.getCR_AC_NO()));
				parameterMap.put("CR_DESC", Utils.isNull(h.getCR_DESC()));
				
				temp = Utils.isNull(h.getCR_AMOUNT()).split("\\.");
				parameterMap.put("CR_AMOUNT", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("CR_AMOUNT_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("CR_AMOUNT_DIGIT", "");
				}
				
				temp = Utils.isNull(h.getCR_ACC_WT_TAX_AMOUNT()).split("\\.");
				parameterMap.put("CR_ACC_WT_TAX_AMOUNT", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("CR_ACC_WT_TAX_AMOUNT_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("CR_ACC_WT_TAX_AMOUNT_DIGIT", "");
				}
				temp = Utils.isNull(h.getCR_TOTAL()).split("\\.");
				parameterMap.put("CR_TOTAL", Utils.isNull(temp[0]));
				if(temp.length > 1){
					parameterMap.put("CR_TOTAL_DIGIT", Utils.isNull(temp[1]));
				}else{
					parameterMap.put("CR_TOTAL_DIGIT", "");
				}
			
				
				//Items
				if(h.getItems() != null &&  h.getItems().size() >0){
					for(int i=0;i<h.getItems().size();i++){
						PayBean item = h.getItems().get(i);
						if(i==0){
							parameterMap.put("accountName1", Utils.isNull(item.getAccountName()));
							parameterMap.put("description1", Utils.isNull(item.getDescription()));
		
							logger.debug("amount:"+item.getAmount());
							String[] a = Utils.isNull(item.getAmount()).split("\\.");
							
							if( !Utils.isNull(a[0]).equals(""))
							    parameterMap.put("amount1", Utils.convertToCurrencyNoDigitStr(Utils.isNull(a[0])));
							
							if(a.length > 1){
								parameterMap.put("amount2Digit1", Utils.isNull(a[1]));
							}else{
								if( !Utils.isNull(a[0]).equals(""))
								   parameterMap.put("amount2Digit1", "00");
							}
						}
						if(i==1){
							parameterMap.put("accountName2", Utils.isNull(item.getAccountName()));
							parameterMap.put("description2", Utils.isNull(item.getDescription()));
		
							String[] a = Utils.isNull(item.getAmount()).split("\\.");
							
							if( !Utils.isNull(a[0]).equals(""))
							    parameterMap.put("amount2", Utils.convertToCurrencyNoDigitStr(Utils.isNull(a[0])));
							
							if(a.length > 1){
								parameterMap.put("amount2Digit2", Utils.isNull(a[1]));
							}else{
								if( !Utils.isNull(a[0]).equals(""))
								   parameterMap.put("amount2Digit2", "00");
							}
						}
						if(i==2){
							parameterMap.put("accountName3", Utils.isNull(item.getAccountName()));
							parameterMap.put("description3", Utils.isNull(item.getDescription()));
		
							String[] a = Utils.isNull(item.getAmount()).split("\\.");
							if( !Utils.isNull(a[0]).equals(""))
							    parameterMap.put("amount3", Utils.convertToCurrencyNoDigitStr(Utils.isNull(a[0])));
							if(a.length > 1){
							   parameterMap.put("amount2Digit3", Utils.isNull(a[1]));
							}else{
								if( !Utils.isNull(a[0]).equals(""))
							       parameterMap.put("amount2Digit3", "00");
							}
						}
						if(i==3){
							parameterMap.put("accountName4", Utils.isNull(item.getAccountName()));
							parameterMap.put("description4", Utils.isNull(item.getDescription()));
		
							String[] a = Utils.isNull(item.getAmount()).split("\\.");
							
							if( !Utils.isNull(a[0]).equals(""))
							    parameterMap.put("amount4", Utils.convertToCurrencyNoDigitStr(Utils.isNull(a[0])));
							if(a.length > 1){
							   parameterMap.put("amount2Digit4", Utils.isNull(a[1]));
							}else{
								if( !Utils.isNull(a[0]).equals(""))
							      parameterMap.put("amount2Digit4", "00");
							}
						}
					}
				}
				
				//Gen Report
				String fileName = "pay_in_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				logger.debug("start report");
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName,h.getItems());
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  พิมพ์รายการที่มีสถานะเป็น CLOSE เท่านั้น");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {
				
			}
		}
	}
	
	public void destroy()
	{
	// do nothing.
	}
}
