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
import com.isecinc.pens.bean.RenewBox;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;

public class RenewBoxDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public static RenewBox save(Connection conn,RenewBox h) throws Exception{
		//Map<String,String> pensItemMapAll = new HashMap<String, String>();
		try{
			//save 
			if(h.getItems() != null && h.getItems().size()>0){
			   for(int i=0;i<h.getItems().size();i++){
				   RenewBox l = (RenewBox)h.getItems().get(i);
				   
				   //insert new box Barcode
				   Barcode bhNew = new Barcode();
				   bhNew.setBoxNo(genBoxNo(conn, new Date()));
				   bhNew.setJobId(l.getJobId());
				   bhNew.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   bhNew.setStatus(STATUS_CLOSE);
				   bhNew.setCreateUser(h.getCreateUser());
				   bhNew.setUpdateUser(h.getUpdateUser());
				  
				   BarcodeDAO.saveHeadModel(conn, bhNew);
				   
				   //barcode old
				   Barcode bhOld = new Barcode();
				   bhOld.setBoxNo(l.getBoxNo());//old box_no
				   bhOld.setJobId(l.getJobId());
				   bhOld.setStatus(STATUS_RENEW);
				   bhOld.setBoxNoRef(bhNew.getBoxNo());//new boxNo
				   bhOld.setCreateUser(h.getCreateUser());
				   bhOld.setUpdateUser(h.getUpdateUser());
				   
				   //Get barcode item status=close
				   List<Barcode> barcodeItemList = BarcodeDAO.searchItemStatusCloseOnly(conn, bhOld);
				   //insert in new box
				   if( barcodeItemList != null && barcodeItemList.size() >0){
					   int lineId = 1;
					   for(int n=0;n<barcodeItemList.size();n++){
						   Barcode line = (Barcode)barcodeItemList.get(n);
						   line.setCreateUser(h.getCreateUser());
						   line.setUpdateUser(h.getUpdateUser());
						   
						   //update old barcode line to Renew
						   line.setStatus(STATUS_RENEW);
						   BarcodeDAO.updateBarcodeLineStatusModelByPKALL(conn, line);
						   
						   //insert new  barcodeline
						   line.setBoxNo(bhNew.getBoxNo());//new BoxNo
						   line.setLineId(lineId);
						   line.setStatus(STATUS_CLOSE);
						   BarcodeDAO.saveItemModel(conn, line);
						   
						   lineId++;
						   
					   }
				   }
				   
				   
				  //update old barcode head to Renew
				   BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, bhOld);
			      
			     
			   }//for
			}//if

