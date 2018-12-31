package com.example.upper.joyevent;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Joystick {
	private String name;
	private int radius;
	private String type;
	private int response;
	private String description;
	private Point original;
	
	Joystick(){
		this.name = "";
		this.radius = 100;
		this.type = GameLayout.TYPE_JOYSTICK_STANDARD;
		this.response = 10;
		this.description = "";
		this.original = null;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public int getRadius() {
		return this.radius;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return this.type;
	}

	public void setResponse(int response){
		this.response = response;
	}

	public int getResponse(){
		return this.response;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setOriginal(Point point) {
		this.original = point;
	}
	
	public Point getOriginal() {
		return this.original;
	}

	public void dump(){
		System.out.println("[JOYSTICK]dump enter...");
		System.out.println("\tName:"+getName());
		System.out.println("\tRadius:"+getRadius());
		if(getOriginal() != null) System.out.println("\tOriginal:"+getOriginal().getX()+","+getOriginal().getY());
		System.out.println("[JOYSTICK]dump exit.");
	}
}
