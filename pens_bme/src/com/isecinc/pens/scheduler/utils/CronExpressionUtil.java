package com.isecinc.pens.scheduler.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.scheduler.manager.exception.CronExpressionException;
import com.pens.util.Utils;

/**
 * @author Arthit Tanaphongwiset This is a helper class that using for
 *         converting all schedule type
 *         <p>
 *         (e.g. hourly, daily, monthly, etc.) to Cron expression in Unix
 *         system.
 *         <p>
 *         Schedule type is separated by method that you can call.
 */
public class CronExpressionUtil {
	public static final String CRON_MONDAY = "MON";

	public static final String CRON_TUESDAY = "TUE";

	public static final String CRON_WEDNESDAY = "WED";

	public static final String CRON_THURSDAY = "THU";

	public static final String CRON_FRIDAY = "FRI";

	public static final String CRON_SATURDAY = "SAT";

	public static final String CRON_SUNDAY = "SUN";

	public static final String CRON_JANUARY = "JAN";

	public static final String CRON_FEBRUARY = "FEB";

	public static final String CRON_MARCH = "MAR";

	public static final String CRON_APRIL = "APR";

	public static final String CRON_MAY = "MAY";

	public static final String CRON_JUNE = "JUN";

	public static final String CRON_JULY = "JUL";

	public static final String CRON_AUGUST = "AUG";

	public static final String CRON_SEPTEMBER = "SEP";

	public static final String CRON_OCTOBER = "OCT";

	public static final String CRON_NOVEMBER = "NOV";

	public static final String CRON_DECEMBER = "DEC";

	private static Logger logger = Logger.getLogger(CronExpressionUtil.class);

	private CronExpressionUtil() {

	}
	
