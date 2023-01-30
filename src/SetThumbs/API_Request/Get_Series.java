package SetThumbs.API_Request;


import SetThumbs.SetKomgaThumbs;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import FFU.funcs;

public class Get_Series {
	public static URL Komg_URL;
	public static URL NEW_URL;
	public static HttpClient CLIENT = null;
	private HttpRequest REQUEST;
	private HttpResponse<String> ChangePage(int num)
			throws IOException, URISyntaxException, InterruptedException {
		NEW_URL = new URL(Komg_URL.toString() +"&page="+num);
		this.REQUEST = HttpRequest.newBuilder()
				.GET()
				.uri(NEW_URL.toURI())
				.build();
		return CLIENT.send(this.REQUEST, HttpResponse.BodyHandlers.ofString());
	}
	public Get_Series(String url, String LibHash, String[] credentials){
		try{
			Komg_URL = new URL(url+("/api/v1/series?library_id={HASH}".replace("{HASH}", LibHash)));
			this.REQUEST = HttpRequest.newBuilder()
					.GET()
					.uri(Komg_URL.toURI())
					.build();
			CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
					.authenticator(new Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication () {
							return new PasswordAuthentication(
									credentials[0],
									credentials[1].toCharArray());
						}
					})
					.build();
		} catch (IOException e) {
			System.out.println("ERR 1");
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	public String[][] Parse() throws IOException, InterruptedException, URISyntaxException{
		HttpResponse<String> status = CLIENT.send(this.REQUEST, HttpResponse.BodyHandlers.ofString());
		if (status.statusCode() == 401){
			JOptionPane.showMessageDialog(null, "Wrong Credentials");
			System.exit(-401);
		}
		if (status.statusCode() != 200){
			JOptionPane.showMessageDialog(null, "Couldn't connect," +
					" check if server is online");
			System.exit(-200);
		}

		JSONObject response= new JSONObject(status.body());
		Object[] Library;
		int TotalPages = response.getInt("totalPages");
		ArrayList<String> id = new ArrayList<>();
		ArrayList<String> title = new ArrayList<>();
		for (int i = 0; i < TotalPages; i++) {
			System.gc();
			response = new JSONObject(ChangePage(i).body());
			Library = response.getJSONArray("content").toList().toArray(Object[]::new);
			for (Object series: Library) {
				int title_start = series.toString().indexOf("title")+6;
				int title_end = series.toString().indexOf(',', title_start);
				int id_index = series.toString().indexOf("id")+3;
				String STR_title = series.toString().substring(title_start, title_end);
				String Title_Month = STR_title.substring(0, STR_title.indexOf(' '));
				String Title_Year = STR_title.substring(STR_title.indexOf(' ')+1);
				if(!(Byte.parseByte(funcs.Find_Month(Title_Month)[0]) <= -127)){
					if (funcs.isInteger(Title_Year)){
						if(SetKomgaThumbs.Paths.containsKey(STR_title)){
							title.add(STR_title);
							id.add(series.toString().substring(id_index, id_index+13));
						}
					}
				}
			}
		}

		return new String[][]{id.toArray(String[]::new), title.toArray(String[]::new)};
	}
}
