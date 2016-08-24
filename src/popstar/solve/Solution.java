package popstar.solve;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import popstar.util.LayoutScoreEnum;
import popstar.util.Util;

public class Solution {
	private static final int levelMaxPathNum = 8;
	private static final int levelMaxDepth = 5;
	private static final int levelBadPathNum = 2;
	
	private int curMaxScore = 0;
	private int initStepCount = 0;
	private Layout curMaxScoreLayout;
	private Layout rootLayout;
	private ArrayList<Node> curMaxPath = new ArrayList<Node>();
	private HashMap<Layout, Integer> curTotalScore = new HashMap<Layout, Integer>();
	private HashMap<Layout, ArrayList<Node>> curTotalPath = new HashMap<Layout, ArrayList<Node>>();
	private HashSet<Layout> layoutSet = new HashSet<Layout>();
	
	public Layout getCurMaxScoreLayout(){
		return curMaxScoreLayout;
	}
	
	public Solution(Layout layout){
		rootLayout = layout;
		layoutSet.add(layout);
		curTotalScore.put(layout, 0);
		InitStep(rootLayout, 0);
		System.out.println("num of path: " + initStepCount);
		getMaxScore();
		System.out.println("score: " + curMaxScore);
		System.out.println("path: " + curMaxPath);
	}
	
	private void InitStep(Layout ancestor, int layer){
		if(layer == levelMaxDepth){
			initStepCount++;
			return;
		}
		List<ArrayList<Node>> validConnectList = ancestor.getValidConnect();
		if(validConnectList.size() == 0){
			if(curTotalScore.get(ancestor) + Util.getBonusScore(ancestor.getCurTotalSingleCount()) > curMaxScore){
				curMaxScore = curTotalScore.get(ancestor) + Util.getBonusScore(ancestor.getCurTotalSingleCount());
				curMaxPath = curTotalPath.get(ancestor);
				curMaxScoreLayout = ancestor;
			}
			return;
		}
		
		ArrayList<Layout> layoutList = new ArrayList<Layout>();
		
		for(ArrayList<Node> nodeList : validConnectList){
			int[][] nextInput = Util.removeConnect(ancestor.getInput(), nodeList, ancestor.getLength(), ancestor.getWidth());
			Layout layout = new Layout(nextInput);
			layout.setBeforeClick(nodeList);
			layoutList.add(layout);
		}
		Collections.sort(layoutList);
		while(layoutList.size() > levelMaxPathNum ){
			if(layoutList.get(layoutList.size() - 1).getCurLayoutScore() > LayoutScoreEnum.singledot_fault.getScore()){
				layoutList.remove(levelMaxPathNum - levelBadPathNum - 1);
			}
			else{
				layoutList.remove(levelMaxPathNum - 1);
			}
			
		}
		for(Layout layout : layoutList){
			layoutSet.add(layout);
			curTotalScore.put(layout, curTotalScore.get(ancestor) + Util.getConnectScore(layout.getBeforeClick()));
			ArrayList<Node> pathLayout = new ArrayList<Node>();
			if (curTotalPath.containsKey(ancestor)){
				pathLayout.addAll(curTotalPath.get(ancestor));
				
			}
			pathLayout.add(layout.getBeforeClick().get(0));
			curTotalPath.put(layout, pathLayout);
			InitStep(layout, layer + 1);
		}
		
		
		layoutSet.remove(ancestor);
		curTotalPath.remove(ancestor);
		curTotalScore.remove(ancestor);
		return;
		
	}
	
