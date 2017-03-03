package com.faforever.gw.bpmn.accessors;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import java.util.UUID;

public class UserInteractionProcessAccessor extends BaseAccessor{
    protected UserInteractionProcessAccessor(DelegateExecution processContext) {
        super(processContext);
    }

    /***
     * Warning: Only call this, if you are sure, there was a user request before.
     * @return UUID of the last user request
     */
    public UUID getRequestId() {
        return (UUID)get("requestId");
    }

    /***
     * Important: the requestId should be passed as a process variable on correlating the message
     * @param requestId Random requestId of the client request
     */
    public void setRequestId(UUID requestId) {
        set("requestId", requestId);
    }

    /***
     * Warning: Only call this, if you are sure, there was a user request before.
     * @return UUID of the character who made the request
     */
    public UUID getRequestCharacter() {
        return (UUID)get("requestCharacter");
    }

    /***
     * Important: the requestCharacter should be passed as a process variable on correlating the message
     * @param requestCharacter Random requestCharacter of the client request
     */
    public void setRequestCharacter(UUID requestCharacter) {
        set("requestCharacter", requestCharacter);
    }


    public static UserInteractionProcessAccessor of(DelegateExecution processContext) {
        return new UserInteractionProcessAccessor(processContext);
    }
}
