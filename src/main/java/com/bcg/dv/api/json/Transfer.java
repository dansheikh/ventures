package com.bcg.dv.api.json;

import java.math.BigDecimal;

public class Transfer {
  public Integer payerId;
  public Integer payeeId;
  public BigDecimal transAmt;

  public Integer getPayerId() {
    return payerId;
  }

  public void setPayerId(Integer payerId) {
    this.payerId = payerId;
  }

  public Integer getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Integer payeeId) {
    this.payeeId = payeeId;
  }

  public BigDecimal getTransAmt() {
    return transAmt;
  }

  public void setTransAmt(BigDecimal transAmt) {
    this.transAmt = transAmt;
  }
}
