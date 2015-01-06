package com.isecinc.pens.dao.query;

import com.isecinc.pens.bean.StockQuery;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.Utils;

public class StockQuerySQL extends PickConstants{

	
	public static StringBuffer genSQLByStatus(StockQuery o,String statusAll){
		StringBuffer sql = new StringBuffer();
		
		if(    ( statusAll.indexOf(STATUS_RETURN) !=-1) ||
				statusAll.indexOf(STATUS_OPEN) !=-1 || statusAll.indexOf(STATUS_CLOSE) !=-1 || 
				statusAll.indexOf(STATUS_MOVE) !=-1 || statusAll.indexOf(STATUS_WORK_IN_PROCESS) !=-1 || statusAll.indexOf(STATUS_FINISH) !=-1
				){ //Get From Barcode
			
			sql = genFromPickBarcode(o,statusAll);
			
		}else if(statusAll.indexOf(STATUS_RESERVE) !=-1){ //Get From STOCK ISSUE
			sql.append(genFromStockIssue(o,statusAll));
		}else if(statusAll.indexOf(STATUS_AVAILABLE) !=-1){ //Get From STOCKFINISH - STOCK ISSUE
			sql = genFromStockAviable(o);
		}
		return sql;
	}
	
	public static StringBuffer genSQLByStatusW3(StockQuery o,String statusAll){
		
		StringBuffer sql = new StringBuffer();
		sql.append("\n select S.*, ");
		sql.append("\n (select max(p.remark) from pensbme_pick_stock p,pensbme_pick_stock_i pi " +
				   "\n       where p.issue_req_no = pi.issue_req_no and pi.job_id = S.job_id and pi.box_no = S.box_no )as remark ");
		sql.append("\n from( ");
		sql.append(genFromStockPick(o,statusAll));
		sql.append("\n )S ");
		return sql;
	}
	
	public static StringBuffer genFromPickBarcode(StockQuery o,String statusAll){
		StringBuffer sql = new StringBuffer("");
		
		sql.append("\n select j.warehouse,j.job_id,j.name as job_name ,l.box_no, l.material_master ,l.group_code," );
		sql.append("\n l.pens_item,l.barcode,l.status ,count(*) as qty,'' as remark ");
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
		sql.append("\n and l.status in("+statusAll+")");//RESERVE
		
		sql.append("\n group by j.warehouse,j.job_id,j.name ,l.box_no,l.line_id, " );
		sql.append("\n          l.material_master ,l.group_code,l.pens_item,l.barcode,l.status ");
		return sql;
	}

	public static StringBuffer genFromStockFinish(StockQuery o){
		StringBuffer sql = new StringBuffer("");
		
		sql.append("\n select warehouse,job_id ,job_name,box_no, material_master ,group_code,pens_item,barcode,status ,qty ");
		sql.append("\n select 'W2' as warehouse,0 as job_id ,''as job_name,'' as box_no, l.material_master ," );
		sql.append("\n l.group_code,l.pens_item,l.barcode,'A' as status ,sum(nvl(onhand_qty,0)-nvl(issue_qty,0)) as qty ");
		sql.append("\n from PENSBI.PENSBME_STOCK_FINISHED l WHERE 1=1 ");
		if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
			sql.append("\n and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
			sql.append("\n and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
		}
		if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
			sql.append("\n and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
			sql.append("\n and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
		}
		sql.append("\n group by l.material_master,l.group_code,l.pens_item,l.barcode");
		return sql;
	}
	
    public static StringBuffer genFromStockIssue(StockQuery o,String statusAll){
    	StringBuffer sql = new StringBuffer("");
    	
    	sql.append("\n select 'W2' as warehouse,0 as job_id ,''as job_name,'' as box_no, l.material_master ,");
    	sql.append("\n l.group_code,l.pens_item,l.barcode,'RE' as status ,sum(nvl(issue_qty,0)) as qty ");
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
		
		if( Utils.isNull(statusAll).equals("'RE'")){
			sql.append("\n and l.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");//RESERVE
		}
		
		sql.append("\n group by l.material_master,l.group_code,l.pens_item,l.barcode");
		
		return sql;
	}
	
    public static StringBuffer genFromStockAviable(StockQuery o){
    	StringBuffer sql = new StringBuffer("");
    	//** Stock Finished **/
    	sql.append("\n select M.* FROM( ");
		sql.append("\n  select warehouse,job_id ,job_name,box_no, material_master ,group_code,pens_item,barcode,status ,sum(qty) as qty FROM( ");
		sql.append("\n 		select 'W2' as warehouse,0 as job_id ,''as job_name,'' as box_no, l.material_master ,l.group_code," );
		sql.append("\n 		l.pens_item,l.barcode,'A' as status ,sum(nvl(onhand_qty,0)-nvl(issue_qty,0)) as qty ");
		sql.append("\n 		from PENSBI.PENSBME_STOCK_FINISHED l WHERE 1=1 ");
		if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
			sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
			sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
		}
		if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
			sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
			sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
		}
		sql.append("\n		 group by l.material_master ,l.group_code,l.pens_item,l.barcode");
		sql.append("\n ");
		
		sql.append("\n UNION ALL ");
		
		//** Stock ISSUE **/
		sql.append("\n 		select 'W2' as warehouse,0 as job_id ,''as job_name,'' as box_no, l.material_master ,l.group_code," );
		sql.append("\n 		l.pens_item,l.barcode,'A' as status ,(-1* sum(nvl(req_qty,0))) as qty ");
		sql.append("\n 		from PENSBI.PENSBME_STOCK_ISSUE h,PENSBME_STOCK_ISSUE_ITEM l ");
		sql.append("\n 		where h.ISSUE_REQ_NO = l.ISSUE_REQ_NO ");
		sql.append("\n 		and l.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
		if( !Utils.isNull(o.getPensItemFrom()).equals("") && !Utils.isNull(o.getPensItemTo()).equals("")){
			sql.append("\n 		and l.pens_item >= '"+Utils.isNull(o.getPensItemFrom())+"'");
			sql.append("\n 		and l.pens_item <= '"+Utils.isNull(o.getPensItemTo())+"'");
		}
		if( !Utils.isNull(o.getGroupCodeFrom()).equals("") && !Utils.isNull(o.getGroupCodeTo()).equals("")){
			sql.append("\n 		and l.group_code >= '"+Utils.isNull(o.getGroupCodeFrom())+"'");
			sql.append("\n 		and l.group_code <= '"+Utils.isNull(o.getGroupCodeTo())+"'");
		}
		sql.append("\n 		group by l.material_master,l.group_code,l.pens_item,l.barcode");
		
		sql.append("\n  	 )A ");
		sql.append("\n  group by A.warehouse,A.job_id,A.job_name,A.box_no, A.material_master ,A.group_code,A.pens_item,A.barcode,A.status ");
		sql.append("\n )M ");
		sql.append("\n WHERE M.qty > 0");
	
		return sql;
    }
    
    public static StringBuffer genFromStockPick(StockQuery o,String statusAll){
		StringBuffer sql = new StringBuffer("");
		
		sql.append("\n select j.warehouse,j.job_id,j.name as job_name ,l.box_no, l.material_master ,l.group_code," );
		sql.append("\n l.pens_item,l.barcode,l.status ,count(*) as qty ");
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
		sql.append("\n and l.status in("+statusAll+")");//RESERVE
		
		sql.append("\n group by j.warehouse,j.job_id,j.name ,l.box_no,l.line_id, " );
		sql.append("\n          l.material_master ,l.group_code,l.pens_item,l.barcode,l.status ");
		return sql;
	}
}
