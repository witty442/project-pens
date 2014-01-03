package util;

import com.isecinc.pens.inf.helper.Utils;

public class ReportHelper {

	/** 
	 * order_no length == 12 only
	 * order ที่ขึ้นต้นด้วย S  ให้ replace เป็น 2
       order ที่ขึ้นต้นด้วย V ให้ replace เป็น  3
       Case length =13
       'SN20156050001',
	 * @param orderNo
	 * @return
	 * @throws Exception
	 */
	public static String convertOrderNoForReport(String orderNo){
		if(Utils.isNull(orderNo).length() ==12){
			String firstPos = orderNo.substring(0,1);
			if(firstPos.equals("S")){
				orderNo = "2"+orderNo.substring(1,orderNo.length());
			}else if(firstPos.equals("V")){
				orderNo = "3"+orderNo.substring(1,orderNo.length());
			}
		}else if(Utils.isNull(orderNo).length() ==13){
			String firstPos = orderNo.substring(0,1);
			if(firstPos.equals("SN")){
				orderNo = "2"+orderNo.substring(2,orderNo.length());
			}
		}
		return orderNo;
	}
	
	
	
}
