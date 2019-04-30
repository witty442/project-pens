package com.isecinc.pens.web.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import util.CConstants;
import util.DBConnection;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.CConstantsBean;
import com.isecinc.pens.bean.PopupBean;

public class LocationInitial extends LocationControlPage {
   
	/**
	 * WITTY
	 */
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public   static Map<String,String> MONTH_MAP = new HashMap<String,String>();
	public static final String TYPE_SEARCH_DAY = "DAY";
	public static final String TYPE_SEARCH_MONTH = "MONTH";
	private static LocationInitial salesAnalystProcess;
	
	public static LocationInitial getInstance(){
		if(salesAnalystProcess == null){
			salesAnalystProcess = new LocationInitial();
			return salesAnalystProcess;
		}
		return salesAnalystProcess;
	}

	public  void initSession(HttpServletRequest requestWeb) {
		References r = null;
		HttpSession session = requestWeb.getSession(true);
		Connection conn = null;
		int i = 0;
		try{
			logger.debug("Initail Session ");
			conn = DBConnection.getInstance().getConnectionApps();
			
			/** init TypeSearch ***/
			List<References> typeSearchList = new ArrayList<References>();
			r = new References("DAY","วัน");
			typeSearchList.add(r);
			r = new References("MONTH","เดือน");
			typeSearchList.add(r);
			session.setAttribute("typeSearchList", typeSearchList);
			
			/** DispType List **/
			List<References> dispTypeList = new ArrayList<References>();
			dispTypeList.add(new References("MAP","แสดงเป็นแผนที่"));
			dispTypeList.add(new References("DATA","แสดงเป็นข้อมูล"));
			session.setAttribute("dispTypeList", dispTypeList);
			
			/** init year **/
			List<References> yearL = initYearList(conn," DESC");
			session.setAttribute("yearList", yearL);
			
		/*	List<References> yearLSAC = initYearList(conn," ASC");
			session.setAttribute("yearListASC", yearLSAC);*/
			
			for(References year:yearL){
				String yearKey = year.getKey();
				String yearShow = year.getName();
				
				MONTH_MAP.put(yearKey+"01", "ม.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"02", "ก.พ. "+yearShow);
				MONTH_MAP.put(yearKey+"03", "มี.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"04", "เม.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"05", "พ.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"06", "มิ.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"07", "ก.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"08", "ส.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"09", "ก.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"10", "ต.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"11", "พ.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"12", "ธ.ค. "+yearShow);
			}

			//CUST_CAT_NO_LIST
			//add Blank Row
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			//dataList.add(item);
			
			item = new PopupBean();
			item.setCustCatNo("S");
			item.setCustCatDesc("Credit Sales");
			dataList.add(item);
			
			item = new PopupBean();
			item.setCustCatNo("C");
			item.setCustCatDesc("Van Sales");
			dataList.add(item);
			
			session.setAttribute("CUST_CAT_LIST",dataList);
			/********************************************************/
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesChannelList_s =searchSalesChannelListModel(conn);
			dataList.addAll(salesChannelList_s);
			session.setAttribute("SALES_CHANNEL_LIST",dataList);
			
			/********************************************************/
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = searchSalesrepListAll(conn,"","","");
			dataList.addAll(salesrepList_s);
			session.setAttribute("SALESREP_LIST",dataList);
			
			/********************************************************/
			//PROVINCE_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setProvince("");
			item.setProvinceName("");
			dataList.add(item);
			
			List<PopupBean> tempList = searchProvinceList(conn,"");
			dataList.addAll(tempList);
			session.setAttribute("PROVINCE_LIST",dataList);
            /********************************************************/
			
			/** ReportType List **/
			List<References> reportTypeList = new ArrayList<References>();
			reportTypeList.add(new References("report1","ร้านค้า , ที่อยู่ "));
			reportTypeList.add(new References("report2","ร้านค้า , ที่อยู่ , ที่อยู่ตาม Google"));
			reportTypeList.add(new References("report3","พนักงานขาย , ร้านค้า ,  ที่อยู่ "));
			reportTypeList.add(new References("report4","ภาคการขาย , พนักงานขาย , ร้านค้า ,  ที่อยู่ "));
			reportTypeList.add(new References("report5"," พนักงานขาย  , จังหวัด , อำเภอ , ร้านค้า , ที่อยู่ "));
			session.setAttribute("REPORT_TYPE_LIST", reportTypeList);
			/**********************************************************/
			/** TripType List **/
			List<References> tripTypeList = new ArrayList<References>();
			tripTypeList.add(new References("real","แสดงข้อมูลร้านค้าตามการบันทึกจริง"));
			tripTypeList.add(new References("trip"," แสดงข้อมูลร้านค้าตาม Trip "));
			tripTypeList.add(new References("NotEqualTrip"," แสดงข้อมูลร้านค้าไม่ตรงตามTrip "));
			session.setAttribute("TRIP_TYPE_LIST", tripTypeList);
			
			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			salesZoneList.add(item);

			List<PopupBean> salesZoneList_s = searchSalesZoneListModel(conn);
			salesZoneList.addAll(salesZoneList_s);
			session.setAttribute("SALES_ZONE_LIST",salesZoneList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			 	DBConnection.getInstance().closeConn(conn, null, null);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public  void initSessionMonitorSpider(HttpServletRequest requestWeb) {
		References r = null;
		HttpSession session = requestWeb.getSession(true);
		Connection conn = null;
		int i = 0;
		try{
			logger.debug("Initail Session ");
			conn = DBConnection.getInstance().getConnectionApps();

			//CUST_CAT_NO_LIST
			//add Blank Row
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			//dataList.add(item);
			
			item = new PopupBean();
			item.setCustCatNo("S");
			item.setCustCatDesc("Credit Sales");
			dataList.add(item);
			
			item = new PopupBean();
			item.setCustCatNo("C");
			item.setCustCatDesc("Van Sales");
			dataList.add(item);
			
			session.setAttribute("CUST_CAT_LIST",dataList);
			/********************************************************/
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesChannelList_s =searchSalesChannelListModel(conn);
			dataList.addAll(salesChannelList_s);
			session.setAttribute("SALES_CHANNEL_LIST",dataList);
			
			/********************************************************/
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = searchSalesrepListAll(conn,"","","");
			dataList.addAll(salesrepList_s);
			session.setAttribute("SALESREP_LIST",dataList);
			
			/********************************************************/
			//PROVINCE_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setProvince("");
			item.setProvinceName("");
			dataList.add(item);
			
			List<PopupBean> tempList = searchProvinceList(conn,"");
			dataList.addAll(tempList);
			session.setAttribute("PROVINCE_LIST",dataList);
            /********************************************************/
			/** Init AVG Distinace Valid **/
			//get constants config all by ref_code
			Map<String, CConstantsBean> constantsMap = CConstants.getConstantsList(conn, CConstants.SPIDER_REF_CODE);
			session.setAttribute("CONSTANTS_MAP",constantsMap);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			 	DBConnection.getInstance().closeConn(conn, null, null);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public  void initSessionTrip(HttpServletRequest requestWeb) {
		References r = null;
		HttpSession session = requestWeb.getSession(true);
		Connection conn = null;
		int i = 0;
		try{
			logger.debug("Initail Session ");
			conn = DBConnection.getInstance().getConnectionApps();

			//CUST_CAT_NO_LIST
			//add Blank Row
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			//dataList.add(item);
			
			item = new PopupBean();
			item.setCustCatNo("S");
			item.setCustCatDesc("Credit Sales");
			dataList.add(item);
			
			item = new PopupBean();
			item.setCustCatNo("C");
			item.setCustCatDesc("Van Sales");
			dataList.add(item);
			
			session.setAttribute("CUST_CAT_LIST",dataList);
			/********************************************************/
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesChannelList_s =searchSalesChannelListModel(conn);
			dataList.addAll(salesChannelList_s);
			session.setAttribute("SALES_CHANNEL_LIST",dataList);
			
			/********************************************************/
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = searchSalesrepListAll(conn,"","","");
			dataList.addAll(salesrepList_s);
			session.setAttribute("SALESREP_LIST",dataList);
			
			/********************************************************/
			//PROVINCE_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setProvince("");
			item.setProvinceName("");
			dataList.add(item);
			
			List<PopupBean> tempList = searchProvinceList(conn,"");
			dataList.addAll(tempList);
			session.setAttribute("PROVINCE_LIST",dataList);
            /********************************************************/
			//init tripList
			session.setAttribute("tripDayList", initTripList());
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			 	DBConnection.getInstance().closeConn(conn, null, null);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public static List<References> initTripList(){
		List<References> dataList = new ArrayList<References>();
		dataList.add(new References("", ""));
		for(int i=1;i<= 23;i++){
			dataList.add(new References(i+"", i+""));
		}
		dataList.add(new References(98+"", 98+""));
		return dataList;
	}
}
