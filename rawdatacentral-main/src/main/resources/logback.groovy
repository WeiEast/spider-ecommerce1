import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import java.nio.charset.Charset

// 30秒扫描一次日志配置更新
scan("30 seconds")
def charsetName = "UTF-8"

// 日志路径
def logPath = "/dashu/log/rawdatacentral"

// 控制台
appender("console", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
}

// 业务日志
appender("sysFile", RollingFileAppender) {
    file = "${logPath}/rawdatacentral.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/rawdatacentral.log.%d{yyyy-MM-dd}.gz"
        maxHistory = 15 // 保留最近天数的日志
    }
}



root(DEBUG, ["console", "sysFile"])
logger("com.alibaba.dubbo.monitor.dubbo", OFF)
logger("com.datatrees.databoss.action.client.service", OFF)


