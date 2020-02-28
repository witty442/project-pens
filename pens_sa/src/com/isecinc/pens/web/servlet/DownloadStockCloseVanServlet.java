package com.isecinc.pens.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.report.salesanalyst.helper.EnvProperties;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockCloseVanBean;
import com.isecinc.pens.web.stock.StockCloseVanReport;
import com.pens.util.BeanParameter;
import com.pens.util.DBConnection;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;

@SuppressWarnings( { "static-access", "serial", "deprecation" })
public class DownloadStockCloseVanServlet extends HttpServlet {

	Logger logger = Logger.getLogger("PENS");
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
        Connection conn = null;
	    try {
	    	User user = (User) request.getSession().getAttribute("user");
	    	
	    	//pdf
        	conn = DBConnection.getInstance().getConnectionApps();
        	ReportUtilServlet reportServlet = new ReportUtilServlet();
			HashMap<String,Object> parameterMap = new HashMap<String,Object>();
			ServletContext context = request.getSession().getServletContext();
			List<StockCloseVanBean> lstData = null;
	
        	String fileName = "stock_close_report";
            String fileJasper =  BeanParameter.getReportPath() + fileName;
            
            //get criteria
            StockBean criBean = new StockBean();
            criBean.setPeriod(Utils.isNull(request.getParameter("period")));
            
            //get data
            StockCloseVanBean beanReport = StockCloseVanReport.searchStockCloseVanList(conn,criBean,user);
            if(beanReport != null){
            	lstData = beanReport.getItemsList();
            }
            //set parameterMap
			parameterMap.put("period",criBean.getPeriod());
			
			//summary map
			parameterMap.put("begin_pri_qty",beanReport.getBegin_pri_qty());
			parameterMap.put("begin_sec_char",beanReport.getBegin_sec_char());
			parameterMap.put("sale_pri_qty",beanReport.getSale_pri_qty());
			parameterMap.put("sale_sec_char",beanReport.getSale_sec_char());
			parameterMap.put("prem_pri_qty",beanReport.getPrem_pri_qty());
			parameterMap.put("prem_sec_char",beanReport.getPrem_sec_char());
			parameterMap.put("total_sale_pri_qty",beanReport.getTotal_sale_pri_qty());
			parameterMap.put("total_sale_sec_char",beanReport.getTotal_sale_sec_char());
			parameterMap.put("rma_pri_qty",beanReport.getRma_pri_qty());
			parameterMap.put("rma_sec_char",beanReport.getRma_sec_char());
			parameterMap.put("receipt_pri_qty",beanReport.getReceipt_pri_qty());
			parameterMap.put("receipt_sec_char",beanReport.getReceipt_sec_char());
			parameterMap.put("return_pri_qty",beanReport.getReturn_pri_qty());
			parameterMap.put("return_sec_char",beanReport.getReturn_sec_char());
			parameterMap.put("adj_issue_pri_qty",beanReport.getAdj_issue_pri_qty());
			parameterMap.put("adj_issue_sec_char",beanReport.getAdj_issue_sec_char());
			parameterMap.put("adj_receipt_pri_qty",beanReport.getAdj_receipt_pri_qty());
			parameterMap.put("adj_receipt_sec_char",beanReport.getAdj_receipt_sec_char());
			parameterMap.put("end_pri_qty",beanReport.getEnd_pri_qty());
			parameterMap.put("end_sec_char",beanReport.getEnd_sec_char());
		
			//Get SalesRepName
			SalesrepBean salesrepBean =  SalesrepDAO.getSalesrepBeanByCode(conn, user.getUserName());
			if(salesrepBean != null){
			   parameterMap.put("salesFullName",user.getUserName()+"-"+salesrepBean.getSalesrepFullName());
			}else{
			   parameterMap.put("salesFullName","");
			}
			String fileNameExport = "stock_close_van.pdf";
			if(lstData != null && lstData.size() >0){
			    reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName, lstData,fileNameExport);
			}else{
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}
	    } catch (Exception e) {
	        e.printStackTrace();
	        logger.error(e.getMessage(),e);
	    }finally{
	    
	    }
	}
	
	
}
