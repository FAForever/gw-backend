package com.faforever.gw.bpmn.message;

import com.faforever.gw.model.Battle;
import com.faforever.gw.model.GwCharacter;
import com.faforever.gw.model.Planet;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlanetNotAttackableErrorMessage implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Planet planet = (Planet)execution.getVariable("planet");
        GwCharacter character = (GwCharacter)execution.getVariable("initator");
        Battle battle = (Battle)execution.getVariable("battle");

        // TODO: Invoke response to frontend
    }
}
