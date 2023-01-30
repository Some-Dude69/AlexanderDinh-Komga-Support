package FFU;

import FilesUtil.GUI;
import FilesUtil.StarterGUI;
import FilesUtil.file_fixer.FileFixer;
import FilesUtil.file_utils.FileRenamer;
import FilesUtil.file_utils.FolderMover;
import SetThumbs.SetKomgaThumbs;
import javax.swing.*;
import static FFU.funcs.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class Options{
	public static String SubFolderMoveLocation;
	public static List<String> Whitelist = new ArrayList<>(12){{
		add("January");
		add("February");
		add("March");
		add("April");
		add("May");
		add("June");
		add("July");
		add("August");
		add("September");
		add("October");
		add("November");
		add("December");
	}};
	public static boolean ShowGui = true;
	public static boolean Maximized = true;
	public static boolean MoveSub_folders = true;
	public static boolean replace = false;
	public static boolean ReportDuplicates = false;
	public static boolean Headless = false;
	public static final HashMap<Short, Path> YearPaths = new HashMap<>();
	public static GUI gui;

	private static void FileFixerUtil() throws InterruptedException, IOException{
		ClearLogs();
		List<Short> sortedKeys;

		StarterGUI starterPack = new StarterGUI();
		do{
			//noinspection BusyWait
			Thread.sleep(200);
			Thread.onSpinWait();
			if (starterPack.Submitted) {
				if (starterPack.Whitelist.isSelected()){
					Whitelist = starterPack.BlackList;
				}else{
					Whitelist = Whitelist
							.stream()
							.filter(s -> !starterPack.BlackList.contains(Find_Month(s)[1]))
							.toList();
				}
				Whitelist = Whitelist
						.stream()
						.sorted(((o1, o2) -> {
							int first = Integer.parseInt(
									Find_Month(o1)[0]);
							int second = Integer.parseInt(
									Find_Month(o2)[0]);

							return first-second;
						}))
						.toList();
				if(Options.MoveSub_folders)
					starterPack.FolderMoveLocation();
				starterPack.dispose();
			}
		}while(!starterPack.Submitted);

		gui = new GUI();

		sortedKeys = YearPaths
				.keySet()
				.stream()
				.sorted(((o1, o2) -> o2-o1)) /*New -> Old*/
				.toList();
		for (Short year: sortedKeys) {
			new FileRenamer(YearPaths.get(year), year);
			if(MoveSub_folders){
				new FolderMover(YearPaths.get(year), year);
			}
			new FileFixer(YearPaths.get(year), year);
		}

		Thread.sleep(250);
		if (!ShowGui || !gui.isVisible())
			JOptionPane.showMessageDialog(null, "DONE!");
		Thread.sleep(550);
		gui.dispose();
	}

	public static void main(String[] args)
			throws IOException, InterruptedException, URISyntaxException {
		ChooserGui chooser = new ChooserGui();
		do{
			//noinspection BusyWait
			Thread.sleep(200);
			Thread.onSpinWait();
			if(chooser.Chosen == 1) FileFixerUtil();
			if(chooser.Chosen == 2) SetKomgaThumbs.Start();
		}while(chooser.Chosen == 0);

		System.exit(0);
	}
}