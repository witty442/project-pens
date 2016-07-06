package com.isecinc.pens.web.summary;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.DiffStockSummary;
import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.PhysicalSummary;
import com.isecinc.pens.bean.TransactionSummary;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class SummaryForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private SummaryCriteria criteria = new SummaryCriteria();

	private List<OnhandSummary> onhandSummaryResults;
	private List<OnhandSummary> onhandSummaryLotusResults;
	private List<OnhandSummary> onhandSummaryMTTResults;
	private List<OnhandSummary> onhandSummaryMTTDetailResults;
	private List<OnhandSummary> onhandBigCResults;
	private List<OnhandSummary> onhandSummaryLotusPeriodResults;
	private List<OnhandSummary> onhandSummaryBmeTransResults;
	private List<OnhandSummary> onhandSummarySizeColorBigCResults;
	private List<OnhandSummary> onhandSummarySizeColorLotusResults;
	private List<OnhandSummary> onhandSummaryMonthEndLotusResults;
	
	private List<TransactionSummary> lotusSummaryResults;
	private List<TransactionSummary> bigcSummaryResults;
	private List<TransactionSummary> topsSummaryResults;
	private List<TransactionSummary> kingSummaryResults;
	private List<PhysicalSummary> physicalSummaryResults;
	private List<DiffStockSummary> diffStockSummaryLists;
	private List<TransactionSummary> summaryByGroupCodeResults;
	
	
	private int onhandSummaryResultsSize;
	private int onhandSummaryLotusResultsSize;
	private int onhandSummaryBmeTransResultsSize;
	private int onhandSummaryMTTResultsSize;
	private int onhandBigCResultsSize;
	private int onhandSummaryLotusPeriodResultsSize;
	private int lotusSummaryResultsSize;
	private int bigcSummaryResultsSize;
	private int topsSummaryResultsSize;
	private int physicalSummaryResultsSize;
	private int diffStockSummaryResultsSize;
	private int summaryByGroupCodeResultsSize;


	
	public List<OnhandSummary> getOnhandSummaryMonthEndLotusResults() {
		return onhandSummaryMonthEndLotusResults;
	}
	public void setOnhandSummaryMonthEndLotusResults(
			List<OnhandSummary> onhandSummaryMonthEndLotusResults) {
		this.onhandSummaryMonthEndLotusResults = onhandSummaryMonthEndLotusResults;
	}
	public List<OnhandSummary> getOnhandSummarySizeColorLotusResults() {
		return onhandSummarySizeColorLotusResults;
	}
	public void setOnhandSummarySizeColorLotusResults(
			List<OnhandSummary> onhandSummarySizeColorLotusResults) {
		this.onhandSummarySizeColorLotusResults = onhandSummarySizeColorLotusResults;
	}
	public List<OnhandSummary> getOnhandSummarySizeColorBigCResults() {
		return onhandSummarySizeColorBigCResults;
	}
	public void setOnhandSummarySizeColorBigCResults(
			List<OnhandSummary> onhandSummarySizeColorBigCResults) {
		this.onhandSummarySizeColorBigCResults = onhandSummarySizeColorBigCResults;
	}
	public List<OnhandSummary> getOnhandSummaryMTTDetailResults() {
		return onhandSummaryMTTDetailResults;
	}
	public void setOnhandSummaryMTTDetailResults(
			List<OnhandSummary> onhandSummaryMTTDetailResults) {
		this.onhandSummaryMTTDetailResults = onhandSummaryMTTDetailResults;
	}
	public List<TransactionSummary> getKingSummaryResults() {
		return kingSummaryResults;
	}
	public void setKingSummaryResults(List<TransactionSummary> kingSummaryResults) {
		this.kingSummaryResults = kingSummaryResults;
	}
	public List<OnhandSummary> getOnhandSummaryBmeTransResults() {
		return onhandSummaryBmeTransResults;
	}
	public void setOnhandSummaryBmeTransResults(
			List<OnhandSummary> onhandSummaryBmeTransResults) {
		this.onhandSummaryBmeTransResults = onhandSummaryBmeTransResults;
	}
	
	public List<TransactionSummary> getSummaryByGroupCodeResults() {
		return summaryByGroupCodeResults;
	}
	public void setSummaryByGroupCodeResults(
			List<TransactionSummary> summaryByGroupCodeResults) {
		this.summaryByGroupCodeResults = summaryByGroupCodeResults;
	}
	public int getSummaryByGroupCodeResultsSize() {
		return summaryByGroupCodeResults !=null?summaryByGroupCodeResults.size():0;
	}

	public List<OnhandSummary> getOnhandSummaryMTTResults() {
		return onhandSummaryMTTResults;
	}
	public void setOnhandSummaryMTTResults(
			List<OnhandSummary> onhandSummaryMTTResults) {
		this.onhandSummaryMTTResults = onhandSummaryMTTResults;
	}
	public int getOnhandSummaryMTTResultsSize() {
		return getOnhandSummaryMTTResults().size();
	}
	public void setOnhandSummaryMTTResultsSize(int onhandSummaryMTTResultsSize) {
		this.onhandSummaryMTTResultsSize = onhandSummaryMTTResultsSize;
	}
	public int getTopsSummaryResultsSize() {
		return topsSummaryResults != null?topsSummaryResults.size():0;
	}
	public void setTopsSummaryResultsSize(int topsSummaryResultsSize) {
		this.topsSummaryResultsSize = topsSummaryResultsSize;
	}
	public List<TransactionSummary> getTopsSummaryResults() {
		return topsSummaryResults;
	}
	public void setTopsSummaryResults(List<TransactionSummary> topsSummaryResults) {
		this.topsSummaryResults = topsSummaryResults;
	}
	public List<OnhandSummary> getOnhandBigCResults() {
		return onhandBigCResults;
	}
	public void setOnhandBigCResults(List<OnhandSummary> onhandBigCResults) {
		this.onhandBigCResults = onhandBigCResults;
	}
	public List<OnhandSummary> getOnhandSummaryLotusPeriodResults() {
		return onhandSummaryLotusPeriodResults;
	}
	public void setOnhandSummaryLotusPeriodResults(
			List<OnhandSummary> onhandSummaryLotusPeriodResults) {
		this.onhandSummaryLotusPeriodResults = onhandSummaryLotusPeriodResults;
	}
	public int getOnhandSummaryLotusPeriodResultsSize() {
		return onhandSummaryLotusPeriodResultsSize;
	}
	public void setOnhandSummaryLotusPeriodResultsSize(
			int onhandSummaryLotusPeriodResultsSize) {
		this.onhandSummaryLotusPeriodResultsSize = onhandSummaryLotusPeriodResultsSize;
	}
	public List<OnhandSummary> getOnhandSummaryLotusResults() {
		return onhandSummaryLotusResults;
	}
	public void setOnhandSummaryLotusResults(
			List<OnhandSummary> onhandSummaryLotusResults) {
		this.onhandSummaryLotusResults = onhandSummaryLotusResults;
	}
	public int getOnhandSummaryLotusResultsSize() {
		return onhandSummaryLotusResultsSize;
	}
	public void setOnhandSummaryLotusResultsSize(int onhandSummaryLotusResultsSize) {
		this.onhandSummaryLotusResultsSize = onhandSummaryLotusResultsSize;
	}
	public List<TransactionSummary> getBigcSummaryResults() {
		return bigcSummaryResults;
	}
	public void setBigcSummaryResults(List<TransactionSummary> bigcSummaryResults) {
		this.bigcSummaryResults = bigcSummaryResults;
	}
	public int getBigcSummaryResultsSize() {
		return bigcSummaryResultsSize;
	}
	public void setBigcSummaryResultsSize(int bigcSummaryResultsSize) {
		this.bigcSummaryResultsSize = bigcSummaryResultsSize;
	}
	public List<DiffStockSummary> getDiffStockSummaryLists() {
		return diffStockSummaryLists;
	}
	public void setDiffStockSummaryLists(List<DiffStockSummary> diffStockSummaryLists) {
		this.diffStockSummaryLists = diffStockSummaryLists;
	}
	public int getDiffStockSummaryResultsSize() {
		return diffStockSummaryLists!=null?diffStockSummaryLists.size():0;
	}
	public void setDiffStockSummaryResultsSize(int diffStockSummaryResultsSize) {
		this.diffStockSummaryResultsSize = diffStockSummaryResultsSize;
	}
	public void setOnhandSummaryResultsSize(int onhandSummaryResultsSize) {
		this.onhandSummaryResultsSize = onhandSummaryResultsSize;
	}
	
	public void setPhysicalSummaryResultsSize(int physicalSummaryResultsSize) {
		this.physicalSummaryResultsSize = physicalSummaryResultsSize;
	}
	public int getPhysicalSummaryResultsSize() {
		return physicalSummaryResults!=null?physicalSummaryResults.size():0;
	}
	
	public int getOnhandSummaryResultsSize() {
		return onhandSummaryResults!=null?onhandSummaryResults.size():0;
	}
    
	
	public int getOnhandBigCResultsSize() {
		return onhandBigCResults!=null?onhandBigCResults.size():0;
	}
	public void setOnhandBigCResultsSize(int onhandBigCResultsSize) {
		this.onhandBigCResultsSize = onhandBigCResultsSize;
	}
	public List<PhysicalSummary> getPhysicalSummaryResults() {
		return physicalSummaryResults;
	}
	public void setPhysicalSummaryResults(
			List<PhysicalSummary> physicalSummaryResults) {
		this.physicalSummaryResults = physicalSummaryResults;
	}
	public SummaryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SummaryCriteria criteria) {
		this.criteria = criteria;
	}
	
	public List<OnhandSummary> getOnhandSummaryResults() {
		return onhandSummaryResults;
	}


	public void setOnhandSummaryResults(List<OnhandSummary> onhandSummaryResults) {
		this.onhandSummaryResults = onhandSummaryResults;
	}
    

	public List<TransactionSummary> getLotusSummaryResults() {
		return lotusSummaryResults;
	}
	public void setLotusSummaryResults(List<TransactionSummary> lotusSummaryResults) {
		this.lotusSummaryResults = lotusSummaryResults;
	}
	public int getLotusSummaryResultsSize() {
		return lotusSummaryResultsSize;
	}
	public void setLotusSummaryResultsSize(int lotusSummaryResultsSize) {
		this.lotusSummaryResultsSize = lotusSummaryResultsSize;
	}
	public PhysicalSummary getPhysicalSummary() {
		return criteria.getPhysicalSummary();
	}

	public void setPhysicalSummary(PhysicalSummary summary) {
		criteria.setPhysicalSummary(summary);
	}
	
	public OnhandSummary getOnhandSummary() {
		return criteria.getOnhandSummary();
	}

	public void setOnhandSummary(OnhandSummary summary) {
		criteria.setOnhandSummary(summary);
	}

	public TransactionSummary getTransactionSummary() {
		return criteria.getTransactionSummary();
	}

	public void setTransactionSummary(TransactionSummary summary) {
		criteria.setTransactionSummary(summary);
	}
	
	public DiffStockSummary getDiffStockSummary() {
		return criteria.getDiffStockSummary();
	}

	public void setDiffStockSummary(DiffStockSummary summary) {
		criteria.setDiffStockSummary(summary);
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		// reset properties
		getOnhandSummary().setDispZeroStock("");
		getOnhandSummary().setDispHaveQty("");
		getDiffStockSummary().setHaveQty("");
	}
    
}
