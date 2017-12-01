package env.daily

import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import java.nio.charset.Charset

scan("60 seconds")
def charsetName = "UTF-8"
def appName = "rawdatacentral"
def serverIp = System.getProperty("server.ip","default");

// 日志路径
def logPath = "/dashu/log/${serverIp}/${appName}"

// 控制台
appender("console", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
}

// 业务日志
appender("sysFile", RollingFileAppender) {
    file = "${logPath}/${appName}.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/${appName}.log.%d{yyyy-MM-dd}.%i"
        maxFileSize = "1GB"
        maxHistory = 7 // 保留最近天数的日志
    }
}



root(INFO, ["console", "sysFile"])
logger("com.alibaba.dubbo.monitor.dubbo", OFF)

