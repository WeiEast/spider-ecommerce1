package env.dev

import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import java.nio.charset.Charset

scan("60 seconds")
def charsetName = "UTF-8"
def appName = "rawdatacentral"
def serverIp = System.getProperty("server.ip", "default");

// 日志路径
def logPath = "/dashu/log/${appName}"

// 控制台
appender("consoleAppender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
}

// 业务日志
appender("sysAppender", RollingFileAppender) {
    file = "${logPath}/${appName}.${serverIp}.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/${appName}.${serverIp}.log.%d{yyyy-MM-dd}.%i"
        maxFileSize = "1GB"
        maxHistory = 7 // 保留最近天数的日志
    }
}

// plugin日志
appender("pluginAppender", RollingFileAppender) {
    file = "${logPath}/plugin.${serverIp}.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/plugin.${serverIp}.log.%d{yyyy-MM-dd}.%i"
        maxFileSize = "1GB"
        maxHistory = 7 // 保留最近天数的日志
    }
}






root(INFO, ["consoleAppender", "sysAppender"])
logger("plugin_log", INFO, ["pluginAppender"], false)
logger("org.apache.http.impl.conn.Wire", OFF)
logger("org.apache.http", INFO)
logger("org.apache.http.client.protocol.ResponseProcessCookies", INFO)
//logger("org.apache.http.client.protocol.ResponseProcessCookies",DEBUG)
logger("com.alibaba.dubbo.monitor.dubbo", OFF)
logger("com.alibaba.dubbo.rpc.protocol.dubbo", OFF)
logger("com.datatrees.rawdatacentral.submitter.filestore.FileStoreServiceImpl", OFF)

