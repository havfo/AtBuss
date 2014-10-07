package net.fosstveit.atbuss.objects;

public class BusRoute {
	
	private int id;
	
	private String name;
	
	private int toStop;
	
	private String toStopName;
	
	public BusRoute() {
		
	}

	public BusRoute(int id, String name, int toStop, String toStopName) {
		super();
		this.id = id;
		this.name = name;
		this.toStop = toStop;
		this.toStopName = toStopName;
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

	public int getToStop() {
		return toStop;
	}

	public void setToStop(int toStop) {
		this.toStop = toStop;
	}

	public String getToStopName() {
		return toStopName;
	}

	public void setToStopName(String toStopName) {
		this.toStopName = toStopName;
	}
}
