package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.ReqReturnWacoal;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class ReqReturnWacoalDAO extends PickConstants{

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
	public static List<ReqReturnWacoal> searchHead(ReqReturnWacoal o,boolean getItems ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ReqReturnWacoal h = null;
		List<ReqReturnWacoal> items = new ArrayList<ReqReturnWacoal>();
		int r = 1;
		int c = 1;
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_PICK_REQ_RETURN i \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getRequestDate()).equals("")){
				sql.append("\n and i.REQUEST_DATE = ? ");
			}
			
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and i.request_status = '"+Utils.isNull(o.getStatus())+"'");
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
				   h = new ReqReturnWacoal();
				   h.setNo(r);
				   h.setRequestDate(Utils.stringValue(rst.getTimestamp("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
				   h.setStatus(Utils.isNull(rst.getString("request_status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("request_status")))); 
				   h.setRemark(Utils.isNull(rst.getString("remark"))); 
				   h.setTotalBox(rst.getInt("total_box"));
				   h.setTotalQty(rst.getInt("total_qty"));
				   
				   if(Utils.isNull(rst.getString("request_status")).equals(STATUS_CANCEL) 
					|| Utils.isNull(rst.getString("request_status")).equals(STATUS_USED) ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
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
	
	
	public static List<ReqReturnWacoal> searchItem(Connection conn,ReqReturnWacoal o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqReturnWacoal h = null;
		int r = 1;
		int c = 1;
        List<ReqReturnWacoal> items = new ArrayList<ReqReturnWacoal>();
		try {
			sql.append("\n select i.* " +
			          " \n ,(select max(name) from PENSBME_PICK_JOB j where j.job_id= i.job_id) as job_name "+
					  " \n from PENSBI.PENSBME_PICK_REQ_RETURN_I i ");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			
			sql.append("\n order by i.request_no ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new ReqReturnWacoal();
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
	
	
	public static List<ReqReturnWacoal> searchBarcoceItemW1() throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqReturnWacoal h = null;
		int r = 1;
        List<ReqReturnWacoal> items = new ArrayList<ReqReturnWacoal>();
        Connection conn = null;
		try {
           
			sql.append("\n select i.box_no,i.job_id,j.warehouse,j.job_name  ,count(*) as qty ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE h, PENSBI.PENSBME_PICK_BARCODE_ITEM i , \n");
			sql.append("\n ( select distinct job_id,warehouse ,name as job_name from PENSBME_PICK_JOB where warehouse ='W1' ) j");
			sql.append("\n where 1=1  ");
			sql.append("\n and h.job_id = j.job_id ");
			sql.append("\n and h.job_id = i.job_id ");
			sql.append("\n and h.box_no = i.box_no ");
			sql.append("\n and h.status = '"+JobDAO.STATUS_CLOSE+"'");
			sql.append("\n and ( i.status = '"+JobDAO.STATUS_CLOSE+"' OR i.status ='' OR i.status is null)");
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
			  
			   h = new ReqReturnWacoal();
		
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
	
	
	public static ReqReturnWacoal save(ReqReturnWacoal h) throws Exception{
		Connection conn = null;

		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//check requestNo
			if(Utils.isNull(h.getRequestNo()).equals("")){
				//Gen requestNo
				h.setRequestNo(genRequestNo(conn,new Date()) );
				h.setStatus(STATUS_NEW);
				logger.debug("RequestNO:"+h.getRequestNo());
				
				//save Head req 
			    saveHeadModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ReqReturnWacoal l = (ReqReturnWacoal)h.getItems().get(i);
					   l.setRequestDate(h.getRequestDate());
					   l.setRequestNo(h.getRequestNo());
					   l.setStatus(h.getStatus());
					   l.setRemark(h.getRemark());
					   
					   //save item req 
				       saveItemModel(conn, l);
				       
				       //Set barcode status = return
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setStatus(JobDAO.STATUS_RETURN);
				       
				       BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
				       BarcodeDAO.updateBarcodeLineStatusModelByPK(conn, b);
				   }
				}
			}else{
				//update status barcode to close (old status)
				List<ReqReturnWacoal> saveData = ReqReturnWacoalDAO.searchHead(h,true);
				if(saveData != null && saveData.size()>0){
				   ReqReturnWacoal oldReq = (ReqReturnWacoal)saveData.get(0);
				   for(int i=0;i<oldReq.getItems().size();i++){
					   ReqReturnWacoal l = (ReqReturnWacoal)oldReq.getItems().get(i);
					   
					   //Set barcode status = return
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setStatus(JobDAO.STATUS_CLOSE);
				       
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
					   ReqReturnWacoal l = (ReqReturnWacoal)h.getItems().get(i);
					   l.setRequestDate(h.getRequestDate());
					   l.setRequestNo(h.getRequestNo());
					   l.setStatus(h.getStatus());
					   l.setRemark(h.getRemark());
					   
				       saveItemModel(conn, l);
				       
				       //Set barcode status = return
				       Barcode b = new Barcode();
				       b.setJobId(l.getJobId());
				       b.setBoxNo(l.getBoxNo());
				       b.setStatus(JobDAO.STATUS_RETURN);
				       
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
			   int seq = SequenceProcess.getNextValue(conn,"RETURN_WACOAL","REQUEST_NO",date);
			   
			   docNo = "RQ"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }
		  return docNo;
	}
		 
	 private static void saveHeadModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			int c =1;
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_REQ_RETURN \n");
				sql.append(" ( REQUEST_DATE,REQUEST_NO ,TOTAL_BOX,TOTAL_QTY \n");
				sql.append("  ,CREATE_DATE ,CREATE_USER,REQUEST_STATUS,REMARK) \n");
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
		
	 public static void updateHeadModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_REQ_RETURN SET  \n");
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
	 
	 private static void saveItemModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			int c =1;
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_REQ_RETURN_I \n");
				sql.append(" ( REQUEST_NO ,LINE_ID ,JOB_ID, BOX_NO, QTY   \n");
				sql.append("  ,CREATE_DATE ,CREATE_USER) \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ? ) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, o.getRequestNo());
				ps.setInt(c++, o.getLineId());
				ps.setString(c++, o.getJobId());
				ps.setString(c++, o.getBoxNo());
				ps.setInt(c++, o.getQty());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				
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
		
	 
	public static void updateStatusModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_REQ_RETURN SET  \n");
				sql.append(" REQUEST_STATUS = ? ,update_user =?,update_date =?   \n");
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
		
		public static void deleteItemModel(Connection conn,ReqReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE FROM PENSBI.PENSBME_PICK_REQ_RETURN_I  \n");
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
