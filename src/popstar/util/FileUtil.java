package popstar.util;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class FileUtil {
	private static final String[] templateFileNames = {"1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg"};
	
	static{
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
	}
	
	private String sourceFileName;
	private int length;
	private int width;
	private int[][] outputArray;
	private Mat sourceMat;
	private ArrayList<Mat> templateMat = new ArrayList<Mat>();
	
	public int[][] getOutputArray() {
		return outputArray;
	}

	public FileUtil(String sourceFileName, int length, int width){
		this.length = length;
		this.width = width;
		this.sourceFileName = sourceFileName;
		outputArray = new int[length][width];
		sourceMat = Highgui.imread(sourceFileName);
		for(String tempalteFileName : templateFileNames){
			String fileName = ClassLoader.getSystemResource("popstar/template/" + tempalteFileName).getFile();
			System.out.println(fileName);
			templateMat.add(Highgui.imread(fileName));
			Highgui.imwrite("C:/Users/suruiliang/Desktop/" + tempalteFileName, templateMat.get(templateMat.size() - 1));
		}
		
	}
	
	public static void main(String[] args){
		FileUtil fileUtil = new FileUtil("C:/Users/suruiliang/Desktop/test.jpg", 10, 10);
	}
	
	
}
