package FFU;

import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;

public final class funcs {
	private funcs(){}
	public static @NotNull String[] Find_Month(String Month){
		byte Month_Num;
		String month_selected;
		if (Month.toLowerCase().contains("january")){
			Month_Num = 1;
			month_selected = "January";
		}else if (Month.toLowerCase().contains("february")){
			Month_Num = 2;
			month_selected = "February";
		}else if (Month.toLowerCase().contains("march")){
			Month_Num = 3;
			month_selected = "March";
		}else if (Month.toLowerCase().contains("april")){
			Month_Num = 4;
			month_selected = "April";
		}else if (Month.toLowerCase().contains("may")){
			Month_Num = 5;
			month_selected = "May";
		}else if (Month.toLowerCase().contains("june")){
			Month_Num = 6;
			month_selected = "June";
		}else if (Month.toLowerCase().contains("july")){
			Month_Num = 7;
			month_selected = "July";
		}else if (Month.toLowerCase().contains("august")){
			Month_Num = 8;
			month_selected = "August";
		}else if (Month.toLowerCase().contains("september")){
			Month_Num = 9;
			month_selected = "September";
		}else if (Month.toLowerCase().contains("october")){
			Month_Num = 10;
			month_selected = "October";
		}else if (Month.toLowerCase().contains("november")){
			Month_Num = 11;
			month_selected = "November";
		}else if (Month.toLowerCase().contains("december")){
			Month_Num = 12;
			month_selected = "December";
		}else{
			Month_Num = -128;
			month_selected = "Nullity";
		}
		return new String[]{String.valueOf(Month_Num), month_selected};
	}
	public static boolean isInteger(String s) {
		if(s.isEmpty()) return false;
		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && s.charAt(i) == '-') {
				if(s.length() == 1) return false;
				else continue;
			}
			if(! Character.isDigit(s.charAt(i))) return false;
		}
		return true;
	}
	public static void ClearLogs() throws IOException {
		File NormalLog = new File("FileFixer.log");
		File WarnLog = new File("FileFixer.warn.log");
		File ERRLog = new File("FileFixer.err.log");

		if(NormalLog.exists()) { //noinspection ResultOfMethodCallIgnored
			NormalLog.delete();
			//noinspection ResultOfMethodCallIgnored
			NormalLog.createNewFile();
		}

		if(WarnLog.exists()){
			//noinspection ResultOfMethodCallIgnored
			WarnLog.delete();
			//noinspection ResultOfMethodCallIgnored
			WarnLog.createNewFile();
		}

		if(ERRLog.exists()) { //noinspection ResultOfMethodCallIgnored
			ERRLog.delete();
			//noinspection ResultOfMethodCallIgnored
			ERRLog.createNewFile();
		}
	}

}
