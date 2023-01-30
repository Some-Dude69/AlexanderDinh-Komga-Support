package SetThumbs;

import FFU.Options;
import SetThumbs.API_Request.Get_Series;
import SetThumbs.API_Request.RemThumbs;
import SetThumbs.API_Request.Upload_Thumbs;
import FFU.funcs;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static FFU.funcs.Find_Month;


public class SetKomgaThumbs{
	public static final HashMap<String, String> Paths = new HashMap<>(120);
	private static void BuildPathsList(){
		try{
			Set<Short> Keys = Options.YearPaths
					.keySet();
			for(Short key :Keys)
				try(Stream<Path> mons = Files.walk(Options.YearPaths.get(key), 1)){
					List<Path> mons_list = mons
							.filter(p -> !p.equals(Options.YearPaths.get(key)))
							.toList();

					for(Path path: mons_list){
						String Month = funcs.Find_Month(path.toString())[1];
						String Year = key.toString();
						Paths.put(
								Month+' '+Year,
								java.nio.file.Paths.get(path.toString(), Month+' '+Year+".jpg").toString());
					}
				}
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("failed to initialize... \n" +
					"Couldnt Find Folder");
			System.exit(-111);
		}
	}


	public static void Start() throws InterruptedException, IOException, URISyntaxException{
		SetUp starterPack = new SetUp();
		do {
			//noinspection BusyWait
			Thread.sleep(200);
			Thread.onSpinWait();
			if (starterPack.Submitted) {
				if (starterPack.Whitelist.isSelected()){
					Options.Whitelist = starterPack.BlackList;
				}
				else{
					Options.Whitelist = Options.Whitelist
							.stream()
							.filter(s -> !starterPack.BlackList.contains(Find_Month(s)[1]))
							.toList();
				}
				Options.Whitelist = Options.Whitelist
						.stream()
						.sorted(((o1, o2) -> {
							int first = Integer.parseInt(
									Find_Month(o1)[0]);
							int second = Integer.parseInt(
									Find_Month(o2)[0]);

							return first-second;
						}))
						.toList();
			}
		}while(!starterPack.Submitted);
		{
			int confirmDialog =
					JOptionPane.showConfirmDialog(starterPack,
							"Do you want to see the WebScraper Working",
							"See WebScraper?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE
					);
			Options.Headless = (confirmDialog != 0);
		} /*Headless mode??*/
		starterPack.dispose();
		for(Short Key: Options.YearPaths.keySet().stream().sorted().toList())
			new WebScraper(Options.YearPaths.get(Key), Key);
		WebScraper.driver.quit();
		BuildPathsList();
		SetKomga();
	}
	private static void SetKomga() throws IOException, InterruptedException, URISyntaxException{
		String url = null;
		String Hash = null;
		String Username = null;
		String Password = null;
		boolean submit;

		AccountInfo inputs = new AccountInfo();
		do{
			Thread.sleep(250);
			Thread.onSpinWait();
			submit = AccountInfo.Submit;
			if (submit){
				if (inputs.IPField.getText().isBlank()){
					url = "http://127.0.0.1:8080";
					inputs.IPField.setText("Defaulting to LocalHost");
				}else{
					url = inputs.IPField.getText();
					if (url.charAt(url.length()-1) == '/'){
						url = url.substring(0,url.length()-1);
					}
					System.out.println(url);
					try {
						URL obj = new URL(url);
						obj.toURI();
					} catch (MalformedURLException | URISyntaxException e) {
						inputs.IPField.setText("Defaulting to LocalHost | enter http:// before the url");
						url = "http://127.0.0.1:8080";
						System.out.println("MALFORMED URL");
					}
				}
				if ((inputs.HashField.getText().length() == 13) &&(!(inputs.HashField.getText().contains(" ")))){
					Hash = inputs.HashField.getText();
				}else{
					inputs.HashField.setText("Cant be BLANK / Wrong Formatting");
					AccountInfo.Submit = false;
					submit = false;
				}
				if (!(inputs.UserField.getText().isBlank())){
					Username = inputs.UserField.getText();
				}else {
					inputs.UserField.setText("Cant be BLANK");
					AccountInfo.Submit = false;
					submit = false;
				}
				if (!(new String(inputs.passwordField.getPassword()).isBlank())){
					Password = new String(inputs.passwordField.getPassword());
				}else {
					JOptionPane.showMessageDialog(null, "Password Cant be BLANK");
					AccountInfo.Submit = false;
					submit = false;
				}
			}
		}while(!submit);

		Thread.sleep(1500);
		inputs.dispose();

		Get_Series getSeries = new Get_Series(url, Hash, new String[]{Username, Password});
		String[][] result = getSeries.Parse();
		RemThumbs.run(result[0], url);
		String finalPassword = Password;
		String finalUsername = Username;
		OkHttpClient up_client = new OkHttpClient.Builder()
				.authenticator((route, response) -> {
					String credential = Credentials.basic(finalUsername, finalPassword);

					return response.request().newBuilder().header("Authorization", credential).build();

				})
				.callTimeout(10, TimeUnit.SECONDS)
				.connectTimeout(10, TimeUnit.SECONDS)
				.writeTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.build();

		Upload_Thumbs up = new Upload_Thumbs(result, up_client);
		up.Upload(url);

		JOptionPane.showMessageDialog(null, "DONE! refresh komga");
	}
}