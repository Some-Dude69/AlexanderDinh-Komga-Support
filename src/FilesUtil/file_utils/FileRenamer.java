package FilesUtil.file_utils;

import Assets.GetAssets;
import FFU.Options;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import static FFU.funcs.Find_Month;

public class FileRenamer {
	Path Year_Path;
	short Year;
	private final HashMap<String, Path[]> Character_Paths;
	public Path[] Month_Paths = new Path[0];
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
								log.append("ERR: found duplicate at")
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
	public FileRenamer(Path year_Path, short year) throws IOException {
		this.Year_Path = year_Path;
		this.Year = year;
		Character_Paths = new HashMap<>(12);
		BuildMonthList();
		BuildCharacterList();
		System.gc();
		this.MainStage();
	}
	private void MainStage() throws IOException {
		Options.gui.setPreview(new GetAssets("Renaming Screen.png").assetFile());
		ArrayList<String> banned = new ArrayList<>();
		for (int i = 1; i <= 10; i++) {
			banned.add("0"+i+".");
			banned.add("0"+i+" -");
			banned.add("0"+i);
		}

		for (Path mon_path: Month_Paths) {
			File Month = new File(mon_path.toUri());
			String NewName = Month.getName();

			NewName = NewName
					.replace('_', ' ')
					.strip();
			if (!NewName.equals(Month.getName())) {
				boolean flag = Month.renameTo(Paths.get(Month.getParentFile().getAbsolutePath(), NewName).toFile());
				if (!flag) {
					try (BufferedWriter writer = new BufferedWriter(
							new FileWriter("FileFixer.err.log", true),
							50)) {
						writer
								.append("ERR: Couldnt  Rename: ")
								.append(mon_path.toFile().getAbsolutePath())
								.append('\n');
					}
				}
			}
		}
		BuildMonthList();
		BuildCharacterList();
		for (Path mon_path: Month_Paths) {
			String month = Find_Month(mon_path.toString())[1];
			for(Path Char: Character_Paths.get(month)){
				File CharF = Char.toFile();
				String NewName = CharF.getName();
				for(String ban: banned){NewName = NewName.replace(ban, "");}
				NewName = NewName
						.replaceAll("^-", "")
						.replaceAll("^\\.", "")
						.replaceAll("-$", "")
						.replaceAll("\\.$", "")
						.strip();
				boolean flag = CharF.renameTo(Paths.get(CharF.getParentFile().getAbsolutePath(), NewName).toFile());
				if (!flag){
					try(BufferedWriter writer= new BufferedWriter(
							new FileWriter("FileFixer.err.log", true),
							50)){
						writer
								.append("ERR: Couldnt  Rename: ")
								.append(CharF.getAbsolutePath())
								.append('\n');
					}
				}
			}
		}
		File YearF = Year_Path.toFile();
		String NewName = YearF.getName();
		NewName = NewName
				.replace('_', ' ')
				.strip();
		Year_Path = Paths.get(YearF.getParentFile().getAbsolutePath(), NewName);
		if (!NewName.equals(YearF.getName())) {
			boolean flag = YearF.renameTo(Year_Path.toFile());
			if (!flag) {
				try (BufferedWriter writer = new BufferedWriter(new FileWriter("FileFixer.err.log"), 50)) {
					writer
							.append("ERR: Couldnt  Rename: ")
							.append(YearF.getAbsolutePath())
							.append('\n');
				}
			}
		}
		Options.YearPaths.replace(Year, Year_Path);
	}

}
