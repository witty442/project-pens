/**
 * Project Name  : PMR
 * 
 * Package Name  : util
 * Environment   : Java 5.0
 * Version       : 1.0
 * Creation Date : 23/11/2553
 * Implement By  : Mr.Danai Khankham (noomcomputer@hotmail.com)
 */

package util;

public class Constants {

	// system default
	public static final int LINE_PER_PAGE = 20; //10
	public static final int CACHE_PAGE_SIZE = 500; //5
	
	public static final String STORE_TYPE_LOTUS_CODE ="020047";
	public static final String STORE_TYPE_BIGC_CODE ="020049";
	public static final String STORE_TYPE_TOPS_CODE ="020058";

	public static final String STORE_TYPE_MTT_CODE_1 ="100001";
	public static final String STORE_TYPE_KING_POWER ="020056";//King Power
	public static final String STORE_TYPE_KING_POWER_2 ="020072";//King Power
	public static final String STORE_TYPE_KING_POWER_3 ="020068";//King Power
	public static final String STORE_TYPE_KING_POWER_4 ="020083";//King Power
	public static final String STORE_TYPE_KING_POWER_5 ="020092";//King Power
	
	public static final String STORE_TYPE_FRIDAY_CODE ="020052";
	public static final String STORE_TYPE_HISHER_CODE = "020051";
	
	public static final String STORE_TYPE_PENS = "020999";
	public static final String STORE_TYPE_GRAND_SALE = "000023";
	public static final String STORE_TYPE_WIENNA = "020064";
	
	public static final String STORE_TYPE_OSHOPPING_CODE ="020066";
	public static final String STORE_TYPE_7CATALOG_CODE ="020070";
	public static final String STORE_TYPE_TVD_CODE ="020074";
	
	public static String STORE_TYPE_LOTUS_ITEM = "LotusItem";
	public static String STORE_TYPE_BIGC_ITEM = "BigCitem";
	public static String STORE_TYPE_FRIDAY_ITEM = "FridayItem";
	public static String STORE_TYPE_OSHOPPING_ITEM = "OshoppingItem";
	public static String STORE_TYPE_7CATALOG_ITEM = "7CItem";
	public static String STORE_TYPE_TVD_ITEM = "TVDItem";
	
	
	public static String getStoreGroupName(String storeGroup){
		if(storeGroup.equals(STORE_TYPE_LOTUS_CODE)){
			return "LOTUS";
		}else 	if(storeGroup.equals(STORE_TYPE_BIGC_CODE)){
			return "BIGC";
		}else 	if(storeGroup.equals(STORE_TYPE_TOPS_CODE)){
			return "TOPS";		
		}else 	if(storeGroup.equals(STORE_TYPE_FRIDAY_CODE)){
			return "FRIDAY";
		}else 	if(storeGroup.equals(STORE_TYPE_TVD_CODE)){
			return "TV-DIRECT";
		}else 	if(storeGroup.equals(STORE_TYPE_MTT_CODE_1)){
			return "MTT";
		}else 	if(storeGroup.equals(STORE_TYPE_KING_POWER)){
			return "KING Power";
		}else 	if(storeGroup.equals(STORE_TYPE_HISHER_CODE)){
			return "HIS-HER";
		}else 	if(storeGroup.equals(STORE_TYPE_PENS)){
			return "กลุ่มภายใน PENS";
		}else 	if(storeGroup.equals(STORE_TYPE_GRAND_SALE)){
			return "Grand Sale";
		}else 	if(storeGroup.equals(STORE_TYPE_WIENNA)){
			return "Wienna - เวียนนา";
		}
		return "";
	}
}