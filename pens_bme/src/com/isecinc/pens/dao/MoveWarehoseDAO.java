package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.components.barcode4j.BarcodeUtils;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.MoveWarehouse;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class MoveWarehoseDAO extends PickConstants{
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public static ReqPickStock save(Connection conn,ReqPickStock h,Map<String, ReqPickStock> dataSaveMapAll) throws Exception{
		ReqPickStock result = new ReqPickStock();
		result.setResultProcess(true);//default
		try{
			
			return result;
		}catch(Exception e){
		  throw e;
		}finally{
			
		}
	}
	
	public static MoveWarehouse searchHead(MoveWarehouse o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		MoveWarehouse h = null;
		List<MoveWarehouse> items = new ArrayList<MoveWarehouse>();
		int totalBox = 0;
		int totalQty = 0;
		int no=1;
		try {
			sql.append("\n SELECT box_no, count(*) as qty ");
			sql.append("\n from PENSBME_PICK_BARCODE");
			sql.append("\n where 1=1   ");
			sql.append("\n and status ='"+PickConstants.STATUS_CLOSE+"'");
			sql.append("\n and job_id ="+o.getJobId()+"");
			sql.append("\n group by box_no  ");
			sql.append("\n order by box_no  ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new MoveWarehouse();
			   h.setNo(no);
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setQty(Utils.isNull(rst.getString("qty")));
			   
			   totalQty += Utils.convertStrToInt(h.getQty());
			   
			   
			   items.add(h);
			   totalBox++;
			   no++;
			}//while

			o.setTotalBox(totalBox+"");
			o.setTotalQty(totalQty+"");
			o.setItems(items);
			if(items != null && items.size() >0){
			  o.setCanEdit(true);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	
	public static MoveWarehouse searchHeadForNewJob(MoveWarehouse h ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		List<MoveWarehouse> items = new ArrayList<MoveWarehouse>();
		int totalBox = 0;
		int totalQty = 0;
		int no=1;
		try {
			sql.append("\n SELECT box_no, count(*) as qty ");
			sql.append("\n from PENSBME_PICK_BARCODE");
			sql.append("\n where 1=1   ");
			sql.append("\n and status ='"+PickConstants.STATUS_CLOSE+"'");
			sql.append("\n and job_id ="+h.getNewJobId()+"");
			sql.append("\n group by box_no  ");
			sql.append("\n order by box_no  ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h.setNo(no);
			   h.setSelected("true");
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setQty(Utils.isNull(rst.getString("qty")));
			   
			   totalQty += Utils.convertStrToInt(h.getQty());
			   
			   
			   items.add(h);
			   totalBox++;
			   no++;
			}//while

			h.setTotalBox(totalBox+"");
			h.setTotalQty(totalQty+"");
			h.setItems(items);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return h;
	}
	
	public static boolean moveBarcodeToNewWarehouse(Connection conn,Job newJob,List<MoveWarehouse> boxNoList ) throws Exception {
		try {
			if(boxNoList != null && boxNoList.size() >0){
				for(int i=0;i<boxNoList.size();i++){
					 MoveWarehouse o = boxNoList.get(i);
					 
					 Barcode oldBarcodeHead = getBarcodeHead(conn, newJob, o);
					 if(oldBarcodeHead != null){
						 //update old record to Move M
						 oldBarcodeHead.setStatus(PickConstants.STATUS_MOVE);
						 BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, oldBarcodeHead);
						 
						 //insert new barcode item
						 Barcode newBarcodeHead = new Barcode();
						 newBarcodeHead.setJobId(newJob.getJobId());//new job id
						 newBarcodeHead.setBoxNo(BarcodeDAO.genBoxNo(conn, new Date()));
						 newBarcodeHead.setTransactionDate(newJob.getOpenDate());
						 newBarcodeHead.setStatus(PickConstants.STATUS_CLOSE);
						 newBarcodeHead.setRemark(oldBarcodeHead.getRemark());
						 
						 BarcodeDAO.saveHeadModel(conn,newBarcodeHead);
						 
						 //insert barcode item from oldBarcode
						 insertBarcodeItem(conn, oldBarcodeHead, newBarcodeHead);
						 
					 }
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
			
			} catch (Exception e) {}
		}
		return true;
	}
	
	public static Barcode getBarcodeHead(Connection conn,Job newJob,MoveWarehouse o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode h = null;
		try {
			sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE i   \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new Barcode();
			  
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setCreateUser(o.getCreateUser());
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setRemark(Utils.isNull(rst.getString("remark"))); 

			
			}//while

			return h;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}

	}
	
	public static void insertBarcodeItem(Connection conn,Barcode oldBarcode ,Barcode newBarcode ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode h = null;
		try {
			sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(oldBarcode.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(oldBarcode.getJobId())+"");
			}
			if( !Utils.isNull(oldBarcode.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(oldBarcode.getBoxNo())+"'");
			}
			sql.append("\n order by i.line_id asc ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new Barcode();
			   h.setLineId(rst.getInt("line_id"));
			   h.setJobId(newBarcode.getJobId());//new job id
			   h.setBoxNo(newBarcode.getBoxNo());//new boxNo
			   h.setStatus(newBarcode.getStatus());//close
			   
			   h.setBarcode(rst.getString("barcode"));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			

			   BarcodeDAO.saveItemModel(conn, h);

			   
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}

	}
	
	public static void updateStatusStockIssueItem(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
			sql.append(" SET  STATUS=?,STATUS_DATE=? ,UPDATE_DATE =?,UPDATE_USER =? \n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			
			ps.setString(c++, o.getStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
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
	
}
