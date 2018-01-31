package com.bcg.dv.services;

import java.io.IOException;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.bcg.dv.api.bindings.Ohlc;
import com.bcg.dv.api.contracts.IEXContract;
import retrofit2.Call;
import retrofit2.Response;

@Service
@Transactional
public class StockService {

  private IEXContract iexRepository;

  public StockService(@Qualifier("iexRepository") IEXContract iexRepository) {
    this.iexRepository = iexRepository;
  }

  public Optional<Ohlc> getStockOhlc(String stock) {
    Call<Ohlc> call = iexRepository.getStockOhlc(stock);
    System.out.println(call.request().url().toString());
    try {
      Response<Ohlc> ohlcResponse = call.execute();
      System.out.println(ohlcResponse.code());
      if (ohlcResponse.isSuccessful()) {
        Ohlc ohlc = ohlcResponse.body();
        return Optional.of(ohlc);
      } else {
        return Optional.empty();
      }

    } catch (IOException e) {
      return Optional.empty();
    }
  }
}
