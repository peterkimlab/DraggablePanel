package com.edxdn.hmsoon.entities;

public class HummingSoonConfig {
	String videoMode;			// youtube or custom
	String videoBaseUrl;        // video base url

	public String getVideoMode() {
		return videoMode;
	}
	public void setVideoMode(String videoMode) {
		this.videoMode = videoMode;
	}
	public String getVideoBaseUrl() {
		return videoBaseUrl;
	}
	public void setVideoBaseUrl(String videoBaseUrl) {
		this.videoBaseUrl = videoBaseUrl;
	}
}
