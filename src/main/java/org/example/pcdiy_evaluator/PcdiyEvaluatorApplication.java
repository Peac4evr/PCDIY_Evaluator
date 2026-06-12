package org.example.pcdiy_evaluator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PcdiyEvaluatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcdiyEvaluatorApplication.class, args);
    }

}
