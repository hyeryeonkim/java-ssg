package com;

public class Request {
	private String requestStr;
	private String controllerName;
	private String actionName;
	private String arg1;
	private String arg2;	
	private String arg3;
	
	public Request(String requestStr) {
		this.requestStr = requestStr;
		
		String[] requestStrBits = requestStr.split(" ");
		controllerName = requestStrBits[0];
		
		if (requestStrBits.length > 1) {
			actionName = requestStrBits[1];
		}
		
		if (requestStrBits.length > 2) {
			arg1 = requestStrBits[2];
		}
		
		if (requestStrBits.length > 3) {
			arg2 = requestStrBits[3];
		}
		
		if (requestStrBits.length > 4) {
			arg3 = requestStrBits[4];
		}
	}
	
	public boolean isValidRequest() {
		return actionName != null;
//		return actionName.length() != 0;
	}

	public String getControllerName() {
		return controllerName;
	}

	public String getActionName() {
		return actionName;
	}

	public String getArg1() {
		return arg1;
	}

	public String getArg2() {
		return arg2;
	}

	public String getArg3() {
		return arg3;
	}
}