	public void getMaxScore(){
		Iterator<Layout> iterator = layoutSet.iterator();
		while(iterator.hasNext()){
			Layout nextLayout = iterator.next();
			List<ArrayList<Node>> validConnectList = nextLayout.getValidConnect();
			while(nextLayout.getValidConnect().size() != 0){
				double inLoopMaxLayoutScore = 0 - Double.MAX_VALUE;
				Layout inLoopMaxLayout = null;
				ArrayList<Node> inLoopConnectNodeList = null;
				ArrayList<Node> inLoopMaxPathLayout = new ArrayList<Node>();
				
				for(ArrayList<Node> nodeList : validConnectList){
					int[][] nextInput = Util.removeConnect(nextLayout.getInput(), nodeList, nextLayout.getLength(), nextLayout.getWidth());
					Layout inLoopNextLayout = new Layout(nextInput);
					if(inLoopNextLayout.getCurLayoutScore() > inLoopMaxLayoutScore){
						inLoopMaxLayout = inLoopNextLayout;
						inLoopMaxLayoutScore = inLoopNextLayout.getCurLayoutScore();
						inLoopConnectNodeList = nodeList;
					}
				}
				if (curTotalPath.containsKey(nextLayout)){
					inLoopMaxPathLayout.addAll(curTotalPath.get(nextLayout));
				}
				inLoopMaxPathLayout.add(inLoopConnectNodeList.get(0));
				curTotalPath.put(inLoopMaxLayout, inLoopMaxPathLayout);
				curTotalPath.remove(nextLayout);
				curTotalScore.put(inLoopMaxLayout, curTotalScore.get(nextLayout) + Util.getConnectScore(inLoopConnectNodeList));
				curTotalScore.remove(nextLayout);
				if(inLoopMaxLayoutScore == Double.MAX_VALUE && inLoopMaxLayout.getMaxConnect().size() != 0){
					ArrayList<Node> nextMaxConnectPath = new ArrayList<Node>();
					int[][] nextInput = Util.removeConnect(inLoopMaxLayout.getInput(), inLoopMaxLayout.getMaxConnect(), inLoopMaxLayout.getLength(), inLoopMaxLayout.getWidth());
					Layout nextMaxConnectLayout = new Layout(nextInput);
					nextMaxConnectPath.addAll(inLoopMaxPathLayout);
					nextMaxConnectPath.add(inLoopMaxLayout.getMaxConnect().get(0));
					curTotalPath.put(nextMaxConnectLayout, nextMaxConnectPath);
					curTotalPath.remove(inLoopMaxLayout);
					curTotalScore.put(nextMaxConnectLayout, curTotalScore.get(inLoopMaxLayout) + Util.getConnectScore(inLoopMaxLayout.getMaxConnect()));
					curTotalScore.remove(inLoopMaxLayout);
					inLoopMaxLayout = nextMaxConnectLayout;
				}
				nextLayout = inLoopMaxLayout;
				validConnectList = nextLayout.getValidConnect();
			}
			if(curTotalScore.get(nextLayout) + Util.getBonusScore(nextLayout.getCurTotalSingleCount()) > curMaxScore){
				curMaxScore = curTotalScore.get(nextLayout) + Util.getBonusScore(nextLayout.getCurTotalSingleCount());
				curMaxPath = curTotalPath.get(nextLayout);
				curMaxScoreLayout = nextLayout;
				curTotalScore.remove(nextLayout);
				curTotalPath.remove(nextLayout);
			}
			else{
				curTotalScore.remove(nextLayout);
				curTotalPath.remove(nextLayout);
			}
			iterator.remove();
		}
	}

	public static void main(String[] args) throws FileNotFoundException{
		Random ra =new Random();
		int score = 0;
		int count = 0;
		Solution solution;
		ArrayList<Integer> scoreList = new ArrayList<Integer>();
		int[][] input = new int[10][10];
		for(int k = 0; k < 100; k++){
			long start = System.currentTimeMillis();
			System.out.println("case:" + (k + 1));
			for(int i = 0; i < 10; i++){
				for(int j = 0; j < 10; j++){
					input[i][j] = ra.nextInt(5) + 1;
				}
			}
			solution = new Solution(new Layout(input));
			scoreList.add(solution.curMaxScore);
			score += solution.curMaxScore;
			count++;
			long end = System.currentTimeMillis();
			System.out.println("time consume: " + (end - start));
		}
		Collections.sort(scoreList);
		
		System.out.println("average score: " + score / count);
//		Scanner scanner = new Scanner(new FileInputStream("C:/Users/suruiliang/Desktop/in"));
//		int[][] input = new int[10][10];
//		for(int i = 0; i < 10; i++){
//			for(int j = 0; j < 10; j++){
//				input[i][j] = scanner.nextInt();
//			}
//		}
//		Solution solution = new Solution(new Layout(input));
//		System.out.println(solution.curMaxScore);
//		scanner.close();
	}
}
