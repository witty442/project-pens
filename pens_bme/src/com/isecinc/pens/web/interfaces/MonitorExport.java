package com.isecinc.pens.web.interfaces;

import com.pens.util.Utils;

public class MonitorExport {
	public static StringBuffer genHTMLCode(String dataStr) {
		  StringBuffer str = new StringBuffer("");
		  try{
			  if(dataStr != null){
				  String[] dataArray = dataStr.split("\n");
				  str.append("<table align='center' border='0' cellpadding='3' cellspacing='1' class='result'>");
				  /***  gen Header ***************/
		          str.append("<tr>"); 
	              String[] columnHeader = dataArray[0].split(",");
	              for(int c=0;c<columnHeader.length;c++){
	            	str.append("<th>");
	            	str.append(columnHeader[c]);
	            	str.append("</th>");
	              }
		          str.append("</tr>");
		          /***  gen Header ***************/
		          if(dataArray != null && dataArray.length > 0){
			          /***  gen Detail ***************/
			          for(int line =1;line<dataArray.length;line++){
			        	  String[] dataLineArray = dataArray[line].split(",");
			        	  String styleClass = (line%2==0)?"lineE":"lineO";
			        	  str.append("<tr class='"+styleClass+"'>");  
			        	  System.out.println("H1:"+dataLineArray.length+":"+columnHeader.length);
			        	 
				          for(int c=0;c<columnHeader.length;c++){
				              str.append("<td>");
				              str.append(c>dataLineArray.length-1?"":dataLineArray[c]);
				              str.append("</td>");
				          }
			        	  
				          str.append("</tr>");
			          }
		          }
		          /***  gen Detail ***************/
				  str.append("</table>");
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return str;
	  }
	  
	  public static StringBuffer genHTMLCodeTransaction(String dataStr) {
		  StringBuffer str = new StringBuffer("");
		  try{
			  if(dataStr != null){
				  String[] dataArray = dataStr.split("\n");
				  str.append("<table align='center' border='0' cellpadding='3' cellspacing='1' class='result'>");
				  /***  gen Header ***************/
		          str.append("<tr>"); 
	              String[] columnHeaderH = dataArray[0].split(",");
	              for(int c=0;c<columnHeaderH.length;c++){
	            	str.append("<th>");
	            	str.append(columnHeaderH[c]);
	            	str.append("</th>");
	              }
		          str.append("</tr>");
		          
		          /***  gen Header Detail***************/
		          if(dataArray != null && dataArray.length > 0){
			          str.append("<tr>"); 
		              String[] columnHeaderD = dataArray[1].split(",");
		              for(int c=0;c<columnHeaderD.length;c++){
		            	str.append("<th>");
		            	str.append(columnHeaderD[c]);
		            	str.append("</th>");
		              }
			          str.append("</tr>");
			          /***  gen Header ***************/
			          
			          /***  gen Detail ***************/
			          for(int line =2;line<dataArray.length;line++){
			        	  //System.out.println("dataLine:"+dataArray[line]);
			        	  String[] dataLineArray = dataArray[line].split(",");
			        	  //System.out.println("dataLineArray length:"+dataLineArray.length);
			        	  String styleClass = (line%2==0)?"lineE":"lineO";
			        	  str.append("<tr class='"+styleClass+"'>"); 
			        	  if(Utils.isNull(dataLineArray[0]).equals("H")){
					          for(int c=0;c<columnHeaderH.length;c++){
					              str.append("<td>");
					              str.append(c>dataLineArray.length-1?"":dataLineArray[c]);
					              str.append("</td>");
					          }
				          }else{

				        	  for(int c=0;c<columnHeaderD.length;c++){
					              str.append("<td>");
					              str.append(c>dataLineArray.length-1?"":dataLineArray[c]);
					              str.append("</td>");
					          }
				        	  
				          }
				          str.append("</tr>");
			          }
			          /***  gen Detail ***************/
		          }
				  str.append("</table>");
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return str;
	  }
	  
}
