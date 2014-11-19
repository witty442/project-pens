package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ReqFinish;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class ReqFinishDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	

	public static void main(String[] s){
		try{
			String today = df.format(new Date());
			   String[] d1 = today.split("/");
			   System.out.println("d1[0]:"+d1[0]);
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   System.out.println("curYear:"+curYear);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static ReqFinish searchReqFinishing(Connection conn,ReqFinish o,boolean getItems ) throws Exception {
		return searchReqFinishingModel(conn,o,getItems);
	}
	
	public static ReqFinish searchReqFinishing(ReqFinish o,boolean getItems ) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			
			return searchReqFinishingModel(conn,o,getItems);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	public static ReqFinish searchReqFinishingModel(Connection conn,ReqFinish h,boolean getItems ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		
		int r = 1;
		int c = 1;
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_REQ_FINISHING i \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(h.getRequestDate()).equals("")){
				sql.append("\n and i.REQUEST_DATE = ? ");
			}
			
			if( !Utils.isNull(h.getStatus()).equals("")){
				sql.append("\n and i.status = '"+Utils.isNull(h.getStatus())+"'");
			}
			
			if( !Utils.isNull(h.getRequestNo()).equals("")){
				sql.append("\n and i.request_no = '"+Utils.isNull(h.getRequestNo())+"'");
			}
			
			sql.append("\n order by i.request_no desc ");
			logger.debug("sql:"+sql);
			
			
			ps = conn.prepareStatement(sql.toString());
			
			if( !Utils.isNull(h.getRequestDate()).equals("")){
				Date tDate  = Utils.parse(h.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();

			while(rst.next()) {
	
				   h.setNo(r);
				   h.setRequestDate(Utils.stringValue(rst.getTimestamp("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setConfirmDate(Utils.stringValue(rst.getTimestamp("confirm_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setRemark(Utils.isNull(rst.getString("remark"))); 
				   h.setTotalBox(rst.getInt("total_box"));
				   h.setTotalQty(rst.getInt("total_qty"));
				   
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_OPEN) ){
					   h.setCanEdit(true);
				   }else{
					   h.setCanEdit(false); 
				   }
 
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_FINISH) ){
					   h.setCanPrint(true);
				   }else{
					   h.setCanPrint(false); 
				   }
				  
				   
				//get Items
				if(getItems){
					h.setItems(searchItem(conn, h));
				}

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
		return h;
	}
	
	public static List<ReqFinish> searchHead(ReqFinish o,boolean getItems ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ReqFinish h = null;
		List<ReqFinish> items = new ArrayList<ReqFinish>();
		int r = 1;
		int c = 1;
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_REQ_FINISHING i \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getRequestDate()).equals("")){
				sql.append("\n and i.REQUEST_DATE = ? ");
			}
			
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and i.status = '"+Utils.isNull(o.getStatus())+"'");
			}
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			
			sql.append("\n order by i.request_no desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			if( !Utils.isNull(o.getRequestDate()).equals("")){
				Date tDate  = Utils.parse(o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();

			while(rst.next()) {
				   h = new ReqFinish();
				   h.setNo(r);
				   h.setRequestDate(Utils.stringValue(rst.getTimestamp("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setRemark(Utils.isNull(rst.getString("remark"))); 
				   h.setTotalBox(rst.getInt("total_box"));
				   h.setTotalQty(rst.getInt("total_qty"));
				   
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_OPEN) ){
					   h.setCanEdit(true);
				   }else{
					   h.setCanEdit(false); 
				   }
 
				//get Items
				if(getItems){
					h.setItems(searchItem(conn, h));
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
	
	
	public static List<ReqFinish> searchItem(Connection conn,ReqFinish o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqFinish h = null;
		int r = 1;
		int c = 1;
        List<ReqFinish> items = new ArrayList<ReqFinish>();
		try {
			sql.append(" select A.job_id,A.job_name,A.box_no,count(*) as qty from(");
			sql.append("\n select i.* " +
			          " \n ,(select max(name) from PENSBME_PICK_JOB j where j.job_id= i.job_id) as job_name "+
					  " \n from PENSBI.PENSBME_REQ_FINISHING_BARCODE i ");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			sql.append("\n ) A group by A.job_id,A.job_name,A.box_no ");
			sql.append("\n order by A.job_id,A.job_name,A.box_no  ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new ReqFinish();
			   //h.setNo(rst.getInt("no"));
			   h.setJobId(rst.getString("job_id"));
			   h.setJobName(rst.getString("job_name"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setQty(rst.getInt("qty"));
			   h.setSelected("true");
			   
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
	
	public static int getMaxQtyLimitReturn(Connection conn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
        int maxQtyLimitReturn = 0;
		try {
			sql.append("\n select con_value from PENSBI.PENSBME_C_CONTROL  ");
			sql.append("\n where 1=1 and con_type ='RETURN_WACOAL' and con_code='MAX_QTY_RETURN_LIMIT'  \n");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				maxQtyLimitReturn = Utils.convertStrToInt(rst.getString("con_value"));
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return maxQtyLimitReturn;
	}
	
	
	public static List<ReqFinish> searchBarcoceItemW2() throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqFinish h = null;
		int r = 1;
        List<ReqFinish> items = new ArrayList<ReqFinish>();
        Connection conn = null;
		try {
           
			sql.append("\n select i.box_no,i.job_id,j.warehouse,j.job_name  ,count(*) as qty ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE h, PENSBI.PENSBME_PICK_BARCODE_ITEM i , \n");
			sql.append("\n ( select distinct job_id,warehouse ,name as job_name from PENSBME_PICK_JOB where warehouse ='W2' ) j");
			sql.append("\n where 1=1  ");
			sql.append("\n and h.job_id = j.job_id ");
			sql.append("\n and h.job_id = i.job_id ");
			sql.append("\n and h.box_no = i.box_no ");
			sql.append("\n and h.status = '"+JobDAO.STATUS_CLOSE+"'");
			sql.append("\n and i.status = '"+JobDAO.STATUS_CLOSE+"'");
			// and boxNo is not pick stock
			sql.append("\n and h.box_no not in(");
			sql.append("\n    select distinct l.box_no from " );
			sql.append("\n    PENSBI.PENSBME_PICK_STOCK h ,PENSBI.PENSBME_PICK_STOCK_I l where 1=1 ");
			sql.append("\n    and h.issue_req_no = l.issue_req_no ");
			sql.append("\n    and ISSUE_REQ_STATUS <> '"+STATUS_CANCEL+"'");
			sql.append("\n) ");
			
			sql.append("\n group by i.box_no ,i.job_id ,j.warehouse,j.job_name ");
			sql.append("\n order by i.box_no asc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new ReqFinish();
		
			   h.setJobId(rst.getString("job_id"));
			   h.setJobName(rst.getString("job_name"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setQty(rst.getInt("qty"));
			
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
	
	
	
	public static ReqFinish save(ReqFinish h) throws Exception{
		Connection conn = null;

		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//check requestNo
			if(Utils.isNull(h.getRequestNo()).equals("")){
				//Gen requestNo
				h.setRequestNo(genRequestNo(conn,new Date()) );
				h.setStatus(STATUS_OPEN);
				logger.debug("RequestNO:"+h.getRequestNo());
				
				//save Head req 
			    saveHeadModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ReqFinish l = (ReqFinish)h.getItems().get(i);
					   l.setRequestDate(h.getRequestDate());
					   l.setRequestNo(h.getRequestNo());
					   l.setStatus(h.getStatus());
					   l.setRemark(h.getRemark());
					   
					   //save item from barcode by job_id box_no
					   Map<String,String> pensItemMap = insertFinishBarcodeFromBarcodeItem(conn,l);
					   //pensItemMapAll.putAll(pensItemMap);
				       
				       //Set barcode status = W (work in process)
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setStatus(JobDAO.STATUS_WORK_IN_PROCESS);
				       
				       BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
				       BarcodeDAO.updateBarcodeLineStatusModelByPK(conn, b);
				   }
				}
			}else{
				//update status barcode to close (old status)
				List<ReqFinish> saveData = ReqFinishDAO.searchHead(h,true);
				if(saveData != null && saveData.size()>0){
				   ReqFinish oldReq = (ReqFinish)saveData.get(0);
				   for(int i=0;i<oldReq.getItems().size();i++){
					   ReqFinish l = (ReqFinish)oldReq.getItems().get(i);
					   
					   //Set barcode status = return
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setStatus(JobDAO.STATUS_WORK_IN_PROCESS);
				       
				       BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
				       BarcodeDAO.updateBarcodeLineStatusModelByPK(conn, b);
				   }
				}
				
				//update Head req 
			    updateHeadModel(conn, h);
			    
				//delete item req
				deleteItemModel(conn, h);
				
				//save line req
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ReqFinish l = (ReqFinish)h.getItems().get(i);
					   l.setRequestDate(h.getRequestDate());
					   l.setRequestNo(h.getRequestNo());
					   l.setStatus(h.getStatus());
					   l.setRemark(h.getRemark());
					   
					   //save item from barcode by job_id box_no
					   Map<String,String> pensItemMap = insertFinishBarcodeFromBarcodeItem(conn,l);
					   //pensItemMapAll.putAll(pensItemMap);
				       
				       
					   //Set barcode status = W (work in process)
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setStatus(JobDAO.STATUS_WORK_IN_PROCESS);
				       
				       BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
				       BarcodeDAO.updateBarcodeLineStatusModelByPK(conn, b);
				   }
				}
			}
			
			conn.commit();
		}catch(Exception e){
		  conn.rollback();
		  throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return h;
	}
	
	// 	RQYYMMXXX  ( เช่น RQ5703001 )  			
	 private static String genRequestNo(Connection conn,Date date) throws Exception{
       String docNo = "";
		   try{
			   
			   String today = df.format(date);
			   String[] d1 = today.split("/");
			  // System.out.println("d1[0]:"+d1[0]);
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			 //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"REQ_FINISHING","REQUEST_NO",date);
			   
			   docNo = "F"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("0000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }
		  return docNo;
	}
		 
	 private static void saveHeadModel(Connection conn,ReqFinish o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			int c =1;
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_REQ_FINISHING \n");
				sql.append(" ( REQUEST_DATE,REQUEST_NO ,TOTAL_BOX,TOTAL_QTY \n");
				sql.append("  ,CREATE_DATE ,CREATE_USER,STATUS,REMARK) \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				Date openDate = Utils.parse( o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setString(c++, o.getRequestNo());
				ps.setInt(c++, o.getTotalBox());
				ps.setInt(c++, o.getTotalQty());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getRemark());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
	 public static void updateHeadModel(Connection conn,ReqFinish o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_REQ_FINISHING SET  \n");
				sql.append(" remark =? ,request_date =? ,update_user =?,update_date =?,total_box =? ,total_qty =?   \n");
				sql.append(" WHERE  REQUEST_NO = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
				Date openDate = Utils.parse( o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				ps.setString(c++, o.getRemark());
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, o.getTotalBox());
				ps.setInt(c++, o.getTotalQty());
				
				ps.setString(c++, Utils.isNull(o.getRequestNo()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 
	 private static Map<String ,String> insertFinishBarcodeFromBarcodeItem(Connection conn,ReqFinish o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			ReqFinish h = null;
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
				  
				   h = new ReqFinish();
				   h.setRequestNo(o.getRequestNo());
				   h.setLineId(rst.getInt("line_id"));
				   h.setJobId(rst.getString("job_id"));
				   h.setBoxNo(rst.getString("box_no"));
				   h.setLineStatus(PickConstants.STATUS_OPEN);
				  
				   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
				   h.setGroupCode(rst.getString("group_code"));
				   h.setPensItem(rst.getString("pens_item"));
				   h.setBarcode(Utils.isNull(rst.getString("barcode")));
				   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
				   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
				   
				   h.setCreateUser(o.getCreateUser());
				   h.setUpdateUser(o.getUpdateUser());
				   
				   //save pick_stock_line
				   saveReqFinishingBarcodeModel(conn, h);
				   
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
	 
	 
	 private static void saveReqFinishingBarcodeModel(Connection conn,ReqFinish o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_REQ_FINISHING_BARCODE \n");
				sql.append(" (REQUEST_NO,job_id, LINE_ID,BOX_NO,PENS_ITEM,material_Master,group_code," +
						"WHOLE_PRICE_BF,RETAIL_PRICE_BF ,CREATE_DATE,CREATE_USER,STATUS" +
						",BARCODE)  \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ? , ? ,? , ?, ?, ?, ?, ?) \n");
				
				ps = conn.prepareStatement(sql.toString());
					
				int c =1;
				
				ps.setString(c++, o.getRequestNo());
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
				ps.setString(c++, o.getLineStatus());
				ps.setString(c++, o.getBarcode());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	public static void updateHeadStatusModel(Connection conn,ReqFinish o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_REQ_FINISHING SET  \n");
				sql.append(" STATUS = ? ,update_user =?,update_date =?   \n");
				sql.append(" WHERE  REQUEST_NO = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.setString(c++, Utils.isNull(o.getRequestNo()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	
	public static void updateItemStatusModel(Connection conn,ReqFinish o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_REQ_FINISHING_BARCODE SET  \n");
			sql.append(" STATUS = ? ,update_user =?,update_date =?   \n");
			sql.append(" WHERE  REQUEST_NO = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setString(c++, o.getLineStatus());
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			
			ps.setString(c++, Utils.isNull(o.getRequestNo()));
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
		
		public static void deleteItemModel(Connection conn,ReqFinish o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE FROM PENSBI.PENSBME_REQ_FINISHING_BARCODE  \n");
				sql.append(" WHERE  REQUEST_NO = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, Utils.isNull(o.getRequestNo()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
	
}
