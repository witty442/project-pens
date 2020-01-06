<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.pens.util.Utils"%>
<% 
	   int totalPage = Utils.convertStrToInt(request.getParameter("totalPage"));
	   int totalRecord = Utils.convertStrToInt(request.getParameter("totalRecord"));
	   int currPage =  Utils.convertStrToInt(request.getParameter("currPage"));
	   int startRec = Utils.convertStrToInt(request.getParameter("startRec"));
	   int endRec = Utils.convertStrToInt(request.getParameter("endRec"));
	%>
	<!-- Display 10 page  -->
	<div align="left" class="pagination">
	     <span>รายการทั้งหมด  <%=totalRecord %> รายการ , หน้า <%=currPage %>/<%=totalPage %></span>
	       <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(1)%>')"  
			       title="Go to page <%=(1)%>"> หน้าแรก</a>
		
		 <!-- Prev Page (currPage-1) < 1 No Action -->
		  <%if(currPage-1 <= 1){ %>
	         <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(currPage==1?1:currPage-1)%>')"  
			       title="Go to page <%=(currPage==1?1:currPage-1)%>"> &laquo;</a>
		  <%}else{ %>
		       <a href="#'" title="Is First Page">&laquo;</a>
		  <%} %>
		 
		 <% 
		  int showPage = 10;
	      boolean moreThan10Page = false;
	      int diffRang = 0;
		  if(totalPage >showPage){
			 moreThan10Page = true;
		  }else{
			 showPage = totalPage;
		  }
			  System.out.println("currPage:"+currPage+",totalPage:"+totalPage);
			  
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
			 System.out.println("startPageP:"+startPageP);
			 System.out.println("endPageP:"+endPageP);
			 
			 for(int r=startPageP;r<endPageP;r++){
				if((r+1) != currPage){
			 %>
			      <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  
			      title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
			<%  }else{ %>	
			      <!-- Active Current Page -->
		          <a href="#" class="active"><%=(currPage) %></a>
		     <% 
		         }//if
			 }//for%>	
			 
			 <% 
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
			 %>
			      <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(r+1)%>')"  
			      title="Go to page <%=(r+1)%>"> <%=(r+1) %></a>
		     <% 
			 }//for
			 %>	
			 
		
		
		<!-- Next Page (currPage+1) >totalPage No Action -->
		  <%if(currPage+1 <= totalPage){ %>
	          <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(currPage+1)%>')"  
			       title="Go to page <%=(currPage+1)%>">&raquo;</a>
		  <%}else{ %>
		       <a href="#'" title="Is Last Page">&raquo;</a>
		  <%} %>
		  
	       <a href="javascript:gotoPage('${pageContext.request.contextPath}','<%=(totalPage)%>')"  
			       title="Go to page <%=(totalPage)%>"> หน้าสุดท้าย</a>	
			      
	    <!--   More Page Jump To -->
		<%if(moreThan10Page) {%>
		    <span> หน้าอื่นๆ:
		    <select id="selectPageG" onchange="selectPageFunctionG('${pageContext.request.contextPath}',this)">
		      <option value=""></option>
		      <% for(int r=0;r<totalPage;r++){ %>
		        <option value="<%=r+1%>"><%=r+1%></option>
		     <%} %>
		    </select>
		    </span>
		 <%} %>
	</div>
			
    <script>
     function selectPageFunctionG(path,selectPageG){
    	gotoPage(path,selectPageG.value);
     }
    </script>		