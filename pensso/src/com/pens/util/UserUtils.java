package com.pens.util;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;

public class UserUtils {
	public static Logger logger = Logger.getLogger("PENS");
	
	public static boolean userInRole(String roleType,User user,String[] roles){
		boolean r = false;
		String roleCheck = "";
		String userRoleTemp = "";
		String userRoles[] = null;
		String userRole = "";
		/** case Admin All Pass **/
		if(user.getUserName().equalsIgnoreCase("admin") 
			|| user.getUserName().equalsIgnoreCase("DEV")	){
			return true;
		}
		for(int i=0;i<roles.length;i++){
			roleCheck = roles[i].toLowerCase().trim();
			userRoleTemp = "";
            if("ROLE_ACCESS".equalsIgnoreCase(roleType)){
            	userRoleTemp = Utils.isNull(user.getRoleAccess()).toLowerCase().trim();
            }
            
            userRoles = userRoleTemp.split("\\|");
			for(int j =0;j<userRoles.length;j++){
				userRole = userRoles[j];
				//logger.debug("roleCheck:["+i+"]["+roleCheck+"]["+userRole+"]");
				
				if( roleCheck.equalsIgnoreCase(userRole)){
					//logger.debug("EQ =roleCheck["+roleCheck+"]:["+i+"]["+userRole+"]");
					r =  true;
					break;
				}
			}//for 2
			
		}//for 1
		return r;
	}

}
