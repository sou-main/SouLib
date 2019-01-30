package soulib.lib;

import java.util.HashMap;

import soulib.fileLib.ConfigFile;
import soulib.net.Event;

public class Lang{
	public static Lang L=new Lang();
	public Lang() {};
	public Event e=null;
	public Lang yobi=null;
	public Lang(String[] lang) {
		set(lang);
	};
	private HashMap<String, String> map=null;
	public String get(String key) {
		return get(key,key);
	}
	public String get(String key,String miss) {
		if(map==null)set(new String[0]);
		String r=map.get(key);
		if(r==null&&yobi!=null)return yobi.get(key,miss);
		return r==null?miss:r;
	}
	public void set(String[] s){
		ConfigFile cf=new ConfigFile(s);
		cf.Kugiri="=";
		map=cf.getMapString();
		if(e!=null) {
			String p=e.run();
			if(p!=null&&!p.isEmpty())System.out.println(p);
		}
	}
}
