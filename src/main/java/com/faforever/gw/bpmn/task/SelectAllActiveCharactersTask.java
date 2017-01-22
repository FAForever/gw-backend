package com.faforever.gw.bpmn.task;


import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class SelectAllActiveCharactersTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("select all active characters tasks");

//        execution.setVariable("activeCharacters", Arrays.asList(new Character("alfons"), new Character("bernd"), new Character("christian")));
    }

    public List<Object> getChars() {
        return Arrays.asList("dieter", "erich", "franz");
    }
}
