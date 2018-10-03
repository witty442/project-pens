package com.isecinc.pens.process.modifier;

import java.io.Serializable;

public class ModifierDescription implements Serializable {

	private static final long serialVersionUID = 8497775415745745399L;

	private String lineDescription;
	private String lineTerritory;

	public String getLineDescription() {
		return lineDescription;
	}

	public void setLineDescription(String lineDescription) {
		this.lineDescription = lineDescription;
	}

	public String getLineTerritory() {
		return lineTerritory;
	}

	public void setLineTerritory(String lineTerritory) {
		this.lineTerritory = lineTerritory;
	}
}
