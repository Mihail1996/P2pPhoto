package ist.cmu.server;

import ist.cmu.server.service.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ImportAutoConfiguration
@ServletComponentScan
public class ServerApplication {

    public static void main(String[] args) {
        LoggerFactory.init();
        SpringApplication.run(ServerApplication.class, args);
    }

}
