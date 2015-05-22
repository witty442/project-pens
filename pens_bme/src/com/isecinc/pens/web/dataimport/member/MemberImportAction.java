package com.isecinc.pens.web.dataimport.member;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import util.DateToolsUtil;
import util.UploadXLSUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.init.InitialMessages;


public class MemberImportAction extends I_Action {

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setNewCriteria(ActionForm form) throws Exception {
	// TODO Auto-generated method stub

	}

	public ActionForward memberUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		StringBuffer whereCause = null;
		String memberCode = null;
		String errorDesc = null;
		String oldMemberCode = null;
		int id = 0;
		int allCount = 0;
		int successCount = 0;
		// int errorCount = 0;
		String forward = "view";
		MemberImportForm actionForm = (MemberImportForm) form;

		try {
			request.getSession().removeAttribute("PAGINATOR");
			User user = (User) request.getSession(true).getAttribute("user");
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);

			FormFile dataFile = actionForm.getMemberFile();
			if (dataFile != null) {
					logger.debug("newImport: " + actionForm.getNewImport());
					logger.debug("contentType: " + dataFile.getContentType());
					logger.debug("fileName: " + dataFile.getFileName());
	
					int sheetNo = 0; // xls sheet no. or name
					int rowNo = 3; // row of begin data
					int maxRowNo = 0; // row of end data is optional, default is 0 depend on keyColumn
					int maxColumnNo = 60; // max column of data per row
					int keyColumn = 3; // must be not null column
	
					UploadXLSUtil uploadXLSUtil = new UploadXLSUtil();
					ArrayList datas = uploadXLSUtil.loadXls(dataFile, sheetNo, rowNo, maxRowNo, maxColumnNo, keyColumn);
	
					if (datas != null && datas.size() > 0) {
						allCount = datas.size();
						for (Object rows : (ArrayList) datas) {
							if (rows != null && ((ArrayList) rows).get(keyColumn) != null) {
								int columnNo = 0;
								for (Object column : (ArrayList) rows) {
									if (column != null && !column.toString().startsWith("java.lang.Object")) {
										logger.debug(columnNo + " : " + column);
										switch (columnNo) {
										case 0:
											// à¸§à¸±à¸?à¸?à¸µà¹?à¸ªà¸¡à¸±à¸?à¸£
											break;
										case 1:
											// à¹€à¸?à¸·à¸­à¸?à¸?à¸µà¹?à¸ªà¸¡à¸±à¸?à¸£
											break;
										case 2:
											// à¸?à¸µà¸?à¸µà¹?à¸ªà¸¡à¸±à¸?à¸£
											break;
	                                 	default:
											break;
										}
									}
	
									columnNo++;
								}//for
	
								
						datas.clear();
	
					 }
				  }
			   }
					
				actionForm.setNextYear(Integer.parseInt(DateToolsUtil.getCurrentDateTime("yyyy")) + 1);
	
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc()
						+ " à¸?à¹?à¸­à¸¡à¸¹à¸¥ " + allCount + " à¸£à¸²à¸¢à¸?à¸²à¸£, à¸ªà¸³à¹€à¸£à¹?à¸? " + successCount + " à¸£à¸²à¸¢à¸?à¸²à¸£, à¹?à¸¡à¹?à¸ªà¸³à¹€à¸£à¹?à¸? "
						+ (allCount - successCount) + " à¸£à¸²à¸¢à¸?à¸²à¸£");
	
				conn.commit();
			}
		} catch (Exception e) {
			forward = "clearform";
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ " à¸?à¹?à¸­à¸¡à¸¹à¸¥ " + allCount + " à¸£à¸²à¸¢à¸?à¸²à¸£, à¸?à¸?à¸?à¹?à¸­à¸¡à¸¹à¸¥à¸?à¸´à¸?à¸?à¸?à¸?à¸´à¸?à¸­à¸?à¸ªà¸¡à¸²à¸?à¸´à¸?à¹€à¸¥à¸?à¸?à¸µà¹? " + memberCode + " "
					+ e.toString());

			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward(forward);
	}

	
	

	private boolean save( User user, Connection conn) throws Exception {

		int nextId = 0;
		return true;
	}
}
