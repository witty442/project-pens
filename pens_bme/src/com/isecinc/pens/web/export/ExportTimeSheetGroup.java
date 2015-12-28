package com.isecinc.pens.web.export;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.isecinc.pens.bean.MCBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class ExportTimeSheetGroup extends Excel{
	
	private static Logger logger = Logger.getLogger("PENS");
	
	public static ExcelResultBean genExportToExcel(User user,MCBean o) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ExcelResultBean excelResutlBean = new ExcelResultBean();
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
		int sheet = 0;
		MCBean h = null;
		boolean found = false;
		int totalHH = 0;
		int totalMM = 0;
		try{
			conn = DBConnection.getInstance().getConnection();
		
			String christYear = (Integer.parseInt(o.getStaffYear())-543)+"";
			 
		   sql.append(" \n SELECT S.emp_ref_id ,s.employee_id,S.emp_type,S.title,S.name,S.surname,S.mobile1,S.mobile2 ");
		   sql.append(" \n,S.status,S.reason_leave, S.note,S.region  " );
		   sql.append("\n ,(select pens_desc from MC_MST_REFERENCE M where M.reference_code='MCarea' and M.pens_value=S.region ) as region_desc");
		   sql.append("\n ,(select pens_desc from MC_MST_REFERENCE M where M.reference_code='EmpStatus' and M.pens_value=S.status ) as status_desc");
		   sql.append("\n ,(select pens_desc from MC_MST_REFERENCE M where M.reference_code='LeaveReason' and M.pens_value=S.reason_leave ) as reason_leave_desc");
		   sql.append("\n ,(SELECT M.pens_desc from MC_MST_REFERENCE M where 1=1 and reference_code ='StaffType' and M.pens_value =S.emp_type ) emp_type_desc");
		 
		   sql.append(" \n from MC_EMPLOYEE S " );
		   sql.append(" \n,(select distinct emp_ref_id from MC_STAFF_TIME T");
		   sql.append("\n    where 1=1 " );
		   sql.append("\n    and EXTRACT(year FROM staff_date) = '"+christYear+"'");
		   sql.append("\n    and EXTRACT(month FROM staff_date) = '"+Utils.isNull(o.getStaffMonth())+"'");
		   sql.append(" \n ) T  ");
		   sql.append("\n  WHERE T.emp_ref_id =S.emp_ref_id " );
		   
		   if( !Utils.isNull(o.getMcArea()).equals("")){
			    sql.append("\n and s.region = '"+Utils.isNull(o.getMcArea())+"'");
			}
		   if( !Utils.isNull(o.getEmpId()).equals("") && !Utils.isNull(o.getEmpId()).equalsIgnoreCase("ALL")){
				sql.append("\n and S.employee_id = "+Utils.isNull(o.getEmpId())+"");
		   }
		   if( !Utils.isNull(o.getEmpType()).equals("")){
				sql.append("\n and S.emp_type = '"+Utils.isNull(o.getEmpType())+"'");
		   }
		   if( !Utils.isNull(o.getName()).equals("")){
				sql.append("\n and S.name like '%"+Utils.isNull(o.getName())+"%'");
		   }

		   sql.append("\n order by S.EMP_REF_ID DESC ");
			
		   logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			
			while (rst.next()) {
				h = new MCBean();
				h.setEmpRefId(rst.getString("EMP_REF_ID"));
				if(rst.getInt("employee_id") != 0){
				   h.setEmpId(Utils.isNull(rst.getString("employee_id")));
				}else{
				   h.setEmpId(""); 
				}
			   h.setEmpType(Utils.isNull(rst.getString("emp_type")));
			   h.setEmpTypeDesc(Utils.isNull(rst.getString("emp_type_desc")));
			   h.setTitle(Utils.isNull(rst.getString("title")));
			   h.setName(Utils.isNull(rst.getString("name")));
			   h.setSurName(Utils.isNull(rst.getString("surname")));
			   h.setMobile1(Utils.isNull(rst.getString("mobile1")));
			   h.setMobile2(Utils.isNull(rst.getString("mobile2")));
			   h.setStatus(Utils.isNull(rst.getString("status")));
			   h.setReasonLeave(Utils.isNull(rst.getString("reason_leave")));
			   h.setNote(Utils.isNull(rst.getString("note")));
			   h.setRegion(Utils.isNull(rst.getString("region")));
			   h.setRegionDesc(Utils.isNull(rst.getString("region_desc")));
			   
			   h.setStaffMonth(o.getStaffMonth());
			   h.setStaffYear(o.getStaffYear());
			   
			   logger.debug("Gen Excel EMP_REF_ID:"+rst.getString("EMP_REF_ID"));
			   h = genExportToExcelModel(conn,user,xssfWorkbook,h);
			   
			   totalMM = h.getTotalMM();
			   totalHH = h.getTotalHH();
			   
			  //TotalTimeALL Calc Second to Hourse :minute
			  long minutes = (totalMM % 60);
			  String minutesStr = minutes < 10 ? "0" + String.valueOf(minutes) : String.valueOf(minutes);
	            
			  long hours = totalHH +(totalMM/60);
			  String hoursStr = hours < 10 ? "0" + String.valueOf(hours) : String.valueOf(hours);
			   
			  
			  String totalTimeAll = hoursStr+":"+minutesStr;
			  ExcelStyle style = new ExcelStyle(xssfWorkbook);
			  genSummary(xssfWorkbook.getSheet("EMPID_"+h.getEmpId()), xssfWorkbook, style,totalTimeAll,xssfWorkbook.getSheet("EMPID_"+h.getEmpId()).getLastRowNum()+1);
			
			  sheet++;
			  found = true;
			}
			
			 //adjust width of the column
			  for(int s=0;s<sheet;s++){
			       XSSFSheet sheet2 = xssfWorkbook.getSheetAt(s);
			       sheet2.setColumnWidth(0, 3000); 
			       sheet2.setColumnWidth(1, 3300);
			       sheet2.setColumnWidth(2, 3300);
			       sheet2.setColumnWidth(3, 5300);
			       sheet2.setColumnWidth(4, 4300);
			       sheet2.setColumnWidth(5, 6300);
			  }  
			  
			  excelResutlBean.setXssfWorkbook(xssfWorkbook);
			  excelResutlBean.setFound(found);
			  
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();rst = null;
                ps.close();ps = null;
				conn.close();conn = null;
			} catch (Exception e) {}
		}
		return excelResutlBean;
	}
	
	public static MCBean genExportToExcelModel(Connection conn,User user,XSSFWorkbook xssfWorkbook ,MCBean h) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int row = 0;
		int totalHH = 0;
		int totalMM = 0;
		try {
		   // Create Sheet.
           XSSFSheet sheet = xssfWorkbook.createSheet("EMPID_"+h.getEmpId());

           ExcelStyle style = new ExcelStyle(xssfWorkbook);
        
           //Gen Header
           row = ExportTimeSheetGroup.genHeader(sheet, xssfWorkbook, style,h);
           
           /*******************DATA ********************************/
           String christYear = (Integer.parseInt(h.getStaffYear())-543)+"";
           
           sql.append(" \n select S.* " );
           sql.append("\n ,(select pens_desc from MC_MST_REFERENCE M where M.reference_code='DayoffReason' and M.pens_value=S.reason_leave ) as dayoffReason_desc");
		   sql.append(" \n from MC_STAFF_TIME  S" );
		   sql.append(" \n WHERE 1=1  ");
		   sql.append(" \n and EXTRACT(year FROM staff_date) = '"+christYear+"'");
		   sql.append(" \n and EXTRACT(month FROM staff_date) = '"+Utils.isNull(h.getStaffMonth())+"'");
           sql.append(" \n and emp_ref_id = "+Utils.isNull(h.getEmpRefId())+"");
		   sql.append("\n order by EMP_REF_ID asc ");
			
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
		
			rst = ps.executeQuery();
	       
			while (rst.next()) {
			   // Create First Data Row
			   row++;
			   logger.debug("Create Row:"+row);
	           XSSFRow dataRow = sheet.createRow(row); 

	           XSSFCell cell1 = dataRow.createCell(0);
	           cell1.setCellStyle(style.dataLineStyle);
	           cell1.setCellValue(Utils.stringValue(rst.getDate("staff_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
	           
	           XSSFCell cell2 = dataRow.createCell(1);
	           cell2.setCellStyle(style.dataLineStyle);
	           cell2.setCellValue(Utils.isNull(rst.getString("START_TIME")));
	           
	           XSSFCell cell3 = dataRow.createCell(2);
	           cell3.setCellStyle(style.dataLineStyle);
	           cell3.setCellValue(Utils.isNull(rst.getString("END_TIME")));
	           
	           XSSFCell cell4 = dataRow.createCell(3);
	           cell4.setCellStyle(style.dataLineStyle);
	           cell4.setCellValue(Utils.isNull(rst.getString("TOTAL_TIME")));
	           
	           XSSFCell cell5 = dataRow.createCell(4);
	           cell5.setCellStyle(style.dataLineStyle);
	           cell5.setCellValue(Utils.isNull(rst.getString("dayoffReason_desc")));
	           
	           XSSFCell cell6 = dataRow.createCell(5);
	           cell6.setCellStyle(style.dataLineStyle);
	           cell6.setCellValue(Utils.isNull(rst.getString("NOTE")));
	           
	           if( !Utils.isNull(rst.getString("TOTAL_TIME")).equals("")){
	             String[] totalTime = Utils.isNull(rst.getString("TOTAL_TIME")).split("\\:");
	             totalHH += Integer.parseInt(totalTime[0]);
	             totalMM += Integer.parseInt(totalTime[1]);
	           }

			}
			h.setTotalHH(totalHH);
			h.setTotalMM(totalMM);
		   
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();rst = null;
                ps.close();ps = null;
				
			} catch (Exception e) {}
		}
		return h;
	}
	
	public static int genHeader(XSSFSheet sheet ,XSSFWorkbook xssfWorkbook,ExcelStyle style,MCBean b){
		XSSFRow headerRow  = null;
		XSSFCell headerCell1 = null;
		XSSFCell headerCell2 = null;
		XSSFCell headerCell3 = null;
		XSSFCell headerCell4 = null;
		XSSFCell headerCell5 = null;
		XSSFCell headerCell6 = null;
		int row = 0;
		try{
			
		   /****  Create Header Row 0 ***/
           headerRow = sheet.createRow(row);
           headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
           //Header Row 0
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(style.headerStyleLeft2);
           headerCell1.setCellValue("รายงานบันทึกการลงเวลาเข้า-ออก งาน");

           sheet.addMergedRegion(new CellRangeAddress(row,row,0,5));//Merge Cell 0-5
	           
		   /****  Create Header Row 0 ***/
           row = row +1;
           headerRow = sheet.createRow(row);
           headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
           //Header Row 0
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(style.headerStyleLeft2);
           headerCell1.setCellValue("รหัสพนักงาน :"+b.getEmpId() +"   ชื่อ-สกุล :"+b.getName()+" "+b.getSurName());

           sheet.addMergedRegion(new CellRangeAddress(row,row,0,5));//Merge Cell 0-5
           
           /*** Create Header Row 1 ***/
           row = row +1;
           headerRow = sheet.createRow(row);
           headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
           //Header Row 0
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(style.headerStyleLeft2);
           headerCell1.setCellValue("ประจำปี/เดือน :"+b.getStaffYear()+"/"+b.getStaffMonth());

           sheet.addMergedRegion(new CellRangeAddress(row,row,0,5));//Merge Cell 0-5
           
           /** Create Header Row 2 ***/
           row = row +1;
           headerRow = sheet.createRow(row);
           headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
           //Header Row 0
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(style.headerStyleLeft2);
           headerCell1.setCellValue("ประเภท :"+b.getEmpTypeDesc() +"  ภาค:"+b.getRegionDesc());

           sheet.addMergedRegion(new CellRangeAddress(row,row,0,5));//Merge Cell 0-5

           /** // Create Header Row 6  ***/
           row = row+2;
           headerRow = sheet.createRow(row);
           
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(style.dataLineBoldStyle);
           headerCell1.setCellValue("วันที่");
           
           headerCell2 = headerRow.createCell(1);
           headerCell2.setCellStyle(style.dataLineBoldStyle);
           headerCell2.setCellValue("เวลาเข้า");
           
           headerCell3 = headerRow.createCell(2);
           headerCell3.setCellStyle(style.dataLineBoldStyle);
           headerCell3.setCellValue("เวลาออก");
           
           headerCell4 = headerRow.createCell(3);
           headerCell4.setCellStyle(style.dataLineBoldStyle);
           headerCell4.setCellValue("รวมเวลา (ชั่วโมง:นาที)");
           
           headerCell5 = headerRow.createCell(4);
           headerCell5.setCellStyle(style.dataLineBoldStyle);
           headerCell5.setCellValue("สาเหตุการลา");
          
           headerCell6 = headerRow.createCell(5);
           headerCell6.setCellStyle(style.dataLineBoldStyle);
           headerCell6.setCellValue("สาเหตุการลา");
            
		}catch(Exception e){
			e.printStackTrace();
		}
		return row;
	}
	
	public static int genSummary(XSSFSheet sheet ,XSSFWorkbook xssfWorkbook,ExcelStyle style,String totalTimeAll,int row){
		XSSFRow headerRow  = null;
		XSSFCell headerCell1 = null;
		XSSFCell headerCell2 = null;
		XSSFCell headerCell3 = null;
		XSSFCell headerCell4 = null;
		XSSFCell headerCell5 = null;
		XSSFCell headerCell6 = null;
		try{

           headerRow = sheet.createRow(row);
           
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(style.dataLineBoldStyle);
           headerCell1.setCellValue("");
           
           headerCell2 = headerRow.createCell(1);
           headerCell2.setCellStyle(style.dataLineBoldStyle);
           headerCell2.setCellValue("");
           
           headerCell3 = headerRow.createCell(2);
           headerCell3.setCellStyle(style.dataLineBoldStyle);
           headerCell3.setCellValue("รวมเวลา");
           
           headerCell4 = headerRow.createCell(3);
           headerCell4.setCellStyle(style.dataLineBoldStyle);
           headerCell4.setCellValue(totalTimeAll);
           
           headerCell5 = headerRow.createCell(4);
           headerCell5.setCellStyle(style.dataLineBoldStyle);
           headerCell5.setCellValue("");
          
           headerCell6 = headerRow.createCell(5);
           headerCell6.setCellStyle(style.dataLineBoldStyle);
           headerCell6.setCellValue("");
            
		}catch(Exception e){
			e.printStackTrace();
		}
		return row;
	}
	
}
