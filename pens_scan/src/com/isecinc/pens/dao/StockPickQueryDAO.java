package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class StockPickQueryDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	
	public static List<Barcode> searchSummaryByDetail(Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Barcode h = null;
		List<Barcode> items = new ArrayList<Barcode>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select h.box_no ,l.line_id");
			sql.append("\n , (select max(name) from PENSBME_PICK_JOB j where h.job_id = j.job_id) as name ");
			sql.append("\n ,h.job_id, l.material_master ,l.group_code,l.pens_item,l.barcode,l.status ");
			if( !Utils.isNull(o.getStatus()).equals("") && "All".equalsIgnoreCase(Utils.isNull(o.getStatus()))){
				sql.append("\n ,p.remark  ");
			}else{
				sql.append("\n ,'' as remark  ");
			}
			
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE h,PENSBI.PENSBME_PICK_BARCODE_ITEM l ");
			
			if( !Utils.isNull(o.getStatus()).equals("") && "All".equalsIgnoreCase(Utils.isNull(o.getStatus()))){
				sql.append("\n left outer join( ");
				sql.append("\n   select ph.remark ,pi.job_id,pi.box_no,pi.line_id,pi.material_master,pi.group_code,pi.pens_item" );
				sql.append("\n   from PENSBME_PICK_STOCK ph,PENSBME_PICK_STOCK_I pi ");
				sql.append("\n   where ph.ISSUE_REQ_NO = pi.ISSUE_REQ_NO ");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and pi.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and pi.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and pi.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and pi.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				if( !Utils.isNull(o.getBoxNoFrom()).equals("") && !Utils.isNull(o.getBoxNoTo()).equals("")){
					sql.append("\n and pi.box_no >= '"+Utils.isNull(o.getBoxNoFrom())+"'");
					sql.append("\n and pi.box_no <= '"+Utils.isNull(o.getBoxNoTo())+"'");
				}
				if( !Utils.isNull(o.getJobId()).equals("")){
					sql.append("\n and pi.job_id = "+Utils.isNull(o.getJobId())+"");
				}
				sql.append("\n ) p on p.job_id = l.job_id and p.box_no = l.box_no and  p.line_id = l.line_id and p.material_master = l.material_master ");
				sql.append("\n  and p.group_code = l.group_code  and p.pens_item = l.pens_item ");
			}
			
			sql.append("\n where h.job_id = l.job_id and h.box_no = l.box_no ");
			
			if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
				sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
				sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
			}
			
			if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
				sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
				sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
			}
			
			if( !Utils.isNull(o.getBoxNoFrom()).equals("") && !Utils.isNull(o.getBoxNoTo()).equals("")){
				sql.append("\n and l.box_no >= '"+Utils.isNull(o.getBoxNoFrom())+"'");
				sql.append("\n and l.box_no <= '"+Utils.isNull(o.getBoxNoTo())+"'");
			}
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and h.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			
			if( !Utils.isNull(o.getStatus()).equals("")){
				if("Onhand".equals(Utils.isNull(o.getStatus()))){
				   sql.append("\n and h.status = '"+JobDAO.STATUS_CLOSE+"'");
				   sql.append("\n and l.status = '"+JobDAO.STATUS_CLOSE+"'");
				}else if("Scanning".equals(Utils.isNull(o.getStatus()))){
				   sql.append("\n and h.status = '"+JobDAO.STATUS_OPEN+"'");
				   sql.append("\n and l.status = '"+JobDAO.STATUS_OPEN+"'");
				}else{
				   sql.append("\n and l.status <> '"+JobDAO.STATUS_RENEW+"'");
				}
			}
			
			sql.append("\n order by l.box_no ,l.material_master ,l.group_code,l.pens_item asc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new Barcode();
			   h.setNo(r);
			   h.setBoxNo(rst.getString("box_no")); 
			   h.setName(Utils.isNull(rst.getString("name"))); 
			   h.setMaterialMaster(Utils.isNull(rst.getString("material_master")));
			   h.setGroupCode(Utils.isNull(rst.getString("group_code")));
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setJobId(Utils.isNull(rst.getString("job_id")));
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status"))));
			   
			
				 //Get Pick Stock Remark
			   h.setRemark(Utils.isNull(rst.getString("remark")));	
			  
			   
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
	
	public static Barcode searchSummaryByBoxNo(Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Barcode h = null;
		List<Barcode> items = new ArrayList<Barcode>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n select h.box_no ");
			sql.append("\n,  (select max(name) from PENSBME_PICK_JOB j where h.job_id = j.job_id) as name ");
			sql.append("\n,  count(*) as qty ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE h,PENSBI.PENSBME_PICK_BARCODE_ITEM l ");
			sql.append("\n where h.job_id = l.job_id and h.box_no = l.box_no ");
			
			if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
				sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
				sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
			}
			
			if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
				sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
				sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
			}
			
			if( !Utils.isNull(o.getBoxNoFrom()).equals("") && !Utils.isNull(o.getBoxNoTo()).equals("")){
				sql.append("\n and l.box_no >= '"+Utils.isNull(o.getBoxNoFrom())+"'");
				sql.append("\n and l.box_no <= '"+Utils.isNull(o.getBoxNoTo())+"'");
			}
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and h.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getStatus()).equals("")){
				if("Onhand".equals(Utils.isNull(o.getStatus()))){
				   sql.append("\n and h.status = '"+JobDAO.STATUS_CLOSE+"'");
				   sql.append("\n and l.status = '"+JobDAO.STATUS_CLOSE+"'");
				   
				}else if("Scanning".equals(Utils.isNull(o.getStatus()))){
				   sql.append("\n and h.status = '"+JobDAO.STATUS_OPEN+"'");
				   sql.append("\n and ( l.status = '"+JobDAO.STATUS_OPEN+"' or l.status is null) ");
				}else{
				   sql.append("\n and l.status <> '"+JobDAO.STATUS_RENEW+"'");
				}
			}
			sql.append("\n group by h.box_no ,h.job_id  ");
			sql.append("\n order by h.box_no ,h.job_id asc ");
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new Barcode();
			   h.setBoxNo(rst.getString("box_no")); 
			   h.setName(Utils.isNull(rst.getString("name"))); 
               h.setQty(rst.getInt("qty"));
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
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}

	public static Barcode searchSummaryByPensItem(Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Barcode h = null;
		List<Barcode> items = new ArrayList<Barcode>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n select l.pens_item,l.group_code ");
			sql.append("\n,  count(*) as qty ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE h,PENSBI.PENSBME_PICK_BARCODE_ITEM l ");
			sql.append("\n where h.job_id = l.job_id and h.box_no = l.box_no ");
			
			if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
				sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
				sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
			}
			
			if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
				sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
				sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
			}
			
			if( !Utils.isNull(o.getBoxNoFrom()).equals("") && !Utils.isNull(o.getBoxNoTo()).equals("")){
				sql.append("\n and l.box_no >= '"+Utils.isNull(o.getBoxNoFrom())+"'");
				sql.append("\n and l.box_no <= '"+Utils.isNull(o.getBoxNoTo())+"'");
			}
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and h.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getStatus()).equals("")){
				if("Onhand".equals(Utils.isNull(o.getStatus()))){
				   sql.append("\n and h.status = '"+JobDAO.STATUS_CLOSE+"'");
				   sql.append("\n and l.status = '"+JobDAO.STATUS_CLOSE+"'");
				   
				}else if("Scanning".equals(Utils.isNull(o.getStatus()))){
				   sql.append("\n and h.status = '"+JobDAO.STATUS_OPEN+"'");
				   sql.append("\n and ( l.status = '"+JobDAO.STATUS_OPEN+"' or l.status is null) ");
				}else{
				   sql.append("\n and l.status <> '"+JobDAO.STATUS_RENEW+"'");
				}
			}
			sql.append("\n group by l.pens_item ,l.group_code  ");
			sql.append("\n order by l.pens_item ,l.group_code  asc ");
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new Barcode();
			   h.setPensItem(rst.getString("pens_item")); 
			   h.setGroupCode(Utils.isNull(rst.getString("group_code"))); 
               h.setQty(rst.getInt("qty"));
               
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
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}

}
