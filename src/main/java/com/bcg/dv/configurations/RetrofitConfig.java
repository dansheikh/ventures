package com.bcg.dv.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bcg.dv.api.contracts.IEXContract;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class RetrofitConfig {

  @Value("${api.iex.url}")
  private String iexBaseUrl;

  @Bean(name = "retrofitBuilder")
  public Retrofit.Builder retrofitBuilder() {
    return new Retrofit.Builder().addConverterFactory(JacksonConverterFactory.create());
  }

  @Bean(name = "iexRetrofit")
  public Retrofit iexRetrofit(Retrofit.Builder builder) {
    return builder.baseUrl(iexBaseUrl).build();
  }

  @Bean(name = "iexRepository")
  public IEXContract iexRepository(@Qualifier("iexRetrofit") Retrofit retrofit) {
    return retrofit.create(IEXContract.class);
  }
}
