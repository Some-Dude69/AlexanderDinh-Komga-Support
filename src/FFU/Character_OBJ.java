package FFU;
public final class Character_OBJ {
	public short Year;
	public String Month;
	public String Character;
	public String Character_Path;
	public byte Month_Number;

	public Character_OBJ(short year, String month, String character,
	                     String character_path, byte month_number) {
		this.Year = year;
		this.Month = month;
		this.Character = character;
		this.Month_Number = month_number;
		this.Character_Path = character_path;
	}

}
