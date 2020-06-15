package com;

class BuildController extends Controller {
	private BuildService buildService;

	BuildController() {
		buildService = Factory.getBuildService();
	}

	@Override
	void doAction(Request request) {
		if (request.getActionName().equals("site")) {
			actionSite(request);
		} else if (request.getActionName().equals("startAutoSite")) {
			actionStartAutoSite(request);
		} else if (request.getActionName().equals("stopAutoSite")) {
			actionStopAutoSite(request);
		}
	}

	private void actionSite(Request request) {
		buildService.buildSite();
		System.out.println("게시물 수동 생성");
	}
	
	private void actionStartAutoSite(Request request) {
		System.out.println("=========================== Auto ===========================");
		buildService.buildStartAutoSite();
	}

	private void actionStopAutoSite(Request request) {
		System.out.println("=========================== Manual ===========================");
		buildService.buildStropAutoSite();
	}
}