package com.example.upper.joyevent;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by upper on 18-10-15.
 */

public class XmlGen {
    public static void createXML(String packageName)throws Exception{
        File file = new File("sdcard/gamelayout/"+packageName+".xml");
        Document document = DocumentHelper.createDocument();
        //创建根节点
        Element rootGame = document.addElement("game");
        //创建叶节点
        Element leafButton1 = rootGame.addElement("button");
        leafButton1.addAttribute("name","BUTTON_A");
        leafButton1.addAttribute("action","click");
        leafButton1.addAttribute("type","single");

        Element leafButton2 = rootGame.addElement("button");
        leafButton2.addAttribute("name","BUTTON_B");
        leafButton2.addAttribute("action","click");
        leafButton2.addAttribute("type","single");

        //自动格式化xml 文件
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileOutputStream(file),format);
        //特殊字符，是否转义，默认为true
        writer.setEscapeText(false);
        writer.write(document);
        writer.close();
    }
}
