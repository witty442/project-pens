package com.isecinc.pens.web.modifier;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Modifier;
import com.isecinc.pens.bean.ModifierLine;

/**
 * Modifier Form Class
 * 
 * @author Atiz.b
 * @version $Id: ModifierForm.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ModifierForm extends I_Form {

	private static final long serialVersionUID = 1L;

	private ModifierCriteria criteria = new ModifierCriteria();

	private Modifier[] results = null;

	private List<ModifierLine> lines = null;

	public ModifierCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ModifierCriteria criteria) {
		this.criteria = criteria;
	}

	public Modifier getModifier() {
		return criteria.getModifier();
	}

	public void setModifier(Modifier modifier) {
		criteria.setModifier(modifier);
	}

	public Modifier[] getResults() {
		return results;
	}

	public void setResults(Modifier[] results) {
		this.results = results;
	}

	public List<ModifierLine> getLines() {
		return lines;
	}

	public void setLines(List<ModifierLine> lines) {
		this.lines = lines;
	}

}
