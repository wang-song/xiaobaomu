package com.song.dingweidb;

public class DingDian {
	
	private String title;
	private String longitude;
	private String latitude;
	private String address;

	public DingDian(String title, String longitude, String latitude,
			String address) {
		this.title = title;
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
	}
	public DingDian() {
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	
	

}
