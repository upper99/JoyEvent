package com.example.upper.joyevent;

public class Point {
	private int id;
	private int x;
	private int y;

	public Point(){
		id = 0;
		x = 0;
		y = 0;
	}

	public Point(int x,int y){
		id = 0;
		this.x = x;
		this.y = y;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}
	
	public int getId() {
		return id;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setX(String x) {
		this.x = Integer.parseInt(x);
	}
	
	public int getX() {
		return x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setY(String y) {
		this.y = Integer.parseInt(y);
	}
	
	public int getY() {
		return this.y;
	}
	
	
}
