package com.isecinc.pens.web.salestarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.SalesrepZoneDAO;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class SalesTargetPDExport {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuffer genResultSearchTargetPD(SalesTargetBean o, List<SalesTargetBean> columnPDList
			,List<SalesTargetBean> rowProductList
			,Map<String, SalesTargetBean> dataMAP,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className = "";
		String keyDataMap ="";//itemCode+pdCode
		String style_text ="td_text";
		String style_text_center ="td_text_center";
		String style_number ="td_number"; 
		String style_number_bold ="td_number_bold"; 
		Map<String, Double> sumColumnPDMap = new HashMap<String, Double>();
		Map<String, Double> sumColumnPDStockMap = new HashMap<String, Double>();
		double sumColumnPD = 0;
		double sumRowProduct = 0;
		double sumRowProductStock = 0;
		try{
			if(excel){
			    h.append(ExcelHeader.EXCEL_HEADER);
			    style_text  ="text";
			    style_number = "currency";
			    style_number_bold = "currency_bold";
			
				h.append("<table id='tblProductHead' align='center' border='0' cellpadding='3' cellspacing='1'> \n");
				h.append("<tr> \n");
				h.append(" <td colspan='5'><b><font size='2'>เป้าหมาย PD</font></b></td> \n");
				h.append("</tr>");
				h.append("<tr> \n");
				h.append(" <td  colspan='5'><b>เดือน : "+o.getPeriod()+"</b></td> \n");
				h.append("</tr>");
				h.append("<tr> \n");
				h.append("  <td  colspan='5'><b>ภาคตามสายดูแล : "+ SalesrepZoneDAO.getSalesZoneDesc(o.getSalesZone())+" &nbsp;&nbsp;PD:"+o.getPdCode()+"</b></td> \n");
				h.append("</tr>");
				h.append("<tr> \n");
				h.append("  <td  colspan='5'><b> แบรนด์ : "+o.getBrand()+"  &nbsp;&nbsp;รหัสสินค้า : "+o.getItemCode()+"</b></td> \n");
				h.append("</tr>");
				h.append("</table> \n");
			}
			
			h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='table table-striped'> \n");
			h.append("<thead> \n");
			//Case Show PD Current Stock
			if( !Utils.isNull(o.getDispCurrentStockPD()).equals("")){
				h.append("<tr class='sticky-header'> \n");
				h.append("<th rowspan='2'>รหัสสินค้า</th> \n");
				h.append("<th rowspan='2'>ชื่อสินค้า</th> \n");
				for(int c=0;c<columnPDList.size();c++){
				   SalesTargetBean columnPDBean = columnPDList.get(c);
				   h.append("<th colspan='2'>"+columnPDBean.getPdCode()+"<br/>"+columnPDBean.getPdDesc()+"</th> \n");
				}
				h.append("<th colspan='2'>ผลรวม</th> \n");
				h.append("</tr> \n");
				h.append("<tr class='sticky-header'> \n");
				for(int c=0;c<columnPDList.size();c++){
					   h.append("<th>เป้าหมาย</th> \n");
					   h.append("<th>สต๊อก PD ปัจจุบัน</th> \n");
					}
				h.append(" <th>เป้าหมาย</th> \n");
				h.append(" <th>สต๊อก PD ปัจจุบัน</th> \n");
				h.append("</tr> \n");
			}else{
				h.append("<tr class='sticky-header'> \n");
				h.append("<th>รหัสสินค้า</th> \n");
				h.append("<th>ชื่อสินค้า</th> \n");
				for(int c=0;c<columnPDList.size();c++){
				   SalesTargetBean columnPDBean = columnPDList.get(c);
				   h.append("<th>"+columnPDBean.getPdCode()+"<br/>"+columnPDBean.getPdDesc()+"</th> \n");
				}
				h.append("<th>ผลรวม</th> \n");
				h.append("</tr> \n");
			 }
			
			h.append("</thead> \n");
			h.append("<tbody> \n");
		    for(int r=0;r<rowProductList.size();r++){
		    	sumRowProduct =0;
		    	sumRowProductStock =0;
		    	SalesTargetBean rowProductBean = rowProductList.get(r);
		    	className = (r %2 == 0)?"lineE":"lineO";
		    	
				h.append("<tr class='"+className+"'> \n");
				h.append("<td class='"+style_text+"' width='5%'>"+rowProductBean.getItemCode()+"</td> \n");
				h.append("<td class='"+style_text+"' width='10%'>"+rowProductBean.getItemName()+"</td> \n");
				 for(int c=0;c<columnPDList.size();c++){
					  SalesTargetBean columnPDBean = columnPDList.get(c);
					  //Case Show PD Current Stock
					  if( !Utils.isNull(o.getDispCurrentStockPD()).equals("")){
						  keyDataMap = rowProductBean.getItemCode()+"-"+columnPDBean.getPdCode();
						   //logger.debug("keyDataMap["+keyDataMap+"]["+dataMAP.get(keyDataMap)+"]");
						   if(dataMAP.get(keyDataMap) != null){
						      h.append("<td class='"+style_number+"' width='5%'>"+dataMAP.get(keyDataMap).getPdQty()+"</td> \n");
						      h.append("<td class='"+style_number+"' width='5%'>"+dataMAP.get(keyDataMap).getPdStockQty()+"</td> \n");
						      sumRowProduct += Utils.convertStrToDouble(dataMAP.get(keyDataMap).getPdQty());
						      sumRowProductStock += Utils.convertStrToDouble(dataMAP.get(keyDataMap).getPdStockQty());
						     
						      //sumColumn 	
						      if(sumColumnPDMap.get(columnPDBean.getPdCode()) !=null){
						    	  //add summary
						    	  sumColumnPD = sumColumnPDMap.get(columnPDBean.getPdCode())+Utils.convertStrToDouble(dataMAP.get(keyDataMap).getPdQty());
						    	  sumColumnPDMap.put(columnPDBean.getPdCode(), sumColumnPD);
						      }else{
						    	  //New summary
						    	  sumColumnPD = Utils.convertStrToDouble(dataMAP.get(keyDataMap).getPdQty());
						    	  sumColumnPDMap.put(columnPDBean.getPdCode(), sumColumnPD);
						      }
						      
						      //sumColumn Stock	PD
						      if(sumColumnPDStockMap.get(columnPDBean.getPdCode()) !=null){
						    	  //add summary
						    	  sumColumnPD = sumColumnPDStockMap.get(columnPDBean.getPdCode())+Utils.convertStrToDouble(dataMAP.get(keyDataMap).getPdStockQty());
						    	  sumColumnPDStockMap.put(columnPDBean.getPdCode(), sumColumnPD);
						      }else{
						    	  //New summary
						    	  sumColumnPD = Utils.convertStrToDouble(dataMAP.get(keyDataMap).getPdStockQty());
						    	  sumColumnPDStockMap.put(columnPDBean.getPdCode(), sumColumnPD);
						      }
						   }else{
							  h.append("<td class='"+style_number+"' width='5%'></td> \n");
							  h.append("<td class='"+style_number+"' width='5%'></td> \n");
						   }
						   
					  }else{
						   keyDataMap = rowProductBean.getItemCode()+"-"+columnPDBean.getPdCode();
						   //logger.debug("keyDataMap["+keyDataMap+"]["+dataMAP.get(keyDataMap)+"]");
						   if(dataMAP.get(keyDataMap) != null){
						      h.append("<td class='"+style_number+"' width='5%'>"+dataMAP.get(keyDataMap).getPdQty()+"</td> \n");
						      sumRowProduct += Utils.convertStrToDouble(dataMAP.get(keyDataMap).getPdQty());
						      
						      //sumColumn 	
						      if(sumColumnPDMap.get(columnPDBean.getPdCode()) !=null){
						    	  //add summary
						    	  sumColumnPD = sumColumnPDMap.get(columnPDBean.getPdCode())+Utils.convertStrToDouble(dataMAP.get(keyDataMap).getPdQty());
						    	  sumColumnPDMap.put(columnPDBean.getPdCode(), sumColumnPD);
						      }else{
						    	  //New summary
						    	  sumColumnPD = Utils.convertStrToDouble(dataMAP.get(keyDataMap).getPdQty());
						    	  sumColumnPDMap.put(columnPDBean.getPdCode(), sumColumnPD);
						      }
						   }else{
							  h.append("<td class='"+style_number+"' width='5%'></td> \n");
						   }
					  }//if
				 }//for
				 
				 //Case Show PD Current Stock
				 //sumRowProduct Stock
				  if( !Utils.isNull(o.getDispCurrentStockPD()).equals("")){
					 h.append("<td class='"+style_number_bold+"' width='5%'>"+Utils.decimalFormat(sumRowProduct,Utils.format_current_2_disgit)+"</td> \n");
					 h.append("<td class='"+style_number_bold+"' width='5%'>"+Utils.decimalFormat(sumRowProductStock,Utils.format_current_2_disgit)+"</td> \n");
				  }else{
					 h.append("<td class='"+style_number_bold+"' width='5%'>"+Utils.decimalFormat(sumRowProduct,Utils.format_current_2_disgit)+"</td> \n");
				  }
				h.append("</tr> \n");
		    }//for
		    
		    //Case Show PD Current Stock
			if( !Utils.isNull(o.getDispCurrentStockPD()).equals("")){
			     //Summary Column
			     double sumAll = 0;double sumAllPD = 0;
				 h.append("<tr class='row_hilight'> \n");
				 h.append("<td class='"+style_text+"' width='5%'></td> \n");
			     h.append("<td class='"+style_text+"' align='right' width='10%'>ผลรวม</td> \n");
				 for(int c=0;c<columnPDList.size();c++){
					  SalesTargetBean columnPDBean = columnPDList.get(c);
					  sumAll += sumColumnPDMap.get(columnPDBean.getPdCode());
					  h.append("<td class='"+style_number_bold+"' width='5%'>"+Utils.decimalFormat(sumColumnPDMap.get(columnPDBean.getPdCode()),Utils.format_current_2_disgit)+"</td> \n");
					  //stock current PD
					  sumAllPD += sumColumnPDStockMap.get(columnPDBean.getPdCode());
					  h.append("<td class='"+style_number_bold+"' width='5%'>"+Utils.decimalFormat(sumColumnPDStockMap.get(columnPDBean.getPdCode()),Utils.format_current_2_disgit)+"</td> \n");
				}
				h.append("<td class='"+style_number_bold+"' width='5%'>"+Utils.decimalFormat(sumAll,Utils.format_current_2_disgit)+"</td> \n");
				h.append("<td class='"+style_number_bold+"' width='5%'>"+Utils.decimalFormat(sumAllPD,Utils.format_current_2_disgit)+"</td> \n");
				h.append("</tr> \n"); 
				h.append("<tbody> \n");
				h.append("</table> \n");
			}else{
				//Summary Column
			     double sumAll = 0;
				 h.append("<tr class='row_hilight'> \n");
				 h.append("<td class='"+style_text+"' width='5%'></td> \n");
			     h.append("<td class='"+style_text+"' align='right' width='10%'>ผลรวม</td> \n");
				 for(int c=0;c<columnPDList.size();c++){
					  SalesTargetBean columnPDBean = columnPDList.get(c);
					  sumAll += sumColumnPDMap.get(columnPDBean.getPdCode());
					  h.append("<td class='"+style_number_bold+"' width='5%'>"+Utils.decimalFormat(sumColumnPDMap.get(columnPDBean.getPdCode()),Utils.format_current_2_disgit)+"</td> \n");
				}
				h.append("<td class='"+style_number_bold+"' width='5%'>"+Utils.decimalFormat(sumAll,Utils.format_current_2_disgit)+"</td> \n");
				h.append("</tr> \n"); 
				h.append("<tbody> \n");
				h.append("</table> \n");
			}
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
		}
	}
}
