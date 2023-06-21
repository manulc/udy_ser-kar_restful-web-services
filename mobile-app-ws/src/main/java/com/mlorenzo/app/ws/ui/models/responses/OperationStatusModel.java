package com.mlorenzo.app.ws.ui.models.responses;

import com.mlorenzo.app.ws.shared.RequestOperationName;
import com.mlorenzo.app.ws.shared.RequestOperationStatus;

public class OperationStatusModel {
	private RequestOperationName operationName;
	private RequestOperationStatus operationResult;
	
	public RequestOperationName getOperationName() {
		return operationName;
	}
	public void setOperationName(RequestOperationName operationName) {
		this.operationName = operationName;
	}
	public RequestOperationStatus getOperationResult() {
		return operationResult;
	}
	public void setOperationResult(RequestOperationStatus operationResult) {
		this.operationResult = operationResult;
	}
	
}
