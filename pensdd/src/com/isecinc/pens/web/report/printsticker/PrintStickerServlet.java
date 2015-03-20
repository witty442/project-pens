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
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.printsticker.PrintSticker;
import com.isecinc.pens.report.printsticker.PrintStickerProcess;

/**
 * PrintSticker Servlet
 * 
 * @author Aneak.t
 * @version $Id: PrintStickerServlet.java,v 1.0 19/01/2011 15:52:00 aneak.t Exp $
 * 
 */
public class PrintStickerServlet extends HttpServlet {

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
		logger.debug("print sticker...");
		PrintStickerProcess process = new PrintStickerProcess();
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap<String, String> parameterMap = new HashMap<String, String>();
		List<PrintSticker> lstData = null;
		Connection conn = null;
		String requestDate = "";
		
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			requestDate = req.getParameter("requestDate").toString();
			if(!requestDate.equals("")){
				lstData = process.doReport(conn, requestDate);
			}
			String fileJasper = BeanParameter.getReportPath() + "printSticker";
			logger.debug("Report Path : " + fileJasper);
			if (lstData != null && lstData.size() > 0) {
				reportServlet.runReport(req, resp, conn, fileJasper, SystemElements.PDF, parameterMap, "printSticker", lstData);
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
