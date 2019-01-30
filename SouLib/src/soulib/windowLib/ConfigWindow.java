package soulib.windowLib;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import soulib.fileLib.ConfigFile;

public class ConfigWindow extends StandardWindow{
	private static WindowMode auto(ConfigFile fil,WindowMode mode) {
		int x=0;
		String[] cfdX = fil.getConfigFileData();
		int y=cfdX.length*50+30;
		for(int i=0;i<cfdX.length;i++){
			if(cfdX[i].length()>x)x=cfdX[i].length();
		}
		x*=50;
		if(mode==null)mode=new WindowMode();
		mode.WindowX=x;
		mode.WindowY=y;
		mode.NoCanvas=true;
		mode.AutoUpdate=false;
		return mode;
	}
	class LocalChoice extends Choice{
		public int id;
		public LocalChoice(int i){
			id=i;
			addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e){
					TypeList[id]=t().getSelectedItem();
					load(id);
				}
			});
		}
		protected LocalChoice t(){
			return this;
		}
	}
	public ConfigFile file;
	public Button bt;
	private String[] cfd;
	private String[] CateList;
	private String[] TypeList;
	private LocalChoice[] ChoiceList;
	private Component[] TF;
	private Component[][] mpcomponents;
	public ConfigWindow(WindowMode mode,ConfigFile file){
		super(auto(file,mode));
		this.file=file;
		cfd = file.getConfigFileData();
		CateList = new String[cfd.length];
		TypeList = new String[cfd.length];
		ChoiceList=new LocalChoice[cfd.length];
		TF = new Component[cfd.length];
		mpcomponents=new Component[cfd.length+1][3];
		set();
		pack();
		setCenter();
	}
	@Override
	protected Component[][] mpcomponents(){
		for(int i=0;i<cfd.length;i++){
			String line=cfd[i];
			int start=line.indexOf(file.Kugiri);
			String cut=line.substring(start+1,line.length());
			int end=cut.indexOf(file.Kugiri);
			String out=line.substring(start+1,end+2);
			CateList[i]=out;
			TypeList[i]=file.getDataType(out);
			ChoiceList[i]=new LocalChoice(i);
			ChoiceList[i].add("S");
			ChoiceList[i].add("I");
			ChoiceList[i].add("L");
			ChoiceList[i].add("F");
			ChoiceList[i].add("D");
			ChoiceList[i].add("B");
			mpcomponents[i][2]=ChoiceList[i];
			load(i);
		}
		bt = new Button("設定");
		bt.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				for(int i=0;i<file.getConfigFileData().length;i++){
					file.setConfigData(CateList[i],TF[i].toString(),ChoiceList[i].getSelectedItem());
				}
				file.saveConfig(null);
				reload();
			}
		});
		mpcomponents[cfd.length][0]=bt;
		mpcomponents[cfd.length][1]=bt;
		mpcomponents[cfd.length][2]=bt;
		return mpcomponents;
	}
	protected void load(int i){
		String out=CateList[i];
		byte typeID=-1;
		if(TypeList[i].equals("I"))typeID=0;
		if(TypeList[i].equals("L"))typeID=1;
		if(TypeList[i].equals("F"))typeID=2;
		if(TypeList[i].equals("D"))typeID=3;
		if(TypeList[i].equals("B"))typeID=4;
		TF[i]=null;
		if(typeID<4){
			class tf extends TextField{
				private String VV="0123456789+-\b";
				private int lenght;
				private short lim;
				public tf(byte type) {
					switch(type) {
						case -1:
							lim=50;
							VV="";
							break;
						case 0:
							lim=10;
							break;
						case 1:
							lim=20;
							break;
						case 2:
							lim=9;
							VV+=".";
							break;
						case 3:
							lim=17;
							VV+=".";
							break;
						default:
							lim=20;
							break;
					}
					enableEvents(AWTEvent.KEY_EVENT_MASK);
				}
				protected void processKeyEvent(KeyEvent e){
					char chr = e.getKeyChar();
					int code=e.getKeyCode();
					if(lenght>lim&&code==0&&chr!='\b') {
						e.consume();
					}if(code==0&&VV.indexOf(chr)==-1){
						e.consume();
					}if(e.getID()==KeyEvent.KEY_RELEASED) {
						lenght=getText().length();
					}
					super.processKeyEvent(e);
				}
				@Override
				public String toString() {
					return getText();
				}
			}
			ChoiceList[i].select(TypeList[i]);
			TF[i]=new tf(typeID);
			((TextField)TF[i]).setText(file.getConfigDataString(out));
		}else{
			ChoiceList[i].select("S");
			TF[i]=new Checkbox() {
				@Override
				public String toString() {
					return getState()?"true":"false";
				}
			};
			((Checkbox)TF[i]).setState(file.getConfigDataBoolean(out));
		}
		Label l=new Label(out);
		mpcomponents[i][0]=l;
		mpcomponents[i][1]=TF[i];
	}
		public void reload() {
			removeAll();
			set();
			pack();
		}
	}