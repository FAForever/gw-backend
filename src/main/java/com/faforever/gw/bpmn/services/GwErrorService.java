package com.faforever.gw.bpmn.services;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.stereotype.Service;

@Slf4j
@Service
/**
 * General purpose service for GW-related BPMN errors
 */
public class GwErrorService {
    public BpmnError getBpmnErrorOf(GwErrorType type) {
        log.error("BpmnError of type {} raised", type.name());
        return new BpmnError(type.getErrorCode(), type.getErrorMessage());
    }
}
