package com.isecinc.pens.web.admin;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.DocSequence;

/**
 * DocSeq Form Class
 * 
 * @author Atiz.b
 * @version $Id: DocSequenceForm.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class DocSequenceForm extends I_Form {

	private static final long serialVersionUID = 6833240487110040200L;

	private DocSequenceCriteria criteria = new DocSequenceCriteria();

	private DocSequence[] results = null;

	public DocSequenceCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DocSequenceCriteria criteria) {
		this.criteria = criteria;
	}

	public DocSequence[] getResults() {
		return results;
	}

	public void setResults(DocSequence[] results) {
		this.results = results;
	}

	public DocSequence getDocSequence() {
		return criteria.getDocSequence();
	}

	public void setDocSequence(DocSequence docSequence) {
		criteria.setDocSequence(docSequence);
	}

}
