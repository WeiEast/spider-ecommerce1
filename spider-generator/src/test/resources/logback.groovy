import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.INFO
// 30秒扫描一次日志配置更新
scan("30 seconds")

// 日志路径
def logPath = "/data/logs"

// 控制台
appender("console", ConsoleAppender) {
    charset = "UTF-8"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%L] %m%n"
    }
}



// 发送以及消费消息的long日志
root(INFO, ["console"])



