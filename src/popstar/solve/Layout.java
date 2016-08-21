package popstar.solve;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import popstar.util.DotType;
import popstar.util.LayoutScoreEnum;
import popstar.util.Util;

public class Layout {
	private int[][] input;
	private int length;
	private int width;
	private List<ArrayList<Node>> allConnect = new ArrayList<>();
	private List<ArrayList<Node>> validConnect = new ArrayList<>();
	private List<ArrayList<Node>> signleDot = new ArrayList<>();
	private ArrayList<Node> maxConnect = new ArrayList<>();
	private int curTotalSingleCount = 0;
	private HashMap<DotType, Integer> curDotCount = new HashMap<>();
	private int curTotalCount = 0;
	private int curMaxDotCount = 0;
	private int expectedMaxScore = 0;
	private int allConnectScore = 0;
	private int curTotalConnectCount = 0;
	private int curMaxConnectCount = 1;
	private DotType curMaxCountType;
	private DotType curMaxConnectCountType;
	private int[] curColCount;
	private int[] curSingleColCount;
	private double[] curSingleColRatio;
	private double curLayoutScore;
	private Layout beforeLayout;

	public Layout(int[][] input){
		this.length = input.length;
		this.width = input[0].length;
		this.input = Util.copyArray(input, length, width);
		curColCount = new int[width];
		curSingleColCount = new int[width];
		curSingleColRatio = new double[width];
		
		getAllConnect(input);
		getValidConnect(allConnect);
		getSingleDotList(allConnect);
		getCurDotCount(input);
		getCurMaxDotCount(curDotCount);
		
		for(int i = 0; i < width; i++){
			curSingleColRatio[i] = (double) curSingleColCount[i] / curColCount[i];
		}
		calCurLayoutScore();
		
	}
	
	public void setBeforeLayout(Layout beforeLayout) {
		this.beforeLayout = beforeLayout;
	}
	
	public Layout getBeforeLayout() {
		return beforeLayout;
	}
	
	public ArrayList<Node> getMaxConnect() {
		return maxConnect;
	}

	public int[][] getInput() {
		return input;
	}
	
	public int getCurTotalSingleCount() {
		return curTotalSingleCount;
	}

	public List<ArrayList<Node>> getValidConnect() {
		return validConnect;
	}
	
	public int getLength() {
		return length;
	}

	public int getWidth() {
		return width;
	}
	
	public double getCurLayoutScore() {
		return curLayoutScore;
	}

	private void calCurLayoutScore(){
		if(curTotalSingleCount > curTotalConnectCount || curSingleColRatio[0] > 0.95){
			curLayoutScore = LayoutScoreEnum.singledot_too_many.getScore();
			return;
		}
		
		for(int i = 1; i < width - 1; i++){
			if(curSingleColRatio[i] > curSingleColRatio[i - 1] + 0.5 || curSingleColRatio[i] > curSingleColRatio[i + 1] + 0.5){
				curLayoutScore = LayoutScoreEnum.singledot_fault.getScore();
				return;
			}
		}
		
		if(curMaxConnectCountType == curMaxCountType && curMaxConnectCount > curMaxDotCount - 5 && curMaxConnectCount > 0){
			curLayoutScore = LayoutScoreEnum.max_connect.getScore();
			return;
		}
		
		curLayoutScore = (double) (allConnectScore + Util.getBonusScore(curTotalSingleCount)) * curTotalConnectCount / (curTotalSingleCount * curTotalCount * expectedMaxScore);
	}
	
	private void getSingleDotList(List<ArrayList<Node>> connectList){
		for(ArrayList<Node> nodeSet : connectList){
			if(nodeSet.size() == 1){
				signleDot.add(nodeSet);
				curSingleColCount[nodeSet.get(0).getY()]++;
				curTotalSingleCount++;
			}
		}
	}
	
	/*
	 * 获得input数组的全部连接，保存为list，其中元素为节点的HashSet, 不包含孤立点
	 */
	private void getValidConnect(List<ArrayList<Node>> connectList){
		for(ArrayList<Node> nodeSet : connectList){
			if(nodeSet.size() > 1){
				if(nodeSet.size() > curMaxConnectCount){
					curMaxConnectCount = nodeSet.size();
					maxConnect = nodeSet;
					curMaxConnectCountType = DotType.getDotType(input[nodeSet.get(0).getX()][nodeSet.get(0).getY()]);
				}
				validConnect.add(nodeSet);
				curTotalConnectCount += nodeSet.size();
				allConnectScore += Util.getConnectScore(nodeSet);
			}
		}
	}
	
	
	/*
	 * 获得input数组的全部连接，保存为list，其中元素为节点的HashSet, 包含孤立点
	 */
	private void getAllConnect(int[][] input){
		boolean[][] visited = new boolean[this.length][this.width];
		for( int i = 0; i < this.length; i++){
			for(int j = 0; j < this.width; j++){
				if (input[i][j] == DotType.invalid.getTypeId() || visited[i][j]){
					continue;
				};
				allConnect.add(getDotConnect(i, j, input, visited));
			}
		}
	}	

	/*
	 * 计算每种Dot的数量
	 */
	private void getCurDotCount(int[][] input){
		for(int i = 0; i < this.length; i++){
			for( int j = 0; j < this.width; j++){
				if(input[i][j] != DotType.invalid.getTypeId()){
					DotType dotType = DotType.getDotType(input[i][j]);
					if (dotType != null){
						if (!curDotCount.containsKey(DotType.getDotType(input[i][j]))){
							curDotCount.put(DotType.getDotType(input[i][j]), 0);
						}
						int count = curDotCount.get(DotType.getDotType(input[i][j]));
						count++;
						curDotCount.put(DotType.getDotType(input[i][j]), count);
						curColCount[j]++;
					}
				}
			}
		}
	}
	
	/*
	 * 获得最大的Dot数量
	 */
	private void getCurMaxDotCount(HashMap<DotType, Integer> resMap){
		for(Entry<DotType, Integer> entry : resMap.entrySet()){
			if (entry.getValue() > curMaxDotCount){
				curMaxDotCount = entry.getValue();
				curMaxCountType = entry.getKey();
			}
			expectedMaxScore += 5 * entry.getValue() * entry.getValue();
			curTotalCount += entry.getValue();
		}
	}
	
	
	/*
	 * 根据Dot坐标取得该Dot的连通
	 */
	private ArrayList<Node> getDotConnect(int x, int y, int[][] input, boolean[][] visited){
		
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
				toConnectDotList.add(Util.nodeArray[curX - 1][curY]);
				visited[curX - 1][curY] = true;
			}
			if(curY - 1 >= 0 && input[curX][curY - 1] == input[curX][curY] && !visited[curX][curY - 1]){
				toConnectDotList.add(Util.nodeArray[curX][curY - 1]);
				visited[curX][curY - 1] = true;
			}
			if(curX + 1 < this.length && input[curX + 1][curY] == input[curX][curY] && !visited[curX + 1][curY]){
				toConnectDotList.add(Util.nodeArray[curX + 1][curY]);
				visited[curX + 1][curY] = true;
			}
			if(curY + 1 < this.width && input[curX][curY + 1] == input[curX][curY] && !visited[curX][curY + 1]){
				toConnectDotList.add(Util.nodeArray[curX][curY + 1]);
				visited[curX][curY + 1] = true;
			}
			
		}
		return connectDotSet;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
	}

}
