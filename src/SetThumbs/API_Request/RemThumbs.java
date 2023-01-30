package SetThumbs.API_Request;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RemThumbs {
	public static void run(String[] Series_hash, String baseUrl)
			throws IOException, URISyntaxException, InterruptedException {
		URL url;
		for (String Hash: Series_hash) {
			url = new URL(baseUrl+("/api/v1/series/{HASH}/thumbnails").replace("{HASH}", Hash));
			HttpRequest REQUEST = HttpRequest.newBuilder()
					.GET()
					.uri(url.toURI())
					.build();
			HttpResponse<String> status = Get_Series.CLIENT.send(REQUEST, HttpResponse.BodyHandlers.ofString());
			String[] response = new StringBuilder(status.body())
					.deleteCharAt(status.body().lastIndexOf(']'))
					.deleteCharAt(0)
					.toString()
					.replace("{", "")
					.replace("}", "")
					.replace("\"", "")
					.split(",");
			for (String s:response) {
				if(! s.contains("id")) continue;
				s = s.replace("id:", "");
				URL del_url = new URL(url.toString()+'/'+s);
				REQUEST = HttpRequest.newBuilder()
						.DELETE()
						.uri(del_url.toURI())
						.build();
				Get_Series.CLIENT.send(REQUEST, HttpResponse.BodyHandlers.ofString());
			}
		}
	}
}
