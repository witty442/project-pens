package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.StockQuery;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.dao.query.StockQuerySQL;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class StockQueryDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	
	public static StockQuery searchStockQuery(StockQuery o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		Connection conn = null;
		StockQuery h = null;
		List<StockQuery> items = new ArrayList<StockQuery>();
		int r = 1;
		int totalQty = 0;
		try {
			logger.debug("process:"+o.getProcess());
			logger.debug("status:"+o.getStatus());
			
			if("Detail".equalsIgnoreCase(o.getSummaryType())){
				sql = genSql(o);
				sql.append("\n ORDER BY AA.warehouse, AA.box_no ,AA.group_code," );
				sql.append("\n          AA.pens_item, AA.material_master ,AA.barcode asc ");
				
			}else if("SummaryByBox".equalsIgnoreCase(o.getSummaryType())){
				
				sql.append("\n SELECT M.warehouse,M.BOX_NO,M.job_id,M.job_name,SUM(M.qty) as qty FROM(");
				sql.append("\n		"+ genSql(o));
				sql.append("\n )M ");
				sql.append("\n GROUP BY M.warehouse, M.BOX_NO,M.job_id,M.job_name ");
				sql.append("\n ORDER BY M.warehouse, M.BOX_NO,M.job_id,M.job_name asc");
				
			}else if("SummaryByPensItem".equalsIgnoreCase(o.getSummaryType())){
				sql.append("\n SELECT M.warehouse,M.group_code,M.PENS_ITEM,SUM(M.qty) as qty FROM(");
				sql.append("\n		"+ genSql(o));
				sql.append("\n )M ");
				sql.append("\n GROUP BY M.warehouse,M.group_code, M.PENS_ITEM ");
				sql.append("\n ORDER BY M.warehouse, M.PENS_ITEM asc");
			}
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new StockQuery();
			   if("Detail".equalsIgnoreCase(o.getSummaryType())){
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
				   h.setRemark(Utils.isNull(rst.getString("remark")));
	               h.setOnhandQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }else if("SummaryByBox".equalsIgnoreCase(o.getSummaryType())){
				   h.setNo(r);
				   h.setWareHouse(rst.getString("warehouse"));
				   h.setBoxNo(rst.getString("box_no")); 
				   h.setName(rst.getString("job_id")+" "+rst.getString("job_name")); 
		           h.setOnhandQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
				}else if("SummaryByPensItem".equalsIgnoreCase(o.getSummaryType())){
					h.setNo(r);
					h.setWareHouse(rst.getString("warehouse"));
					h.setPensItem(Utils.isNull(rst.getString("pens_item")));
					h.setGroupCode(Utils.isNull(rst.getString("group_code")));
			        h.setOnhandQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
				}
			   items.add(h);
			   r++;
			   
			   totalQty += rst.getInt("qty");
			   
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
	
	
	private static StringBuffer genSql(StockQuery o)throws Exception{
		StringBuffer sql = new StringBuffer();
		String groupFromBarcode ="";
		String groupFromStockPick ="";
		String groupFromStockIssue ="";
		String groupFromStockFinish ="";
		
		if(o.getWareHouse().equals("W1")){
			
			if("ALL".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_OPEN+"','"+STATUS_CLOSE+"','"+STATUS_RETURN+"'"; 
			}else if("ONHAND".equals(o.getStatus())){
				groupFromBarcode = "'"+STATUS_CLOSE+"'";
			}else if("RETURN".equals(o.getStatus())){
				groupFromBarcode = "'"+STATUS_RETURN+"'";
			}else if("SCANNING".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_OPEN+"'";
			}

		}else if(o.getWareHouse().equals("W2")){
		
			if("ALL".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_OPEN+"','"+STATUS_CLOSE+"','"+STATUS_WORK_IN_PROCESS+"','"+STATUS_FINISH+"','"+STATUS_CANCEL+"'"; 
			}else if("ONHAND".equals(o.getStatus())){
				groupFromBarcode = "'"+STATUS_CLOSE+"'";
			}else if("SCANNING".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_OPEN+"'";
			}else if("FINISHING".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_WORK_IN_PROCESS+"'";
			}else if("FINISHGOODS".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_FINISH+"'";
			}

		}else if(o.getWareHouse().equals("W3")){
			
			if("ALL".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_OPEN+"','"+STATUS_CLOSE+"','"+STATUS_RETURN+"'"; 
				 groupFromStockPick = "'"+STATUS_ISSUED+"'";
			}else if("SCANNING".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_OPEN+"'";
			}else if("ONHAND".equals(o.getStatus())){
				groupFromBarcode = "'"+STATUS_CLOSE+"'";
			}else if("ISSUED".equals(o.getStatus())){
				groupFromStockPick = "'"+STATUS_ISSUED+"'";
			}
        }else if(o.getWareHouse().equals("W4")){
			
			if("ALL".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_OPEN+"','"+STATUS_CLOSE+"','"+STATUS_RETURN+"'"; 
				 groupFromStockPick = "'"+STATUS_ISSUED+"'";
			}else if("SCANNING".equals(o.getStatus())){
				 groupFromBarcode = "'"+STATUS_OPEN+"'";
			}else if("ONHAND".equals(o.getStatus())){
				groupFromBarcode = "'"+STATUS_CLOSE+"'";
			}else if("ISSUED".equals(o.getStatus())){
				groupFromStockPick = "'"+STATUS_ISSUED+"'";
			}
		}
		
		logger.debug("groupFromBarcode:"+groupFromBarcode);
		logger.debug("groupFromStockIssue:"+groupFromStockIssue);
		logger.debug("groupFromStockFinish:"+groupFromStockFinish);
		logger.debug("groupFromStockPick:"+groupFromStockPick);
		
		sql.append("\n SELECT AA.* FROM( ");
		if( !Utils.isNull(groupFromBarcode).equals("")){
		    sql.append(StockQuerySQL.genSQLByStatus(o,groupFromBarcode));
		}
		if( !Utils.isNull(groupFromStockIssue).equals("")){
			sql.append("\n UNION ALL");
			sql.append(StockQuerySQL.genSQLByStatus(o,groupFromStockIssue));
		}
		if( !Utils.isNull(groupFromStockFinish).equals("")){
			sql.append("\n UNION ALL");
			sql.append(StockQuerySQL.genSQLByStatus(o,groupFromStockFinish));
		}
		if( !Utils.isNull(groupFromStockPick).equals("")){
			if( !Utils.isNull(groupFromBarcode).equals("")){
			  sql.append("\n UNION ALL");
			}
			sql.append(StockQuerySQL.genSQLByStatusW3andW4(o,groupFromStockPick));
		}
		sql.append("\n )AA ");

		return sql;
		
	}
	
	public static StockQuery searchSummaryFinishGoodByDetail(StockQuery o ) throws Exception {
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
			//Case 1 Status Avialble
			if(o.getStatus().equals(STATUS_AVAILABLE)){
				sql.append("\n select A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ,A.qty ");
				sql.append("\n FROM( ");
				
				sql.append("\n 	select A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ,sum(qty) as qty FROM(");
				//** Stock Finished **/
				sql.append("\n 		select l.material_master ,l.group_code,l.pens_item,l.barcode,'A' as status ,sum(nvl(onhand_qty,0)-nvl(issue_qty,0)) as qty ");
				sql.append("\n 		from PENSBI.PENSBME_STOCK_FINISHED l WHERE 1=1 ");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n 			group by l.material_master ,l.group_code,l.pens_item,l.barcode");
				
				sql.append("\n 			UNION ALL ");
				
				//** Stock ISSUE  substract status OPEN(O) ,P POST**/
				sql.append("\n 			select  l.material_master ,l.group_code,l.pens_item,l.barcode,'A' as status ,(-1*sum(nvl(req_qty,0))) as qty ");
				sql.append("\n 			from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n 			where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n 			and h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n   		 group by l.material_master,l.group_code,l.pens_item,l.barcode");
				
				sql.append("\n      )A ");
				sql.append("\n      group by A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ");
				sql.append("\n )A ");
				sql.append("\n WHERE A.qty > 0 ");
				sql.append("\n order by A.group_code,A.pens_item, A.material_master ,A.barcode asc ");
				
			}else if(o.getStatus().equals(STATUS_STOCK)){
					
				sql.append("\n select A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ,sum(qty) as qty ");
				sql.append("\n FROM( ");
				
				//** Stock Finished **/
				sql.append("\n select l.material_master ,l.group_code,l.pens_item,l.barcode,'S' as status ,sum(nvl(onhand_qty,0)-nvl(issue_qty,0)) as qty ");
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
				sql.append("\n )A ");
				sql.append("\n group by A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ");
				sql.append("\n order by A.group_code,A.pens_item, A.material_master ,A.barcode asc ");
					
			}else if(o.getStatus().equals(STATUS_RESERVE)){ //O ,P
				sql.append("\n select l.material_master ,l.group_code,l.pens_item,l.barcode,'RE' as status ,(sum(nvl(req_qty,0))) as qty ");
				sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n and h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n group by l.material_master,l.group_code,l.pens_item,l.barcode");
				sql.append("\n order by l.group_code,l.pens_item, l.material_master ,l.barcode asc ");
				
			}else if(o.getStatus().equals(STATUS_ISSUED)){ // I
				sql.append("\n select l.material_master ,l.group_code,l.pens_item,l.barcode,'I' as status ,(sum(nvl(issue_qty,0))) as qty ");
				sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n and h.status ='"+STATUS_ISSUED+"'");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n group by l.material_master,l.group_code,l.pens_item,l.barcode");
				sql.append("\n order by l.group_code,l.pens_item, l.material_master ,l.barcode asc ");
			}else{
				//ALL STock
				sql.append("\n select M.* FROM( ");
				//** Stock Finished **/
				sql.append("\n select A.material_master ,A.group_code,A.pens_item,A.barcode ");
				sql.append("\n ,(CASE WHEN A.qty = 0 THEN 'S' ELSE 'A' END) as status , A.qty ");
				sql.append("\n FROM( ");
				
				sql.append("\n 	select A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ,sum(qty) as qty FROM(");
				//** Stock Finished **/
				sql.append("\n 		select l.material_master ,l.group_code,l.pens_item,l.barcode,'A' as status ,sum(nvl(onhand_qty,0)-nvl(issue_qty,0)) as qty ");
				sql.append("\n 		from PENSBI.PENSBME_STOCK_FINISHED l WHERE 1=1 ");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				sql.append("\n 			group by l.material_master ,l.group_code,l.pens_item,l.barcode");
				
				sql.append("\n 			UNION ALL ");
				
				//** Stock ISSUE  substract status OPEN(O) ,P POST**/
				sql.append("\n 			select  l.material_master ,l.group_code,l.pens_item,l.barcode,'A' as status ,(-1*sum(nvl(req_qty,0))) as qty ");
				sql.append("\n 			from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n 			where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n 			and h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				sql.append("\n   		group by l.material_master,l.group_code,l.pens_item,l.barcode");
				
				sql.append("\n      )A ");
				sql.append("\n      group by A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ");
				sql.append("\n )A ");
				
				sql.append("\n UNION ALL ");
				
				/** Stock Reservse **/
				sql.append("\n select l.material_master ,l.group_code,l.pens_item,l.barcode,'RE' as status ,(sum(nvl(req_qty,0))) as qty ");
				sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n and h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n group by l.material_master,l.group_code,l.pens_item,l.barcode");
			
				sql.append("\n UNION ALL ");
				
				/** Stock Issued **/
				sql.append("\n select l.material_master ,l.group_code,l.pens_item,l.barcode,'I' as status ,(sum(nvl(issue_qty,0))) as qty ");
				sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n and h.status ='"+STATUS_ISSUED+"'");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n  group by l.material_master,l.group_code,l.pens_item,l.barcode");
				sql.append("\n )M ");
				sql.append("\n order by M.group_code,M.pens_item, M.material_master ,M.barcode,M.status asc ");
			}
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new StockQuery();
			   h.setNo(r);
			   h.setMaterialMaster(Utils.isNull(rst.getString("material_master")));
			   h.setGroupCode(Utils.isNull(rst.getString("group_code")));
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status"))));
               h.setOnhandQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			
			   items.add(h);
			   r++;
			   
			   totalQty +=rst.getInt("qty");
			}//while

			o.setTotalQty(totalQty);
			o.setItems(items);
			
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
	
	public static StockQuery searchSummaryFinishGoodByPensItem(StockQuery o ) throws Exception {
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
			//Case 1 Status Avialble
			if(o.getStatus().equals(STATUS_AVAILABLE)){
				sql.append("\n select A.group_code,A.pens_item,A.status ,A.qty ");
				sql.append("\n FROM( ");
				
				sql.append("\n 	select A.group_code,A.pens_item,A.status ,sum(qty) as qty FROM(");
				//** Stock Finished **/
				sql.append("\n 		select l.group_code,l.pens_item,'A' as status ,sum(nvl(onhand_qty,0)-nvl(issue_qty,0)) as qty ");
				sql.append("\n 		from PENSBI.PENSBME_STOCK_FINISHED l WHERE 1=1 ");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n 			group by l.group_code,l.pens_item");
				
				sql.append("\n 			UNION ALL ");
				
				//** Stock ISSUE  substract status OPEN(O) ,P POST**/
				sql.append("\n 			select l.group_code,l.pens_item,'A' as status ,(-1*sum(nvl(req_qty,0))) as qty ");
				sql.append("\n 			from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n 			where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n 			and h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n   		 group by l.group_code,l.pens_item");
				
				sql.append("\n      )A ");
				sql.append("\n      group by A.group_code,A.pens_item,A.status ");
				sql.append("\n )A ");
				sql.append("\n WHERE A.qty > 0 ");
				sql.append("\n order by A.group_code,A.pens_item asc ");
				
			}else if(o.getStatus().equals(STATUS_STOCK)){
					
				sql.append("\n select A.group_code,A.pens_item,A.status ,sum(qty) as qty ");
				sql.append("\n FROM( ");
				
				//** Stock Finished **/
				sql.append("\n select l.group_code,l.pens_item,'S' as status ,sum(nvl(onhand_qty,0)-nvl(issue_qty,0)) as qty ");
				sql.append("\n from PENSBI.PENSBME_STOCK_FINISHED l WHERE 1=1 ");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n group by l.group_code,l.pens_item");
				sql.append("\n )A ");
				sql.append("\n group by A.group_code,A.pens_item,A.status ");
				sql.append("\n order by A.group_code,A.pens_item asc ");
					
			}else if(o.getStatus().equals(STATUS_RESERVE)){ //O ,P
				sql.append("\n select l.group_code,l.pens_item,'RE' as status ,(sum(nvl(req_qty,0))) as qty ");
				sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n and h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n group by l.group_code,l.pens_item");
				sql.append("\n order by l.group_code,l.pens_item asc ");
				
			}else if(o.getStatus().equals(STATUS_ISSUED)){ // I
				sql.append("\n select l.group_code,l.pens_item,'I' as status ,(sum(nvl(issue_qty,0))) as qty ");
				sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n and h.status ='"+STATUS_ISSUED+"'");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n group by l.group_code,l.pens_item");
				sql.append("\n order by l.group_code,l.pens_item asc ");
			}else{
				//ALL STock
				sql.append("\n select M.* FROM( ");
				//** Stock Finished **/
				sql.append("\n select A.group_code,A.pens_item ");
				sql.append("\n ,(CASE WHEN A.qty = 0 THEN 'S' ELSE 'A' END) as status , A.qty ");
				sql.append("\n FROM( ");
				
				sql.append("\n 	select A.group_code,A.pens_item,A.status ,sum(qty) as qty FROM(");
				//** Stock Finished **/
				sql.append("\n 		select l.group_code,l.pens_item,'A' as status ,sum(nvl(onhand_qty,0)-nvl(issue_qty,0)) as qty ");
				sql.append("\n 		from PENSBI.PENSBME_STOCK_FINISHED l WHERE 1=1 ");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				sql.append("\n 			group by l.group_code,l.pens_item");
				
				sql.append("\n 			UNION ALL ");
				
				//** Stock ISSUE  substract status OPEN(O) ,P POST**/
				sql.append("\n 			select  l.group_code,l.pens_item,'A' as status ,(-1*sum(nvl(req_qty,0))) as qty ");
				sql.append("\n 			from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n 			where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n 			and h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				sql.append("\n   		group by l.group_code,l.pens_item");
				
				sql.append("\n      )A ");
				sql.append("\n      group by A.group_code,A.pens_item,A.status ");
				sql.append("\n )A ");
				
				sql.append("\n UNION ALL ");
				
				/** Stock Reservse **/
				sql.append("\n select l.group_code,l.pens_item,'RE' as status ,(sum(nvl(req_qty,0))) as qty ");
				sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n and h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n group by l.group_code,l.pens_item");
			
				sql.append("\n UNION ALL ");
				
				/** Stock Issued **/
				sql.append("\n select l.group_code,l.pens_item,'I' as status ,(sum(nvl(issue_qty,0))) as qty ");
				sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
				sql.append("\n where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
				sql.append("\n and h.status ='"+STATUS_ISSUED+"'");
				if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
					sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
					sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
				}
				if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
					sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
					sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
				}
				
				sql.append("\n  group by l.group_code,l.pens_item");
				sql.append("\n )M ");
				sql.append("\n order by M.group_code,M.pens_item,M.status asc ");
			}
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new StockQuery();
			   h.setNo(r);
			   h.setGroupCode(Utils.isNull(rst.getString("group_code")));
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status"))));
               h.setOnhandQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			
			   items.add(h);
			   r++;
			   totalQty +=rst.getInt("qty");
			}//while
			
            o.setTotalQty(totalQty);
			o.setItems(items);
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
