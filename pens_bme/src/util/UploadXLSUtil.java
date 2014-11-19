package util;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts.upload.FormFile;

public class UploadXLSUtil {

	// private Logger logger = Logger.getLogger("PENS");
	private static Logger logger = Logger.getLogger("PENS");

	private final DecimalFormat intFormat = new DecimalFormat("#0");
	private final DecimalFormat decimalFormat = new DecimalFormat("###0.00");

	public ArrayList loadXls(FormFile dataFile, int sheetNo, int rowNo, int maxRowNo, int maxColumnNo, int keyColumn)
			throws Exception {
		ArrayList rows = new ArrayList();
		try {
		
			Workbook wb = new HSSFWorkbook(dataFile.getInputStream());
			Sheet sheet = wb.getSheetAt(sheetNo);
			Row row = null;
			Cell cell = null;
            int no = 0;
			logger.debug("number of sheet: " + wb.getNumberOfSheets());
			logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
            //logger.debug("Total Rows:"+sheet.get)
			// sheet.getLastRowNum()
			for (int i = rowNo; i <= 7; i++) {
				row = sheet.getRow(i);
				for (int colNo = 0; colNo < maxColumnNo; colNo++) {
					cell = row.getCell((short) colNo);
					logger.debug("row["+i+"]col[("+colNo+"]value["+getCellValue(colNo, cell)+"]");
					//columns.add(colNo, getCellValue(colNo, cell));
				}
			}
			
		} catch (Exception e) {
			throw e;
		}

		return null;//rows;
	}

	public Object getCellValue(int colNo, Cell cell) throws Exception {
		Object value = new Object();
		try {
			if (cell != null) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BLANK:
					// logger.debug("column(" + colNo + "): CELL_TYPE_BLANK");
					value = null;
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					// logger.debug("column(" + colNo + "): CELL_TYPE_BOOLEAN, value: " + cell.getBooleanCellValue());
					value = cell.getBooleanCellValue();
					break;
				case Cell.CELL_TYPE_ERROR:
					// logger.debug("column(" + colNo + "): CELL_TYPE_ERROR");
					value = null;
					break;
				case Cell.CELL_TYPE_FORMULA:
					if (cell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC) {
						// logger.debug("column(" + colNo + "): CELL_TYPE_FORMULA(" + cell.getCachedFormulaResultType()
						// + "), value: " + cell.getNumericCellValue());
						value = cell.getNumericCellValue();
					} else {
						RichTextString text = cell.getRichStringCellValue();
						// logger.debug("column(" + colNo + "): CELL_TYPE_FORMULA(" + cell.getCachedFormulaResultType()
						// + "), value: " + text.getString());
						value = text.getString();
					}
					break;
				case Cell.CELL_TYPE_NUMERIC:
					//logger.debug("column(" + colNo + "): CELL_TYPE_NUMERIC, value: " + cell.getNumericCellValue());
					
					value = cell.getNumericCellValue();
					// date value
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						value = cell.getDateCellValue();
					}
					break;
				default:
					RichTextString text = cell.getRichStringCellValue();
					//logger.debug("column(" + colNo + "): CELL_TYPE_STRING, value: " + text.getString());
					value = text.getString();
					break;
				}
			}else{
				value = null;
			}
		} catch (Exception e) {
			throw e;
		}

		return value;
	}

	public String getIntFormat(Object value) {
		String valueFormat = "0";
		try {
			valueFormat = intFormat.format(value);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return valueFormat;
	}

	public String getDecimalFormat(Object value) {
		String valueFormat = "0.00";
		try {
			valueFormat = decimalFormat.format(value);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return valueFormat;
	}

	public int getIntValue(Object value) {
		return Integer.parseInt(getIntFormat(value));
	}

	public double getDecimalValue(Object value) {
		return Double.parseDouble(getDecimalFormat(value));
	}

	// public float getFloatValue(Object value) {
	// return Float.parseFloat(getDecimalFormat(value));
	// }
	//
	// public double getDoubleValue(Object value) {
	// return Double.parseDouble(getDecimalFormat(value));
	// }

	public static Object cloneAttributes(Object input, Object output) throws Exception {
		Object outputObject = null;

		try {
			HashMap inputGetMethods = new HashMap();
			outputObject = output;

			Method inputMethods[] = input.getClass().getDeclaredMethods();
			for (int i = 0; i < inputMethods.length; i++) {
				String methodName = inputMethods[i].getName();
				if (methodName.startsWith("get")) {
					logger.debug("inputMethods[" + i + "]: " + methodName);
					Object value = inputMethods[i].invoke(input, null);
					inputGetMethods.put(methodName, value);
				}
			}

			Method outputMethods[] = outputObject.getClass().getDeclaredMethods();
			for (int i = 0; i < outputMethods.length; i++) {
				if (outputMethods[i].getName().startsWith("set")) {
					logger.debug("outputMethods[" + i + "]: " + outputMethods[i].getName());
					String tempName = outputMethods[i].getName().substring(3);
					Object value = inputGetMethods.get("get" + tempName);
					if (value != null) {
						outputMethods[i].invoke(outputObject, new Object[] { value });
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return outputObject;
	}
}
