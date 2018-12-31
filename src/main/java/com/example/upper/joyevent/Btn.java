package com.example.upper.joyevent;

import java.util.ArrayList;

import static java.lang.Math.min;

/**
 * Created by upper on 18-9-7.
 */

public class Btn {
	private String name;
    private String action;
    private String type;
    private String description;
    private int numPoints;
    private int indexPoint;//当为多点循环时，需要记录当前点的index，初始值为0
    private ArrayList<Point> pointList;

    Btn(){
        this.action = GameLayout.ACTION_CLICK;
        this.type = GameLayout.TYPE_SINGLE;
        this.numPoints = 1;
        this.pointList = new ArrayList<Point>();
        this.indexPoint = 0;
        this.name = null;
        this.description = null;
    }

    //单点click
    Btn(Point point){
        this.action = GameLayout.ACTION_CLICK;
        this.type = GameLayout.TYPE_SINGLE;
        this.numPoints = 1;
        this.pointList = new ArrayList<Point>();
        this.pointList.add(point);
    }

    //多点click
    Btn(int num,ArrayList<Point> points){
        this.action = GameLayout.ACTION_CLICK;
        this.type = GameLayout.TYPE_MULTI;
        this.numPoints = num;
        this.pointList = new ArrayList<Point>(points);
    }

    //划屏1
    Btn(String type,Point startPoint,Point endPoint){
        this.action = GameLayout.ACTION_SWIPE;
        this.type = type;
        this.numPoints = 2;
        this.pointList = new ArrayList<Point>();
        this.pointList.add(startPoint);
        this.pointList.add(endPoint);
    }

    //划屏2
    Btn(Point startPoint,Point endPoint){
        this.type = GameLayout.TYPE_SWIPE_AUTO;
        this.numPoints = 2;
        this.pointList = new ArrayList<Point>();
        this.pointList.add(startPoint);
        this.pointList.add(endPoint);
    }

    public void setName(String strName) {
    	this.name = strName;
    }
    
    public String getName() {
    	return name;
    }
    
    public void setAction(String strAction){
        this.action = strAction;
    }

    public String getAction(){
        return action;
    }

    public void setType(String strType){
        this.type = strType;
    }

    public String getType(){
        return this.type;
    }
    
    public void setDescription(String desc) {
    	this.description = desc;
    }
    
    public String getDescription() {
    	return this.description;
    }

    public void setPointsNum(int num) {
    	this.numPoints = num;
    }
    
    public int getPointsNum(){
        if(pointList == null){
            return 0;
        }else if(this.numPoints == 0){//单点时，numPoints可能没有设置
            return pointList.size();
        }else{//xml中numPoints与Point不一致时
            return min(pointList.size(),numPoints);
        }
    }
    
    public void addPoint(Point point) {
    	this.pointList.add(point);
    }

    public void setPointList(ArrayList<Point> pointlist){
        this.pointList = pointlist;
    }

    public Point getPoint(){
        if(getPointsNum() == 1) {
            return this.pointList.get(0);
        }else if(getPointsNum() > 1){
            return this.pointList.get(indexPoint);
        }else{
            return null;
        }
    }

    public Point getPoint(int index){
        return this.pointList.get(index);
    }

    public int getPointIndex(){
        return this.indexPoint;
    }

    public void increasePointIndex(){
        this.indexPoint = (this.indexPoint+1)%this.getPointsNum();
    }

    public void decreasePointIndex(){
        this.indexPoint = (this.indexPoint-1)%this.getPointsNum();
    }

    public ArrayList<Point> getPointList(){
            return this.pointList;
        }

    public void dump(){
        System.out.println("[BTN]dump enter...");
        System.out.println("\tName:"+getName());
        System.out.println("\tAction:"+getAction());
        System.out.println("\tType:"+getType());
        System.out.println("[BTN]dump exit.");
    }
}