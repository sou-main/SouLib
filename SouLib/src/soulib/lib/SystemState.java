package soulib.lib;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import soulib.fileLib.FileEditor;

public class SystemState{
	public String JVM_Version=System.getProperty("java.vm.specification.version");
	public String OS_Name=System.getProperty("os.name");
	public String OS_Version=System.getProperty("os.version");
	public String tmpDir=System.getProperty("java.io.tmpdir");
	public int CPUCores=Runtime.getRuntime().availableProcessors();
	public long freeMemory=Runtime.getRuntime().freeMemory();
	public int freeMemoryMB=(int) (freeMemory/1048576);
	public long totalMemory=Runtime.getRuntime().totalMemory();
	public int totalMemoryMB=(int) (totalMemory/1048576);
	public long maxMemory=Runtime.getRuntime().maxMemory();
	public int maxMemoryMB=(int) (maxMemory/1048576);
	public long usedMemory=totalMemory-freeMemory;
	public int usedMemoryMB=(int) (usedMemory/1048576);
	public GraphicsEnvironment LocalGraphicsEnvironment=GraphicsEnvironment.getLocalGraphicsEnvironment();
	public GraphicsDevice DefaultGraphicsDevice=LocalGraphicsEnvironment.getDefaultScreenDevice();
	public GraphicsDevice[] AllGraphicsDevice=LocalGraphicsEnvironment.getScreenDevices();
	public String UserName=System.getProperty("user.name");
	public String UserHome=System.getProperty("user.home");
	public static SystemState SS=new SystemState();
	public static DisplayMode getDisplayMode(GraphicsDevice d){
		if(d==null) d=SS.DefaultGraphicsDevice;
		return d.getDisplayMode();
	}
	public static SystemState StaticUpdate(){
		return SS.Update();
	}
	public SystemState Update(){
		tmpDir=System.getProperty("java.io.tmpdir");
		CPUCores=Runtime.getRuntime().availableProcessors();
		freeMemory=Runtime.getRuntime().freeMemory();
		freeMemoryMB=(int) (freeMemory/1048576);
		totalMemory=Runtime.getRuntime().totalMemory();
		totalMemoryMB=(int) (totalMemory/1048576);
		maxMemory=Runtime.getRuntime().maxMemory();
		maxMemoryMB=(int) (maxMemory/1048576);
		usedMemory=totalMemory-freeMemory;
		usedMemoryMB=(int) (usedMemory/1048576);
		LocalGraphicsEnvironment=GraphicsEnvironment.getLocalGraphicsEnvironment();
		DefaultGraphicsDevice=LocalGraphicsEnvironment.getDefaultScreenDevice();
		AllGraphicsDevice=LocalGraphicsEnvironment.getScreenDevices();
		return this;
	}
	public static void SaveSystemData(){
		SaveSystemData(new File("SaveData"+FileEditor.Dir()+"SystemData.txt"));
	}
	public static String[] SaveSystemData(File save){
		return SaveSystemData(null,save);
	}
	public static String[] SaveSystemData(String[] Data,File save){
		try{
			String raw=SaveSystemData(new StringBuilder()).toString();
			Data=FileEditor.FE.StringToStrings(raw);
			if(save!=null) FileEditor.FE.SaveFile(raw,save);
			return Data;
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	public static Appendable SaveSystemData(Appendable out) throws IOException{
		SystemState ss=new SystemState();
		Date date=new Date();
		try{
			SimpleDateFormat Format=new SimpleDateFormat("yyyy/MM/dd-HH:mm");
			out.append(Format.format(date)).append(" : SystemData");
		}catch(Throwable t){
			out.append("SystemData");
			t.printStackTrace();
		}
		out.append(FileEditor.Line());
		out.append(FileEditor.Line());
		try{
			out.append("JVM-Version : ").append(System.getProperty("java.vm.specification.version"));
			out.append(FileEditor.Line());
		}catch(Throwable t){
			out.append("Miss");
		}
		try{
			out.append("OS-Name : ").append(System.getProperty("os.name")).append(FileEditor.Line());
		}catch(Throwable t){
			out.append("Miss");
		}
		try{
			out.append("OS-Version : ").append(System.getProperty("os.version")).append(FileEditor.Line());
		}catch(Throwable t){
			out.append("Miss");
		}
		try{
			out.append("tmpDir : ").append(System.getProperty("java.io.tmpdir")).append(FileEditor.Line());
		}catch(Throwable t){
			out.append("Miss");
		}
		try{
			out.append("UserName : ").append(System.getProperty("user.name")).append(FileEditor.Line());
		}catch(Throwable t){
			out.append("Miss");
		}
		try{
			out.append("CPUCores : ").append(Integer.toString(Runtime.getRuntime().availableProcessors()));
			out.append(FileEditor.Line());
		}catch(Throwable t){
			out.append("Miss");
		}
		try{
			out.append("MemoryTotal : ").append(Long.toString(Runtime.getRuntime().totalMemory()));
			out.append("Byte(").append(Long.toString(Runtime.getRuntime().totalMemory()/1048576L));
			out.append(")MB").append(FileEditor.Line());
		}catch(Throwable t){
			out.append("Miss");
		}
		try{
			out.append("MemoryMax : ").append(Long.toString(Runtime.getRuntime().maxMemory()));
			out.append("Byte(").append(Long.toString(Runtime.getRuntime().maxMemory()/1048576L));
			out.append(")MB").append(FileEditor.Line());
		}catch(Throwable t){
			out.append("Miss");
		}
		try{
			out.append("UsedMemory : ").append(Long.toString(ss.usedMemory));
			out.append("Byte(").append(Long.toString(ss.usedMemory/1048576));
			out.append(")MB").append(FileEditor.Line());
		}catch(Throwable t){
			out.append("Miss");
		}
		try{
			out.append("=======!---DisplayData---!=======").append(FileEditor.Line());
			GraphicsEnvironment ge=GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice device=ge.getDefaultScreenDevice();
			DisplayMode mode=device.getDisplayMode();
			DisplayMode[] modes=device.getDisplayModes();
			out.append("NowMode : ").append(Integer.toString(mode.getWidth())).append(",");
			out.append(Integer.toString(mode.getHeight())).append(",");
			out.append(Integer.toString(mode.getBitDepth())).append(",");
			out.append(Integer.toString(mode.getRefreshRate())).append(FileEditor.Line());
			out.append("DisplayModes : ").append(Integer.toString(modes.length)).append(FileEditor.Line());
			GraphicsDevice[] deviceList=ge.getScreenDevices();
			int DL=deviceList.length;
			out.append("DisplayDevice : ").append(Integer.toString(DL)).append(FileEditor.Line());
			out.append("DefaultDisplayDevice : ").append(device.getIDstring()).append(FileEditor.Line());
			for(int i=0;i<deviceList.length;i++){
				out.append("DeviceName").append(Integer.toString(i+1)).append(" : ");
				out.append(deviceList[i].getIDstring()).append(FileEditor.Line());
			}
			out.append("Width,Height,BitDepth,RefreshRate");
			for(int i=0;i<modes.length;i++){
				out.append(Integer.toString(modes[i].getWidth())).append(",");
				out.append(Integer.toString(modes[i].getHeight())).append(",");
				out.append(Integer.toString(modes[i].getBitDepth())).append(",");
				out.append(Integer.toString(modes[i].getRefreshRate())).append(FileEditor.Line());;
			}
		}catch(Throwable t){
			out.append("DisplayData-Miss");
		}
		return out;
	}
	public static void main(String[] args){
		SaveSystemData(new File("SystemData.txt"));
		System.exit(0);
	}
}
