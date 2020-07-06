package com.isecinc.core;

/**
 * PENS Exception
 * 
 * @author Atiz.b
 * @version $Id: PENSException.java,v 1.0 14/06/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ISECException extends Exception {

	private static final long serialVersionUID = 8611747013879884262L;

	public static final int INFORMATION = 1;
	public static final int VALIDATION_ERROR = 2;
	public static final int FETAL_ERROR = 5;

	public ISECException(int error, String message) {
		super(message);
	}
}
