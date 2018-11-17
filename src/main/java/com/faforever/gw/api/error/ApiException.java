package com.faforever.gw.api.error;

import com.faforever.gw.bpmn.services.GwErrorType;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@ToString
public class ApiException extends RuntimeException {

	private final Error[] errors;

	public ApiException(GwErrorType gwErrorType) {
		this(new Error(gwErrorType));
	}

	ApiException(Error error) {
		this(new Error[]{error});
	}

	ApiException(Error[] errors) {
		super(Arrays.toString(errors));
		this.errors = errors;
	}

	public static ApiException of(GwErrorType errorCode, Object... args) {
		return new ApiException(new Error(errorCode, args));
	}

	public static ApiException of(Error[] errors) {
		return new ApiException(errors);
	}
}
