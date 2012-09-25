package com.isecinc.pens.web.modifier;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Modifier;

/**
 * Modifier Criteria Class
 * 
 * @author Atiz.b
 * @version $Id: ModifierCriteria.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ModifierCriteria extends I_Criteria {

	private static final long serialVersionUID = 648830945368101742L;

	private Modifier modifier = new Modifier();

	public Modifier getModifier() {
		return modifier;
	}

	public void setModifier(Modifier modifier) {
		this.modifier = modifier;
	}

}
