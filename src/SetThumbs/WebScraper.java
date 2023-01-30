package SetThumbs;

import FFU.Options;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.stream.Stream;

import static FFU.funcs.Find_Month;

public class WebScraper{
	private static final String PRODUCT_IMAGE = "/html/body/div[3]/div/main/section/article/div/div/div/img";
	private static final String MAIN_PAGE = "/html/body/div[3]/div/div/main";
	private static final By JANUARY_2019;
	static{
		int MONTHS_SINCE_2019;
		TimeZone BIT = TimeZone.getTimeZone("GMT-12:00");
		Calendar calender = Calendar.getInstance();
		calender.setTimeZone(BIT);

		MONTHS_SINCE_2019 = (calender.get(Calendar.YEAR));
		MONTHS_SINCE_2019 -= 2019;
		MONTHS_SINCE_2019 *= 12;
		if(calender.get(Calendar.DAY_OF_MONTH) >= 8)
			MONTHS_SINCE_2019 += calender.get(Calendar.MONTH);

		else MONTHS_SINCE_2019 += calender.get(Calendar.MONTH) -1;

		MONTHS_SINCE_2019+=1; /*accounting for kda*/
		JANUARY_2019 =
				By.cssSelector("article.product-card:nth-child("+MONTHS_SINCE_2019+')');
	}
	private static final String HTML_PATH;
	static{
		try {
			File tmpFile = File.createTempFile("WebScraper9002.html", ".tmp");
			HTML_PATH = tmpFile.getAbsolutePath();
			if(!tmpFile.delete()) throw new RuntimeException("Unable to delete tm file");
			tmpFile.deleteOnExit();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static final WebDriver driver;
	static {
		WebDriverManager.edgedriver().setup();
		EdgeOptions options = new EdgeOptions();

		options.addArguments("--disable-gpu");
		options.addArguments("--disable-extensions");
		options.setPageLoadStrategy(PageLoadStrategy.EAGER);
		options.setPageLoadTimeout(Duration.ofSeconds(10));
		if(Options.Headless) options.addArguments("--headless");

		driver = new EdgeDriver(options);
		driver.manage().window().maximize();

		LoadPage();
	}
	private static final String GumRoadURL = "https://alexanderdinh.gumroad.com/";
	public Path[] Month_Paths;
	private final Path Year_Path;
	private final short Year;

	public void Download(String strURL, String path, String month){
		try {
			URL url = new URL(strURL);
			MimeType Mime_Type = MimeTypes.getDefaultMimeTypes()
					.forName(url.openConnection().getContentType());
			String FileExtension = Mime_Type.getExtension();

			InputStream in = new BufferedInputStream(url.openStream());
			FileOutputStream fos = new FileOutputStream(Paths.get(path, (month+' '+Year+".jpg")).toFile());

			if(FileExtension.equals(".jpg")){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[65536];
				int n;

				while (-1 != (n = in.read(buf)))
					out.write(buf, 0, n);

				byte[] response = out.toByteArray();
				fos.write(response);
				out.close();
			}
			else{
				BufferedImage tmpImage = ImageIO.read(in);
				BufferedImage covImage = new BufferedImage(
						tmpImage.getWidth(),
						tmpImage.getHeight(),
						BufferedImage.TYPE_INT_RGB); // no alpha chanel
				covImage.createGraphics()
						.drawImage(tmpImage,
								0,
								0,
								Color.BLACK,
								null);
				ImageIO.write(covImage, "jpg", fos);
			}

			in.close();
			fos.close();
		}catch(IOException | MimeTypeException e){throw new RuntimeException(e);}
	}
	private static void LoadPage(){
		JavascriptExecutor driverExecutor = (JavascriptExecutor) driver;
		while(true){
			try{
				driver.get(GumRoadURL);
				break;
			}catch (TimeoutException ignored) {driver.navigate().refresh();}
		}

		WebElement mainPage_element = driver.findElement(By.xpath(MAIN_PAGE));
		long pause_time = 400L;
		long old_Height = -1L;
		int failed = 0;

		try {Thread.sleep(pause_time);}
		catch(InterruptedException ignored){}

		while(true){
			driverExecutor.executeScript
					("arguments[0].scroll(0, arguments[0].scrollHeight);", mainPage_element);

			try {
				//noinspection BusyWait
				Thread.sleep(pause_time);
			}catch(InterruptedException ignored){}

			long new_height = (long)
					driverExecutor.executeScript("return arguments[0].scrollHeight", mainPage_element);
			try{
				driver.findElement(JANUARY_2019);
				break;
			}catch(NoSuchElementException ignored) {}
			if (new_height == old_Height){
				if (++failed > 5) {
					if(pause_time <= 1000) pause_time += 50L;
					driver.get(driver.getCurrentUrl());
					mainPage_element = driver.findElement(By.xpath(MAIN_PAGE));
					failed = 0;
				}
			}
			old_Height = new_height;
		}
		try (FileWriter writer = new FileWriter(HTML_PATH, false)) {
			writer.write(driver.getPageSource());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private Elements ReverseElements(Elements OrgArray){
		Elements RevElm = new Elements(OrgArray.size());
		for (int i = OrgArray.size()-1; i >= 0; i--) {
			RevElm.add(OrgArray.get(i));
		}
		return RevElm;
	}
	private void BuildMonthList(){
		try{
			try(Stream<Path> tmp = Files.walk(Year_Path, 1)){
				Month_Paths = new ArrayList<>(tmp
						.filter(p -> !p.equals(this.Year_Path))
						.filter(p -> Options.Whitelist.contains(Find_Month(p.toString())[1]))
						.filter(p -> !Files.isRegularFile(p))
						.sorted((o1, o2) -> {
							int first = Integer.parseInt(
									Find_Month(o1.toString())[0]);
							int second = Integer.parseInt(
									Find_Month(o2.toString())[0]);

							return first-second;
						})
						.toList()).toArray(Path[]::new);
			}
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("failed to initialize... \n" +
					"Couldnt Find Folder");
			System.exit(-111);
		}
	}
	public WebScraper(Path year_Path, short year){
		this.Year_Path = year_Path;
		this.Year = year;
		BuildMonthList();
		System.gc();
		MainStage();
	}
	private void MainStage(){
		HashMap<String, String> Rewards_List = new HashMap<>(12);
		String PageSource;
		synchronized(driver){
			if(Paths.get(HTML_PATH).toFile().exists()){
				try(FileReader HTMLDoc = new FileReader(HTML_PATH)){
					StringBuilder HTMLDoc_String =new StringBuilder(50_000);
					int data;
					while((data = HTMLDoc.read()) != -1){
						HTMLDoc_String.append((char) data);
					}
					PageSource = HTMLDoc_String.toString();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			else throw new RuntimeException(HTML_PATH + "/HTMl doc Didnt Load properly");

			Document LoadedMainPage = Jsoup.parse(PageSource);
			Elements HTML_Rewards_List = ReverseElements(
					LoadedMainPage.selectXpath(MAIN_PAGE).select("article"));
			for(Element Reward :HTML_Rewards_List){
				if(!Reward.attr("class").equals("product-card")){continue;}
				String link = Reward.children().select("link").attr("href");
				if(link.endsWith("?layout=profile")){
					link = link.substring(0, link.length() - "?layout=profile".length());
				}

				String Reward_Name = Reward.children().select("h3").text();
				String Reward_Month = Find_Month(Reward_Name)[1];
				if(Reward_Month.equals("Nullity")){continue;}

				if(Integer.parseInt(Reward_Name.replaceAll("[^0-9]", "")) == Year){
					Rewards_List.put(Reward_Month, link);
				}
			}
		}
		for(Path mon_path : Month_Paths){
			String Month = Find_Month(mon_path.toFile().getName())[1];
			String link = Rewards_List.get(Month);
			while(true){
				try{
					driver.get(link);
					new WebDriverWait(driver, Duration.ofSeconds(10))
							.until(d -> d.findElement(By.xpath(PRODUCT_IMAGE)));
					break;
				}catch (TimeoutException ignored) {driver.navigate().refresh();}
			} // try to load page
			Element LoadedProductPage =
					Jsoup.parse(driver.getPageSource())
							.selectXpath(PRODUCT_IMAGE)
							.first();
			if(LoadedProductPage == null) throw new RuntimeException("COULDNT FIND IMAGE DURING SCRAPE");
			String img_link = LoadedProductPage.attr("src");
			Download(img_link, mon_path.toString(),  Month);
		}
	}
}
