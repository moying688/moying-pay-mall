package cn.org.moying.config;

import cn.org.moying.infrastructure.gateway.IWeixinApiService;
import lombok.extern.slf4j.Slf4j;
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

    private static final String BASE_URL = "https://api.weixin.qq.com/";

    @Bean
    public Retrofit retrofit(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Bean
    public IWeixinApiService weixinApiService(Retrofit retrofit){
        return retrofit.create(IWeixinApiService.class);
    }
}
