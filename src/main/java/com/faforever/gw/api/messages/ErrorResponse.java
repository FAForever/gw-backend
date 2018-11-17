package com.faforever.gw.api.messages;

import com.faforever.gw.api.error.ErrorResult;
import lombok.Data;

import java.util.ArrayList;

@Data
public class ErrorResponse {
	private final ArrayList<ErrorResult> errors = new ArrayList<>();

	public ErrorResponse addError(ErrorResult newError) {
		errors.add(newError);
		return this;
	}
}