package net.fosstveit.atbuss.utils;

import android.os.Looper;

import net.fosstveit.atbuss.BuildConfig;

/**
 * Created by havfo on 03.02.14.
 */
public class ThreadPreconditions {
	public static void checkOnMainThread() {
		if (BuildConfig.DEBUG) {
			if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
				throw new IllegalStateException(
						"This method should be called from the Main Thread");
			}
		}
	}
}
