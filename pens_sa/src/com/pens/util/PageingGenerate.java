package com.pens.util;

import org.apache.log4j.Logger;

public class PageingGenerate {
	protected static Logger logger = Logger.getLogger("PENS");
	
	//Gen Paging
	public static StringBuffer genPageing(int totalPage,int totalRecord,int currPage, int startRec,int endRec,int no){
		StringBuffer h = new StringBuffer("");
		try{
			   h.append("<div align='left' class='pagination'>\n");
			   h.append("<span>รายการทั้งหมด  "+totalRecord +" รายการ , หน้า "+currPage +"/"+totalPage +"</span>\n");
			   h.append("<a href='javascript:gotoPage('1')'\n");  
			   h.append("  title='Go to page 1'> หน้าแรก</a>\n");
				
			   h.append("<!-- Prev Page (currPage-1) < 1 No Action -->\n");
				  if(currPage > 1){ 
					  h.append("<a href=javascript:gotoPage('"+(currPage==1?1:currPage-1)+"') \n");  
					  h.append("title='Go to page "+(currPage==1?1:currPage-1)+"'> &laquo;</a>\n");
				  }else{ 
					  h.append("<a href='#' title='Is First Page'>&laquo;</a>\n");
				  } 
				 
				  int showPage = 10;
			      boolean moreThan10Page = false;
			      int diffRang = 0;
				  if(totalPage >showPage){
					 moreThan10Page = true;
				  }else{
					 showPage = totalPage;
				  }
					 // System.out.println('currPage:'+currPage+',totalPage:'+totalPage);
					  
					 //calc start prefix by currPage
					 int startPageP = 0;
					 int endPageP = 0;
					 if(moreThan10Page){
						 if(currPage==1){
							 startPageP = 0;
							 endPageP = 10;
						 }else if(currPage==totalPage){
							 startPageP = currPage-5;
							 endPageP = currPage;
						 }else{
							 startPageP = currPage-5;
							 endPageP = currPage;
							 if(startPageP < 0){
								 //bug wait think
								 startPageP  = 0;
							 }
						 }
						 diffRang = endPageP-startPageP;
					 }else{
						 //normal
						 startPageP = 0;
						 endPageP = totalPage;
					 }
					 //System.out.println('startPageP:'+startPageP);
					 //System.out.println('endPageP:"+endPageP);
					 
					 for(int r=startPageP;r<endPageP;r++){
						if((r+1) != currPage){
					      h.append("<a href=javascript:gotoPage('"+(r+1)+"')");  
					      h.append(" title='Go to page "+(r+1)+"'> "+(r+1) +"</a>");
					    }else{ 
					      h.append("<!-- Active Current Page -->");
					      h.append("<a href='#' class='active'>"+(currPage) +"</a>");
				        }//if
					 }//for%>	
					 
					//calc start lastfix by currPage
					 int startPageL = 0;
					 int endPageL = 0;
					 if(moreThan10Page){
						 if(currPage==1){
							 startPageL = currPage;
							 endPageL = currPage;
						 }else if(currPage==totalPage){
							 startPageL = currPage;
							 endPageL = currPage;
						 }else{
							 startPageL = currPage;
							 endPageL = currPage +5;
							 if(endPageL > totalPage){
								 startPageL = totalPage-5;
								 endPageL = totalPage;
							 }else if(endPageL-startPageL <5){//diff <5
								 endPageL += (endPageL-startPageL);//add diff
							 }
							 
							//diffRang < 10 add 
							 diffRang += endPageL-startPageL;
							 if(diffRang <=10){
								 endPageL += 10-diffRang;
							 }
						 } 
					 }//if
					 System.out.println("startPageL:"+startPageL);
					 System.out.println("endPageL:"+endPageL);
					 
					 for(int r=startPageL;r<endPageL;r++){
					      h.append("<a href=javascript:gotoPage('"+(r+1)+"') "); 
						  h.append("title='Go to page "+(r+1)+"'> "+(r+1) +"</a>");
				     
					 }//for	
					 
				
				  h.append("<!-- Next Page (currPage+1) >totalPage No Action -->");
				  if(currPage+1 <= totalPage){ 
				      h.append("<a href=javascript:gotoPage('"+(currPage+1)+"') ");
				      h.append("title='Go to page "+(currPage+1)+"'>&raquo;</a>");
				  }else{ 
				       h.append("<a href='#' title='Is Last Page'>&raquo;</a>");
				  } 
				  
				  h.append("<a href=javascript:gotoPage('"+totalPage+"')");  
				  h.append(" title='Go to page "+totalPage+"'> หน้าสุดท้าย</a>	");
					      
				  h.append("<!--   More Page Jump To -->");
				if(moreThan10Page) {
					h.append("<span> หน้าอื่นๆ:");
					h.append("<select id='selectPageG' onchange='selectPageFunctionG(this)'>");
					h.append("<option value=''></option>");
				       for(int r=0;r<totalPage;r++){ 
				    	 h.append("<option value='"+(r+1)+"'>"+(r+1)+"</option>");
				     } 
				    h.append("</select>");
				    h.append("</span>");
				 } 
				/** Jumping Page **/
				h.append("<script>");
			    h.append(" function selectPageFunctionG(selectPageG){");
			    h.append("  gotoPage(selectPageG.value);");
			    h.append(" }");
			    h.append("</script>");
			  h.append("</div>");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
}
