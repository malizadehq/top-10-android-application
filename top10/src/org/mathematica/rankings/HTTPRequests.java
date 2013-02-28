package org.mathematica.rankings;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mathematica.logging.Logger;

public class HTTPRequests {

	private static HTTPRequests connection = null;
	private final String SERVER_URL = "http://atataru.comeze.com/ratings/";
	private Logger logger = null;

	public static HTTPRequests getInstante() {
		if (connection == null)
			return new HTTPRequests();
		else
			return connection;
	}

	private HTTPRequests() {
		logger = new Logger(this.getClass().getName());
	}

	public String doHTTPRequestStringPost(String module,
			List<NameValuePair> arguments) {
		String serverAnswer = "";
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(SERVER_URL + module);
		logger.log("Creating a request to: " + SERVER_URL + module);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(arguments));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			serverAnswer = httpclient.execute(httppost, responseHandler);
			logger.log("Request response: " + serverAnswer);
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return serverAnswer;
	}
}
