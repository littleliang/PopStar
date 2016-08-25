package popstar.util;

import org.opencv.core.Point;

public class CvPoint{
	private double x;
	private double y;
	private DotType dotType;
	
	public CvPoint(Point point, int dotTypeValue){
		x = point.x;
		y = point.y;
		dotType = DotType.getDotType(dotTypeValue);
	}
	
	public DotType geDotType() {
		return dotType;
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}
