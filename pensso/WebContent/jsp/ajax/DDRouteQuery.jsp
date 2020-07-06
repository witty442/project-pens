<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page language="java" contentType="text/html; charset=TIS-620"
	pageEncoding="TIS-620"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.isecinc.pens.bean.DeliveryRoute"%>
<%@page import="com.isecinc.pens.model.MDeliveryRoute"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%
	String districtId = (String) request.getParameter("districtId");
	String registerDate = (String) request.getParameter("registerDate");
	String dayout = "";

	//System.out.println(districtId);
	//System.out.println(registerDate);

	String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
	int idx = 0;
	int idx2 = 0;

	List<DeliveryRoute> ddrs = new ArrayList<DeliveryRoute>();

	try {
		Date d = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).parse(registerDate);
		Calendar cld = Calendar.getInstance(new Locale("th", "TH"));
		cld.setTime(d);

		//System.out.println(cld.get(Calendar.DAY_OF_WEEK));
		idx = cld.get(Calendar.DAY_OF_WEEK) - 1;
		//System.out.println(days[idx]);

		ddrs = new MDeliveryRoute().lookUp(Integer.parseInt(districtId));
		if (ddrs.size() == 2) {
			//have 2 dd day & same route on Sat
			if (6 - idx <= 3) {
				//...
				for (DeliveryRoute ddr : ddrs) {
					if (!ddr.getDays().equalsIgnoreCase("Sat")) {
						dayout = ddr.getDays();
						break;
					}
				}
			} else {
				for (DeliveryRoute ddr : ddrs) {
					if (!ddr.getDays().equalsIgnoreCase("Sat")) {
						for (int i = 0; i < days.length; i++) {
							if(ddr.getDays().equalsIgnoreCase(days[i])){
								idx2 = i;
								break;
							}
						}
					}
				}
				//...
				if(idx2-idx>3 && idx2<6){
					//more 3 days but not Sat
					dayout = days[idx2];
				}else{
					//use Sat
					dayout = days[6];
				}
			}
		} else {
			// have one dd Day
			dayout = ddrs.get(0).getDays();
		}

	} catch (Exception e) {
		e.printStackTrace();
	} finally {

	}
%>
<%=dayout%>