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
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;

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
			sql.append("\n select i.* from PENSBI.PENSBME_REQ_FINISHING i ");
			sql.append("\n where 1=1   ");
			
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
				Date tDate  = DateUtil.parse(h.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();
			while(rst.next()) {
	
				   h.setNo(r);
				   h.setRequestDate(DateUtil.stringValue(rst.getTimestamp("request_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setConfirmDate(DateUtil.stringValue(rst.getTimestamp("confirm_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setRemark(Utils.isNull(rst.getString("remark"))); 
				   h.setWareHouse(Utils.isNull(rst.getString("warehouse"))); 
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
	
	public static int searchTotalHead(Connection conn,ReqFinish o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		int c = 1;
		try {
			sql.append("\n select count(*) as c from PENSBI.PENSBME_REQ_FINISHING i \n");
			sql.append("\n where 1=1   \n");
			sql.append(genWhereSqlSearchHead(o));
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				 totalRec = rst.getInt("c");
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRec;
	}
	
	
	
	public static List<ReqFinish> searchHead(Connection conn,ReqFinish o,boolean getItems ,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqFinish h = null;
		List<ReqFinish> items = new ArrayList<ReqFinish>();
		int r = 1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n 		select i.* from PENSBI.PENSBME_REQ_FINISHING i ");
			sql.append("\n 		where 1=1   ");
			sql.append(genWhereSqlSearchHead(o));
			sql.append("\n 		order by i.request_no desc ");
            sql.append("\n   )A ");
            
        	// get record start to end 
            if( !allRec){
        	  sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
        	sql.append("\n )M  ");
			if( !allRec){
			   sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
			}
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				   h = new ReqFinish();
				   h.setNo(r);
				   h.setRequestDate(DateUtil.stringValue(rst.getTimestamp("request_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setRemark(Utils.isNull(rst.getString("remark"))); 
				   h.setTotalBox(rst.getInt("total_box"));
				   h.setTotalQty(rst.getInt("total_qty"));
				   h.setWareHouse(Utils.isNull(rst.getString("WAREHOUSE"))); 
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
			} catch (Exception e) {}
		}
		return items;
	}

	private static StringBuffer genWhereSqlSearchHead(ReqFinish o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		if( !Utils.isNull(o.getRequestDate()).equals("")){
			//BudishDate
			Date tDate  = DateUtil.parse(o.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateChristStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n 		and i.request_date = to_date('"+dateChristStr+"','dd/mm/yyyy')");
		}
		if( !Utils.isNull(o.getStatus()).equals("")){
			sql.append("\n 		and i.status = '"+Utils.isNull(o.getStatus())+"'");
		}
		if( !Utils.isNull(o.getRequestNo()).equals("")){
			sql.append("\n 		and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
		}
		if( !Utils.isNull(o.getWareHouse()).equals("")){
			sql.append("\n 		and i.WAREHOUSE = '"+Utils.isNull(o.getWareHouse())+"'");
		}
		
		
		return sql;
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
	
	
	public static List<ReqFinish> searchBarcoceItemW2_W4(String wareHouse) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqFinish h = null;
		int r = 1;
        List<ReqFinish> items = new ArrayList<ReqFinish>();
        Connection conn = null;
		try {
           
			sql.append("\n select i.box_no,i.job_id,j.job_name  ,count(*) as qty ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE h, PENSBI.PENSBME_PICK_BARCODE_ITEM i , \n");
			sql.append("\n ( select distinct job_id,warehouse ,name as job_name from PENSBME_PICK_JOB ) j");
			sql.append("\n where 1=1  ");
			sql.append("\n and h.job_id = j.job_id ");
			sql.append("\n and h.job_id = i.job_id ");
			sql.append("\n and h.box_no = i.box_no ");
			if( !Utils.isNull(wareHouse).equals("")){
			  sql.append("\n and h.warehouse ='"+Utils.isNull(wareHouse)+"' ");
			}
			sql.append("\n and h.status = '"+JobDAO.STATUS_CLOSE+"'");
			sql.append("\n and i.status = '"+JobDAO.STATUS_CLOSE+"'");

			sql.append("\n group by i.box_no ,i.job_id ,j.job_name ");
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
				h.setRequestNo(genRequestNo(new Date()) );
				h.setStatus(STATUS_OPEN);
				logger.debug("RequestNO:"+h.getRequestNo());
				
				//save Head req 
			    saveHeadModel(conn, h);
				
			    //step insert new req finishing item
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ReqFinish l = (ReqFinish)h.getItems().get(i);
					   l.setRequestDate(h.getRequestDate());
					   l.setRequestNo(h.getRequestNo());
					   l.setStatus(h.getStatus());
					   l.setRemark(h.getRemark());
					   
					   //save item from barcode by job_id box_no
					   Map<String,String> pensItemMap = insertFinishBarcodeFromBarcodeItemAndUpdateBarcodeItem(conn,l);
					   //pensItemMapAll.putAll(pensItemMap);
				       
				       //Set barcode status = W (work in process)
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setCreateUser(h.getCreateUser());
				       b.setUpdateUser(h.getUpdateUser());
				       b.setStatus(JobDAO.STATUS_WORK_IN_PROCESS);
				       
				       BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
				       
				   }
				}
			}else{

				//step1 update new total Head req 
			    updateHeadModel(conn, h);
			    
				//Step2 delete reqFiishingItem and update barcode item status='CLOSE'
				deleteReqFinishingItemItemAndUpdateBarcodeToClose(conn,h);
			
				//step3 insert new req finishing item
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ReqFinish l = (ReqFinish)h.getItems().get(i);
					   l.setRequestDate(h.getRequestDate());
					   l.setRequestNo(h.getRequestNo());
					   l.setStatus(h.getStatus());
					   l.setRemark(h.getRemark());
					   
					   //save item from barcode by job_id box_no
					   Map<String,String> pensItemMap = insertFinishBarcodeFromBarcodeItemAndUpdateBarcodeItem(conn,l);
					   //pensItemMapAll.putAll(pensItemMap);
				       
					   //step4.1 Set barcode status = W (work in process)
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setStatus(JobDAO.STATUS_WORK_IN_PROCESS);
				       
				       BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
				     
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
	
	//1-delete ReqFinishing Item 2, update status barcode item and head
	private static void deleteReqFinishingItemItemAndUpdateBarcodeToClose(Connection conn,ReqFinish h) throws Exception{
		logger.debug("--deleteReqFinishingItemItemAndUpdateBarcodeToClose--");
		int no = 1;
		try{
		    List<ReqFinish> reqFinishingItemList = searchreqFinishingItemListByRequestNo(conn, h);
		    
		    //delete req finishing item all
			 deleteReqFinishingItemModel(conn, h);
			   
			logger.debug("reqFinishingItemList:"+reqFinishingItemList.size());
			   
			  if(reqFinishingItemList !=null && reqFinishingItemList.size() >0){
				  
				   for(int k=0;k<reqFinishingItemList.size();k++){
					   ReqFinish p = (ReqFinish)reqFinishingItemList.get(k);
					   p.setCreateUser(h.getCreateUser());
					   p.setUpdateUser(h.getUpdateUser());

				       //update barcode item (status = C)
				       p.setLineStatus(PickConstants.STATUS_CLOSE);
				       updateStatusBarcodeItemModel(conn, p);
				       
					   no++;
				   }//for 2
			   }//if 
		}catch(Exception e){
			throw e;
		}
	}
		
	public static List<ReqFinish> searchreqFinishingItemListByRequestNo(Connection conn,ReqFinish o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqFinish h = null;
		int r = 1;
        List<ReqFinish> items = new ArrayList<ReqFinish>();
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_REQ_FINISHING_BARCODE i   \n");
			sql.append("\n where 1=1   \n");
			sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			
			//sql.append("\n order by line_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ReqFinish();
			   h.setRequestNo(rst.getString("request_no"));
			   
			   h.setLineId(rst.getInt("line_id"));//Line_id(Barcode by boxNo)
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

	
	public static void updateBarcodeToCloseFromReqFinishingItem(Connection conn,ReqFinish h) throws Exception{
		logger.debug("--updateBarcodeToCloseFromReqFinishingItem--");
		int no = 1;
		try{
		    List<ReqFinish> reqFinishingItemList = searchreqFinishingItemListByRequestNo(conn, h);
		       
			logger.debug("reqFinishingItemList:"+reqFinishingItemList.size());
			   
			  if(reqFinishingItemList !=null && reqFinishingItemList.size() >0){
				  
				   for(int k=0;k<reqFinishingItemList.size();k++){
					   ReqFinish p = (ReqFinish)reqFinishingItemList.get(k);
					   p.setCreateUser(h.getCreateUser());
					   p.setUpdateUser(h.getUpdateUser());

					   //delete Pick Stock Line
					   logger.debug("no["+(no)+"]update barcode item:"+p.getPensItem()+",boxNo["+p.getBoxNo()+"]lineId:"+p.getLineId());

				       //update barcode item (status = C)
				       p.setLineStatus(PickConstants.STATUS_CLOSE);
				       updateStatusBarcodeItemModel(conn, p);
				       
					   no++;
				   }//for 2
			   }//if 
					  
			
		}catch(Exception e){
			throw e;
		}
	}
		
	// 	RQYYMMXXX  ( เช่น RQ5703001 )  			
	 private static String genRequestNo(Date date) throws Exception{
       String docNo = "";
       Connection conn = null;
		   try{
			   conn = DBConnection.getInstance().getConnection();
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
		   }finally{
			   if(conn != null){
				   conn.close();conn=null;
			   }
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
				sql.append("  ,CREATE_DATE ,CREATE_USER,STATUS,REMARK,WAREHOUSE) \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				Date openDate = DateUtil.parse( o.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setString(c++, o.getRequestNo());
				ps.setInt(c++, o.getTotalBox());
				ps.setInt(c++, o.getTotalQty());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getRemark());
				ps.setString(c++, Utils.isNull(o.getWareHouse()));
				
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
				sql.append(" remark =? ,request_date =? ,update_user =?,update_date =?,total_box =? ,total_qty =? ,WAREHOUSE =?   \n");
				sql.append(" WHERE  REQUEST_NO = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
				Date openDate = DateUtil.parse( o.getRequestDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				ps.setString(c++, o.getRemark());
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, o.getTotalBox());
				ps.setInt(c++, o.getTotalQty());
				ps.setString(c++, Utils.isNull(o.getWareHouse()));
				
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
	 
	 
	 private static Map<String ,String> insertFinishBarcodeFromBarcodeItemAndUpdateBarcodeItem(Connection conn,ReqFinish o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			ReqFinish h = null;
			int r = 1;
	        Map<String ,String> pensItemMap = new HashMap<String, String>();
			try {
				sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
				sql.append("\n where 1=1 \n");
				sql.append("\n and i.status ='"+PickConstants.STATUS_CLOSE+"'");
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
				   
				   /** save pick_stock_line **/
				   saveReqFinishingBarcodeModel(conn, h);
				   
				   /** update Barcode Item to W **/
			       h.setLineStatus(JobDAO.STATUS_WORK_IN_PROCESS);
				   updateStatusBarcodeItemModel(conn,h);
				   
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
	 
	 public static void updateStatusBarcodeItemModel(Connection conn,ReqFinish o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE_ITEM SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE  BOX_NO = ? and  material_master =? and group_code =? and pens_item = ? and job_id = ? and line_id =? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getLineStatus());
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
	
	public static void updateReqFinishingItemStatusModel(Connection conn,ReqFinish o) throws Exception{
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
		
	public static void deleteReqFinishingItemModel(Connection conn,ReqFinish o) throws Exception{
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
