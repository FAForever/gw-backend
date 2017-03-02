package com.faforever.gw.bpmn.accessors;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;

/***
 * Abstract class for conveniently accessing process variables
 * Each BPMN process should have it's own subclass.
 */
@Slf4j
public abstract class BaseAccessor {
    private final DelegateExecution processContext;

    public String getBusinessKey() {
        return processContext.getProcessInstance().getBusinessKey();
    }

    protected Object get(String variable) {
        return processContext.getVariable(variable);
    }

    protected Object getOrDefault(String variable, Object defaultValue) {
        return processContext.getVariables().getOrDefault(variable, defaultValue);
    }

    protected void set(String variable, Object value) {
        log.debug("-> set {} to {}", variable, value.toString());
        processContext.setVariable(variable, value);
    }

    protected BaseAccessor(DelegateExecution processContext) {
        this.processContext = processContext;
    }
}
