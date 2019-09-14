package cn.ideamake.components.im;

import cn.ideamake.components.im.common.Rest;
import cn.ideamake.components.im.service.PeriodService;
import cn.ideamake.components.im.web.PeriodController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Slf4j
public class ImApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImApplication.class, args);

    }
}
