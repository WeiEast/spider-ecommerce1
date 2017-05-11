import ch.qos.logback.classic.encoder.PatternLayoutEncoder
// 30秒扫描一次日志配置更新
scan("30 seconds")

// 日志路径
def logPath = "/dashu/log"

// 控制台
appender("console", ConsoleAppender) {
    charset = "UTF-8"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%L] %m%n"
    }
}


// 业务日志
appender("sysFile", RollingFileAppender) {
    file = "${logPath}/rawdatacentral.log"
    charset = "UTF-8"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%L] %m%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/rawdatacentral.log.%d{yyyy-MM-dd}.gz"
        maxHistory = 15 // 保留最近天数的日志
    }
}



root(INFO, ["console", "sysFile"])
