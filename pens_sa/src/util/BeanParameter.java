package util;

import java.io.Serializable;

public class BeanParameter implements Serializable {

	private static final long serialVersionUID = 6208466622880156655L;

	private static String fileName = "";
	private static String contextname;
	private static String database;
	private static String reportPath;
	private static String logo;
	private static String pensTaxNo;
	private static String tempPath;
	// MODIFIER
	// MOD TYPE
	private static String modifierDiscount;
	private static String modifierPricebreakheader;
	private static String modifierPromotiongood;
	// ATTRIBUTE TYPE
	private static String modifierItemCategory;
	private static String modifierItemNumber;
	// BREAK TYPE
	private static String breakTypePoint;
	private static String breakTypeRecurring;

	// APPLICATION METHOD
	private static String appMethodAMT;
	private static String appMethodPercent;
	private static String appMethodNewPrice;
	// MODIFIER -- end --

	// QUALIFIER
	// QUA TYPE
	private static String qualifierContext;
	private static String qualifierType;
	private static String qualifierVAN;
	private static String qualifierTT;
	private static String qualifierDD;

	private static String lineQualifierContext;
	private static String lineQualifierType;

	// QUALIFIER -- end --

	
	public static String getDatabase() {
		return database;
	}

	public static String getTempPath() {
		return tempPath;
	}

	public static void setTempPath(String tempPath) {
		BeanParameter.tempPath = tempPath;
	}

	public static void setDatabase(String database) {
		BeanParameter.database = database;
	}

	public static String getContextname() {
		return contextname;
	}

	public static void setContextname(String contextname) {
		BeanParameter.contextname = contextname;
	}

	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String fileName) {
		BeanParameter.fileName = fileName;
	}

	public static String getReportPath() {
		return reportPath;
	}

	public static void setReportPath(String reportPath) {
		BeanParameter.reportPath = reportPath;
	}

	public static String getLogo() {
		return logo;
	}

	public static void setLogo(String logo) {
		BeanParameter.logo = logo;
	}

	public static String getQualifierContext() {
		return qualifierContext;
	}

	public static void setQualifierContext(String qualifierContext) {
		BeanParameter.qualifierContext = qualifierContext;
	}

	public static String getQualifierVAN() {
		return qualifierVAN;
	}

	public static void setQualifierVAN(String qualifierVAN) {
		BeanParameter.qualifierVAN = qualifierVAN;
	}

	public static String getQualifierTT() {
		return qualifierTT;
	}

	public static void setQualifierTT(String qualifierTT) {
		BeanParameter.qualifierTT = qualifierTT;
	}

	public static String getQualifierDD() {
		return qualifierDD;
	}

	public static void setQualifierDD(String qualifierDD) {
		BeanParameter.qualifierDD = qualifierDD;
	}

	public static String getQualifierType() {
		return qualifierType;
	}

	public static void setQualifierType(String qualifierType) {
		BeanParameter.qualifierType = qualifierType;
	}

	public static String getModifierItemCategory() {
		return modifierItemCategory;
	}

	public static void setModifierItemCategory(String modifierItemCategory) {
		BeanParameter.modifierItemCategory = modifierItemCategory;
	}

	public static String getModifierItemNumber() {
		return modifierItemNumber;
	}

	public static void setModifierItemNumber(String modifierItemNumber) {
		BeanParameter.modifierItemNumber = modifierItemNumber;
	}

	public static String getModifierDiscount() {
		return modifierDiscount;
	}

	public static void setModifierDiscount(String modifierDiscount) {
		BeanParameter.modifierDiscount = modifierDiscount;
	}

	public static String getModifierPricebreakheader() {
		return modifierPricebreakheader;
	}

	public static void setModifierPricebreakheader(String modifierPricebreakheader) {
		BeanParameter.modifierPricebreakheader = modifierPricebreakheader;
	}

	public static String getModifierPromotiongood() {
		return modifierPromotiongood;
	}

	public static void setModifierPromotiongood(String modifierPromotiongood) {
		BeanParameter.modifierPromotiongood = modifierPromotiongood;
	}

	public static String getBreakTypePoint() {
		return breakTypePoint;
	}

	public static void setBreakTypePoint(String breakTypePoint) {
		BeanParameter.breakTypePoint = breakTypePoint;
	}

	public static String getBreakTypeRecurring() {
		return breakTypeRecurring;
	}

	public static void setBreakTypeRecurring(String breakTypeRecurring) {
		BeanParameter.breakTypeRecurring = breakTypeRecurring;
	}

	public static String getAppMethodAMT() {
		return appMethodAMT;
	}

	public static void setAppMethodAMT(String appMethodAMT) {
		BeanParameter.appMethodAMT = appMethodAMT;
	}

	public static String getAppMethodPercent() {
		return appMethodPercent;
	}

	public static void setAppMethodPercent(String appMethodPercent) {
		BeanParameter.appMethodPercent = appMethodPercent;
	}

	public static String getAppMethodNewPrice() {
		return appMethodNewPrice;
	}

	public static void setAppMethodNewPrice(String appMethodNewPrice) {
		BeanParameter.appMethodNewPrice = appMethodNewPrice;
	}

	public static String getLineQualifierContext() {
		return lineQualifierContext;
	}

	public static void setLineQualifierContext(String lineQualifierContext) {
		BeanParameter.lineQualifierContext = lineQualifierContext;
	}

	public static String getLineQualifierType() {
		return lineQualifierType;
	}

	public static void setLineQualifierType(String lineQualifierType) {
		BeanParameter.lineQualifierType = lineQualifierType;
	}

	public static String getPensTaxNo() {
		return pensTaxNo;
	}

	public static void setPensTaxNo(String pensTaxNo) {
		BeanParameter.pensTaxNo = pensTaxNo;
	}

}
