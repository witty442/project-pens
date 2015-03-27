package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.DescriptorKey;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ReqReturnWacoal;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class PickStockDAO extends PickConstants{
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public PickStockDAO() {
		// TODO Auto-generated constructor stub
	}
    @Deprecated
	public static PickStock save(Connection conn,PickStock h,Map<String, PickStock> dataSaveMapAll) throws Exception{
		PickStock result = new PickStock();
		result.setResultProcess(true);//default
		try{
			
			Date tDate  = Utils.parse(h.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			//check documentNo
			if(Utils.isNull(h.getIssueReqNo()).equals("")){
				//Gen issueReqNo
				h.setIssueReqNo(genIssueReqNo(conn,tDate));
				h.setIssueReqStatus(STATUS_OPEN);
				h.setPickType(PickConstants.PICK_TYPE_ITEM);
				
				logger.debug("New IssueReqNo:"+h.getIssueReqNo());
				
				//save head
				savePickStockHeadModel(conn, h);
				
				logger.debug("dataSaveMapAll["+dataSaveMapAll.size()+"]");
				
				//validate and insert ,update status barcode item
				result = validateAndInsert(conn,result,h,dataSaveMapAll);
	
			}else{
				//Edit
                h.setIssueReqStatus(STATUS_OPEN);
				logger.debug("Update IssueReqNo:"+h.getIssueReqNo());
				
				//save head
				updatePickStockHeadModel(conn, h);
				
				logger.debug("dataSaveMapAll["+dataSaveMapAll.size()+"]");
				
				//delete pickStockItem and update barcode item status=''
				deletePickStockItemAndUpdateBarcodeItem(conn,h,dataSaveMapAll);
				
				//validate stock and insert ,update status barcode item
				result = validateAndInsert(conn,result,h,dataSaveMapAll);
			}
			
			return result;
		}catch(Exception e){
		  throw e;
		}finally{
			
		}
	}
	
	public static PickStock saveBox(Connection conn,PickStock h) throws Exception{
		Map<String,String> pensItemMapAll = new HashMap<String, String>();
		try{
			
			Date tDate  = Utils.parse(h.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			//check documentNo
			if(Utils.isNull(h.getIssueReqNo()).equals("")){
				//Gen issueReqNo
				h.setIssueReqNo(genIssueReqNo(conn,tDate));
				h.setIssueReqStatus(STATUS_OPEN);
				h.setPickType(PickConstants.PICK_TYPE_BOX);
				
				logger.debug("New IssueReqNo:"+h.getIssueReqNo());
				
				//save head
				savePickStockHeadModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   PickStock l = (PickStock)h.getItems().get(i);
					   l.setIssueReqNo(h.getIssueReqNo());
					   l.setCreateUser(h.getCreateUser());
					   l.setUpdateUser(h.getUpdateUser());
					   l.setIssueReqStatus(h.getIssueReqStatus());
					   
					   //save item 
					   Map<String,String> pensItemMap = insertPickStockLineFromBarcodeItem(conn,l);
					   pensItemMapAll.putAll(pensItemMap);
					   
				       //Set barcode status = ISSUE
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setStatus(PickConstants.STATUS_ISSUED);
				       b.setUpdateUser(h.getUpdateUser());
				       
				       //update barcode_item DB
				       BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
				       BarcodeDAO.updateBarcodeLineStatusModelByPK(conn, b);
				   }//for
				}//if
				
			}else{
				//Edit
                h.setIssueReqStatus(STATUS_OPEN);
				logger.debug("Update IssueReqNo:"+h.getIssueReqNo());
				
				//save head
				updatePickStockHeadModel(conn, h);
				
				//delete pickStockItem and update barcode item status='CLOSE'
				deletePickStockItemAndUpdateBarcodeToClose(conn,h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   PickStock l = (PickStock)h.getItems().get(i);
					   l.setIssueReqNo(h.getIssueReqNo());
					   l.setCreateUser(h.getCreateUser());
					   l.setUpdateUser(h.getUpdateUser());
					  
					 
					   //save item 
					   Map<String,String> pensItemMap = insertPickStockLineFromBarcodeItem(conn,l);
					   pensItemMapAll.putAll(pensItemMap);
				       
				       //Set barcode status = ISSUE
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setStatus(PickConstants.STATUS_ISSUED);
				       b.setUpdateUser(h.getUpdateUser());
				       
				       
				       //update barcode_item DB
				       BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
				       BarcodeDAO.updateBarcodeLineStatusModelByPK(conn, b);
				   }//for
				}//if
			}
			
			h.setResultProcess(true);//default
			h.setPensItemMapAll(pensItemMapAll);
			
			return h;
		}catch(Exception e){
		  throw e;
		}finally{
			
		}
	}
    
	
	private static Map<String ,String> insertPickStockLineFromBarcodeItem(Connection conn,PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        Map<String ,String> pensItemMap = new HashMap<String, String>();
		try {
			sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1 \n");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
	
			sql.append("\n order by line_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new PickStock();
			   h.setIssueReqNo(o.getIssueReqNo());
			   h.setLineId(rst.getInt("line_id"));
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setIssueReqStatus(o.getIssueReqStatus());
			  
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			   
			   h.setCreateUser(o.getCreateUser());
			   h.setUpdateUser(o.getUpdateUser());
			   
			   //save pick_stock_line
			   savePickStockLineModel(conn, h);
			   
			   pensItemMap.put(h.getPensItem(), h.getPensItem());
			   r++;
			   
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pensItemMap;
	}
	
	//get barcode item status=close ,insert pick stock item ,update barcode item =issue
	private static Map<String ,String> insertPickStockLineAndUpdateBarcodeItemToIssue(Connection conn,PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        Map<String ,String> pensItemMap = new HashMap<String, String>();
		try {
			sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1 \n");
			sql.append("\n and  i.status ='"+PickConstants.STATUS_CLOSE+"' \n");
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
	
			sql.append("\n order by line_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new PickStock();
			   h.setIssueReqNo(o.getIssueReqNo());
			   h.setLineId(rst.getInt("line_id"));
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			  
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			   
			   h.setCreateUser(o.getCreateUser());
			   h.setUpdateUser(o.getUpdateUser());
			   
			   //save pick_stock_line
			   savePickStockLineModel(conn, h);
			   
			   //Update barcode item status=issue
		      //sql.append(" WHERE  BOX_NO = ? and  material_master =? and group_code =? and pens_item = ? and job_id = ? and line_id =? \n" );
			   h.setBarcodeItemStatus(PickConstants.STATUS_ISSUED);
			   h.setUpdateUser(h.getUpdateUser());
		       updateStatusBarcodeItemModel(conn, h);
			   
			   pensItemMap.put(h.getPensItem(), h.getPensItem());
			   r++;
			   
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pensItemMap;
	}
	
	/**
	 * 
	 * @param conn
	 * @param result
	 * @param h
	 * @param dataSaveLists
	 * @return
	 * @throws Exception
	 */
	private static PickStock validateAndInsert(Connection conn,PickStock result,PickStock h,Map<String, PickStock> dataSaveMapAll) throws Exception{
		try{
			//insert List
            Iterator its =  dataSaveMapAll.keySet().iterator();
			while(its.hasNext()){
				   String key = (String)its.next();
				   PickStock lineChk = (PickStock)dataSaveMapAll.get(key);
				   logger.debug("Keymap["+key+"]="+lineChk);
				   
				   int qty = Utils.convertStrToInt(lineChk.getQty());
				
				   //Step2 get Data barcode By pens_item,box_no
				   List<PickStock> barcodeItemInStockList = searchBarcodeItemInStock(conn, lineChk);
				   logger.debug("barcodeItemInStockList:"+barcodeItemInStockList.size());
				   
				   if(barcodeItemInStockList !=null && barcodeItemInStockList.size() >0){
					   int no = 1;
					   for(int k=0;k<barcodeItemInStockList.size();k++){
						   //save line = qty
						   if(no<=qty){
							   PickStock p = (PickStock)barcodeItemInStockList.get(k);
							   p.setIssueReqNo(h.getIssueReqNo());
							   p.setCreateUser(h.getCreateUser());
							   p.setUpdateUser(h.getUpdateUser());
							   
							   logger.debug("no["+no+"]insert pick stock boxNo["+p.getBoxNo()+"]pensItem["+p.getPensItem()+"]Mat["+p.getMaterialMaster()+"]lineId["+p.getLineId()+"]");
							   
							   //save pick_stock item
						       savePickStockLineModel(conn, p);
						       
						       //update barcode item (status = P)
						       p.setBarcodeItemStatus(PickConstants.STATUS_ISSUED);
						       updateStatusBarcodeItemModel(conn, p);
						       
						       no++;
						   }
					   }//for 2
				   }//if 
			}//while 1
			return result;
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * 
	 * @param conn
	 * @param result
	 * @param h
	 * @param dataSaveLists
	 * @return
	 * @throws Exception
	 * 
	 * Update status barcode item = ''
	 */
	private static void deletePickStockItemAndUpdateBarcodeItem(Connection conn,PickStock h,Map<String, PickStock> dataSaveMapAll) throws Exception{
		int no = 1;
		try{
			//insert List
			Iterator its =  dataSaveMapAll.keySet().iterator();
			while(its.hasNext()){
			   String key = (String)its.next();
			   PickStock lineChk = (PickStock)dataSaveMapAll.get(key);
			   logger.debug("Keymap["+key+"]="+lineChk);
			   
			   //Step2 get pick_stock item
			   lineChk.setIssueReqNo(h.getIssueReqNo());
			   List<PickStock> pickStockItemList = searchPickStockItem(conn, lineChk);
			   logger.debug("pickStockItemList:"+pickStockItemList.size());
			   
			   if(pickStockItemList !=null && pickStockItemList.size() >0){
				   for(int k=0;k<pickStockItemList.size();k++){
					   PickStock p = (PickStock)pickStockItemList.get(k);
					   p.setCreateUser(h.getCreateUser());
					   p.setUpdateUser(h.getUpdateUser());
					   
					   //delete pick stock line by pk
						deletePickStockLineModelByPK(conn, p);
						
					   //delete Pick Stock Line
					   logger.debug("no["+(no)+"]update barcode item:"+p.getPensItem()+",boxNo["+p.getBoxNo()+"]lineId:"+p.getLineId());

				       //update barcode item (status = C)
				       p.setBarcodeItemStatus(PickConstants.STATUS_CLOSE);
				       updateStatusBarcodeItemModel(conn, p);
					   no++;
				   }//for 2
			   }//if 
			}//while 1

		}catch(Exception e){
			throw e;
		}
	}
	
	//1-delete pickStockLine 2, update status barcode item and head
	private static void deletePickStockItemAndUpdateBarcodeToClose(Connection conn,PickStock h) throws Exception{
		int no = 1;
	    Map<String,PickStock> boxNoMap = new HashMap<String, PickStock>();
		try{
			  List<PickStock> pickStockItemList = searchPickStockItem(conn, h);
			  logger.debug("pickStockItemList:"+pickStockItemList.size());
			   
			  if(pickStockItemList !=null && pickStockItemList.size() >0){
				   for(int k=0;k<pickStockItemList.size();k++){
					   PickStock p = (PickStock)pickStockItemList.get(k);
					   p.setCreateUser(h.getCreateUser());
					   p.setUpdateUser(h.getUpdateUser());
					   
					   //delete pick stock line by pk
						deletePickStockLineModelByPK(conn, p);
						
					   //delete Pick Stock Line
					   logger.debug("no["+(no)+"]update barcode item:"+p.getPensItem()+",boxNo["+p.getBoxNo()+"]lineId:"+p.getLineId());

				       //update barcode item (status = C)
				       p.setBarcodeItemStatus(PickConstants.STATUS_CLOSE);
				       updateStatusBarcodeItemModel(conn, p);
				       
				       //set 4 update barcode head
				       boxNoMap.put(p.getBoxNo(), p);
					   no++;
				   }//for 2
				   
				   //update barcode head
				   if( !boxNoMap.isEmpty()){
					   Iterator it = boxNoMap.keySet().iterator();
					   while(it.hasNext()){
						   String key = (String)it.next();
						   PickStock pBoxNo = (PickStock)boxNoMap.get(key);
						   
						   Barcode b = new Barcode();
					       b.setJobId(pBoxNo.getJobId());
					       b.setBoxNo(pBoxNo.getBoxNo());
					       b.setStatus(PickConstants.STATUS_CLOSE);
					       b.setUpdateUser(h.getUpdateUser());
						  
						   BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
					     
						   
					   }
				   }
			   }//if 
		}catch(Exception e){
			throw e;
		}
	}
	
	public static void updateStatusBarcodeItemModel(Connection conn,PickStock o) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE_ITEM SET  \n");
			sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
			
			sql.append(" WHERE  BOX_NO = ? and  material_master =? and group_code =? and pens_item = ? and job_id = ? and line_id =? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setString(c++, o.getBarcodeItemStatus());
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, Utils.isNull(o.getBoxNo()));
			ps.setString(c++, Utils.isNull(o.getMaterialMaster()));
			ps.setString(c++, Utils.isNull(o.getGroupCode()));
			ps.setString(c++, Utils.isNull(o.getPensItem()));
			ps.setString(c++, Utils.isNull(o.getJobId()));
			ps.setInt(c++, o.getLineId());
			
			int r = ps.executeUpdate();
			logger.debug("Update:"+r);
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}

	
	public static List<PickStock> searchHead(PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		PickStock h = null;
		List<PickStock> items = new ArrayList<PickStock>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n SELECT * ");
			sql.append("\n from PENSBME_PICK_STOCK    \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'  ");
			}
			
			if( !Utils.isNull(o.getIssueReqStatus()).equals("")){
				sql.append("\n and issue_req_status = '"+Utils.isNull(o.getIssueReqStatus())+"'  ");
			}
			
			if( !Utils.isNull(o.getIssueReqDate()).equals("")){
				Date tDate  = Utils.parse(o.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String returnDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and ISSUE_REQ_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
			}
			
			if( !Utils.isNull(o.getConfirmIssueDate()).equals("")){
				Date tDate  = Utils.parse(o.getConfirmIssueDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String returnDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and CONFIRM_ISSUE_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
			}
			
			if( !Utils.isNull(o.getPickType()).equals("")){
				sql.append("\n and pick_type= '"+Utils.isNull(o.getPickType())+"'  ");
			}
			
			sql.append("\n order by issue_req_no DESC ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
				   h = new PickStock();
				  
				   h.setIssueReqDate(Utils.stringValue(rst.getDate("issue_req_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setIssueReqNo(Utils.isNull(rst.getString("issue_req_no")));
				   h.setIssueReqStatus(Utils.isNull(rst.getString("issue_req_status")));
				   h.setIssueReqStatusDesc(getStatusReqDesc(h.getIssueReqStatus()));

				   h.setConfirmIssueDate(Utils.stringValue(rst.getDate("confirm_issue_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)); 
				   h.setRemark(Utils.isNull(rst.getString("remark")));
				   h.setPickUser(Utils.isNull(rst.getString("pick_user")));
				   h.setPickType(Utils.isNull(rst.getString("pick_type")));
				   h.setPickTypeDesc(getStatusDesc(h.getPickType()));
				   h.setSubPickType(Utils.isNull(rst.getString("sub_pick_type")));
				   
				   if(Utils.isNull(rst.getString("issue_req_status")).equals(STATUS_ISSUED) || Utils.isNull(rst.getString("issue_req_status")).equals(STATUS_CANCEL) ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
				   
				   if(Utils.isNull(rst.getString("issue_req_status")).equals(STATUS_OPEN) ){
					   h.setCanConfirm(true);
				   }
 
			   items.add(h);
			   r++;
			   
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return items;
	}
	
	//barcode status = close or null()
	public static List<PickStock> searchBarcodeItemInStock(Connection conn,PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        List<PickStock> items = new ArrayList<PickStock>();
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1 and (status ='' or status is null or status ='"+STATUS_CLOSE+"') \n");
			
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and i.group_code = '"+Utils.isNull(o.getGroupCode())+"'");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			if( !Utils.isNull(o.getMaterialMaster()).equals("")){
				sql.append("\n and i.material_master = '"+Utils.isNull(o.getMaterialMaster())+"'");
			}
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and i.pens_item = '"+Utils.isNull(o.getPensItem())+"'");
			}
			sql.append("\n order by line_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new PickStock();
			   h.setLineId(rst.getInt("line_id"));
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			  
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			   
			   items.add(h);
			   r++;
			   
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
	
	public static List<PickStock> searchPickStockItem(Connection conn,PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        List<PickStock> items = new ArrayList<PickStock>();
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_PICK_STOCK_I i   \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and i.group_code = '"+Utils.isNull(o.getGroupCode())+"'");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			if( !Utils.isNull(o.getMaterialMaster()).equals("")){
				sql.append("\n and i.material_master = '"+Utils.isNull(o.getMaterialMaster())+"'");
			}
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and i.pens_item = '"+Utils.isNull(o.getPensItem())+"'");
			}
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			
			sql.append("\n order by line_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new PickStock();
			   h.setIssueReqNo(rst.getString("issue_req_no"));
			   h.setLineId(rst.getInt("line_id"));
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			  
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			   
			   items.add(h);
			   r++;
			   
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
	
	public static List<PickStock> searchPickStockItemByIssueReqNo(Connection conn,PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        List<PickStock> items = new ArrayList<PickStock>();
     
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_PICK_STOCK_I i ");
			sql.append("\n where 1=1 ");
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			sql.append("\n order by line_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new PickStock();
			   h.setLineId(rst.getInt("line_id"));
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			  
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			   
			  	   
			   items.add(h);
			   r++;
			   
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
	
	public static PickStock searchPickStockItemByIssueReqNo4Report(Connection conn,PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        List<PickStock> items = new ArrayList<PickStock>();
        int totalQty = 0;
		try {
			sql.append("\n select i.job_id,i.box_no,i.MATERIAL_MASTER,i.group_code,i.pens_item ,count(*) as qty ");
			sql.append("\n ,(select max(name) from PENSBME_PICK_JOB j where j.job_id = i.job_id) as job_name ");
			sql.append("\n from PENSBI.PENSBME_PICK_STOCK_I i ");
			sql.append("\n where 1=1 ");
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			sql.append("\n group by i.job_id,i.box_no,i.MATERIAL_MASTER,i.group_code,i.pens_item ");
			sql.append("\n order by i.job_id,i.box_no,i.MATERIAL_MASTER,i.group_code,i.pens_item  asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new PickStock();
			
			   h.setJobId(rst.getString("job_id"));
			   h.setJobName(rst.getString("job_name"));
			   h.setBoxNo(rst.getString("box_no"));
			  
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"), Utils.format_current_no_disgit));
			   h.setQtyInt(rst.getInt("qty"));
			   
			   totalQty += rst.getInt("qty");
					   
			   items.add(h);
			   r++;
			   
			}//while
			
			o.setItems(items);
			o.setTotalQty(totalQty);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return o;
	}
	
	public static List<PickStock> searchPickStockItemByIssueReqNoGroupByBoxNo(Connection conn,PickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        List<PickStock> items = new ArrayList<PickStock>();
		try {

			sql.append("\n select distinct i.box_no,i.job_id from PENSBI.PENSBME_PICK_STOCK_I i ");
			sql.append("\n where 1=1 ");
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			sql.append("\n order by i.box_no, i.job_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new PickStock();
			  
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));

			   items.add(h);
			   r++;
			   
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
	
	public static PickStock searchPickStock(Connection conn,PickStock h ,boolean getItems) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("\n select  j.*  ");
			sql.append("\n  ,(SELECT m.pens_desc from PENSBME_MST_REFERENCE m ");
			sql.append("\n    where 1=1  and j.store_code = m.pens_value and m.reference_code ='Store') as store_name ");
			sql.append("\n from PENSBME_PICK_STOCK j ");
			sql.append("\n where 1=1   ");
			sql.append("\n and issue_req_no = '"+h.getIssueReqNo()+"'");

			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   
			   h.setIssueReqNo(Utils.isNull(rst.getString("issue_req_no")));
			   if(rst.getDate("issue_req_date") != null){
				  String dateStr = Utils.stringValue(rst.getDate("issue_req_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			      h.setIssueReqDate(dateStr);
			   }
			   h.setIssueReqStatus(rst.getString("issue_req_status"));
			   h.setIssueReqStatusDesc(getStatusReqDesc(h.getIssueReqStatus()));
			   h.setPickUser(rst.getString("pick_user"));
			   h.setRemark(rst.getString("remark"));
			   h.setPickType(rst.getString("pick_type"));
			   h.setSubPickType(Utils.isNull(rst.getString("sub_pick_type")));
			   
			   h.setCustGroup(Utils.isNull(rst.getString("cust_group"))); 
			   h.setStoreCode(Utils.isNull(rst.getString("store_code"))); 
			   h.setStoreName(Utils.isNull(rst.getString("store_name"))); 
			   h.setStoreNo(Utils.isNull(rst.getString("store_no"))); 
			   h.setSubInv(Utils.isNull(rst.getString("sub_inv"))); 
			   
			   if(rst.getDate("confirm_issue_date") != null){
				   String dateStr = Utils.stringValue(rst.getDate("confirm_issue_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				   h.setConfirmIssueDate(dateStr);
			   }
			   
			   if(h.isModeEdit()){
				   //can edit
				   if( Utils.isNull(h.getIssueReqStatus()).equals(STATUS_ISSUED)
					|| Utils.isNull(h.getIssueReqStatus()).equals(STATUS_CANCEL)){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true);
				   }
				   
				   //can cancel
				   if( Utils.isNull(h.getIssueReqStatus()).equals(STATUS_OPEN)){
					   h.setCanCancel(true);
				   }else{
					   h.setCanCancel(false); 
				   }
			   }else{
				   h.setCanEdit(false);
				   h.setCanCancel(false); 
			   }
			   
			   //can confirm 
			   if(h.isModeConfirm()){
				   if( Utils.isNull(h.getIssueReqStatus()).equals(STATUS_OPEN)){
					   h.setCanConfirm(true);
				   }else{
					   h.setCanConfirm(false); 
				   }
			   }else{
				   h.setCanConfirm(false);  
			   }
			}//while
			
			if(h != null){
				if(getItems){
				  if( Utils.isNull(h.getIssueReqStatus()).equals(STATUS_ISSUED)
							|| Utils.isNull(h.getIssueReqStatus()).equals(STATUS_CANCEL)){
					  
					  if(PickConstants.PICK_TYPE_BOX.equals(h.getPickType())){
						  h = searchPickStockItemByPKGroupByBoxNo(conn,h);
						  
				      }else if(PickConstants.PICK_TYPE_ITEM.equals(h.getPickType()) &&  PickConstants.SUB_PICK_TYPE_PART_BOX.equals(h.getSubPickType()) ){
						  h = searchPickStockItemByPKGroupByBoxNoCasePartOfBox(conn,h);
					  }else{
			              h = searchPickStockItemByPK(conn,h);
					  }
				  }else{
					  if(PickConstants.PICK_TYPE_BOX.equals(h.getPickType()) ){  
						  h = searchPickStockItemByPKGroupByBoxNo(conn,h);
					  }else if( PickConstants.PICK_TYPE_ITEM.equals(h.getPickType()) && PickConstants.SUB_PICK_TYPE_PART_BOX.equals(h.getSubPickType()) ){
						  h = searchPickStockItemByPKGroupByBoxNoCasePartOfBox(conn,h);
					  }else{
					      h = searchPickStockItemAll(conn,h); 
					  }
				  }
				}
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				
			} catch (Exception e) {}
		}
		return h;
	}
	
	public static PickStock searchPickStockItemAll(Connection conn,PickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PickStock> items = new ArrayList<PickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		try {
			
			if( !pickStock.isNewReq()){
				sql.append("\n SELECT M.*,P.qty FROM ( ");
				sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
				sql.append("\n from PENSBME_PICK_ONHAND ");
				sql.append("\n where 1=1   ");
				sql.append("\n ) M LEFT OUTER JOIN  ");
				sql.append("\n (  ");
				sql.append("\n select  i.box_no,MATERIAL_MASTER,group_code,pens_item,count(*) as qty   ");
				sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
				sql.append("\n where 1=1   ");
				sql.append("\n and h.issue_req_no = i.issue_req_no ");
				if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
				}
				sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
				sql.append("\n )P ");
				sql.append("\n ON m.BOX_NO = p.BOX_NO and M.pens_item = P.pens_item  " +
						   "\n and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
				sql.append("\n order by m.box_no,M.MATERIAL_MASTER,M.group_code,M.pens_item ");
		    }else{
		    	sql.append("\n SELECT M.*,0 as qty FROM ( ");
				sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
				sql.append("\n from PENSBME_PICK_ONHAND ");
				sql.append("\n where 1=1   ");
				sql.append("\n )M  ");
				sql.append("\n order by m.box_no,M.MATERIAL_MASTER,M.group_code,M.pens_item ");
		    }
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   PickStock h = new PickStock();
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   //Case Edit 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   
			   //Case cancel no add
			   if( !STATUS_CANCEL.equals(pickStock.getIssueReqStatus())){
				   if(!pickStock.isNewReq()){
					  onhandQty +=qty;
				   }
			   }
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(rst.getInt("qty") != 0){
			     h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }
			   
			   h.setLineItemErrorStyle("");
			   
			   items.add(h);
			   r++;
			   
			   //sum total qty
			   totalQty += rst.getInt("qty");
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	public static PickStock searchPickStockItemAllByPageCaseEdit(Connection conn,PickStock pickStock,int pageNumber,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PickStock> items = new ArrayList<PickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		try {
			if( !pickStock.isNewReq()){
				sql.append("\n select M2.* ,");
				sql.append("\n (select name from pensbme_pick_job j where M2.job_id = j.job_id) as job_name ");
				sql.append("\n FROM( ");
				sql.append("\n SELECT M.*,");
				sql.append("\n (select Max(B.job_id) from PENSBME_PICK_BARCODE B where M.box_no= B.box_no) as job_id ");
				
				sql.append("\n FROM( ");
				sql.append("\n SELECT a.*, rownum r__ ");
				sql.append("\n FROM ( ");
				sql.append("\n SELECT  U.*  FROM( ");
				
                sql.append("\n SELECT M.* ,P.qty ,O.onhand_qty FROM ( ");
				sql.append("\n select i.box_no,MATERIAL_MASTER,group_code,pens_item  ");
				sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
				sql.append("\n where 1=1 and h.pick_type ='ITEM'  ");
				sql.append("\n and h.issue_req_no = i.issue_req_no ");
				if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
				}
				sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
	
				sql.append("\n UNION ");
				sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item  "); 
				sql.append("\n from PENSBME_PICK_ONHAND  ");
				sql.append("\n where 1=1    ");
				sql.append("\n and qty <> 0  ");
				sql.append("\n  ) M LEFT OUTER JOIN  ");
				
				sql.append("\n ( select  i.box_no,MATERIAL_MASTER,group_code,pens_item,count(*) as qty   ");
				sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
				sql.append("\n where 1=1 and h.pick_type ='ITEM'  ");
				sql.append("\n and h.issue_req_no = i.issue_req_no ");
				if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
				}
				sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
				sql.append("\n )P ");
				sql.append("\n ON M.BOX_NO = P.BOX_NO and M.pens_item = P.pens_item  " +
						   "\n and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
				
				sql.append("\n LEFT OUTER JOIN  ");
				sql.append("\n ( select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
				sql.append("\n from PENSBME_PICK_ONHAND ");
				sql.append("\n where 1=1   ");
				sql.append("\n ) O   ");
				sql.append("\n ON M.BOX_NO = O.BOX_NO and M.pens_item = O.pens_item  " +
						   "\n and M.MATERIAL_MASTER = O.MATERIAL_MASTER  and M.group_code = O.group_code  ");
				
				sql.append("\n   )U ");
			    sql.append("\n order by U.box_no,U.MATERIAL_MASTER,U.group_code,U.pens_item ");
				  
				sql.append("\n   )a  ");
				sql.append("\n  WHERE rownum < (("+pageNumber+" * "+pageSize+") + 1 )  ");
				sql.append("\n )M  ");
				sql.append("\n WHERE r__ >= ((("+pageNumber+"-1) * "+pageSize+") + 1)  ");
				sql.append("\n )M2  ");
				
		    }else{
		    	sql.append("\n select M2.* ,");
				sql.append("\n (select name from pensbme_pick_job j where M2.job_id = j.job_id) as job_name ");
				sql.append("\n FROM( ");
				sql.append("\n SELECT M.*,");
				sql.append("\n (select Max(B.job_id) from PENSBME_PICK_BARCODE B where M.box_no= B.box_no) as job_id ");
				
				sql.append("\n FROM( ");
				sql.append("\n SELECT a.*, rownum r__ ");
				sql.append("\n FROM ( \n");
			    	sql.append("\n SELECT M.*,0 as qty FROM ( ");
					sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
					sql.append("\n from PENSBME_PICK_ONHAND ");
					sql.append("\n where 1=1   ");
					sql.append("\n and qty <> 0 ");
					sql.append("\n )M  ");
					sql.append("\n order by m.box_no,M.MATERIAL_MASTER,M.group_code,M.pens_item ");
				sql.append("\n   )a  ");
				sql.append("\n  WHERE rownum < (("+pageNumber+" * "+pageSize+") + 1 )  ");
				sql.append("\n )M  ");
				sql.append("\n WHERE r__ >= ((("+pageNumber+"-1) * "+pageSize+") + 1)  ");
				sql.append("\n )M2  ");
		    }
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   PickStock h = new PickStock();
			   h.setJobId(Utils.isNull(rst.getString("job_id")));
			   h.setJobName(Utils.isNull(rst.getString("job_name")));
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   //Case Edit 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   
			   //Case cancel no add
			   if( !STATUS_CANCEL.equals(pickStock.getIssueReqStatus())){
				   if(!pickStock.isNewReq()){
					  onhandQty +=qty;
				   }
			   }
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(rst.getInt("qty") != 0){
			     h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }
			   
			   h.setLineItemErrorStyle("");
			   h.setNewReq(pickStock.isNewReq());
			   
			   items.add(h);
			   r++;
			   
			   //sum total qty
			   totalQty += rst.getInt("qty");
			   
			   
			   
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	public static Map<String,PickStock> searchPickStockItemByPKAllToMap(Connection conn,PickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Map<String,PickStock> dataEditMapAll = new HashMap<String, PickStock>();
		int c = 1;
		int r = 1;
		try {
			sql.append("\n select  i.box_no,MATERIAL_MASTER,group_code,pens_item,count(*) as qty   ");
			sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
			sql.append("\n where 1=1 and h.pick_type ='ITEM'  ");
			sql.append("\n and h.issue_req_no = i.issue_req_no ");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   PickStock h = new PickStock();
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			 
			   if(rst.getInt("qty") != 0){
			     h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }
			   //Key Map  
			   String key = h.getBoxNo()+"_"+h.getMaterialMaster()+"_"+h.getGroupCode()+"_"+h.getPensItem();
			   dataEditMapAll.put(key, h);
			   
			   r++;
			}//while
			
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return dataEditMapAll;
	}
	
	public static int getTotalQtyInPickStock(Connection conn,PickStock pickStock ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			sql.append("\n select count(*) as qty   ");
			sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
			sql.append("\n where 1=1 and h.pick_type ='ITEM'  ");
			sql.append("\n and h.issue_req_no = i.issue_req_no ");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n group by h.issue_req_no");
				
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("qty");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
	}
	
	public static int getCountPickStockItemAllCaseEdit(Connection conn,PickStock pickStock ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int c = 1;
		int r = 1;
		int totalRecord = 0;
		try {
			
			if(pickStock.isNewReq() == false){
				
				sql.append("\n SELECT count(*) as c FROM ( ");
				
				sql.append("\n SELECT M.* ,P.qty ,O.onhand_qty FROM ( ");
				
				sql.append("\n select i.box_no,MATERIAL_MASTER,group_code,pens_item  ");
				sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
				sql.append("\n where 1=1  and h.pick_type ='ITEM'");
				sql.append("\n and h.issue_req_no = i.issue_req_no ");
				if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
				}
				sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
				sql.append("\n UNION  ");
				sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item  "); 
				sql.append("\n from PENSBME_PICK_ONHAND  ");
				sql.append("\n where 1=1    ");
				sql.append("\n and qty <> 0  ");
				sql.append("\n  ) M LEFT OUTER JOIN  ");
				
				sql.append("\n ( select  i.box_no,MATERIAL_MASTER,group_code,pens_item,count(*) as qty   ");
				sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
				sql.append("\n where 1=1 and h.pick_type ='ITEM'  ");
				sql.append("\n and h.issue_req_no = i.issue_req_no ");
				if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
				}
				sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
				sql.append("\n )P ");
				sql.append("\n ON M.BOX_NO = P.BOX_NO and M.pens_item = P.pens_item  " +
						   "\n and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
				
				sql.append("\n LEFT OUTER JOIN  ");
				sql.append("\n ( select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
				sql.append("\n from PENSBME_PICK_ONHAND ");
				sql.append("\n where 1=1   ");
				sql.append("\n ) O   ");
				sql.append("\n ON M.BOX_NO = O.BOX_NO and M.pens_item = O.pens_item  " +
						   "\n and M.MATERIAL_MASTER = O.MATERIAL_MASTER  and M.group_code = O.group_code  ");
				
				sql.append("\n )C ");
		    }else{
		    	sql.append("\n SELECT count(*) as c FROM ( ");
				sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
				sql.append("\n from PENSBME_PICK_ONHAND ");
				sql.append("\n where 1=1   ");
				sql.append("\n and qty <> 0 ");
				sql.append("\n )M  ");
		    }
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("c");
			}//while
			

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
	}
	
	public static int getCountPickStockItemAllCaseNoEdit(Connection conn,PickStock pickStock ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int c = 1;
		int r = 1;
		int totalRecord = 0;
		try {
			sql.append("\n SELECT count(*) as c FROM ( ");
			sql.append("\n select  i.box_no,MATERIAL_MASTER,group_code,pens_item,count(*) as qty   ");
			sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
			sql.append("\n where 1=1 and h.pick_type ='ITEM'  ");
			sql.append("\n and h.issue_req_no = i.issue_req_no ");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
			sql.append("\n )P INNER JOIN");
			
			sql.append("\n ( select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
			sql.append("\n from PENSBME_PICK_ONHAND ");
			sql.append("\n where 1=1   ");
			sql.append("\n ) M   ");
			sql.append("\n ON m.BOX_NO = p.BOX_NO and M.pens_item = P.pens_item  " +
					   "\n and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
		    
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("c");
			}//while
			

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
	}
	
	public static PickStock searchPickStockItemByPageByPKCaseNoEdit(Connection conn,PickStock pickStock,int pageNumber,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PickStock> items = new ArrayList<PickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		try {
			sql.append("\n select M2.* ,");
			sql.append("\n (select name from pensbme_pick_job j where M2.job_id = j.job_id) as job_name ");
			sql.append("\n FROM( ");
			sql.append("\n SELECT M.*,");
			sql.append("\n (select Max(B.job_id) from PENSBME_PICK_BARCODE B where M.box_no= B.box_no) as job_id ");
			sql.append("\n FROM( \n");
			sql.append("\n SELECT a.*, rownum r__ \n");
			sql.append("\n FROM ( \n");
				sql.append("\n SELECT M.*,P.qty FROM ( ");
					sql.append("\n   ");
					sql.append("\n select  i.box_no,MATERIAL_MASTER,group_code,pens_item,count(*) as qty   ");
					sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
					sql.append("\n where 1=1 and h.pick_type ='ITEM'  ");
					sql.append("\n and h.issue_req_no = i.issue_req_no ");
					if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
					   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
					}
					sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
					sql.append("\n )P INNER JOIN  ");
				
					sql.append("\n ( ");
					sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
					sql.append("\n from PENSBME_PICK_ONHAND ");
					sql.append("\n where 1=1   ");
					sql.append("\n ) M  ");
					
					sql.append("\n ON m.BOX_NO = p.BOX_NO and M.pens_item = P.pens_item  " +
							   "\n and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
					
					sql.append("\n order by m.box_no,M.MATERIAL_MASTER,M.group_code,M.pens_item ");
			sql.append("\n   )a  ");
			sql.append("\n  WHERE rownum < (("+pageNumber+" * "+pageSize+") + 1 )  ");
			sql.append("\n )M  ");
			sql.append("\n WHERE r__ >= ((("+pageNumber+"-1) * "+pageSize+") + 1)  ");
			sql.append("\n )M2  ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   PickStock h = new PickStock();
			   h.setJobId(Utils.isNull(rst.getString("job_id")));
			   h.setJobName(Utils.isNull(rst.getString("job_name")));
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   //Case Edit 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   
			   //Case cancel no add
			   if( !STATUS_CANCEL.equals(pickStock.getIssueReqStatus())){
				  onhandQty +=qty;
			   }
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(rst.getInt("qty") != 0){
			     h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }
			   
			   h.setLineItemErrorStyle("");
			   h.setNewReq(pickStock.isNewReq());
			   
			   items.add(h);
			   r++;
			   
			   //sum total qty
			   totalQty += rst.getInt("qty");
			   
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	public static PickStock searchPickStockItemByPK(Connection conn,PickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PickStock> items = new ArrayList<PickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		try {
			sql.append("\n SELECT M.*,P.qty FROM ( ");
				sql.append("\n   ");
				sql.append("\n select  i.box_no,MATERIAL_MASTER,group_code,pens_item,count(*) as qty   ");
				sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
				sql.append("\n where 1=1   ");
				sql.append("\n and h.issue_req_no = i.issue_req_no ");
				if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
				}
				sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
				sql.append("\n )P INNER JOIN  ");
			
				sql.append("\n ( ");
				sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
				sql.append("\n from PENSBME_PICK_ONHAND ");
				sql.append("\n where 1=1   ");
				sql.append("\n ) M  ");
				
				sql.append("\n ON m.BOX_NO = p.BOX_NO and M.pens_item = P.pens_item  " +
						   "\n and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
				
				sql.append("\n order by m.box_no,M.MATERIAL_MASTER,M.group_code,M.pens_item ");
		    
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   PickStock h = new PickStock();
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   //Case Edit 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   
			   //Case cancel no add
			   if( !STATUS_CANCEL.equals(pickStock.getIssueReqStatus())){
				  onhandQty +=qty;
			   }
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(rst.getInt("qty") != 0){
			     h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }
			   
			   h.setLineItemErrorStyle("");
			   
			   items.add(h);
			   r++;
			   
			   //sum total qty
			   totalQty += rst.getInt("qty");
			   
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}

	public static PickStock searchPickStockItemByPKGroupByBoxNoCasePartOfBox(Connection conn,PickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PickStock> items = new ArrayList<PickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		try {
			
			sql.append("\n SELECT M.*,T1.total_box_qty");
			sql.append("\n FROM( ");
			sql.append("\n    SELECT P.*,(select max(name) from PENSBME_PICK_JOB j WHERE j.job_id =P.job_id )as job_name " );
			sql.append("\n    FROM ( ");
			sql.append("\n    select  i.box_no,i.job_id,count(*) as qty   ");
			sql.append("\n    from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
			sql.append("\n    where 1=1   ");
			sql.append("\n    and h.issue_req_no = i.issue_req_no ");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			   sql.append("\n  and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n    group by i.box_no, i.job_id ");
			sql.append("\n  ) P ");
			sql.append("\n ) M  LEFT OUTER JOIN ");
			 
			sql.append("\n (  select h.box_no,h.job_id , count(*) as total_box_qty   ");
			sql.append("\n     from PENSBI.PENSBME_PICK_BARCODE h,  ");
			sql.append("\n     PENSBME_PICK_BARCODE_ITEM s ");
			sql.append("\n     where s.box_no = h.box_no   ");
			sql.append("\n     and s.job_id = h.job_id ");
			
			sql.append("\n     group by h.box_no ,h.job_id  ");
			sql.append("\n ) T1  ON T1.job_id = M.job_id AND T1.box_no = M.box_no ");
			
			sql.append("\n order by M.box_no,M.job_id  ");
		    
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   PickStock h = new PickStock();
			   h.setSelected("true");
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setJobName(rst.getString("job_name"));
			   h.setJobId(rst.getString("job_id"));
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   h.setTotalBoxQty(Utils.decimalFormat(rst.getInt("total_box_qty"),Utils.format_current_no_disgit));
			   items.add(h);
			   r++;
			   
			   //sum total qty
			   totalQty += rst.getInt("qty");
			   
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	public static PickStock searchPickStockItemByPKGroupByBoxNo(Connection conn,PickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PickStock> items = new ArrayList<PickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		try {
			sql.append("\n SELECT P.*,  ");
				sql.append("\n (select max(name) from PENSBME_PICK_JOB j WHERE j.job_id =P.job_id )as job_name FROM ( ");
				sql.append("\n select  i.box_no,i.job_id,count(*) as qty   ");
				sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
				sql.append("\n where 1=1   ");
				sql.append("\n and h.issue_req_no = i.issue_req_no ");
				if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
				}
				sql.append("\n group by i.box_no, i.job_id ");
				sql.append("\n )P   ");
			
				sql.append("\n order by P.box_no,P.job_id  ");
		    
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   PickStock h = new PickStock();
			   h.setSelected("true");
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setJobName(rst.getString("job_name"));
			   h.setJobId(rst.getString("job_id"));
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			 
			   items.add(h);
			   r++;
			   
			   //sum total qty
			   totalQty += rst.getInt("qty");
			   
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	
	public static String getStoreName(Connection conn,String storeCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String storeName = "";
		StringBuilder sql = new StringBuilder();
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT * from PENSBME_MST_REFERENCE ");
			sql.append("\n  where 1=1 and reference_code ='Store' ");
		    sql.append("\n and pens_value ='"+storeCode+"' \n");

			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
		
			if (rst.next()) {
				storeName = Utils.isNull(rst.getString("pens_desc"));
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return storeName;
	}
	
	
	// ( Running :  TYYMMxxxx  เช่น T57040001 เป็นต้น	
	 private static String genIssueReqNo(Connection conn,Date tDate) throws Exception{
		   String orderNo = "";
		   try{
			   String today = df.format(tDate);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			   //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"STOCK", "ISSUE_REQ_NO",tDate);
			   
			   orderNo = "T"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("0000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }
		  return orderNo;
	}
	 

	private static void savePickStockHeadModel(Connection conn,PickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_PICK_STOCK \n");
			sql.append(" (ISSUE_REQ_NO, ISSUE_REQ_DATE, ISSUE_REQ_STATUS, PICK_USER,REMARK ," +
					"CREATE_DATE,CREATE_USER,PICK_TYPE,CUST_GROUP,STORE_CODE,STORE_NO,SUB_INV,SUB_PICK_TYPE)  \n");
		    sql.append(" VALUES (?, ?, ?, ?, ?, ? , ? ,?,?,?,?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
				
			Date tDate = Utils.parse( o.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			int c =1;
			
			ps.setString(c++, o.getIssueReqNo());
			ps.setTimestamp(c++, new java.sql.Timestamp(tDate.getTime()));
			ps.setString(c++, o.getIssueReqStatus());
			ps.setString(c++, o.getPickUser());
			ps.setString(c++, o.getRemark());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getCreateUser());
			ps.setString(c++, o.getPickType());
			ps.setString(c++, o.getCustGroup());
			ps.setString(c++, o.getStoreCode());
			ps.setString(c++, o.getStoreNo());
			ps.setString(c++, o.getSubInv());
			ps.setString(c++, Utils.isNull(o.getSubPickType()));
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updatePickStockHeadModel(Connection conn,PickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_PICK_STOCK \n");
			sql.append(" SET ISSUE_REQ_STATUS =?, PICK_USER = ?,REMARK =? ,UPDATE_DATE =?,UPDATE_USER =? \n");
			if( !Utils.isNull(o.getConfirmIssueDate()).equals("")){
				sql.append(" ,confirm_issue_date =? \n ");
			}
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
			
			ps = conn.prepareStatement(sql.toString());
			int c =1;
			
			ps.setString(c++, o.getIssueReqStatus());
			ps.setString(c++, o.getPickUser());
			ps.setString(c++, o.getRemark());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			
			if( !Utils.isNull(o.getConfirmIssueDate()).equals("")){
				Date confrimDate = Utils.parse( o.getConfirmIssueDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			    ps.setTimestamp(c++, new java.sql.Timestamp(confrimDate.getTime()));
			}
			
			ps.setString(c++, o.getIssueReqNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updatePickStockItemModel(Connection conn,PickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_PICK_STOCK_I \n");
			sql.append(" SET ISSUE_REQ_STATUS =?,UPDATE_DATE =?,UPDATE_USER =? \n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
			
			ps = conn.prepareStatement(sql.toString());
			int c =1;
			
			ps.setString(c++, o.getIssueReqStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setString(c++, o.getIssueReqNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updateCancelPickStockHeadModel(Connection conn,PickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateCancelPickStockHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_PICK_STOCK \n");
			sql.append(" SET ISSUE_REQ_STATUS =?,UPDATE_DATE =?,UPDATE_USER =? \n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
			
			ps = conn.prepareStatement(sql.toString());
			int c =1;
			
			ps.setString(c++, o.getIssueReqStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setString(c++, o.getIssueReqNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updateBarcodeByIssueReqNo(Connection conn,PickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateBarcodeByIssueReqNo");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_PICK_BARCODE \n");
			sql.append(" SET STATUS =?,UPDATE_DATE =?,UPDATE_USER =? \n");
			sql.append(" WHERE BOX_NO IN( \n");
		    sql.append("   SELECT BOX_NO FROM PENSBME_PICK_STOCK_I WHERE ISSUE_REQ_NO = ? \n");
		    sql.append(" ) \n");
		    
			ps = conn.prepareStatement(sql.toString());
			int c =1;
			
			ps.setString(c++, o.getIssueReqStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setString(c++, o.getIssueReqNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	public static void updateBarcodeItemByIssueReqNo(Connection conn,PickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateBarcodeByIssueReqNo");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_PICK_BARCODE_ITEM \n");
			sql.append(" SET STATUS =?,UPDATE_DATE =?,UPDATE_USER =? \n");
			sql.append(" WHERE BOX_NO IN( \n");
		    sql.append("   SELECT BOX_NO FROM PENSBME_PICK_STOCK_I WHERE ISSUE_REQ_NO = ? \n");
		    sql.append(" ) \n");
		    
			ps = conn.prepareStatement(sql.toString());
			int c =1;
			
			ps.setString(c++, o.getIssueReqStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setString(c++, o.getIssueReqNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	private static void savePickStockLineModel(Connection conn,PickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_PICK_STOCK_I \n");
			sql.append(" (ISSUE_REQ_NO,job_id, LINE_ID,BOX_NO,PENS_ITEM,material_Master,group_code,WHOLE_PRICE_BF,RETAIL_PRICE_BF ,CREATE_DATE,CREATE_USER,ISSUE_REQ_STATUS)  \n");
		    sql.append(" VALUES (?, ?, ?, ?, ?, ? , ? ,? , ?, ?, ?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
				
			int c =1;
			
			ps.setString(c++, o.getIssueReqNo());
			ps.setString(c++, o.getJobId());
			ps.setInt(c++, o.getLineId());
			ps.setString(c++, o.getBoxNo());
			ps.setString(c++, o.getPensItem());
			ps.setString(c++, o.getMaterialMaster());
			ps.setString(c++, o.getGroupCode());
			ps.setDouble(c++, Utils.convertStrToDouble(o.getWholePriceBF()));
			ps.setDouble(c++, Utils.convertStrToDouble(o.getRetailPriceBF()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getCreateUser());
			ps.setString(c++, o.getIssueReqStatus());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}

//PK  (ISSUE_REQ_NO,JOB_ID,BOX_NO,LINE_ID)	
	public static void deletePickStockLineModelByPK(Connection conn,PickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("deletePickStockLineModelByPK");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBI.PENSBME_PICK_STOCK_I \n");
			sql.append(" WHERE ISSUE_REQ_NO ='"+o.getIssueReqNo()+"' \n" );
			sql.append(" and JOB_ID ="+o.getJobId()+" \n" );
			sql.append(" and BOX_NO ='"+o.getBoxNo()+"' \n" );
			sql.append(" and LINE_ID ="+o.getLineId()+" \n" );
			sql.append(" and MATERIAL_MASTER ='"+o.getMaterialMaster()+"' \n" );
			sql.append(" and GROUP_CODE='"+o.getGroupCode()+"' \n" );
			sql.append(" and PENS_ITEM='"+o.getPensItem()+"' \n" );
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static String getStatusReqDesc(String status){
		String desc = "";
		if(STATUS_OPEN.equals(status)){
			desc = "OPEN";
		}else if(STATUS_ISSUED.equals(status)){
			desc = "ISSUED";
		}else if(STATUS_CANCEL.equals(status)){
			desc = "CANCEL";
		}
		
		return desc;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<PickStock> searchBarcoceItemStatusCloseW3(Connection conn ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        List<PickStock> items = new ArrayList<PickStock>();
		try {
           
			sql.append("\n select i.box_no,i.job_id, j.name as job_name ");
			sql.append("\n ,count(*) as qty ");
			sql.append("\n from PENSBME_PICK_JOB j,PENSBI.PENSBME_PICK_BARCODE h, PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1  ");
			sql.append("\n and j.job_id = h.job_id ");
			sql.append("\n and h.job_id = i.job_id ");
			sql.append("\n and h.box_no = i.box_no ");
			sql.append("\n and h.warehouse = 'W3'");
			sql.append("\n and h.status = '"+STATUS_CLOSE+"'");
			sql.append("\n and ( i.status = '"+STATUS_CLOSE+"' OR i.status ='' OR i.status is null)");
			
			sql.append("\n group by i.box_no ,i.job_id,j.name ");
			sql.append("\n order by i.box_no asc ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new PickStock();
			   h.setJobId(rst.getString("job_id"));
			   h.setJobName(rst.getString("job_name"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			
			   items.add(h);
			   r++;
			   
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
	
    //TEst	
	public static List<PickStock> searchBarcoceItemInBoxCasePickSomeItem(Connection conn ,String boxNoSqlPick) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        List<PickStock> items = new ArrayList<PickStock>();
		try {
           
			sql.append("\n SELECT M.*,T1.total_box_qty,T2.qty ");
			sql.append("\n FROM( ");
			sql.append("\n    select i.box_no,i.job_id ");
			sql.append("\n    ,(select max(name) from PENSBME_PICK_JOB j where j.job_id=i.job_id )as job_name  ");
		
			sql.append("\n    from PENSBI.PENSBME_PICK_BARCODE h,  ");
			sql.append("\n    PENSBI.PENSBME_PICK_BARCODE_ITEM i    ");
			sql.append("\n    where 1=1   ");
			sql.append("\n    and h.job_id = i.job_id  ");
			sql.append("\n    and h.box_no = i.box_no  ");
			sql.append("\n    and h.status = '"+JobDAO.STATUS_CLOSE+"'");
			sql.append("\n    and ( i.status = '"+JobDAO.STATUS_CLOSE+"' OR i.status ='' OR i.status is null) ");
			if( !Utils.isNull(boxNoSqlPick).equals("")){
			   sql.append("\n    and h.box_no not in("+boxNoSqlPick+")");
			}
			sql.append("\n    group by i.box_no ,i.job_id  ");
			sql.append("\n ) M  LEFT OUTER JOIN ");
			 
			sql.append("\n (  select h.box_no,h.job_id , count(*) as total_box_qty   ");
			sql.append("\n     from PENSBI.PENSBME_PICK_BARCODE h,  ");
			sql.append("\n     PENSBME_PICK_BARCODE_ITEM s ");
			sql.append("\n     where s.box_no = h.box_no   ");
			sql.append("\n     and s.job_id = h.job_id ");
			if( !Utils.isNull(boxNoSqlPick).equals("")){
				sql.append("\n    and h.box_no not in("+boxNoSqlPick+")");
			}
			sql.append("\n     group by h.box_no ,h.job_id  ");
			sql.append("\n ) T1  ON T1.job_id = M.job_id AND T1.box_no = M.box_no ");
			 
			sql.append("\n LEFT OUTER JOIN ");
			sql.append("\n (   select h.box_no,h.job_id , count(*) as qty   ");
			sql.append("\n     from PENSBI.PENSBME_PICK_BARCODE h,  ");
			sql.append("\n     PENSBME_PICK_BARCODE_ITEM s ");
			sql.append("\n     where s.box_no = h.box_no   ");
			sql.append("\n     and s.job_id = h.job_id ");
			sql.append("\n     and h.status = '"+JobDAO.STATUS_CLOSE+"'" );
			sql.append("\n     and ( s.status = '"+JobDAO.STATUS_CLOSE+"' OR s.status ='' OR s.status is null) ");
			if( !Utils.isNull(boxNoSqlPick).equals("")){
				 sql.append("\n    and h.box_no not in("+boxNoSqlPick+")");
		    }
			sql.append("\n     group by h.box_no ,h.job_id  ");
			sql.append("\n ) T2 ON T2.job_id = M.job_id AND T2.box_no = M.box_no ");
			sql.append("\n WHERE T1.total_box_qty <> T2.qty ");
			sql.append("\n order by M.box_no asc  ");
 
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new PickStock();
			   h.setJobId(rst.getString("job_id"));
			   h.setJobName(rst.getString("job_name"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   h.setTotalBoxQty(Utils.decimalFormat(rst.getInt("total_box_qty"),Utils.format_current_no_disgit));
			   
			   items.add(h);
			   r++;
			   
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

}
