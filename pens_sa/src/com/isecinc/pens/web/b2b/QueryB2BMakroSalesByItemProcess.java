package com.isecinc.pens.web.b2b;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelGenHelper;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class QueryB2BMakroSalesByItemProcess  {
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	public static int pageSize = 100;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		B2BForm aForm = (B2BForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			logger.debug("action:"+action);
			if("new".equals(action)){
				request.setAttribute("b2bForm_RESULT",null);
				
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				B2BBean bean = new B2BBean();
				aForm.setBean(bean);
				
				//prepare Session List
				prepareSearchData(request, conn, user);
				
			}else if("back".equals(action)){
				//clear session 
				request.setAttribute("b2bForm_RESULT",null);
				aForm.setResultsSearch(null);
				//prepare bean
				B2BBean bean = new B2BBean();
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}

	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		boolean excel = false,foundData=false;
		try {
			//save criteria case Back from detail
			aForm.setBeanCriteria(aForm.getBean());
			
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//clear session from result
			request.setAttribute("b2bForm_RESULT",null);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(searchReportTotalRec(conn,aForm.getBean(),user));
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    B2BBean stockResult = searchReport(conn,user,aForm,excel,allRec,currPage,pageSize);
				StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
				if(resultHtmlTable != null){
					 request.setAttribute("b2bForm_RESULT",resultHtmlTable);
					 foundData = true;
				}
				if(foundData==false){
					 request.setAttribute("Message", "ไม่พบข้อมูล");
					 request.setAttribute("b2bForm_RESULT",null);
				}
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
                B2BBean stockResult = searchReport(conn,user,aForm,excel,allRec,currPage,pageSize);
				StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
				if(resultHtmlTable != null){
					request.setAttribute("b2bForm_RESULT",resultHtmlTable);
					foundData = true;
				}
				if(foundData==false){
					 request.setAttribute("Message", "ไม่พบข้อมูล");
					 request.setAttribute("b2bForm_RESULT",null);
					// aForm.getBean().setItemsList(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("exportToExcel : ");
		B2BForm aForm = (B2BForm) form;
		boolean excel = true;
		Connection conn = null;
		User user = (User)request.getSession().getAttribute("user");
		String rootPathTemp = "";
		String fileName = "";
		EnvProperties env = EnvProperties.getInstance();
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			XSSFWorkbook workbook = searchReportToExcel(conn,user,aForm);
			logger.debug("workbook:"+workbook);
			
		    if(workbook != null){
		    	// gen file to temp server dev_temp
				rootPathTemp = FileUtil.getRootPathTemp(env);
				fileName = "B2BMakroSalesByItem_"+user.getUserName()+".xls";
				String pathFile = rootPathTemp+fileName;
				
				logger.debug("Write to :"+pathFile);
				FileOutputStream fileOut = new FileOutputStream(pathFile);
			    workbook.write(fileOut);
			    fileOut.close();
			    
			    //set for load in web onload
			    request.setAttribute("LOAD_EXCEL", fileName);
			    return mapping.findForward("search");
		   }else{
			  request.setAttribute("Message","ไม่พบข้อมูล");
			  return mapping.findForward("search");
		   }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception ee){}
		}
		return null;
	}
	public ActionForward loadExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("loadExcel");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		String fileName = ""; 
		String pathFile = "";
		EnvProperties env = EnvProperties.getInstance();
		try {
			 logger.debug("loadExcel :pageName["+pageName+"]");
			 pathFile = FileUtil.getRootPathTemp(env);
	    	 fileName = Utils.isNull(request.getParameter("fileName"));
	    	 
	    	 logger.debug(" :fileName["+fileName+"]");
	    	 if( !Utils.isNull(fileName).equals("")){
	    		pathFile +=fileName; 
		    	logger.debug("pathFile:"+pathFile);
		    	  
	    		//read file from temp file
				 byte[] bytes = FileUtil.readFileToByte(new FileInputStream(pathFile));
				 
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				response.setContentType("application/excel");
				
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.write(bytes, 0, bytes.length);
				servletOutputStream.flush();
				servletOutputStream.close();
	    	 }
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public static XSSFWorkbook searchReportToExcel(Connection conn,User user,B2BForm aForm) throws Exception{
		XSSFWorkbook workbook = null;
		Sheet sheet = null;
	    CellStyle headerCellStyle = null;
	    Font headerFont = null;
		String[] columns = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer();
		ExcelGenHelper excelGenH = null;
		try{
	        //sql
	        sql.append("\n 	select H.* from PENSBI.XXPENS_BI_B2B_SALES_ITEM_TEMP H");
			sql.append("\n  where 1=1");
			//Gen Where Condition
			sql.append(genWhereCondSql(conn, aForm.getBean(),user));
			sql.append("\n   ORDER BY H.supplier_number,H.location_number asc");
			
		
	        /** get Data from view **/
	        ps = conn.prepareStatement(sql.toString());
	        rs = ps.executeQuery();
	        rsmd = ps.getMetaData();
	        
	       //init column head
	        columns = new String[rsmd.getColumnCount()];
	        for(int i=1;i<=rsmd.getColumnCount();i++){
	        	columns[i-1] = rsmd.getColumnName(i);
	        }
	        int i=0;
	        int rowNum = 1;
	        int cellNo =0;
	        Cell cell = null;
	        while(rs.next()) {
	        	if(rowNum==1){
	        		//Create Head Excel
	        		workbook = new XSSFWorkbook();
	    			sheet = workbook.createSheet("DATA");
	    			
	    			/** init ExcelGenHelper **/
	    			excelGenH = new ExcelGenHelper(workbook);
	    			
	    			// Create a Font for styling header cells
	    	        headerFont = workbook.createFont();
	    	        headerFont.setFontHeightInPoints((short) 14);
	    	        
	    	       // Create a CellStyle with the font
	    	        headerCellStyle = workbook.createCellStyle();
	    	        headerCellStyle.setFont(headerFont);
	    	        
	    	        // Create a Row
	    	        Row headerRow = sheet.createRow(0);
	    	        // Create cells
	    	        for(i = 0; i < columns.length; i++) {
	    	            cell = headerRow.createCell(i);
	    	            cell.setCellValue(columns[i]);
	    	            cell.setCellStyle(headerCellStyle);
	    	        }
	    	      
	        	}//if
	        	//Create cell by row
	            Row row = sheet.createRow(rowNum++);
	            for(i=0;i<columns.length;i++){
	            	//logger.debug("cellNum:"+cellNum);
	            	if(columns[i].startsWith("QTY")){
			            cell = excelGenH.genCell(row, cellNo, "NUMBER", rs.getDouble(columns[i]));
	            	}else if("EOQ_QTY".equalsIgnoreCase(columns[i]) || "ODER_IN_TRANSIT_QTY".equalsIgnoreCase(columns[i]) 
	            			|| "AVG_NET_SALES_QTY".equalsIgnoreCase(columns[i]) || "NET_SALES_QTY_YTD".equalsIgnoreCase(columns[i]) 
	            			|| "STOCK_COVER_DAYS".equalsIgnoreCase(columns[i]) || "NET_SALES_QTY_LY_YTD".equalsIgnoreCase(columns[i]) 
	            			|| "NET_SALES_QTY_YTM".equalsIgnoreCase(columns[i]) 
	            			){
	            		cell = excelGenH.genCell(row, cellNo, "CURRENCY", rs.getDouble(columns[i]));
	            	}else{
		                cell = excelGenH.genCell(row, cellNo, "TEXT", rs.getString(columns[i]));
	            	}
	            	cellNo++;
	            }//for
	            cellNo =0;
	        }//while
	        
			// Resize all columns to fit the content size
	        for(i = 0; i < columns.length; i++) {
	            sheet.autoSizeColumn(i);
	        }
			return workbook;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			rs.close();
			ps.close();
			rsmd =null;
		}
	}
	public B2BBean searchReport(Connection conn,User user,B2BForm vanForm,boolean excel,boolean allRec,int currPage,int pageSize) {
		PreparedStatement ps = null;
		ResultSet rst = null;
		ResultSetMetaData rsmd = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer html = null;
		int r = 0;
		B2BBean o = vanForm.getBean();
		String[] columns = null;
		try{
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n 	select H.* from PENSBI.XXPENS_BI_B2B_SALES_ITEM_TEMP H");
			sql.append("\n  where 1=1");
			//Gen Where Condition
			sql.append(genWhereCondSql(conn, o,user));
			
			sql.append("\n   ORDER BY H.supplier_number,H.location_number asc");
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
			rsmd = ps.getMetaData();
			rst = ps.executeQuery();
			
			//init Column Header
		    columns = new String[rsmd.getColumnCount()];
	        for(int i=1;i<=rsmd.getColumnCount();i++){
	        	columns[i-1] = rsmd.getColumnName(i);
	        }
		        
			while (rst.next()) {
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(conn,vanForm,o,excel,columns).toString());
				 html.append("<tbody>");
			  }
			  //get row table
			  html.append(genRowTable(o,excel,columns,rst).toString());
			  
			}//while
			logger.debug("TotalRec:"+r);
			
			//Check Execute Found data
			if(r>0){
			  // gen end Table
			  html.append("  </tbody>");
			  html.append(" </table>");
			  html.append("</div>");
			}
			o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				ps.close();
				rsmd = null;
			} catch (Exception e) {}
		}
	  return o;
	}
	
	public int searchReportTotalRec(Connection conn,B2BBean o,User user) {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try{
			sql.append("\n 	select count(*) as c from PENSBI.XXPENS_BI_B2B_SALES_ITEM_TEMP H ");
			sql.append("\n  where 1=1");
			//Gen Where Condition
			sql.append(genWhereCondSql(conn, o,user));
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
			 totalRec = rst.getInt("c");
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return totalRec;
	}
	
	public static StringBuffer genWhereCondSql(Connection conn,B2BBean o,User user) throws Exception{
		StringBuffer sql = new StringBuffer("");
		//sql.append("\n and S.store_type ='MAKRO'");
		if( !Utils.isNull(o.getRegion()).equals("")){
			if(Utils.isNull(o.getRegion()).equals("NA")){
			  sql.append("\n and (H.region is null or H.region ='')");
			}else{
			  sql.append("\n and H.region ='"+Utils.isNull(o.getRegion())+"'");
			}
		}
		
		return sql;
	}
	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private  StringBuffer genHeadTable(Connection conn,B2BForm vanForm,B2BBean head,boolean excel, String[] columns) throws Exception{
		StringBuffer h = new StringBuffer("");
		if(excel){
			int colspan=columns.length;
			h.append(ExcelHeader.EXCEL_HEADER);
			
		/*	h.append("<table id='tblProduct' align='center' border='1'> \n");
			h.append("<tr> \n");
			h.append("<td colspan="+colspan+"><b> รายการบิลยกเลิก ที่ Sales appl.(แบบสรุป)</b> </td>\n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td colspan="+colspan+">ภาค :"+head.getRegion()+" </td>\n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append(" <td colspan="+colspan+"> \n");
			h.append(" วันที่พิมพ์ : &nbsp;"+DateUtil.getCurrentDateTime(DateUtil.DD_MM_YYYY_HH_MM_SS_WITH_SLASH)+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");*/
		}else{
			//Gen Paging
		   int totalPage = vanForm.getTotalPage();
		   int totalRecord = vanForm.getTotalRecord();
		   int currPage =  vanForm.getCurrPage();
		   int startRec = vanForm.getStartRec();
		   int endRec = vanForm.getEndRec();
		   int no = startRec;
			
		   h.append("<div align='left'> \n");
		   h.append("<span class='pagebanner'>รายการทั้งหมด  "+totalRecord+" รายการ, แสดงรายการที่  "+startRec+" ถึง  "+endRec+".</span>\n");
		   h.append("<span class='pagelinks'>\n");
		   h.append("หน้าที่ \n");
		   for(int r=0;r<totalPage;r++){
			 if(currPage ==(r+1)){
			    h.append("<strong>"+(r+1) +"</strong>\n");
			 }else{ 
				h.append("<a href='javascript:gotoPage("+(r+1)+")'" );
				h.append(" title='Go to page "+(r+1)+"'> "+(r+1)+"</a>\n");
		     }
		  }//for 
		   
		  h.append("</span>\n");
		  h.append("</div>\n");
		}
		String width="100%";
		h.append("<div class='sticky-table sticky-ltr-cells'>");
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='table table-striped'> \n");
		h.append("<thead>");
		h.append("<tr> \n");
		for(int i=0;i<columns.length;i++){
		  if( !"r__".equalsIgnoreCase(columns[i])){
		     h.append(" <th >"+columns[i]+"</th> \n");
		  }
		}
		h.append("</tr> \n");
		h.append("</thead>");
		return h;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @param ROWVALUE_MAP
	 * @param ROWDESC_MAP
	 * @param o
	 * @return
	 * @throws Exception
	 */
	private  StringBuffer genRowTable(B2BBean head,boolean excel,String columns[],ResultSet rs) throws Exception{
		StringBuffer h = new StringBuffer("");
		String classText = "td_text";
		String classTextCenter = "td_text_center";
		String classCurrency = "td_number ";
		h.append("<tr class='lineE'> \n");
		if(excel){
			classText = "text";
			classTextCenter = "text";
			classCurrency = "currency";
		}
		
		for(int i=0;i<columns.length;i++){
		   if( !"r__".equalsIgnoreCase(columns[i])){
		       h.append("<td class='"+classText+"' width='5%'>"+Utils.isNull(rs.getString(columns[i]))+"</td> \n");
		   }
		}//for
		h.append("</tr> \n");
		return h;
	}
	
	public  void prepareSearchData(HttpServletRequest request,Connection conn,User user){
		try{
			//init regionList
			request.getSession().setAttribute("REGION_LIST", initRegionList(conn));
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static List<PopupBean> initRegionList(Connection conn){
		List<PopupBean> regionList = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		PopupBean item = new PopupBean();
		try{
			//blank
			item = new PopupBean();
			item.setKeyName("");
			item.setValue("");
			regionList.add(item);
			
			item = new PopupBean();
			item.setKeyName("ไม่พบภาค");
			item.setValue("NA");
			regionList.add(item);
			
			sql.append("\n  SELECT distinct region from PENSBI.XXPENS_BI_B2B_MST_STORE M ");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				item = new PopupBean();
				item.setKeyName(Utils.isNull(rst.getString("region")));
				item.setValue(Utils.isNull(rst.getString("region")));
				regionList.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return regionList;
	}
}
