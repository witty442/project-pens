<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
      <nav class="sb-topnav navbar navbar-expand navbar-dark bg-dark">
           <a class="navbar-brand" href="mainpage.jsp">PENS </a>
           <button class="btn btn-link btn-sm order-1 order-lg-0" id="sidebarToggle" href="#">
             <i class="fas fa-bars"></i>
           </button>
          
          <!-- Navbar header-->
          <!--  <ul class="navbar-nav ml-auto ml-md-0">
              <li class="nav-item dropdown">
                  <a class="nav-link dropdown-toggle" id="userDropdown" href="#" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fas fa-user fa-fw"></i></a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                    
                      <div class="dropdown-divider"></div>
                      <a class="dropdown-item" href="login.html">Logout</a>
                  </div>
              </li>
          </ul>  -->
         
      </nav>
    
    <div id="layoutSidenav" >
      <div id="layoutSidenav_nav" >
         <!-- Menu -->
         <jsp:include page="menu.jsp"  flush="true"/>
      </div>
      
      <!-- Content -->
      <div id="layoutSidenav_content">
         <main>
	       <div class="container-fluid">
      
