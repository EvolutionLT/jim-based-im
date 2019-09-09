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

@SpringBootApplication(scanBasePackages = {"cn.ideamake.components.im","cn.ideamake.components.im.web"},scanBasePackageClasses = PeriodController.class )
@RestController
@Slf4j
public class ImApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImApplication.class, args);

    }
    /**
     * 用户通过用户名和密码换取token
     * @return
     */
    @Autowired
    public PeriodService periodService;

    @PostMapping("/tokens")
    public Rest getToken(){
        log.info(periodService.getUserInfoById("asd").toString());
        return Rest.ok();
    }
}
