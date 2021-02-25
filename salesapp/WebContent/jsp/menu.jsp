<%@page import="com.isecinc.pens.web.batchtask.BatchTaskConstants"%>
<%@page import="com.pens.util.EncyptUtils"%>
<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@page import="com.isecinc.pens.bean.User"%>
<%
  String role = ((User)session.getAttribute("user")).getType();
  User user = (User)session.getAttribute("user");
  
  /** No of menu **/
  int no = 0;
  int subNo = 0;
 // System.out.println("Role:"+user.getRole().getKey());
%>
 <nav class="sb-sidenav accordion sb-sidenav-light" id="sidenavAccordion">
     <div class="sb-sidenav-menu">
         <div class="nav">
             <a class="nav-link" href="${pageContext.request.contextPath}/jsp/customerAction.do?do=prepareCustTrip">
                 <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
                 ร้านค้า
             </a>
             <%if (role.equalsIgnoreCase(User.VAN) ){ %>
	             <a class="nav-link" href="${pageContext.request.contextPath}/jsp/customerAction.do?do=prepare">
	                 <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
	                  ร้านค้าใหม่
	             </a>
	          <%} %>
              <a class="nav-link" href="${pageContext.request.contextPath}/jsp/saleOrderAction.do?do=search&action=new">
	                 <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
	                  ค้นหารายการขาย
	          </a>
	          <a class="nav-link" href="${pageContext.request.contextPath}/jsp/manageOrderReceipt.do">
	                 <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
	                  จัดการ รายการขายและรายการรับเงิน
	          </a>
	          <a class="nav-link" href="${pageContext.request.contextPath}/jsp/pdReceipt.do?do=prepare">
	                 <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
	                  บันทึกการเก็บเงินขายเชื่อ
	          </a>
	          <a class="nav-link" href="${pageContext.request.contextPath}/jsp/transferAction.do?do=search&action=newsearch">
	                 <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
	                  	บันทึกส่งเงินโอนเข้าบริษัท
	          </a>
             <%if ( role.equalsIgnoreCase(User.TT) ){ %>
                 <!-- ************ TT ************ -->
                  <a class="nav-link" href="${pageContext.request.contextPath}/jsp/receiptAction.do?do=search&action=new">
	                 <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
	                  ค้นหารายการรับเงิน
	             </a>
                 <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapsePages" aria-expanded="false" aria-controls="collapsePages">
                   <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
                    Interfaces
                   <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
                </a>
                <div class="collapse" id="collapsePages" aria-labelledby="headingTwo" data-parent="#sidenavAccordion">
                    <nav class="sb-sidenav-menu-nested nav accordion" id="sidenavAccordionPages">
                     <a class="nav-link" href="${pageContext.request.contextPath}/jsp/batchTaskAction.do?do=prepare&pageAction=new&pageName=<%=BatchTaskConstants.EXPORT_RECEIPT%>">
                        <bean:message key="<%=BatchTaskConstants.EXPORT_RECEIPT%>" bundle="sysprop"/>
                     </a>
                    </nav>
                </div>
            <%}else if (role.equalsIgnoreCase(User.VAN) ){ %>
                 <!-- ************ VAN ************ -->
                
            <%} %>
             
             <!-- ************ ADMIN ***************** -->
             <%if ( role.equalsIgnoreCase(User.ADMIN) ){ %>
               <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapsePages" aria-expanded="false" aria-controls="collapsePages">
                  <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
                  Administrator
                  <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
               </a>
               <div class="collapse" id="collapsePages" aria-labelledby="headingTwo" data-parent="#sidenavAccordion">
                  <nav class="sb-sidenav-menu-nested nav accordion" id="sidenavAccordionPages">
                      <a class="nav-link" href="${pageContext.request.contextPath}/jsp/searchTask.do?do=prepare&action=new">
                          <bean:message bundle="sysprop" key="searchTask"/>
                      </a>
                      <a class="nav-link" href="${pageContext.request.contextPath}/jsp/schedule.do?do=prepare&action=new">
                          <bean:message bundle="sysprop" key="scheduler"/>
                      </a>
                  </nav> 
               </div>
            <%} %>
            
             <!-- Logout -->
	        <a class="nav-link" href="${pageContext.request.contextPath}/login.do?do=logoff">
	           <div class="sb-nav-link-icon"><i class="fas fa-sign-out-alt"></i></div>
	           Logout
	        </a>
        </div>  
        
        <div class="sb-sidenav-footer">
         <div class="small">ชื่อผู้ใช้งาน:</div>
           <%=user.getCode() %>-<%=user.getRole().getName()%>
         </div>
     </div>
 </nav>

  