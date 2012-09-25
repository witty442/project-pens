package com.isecinc.core.report;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;

public abstract class I_ReportProcess<T> {
	protected Logger logger = Logger.getLogger("PENS");
	
	public abstract List<T> doReport(T t, User user, Connection conn) throws Exception;
}
