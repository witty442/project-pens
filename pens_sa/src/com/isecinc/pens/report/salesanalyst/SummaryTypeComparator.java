package com.isecinc.pens.report.salesanalyst;

import java.util.Comparator;

import com.isecinc.core.bean.References;

public class SummaryTypeComparator implements Comparator<References> {
	  @Override
	  public int compare(References s1, References s2) {
          return s1.getKey().compareToIgnoreCase(s2.getKey());
       }
}
