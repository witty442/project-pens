package com.isecinc.pens.web.ordernissin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OrderNissin;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MOrderNissin;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateToolsUtil;
import com.pens.util.UserUtils;
import com.pens.util.excel.ExcelHeader;

public class OrderNissinReport {

	protected static Logger logger = Logger.getLogger("PENS");

	public static StringBuffer genReportToExcel(HttpServletRequest request,OrderNissinForm aForm,boolean excel) throws Exception{
		StringBuffer h = null;
		StringBuffer sql = new StringBuffer("");
		String className = "";
		String classNameNumber = "";
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try{
			//set style excel or html
			if(excel){
				className="text";
				classNameNumber="num_currency";
			}else{
				className="td_text_center";
				classNameNumber="td_number";
			}
			
			conn = DBConnectionApps.getInstance().getConnection();
			
			aForm.getBean().setUser(user);
			OrderNissin orderBean = MOrderNissin.searchHead(conn,user,aForm.getBean(),true,0,0);
			
		    for(int i=0;i<orderBean.getItemsList().size();i++){
		    	OrderNissin item = orderBean.getItemsList().get(i);
		 
		    	if(i==0){
		    		h = new StringBuffer("");
		    		if(excel){
		    		   h.append(ExcelHeader.EXCEL_HEADER);
					   h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
		    		}else{
		    		   h.append("<table id='tblProduct' align='center' border='0' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
		    		}
					h.append("<tr>\n");
					h.append("<th >Order Id</th> \n");
					h.append("<th >วันที่ Nissin คีย์ สั่งซื้อ</th> \n");
					h.append("<th >Ref Cust ID</th> \n");
					h.append("<th >ชื่อร้านค้า</th> \n");
					h.append("<th >จังหวัด </th> \n");
					h.append("<th >อำเภอ</th> \n");
					h.append("<th >สถานะ </th> \n");
					h.append("<th >รหัสเซลล์ PENS</th> \n");
					h.append("<th >ผู้บันทึก Order</th> \n");
					if( !UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS})){
						h.append("<th >Invoice No</th> \n");
						h.append("<th >Invoice Date</th> \n");
						h.append("<th >Oracle Cust No</th> \n");
						h.append("<th >ผู้บันทึก Order</th> \n");
					}else{
						h.append("<th >Invoice Date</th> \n");
					}
					h.append("</tr> \n");
		    	}
		    	
				h.append("<tr> \n");
			    h.append("<td class='"+className+"' >"+item.getId()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getOrderDate()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getCustomerNis().getId()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getCustomerNis().getName()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getCustomerNis().getProvinceName()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getCustomerNis().getDistrictName()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getDocStatus()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getSalesrepCode()+"</td> \n");
			    h.append("<td class='"+className+"' >"+item.getNisCreateUser()+"</td> \n");
			    if( !UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS})){
				    h.append("<td class='"+className+"' >"+item.getInvoiceNo()+"</td> \n");
				    h.append("<td class='"+className+"' >"+item.getInvoiceDate()+"</td> \n");
				    h.append("<td class='"+className+"' >"+item.getOraCustomerCode()+"</td> \n");
				    h.append("<td class='"+className+"' >"+item.getNisCreateUser()+"</td> \n");
			    }else{
			    	h.append("<td class='"+className+"' >"+item.getInvoiceDate()+"</td> \n");
			    }
				h.append("</tr> \n");
		    }//for
		   h.append("</table> \n");
		   return h;
		}catch(Exception e){
			throw e;
		}finally{
			if(conn !=null){
				conn.close();
			}
		}
	}
}
