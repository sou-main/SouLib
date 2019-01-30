package soulib.fileLib;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileData{
	public String FilePath;
	public long lastModified;
	private static SimpleDateFormat sdf;
	public FileData(String FilePath){
		this.FilePath=FilePath;
		try{
			BasicFileAttributes fp=Files.readAttributes(new File(FilePath).toPath(),BasicFileAttributes.class);
			long l=fp.lastModifiedTime().toMillis();
			if(l>0) lastModified=l;
		}catch(Exception e){
		}
	}
	public String getName(){
		return FilePath;
	}
	public static ArrayList<FileData> toFileData(ArrayList<String> s){
		ArrayList<FileData> list=new ArrayList<FileData>();
		for(String t:s){
			list.add(new FileData(t));
		}
		return list;
	}
	public static ArrayList<String> toStringArray(ArrayList<FileData> s){
		ArrayList<String> list=new ArrayList<String>();
		for(FileData t:s){
			list.add(t.FilePath);
		}
		return list;
	}
	@Override
	public String toString(){
		if(sdf==null) sdf=new SimpleDateFormat("　最終更新日yyyy年MM月dd日HH時mm分ss秒");
		return "FilePath="+FilePath+sdf.format(new Date(lastModified));
	}
	@Override
	public boolean equals(Object o){
		// System.out.println("EqualsTest"+o.toString());
		if(o instanceof FileData){
			FileData fd=(FileData)o;
			if(fd.FilePath.equals(this.FilePath)&&fd.lastModified==this.lastModified){
				//System.out.println("Equals"+o.toString());
				return true;
			}//else if(fd.FilePath.equals(this.FilePath))
			//System.err.println("NotEquals"+fd.lastModified+" & This="+lastModified);
		}// else System.err.println("Error"+o.toString());
		return false;
	}
}
