package org.mathematica.rankings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
		StringTokenizer st = new StringTokenizer(userData, ";");
		user.username = st.nextToken();
		user.email = st.nextToken();
		user.profile_picture_url = st.nextToken();
		user.location = st.nextToken();
		st.nextToken();
		st.nextToken();
		String[] tokens = st.nextToken().trim().split("\n");
		user.overall_score = Integer.parseInt(tokens[0].trim());
		return user;
	}

	public static void updateAppForUser(String email) {
		HTTPRequests httpRequest = HTTPRequests.getInstante();
		List<NameValuePair> arguments = new ArrayList<NameValuePair>(6);
		arguments.add(new BasicNameValuePair("email", email));
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

		String username = AppData.username;
		if (username.isEmpty() || username.length() == 0) {
			username = AppData.email.substring(0, AppData.email.indexOf('@'));
		}
		String email_address = AppData.email;
		String profile_picture_url = AppData.userProfilePictureURL;
		if (profile_picture_url.isEmpty() || profile_picture_url.length() == 0) {
			profile_picture_url = "http://sphotos-a.xx.fbcdn.net/hphotos-prn1/543248_442060402512153_1367978580_n.png";
		}
		String location = "0,0";
		int overall_score = PointsManager.getTotalPoints();
		int current_score = PointsManager.getPoints();

		List<NameValuePair> arguments = new ArrayList<NameValuePair>(6);
		arguments.add(new BasicNameValuePair("username", username));
		arguments.add(new BasicNameValuePair("email_address", email_address));
		arguments.add(new BasicNameValuePair("profile_picture_url",
				profile_picture_url));
		arguments.add(new BasicNameValuePair("location", location));
		arguments.add(new BasicNameValuePair("overall_score", ""
				+ overall_score));
		arguments.add(new BasicNameValuePair("current_score", ""
				+ current_score));
		String module = "update_ui_post.php";

		httpRequest.doHTTPRequestStringPost(module, arguments);
	}
}
