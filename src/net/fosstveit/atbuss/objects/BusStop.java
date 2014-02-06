package net.fosstveit.atbuss.objects;

public class BusStop {

	private int id;

	private String name;

	private double latitude;

	private double longitude;

	private int distance = 0;

	private int direction = 0;
	
	private int numUsed;

	public BusStop(int id, String name, double latitude, double longitude, int numUsed) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.numUsed = numUsed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getNumUsed() {
		return numUsed;
	}

	public void setNumUsed(int numUsed) {
		this.numUsed = numUsed;
	}
}
