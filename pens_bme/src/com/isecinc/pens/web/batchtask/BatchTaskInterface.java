package com.isecinc.pens.web.batchtask;

import java.util.List;

import com.isecinc.pens.bean.MonitorBean;

public interface BatchTaskInterface {
  public String[] getParam();
  public List<BatchTaskListBean> getParamListBox();
  public String getButtonName();
  public String getDescription();
  public String getValidateScript();
  public MonitorBean run(MonitorBean model);
  public String getDevInfo();
  public BatchTaskDispBean getBatchDisp();
}
