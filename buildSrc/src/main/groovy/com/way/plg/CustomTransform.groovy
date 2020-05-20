package com.way.plg

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class CustomTransform extends Transform {
    private static Project project

    CustomTransform(Project project) {
        this.project = project
    }

    @Override
    boolean isIncremental() {
        return false;
    }

    @Override
    Set<QualifiedContent.ContentType> getOutputTypes() {
        return super.getOutputTypes();
    }

    @Override
    String getName() {
        return "CustomPlugin";
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        println("==============================CustomPlugin visit start========================================")


        def outputProvider = transformInvocation.outputProvider

        transformInvocation.inputs.each { TransformInput input ->
            //宿主项目
            input.directoryInputs.each { DirectoryInput directoryInput ->
                MyInject.injectDir(directoryInput.file.absolutePath, project)

                def dest = outputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY)

                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            //第三方jar 虽然对jar没有操作，但是也要输出到out路径
            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(
                        jarName + md5Name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        /*def isIncremental = transformInvocation.isIncremental()
        def outputProvider = transformInvocation.outputProvider

        println "===============isIncremental= ${isIncremental} ==========================="

        if (!isIncremental){
            outputProvider.deleteAll()
        }

        transformInvocation.inputs.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                //
                processJarInput(jarInput, outputProvider, isIncremental)
            }

            input.directoryInputs.forEach{ directoryInput ->
                processDirectoryInput(directoryInput, outputProvider, isIncremental)
            }
        }*/

        println("==============================CustomPlugin visit end========================================")

    }

    private void processJarInput(JarInput jarInput, TransformOutputProvider outputProvider, boolean isIncremental){
        def dest = outputProvider.getContentLocation(jarInput.file.absolutePath,
                jarInput.contentTypes,
                jarInput.scopes,
                Format.JAR)

        if (isIncremental){
            processJarInputIncremental(jarInput, dest)
        }else {
            processJarInputNoIncremental(jarInput, dest)
        }
    }

    private void processJarInputNoIncremental(JarInput jarInput, File dest) {
        transformJarInput(jarInput, dest)
    }

    //jar 增量的修改
    private void processJarInputIncremental(JarInput jarInput, File dest) {
        switch (jarInput.status){
            case Status.ADDED:
                transformJarInput(jarInput, dest)
                break
            case Status.CHANGED:
                if (dest.exists()){
                    FileUtil.forceDelete(dest)
                }
                transformJarInput(jarInput, dest)
                break
            case Status.REMOVED:
                if (dest.exists()){
                    FileUtil.forceDelete(dest)
                }
                break
            case Status.NOTCHANGED:
                break
            default:
                break
        }
    }

    private void transformJarInput(JarInput jarInput, File dest) {
        FileUtils.copyFile(jarInput.file, dest)
    }

    private void processDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider, boolean isIncremental){
        def dest = outputProvider.getContentLocation(directoryInput.file.absolutePath,
                                                    directoryInput.contentTypes,
                                                    directoryInput.scopes,
                                                    Format.DIRECTORY)

        if (isIncremental) {
            //处理增量编译
            processDirectoryInputIncremental(directoryInput, dest)
        } else {
            processDirectoryInputNoIncremental(directoryInput, dest)
        }
    }

    private void processDirectoryInputNoIncremental(DirectoryInput directoryInput, File dest) {
        transformDirectoryInput(directoryInput, dest)
    }

    //文件增量修改
    private void processDirectoryInputIncremental(DirectoryInput directoryInput, File dest) {
        FileUtils.forceMkdir(dest)
        def srcDirPath = directoryInput.file.absolutePath
        def destDirPath = dest.absolutePath
        def fileStatusMap = directoryInput.changedFiles
        fileStatusMap.forEach { entry ->
            def inputFile = entry.key
            def status = entry.value
            def destFilePath = inputFile.absolutePath.replace(srcDirPath, destDirPath)
            def destFile = new File(destFilePath)

            switch (jarInput.status){
                case Status.ADDED:
                    transformDirectoryInput(directoryInput, dest)
                    break
                case Status.CHANGED:
                    //处理有变化的
                    FileUtils.touch(destFile)
                    //Changed的状态需要先删除之前的
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest)
                    }
                    //真正transform的地方
                    transformDirectoryInput(directoryInput, dest)
                    break
                case Status.REMOVED:
                    if (destFile.exists()) {
                        FileUtils.forceDelete(destFile)
                    }
                    break
                case Status.NOTCHANGED:
                    break
                default:
                    break
            }
        }
    }

    private void transformDirectoryInput(DirectoryInput directoryInput, File dest) {
        /*if (directoryInput.file.isDirectory() == true) {
            File dir = new File(directoryInput.file.absolutePath)
            dir.eachFileRecurse { file ->
                def name = file.name
                //在这里进行代码处理
                if (name.endsWith(".class")
                        && !name.startsWith("R\$")
                        && "R.class" != name
                        && "BuildConfig.class" != name) {

                    def className = name.split(".class")[0]
                    println "************${className}"
                    handleClass(file, className)
                }
            }
        }*/

        MyInject.injectDir(directoryInput.file.absolutePath, project)

        //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        println(directoryInput.file.absolutePath)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }
}
