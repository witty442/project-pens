package util;

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
		if(user.getUserName().equalsIgnoreCase("admin")){
			return true;
		}
		for(int i=0;i<roles.length;i++){
			roleCheck = roles[i].toLowerCase().trim();
			userRoleTemp = "";
            if("ROLE_SALESTARGET".equalsIgnoreCase(roleType)){
            	userRoleTemp = user.getRoleSalesTarget().toLowerCase().trim();
            }else if("ROLE_CR_STOCK".equalsIgnoreCase(roleType)){
            	userRoleTemp = user.getRoleCRStock().toLowerCase().trim();
            }else if("ROLE_SPIDER".equalsIgnoreCase(roleType)){
            	userRoleTemp = user.getRoleSpider().toLowerCase().trim();
            }else if("ROLE_STOCK_VAN".equalsIgnoreCase(roleType)){
            	userRoleTemp = user.getRoleStockVan().toLowerCase().trim();
            }else if("ROLE_SA".equalsIgnoreCase(roleType)){
            	userRoleTemp = user.getRoleSA().toLowerCase().trim();
            }else if("ROLE_PRODSHOW".equalsIgnoreCase(roleType)){
            	userRoleTemp = user.getRoleProdShow().toLowerCase().trim();
            }else if("ROLE_MC".equalsIgnoreCase(roleType)){
            	userRoleTemp = user.getRoleMC().toLowerCase().trim();
            }else if("ROLE_VANDOC".equalsIgnoreCase(roleType)){
            	userRoleTemp = user.getRoleVanDoc().toLowerCase().trim();
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
	
	public static boolean userInRoleSalesTarget(User user,String[] roles){
		boolean r = false;
		for(int i=0;i<roles.length;i++){
			String roleCheck = roles[i].toLowerCase().trim();
			String userRoleTemp = user.getRoleSalesTarget().toLowerCase().trim();
			String userRoles[] = userRoleTemp.split("\\|");

			for(int j =0;j<userRoles.length;j++){
				String userRole = userRoles[j];
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
	
	/*public static boolean userInRoleCreditStock(User user,String[] roles){
		boolean r = false;
		for(int i=0;i<roles.length;i++){
			String roleCheck = roles[i].toLowerCase().trim();
			String userRoleTemp = user.getRoleCRStock().toLowerCase().trim();
			String userRoles[] = userRoleTemp.split("\\|");

			for(int j =0;j<userRoles.length;j++){
				String userRole = userRoles[j];
				//logger.debug("roleCheck:["+i+"]["+roleCheck+"]["+userRole+"]");
				
				if( roleCheck.equalsIgnoreCase(userRole)){
					//logger.debug("EQ =roleCheck["+roleCheck+"]:["+i+"]["+userRole+"]");
					r =  true;
					break;
				}
			}//for 2
			
		}//for 1
		return r;
	}*/
	
	/*public static boolean userInRoleSpider(User user,String[] roles){
		boolean r = false;
		*//** case Admin All Pass **//*
		if(user.getUserName().equalsIgnoreCase("admin")){
			return true;
		}
		for(int i=0;i<roles.length;i++){
			String roleCheck = roles[i].toLowerCase().trim();
			String userRoleTemp = user.getRoleSpider().toLowerCase().trim();
			String userRoles[] = userRoleTemp.split("\\|");

			for(int j =0;j<userRoles.length;j++){
				String userRole = userRoles[j];
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
	*/
	/*public static boolean userInRoleStockVan(User user,String[] roles){
		boolean r = false;
		*//** case Admin All Pass **//*
		if(user.getUserName().equalsIgnoreCase("admin")){
			return true;
		}
		for(int i=0;i<roles.length;i++){
			String roleCheck = roles[i].toLowerCase().trim();
			String userRoleTemp = user.getRoleStockVan().toLowerCase().trim();
			String userRoles[] = userRoleTemp.split("\\|");

			for(int j =0;j<userRoles.length;j++){
				String userRole = userRoles[j];
				//logger.debug("roleCheck:["+i+"]["+roleCheck+"]["+userRole+"]");
				
				if( roleCheck.equalsIgnoreCase(userRole)){
					//logger.debug("EQ =roleCheck["+roleCheck+"]:["+i+"]["+userRole+"]");
					r =  true;
					break;
				}
			}//for 2
			
		}//for 1
		return r;
	}*/
	
	/*public static boolean userInRoleSA(User user,String[] roles){
		boolean r = false;
		*//** case Admin All Pass **//*
		if(user.getUserName().equalsIgnoreCase("admin")){
			return true;
		}
		for(int i=0;i<roles.length;i++){
			String roleCheck = roles[i].toLowerCase().trim();
			String userRoleTemp = user.getRoleSA().toLowerCase().trim();
			String userRoles[] = userRoleTemp.split("\\|");

			for(int j =0;j<userRoles.length;j++){
				String userRole = userRoles[j];
				//logger.debug("roleCheck:["+i+"]["+roleCheck+"]["+userRole+"]");
				
				if( roleCheck.equalsIgnoreCase(userRole)){
					//logger.debug("EQ =roleCheck["+roleCheck+"]:["+i+"]["+userRole+"]");
					r =  true;
					break;
				}
			}//for 2
			
		}//for 1
		return r;
	}*/
	
	/*public static boolean userInRoleProdShow(User user,String[] roles){
		boolean r = false;
		*//** case Admin All Pass **//*
		if(user.getUserName().equalsIgnoreCase("admin")){
			return true;
		}
		for(int i=0;i<roles.length;i++){
			String roleCheck = roles[i].toLowerCase().trim();
			String userRoleTemp = user.getRoleProdShow().toLowerCase().trim();
			String userRoles[] = userRoleTemp.split("\\|");

			for(int j =0;j<userRoles.length;j++){
				String userRole = userRoles[j];
				//logger.debug("roleCheck:["+i+"]["+roleCheck+"]["+userRole+"]");
				
				if( roleCheck.equalsIgnoreCase(userRole)){
					//logger.debug("EQ =roleCheck["+roleCheck+"]:["+i+"]["+userRole+"]");
					r =  true;
					break;
				}
			}//for 2
			
		}//for 1
		return r;
	}*/
	/*public static boolean userInRoleMC(User user,String[] roles){
		boolean r = false;
		*//** case Admin All Pass **//*
		if(user.getUserName().equalsIgnoreCase("admin")){
			return true;
		}
		for(int i=0;i<roles.length;i++){
			String roleCheck = roles[i].toLowerCase().trim();
			String userRoleTemp = user.getRoleMC().toLowerCase().trim();
			logger.debug("userRoleTemp:"+userRoleTemp);

			String userRoles[] = userRoleTemp.split("\\|");
			logger.debug("userRoles:"+userRoles.length);

			for(int j =0;j<userRoles.length;j++){
				String userRole = userRoles[j];
				logger.debug("roleCheck:["+i+"]["+roleCheck+"]["+userRole+"]");
				
				if( roleCheck.equalsIgnoreCase(userRole)){
					logger.debug("EQ =roleCheck["+roleCheck+"]:["+i+"]["+userRole+"]");
					r =  true;
					break;
				}
			}//for 2
			
		}//for 1
		return r;
	}*/
	
	/*public static boolean userInRoleVanDoc(User user,String[] roles){
		boolean r = false;
		*//** case Admin All Pass **//*
		if(user.getUserName().equalsIgnoreCase("admin")){
			return true;
		}
		for(int i=0;i<roles.length;i++){
			String roleCheck = roles[i].toLowerCase().trim();
			String userRoleTemp = user.getRoleVanDoc().toLowerCase().trim();
			String userRoles[] = userRoleTemp.split("\\|");

			for(int j =0;j<userRoles.length;j++){
				String userRole = userRoles[j];
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
	*/
	

}
