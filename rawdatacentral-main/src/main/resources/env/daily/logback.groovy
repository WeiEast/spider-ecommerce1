package env.daily

import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import java.nio.charset.Charset

scan("60 seconds")
def charsetName = "UTF-8"
def appName = "rawdatacentral"
def serverIp = System.getProperty("server.ip", "default");

// 日志路径
def logPath = "/dashu/log"

// 控制台
appender("consoleAppender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
}

// 业务日志
appender("sysAppender", RollingFileAppender) {
    file = "${logPath}/${appName}/%d{yyyy-MM-dd}/${serverIp}/sys.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/${appName}/%d{yyyy-MM-dd}/${serverIp}/sys.log.%i"
        maxFileSize = "1GB"
        maxHistory = 7 // 保留最近天数的日志
    }
}

// plugin日志
appender("pluginAppender", RollingFileAppender) {
    file = "${logPath}/${appName}/%d{yyyy-MM-dd}/${serverIp}/plugin.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/${appName}/%d{yyyy-MM-dd}/${serverIp}/plugin.log.%i"
        maxFileSize = "1GB"
        maxHistory = 7 // 保留最近天数的日志
    }
}

root(INFO, ["consoleAppender", "sysAppender"])
logger("plugin_log", INFO, ["pluginAppender"], false)
logger("com.alibaba.dubbo.monitor.dubbo", OFF)

