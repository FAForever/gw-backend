package com.faforever.gw.config;

import org.camunda.bpm.engine.impl.jobexecutor.CallerRunsRejectedJobsHandler;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.camunda.bpm.engine.spring.components.jobexecutor.SpringJobExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class CamundaConfig {

    @Bean
    public JobExecutor jobExecutor() {
        final SpringJobExecutor springJobExecutor = new SpringJobExecutor();
        springJobExecutor.setTaskExecutor(taskExecutor());
        springJobExecutor.setRejectedJobsHandler(new CallerRunsRejectedJobsHandler());

        return springJobExecutor;
    }

    /**
     * @return Spring boot default taskExecutor (required by camunda)
     */
    @Bean TaskExecutor taskExecutor() {
        final TaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        return taskExecutor;
    }
}
