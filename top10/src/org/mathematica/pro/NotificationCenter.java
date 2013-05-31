package org.mathematica.pro;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.mathematica.constants.HTTP;
import org.mathematica.logging.Logger;
import org.mathematica.logic.RetainedConfig;
import org.mathematica.rankings.HTTPRequests;

import android.content.Context;

public class NotificationCenter {

	private static String news = null;
	private static final Logger logger = new Logger("NotificationCenter");

	public static String showLatestNews(Context context) {
		if (isThereANewUpdate()) {
			return getNews();
		}
		return "";
	}

	private static boolean isThereANewUpdate() {
		String requestOutput = HTTPRequests.getInstante()
				.doHTTPRequestStringPost("latest_news_id.php",
						new ArrayList<NameValuePair>());

		if (requestOutput.equals(HTTP.ERROR)) {
			return false;
		}

		StringTokenizer st = new StringTokenizer(requestOutput, ";");
		int latest_news_id = Integer.parseInt(st.nextToken().toString());
		logger.log("latest_news_id: " + latest_news_id);
		news = st.nextToken().toString();
		int stored_news_id = RetainedConfig
				.getConfigValue(RetainedConfig.STORED_NEWS_ID);
		logger.log("stored_news_id: " + stored_news_id);

		RetainedConfig.setConfigValue(RetainedConfig.STORED_NEWS_ID,
				latest_news_id);

		return latest_news_id != stored_news_id;
	}

	private static String getNews() {
		return news;
	}
}
