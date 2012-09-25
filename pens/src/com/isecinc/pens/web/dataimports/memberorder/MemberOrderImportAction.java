package com.isecinc.pens.web.dataimports.memberorder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;

import util.DBCPConnectionProvider;
import util.UploadXLSUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.process.dataimports.MemberOrderImportProcess;

/**
 * Member Import Action
 * 
 * @author atiz.b
 * @version $Id: MemberOrderImportAction.java,v 1.0 13/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MemberOrderImportAction extends DispatchAction {

	/** Logger */
	protected Logger logger = Logger.getLogger("PENS");

	/**
	 * Prepare
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Prepare " + this.getClass().toString());
		saveToken(request);
		return mapping.findForward("import");
	}

	/**
	 * Import Member Order
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward importMemberOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Import Member Order " + this.getClass().toString());
		Connection conn = null;
		try {
			// check Token
			if (!isTokenValid(request)) { return mapping.findForward("import"); }
			User user = (User) request.getSession().getAttribute("user");
			MemberOrderImportForm orderImportForm = (MemberOrderImportForm) form;
			logger.debug("Order Date " + orderImportForm.getOrderDate());
			logger.debug("Members " + orderImportForm.getMemberIds());
			logger.debug("Imported File " + orderImportForm.getImportFile());
			// validate imported file
			FormFile dataFile = orderImportForm.getImportFile();
			List<Member> members = new ArrayList<Member>();
			if (dataFile != null) {
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());
				String[] names = dataFile.getFileName().split("\\.");
				if (names[names.length - 1].startsWith("xls")) {
					// Read XLS at 1st Column to get Member Code
					int sheetNo = 0; // xls sheet no. or name
					int rowNo = 0; // row of begin data
					int maxRowNo = 0; // row of end data is optional, default is 0 depend on keyColumn
					int maxColumnNo = 1; // max column of data per row
					int keyColumn = 0; // must be not null column
					UploadXLSUtil uploadXLSUtil = new UploadXLSUtil();
					ArrayList memberCodes = uploadXLSUtil.loadXls(dataFile, sheetNo, rowNo, maxRowNo, maxColumnNo,
							keyColumn);
					Member m;
					for (Object rows : (ArrayList) memberCodes) {
						for (Object column : (ArrayList) rows) {
							logger.debug("columnVal: " + column.toString());
							m = new MMember().findByCode(String.valueOf(new Double(column.toString()).intValue()));
							if (m == null) {
								request.setAttribute("Message", SystemMessages.getCaption("NoMemberCodeFor",
										new Locale("th", "TH"))
										+ new Double(column.toString()).intValue());
								return mapping.findForward("import");
							}
							members.add(m);
						}
					}
				} else {
					request.setAttribute("Message", SystemMessages.getCaption("InvalidFileContent", new Locale("th",
							"TH")));
					return mapping.findForward("import");
				}
			}

			request.setAttribute("memberIds", orderImportForm.getMemberIds());
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			List<Order> orderCreated = new ArrayList<Order>();
			String[] memberIds;
			// Create Order From Imported File
			memberIds = new String[members.size()];
			int i = 0;
			for (Member m : members) {
				memberIds[i] = String.valueOf(m.getId());
				i++;
			}
			if (memberIds.length != 0) {
				orderCreated.addAll(new MemberOrderImportProcess().createOrder(memberIds, orderImportForm
						.getOrderDate(), user, conn));
			}
			// Create Order From Member Ids
			memberIds = orderImportForm.getMemberIds();
			if (memberIds != null) {
				orderCreated.addAll(new MemberOrderImportProcess().createOrder(memberIds, orderImportForm
						.getOrderDate(), user, conn));
			}
			orderImportForm.setOrders(orderCreated);
			if (orderCreated.size() == 0) {
				request.setAttribute("Message", SystemMessages.getCaption("NothingToImport", new Locale("th", "TH")));
				return mapping.findForward("import");
			}
			orderImportForm.setTotalOrder(orderCreated.size());
			conn.commit();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			// save token
			saveToken(request);
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {}
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
			return mapping.findForward("import");
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward("importOrder");
	}
}
