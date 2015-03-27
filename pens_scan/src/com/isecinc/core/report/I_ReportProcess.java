package com.isecinc.core.report;

import java.sql.Connection;
import java.util.List;
import com.isecinc.pens.bean.User;

public abstract class I_ReportProcess<T> {

	public abstract List<T> doReport(T t, User user, Connection conn) throws Exception;
}
