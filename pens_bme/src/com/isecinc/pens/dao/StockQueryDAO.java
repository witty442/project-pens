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
import com.isecinc.pens.bean.StockQuery;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class StockQueryDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	
	public static List<StockQuery> searchSummaryByDetail(StockQuery o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		StockQuery h = null;
		List<StockQuery> items = new ArrayList<StockQuery>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select A.warehouse,A.job_id,A.job_name,A.box_no, A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ,sum(qty) as qty ");
			sql.append("\n FROM( ");
			/** Get From Barcode **/
			sql.append("\n select j.warehouse,j.job_id,j.name as job_name ,l.box_no, l.material_master ,l.group_code,l.pens_item,l.barcode,l.status ,count(*) as qty ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE h,PENSBI.PENSBME_PICK_BARCODE_ITEM l ,PENSBME_PICK_JOB j ");
			sql.append("\n where h.job_id = l.job_id and h.box_no = l.box_no ");
			sql.append("\n and  h.job_id = j.job_id");
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
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and j.warehouse = '"+Utils.isNull(o.getWareHouse())+"'");
			}
			/** Case View by Process 1 all stock,2,On Process **/
			if( !Utils.isNull(o.getProcess()).equals("")){
				if(Utils.isNull(o.getProcess()).equals("ALL")){
				   sql.append("\n and l.status not in('"+STATUS_FINISH+"','"+STATUS_MOVE+"','"+STATUS_CANCEL+"'");
				}else{
				   sql.append("\n and l.status in('"+STATUS_CLOSE+"','"+STATUS_ISSUED+"')");
				}
			}else{
				if( !Utils.isNull(o.getStatus()).equals("")){
				  sql.append("\n and l.status in("+Utils.converToTextSqlIn(o.getStatus())+")");
				}
			}
			sql.append("\n group by j.warehouse,j.job_id,j.name,l.box_no ,l.material_master ,l.group_code,l.pens_item,l.barcode,l.status ");
			
			sql.append("\n UNION ALL ");
			
			//** Stock Finished **/
			sql.append("\n select 'W2' as warehouse,0 as job_id ,''as job_name,'' as box_no, l.material_master ,l.group_code,l.pens_item,l.barcode,'A' as status ,sum(nvl(onhand_qty,0)-nvl(issue_qty,0)) as qty ");
			sql.append("\n from PENSBI.PENSBME_STOCK_FINISHED l WHERE 1=1 ");
			if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
				sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
				sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
			}
			if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
				sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
				sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
			}
			
			sql.append("\n group by l.material_master ,l.group_code,l.pens_item,l.barcode");
			
			sql.append("\n UNION ALL ");
			
			//** Stock ISSUE **/
			sql.append("\n select 'W2' as warehouse,0 as job_id ,''as job_name,'' as box_no, l.material_master ,l.group_code,l.pens_item,l.barcode,'A' as status ,sum(nvl(qty,0)) as qty ");
			sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
			sql.append("\n where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
			if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
				sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
				sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
			}
			if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
				sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
				sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
			}
			
			sql.append("\n group by l.material_master,l.group_code,l.pens_item,l.barcode");
			
			sql.append("\n )A ");
			sql.append("\n group by A.warehouse,A.job_id,A.job_name,A.box_no, A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ");
			sql.append("\n order by A.warehouse, A.box_no ,A.group_code,A.pens_item, A.material_master ,A.barcode asc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new StockQuery();
			   h.setNo(r);
			   h.setWareHouse(rst.getString("warehouse"));
			   h.setBoxNo(rst.getString("box_no")); 
			   h.setName(Utils.isNull(rst.getString("job_name"))); 
			   h.setMaterialMaster(Utils.isNull(rst.getString("material_master")));
			   h.setGroupCode(Utils.isNull(rst.getString("group_code")));
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setJobId(Utils.isNull(rst.getString("job_id")));
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status"))));
               h.setOnhandQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			
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
	
	public static StockQuery searchSummaryByBoxNo(StockQuery o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		StockQuery h = null;
		List<StockQuery> items = new ArrayList<StockQuery>();
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
			  
			   h = new StockQuery();
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

	public static StockQuery searchSummaryByPensItem(StockQuery o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		StockQuery h = null;
		List<StockQuery> items = new ArrayList<StockQuery>();
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
			  
			   h = new StockQuery();
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
