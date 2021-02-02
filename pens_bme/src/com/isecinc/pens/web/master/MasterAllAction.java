package com.isecinc.pens.web.master;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class MasterAllAction {
	
	public static void precess(HttpServletRequest request, String processType,Map<String, String> param) throws Exception{
		try{
			if("GenBMEProductGroup".equalsIgnoreCase(processType)){
				GenBMEProductGroup.process(param);
			}else{
				
			}
			request.setAttribute("Message", "Process Success");
		}catch(Exception e){
			request.setAttribute("Message", "Error :"+e.getMessage());
		}
	}
}
