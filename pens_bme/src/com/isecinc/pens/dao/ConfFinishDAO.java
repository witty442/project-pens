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

public class ConfFinishDAO extends PickConstants{

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
	
	public static List<ReqFinish> searchHead(Connection conn,ReqFinish o,boolean getItems ) throws Exception {
		return searchHeadModel(conn, o, getItems);
	}
	
	public static List<ReqFinish> searchHead(ReqFinish o,boolean getItems ) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			
			return searchHeadModel(conn, o, getItems);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
		
	public static List<ReqFinish> searchHeadModel(Connection conn,ReqFinish o,boolean getItems ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
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
			if( !Utils.isNull(o.getConfirmDate()).equals("")){
				sql.append("\n and i.CONFIRM_DATE = ? ");
			}
			
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and i.status = '"+Utils.isNull(o.getStatus())+"'");
			}
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and i.WAREHOUSE = '"+Utils.isNull(o.getWareHouse())+"'");
			}
			
			sql.append("\n order by i.request_no desc ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			if( !Utils.isNull(o.getRequestDate()).equals("")){
				Date tDate  = Utils.parse(o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			if( !Utils.isNull(o.getConfirmDate()).equals("")){
				Date tDate  = Utils.parse(o.getConfirmDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();

			while(rst.next()) {
				   h = new ReqFinish();
				   h.setNo(r);
				   h.setRequestDate(Utils.stringValue(rst.getTimestamp("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setConfirmDate(Utils.stringValue(rst.getTimestamp("confirm_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
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
 
				//get Items
				if(getItems){
				//	h.setItems(searchItem(conn, h));
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
	
	
	public static List<ReqFinish> searchItemByGroupCode(Connection conn,ReqFinish o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqFinish h = null;
		int r = 1;
		int c = 1;
        List<ReqFinish> items = new ArrayList<ReqFinish>();
		try {
			sql.append("   select i.group_code ,count(*) as qty ");
			sql.append("\n from PENSBI.PENSBME_REQ_FINISHING_BARCODE i ");
			sql.append("\n where 1=1  \n");
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			sql.append("\n group by i.group_code  ");
			sql.append("\n order by i.group_code  ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ReqFinish();
			   h.setNo(r);
			   h.setGroupCode(rst.getString("group_code"));
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

	public static List<ReqFinish> searchItemByBox(Connection conn,ReqFinish o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqFinish h = null;
		int r = 1;
		int c = 1;
        List<ReqFinish> items = new ArrayList<ReqFinish>();
		try {
			sql.append("   select i.box_no ,count(*) as qty ");
			sql.append("\n from PENSBI.PENSBME_REQ_FINISHING_BARCODE i ");
			sql.append("\n where 1=1  \n");
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			sql.append("\n group by i.box_no  ");
			sql.append("\n order by i.box_no  ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new ReqFinish();
			   h.setNo(r);
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
			} catch (Exception e) {}
		}
		return items;
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
	
	
	public static ReqFinish save(Connection conn,ReqFinish h) throws Exception{
		try{
			updateREQ_FINISHING(conn, h);
			updateREQ_FINISHING_BARCODE(conn,h);
			
			//Get From REQ_FINISHING_BARCODE
			List<Barcode> barcodeList = searchReqFinishingBarcodeItemList(conn,h);
			
			if(barcodeList != null && barcodeList.size() >0){
				for(int i=0;i<barcodeList.size();i++){
				 //Set barcode status = FINISH(F)
			      Barcode b = (Barcode)barcodeList.get(i);
			      b.setStatus(PickConstants.STATUS_FINISH);
			      b.setUpdateUser(h.getUpdateUser());
			       
			      BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
			     
				}
				//update barcode item to Finish
				updateBarcodeToFinishFromReqFinishingItem(conn,h);
			}
			
		}catch(Exception e){
		  throw e;
		}finally{
		}
		return h;
	}
	
	public static void updateBarcodeToFinishFromReqFinishingItem(Connection conn,ReqFinish h) throws Exception{
		logger.debug("--updateBarcodeToFinishFromReqFinishingItem--");
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

				       //update barcode item (status = F)
				       p.setLineStatus(PickConstants.STATUS_FINISH);
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
	
	public static List<Barcode> searchReqFinishingBarcodeItemList(Connection conn,ReqFinish o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode h = null;
		int r = 1;
		int c = 1;
        List<Barcode> items = new ArrayList<Barcode>();
		try {
			sql.append(" select distinct job_id,box_no from PENSBI.PENSBME_REQ_FINISHING_BARCODE i ");
			sql.append("\n where 1=1   \n");
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
		
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new Barcode();
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
	
	
	 private static void updateREQ_FINISHING(Connection conn,ReqFinish o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_REQ_FINISHING SET  \n");
				sql.append(" confirm_date =? ,status = ? ,update_user =?,update_date =?  \n");
				sql.append(" WHERE  REQUEST_NO = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
				Date openDate = Utils.parse( o.getConfirmDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
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

	 private static void updateREQ_FINISHING_BARCODE(Connection conn,ReqFinish o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateREQ_FINISHING_BARCODE");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_REQ_FINISHING_BARCODE SET  \n");
				sql.append(" status = ? ,update_user =?,update_date =?  \n");
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
		
	
}
