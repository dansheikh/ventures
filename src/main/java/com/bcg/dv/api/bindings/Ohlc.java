package com.bcg.dv.api.bindings;

public class Ohlc {
  private static class TimePrice {
    private Double price;
    private Long time;

    public Double getPrice() {
      return price;
    }

    public void setPrice(Double price) {
      this.price = price;
    }

    public Long getTime() {
      return time;
    }

    public void setTime(Long time) {
      this.time = time;
    }
  }

  private TimePrice open;
  private TimePrice close;
  private Double high;
  private Double low;

  public TimePrice getOpen() {
    return open;
  }

  public void setOpen(TimePrice open) {
    this.open = open;
  }

  public TimePrice getClose() {
    return close;
  }

  public void setClose(TimePrice close) {
    this.close = close;
  }

  public Double getHigh() {
    return high;
  }

  public void setHigh(Double high) {
    this.high = high;
  }

  public Double getLow() {
    return low;
  }

  public void setLow(Double low) {
    this.low = low;
  }

}
