package com.pens.util;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PageingBean;

public class PageingGenerate {
	protected static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * For Generate Paging By page SQL 
	 * @param totalPage
	 * @param totalRecord
	 * @param currPage
	 * @param startRec
	 * @param endRec
	 * @param no (start count record)
	 * @return
	 */
	public static StringBuffer genPageing(int totalPage,int totalRecord,int currPage, int startRec,int endRec,int no){
		StringBuffer h = new StringBuffer("");
		int showNextPage = 5;
		try{
			  h.append("<div align='left' class='pagination'>\n");
			  h.append("<span>รายการทั้งหมด  "+totalRecord +" รายการ , หน้า "+currPage +"/"+totalPage +"</span>\n");
			  h.append("<a href=javascript:gotoPage('1')\n");  
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
			 int startPagePre = 0;
			 int endPagePre = 0;
			 if(moreThan10Page){
				 if(currPage==1){
					 startPagePre = 0;
					 endPagePre = 10;
				 }else if(currPage==totalPage){
					 startPagePre = currPage-showNextPage;
					 endPagePre = currPage;
				 }else{
					 startPagePre = currPage-showNextPage;
					 endPagePre = currPage;
					 if(startPagePre < 0){
						 //bug wait think
						 startPagePre  = 0;
					 }
				 }
				 diffRang = endPagePre-startPagePre;
			 }else{
				 //normal
				 startPagePre = 0;
				 endPagePre = totalPage;
			 }
			 //logger.debug("startPagePre:"+startPagePre);
			 //logger.debug("endPagePre:"+endPagePre);
			 
			 for(int r=startPagePre;r<endPagePre;r++){
				if((r+1) != currPage){
			      h.append("<a href=javascript:gotoPage('"+(r+1)+"')");  
			      h.append(" title='Go to page "+(r+1)+"'>"+(r+1) +"</a>");
			    }else{ 
			      h.append("<!-- Active Current Page -->");
			      h.append("<a href='#' class='active'>"+(currPage) +"</a>");
		        }//if
			 }//for%>	
				 
			//calc start lastFix by currPage
			 int startPagePos = 0;
			 int endPagePos = 0;
			 if(moreThan10Page){
				 if(currPage==1){
					 startPagePos = currPage;
					 endPagePos = currPage;
				 }else if(currPage==totalPage){
					 startPagePos = currPage;
					 endPagePos = currPage;
				 }else{
					 startPagePos = currPage;
					 endPagePos = currPage +showNextPage;
					 if(endPagePos > totalPage){
						 startPagePos = totalPage-showNextPage;
						 endPagePos = totalPage;
						 
						 //check startPagePos no collapse in Pre(startPage -endPage)
						// logger.debug("Case Test ");
						// logger.debug("startPagePre:"+startPagePre+",endPagePre:"+endPagePre);
						// logger.debug("startPagePos:"+startPagePos);
						 
						 if(startPagePos >= startPagePre && startPagePos <= endPagePre){
							 startPagePos = endPagePre;
						 }
						 
					 }else if(endPagePos-startPagePos < showNextPage){//diff <5(showNextPage)
						 endPagePos += (endPagePos-startPagePos);//add diff
					 }
					 
					 //logger.debug("1)diffRang:"+diffRang);
					//diffRang < 10 add 
					 diffRang += endPagePos-startPagePos;
					 //logger.debug("2)diffRang:"+diffRang);
					 
					 if(diffRang <=10){
						 endPagePos += 10-diffRang;
						 //case endPagePos > totalPage set endPagePos=totalPage
						 endPagePos =endPagePos>totalPage?totalPage:endPagePos;
					 }
				 } 
			 }//if
			//logger.debug("startPagePos:"+startPagePos);
			//logger.debug("endPagePos:"+endPagePos);
			 
			for(int r=startPagePos;r<endPagePos;r++){
			     h.append("<a href=javascript:gotoPage('"+(r+1)+"') "); 
				 h.append("title='Go to page "+(r+1)+"'>"+(r+1) +"</a>");
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
	
	public static PageingBean genPageingByList(int totalRecord,int pageSize, int currPage){
		PageingBean pageingBean = new PageingBean();
		StringBuffer h = new StringBuffer("");
		int totalPage = 0;
		int startRec = 0;
		int endRec = 0;
		int no =0;
		int showNextPage = 5;
		try{
			//Calc TotalPage 
			totalPage = Utils.calcTotalPage(totalRecord, pageSize);
			//calc startRec endRec
			startRec = ((currPage-1)*pageSize)+1;
			endRec = (currPage * pageSize);
		    if(endRec > totalRecord){
			   endRec = totalRecord;
		    }
		    no = Utils.calcStartNoInPage(currPage, pageSize);
		    
			  h.append("<div align='left' class='pagination'>\n");
			  h.append("<span>รายการทั้งหมด  "+totalRecord +" รายการ , หน้า "+currPage +"/"+totalPage +"</span>\n");
			  h.append("<a href=javascript:gotoPage('1')\n");  
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
			 int startPagePre = 0;
			 int endPagePre = 0;
			 if(moreThan10Page){
				 if(currPage==1){
					 startPagePre = 0;
					 endPagePre = 10;
				 }else if(currPage==totalPage){
					 startPagePre = currPage-showNextPage;
					 endPagePre = currPage;
				 }else{
					 startPagePre = currPage-showNextPage;
					 endPagePre = currPage;
					 if(startPagePre < 0){
						 //bug wait think
						 startPagePre  = 0;
					 }
				 }
				 diffRang = endPagePre-startPagePre;
			 }else{
				 //normal
				 startPagePre = 0;
				 endPagePre = totalPage;
			 }
			 logger.debug("startPagePre:"+startPagePre);
			 logger.debug("endPagePre:"+endPagePre);
			 
			 for(int r=startPagePre;r<endPagePre;r++){
				if((r+1) != currPage){
			      h.append("<a href=javascript:gotoPage('"+(r+1)+"')");  
			      h.append(" title='Go to page "+(r+1)+"'>"+(r+1) +"</a>");
			    }else{ 
			      h.append("<!-- Active Current Page -->");
			      h.append("<a href='#' class='active'>"+(currPage) +"</a>");
		        }//if
			 }//for%>	
				 
			//calc start lastFix by currPage
			 int startPagePos = 0;
			 int endPagePos = 0;
			 if(moreThan10Page){
				 if(currPage==1){
					 startPagePos = currPage;
					 endPagePos = currPage;
				 }else if(currPage==totalPage){
					 startPagePos = currPage;
					 endPagePos = currPage;
				 }else{
					 startPagePos = currPage;
					 endPagePos = currPage +showNextPage;
					 if(endPagePos > totalPage){
						 startPagePos = totalPage-showNextPage;
						 endPagePos = totalPage;
						 
						 //check startPagePos no collapse in Pre(startPage -endPage)
						 logger.debug("Case Test ");
						 logger.debug("startPagePre:"+startPagePre+",endPagePre:"+endPagePre);
						 logger.debug("startPagePos:"+startPagePos);
						 
						 if(startPagePos >= startPagePre && startPagePos <= endPagePre){
							 startPagePos = endPagePre;
						 }
						 
					 }else if(endPagePos-startPagePos < showNextPage){//diff <5(showNextPage)
						 endPagePos += (endPagePos-startPagePos);//add diff
					 }
					 
					 logger.debug("1)diffRang:"+diffRang);
					//diffRang < 10 add 
					 diffRang += endPagePos-startPagePos;
					 logger.debug("2)diffRang:"+diffRang);
					 
					 if(diffRang <=10){
						 endPagePos += 10-diffRang;
						 //case endPagePos > totalPage set endPagePos=totalPage
						 endPagePos =endPagePos>totalPage?totalPage:endPagePos;
					 }
				 } 
			 }//if
			logger.debug("startPagePos:"+startPagePos);
			logger.debug("endPagePos:"+endPagePos);
			 
			for(int r=startPagePos;r<endPagePos;r++){
			     h.append("<a href=javascript:gotoPage('"+(r+1)+"') "); 
				 h.append("title='Go to page "+(r+1)+"'>"+(r+1) +"</a>");
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
			
		  //set return
		  pageingBean.setCurrPage(currPage);
		  pageingBean.setTotalPage(totalPage);
		  pageingBean.setStartRec(startRec);
		  pageingBean.setEndRec(endRec);
		  pageingBean.setNo(no);
		  
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return pageingBean;
	}
}
