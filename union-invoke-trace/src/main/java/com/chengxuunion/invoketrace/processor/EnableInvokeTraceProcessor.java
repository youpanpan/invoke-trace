package com.chengxuunion.invoketrace.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.*;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * 自定义注解处理器
 *
 * @author youpanpan
 * @date: 2019-03-13 10:38
 * @since v1.0
 */
@SupportedAnnotationTypes({"com.chengxuunion.invoketrace.processor.EnableInvokeTrace"})
public class EnableInvokeTraceProcessor extends AbstractProcessor {

    /**
     * 用于打印日志
     * */
    private Messager messager;

    /**
     * 用于处理元素
     * */
    private Elements elementUtils;

    /**
     * 用来创建java文件或者class文件
     * */
    private Filer filer;

    /**
     * 切面类名前缀
     */
    private static final String ASPECT_JAVA_PREFIX = "InvokerTraceAspect";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE, "EnableInvokeTraceProcessor init.");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes(){
        Set<String> set = new HashSet<>();
        set.add(EnableInvokeTrace.class.getCanonicalName());
        return Collections.unmodifiableSet(set);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(EnableInvokeTrace.class);
        List<String[]> invokeTraceList = new ArrayList<>();

        // 获取配置该注解的值列表
        for (Element element : elements) {

            // 只在类上定义
            if (element.getKind() == ElementKind.CLASS && element instanceof TypeElement) {
                TypeElement t = (TypeElement)element;
                String qName = t.getQualifiedName().toString();
                messager.printMessage(Diagnostic.Kind.NOTE, qName);
                EnableInvokeTrace invokeTrace = t.getAnnotation(EnableInvokeTrace.class);
                String value = invokeTrace.value();
                messager.printMessage(Diagnostic.Kind.NOTE, invokeTrace.value());

                String[] arr = new String[2];
                arr[0] = value;
                arr[1] = getPackage(qName);
                invokeTraceList.add(arr);
            }
        }

        if (invokeTraceList.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.WARNING, "没有使用EnableInvokeTrace");
            return true;
        }

        // 对每个cron表达式，生成一个AOP类
        int i = 1;
        try {
            String className = "";
            for (String[] arr : invokeTraceList) {
                className = ASPECT_JAVA_PREFIX + i;
                JavaFileObject sourceFileObj = filer.createSourceFile(arr[1] + ".config." + className);
                createSourceFile(arr[0], className, arr[1] + ".config", sourceFileObj.openWriter());
                //compile(sourceFileObj.toUri().getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 创建切面类源文件
     *
     * @param pointCutExpress
     * @param className
     * @param packageName
     * @param writer
     * @throws IOException
     */
    private void createSourceFile(String pointCutExpress, String className, String packageName, Writer writer) throws IOException{
        StringBuilder javaBuilder = new StringBuilder();
        javaBuilder.append("package "+ packageName +"; \n");
        javaBuilder.append(" \n");
        javaBuilder.append("import com.chengxuunion.invoketrace.processor.EnableInvokePoint; \n");
        javaBuilder.append("import com.chengxuunion.util.id.IdGenerator; \n");
        javaBuilder.append("import org.aspectj.lang.ProceedingJoinPoint; \n");
        javaBuilder.append("import org.aspectj.lang.annotation.Around; \n");
        javaBuilder.append("import org.aspectj.lang.annotation.Aspect; \n");
        javaBuilder.append("import org.aspectj.lang.annotation.Pointcut; \n");
        javaBuilder.append("import org.springframework.context.annotation.ComponentScan; \n");
        javaBuilder.append("import org.springframework.stereotype.Component; \n");
        javaBuilder.append(" \n");
        javaBuilder.append("/** \n");
        javaBuilder.append(" * @author youpanpan \n");
        javaBuilder.append(" * @date: 2019-03-13 14:21 \n");
        javaBuilder.append(" * @since v1.0 \n");
        javaBuilder.append(" */ \n");
        javaBuilder.append("@Component \n");
        javaBuilder.append("@ComponentScan({\"com.chengxuunion.invoketrace\"}) \n");
        javaBuilder.append("@Aspect \n");
        javaBuilder.append("public class "+ className +" { \n");
        javaBuilder.append(" \n");
        javaBuilder.append("    //声明切点 \n");
        javaBuilder.append("    @Pointcut(\""+ pointCutExpress +"\") \n");
        javaBuilder.append("    public void pointcut(){} \n");
        javaBuilder.append(" \n");
        javaBuilder.append("    @Around(\"pointcut()\") \n");
        javaBuilder.append("    public Object around(ProceedingJoinPoint pjp) { \n");
        javaBuilder.append("        long identify = IdGenerator.getInstance().nextId(); \n");
        javaBuilder.append("        EnableInvokePoint.before(pjp, identify); \n");
        javaBuilder.append("        Object obj = null; \n");
        javaBuilder.append("        try { \n");
        javaBuilder.append("            obj = pjp.proceed(); \n");
        javaBuilder.append("        } catch (Throwable throwable) { \n");
        javaBuilder.append("            throwable.printStackTrace(); \n");
        javaBuilder.append("        } \n");
        javaBuilder.append("        EnableInvokePoint.after(pjp, identify, obj); \n");
        javaBuilder.append("        return obj; \n");
        javaBuilder.append("    } \n");
        javaBuilder.append("} \n");
        javaBuilder.append("");

        writer.write(javaBuilder.toString());
        writer.flush();
        writer.close();
    }

    /**
     * 编译原文件
     *
     * @param path
     * @throws IOException
     */
    private void compile(String path) throws IOException{
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //文件管理者
        StandardJavaFileManager fileMgr =compiler.getStandardFileManager(null, null, null);
        Iterable units = fileMgr.getJavaFileObjects(path);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileMgr, null, null, null, units);
        task.call();
        fileMgr.close();
    }

    /**
     * 读取包名
     * @param name
     * @return
     */
    private String getPackage(String name) {
        String result = name;
        if (name.contains(".")) {
            result = name.substring(0, name.lastIndexOf("."));
        }else {
            result = "";
        }
        return result;
    }
}
