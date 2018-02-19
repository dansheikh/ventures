package com.bcg.dv.api.contracts;

import com.bcg.dv.api.bindings.Ohlc;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface IEXContract {
  @GET("stock/{stock}/price")
  Call<Double> getStockPrice(@Path("stock") String stock);

  @GET("stock/{stock}/ohlc")
  @Headers({"Accept: application/json"})
  Call<Ohlc> getStockOhlc(@Path("stock") String stock);
}
