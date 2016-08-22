package popstar.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileUtil {
	private String imgFileName;
	private int length;
	private int width;
	private int[][] outputArray;
	
	public int[][] getOutputArray() {
		return outputArray;
	}

	public FileUtil(String fileName, int length, int width) throws IOException{
		this.length = length;
		this.width = width;
		this.imgFileName = fileName;
		outputArray = new int[length][width];
		File file = new File(imgFileName);
		BufferedImage bufferedImage = ImageIO.read(file);
	}
	
	public static void main(String[] args) throws IOException {
		FileUtil fileUtil = new FileUtil("C:/Users/suruiliang/Desktop/in.png", 10, 10);
		System.out.println(fileUtil);
	}
	
	
}
