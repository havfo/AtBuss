package net.fosstveit.atbuss.managers;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import net.fosstveit.atbuss.interfaces.CompassCallback;

/**
 * Created by Håvar Aambø Fosstveit on 02.02.14.
 */
public class CompassManager {

	private SensorManager sensorManager = null;
	private SensorEventListener sensorEventListener = null;

	private Sensor accelerometer;
	private Sensor magnetometer;

	private float[] lastAccelerometer = new float[3];
	private float[] lastMagnetometer = new float[3];
	private boolean lastAccelerometerSet = false;
	private boolean lastMagnetometerSet = false;

	private float[] mR = new float[9];
	private float[] orientation = new float[3];

	private int currentOrientation = 0;

	private CompassCallback compassCallback = null;

	public CompassManager() {
		sensorEventListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent sensorEvent) {
				if (sensorEvent.sensor == accelerometer) {
					System.arraycopy(sensorEvent.values, 0, lastAccelerometer,
							0, sensorEvent.values.length);
					lastAccelerometerSet = true;
				} else if (sensorEvent.sensor == magnetometer) {
					System.arraycopy(sensorEvent.values, 0, lastMagnetometer,
							0, sensorEvent.values.length);
					lastMagnetometerSet = true;
				}
				if (lastAccelerometerSet && lastMagnetometerSet) {
					SensorManager.getRotationMatrix(mR, null,
							lastAccelerometer, lastMagnetometer);
					SensorManager.getOrientation(mR, orientation);
				}

				currentOrientation = (int) (orientation[0] * 180 / Math.PI);
				compassCallback.onCompassUpdate(currentOrientation);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int i) {
			}
		};
	}

	public void startListening(final Activity activity) {
		if (sensorManager == null) {
			sensorManager = (SensorManager) activity
					.getSystemService(Context.SENSOR_SERVICE);
		}

		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		lastAccelerometerSet = false;
		lastMagnetometerSet = false;
		sensorManager.registerListener(sensorEventListener, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(sensorEventListener, magnetometer,
				SensorManager.SENSOR_DELAY_UI);
	}

	public void stopListening() {
		sensorManager.unregisterListener(sensorEventListener);
		sensorManager = null;

		accelerometer = null;
		magnetometer = null;
	}

	public void setCompassCallback(final CompassCallback compassCallback) {
		this.compassCallback = compassCallback;
	}

	public CompassCallback getCompassCallback() {
		return compassCallback;
	}
}
