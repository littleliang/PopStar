package popstar.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	private static final String tempFileName = "temp.jpg";
	
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
		int count = 0;
		ArrayList<CvPoint> nodeList = new ArrayList<CvPoint>();
		while(count < length * width){
			count++;
			MinMaxLocResult mlr;
			Point matchLoc = null;
			int dotTypeValue = 0;
			double maxScore = 0 - Double.MAX_VALUE;
			for(int i = 0; i < templateMat.size(); i++){
				Imgproc.matchTemplate(sourceMat, templateMat.get(i), result,Imgproc.TM_CCOEFF_NORMED);
				mlr = Core.minMaxLoc(result);
			    if(mlr.maxVal > maxScore){
			    	maxScore = mlr.maxVal;
			    	matchLoc = mlr.maxLoc;
			    	dotTypeValue = i + 1;
			    }
			}
			nodeList.add(new CvPoint(matchLoc, dotTypeValue));
			Core.rectangle(sourceMat, matchLoc, new Point(matchLoc.x + templateMat.get(0).width(),matchLoc.y + templateMat.get(0).height()), new Scalar(0,0,0), -1);
		}
		Collections.sort(nodeList, new Comparator<CvPoint>(){

			@Override
			public int compare(CvPoint o1, CvPoint o2) {
				if(o1.getX() < o2.getX())
					return -1;
				else if(o1.getX() > o2.getX())
					return 1;
				else
					return 0;
			}
			
		});
		for(int i = 0; i < width; i++){
			List<CvPoint> sortList = nodeList.subList(i * length, (i + 1) * length);
			Collections.sort(sortList, new Comparator<CvPoint>(){

				@Override
				public int compare(CvPoint o1, CvPoint o2) {
					if(o1.getY() < o2.getY())
						return -1;
					else if(o1.getY() > o2.getY())
						return 1;
					else
						return 0;
				}
				
			});
			for(int j = 0; j < length; j++){
				outputArray[j][i] = sortList.get(j).geDotType().getTypeId();
			}
		}
	}
}
