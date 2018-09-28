<%@page import="com.isecinc.pens.bean.MTTBean"%>
<%@page import="com.pens.util.*"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://displaytag.sf.net" prefix="display" %>

<jsp:useBean id="mttForm" class="com.isecinc.pens.web.mtt.MTTForm" scope="session" />
<%
String screenWidth = Utils.isNull(session.getAttribute("screenWidth"));
String screenHeight = Utils.isNull(session.getAttribute("screenHeight"));

String totalPage = "";
String currentPage = "";
if(mttForm.getResultsSearch() != null){ 
	 totalPage = String.valueOf(Utils.calcTotalPage(mttForm.getResultsSearch().size(),50));
	 
	String queryStr= request.getQueryString();
	if(queryStr.indexOf("d-") != -1){
		queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
		System.out.println("queryStr:"+queryStr);
		currentPage = request.getParameter(queryStr);
	}
}
/** Case ToatlPage ==1 set currentPage=1 ***/
if(totalPage.equals("1")){
	currentPage ="1";
}
String totalQty = "";
if(session.getAttribute("summary") != null){
	MTTBean s = (MTTBean)session.getAttribute("summary");
	if(s != null){
		totalQty = s.getTotalQty()+"";
	}
}

System.out.println("totalPage:"+totalPage);
System.out.println("currentPage:"+currentPage);
%>
<html>
<head>
<style>
#scroll {
    width:<%=screenWidth%>px;

    background:#A3CBE0;
	border:1px solid #000;
	overflow:auto;
	white-space:nowrap;
	box-shadow:0 0 25px #000;
	}
</style>
</head>
     <c:if test="${mttForm.resultsSearch != null}">
    							
		<br/>
		<div id ="scroll" >
			<display:table style="width:100%;" id="item" name="sessionScope.mttForm.resultsSearch"
			 defaultsort="0" defaultorder="descending" class="resultDisp"
			 requestURI="#" sort="list" pagesize="50">	
			 
			    <display:setProperty name="export.pdf" value="true" />
                <display:setProperty name="export.excel.filename" value="ActorDetails.xls" />
		        <display:setProperty name="export.pdf.filename" value="ActorDetails.pdf" />
		        <display:setProperty name="export.csv.filename" value="ActorDetails.csv" />
		
			    <display:column  title="No" property="no"  sortable="true" class="td_text" style="width:10%" />
			    <display:column  title="วันที่ขาย" property="saleDate"  sortable="true" class="td_text"  style="width:10%" />	 
			    <display:column  title="วันที่ Scan" property="createDate"  sortable="true" class="td_text"  style="width:10%"  />	
			    <display:column  title="Doc No" property="docNo"  sortable="true" class="td_text"  style="width:10%"  />	
			    <display:column  title="กลุ่ม" property="custGroup"  sortable="true" class="td_text"  style="width:10%"  />	
			    <display:column  title="รหัสกลุ่ม" property="custGroupName"  sortable="true" class="td_text"  style="width:10%"  />
			    <display:column  title="รหัสร้านค้า" property="storeCode"  sortable="true" class="td_text"  style="width:10%"  />	
			    <display:column  title="ชื่อร้านค้า" property="storeName"  sortable="true" class="td_text"  style="width:10%"  />	
			    <display:column  title="Barcode " property="barcode"  sortable="true" class="td_text"  style="width:10%"  />	
			    <display:column  title="Group Code" property="groupCode"  sortable="true" class="td_text"  style="width:10%"  />	
			    <display:column  title="Material Master" property="materialMaster"  sortable="true" class="td_text"  style="width:10%"  />
			    <display:column  title="Pens Item" property="pensItem"  sortable="true" class="td_text"  style="width:10%"  />
			    <display:column  title="จำนวนชิ้นที่ขาย" property="qty"  sortable="true" class="td_number"  style="width:10%"  />
			    <display:column  title="ราคาปลีกก่อน Vat" property="retailPriceBF"  sortable="true" class="td_number"  style="width:10%"  />
			    <display:column  title="Remark" property="remark"  sortable="true" class="td_text"  style="width:10%"  />

			     <%if(currentPage.equals(totalPage)){ %>
			       <display:footer>
					  <tr>
					   <td class=""></td> 
					   <td class=""></td> 
					   <td class=""></td>
				       <td class=""></td>
				       <td class=""></td>
				       <td class=""></td>
				       <td class=""></td>
				       <td class=""></td>
				       <td class=""></td>
				       <td class=""></td>
				       <td class=""></td>
				      
					   <td class="td_number_bold" align="right">
						  <B> Total </B>
						</td>
						<td class="td_number_bold" ><B>  <span id="totalQty"></span>  </B></td>
						<td class=""></td>
					 </tr>
				</display:footer> 
			     <%} %>
			</display:table>
	   </div>
    </c:if>
    
   <%if(currentPage.equals(totalPage)){ %>
	<script>
	  document.getElementById("totalQty").innerHTML = '<%=totalQty%>';
	</script>
<%} %>
</body>
</html>