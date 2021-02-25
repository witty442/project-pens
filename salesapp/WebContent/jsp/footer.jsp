          <%@page import="com.pens.util.ControlCode"%>
</div>
         </main>
         
          <footer class="py-4 bg-light mt-auto">
             <div class="container-fluid">
                 <div class="d-flex align-items-center justify-content-between small">
                     <div class="text-muted">Copyright &copy; PENS 2021</div>
                     <div>
                       <!--   <a href="#">Privacy Policy</a>
                         &middot;
                         <a href="#">Terms &amp; Conditions</a> -->
                     </div>
                 </div>
             </div>
         </footer>
     </div>
 </div>
  
<%if(ControlCode.canExecuteMethod("template", "sb_admin")){ %>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap/scripts_init_sb_admin.js"></script> 
<%}else{ %>
  <script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap/scripts_init_sb_admin.js"></script> 
<%} %>
  
			 
			 
		