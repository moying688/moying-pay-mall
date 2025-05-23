package cn.org.moying.config;

import cn.org.moying.infrastructure.gateway.IGroupBuyMarketService;
import cn.org.moying.infrastructure.gateway.IWeixinApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @Author: moying
 * @CreateTime: 2025-05-18
 * @Description:
 */


@Slf4j
@Configuration
public class Retrofit2Config {



    @Value("${app.config.group-buy-market.api-url}")
    private String groupBuyMarketApiUrl;

    @Bean
    public IWeixinApiService weixinApiService(){
        Retrofit retrofit =  new Retrofit.Builder()
                .baseUrl("https://api.weixin.qq.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(IWeixinApiService.class);
    }

    @Bean("groupBuyMarketService")
    public IGroupBuyMarketService groupBuyMarketService(){
        Retrofit retrofit =  new Retrofit.Builder()
                .baseUrl(groupBuyMarketApiUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(IGroupBuyMarketService.class);
    }
}
