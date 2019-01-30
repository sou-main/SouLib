package soulib.fileLib.old;

import java.util.List;

import soulib.fileLib.ConfigFile;
import soulib.fileLib.FileEditor;
import soulib.lib.Command;
import soulib.logLib.GameLog;

public class CommandFile{
	/**
	 *
	 * @param s 対象のデータ
	 * @param list コマンドリスト
	 */
	public CommandFile(String s,List<Command> list){
		String[] t={s};
		command(t,list,0);
	}
	public CommandFile(String[] s,List<Command> list,int Line){
		command(s,list,Line);
	}
	public CommandFile(){};
	/**
	 * 対象のデータの対象の行にコマンドリストの
	 * どれかのコマンド名が含まれていた場合
	 * そのコマンドのCode(String s)を実行します
	 * @param s 対象のデータ
	 * @param list コマンドリスト
	 * @param Line 対象の行
	 */
	protected void command(String[] s,List<Command> list,int Line){
		ConfigFile cf=new ConfigFile(s);
		cf.Kugiri=" ";
		String st="No Command";
		for(int i=0;i<list.size();i++){
			if(cf.getConfigLine(list.get(i).getName())==Line){
				Command cm=list.get(i);
				String DATA=cf.getConfigDataString(cm.getName());
				try{
					st=cm.Code(DATA,false);
				}catch(Throwable t){
					t.printStackTrace();
					String str=t.getMessage();
					String[] u=new FileEditor().StringToStrings(str);
					for(int j=0;j<u.length;j++){
						st=u[j];
					}
				}
				break;
			}
		}
		if(st=="No Command"){
			GameLog.Log("コマンドが存在しません");
		}else if(st=="Success"){
			GameLog.Log("実行に成功しました");
		}else if(st=="DebugCommand"){
			GameLog.Log("実行権限がありません");
		}else{
			GameLog.Log("エラーが発生しました");
			GameLog.Log("内容 "+st);
		}
	}
}