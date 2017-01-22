package com.faforever.gw.bpmn.task;


import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoopTask implements JavaDelegate{

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.error("NoopTask invoked - this should be replaced with an actual Bean");
    }
}
