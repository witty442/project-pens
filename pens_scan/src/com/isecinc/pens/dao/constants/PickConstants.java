package com.isecinc.pens.dao.constants;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.bean.References;
import com.isecinc.pens.dao.GeneralDAO;

public class PickConstants {

	public static String STATUS_OPEN ="O";
	public static String STATUS_CLOSE ="C";
	public static String STATUS_CANCEL ="AB";
	public static String STATUS_RETURN ="R";
	public static String STATUS_ISSUED ="I";
	@Deprecated
	public static String STATUS_RENEW ="RN";
	
	public static String STATUS_NEW ="N";
	public static String STATUS_USED ="U";
	public static String STATUS_FINISH ="F";//Finishing
	public static String STATUS_WORK_IN_PROCESS ="W";//Work in process
	public static String STATUS_POST ="P";//Post
	public static String STATUS_MOVE ="M";//Move
	
	//Stock Query Status
	public static String STATUS_RESERVE ="RE";// O,P
	public static String STATUS_AVAILABLE ="A";// ONHAND_QTY-ISSUE_QTY > 0
	public static String STATUS_STOCK ="S";//STOCK (STOCK_FINISH)
	
	public static int ONHAND_PAGE_SIZE = 200;
	public static int REQ_PICK_PAGE_SIZE = 10;
	public static int CONF_PICK_PAGE_SIZE = 10;
	
	@Deprecated
	public static String PICK_TYPE_ITEM ="ITEM";
	public static String PICK_TYPE_BOX ="BOX";
	@Deprecated
	public static String SUB_PICK_TYPE_PART_BOX ="PBOX";
	
	public static String getStatusDesc(String status){
		String d = "";
		if(STATUS_OPEN.equals(status)){
			d ="OPEN";
		}else if(STATUS_CLOSE.equals(status)){
			d = "CLOSE";
		}else if(STATUS_CANCEL.equals(status)){
			d = "CANCEL";
		}else if(STATUS_RETURN.equals(status)){
			d = "RETURN";
		}else if(STATUS_ISSUED.equals(status)){
			d = "ISSUED";
		}else if(STATUS_NEW.equals(status)){
			d = "NEW";
		}else if(STATUS_USED.equals(status)){
			d = "USED";
		}else if(STATUS_RENEW.equals(status)){
			d = "RENEW";
		}else if(PICK_TYPE_BOX.equals(status)){
			d = "BOX";
		}else if(SUB_PICK_TYPE_PART_BOX.equals(status)){
			d = "PART OF BOX";
		}else if(PICK_TYPE_ITEM.equals(status)){
			d = "ITEM";
		}else if(STATUS_FINISH.equals(status)){
			d = "FINISH";
		}else if(STATUS_WORK_IN_PROCESS.equals(status)){
			d = "WORK IN PROCESS";
		}else if(STATUS_POST.equals(status)){
			d = "POST";
		}else if(STATUS_MOVE.equals(status)){
			d = "MOVE";
		}else if(STATUS_RESERVE.equals(status)){
			d = "Reserved";
		}else if(STATUS_AVAILABLE.equals(status)){
			d = "Available";
		}else if(STATUS_STOCK.equals(status)){
			d = "Stock";
		}
		return d;
	}
	
	public static List<References> getWareHouseList() {
		try{
		    return GeneralDAO.searchWareHouseList();
		}catch(Exception e){
			
		}
		return null;
	}
	
