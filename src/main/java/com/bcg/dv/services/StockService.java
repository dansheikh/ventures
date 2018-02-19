package com.bcg.dv.services;

import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    try {
      Response<Ohlc> ohlcResponse = call.execute();

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
