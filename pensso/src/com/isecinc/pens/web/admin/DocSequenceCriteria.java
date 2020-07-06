package com.isecinc.pens.web.admin;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.DocSequence;

/**
 * DocSeq Criteria Class
 * 
 * @author Atiz.b
 * @version $Id: DocSequenceCriteria.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class DocSequenceCriteria extends I_Criteria {

	private static final long serialVersionUID = 5867435490290836839L;

	private DocSequence docSequence = new DocSequence();

	public DocSequence getDocSequence() {
		return docSequence;
	}

	public void setDocSequence(DocSequence docSequence) {
		this.docSequence = docSequence;
	}

}
