package soulib.compiler;

import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class ClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	public ClassFileManager(JavaCompiler compiler, DiagnosticListener<? super JavaFileObject> listener) {
		super(compiler.getStandardFileManager(listener, null, null));
	}

	/** キー：クラス名、値：クラスファイルのオブジェクト */
	protected final Map<String, JavaClassObject> map = new HashMap<String, JavaClassObject>();
	private ClassLoader baseClassLoader;
	// クラスファイルを生成するときに呼ばれる
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
		JavaClassObject co = new JavaClassObject(className, kind);
		map.put(className, co); // クラス名をキーにしてファイルオブジェクトを保持しておく
		return co;
	}
	protected ClassLoader loader = null;

	@Override
	public ClassLoader getClassLoader(Location location) {
		if (loader == null) {
			if(baseClassLoader==null)loader = new Loader();
			else loader=new Loader(baseClassLoader);
		}
		return loader;
	}
	/** コンパイルしたクラスを返す為のクラスローダー */
	private class Loader extends SecureClassLoader {
		public Loader() {
			
		}
		public Loader(ClassLoader baseClassLoader) {
			super(baseClassLoader);
		}
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			JavaClassObject co = map.get(name);
			if (co == null) {
				return super.findClass(name);
			}
			Class<?> c = co.getDefinedClass();
			if (c == null) {
				byte[] b = co.getBytes();
				c = super.defineClass(name, b, 0, b.length);
				co.setDefinedClass(c);
			}
			return c;
		}
	}

	public void setBaseClassLoader(ClassLoader bcl){
		baseClassLoader=bcl;
	}
}