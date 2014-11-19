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

import com.isecinc.pens.bean.ConfirmReturnWacoal;
import com.isecinc.pens.bean.ControlReturnReport;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ReqReturnWacoal;
import com.isecinc.pens.bean.ReturnBoxReport;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class ConfirmReturnWacoalDAO extends PickConstants{

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
	
	public static List<ConfirmReturnWacoal> searchHeadFromReq(ConfirmReturnWacoal o,boolean getItems ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ConfirmReturnWacoal h = null;
		List<ConfirmReturnWacoal> items = new ArrayList<ConfirmReturnWacoal>();
		int r = 1;
		int c = 1;
		try {
			String requestDateStr = "";
			String returnDateStr = "";
			
			if( !Utils.isNull(o.getRequestDate()).equals("")){
				Date tDate  = Utils.parse(o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				requestDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
			}
			if( !Utils.isNull(o.getReturnDate()).equals("")){
				Date tDate  = Utils.parse(o.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				returnDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
			}
			
			sql.append("\n SELECT A.* FROM( ");
				sql.append("\n select R.*,'' as return_no,'' as return_status,null as return_date  ");
				sql.append("\n from PENSBI.PENSBME_PICK_REQ_RETURN R ");
				sql.append("\n where 1=1  ");
				sql.append("\n and R.request_status <> '"+STATUS_NEW+"'");
				sql.append("\n and R.request_no not in( ");
				sql.append("\n   select C.request_no from PENSBI.PENSBME_PICK_CONF_RETURN C ");
				sql.append("\n   where C.return_status in('"+STATUS_CANCEL+"','"+STATUS_RETURN+"')");
				sql.append("\n  ) ");
				 
				if( !Utils.isNull(o.getRequestDate()).equals("")){
					sql.append("\n and R.REQUEST_DATE = to_date('"+requestDateStr+"','dd/mm/yyyy') ");
				}
				
				if( !Utils.isNull(o.getRequestStatus()).equals("")){
					sql.append("\n and R.request_status = '"+Utils.isNull(o.getRequestStatus())+"'");
				}
				
				if( !Utils.isNull(o.getRequestNo()).equals("")){
					sql.append("\n and R.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
				}
				
				if( !Utils.isNull(o.getReturnDate()).equals("") ||
					!Utils.isNull(o.getReturnNo()).equals("") ||
					!Utils.isNull(o.getReturnStatus()).equals("") ||
					!Utils.isNull(o.getCnNo()).equals("")){
					
					sql.append("\n and R.request_no in( ");
					sql.append("\n  select C.request_no from PENSBI.PENSBME_PICK_CONF_RETURN C where 1=1 ");
					
					if( !Utils.isNull(o.getReturnDate()).equals("")){
						sql.append("\n and C.RETURN_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
					}
					
					if( !Utils.isNull(o.getReturnNo()).equals("")){
						sql.append("\n and C.return_no = '"+Utils.isNull(o.getReturnNo())+"'");
					}
					
					if( !Utils.isNull(o.getReturnStatus()).equals("")){
						sql.append("\n and C.return_status = '"+Utils.isNull(o.getReturnStatus())+"'");
					}
					
					if( !Utils.isNull(o.getCnNo()).equals("")){
						sql.append("\n and C.CN_NO = '"+Utils.isNull(o.getCnNo())+"'");
					}
					
					sql.append("\n  ) ");
				}	
			sql.append("\n UNION ALL");	
				sql.append("\n select R.*,'' as return_no,'' as return_status,null as return_date  ");
				sql.append("\n from PENSBI.PENSBME_PICK_REQ_RETURN R ");
				sql.append("\n where 1=1  ");
				sql.append("\n and R.request_status = '"+STATUS_NEW+"'");
				 
				if( !Utils.isNull(o.getRequestDate()).equals("")){
					sql.append("\n and R.REQUEST_DATE = to_date('"+requestDateStr+"','dd/mm/yyyy') ");
				}
				
				if( !Utils.isNull(o.getRequestStatus()).equals("")){
					sql.append("\n and R.request_status = '"+Utils.isNull(o.getRequestStatus())+"'");
				}
				
				if( !Utils.isNull(o.getRequestNo()).equals("")){
					sql.append("\n and R.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
				}
				
				if( !Utils.isNull(o.getReturnDate()).equals("") ||
					!Utils.isNull(o.getReturnNo()).equals("") ||
					!Utils.isNull(o.getReturnStatus()).equals("") ||
					!Utils.isNull(o.getCnNo()).equals("")){
					
					sql.append("\n and R.request_no in( ");
					sql.append("\n  select C.request_no from PENSBI.PENSBME_PICK_CONF_RETURN C where 1=1 ");
					
					if( !Utils.isNull(o.getReturnDate()).equals("")){
						sql.append("\n and C.RETURN_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
					}
					
					if( !Utils.isNull(o.getReturnNo()).equals("")){
						sql.append("\n and C.return_no = '"+Utils.isNull(o.getReturnNo())+"'");
					}
					
					if( !Utils.isNull(o.getReturnStatus()).equals("")){
						sql.append("\n and C.return_status = '"+Utils.isNull(o.getReturnStatus())+"'");
					}
					
					if( !Utils.isNull(o.getCnNo()).equals("")){
						sql.append("\n and C.CN_NO = '"+Utils.isNull(o.getCnNo())+"'");
					}
					
					sql.append("\n  ) ");
				}	
			sql.append("\n UNION ALL");
			
				sql.append("\n select R.*,C.return_no,C.return_status,C.return_date ");
				sql.append("\n from PENSBI.PENSBME_PICK_CONF_RETURN C ");
				sql.append("\n INNER JOIN PENSBI.PENSBME_PICK_REQ_RETURN R ON R.request_no = C.request_no and C.return_status ='"+STATUS_CANCEL+"'");
				sql.append("\n where 1=1  ");
				
				if( !Utils.isNull(o.getRequestDate()).equals("")){
					sql.append("\n and R.REQUEST_DATE = to_date('"+requestDateStr+"','dd/mm/yyyy') ");
				}
				
				if( !Utils.isNull(o.getRequestStatus()).equals("")){
					sql.append("\n and R.request_status = '"+Utils.isNull(o.getRequestStatus())+"'");
				}
				
				if( !Utils.isNull(o.getRequestNo()).equals("")){
					sql.append("\n and R.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
				}
				
				if( !Utils.isNull(o.getReturnDate()).equals("")){
					sql.append("\n and C.RETURN_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
				}
				
				if( !Utils.isNull(o.getReturnNo()).equals("")){
					sql.append("\n and C.return_no = '"+Utils.isNull(o.getReturnNo())+"'");
				}
				
				if( !Utils.isNull(o.getReturnStatus()).equals("")){
					sql.append("\n and C.return_status = '"+Utils.isNull(o.getReturnStatus())+"'");
				}
				
				if( !Utils.isNull(o.getCnNo()).equals("")){
					sql.append("\n and C.CN_NO = '"+Utils.isNull(o.getCnNo())+"'");
				}
				
			sql.append("\n UNION ALL");	
				sql.append("\n select R.*,C.return_no,C.return_status,C.return_date ");
				sql.append("\n from PENSBI.PENSBME_PICK_CONF_RETURN C ");
				sql.append("\n INNER JOIN PENSBI.PENSBME_PICK_REQ_RETURN R ON R.request_no = C.request_no and C.return_status ='"+STATUS_RETURN+"'");
				sql.append("\n where 1=1  ");
				
				if( !Utils.isNull(o.getRequestDate()).equals("")){
					sql.append("\n and R.REQUEST_DATE = to_date('"+requestDateStr+"','dd/mm/yyyy') ");
				}
				
				if( !Utils.isNull(o.getRequestStatus()).equals("")){
					sql.append("\n and R.request_status = '"+Utils.isNull(o.getRequestStatus())+"'");
				}
				
				if( !Utils.isNull(o.getRequestNo()).equals("")){
					sql.append("\n and R.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
				}
				
				if( !Utils.isNull(o.getReturnDate()).equals("")){
					sql.append("\n and C.RETURN_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
				}
				
				if( !Utils.isNull(o.getReturnNo()).equals("")){
					sql.append("\n and C.return_no = '"+Utils.isNull(o.getReturnNo())+"'");
				}
				
				if( !Utils.isNull(o.getReturnStatus()).equals("")){
					sql.append("\n and C.return_status = '"+Utils.isNull(o.getReturnStatus())+"'");
				}
				if( !Utils.isNull(o.getCnNo()).equals("")){
					sql.append("\n and C.CN_NO = '"+Utils.isNull(o.getCnNo())+"'");
				}

			sql.append("\n ) A ");
			sql.append("\n order by A.request_no desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
				   h = new ConfirmReturnWacoal();
				   h.setNo(r+"");
				   h.setRequestDate(Utils.stringValue(rst.getTimestamp("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
				   h.setRequestStatus(Utils.isNull(rst.getString("request_status"))); 
				   h.setRequestStatusDesc(getStatusDesc(Utils.isNull(rst.getString("request_status")))); 
				   h.setRemark(Utils.isNull(rst.getString("remark"))); 
				   
				   h.setTotalBox(rst.getInt("total_box"));
				   h.setTotalQty(rst.getInt("total_qty"));
				   
				   h.setReturnDate(Utils.stringValue(rst.getTimestamp("return_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setReturnNo(Utils.isNull(rst.getString("return_no")));
				   h.setReturnStatus(Utils.isNull(rst.getString("return_status"))); 
				   h.setReturnStatusDesc(getStatusDesc(Utils.isNull(rst.getString("return_status")))); 
				   
				   if(h.getRequestStatus().equals(STATUS_CANCEL) ){
					   h.setCanEdit(false);
					   h.setCanPrint(false);
				   }else{
					   if(h.getReturnStatus().equals(STATUS_CANCEL) || h.getReturnStatus().equals(STATUS_RETURN)){
						   h.setCanEdit(false);
					   }else{
						   h.setCanEdit(true); 
					   }
					   //can print
					   if( !h.getReturnStatus().equals(STATUS_CANCEL)){
						   h.setCanPrint(true);
					   }else{
						   h.setCanPrint(false);
					   }
				   }
				   
				//get Items
				if(getItems){
					h.setItems(searchItemFromReq(conn, h));
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
	public static List<ConfirmReturnWacoal> searchHeadFromReqDetail(ConfirmReturnWacoal o,boolean getItems ) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchHeadFromReqDetail(conn, o, getItems);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn !=null){
				conn.close();
			}
		}
	}
	public static List<ConfirmReturnWacoal> searchHeadFromReqDetail(Connection conn,ConfirmReturnWacoal o,boolean getItems ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfirmReturnWacoal h = null;
		List<ConfirmReturnWacoal> items = new ArrayList<ConfirmReturnWacoal>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select R.* ");
			sql.append("\n from PENSBI.PENSBME_PICK_REQ_RETURN R ");
			sql.append("\n where 1=1  ");

			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and R.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}

			sql.append("\n order by R.request_date,R.request_no ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ConfirmReturnWacoal();
			  // h.setNo(r);
			   h.setRequestDate(Utils.stringValue(rst.getTimestamp("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   
			   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
			   h.setRequestStatus(Utils.isNull(rst.getString("request_status"))); 
			   h.setRequestStatusDesc(getStatusDesc(Utils.isNull(rst.getString("request_status")))); 
			   h.setRemark(Utils.isNull(rst.getString("remark"))); 
			   
			   h.setTotalBox(rst.getInt("total_box"));
			   h.setTotalQty(rst.getInt("total_qty"));
			   //h.setCnNo(Utils.isNull(rst.getString("CN_NO")));

			   if(h.getRequestStatus().equals(STATUS_CANCEL) ){
				   h.setCanEdit(false);
				   h.setCanPrint(false);
				   h.setCanCancel(false);
			   }else{
			       h.setCanEdit(true);
			       h.setCanPrint(false);
			       h.setCanCancel(false);
			   }
			   
			 //get Items
			  if(getItems){
			    h.setItems(searchItemFromReq(conn, h));
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
	
	public static String  searchCnNoByDate(Connection conn,ConfirmReturnWacoal o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String cnNo = "";
		try {
			sql.append("\n select distinct R.cn_no ");
			sql.append("\n from PENSBI.PENSBME_PICK_CONF_RETURN R ");
			sql.append("\n where 1=1  ");

			if( !Utils.isNull(o.getReturnDate()).equals("")){
				Date tDate  = Utils.parse(o.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String dateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n and return_date = to_date('"+dateStr+"','dd/mm/yyyy')");
			}
			logger.debug("sql:"+sql);
	
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			  cnNo = rst.getString("cn_no");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			
			} catch (Exception e) {}
		}
		return cnNo;
	}
	
	public static int  getTotalQtyByReturnDate(Connection conn,ConfirmReturnWacoal o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalQty = 0;
		try {
			sql.append("\n select return_date ,sum(total_qty) as total_qty ");
			sql.append("\n from PENSBI.PENSBME_PICK_CONF_RETURN R ");
			sql.append("\n where 1=1  ");
			sql.append("\n and return_status ='"+STATUS_RETURN+"'");
			if( !Utils.isNull(o.getReturnDate()).equals("")){
				Date tDate  = Utils.parse(o.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String dateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n and return_date = to_date('"+dateStr+"','dd/mm/yyyy')");
			}
			sql.append("\n group by return_date ");
			
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			  totalQty = rst.getInt("total_qty");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalQty;
	}
	
	public static List<ConfirmReturnWacoal> searchHeadFromReturn(ConfirmReturnWacoal o,boolean getItems ) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			
			return searchHeadFromReturn(conn, o, getItems);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	public static List<ConfirmReturnWacoal> searchHeadFromReturn(Connection conn,ConfirmReturnWacoal o,boolean getItems ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfirmReturnWacoal h = null;
		List<ConfirmReturnWacoal> items = new ArrayList<ConfirmReturnWacoal>();
		int r = 1;
		int c = 1;
		try {

			sql.append("\n select R.*,C.return_no,C.return_status,C.return_date,C.CN_NO ,C.remark as conf_remark ");
			sql.append("\n from PENSBI.PENSBME_PICK_REQ_RETURN R ");
			sql.append("\n LEFT OUTER JOIN PENSBI.PENSBME_PICK_CONF_RETURN C ON R.request_no = C.request_no ");
			sql.append("\n where 1=1  ");
			
			if( !Utils.isNull(o.getRequestDate()).equals("")){
				Date tDate  = Utils.parse(o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String dateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n and R.REQUEST_DATE = to_date('"+dateStr+"','dd/mm/yyyy') ");
			}
			
			if( !Utils.isNull(o.getRequestStatus()).equals("")){
				sql.append("\n and R.request_status = '"+Utils.isNull(o.getRequestStatus())+"'");
			}
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and R.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			
			if( !Utils.isNull(o.getReturnDate()).equals("")){
				Date tDate  = Utils.parse(o.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String dateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n and C.RETURN_DATE = to_date('"+dateStr+"','dd/mm/yyyy') ");
			}
			
			if( !Utils.isNull(o.getReturnNo()).equals("")){
				sql.append("\n and C.return_no = '"+Utils.isNull(o.getReturnNo())+"'");
			}
			
			if( !Utils.isNull(o.getReturnStatus()).equals("")){
				sql.append("\n and C.return_status = '"+Utils.isNull(o.getReturnStatus())+"'");
			}
			
			sql.append("\n order by R.request_date,R.request_no ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
				   h = new ConfirmReturnWacoal();
				  // h.setNo(r);
				   h.setRequestDate(Utils.stringValue(rst.getTimestamp("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
				   h.setRequestStatus(Utils.isNull(rst.getString("request_status"))); 
				   h.setRequestStatusDesc(getStatusDesc(Utils.isNull(rst.getString("request_status")))); 
				   h.setRemark(Utils.isNull(rst.getString("conf_remark"))); 
				   
				   h.setTotalBox(rst.getInt("total_box"));
				   h.setTotalQty(rst.getInt("total_qty"));
				   
				   h.setReturnDate(Utils.stringValue(rst.getTimestamp("return_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setReturnNo(Utils.isNull(rst.getString("return_no")));
				   h.setReturnStatus(Utils.isNull(rst.getString("return_status"))); 
				   h.setReturnStatusDesc(getStatusDesc(Utils.isNull(rst.getString("return_status")))); 
				   h.setCnNo(Utils.isNull(rst.getString("CN_NO")));
				   
				   if(h.getRequestStatus().equals(STATUS_CANCEL) ){
					   h.setCanEdit(false);
					   h.setCanPrint(false);
					   h.setCanCancel(false);
				   }else{
					   //can Edit
					   if(h.getReturnStatus().equals(STATUS_CANCEL) || h.getReturnStatus().equals(STATUS_RETURN)){
						   h.setCanEdit(false);
					   }else{
						   h.setCanEdit(true); 
					   }
					   
					   //can Cancel
					   if(h.getReturnStatus().equals(STATUS_RETURN)){
						   h.setCanCancel(true);
					   }else{
						   h.setCanCancel(false); 
					   }
					   
					   //can print
					   if( !h.getReturnStatus().equals(STATUS_CANCEL)){
						   h.setCanPrint(true);
					   }else{
						   h.setCanPrint(false);
					   }
				   }
 
				//get Items
				if(getItems){
					h.setItems(searchItemFromReq(conn, h));
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
	
	public static List<ConfirmReturnWacoal> searchItemFromReq(Connection conn,ConfirmReturnWacoal o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfirmReturnWacoal h = null;
		int r = 1;
		int c = 1;
        List<ConfirmReturnWacoal> items = new ArrayList<ConfirmReturnWacoal>();
		try {
			sql.append("\n select i.* " +
			          "\n ,(select max(name) from PENSBME_PICK_JOB j where j.job_id= i.job_id) as job_name "+
					  "\n from PENSBI.PENSBME_PICK_REQ_RETURN h ,PENSBI.PENSBME_PICK_REQ_RETURN_I i ");
			sql.append("\n where 1=1 and h.request_no = i.request_no  \n");
			
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and h.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			
			sql.append("\n order by h.request_date,h.request_no ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());

			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new ConfirmReturnWacoal();
			   h.setNo(r+"/"+o.getTotalBox());
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
			} catch (Exception e) {}
		}
		return items;
	}
	
	public static List<ConfirmReturnWacoal> searchItemFromReturn(Connection conn,ConfirmReturnWacoal o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfirmReturnWacoal h = null;
		int r = 1;
		int c = 1;
        List<ConfirmReturnWacoal> items = new ArrayList<ConfirmReturnWacoal>();
		try {
			sql.append("\n select i.* " +
			          "\n ,(select max(name) from PENSBME_PICK_JOB j where j.job_id= i.job_id) as job_name "+
					  "\n from PENSBI.PENSBME_PICK_CONF_RETURN h ,PENSBI.PENSBME_PICK_CONF_RETURN_I i ");
			sql.append("\n where 1=1 and h.request_no = i.request_no  \n");
			
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and h.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			
			sql.append("\n order by h.request_date,h.request_no ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());

			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new ConfirmReturnWacoal();
			   h.setNo(r+"/"+o.getTotalBox());
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
			} catch (Exception e) {}
		}
		return items;
	}
	
	public static List<ControlReturnReport> searchItemForReportControlReturn(Connection conn,ConfirmReturnWacoal o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfirmReturnWacoal h = null;
		int maxRow = 40;
		int r = 1;
		int p = 1;
        List<ControlReturnReport> items = new ArrayList<ControlReturnReport>();
		try {
			sql.append("\n select i.line_Id,i.qty " +
					  "\n from PENSBI.PENSBME_PICK_CONF_RETURN h ,PENSBI.PENSBME_PICK_CONF_RETURN_I i ");
			sql.append("\n where 1=1 and h.return_no = i.return_no  \n");
			if( !Utils.isNull(o.getReturnNo()).equals("")){
				sql.append("\n and h.return_no = '"+Utils.isNull(o.getReturnNo())+"'");
			}
			
			sql.append("\n order by i.line_Id asc  ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());

			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   //page
			   if(r > maxRow){
				  p++;
				  r = 1;
			   }
			   logger.debug("r["+r+"]r%maxRow["+(r%maxRow)+"]p["+p+"]");
			   
			   if(p==1){
				  ControlReturnReport item = new ControlReturnReport();
			      item.setLineId1(rst.getString("line_Id"));
			      item.setQty1(rst.getString("qty"));
			      items.add(item);
			   }else if(p==2){
				   ControlReturnReport item = items.get(r-1);
				   item.setLineId2(rst.getString("line_Id"));
				   item.setQty2(rst.getString("qty"));
				   items.set(r-1,item);  
				   
			   }else if(p==3){
				   ControlReturnReport item = items.get(r-1);
				   item.setLineId3(rst.getString("line_Id"));
				   item.setQty3(rst.getString("qty"));
				   items.set(r-1,item);   
			   }else if(p==4){
				   ControlReturnReport item = items.get(r-1);
				   item.setLineId4(rst.getString("line_Id"));
				   item.setQty4(rst.getString("qty"));
				   items.set(r-1,item);  
			   }else if(p==5){
				   ControlReturnReport item = items.get(r-1);
				   item.setLineId5(rst.getString("line_Id"));
				   item.setQty5(rst.getString("qty"));
				   items.set(r-1,item); 
			   }

			   r++;
			   
			}//while
			
			if(items != null && items.size() < maxRow){
				int diffRow = new Double(maxRow).intValue() - items.size();
				for(int i=0;i<diffRow;i++){
					ControlReturnReport item = new ControlReturnReport();
					item.setLineId1("");
				    item.setQty1("");
				    items.add(item);
				}
			}

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
	
	public static List<ReturnBoxReport> searchItemForReportReturnBox(Connection conn,ConfirmReturnWacoal o,String boxNo ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int r = 1;
		int p = 1;
        List<ReturnBoxReport> items = new ArrayList<ReturnBoxReport>();
		try {
			sql.append("\n  select  ");
			sql.append("\n  rh.cn_no,rh.remark,ri.box_no,ri.line_id,rh.total_box ");
			sql.append("\n  from  ");
			sql.append("\n  PENSBI.PENSBME_PICK_CONF_RETURN   rh , ");
			sql.append("\n  PENSBI.PENSBME_PICK_CONF_RETURN_I ri ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and rh.return_no = ri.return_no  ");
			if( !Utils.isNull(o.getReturnNo()).equals("")){
				sql.append("\n  and rh.return_no = '"+Utils.isNull(o.getReturnNo())+"'");
			}
			if( !Utils.isNull(boxNo).equals("")){
				sql.append("\n  and ri.box_no = '"+Utils.isNull(boxNo)+"'");
			}
			sql.append("\n   group by  rh.cn_no,rh.remark,ri.box_no, ri.line_id ,rh.total_box ");
		
			sql.append("\n order by  ri.line_id asc   ");
	
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());

			rst = ps.executeQuery();

			while(rst.next()) {
				
			    items.addAll(searchItemForReportReturnBoxItem(conn,rst.getString("box_no"),rst.getString("line_id"),
			    		rst.getString("total_box"),rst.getString("remark"),rst.getString("cn_no")));
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
	
	private static List<ReturnBoxReport> searchItemForReportReturnBoxItem(Connection conn,String boxNo,String lineId,String totalBox,String address,String cnNo) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int r = 1;
		int p = 1;
        List<ReturnBoxReport> items = new ArrayList<ReturnBoxReport>();
		try {
			sql.append("\n select  ");
			sql.append("\n A.job_id,A.name,A.cust_group, ");
			sql.append("\n A.group_code, ");
			sql.append("\n A.material_master, ");
			sql.append("\n A.whole_price_bf, ");
			sql.append("\n A.qty, ");
			sql.append("\n (A.qty*A.whole_price_bf) as whole_price_bf_amount, ");
			sql.append("\n A.retail_price_bf, ");
			sql.append("\n (A.qty*A.retail_price_bf) as retail_price_bf_amount ");
			sql.append("\n from( ");
			sql.append("\n  select  ");
			sql.append("\n  j.job_id,j.name,j.cust_group,");
			sql.append("\n  bi.group_code, ");
			sql.append("\n  bi.material_master, bi.whole_price_bf, ");
			sql.append("\n  bi.retail_price_bf , count(*) as qty ");
			sql.append("\n  from  ");
			sql.append("\n  PENSBI.PENSBME_PICK_BARCODE  bh, ");
			sql.append("\n  PENSBI.PENSBME_PICK_BARCODE_ITEM bi, ");
			sql.append("\n  PENSBI.PENSBME_PICK_JOB  j ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and bh.job_id = bi.job_id ");
			sql.append("\n  and bh.box_no = bi.box_no ");
			sql.append("\n  and bi.job_id = j.job_id ");
			sql.append("\n  and bh.status = '"+PickConstants.STATUS_RETURN+"'");
			sql.append("\n  and bh.box_no = '"+Utils.isNull(boxNo)+"'");
			
			sql.append("\n  group by   j.job_id,j.name,j.cust_group,bi.group_code, ");
			sql.append("\n  bi.material_master, bi.whole_price_bf, bi.retail_price_bf  ");
			sql.append("\n ) A ");
			sql.append("\n order by A.group_code, A.material_master  ");
	
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());

			rst = ps.executeQuery();

			while(rst.next()) {
				ReturnBoxReport item = new ReturnBoxReport();
				item.setNo(r+"");
				item.setJobName(Utils.isNull(rst.getString("job_id"))+" "+Utils.isNull(rst.getString("name")));
				item.setCustGroup(Utils.isNull(rst.getString("cust_group")));
				item.setAddress(Utils.isNull(address));
				item.setCnNo(Utils.isNull(cnNo));
				
				item.setBoxNo(boxNo);
				item.setBoxNoDisp(lineId+"/"+totalBox);
				item.setGroupCode(Utils.isNull(rst.getString("group_code")));
				item.setMaterialMaster(Utils.isNull(rst.getString("material_master")));
				item.setQty(rst.getInt("qty"));
				item.setWholePriceBF(rst.getDouble("whole_price_bf"));
				item.setWholePriceBFAmount(rst.getDouble("whole_price_bf_amount"));
				item.setRetailPriceBF(rst.getDouble("retail_price_bf"));
				item.setRetailPriceBFAmount(rst.getDouble("retail_price_bf_amount"));
			    r++;
			   
			    items.add(item);
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
	
	public static String boxNoSeq(Connection conn,ConfirmReturnWacoal o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String boxNoSeq = "";
		try {
	
			sql.append("\n  select  ");
			sql.append("\n  i.line_id,h.total_box ");
			sql.append("\n  from  ");
			sql.append("\n  PENSBI.PENSBME_PICK_CONF_RETURN rh , ");
			sql.append("\n  PENSBI.PENSBME_PICK_CONF_RETURN_I ri ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and rh.return_no = ri.return_no  ");
			if( !Utils.isNull(o.getReturnNo()).equals("")){
				sql.append("\n  and rh.return_no = '"+Utils.isNull(o.getReturnNo())+"'");
			}
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());

			rst = ps.executeQuery();

			if(rst.next()) {
				
			}//while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return boxNoSeq;
	}
	
	public static ConfirmReturnWacoal save(Connection conn,ConfirmReturnWacoal h) throws Exception{
		try{
			
			//check requestNo
			if(Utils.isNull(h.getReturnNo()).equals("")){
				//Update req_table to status =USE
				ReqReturnWacoal req = new ReqReturnWacoal();
				req.setRequestNo(h.getRequestNo());
				req.setStatus(STATUS_USED);
				req.setUpdateUser(h.getUpdateUser());
				
				ReqReturnWacoalDAO.updateStatusModel(conn, req);
				
				//set new status
				h.setRequestStatus(req.getStatus());
				
				//Gen requestNo
				h.setReturnNo(genReturnNo(conn,new Date()) );
				h.setReturnStatus(STATUS_RETURN);
				logger.debug("ReturnNO:"+h.getReturnNo());
				
				//1 day 1 CNNO
				String cnNo = searchCnNoByDate(conn,h);
				if(Utils.isNull(cnNo).equals("")){
					Date returnDate  = Utils.parse(h.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					cnNo = genCnNo(conn, returnDate);
				}
				h.setCnNo(cnNo);
				logger.debug("CN_NO:"+h.getCnNo());

				saveHeadModel(conn, h);
				
				//Insert Line 
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ConfirmReturnWacoal l = (ConfirmReturnWacoal)h.getItems().get(i);
					   l.setReturnNo(h.getReturnNo());
	
					   //save item req 
				       saveItemModel(conn, l);
				   }
				}
			}
		}catch(Exception e){
		  throw e;
		}
		return h;
	}
	
	public static ConfirmReturnWacoal cancelReturn(Connection conn,ConfirmReturnWacoal h) throws Exception{
		try{
			/** Update Request to NEW **/
			ReqReturnWacoal req = new ReqReturnWacoal();
			req.setRequestNo(h.getRequestNo());
			req.setStatus(STATUS_NEW);
			req.setUpdateUser(h.getUpdateUser());
			
			ReqReturnWacoalDAO.updateStatusModel(conn, req);
			
			/** Update Return to Cancel **/
			updateStatusReturnModel(conn, h);

		}catch(Exception e){
		  throw e;
		}
		return h;
	}
	
	// 	RQYYMMXXX  ( เช่น RQ5703001 )  			
	 private static String genReturnNo(Connection conn,Date date) throws Exception{
       String docNo = "";
		   try{
			   
			   String today = df.format(date);
			   String[] d1 = today.split("/");
			  // System.out.println("d1[0]:"+d1[0]);
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			 //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"RETURN_WACOAL","RETURN_NO",date);
			   
			   docNo = "RN"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }
		  return docNo;
	}
	 
	 //yyyymmrrr  เช่น 255705001  เป็นต้น
	 private static String genCnNo(Connection conn,Date date) throws Exception{
       String docNo = "";
		   try{
			   
			   String today = df.format(date);
			   String[] d1 = today.split("/");
			  // System.out.println("d1[0]:"+d1[0]);
			   int curYear = Integer.parseInt(d1[0].substring(0,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			 //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"RETURN_WACOAL","CN_NO",date);
			   
			   docNo = new DecimalFormat("0000").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }
		  return docNo;
	}
	 
	 private static void saveHeadModel(Connection conn,ConfirmReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			int c =1;
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_CONF_RETURN \n");
				sql.append(" ( RETURN_DATE,RETURN_NO,REQUEST_NO ,TOTAL_BOX,TOTAL_QTY \n");
				sql.append("  ,CREATE_DATE ,CREATE_USER,RETURN_STATUS,REMARK,CN_NO) \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ? ,? ,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				Date returnDate = Utils.parse( o.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				ps.setTimestamp(c++, new java.sql.Timestamp(returnDate.getTime()));
				ps.setString(c++, o.getReturnNo());
				ps.setString(c++, o.getRequestNo());
				ps.setInt(c++, o.getTotalBox());
				ps.setInt(c++, o.getTotalQty());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getReturnStatus());
				ps.setString(c++, o.getRemark());
				ps.setString(c++, o.getCnNo());
				
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
	 
	 private static void saveItemModel(Connection conn,ConfirmReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			int c =1;
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_CONF_RETURN_I \n");
				sql.append(" ( RETURN_NO ,LINE_ID ,JOB_ID, BOX_NO, QTY   \n");
				sql.append("  ,CREATE_DATE ,CREATE_USER) \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ? ) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, o.getReturnNo());
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
	 
	public static void updateStatusReturnModel(Connection conn,ConfirmReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_CONF_RETURN SET  \n");
				sql.append(" RETURN_STATUS = ? ,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE RETURN_NO = ?  \n" );
	
				ps = conn.prepareStatement(sql.toString());

				ps.setString(c++, o.getReturnStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.setString(c++, Utils.isNull(o.getReturnNo()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	
	public static PickStock searchConfirmStockItemByReturnNo4Report(Connection conn,ConfirmReturnWacoal c ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PickStock h = null;
		int r = 1;
        List<PickStock> items = new ArrayList<PickStock>();
        int totalQty = 0;
        PickStock o = new PickStock();
		try {
			sql.append("\n select i.job_id,i.box_no,i.MATERIAL_MASTER,i.group_code,i.pens_item ,count(*) as qty ");
			sql.append("\n ,(select max(name) from PENSBME_PICK_JOB j where j.job_id = i.job_id) as job_name ");
			sql.append("\n FROM PENSBME_PICK_BARCODE_ITEM i ,");
			sql.append("\n PENSBME_PICK_CONF_RETURN_I ci");
			sql.append("\n where 1=1 ");
			sql.append("\n and i.box_no = ci.box_no");
			if( !Utils.isNull(c.getReturnNo()).equals("")){
				sql.append("\n and ci.return_no = '"+Utils.isNull(c.getReturnNo())+"'");
			}
			sql.append("\n group by i.job_id,i.box_no,i.MATERIAL_MASTER,i.group_code,i.pens_item ");
			sql.append("\n order by i.job_id,i.box_no,i.MATERIAL_MASTER,i.group_code,i.pens_item  asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new PickStock();
			
			   h.setJobId(rst.getString("job_id"));
			   h.setJobName(rst.getString("job_name"));
			   h.setBoxNo(rst.getString("box_no"));
			  
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"), Utils.format_current_no_disgit));
			   h.setQtyInt(rst.getInt("qty"));
			   
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
			} catch (Exception e) {}
		}
		return o;
	}
		
}
