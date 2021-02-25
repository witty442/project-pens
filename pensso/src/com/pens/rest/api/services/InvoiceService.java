package com.pens.rest.api.services;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Invoice;
import com.isecinc.pens.model.MInvoice;
import com.pens.rest.api.mapping.InvoiceAPIBean;
import com.pens.web.filter.AuthService;


@Path("/invoice")
public class InvoiceService {
	private static Logger logger = Logger.getLogger("PENS");
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Invoice getInvoice(Invoice invParam) {
		Invoice inv = new Invoice();
		
		return inv; 
	}
	

	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public InvoiceAPIBean getInvoiceListToSalesApp(@HeaderParam("authorization") String authString) {

		InvoiceAPIBean invoiceApiBean = new InvoiceAPIBean();
	    if(!new AuthService().isUserAuthenticated(authString)){
             logger.debug("User is unauthorized.");
	    	 throw new SecurityException("User is unauthorized.");
        }else{
	 
			// get invoice list is no export
			List<Invoice> invoiceList = MInvoice.getInvoiceListToSalesApp();
			
			
			if(invoiceList != null && invoiceList.size()>0){
				invoiceApiBean.setInvoiceList(invoiceList);
			}
       }
	   return invoiceApiBean; 
	}
	
	
	//Update invoice
	@POST
	@Path("/put")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateInvoiceExportToSalesFlag(@HeaderParam("authorization") String authString,String invoiceIdArr) {
		logger.debug("Update exported_to_sales =Y InvoiceId : " + invoiceIdArr);
		 if(!new AuthService().isUserAuthenticated(authString)){
             logger.debug("User is unauthorized.");
	    	 throw new SecurityException("User is unauthorized.");
        }else{
			//update exported_to_sales = Y  by invoice_id list
			MInvoice.updateExportToSalesInvoice(invoiceIdArr);
			
			Invoice custMsg = new Invoice();
			custMsg.setStatusMessage("SUCCESS");
			
			return Response.status(201).entity(custMsg).build();
        }
		//return custMsg;
	}
	
}