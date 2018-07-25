package com.datatrees.rawdatacentral.common.utils;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.share.common.utils.CollectionUtils;
import com.datatrees.spider.share.common.utils.JsoupXpathUtils;
import org.jsoup.nodes.Element;
import org.junit.Test;

/**
 * @author Jerry
 * @since 20:02 19/03/2018
 */
public class JsoupXpathUtilsTest {

    private String pageContent = "<!doctype html>\n" + "<html>\n" + "<head>\n" + "\t<meta charset=\"GBK\" />\n" + "\t<title>登录中心 － 支付宝</title>\n" +
            "\n" + "    <!-- " + "FD:130:authcenter/login/certCheck_static.vm:START -->\n" + "\n" +
            "<link rel=\"icon\" href=\"https://i.alipayobjects.com/common/favicon/favicon.ico\" type=\"image/x-icon\" />\n" +
            "<link rel=\"shortcut icon\" href=\"https://i.alipayobjects.com/common/favicon/favicon.ico\" type=\"image/x-icon\" /><meta name=\"viewport\" content=\"width=device-width, " +
            "initial-scale=1, maximum-scale=1, user-scalable=no\" />\n" + "\n" +
            "<!-- FD:106:alipay/tracker/tracker_time.vm:START --><!-- FD:106:alipay/tracker/tracker_time.vm:784:tracker_time.schema:全站 tracker 开关:START --><script charset=\"utf-8\" crossorigin=\"crossorigin\" src=\"https://a.alipayobjects.com/static/ar/alipay.light.base-1.8.js\"></script>\n" +
            "\n" + "\n" + "<script type=\"text/javascript\">\n" + "if (!window._to) {\n" + "  window._to = { start: new Date() };\n" + "}\n" +
            "</script>\n" + "\n" +
            "<script charset=\"utf-8\" src=\"https://as.alipayobjects.com/??g/component/tracker/2.3.2/index.js,g/component/smartracker/2.0.2/index.js\"></script>\n" +
            "<script charset=\"utf-8\" src=\"https://a.alipayobjects.com/g/utiljs/rd/1.0.2/rd.js\"></script>\n" + "\n" + "\n" + "\n" + "<script>\n" +
            "  window.Tracker && Tracker.start &&  Tracker.start();\n" + "</script>\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +
            "<!-- FD:106:alipay/tracker/tracker_time.vm:784:tracker_time.schema:全站 tracker 开关:END -->\n" +
            "<!-- FD:106:alipay/tracker/tracker_time.vm:END -->\n" + "\n" + "\n" + "\n" + "<style type=\"text/css\">\n" + "/**\n" + " * 浏览器重置\n" +
            " */\n" + "* {\n" + "    margin: 0;\n" + "    padding: 0;\n" + "}\n" + "html{\n" + "    color:#000;background:#fff;\n" +
            "    -webkit-text-size-adjust: 100%;\n" + "    -ms-text-size-adjust: 100%;\n" + "}\n" + "body,button,input,select,textarea{\n" +
            "    font:12px/1.5 tahoma,arial,\"Hiragino Sans GB\",\\5b8b\\4f53;\n" + "}\n" + "/* 去除默认边框 */\n" + "fieldset,img{\n" + "    border:0;\n" +
            "}\n" + "/* 清理浮动 */\n" + ".fn-clear:after {\n" + "    visibility:hidden;\n" + "    display:block;\n" + "    font-size:0;\n" +
            "    content:\" \";\n" + "    clear:both;\n" + "    height:0;\n" + "}\n" + ".fn-clear {\n" + "    zoom:1; /* for IE6 IE7 */\n" + "}\n" +
            "/* 隐藏, 通常用来与 JS 配合 */\n" + "body .fn-hide {\n" + "    display:none;\n" + "}\n" + "/**\n" + " * 页面样式\n" + " */\n" +
            ".header,#content{\n" + "    width: 950px;\n" + "    margin: 10px auto;\n" + "}\n" + ".header{\n" + "    margin-top: 20px;\n" +
            "    overflow: hidden;\n" + "    zoom: 1;\n" + "}\n" + ".header-title {\n" + "    float: left;\n" + "}\n" + ".header-title #logo {\n" +
            "    float: left;\n" + "}\n" + ".header-title h2 {\n" + "    float: left;\n" + "    font-size: 16px;\n" + "    font-weight: normal;\n" +
            "    font-family: 'Microsoft YaHei';\n" + "    color: #666;\n" + "    line-height: 16px;\n" + "    margin: 15px 0 0 10px;\n" +
            "    padding: 0px 0 0 10px;\n" + "    border-left: 1px solid #888;\n" + "}\n" + ".header-title h2.no-logo {\n" + "    margin-left: 0;\n" +
            "    padding-left: 0;\n" + "    border: none;\n" + "}\n" + "#footer {\n" + "    margin-top: 40px;\n" + "    text-align: center;\n" +
            "    border-top: 1px solid #f5f5f5;\n" + "    padding-top: 20px;\n" + "}\n" + "#footer .foot-nav a {\n" + "    margin:0 7px;\n" + "}\n" +
            ".copyright {\n" + "    color: #808080;\n" + "    margin-top: 20px;\n" + "    text-align: center;\n" + "}\n" + ".server{\n" +
            "    text-align: center;\n" + "    color: #fff;\n" + "}\n" + ".p-notice h3 {font-size: 14px; font-weight: bold;}\n" + "/**\n" +
            " * 响应式布局\n" + " */\n" + "/** 小于 320px **/\n" + "@media only screen and (max-width:320px) {\n" + "    .header,#content{\n" +
            "        width: 90%;\n" + "    }\n" + "}\n" + "/** 大于 768px ，小于 949px **/\n" +
            "@media only screen and (min-width: 321px) and (max-width:949px) {\n" + "    .header,#content{\n" + "        width: 94%;\n" + "    }\n" +
            "}\n" + "</style>\n" + "\n" + "\n" + "\n" + "\n" + "<script type=\"text/javascript\">\n" + "AralePreload = [];\n" +
            "var AP = AP || {};\n" + "AP.PageVar = {\n" + "\tapp_domain: \"https://authgtj.alipay.com:443\",\n" +
            "\tpersonal_domain:\"https://lab.alipay.com:443\",\n" + "\tpersonalprod_domain:\"\",\n" + "\tmipgw_domain:\"\",\n" +
            "\tccrprod_domain:\"\"\n" + "}\n" + "</script>\n" + "<!-- FD:130:authcenter/login/certCheck_static.vm:END -->\n" + "</head>\n" + "\n" +
            "<body class=\"blank\">\n" + "\t<div id=\"container\">\n" + "<!-- FD:106:alipay/tracker/um.vm:START -->\n" + "\n" +
            "<style type=\"text/css\">\n" + ".umidWrapper{display:block; height:1px;}\n" + "</style>\n" +
            "<span style=\"display:inline;width:1px;height:1px;overflow:hidden\">\n" + "\n" + "<script type=\"text/javascript\">\n" +
            "var cimg = new Image(1,1);\n" + "cimg.onload = function() {\n" + "    cimg.onload = null;\n" + "};\n" +
            "cimg.src = \"https://ynuf.alipay.com/service/clear.png?xt=C4d196fd90f1f920bd6aa2b24f7eba661&xa=0000000000000A2126231E1B\";\n" +
            "</script>\n" + "\n" +
            "<script type=\"text/javascript\" src=\"https://assets.alicdn.com/g/security/umscript/3.0.11/um.js\" charset=\"utf-8\"></script>\n" +
            "\n" + "<script type=\"text/javascript\">\n" + "um.init({\n" + "    enabled: 1, ratio: 1, timeout: 5000,\n" +
            "    token: 'C4d196fd90f1f920bd6aa2b24f7eba661', timestamp: '7976219856555E4653486C75',\n" +
            "    serviceUrl: 'https://ynuf.alipay.com/service/um.json',\n" + "  appName:'0000000000000A2126231E1B'\n" + "});\n" + "\n" +
            "</script>\n" + "</span>\n" +
            "<!-- FD:106:alipay/tracker/um.vm:END --><!-- FD:231:alipay/tracker/seajs.vm:START --><!-- FD:231:alipay/tracker/seajs.vm:2882:tracker/seajs.schema:seajs-静态文件地址:START -->\n" +
            "\n" + "\t\n" + "\n" + "\n" + "<!-- monitor 防错代码 -->\n" + "<script>\n" + "(function(win){\n" + "  if(!win.monitor){win.monitor = {};}\n" +
            "\n" + "  var METHODS = [\"lost\", \"log\", \"error\", \"on\", \"off\"];\n" + "\n" +
            "  for(var i=0,method,l=METHODS.length; i<l; i++){\n" + "    method = METHODS[i];\n" +
            "    if(\"function\" !== typeof win.monitor[method]){\n" + "      win.monitor[method] = function(){};\n" + "    }\n" + "  }\n" +
            "})(window);\n" + "</script>\n" + "\n" + "<!-- seajs以及插件 -->\n" +
            "<script charset=\"utf-8\" crossorigin=\"anonymous\" id=\"seajsnode\" onerror=\"window.monitor && monitor.lost && monitor.lost(this.src)\" src=\"https://a.alipayobjects.com:443/??seajs/seajs/2.2.3/sea.js,seajs/seajs-combo/1.0.0/seajs-combo.js,seajs/seajs-style/1.0.2/seajs-style.js,seajs/seajs-log/1.0.0/seajs-log.js,jquery/jquery/1.7.2/jquery.js,gallery/json/1.0.3/json.js,alipay-request/3.0.8/index.js\"></script>\n" +
            "\n" + "<!-- seajs config 配置 -->\n" + "<script>\n" + "seajs.config({\n" + "  alias: {\n" + "    '$': 'jquery/jquery/1.7.2/jquery',\n" +
            "    '$-debug': 'jquery/jquery/1.7.2/jquery',\n" + "    'jquery': 'jquery/jquery/1.7.2/jquery',\n" +
            "    'jquery-debug': 'jquery/jquery/1.7.2/jquery-debug',\n" + "    'seajs-debug': 'seajs/seajs-debug/1.1.1/seajs-debug'\n" + "  },\n" +
            "  crossorigin: function(uri){\n" + "\n" + "    function typeOf(type){\n" + "\t  return function(object){\n" +
            "\t    return Object.prototype.toString.call(object) === '[object ' + type + ']';\n" + "\t  }\n" + "\t}\n" +
            "\tvar isString = typeOf(\"String\");\n" + "\tvar isRegExp = typeOf(\"RegExp\");\n" + "\n" + "\tvar whitelist = [];\n" + "\n" + "    \n" +
            "      \n" + "        whitelist.push(\"https://a.alipayobjects.com/\");\n" + "      \n" + "    \n" + "\n" +
            "\tfor (var i=0, rule, l=whitelist.length; i<l; i++){\n" + "\t  rule = whitelist[i];\n" + "\t  if (\n" +
            "\t    (isString(rule) && uri.indexOf(rule) === 0) ||\n" + "\t    (isRegExp(rule) && rule.test(uri))\n" + "\t\t) {\n" + "\n" +
            "\t    return \"anonymous\";\n" + "\t  }\n" + "\t}\n" + "  },\n" + "  vars: {\n" + "    locale: 'zh-cn'\n" + "  }\n" + "});\n" +
            "</script>\n" + "\n" + "<!-- 兼容原有的 plugin-i18n 写法 -->\n" +
            "<!-- https://github.com/seajs/seajs/blob/1.3.1/src/plugins/plugin-i18n.js -->\n" + "<script>\n" +
            "seajs.pluginSDK = seajs.pluginSDK || {\n" + "  Module: {\n" + "    _resolve: function() {}\n" + "  },\n" + "  config: {\n" +
            "    locale: ''\n" + "  }\n" + "};\n" + "// 干掉载入 plugin-i18n.js，避免 404\n" + "seajs.config({\n" + "  map: [\n" +
            "\t[/^.*\\/seajs\\/plugin-i18n\\.js$/, ''],\n" + "\t[/^.*\\i18n!lang\\.js$/, '']\n" + "  ]\n" + "});\n" + "</script>\n" + "\n" +
            "<!-- 路由旧 ID，解决 seajs.use('select/x.x.x/select') 的历史遗留问题 -->\n" + "<script>\n" + "(function(){\n" + "\n" +
            "var JQ = '/jquery/1.7.2/jquery.js';\n" +
            "seajs.cache['https://a.alipayobjects.com:443/gallery' + JQ] = seajs.cache['https://a.alipayobjects.com:443/jquery' + JQ];\n" + "\n" +
            "var GALLERY_MODULES = [\n" + "  'async','backbone','coffee','cookie','es5-safe','handlebars','iscroll',\n" +
            "  'jasmine','jasmine-jquery','jquery','jquery-color','json','keymaster',\n" +
            "  'labjs','less','marked','moment','mustache','querystring','raphael',\n" +
            "  'socketio','store','swfobject','underscore','zepto','ztree'\n" + "];\n" + "\n" + "var ARALE_MODULES = [\n" +
            "  'autocomplete','base','calendar','class','cookie','dialog','easing',\n" +
            "  'events','iframe-uploader','iframe-shim','messenger','overlay','popup',\n" +
            "  'position','select','switchable','tip','validator','widget'\n" + "];\n" + "\n" + "var util = {};\n" +
            "util.indexOf = Array.prototype.indexOf ?\n" + "  function(arr, item) {\n" + "    return arr.indexOf(item);\n" + "  } :\n" +
            "  function(arr, item) {\n" + "    for (var i = 0; i < arr.length; i++) {\n" + "      if (arr[i] === item) {\n" + "        return i;\n" +
            "      }\n" + "    }\n" + "    return -1;\n" + "  };\n" + "util.map = Array.prototype.map ?\n" + "  function(arr, fn) {\n" +
            "    return arr.map(fn);\n" + "  } :\n" + "  function(arr, fn) {\n" + "    var ret = [];\n" +
            "\tfor (var i = 0; i < arr.length; i++) {\n" + "        ret.push(fn(arr[i], i, arr));\n" + "    }\n" + "    return ret;\n" + "  };\n" +
            "\n" + "function contains(arr, item) {\n" + "  return util.indexOf(arr, item) > -1\n" + "}\n" + "\n" + "function map(id) {\n" +
            "  id = id.replace('#', '');\n" + "\n" + "  var parts = id.split('/');\n" + "  var len = parts.length;\n" + "  var root, name;\n" + "\n" +
            "  // id = root/name/x.y.z/name\n" + "  if (len === 4) {\n" + "    root = parts[0];\n" + "    name = parts[1];\n" + "\n" +
            "    // gallery 或 alipay 开头的没有问题\n" + "    if (root === 'alipay' || root === 'gallery') {\n" + "      return id;\n" + "    }\n" + "\n" +
            "    // arale 开头的\n" + "    if (root === 'arale') {\n" + "      // 处理 arale/handlebars 的情况\n" +
            "      if (contains(GALLERY_MODULES, name)) {\n" + "        return id.replace('arale/', 'gallery/');\n" + "      } else {\n" +
            "        return id;\n" + "      }\n" + "    }\n" + "  }\n" + "  // id = name/x.y.z/name\n" + "  else if (len === 3) {\n" +
            "    name = parts[0]\n" + "\n" + "    // 开头在 GALLERY_MODULES 或 ARALE_MODULES\n" + "    if (contains(GALLERY_MODULES, name)) {\n" +
            "      return 'gallery/' + id;\n" + "    } else if (contains(ARALE_MODULES, name)) {\n" + "      return 'arale/' + id;\n" + "    }\n" +
            "  }\n" + "\n" + "  return id;\n" + "}\n" + "\n" + "var _use = seajs.use;\n" + "\n" + "seajs.use = function(ids, callback) {\n" +
            "  if (typeof ids === 'string') {\n" + "    ids = [ids];\n" + "  }\n" + "\n" + "  ids = util.map(ids, function(id) {\n" +
            "    return map(id);\n" + "  });\n" + "\n" + "  return _use(ids, callback);\n" + "}\n" + "\n" + "})();\n" +
            "</script><!-- FD:231:alipay/tracker/seajs.vm:2882:tracker/seajs.schema:seajs-静态文件地址:END --><!-- FD:231:alipay/tracker/seajs.vm:END -->\n" +
            "\n" + "\n" +
            "<!-- FD:231:alipay/notice/headNotice.vm:START --><!-- FD:231:alipay/notice/headNotice.vm:5381:notice/headNotice.schema:headNotice-全站公告:START --><!--[if lte IE 7]>\n" +
            "<style>.kie-bar { display: none; height: 24px; line-height: 1.8; font-weight:normal; text-align: center; border:1px solid #fce4b5; background-color:#FFFF9B; color:#e27839; position: relative; font-size: 12px; margin: 5px 0 0 0; padding: 5px 0 2px 0; } .kie-bar a { text-decoration: none; color:#08c; background-repeat: none; } .kie-bar a#kie-setup-IE8,.kie-bar a#kie-setup-taoBrowser { padding: 0 0 2px 20px; *+padding-top: 2px; *_padding-top: 2px; background-repeat: no-repeat; background-position: 0 0; } .kie-bar a:hover { text-decoration: underline; } .kie-bar a#kie-setup-taoBrowser { background-position: 0 -20px; }</style>\n" +
            "<div id=\"kie-bar\" class=\"kie-bar\">您现在使用的浏览器版本过低，可能会导致部分图片和信息的缺失。请立即 <a href=\"http://www.microsoft.com/china/windows/IE/upgrade/index.aspx\" id=\"kie-setup-IE8\" seed=\"kie-setup-IE8\" target=\"_blank\" title=\"免费升级至IE8浏览器\">免费升级</a> 或下载使用 <a href=\"http://download.browser.taobao.com/client/browser/down.php?pid=0080_2062\" id=\"kie-setup-taoBrowser\" seed=\"kie-setup-taoBrowser\" target=\"_blank\" title=\"淘宝浏览器\">淘宝浏览器</a> ，安全更放心！ <a title=\"查看帮助\" target=\"_blank\" seed=\"kie-setup-help\" href=\"https://help.alipay.com/lab/help_detail.htm?help_id=260579\">查看帮助</a></div>\n" +
            "<script type=\"text/javascript\">\n" + "(function () {\n" + "    function IEMode() {\n" +
            "        var ua = navigator.userAgent.toLowerCase();\n" + "        var re_trident = /\\btrident\\/([0-9.]+)/;\n" +
            "        var re_msie = /\\b(?:msie |ie |trident\\/[0-9].*rv[ :])([0-9.]+)/;\n" + "        var version;\n" +
            "        if (!re_msie.test(ua)) {\n" + "            return false;\n" + "        }\n" + "        var m = re_trident.exec(ua);\n" +
            "        if (m) {\n" + "            version = m[1].split(\".\");\n" + "            version[0] = parseInt(version[0], 10) + 4;\n" +
            "            version = version.join(\".\");\n" + "        } else {\n" + "            m = re_msie.exec(ua);\n" +
            "            version = m[1];\n" + "        }\n" + "        return parseFloat(version);\n" + "    }\n" + "    var ie = IEMode();\n" +
            "    if (ie && ie < 8 && (self.location.href.indexOf(\"_xbox=true\") < 0)) {\n" +
            "        document.getElementById('kie-bar').style.display = 'block';\n" +
            "        document.getElementById('kie-setup-IE8').style.backgroundImage = 'url(https://i.alipayobjects.com/e/201307/jYwARebNl.png)';\n" +
            "        document.getElementById('kie-setup-taoBrowser').style.backgroundImage = 'url(https://i.alipayobjects.com/e/201307/jYwARebNl.png)';\n" +
            "    }\n" + "})();\n" + "</script>\n" + "<![endif]-->\n" + "\n" + "\n" + "\n" + "<style>\n" +
            "  .global-notice-announcement { width: 100%; min-width: 990px; height: 24px; line-height: 24px; }\n" +
            "  .global-notice-announcement p { width: 990px; margin: 0 auto; text-align: left; font-size: 12px; color: #fff; }\n" +
            "  .ssl-v3-rc4 { display: none; }\n" + "</style>\n" +
            "<div id=\"J-global-notice-ssl\" class=\"global-notice-announcement ssl-v3-rc4\" style=\"background-color: #ff6600;\">\n" +
            "  <p>您的浏览器版本太低，为保障信息的安全，<a href=\"https://www.alipay.com/x/kill-ie.htm\">请于2月28日前升级浏览器</a></p>\n" + "</div>\n" + "<script>\n" +
            "  /*\n" + "   * 获取cookie\n" + "   * @param {String} ctoken\n" + "   */\n" + "  function getCookie(name) {\n" +
            "    if (document.cookie.length > 0) {\n" + "      var begin = document.cookie.indexOf(name + '=');\n" + "      if (begin !== -1) {\n" +
            "        begin += name.length + 1;\n" + "        var end = document.cookie.indexOf(';', begin);\n" + "        if (end === -1) {\n" +
            "          end = document.cookie.length;\n" + "        }\n" + "        return unescape(document.cookie.substring(begin, end));\n" +
            "      }\n" + "    }\n" + "    return null;\n" + "  }\n" + "  window.onload = function() {\n" +
            "    var globalNoticeSsl = document.getElementById('J-global-notice-ssl');\n" + "    if (globalNoticeSsl) {\n" +
            "      var sslUpgradeTag = getCookie('ssl_upgrade');\n" + "      if (sslUpgradeTag && sslUpgradeTag === '1') {\n" +
            "        // 展示升级公告\n" + "        globalNoticeSsl.setAttribute('class', 'global-notice-announcement');\n" + "      } else {\n" +
            "        // 删除升级公告\n" + "        globalNoticeSsl.parentNode.removeChild(globalNoticeSsl);\n" + "      }\n" + "    }\n" + "  }\n" +
            "</script>\n" + "\n" +
            "<!-- FD:231:alipay/notice/headNotice.vm:5381:notice/headNotice.schema:headNotice-全站公告:END --><!-- FD:231:alipay/notice/headNotice.vm:END -->\n" +
            "<div class=\"header\">\n" + "    \t<div class=\"header-title\">\n" + "                <h1 id=\"logo\">\n" +
            "            <a href=\"https://www.alipay.com:443\" title=\"支付宝首页\" target=\"_self\">\n" +
            "                <img src=\"https://i.alipayobjects.com/i/ecmng/png/201405/2hrVAOpOLl.png\" width=\"95\" height=\"37\" alt=\"支付宝\" seed=\"auth-alipayLogo\"/>\n" +
            "            </a>\n" + "        </h1>\n" + "        <h2>登录</h2>\n" + "        \t</div>\n" + "\t</div>\n" + "\n" + "\n" + "\n" +
            "<script type=\"text/javascript\">\n" + "var umCounter = 0, certCounter = 0, umStatus = false, certStatus = false;\n" + "\n" +
            "function execute() {\n" + "    certPoll();\n" + "    umpoll();\n" + "}\n" + "function checkSubmit() {\n" +
            "    if(certStatus && umStatus) {\n" + "        delegateSubmit();\n" + "    }\n" + "}\n" + "function certPoll() {\n" +
            "    if(window.light && light.page && light.page.needCheckScProd > light.page.checkedScProd && !certStatus && certCounter++ < 120) {\n" +
            "        setTimeout(certPoll, 50);\n" + "     } else {\n" + "          certStatus = true;\n" + "          checkSubmit();\n" + "    };\n" +
            "}\n" + "function umpoll() {\n" + "    if(window.um && !um.getStatus() && !umStatus && umCounter++ < 100) {\n" +
            "        setTimeout(umpoll, 50);\n" + "    } else {\n" + "        umStatus = true;\n" + "        checkSubmit();\n" + "    };\n" + "}\n" +
            "\n" + "//  防止重提交\n" + "var form_submitted = false;\n" + "function delegateSubmit () {\n" + "    if (form_submitted) { return; }\n" +
            "    form_submitted = true;\n" + "    submit();\n" + "}\n" + "\n" + "    function submit(){\n" + "        setTimeout(function(){\n" +
            "            if(window.light && light.node){\n" + "                var tips = light.node('#content .p-notice h3'), klass = 'fn-hide';\n" +
            "                tips.item(0).addClass(klass);\n" + "                tips.item(1).removeClass(klass);\n" + "            };\n" +
            "            document.getElementById(\"J-submit-cert-check\").onclick = function(){\n" + "                var button = this;\n" +
            "                setTimeout(function(){\n" + "                    button.disabled = true;\n" + "                },50);\n" +
            "                                if(typeof Tracker != 'undefined'){\n" +
            "                    Tracker.click('auth-certCheck-submitBtn');\n" + "                }\n" + "            };\n" + "        },3000);\n" +
            "\n" + "                try {\n" + "            document.getElementById(\"J-TTI\").value =  new Date() - window._to.start;\n" +
            "        } catch(e) {}\n" + "        document.getElementById(\"LoginForm\").submit();\n" + "    }\n" + "\n" + "</script>\n" + "\n" +
            "<form name=\"LoginForm\" id=\"LoginForm\" method=\"post\" action=\"https://authgtj.alipay.com:443/login/certCheck.htm\" >\n" +
            "    <input type=\"hidden\" name=\"goto\" value=\"https://consumeprod.alipay.com/record/index.htm?null=\"/>\n" +
            "    <input type=\"hidden\" name=\"redirectType\" value=\"\"/>\n" + "    <input type=\"hidden\" name=\"loginScene\" value=\"\" />\n" +
            "        <input type=\"hidden\" name=\"tti\" value=\"\" id=\"J-TTI\" />\n" +
            "        <input type=\"hidden\" name=\"isIframe\" value=\"false\" id=\"J-iframe\" />\n" +
            "        <!-- FD:130:authcenter/login/renderCertCheckView.vm:START -->\t\n" + "    \n" + "<object classid=clsid:\n" +
            "        codebase=\"https://download.alipay.com#Version=\" class=\"fn-hide\" width=\"0\" height=\"0\"></object>\n" +
            "<object classid=clsid:\n" +
            "        codebase=\"https://download.alipay.com#Version=\" class=\"fn-hide\" width=\"0\" height=\"0\"></object>\n" +
            "<style type=\"text/css\" media=\"screen\">\n" + "    .p-notice {\n" + "        background-color: #E5F5FF;\n" +
            "        border: 1px solid #CBD7E3;\n" + "        color: #333333;\n" + "        margin-top: 13px;\n" +
            "        padding: 20px 20px 30px;\n" + "    }\n" + "\n" + "    .p-notice img {\n" + "        float: left;\n" +
            "        padding-right: 20px;\n" + "    }\n" + "\n" + "    .p-notice h3 {\n" + "        line-height: 32px;\n" + "    }\n" + "\n" +
            "    .alieditContainer {\n" + "        visibility: hidden;\n" + "    }\n" + "</style>\n" + "<div id=\"content\">\n" +
            "    <div class=\"p-notice\">\n" + "        <img src=\"https://i.alipayobjects.com/e/201305/M9UQl3TuH.gif\" alt=\"检测中\"/>\n" +
            "        <h3>正在检测您的账户安全状态，请稍候……</h3>\n" +
            "        <h3 class=\"fn-hide\">正在提交数据… 如果没有进入下一个页面，请点<input type=\"submit\" value=\"这里\" id=\"J-submit-cert-check\">继续。</h3>\n" +
            "    </div>\n" + "    <div class=\"fn-hide\">\n" +
            "        <input type=\"hidden\" id=\"_seaside_gogo_\" name=\"_seaside_gogo_\" value=\"\" />\n" +
            "        <input type=\"hidden\" id=\"_seaside_gogo_p\" name=\"_seaside_gogo_p\" value=\"\" />\n" +
            "            <input type=\"hidden\" name=\"REMOTE_PCID_NAME\" value=\"_seaside_gogo_pcid\"/>\n" +
            "        <input type=\"hidden\" name=\"_seaside_gogo_pcid\" value=\"\"/>\n" + "\n" +
            "        <input type=\"hidden\" name=\"is_sign\" value=\"Y\"/>\n" +
            "        <textarea name=\"signedData\" rows=\"5\" cols=\"50\">123</textarea>\n" +
            "        <input type=\"hidden\" name=\"real_sn\" value=\"\"/>\n" + "\n" +
            "        <input name=\"certCmdInput\" id='certCmdInput' type=\"hidden\" value=\"version=1&service=logonsignmac&refercode=4aa66070ec43b248b9d85007aa9849491521460350482&kcname=login&machinesns=00862c87e9833de8c5fe1914d49aa5fc9d%2600d8ba5eb7d31760f9fd2ad474348a9478&machineissuer=O%3DAlipay.com%20Corporation%2C%20OU%3DMachine%20CA%20Center%2C%20CN%3DAlipay.com%20Corporation%20Machine%20CA&userid=2088202440742366&usertype=M&title=&notice=&sign=b4fxRn3JIWFA8Zff2B33Gl1z6rzKb1ArJ4rRPiXUoTp5PeAiQmIAESOxQhlxMiBJQKUvQLa8iSLP%2BybCcJ3mIAne5VUPlu8x48G6XUWR9irdNxwkOjO5cFBhUuXj6djculLdWG%2FsvzQHMQD%2FHxUtz9rPEjmIBuhUbLoYETm9hPc%3D\"/>\n" +
            "        <input name=\"certCmdOutput\" id='certCmdOutput' type=\"hidden\" value=\"\"/>\n" +
            "        <input name=\"certfg\" type=\"hidden\" value=\"\"/>\n" + "        <input name=\"goto\" type=\"hidden\" value=\"\">\n" + "\n" +
            "    \t<input type=\"hidden\" name=\"security_chrome_extension_aliedit_installed\" value=\"\"/>\n" +
            "        <input type=\"hidden\" name=\"security_chrome_extension_alicert_installed\" value=\"\"/>\n" +
            "        <input type=\"hidden\" name=\"security_activeX_enabled\" value=\"\"/>\n" + "\t</div>\n" +
            "    <span id=\"pwd_container\" class=\"alieditContainer\"></span>\n" + "</div>\n" + "\n" + "\n" + "\n" +
            "<script type=\"text/javascript\" charset=\"utf-8\" id=\"iTrusPTAScript\" src=\"https://a.alipayobjects.com/build/js/sc/itruscert.js?t=20110809\"></script>\n" +
            "\n" + " \n" + "\n" + "\n" + "\n" + "\n" + "            \n" +
            "    <script type=\"text/javascript\" charset=\"utf-8\" src=\"https://a.alipayobjects.com/static/ar/??alipay.light.base-1.8.js,alipay.light.page-1.11-sizzle.js,alipay.security.base-1.7.js,alipay.security.edit-1.12.js,alipay.security.cert-1.5.js,alipay.security.otp-1.0.js,alipay.security.mobile-1.1.js,alipay.security.core-1.1.js,alipay.security.utils.base64-1.0.js,alipay.security.utils.chromeExtension-1.1.js\"></script>\n" +
            "<script>\n" + "    light.has('page/products') || light.register('page/products');\n" +
            "    light.has('page/scProducts') || light.register('page/scProducts', light, []);\n" +
            "    alipay.security.utils.chromeExtension.setExtensionId('lapoiohkeidniicbalnfmakkbnpejgbi');\n" + "\n" + "</script>\n" + "\n" +
            "<script type=\"text/javascript\">\n" + "    (function(s){\n" + "        var form = light.node('form'),\n" +
            "            machineSns = '00862c87e9833de8c5fe1914d49aa5fc9d&00d8ba5eb7d31760f9fd2ad474348a9478',\n" + "            sn = '',\n" +
            "            machineIssuer = '',\n" + "            randomValue = 'LOGONDATA:85f836e4f8eb13886395d16b038bad55',\n" +
            "            ua = light.client.info,\n" + "            icbcIssuer = \"\",\n" + "            icbcBlurSubject = \"\",\n" +
            "            checkedEdit = false,\n" + "            checkedCert = false,\n" + "            areadySendInfoToSC = false;\n" + "\n" +
            "        form.field('security_activeX_enabled', alipay.security.activeXEnabled);\n" + "\n" + "        \n" +
            "        var signMachineCertByCdo = function() {\n" + "                light.page.needCheckScProd ++;\n" +
            "                s.cdo.handlers['*'] = s.cdo.handlers[12004] = function() {\n" + "                    light.page.checkedScProd ++;\n" +
            "                    checkedCert = true;\n" + "                };\n" + "                s.create(s.cdo).render(function () {\n" + "\n" +
            "                                            this.execute(document.getElementById('certCmdInput').value, function (obj) {\n" +
            "                            form.field('certCmdOutput', obj.rawData);\n" + "                            checkedCert = true;\n" +
            "                            sendInfoToSC(function() {\n" + "                                light.page.checkedScProd++;\n" +
            "                            });\n" + "                        });\n" + "                    \n" + "                });\n" +
            "            },\n" + "            signMachineCertByPta = function () {\n" + "                light.page.needCheckScProd++;\n" +
            "                s.create(s.pta).render(function () {\n" + "                    var sns = machineSns.split('&');\n" + "\n" +
            "                    for (var i = 0, l = sns.length; i < l; i++) {\n" +
            "                        var signData = this.sign(randomValue, sns[i], machineIssuer);\n" + "                        if (signData) {\n" +
            "                            form.field('signedData', signData).field('real_sn', sns[i]);\n" + "                            break;\n" +
            "                        }\n" + "                    }\n" + "                    checkedCert = true;\n" +
            "                    sendInfoToSC(function() {\n" + "                        light.page.checkedScProd++;\n" +
            "                    });\n" + "                });\n" + "            },\n" + "            signUserCert = function () {\n" +
            "                light.page.needCheckScProd++;\n" + "                s.create(s.pta).render(function () {\n" +
            "                    var signData = this.sign(randomValue, sn, icbcIssuer, '', icbcBlurSubject);\n" +
            "                    form.field('signedData', signData);\n" + "                    checkedCert = true;\n" +
            "                    sendInfoToSC(function() {\n" + "                        light.page.checkedScProd++;\n" +
            "                    });\n" + "                });\n" + "            },\n" + "            checkCert = function () {\n" +
            "                if (machineSns) {\n" + "                    if (s.cdo.installed) {\n" +
            "                        signMachineCertByCdo(); //数字证书by新控件\n" + "                    } else if (s.pta.installed) {\n" +
            "                        signMachineCertByPta(); //数字证书by老控件\n" + "                    }\n" +
            "                } else if (ua.engine.trident && sn && s.pta.installed) {\n" + "                    signUserCert(); //支付盾&U盾\n" +
            "                }\n" + "            },\n" + "            checkEdit = function () {\n" + "                if (!s.edit.installed) {\n" +
            "                    checkedEdit = true;\n" + "                    return;\n" + "                }\n" +
            "                light.page.needCheckScProd++;\n" + "                s.create(s.edit, {\n" +
            "                    container: 'pwd_container',\n" + "                    id: 'edit_pwd',\n" +
            "                    name: 'edit_pwd',\n" + "                    width: '1',\n" + "                    height: '1',\n" +
            "                    passwordMode: '1'\n" + "                }).render(function () {\n" +
            "                            form.field('_seaside_gogo_', this.getCi1()).field('_seaside_gogo_p', this.getCi2());\n" +
            "                            checkedEdit = true;\n" + "                            sendInfoToSC(function() {\n" +
            "                                light.page.checkedScProd++;\n" + "                            });\n" + "                        });\n" +
            "            },\n" + "            sendInfoToSC = function(callback){\n" +
            "                                    callback && callback();\n" + "                            };\n" + "\n" +
            "        light.page.checkSc = function () {\n" + "\n" + "            light.page.needCheckScProd = light.page.checkedScProd = 0;\n" +
            "\n" + "                        checkEdit();\n" + "            checkCert();\n" + "            \n" + "\n" + "\n" + "        }\n" + "\n" +
            "\t\t// -----  extension -----------\n" + "        function checkEditByExtension () {\n" + "            light.page.needCheckScProd++;\n" +
            "            var ex = alipay.security.utils.chromeExtension;\n" + "\n" + "            ex.checkControl(function (installed) {\n" + "\n" +
            "                if (installed) {\n" + "                    form.field('security_chrome_extension_aliedit_installed', 'true');\n" +
            "                    ex.execute({ command:'mac' }, function (response) {\n" +
            "                        if (typeof response === 'object' && typeof response.mac === 'string') {\n" +
            "                            if (/XOR_1_0{30}_/.test(response.mac) || response.mac.length === 344) {\n" +
            "                                form.field('_seaside_gogo_', response.mac);\n" + "                            }\n" +
            "                            light.track('certcheck-ex-mac-ok');\n" + "                        } else {\n" +
            "                            light.track('certcheck-ex-mac-err');\n" + "                        }\n" +
            "                        ex.execute( { command: 'ipproxy' }, function (response) {\n" +
            "                            if (response && response.ipproxy) {\n" +
            "                                form.field('_seaside_gogo_p', response.ipproxy);\n" + "                            }\n" +
            "                            checkedEdit = true;\n" +
            "                            sendInfoToSC(function () { light.page.checkedScProd++; });\n" + "                        } );\n" + "\n" +
            "                    });\n" + "                } else {\n" + "                    checkedEdit = true;\n" +
            "                    sendInfoToSC(function () { light.page.checkedScProd++; });\n" + "                }\n" + "\n" + "\n" + "\n" +
            "            });\n" + "        }\n" + "        function checkCertByExtension () {\n" + "\n" +
            "            light.page.needCheckScProd++;\n" + "            var ex = alipay.security.utils.chromeExtension;\n" + "\n" +
            "            ex.checkControl(function (installed) {\n" + "                if (installed) {\n" +
            "                    form.field('security_chrome_extension_alicert_installed', 'true')\n" +
            "                    ex.execute({ command: 'cert', input: document.getElementById('certCmdInput').value }, function (response) {\n" +
            "                        if (response && response.cert) {\n" +
            "                            form.field('certCmdOutput', response.cert);\n" +
            "                            light.track('certcheck-ex-cert-ok');\n" + "                        } else {\n" +
            "                            light.track('certcheck-ex-cert-err');\n" + "                        }\n" +
            "                        checkedCert = true;\n" + "                        sendInfoToSC(function() {\n" +
            "                            light.page.checkedScProd++;\n" + "                        });\n" + "                    });\n" +
            "                } else {\n" + "                    checkedCert = true;\n" +
            "                    sendInfoToSC(function () { light.page.checkedScProd++; });\n" + "                }\n" +
            "            }, 'alicert');\n" + "        }\n" + "        // -------  extension end ------------\n" + "\n" +
            "        window.machineSns = machineSns;\n" + "        window.sign_checkForm = function () {\n" + "            return true;\n" +
            "        };\n" + "        window.jsMain = light.page.checkSc;\n" +
            "        light.has('prod/security/cdo') || light.register('prod/security/cdo', light, s.cdo);\n" + "\n" + "    })(alipay.security);\n" +
            "</script>\n" + "\n" + "\n" + "\n" + "<!-- Powered by Alipay Security -->\n" +
            "<!-- FD:130:authcenter/login/renderCertCheckView.vm:END -->        <input type=\"hidden\" name=\"securityId\" value=\"web|cert_check|3ad19d58-fffc-4512-9027-5309c6941691RZ11\" />\n" +
            "</form>\n" + "<script type=\"text/javascript\">\n" + "    if (document.getElementById('useMutiplePolicy')) { // 使用新的安全前端动态检测策略\n" +
            "        alipay.security.Certcheck.onCheckComplete(function () {\n" + "            submit();\n" + "        });\n" + "    } else  {\n" +
            "        if (window.light && light.page) {\n" + "            light.page.checkSc();\n" + "        }\n" + "        execute();\n" +
            "    }\n" + "</script>\n" + "<script type=\"text/javascript\">\n" + "    if(window.top !== window.self){\n" + "        try {\n" +
            "            document.getElementById(\"J-iframe\").value = \"true\";\n" + "        } catch(e) {}\n" + "    }\n" + "</script>\n" + "\n" +
            "        <!-- FD:231:alipay/foot/copyright.vm:START --><!-- FD:231:alipay/foot/copyright.vm:2604:foot/copyright.schema:支付宝copyright:START -->\n" +
            "<style>\n" + ".copyright,.copyright a,.copyright a:hover{color:#808080;}\n" + "</style>\n" + "<div class=\"copyright\">\n" +
            "      <a href=\"https://fun.alipay.com/certificate/jyxkz.htm\" target=\"_blank\">ICP证：沪B2-20150087</a>\n" + "  </div>\n" +
            "<div class=\"server\" id=\"ServerNum\">\n" + "  authcenter-30-6095 &nbsp; 0b90041a1521460350466440311670_0\n" + "</div>\n" +
            "<!-- FD:231:alipay/foot/copyright.vm:2604:foot/copyright.schema:支付宝copyright:END --><!-- FD:231:alipay/foot/copyright.vm:END -->    \n" +
            "\t</div>\n" + "\t\t\t\t\t\t\t\t\t            \n" + "                \t\t\t\t\t\t</body>\n" + "</html>\n";

    @Test
    public void selectFirst() {

    }

    @Test
    public void selectAttributes() {
        List<Map<String, String>> list = JsoupXpathUtils.selectAttributes(pageContent, "//form/textarea[@name]");

        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void selectElements() {
        List<Element> elements = JsoupXpathUtils.selectElements(pageContent, "//form//input[@name]|//form//textarea[@name]");

        if (CollectionUtils.isNotEmpty(elements)) {
            for (Element element : elements) {
                System.out.println(element.attr("name") + ":" + element.val());
            }
        }
    }
}