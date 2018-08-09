

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import com.datatrees.spider.share.common.utils.DateUtils

import java.nio.charset.Charset

scan("60 seconds")
def charsetName = "UTF-8"
def appName = "spider"
def serverIp = System.getProperty("server.ip","127.0.0.1")

def ips =serverIp.split("\\.");
// 日志路径
def publishDate = DateUtils.format(new Date(), "MMdd")
def logPath = "/dashu/log/${appName}/${publishDate}/${ips[2]}.${ips[3]}"

// 控制台
appender("consoleAppender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
}

// 业务日志
appender("sysAppender", RollingFileAppender) {
    file = "${logPath}/sys.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/sys.log.%d{yyyy-MM-dd}.%i"
        maxFileSize = "1GB"
        maxHistory = 7 // 保留最近天数的日志
    }
}

// plugin日志
appender("pluginAppender", RollingFileAppender) {
    file = "${logPath}/plugin.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%p] [%.10t] [%c{1}][%M][%L] %m%n"
        charset = Charset.forName(charsetName)
    }
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/plugin.log.%d{yyyy-MM-dd}.%i"
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
logger("com.alibaba.dubbo.rpc.cluster.support.wrapper", OFF)
logger("com.alibaba.dubbo.rpc.protocol.dubbo", OFF)
logger("com.datatrees.spider.spider.share.service.impl.FileStoreServiceImpl", OFF)
logger("com.datatrees.spider.spider.share.service.impl.AppCrawlerConfigServiceImpl", DEBUG)
logger("com.datatrees.crawler.core.processor.operation", DEBUG)
logger("com.datatrees.crawler.core.processor.extractor", DEBUG)

