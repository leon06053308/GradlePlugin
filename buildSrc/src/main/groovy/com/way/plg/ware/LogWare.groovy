package com.way.plg.ware

class LogWare{
    static String generateLogContent(LogLevel level, String tag, String methodName){
        String logLevel = getLogLevel(level)

        StringBuilder codeContent = new StringBuilder()
        codeContent.append("    android.util.Log.${logLevel}")
        codeContent.append("(")
        codeContent.append("\"${tag}\",")
        codeContent.append("\"${methodName}\"")
        codeContent.append(");")

        return codeContent.toString()
    }

    private static String getLogLevel(LogLevel level){
        switch (level){
            case LogLevel.DEBUG:
                return "d"
            case LogLevel.INFO:
                return "i"
            default:
                return "d"
        }
    }
}