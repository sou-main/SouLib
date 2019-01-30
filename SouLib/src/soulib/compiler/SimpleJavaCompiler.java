package soulib.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class SimpleJavaCompiler{

	protected JavaCompileErrorListener listener = new JavaCompileErrorListener();

	protected JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private ClassFileManager manager;
	private String[] source_code;
	public ClassLoader baseClassLoader;
	private ArrayList<Class<?>> c;
	private byte[][] bc;
	private String[] bccn;

	public SimpleJavaCompiler(String... source) {
		source_code=source;
	}
	public SimpleJavaCompiler(File source_file) throws FileNotFoundException, IOException{
		this(new FileReader(source_file));
	}
	public SimpleJavaCompiler(InputStream source) throws FileNotFoundException, IOException{
		this(new InputStreamReader(source));
	}
	public SimpleJavaCompiler(Reader... source) throws IOException{
		source_code=new String[source.length];
		for(int i=0;i<source.length;i++) {
			StringWriter sw=new StringWriter();
			PrintWriter pw=new PrintWriter(sw);
			BufferedReader br=new BufferedReader(source[i]);
			try{
				String line;
				while(br.ready()) {
					line=br.readLine();
					if(line==null)break;
					pw.println(line);
				}
				pw.flush();
			}finally {
				source[i].close();
			}
			source_code[i]=sw.toString();
		}
	}
	public void setPrintStream(PrintStream ps) {
		listener.out=ps;
	}
	/** コンパイル実行
	 * @throws ClassNotFoundException */
	@SuppressWarnings("unchecked")
	public <T> Class<T> compile() throws IllegalArgumentException, ClassNotFoundException{
		ArrayList<Class<?>> al=compile0();
		return (Class<T>) al.get(0);
	}
	public ArrayList<Class<?>> compile0() throws IllegalArgumentException, ClassNotFoundException{
		if(c!=null)return c;
		if(compiler==null)return null;
		List<JavaFileObject> compilationUnits = new ArrayList<JavaFileObject>();
		String[] class_name0=new String[source_code.length];
		for(int i=0;i<source_code.length;i++){
			String sc=source_code[i];
			String key="class ";
			int begin=sc.indexOf(key)+key.length();
			String buf=sc.substring(begin);
			int end=Math.min(buf.indexOf("{"),buf.indexOf(" "));
			String CLASS_NAME=buf.substring(0,end);
			key="package ";
			begin=sc.indexOf(key);
			end=sc.indexOf(";");
			// パッケージ名 + クラス名
			String class_name;
			if(begin>-1)class_name=sc.substring(begin+key.length(),end)+"."+CLASS_NAME;
			else class_name=CLASS_NAME;
			JavaFileObject fo = new JavaSourceFromString(class_name, sc);
			compilationUnits.add(fo);
			class_name0[i]=class_name;
		}
		List<String> options = Arrays.asList(
					"-classpath", System.getProperty("java.class.path")
				);
		manager = new ClassFileManager(compiler, listener);
		CompilationTask task = compiler.getTask(null,manager,//出力ファイルを扱うマネージャー
						listener,//エラー時の処理を行うリスナー（nullでもよい）
						options,//コンパイルオプション
						null,compilationUnits	//コンパイル対象ファイル群
					);
		manager.setBaseClassLoader(baseClassLoader);
		//コンパイル実行
		boolean successCompile = task.call();
		if (!successCompile) {
			throw new IllegalArgumentException("コンパイル失敗");
		}
		ClassLoader cl=manager.getClassLoader(null);
		//@SuppressWarnings("unchecked")
		c=new ArrayList<Class<?>>();
		bc=new byte[class_name0.length][];
		bccn=new String[class_name0.length];
		for(int i=0;i<class_name0.length;i++) {
			this.c.add(cl.loadClass(class_name0[i]));
			JavaClassObject jco=manager.map.get(class_name0[i]);
			if(jco!=null) {
				bccn[i]=class_name0[i];
				bc[i]=jco.getBytes();
			}
		}
		return c;
	}
	public String[] byteCodeClassName() {
		return bccn;
	}
	public byte[][] getByteCode() {
		return bc;
	}
	public static Class<?> loadByteCode(final byte[] b) throws ClassNotFoundException{
		SecureClassLoader cl=new SecureClassLoader(){
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				return super.defineClass(null, b, 0, b.length);
			}
		};
		return cl.loadClass("LBC");
	}
}
