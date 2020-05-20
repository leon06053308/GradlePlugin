package com.way.plg.utils

import javassist.ClassPool
import javassist.CtMethod


public class Utils {
    /**
     * 事先载入相关类
     * @param pool
     */
    /*static void importBaseClass(ClassPool pool) {
        pool.importPackage(LogTimeHelper.LogTimeAnnotation);
        pool.importPackage(BusHelper.OkBusAnnotation);
        pool.importPackage(BusHelper.OkBusRegisterAnnotation);
        pool.importPackage(BusHelper.OkBusUnRegisterAnnotation);
        pool.importPackage("android.os.Bundle");
        pool.importPackage("com.deemons.bus.OkBus")
        pool.importPackage("com.deemons.bus.Event")
        pool.importPackage("android.os.Message")
    }*/

    static String getSimpleName(CtMethod ctmethod) {
        def methodName = ctmethod.getName();
        return methodName.substring(
                methodName.lastIndexOf('.') + 1, methodName.length());
    }

    static String getClassName(int index, String filePath) {
        int end = filePath.length() - 6 // .class = 6
        return filePath.substring(index, end).replace('\\', '.').replace('/', '.')
    }

    static String getSimpleClassName(String className) {
        String[] temp = className.split("\\.")
        return temp[temp.length-1]
    }
}
