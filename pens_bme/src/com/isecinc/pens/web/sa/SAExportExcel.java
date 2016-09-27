package com.isecinc.pens.web.sa;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.ExcelHeader;

import com.isecinc.pens.bean.SAReportBean;
import com.isecinc.pens.bean.SATranBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SAReportDAO;
import com.isecinc.pens.dao.SATranDAO;
import com.isecinc.pens.inf.helper.Utils;

public class SAExportExcel {
	
	private static Logger logger = Logger.getLogger("PENS");
	
	
	public static StringBuffer genSARewardTranReport(SATranBean bean,User user){
		StringBuffer h = new StringBuffer("");
		String colSpan= "9";
		double totalBmeAmt = 0;
		double totalWacoalAmt = 0;
		try{
			//include style header
			h.append(ExcelHeader.EXCEL_HEADER);
		
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"' ><b>รายงานบันทึกค่าเฝ้าตู้</b></td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
		
			SATranBean result =  SATranDAO.searchReport(bean);
		
		    if(result != null && result.getItems().size() >0){
		        List<SATranBean> list = result.getItems();
		        
			    h.append("<table border='1'> \n");
				h.append("<tr  class='colum_head'> \n");
				  h.append("<td>Emp ID</td> \n");
				  h.append("<td>Name</td> \n");
				  h.append("<td>Surname</td> \n");
				  h.append("<td>Group Store</td> \n");
				  h.append("<td>Branch</td> \n");
				  h.append("<td>Year Month</td> \n");
				  h.append("<td>Pay Date</td> \n");
				  h.append("<td>Bme Amt</td> \n");
				  h.append("<td>Wacoal Amt</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					SATranBean s = (SATranBean)list.get(i);
					totalBmeAmt += Utils.convertStrToDouble(s.getBmeAmt());
					totalWacoalAmt += Utils.convertStrToDouble(s.getWacoalAmt());
					
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getEmpId()+"</td> \n");
					  h.append("<td class='text'>"+s.getName()+"</td> \n");
					  h.append("<td class='text'>"+s.getSurname()+"</td> \n");
					  h.append("<td class='num'>"+s.getGroupStore()+"</td> \n");
					  h.append("<td class='text'>"+s.getBranch()+"</td> \n");
					  h.append("<td class='text'>"+s.getYearMonth()+"</td> \n");
					  h.append("<td class='text'>"+s.getPayDate()+"</td> \n");
					  h.append("<td class='num'>"+s.getBmeAmt()+"</td> \n");
					  h.append("<td class='num'>"+s.getWacoalAmt()+"</td> \n");
					h.append("</tr>");
				}
				h.append("<tr> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'>ยอดรวม</td> \n");
				  h.append("<td class='num'>"+Utils.decimalFormat(totalBmeAmt,Utils.format_current_2_disgit)+"</td> \n");
				  h.append("<td class='num'>"+Utils.decimalFormat(totalWacoalAmt,Utils.format_current_2_disgit)+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
		    }else{
		    	return new StringBuffer("");
		    }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public static StringBuffer genSAStatementReport(SAReportBean bean,User user){
		StringBuffer h = new StringBuffer("");
		String colSpan= "8";
		try{
			Date date = Utils.parse(bean.getMonth(), Utils.DD_MM_YYYY_WITHOUT_SLASH);
			
			String MMMMYYYY= Utils.stringValue(date, "MMMM yyyy",Utils.local_th);
		
			//include style header
			h.append(ExcelHeader.EXCEL_HEADER);
		
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"' ><b>Statemet ค่าความเสียหาย </b></td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"'  ><b> ณ เดือน   :&nbsp;&nbsp;"+MMMMYYYY+"</b></td> \n");
			h.append("</tr> \n");

			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' ></td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' >Employee ID&nbsp;&nbsp;:&nbsp;&nbsp;"+bean.getEmpId()+"&nbsp;&nbsp;"+bean.getFullName()+" &nbsp;&nbsp;Group Store&nbsp;&nbsp;:&nbsp;&nbsp;"+bean.getGroupStore()+"&nbsp;&nbsp;Branch&nbsp;&nbsp;:&nbsp;&nbsp;"+bean.getBranch()+"</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' ></td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
		
		    List<SAReportBean> list = SAReportDAO.searchReport( bean);
		    
		    if(list != null && list.size() >0){
			    h.append("<table border='1'> \n");
				h.append("<tr  class='colum_head'> \n");
				  h.append("<td>Type</td> \n");
				  h.append("<td>Invoice no / Ref Wacoal</td> \n");
				  h.append("<td>วันที่บันทึก</td> \n");
				  h.append("<td>ค่าความเสียหาย</td> \n");
				  h.append("<td>Line No</td> \n");
				  h.append("<td>ประเภทชำระ</td> \n");
				  h.append("<td>วันที่ชำระ</td> \n");
				  h.append("<td>ยอดชำระ </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					SAReportBean s = (SAReportBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getType()+"</td> \n");
					  h.append("<td class='text'>"+s.getInvRefwal()+"</td> \n");
					  h.append("<td class='text'>"+s.getTranDate()+"</td> \n");
					  h.append("<td class='num'>"+s.getTotalDamage()+"</td> \n");
					  h.append("<td class='text'>"+s.getLineId()+"</td> \n");
					  h.append("<td class='text'>"+s.getPayType()+"</td> \n");
					  h.append("<td class='text'>"+s.getPayDate()+"</td> \n");
					  h.append("<td class='num'>"+s.getPayAmt()+"</td> \n");
					h.append("</tr>");
				}
				h.append("</table> \n");
		    }else{
		    	return new StringBuffer("");
		    }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public static StringBuffer genSAStatementAllReport(SAReportBean bean,User user){
		StringBuffer h = new StringBuffer("");
		String colSpan= "12";
		double totalAmt = 0;
		try{
			Date date = Utils.parse(bean.getMonth(), Utils.DD_MM_YYYY_WITHOUT_SLASH);
			
			String MMMMYYYY= Utils.stringValue(date, "MMMM yyyy",Utils.local_th);
		
			//include style header
			h.append(ExcelHeader.EXCEL_HEADER);
		
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"' ><b>Statemet ค่าความเสียหาย </b></td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"'  ><b> ณ เดือน&nbsp;&nbsp;:&nbsp;&nbsp;"+MMMMYYYY+"</b></td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' ></td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

		    List<SAReportBean> list = SAReportDAO.searchReportAll( bean);
		    if(list != null && list.size() >0){
			    h.append("<table border='1'> \n");
				h.append("<tr class='colum_head'> \n");
				  h.append("<td>Emp ID</td> \n");
				  h.append("<td>Name-Surname</td> \n");
				  h.append("<td>Group Store</td> \n");
				  h.append("<td>Branch</td> \n");
				  h.append("<td>Type</td> \n");
				  h.append("<td>Invoice no / Ref Wacoal</td> \n");
				  h.append("<td>วันที่บันทึก</td> \n");
				  h.append("<td>ค่าความเสียหาย</td> \n");
				  h.append("<td>Line No</td> \n");
				  h.append("<td>ประเภทชำระ</td> \n");
				  h.append("<td>วันที่ชำระ</td> \n");
				  h.append("<td>ยอดชำระ </td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					SAReportBean s = (SAReportBean)list.get(i);
					totalAmt +=  Utils.convertStrToDouble(s.getPayAmt());
					  h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getEmpId()+"</td> \n");
					  h.append("<td class='text'>"+s.getName()+" "+s.getSurname()+"</td> \n");
					  h.append("<td class='text'>"+s.getGroupStore()+"</td> \n");
					  h.append("<td class='text'>"+s.getBranch()+"</td> \n");
					  h.append("<td class='text'>"+s.getType()+"</td> \n");
					  h.append("<td class='text'>"+s.getInvRefwal()+"</td> \n");
					  h.append("<td class='text'>"+s.getTranDate()+"</td> \n");
					  h.append("<td class='num'>"+s.getTotalDamage()+"</td> \n");
					  h.append("<td class='text'>"+s.getLineId()+"</td> \n");
					  h.append("<td class='text'>"+s.getPayType()+"</td> \n");
					  h.append("<td class='text'>"+s.getPayDate()+"</td> \n");
					  h.append("<td class='num'>"+s.getPayAmt()+"</td> \n");
					h.append("</tr>");
				}
				 h.append("<tr> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td ></td> \n");
				  h.append("<td ></td> \n");
				  h.append("<td >รวมยอดค้างชำระ</td> \n");
				  h.append("<td class='num'>"+Utils.decimalFormat(totalAmt,Utils.format_current_2_disgit)+"</td> \n");
				h.append("</tr>");
				h.append("</table> \n");
		    }else{
		    	return new StringBuffer("");
		    }	
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	public static StringBuffer genSAOrisoftReport(SAReportBean bean,User user){
		StringBuffer h = new StringBuffer("");
		String colSpan= "10";
		double totalAmt = 0;
		try{
			Date date = Utils.parse("01"+bean.getMonth(), Utils.DD_MM_YYYY_WITHOUT_SLASH);
			
			String MMMMYYYY= Utils.stringValue(date, "MMMM yyyy",Utils.local_th);
		
			//include style header
			h.append(ExcelHeader.EXCEL_HEADER);
		
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"' ><b>รายงานสรุบยอดส่ง Orisoft </b></td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"'  ><b> ณ เดือน&nbsp;&nbsp;:&nbsp;&nbsp;"+MMMMYYYY+"</b></td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' ></td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");
			
            SAReportBean dataBean =  SAReportDAO.searchData4OrisoftReport( bean);
		    List<SAReportBean> list =dataBean.getItems();
		    
		    if(list != null && list.size() >0){
			    h.append("<table border='1'> \n");
				h.append("<tr class='colum_head'> \n");
				  h.append("<td>Emp ID</td> \n");
				  h.append("<td>Name</td> \n");
				  h.append("<td>Surname</td> \n");
				  h.append("<td>Group Store</td> \n");
				  h.append("<td>Branch</td> \n");
				  h.append("<td>as Of Month</td> \n");
				  h.append("<td>Surety Bond จากเงินเดือน</td> \n");
				  h.append("<td>ค่าเฝ้าตู้</td> \n");
				  h.append("<td>ค่าเสียหายหักจากเงินเดือน</td> \n");
				  h.append("<td>หัก Surety Bond ของบริษัท</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					SAReportBean s = (SAReportBean)list.get(i);
					totalAmt +=  Utils.convertStrToDouble(s.getPayAmt());
					  h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getEmpId()+"</td> \n");
					  h.append("<td class='text'>"+s.getName()+"</td> \n");
					  h.append("<td class='text'>"+s.getSurname()+"</td> \n");
					  h.append("<td class='text'>"+s.getGroupStore()+"</td> \n");
					  h.append("<td class='text'>"+s.getBranch()+"</td> \n");
					  h.append("<td class='text'>"+s.getAsOfMonth()+"</td> \n");
					  h.append("<td class='num'>"+s.getSuretyBondAmt()+"</td> \n");
					  h.append("<td class='num'>"+s.getRewardAmt()+"</td> \n");
					  h.append("<td class='num'>"+s.getNetDamageAmt()+"</td> \n");
					  h.append("<td class='num'>"+s.getNetSuretyBondAmt()+"</td> \n");
					 h.append("</tr>");
				}
				 /* h.append("<tr> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td></td> \n");
				  h.append("<td >รวมยอดค้างชำระ</td> \n");
				  h.append("<td class='num'>"+Utils.decimalFormat(totalAmt,Utils.format_current_2_disgit)+"</td> \n");
				h.append("</tr>");*/
				h.append("</table> \n");
		    }else{
		    	return new StringBuffer("");
		    }	
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
}
