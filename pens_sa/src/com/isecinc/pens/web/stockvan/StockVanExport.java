package com.isecinc.pens.web.stockvan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import util.ExcelHeader;
import util.Utils;

public class StockVanExport {
	public static Logger logger = Logger.getLogger("PENS");
	
	
public static StringBuffer genResultStockVanPD(StockVanForm stockVanForm,HttpServletRequest request,String typeExport) throws Exception{
   StringBuffer out = new StringBuffer("");
   String td_text_center = "td_text_center";
   String td_text = "td_text";
   String td_number = "td_number";
   String td_number_bold = "td_number_bold";
   try{
	   if("EXCEL".equalsIgnoreCase(typeExport)){
		   out.append(ExcelHeader.EXCEL_HEADER);//excel style
		   
		   td_text_center = "text";
		   td_text = "text";
		   td_number = "num_currency";
		   td_number_bold = "num_currency_bold";
	   }
	   boolean dispHaveQty = !Utils.isNull(stockVanForm.getBean().getDispHaveQty()).equals("")?true:false;
	   if(stockVanForm.getResultsSearch() != null && stockVanForm.getResultsSearch().size()>0){
	   	
	   /** For summary column **/
	   Map<String,StockVanBean> summaryByColumnMap = new HashMap<String,StockVanBean>();
	   double totalRowQty = 0;//for check 

	   StockVanBean sumBean = null;
	   StringBuffer headTableHtml = new StringBuffer("");
	   StringBuffer rowTableHtml = new StringBuffer("");

	   List<StockVanBean> columnList = (List<StockVanBean>)request.getSession().getAttribute("COLUMN_LIST");
	   /** Calculate width **/
	   int tableWidth =100;
	   int column_1 = 5;
	   int column_2 = 10;
	   int width = 5;
	   int totalColumn = columnList.size();
	   if( !Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")){
	   	totalColumn  =totalColumn*2;//display column plan 
	   }

	   //85% remain to calc
	   if(85%totalColumn==0){
	   	width = 85/totalColumn;
	   }else{
	   	width = 85/totalColumn;
	   }
	   if(totalColumn >8){
	       if(width>5){
	       	int remainWidth = 85-(totalColumn*5);
	       	column_2 +=remainWidth;
	       	width = 5;
	       }
	   }else{
	   	tableWidth = 65;
	   	column_1 = 5;
	   	column_2 = 10;
	   	width = 5;
	   }

	   /***********************Validate Sum Qty Column,Row <>0***********************************/
	   List<StockVanBean> resultList = stockVanForm.getResultsSearch();
	   for(int n=0;n<resultList.size();n++){
	   	StockVanBean rowItem = (StockVanBean)resultList.get(n);
	      //for by column
	      if(rowItem.getRowColumnDataList() != null && rowItem.getRowColumnDataList() .size()>0){ 
	          	for(int c=0;c<rowItem.getRowColumnDataList().size();c++){
	          		StockVanBean columnDataBean = (StockVanBean)rowItem.getRowColumnDataList().get(c);
	          		
	          		//1 Row by pd  ,column product
	          		if( Utils.isNull(stockVanForm.getBean().getDispType()).equals("1")){
	          			//no display plan
	          			if( Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")){ 
	                         //Summary by column (product_code)
	                         if(summaryByColumnMap.get(columnDataBean.getProductCode()) != null){
	                         	 sumBean = summaryByColumnMap.get(columnDataBean.getProductCode());
	                         	 double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	                         	 //logger.debug("sumPdQty:"+sumPdQty);
	                         	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
	                         	
	                         	 //add to summary Map
	                         	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);
	                         }else{
	                         	 sumBean = new StockVanBean();
	                         	 double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	                         	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
	                         	// logger.debug("sumPdQty:"+sumPdQty);
	                         	 //add to summary Map
	                         	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);
	                         }
	          			}else{
	          				 //Summary by column (product_code)
	                        if(summaryByColumnMap.get(columnDataBean.getProductCode()) != null){
	                          	 sumBean = summaryByColumnMap.get(columnDataBean.getProductCode());
	                          	 double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	                          	 double sumPdIntQty = Utils.convertStrToDouble2Digit(sumBean.getPdIntQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
	                          	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
	                          	 sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
	                          	 //add to summary Map
	                          	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);	
	                         }else{
	                          	 sumBean = new StockVanBean();
	                          	 double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	                          	 double sumPdIntQty = Utils.convertStrToDouble2Digit(sumBean.getPdIntQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
	                          	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
	                          	 sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
	                          	
	                          	 //add to summary Map
	                          	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);            
	                         }//if
	          			}//if
	          		}else{
	          			// Row by product  ,column pd
	          			//no display plan
	          			if( Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")){ 
	          				//Summary by column (pd_code)
	                          if(summaryByColumnMap.get(columnDataBean.getPdCode()) != null){
	                          	sumBean = summaryByColumnMap.get(columnDataBean.getPdCode());
	                          	double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
	                          	
	                          	//add to summary Map
	                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
	                          }else{
	                          	sumBean = new StockVanBean();
	                          	double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
	                          	
	                          	//add to summary Map
	                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
	                          }
	          			}else{	
	          				//Summary by column (product_code)
	                          if(summaryByColumnMap.get(columnDataBean.getPdCode()) != null){
	                          	 sumBean = summaryByColumnMap.get(columnDataBean.getPdCode());
	                          	 double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	                          	 double sumPdIntQty = Utils.convertStrToDouble2Digit(sumBean.getPdIntQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
	                          	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
	                          	 sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
	                          	
	                          	 //add to summary Map
	                          	 summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
	                          }else{
	                          	 sumBean = new StockVanBean();
	                          	 double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	                          	 double sumPdIntQty = Utils.convertStrToDouble2Digit(sumBean.getPdIntQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
	                          	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
	                          	 sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
	                          	
	                          	 //add to summary Map
	                          	 summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
	                          }//if
	          			}//fi
	          		}//if
	            }//for 2
	        }//if    		 
	   }//for row

	   /*********************** Head Table ******************************************************/
	     headTableHtml.append("<table id='tblProduct' align='center' border='1' width='"+tableWidth+"%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'>");
	     headTableHtml.append("<tr> ");
	          if(columnList != null && columnList.size()>0){ 
	           //1 row by pd ,column by product
	           if( Utils.isNull(stockVanForm.getBean().getDispType()).equals("1")){	
	   			if( Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")){ 
	   				headTableHtml.append("<th>PD/หน่วยรถ</th>");
	   				headTableHtml.append("<th>ชื่อ PD</th>");
	   			    
	   				//<!-- no display plan -->
             	    for(int k=0;k<columnList.size();k++){
                      StockVanBean s = (StockVanBean)columnList.get(k);
                      //check qty <> 0 to show
                      if(dispHaveQty){
                         if(Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty())  != 0){
                            headTableHtml.append("<th>"+s.getProductCode()+"</th>");
                         }
                      }else{
                    	 headTableHtml.append("<th>"+s.getProductCode()+"</th>");
                      }
                   }//for 
	   			}else{ 
	   				headTableHtml.append("<th rowspan='2'>PD/หน่วยรถ</th>");
	   				headTableHtml.append("<th rowspan='2'>ชื่อ PD</th>");
	   			    
	   			   // <!-- display plan -->
	   			     for(int k=0;k<columnList.size();k++){
                          StockVanBean s = (StockVanBean)columnList.get(k);
                          //check qty <> 0 to show
                          if(dispHaveQty){
	                          if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
	                       	   || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0 ){
	                               
	                       	   headTableHtml.append("<th colspan='2'>"+s.getProductCode()+"</th>");
	                          }
                          }else{
                        	  headTableHtml.append("<th colspan='2'>"+s.getProductCode()+"</th>");
                          }
                       }//for 
	   			} 
	   		}else{ 
	   			//2 row by product,column by pd
	   		   headTableHtml.append("<th>รหัสสินค้า</th>");
	   		   headTableHtml.append("<th>ชื่อสินค้า</th>");
	   			if( Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")){ 
	   				//<!-- no display plan -->
             	    for(int k=0;k<columnList.size();k++){
                      StockVanBean s = (StockVanBean)columnList.get(k);
                      //check qty <> 0 to show
                      if(dispHaveQty){
	                      if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty())  != 0){
	                         headTableHtml.append("<th>"+s.getPdCode()+"</th>");
	                      }
                      }else{
                    	  headTableHtml.append("<th>"+s.getPdCode()+"</th>");
                      }
                   }//for 
	   			}else{
	   			    //<!--  display plan -->
	   				for(int k=0;k<columnList.size();k++){
                      StockVanBean s = (StockVanBean)columnList.get(k);
                      //check qty <> 0 to show
                      if(dispHaveQty){
	                      if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty()) != 0
	                   	   || Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdIntQty()) != 0 ){
	                         headTableHtml.append("<th>"+s.getPdCode()+"</th>");
	                         headTableHtml.append("<th>"+s.getPdCodeIntransit()+" ระหว่างทาง</th>");
	                      }
                      }else{
                    	  headTableHtml.append("<th>"+s.getPdCode()+"</th>");
	                      headTableHtml.append("<th>"+s.getPdCodeIntransit()+" ระหว่างทาง</th>");
                      }
                   }//for 
	   			} 
	   		}//if 2 
	          }//if 1
	     
	     out.append("</tr>");
	   	   
	    //Case 1,and display plan  
	    if( Utils.isNull(stockVanForm.getBean().getDispType()).equals("1")
	   	  && !Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("") ){
	   	   headTableHtml.append("<tr>");
	   	  for(int k=0;k<columnList.size();k++){
              StockVanBean s = (StockVanBean)columnList.get(k);
              if(dispHaveQty){
                  if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
               	   || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0 ){
			           headTableHtml.append("<th>ใน PD</th>");
			           headTableHtml.append("<th>ระหว่างทาง</th>");
                  }
              }else{
            	  headTableHtml.append("<th>ใน PD</th>");
		          headTableHtml.append("<th>ระหว่างทาง</th>");
              }
	   	  }//for
	   	  headTableHtml.append("</tr>");
	    } 
	    out.append(headTableHtml.toString());
	    /*********************** Head Table ******************************************************/
	      
	     /********************* Row Detail *******************************************************/
	   	String tabclass ="";
	   	for(int n=0;n<resultList.size();n++){
	   		rowTableHtml = new StringBuffer("");
	   		StockVanBean rowItem = (StockVanBean)resultList.get(n);
	   		if(n%2==0){ 
	   			tabclass="lineO";
	   		}else{
	   			tabclass ="lineE";
	   		}
	   	
	   		rowTableHtml.append("<tr class="+tabclass+">");
	   		 	//1 Row by pd  ,column product
	   		    if( Utils.isNull(stockVanForm.getBean().getDispType()).equals("1")){
	   		       rowTableHtml.append("<td class='"+td_text_center+"' width='"+column_1+"%'>"+rowItem.getPdCode()+"</td>");
	   		       rowTableHtml.append("<td class='"+td_text+"' width='"+column_2+"%'>"+rowItem.getPdDesc()+"</td>");
	   			}else{ 
	   			   //<!--  Row by product  ,column pd--> 
	   			   rowTableHtml.append("<td class='"+td_text_center+"' width='"+column_1+"%'>"+rowItem.getProductCode()+"</td>");
	   			   rowTableHtml.append("<td class='"+td_text+"' width='"+column_2+"%'>"+rowItem.getProductName()+"</td>");
	   			} 
	   			 //<!--  For By Column Select List-->
	   		       // System.out.println("getRowColumnDataList Size:"+rowItem.getRowColumnDataList() .size());
	   		      
	   		        if(rowItem.getRowColumnDataList() != null && rowItem.getRowColumnDataList() .size()>0){ 
	   		        	for(int c=0;c<rowItem.getRowColumnDataList().size();c++){
	   		        		StockVanBean columnDataBean = (StockVanBean)rowItem.getRowColumnDataList().get(c);
	   		        		
	   		        		//1 Row by pd  ,column product
	   		        		if( Utils.isNull(stockVanForm.getBean().getDispType()).equals("1")){
	   		        			//no display plan
	   		        			if( Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")){ 
	   		        				//check qty <> 0 to show
	   		        			    if(dispHaveQty){
		   	                            if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdQty()) != 0){
		   		        				   rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdQty()+"</td>");
		   		        				   //for check all column in row qty <>0
		   		                           totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   	                             }
	   		        			    }else{
	   		        			    	rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdQty()+"</td>");
		   		        				//for check all column in row qty <>0
		   		                        totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	   		        			    }
	   		        			}else{
	   		        				// display plan
	   		        				//check qty <> 0 to show
	   		        				if(dispHaveQty){
		                                  if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdQty()) != 0
		                       	          || Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdIntQty()) != 0 ){
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdQty()+"</td>");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdIntQty()+"</td>");
		   		        				
		   		        				    //for check all column in row qty <>0
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		                                  }
	   		        				}else{
	   		        					rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdQty()+"</td>");
	   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdIntQty()+"</td>");
	   		        				
	   		        				    //for check all column in row qty <>0
	   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
	   		        				}
	   		        			}//if
	   		        		}else{
	   		        			// Row by product  ,column pd
	   		        			//no display plan
	   		        			if( Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")){ 
	   		        				//check qty <> 0 to show
	   		        				if(dispHaveQty){
		   	                            if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getPdCode()).getPdQty()) != 0){
		   		        				   rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdQty()+"</td>");
		   		        				   //for check all column in row qty <>0
		   		                           totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   	                            }
	   		        				}else{
	   		        				   rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdQty()+"</td>");
	   		        				   //for check all column in row qty <>0
	   		                           totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	   		        				}
	   		        			}else{
	   		        				// display plan
	   		        			   //check qty <> 0 to show
	   		        				if(dispHaveQty){
	   		        				  if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getPdCode()).getPdQty()) != 0
	                       	           || Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getPdCode()).getPdIntQty()) != 0 ){
	   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdQty()+"</td>");
	   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdIntQty()+"</td>");
	   		        				   
	   		        				   //for check all column in row qty <>0
	   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
	   		        				  }
	   		        				}else{
	   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdQty()+"</td>");
	   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+columnDataBean.getPdIntQty()+"</td>");
	   		        				   
	   		        				   //for check all column in row qty <>0
	   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
	   		        				}
	   		        			}//fi
	   		        		}//if
	   		             }//for 2
	   		           }//if  
	   		         
	   		       // <!--  For By Column List-->
	   		       rowTableHtml.append("</tr>"); 
	   		          
	   		// System.out.println("totalRowQty["+totalRowQty+"]");
	   		 /**Show Row is sum Row Qty != 0 **/
	   		 if(totalRowQty != 0){
	   			 out.append(rowTableHtml.toString());
	   		 }
	   		 totalRowQty=0;         		 
	   	}//for row
	   	
	   	//<!-- ***** Summary ***** -->
	   	out.append("<tr class='row_hilight'>"); 
	   	out.append("<td width='"+(column_1+column_2)+"%' colspan='2' align='right'>ยอดรวม</td>");
	   	if(columnList != null && columnList.size() >0){
	        for(int k=0;k<columnList.size();k++){
	              StockVanBean s = (StockVanBean)columnList.get(k);
	              //1 Row by pd  ,column product
	          		if( Utils.isNull(stockVanForm.getBean().getDispType()).equals("1")){
	          			//no display plan
		          		 if( Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")){ 
		          		   sumBean = summaryByColumnMap.get(s.getProductCode());
		          		   //check qty <> 0 to show
		          		   if(dispHaveQty){
		                      if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0){
		          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td>");
		                      }
		          		   }else{
		          			 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td>"); 
		          		   }
		          		 
	          			 }else{
	          				//display plan
	          				sumBean = summaryByColumnMap.get(s.getProductCode());
	          			   //check qty <> 0 to show
	          			   if(dispHaveQty){
		                       if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
		                       || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0){
		          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td>");
		          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td>");
		                       }
		                   }else{
		                	   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td>");
	          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td>");  
		                   }
	          			}
	          		}else{
	          			// Row by product  ,column pd
	          			// no display plan
	          			if( Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")){ 
	          				sumBean = summaryByColumnMap.get(s.getPdCode());
	          			    //check qty <> 0 to show
	          				if(dispHaveQty){
	                          if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty()) != 0){
	          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td>");
	                          }
	          				}else{
	          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td>");
	          				}
	          			}else{
	          			   // display plan
	          				sumBean = summaryByColumnMap.get(s.getPdCode());
	          			   //check qty <> 0 to show
	          			   if(dispHaveQty){
		                       if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty()) != 0
		                        || Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdIntQty()) != 0){
		          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td>");
		          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td>");
		                       }
	          			   }else{
	          				  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td>");
	          				  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td>");
	          			   }
	          			}//if
	          		}//if
	           }//for 
	   	//<!-- ***** Summary ***** -->
	   	}//if
	   	
	     out.append("</tr>"); 
	     out.append("</table>");
	   }              
   }catch(Exception e){
   	 e.printStackTrace();
   }
      return out;
	}

}
