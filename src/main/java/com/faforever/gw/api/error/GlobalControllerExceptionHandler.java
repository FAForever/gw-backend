package com.faforever.gw.api.error;

import com.faforever.gw.api.messages.ErrorResponse;
import com.faforever.gw.bpmn.services.GwErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;
import java.util.Arrays;

@ControllerAdvice
@Slf4j
class GlobalControllerExceptionHandler {

	@ExceptionHandler({ApiException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	private ErrorResponse createResponseFromApiException(ApiException apiException, HttpStatus status) {
		ErrorResponse response = new ErrorResponse();
		Arrays.stream(apiException.getErrors()).forEach(error -> {
			GwErrorType errorCode = error.getErrorCode();
			final Object[] args = error.getArgs();
			response.addError(new ErrorResult(
					String.valueOf(status.value()),
					MessageFormat.format(errorCode.getErrorMessage(), args),
					String.valueOf(errorCode.getErrorCode()),
					ErrorResult.createMeta(args, null).orElse(null)
			));
		});
		return response;
	}
}
