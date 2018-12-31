package com.example.upper.joyevent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class GameLayout{
	private String xmlFilePath;
    private String gameName_Ch;
    private String gameName_En;
    private String gamePackage;
    private String gameVersion;
    private String gameDescription;
    private HashMap<String,Btn> btnsMap;
    private HashMap<String,Joystick> joysMap;
	private HashMap<String,ComboBtn> combosMap;

	public static final String ACTION_CLICK = "click";
	public static final String ACTION_SWIPE = "swipe";
	public static final String TYPE_SINGLE = "single";
	public static final String TYPE_LOOP = "loop";
	public static final String TYPE_MULTI = "multi";
	public static final String TYPE_SWIPE_UP = "UP";
	public static final String TYPE_SWIPE_DOWN = "DOWN";
	public static final String TYPE_SWIPE_LEFT = "LEFT";
	public static final String TYPE_SWIPE_RIGHT = "RIGHT";
	public static final String TYPE_SWIPE_LEFT_UP = "LEFT_UP";
	public static final String TYPE_SWIPE_LEFT_DOWN = "LEFT_DOWN";
	public static final String TYPE_SWIPE_RIGHT_UP = "RIGHT_UP";
	public static final String TYPE_SWIPE_RIGHT_DOWN = "RIGHT_DOWN";
	public static final String TYPE_SWIPE_AUTO = "AUTO";//根据startPoint和endPoint自动得出划屏方向
	public static final String TYPE_JOYSTICK_STANDARD = "standard";
	public static final String TYPE_JOYSTICK_DIRECTION = "direction";
	public static final String DEFAULT_GAMELAYOUT_PATH = "/sdcard/gamelayout/";
	public static final String XML_SUFFIX = ".xml";
	public static final int JOYSTICK_L = 0x010;
	public static final int JOYSTICK_R = 0x020;

	GameLayout(){
		xmlFilePath = null;
		btnsMap = new HashMap<String,Btn>();
		joysMap = new HashMap<String,Joystick>();
		combosMap = new HashMap<String,ComboBtn>();
	}

	GameLayout(String filepath){
		xmlFilePath = filepath;
		init();
	}

	GameLayout(String packageName,boolean bRelativePath){
		if(bRelativePath) {
			xmlFilePath = DEFAULT_GAMELAYOUT_PATH + packageName + XML_SUFFIX;
		}else{
			xmlFilePath = packageName;
		}
		init();
	}

	public void init(){
		gameName_Ch = null;
		gameName_En = null;
		gamePackage = null;
		gameVersion = null;
		gameDescription = null;
		btnsMap = new HashMap<String,Btn>();
		joysMap = new HashMap<String,Joystick>();
		combosMap = new HashMap<String,ComboBtn>();
	}

	public boolean parse(){
		if(xmlFilePath == null) return false;

		System.out.println("start parsing..."+xmlFilePath);
		SAXReader reader = new SAXReader();
		int gamebtnNum = 0;
		int gameJoystickNum = 0;
		int gameComboNum = 0;
		try {
            // 通过reader对象的read方法加载books.xml文件,获取docuemnt对象。
            Document document = reader.read(new File(xmlFilePath));
            // 通过document对象获取根节点bookstore
            Element game = document.getRootElement();
            List<Attribute> gameAttrs = game.attributes();
            for (Attribute attr : gameAttrs) {
                System.out.println("游戏属性名：" + attr.getName() + "--属性值："
                        + attr.getValue());
                String tmpName = attr.getName();
                String tmpVal = attr.getValue();
                if(tmpName.equals("name_Ch")) this.setGameNameCh(tmpVal);
                else if(tmpName.equals("name_En")) this.setGameName_En(tmpVal);
                else if(tmpName.equals("package")) this.setGamePackage(tmpVal);
                else if(tmpName.equals("version")) this.setGameVersion(tmpVal);
                else if(tmpName.equals("description")) this.setGameDescription(tmpVal);
         
            }
            
            // 通过element对象的elementIterator方法获取迭代器
            Iterator it = game.elementIterator(); 
            // 遍历迭代器，获取根节点中的信息（button）
            while (it.hasNext()) {                        
                Element gameElement = (Element) it.next();
                //目前<game></game>中仅支持<button>和<joystick>节点
                if(gameElement.getName().equals("button")) {//解析button节点
                	Btn btn = new Btn();
                	gamebtnNum++;
                	System.out.println("=====开始遍历游戏布局按键====="+gamebtnNum);
                	
                	// 获取button的属性名以及 属性值
                    List<Attribute> gameBtnAttrs = gameElement.attributes();
                    for (Attribute attr : gameBtnAttrs) {
                        System.out.println("按键属性名：" + attr.getName() + "--属性值："
                                + attr.getValue());
                        String tmpName = attr.getName();
                        String tmpVal = attr.getValue();
                        if(tmpName.equals("name")) btn.setName(tmpVal);
                        else if(tmpName.equals("action")) btn.setAction(tmpVal);
                        else if(tmpName.equals("type")) btn.setType(tmpVal);
                        else if(tmpName.equals("points")) btn.setPointsNum(Integer.parseInt(tmpVal));
                        else if(tmpName.equals("description")) btn.setDescription(tmpVal);
                    }
                    
                    // 获取button下point属性名及属性值
                    Iterator itt = gameElement.elementIterator();
                    while (itt.hasNext()) {
                        Element buttonPoint = (Element) itt.next();   
                        List<Attribute> btnPointAttrs = buttonPoint.attributes();
                        Point point = new Point();
                        for (Attribute attr : btnPointAttrs) {
                            System.out.println("point属性名：" + attr.getName() + "--属性值："
                                    + attr.getValue());
                            String tmpName = attr.getName();
                            String tmpVal = attr.getValue();
                            if(tmpName.equals("id")) point.setId(tmpVal);
                            else if(tmpName.equals("x")) point.setX(tmpVal);
                            else if(tmpName.equals("y")) point.setY(tmpVal);
                        }
                        
                        btn.addPoint(point);
                        //System.out.println("节点名：" + buttonPoint.getName() + "--节点值：" + bookChild.getStringValue());
                    }
                    System.out.println("=====结束遍历游戏布局按键=====\n");
                    
                    this.addBtn(btn.getName(), btn);
                }else if(gameElement.getName().equals("joystick")) {
                	//解析joystick节点
                	Joystick joystick = new Joystick();
                	gameJoystickNum++;
                	System.out.println("=====开始遍历游戏布局摇杆====="+gameJoystickNum);
                	
                	// 获取button的属性名以及 属性值
                    List<Attribute> gameBtnAttrs = gameElement.attributes();
                    for (Attribute attr : gameBtnAttrs) {
                        System.out.println("摇杆属性名：" + attr.getName() + "--属性值："
                                + attr.getValue());
                        String tmpName = attr.getName();
                        String tmpVal = attr.getValue();
                        if(tmpName.equals("name")) joystick.setName(tmpVal);
                        else if(tmpName.equals("radius")) joystick.setRadius(Integer.parseInt(tmpVal));
                        else if(tmpName.equals("type")) joystick.setType(tmpVal);
                        else if(tmpName.equals("response")) joystick.setResponse(Integer.parseInt(tmpVal));
                        else if(tmpName.equals("description")) joystick.setDescription(tmpVal);
                    }
                    
                    // 获取joystick下point属性名及属性值
                    Iterator itt = gameElement.elementIterator();
                    while (itt.hasNext()) {
                        Element joystickPoint = (Element) itt.next();   
                        List<Attribute> joystickPointAttrs = joystickPoint.attributes();
                        Point point = new Point();
                        for (Attribute attr : joystickPointAttrs) {
                            System.out.println("point属性名：" + attr.getName() + "--属性值："
                                    + attr.getValue());
                            String tmpName = attr.getName();
                            String tmpVal = attr.getValue();
                            if(tmpName.equals("x")) point.setX(tmpVal);
                            else if(tmpName.equals("y")) point.setY(tmpVal);
                        }   
                        
                        joystick.setOriginal(point);
                    }                                 
                    
                    System.out.println("=====结束遍历游戏布局摇杆=====\n");
                    
                    this.addJoystick(joystick.getName(), joystick);              
                
                }else if(gameElement.getName().equals("combo")) {
					//解析combo节点
					ComboBtn combo = new ComboBtn();
					gameComboNum++;
					System.out.println("=====开始遍历游戏布局组合按键====="+gameComboNum);

					// 获取button的属性名以及 属性值
					List<Attribute> gameComboAttrs = gameElement.attributes();
					for (Attribute attr : gameComboAttrs) {
						System.out.println("组合按键属性名：" + attr.getName() + "--属性值："
								+ attr.getValue());
						String tmpName = attr.getName();
						String tmpVal = attr.getValue();
						if(tmpName.equals("name")) combo.setName(tmpVal);
						else if(tmpName.equals("action")) combo.setAction(tmpVal);
						else if(tmpName.equals("type")) combo.setType(tmpVal);
						else if(tmpName.equals("points")) combo.setPointsNum(Integer.parseInt(tmpVal));
						else if(tmpName.equals("description")) combo.setDescription(tmpVal);
					}

					// 获取button下point属性名及属性值
					Iterator itt = gameElement.elementIterator();
					while (itt.hasNext()) {
						Element comboPoint = (Element) itt.next();
						List<Attribute> comboPointAttrs = comboPoint.attributes();
						Point point = new Point();
						for (Attribute attr : comboPointAttrs) {
							System.out.println("point属性名：" + attr.getName() + "--属性值："
									+ attr.getValue());
							String tmpName = attr.getName();
							String tmpVal = attr.getValue();
							if(tmpName.equals("id")) point.setId(tmpVal);
							else if(tmpName.equals("x")) point.setX(tmpVal);
							else if(tmpName.equals("y")) point.setY(tmpVal);
						}

						combo.addPoint(point);
						//System.out.println("节点名：" + buttonPoint.getName() + "--节点值：" + bookChild.getStringValue());
					}
					System.out.println("=====结束遍历游戏布局组合按键=====\n");

					this.addCombo(combo.getName(), combo);
				}
            }

        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
        	System.out.println("button节点数："+gamebtnNum);
        }	
		
		return true;
	}
	
	public void setGameNameCh(String nameCh) {
		this.gameName_Ch = nameCh;
	}

	public String getGameNameCh(){
		return gameName_Ch;
	}
	
	public void setGameName_En(String nameEn) {
		this.gameName_En = nameEn;
	}

	public String getGameName_En(){
		return gameName_En;
	}

	public void setGamePackage(String packageName) {
		this.gamePackage = packageName;
	}
	
	public String getGamePackage(){
		return gamePackage;
	}
	
	public void setGameVersion(String version) {
		this.gameVersion = version;
	}
	
	public String getGameVersion(){
		return gameVersion;
	}
	
	public void setGameDescription(String description) {
		this.gameDescription = description;
	}
	
	public String getGameDescription(){
		return gameDescription;
	}
	
	public void addBtn(String btnName,Btn btn) {
		btnsMap.put(btnName, btn);
	}
	
	public Btn getBtn(String btnName) {
		return btnsMap.get(btnName);
	}
	
	public void addJoystick(String joystickName,Joystick joystick) {
		joysMap.put(joystickName, joystick);
	}
	
	public Joystick getJoystick(String joystickName) {
		return joysMap.get(joystickName);
	}

	public void addCombo(String btnName,ComboBtn combo) {
		combosMap.put(btnName, combo);
	}

	public ComboBtn getCombo(String comboName) {
		return combosMap.get(comboName);
	}
	
	public HashMap<String,Btn> getBtnsMap(){
		return btnsMap;
	}
	
	public HashMap<String,Joystick> getJoysMap(){
		return joysMap;
	}

	public HashMap<String,ComboBtn> getCombosMap(){
		return combosMap;
	}

	public boolean create(String filename) throws IOException {
		File file;
		if(filename == null){
			file = new File(xmlFilePath);
		}else{
			file = new File(DEFAULT_GAMELAYOUT_PATH+filename+XML_SUFFIX);
		}

		Document document = DocumentHelper.createDocument();
		//1.创建根节点
		Element rootGame = document.addElement("game");
		//设置根节点属性
		if(gameName_Ch != null) rootGame.addAttribute("name_Ch",gameName_Ch);
		if(gameName_En != null) rootGame.addAttribute("name_En",gameName_En);
		if(gamePackage != null) rootGame.addAttribute("package",gamePackage);
		if(gameVersion != null) rootGame.addAttribute("version",gameVersion);
		if(gameDescription != null) rootGame.addAttribute("description",gameDescription);

		//2.创建叶节点
		if(btnsMap.size() != 0) {
			for(Map.Entry<String,Btn> entry:btnsMap.entrySet()) {
				//String btnName = entry.getKey();
				Btn btn = entry.getValue();
				Element leafButton = rootGame.addElement("button");
				//button节点属性
				leafButton.addAttribute("name", btn.getName());
				leafButton.addAttribute("action", btn.getAction());
				leafButton.addAttribute("type", btn.getType());
				leafButton.addAttribute("description",btn.getDescription());
				if(btn.getPointsNum() > 1) leafButton.addAttribute("points",""+btn.getPointsNum());

				//button下有Point叶节点
				if(btn.getPointList().size() != 0) {
					for(Point point:btn.getPointList()) {
						Element subleafPoint = leafButton.addElement("Point");
						subleafPoint.addAttribute("x",""+point.getX());
						subleafPoint.addAttribute("y",""+point.getY());
					}
				}
			}
		}

		//3.创建叶节点joystick
		if(joysMap.size() != 0){
			for(Map.Entry<String,Joystick> entry:joysMap.entrySet()) {
				//String joystickName = entry.getKey();
				Joystick joystick = entry.getValue();
				Element leafJoystick = rootGame.addElement("joystick");
				//joystick节点属性
				leafJoystick.addAttribute("name",joystick.getName());
				leafJoystick.addAttribute("radius",""+joystick.getRadius());
				leafJoystick.addAttribute("type",""+joystick.getType());
				leafJoystick.addAttribute("description",joystick.getDescription());

				if(joystick.getOriginal() != null){
					Element subleafPoint = leafJoystick.addElement("Point");
					Point point = joystick.getOriginal();
					subleafPoint.addAttribute("x",""+point.getX());
					subleafPoint.addAttribute("y",""+point.getY());
				}
			}
		}

		//4.创建叶节点combo
		if(combosMap.size() != 0){
			for(Map.Entry<String,ComboBtn> entry:combosMap.entrySet()) {
				//String comboName = entry.getKey();
				ComboBtn combo = entry.getValue();
				Element leafCombo = rootGame.addElement("combo");
				//button节点属性
				leafCombo.addAttribute("name", combo.getName());
				leafCombo.addAttribute("action", combo.getAction());
				leafCombo.addAttribute("type", combo.getType());
				leafCombo.addAttribute("description",combo.getDescription());
				if(combo.getPointsNum() > 1) leafCombo.addAttribute("points",""+combo.getPointsNum());

				//combo下有Point叶节点
				if(combo.getPointList().size() != 0) {
					for(Point point:combo.getPointList()) {
						Element subleafPoint = leafCombo.addElement("Point");
						subleafPoint.addAttribute("x",""+point.getX());
						subleafPoint.addAttribute("y",""+point.getY());
					}
				}
			}
		}

		//自动格式化xml 文件
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileOutputStream(file),format);
		//特殊字符，是否转义，默认为true
		writer.setEscapeText(false);
		writer.write(document);
		writer.close();

		return true;
	}

	public void dump(){
		System.out.println("appname_Ch:"+this.gameName_Ch);
		System.out.println("appname_En:"+this.gameName_En);
		System.out.println("packagename:"+this.gamePackage);

		for(String btnName:btnsMap.keySet()){
			Btn btn = getBtn(btnName);
			btn.dump();
		}

		for(String joystickName:joysMap.keySet()){
			Joystick joystick = getJoystick(joystickName);
			joystick.dump();
		}
	}

	//包名更新时，需要更新文件名
	public boolean change(String rawPackageName,String newPackageName){
		File rawfile = new File(DEFAULT_GAMELAYOUT_PATH+rawPackageName+XML_SUFFIX);
		File newfile = new File(DEFAULT_GAMELAYOUT_PATH+newPackageName+XML_SUFFIX);
		return rawfile.renameTo(newfile);
	}

	public boolean remove(String packageName){
		File rawfile = new File(DEFAULT_GAMELAYOUT_PATH+packageName+XML_SUFFIX);
		return rawfile.delete();
	}

	public static HashMap<String,String> getGameLayoutsMap(){
		File folder = new File(GameLayout.DEFAULT_GAMELAYOUT_PATH);
		if(!folder.exists()){
			folder.mkdir();
			folder.canRead();
			folder.canWrite();
			return null;
		}
		folder.canRead();
		folder.canWrite();
		File[] files = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
			if(filename.endsWith(GameLayout.XML_SUFFIX)){
				return true;
			}else {
				return false;
			}
			}
		});

		HashMap<String,String> map = new HashMap<String,String>();
		for(File f:files){
			String filename = f.getName();
			System.out.println(filename);
			String packageName = GameLayoutUtils.xmlFileName2PackageName(filename);
			System.out.println(packageName);
			GameLayout gameLayout = new GameLayout(packageName,true);
			gameLayout.parse();
			String gameName_Ch = gameLayout.getGameNameCh();
			System.out.println(gameName_Ch);
			map.put(gameName_Ch,packageName);
		}

		return map;
	}
}




