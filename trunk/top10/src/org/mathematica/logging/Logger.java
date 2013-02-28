package org.mathematica.logging;

import org.mathematica.constants.LOG_PRIORITY;
import org.mathematica.constants.LoggingConstants;

import android.util.Log;

public class Logger {
	private String TAG = null;
	private LOG_PRIORITY loggingPriority = null;

	public Logger() {
		this(LoggingConstants.DEFAULT_TAG);
	}

	public Logger(final String defaultTag) {
		this(LOG_PRIORITY.GLOBALLY_DEFINED, defaultTag);
	}

	public Logger(final LOG_PRIORITY priority, final String defaultTag) {
		TAG = defaultTag;
		loggingPriority = priority;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		TAG = null;
		loggingPriority = null;
	}

	public void log(final String message) {
		if (LoggingConstants.enableLogging
				&& loggingPriority == LOG_PRIORITY.GLOBALLY_DEFINED) {
			Log.i(TAG, message);
		} else if (loggingPriority == LOG_PRIORITY.OVERRIDE_GLOBAL) {
			Log.i(TAG, message);
		}
	}
}
