package net.fosstveit.atbuss.utils;

import net.fosstveit.atbuss.MainActivity;
import net.fosstveit.atbuss.objects.BusEvent;
import net.fosstveit.atbuss.objects.BusRoute;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Håvar Aambø Fosstveit
 */
public class Utils {
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> task,
			T... params) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		} else {
			task.execute(params);
		}
	}

	public static void getBusStops() {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://fosstveit.no/buss/getstops.php");

			HttpResponse response;
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			String result = "";

			if (entity != null) {
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				instream.close();
			}

			String[] values = result.split("<spl>", -1);

			MainActivity.sqliteManager.addBusStops(values);

			// for (int i = 0; i < values.length; i++) {
			// String[] tmp = values[i].split("<ln>", -1);
			//
			// AtBussActivity.sqliteManager.addBusStop(tmp[0], tmp[1], tmp[2],
			// tmp[3]);

			// BusStop stop = new BusStop(Integer.parseInt(tmp[0]), tmp[1],
			// Double.parseDouble(tmp[2]), Double.parseDouble(tmp[3]));
			// stops.add(stop);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getVersion() {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://fosstveit.no/buss/version.php");

			HttpResponse response;
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			String result = "";

			if (entity != null) {
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				instream.close();
			}

			result = result.replace("\n", "").replace("\r", "");

			MainActivity.sqliteManager.addVersion(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BusRoute[] getRoutes(int busStopId) {
		BusRoute[] ret = null;

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://fosstveit.no/buss/getroutes.php?busstopid=" + busStopId);

			HttpResponse response;
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			String result = "";

			if (entity != null) {
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				instream.close();
			}

			result = result.replace("\n", "").replace("\r", "");

			JSONParser parser = new JSONParser();

			JSONArray arr = (JSONArray) (parser.parse(result));
			ret = new BusRoute[arr.size()];

			for (int i = 0; i < arr.size(); i++) {
				JSONObject tmp = (JSONObject) arr.get(i);

				ret[i] = new BusRoute(Integer.parseInt((String) tmp.get("perc")),
						(String) tmp.get("route"),
						Integer.parseInt((String) tmp.get("tostop")),
						(String) tmp.get("tostopname"));
			}

			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static BusEvent[] getBusTime(int busStopId, int numStops) {
		BusEvent[] ret = null;

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://fosstveit.no/buss/index.php?busstopid=" + busStopId
							+ "&num=" + numStops);

			HttpResponse response;
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			String result = "";

			if (entity != null) {
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);
				instream.close();
			}

			result = result.replace("\n", "").replace("\r", "");

			JSONParser parser = new JSONParser();

			JSONArray arr = (JSONArray) (parser.parse(result));
			ret = new BusEvent[arr.size()];

			for (int i = 0; i < arr.size(); i++) {
				JSONObject tmp = (JSONObject) arr.get(i);

				Calendar cal = Calendar.getInstance();

				String[] t = ((String) ((String) tmp.get("orario")).split(" ")[1])
						.split(":");

				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(t[0]));
				cal.set(Calendar.MINUTE, Integer.parseInt(t[1]));
				cal.set(Calendar.SECOND, 30);
				cal.set(Calendar.MILLISECOND, 0);

				Date endD = cal.getTime();

				Date start = new Date();
				long difference = endD.getTime() - start.getTime();

				int minutes = (int) ((difference / 1000.0) / 60.0);

				if (minutes < 0) {
					minutes += 1440;
				}

				ret[i] = new BusEvent("Rute " + (String) tmp.get("codAzLinea"),
						((String) tmp.get("orario")).split(" ")[1],
						(String) tmp.get("capDest"),
						((String) tmp.get("orarioSched")).split(" ")[1],
						minutes);
			}

			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String askOracle(String question) {
		String answer = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"https://www.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question="
							+ question);
			HttpResponse response;
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				answer = convertStreamToString(instream);
				instream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return answer;
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
