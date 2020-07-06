package com.isecinc.pens.web.toolmanage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.dao.ToolManageDAO;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class ToolManageReport {
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	
	public static StringBuffer genReport(Connection conn,ToolManageBean bean,String action) throws Exception{
		StringBuffer h = null;
		StringBuffer rowH = null;
		String key  ="";//storeCode-item
		String tableStyle ="align='center' border='0' cellpadding='3' cellspacing='2' class='tableSearchNoWidth' width='100%'";
		List<ToolManageBean> rowList = null;
		Map<String,Integer> sumRowMap = new HashMap<String, Integer>();
		Map<String,Integer> sumColumnMap = new HashMap<String, Integer>();
		int columnWidthPercent= 0;
		ToolManageBean store = null;
		ToolManageBean item = null;
		ToolManageBean data = null;
		try{
	    	if("ExportToExcel".equalsIgnoreCase(action)){
	    		 tableStyle ="border='1'";
	    	}
	    	logger.debug("CountStkDate:"+bean.getCountStkDate());
	    	//Get Item List
	    	List<ToolManageBean> itemList = searchItemList(conn, bean);
	    	//Get Store  List
	    	List<ToolManageBean> storeList = searchStoreList(conn, bean);
	    	//validate  (asOfDate <= countStkDate ) error
	    	if( !Utils.isNull(bean.getStoreCode()).equals("")){
		    	boolean valid = validateAsOfDate(bean.getDocDate(),bean);
		    	if(valid==false){
		    		h = new StringBuffer("AsOfDate Invalid");
		    		return h;
		    	}
	    	}
	    	//calc column width (remain 90%)
	    	if(itemList != null && itemList.size() >0){
	    	   columnWidthPercent =itemList!=null? 80/itemList.size():0;
	    	}
	    	//Get CustGroup List
	    	if(bean.getReportType().equals("Summary")){
	    		// = CustGroup List
	    		rowList = searchCustGroupList(conn, bean);
	    	}else{
	    		rowList = storeList;
	    	}
	    	
	    	//Get StoreCode IN OUT
	    	Map<String, ToolManageBean> dataMap = searchDataToMap(conn, bean,storeList);
	    	
			if(rowList != null && rowList.size() >0){
				
				//SumColMap and SumRowMap for check <> 0
				for(int r=0;r<rowList.size();r++){
					store = (ToolManageBean)rowList.get(r);
					for(int c=0;c<itemList.size();c++){
						item = (ToolManageBean)itemList.get(c);
						if(bean.getReportType().equals("Summary")){
						   key = store.getCustGroup()+"|"+item.getItem();
						}else{
						   key = store.getStoreCode()+"|"+item.getItem();
						}
						data = dataMap.get(key);
						//Sum by Column
						if(sumColumnMap.get(item.getItem()) != null){
							int prevQty = sumColumnMap.get(item.getItem());
							int newQty = data!=null?Utils.convertStrToInt(data.getQty()):0;
							//logger.debug("prevQty:"+prevQty);
							//logger.debug("newQty:"+newQty);
							sumColumnMap.put(item.getItem(), (prevQty+newQty));
						}else{
							sumColumnMap.put(item.getItem(), data!=null?Utils.convertStrToInt(data.getQty()):0);
						}
						
						//sumRow for check all row <> 0
						if(sumRowMap.get(r+"") != null){
							int prevQty = sumRowMap.get(r+"");
							int newQty = data!=null?Utils.convertStrToInt(data.getQty()):0;
							//logger.debug("prevQty:"+prevQty);
							//logger.debug("newQty:"+newQty);
							sumRowMap.put(r+"", (prevQty+newQty));
						}else{
							sumRowMap.put(r+"", data!=null?Utils.convertStrToInt(data.getQty()):0);
						}
					}//for column
				}//for row
				
				h = new StringBuffer("");
				h.append(ExcelHeader.EXCEL_HEADER);
				h.append("<table "+tableStyle+"> \n");
				if(bean.getReportType().equals("Summary")){
				   h.append("<th  class='colum_head' width='20%'>กลุ่มร้านค้า/อุปกรณ์</th> \n");
				}else{
				   h.append("<th  class='colum_head' width='20%'>ร้านค้า/อุปกรณ์</th> \n");
				   h.append("<th  class='colum_head' width='5%'>วันที่นับสต๊อกล่าสุด</th> \n");
				}
				
				//Gen Header By Item
				for(int i=0;i<itemList.size();i++){
					ToolManageBean s = (ToolManageBean)itemList.get(i);
					logger.debug("sumColumnMap["+s.getItem()+"]["+sumColumnMap.get(item.getItem()) +"]");
					if(sumColumnMap.get(s.getItem()) != 0){
				       h.append("<th>"+s.getItem()+"<br/>"+s.getItemName()+"</th> \n");
					}
				}
				h.append("</tr> \n");
				
			
				String tabclass ="lineE";
				//Loop Store
				for(int r=0;r<rowList.size();r++){
					store = (ToolManageBean)rowList.get(r);
					tabclass=r%2==0?"lineO":"lineE";
					
				    rowH = new StringBuffer("");
				    rowH.append("<tr class='"+tabclass+"'> \n");
					if(bean.getReportType().equals("Summary")){
						rowH.append("<td class='text' align='left' width='15%'>"+store.getCustGroup()+"-"+store.getCustGroupName()+"</td> \n");
					}else{
						rowH.append("<td class='text' align='left' width='15%'>"+store.getStoreCode()+"-"+store.getStoreName()+"</td> \n");
						rowH.append("<td class='text' align='left' width='5%'>"+DateUtil.stringValueNull(store.getCountStkDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td> \n");
					}
					//Loop Item
					for(int c=0;c<itemList.size();c++){
						item = (ToolManageBean)itemList.get(c);
						if(bean.getReportType().equals("Summary")){
						   key = store.getCustGroup()+"|"+item.getItem();
						}else{
						   key = store.getStoreCode()+"|"+item.getItem();
						}
						data = dataMap.get(key);
						//no display column all row =0
						if(sumColumnMap.get(item.getItem()) != 0){
							if(data != null){
							   //logger.debug("get key:"+key+",QTY["+data.getQty()+"]");
								rowH.append("<td align='right' class='num' width='"+columnWidthPercent+"%'>"+data.getQty()+"</td> \n");
							}else{
							  // logger.debug("get key:"+key+",QTY[0]");
								rowH.append("<td  align='right' class='num' width='"+columnWidthPercent+"%'>0</td> \n");
							}
					    }
					}//for Loop Item
					rowH.append("</tr> \n");
					h.append(rowH);
					
				}//for Loop Store
				//Summary
				 h.append("<tr> \n");
				 h.append("<td  align='right' colspan='2'><b>รวม</b></td> \n");
				 for(int i=0;i<itemList.size();i++){
					ToolManageBean s = (ToolManageBean)itemList.get(i);
					//no display column all row =0
					if(sumColumnMap.get(s.getItem()) != 0){
				       h.append("<td align='right'><b>"+(Utils.decimalFormat(sumColumnMap.get(s.getItem()),Utils.format_current_no_disgit))+"</b></th> \n");
					}//if
				 }//for
				 h.append("</tr> \n");
				h.append("</table> \n");
			    
				//logger.debug("table str: \n"+h.toString());
			}
			return h;
	    }catch(Exception e){
	    	throw e;
	    }
	}
	
	//asOfDate <= countStkDate then invalid
	public static boolean validateAsOfDateAllStore(String asOfDateStr,List<ToolManageBean> storeList) throws Exception{
		boolean r = true;
		Date asOfDate = null;
		Date countStkDate = null;
		
		if(storeList != null){
			for(int i=0;i<storeList.size();i++){
				ToolManageBean bean = storeList.get(i);
				if( bean.getCountStkDate() != null){
					countStkDate = bean.getCountStkDate();
					asOfDate = DateUtil.parse(Utils.isNull(asOfDateStr), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					logger.debug("countStkDate:"+countStkDate);
					logger.debug("asOfDate:"+asOfDate);
					if(asOfDate.before(countStkDate) || asOfDate.equals(countStkDate)){
				    	r = false;
				    	break;
				    }
				}
			}//for
		}//if
		logger.debug("validateAsOfDate result:"+r);
		return r;
	}
	//asOfDate <= countStkDate then invalid
	public static boolean validateAsOfDate(String asOfDateStr,ToolManageBean bean) throws Exception{
		boolean r = true;
		Date asOfDate = null;
		Date countStkDate = null;
		if( bean.getCountStkDate() != null){
			countStkDate = bean.getCountStkDate();
			asOfDate = DateUtil.parse(Utils.isNull(asOfDateStr), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			logger.debug("countStkDate:"+countStkDate);
			logger.debug("asOfDate:"+asOfDate);
			if(asOfDate.before(countStkDate) || asOfDate.equals(countStkDate)){
		    	r = false;
		    }
		}
		
		logger.debug("validateAsOfDate result:"+r);
		return r;
	}
		
	public static List<ToolManageBean> searchItemList(Connection conn,ToolManageBean bean) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ToolManageBean h = null;
		List<ToolManageBean> items = new ArrayList<ToolManageBean>();
		try {
			sql.append("\n select A.*from (");
			sql.append("\n  select distinct D.item ");
			sql.append("\n ,(select M.item_name from PENSBME_ITEM_MASTER M where M.item = D.item) as item_name");
            sql.append("\n  from PENSBME_INITIAL_ITEM H,PENSBME_INITIAL_ITEM_DETAIL D");
            sql.append("\n  where H.store_code = D.store_code ");
            sql.append("\n  and H.Count_stk_date = D.Count_stk_date");
            if( !Utils.isNull(bean.getCustGroup()).equals("")){
     	       sql.append("\n and H.store_code like '"+Utils.isNull(bean.getCustGroup())+"%' ");
     	    }
     	    if( !Utils.isNull(bean.getStoreCode()).equals("")){
     	       sql.append("\n and H.store_code = '"+Utils.isNull(bean.getStoreCode())+"' ");
     	    }
     	   if( !Utils.isNull(bean.getItemFrom()).equals("") && !Utils.isNull(bean.getItemTo()).equals("")){
  		     sql.append("\n and D.item >= '"+Utils.isNull(bean.getItemFrom())+"' ");
  		     sql.append("\n and D.item <= '"+Utils.isNull(bean.getItemTo())+"' ");
  		}
	        sql.append("\n  UNION ");
            sql.append("\n  select distinct d.item ");
            sql.append("\n ,(select M.item_name from PENSBME_ITEM_MASTER M where M.item = d.item) as item_name");
            sql.append("\n  from PENSBME_ITEM_INOUT H ,PENSBME_ITEM_INOUT_DETAIL d");
		    sql.append("\n  where 1=1 and H.doc_no = d.doc_no");
		    //Where Condition
		    sql.append(genWhereSearchList(bean));
		    sql.append("\n )A ");
            sql.append("\n  order by A.item asc");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new ToolManageBean();
			   h.setItem(Utils.isNull(rst.getString("item")));
			   h.setItemName(Utils.isNull(rst.getString("item_name")));
			   items.add(h);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	
	public static List<ToolManageBean> searchStoreList(Connection conn,ToolManageBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ToolManageBean h = null;
		List<ToolManageBean> items = new ArrayList<ToolManageBean>();
		try {
			sql.append("\n select A.* from ( ");
            sql.append("\n  select distinct H.store_code ,to_number(REPLACE(H.store_code,'-', '')) as store_code_int");
            sql.append("\n ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = H.store_code) as STORE_NAME ");
			sql.append("\n ,(select max(Count_stk_date) from pensbi.PENSBME_INITIAL_ITEM M ");
			sql.append("\n   where  M.store_code = H.store_code) as Count_stk_date ");
            sql.append("\n  from PENSBME_ITEM_INOUT H ,PENSBME_ITEM_INOUT_DETAIL D");
		    sql.append("\n  where H.doc_no = D.doc_no");
		    //Where Condition
		    sql.append(genWhereSearchList(o));
		    sql.append("\n  union ");
		    sql.append("\n  select distinct H.store_code ,to_number(REPLACE(H.store_code,'-', '')) as store_code_int");
		    sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
		    sql.append("\n  where M.reference_code = 'Store' and M.pens_value = trim(H.store_code)) as STORE_NAME ");
		    sql.append("\n  , H.Count_stk_date ");
		    sql.append("\n  from PENSBME_INITIAL_ITEM H");
		    sql.append("\n  where 1=1");
		    //not in transaction
		    sql.append("\n  and H.store_code not in(");
		    sql.append("\n    select distinct H.store_code ");
            sql.append("\n    from PENSBME_ITEM_INOUT H ,PENSBME_ITEM_INOUT_DETAIL D");
		    sql.append("\n    where H.doc_no = D.doc_no");
		    //Where Condition
		    sql.append(genWhereSearchList(o));
		    sql.append("\n   )");
			/*Date countStkDate = null;
			if( !Utils.isNull(o.getCountStkDate()).equals("")){
				countStkDate = Utils.parse(Utils.isNull(o.getCountStkDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			}*/
			/*if(countStkDate != null){
				 Date asOfDate = Utils.parse(Utils.isNull(o.getDocDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				 sql.append("\n AND H.Count_stk_date > to_date('"+countStkDate+"','dd/mm/yyyy')  ");
				 sql.append("\n AND H.Count_stk_date <= to_date('"+asOfDate+"','dd/mm/yyyy')  ");
			}else{
				 Date asOfDate = Utils.parse(Utils.isNull(o.getDocDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			     sql.append("\n and H.Count_stk_date <= to_date('"+Utils.stringValue(asOfDate, Utils.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}*/

		    if( !Utils.isNull(o.getCustGroup()).equals("")){
		       sql.append("\n and H.store_code LIKE '"+Utils.isNull(o.getCustGroup())+"%' ");
		    }
		    if( !Utils.isNull(o.getStoreCode()).equals("")){
		       sql.append("\n and H.store_code = '"+Utils.isNull(o.getStoreCode())+"' ");
		    }
		    sql.append("\n )A ");
            sql.append("\n order by A.store_code_int asc");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ToolManageBean();
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   h.setCountStkDate(rst.getDate("Count_stk_date"));
			   items.add(h);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	public static List<ToolManageBean> searchCustGroupList(Connection conn,ToolManageBean bean) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ToolManageBean h = null;
		List<ToolManageBean> items = new ArrayList<ToolManageBean>();
		try {
            sql.append("\n  select distinct H.cust_group ");
            sql.append("\n ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Customer' and M.pens_value = H.cust_group) as cust_group_name ");
            sql.append("\n  from PENSBME_ITEM_INOUT H");
		    sql.append("\n  where 1=1 ");
		    //Where Condition
		    sql.append(genWhereSearchList(bean));
            sql.append("\n   order by H.cust_group asc");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ToolManageBean();
			   h.setCustGroup(Utils.isNull(rst.getString("cust_group")));
			   h.setCustGroupName(Utils.isNull(rst.getString("cust_group_name")));
			   items.add(h);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	public static Map<String,ToolManageBean> searchDataToMap(Connection conn,ToolManageBean bean,List<ToolManageBean> storeList) throws Exception {
		Map<String,ToolManageBean> dataMap = new HashMap<String, ToolManageBean>();
	
		if(storeList != null){
			for(int i=0;i<storeList.size();i++){
				ToolManageBean storeBean = storeList.get(i);
				storeBean.setReportType(bean.getReportType());
				//storeBean.setCountStkDate(bean.getCountStkDate());
				storeBean.setDocDate(bean.getDocDate());
				storeBean.setItemFrom(bean.getItemFrom());
				storeBean.setItemTo(bean.getItemTo());
				//set data to Map By StoreCode
				dataMap  = searchDataToMapByStoreCode(conn,dataMap,storeBean);
			}
			
		   if(bean.getReportType().equals("Summary")){
			    String key = "";//custGroup+storeCode+item
			    String[] keyArr = null;
			    String newKey = "";
			    ToolManageBean currBean = null;
			    ToolManageBean oldBean = null;
			    
				Map<String,ToolManageBean> dataCustGroupMap = new HashMap<String, ToolManageBean>();
				//Case Summary by CustGroup
				Iterator<String> dataIt = dataMap.keySet().iterator();
				while(dataIt.hasNext()){
					key = dataIt.next();
					keyArr = key.split("\\|");
					newKey = keyArr[0]+"|"+keyArr[2];//custGroup+item
					currBean = dataMap.get(key);
					
					if(dataCustGroupMap.get(newKey) !=null){
						//old+cur
						oldBean = dataCustGroupMap.get(newKey);
						oldBean.setQty(""+(Utils.convertCurrentcyToInt(oldBean.getQty())+Utils.convertCurrentcyToInt(currBean.getQty())));
						
						dataCustGroupMap.put(newKey, oldBean);
					}else{
						dataCustGroupMap.put(newKey, currBean);
					}
					
				}//while
				//set to Show
				dataMap = dataCustGroupMap;
		   }
		}
		return dataMap;
	}
	
	public static Map<String,ToolManageBean> searchDataToMapByStoreCode(Connection conn,Map<String,ToolManageBean> dataMap ,ToolManageBean bean) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ToolManageBean h = null;
		String key  ="";
		Date countStkDate = null;
		try {
			logger.debug("countStkDate:"+bean.getCountStkDate());
			countStkDate = bean.getCountStkDate();//Utils.parse(Utils.isNull(bean.getCountStkDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			sql.append("\n select M.cust_group,M.store_code,M.item ");
			sql.append("\n ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = M.store_code) as STORE_NAME ");
			sql.append("\n , NVL(INIT.INIT_QTY,0) as INIT_QTY ,NVL(OUTS.OUT_QTY,0) as OUT_QTY , NVL(INS.IN_QTY ,0) as IN_QTY ");
			sql.append("\n ,( NVL(INIT.INIT_QTY,0) + NVL(OUTS.OUT_QTY,0) - NVL(INS.IN_QTY,0)) as ONHAND_QTY  ");
			sql.append("\n FROM ( ");
            sql.append("\n  select distinct (substr(H.store_code,0,6)) as cust_group,H.store_code ,D.item ");
            sql.append("\n  from PENSBME_INITIAL_ITEM H,PENSBME_INITIAL_ITEM_DETAIL D");
            sql.append("\n  where H.store_code = D.store_code ");
            sql.append("\n  and H.Count_stk_date = D.Count_stk_date");
            if( !Utils.isNull(bean.getCustGroup()).equals("")){
     	       sql.append("\n and H.cust_group = '"+Utils.isNull(bean.getCustGroup())+"' ");
     	    }
     	    if( !Utils.isNull(bean.getStoreCode()).equals("")){
     	       sql.append("\n and H.store_code = '"+Utils.isNull(bean.getStoreCode())+"' ");
     	    }
     	    if( countStkDate != null){
     	       sql.append("\n and H.Count_stk_date = to_date('"+DateUtil.stringValue(countStkDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
     	    }
            sql.append("\n  UNION ");
            
            sql.append("\n  select distinct H.cust_group,H.store_code,D.item ");
            sql.append("\n  from PENSBME_ITEM_INOUT H,PENSBME_ITEM_INOUT_DETAIL D");
		    sql.append("\n  where H.doc_no = D.doc_no  ");
		    sql.append(genWhereSearchList(bean));
		    sql.append("\n ) M ");
		    
		    /** INIT **/
		    sql.append("\n LEFT OUTER JOIN(	 ");
		    sql.append("\n   select H.store_code,D.item ,NVL(sum(QTY),0) as INIT_QTY ");
            sql.append("\n   from PENSBME_INITIAL_ITEM H,PENSBME_INITIAL_ITEM_DETAIL D");
		    sql.append("\n   where H.store_code = D.store_code  ");
		    sql.append("\n   and H.Count_stk_date = D.Count_stk_date ");
		    if( !Utils.isNull(bean.getCustGroup()).equals("")){
     	       sql.append("\n and H.cust_group = '"+Utils.isNull(bean.getCustGroup())+"' ");
     	    }
     	    if( !Utils.isNull(bean.getStoreCode()).equals("")){
     	       sql.append("\n and H.store_code = '"+Utils.isNull(bean.getStoreCode())+"' ");
     	    }
     	    if( countStkDate != null){
     	       sql.append("\n and H.Count_stk_date = to_date('"+DateUtil.stringValue(countStkDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
     	    }
		    sql.append("\n   group by H.store_code,D.item");
		    sql.append("\n )INIT ON M.store_code=INIT.store_code and M.item = INIT.item ");
		    /** OUT **/
		    sql.append("\n LEFT OUTER JOIN(	 ");
		    sql.append("\n   select H.store_code,D.item ,NVL(sum(QTY),0) as OUT_QTY ");
            sql.append("\n   from PENSBME_ITEM_INOUT H,PENSBME_ITEM_INOUT_DETAIL D");
		    sql.append("\n   where H.doc_no = D.doc_no  ");
		    sql.append("\n   and H.doc_type='OUT'  ");
			sql.append("\n   and H.status = 'POST' ");
		    sql.append(genWhereSearchList(bean));
		    sql.append("\n   group by H.store_code,D.item");
		    sql.append("\n ) OUTS ON M.store_code=OUTS.store_code and M.item = OUTS.item ");
		    
		    /** IN **/
		    sql.append("\n LEFT OUTER JOIN(	 ");
		    sql.append("\n   select H.store_code,D.item ,NVL(sum(QTY),0) as IN_QTY ");
            sql.append("\n   from PENSBME_ITEM_INOUT H,PENSBME_ITEM_INOUT_DETAIL D");
		    sql.append("\n   where H.doc_no = D.doc_no  ");
		    sql.append("\n   and H.doc_type='IN'  ");
		    sql.append("\n   and H.status = 'POST' ");
		    sql.append(genWhereSearchList(bean));
		    sql.append("\n   group by H.store_code,D.item");
		    sql.append("\n )INS ON M.store_code=INS.store_code and M.item = INS.item ");

			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new ToolManageBean();
			   h.setCustGroup(Utils.isNull(rst.getString("cust_group")));
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   h.setItem(Utils.isNull(rst.getString("item")));
			 
			   h.setQty(Utils.decimalFormat(rst.getInt("ONHAND_QTY"), Utils.format_current_no_disgit));
			   
			   //Case Summary re sum by custGroup
			   if(bean.getReportType().equals("Summary")){
			       key = h.getCustGroup()+"|"+h.getStoreCode()+"|"+h.getItem();
			   }else{
				   key = h.getStoreCode()+"|"+h.getItem();
			   }
			   logger.debug("set key:"+key+",QTY["+h.getQty()+"]");
			   dataMap.put(key, h);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return dataMap;
	}
	
	public static StringBuffer genWhereSearchList(ToolManageBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");

		if(o.getCountStkDate() != null){
			 Date asOfDate = DateUtil.parse(Utils.isNull(o.getDocDate()), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			 sql.append("\n AND H.doc_date > to_date('"+DateUtil.stringValue(o.getCountStkDate(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')  ");
			 sql.append("\n AND H.doc_date <= to_date('"+DateUtil.stringValue(asOfDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')  ");
		}else{
			 Date asOfDate = DateUtil.parse(Utils.isNull(o.getDocDate()), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
		     sql.append("\n and H.doc_date <= to_date('"+DateUtil.stringValue(asOfDate, DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
		}

	    if( !Utils.isNull(o.getCustGroup()).equals("")){
	       sql.append("\n and H.cust_group = '"+Utils.isNull(o.getCustGroup())+"' ");
	    }
	    if( !Utils.isNull(o.getStoreCode()).equals("")){
	       sql.append("\n and H.store_code = '"+Utils.isNull(o.getStoreCode())+"' ");
	    }
	    if( !Utils.isNull(o.getDocNo()).equals("")){
		     sql.append("\n and H.doc_no = '"+Utils.isNull(o.getDocNo())+"' ");
		}
	    if( !Utils.isNull(o.getItemFrom()).equals("") && !Utils.isNull(o.getItemTo()).equals("")){
		     sql.append("\n and D.item >= '"+Utils.isNull(o.getItemFrom())+"' ");
		     sql.append("\n and D.item <= '"+Utils.isNull(o.getItemTo())+"' ");
		}
	    
		 return sql;
	}
}
