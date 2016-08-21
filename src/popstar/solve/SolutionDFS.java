package popstar.solve;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import popstar.util.DotType;



public class SolutionDFS {
	private int length;
	private int width;
	private int curMaxScore = 0;
	private int count = 0;
	private ArrayList<Node> curMaxPath = new ArrayList<>();
	
	/*
	 * 构造函数，确定Popstar数组大小范围
	 */
	public SolutionDFS(int[][] input){
		this.length = input.length;
		this.width = input[0].length;
	}
	
	/*
	 * 在得到input数组的getAllConnect之后，确定数组中孤立点的数量
	 */
	public int getSingleDotCount(List<ArrayList<Node>> connectList){
		int res = 0;
		for(ArrayList<Node> nodeSet : connectList){
			if(nodeSet.size() == 1){
				res++;
			}
		}
		return res;
	}
	
	/*
	 * 获得input数组的全部连接，保存为list，其中元素为节点的HashSet, 不包含孤立点
	 */
	public List<ArrayList<Node>> getValidConnect(List<ArrayList<Node>> connectList){
		List<ArrayList<Node>> res = new ArrayList<>();
		for(ArrayList<Node> nodeSet : connectList){
			if(nodeSet.size() > 1){
				res.add((ArrayList<Node>) nodeSet.clone());
			}
		}
		return res;
	}
	
	
	/*
	 * 获得input数组的全部连接，保存为list，其中元素为节点的HashSet, 包含孤立点
	 */
	public List<ArrayList<Node>> getAllConnect(int[][] input){
		List<ArrayList<Node>> res = new ArrayList<>();
		boolean[][] visited = new boolean[this.length][this.width];
		for( int i = 0; i < this.length; i++){
			for(int j = 0; j < this.width; j++){
				if (input[i][j] == DotType.invalid.getTypeId() || visited[i][j]){
					continue;
				};
				res.add(getDotConnect(i, j, input, visited));
			}
		}
		return res;
	}
	
	/*
	 * 移除数组中的一个连通
	 */
	public int[][] removeConnect(int[][] input, ArrayList<Node> nodeSet){
		int[][] res = copyArray(input);
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
		filterCol(res);
		return res;
	}
	
	/*
	 *去除空白的行，实现向左下沉的逻辑 
	 */
	public void filterCol(int[][] input) {
		int firstBlank = 0;
		int firstNotBlank = 0;
		while (firstBlank < this.width && firstNotBlank < this.width) {
			while (input[this.length - 1][firstBlank] != DotType.invalid.getTypeId()) {
				firstBlank++;
				if (firstBlank >= this.width - 1) {
					return;
				}
			}
			firstNotBlank = firstBlank + 1;
			while (input[this.length - 1][firstNotBlank] == DotType.invalid.getTypeId()) {
				firstNotBlank++;
				if (firstNotBlank >= this.width) {
					return;
				}
			}
			
			for(int i = 0; i < this.length; i++){
				input[i][firstBlank] = input[i][firstNotBlank];
				input[i][firstNotBlank] = DotType.invalid.getTypeId();
			}
			firstBlank++;
			firstNotBlank++;
		}
	}
	
	/*
	 * 复制数组
	 */
	public int[][] copyArray(int[][] input) {
		int[][] res = new int[this.length][this.width];
		for( int i = 0; i < this.length; i++){
			for(int j = 0; j < this.width; j++){
				res[i][j] = input[i][j];
			}
		}
		return res;
	}
	
	/*
	 * 计算连通的得分
	 */
	public int getConnectScore(ArrayList<Node> nodeSet){
		return nodeSet.size() * nodeSet.size() * 5;
	}
	
	/*
	 * 计算Bouns得分
	 */
	public int getBonusScore(int count){
		if (count >= 10 || count < 0){
			return 0;
		}
		else{
			return 2000 - 20 * count * count;
		}
	}
	
	/*
	 * 计算所有连通的总得分
	 */
	public int getAllConnectScore(List<ArrayList<Node>> connectList){
		int res = 0;
		for(ArrayList<Node> nodeSet : connectList){
			if(nodeSet.size() > 1){
				res += getConnectScore(nodeSet);
			}
		}
		return res;
		
	}
	
	/*
	 * 计算数组中有效点
	 */
	public int getCurCount(int[][] input){
		int res = 0;
		for(int i = 0; i < this.length; i++){
			for( int j = 0; j < this.width; j++){
				if(input[i][j] != DotType.invalid.getTypeId()){
					res++;
				}
			}
		}
		return res;
	}
	
	/*
	 * 计算数组可能获得的最高得分
	 */
	public int getExpectedMaxScore(HashMap<Integer, Integer> resMap){
		int res = 0;
		for(Integer integer : resMap.values()){
			res += 5 * integer * integer;
		}
		return res;
	}
	
	/*
	 * 计算每种Dot的数量
	 */
	public HashMap<DotType, Integer> getCurDotCount(int[][] input){
		HashMap<DotType, Integer> resMap = new HashMap<>();
		for(int i = 0; i < this.length; i++){
			for( int j = 0; j < this.width; j++){
				if(input[i][j] != DotType.invalid.getTypeId()){
					DotType dotType = DotType.getDotType(input[i][j]);
					if (dotType != null){
						if (!resMap.containsKey(DotType.getDotType(input[i][j]))){
							resMap.put(DotType.getDotType(input[i][j]), 0);
						}
						int count = resMap.get(DotType.getDotType(input[i][j]));
						count++;
						resMap.put(DotType.getDotType(input[i][j]), count);
					}
				}
			}
		}
		return resMap;
	}
	
