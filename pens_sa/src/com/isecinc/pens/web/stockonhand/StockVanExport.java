package com.isecinc.pens.web.stockonhand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class StockVanExport {
	public static Logger logger = Logger.getLogger("PENS");
	
	
public static StringBuffer genResultStockVanPD(StockOnhandForm stockVanForm,HttpServletRequest request,String typeExport) throws Exception{
   StringBuffer out = new StringBuffer("");
   String td_text_center = "td_text_center";
   String td_text = "td_text";
   String td_number = "td_number";
   String td_number_bold = "td_number_bold";
   String pdDescShow = "";
   try{
	   /** Case Export to Excel **/
	   if("EXCEL".equalsIgnoreCase(typeExport)){
		   out.append(ExcelHeader.EXCEL_HEADER);//excel style
		   
		   td_text_center = "text";
		   td_text = "text";
		   td_number = "currency";
		   td_number_bold = "currency_bold";
	   }
	   String dispType = Utils.isNull(stockVanForm.getBean().getDispType());
	   String pdType = Utils.isNull(stockVanForm.getBean().getPdType());
	   boolean dispHaveQty = !Utils.isNull(stockVanForm.getBean().getDispHaveQty()).equals("")?true:false;
	   boolean dispPlan = !Utils.isNull(stockVanForm.getBean().getDispPlan()).equals("")?true:false;
	   boolean dispPrice = !Utils.isNull(stockVanForm.getBean().getDispPrice()).equals("")?true:false;
	   
	   logger.debug("pdType:"+pdType);
	   logger.debug("dispType:"+dispType);
	   logger.debug("dispHaveQty:"+dispHaveQty);
	   logger.debug("dispPlan:"+dispPlan);
	   logger.debug("dispPrice:"+dispPrice);
	   
	   if(stockVanForm.getResultsSearch() != null && stockVanForm.getResultsSearch().size()>0){
	   	
	   /** For summary column **/
	   Map<String,StockOnhandBean> summaryByColumnMap = new HashMap<String,StockOnhandBean>();
	   double totalRowQty = 0;//for check 

	   StockOnhandBean sumBean = null;
	   StringBuffer headTableHtml = new StringBuffer("");
	   StringBuffer rowTableHtml = new StringBuffer("");

	   List<StockOnhandBean> columnList = (List<StockOnhandBean>)request.getSession().getAttribute("COLUMN_LIST");
	   if(columnList ==null || (columnList !=null && columnList.size()==0)){
		   return new StringBuffer("");
	   }
	   /** Calculate width **/
	   int tableWidth =100;
	   int column_1 = 5;
	   int column_2 = 10;
	   int width = 5;
	   int totalColumn = columnList.size();
	   if( dispPlan==true ){
	   	    totalColumn  =totalColumn*2;//display column plan 
	   }
	   if( dispPlan==true && dispPrice==true  ){
		   	totalColumn  =totalColumn*4;//display column plan 
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
	   List<StockOnhandBean> resultList = stockVanForm.getResultsSearch();
	   /************* Summary by Column *********************************************************/
	   if(true){
		   for(int n=0;n<resultList.size();n++){
		   	  StockOnhandBean rowItem = (StockOnhandBean)resultList.get(n);
		      //for by column
		      if(rowItem.getRowColumnDataList() != null && rowItem.getRowColumnDataList() .size()>0){ 
		          	for(int c=0;c<rowItem.getRowColumnDataList().size();c++){
		          		StockOnhandBean columnDataBean = (StockOnhandBean)rowItem.getRowColumnDataList().get(c);
		          		
		          		//1 Row by pd  ,column product
		          		if( dispType.equals("1")){
		          		    // 1.1) no plan,no price test=pass
		          			if(dispPlan==false && dispPrice==false){
		          			    //Summary by column (product_code)
		                         if(summaryByColumnMap.get(columnDataBean.getProductCode()) != null){
		                         	 sumBean = summaryByColumnMap.get(columnDataBean.getProductCode());
		                         	 double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                         	 //logger.debug("sumPdQty:"+sumPdQty);
		                         	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                         	 //add to summary Map
		                         	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);
		                         }else{
		                         	 sumBean = new StockOnhandBean();
		                         	 double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                         	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                         	 
		                         	 // logger.debug("sumPdQty:"+sumPdQty);
		                         	 //add to summary Map
		                         	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);
		                         }
		          			// 1.2) display plan,no price test=pass
		          			}else if( dispPlan==true && dispPrice==false){ 
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
		                          	 sumBean = new StockOnhandBean();
		                          	 double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                          	 double sumPdIntQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		                        	 
		                          	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	 sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
		                          	
		                          	 //add to summary Map
		                          	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);            
		                         }//if
		          			// 1.3) no plan ,display price  test=
		          			}else if(dispPlan==false && dispPrice==true){ 
		          			    //Summary by column (product_code)
		                         if(summaryByColumnMap.get(columnDataBean.getProductCode()) != null){
		                         	 sumBean = summaryByColumnMap.get(columnDataBean.getProductCode());
		                         	 double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                         	 double sumPdPrice = Utils.convertStrToDouble2Digit(sumBean.getPdPrice())+Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		                         	 //logger.debug("sumPdQty:"+sumPdQty);
		                         	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                         	 sumBean.setPdPrice(String.valueOf(sumPdPrice));//add summary
		                         	
		                         	 //add to summary Map
		                         	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);
		                         }else{
		                         	 sumBean = new StockOnhandBean();
		                         	 double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                         	 double sumPdPrice = Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		                         	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                         	 sumBean.setPdPrice(String.valueOf(sumPdPrice));//add summary
		                         	 
		                         	 // logger.debug("sumPdQty:"+sumPdQty);
		                         	 //add to summary Map
		                         	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);
		                         }
		          			// 1.4) display plan ,display price  test=
		          			}else if( dispPlan==true && dispPrice==true){
		          			   //Summary by column (product_code)
		                        if(summaryByColumnMap.get(columnDataBean.getProductCode()) != null){
		                          	 sumBean = summaryByColumnMap.get(columnDataBean.getProductCode());
		                          	 double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                          	 double sumPdPrice = Utils.convertStrToDouble2Digit(sumBean.getPdPrice())+Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		                          	 double sumPdIntQty = Utils.convertStrToDouble2Digit(sumBean.getPdIntQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		                          	 double sumPdIntPrice = Utils.convertStrToDouble2Digit(sumBean.getPdIntPrice())+Utils.convertStrToDouble2Digit(columnDataBean.getPdIntPrice());
		                          	
		                          	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	 sumBean.setPdPrice(String.valueOf(sumPdPrice));//add summary
		                          	 sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
		                          	 sumBean.setPdIntPrice(String.valueOf(sumPdIntPrice));//add summary
		                          	 //add to summary Map
		                          	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);	
		                         }else{
		                          	 sumBean = new StockOnhandBean();
		                          	 double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                          	 double sumPdPrice = Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		                          	 double sumPdIntQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		                          	 double sumPdIntPrice = Utils.convertStrToDouble2Digit(columnDataBean.getPdIntPrice());
		                        	 
		                          	 sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	 sumBean.setPdPrice(String.valueOf(sumPdPrice));//add summary
		                          	 sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
		                          	 sumBean.setPdIntPrice(String.valueOf(sumPdIntPrice));//add summary
		                          	
		                          	 //add to summary Map
		                          	 summaryByColumnMap.put(columnDataBean.getProductCode(), sumBean);            
		                         }//if
		          			}
		          		}else{
		          			//2). Row by product  ,column pd
		          		    // 2.1) no plan,no price test=pass
		          			if(dispPlan==false && dispPrice==false){
		          			     //Summary by column (pd_code)
		                         if(summaryByColumnMap.get(columnDataBean.getPdCode()) != null){
		                          	sumBean = summaryByColumnMap.get(columnDataBean.getPdCode());
		                          	double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	
		                          	//add to summary Map
		                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
		                         }else{
		                          	sumBean = new StockOnhandBean();
		                          	double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	
		                          	//add to summary Map
		                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
		                         }
		          			// 2.2) display plan,no price test=pass
		          			}else if( dispPlan==true && dispPrice==false){ 
		          			    //Summary by column (pd_code)
		                         if(summaryByColumnMap.get(columnDataBean.getPdCode()) != null){
		                          	sumBean = summaryByColumnMap.get(columnDataBean.getPdCode());
		                          	double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                        	double sumPdIntQty = Utils.convertStrToDouble2Digit(sumBean.getPdIntQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
		                          	//add to summary Map
		                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
		                         }else{
		                          	sumBean = new StockOnhandBean();
		                          	double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                          	double sumPdIntQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
		                          	//add to summary Map
		                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
		                         }
		          			// 2.3) no plan ,display price  test=
		          			 }else if(dispPlan==false && dispPrice==true){ 
		          				//Summary by column (pd_code)
		                         if(summaryByColumnMap.get(columnDataBean.getPdCode()) != null){
		                          	sumBean = summaryByColumnMap.get(columnDataBean.getPdCode());
		                          	double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                        	double sumPdPrice = Utils.convertStrToDouble2Digit(sumBean.getPdPrice())+Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	sumBean.setPdPrice(String.valueOf(sumPdPrice));//add summary
		                          	//add to summary Map
		                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
		                         }else{
		                          	sumBean = new StockOnhandBean();
		                          	double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                          	double sumPdPrice = Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	sumBean.setPdPrice(String.valueOf(sumPdPrice));//add summary
		                          	//add to summary Map
		                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
		                         }
		          			// 2.4) display plan ,display price  test=
		          			}else if( dispPlan==true && dispPrice==true){
		          			    //Summary by column (pd_code)
		                         if(summaryByColumnMap.get(columnDataBean.getPdCode()) != null){
		                          	sumBean = summaryByColumnMap.get(columnDataBean.getPdCode());
		                          	double sumPdQty = Utils.convertStrToDouble2Digit(sumBean.getPdQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                        	double sumPdPrice = Utils.convertStrToDouble2Digit(sumBean.getPdPrice())+Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		                        	double sumPdIntQty = Utils.convertStrToDouble2Digit(sumBean.getPdIntQty())+Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		                        	double sumPdIntPrice = Utils.convertStrToDouble2Digit(sumBean.getPdIntPrice())+Utils.convertStrToDouble2Digit(columnDataBean.getPdIntPrice());
		                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	sumBean.setPdPrice(String.valueOf(sumPdPrice));//add summary
		                          	sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
		                          	sumBean.setPdIntPrice(String.valueOf(sumPdIntPrice));//add summary
		                          	//add to summary Map
		                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
		                         }else{
		                          	sumBean = new StockOnhandBean();
		                          	double sumPdQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		                          	double sumPdPrice = Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		                          	double sumPdIntQty = Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		                          	double sumPdIntPrice = Utils.convertStrToDouble2Digit(columnDataBean.getPdIntPrice());
		                          	sumBean.setPdQty(String.valueOf(sumPdQty));//add summary
		                          	sumBean.setPdPrice(String.valueOf(sumPdPrice));//add summary
		                          	sumBean.setPdIntQty(String.valueOf(sumPdIntQty));//add summary
		                          	sumBean.setPdIntPrice(String.valueOf(sumPdIntPrice));//add summary
		                          	//add to summary Map
		                          	summaryByColumnMap.put(columnDataBean.getPdCode(), sumBean);
		                         }
		          			}//if 
		          		}//if
		            }//for 2
		        }//if    		 
		   }//for row
	   }
	   /*********************** Head Table ******************************************************/
	     headTableHtml.append("<table id='tblProduct' align='center' border='1' width='"+tableWidth+"%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
	     headTableHtml.append("<tr> \n");
	          if(columnList != null && columnList.size()>0){ 
	           //1 row by pd ,column by product
	           if(dispType.equals("1")){	
	        	 //  1.1)no display plan ,price
	   			 if( dispPlan==false && dispPrice==false){ 
	   				headTableHtml.append("<th>PD/หน่วยรถ</th> \n");
	   				headTableHtml.append("<th>ชื่อ PD</th>\n");
	   			    if(pdType.equalsIgnoreCase("P") && dispType.equals("1")){
	   					headTableHtml.append("<th>จังหวัด</th>\n");
	   				}
             	    for(int k=0;k<columnList.size();k++){
                      StockOnhandBean s = (StockOnhandBean)columnList.get(k);
                      //check qty <> 0 to show
                      if(dispHaveQty){
                         if(Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty())  != 0){
                            headTableHtml.append("<th>"+s.getProductCode()+"</th> \n");
                         }
                      }else{
                    	 headTableHtml.append("<th>"+s.getProductCode()+"</th> \n");
                      }
                   }//for 
	   			   // 1.2)display plan ,no price
	   			  }else if( dispPlan==true && dispPrice==false){ 
	   				  headTableHtml.append("<th rowspan='2'>PD/หน่วยรถ</th> \n");
	   				  headTableHtml.append("<th rowspan='2'>ชื่อ PD</th> \n");
	   				  if(pdType.equalsIgnoreCase("P") && dispType.equals("1")){
	   					  headTableHtml.append("<th rowspan='2'>จังหวัด</th>\n");
	   				  }
	   			      for(int k=0;k<columnList.size();k++){
                          StockOnhandBean s = (StockOnhandBean)columnList.get(k);
                          //check qty <> 0 to show
                          if(dispHaveQty){
	                          if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
	                       	   || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0 ){
	                               
	                       	   headTableHtml.append("<th colspan='2'>"+s.getProductCode()+"</th> \n");
	                          }
                          }else{
                        	  headTableHtml.append("<th colspan='2'>"+s.getProductCode()+"</th> \n");
                          }
                        }//for 
	   			    //  1.3).no plan ,display price   
	   				}else if( dispPlan ==false && dispPrice==true){ 
	   					  headTableHtml.append("<th rowspan='2'>PD/หน่วยรถ</th> \n");
		   				  headTableHtml.append("<th rowspan='2'>ชื่อ PD</th> \n");
		   				  if(pdType.equalsIgnoreCase("P") && dispType.equals("1")){
		   					  headTableHtml.append("<th rowspan='2'>จังหวัด</th>\n");
		   				  }
		   			      for(int k=0;k<columnList.size();k++){
	                          StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                          //check qty <> 0 to show
	                          if(dispHaveQty){
		                          if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
		                       	   || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0 ){
		                               
		                       	   headTableHtml.append("<th colspan='2'>"+s.getProductCode()+"</th> \n");
		                          }
	                          }else{
	                        	  headTableHtml.append("<th colspan='2'>"+s.getProductCode()+"</th> \n");
	                          }
	                        }//for 
	   				//  1.4).Display plan ,display price 
	   				}else if( dispPlan==true && dispPrice==true){ 
		   				  headTableHtml.append("<th rowspan='3'>PD/หน่วยรถ</th> \n");
		   				  headTableHtml.append("<th rowspan='3'>ชื่อ PD</th> \n");
		   				  if(pdType.equalsIgnoreCase("P") && dispType.equals("1")){
		   					  headTableHtml.append("<th rowspan='3'>จังหวัด</th>\n");
		   				  }
		   			      for(int k=0;k<columnList.size();k++){
	                         StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                         //check qty <> 0 to show
	                         if(dispHaveQty){
		                          if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
		                       	   || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0 ){
		                               
		                       	   headTableHtml.append("<th colspan='4'>"+s.getProductCode()+"</th> \n");
		                          }
	                         }else{
	                       	     headTableHtml.append("<th colspan='4'>"+s.getProductCode()+"</th> \n");
	                         }
	                      }//for 
	   				} 
	   		}else{ 
	   		   //Case 2 row by product,column by pd
		   		// 2.1) no plan,no price test=pass
		   		if(dispPlan==false && dispPrice==false){
		   			 headTableHtml.append("<th>รหัสสินค้า</th> \n");
		   		     headTableHtml.append("<th>ชื่อสินค้า</th> \n");
			   		 for(int k=0;k<columnList.size();k++){
	                     StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                     //check qty <> 0 to show
	                     if(dispHaveQty){
		                      if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty())  != 0){
		                    	 pdDescShow = "EXCEL".equals(typeExport)?"-"+s.getPdDesc():"";//show only export excel
		                         headTableHtml.append("<th>"+s.getPdCode()+pdDescShow+"</th>");
		                      }
	                     }else{
	                    	 pdDescShow = "EXCEL".equals(typeExport)?"-"+s.getPdDesc():"";//show only export excel
	                         headTableHtml.append("<th>"+s.getPdCode()+pdDescShow+"</th>");
	                     }
	                  }//for 
		   		// 2.2) display plan,no price test=pass
		   		}else if( dispPlan==true && dispPrice==false){ 
		   			 headTableHtml.append("<th>รหัสสินค้า</th> \n");
		   		     headTableHtml.append("<th>ชื่อสินค้า</th> \n");
		   			 for(int k=0;k<columnList.size();k++){
	                     StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                     //check qty <> 0 to show
	                     if(dispHaveQty){
	                    	 if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty()) != 0
	      	                   	   || Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdIntQty()) != 0 ){
	                    		 pdDescShow = "EXCEL".equals(typeExport)?"-"+s.getPdDesc():"";//show only export excel
		                         headTableHtml.append("<th>"+s.getPdCode()+pdDescShow+"</th>");
	 	                         headTableHtml.append("<th>"+s.getPdCodeIntransit()+" ระหว่างทาง</th>");
		                      }
	                     }else{
	                    	 pdDescShow = "EXCEL".equals(typeExport)?"-"+s.getPdDesc():"";//show only export excel
	                         headTableHtml.append("<th>"+s.getPdCode()+pdDescShow+"</th>");
	                         headTableHtml.append("<th>"+s.getPdCodeIntransit()+" ระหว่างทาง</th>");
	                     }
	                  }//for 
		   		// 2.3) no plan ,display price  test=
		   		}else if(dispPlan==false && dispPrice==true){ 
		   			 headTableHtml.append("<th rowspan='2'>รหัสสินค้า</th> \n");
		   		     headTableHtml.append("<th rowspan='2'>ชื่อสินค้า</th> \n");
			   		 for(int k=0;k<columnList.size();k++){
	                     StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                     //check qty <> 0 to show
	                     if(dispHaveQty){
	                    	 if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty()) != 0 ){
	                    		 pdDescShow = "EXCEL".equals(typeExport)?"-"+s.getPdDesc():"";//show only export excel
		                         headTableHtml.append("<th colspan='2'>"+s.getPdCode()+pdDescShow+"</th>");
		                      }
	                     }else{
	                    	 pdDescShow = "EXCEL".equals(typeExport)?"-"+s.getPdDesc():"";//show only export excel
	                         headTableHtml.append("<th colspan='2'>"+s.getPdCode()+pdDescShow+"</th>");
	                     }
	                  }//for 
		   		// 2.4) display plan ,display price  test=
		   		}else if( dispPlan==true && dispPrice==true){
		   			 headTableHtml.append("<th rowspan='3'>รหัสสินค้า</th> \n");
		   		     headTableHtml.append("<th rowspan='3'>ชื่อสินค้า</th> \n");
		   		 	 for(int k=0;k<columnList.size();k++){
	                     StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                     //check qty <> 0 to show
	                     if(dispHaveQty){
	                    	 if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty()) != 0 
	                    	   || Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdIntQty()) != 0 ){
	                    		 pdDescShow = "EXCEL".equals(typeExport)?"-"+s.getPdDesc():"";//show only export excel
		                         headTableHtml.append("<th colspan='4'>"+s.getPdCode()+pdDescShow+"</th>");
		                      }
	                     }else{
	                    	 pdDescShow = "EXCEL".equals(typeExport)?"-"+s.getPdDesc():"";//show only export excel
	                         headTableHtml.append("<th colspan='4'>"+s.getPdCode()+pdDescShow+"</th>");
	                     }
	                  }//for 
		   		}//if
	   		}//if 2 
	      }//if 1
	     
	     headTableHtml.append("</tr>");
	   	/**** Show Row ,colspan  head Table *******************************************************/   
	    //Case 1,and display plan  
	    if(dispType.equals("1")){
	    	 //  1.1)no display plan ,price
   			if( dispPlan==false && dispPrice==false){ 

	    	//  1.2) display plan ,no price 
   			}else if( dispPlan==true && dispPrice==false){ 
		   	      headTableHtml.append("<tr>");
		   	      for(int k=0;k<columnList.size();k++){
	                StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                if(dispHaveQty){
	                   if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
	               	    || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0 ){
				           headTableHtml.append("<th>ใน PD</th> \n");
				           headTableHtml.append("<th>ระหว่างทาง</th> \n");
	                   }
	                }else{
	            	  headTableHtml.append("<th>ใน PD</th> \n");
			          headTableHtml.append("<th>ระหว่างทาง</th> \n");
	               }
		   	     }//for
		   	     headTableHtml.append("</tr> \n"); 
		   // 1.3).no plan ,display price   test =
		   }else if( dispPlan==false && dispPrice==true){ 
			     headTableHtml.append("<tr>");
		   	     for(int k=0;k<columnList.size();k++){
	                StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                if(dispHaveQty){
	                   if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
	               	    || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0 ){
				           headTableHtml.append("<th>สต๊อก</th> \n");
				           headTableHtml.append("<th>ยอดเงิน</th> \n");
	                   }
	                }else{
	            	  headTableHtml.append("<th>สต๊อก</th> \n");
			          headTableHtml.append("<th>ยอดเงิน</th> \n");
	               }
		   	     }//for
		   	     headTableHtml.append("</tr> \n");
		   // 1.4).Display plan ,display price 
		   }else if( dispPlan==true && dispPrice==true){ 
			     //***** Row head 2 *************************************************/
			      headTableHtml.append("<tr>");
		   	      for(int k=0;k<columnList.size();k++){
	                StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                if(dispHaveQty){
	                   if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
	               	    || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0 ){
				           headTableHtml.append("<th colspan='2'>ใน PD</th> \n");
				           headTableHtml.append("<th colspan='2'>ระหว่างทาง</th> \n");
	                   }
	                }else{
	            	   headTableHtml.append("<th colspan='2'>ใน PD</th> \n");
			           headTableHtml.append("<th colspan='2'>ระหว่างทาง</th> \n");
	               }
		   	     }//for
		   	     headTableHtml.append("</tr> \n");
		   	     //***** Row head 3 *************************************************/
			   	 headTableHtml.append("<tr>");
		   	     for(int k=0;k<columnList.size();k++){
	                StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                if(dispHaveQty){
	                   if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdQty()) != 0
	               	    || Utils.convertStrToDouble(summaryByColumnMap.get(s.getProductCode()).getPdIntQty()) != 0 ){
				           headTableHtml.append("<th>สต๊อก</th> \n");
				           headTableHtml.append("<th>ยอดเงิน</th> \n");
				           headTableHtml.append("<th>สต๊อก</th> \n");
				           headTableHtml.append("<th>ยอดเงิน</th> \n");
	                   }
	                }else{
	            	   headTableHtml.append("<th>สต๊อก</th> \n");
			           headTableHtml.append("<th>ยอดเงิน</th> \n");
			           headTableHtml.append("<th>สต๊อก</th> \n");
			           headTableHtml.append("<th>ยอดเงิน</th> \n");
	               }
		   	     }//for
		   	     headTableHtml.append("</tr> \n");
	       }//if
	    }else{
	    	//Case 2,row product ,pd column
	    	// 2.1) no plan,no price test=pass
	    	if(dispPlan==false && dispPrice==false){

	    	// 2.2) display plan,no price test=pass
	    	}else if( dispPlan==true && dispPrice==false){ 

	    	// 2.3) no plan ,display price  test=
	    	}else if(dispPlan==false && dispPrice==true){ 
	    		  headTableHtml.append("<tr>");
	    		  logger.debug("Case 2.3 columnSize:"+columnList.size());
		   	      for(int k=0;k<columnList.size();k++){
	                StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                if(dispHaveQty){
	                   if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty()) != 0){
	                	   logger.debug("add -->:dispHaveQty");
				           headTableHtml.append("<th>สต๊อก</th> \n");
				           headTableHtml.append("<th>ยอดเงิน</th> \n");
	                   }
	                }else{
	                	 logger.debug("add -->:");
	            	  headTableHtml.append("<th>สต๊อก</th> \n");
			          headTableHtml.append("<th>ยอดเงิน</th> \n");
	               }
		   	     }//for
		   	     headTableHtml.append("</tr> \n");
	    	// 2.4) display plan ,display price  test=
	    	}else if( dispPlan==true && dispPrice==true){
	    		 //***** Row head 2 *************************************************/
			     headTableHtml.append("<tr>");
		   	      for(int k=0;k<columnList.size();k++){
	                StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                if(dispHaveQty){
	                   if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty()) != 0
	               	    || Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdIntQty()) != 0 ){
	                	   logger.debug("add row2 dispHaveQty");
				           headTableHtml.append("<th colspan='2'>ใน PD</th> \n");
				           headTableHtml.append("<th colspan='2'>ระหว่างทาง</th> \n");
	                   }
	                }else{
	                	  logger.debug("add row2");
	            	   headTableHtml.append("<th colspan='2'>ใน PD</th> \n");
			           headTableHtml.append("<th colspan='2'>ระหว่างทาง</th> \n");
	               }
		   	     }//for
		   	     headTableHtml.append("</tr> \n");
		   	     //***** Row head 3 *************************************************/
			   	 headTableHtml.append("<tr>");
		   	     for(int k=0;k<columnList.size();k++){
	                StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	                if(dispHaveQty){
	                   if( Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdQty()) != 0
	               	    || Utils.convertStrToDouble(summaryByColumnMap.get(s.getPdCode()).getPdIntQty()) != 0 ){
	                	   logger.debug("add dispHaveQty");
				           headTableHtml.append("<th>สต๊อก</th> \n");
				           headTableHtml.append("<th>ยอดเงิน</th> \n");
				           headTableHtml.append("<th>สต๊อก</th> \n");
				           headTableHtml.append("<th>ยอดเงิน</th> \n");
	                   }
	                }else{
	                   logger.debug("add ");
	            	   headTableHtml.append("<th>สต๊อก</th> \n");
			           headTableHtml.append("<th>ยอดเงิน</th> \n");
			           headTableHtml.append("<th>สต๊อก</th> \n");
			           headTableHtml.append("<th>ยอดเงิน</th> \n");
	               }
		   	     }//for
		   	     headTableHtml.append("</tr> \n");
	    	}//if
	    }//if
	    out.append(headTableHtml.toString());
	    /*********************** Head Table ******************************************************/
	      
	     /********************* Row Detail *******************************************************/
	   	String tabclass ="";
	   	StockOnhandBean rowItem = null;
	   	StockOnhandBean columnDataBean = null;
	   	for(int n=0;n<resultList.size();n++){
	   		rowTableHtml = new StringBuffer("");
	   		rowItem = (StockOnhandBean)resultList.get(n);
	   		if(n%2==0){ 
	   			tabclass="lineO";
	   		}else{
	   			tabclass ="lineE";
	   		}
	   	
	   		rowTableHtml.append("<tr class="+tabclass+">");
	   		 	//1) Row by pd  ,column product
	   		    if(dispType.equals("1")){
	   		       rowTableHtml.append("<td class='"+td_text_center+"' width='"+column_1+"%'>"+rowItem.getPdCode()+"</td> \n");
	   		       rowTableHtml.append("<td class='"+td_text+"' width='"+column_2+"%'>"+rowItem.getPdDesc()+"</td> \n");
	   		       if(pdType.equalsIgnoreCase("P") && dispType.equals("1")){
	   		    	  rowTableHtml.append("<td class='"+td_text+"' width='"+column_2+"%'>"+rowItem.getProvince()+"</td> \n");
 				   }
	   		    }else{ 
	   			 //<!--2)  Row by product  ,column pd--> 
	   			   rowTableHtml.append("<td class='"+td_text_center+"' width='"+column_1+"%'>"+rowItem.getProductCode()+"</td> \n");
	   			   rowTableHtml.append("<td class='"+td_text+"' width='"+column_2+"%'>"+rowItem.getProductName()+"</td> \n");
	   			} 
	   			 //<!--  For By Column Select List-->
	   		        if(rowItem.getRowColumnDataList() != null && rowItem.getRowColumnDataList() .size()>0){ 
	   		        	for(int c=0;c<rowItem.getRowColumnDataList().size();c++){
	   		        		columnDataBean = (StockOnhandBean)rowItem.getRowColumnDataList().get(c);
	   		        		
	   		        		//1) Row by pd  ,column product
	   		        		if(dispType.equals("1")){
	   		        			// 1.1) no plan,no price test=pass
	   		        			if(dispPlan==false && dispPrice==false){ 
	   		        				//check qty <> 0 to show
	   		        			    if(dispHaveQty){
		   	                            if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdQty()) != 0){
		   		        				   rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td> \n");
		   		        				   //for check all column in row qty <>0
		   		                           totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   	                             }
	   		        			    }else{
	   		        			    	rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td> \n");
		   		        				//for check all column in row qty <>0
		   		                        totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	   		        			    }
		   	                      // 1.2) display plan,no price test=pass
	   		        			  }else if( dispPlan==true && dispPrice==false){ 
	   		        				   //check qty <> 0 to show
		   		        				if(dispHaveQty){
			                                  if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdQty()) != 0
			                       	          || Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdIntQty()) != 0 ){
			   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td> \n");
			   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntQty())+"</td> \n");
			   		        				
			   		        				    //for check all column in row qty <>0
			   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
			   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
			                                  }
		   		        				}else{
		   		        					rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td> \n");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntQty())+"</td> \n");
		   		        				
		   		        				    //for check all column in row qty <>0
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		   		        				}
					              // 1.3) no plan ,display price  test=
	   		   		        	  }else if(dispPlan==false && dispPrice==true){ 
	   		   		        	      //check qty <> 0 to show
		   		        				if(dispHaveQty){
			                                  if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdQty()) != 0
			                       	          || Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdIntQty()) != 0 ){
			   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td> \n");
			   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdPrice())+"</td> \n");
			   		        				
			   		        				    //for check all column in row qty <>0
			   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
			   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
			                                  }
		   		        				}else{
		   		        					rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td> \n");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdPrice())+"</td> \n");
		   		        				
		   		        				    //for check all column in row qty <>0
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		   		        				}	
						          // 1.4) Display plan ,display price  test=
	   		   		        	  }else if( dispPlan==true && dispPrice==true){ 
		   		        				if(dispHaveQty){ //check qty <> 0 to show
			                                  if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdQty()) != 0
			                       	          || Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getProductCode()).getPdIntQty()) != 0 ){
			   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td> \n");
			   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdPrice())+"</td> \n");
			   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntQty())+"</td> \n");
			   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntPrice())+"</td> \n");
			   		        				    
			   		        				    //for check all column in row qty <>0
			   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
			   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
			   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
			   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntPrice());
			                                  }
		   		        				}else{
		   		        					rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td> \n");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdPrice())+"</td> \n");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntQty())+"</td> \n");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntPrice())+"</td> \n");
		   		        				   
		   		        				    //for check all column in row qty <>0
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntPrice());
		   		        				}		
	   		   		        	  }//if
	   		        		}else{
	   		        			// 2)Row by product  ,column pd
	   		        		   // 2.1) no plan,no price test=pass
	   		        			if(dispPlan==false && dispPrice==false){
		   		        			if(dispHaveQty){//check qty <> 0 to show
		   	                            if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getPdCode()).getPdQty()) != 0){
		   		        				   rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td>");
		   		        				   //for check all column in row qty <>0
		   		                           totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   	                            }
		   		        			}else{
	   		        				   rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td>");
	   		        				   //for check all column in row qty <>0
	   		                           totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   		        			}
	   		        			// 2.2) display plan,no price test=pass
	   		        			}else if( dispPlan==true && dispPrice==false){ 
	   		        				  if(dispHaveQty){
		   		        				  if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getPdCode()).getPdQty()) != 0
		                       	           || Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getPdCode()).getPdIntQty()) != 0 ){
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td>");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntQty())+"</td>");
		   		        				   
		   		        				   //for check all column in row qty <>0
		   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		   		        				  }
		   		        				}else{
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td>");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntQty())+"</td>");
		   		        				   
		   		        				   //for check all column in row qty <>0
		   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		   		        				}
	   		        			// 2.3) no plan ,display price  test=
	   		        			}else if(dispPlan==false && dispPrice==true){ 
	   		        				if(dispHaveQty){
		   		        				  if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getPdCode()).getPdQty()) != 0){
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td>");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdPrice())+"</td>");
		   		        				   
		   		        				   //for check all column in row qty <>0
		   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   		        				  }
		   		        				}else{
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td>");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdPrice())+"</td>");
		   		        				   
		   		        				   //for check all column in row qty <>0
		   		                        	totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   		        				}
	   		        			// 2.4) display plan ,display price  test=
	   		        			}else if( dispPlan==true && dispPrice==true){
	   		        				if(dispHaveQty){ //check qty <> 0 to show
		                                  if( Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getPdCode()).getPdQty()) != 0
		                       	          || Utils.convertStrToDouble(summaryByColumnMap.get(columnDataBean.getPdCode()).getPdIntQty()) != 0 ){
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td>");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdPrice())+"</td>");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntQty())+"</td>");
		   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntPrice())+"</td>");
		   		        				    
		   		        				    //for check all column in row qty <>0
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
		   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntPrice());
		                                  }
	   		        				}else{
	   		        					rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdQty())+"</td>");
	   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdPrice())+"</td>");
	   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntQty())+"</td>");
	   		        				    rowTableHtml.append("<td class='"+td_number+"' width='"+width+"%'>"+Utils.isNullDoubleStrToZero(columnDataBean.getPdIntPrice())+"</td>");
	   		        				   
	   		        				    //for check all column in row qty <>0
	   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdQty());
	   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdPrice());
	   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntQty());
	   	                        	    totalRowQty +=Utils.convertStrToDouble2Digit(columnDataBean.getPdIntPrice());
	   		        				}//if		
 		   		        	   }//if in case 2
	   		        	 }//if case 2
	   		        }//if
	   		 }//for 2
	   		         
	   		 // <!--  For By Column List-->
	   		 rowTableHtml.append("</tr>"); 
	   		          
	   		// System.out.println("totalRowQty["+totalRowQty+"]");
	   		 /**Show Row is sum Row Qty != 0 **/
	   		 if(dispHaveQty){
		   		 if(totalRowQty != 0){
		   			 out.append(rowTableHtml.toString());
		   		 }
	   		 }else{
	   			out.append(rowTableHtml.toString()); 
	   		 }
	   		 totalRowQty=0;         		 
	   	}//for row
	   	
	  /**************** Summary ***************************************************************/
	   	out.append("<tr class='row_hilight'>"); 
	    if(pdType.equalsIgnoreCase("P") && dispType.equals("1")){
	   	   out.append("<td width='"+(column_1+column_2+column_2)+"%' colspan='3' align='right'>ยอดรวม</td> \n");
	    }else{
	       out.append("<td width='"+(column_1+column_2)+"%' colspan='2' align='right'>ยอดรวม</td> \n"); 
	    }
	   	if(columnList != null && columnList.size() >0){
	        for(int k=0;k<columnList.size();k++){
	              StockOnhandBean s = (StockOnhandBean)columnList.get(k);
	              //1 Row by pd  ,column product
	          		if(dispType.equals("1")){
	          		   // 1.1) no plan,no price test=pass
	          			if(dispPlan==false && dispPrice==false){
	          			   sumBean = summaryByColumnMap.get(s.getProductCode());
		          		   if(dispHaveQty){ //check qty <> 0 to show
		                      if( Utils.convertStrToDouble(sumBean.getPdQty()) != 0){
		          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
		                      }
		          		   }else{
		          			   logger.debug("producCode["+s.getProductCode()+"]sumBean.getPdQty():"+sumBean.getPdQty());
		          			   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n"); 
		          		   }
	          			// 1.2) display plan,no price test=pass
	          			}else if( dispPlan==true && dispPrice==false){ 
	          			   sumBean = summaryByColumnMap.get(s.getProductCode());
		          		   if(dispHaveQty){ //check qty <> 0 to show
		                      if( Utils.convertStrToDouble(sumBean.getPdQty()) != 0
		                    	|| Utils.convertStrToDouble(sumBean.getPdIntQty()) != 0){
		          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
		          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td> \n");
		                      }
		          		   }else{
		          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n"); 
		          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td> \n"); 
		          		   }
	          			// 1.3) no plan ,display price  test=
	          			 }else if(dispPlan==false && dispPrice==true){ 
	          				   sumBean = summaryByColumnMap.get(s.getProductCode());
			          		   if(dispHaveQty){ //check qty <> 0 to show
			                      if( Utils.convertStrToDouble(sumBean.getPdQty()) != 0){
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdPrice(),Utils.format_current_2_disgit)+"</td> \n");
			                      }
			          		   }else{
			          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n"); 
			          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdPrice(),Utils.format_current_2_disgit)+"</td> \n"); 
			          		   }
	          			// 1.4) Display plan ,display price  test=
	          			}else if( dispPlan==true && dispPrice==true){
	          				   sumBean = summaryByColumnMap.get(s.getProductCode());
			          		   if(dispHaveQty){ //check qty <> 0 to show
			                      if( Utils.convertStrToDouble(sumBean.getPdQty()) != 0
			                    		|| Utils.convertStrToDouble(sumBean.getPdIntQty()) != 0  ){
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdPrice(),Utils.format_current_2_disgit)+"</td> \n");
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td> \n");
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntPrice(),Utils.format_current_2_disgit)+"</td> \n");
			                      }
			          		   }else{
			          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n"); 
			          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdPrice(),Utils.format_current_2_disgit)+"</td> \n");
			          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td> \n");
		          				  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntPrice(),Utils.format_current_2_disgit)+"</td> \n");
			          		   }
	          			}
	          		}else{
	          			//2) Row by product  ,column pd
	          		   // 2.1) no plan,no price test=pass
	          			if(dispPlan==false && dispPrice==false){
	          				 sumBean = summaryByColumnMap.get(s.getPdCode());
	          				 if(dispHaveQty){//check qty <> 0 to show
	                           if( Utils.convertStrToDouble(sumBean.getPdQty()) != 0){
	          				      out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
	                           }
	          				 }else{
	          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
	          				 }
	          			// 2.2) display plan,no price test=pass
	          			}else if( dispPlan==true && dispPrice==false){ 
	          				 sumBean = summaryByColumnMap.get(s.getPdCode());
	          				 if(dispHaveQty){
			                       if( Utils.convertStrToDouble(sumBean.getPdQty()) != 0
			                        || Utils.convertStrToDouble(sumBean.getPdIntQty()) != 0){
			          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
			          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td> \n");
			                       }
		          			  }else{
		          				  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
		          				  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td> \n");
		          			  }
	          			// 2.3) no plan ,display price  test=
	          			}else if(dispPlan==false && dispPrice==true){ 
	          				 sumBean = summaryByColumnMap.get(s.getPdCode());
	          				 if(dispHaveQty){
			                       if( Utils.convertStrToDouble(sumBean.getPdQty()) != 0){
			          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
			          				   out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdPrice(),Utils.format_current_2_disgit)+"</td> \n");
			                       }
		          			  }else{
		          				  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
		          				  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdPrice(),Utils.format_current_2_disgit)+"</td> \n");
		          			  }
	          			// 2.4) display plan ,display price  test=
	          			}else if( dispPlan==true && dispPrice==true){
	          				   sumBean = summaryByColumnMap.get(s.getPdCode());
			          		   if(dispHaveQty){ //check qty <> 0 to show
			                      if(   Utils.convertStrToDouble(sumBean.getPdQty()) != 0
			                    	 || Utils.convertStrToDouble(sumBean.getPdIntQty()) != 0){
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n");
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdPrice(),Utils.format_current_2_disgit)+"</td> \n");
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td> \n");
			          				 out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntPrice(),Utils.format_current_2_disgit)+"</td> \n");
			                      }
			          		   }else{
			          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdQty(),Utils.format_current_2_disgit)+"</td> \n"); 
			          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdPrice(),Utils.format_current_2_disgit)+"</td> \n");
			          			  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntQty(),Utils.format_current_2_disgit)+"</td> \n");
		          				  out.append("<td class='"+td_number_bold+"'>"+Utils.convertStrDoubleToStr(sumBean.getPdIntPrice(),Utils.format_current_2_disgit)+"</td> \n");
			          		   }
	          			}
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
