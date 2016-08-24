package popstar.util;


public class FileUtil {
	private static final String[] templateFileNames = {"1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg"};
	
	private String sourceFileName;
	private int length;
	private int width;
	private int[][] outputArray;
	
	public int[][] getOutputArray() {
		return outputArray;
	}

	public FileUtil(String sourceFileName, int length, int width){
		this.length = length;
		this.width = width;
		this.sourceFileName = sourceFileName;
		outputArray = new int[length][width];
		
	}
	
	public static void main(String[] args){
	}
	
	
}
