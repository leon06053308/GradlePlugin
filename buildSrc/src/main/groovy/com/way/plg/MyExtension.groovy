package com.way.plg

class MyExtension {
    def packageName
    def logLevel = "d"

    def getPackageName() {
        return packageName
    }

    void setPackageName(packageName) {
        this.packageName = packageName
    }
}