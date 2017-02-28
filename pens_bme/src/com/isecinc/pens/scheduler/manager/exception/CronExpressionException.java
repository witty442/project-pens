package com.isecinc.pens.scheduler.manager.exception;

/**
 * @author Pakonkit
 * 
 */
public class CronExpressionException extends Exception {
    
    public CronExpressionException(){
        super();
    }
    
    public CronExpressionException(String message){
        super(message);
    }
    
    /**
	 * @param message
	 * @param cause
	 */
	public CronExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public CronExpressionException(Throwable cause) {
		super(cause);
	}
}
