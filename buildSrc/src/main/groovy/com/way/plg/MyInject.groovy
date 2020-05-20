package com.way.plg

import com.way.annotation.DebugLog
import com.way.annotation.InfoLog
import com.way.plg.utils.Utils
import com.way.plg.ware.LogLevel
import com.way.plg.ware.LogWare
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.MethodInfo
import javassist.bytecode.annotation.Annotation
import org.gradle.api.Project


class MyInject {

    public static void injectDir(String path, Project project) {

        println("----injectDir: ${path}")
        String packageName = project.Trace.packageName

        try {
            ClassPool pool = ClassPool.getDefault()
            pool.appendClassPath(path)

            //project.android.bootClasspath 加入android.jar，否则找不到android相关的所有类
            pool.appendClassPath(project.android.bootClasspath[0].toString());
            pool.importPackage("android.os.Bundle")

            File dir = new File(path)
            if (!dir.isDirectory()) {
                return;
            }
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath//确保当前文件是class文件，并且不是系统自动生成的class文件
                if (filePath.endsWith(".class") && !filePath.contains('R$') && !filePath.contains('$')//代理类
                        && !filePath.contains('R.class') && !filePath.contains("BuildConfig.class")) {
                    // 判断当前目录是否是在我们的应用包里面

                    println("=======${filePath}")
                    int index = filePath.indexOf(packageName.replace(".", "/"));
                    boolean isMyPackage = index != -1;
                    if (isMyPackage) {
                        String className = Utils.getClassName(index, filePath);
                        println("-----***className: ${className}")

                        FileInputStream fileInputStream = new FileInputStream(filePath)
                        CtClass c = pool.makeClass(fileInputStream)
                        //CtClass c = pool.getCtClass(className)
                        if (c.isFrozen()) c.defrost()


                        CtMethod[] methods = c.getDeclaredMethods()

                        String tag = Utils.getSimpleClassName(className)
                        for (CtMethod method : methods) {
                            String methodName = method.name + "..."
                            /*Object[] annotations = method.getAnnotations()

                            if (annotations != null && annotations.length > 0) {
                                MethodInfo methodInfo = method.getMethodInfo()
                                AnnotationsAttribute attribute = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag)

                                Annotation annotation = attribute.getAnnotation(TestAnno.name)
                                if (annotation != null) {
                                    def names = annotation.getMemberNames()
                                    String name = annotation.getMemberValue("name")
                                    String value = annotation.getMemberValue("value")
                                }
                            }*/

                            LogLevel level;
                            MethodInfo methodInfo = method.getMethodInfo()
                            AnnotationsAttribute attribute = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag)
                            if (attribute != null){
                                for (Annotation annotation : attribute.getAnnotations()){
                                    switch (annotation.typeName){
                                        case DebugLog.class.name:
                                            level = LogLevel.DEBUG
                                            break
                                        case InfoLog.class.name:
                                            level = LogLevel.INFO
                                            break
                                        default:
                                            level = LogLevel.DEBUG
                                            break
                                    }

                                    String codes = LogWare.generateLogContent(level, tag, methodName)

                                    method.insertBefore(codes)
                                }
                            }

                            //method.insertBefore(" System.out.println(\"start\"); ")
                            //method.insertBefore("android.util.Log.i(${tag}, ${methodName});")

                            /*StringBuilder startInjectSB = new StringBuilder()
                            startInjectSB.append("    android.util.Log.d(\"${tag}\",")
                            startInjectSB.append("\"${methodName}\"")
                            startInjectSB.append(");")

                            String ss = startInjectSB.toString()*/

                        }

                        println("===========${path}")
                        c.writeFile(path)

                        c.detach()//用完一定记得要卸载，否则pool里的永远是旧的代码
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace()
        }
    }
}