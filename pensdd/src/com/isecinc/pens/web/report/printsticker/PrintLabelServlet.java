package com.isecinc.pens.web.report.printsticker;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import util.BeanParameter;
import util.DBCPConnectionProvider;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.printsticker.PrintLabel;
import com.isecinc.pens.report.printsticker.PrintLabelProcess;

/**
 * PrintSticker Servlet
 * 
 * @author Wit:
 * @version :
 * 
 */
public class PrintLabelServlet extends HttpServlet {

	private static final long serialVersionUID = 5013476314777319481L;

	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		performTask(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		performTask(req, resp);
	}

	public void performTask(HttpServletRequest req, HttpServletResponse resp) {
		logger.debug("print Label...");
		PrintLabelProcess process = new PrintLabelProcess();
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		List<PrintLabel> lstData = null;
		Connection conn = null;
		String memberCodeFrom = "";
		String memberCodeTo = "";
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			memberCodeFrom = Utils.isNull(req.getParameter("memberCodeFrom"));
			memberCodeTo = Utils.isNull(req.getParameter("memberCodeTo"));
			
			lstData = process.doReport(conn, memberCodeFrom,memberCodeTo);
			
			String fileJasper = BeanParameter.getReportPath() + "printLabel";
			logger.debug("Report Path : " + fileJasper);
			if (lstData != null && lstData.size() > 0) {
				reportServlet.runReport(req, resp, conn, fileJasper, SystemElements.PDF, parameterMap, "printLabel", lstData);
			} else {
				req.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			logger.debug(e);
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
	
}
