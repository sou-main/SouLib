package soulib.compiler;

import java.io.PrintStream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class JavaCompileErrorListener implements DiagnosticListener<JavaFileObject> {

	PrintStream out;
	public JavaCompileErrorListener() {
		out=System.out;
	}
	public JavaCompileErrorListener(PrintStream ps) {
		out=ps;
	}
	// コンパイルエラーが起きたときに呼ばれる
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		//System.out.println("▼report start");
		out.println("errcode：" + diagnostic.getCode());
		out.println("line   ：" + diagnostic.getLineNumber());
		out.println("column ：" + diagnostic.getColumnNumber());
		out.println("message：" + diagnostic.getMessage(null));
		out.println(diagnostic.toString());
		//System.out.println("▲report end");
	}
}