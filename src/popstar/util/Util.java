package popstar.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import popstar.solve.Node;

public class Util {
	private static final int length = 15;
	private static final int width = 15;
	public static Node[][] nodeArray = new Node[length][width];
	static {
		for(int i = 0; i < length; i++){
			for(int j = 0; j < width; j++){
				nodeArray[i][j] = new Node(i, j);
			}
		}
		
	}
	
	
	public static int[][] copyArray(int[][] input, int length, int width) {
		int[][] res = new int[length][width];
		for( int i = 0; i < length; i++){
			for(int j = 0; j < width; j++){
				res[i][j] = input[i][j];
			}
		}
		return res;
	}
	
	public static boolean checkInput(int[][] input, int length, int width){
		for(int i = 0; i < length; i++){
			for( int j = 0; j < width; j++){
				if(DotType.getDotType(input[i][j]) == null){
					return false;
				}
			}
		}	
		return true;
	}
	
	public static void print(int[][] input, int length, int width){
		for(int i = 0; i < length; i++){
			for( int j = 0; j < width; j++){
					System.out.print(input[i][j]);
					System.out.print("\t");
			}
			System.out.println();
		}
	}
	
	public static void arrayToFile(String filename, int[][] input, int length, int width) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
		for(int i = 0; i < length; i++){
			for( int j = 0; j < width; j++){
					bw.append(input[i][j] + "\t");
			}
			bw.append("\n");
		}
		bw.close();
	}
	
	public static int getBonusScore(int count){
		if (count >= 10 || count < 0){
			return 0;
		}
		else{
			return 2000 - 20 * count * count;
		}
	}
	
	/*
	 * 移除数组中的一个连通
	 */
	public static int[][] removeConnect(int[][] input, ArrayList<Node> nodeSet, int length, int width){
		int[][] res = copyArray(input, length, width);
		Collections.sort(nodeSet);
		for(Node node : nodeSet){
			int x = node.getX();
			int y = node.getY();
			for(int i = x; i > 0; i--){
				if(res[i - 1][y] == DotType.invalid.getTypeId()){
					res[i][y] = DotType.invalid.getTypeId();
					break;
				}
				res[i][y] = res[i - 1][y];
			}
			res[0][y] = DotType.invalid.getTypeId();
		}
		filterCol(res, length, width);
		return res;
	}
	
	/*
	 *去除空白的行，实现向左下沉的逻辑 
	 */
	public static void filterCol(int[][] input, int length, int width) {
		int firstBlank = 0;
		int firstNotBlank = 0;
		while (firstBlank < width && firstNotBlank < width) {
			while (input[length - 1][firstBlank] != DotType.invalid.getTypeId()) {
				firstBlank++;
				if (firstBlank >= width - 1) {
					return;
				}
			}
			firstNotBlank = firstBlank + 1;
			while (input[length - 1][firstNotBlank] == DotType.invalid.getTypeId()) {
				firstNotBlank++;
				if (firstNotBlank >= width) {
					return;
				}
			}
			
			for(int i = 0; i < length; i++){
				input[i][firstBlank] = input[i][firstNotBlank];
				input[i][firstNotBlank] = DotType.invalid.getTypeId();
			}
			firstBlank++;
			firstNotBlank++;
		}
	}
	/*
	 * 计算连通的得分
	 */
	public static int getConnectScore(ArrayList<Node> nodeSet){
		return nodeSet.size() * nodeSet.size() * 5;
	}
}
