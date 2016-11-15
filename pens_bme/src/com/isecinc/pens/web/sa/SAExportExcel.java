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
				  h.append("<th>Emp ID</th> \n");
				  h.append("<th>Name</th> \n");
				  h.append("<th>Surname</th> \n");
				  h.append("<th>Group Store</th> \n");
				  h.append("<th>Branch</th> \n");
				  h.append("<th>Year Month</th> \n");
				  h.append("<th>Pay Date</th> \n");
				  h.append("<th>Bme Amt</th> \n");
				  h.append("<th>Wacoal Amt</th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					SATranBean s = (SATranBean)list.get(i);
					totalBmeAmt += Utils.convertStrToDouble(s.getBmeAmt());
					totalWacoalAmt += Utils.convertStrToDouble(s.getWacoalAmt());
					
					h.append("<tr> \n");
					  h.append("<th class='text'>"+s.getEmpId()+"</td> \n");
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
	
	public static StringBuffer genSADeptReport(SAReportBean bean ,List<SAReportBean> list,User user){
		StringBuffer h = new StringBuffer("");
		String colSpan= "9";
		double totalInvoiceAmt = 0;
		double totalDamage = 0;
		double totalRewardBme = 0;
		double totalRewardWacoal = 0;
		try{
			if("Summary".equalsIgnoreCase(bean.getSummaryType())){
				colSpan= "10";
			}
			//include style header
			h.append(ExcelHeader.EXCEL_HEADER);
		
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"' ><b>รายงานลูกหนี้พนักงาน SA </b></td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' ></td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
		
		    if(list != null && list.size() >0){
			    h.append("<table border='1'> \n");
				h.append("<tr  class='colum_head'> \n");
				  h.append("<th>รหัสพนักงาน</th> \n");
				  h.append("<th>รหัสลูกหนี้ Oracle</th> \n");
				  h.append("<th>Name</th> \n");
				  h.append("<th>Surname</th> \n");
				  h.append("<th>Group Store</th> \n");
				  h.append("<th>Branch</th> \n");
				  if(!"Summary".equalsIgnoreCase(bean.getSummaryType())){
				    h.append("<th>Invoice No</th> \n");
				  }else{
					  h.append("<th>ยอดสะสม ค่าเฝ้าตู้ Bme</th> \n");
					  h.append("<th>ยอดสะสม ค่าเฝ้าตู้ Wacoal</th> \n");
				  }
				  h.append("<th>Invoice Amt </th> \n");
				  h.append("<th>บันทึกค่าความเสียหาย </th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					SAReportBean s = (SAReportBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getEmpId()+"</td> \n");
					  h.append("<td class='text'>"+s.getOracleRefId()+"</td> \n");
					  h.append("<td class='text'>"+s.getName()+"</td> \n");
					  h.append("<td class='text'>"+s.getSurname()+"</td> \n");
					  h.append("<td class='text'>"+s.getGroupStore()+"</td> \n");
					  h.append("<td class='text'>"+s.getBranch()+"</td> \n");
					  if(!"Summary".equalsIgnoreCase(bean.getSummaryType())){
					     h.append("<td class='text'>"+s.getInvoiceNo()+"</td> \n");
					  }else{
						  h.append("<td class='currency'>"+s.getTotalRewardBme()+"</td> \n");
						  h.append("<td class='currency'>"+s.getTotalRewardWacoal()+"</td> \n");
					  }
					  
					  h.append("<td class='currency'>"+s.getTotalInvoiceAmt()+"</td> \n");
					  h.append("<td class='currency'>"+s.getTotalDamage()+"</td> \n");
					  
					h.append("</tr>");
					
					 totalInvoiceAmt += Utils.convertStrToDouble(s.getTotalInvoiceAmt());
					 totalDamage += Utils.convertStrToDouble(s.getTotalDamage());
					 totalRewardBme += Utils.convertStrToDouble(s.getTotalRewardBme());
					 totalRewardWacoal += Utils.convertStrToDouble(s.getTotalRewardWacoal());
				}
				h.append("<tr> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  h.append("<td class='text'></td> \n");
				  if(!"Summary".equalsIgnoreCase(bean.getSummaryType())){
					 h.append("<td class='text'></td> \n");
				     h.append("<td class='colum_head'>ยอดรวม</td> \n");
				  }else{
					 h.append("<td class='colum_head'>ยอดรวม</td> \n");
					  h.append("<td class='currency_bold'>"+Utils.decimalFormat(totalRewardBme,Utils.format_current_2_disgit)+"</td> \n");
					  h.append("<td class='currency_bold'>"+Utils.decimalFormat(totalRewardWacoal,Utils.format_current_2_disgit)+"</td> \n");
				  }
				  h.append("<td class='currency_bold'>"+Utils.decimalFormat(totalInvoiceAmt,Utils.format_current_2_disgit)+"</td> \n");
				  h.append("<td class='currency_bold'>"+Utils.decimalFormat(totalDamage,Utils.format_current_2_disgit)+"</td> \n");
				  
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
		String colSpan= "9";
		try{
			Date date = Utils.parse(bean.getMonth(), Utils.DD_MM_YYYY_WITHOUT_SLASH);
			
			String MMMMYYYY= Utils.stringValue(date, "MMMM yyyy",Utils.local_th);
		
			//include style header
			h.append(ExcelHeader.EXCEL_HEADER);
		
			//Header
			h.append("<table border='1'> \n");
			
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"' ><b>Statement ค่าความเสียหาย </b></td> \n");
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
		
		    List<SAReportBean> list = SAReportDAO.searchStatementReport( bean);
		    
		    if(list != null && list.size() >0){
			    h.append("<table border='1'> \n");
				h.append("<tr  class='colum_head'> \n");
				  h.append("<th>Type</th> \n");
				  h.append("<th>Invoice no / Ref Wacoal</th> \n");
				  h.append("<th>วันที่บันทึก</th> \n");
				  h.append("<th>ค่าความเสียหาย</th> \n");
				  h.append("<th>Line No</th> \n");
				  h.append("<th>ประเภทชำระ</th> \n");
				  h.append("<th>วันที่ชำระ</th> \n");
				  h.append("<th>ยอดชำระแล้ว </th> \n");
				  h.append("<th>ยอดค้างชำระ </th> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<list.size();i++){
					SAReportBean s = (SAReportBean)list.get(i);
					h.append("<tr> \n");
					  h.append("<td class='text'>"+s.getType()+"</td> \n");
					  h.append("<td class='text'>"+s.getInvRefwal()+"</td> \n");
					  h.append("<td class='text'>"+s.getTranDate()+"</td> \n");
					  h.append("<td class='currency'>"+s.getTotalDamage()+"</td> \n");
					  h.append("<td class='text'>"+s.getLineId()+"</td> \n");
					  h.append("<td class='text'>"+s.getPayType()+"</td> \n");
					  if(Utils.isNull(s.getPayType()).equals("")){
						h.append("<td class='text'><b>"+s.getPayDate()+"</b></td> \n");
						h.append("<td class='currency'><b>"+s.getPayAmt()+"</b></td> \n");
						h.append("<td class='currency'><b>"+s.getDelayPayAmt()+"</b></td> \n");
					  }else{
					    h.append("<td class='text'>"+s.getPayDate()+"</td> \n");
					    h.append("<td class='currency'>"+s.getPayAmt()+"</td> \n");
					    h.append("<td class='currency'>"+s.getDelayPayAmt()+"</td> \n");
					  }
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
			h.append("<td align='center' colspan='"+colSpan+"' ><b>Statement ค่าความเสียหาย </b></td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='center' colspan='"+colSpan+"'  ><b> ณ เดือน&nbsp;&nbsp;:&nbsp;&nbsp;"+MMMMYYYY+"</b></td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td align='left' colspan='"+colSpan+"' ></td> \n");
			h.append("</tr> \n");
			
			h.append("</table> \n");

		    List<SAReportBean> list = SAReportDAO.searchStatementReportAll( bean);
		    if(list != null && list.size() >0){
			    h.append("<table border='1'> \n");
				h.append("<tr class='colum_head'> \n");
				  h.append("<th>Emp ID</th> \n");
				  h.append("<th>Name-Surname</th> \n");
				  h.append("<th>Group Store</th> \n");
				  h.append("<th>Branch</th> \n");
				  h.append("<th>Type</th> \n");
				  h.append("<th>Invoice no / Ref Wacoal</th> \n");
				  h.append("<th>วันที่บันทึก</th> \n");
				  h.append("<th>ค่าความเสียหาย</th> \n");
				  h.append("<th>Line No</th> \n");
				  h.append("<th>ประเภทชำระ</th> \n");
				  h.append("<th>วันที่ชำระ</th> \n");
				  h.append("<th>ยอดค้างชำระ </th> \n");
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
					  h.append("<td class='currency'>"+s.getTotalDamage()+"</td> \n");
					  h.append("<td class='text'>"+s.getLineId()+"</td> \n");
					  h.append("<td class='text'>"+s.getPayType()+"</td> \n");
					  h.append("<td class='text'>"+s.getPayDate()+"</td> \n");
					  h.append("<td class='currency'>"+s.getPayAmt()+"</td> \n");
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
				  h.append("<td ><b>รวมยอดค้างชำระ</b></td> \n");
				  h.append("<td class='currency'><b>"+Utils.decimalFormat(totalAmt,Utils.format_current_2_disgit)+"</b></td> \n");
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
				  h.append("<th>Emp ID</th> \n");
				  h.append("<th>Name</th> \n");
				  h.append("<th>Surname</th> \n");
				  h.append("<th>Group Store</th> \n");
				  h.append("<th>Branch</th> \n");
				  h.append("<th>as Of Month</th> \n");
				  h.append("<th>Surety Bond จากเงินเดือน</th> \n");
				  h.append("<th>ค่าเฝ้าตู้</th> \n");
				  h.append("<th>ค่าเสียหายหักจากเงินเดือน</th> \n");
				  h.append("<th>หัก Surety Bond ของบริษัท</th> \n");
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
