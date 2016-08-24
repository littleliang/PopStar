package popstar.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class FileUtil {
	private static final String[] templateFileNames = {"1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg"};
	private static final String tempFileName = "C:/Users/temp.jpg";
	
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

	public FileUtil(String sourceFileName, int length, int width) throws IOException{
		this.length = length;
		this.width = width;
		this.sourceFileName = sourceFileName;
		outputArray = new int[this.length][this.width];
		sourceMat = Highgui.imread(this.sourceFileName);
		for(String tempalteFileName : templateFileNames){
			InputStream input = this.getClass().getResourceAsStream("/" + tempalteFileName);
			FileOutputStream fos = new FileOutputStream(tempFileName);
			byte[] b = new byte[1024];
			while((input.read(b)) != -1){
				fos.write(b);
			}
			input.close();
			fos.close();
			templateMat.add(Highgui.imread(tempFileName));
		}
		File file = new File(tempFileName);
		file.delete();
		Process();
	}
	
	public void Process(){
		Mat result = Mat.zeros(sourceMat.rows(), sourceMat.cols(), CvType.CV_32FC1);
		for(Mat mat : templateMat){
			Imgproc.matchTemplate(sourceMat, mat, result,Imgproc.TM_SQDIFF_NORMED);
			MinMaxLocResult mlr = Core.minMaxLoc(result);
		    Point matchLoc = mlr.minLoc;
		    while(mlr.minVal < 0.013){
		    	Core.rectangle(sourceMat, matchLoc, new Point(matchLoc.x + templateMat.get(0).width(),matchLoc.y + templateMat.get(0).height()), new Scalar(0,0,0), -1);
		    	Imgproc.matchTemplate(sourceMat, mat, result,Imgproc.TM_SQDIFF_NORMED);
				mlr = Core.minMaxLoc(result);
			    matchLoc = mlr.minLoc;
		    }
		    Core.rectangle(sourceMat, matchLoc, new Point(matchLoc.x + templateMat.get(0).width(),matchLoc.y + templateMat.get(0).height()), new Scalar(0,0,0), -1);
		    Highgui.imwrite("C:/Users/suruiliang/Desktop/res.jpg",sourceMat);
		}
	    Highgui.imwrite("C:/Users/suruiliang/Desktop/res.jpg",sourceMat);
	}
	
	public static void main(String[] args) throws IOException{
		FileUtil fileUtil = new FileUtil("C:/Users/suruiliang/Desktop/test.jpg", 10, 10);
		System.out.println(fileUtil);
	}
	
	
}
