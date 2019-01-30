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
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class SimpleJavaCompiler{

	protected JavaCompileErrorListener listener = new JavaCompileErrorListener();

	protected JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private JavaFileManager manager;
	private final String source_code;
	private Class<?> c;

	public SimpleJavaCompiler(String source) {
		source_code=source;
	}
	public SimpleJavaCompiler(File source_file) throws FileNotFoundException, IOException{
		this(new FileReader(source_file));
	}
	public SimpleJavaCompiler(InputStream source) throws FileNotFoundException, IOException{
		this(new InputStreamReader(source));
	}
	public SimpleJavaCompiler(Reader source) throws IOException{
		StringWriter sw=new StringWriter();
		PrintWriter pw=new PrintWriter(sw);
		BufferedReader br=new BufferedReader(source);
		try{
			String line;
			while(br.ready()) {
				line=br.readLine();
				if(line==null)break;
				pw.println(line);
			}
			pw.flush();
		}finally {
			source.close();
		}
		source_code=sw.toString();
	}
	public void setPrintStream(PrintStream ps) {
		listener.out=ps;
	}
	/** コンパイル実行 
	 * @throws ClassNotFoundException */
	@SuppressWarnings("unchecked")
	public <T> Class<T> compile() throws IllegalArgumentException, ClassNotFoundException{
		if(c!=null)return (Class<T>) c;
		String key="class ";
		int begin=source_code.indexOf(key)+key.length();
		String buf=source_code.substring(begin);
		int end=Math.min(buf.indexOf("{"),buf.indexOf(" "));
		String CLASS_NAME=buf.substring(0,end);
		key="package ";
		begin=source_code.indexOf(key);
		end=source_code.indexOf(";");
		// パッケージ名 + クラス名
		String class_name;
		if(begin>-1)class_name=source_code.substring(begin+key.length(),end)+"."+CLASS_NAME;
		else class_name=CLASS_NAME;
		JavaFileObject fo = new JavaSourceFromString(class_name, source_code);

		List<JavaFileObject> compilationUnits = Arrays.asList(fo);
		List<String> options = Arrays.asList(
					"-classpath", System.getProperty("java.class.path")
				);
		manager = new ClassFileManager(compiler, listener);
		CompilationTask task = compiler.getTask(null,manager,	//出力ファイルを扱うマネージャー
						listener,//エラー時の処理を行うリスナー（nullでもよい）
						options,//コンパイルオプション
						null,compilationUnits	//コンパイル対象ファイル群
					);
		//コンパイル実行
		boolean successCompile = task.call();
		if (!successCompile) {
			throw new IllegalArgumentException("コンパイル失敗：" + class_name);
		}
		ClassLoader cl=manager.getClassLoader(null);
		//@SuppressWarnings("unchecked")
		this.c = (Class<T>)cl.loadClass(class_name);
		return (Class<T>) c;
	}
}