	//sample  
	//	0 9 12 1/2 * ? *  ->start every 2 day at 12:09 am
	public static String dailyToCronExpr(String days ,Date startDate)
			throws CronExpressionException {
		try {
			StringBuffer cronExpr = new StringBuffer();
			StringBuffer cronDays = new StringBuffer();

			if (Utils.isNull(days).equals("")) {
				throw new CronExpressionException("Days is Empty");
			}

			cronDays.append("1/"+days);
				
	
			int startHour = Integer.parseInt(DateUtil.stringValue(startDate,
					"HH"));
			int startMinute = Integer.parseInt(DateUtil.stringValue(startDate,
					"mm"));
			int startDay = Integer.parseInt(DateUtil.stringValue(startDate,
					"dd"));

			// Cron Expression 0 startMinite startHour ? * dayOfWeek
			cronExpr.append("0 ");
			cronExpr.append(startMinute);
			cronExpr.append(" ");
			cronExpr.append(startHour);
			cronExpr.append(" ");
			cronExpr.append(cronDays);
			cronExpr.append(" * ? *");
			logger.debug("CronExpr : " + cronExpr.toString());

			return cronExpr.toString();

		} catch (Exception e) {
			throw new CronExpressionException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Convert schedule in type of weekly to Cron expression
	 * 
	 * @version 1.0
	 * @author Arthit Tanaphongwiset
	 * @param dayOfWeek
	 * @param startDate
	 * @return String - Cron Expression
	 * @throws CronExpressionException
	 */
	public static String weeklyToCronExpr(String[] dayOfWeek, Date startDate)
			throws CronExpressionException {
		try {
			StringBuffer cronExpr = new StringBuffer();
			StringBuffer cronDayOfWeek = new StringBuffer();

			if (dayOfWeek == null) {
				throw new CronExpressionException("Day of Week is Empty");
			}

			for (int i = 0; i < dayOfWeek.length; i++) {
				cronDayOfWeek.append(dayOfWeek[i]);
				if (i < dayOfWeek.length - 1) {
					cronDayOfWeek.append(",");
				}
			}

			int startHour = Integer.parseInt(DateUtil.stringValue(startDate,
					"HH"));
			int startMinute = Integer.parseInt(DateUtil.stringValue(startDate,
					"mm"));
			int startDay = Integer.parseInt(DateUtil.stringValue(startDate,
					"dd"));

			// Cron Expression 0 startMinite startHour ? * dayOfWeek
			cronExpr.append("0 ");
			cronExpr.append(startMinute);
			cronExpr.append(" ");
			cronExpr.append(startHour);
			cronExpr.append(" ? * ");
			cronExpr.append(cronDayOfWeek.toString());
			logger.debug("CronExpr : " + cronExpr.toString());

			return cronExpr.toString();

		} catch (Exception e) {
			throw new CronExpressionException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Convert schedule in type of Nth day of month to Cron expression.
	 * 
	 * @version 1.0
	 * @author Arthit Tanaphongwiset
	 * @param nthDay
	 * @param months
	 * @param startDate
	 * @return String - Cron Expression
	 * @throws CronExpressionException
	 */
	public static String nthDayOfMonthToCronExpr(int nthDay,Date startDate) throws CronExpressionException {
		try {
			StringBuffer cronExpr = new StringBuffer();
			StringBuffer cronMonth = new StringBuffer();
			logger.debug("nthDay : " + nthDay);
			//logger.debug("months : " + months);
			logger.debug("startDate : " + startDate);
			logger.debug("startDate : "
					+ DateUtil.stringValue(startDate, "dd/MM/yyyy HH:mm"));

			if (nthDay < 1 || nthDay > 31) {
				throw new CronExpressionException(
						"Nth day out of range between 1 and 31");
			}

			/*if (cronMonth == null) {
				throw new CronExpressionException("Month is empty");
			}*/

			/*for (int i = 0; i < months.length; i++) {
				logger.debug("months[" + i + "]: " + (String) months[i]);
				cronMonth.append((String) months[i]);
				if (i < months.length - 1) {
					cronMonth.append(",");
				}
			}*/
			
			cronMonth.append("1/1");//run every 1 Month

			int startHour = Integer.parseInt(DateUtil.stringValue(startDate,
					"HH"));
			int startMinute = Integer.parseInt(DateUtil.stringValue(startDate,
					"mm"));
			//int startDay =
			// Integer.parseInt(DateUtil.stringValue(startDate,"dd"));

			// Cron Expression 0 startMinite startHour startDay months ?
			cronExpr.append("0 ");
			cronExpr.append(startMinute);
			cronExpr.append(" ");
			cronExpr.append(startHour);
			cronExpr.append(" ");
			cronExpr.append(nthDay);
			cronExpr.append(" ");
			cronExpr.append(cronMonth.toString());
			cronExpr.append(" ?");
			logger.debug("CronExpr : " + cronExpr.toString());

			return cronExpr.toString();

		} catch (Exception e) {
			throw new CronExpressionException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Convert schedule in type of last day of month to Cron expression
	 * 
	 * @return Cron expression
	 * @throws Exception
	 * @version 1.0
	 * @author Arthit Tanaphongwiset
	 */
	public static String lastDayOfMonthToCronExpr(String[] months,
			Date startDate) throws Exception {
		try {
			StringBuffer cronExpr = new StringBuffer();
			StringBuffer cronMonth = new StringBuffer();

			if (cronMonth == null) {
				throw new CronExpressionException("Month is empty");
			}

			for (int i = 0; i < months.length; i++) {
				cronMonth.append((String) months[i]);
				if (i < months.length - 1) {
					cronMonth.append(",");
				}
			}

			int startHour = Integer.parseInt(DateUtil.stringValue(startDate,
					"HH"));
			int startMinute = Integer.parseInt(DateUtil.stringValue(startDate,
					"mm"));

			// Cron Expression 0 startMinite startHour L * ?
			cronExpr.append("0 ");
			cronExpr.append(startMinute);
			cronExpr.append(" ");
			cronExpr.append(startHour);
			cronExpr.append(" L ");
			cronExpr.append(cronMonth.toString());
			cronExpr.append(" ?");
			logger.debug("CronExpr : " + cronExpr.toString());

			return cronExpr.toString();

		} catch (Exception e) {
			throw new CronExpressionException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Convert to days in form cron expression by specify number(integer) of day
	 * suggest use java.util.Calendar. For example specify Calendar.MONDAY in
	 * days array.
	 * 
	 * @param days
	 * @return collection of days in cron expression
	 * @throws Exception
	 */
	public static String[] convertToCronDays(int[] days) throws Exception {
		ArrayList cronDays = new ArrayList();

		if (days != null) {
			for (int i = 0; i < days.length; i++) {
				int day = days[i];

				switch (day) {
				case Calendar.MONDAY:
					cronDays.add(CronExpressionUtil.CRON_MONDAY);
					break;
				case Calendar.TUESDAY:
					cronDays.add(CronExpressionUtil.CRON_TUESDAY);
					break;
				case Calendar.WEDNESDAY:
					cronDays.add(CronExpressionUtil.CRON_WEDNESDAY);
					break;
				case Calendar.THURSDAY:
					cronDays.add(CronExpressionUtil.CRON_THURSDAY);
					break;
				case Calendar.FRIDAY:
					cronDays.add(CronExpressionUtil.CRON_FRIDAY);
					break;
				case Calendar.SATURDAY:
					cronDays.add(CronExpressionUtil.CRON_SATURDAY);
					break;
				case Calendar.SUNDAY:
					cronDays.add(CronExpressionUtil.CRON_SUNDAY);
				}
			}
		}

		return (String[]) cronDays.toArray();
	}

	/**
	 * Convert to months in form cron expression by specify number of
	 * month(integer) suggest use java.util.Calendar. For example specify
	 * Calendar.JANUARY in months array.
	 * 
	 * @param months
	 * @return collection of months in cron expression
	 * @throws Exception
	 */
	public static String[] convertToCronMonths(int[] months) throws Exception {
		ArrayList cronMonths = new ArrayList();

		if (months != null) {
			for (int i = 0; i < months.length; i++) {
				int month = months[i];

				switch (month) {
				case Calendar.JANUARY:
					cronMonths.add(CronExpressionUtil.CRON_JANUARY);
					break;
				case Calendar.FEBRUARY:
					cronMonths.add(CronExpressionUtil.CRON_FEBRUARY);
					break;
				case Calendar.MARCH:
					cronMonths.add(CronExpressionUtil.CRON_MARCH);
					break;
				case Calendar.APRIL:
					cronMonths.add(CronExpressionUtil.CRON_APRIL);
					break;
				case Calendar.MAY:
					cronMonths.add(CronExpressionUtil.CRON_MAY);
					break;
				case Calendar.JUNE:
					cronMonths.add(CronExpressionUtil.CRON_JUNE);
					break;
				case Calendar.JULY:
					cronMonths.add(CronExpressionUtil.CRON_JULY);
					break;
				case Calendar.AUGUST:
					cronMonths.add(CronExpressionUtil.CRON_AUGUST);
					break;
				case Calendar.SEPTEMBER:
					cronMonths.add(CronExpressionUtil.CRON_SEPTEMBER);
					break;
				case Calendar.OCTOBER:
					cronMonths.add(CronExpressionUtil.CRON_OCTOBER);
					break;
				case Calendar.NOVEMBER:
					cronMonths.add(CronExpressionUtil.CRON_NOVEMBER);
					break;
				case Calendar.DECEMBER:
					cronMonths.add(CronExpressionUtil.CRON_DECEMBER);
				}
			}
		}

		return (String[]) cronMonths.toArray();
	}

	/**
	 * Convert date inform Cron Expressoin.
	 * 
	 * @param date
	 * @return String
	 * @throws Exception
	 */
	public static String convertDateToCronExpr(Date date) throws Exception {
		try {
			String[] cronMonthList = { "",CRON_JANUARY, CRON_FEBRUARY, CRON_MARCH,
					CRON_APRIL, CRON_MAY, CRON_JUNE, CRON_JULY, CRON_AUGUST,
					CRON_SEPTEMBER, CRON_OCTOBER, CRON_NOVEMBER, CRON_DECEMBER };

			StringBuffer startTimeCronExpr = new StringBuffer();
			startTimeCronExpr.append("0 ");
			startTimeCronExpr.append(Integer.parseInt(DateUtil.stringValue(
					date, "mm")));
			startTimeCronExpr.append(" ");
			startTimeCronExpr.append(Integer.parseInt(DateUtil.stringValue(
					date, "HH")));
			startTimeCronExpr.append(" *");
			//startTimeCronExpr.append(Integer.parseInt(DateUtil.stringValue(
			//		date, "dd")));
			startTimeCronExpr.append(" ");
			int month = Integer.parseInt(DateUtil.stringValue(date, "MM"));
			logger.debug("month : "+month);
			
			startTimeCronExpr.append(cronMonthList[month]);
			startTimeCronExpr.append(" ? ");
			
			startTimeCronExpr.append(DateUtil.stringValue(date, "yyyy"));

			logger.debug("Cron Date Expr : " + startTimeCronExpr.toString());

			return startTimeCronExpr.toString();
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		}
	}
}