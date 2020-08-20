package com.isecinc.pens.scheduler.utils;

import javax.servlet.http.HttpServletRequest;

import com.isecinc.pens.bean.User;

public class SecurityUtils {
 
	public static SecurityUtils getInstance(){
		return new SecurityUtils();
	}
	
	public String getUserId(HttpServletRequest request){
		String userId ="anymous";
		try{
			if(request.getSession().getAttribute(Constants.USER_SESSION) != null){
				User userBean = (User)request.getSession().getAttribute(Constants.USER_SESSION);
				userId = userBean.getUserName();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return userId;
	}
	public String getUserAndTime(HttpServletRequest request){
		String detial ="anymous";
		try{
			if(request.getSession().getAttribute(Constants.USER_SESSION) != null){
				User userBean = (User)request.getSession().getAttribute(Constants.USER_SESSION);
				//detial = "User ID : "+userBean.getId()+"| Date "+userBean.getDateLogon()+"  Time "+userBean.getTimeLogon();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return detial;
	}
	public String gerUserMapping(HttpServletRequest request){
		String userGroupId = "";
		try{
			if(request.getSession().getAttribute(Constants.USER_SESSION) != null){
				User userBean = (User)request.getSession().getAttribute(Constants.USER_SESSION);
				//userGroupId = userBean.getUserGroupId()+"";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return userGroupId;
	}
	public String getUserInRole(HttpServletRequest request){
		String users ="";
		try{
			if(request.getSession().getAttribute(Constants.USER_SESSION) != null){
				users ="'user1','user2'";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return users;
	}
}
