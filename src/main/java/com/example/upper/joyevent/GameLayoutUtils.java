package com.example.upper.joyevent;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 10126090 on 2018/10/25.
 */
public final class GameLayoutUtils {
    public static String textval(EditText et){
        String val = et.getText().toString().trim();
        if(val.equals("")){
            return null;
        }else{
            return val;
        }
    }

    public static String xmlFileName2PackageName(String xmlFileName){
        if(xmlFileName.endsWith(GameLayout.XML_SUFFIX)) {
            return xmlFileName.substring(0,xmlFileName.length()-GameLayout.XML_SUFFIX.length());
        }else{
            return null;
        }
    }

    //根据字符串"（x,y）"解析出Point
    public static Point parsePoint(String corrdirate){
        Point retPoint = null;
        String pattern = "^\\((\\d+),\\s*(\\d+)\\)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(corrdirate);
        if(m.find()){
            retPoint = new Point();
            retPoint.setX(Integer.parseInt(m.group(1)));
            retPoint.setY(Integer.parseInt(m.group(2)));
            System.out.println(m.group(1)+","+m.group(2));
        }
        return retPoint;
    }

    //根据"(x1,y1)/(x2,y2)"解析
    public static ArrayList<Point> parsePoints(String corrdirates){
        ArrayList<Point> points = new ArrayList<Point>();
        if(corrdirates.contains("/")) {
            String[] arr = corrdirates.split("/");
            for (int i = 0; i < arr.length; i++) {
                Point p = parsePoint(arr[i]);
                if (p != null) points.add(p);
            }
        }else{
            Point p = parsePoint(corrdirates);
            if(p != null) {
                points.add(p);
            }
        }

        return points;
    }

    public static String formatPoint(Point point){
        if(point == null){
            return "";
        }else{
            String ret = String.format("(%d,%d)",point.getX(),point.getY());
            System.out.println("formatPoint ret:"+ret);
            return ret;
        }
    }

    public static String formatPoints(ArrayList<Point> points){
        if(points == null || points.size() == 0) return "";
        String ret = "";
        for(Point p:points){
            ret += formatPoint(p)+"/";
        }

        return ret.substring(0,ret.length()-1);
    }
}
