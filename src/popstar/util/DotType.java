package popstar.util;

public enum DotType {
	invalid(0), red(1), green(2), blue(3), yellow(4), violet(5);
	
	private int typeId;
	
	private DotType(int typeId) {
		this.typeId = typeId;
	}
	
	public static DotType getDotType(int typeId) {
		switch (typeId) {
		case 1:
			return red;
		case 2:
			return green;
		case 3:
			return blue;
		case 4:
			return yellow;
		case 5:
			return violet;
		default:
			return null;
		}
	}
	
	public int getTypeId(){
		return typeId;
	}
}