package Assets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class GetAssets {
	private final String Name;

	public GetAssets(String name) {this.Name = name;}

	private static File getResourceAsFile(InputStream in){
		try {

			File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
			tempFile.deleteOnExit();

			try (FileOutputStream out = new FileOutputStream(tempFile)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			}
			return tempFile;

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IO Except");
		}
	}

	public InputStream assetIS(){
		return getClass().getResourceAsStream(this.Name);
	}
	public File assetFile(){
		try(InputStream ObjectIn = getClass().getResourceAsStream(this.Name)){
			return getResourceAsFile(Objects.requireNonNull(ObjectIn));
		}catch (IOException e){
			throw new RuntimeException(e);
		}
	}
}
