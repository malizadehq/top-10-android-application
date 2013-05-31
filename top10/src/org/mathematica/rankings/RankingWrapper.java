package org.mathematica.rankings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.mathematica.constants.HTTP;
import org.mathematica.globals.AppData;
import org.mathematica.logic.PointsManager;
import org.mathematica.structures.RankedUser;

public class RankingWrapper {
	public static ArrayList<RankedUser> getTopTen() {
		ArrayList<RankedUser> topUsers = new ArrayList<RankedUser>();
		topUsers.clear();
		HTTPRequests httpRequest = HTTPRequests.getInstante();
		List<NameValuePair> arguments = new ArrayList<NameValuePair>();
		String module = "top_eleven_post.php";
		String data = httpRequest.doHTTPRequestStringPost(module, arguments);

		if (data.equals(HTTP.ERROR)) {
			return topUsers;
		}

		StringTokenizer st = new StringTokenizer(data, ";");
		String email = "";
		while (st.hasMoreTokens()) {
			email = st.nextToken();
			if (email.contains("@")) {
				topUsers.add(getUser(email));
			}
		}
		return topUsers;
	}

	private static RankedUser getUser(String email) {
		HTTPRequests httpRequest = HTTPRequests.getInstante();
		List<NameValuePair> arguments = new ArrayList<NameValuePair>(6);
		arguments.add(new BasicNameValuePair("email_address", email));
		String module = "list_data_post.php";
		String userData = httpRequest
				.doHTTPRequestStringPost(module, arguments);
		RankedUser user = new RankedUser();

		if (userData.equals(HTTP.ERROR)) {
			return user;
		}

		StringTokenizer st = new StringTokenizer(userData, ";");
		user.username = st.nextToken();
		user.email = st.nextToken();
		user.profile_picture_url = st.nextToken();
		user.location = st.nextToken();
		try {
			user.last_updated = new SimpleDateFormat("yyyy-MM-dd mm:mm:mm")
					.parse(st.nextToken());
		} catch (ParseException e) {
		}
		st.nextToken();
		String[] tokens = st.nextToken().trim().split("\n");
		user.overall_score = Integer.parseInt(tokens[0].trim());
		return user;
	}

	public static void updateAppForUser(String email) {
		HTTPRequests httpRequest = HTTPRequests.getInstante();
		List<NameValuePair> arguments = new ArrayList<NameValuePair>(6);
		arguments.add(new BasicNameValuePair("email_address", email));
		String module = "list_data_post.php";
		String userData = httpRequest
				.doHTTPRequestStringPost(module, arguments);
		if (!userData.contains(AppData.email)) {
			return;
		}
		StringTokenizer st = new StringTokenizer(userData, ";");
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		PointsManager.setPoints(Integer.parseInt(st.nextToken()));
		String[] tokens = st.nextToken().trim().split("\n");
		if (Integer.parseInt(tokens[0].trim()) > PointsManager.getTotalPoints()) {
			PointsManager.setTotalPoints(Integer.parseInt(tokens[0].trim()));
		}
	}

	public static void updateData() {
		HTTPRequests httpRequest = HTTPRequests.getInstante();

		List<NameValuePair> arguments = new ArrayList<NameValuePair>(6);
		arguments.add(new BasicNameValuePair("username", AppData.username));
		arguments.add(new BasicNameValuePair("email_address", AppData.email));
		arguments.add(new BasicNameValuePair("profile_picture_url",
				AppData.userProfilePictureURL));
		arguments.add(new BasicNameValuePair("location", "0,0"));
		arguments.add(new BasicNameValuePair("overall_score", ""
				+ PointsManager.getTotalPoints()));
		arguments.add(new BasicNameValuePair("current_score", ""
				+ PointsManager.getPoints()));
		String module = "update_ui_post.php";

		httpRequest.doHTTPRequestStringPost(module, arguments);
	}
}
