package com.isecinc.pens.web.sales;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import util.ControlCode;

import com.isecinc.pens.inf.helper.Utils;

public class ControlOrderPage {
	
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");
	public static String STEP_ORDER_1 ="STEP_ORDER_1";
	public static String STEP_ORDER_2 ="STEP_ORDER_2";
	public static String STEP_ORDER_3 ="STEP_ORDER_3";
	
	public static void setPrevOrderStepAction(HttpServletRequest request, String stepOrderAction){
		request.getSession().setAttribute("PREV_STEP_ORDER_ACTION", stepOrderAction);
		logger.info(" afterset prevStepOrderAction["+Utils.isNull(request.getSession().getAttribute("PREV_STEP_ORDER_ACTION"))+"]");
	}
	
	public static boolean stepIsValid(HttpServletRequest request,String curStepOrderAction){
		boolean valid = true;
		
		//No Validate
		if( !ControlCode.canExecuteMethod("ControlOrderPage", "stepIsValid")){
			return true;
		}
		
		String prevStepOrderAction = "";
		if( Utils.isNull(request.getSession().getAttribute("PREV_STEP_ORDER_ACTION")).equals("")){
			valid = false;
		}else{
			prevStepOrderAction = Utils.isNull(request.getSession().getAttribute("PREV_STEP_ORDER_ACTION"));
		}
		
		logger.info("prevStepOrderAction["+prevStepOrderAction+"]");
		logger.info("curStepOrderAction["+curStepOrderAction+"]");
		
		/** STEP_ORDER_PROMOTION  Prev Step Must equals STEP_BEFORE_ORDER  */
		if(curStepOrderAction.equalsIgnoreCase(STEP_ORDER_2)){
			if( !STEP_ORDER_1.equalsIgnoreCase(prevStepOrderAction)){
				valid = false;
			}
		}
		
		/** STEP_ORDER_SAVE  PrevStep Must equals STEP_ORDER  */
		if(curStepOrderAction.equalsIgnoreCase(STEP_ORDER_3)){
			if( !STEP_ORDER_2.equalsIgnoreCase(prevStepOrderAction)){
				valid = false;
			}
		}
		logger.info("valid:"+valid);
		return valid;
	}
}