			return h;
		}catch(Exception e){
		  throw e;
		}finally{
			
		}
	}
	
	
	
	// ( Running :  yymmxxxx  เช่น 57030001 )			
		 private static String genBoxNo(Connection conn,Date date) throws Exception{
	       String docNo = "";
			   try{
				   
				   String today = df.format(date);
				   String[] d1 = today.split("/");
				   int curYear = Integer.parseInt(d1[0].substring(0,4));
				   int curMonth = Integer.parseInt(d1[1]);
				 
				 //get Seq
				   int seq = SequenceProcess.getNextValue(conn,"BOX_NO","BOX_NO",date);
				   
				   docNo = new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("0000").format(seq);
			   }catch(Exception e){
				   throw e;
			   }
			  return docNo;
		}
	
	public static  RenewBox  searchHead(RenewBox o ,String boxNoWhereIn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		RenewBox h = null;
		List<RenewBox> items = new ArrayList<RenewBox>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n SELECT M.*,T2.qty ");
			sql.append("\n FROM( ");
				sql.append("\n select i.* ,j.name ,j.status as job_status,j.store_code,j.store_no,j.sub_inv " );
				sql.append("\n from PENSBI.PENSBME_PICK_BARCODE i INNER JOIN  \n");
				sql.append("\n ( ");
				sql.append("\n    select distinct job_id,name,status,store_code,store_no,sub_inv from PENSBME_PICK_JOB ");
				sql.append("\n ) j on i.job_id = j.job_id");
				sql.append("\n where 1=1 and i.box_no_ref is not null  \n");
				
				if( !Utils.isNull(boxNoWhereIn).equals("")){
					sql.append("\n and i.box_no in("+Utils.isNull(boxNoWhereIn)+")");
				}
				if( !Utils.isNull(o.getJobId()).equals("")){
					sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
				}
				if( !Utils.isNull(o.getBoxNo()).equals("")){
					sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
				}
		sql.append("\n ) M LEFT OUTER JOIN ");
				sql.append("\n (   select h.box_no,h.job_id , count(*) as qty   ");
				sql.append("\n     from PENSBI.PENSBME_PICK_BARCODE h,  ");
				sql.append("\n     PENSBME_PICK_BARCODE_ITEM s ");
				sql.append("\n     where s.box_no = h.box_no   ");
				sql.append("\n     and s.job_id = h.job_id ");
				sql.append("\n     and h.status = '"+JobDAO.STATUS_CLOSE+"'" );
				sql.append("\n     and ( s.status = '"+JobDAO.STATUS_CLOSE+"' OR s.status ='' OR s.status is null) ");
				
				if( !Utils.isNull(o.getJobId()).equals("")){
					sql.append("\n and h.job_id = "+Utils.isNull(o.getJobId())+"");
				}
				
				sql.append("\n     group by h.box_no ,h.job_id  ");
			sql.append("\n ) T2 ON T2.job_id = M.job_id AND T2.box_no = M.box_no_ref ");
			
			sql.append("\n order by M.box_no desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new RenewBox();
			   h.setLineId(r);
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
	           h.setJobName(rst.getString("name"));
	           h.setBoxNoRef(rst.getString("box_no_ref"));
	           h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
	           
			   items.add(h);
			   r++;
			}//while

			//set Result 
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
	
	public static List<RenewBox> searchBarcoceItemInBoxCasePickSomeItem(Connection conn ,RenewBox o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		RenewBox h = null;
		int r = 1;
        List<RenewBox> items = new ArrayList<RenewBox>();
		try {
			sql.append("\n SELECT M.*,T1.total_box_qty,T2.remain_qty ,(T1.total_box_qty-T2.remain_qty) as qty ");
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
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and h.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and h.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			
			sql.append("\n    group by i.box_no ,i.job_id  ");
			sql.append("\n ) M  LEFT OUTER JOIN ");
			 
			sql.append("\n (  select h.box_no,h.job_id , count(*) as total_box_qty   ");
			sql.append("\n     from PENSBI.PENSBME_PICK_BARCODE h,  ");
			sql.append("\n     PENSBME_PICK_BARCODE_ITEM s ");
			sql.append("\n     where s.box_no = h.box_no   ");
			sql.append("\n     and s.job_id = h.job_id ");
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and s.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and s.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			sql.append("\n     group by h.box_no ,h.job_id  ");
			sql.append("\n ) T1  ON T1.job_id = M.job_id AND T1.box_no = M.box_no ");
			 
			sql.append("\n LEFT OUTER JOIN ");
			sql.append("\n (   select h.box_no,h.job_id , count(*) as remain_qty   ");
			sql.append("\n     from PENSBI.PENSBME_PICK_BARCODE h,  ");
			sql.append("\n     PENSBME_PICK_BARCODE_ITEM s ");
			sql.append("\n     where s.box_no = h.box_no   ");
			sql.append("\n     and s.job_id = h.job_id ");
			sql.append("\n     and h.status = '"+JobDAO.STATUS_CLOSE+"'" );
			sql.append("\n     and ( s.status = '"+JobDAO.STATUS_CLOSE+"' OR s.status ='' OR s.status is null) ");
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and s.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and s.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			
			sql.append("\n     group by h.box_no ,h.job_id  ");
			sql.append("\n ) T2 ON T2.job_id = M.job_id AND T2.box_no = M.box_no ");
			sql.append("\n WHERE T1.total_box_qty <> T2.remain_qty ");
			sql.append("\n order by M.box_no asc  ");
 
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
  
			while(rst.next()) {
			   h = new RenewBox();
			   h.setLineId(r);
			   h.setJobId(rst.getString("job_id"));
			   h.setJobName(rst.getString("job_name"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   h.setTotalBoxQty(Utils.decimalFormat(rst.getInt("total_box_qty"),Utils.format_current_no_disgit));
			   h.setRemainQty(Utils.decimalFormat(rst.getInt("remain_qty"),Utils.format_current_no_disgit));
			   h.setBoxNoRef("");
			   
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
