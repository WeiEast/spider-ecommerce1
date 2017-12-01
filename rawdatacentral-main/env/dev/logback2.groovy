import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import java.nio.charset.Charset

// 30秒扫描一次日志配置更新
scan("60 seconds")
def charsetName = "UTF-8"
def appName = "rawdatacentral"
def serverIp = System.getProperty("server.ip");

// 日志路径
def logPath = "/dashu/log/${appName}/${serverIp}"

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
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/${appName}.log.%d{yyyy-MM-dd}"
        maxHistory = 3 // 保留最近天数的日志
    }
}



root(INFO, ["console", "sysFile"])
logger("org.apache.http.impl.conn.Wire",OFF)
logger("org.apache.http",INFO)
logger("org.apache.http.client.protocol.ResponseProcessCookies",INFO)
//logger("org.apache.http.client.protocol.ResponseProcessCookies",DEBUG)
logger("com.alibaba.dubbo.monitor.dubbo", OFF)
logger("com.alibaba.dubbo.rpc.protocol.dubbo", OFF)
logger("com.datatrees.rawdatacentral.submitter.filestore.FileStoreServiceImpl", OFF)

