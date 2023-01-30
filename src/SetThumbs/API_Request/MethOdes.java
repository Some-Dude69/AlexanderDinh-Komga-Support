package SetThumbs.API_Request;

import org.jetbrains.annotations.NotNull;

public final class MethOdes {
	private MethOdes(){}
	public static @NotNull String[] Find_Month_Num(String Month){
		byte Month_Num;
		String month_selected;
		if (Month.contains("January")){
			Month_Num = 1;
			month_selected = "January";
		}else if (Month.contains("February")){
			Month_Num = 2;
			month_selected = "February";
		}else if (Month.contains("March")){
			Month_Num = 3;
			month_selected = "March";
		}else if (Month.contains("April")){
			Month_Num = 4;
			month_selected = "April";
		}else if (Month.contains("May")){
			Month_Num = 5;
			month_selected = "May";
		}else if (Month.contains("June")){
			Month_Num = 6;
			month_selected = "June";
		}else if (Month.contains("July")){
			Month_Num = 7;
			month_selected = "July";
		}else if (Month.contains("August")){
			Month_Num = 8;
			month_selected = "August";
		}else if (Month.contains("September")){
			Month_Num = 9;
			month_selected = "September";
		}else if (Month.contains("October")){
			Month_Num = 10;
			month_selected = "October";
		}else if (Month.contains("November")){
			Month_Num = 11;
			month_selected = "November";
		}else if (Month.contains("December")){
			Month_Num = 12;
			month_selected = "December";
		}else{
			Month_Num = -128;
			month_selected = "Nullity";
		}
		return new String[]{String.valueOf(Month_Num), month_selected};
	}
	public static boolean isInteger(@NotNull String s) {
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
}
