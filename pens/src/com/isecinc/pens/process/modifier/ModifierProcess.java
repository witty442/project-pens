package com.isecinc.pens.process.modifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import util.BeanParameter;
import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Modifier;
import com.isecinc.pens.bean.ModifierAttr;
import com.isecinc.pens.bean.ModifierLine;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.ProductC4;
import com.isecinc.pens.bean.ProductCategory;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.Qualifier;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MModifier;
import com.isecinc.pens.model.MModifierAttr;
import com.isecinc.pens.model.MModifierLine;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductCategory;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MQualifier;
import com.isecinc.pens.model.MUser;

/**
 * Modifier Process Class
 * 
 * @author Atiz.b
 * @version $Id: ModifierProcess.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ModifierProcess {

	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");

	private String LEVEL_LINE = "LINE";
	private String LEVEL_LINEGROUP = "LINEGROUP";

	/** Lines for NEWPRICE */
	// Flag Promotion = 'Y'
	private List<OrderLine> addLines = new ArrayList<OrderLine>();

	Modifier modifier = null;
	ModifierLine modifierLine = null;
	ModifierAttr modifierAttr = null;

	private String terriory = "";
	private String territoryName = "";
	boolean isDebug = true;

	/**
	 * Modifier with Territory
	 * 
	 * @param terriory
	 */
	public ModifierProcess(String terriory) {
		this.terriory = terriory;
		for (References rf : InitialReferences.getReferenes().get(InitialReferences.TERRITORY)) {
			if (rf.getKey().equalsIgnoreCase(terriory)) {
				territoryName = rf.getName();
				break;
			}
		}
	}

	/**
	 * Find Modifier
	 * 
	 * @param orderLines
	 * @param conn
	 * @throws Exception
	 */
	public void findModifier(List<OrderLine> orderLines, User user, Connection conn) throws Exception {
		logger.debug("Find Modifier..");
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String sql = "";
			Product product;

			// Clear All Discount
			for (OrderLine oline : orderLines) {
				oline.setDiscount(0);
				oline.setDiscount1(0);
				oline.setDiscount2(0);
			}

			// create statement
			stmt = conn.createStatement();
            
			if(isDebug){
			  logger.info("************** Start  LINE Modifier ************************************");  //What ? no used
			}
			
			for (OrderLine oline : orderLines) {
				
				product = new MProduct().find(String.valueOf(oline.getProduct().getId()));
				
				// ITEM CATEGORY MODIFIER
				if(isDebug){
				  logger.info("Item Category LINE Modifier.. Step by Product["+product+"]");
				}
				
				sql = createSQL(  BeanParameter.getModifierItemCategory()
						        , String.valueOf(product.getProductCategory().getId())
						        , oline.getProduct().getId(), oline.getUom().getId()
						        ,LEVEL_LINE, user);
				
				rst = stmt.executeQuery(sql);
				
				if(isDebug){
				  logger.info("Item Category LINE Modifier >> SQL[\n "+sql+"\n]");
				}
				
				processModifier(rst, oline);

				if(rst != null) { 
					rst.close();
					rst = null;
				}
				
				// ITEM MODIFIER
				if(isDebug){
				  logger.info("Item LINE Modifier..Step by Product["+product+"]");
				}
				
				sql = createSQL(  BeanParameter.getModifierItemNumber()
						        , String.valueOf(product.getId()) 
						        , oline.getProduct().getId(), oline.getUom().getId()
						        , LEVEL_LINE, user);
				
				if(isDebug){
				  logger.info("Item LINE Modifier >>SQL[\n "+sql+"\n]");
				}
				
				rst = stmt.executeQuery(sql);
				processModifier(rst, oline);

				logger.info("AddLines Size["+addLines.size()+"]");
				if(rst != null) { 
					rst.close();
					rst = null;
				}

			}// line

			if(isDebug){
			  logger.info("************** END  LINE Modifier ************************************"); 
			}
			// Check Line Group Modifier
	
			if(isDebug){
			  logger.info("************ Find Product Category From OrderLine product and distinct productCategory*******"); 
			}
			List<ProductCategory> itemCats = new ArrayList<ProductCategory>();
			ProductCategory cat;
			boolean bIn = false;
			for (OrderLine oline : orderLines) {
				cat = null;
				if (oline.getProduct().getProductCategory() != null)
					cat = new MProductCategory().find(String.valueOf(oline.getProduct().getProductCategory().getId()));
				if (cat != null) {
					if (itemCats.size() == 0) {
						if(isDebug){
						  logger.info("product_cate_id:"+cat.getId());
						}
						itemCats.add(cat);
					}
					bIn = false;
					for (ProductCategory item : itemCats) {
						if (item.getId() == cat.getId()) {
							bIn = true;
						}
					}
					//Check Duplicate 
					if (!bIn) {
						if(isDebug){
						  logger.info("product_cate_id:"+cat.getId());
						}
						itemCats.add(cat);
					}
				}
			}
			
			if(isDebug){
				logger.info("************ END Product Category From OrderLine product and distinct productCategory*******"); 
				
				logger.info(" Distinct ProductCategory ID >> itemCats size :"+(itemCats != null?itemCats.size():0));
				
				logger.info(" ************* Start  Item Category LINEGROUP Modifier *************************************");
			}
			for (ProductCategory item : itemCats) {
				if(isDebug){
				  logger.info("Loop ProductCategory Step By item"+item);
				}
				
				sql = createSQL(BeanParameter.getModifierItemCategory(), String.valueOf(item.getId()), 0, "",LEVEL_LINEGROUP, user);
				
				if(isDebug){
				  logger.info("CateId["+item.getId()+"Loop Cate LINEGROUP>> SQL[\n "+sql+"\n]");
				}
				
				rst = stmt.executeQuery(sql);
				
				processModifierLINEGROUP(rst, orderLines, BeanParameter.getModifierItemCategory());
				
				if(rst != null) { 
					rst.close();
					rst = null;
				}
			}
			
			if(isDebug){
			  logger.info(" ************* End  Item Category LINEGROUP Modifier ***************************************");
			}
			
			for (OrderLine line : orderLines) {
				line.setDiscount(line.getBestDiscount());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
	}

	public void getModifierLineDescription(ProductC4 productC4, User user) throws Exception {
		productC4.setListDescription(getModifierLineDescription(productC4.getProduct(), user));
	}

	/**
	 * Get Modifier Line Description
	 * 
	 * @param product
	 * @param user
	 * @return description
	 * @throws Exception
	 */
	public List<ModifierDescription> getModifierLineDescription(Product product, User user) throws Exception {
		List<ModifierDescription> listDesc = new ArrayList<ModifierDescription>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String sql;
			conn = new DBCPConnectionProvider().getConnection(conn);
			// ITEM CATEGORY MODIFIER
			stmt = conn.createStatement();
			logger.debug("Item Category Modifier..");
			sql = createSQL(BeanParameter.getModifierItemCategory(), String.valueOf(product.getProductCategory()
					.getId()), product.getId(), "", "", user);
			rst = stmt.executeQuery(sql);
			listDesc.addAll(getLineDescription(rst));

			// ITEM MODIFIER
			logger.debug("Item Modifier..");
			sql = createSQL(BeanParameter.getModifierItemNumber(), String.valueOf(product.getId()), product.getId(),
					"", "", user);
			rst = stmt.executeQuery(sql);
			listDesc.addAll(getLineDescription(rst));

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return listDesc;
	}

	/**
	 * Get Line Description
	 * 
	 * @param rst
	 * @return description
	 * @throws Exception
	 */
	private List<ModifierDescription> getLineDescription(ResultSet rst) throws Exception {
		List<ModifierDescription> listDesc = new ArrayList<ModifierDescription>();
		String desc = "";
		boolean useHead = false;
		boolean useLine = false;
		ModifierDescription modifierDescription;
		String whereCause = "";
		Qualifier[] quas;
		List<String> terr = new ArrayList<String>();
		while (rst.next()) {
			desc = "";
			useHead = false;
			getAllModifer(rst);
			// check start - end
			// header first then line
			useHead = DateToolsUtil.checkStartEnd(modifier.getStartDate(), modifier.getEndDate());
			if (useHead) {
				useLine = DateToolsUtil.checkStartEnd(modifierLine.getStartDate(), modifierLine.getEndDate());
			}
			if (useLine) {
				if (desc.length() > 0) desc += "<br>";
				desc += ConvertNullUtil.convertToString(modifierLine.getDescription());
				for (ModifierLine ml : modifierLine.getRelatedModifier()) {
					if (ConvertNullUtil.convertToString(ml.getDescription()).trim().length() > 0)
						desc += "<br>" + ConvertNullUtil.convertToString(ml.getDescription());
				}

				// find Qualifier to Territory

				// get Qulifier
				whereCause = "  AND QUALIFIER_CONTEXT = '" + BeanParameter.getLineQualifierContext() + "' ";
				whereCause += "  AND QUALIFIER_TYPE ='" + BeanParameter.getLineQualifierType() + "' ";
				whereCause += "  AND OPERATOR = '=' ";
				whereCause += "  AND ISEXCLUDE = 'N' ";
				whereCause += "  AND ISACTIVE = 'Y' ";
				whereCause += "  AND MODIFIER_LINE_ID = " + modifierLine.getId();
				quas = new MQualifier().search(whereCause);
				terr = new ArrayList<String>();
				if (quas != null) {
					for (Qualifier q : quas) {
						logger.debug(q);
						if (!terr.contains(q.getQualifierValue())) terr.add(q.getQualifierValue());
					}
				}
			} else {
				if(isDebug){
				  logger.debug("No Promotion..");
				}
			}
			if (desc.length() > 0) {
				if (desc.startsWith("<br>")) desc = desc.substring(4);
				modifierDescription = new ModifierDescription();
				modifierDescription.setLineDescription(desc);

				desc = "";
				for (String s : terr) {
					desc += "," + s;
				}
				if (desc.length() > 0) desc = desc.substring(1);
				modifierDescription.setLineTerritory(desc);

				listDesc.add(modifierDescription);
			}
		}
		return listDesc;
	}

	/**
	 * Process Modifier
	 * 
	 * @param rst
	 * @param oline
	 * @throws Exception
	 */
	private void processModifier(ResultSet rst, OrderLine oline) throws Exception {
		if(isDebug){
		  logger.debug("*********** processModifier***********************");
		}
		boolean useHead = false;
		boolean useLine = false;
		
		shipDate = null;
		requestDate = null;

		List<OrderLine> useLines = new ArrayList<OrderLine>();
		useLines.add(oline);

		while (rst.next()) {
            
			useHead = false;

			getAllModifer(rst);

			if(isDebug){
			  logger.info("Loop ***************************");
			}
			// check start - end
			// header first then line
			useHead = DateToolsUtil.checkStartEnd(modifier.getStartDate(), modifier.getEndDate());
			if (useHead) {
				if(isDebug){
				  logger.info("useHead:"+useHead);
				}
				useLine = DateToolsUtil.checkStartEnd(modifierLine.getStartDate(), modifierLine.getEndDate());
			}
			if(isDebug){
			  logger.info("useLine:"+useLine);
			}
			
			if (useLine) {
				// double discount = 0;
				if(isDebug){
					logger.info("Modifier .." + modifier);
					logger.info("Modifier Line.." + modifierLine);
					logger.info("Modifier Attr.." + modifierAttr);
				}
				// Discount
				if (modifierLine.getType().equalsIgnoreCase(BeanParameter.getModifierDiscount())) {
					discountProcess(oline.getQty(), oline.getLineAmount(), useLines, false);
				}
				// Pricebreak Header
				if (modifierLine.getType().equalsIgnoreCase(BeanParameter.getModifierPricebreakheader())) {
					priceBreakProcess(oline.getQty(), oline.getLineAmount(), useLines, false);
				}
				// Promotional Goods
				if (modifierLine.getType().equalsIgnoreCase(BeanParameter.getModifierPromotiongood())) {
					shipDate = oline.getShippingDate();
					requestDate = oline.getRequestDate();
					
					promotionalGoodProcess(oline.getQty());
				}
			} else {
				if(isDebug){
				  logger.info("No Promotion..");
				}
			}
			
			if(isDebug){
			  logger.info("End Loop ***************************");
			}
		}
		
		if(isDebug){
		  logger.info("*****************END processModifier ***********************");
		}
	}
	
	private String shipDate = null;
	private String requestDate = null;

	/**
	 * Process Modifier
	 * 
	 * @param rst
	 * @param oline
	 * @throws Exception
	 */
	private void processModifierLINEGROUP(ResultSet rst, List<OrderLine> orderLines, String attr) throws Exception {
    
		if(isDebug){
		  logger.info(" ******* Start processModifierLINEGROUP****************************************");
		}
		
		boolean useHead = false;
		boolean useLine = false;
		// double discount = 0;
		// double avgDisount = 0;
		List<OrderLine> noExcludeLines;
		int sumQty = 0;
		double sumAmount = 0;
		String excludeSQL = "";
		ModifierAttr[] excludeAttrs;
		boolean bExclude = false;
		
		while (rst.next()) {
			useHead = false;
			getAllModifer(rst);

			if(isDebug){
				logger.info("Loop Modifier .." + modifier);
				logger.info("Loop Modifier Line.." + modifierLine);
				logger.info("Loop Modifier Attr.." + modifierAttr);
			}
			
			// check start - end
			// header first then line
			useHead = DateToolsUtil.checkStartEnd(modifier.getStartDate(), modifier.getEndDate());
			if (useHead) {
				useLine = DateToolsUtil.checkStartEnd(modifierLine.getStartDate(), modifierLine.getEndDate());
			}
			if (useLine) {
				if(isDebug){
				  logger.info(">> Pass Modifier ..");
				}
				
				// FIND EXCLUDE ON THISLINE
				excludeSQL = " AND modifier_line_id = " + modifierLine.getId();
				excludeSQL += "  AND isexclude = 'Y'";
				excludeAttrs = new MModifierAttr().search(excludeSQL);

				if (attr.equalsIgnoreCase(BeanParameter.getModifierItemCategory())) {
					noExcludeLines = new ArrayList<OrderLine>();
					sumQty = 0;sumAmount = 0;
					
					for (OrderLine oline : orderLines) {
						if (       (oline.getProduct().getProductCategory().getId() == Integer.parseInt(modifierAttr.getProductAttributeValue()))
								&& (oline.getUom().getId().equalsIgnoreCase(modifierAttr.getProductUOM().getId()))) {

							// Same Category & UOM
							if(isDebug){
							 logger.info("Line Same Attr :CATS : " + modifierAttr.getProductAttributeValue() + " UOM : "+ modifierAttr.getProductUOM().getId() +" FIND EXCLUDE");
							}
							
							// find on ISEXCLUDE
							bExclude = false;
							if (excludeAttrs != null) {
								for (ModifierAttr excAttr : excludeAttrs) {
									if (excAttr.getProductAttribute().equalsIgnoreCase(BeanParameter.getModifierItemNumber())) {
										if(isDebug){
										  logger.info("EXCLUDE..IN ITEM_NUMBER");
										}
										if (excAttr.getProductAttributeValue().equalsIgnoreCase(String.valueOf(oline.getProduct().getId()))) {
											if(isDebug){
											  logger.info(oline.getProduct() + "...IS EXCLUDED");
											}
											
											bExclude = true;
											break;
										}
									}
								}
							} else {
								if(isDebug){
								  logger.info("NO EXCLUDE..");
								}
							}
							if (!bExclude) {
								noExcludeLines.add(oline);
								sumQty += oline.getQty();
								sumAmount += oline.getLineAmount();
							}
							
							shipDate = oline.getShippingDate();
							requestDate = oline.getRequestDate();
							
						}//if
					}//for 
					
					/**  Process Modifier with OrderLines   Process Line is no Exclude**/
					
					// Discount
					if (modifierLine.getType().equalsIgnoreCase(BeanParameter.getModifierDiscount())) {
						discountProcess(sumQty, sumAmount, noExcludeLines, true);
					}
					// Price break Header
					if (modifierLine.getType().equalsIgnoreCase(BeanParameter.getModifierPricebreakheader())) {
						priceBreakProcess(sumQty, sumAmount, noExcludeLines, true);
					}
					// Promotional Good
					if (modifierLine.getType().equalsIgnoreCase(BeanParameter.getModifierPromotiongood())) {
						promotionalGoodProcess(sumQty);
					}
					
				}//if
			} else {
				logger.info("No Promotion..");
			}
		}//for
		
		if(isDebug){
		  logger.info(" ******* END processModifierLINEGROUP****************************************");
		}

	}

	/**
	 * Get All Modifier
	 * 
	 * @param rst
	 * @throws Exception
	 */
	private void getAllModifer(ResultSet rst) throws Exception {

		modifier = new Modifier();
		modifierLine = new ModifierLine();
		modifierAttr = new ModifierAttr();

		modifier = new MModifier().find(rst.getString("MODIFIER_ID"));
		modifierLine = new MModifierLine().find(rst.getString("MODIFIER_LINE_ID"));
		modifierAttr = new MModifierAttr().find(rst.getString("MODIFIER_ATTR_ID"));
		return;
	}

	/**
	 * Discount Process
	 * 
	 * @param orline
	 * @return discount
	 * @throws Exception
	 */
	private double discountProcess(double sumQty, double sumAmount, List<OrderLine> useLines, boolean blineGroup)
			throws Exception {
		if(isDebug){
		  logger.info("----Start Discount Process-----");
		}
		boolean isPromotion = false;
		double discount = 0;

		if(isDebug){
		  logger.info("Qty["+ sumQty+"] to Get Discount.. Attr.valueFrom[" + modifierAttr.getValueFrom()+"]to["+modifierAttr.getValueTo()+"]");
		}
		
		/** OLD Code **/
		/*if (sumQty >= modifierAttr.getValueFrom()) {
			isPromotion = true;
		}
		*/
		
		/** Wit Edit 29/08/2012 **/
		if( modifierAttr.getValueTo() != 0){
			if (sumQty >= modifierAttr.getValueFrom() && sumQty <=modifierAttr.getValueTo()) {
				isPromotion = true;
			}
		}else{
			if (sumQty >= modifierAttr.getValueFrom()) {
				isPromotion = true;
			}
		}
		
		
		// Is Promotion
		if (isPromotion) {
			if(isDebug){
			  logger.info("Is Promotion..BreakType["+modifierLine.getBreakType()+"] App Method[" + modifierLine.getApplicationMethod() + "]Value[" + modifierLine.getValues()+"]");
			}
			
			if (modifierLine.getBreakType().equalsIgnoreCase(BeanParameter.getBreakTypePoint())) {
				// POINT
				if (modifierLine.getApplicationMethod().equalsIgnoreCase(BeanParameter.getAppMethodAMT())) {
					// AMT
					for (OrderLine line : useLines) {
						// atiz.b 20110707
						if (line.getQty() >= modifierAttr.getValueFrom()) {
							if (modifierAttr.getValueTo() != 0) {
								if (line.getQty() <= modifierAttr.getValueTo()) {
									discount = line.getQty() * modifierLine.getValues();
									if (line.getBestDiscount() == 0) line.setBestDiscount(discount);
									if (line.getBestDiscount() < discount) line.setBestDiscount(discount);
								}
							} else {
								discount = line.getQty() * modifierLine.getValues();
								if (line.getBestDiscount() == 0) line.setBestDiscount(discount);
								if (line.getBestDiscount() < discount) line.setBestDiscount(discount);
							}
							logger.info("LineProduct["+line.getProduct().getCode()+"] BreakType[Point] method[AMT] :BestDiscount["+line.getBestDiscount()+"] result discount["+discount+"]");
						}//if
					}//for
					
				} else if (modifierLine.getApplicationMethod().equalsIgnoreCase(BeanParameter.getAppMethodPercent())) {
					// PERCENT
					for (OrderLine line : useLines) {
						// atiz.b 20110707
						if (line.getQty() >= modifierAttr.getValueFrom()) {
							if (modifierAttr.getValueTo() != 0) {
								if (line.getQty() <= modifierAttr.getValueTo()) {
									discount = line.getLineAmount() * modifierLine.getValues() / 100;
									if (line.getBestDiscount() == 0) line.setBestDiscount(discount);
									if (line.getBestDiscount() < discount) line.setBestDiscount(discount);
								}
							} else {
								discount = line.getLineAmount() * modifierLine.getValues() / 100;
								if (line.getBestDiscount() == 0) line.setBestDiscount(discount);
								if (line.getBestDiscount() < discount) line.setBestDiscount(discount);
							}
							if(isDebug){
							  logger.info("LineProduct["+line.getProduct().getCode()+"] BreakType[point] Method[AMT Percent] :BestDiscount["+line.getBestDiscount()+"] result discount["+discount+"]");
							}
						}//if
					}//for
				}

			}

			// RECURRING
			if (modifierLine.getBreakType().equalsIgnoreCase(BeanParameter.getBreakTypeRecurring())) {
				if(isDebug){
				  logger.info("MLine Break Recurring");
				}
				
				// LUMSUM
				discount = 0;
				double lineDiscount = 0;
				if (sumQty >= modifierAttr.getValueFrom()) {
					discount = new Double(sumQty / modifierAttr.getValueFrom()).intValue();
					discount *= modifierLine.getValues();
					for (OrderLine line : useLines) {
						lineDiscount = discount * line.getQty() / sumQty;
						if (line.getBestDiscount() == 0) line.setBestDiscount(lineDiscount);
						if (line.getBestDiscount() < lineDiscount) line.setBestDiscount(lineDiscount);
						
						if(isDebug){
						  logger.info("BreakType[RECURRING] :BestDiscount["+line.getBestDiscount()+"] result lineDiscount["+lineDiscount+"]");
						}
					}
				}
			}
		} else {
			if(isDebug){
			  logger.info("No Promotion..");
			}
		}
		
		if(isDebug){
		  logger.info("---- End  Discount Process---- ");
		}
		
		return 0;

	}

	/**
	 * Price Break Process
	 * 
	 * @param orline
	 * @return price break discount
	 * @throws Exception
	 * Discount By Step 
	 */
	private double priceBreakProcess(double sumQty, double sumAmount, List<OrderLine> useLines, boolean blineGroup)
			throws Exception {
		if(isDebug){
		  logger.info("---- Start PriceBreak Process-------");
		}
		
		boolean isPromotion = false;
		double discount = 0;
		ModifierLine promoLine = null;
		//logger.info("Relate Lines..(m_line_from -> m_line_to)" + modifierLine.getRelatedModifier().size());
		
		// get relate line
		for (ModifierLine mrline : modifierLine.getRelatedModifier()) {
			if(isDebug){
			  logger.info("Relate MLines : " + mrline);
			  logger.info("Qty["+sumQty+"] to Get Discount.. mLineTo.Attr.ValueFrom" + mrline.getAttr().getValueFrom() + " to "+ mrline.getAttr().getValueTo());
			}
			
			if (sumQty >= mrline.getAttr().getValueFrom() && sumQty <= mrline.getAttr().getValueTo()) {
				promoLine = mrline;
				isPromotion = true;
				break;
			}
		}
		if (isPromotion) {
			if(isDebug){
			  logger.info("Is Promotion..BreakType["+promoLine.getBreakType()+"]Application Method[" + promoLine.getApplicationMethod()+"]");
			  logger.info("ProLine.." + promoLine);
			 //logger.debug(" + " Value : "+ promoLine.getValues());
			}
			
			if (promoLine.getBreakType().equalsIgnoreCase(BeanParameter.getBreakTypePoint())) {
				// POINT
				if (promoLine.getApplicationMethod().equalsIgnoreCase(BeanParameter.getAppMethodAMT())) {
					// AMT
					for (OrderLine line : useLines) {
						discount = line.getQty() * promoLine.getValues();
						if (line.getBestDiscount() == 0) line.setBestDiscount(discount);
						if (line.getBestDiscount() < discount) line.setBestDiscount(discount);
						
						if(isDebug){
						  logger.info("BreakType[Point] Method[AMT] :BestDiscount["+line.getBestDiscount()+"] result discount["+discount+"]");
						}
					}
				} else if (promoLine.getApplicationMethod().equalsIgnoreCase(BeanParameter.getAppMethodPercent())) {
					// PERCENT
					for (OrderLine line : useLines) {
						discount = line.getLineAmount() * promoLine.getValues() / 100;
						if (line.getBestDiscount() == 0) line.setBestDiscount(discount);
						if (line.getBestDiscount() < discount) line.setBestDiscount(discount);
					
						if(isDebug){
						  logger.info("BreakType[Point] Method[AMT Percent]:BestDiscount["+line.getBestDiscount()+"] result discount["+discount+"]");
						}
					}
				}
			}

			// RECURRING
			if (promoLine.getBreakType().equalsIgnoreCase(BeanParameter.getBreakTypeRecurring())) {
		
				// LUMSUM
				discount = 0;
				double lineDiscount = 0;
				if (sumQty >= modifierAttr.getValueFrom()) {
					discount = new Double(sumQty / modifierAttr.getValueFrom()).intValue();
					discount *= modifierLine.getValues();
					for (OrderLine line : useLines) {
						lineDiscount = discount * line.getQty() / sumQty;
						if (line.getBestDiscount() == 0) line.setBestDiscount(lineDiscount);
						if (line.getBestDiscount() < lineDiscount) line.setBestDiscount(lineDiscount);
						
						if(isDebug){
						  logger.info("BreakType[RECURRING] :BestDiscount["+line.getBestDiscount()+"] result lineDiscount["+lineDiscount+"]");
						}
					}//for
				}//if
			}
		} else {
			if(isDebug){
			  logger.info("No Promotion..");
			}
		}
		
		if(isDebug){
		  logger.info("---- End PriceBreak Process-------");
		}
		
		return 0;
	}

	/**
	 * Promotion Good Process
	 * 
	 * @param orline
	 * @return promotion good discount & add lines
	 * @throws Exception
	 */
	private double promotionalGoodProcess(double sumQty) throws Exception {
		if(isDebug){
		  logger.info("---- Start Promotional Good Process ----");
		}
		
		OrderLine oLine;
		List<ProductPrice> pps;
		boolean isPromotion = false;
		double price = 0;
		double discount = 0;
		double qty = 0;
		int round = 1;
		
		if(isDebug){
		  logger.info("Mline Break Type[" + modifierLine.getBreakType()+"]");
		}
		
		if (modifierLine.getBreakType().equalsIgnoreCase(BeanParameter.getBreakTypeRecurring())) {
			if(isDebug){
			  logger.info("Case BreakType[Recurring] -> Qty["+sumQty+"] Check Qty In Promotion Attr.valueFrom["+ modifierAttr.getValueFrom()+"] to ["+modifierAttr.getValueTo()+"]");
			}
			
			// recurring
			if (modifierAttr.getValueTo() != 0) {
				if (sumQty >= modifierAttr.getValueFrom() && sumQty <= modifierAttr.getValueTo()) {
					isPromotion = true;
					round = new Double(sumQty / modifierAttr.getValueFrom()).intValue();
				}
			} else {
				if (sumQty >= modifierAttr.getValueFrom()) {
					isPromotion = true;
					round = new Double(sumQty / modifierAttr.getValueFrom()).intValue();
				}
			}
			
		} else {
			
			if (modifierLine.getBreakType().equalsIgnoreCase(BeanParameter.getBreakTypePoint())) {
				if(isDebug){
				  logger.info("Case BreakType[Point] -> Qty["+sumQty+"] Check Qty In Promotion Attr.valueFrom["+ modifierAttr.getValueFrom()+"] to ["+modifierAttr.getValueTo()+"]");
				}
				
				if (modifierAttr.getValueTo() != 0) {
					if (sumQty >= modifierAttr.getValueFrom() && sumQty <= modifierAttr.getValueTo()) {
						isPromotion = true;
						round = 1;
					}
				} else {
					if (sumQty >= modifierAttr.getValueFrom()) {
						isPromotion = true;
						round = 1;
					}
				}
			}
		}
		// Is Promotion
		if (isPromotion) {
			if(isDebug){
			  logger.info("Is Promotion..Round[" + round+"]");
			  logger.info("Relate relate.m_line_to.MLines Size[" + modifierLine.getRelatedModifier().size()+"]");
			}
			
			// get relate line
			boolean baseUOM = true;
			for (ModifierLine mrline : modifierLine.getRelatedModifier()) {
				price = 0;discount = 0;qty = 0;
				baseUOM = true;

				oLine = new OrderLine();
				if(isDebug){
				  logger.info("Attr.Product Attibute[" + mrline.getAttr().getProductAttribute()+"] Attibute Value[" + mrline.getAttr().getAttributeValueLabel()+"]");
				}
				
				if (mrline.getAttr().getProductAttribute().equalsIgnoreCase(BeanParameter.getModifierItemNumber())) {
					// promotion in item
					oLine.setProduct(mrline.getAttr().getProduct());
					
					if(isDebug){
					  logger.info(mrline.getAttr().getProduct().getUom().getId());
					}
				}
				if (!mrline.getAttr().getProductUOM().getId().equalsIgnoreCase(mrline.getAttr().getProduct().getUom().getId())) {
					baseUOM = false;
				}

				pps = new MProductPrice().getCurrentPrice(String.valueOf(oLine.getProduct().getId()), String.valueOf(mrline.getPricelistId()), mrline.getAttr().getProductUOM().getId());
				for (ProductPrice pp : pps){
					price = pp.getPrice();
				}
				
				if (mrline.getApplicationMethod().equalsIgnoreCase(BeanParameter.getAppMethodPercent())) {
					if (mrline.getType().equalsIgnoreCase(BeanParameter.getModifierDiscount())) {
						discount = price * mrline.getValues() * 100;
					}
					if(isDebug){
					  logger.info(" Method[AMT Percent] :result discount["+discount+"]");
					}
				}
				if (mrline.getApplicationMethod().equalsIgnoreCase(BeanParameter.getAppMethodAMT())) {
					if (mrline.getType().equalsIgnoreCase(BeanParameter.getModifierDiscount())) {
						discount = mrline.getValues();
					}
					
					if(isDebug){
					  logger.info(" Method[AMT] :result discount["+discount+"]");
					}
				}
				
				if (mrline.getApplicationMethod().equalsIgnoreCase(BeanParameter.getAppMethodNewPrice())) {
					price = mrline.getValues();
				}
				
				qty = round * mrline.getBenefitQty();

				logger.info("new Promotion Qty["+ qty+"]");
				  
				if (baseUOM) {
					oLine.setUom(mrline.getAttr().getProductUOM());
					oLine.setPrice(price);
					oLine.setQty(qty);
					oLine.setLineAmount(price * qty);
					oLine.setDiscount(discount);
					oLine.setTotalAmount(oLine.getLineAmount() - discount);

					oLine.setUom1(mrline.getAttr().getProductUOM());
					oLine.setPrice1(price);
					oLine.setQty1(qty);
					oLine.setLineAmount1(price * qty);
					oLine.setDiscount1(discount);
					oLine.setTotalAmount1(oLine.getLineAmount() - discount);
				} else {
					oLine.setUom(mrline.getAttr().getProductUOM());
					oLine.setPrice(price);
					oLine.setQty(qty);
					oLine.setLineAmount(price * qty);
					oLine.setDiscount(discount);
					oLine.setTotalAmount(oLine.getLineAmount() - discount);

					oLine.setUom2(mrline.getAttr().getProductUOM());
					oLine.setPrice2(price);
					oLine.setQty2(qty);
					oLine.setLineAmount2(price * qty);
					oLine.setDiscount2(discount);
					oLine.setTotalAmount2(oLine.getLineAmount() - discount);
				}
				oLine.setPromotion("Y");
				
				if(this.requestDate == null)
					oLine.setRequestDate(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));
				else
					oLine.setRequestDate(requestDate);
				
				if(this.shipDate == null)
					oLine.setShippingDate(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));
				else
					oLine.setShippingDate(shipDate);

				if(isDebug){
				   logger.info("new Promotion Order Line.." + oLine);
				}
				
				logger.info("Qty1["+oLine.getQty1()+"]qty2["+oLine.getQty2()+"]qty["+oLine.getQty()+"]");
				
				addLines.add(oLine);
			}
		} else {
			if(isDebug){
			  logger.info("No Promotion..");
			}
		}
		
		if(isDebug){
		  logger.info("---- End Promotional Good Process ----");
		}
		
		return 0;
	}

	/**
	 * Create SQL
	 * 
	 * @param attr
	 * @param attrValue
	 * @param uom
	 * @return SQL String
	 */
	private String createSQL(String attr, String attrValue, int productId, String uom, String levelType, User user) {
		String sql = "";
		// Product Category Modifier
		sql = "select a.MODIFIER_ATTR_ID, a.MODIFIER_LINE_ID, a.MODIFIER_ID \r\n";
		sql += "from m_modifier_attr a, m_modifier b \r\n";
		sql += "where 1=1 \r\n";
		// FIXED : Null Pointer Exception When Promotion Attribute Has Product_UOM_ID Null
		//sql += "and a.product_uom_id is not null \r\n";
		
		sql += "  and b.isactive = 'Y' \r\n";
		sql += "  and a.isexclude = 'N' \r\n";
		sql += "  and a.MODIFIER_ID = b.MODIFIER_ID \r\n";
		// sql += "  and date_format(b.start_date,'%Y%m%d') <= date_format(current_timestamp,'%Y%m%d') ";
		// sql += "  and date_format(b.end_date,'%Y%m%d') >= date_format(current_timestamp,'%Y%m%d') ";
		sql += "  and a.product_attribute = '" + attr + "' \r\n";
		sql += "  and a.product_attribute_value = '" + attrValue + "' \r\n";
		if (uom.length() > 0) sql += "  and a.product_uom_id = '" + uom + "' \r\n";
		sql += "  and a.MODIFIER_LINE_ID not in (select rm.MODIFIER_LINE_TO_ID from m_relation_modifier rm) \r\n";

		// only Line Level
		if (levelType.length() > 0)
			sql += "  and a.MODIFIER_LINE_ID in (select rm.MODIFIER_LINE_ID from m_modifier_line rm WHERE LEVEL='"
					+ levelType + "' and isactive = 'Y' ) \r\n";

		// join order qualifier
		sql += "  and a.modifier_id IN (\r\n";
		sql += " select modifier_id from m_qualifier \r\n";
		sql += " where operator = '=' \r\n";
		sql += "   and isexclude = 'N' \r\n";
		sql += "   and isactive = 'Y' \r\n";
		sql += "   and QUALIFIER_CONTEXT = '" + BeanParameter.getQualifierContext() + "' \r\n";
		sql += "   and qualifier_type = '" + BeanParameter.getQualifierType() + "' \r\n";
		if (user.getType().equalsIgnoreCase(User.VAN))
			sql += "   and QUALIFIER_VALUE = '" + BeanParameter.getQualifierVAN() + "' \r\n";

		if (user.getType().equalsIgnoreCase(User.DD))
			sql += "   and QUALIFIER_VALUE = '" + BeanParameter.getQualifierDD() + "' \r\n";

		if (user.getType().equalsIgnoreCase(User.TT))
			sql += "   and QUALIFIER_VALUE = '" + BeanParameter.getQualifierTT() + "' \r\n";

		sql += ")\r\n";

		// join customer qualifier
		if (terriory.length() > 0) {

			// in territory
			sql += "  and (a.MODIFIER_LINE_ID IN (\r\n";
			sql += "select MODIFIER_LINE_ID \r\n";
			sql += "from m_qualifier \r\n";
			sql += "where QUALIFIER_CONTEXT = '" + BeanParameter.getLineQualifierContext() + "' \r\n";
			sql += "  and QUALIFIER_TYPE = '" + BeanParameter.getLineQualifierType() + "' \r\n";
			sql += "  and OPERATOR = '=' \r\n";
			sql += "  and ISEXCLUDE = 'N' \r\n";
			sql += "  and ISACTIVE = 'Y' \r\n";
			sql += "  and QUALIFIER_VALUE = '" + territoryName + "' \r\n";
			sql += ") \r\n";

			// no qualifier
			sql += "  or a.MODIFIER_LINE_ID NOT IN (\r\n";
			sql += "select MODIFIER_LINE_ID \r\n";
			sql += "from m_qualifier \r\n";
			sql += "where QUALIFIER_CONTEXT = '" + BeanParameter.getLineQualifierContext() + "' \r\n";
			sql += "  and QUALIFIER_TYPE = '" + BeanParameter.getLineQualifierType() + "' \r\n";
			sql += "  and OPERATOR = '=' \r\n";
			sql += "  and ISEXCLUDE = 'N' \r\n";
			sql += "  and ISACTIVE = 'Y' \r\n";
			sql += ")) \r\n";
		}

		// exclude
		if (productId != 0) {
			sql += "  and a.MODIFIER_LINE_ID not in( \r\n";
			sql += "	select MODIFIER_LINE_ID from m_modifier_attr \r\n";
			sql += "  where 1=1 \r\n";
			sql += "	  and product_attribute = '" + BeanParameter.getModifierItemCategory() + "' \r\n";
			sql += "	  and product_attribute_value = '" + productId + "' \r\n";
			sql += "	  and isexclude = 'Y' \r\n";
			sql += "	) \r\n";
		}
		
		return sql;
	}

	/**
	 * Get Add Lines
	 * 
	 * @return get Promotion Good Lines
	 */
	public List<OrderLine> getAddLines() {
		return addLines;
	}

	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			InitialReferences init = new InitialReferences();
			init.init(conn);

			List<OrderLine> orderLines = new ArrayList<OrderLine>();
			orderLines.add(new MOrderLine().find("198"));

			new ModifierProcess("").findModifier(orderLines, new MUser().find("2"), conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

	}
}
