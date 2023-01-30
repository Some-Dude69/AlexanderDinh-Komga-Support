package FilesUtil.file_fixer;

import FFU.Options;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CBZ_Zipper {
	private final String Zip_Path;
	public CBZ_Zipper(String ZipPath){
		Zip_Path = Paths.get(ZipPath).toFile().getAbsolutePath();
	}
	private void write_zip() throws FileNotFoundException{
		FileOutputStream FileOutput = new FileOutputStream(Zip_Path+".cbz");
		ZipOutputStream zipOut = new ZipOutputStream(FileOutput);
		Path path = Paths.get(Zip_Path);
		try (Stream<Path> Path_Stream = Files.walk(path, 1)){
			List<Path> Sub_Paths = new ArrayList<>(Path_Stream
					.filter(p -> !p.toString().equals(Zip_Path))
					.toList());
			if (!Options.MoveSub_folders){
				Sub_Paths = Sub_Paths
						.stream()
						.filter(Files::isRegularFile)
                        .toList();
			}
			for (Path PathToZipThis : Sub_Paths) {
				File zipThis = PathToZipThis.toFile();
				try(FileInputStream fis = new FileInputStream(zipThis)){
					ZipEntry zipEntry = new ZipEntry(zipThis.getName());
					zipOut.putNextEntry(zipEntry);
					byte[] bytes = new byte[65536];
					int len;
					while ((len = fis.read(bytes)) >=0){
						zipOut.write(bytes, 0, len);
					}
				}
			}
			zipOut.close();
			FileOutput.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private void CBZ_Compress(boolean ReportDuplicate) throws FileNotFoundException{
		if (!new File(Zip_Path+".cbz").exists()) {
			write_zip();
		}else if(ReportDuplicate){
			try(FileWriter log = new FileWriter("FileFixer.err.log")){
				log.append("ERR: Found Duplicate at ")
						.append(Paths.get(Zip_Path).getFileName().toString())
						.append(".cbz, ")
						.append("At Location: ")
						.append(Zip_Path)
						.append('\n');
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	public void CBZ_Compress(boolean ReportDuplicate, boolean Replace) throws FileNotFoundException{
		if(Replace){
			Path ZipPath = Paths.get(Zip_Path);
			boolean FileExists = ZipPath.toFile().exists();
			write_zip();
			if(ReportDuplicate && FileExists){
				try(FileWriter log = new FileWriter("FileFixer.log")){
					log.append("Report: Replaced ")
							.append(ZipPath.getFileName().toString())
							.append(".cbz, ")
							.append("At Location: ")
							.append(Zip_Path)
							.append("\n");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}else CBZ_Compress(ReportDuplicate);
	}
}
