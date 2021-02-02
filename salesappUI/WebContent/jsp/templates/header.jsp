<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
      <nav class="sb-topnav navbar navbar-expand navbar-dark bg-dark">
          <button class="btn btn-link btn-sm order-1 order-lg-0" id="sidebarToggle" href="#"><i class="fas fa-bars"></i></button>
          <a class="navbar-brand" href="index.html">PENS SalesApp</a>
          
          <!-- Navbar-->
          <!-- <ul class="navbar-nav ml-auto ml-md-0">
              <li class="nav-item dropdown">
                  <a class="nav-link dropdown-toggle" id="userDropdown" href="#" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i class="fas fa-user fa-fw"></i></a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                      <a class="dropdown-item" href="#">Settings</a>
                      <a class="dropdown-item" href="#">Activity Log</a>
                      <div class="dropdown-divider"></div>
                      <a class="dropdown-item" href="login.html">Logout</a>
                  </div>
              </li>
          </ul> -->
      </nav>
    
    <div id="layoutSidenav">
      <div id="layoutSidenav_nav">
          <nav class="sb-sidenav accordion sb-sidenav-light" id="sidenavAccordion">
              <div class="sb-sidenav-menu">
                  <div class="nav">
                      <div class="sb-sidenav-menu-heading">������ѡ</div>
                      <a class="nav-link" href="index.html">
                          <div class="sb-nav-link-icon"><i class="fas fa-tachometer-alt"></i></div>
                           �����������
                      </a>
                      <a class="nav-link" href="index.html">
                          <div class="sb-nav-link-icon"><i class="fas fa-tachometer-alt"></i></div>
                           ����������� 2
                      </a>
                      
                      <div class="sb-sidenav-menu-heading">Interface</div>
                      <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseLayouts" aria-expanded="false" aria-controls="collapseLayouts">
                          <div class="sb-nav-link-icon"><i class="fas fa-columns"></i></div>
                          Layouts
                          <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
                      </a>
                      <div class="collapse" id="collapseLayouts" aria-labelledby="headingOne" data-parent="#sidenavAccordion">
                          <nav class="sb-sidenav-menu-nested nav">
                              <a class="nav-link" href="layout-static.html">Static Navigation</a>
                              <a class="nav-link" href="layout-sidenav-light.html">Light Sidenav</a>
                          </nav>
                      </div>
                      <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapsePages" aria-expanded="false" aria-controls="collapsePages">
                          <div class="sb-nav-link-icon"><i class="fas fa-book-open"></i></div>
                          Pages
                          <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
                      </a>
                      <div class="collapse" id="collapsePages" aria-labelledby="headingTwo" data-parent="#sidenavAccordion">
                          <nav class="sb-sidenav-menu-nested nav accordion" id="sidenavAccordionPages">
                              <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#pagesCollapseAuth" aria-expanded="false" aria-controls="pagesCollapseAuth">
                                  Authentication
                                  <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
                              </a>
                              <div class="collapse" id="pagesCollapseAuth" aria-labelledby="headingOne" data-parent="#sidenavAccordionPages">
                                  <nav class="sb-sidenav-menu-nested nav">
                                      <a class="nav-link" href="login.html">Login</a>
                                      <a class="nav-link" href="register.html">Register</a>
                                      <a class="nav-link" href="password.html">Forgot Password</a>
                                  </nav>
                              </div>
                              <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#pagesCollapseError" aria-expanded="false" aria-controls="pagesCollapseError">
                                  Error
                                  <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
                              </a>
                              <div class="collapse" id="pagesCollapseError" aria-labelledby="headingOne" data-parent="#sidenavAccordionPages">
                                  <nav class="sb-sidenav-menu-nested nav">
                                      <a class="nav-link" href="401.html">401 Page</a>
                                      <a class="nav-link" href="404.html">404 Page</a>
                                      <a class="nav-link" href="500.html">500 Page</a>
                                  </nav>
                              </div>
                          </nav>
                      </div>
                     
              </div>
          </nav>
      </div>
      
