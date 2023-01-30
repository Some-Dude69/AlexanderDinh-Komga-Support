package SetThumbs.API_Request;


import SetThumbs.SetKomgaThumbs;
import net.coobird.thumbnailator.Thumbnails;
import okhttp3.OkHttpClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Upload_Thumbs {
	private final String[] Titles;
	private final String[] Hash;
	public final OkHttpClient CLIENT;
	public Upload_Thumbs(String[][] lib, OkHttpClient client){
		this.Hash = lib[0];
		this.Titles = lib[1];
		this.CLIENT = client;
	}

	public void Upload(String url) throws IOException{
		String month;
		String Year;
		String title;
		UpThumb up = new UpThumb(this.CLIENT);
		for (int i = 0; i < this.Titles.length; i++) {
			title = this.Titles[i];
			month = title.substring(0,title.indexOf(' '));
			Year = title.substring(title.indexOf(' ')+1);

			String image_path = SetKomgaThumbs.Paths.get(month+' '+Year);

			BufferedImage thumb = Thumbnails.of(new File(image_path))
					.size(840, 840)
					.asBufferedImage();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			ImageIO.write(thumb, "jpg", bos);

			byte[] thumbData = bos.toByteArray();

			URL sendUrl = new URL(url +"/api/v1/series/"+this.Hash[i]+"/thumbnails?selected=true");
			Image img = new Image(thumbData);
			up.uploadSeriesThumbnail(img, sendUrl);
			System.gc();
		}
	}

}
