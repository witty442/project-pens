<%@ page language="java" contentType="text/html; charset=TIS-620"
    pageEncoding="TIS-620"%>

<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

	<tr align="center">
	  <td   class="tblFooter_Center" colspan="${headerCount}">
		<!--a class="CursorHand" >หน้าแรก &nbsp;</a-->
		<c:if test="${pageKey.hasFirst eq true}">
			<a href="#" onclick="pageKeyTodo('first');" class="CursorHand" >หน้าแรก &nbsp;</a>
		</c:if>
		<c:if test="${pageKey.hasFirst eq false}">
			หน้าแรก &nbsp;
		</c:if>

		<!--a class="CursorHand" >ก่อนหน้า &nbsp;</a-->
		<c:if test="${pageKey.hasPrevious eq true}">
			<a href="#" onclick="pageKeyTodo('previous');" class="CursorHand" >ก่อนหน้า &nbsp;</a>
		</c:if>
		<c:if test="${pageKey.hasPrevious eq false}">
			ก่อนหน้า &nbsp;
		</c:if>

		<!--a class="CursorHand" >ถัดไป &nbsp;</a-->
		<c:if test="${pageKey.hasNext eq true}">
			<a href="#" onclick="pageKeyTodo('next');" class="CursorHand" >ถัดไป &nbsp;</a>
		</c:if>
		<c:if test="${pageKey.hasNext eq false}">
			ถัดไป &nbsp;
		</c:if>

		<!--a class="CursorHand" >หน้าสุดท้าย</a-->
		<c:if test="${pageKey.hasLast eq true}">
			<a href="#" onclick="pageKeyTodo('last');" class="CursorHand" >หน้าสุดท้าย</a>
		</c:if>
		<c:if test="${pageKey.hasLast eq false}">
			หน้าสุดท้าย
		</c:if>

	  </td>
	</tr>

<script language="javascript" type="text/javascript">
	function pageKeyTodo(command){
		var form = document.forms['${formName}'];
		//form.command.value = command;
		form.action="${actionName}?do="+command;	
		form.pageObjKey.value = '${pageKey}';
		form.submit();
		openInfoProgress();
	}

	/*function pageKeyGotoPage() {
		var form = document.forms['${formName}'];
		//form.command.value = 'gotoPage';
		form.action="${pageContext.request.contextPath}/jsp/province.do?do=gotoPage";	
		form.pageObjKey.value = '${pageKey}';
		var currentPageField = document.getElementById('${pageKey}textfield3');
		var currentPageValue = currentPageField.value;
		if(currentPageValue==null||currentPageValue == 'undefind' || currentPageValue.length == 0 || currentPageValue < 1){
			currentPageValue = 1;
		}
		var goPage = currentPageValue;
		form.currentPage.value = goPage;
		form.submit();
		//window.print();
	}*/
</script>