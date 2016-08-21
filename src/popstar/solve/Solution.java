package popstar.solve;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import popstar.util.Util;

public class Solution {
	private int curMaxScore = 0;
	private int count = 0;
	private Layout curMaxScoreLayout;
	private Layout originLayout;
	private ArrayList<Node> curMaxPath = new ArrayList<>();
	private HashMap<Layout, Integer> curTotalScore = new HashMap<>();
	private HashMap<Layout, ArrayList<Node>> curTotalPath = new HashMap<>();
	private HashSet<Layout> layoutSet = new HashSet<>();
	
	public Solution(Layout layout){
		originLayout = layout;
		layoutSet.add(layout);
		curTotalScore.put(layout, 0);
		InitFirst3Step(originLayout, 0);
		System.out.println(layoutSet.size());
		getMaxScore();
		System.out.println("score: " + curMaxScore);
	}
	
	private void InitFirst3Step(Layout ancestor, int layer){
		if(layer == 3 || count >= 1000){
			count++;
			return;
		}
		layoutSet.remove(ancestor);
		List<ArrayList<Node>> validConnectList = ancestor.getValidConnect();
		if(validConnectList.size() == 0){
			if(curTotalScore.get(ancestor) + Util.getBonusScore(ancestor.getCurTotalSingleCount()) > curMaxScore){
				curMaxScore = curTotalScore.get(ancestor) + Util.getBonusScore(ancestor.getCurTotalSingleCount());
				curMaxPath = curTotalPath.get(ancestor);
				curMaxScoreLayout = ancestor;
			}
			return;
		}
		for(ArrayList<Node> nodeList : validConnectList){
			int[][] temp = Util.removeConnect(ancestor.getInput(), nodeList, ancestor.getLength(), ancestor.getWidth());
			Layout layout = new Layout(temp);
			layoutSet.add(layout);
			layout.setBeforeLayout(ancestor);
			curTotalScore.put(layout, curTotalScore.get(ancestor) + Util.getConnectScore(nodeList));
			ArrayList<Node> tempList = new ArrayList<>();
			if (curTotalPath.containsKey(ancestor)){
				tempList.addAll(curTotalPath.get(ancestor));
			}
			tempList.add(nodeList.get(0));
			curTotalPath.put(layout, tempList);
			InitFirst3Step(layout, layer + 1);
		}
		return;
		
	}
	
	public void getMaxScore(){
		int count = 0;
		for(Layout layout : layoutSet){
			count++;
			if(count % 1000 == 0){
				System.out.println("count: " + count);
			}
			Layout tempLayout = layout;
			List<ArrayList<Node>> validConnectList = tempLayout.getValidConnect();
			while(tempLayout.getValidConnect().size() != 0){
				double layoutScore = 0 - Double.MAX_VALUE;
				Layout tempMax = null;
				ArrayList<Node> tempNodeList = null;
				ArrayList<Node> tempList = new ArrayList<>();
				
				for(ArrayList<Node> nodeList : validConnectList){
					int[][] tempInput = Util.removeConnect(tempLayout.getInput(), nodeList, tempLayout.getLength(), tempLayout.getWidth());
					Layout temp = new Layout(tempInput);
					if(temp.getCurLayoutScore() > layoutScore){
						tempMax = temp;
						layoutScore = temp.getCurLayoutScore();
						tempNodeList = nodeList;
					}
				}
				if (curTotalPath.containsKey(tempLayout)){
					tempList.addAll(curTotalPath.get(tempLayout));
				}
				tempList.add(tempNodeList.get(0));
				curTotalPath.put(tempMax, tempList);
				curTotalPath.remove(tempLayout);
				curTotalScore.put(tempMax, curTotalScore.get(tempLayout) + Util.getConnectScore(tempNodeList));
				curTotalScore.remove(tempLayout);
				tempMax.setBeforeLayout(tempLayout);
				if(layoutScore == Double.MAX_VALUE && tempMax.getMaxConnect().size() != 0){
					ArrayList<Node> temptempList = new ArrayList<>();
					int[][] tempInput = Util.removeConnect(tempMax.getInput(), tempMax.getMaxConnect(), tempMax.getLength(), tempMax.getWidth());
					Layout temp = new Layout(tempInput);
					temp.setBeforeLayout(tempMax);
					temptempList.addAll(tempList);
					temptempList.add(tempMax.getMaxConnect().get(0));
					curTotalPath.put(temp, temptempList);
					curTotalPath.remove(tempMax);
					curTotalScore.put(temp, curTotalScore.get(tempMax) + Util.getConnectScore(tempMax.getMaxConnect()));
					curTotalScore.remove(tempMax);
					tempMax = temp;
				}
				tempLayout = tempMax;
				validConnectList = tempLayout.getValidConnect();
			}
			if(curTotalScore.get(tempLayout) + Util.getBonusScore(tempLayout.getCurTotalSingleCount()) > curMaxScore){
				curMaxScore = curTotalScore.get(tempLayout) + Util.getBonusScore(tempLayout.getCurTotalSingleCount());
				curMaxPath = curTotalPath.get(tempLayout);
				curMaxScoreLayout = tempLayout;
			}
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		Random ra =new Random();
		int score = 0;
		int count = 0;
		Solution solution;
		int[][] input = new int[10][10];
		for(int k = 0; k < 100; k++){
			for(int i = 0; i < 10; i++){
				for(int j = 0; j < 10; j++){
					input[i][j] = ra.nextInt(5) + 1;
				}
			}
			solution = new Solution(new Layout(input));
			score += solution.curMaxScore;
			count++;
		}
		
		
		System.out.println("average score: " + score / count);
	}

}
