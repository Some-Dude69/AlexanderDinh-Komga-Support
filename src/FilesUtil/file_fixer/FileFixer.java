package FilesUtil.file_fixer;

import FFU.Character_OBJ;
import FFU.Options;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import static FFU.funcs.Find_Month;


public class FileFixer{
	private final HashMap<String, Path[]> Character_Paths = new HashMap<>(12);
	public Path[] Month_Paths = new Path[0];
	private final Path Year_Path;
	private final short Year;

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
	private void BuildCharacterList(){
		try{
			for(Path month_path : Month_Paths){
				String[] Month_info = Find_Month(month_path.toString());
				if (!Month_info[1].equals("Nullity")){
					try(Stream<Path> tmp = Files.walk(month_path, 1)) {
						Path[] Char_Paths = new ArrayList<>(
								tmp
										.filter(p -> !p.equals(month_path))
										.filter(p -> !Files.isRegularFile(p))
										.toList()).toArray(Path[]::new);
						if (!Character_Paths.containsKey(Month_info[1]))
							Character_Paths.put(Month_info[1], Char_Paths);
						else {
							try (FileWriter log = new FileWriter("FileFixer.err.log", true)) {
								log.append("ERR: found duplicate at ")
										.append(Month_info[1])
										.append(", ")
										.append(String.valueOf(Year))
										.append(": ")
										.append(month_path.toString())
										.append('\n');
							}
						}
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("failed to initialize... \n" +
					"Couldnt Find Chars");
			System.exit(-222);
		}
	}
	private void zipUp(Path CharToZip){
		try {
			new CBZ_Zipper(CharToZip.toString()).CBZ_Compress(Options.ReportDuplicates, Options.replace);
		} catch (FileNotFoundException e){
			throw new RuntimeException(e);
		}
	}
	private void MainStage(){
		for (Path mon_path: Month_Paths) {
			String[] Month_info = Find_Month(mon_path.toString());
			String month = Month_info[1];
			byte month_Number = Byte.parseByte(Month_info[0]);

			Options.gui.setPreview(mon_path + "\\" + month + ' ' + Year + ".jpg"); // change gui components
			Options.gui.Reset_bar(Character_Paths.get(month).length); // change gui components

			for (Path CharToZip:Character_Paths.get(month)){
				Character_OBJ obj = new Character_OBJ(
						Year,
						month,
						CharToZip.getFileName().toString(),
						CharToZip.toString(),
						month_Number
				);
				XML_Create.INSTANCE.WriteXML(obj); // creates ComicInfo.xml

				zipUp(CharToZip); // zips the character

				if(!Paths.get(CharToZip.toString(), "ComicInfo.xml").toFile().delete()){
					try (FileWriter log = new FileWriter("FileFixer.warn.log", true)) {
						log.append("Warning: Couldnt Delete ComicInfo.xml File")
								.append(Month_info[1])
								.append(", ")
								.append(String.valueOf(Year))
								.append(": ")
								.append(CharToZip.toString())
								.append('\n');
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				//reports undeleted XMLs to the LOG


				Options.gui.Increment_bar(); // reports the character as done
			}
		}
		System.out.println("Done With: "+Year);
	}
	public FileFixer(Path year_Path, short year){
		this.Year_Path = year_Path;
		this.Year = year;
		BuildMonthList();
		BuildCharacterList();
		System.gc();
		MainStage();
	}
}
