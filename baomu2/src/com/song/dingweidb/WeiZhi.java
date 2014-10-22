package com.song.dingweidb;

public class WeiZhi {
	
	private String time;
	private String longitude;
	private String latitude;
	private String address;
	private String lingwai;
	
	public WeiZhi() {
	}
	public WeiZhi(String time, String longitude, String latitude, String address) {
		super();
		this.time = time;
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
	}

	public WeiZhi(String time, String longitude, String latitude,
			String address, String lingwai) {
		super();
		this.time = time;
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
		this.lingwai = lingwai;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLingwai() {
		return lingwai;
	}
	public void setLingwai(String lingwai) {
		this.lingwai = lingwai;
	}
	
	
	
	
	

}