	public static List<References> getJobStatusList(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_OPEN, getStatusDesc(STATUS_OPEN)));
		statusList.add(new References(STATUS_CLOSE, getStatusDesc(STATUS_CLOSE)));
		statusList.add(new References(STATUS_CANCEL, getStatusDesc(STATUS_CANCEL)));
		statusList.add(new References(STATUS_RETURN, getStatusDesc(STATUS_RETURN)));
		return statusList;
	}
	 
	public static List<References> getBarcodeStatusList(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_OPEN, getStatusDesc(STATUS_OPEN)));
		statusList.add(new References(STATUS_CLOSE, getStatusDesc(STATUS_CLOSE)));
		statusList.add(new References(STATUS_CANCEL, getStatusDesc(STATUS_CANCEL)));
		statusList.add(new References(STATUS_RETURN, getStatusDesc(STATUS_RETURN)));
		statusList.add(new References(STATUS_ISSUED, getStatusDesc(STATUS_ISSUED)));
		statusList.add(new References(STATUS_WORK_IN_PROCESS, getStatusDesc(STATUS_WORK_IN_PROCESS)));
		statusList.add(new References(STATUS_FINISH, getStatusDesc(STATUS_FINISH)));
		return statusList;
	}
	
	public static List<References> getStockQueryStatusList(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_OPEN, getStatusDesc(STATUS_OPEN)));
		statusList.add(new References(STATUS_CLOSE, getStatusDesc(STATUS_CLOSE)));
		statusList.add(new References(STATUS_RETURN, getStatusDesc(STATUS_RETURN)));
		statusList.add(new References(STATUS_MOVE, getStatusDesc(STATUS_MOVE)));
		statusList.add(new References(STATUS_WORK_IN_PROCESS, getStatusDesc(STATUS_WORK_IN_PROCESS)));
		statusList.add(new References(STATUS_FINISH, getStatusDesc(STATUS_FINISH)));
		statusList.add(new References(STATUS_RESERVE, getStatusDesc(STATUS_RESERVE)));//I ,P
		statusList.add(new References(STATUS_AVAILABLE, getStatusDesc(STATUS_AVAILABLE)));
		
		return statusList;
	}
	
	public static List<References> getRequestStatusW1List(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_NEW, getStatusDesc(STATUS_NEW)));
		statusList.add(new References(STATUS_USED, getStatusDesc(STATUS_USED)));
		statusList.add(new References(STATUS_CANCEL, getStatusDesc(STATUS_CANCEL)));
		return statusList;
	}
	
	public static List<References> getRequestStatusW2ListInPageReqFinish(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_OPEN, getStatusDesc(STATUS_OPEN)));
		statusList.add(new References(STATUS_FINISH, getStatusDesc(STATUS_FINISH)));
		statusList.add(new References(STATUS_CANCEL, getStatusDesc(STATUS_CANCEL)));
		return statusList;
	}
	
	public static List<References> getRequestStatusW2ListInPageReqPickStock(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_OPEN, getStatusDesc(STATUS_OPEN)));
		statusList.add(new References(STATUS_POST, getStatusDesc(STATUS_POST)));
		statusList.add(new References(STATUS_CANCEL, getStatusDesc(STATUS_CANCEL)));
		return statusList;
	}
	
	public static List<References> getRequestStatusW2ListInPageQueryFinishGood(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_AVAILABLE, getStatusDesc(STATUS_AVAILABLE)));
		statusList.add(new References(STATUS_STOCK, getStatusDesc(STATUS_STOCK)));
		statusList.add(new References(STATUS_RESERVE, getStatusDesc(STATUS_RESERVE)));
		//statusList.add(new References(STATUS_ISSUED, getStatusDesc(STATUS_ISSUED)));
		//statusList.add(new References("ALL", "ALL"));
		return statusList;
	}
	
	public static List<References> getRequestStatusW2ListInPageConfPickStock(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_POST, getStatusDesc(STATUS_POST)));
		statusList.add(new References(STATUS_ISSUED, getStatusDesc(STATUS_ISSUED)));
		return statusList;
	}
	
	public static List<References> getIssueReqStatusList(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_OPEN, getStatusDesc(STATUS_OPEN)));
		statusList.add(new References(STATUS_ISSUED, getStatusDesc(STATUS_ISSUED)));
		statusList.add(new References(STATUS_CANCEL, getStatusDesc(STATUS_CANCEL)));
		
		return statusList;
	}
	
	public static List<References> getIssueReqStatusW3List(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_NEW, getStatusDesc(STATUS_NEW)));
		statusList.add(new References(STATUS_USED, getStatusDesc(STATUS_USED)));
		statusList.add(new References(STATUS_CANCEL, getStatusDesc(STATUS_CANCEL)));
		
		return statusList;
	}
	
	public static List<References> getConfStatusList(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_RETURN, getStatusDesc(STATUS_RETURN)));
		statusList.add(new References(STATUS_CANCEL, getStatusDesc(STATUS_CANCEL)));
		
		return statusList;
	}
	
	public static List<References> getPickTypeList(){
		List<References> statusList = new ArrayList<References>();
		//statusList.add(new References(PICK_TYPE_ITEM, getStatusDesc(PICK_TYPE_ITEM)));
		statusList.add(new References(PICK_TYPE_BOX, getStatusDesc(PICK_TYPE_BOX)));
		
		return statusList;
	}

}
