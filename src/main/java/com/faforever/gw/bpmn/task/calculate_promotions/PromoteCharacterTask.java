package com.faforever.gw.bpmn.task.calculate_promotions;


import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Slf4j
@Component
public class PromoteCharacterTask implements JavaDelegate{

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        throw new NotImplementedException();
    }
}
