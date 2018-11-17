package com.faforever.gw.api.error;


import com.faforever.gw.bpmn.services.GwErrorType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Error {

	private final GwErrorType errorCode;
	private final Object[] args;

	public Error(GwErrorType errorCode, Object... args) {
		this.errorCode = errorCode;
		this.args = args;
	}
}