	/*
	 * 获得最大的Dot数量
	 */
	public int getCurMaxDotCount(HashMap<DotType, Integer> resMap){
		int res = 0;
		for(Integer integer : resMap.values()){
			if (integer > res){
				res = integer;
			}
		}
		return res;
	}
	
	/*
	 * check input is/not valid
	 */
	public boolean chechInput(int[][] input){
		for(int i = 0; i < this.length; i++){
			for( int j = 0; j < this.width; j++){
				if(DotType.getDotType(input[i][j]) == null){
					return false;
				}
			}
		}	
		return true;
	}
	
	/*
	 * 根据Dot坐标取得该Dot的连通
	 */
	public ArrayList<Node> getDotConnect(int x, int y, int[][] input, boolean[][] visited){
		
		if (input[x][y] == DotType.invalid.getTypeId()){
			return null;
		}
		ArrayList<Node> connectDotSet = new ArrayList<>();
		LinkedList<Node> toConnectDotList = new LinkedList<>();
		visited[x][y] = true;
		toConnectDotList.add(new Node(x, y));
		while(!toConnectDotList.isEmpty()){
			Node curNode = toConnectDotList.poll();
			connectDotSet.add(curNode);
			int curX = curNode.getX();
			int curY = curNode.getY();
			if(curX - 1 >= 0 && input[curX - 1][curY] == input[curX][curY] && !visited[curX - 1][curY]){
				toConnectDotList.add(new Node(curX - 1, curY));
				visited[curX - 1][curY] = true;
			}
			if(curY - 1 >= 0 && input[curX][curY - 1] == input[curX][curY] && !visited[curX][curY - 1]){
				toConnectDotList.add(new Node(curX, curY - 1));
				visited[curX][curY - 1] = true;
			}
			if(curX + 1 < this.length && input[curX + 1][curY] == input[curX][curY] && !visited[curX + 1][curY]){
				toConnectDotList.add(new Node(curX + 1, curY));
				visited[curX + 1][curY] = true;
			}
			if(curY + 1 < this.width && input[curX][curY + 1] == input[curX][curY] && !visited[curX][curY + 1]){
				toConnectDotList.add(new Node(curX, curY + 1));
				visited[curX][curY + 1] = true;
			}
			
		}
		return connectDotSet;
	}
	
	public void getMaxScore(int[][] input, int beforeScore, ArrayList<Node> beforePath){
		
		List<ArrayList<Node>> connectList = getAllConnect(input);
		List<ArrayList<Node>> validConnectList = getValidConnect(connectList);
		if (validConnectList.size() == 0){
			if(beforeScore + getBonusScore(connectList.size()) > this.curMaxScore){
				System.out.println(this.curMaxScore);
				this.curMaxScore = beforeScore + getBonusScore(connectList.size());
				this.curMaxPath = (ArrayList<Node>) beforePath.clone();
			}
			return;
		}
		
		for(ArrayList<Node> nodeList : validConnectList){
			if (nodeList.size() == 0){
				System.out.println();
			}
			beforePath.add(nodeList.get(0));
			int[][] temp = removeConnect(input, nodeList);
			getMaxScore(temp, beforeScore + getConnectScore(nodeList), beforePath);
			beforePath.remove(nodeList.get(0));
		}
		return;
	}
	
	public void getScore(int[][] input, ArrayList<Node> inputNode){
		int score = 0;
		int[][] temp = input;
		for(Node node : inputNode){
			boolean[][] visited = new boolean[this.length][this.width];
			ArrayList<Node> nodeList = getDotConnect(node.getX(), node.getY(), temp, visited);
			System.out.println("nodelist size: " + nodeList.size());
			print(temp);
			temp = removeConnect(temp, nodeList);
			score += getConnectScore(nodeList);
		}
		score += getBonusScore(getCurCount(temp));
		System.out.println("bonus size: " + getCurCount(temp));
		System.out.println("score: " + score);
	}
	
	/*
	 * 打印矩阵
	 */
	public void print(int[][] input){
		for(int i = 0; i < this.length; i++){
			for( int j = 0; j < this.width; j++){
					System.out.print(input[i][j]);
					System.out.print("\t");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner scanner = new Scanner(new FileInputStream("C:/Users/suruiliang/Desktop/in"));
		int[][] input = new int[10][10];
		for(int i = 0; i < 10; i++){
			for(int j = 0; j < 10; j++){
				input[i][j] = scanner.nextInt();
			}
		}
		SolutionDFS solution = new SolutionDFS(input);
		ArrayList<Node> beforePath = new ArrayList<>();
//		ArrayList<Node> inputNode = new ArrayList<>();
//		inputNode.add(new Node(1,9));
//		inputNode.add(new Node(6,0));
//		inputNode.add(new Node(5,3));
//		inputNode.add(new Node(5,6));
//		inputNode.add(new Node(6,4));
//		inputNode.add(new Node(5,1));
//		inputNode.add(new Node(2,2));
//		inputNode.add(new Node(1,5));
//		inputNode.add(new Node(3,6));
//		inputNode.add(new Node(6,1));
//		inputNode.add(new Node(5,1));
//		inputNode.add(new Node(3,8));
//		inputNode.add(new Node(7,5));
//		inputNode.add(new Node(4,2));
//		inputNode.add(new Node(6,3));
//		inputNode.add(new Node(7,8));
//		inputNode.add(new Node(8,8));
//		inputNode.add(new Node(9,3));
//		inputNode.add(new Node(7,4));
//		inputNode.add(new Node(6,0));
//		inputNode.add(new Node(9,1));
//		inputNode.add(new Node(9,2));
//		inputNode.add(new Node(8,2));
//		inputNode.add(new Node(9,0));
//		solution.getScore(input, inputNode);
		solution.getMaxScore(input, 0, beforePath);
	}

}
