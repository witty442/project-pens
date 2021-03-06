package com.isecinc.pens.dao.constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.isecinc.core.bean.References;
import com.isecinc.pens.dao.GeneralDAO;
import com.pens.util.Utils;

public class PickConstants extends Constants{

	
	public static String STATUS_OPEN ="O";
	public static String STATUS_CLOSE ="C";
	public static String STATUS_CANCEL ="AB";
	public static String STATUS_RETURN ="R";
	public static String STATUS_ISSUED ="I";
	public static String STATUS_BEF ="B";
	
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
	
	public static String WAREHOUSE_W1 ="W1";//Move
	public static String WAREHOUSE_W2 ="W2";//Move
	public static String WAREHOUSE_W3 ="W3";//Move
	public static String WAREHOUSE_W4 ="W4";//Move
	public static String WAREHOUSE_W5 ="W5";//Move
	public static String WAREHOUSE_W6 ="W6";//Move
	public static String WAREHOUSE_W7 ="W7";//Move
	
	public static String WORK_STEP_POST_BYSALE = "Post by Sale";
	public static String WORK_STEP_PICK_COMPLETE = "Pick Complete";
	
	@Deprecated
	public static String PICK_TYPE_ITEM ="ITEM";
	public static String PICK_TYPE_BOX ="BOX";
	public static String PICK_TYPE_GROUP ="GROUP";
	
	public static Map<String, String> wareHouseMap;
	
	@Deprecated
	public static String SUB_PICK_TYPE_PART_BOX ="PBOX";
	
	public static String getStoreGroupName(String storeGroup){
		if(storeGroup.equalsIgnoreCase(STORE_TYPE_LOTUS_CODE)){
			return "LOTUS";
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_BIGC_CODE)){
			return "BIGC";
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_TOPS_CODE)){
			return "TOPS";		
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_FRIDAY_CODE)){
			return "FRIDAY";
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_TVD_CODE)){
			return "TV-DIRECT";
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_MTT_CODE_1)){
			return "MTT";
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_KING_POWER)){
			return "KING Power";
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_HISHER_CODE)){
			return "HIS-HER";
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_PENS)){
			return "��������� PENS";
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_GRAND_SALE)){
			return "Grand Sale";
		}else 	if(storeGroup.equalsIgnoreCase(STORE_TYPE_WIENNA)){
			return "Wienna - ���¹��";
		}
		return "";
	}
	
	public static String getStatusDesc(String status){
		String d = "";
		status = Utils.isNull(status);
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
		}else if(PICK_TYPE_GROUP.equals(status)){
			d = "GROUP";
		}else if(STATUS_BEF.equals(status)){
			d = "BEF";
		}
		return d;
	}
	
	
	public static String getWareHouseDesc(String w){
		//Check Init
		wareHouseMap = wareHouseMap==null?GeneralDAO.initWarehouseDesc():wareHouseMap;
				
		String d = "";
		w = Utils.isNull(w);
		if(WAREHOUSE_W1.equals(w)){
			d = wareHouseMap.get("W1");
		}else if(WAREHOUSE_W2.equals(w)){
			d = wareHouseMap.get("W2");
		}else if(WAREHOUSE_W3.equals(w)){
			d = wareHouseMap.get("W3");
		}else if(WAREHOUSE_W4.equals(w)){
			d = wareHouseMap.get("W4");
		}else if(WAREHOUSE_W5.equals(w)){
			d = wareHouseMap.get("W5");
		}else if(WAREHOUSE_W6.equals(w)){
			d = wareHouseMap.get("W6");
		}else if(WAREHOUSE_W7.equals(w)){
			d = wareHouseMap.get("W7");
		}
		return d;
	}
	
	public static List<References> getWareHouseList() {
		try{
		    return GeneralDAO.searchWareHouseList("","");
		}catch(Exception e){
			
		}
		return null;
	}
	
	public static List<References> getWareHouseList(String codeSqlIn) {
		try{
		    return GeneralDAO.searchWareHouseList(codeSqlIn,"");
		}catch(Exception e){
			
		}
		return null;
	}
	public static List<References> getWareHouseList(String codeSqlIn,String codeSqlnotIn) throws Exception{
		try{
		    return GeneralDAO.searchWareHouseList(codeSqlIn,codeSqlnotIn);
		}catch(Exception e){
			throw e;
		}
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
	
	public static List<References> getScanCheckoutStatusList(){
		List<References> statusList = new ArrayList<References>();
		statusList.add(new References(STATUS_OPEN, getStatusDesc(STATUS_OPEN)));
		statusList.add(new References(STATUS_ISSUED, getStatusDesc(STATUS_ISSUED)));
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
		statusList.add(new References(STATUS_BEF, getStatusDesc(STATUS_BEF)));
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
		statusList.add(new References(STATUS_OPEN, getStatusDesc(STATUS_OPEN)));
		statusList.add(new References(STATUS_ISSUED, getStatusDesc(STATUS_ISSUED)));
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
		statusList.add(new References(PICK_TYPE_GROUP, getStatusDesc(PICK_TYPE_GROUP)));
		
		return statusList;
	}

}
