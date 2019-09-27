package com.isecinc.pens.web.b2b;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.salestarget.SalesTargetBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class B2BControlPage {
	protected static Logger logger = Logger.getLogger("PENS");
	

	public static void prepareB2BMakro(HttpServletRequest request,Connection conn,User user,String pageName){
		PopupBean item = null;
		List<PopupBean> dataList = null;
		try{
			//Type file List
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();item.setKeyName("");item.setValue("");
			dataList.add(item);
			item = new PopupBean();item.setKeyName("B2B_ITEM");item.setValue("B2B_ITEM");
			dataList.add(item);
			item = new PopupBean();item.setKeyName("Sales_By_Item");item.setValue("Sales_By_Item");
			dataList.add(item);
			request.getSession().setAttribute("TYPE_FILE_LIST",dataList);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	
}
