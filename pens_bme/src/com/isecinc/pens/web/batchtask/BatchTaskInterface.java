package com.isecinc.pens.web.batchtask;

import com.isecinc.pens.inf.bean.MonitorBean;

public interface BatchTaskInterface {
  public String getParam();
  public String getValidateScript();
  public MonitorBean run(MonitorBean model);
}
