package FilesUtil.file_utils;

import Assets.GetAssets;
import FFU.Options;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static FFU.funcs.Find_Month;

public class FolderMover {
	Path Year_Path;
	short Year;
	private final HashMap<String, Path[]> Character_Paths;
	public Path[] Month_Paths = new Path[0];
	public static Path MakeSuffix(Path path){
		if(path.toFile().exists()){
			int index = 0;
			String NewPath;
			while(true) {
				NewPath = path.toAbsolutePath() + " - Copy" + (index <= 0 ? "" : index);
				if(!Paths.get(NewPath).toFile().exists()) break;
				if(++index >= 10){
					try(FileWriter writer = new FileWriter("FileFixer.err.log",true)){
						writer
								.append("ERR: Maximum Amount Of Copies Reached at")
								.append(path.toAbsolutePath().toString())
								.append('\n');
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					break;
				} // report it broke
			}
			return Paths.get(NewPath);
		}
		return path;
	}
	private void BuildMonthList() {
		try {
			try (Stream<Path> tmp = Files.walk(Year_Path, 1)) {
				Month_Paths = new ArrayList<>(tmp
						.filter(p -> !p.equals(this.Year_Path))
						.filter(p -> Options.Whitelist.contains(Find_Month(p.toString())[1]))
						.filter(p -> !Files.isRegularFile(p))
						.sorted((o1, o2) -> {
							int first = Integer.parseInt(
									Find_Month(o1.toString())[0]);
							int second = Integer.parseInt(
									Find_Month(o2.toString())[0]);

							return first - second;
						})
						.toList()).toArray(Path[]::new);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("failed to initialize... \n" +
					"Couldnt Find Folder");
			System.exit(-111);
		}
	}
	private void BuildCharacterList() {
		try {
			for (Path month_path : Month_Paths) {
				String[] Month_info = Find_Month(month_path.toString());
				if (!Month_info[1].equals("Nullity")) {
					try (Stream<Path> tmp = Files.walk(month_path, 1)) {
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
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("failed to initialize... \n" +
					"Couldnt Find Chars");
			System.exit(-222);
		}
	}
	private void MoveSubs(Path CharFolder, boolean Char){
		try(Stream<Path> pathStream = Files.walk(CharFolder, 1)){
			List<Path> FolderList = pathStream
					.filter(Files::isDirectory)
					.filter(p -> !p.equals(CharFolder))
					.toList();
			if(Char){
				for (Path folder : FolderList) {
					String name = folder.toFile().getName().toLowerCase();
					if (name.contains("psd")) {
						Path Destination = MakeSuffix(Paths.get(Options.SubFolderMoveLocation,
								"Alexander Dinh (PSDs)",
								folder.toFile().getParentFile().getParentFile().getParentFile().getName(),
								folder.toFile().getParentFile().getParentFile().getName(), //month
								folder.toFile().getParentFile().getName() + " PSD" //char
						).toAbsolutePath());
						boolean flag = !new File(Destination.getParent().toUri()).mkdirs();
						if(flag){
							try(FileWriter writer = new FileWriter("FileFixer.err.log",true)){
								writer
										.append("ERR: Couldnt  Move: ")
										.append(CharFolder.toFile().getAbsolutePath())
										.append('\n');
							}
							catch (IOException ex) {
								throw new RuntimeException(ex);
							}
						}
						Files.move(
								Paths.get(folder.toFile().getAbsolutePath()),
								Destination
						);
					}
					else if(name.contains("animation")) {
						Path Destination = MakeSuffix(Paths.get(Options.SubFolderMoveLocation,
								"Alexander Dinh (Animations)",
								folder.toFile().getParentFile().getParentFile().getParentFile().getName(),
								folder.toFile().getParentFile().getParentFile().getName(), //month
								folder.toFile().getParentFile().getName() + " Animation" //char
						).toAbsolutePath());
						boolean flag = !new File(Destination.getParent().toUri()).mkdirs();
						if(flag){
							try(FileWriter writer = new FileWriter("FileFixer.err.log",true)){
										writer
										.append("ERR: Couldnt  Move: ")
										.append(CharFolder.toFile().getAbsolutePath())
										.append('\n');
							}
							catch (IOException ex) {
								throw new RuntimeException(ex);
							}
						}
						Files.move(
								Paths.get(folder.toFile().getAbsolutePath()),
								Destination
						);
					}
					else if(name.contains("comic")) {
						Path Destination = MakeSuffix(Paths.get(Options.SubFolderMoveLocation,
								"Alexander Dinh (Comics)",
								folder.toFile().getParentFile().getParentFile().getParentFile().getName(),
								folder.toFile().getParentFile().getParentFile().getName(), //month
								folder.toFile().getParentFile().getName() + " Comic" //char
						).toAbsolutePath());
						boolean flag = !new File(Destination.getParent().toUri()).mkdirs();
						if(flag){
							try(FileWriter writer = new FileWriter("FileFixer.err.log",true)){
								writer
										.append("ERR: Couldnt  Move: ")
										.append(CharFolder.toFile().getAbsolutePath())
										.append('\n');
							}
							catch (IOException ex) {
								throw new RuntimeException(ex);
							}
						}
						Files.move(
								Paths.get(folder.toFile().getAbsolutePath()),
								Destination
						);

					}
					else if(name.contains("dialog")){
						Path Destination = MakeSuffix(Paths.get(Options.SubFolderMoveLocation,
								"Alexander Dinh (Special dialog)",
								folder.toFile().getParentFile().getParentFile().getParentFile().getName(),
								folder.toFile().getParentFile().getParentFile().getName(), //month
								folder.toFile().getParentFile().getName() + " Special dialog" //char
						).toAbsolutePath());
						boolean flag = !new File(Destination.getParent().toUri()).mkdirs();
						if(flag){
							try(FileWriter writer = new FileWriter("FileFixer.err.log",true)){
								writer
										.append("ERR: Couldnt  Move: ")
										.append(CharFolder.toFile().getAbsolutePath())
										.append('\n');
							}
							catch (IOException ex) {
								throw new RuntimeException(ex);
							}
						}
						Files.move(
								Paths.get(folder.toFile().getAbsolutePath()),
								Destination
						);
					}
					else {
						Files.move(
								Paths.get(folder.toFile().getAbsolutePath()),
								Paths.get(Options.SubFolderMoveLocation,
										"Alexander Dinh (MISC)",
										folder.toFile().getParentFile().getParentFile().getParentFile().getName(),
										folder.toFile().getParentFile().getParentFile().getName(), //month
										folder.toFile().getParentFile().getName(), //char
										folder.toFile().getName()
								)
						);
					}
				}
			}else{
				for(Path folder: FolderList){
					String name = folder.toFile().getName().toLowerCase();
					if (name.contains("psd")) {
						Path Destination = MakeSuffix(Paths.get(Options.SubFolderMoveLocation,
								"Alexander Dinh (PSDs)",
								folder.toFile().getParentFile().getParentFile().getName(), //year
								folder.toFile().getParentFile().getName() + " PSDs"//month
						).toAbsolutePath());
						boolean flag = !new File(Destination.getParent().toUri()).mkdirs();
						if(flag){
							try(FileWriter writer = new FileWriter("FileFixer.err.log",true)){
								writer
										.append("ERR: Couldnt  Move: ")
										.append(CharFolder.toFile().getAbsolutePath())
										.append('\n');
							}
							catch (IOException ex) {
								throw new RuntimeException(ex);
							}
						}
						Files.move(
								Paths.get(folder.toFile().getAbsolutePath()),
								Destination
						);
					}
					else if(name.contains("animation")) {
						Path Destination = MakeSuffix(Paths.get(Options.SubFolderMoveLocation,
								"Alexander Dinh (Animations)",
								folder.toFile().getParentFile().getParentFile().getName(), //year
								folder.toFile().getParentFile().getName() + " Animations"//month
						).toAbsolutePath());
						boolean flag = !new File(Destination.getParent().toUri()).mkdirs();
						if(flag){
							try(FileWriter writer = new FileWriter("FileFixer.err.log",true)){
								writer
										.append("ERR: Couldnt  Move: ")
										.append(CharFolder.toFile().getAbsolutePath())
										.append('\n');
							}
							catch (IOException ex) {
								throw new RuntimeException(ex);
							}
						}
						Files.move(
								Paths.get(folder.toFile().getAbsolutePath()),
								Destination
						);
					}
				}
			}
		} catch (IOException e) {
			try(FileWriter writer = new FileWriter("FileFixer.err.log",true)){
				writer
						.append("ERR: Couldnt  Move: ")
						.append(CharFolder.toFile().getAbsolutePath())
						.append('\n');
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public FolderMover(Path year_Path, short year) {
		this.Year_Path = year_Path;
		this.Year = year;
		Character_Paths = new HashMap<>(12);
		BuildMonthList();
		BuildCharacterList();
		System.gc();
		MainStage();
	}

	private void MainStage(){
		Options.gui.setPreview(new GetAssets("Moving Screen.png").assetFile());
		for (Path mon_path : Month_Paths) {
			String[] Month_info = Find_Month(mon_path.toString());
			String month = Month_info[1];

			for(Path CharToFix : Character_Paths.get(month)) {
				this.MoveSubs(CharToFix, true);
			}
			this.MoveSubs(mon_path, false);
		}
	}
}