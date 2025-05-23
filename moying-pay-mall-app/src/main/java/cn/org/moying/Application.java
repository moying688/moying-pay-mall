package cn.org.moying;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configurable
@EnableScheduling
public class Application {

    // urpnrr8503@sandbox.com
    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

}
