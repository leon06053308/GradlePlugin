package com.way.plg.ware

class LogWare{
    static String generateLogContent(LogLevel level, String tag, String methodName, List<String> paramNames){
        String logLevel = getLogLevel(level)

        if (paramNames != null &&paramNames.size() > 0){
            methodName += ": "
        }else {
            methodName += "..."
        }

        StringBuilder codeContent = new StringBuilder()
        codeContent.append("    android.util.Log.${logLevel}")
            .append("(")
            .append("\"${tag}\",")
            .append("\"${methodName}\"")

        for (String param : paramNames){
            codeContent
                .append("+")
                .append("\"${param}-\"")
                .append("+")
                .append(param)
                .append("+")
                .append("\" \"")

        }

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