package net.fosstveit.atbuss.objects;

/**
 * @author Håvar Aambø Fosstveit
 */
public class BusEvent {

	private String route;

	private String time;

	private int minutes;

	private String sched;

	private String dir;

	public BusEvent(String route, String time, String dir, String sched,
			int minutes) {
		super();
		this.route = route;
		this.time = time;
		this.dir = dir;
		this.sched = sched;
		this.minutes = minutes;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getSched() {
		return sched;
	}

	public void setSched(String sched) {
		this.sched = sched;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
}
