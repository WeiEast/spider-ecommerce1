import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import java.nio.charset.Charset

// 30秒扫描一次日志配置更新
scan("30 seconds")
def charsetName = "UTF-8"

// 日志路径
def logPath = "/dashu/log"

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
    append = true
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/rawdatacentral.log.%d{yyyy-MM-dd}"
        maxHistory = 15 // 保留最近天数的日志
    }
}



root(INFO, ["console", "sysFile"])
logger("com.alibaba.dubbo.monitor.dubbo", OFF)

