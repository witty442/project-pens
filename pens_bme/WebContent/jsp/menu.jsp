<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>


<ul id="nav">

	<li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp';"><span>Stock Onhand B'me </span></a>
		<ul>
		    <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/importAction.do?do=prepare&page=master&action=new';">1.<span><bean:message bundle="sysprop" key="ImportBMEMaster"/></span></a>
			</li>
           	<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/importAction.do?do=prepare&page=onhand&action=new';">2.<span><bean:message bundle="sysprop" key="ImportBMEFromWacoal"/></span></a>
			</li>
		    <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/importAction.do?do=prepare&page=lotus&action=new';">3.<span><bean:message bundle="sysprop" key="ImportBMEFromLotus"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/importAction.do?do=prepare&page=bigc&action=new';">4.<span><bean:message bundle="sysprop" key="ImportBMEFromBigC"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/importAction.do?do=prepare&page=tops&action=new';">5.<span><bean:message bundle="sysprop" key="ImportBMEFromTops"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/importAction.do?do=prepare&page=physical&action=new';">6.<span><bean:message bundle="sysprop" key="ImportBMEPhysical"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/importAction.do?do=prepare&page=return_wacoal&action=new';">7.<span><bean:message bundle="sysprop" key="ImportReturnWacoal"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/masterAction.do?do=prepare&action=new&page=master';">8.<span><bean:message bundle="sysprop" key="MaintainMaster"/></span></a>
			</li>
		</ul>
	</li>

    <li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp';"><span>Report</span></a>
		<ul>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new&page=onhand';"><span>1.<bean:message bundle="sysprop" key="SummaryBMEFromWacoal"/></span></a>
			</li>
			  <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new&page=onhandLotus';"><span>2.<bean:message bundle="sysprop" key="SummaryBMEOnhandLotus"/></span></a>
			</li>  
			 <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new&page=lotus';"><span>3.<bean:message bundle="sysprop" key="SummaryBMEFromLotus"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new&page=BigC';"><span>4.<bean:message bundle="sysprop" key="SummaryBMEFromBigC"/></span></a>
			</li>
					 <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new&page=tops';"><span>5.<bean:message bundle="sysprop" key="SummaryBMEFromTops"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new&page=onhandBigC';"><span>6.<bean:message bundle="sysprop" key="SummaryBMEOnhandBigC"/></span></a>
			</li>  
			 <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new&page=onhandLotusPeriod';"><span>7.<bean:message bundle="sysprop" key="SummaryBMEOnhandLotusPeriod"/></span></a>
			</li> 
			<%-- <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new&page=physical';"><span><bean:message bundle="sysprop" key="SummaryBMEFromPhysical"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/summaryAction.do?do=prepare&action=new&page=diff_stock';"><span><bean:message bundle="sysprop" key="SummaryBMEDiffStock"/></span></a>
			</li> --%>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/reportAction.do?do=prepare&action=new';"><span>8.<bean:message bundle="sysprop" key="Report"/></span></a>
			</li>
		</ul>
	</li>
	
	 <li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp';"><span>Order</span></a>
		<ul>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/orderAction.do?do=prepare&action=new';"><span><bean:message bundle="sysprop" key="Order"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/orderAction.do?do=prepareView&action=new';"><span><bean:message bundle="sysprop" key="OrderInquiry"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/orderAction.do?do=prepareHistory&action=new';"><span><bean:message bundle="sysprop" key="OrderInquiryHistory"/></span></a>
			</li>
		</ul>
	</li>
	 <li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp';"><span>Transaction</span></a>
		<ul>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/adjustStockAction.do?do=prepare2&action=new';"><span>1.<bean:message bundle="sysprop" key="adjustStock"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/adjustStockSAAction.do?do=prepare2&action=new';"><span>2.<bean:message bundle="sysprop" key="adjustStockSA"/></span></a>
			</li>
		</ul>
	</li>
	<%-- <li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp';"><span>OLD</span></a>
		<ul>
			 <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/pickStockAction.do?do=prepare2&action=new';"><span>dd6.<bean:message bundle="sysprop" key="pickStock"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/renewBoxAction.do?do=prepare2&action=new';"><span>dd7.<bean:message bundle="sysprop" key="renewBox"/></span></a>
			</li>   
		</ul>
	</li> --%>
	<li><a href="#" class="parent" onclick="window.location='${pageContext.request.contextPath}/jsp/mainpage.jsp';"><span>Pick</span></a>
		<ul>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/jobAction.do?do=prepare2&action=new';"><span>1.<bean:message bundle="sysprop" key="job"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/barcodeAction.do?do=prepare2&action=new';"><span>2.<bean:message bundle="sysprop" key="scanBarcode"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/moveWarehouseAction.do?do=prepare&action=new';"><span>3.<bean:message bundle="sysprop" key="moveWarehouse"/></span></a>
			</li>
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/reqFinishAction.do?do=prepare2&action=new';"><span>4.<bean:message bundle="sysprop" key="reqFinish"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/confFinishAction.do?do=prepare2&action=new';"><span>5.<bean:message bundle="sysprop" key="confFinish"/></span></a>
			</li> 
			 <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/stockQueryAction.do?do=prepare&action=new';"><span>7.<bean:message bundle="sysprop" key="stockQuery"/></span></a>
			</li>
			 <li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/reqPickStockAction.do?do=prepare2&action=new';"><span>8.<bean:message bundle="sysprop" key="reqPickStock"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/confPickStockAction.do?do=prepare2&action=new';"><span>9.<bean:message bundle="sysprop" key="confPickStock"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/reqReturnWacoalAction.do?do=prepare2&action=new';"><span>10.<bean:message bundle="sysprop" key="reqReturnWacoal"/></span></a>
			</li> 
			<li>
				<a href="#" onclick="window.location='${pageContext.request.contextPath}/jsp/confirmReturnWacoalAction.do?do=prepare2&action=new';"><span>11.<bean:message bundle="sysprop" key="confirmReturnWacoal"/></span></a>
			</li>
			
			
		</ul>
	</li>
</ul>
   