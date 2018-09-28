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
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;

public class ConfirmReturnDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	static int maxRowOnePage = 40;
	static int maxRowMorePage = 47;
	static int maxRowMorePage2 = 51;
	static int maxColumn = 5;
	static int maxCountPerPage = maxRowMorePage * maxColumn;
	static int maxCountPerPage2 = maxRowMorePage2 * maxColumn;

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
	
	public static List<ConfirmReturnWacoal> searchHead(Connection conn,ConfirmReturnWacoal o,boolean getItems,boolean allRec ,int currPage,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfirmReturnWacoal h = null;
		List<ConfirmReturnWacoal> items = new ArrayList<ConfirmReturnWacoal>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
				sql.append("\n SELECT A.* FROM( ");
					sql.append("\n select R.*  ");
					sql.append(" ,(select NVL(count(distinct B.box_no),0) FROM PENSBME_PICK_RETURN_I B WHERE B.request_no = R.request_no group by B.request_no) as total_box \n");
					sql.append(" ,(select NVL(count(*),0) FROM PENSBME_PICK_RETURN_I B WHERE B.request_no = R.request_no group by B.request_no) as total_qty \n");
					sql.append("\n from PENSBI.PENSBME_PICK_RETURN R ");
					sql.append("\n where 1=1  ");
					sql.append(genWhereSqlSearchHead(o));
				sql.append("\n ) A ");
				sql.append("\n order by A.request_no desc ");
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
            //Clac Start No
			r = Utils.calcStartNoInPage(currPage, pageSize);
			while(rst.next()) {
				   h = new ConfirmReturnWacoal();
				   h.setNo(r+"");
				   h.setRequestDate(Utils.stringValue(rst.getTimestamp("request_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setRemark(Utils.isNull(rst.getString("return_remark"))); 
				   h.setTotalBox(rst.getInt("total_box"));
				   h.setTotalQty(rst.getInt("total_qty"));
				   h.setReturnDate(Utils.stringValue(rst.getTimestamp("return_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setReturnNo(Utils.isNull(rst.getString("return_no")));
				   h.setCnNo(Utils.isNull(rst.getString("CN_NO")));
				   
				   if(h.getStatus().equals(STATUS_CANCEL) ){
					   h.setCanEdit(false);
					   h.setCanPrint(false);
				   }else{
					   if(h.getStatus().equals(STATUS_CANCEL) || h.getStatus().equals(STATUS_RETURN)){
						   h.setCanEdit(false);
					   }else{
						   h.setCanEdit(true); 
					   }
					   //can print
					   if( !h.getStatus().equals(STATUS_CANCEL)){
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
	
	public static int searchTotalRecHead(Connection conn,ConfirmReturnWacoal o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 1;
		try {
			sql.append("\n SELECT count(*) as c FROM( ");
				sql.append("\n select R.*  ");
				sql.append(" ,(select NVL(count(distinct B.box_no),0) FROM PENSBME_PICK_RETURN_I B WHERE B.request_no = R.request_no group by B.request_no) as total_box \n");
				sql.append(" ,(select NVL(count(*),0) FROM PENSBME_PICK_RETURN_I B WHERE B.request_no = R.request_no group by B.request_no) as total_qty \n");
				sql.append("\n from PENSBI.PENSBME_PICK_RETURN R ");
				sql.append("\n where 1=1  ");
				sql.append(genWhereSqlSearchHead(o));
			sql.append("\n ) A ");
			sql.append("\n order by A.request_no desc ");
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
	public static StringBuffer genWhereSqlSearchHead(ConfirmReturnWacoal o) throws Exception{
		StringBuffer sql = new StringBuffer("");

		if( !Utils.isNull(o.getRequestDate()).equals("")){
			Date tDate  = Utils.parse(o.getRequestDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String requestDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and R.REQUEST_DATE = to_date('"+requestDateStr+"','dd/mm/yyyy') ");
		}
		if( !Utils.isNull(o.getStatus()).equals("")){
			sql.append("\n and R.status = '"+Utils.isNull(o.getStatus())+"'");
		}
		if( !Utils.isNull(o.getRequestNo()).equals("")){
			sql.append("\n and R.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
		}
		if( !Utils.isNull(o.getReturnDate()).equals("")){
			Date tDate  = Utils.parse(o.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String returnDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and R.RETURN_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
		}
		if( !Utils.isNull(o.getReturnNo()).equals("")){
			sql.append("\n and R.return_no = '"+Utils.isNull(o.getReturnNo())+"'");
		}
		if( !Utils.isNull(o.getCnNo()).equals("")){
			sql.append("\n and R.CN_NO = '"+Utils.isNull(o.getCnNo())+"'");
		}
		return sql;
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
			sql.append(" ,(select NVL(count(distinct B.box_no),0) FROM PENSBME_PICK_RETURN_I B WHERE B.request_no = R.request_no group by B.request_no) as total_box \n");
			sql.append(" ,(select NVL(count(*),0) FROM PENSBME_PICK_RETURN_I B WHERE B.request_no = R.request_no group by B.request_no) as total_qty \n");
			sql.append("\n from PENSBI.PENSBME_PICK_RETURN R ");
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
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   h.setRemark(Utils.isNull(rst.getString("return_remark"))); 
			   
			   h.setTotalBox(rst.getInt("total_box"));
			   
			   h.setTotalQty(rst.getInt("total_qty"));
			   h.setReturnNo(Utils.isNull(rst.getString("RETURN_NO")));
			   h.setCnNo(Utils.isNull(rst.getString("CN_NO")));
			   if(rst.getTimestamp("return_date") != null)
			     h.setReturnDate(Utils.stringValue(rst.getTimestamp("return_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   
			   if(h.getStatus().equals(STATUS_CANCEL) ){
				   h.setCanEdit(false);
				   h.setCanPrint(false);
			   }else{
				   if(h.getStatus().equals(STATUS_CANCEL) || h.getStatus().equals(STATUS_RETURN)){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
				   //can print
				   if( !h.getStatus().equals(STATUS_CANCEL) && !h.getStatus().equals(STATUS_NEW)){
					   h.setCanPrint(true);
				   }else{
					   h.setCanPrint(false);
				   }
				   //Can cancel
				   if( h.getStatus().equals(STATUS_RETURN)){
					   h.setCanCancel(true);
				   }else{
					   h.setCanCancel(false); 
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
	
	public static String  searchCnNoByDate(Connection conn,ConfirmReturnWacoal o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String cnNo = "";
		try {
			sql.append("\n select distinct R.cn_no ");
			sql.append("\n from PENSBI.PENSBME_PICK_RETURN R ");
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
			sql.append("\n select H.return_date ,count(*) as total_qty ");
			sql.append("\n from PENSBME_PICK_RETURN H,PENSBME_PICK_RETURN_I R ");
			sql.append("\n where 1=1  ");
			sql.append("\n and H.request_no = R.request_no");
			sql.append("\n and H.status ='"+STATUS_RETURN+"'");
			if( !Utils.isNull(o.getReturnDate()).equals("")){
				Date tDate  = Utils.parse(o.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String dateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				sql.append("\n and H.return_date = to_date('"+dateStr+"','dd/mm/yyyy')");
			}
			sql.append("\n group by H.return_date ");
			
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
	
	
	public static List<ConfirmReturnWacoal> searchItemFromReq(Connection conn,ConfirmReturnWacoal o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfirmReturnWacoal h = null;
		int r = 1;
		int c = 1;
        List<ConfirmReturnWacoal> items = new ArrayList<ConfirmReturnWacoal>();
		try {
			sql.append(" select i.job_id,i.box_no \n");
			sql.append(" ,(select max(name) from PENSBME_PICK_JOB j where j.job_id= i.job_id) as job_name \n");
			sql.append(" ,count(*) as qty \n");	
            sql.append(" from PENSBME_PICK_RETURN h ,PENSBME_PICK_RETURN_I i ");
			sql.append("\n where 1=1 and h.request_no = i.request_no  \n");
			
			if( !Utils.isNull(o.getRequestNo()).equals("")){
				sql.append("\n and h.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			sql.append("\n group by i.job_id,i.box_no ");
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
	
	public static int calcMaxPageReportControlReturn(Connection conn,ConfirmReturnWacoal o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalPage = 0;
		try {
			sql.append("\n select count(distinct i.box_no) as c " );
			sql.append("\n from PENSBME_PICK_RETURN_I i ");
			sql.append("\n where 1=1 \n");
		    sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			sql.append("\n group by i.box_no  ");
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());

			rst = ps.executeQuery();

			if(rst.next()) {
				totalPage =  Utils.calcTotalPage(rst.getInt("c"), maxCountPerPage);
		
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalPage;
	}
	
	public static List<ControlReturnReport> searchItemForReportControlReturn(Connection conn,ConfirmReturnWacoal o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfirmReturnWacoal h = null;
	    int totalPage = 0;
		int row = 1;
		int column = 1;
		int pageNumber = 1;
		int maxRowInpage = 0;
		int no=1;
		int startSplitPage = 0;
		int maxRowCase = maxRowMorePage;
        List<ControlReturnReport> items = new ArrayList<ControlReturnReport>();
        int startRow = 0;
		try {
			sql.append("\n select i.box_no,count(*) as qty " );
			sql.append("\n from PENSBME_PICK_RETURN_I i ");
			sql.append("\n where 1=1 \n");
		    sql.append("\n and i.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			sql.append("\n group by i.box_no  ");
			sql.append("\n order by i.box_no asc  ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());

			rst = ps.executeQuery();

			while(rst.next()) {
			    
				if(totalPage==1){
				  //one page
				   if(row > maxRowOnePage){
					  column++;//Column
					  row = 1; //reset Row =1
				   }
				}else{
					
				   //2 page
					if((pageNumber-1) ==1){
					   startSplitPage = maxCountPerPage * (pageNumber-1); //40
					}else if((pageNumber-1) ==2){
					   startSplitPage = maxCountPerPage + maxCountPerPage2;
					}else{
					   startSplitPage = maxCountPerPage + (maxCountPerPage2 * (pageNumber-2));//47
					}
					
					if(no > startSplitPage){
						column = 1;//reset Column
						if(pageNumber == 1){
							maxRowCase = maxRowMorePage;//47 Row
							startRow =((pageNumber-1)*maxRowCase )+1; //start row= 1;
							
							 maxRowInpage = pageNumber*maxRowCase; //reset Row = 40
						}
						if(pageNumber ==2){
							maxRowCase = maxRowMorePage2;//51 Row
							startRow =((pageNumber-1)*maxRowMorePage )+1; //start row= 41,;
							
							 maxRowInpage = maxRowMorePage +maxRowMorePage2; //reset Row = 87
						}
                        if(pageNumber > 2){
                        	maxRowCase = maxRowMorePage2;//51 Row
                        	startRow = maxRowMorePage2 + ((pageNumber-2)*maxRowCase )+1; //start row= 88;
							
							maxRowInpage = maxRowMorePage2 +(pageNumber-1)*maxRowCase; //reset Row = 134,..n
						}
						
                        row = startRow;
                       
						pageNumber++;//Next Page
					}
					//reset
					if(row > maxRowInpage){
						column++;//Column
						row = startRow;
					}
					 
				}

			   logger.debug("No["+no+"]startSplitPage["+startSplitPage+"]pageNumber["+(pageNumber-1)+"]row["+row+"]maxRowInpage["+maxRowInpage+"]column["+column+"]");
			   
			   if(column==1){
				  ControlReturnReport item = new ControlReturnReport();
			      item.setLineId1(no+"");
			      item.setQty1(rst.getString("qty"));
			      items.add(item);
			      //logger.debug("lastitem size["+items.size()+"]");
			   }else if(column==2){
				   ControlReturnReport item = items.get(row-1);
				   item.setLineId2(no+"");
				   item.setQty2(rst.getString("qty"));
				   items.set(row-1,item);  
				   
			   }else if(column==3){
				   ControlReturnReport item = items.get(row-1);
				   item.setLineId3(no+"");
				   item.setQty3(rst.getString("qty"));
				   items.set(row-1,item);   
			   }else if(column==4){
				   ControlReturnReport item = items.get(row-1);
				   item.setLineId4(no+"");
				   item.setQty4(rst.getString("qty"));
				   items.set(row-1,item);  
			   }else if(column==5){
				   ControlReturnReport item = items.get(row-1);
				   item.setLineId5(no+"");
				   item.setQty5(rst.getString("qty"));
				   items.set(row-1,item); 
			   }
			   
              row++;
              no++;
			}//while
			
			logger.debug("items Size:"+items.size());
			//Add blank Column
			if(totalPage==1){
				if(items != null && items.size() < maxRowOnePage){
					int diffRow = new Double(maxRowOnePage).intValue() - items.size();
					for(int i=0;i<diffRow;i++){
						ControlReturnReport item = new ControlReturnReport();
						item.setLineId1("");
					    item.setQty1("");
					    items.add(item);
					}
				}
			}else{
				//More than one page
				logger.debug("lastRow["+row+"]maxZRowInPage["+maxRowInpage+"]");
				if(items != null && items.size() > 0 ){
					if((maxRowInpage - row) > 6){
						int diffRow = (maxRowInpage - row)-6;
						for(int i=0;i<diffRow;i++){
							ControlReturnReport item = new ControlReturnReport();
							item.setLineId1("");
						    item.setQty1("");
						    items.add(item);
						}
					}
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
		int no = 1;
        List<ReturnBoxReport> items = new ArrayList<ReturnBoxReport>();
		try {
			sql.append("\n  select  ");
			sql.append("\n  rh.cn_no,rh.return_remark as remark,ri.box_no");
			sql.append("\n ,(select NVL(count(distinct B.box_no),0) FROM PENSBME_PICK_RETURN_I B WHERE B.request_no ='"+Utils.isNull(o.getRequestNo())+"' group by B.request_no) as total_box");
			sql.append("\n  from  ");
			sql.append("\n  PENSBME_PICK_RETURN   rh , ");
			sql.append("\n  PENSBME_PICK_RETURN_I ri ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and rh.request_no = ri.request_no  ");
			if( !Utils.isNull(o.getReturnNo()).equals("")){
				sql.append("\n  and rh.request_no = '"+Utils.isNull(o.getRequestNo())+"'");
			}
			if( !Utils.isNull(boxNo).equals("")){
				sql.append("\n  and ri.box_no = '"+Utils.isNull(boxNo)+"'");
			}
			sql.append("\n   group by  rh.cn_no,rh.return_remark,ri.box_no");
		
			sql.append("\n order by  ri.box_no asc   ");
	
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());

			rst = ps.executeQuery();

			while(rst.next()) {
				
			    items.addAll(searchItemForReportReturnBoxItem(conn,rst.getString("box_no"),no,rst.getString("total_box"),rst.getString("remark"),rst.getString("cn_no")));
			    no++;
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
	
	private static List<ReturnBoxReport> searchItemForReportReturnBoxItem(Connection conn,String boxNo,int lineId,String totalBox,String address,String cnNo) throws Exception {
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
			sql.append("\n  PENSBME_PICK_RETURN  bh, ");
			sql.append("\n  PENSBME_PICK_RETURN_I bi, ");
			sql.append("\n  PENSBME_PICK_JOB j ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and bh.request_no = bi.request_no ");
			sql.append("\n  and bi.job_id = j.job_id ");
			sql.append("\n  and bh.status = '"+PickConstants.STATUS_RETURN+"'");
			//sql.append("\n  and bi.status = '"+PickConstants.STATUS_RETURN+"'");
			sql.append("\n  and bi.box_no = '"+Utils.isNull(boxNo)+"'");
			
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
			//if(Utils.isNull(h.getReturnNo()).equals("")){
				
				//Gen requestNo
				h.setReturnNo(genReturnNo(new Date()) );
				h.setStatus(STATUS_RETURN);
				
				logger.debug("ReturnNO:"+h.getReturnNo());
				
				//1 day 1 CNNO
				String cnNo = searchCnNoByDate(conn,h);
				if(Utils.isNull(cnNo).equals("")){
					Date returnDate  = Utils.parse(h.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					cnNo = genCnNo(conn, returnDate);
				}
				h.setCnNo(cnNo);
				logger.debug("CN_NO:"+h.getCnNo());

				updateConfirmReturnModel(conn, h);
			//}
		}catch(Exception e){
		  throw e;
		}
		return h;
	}
	
	public static ConfirmReturnWacoal cancelReturn(Connection conn,ConfirmReturnWacoal h) throws Exception{
		try{
			/** Update Return to Cancel **/
			updateCancelReturnModel(conn, h);

		}catch(Exception e){
		  throw e;
		}
		return h;
	}
	
	// 	RQYYMMXXX  ( เช่น RQ5703001 )  			
	 private static String genReturnNo(Date date) throws Exception{
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
			   int seq = SequenceProcess.getNextValue(conn,"RETURN_WACOAL","RETURN_NO",date);
			   
			   docNo = "RN"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn != null){
				   conn.close();conn=null;
			   }
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
	 
	 public static void updateStatusReturnModel(Connection conn,ConfirmReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_PICK_RETURN SET  \n");
				sql.append(" STATUS = ? ,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE REQUEST_NO = ?  \n" );
	
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
	 
	 public static void updateCancelReturnModel(Connection conn,ConfirmReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_PICK_RETURN SET  \n");
				sql.append(" STATUS = ? ,update_user =?,update_date =? ,return_no ='' ,cn_no ='',return_date = null,return_remark =''   \n");
				
				sql.append(" WHERE REQUEST_NO = ?  \n" );
	
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
	 
	public static void updateConfirmReturnModel(Connection conn,ConfirmReturnWacoal o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_PICK_RETURN  \n");
				sql.append(" SET STATUS = ? ,update_user =?,update_date =? ,CN_NO =? ,RETURN_NO =? ,RETURN_DATE =? ,RETURN_REMARK = ?  \n");
				
				sql.append(" WHERE REQUEST_NO = ?  \n" );
	
				logger.debug("sql:"+sql.toString());
				Date returnDate = Utils.parse( o.getReturnDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					
				ps = conn.prepareStatement(sql.toString());

				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCnNo());
				ps.setString(c++, o.getReturnNo());
				ps.setTimestamp(c++, new java.sql.Timestamp(returnDate.getTime()));
				ps.setString(c++, o.getRemark());
				
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
			sql.append("\n FROM PENSBME_PICK_RETURN_I i");
			sql.append("\n where 1=1 ");
			if( !Utils.isNull(c.getRequestNo()).equals("")){
				sql.append("\n and i.request_no = '"+Utils.isNull(c.getRequestNo())+"'");
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
