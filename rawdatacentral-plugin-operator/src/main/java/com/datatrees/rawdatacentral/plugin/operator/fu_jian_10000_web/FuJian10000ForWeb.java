package com.datatrees.rawdatacentral.plugin.operator.fu_jian_10000_web;

import javax.script.Invocable;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.plugin.operator.common.LoginUtilsForChina10000Web;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 因web版个人信息不足，需从wap版查询 姓名、身份证、入网时间
 * Created by guimeichao on 17/9/19.
 */
public class FuJian10000ForWeb implements OperatorPluginService {

    private static final Logger                     logger         = LoggerFactory.getLogger(FuJian10000ForWeb.class);
    private static final String                     javaScript_wap
                                                                   = "CnZhciBiaVJhZGl4QmFzZSA9IDI7CnZhciBiaVJhZGl4Qml0cyA9IDE2Owp2YXIgYml0c1BlckRpZ2l0ID0gYmlSYWRpeEJpdHM7CnZhciBiaVJhZGl4ID0gMSA8PCAxNjsgLy8gPSAyXjE2ID0gNjU1MzYKdmFyIGJpSGFsZlJhZGl4ID0gYmlSYWRpeCA+Pj4gMTsKdmFyIGJpUmFkaXhTcXVhcmVkID0gYmlSYWRpeCAqIGJpUmFkaXg7CnZhciBtYXhEaWdpdFZhbCA9IGJpUmFkaXggLSAxOwp2YXIgbWF4SW50ZWdlciA9IDk5OTk5OTk5OTk5OTk5OTg7IAoKdmFyIG1heERpZ2l0czsKdmFyIFpFUk9fQVJSQVk7CnZhciBiaWdaZXJvLCBiaWdPbmU7CgpmdW5jdGlvbiBzZXRNYXhEaWdpdHModmFsdWUpCnsKCW1heERpZ2l0cyA9IHZhbHVlOwoJWkVST19BUlJBWSA9IG5ldyBBcnJheShtYXhEaWdpdHMpOwoJZm9yICh2YXIgaXphID0gMDsgaXphIDwgWkVST19BUlJBWS5sZW5ndGg7IGl6YSsrKSBaRVJPX0FSUkFZW2l6YV0gPSAwOwoJYmlnWmVybyA9IG5ldyBCaWdJbnQoKTsKCWJpZ09uZSA9IG5ldyBCaWdJbnQoKTsKCWJpZ09uZS5kaWdpdHNbMF0gPSAxOwp9CgpzZXRNYXhEaWdpdHMoMTI5KTsKCnZhciBkcGwxMCA9IDE1Owp2YXIgbHIxMCA9IGJpRnJvbU51bWJlcigxMDAwMDAwMDAwMDAwMDAwKTsKCmZ1bmN0aW9uIEJpZ0ludChmbGFnKQp7CglpZiAodHlwZW9mIGZsYWcgPT0gImJvb2xlYW4iICYmIGZsYWcgPT0gdHJ1ZSkgewoJCXRoaXMuZGlnaXRzID0gbnVsbDsKCX0KCWVsc2UgewoJCXRoaXMuZGlnaXRzID0gWkVST19BUlJBWS5zbGljZSgwKTsKCX0KCXRoaXMuaXNOZWcgPSBmYWxzZTsKfQoKZnVuY3Rpb24gYmlGcm9tRGVjaW1hbChzKQp7Cgl2YXIgaXNOZWcgPSBzLmNoYXJBdCgwKSA9PSAnLSc7Cgl2YXIgaSA9IGlzTmVnID8gMSA6IDA7Cgl2YXIgcmVzdWx0OwoJLy8gU2tpcCBsZWFkaW5nIHplcm9zLgoJd2hpbGUgKGkgPCBzLmxlbmd0aCAmJiBzLmNoYXJBdChpKSA9PSAnMCcpICsraTsKCWlmIChpID09IHMubGVuZ3RoKSB7CgkJcmVzdWx0ID0gbmV3IEJpZ0ludCgpOwoJfQoJZWxzZSB7CgkJdmFyIGRpZ2l0Q291bnQgPSBzLmxlbmd0aCAtIGk7CgkJdmFyIGZnbCA9IGRpZ2l0Q291bnQgJSBkcGwxMDsKCQlpZiAoZmdsID09IDApIGZnbCA9IGRwbDEwOwoJCXJlc3VsdCA9IGJpRnJvbU51bWJlcihOdW1iZXIocy5zdWJzdHIoaSwgZmdsKSkpOwoJCWkgKz0gZmdsOwoJCXdoaWxlIChpIDwgcy5sZW5ndGgpIHsKCQkJcmVzdWx0ID0gYmlBZGQoYmlNdWx0aXBseShyZXN1bHQsIGxyMTApLAoJCQkgICAgICAgICAgICAgICBiaUZyb21OdW1iZXIoTnVtYmVyKHMuc3Vic3RyKGksIGRwbDEwKSkpKTsKCQkJaSArPSBkcGwxMDsKCQl9CgkJcmVzdWx0LmlzTmVnID0gaXNOZWc7Cgl9CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaUNvcHkoYmkpCnsKCXZhciByZXN1bHQgPSBuZXcgQmlnSW50KHRydWUpOwoJcmVzdWx0LmRpZ2l0cyA9IGJpLmRpZ2l0cy5zbGljZSgwKTsKCXJlc3VsdC5pc05lZyA9IGJpLmlzTmVnOwoJcmV0dXJuIHJlc3VsdDsKfQoKZnVuY3Rpb24gYmlGcm9tTnVtYmVyKGkpCnsKCXZhciByZXN1bHQgPSBuZXcgQmlnSW50KCk7CglyZXN1bHQuaXNOZWcgPSBpIDwgMDsKCWkgPSBNYXRoLmFicyhpKTsKCXZhciBqID0gMDsKCXdoaWxlIChpID4gMCkgewoJCXJlc3VsdC5kaWdpdHNbaisrXSA9IGkgJiBtYXhEaWdpdFZhbDsKCQlpID0gTWF0aC5mbG9vcihpIC8gYmlSYWRpeCk7Cgl9CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiByZXZlcnNlU3RyKHMpCnsKCXZhciByZXN1bHQgPSAiIjsKCWZvciAodmFyIGkgPSBzLmxlbmd0aCAtIDE7IGkgPiAtMTsgLS1pKSB7CgkJcmVzdWx0ICs9IHMuY2hhckF0KGkpOwoJfQoJcmV0dXJuIHJlc3VsdDsKfQoKdmFyIGhleGF0cmlnZXNpbWFsVG9DaGFyID0gbmV3IEFycmF5KAogJzAnLCAnMScsICcyJywgJzMnLCAnNCcsICc1JywgJzYnLCAnNycsICc4JywgJzknLAogJ2EnLCAnYicsICdjJywgJ2QnLCAnZScsICdmJywgJ2cnLCAnaCcsICdpJywgJ2onLAogJ2snLCAnbCcsICdtJywgJ24nLCAnbycsICdwJywgJ3EnLCAncicsICdzJywgJ3QnLAogJ3UnLCAndicsICd3JywgJ3gnLCAneScsICd6JwopOwoKZnVuY3Rpb24gYmlUb1N0cmluZyh4LCByYWRpeCkKCS8vIDIgPD0gcmFkaXggPD0gMzYKewoJdmFyIGIgPSBuZXcgQmlnSW50KCk7CgliLmRpZ2l0c1swXSA9IHJhZGl4OwoJdmFyIHFyID0gYmlEaXZpZGVNb2R1bG8oeCwgYik7Cgl2YXIgcmVzdWx0ID0gaGV4YXRyaWdlc2ltYWxUb0NoYXJbcXJbMV0uZGlnaXRzWzBdXTsKCXdoaWxlIChiaUNvbXBhcmUocXJbMF0sIGJpZ1plcm8pID09IDEpIHsKCQlxciA9IGJpRGl2aWRlTW9kdWxvKHFyWzBdLCBiKTsKCQlkaWdpdCA9IHFyWzFdLmRpZ2l0c1swXTsKCQlyZXN1bHQgKz0gaGV4YXRyaWdlc2ltYWxUb0NoYXJbcXJbMV0uZGlnaXRzWzBdXTsKCX0KCXJldHVybiAoeC5pc05lZyA/ICItIiA6ICIiKSArIHJldmVyc2VTdHIocmVzdWx0KTsKfQoKZnVuY3Rpb24gYmlUb0RlY2ltYWwoeCkKewoJdmFyIGIgPSBuZXcgQmlnSW50KCk7CgliLmRpZ2l0c1swXSA9IDEwOwoJdmFyIHFyID0gYmlEaXZpZGVNb2R1bG8oeCwgYik7Cgl2YXIgcmVzdWx0ID0gU3RyaW5nKHFyWzFdLmRpZ2l0c1swXSk7Cgl3aGlsZSAoYmlDb21wYXJlKHFyWzBdLCBiaWdaZXJvKSA9PSAxKSB7CgkJcXIgPSBiaURpdmlkZU1vZHVsbyhxclswXSwgYik7CgkJcmVzdWx0ICs9IFN0cmluZyhxclsxXS5kaWdpdHNbMF0pOwoJfQoJcmV0dXJuICh4LmlzTmVnID8gIi0iIDogIiIpICsgcmV2ZXJzZVN0cihyZXN1bHQpOwp9Cgp2YXIgaGV4VG9DaGFyID0gbmV3IEFycmF5KCcwJywgJzEnLCAnMicsICczJywgJzQnLCAnNScsICc2JywgJzcnLCAnOCcsICc5JywKICAgICAgICAgICAgICAgICAgICAgICAgICAnYScsICdiJywgJ2MnLCAnZCcsICdlJywgJ2YnKTsKCmZ1bmN0aW9uIGRpZ2l0VG9IZXgobikKewoJdmFyIG1hc2sgPSAweGY7Cgl2YXIgcmVzdWx0ID0gIiI7Cglmb3IgKGkgPSAwOyBpIDwgNDsgKytpKSB7CgkJcmVzdWx0ICs9IGhleFRvQ2hhcltuICYgbWFza107CgkJbiA+Pj49IDQ7Cgl9CglyZXR1cm4gcmV2ZXJzZVN0cihyZXN1bHQpOwp9CgpmdW5jdGlvbiBiaVRvSGV4KHgpCnsKCXZhciByZXN1bHQgPSAiIjsKCXZhciBuID0gYmlIaWdoSW5kZXgoeCk7Cglmb3IgKHZhciBpID0gYmlIaWdoSW5kZXgoeCk7IGkgPiAtMTsgLS1pKSB7CgkJcmVzdWx0ICs9IGRpZ2l0VG9IZXgoeC5kaWdpdHNbaV0pOwoJfQoJcmV0dXJuIHJlc3VsdDsKfQoKZnVuY3Rpb24gY2hhclRvSGV4KGMpCnsKCXZhciBaRVJPID0gNDg7Cgl2YXIgTklORSA9IFpFUk8gKyA5OwoJdmFyIGxpdHRsZUEgPSA5NzsKCXZhciBsaXR0bGVaID0gbGl0dGxlQSArIDI1OwoJdmFyIGJpZ0EgPSA2NTsKCXZhciBiaWdaID0gNjUgKyAyNTsKCXZhciByZXN1bHQ7CgoJaWYgKGMgPj0gWkVSTyAmJiBjIDw9IE5JTkUpIHsKCQlyZXN1bHQgPSBjIC0gWkVSTzsKCX0gZWxzZSBpZiAoYyA+PSBiaWdBICYmIGMgPD0gYmlnWikgewoJCXJlc3VsdCA9IDEwICsgYyAtIGJpZ0E7Cgl9IGVsc2UgaWYgKGMgPj0gbGl0dGxlQSAmJiBjIDw9IGxpdHRsZVopIHsKCQlyZXN1bHQgPSAxMCArIGMgLSBsaXR0bGVBOwoJfSBlbHNlIHsKCQlyZXN1bHQgPSAwOwoJfQoJcmV0dXJuIHJlc3VsdDsKfQoKZnVuY3Rpb24gaGV4VG9EaWdpdChzKQp7Cgl2YXIgcmVzdWx0ID0gMDsKCXZhciBzbCA9IE1hdGgubWluKHMubGVuZ3RoLCA0KTsKCWZvciAodmFyIGkgPSAwOyBpIDwgc2w7ICsraSkgewoJCXJlc3VsdCA8PD0gNDsKCQlyZXN1bHQgfD0gY2hhclRvSGV4KHMuY2hhckNvZGVBdChpKSkKCX0KCXJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGJpRnJvbUhleChzKQp7Cgl2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOwoJdmFyIHNsID0gcy5sZW5ndGg7Cglmb3IgKHZhciBpID0gc2wsIGogPSAwOyBpID4gMDsgaSAtPSA0LCArK2opIHsKCQlyZXN1bHQuZGlnaXRzW2pdID0gaGV4VG9EaWdpdChzLnN1YnN0cihNYXRoLm1heChpIC0gNCwgMCksIE1hdGgubWluKGksIDQpKSk7Cgl9CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaUZyb21TdHJpbmcocywgcmFkaXgpCnsKCXZhciBpc05lZyA9IHMuY2hhckF0KDApID09ICctJzsKCXZhciBpc3RvcCA9IGlzTmVnID8gMSA6IDA7Cgl2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOwoJdmFyIHBsYWNlID0gbmV3IEJpZ0ludCgpOwoJcGxhY2UuZGlnaXRzWzBdID0gMTsgLy8gcmFkaXheMAoJZm9yICh2YXIgaSA9IHMubGVuZ3RoIC0gMTsgaSA+PSBpc3RvcDsgaS0tKSB7CgkJdmFyIGMgPSBzLmNoYXJDb2RlQXQoaSk7CgkJdmFyIGRpZ2l0ID0gY2hhclRvSGV4KGMpOwoJCXZhciBiaURpZ2l0ID0gYmlNdWx0aXBseURpZ2l0KHBsYWNlLCBkaWdpdCk7CgkJcmVzdWx0ID0gYmlBZGQocmVzdWx0LCBiaURpZ2l0KTsKCQlwbGFjZSA9IGJpTXVsdGlwbHlEaWdpdChwbGFjZSwgcmFkaXgpOwoJfQoJcmVzdWx0LmlzTmVnID0gaXNOZWc7CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaUR1bXAoYikKewoJcmV0dXJuIChiLmlzTmVnID8gIi0iIDogIiIpICsgYi5kaWdpdHMuam9pbigiICIpOwp9CgpmdW5jdGlvbiBiaUFkZCh4LCB5KQp7Cgl2YXIgcmVzdWx0OwoKCWlmICh4LmlzTmVnICE9IHkuaXNOZWcpIHsKCQl5LmlzTmVnID0gIXkuaXNOZWc7CgkJcmVzdWx0ID0gYmlTdWJ0cmFjdCh4LCB5KTsKCQl5LmlzTmVnID0gIXkuaXNOZWc7Cgl9CgllbHNlIHsKCQlyZXN1bHQgPSBuZXcgQmlnSW50KCk7CgkJdmFyIGMgPSAwOwoJCXZhciBuOwoJCWZvciAodmFyIGkgPSAwOyBpIDwgeC5kaWdpdHMubGVuZ3RoOyArK2kpIHsKCQkJbiA9IHguZGlnaXRzW2ldICsgeS5kaWdpdHNbaV0gKyBjOwoJCQlyZXN1bHQuZGlnaXRzW2ldID0gbiAlIGJpUmFkaXg7CgkJCWMgPSBOdW1iZXIobiA+PSBiaVJhZGl4KTsKCQl9CgkJcmVzdWx0LmlzTmVnID0geC5pc05lZzsKCX0KCXJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGJpU3VidHJhY3QoeCwgeSkKewoJdmFyIHJlc3VsdDsKCWlmICh4LmlzTmVnICE9IHkuaXNOZWcpIHsKCQl5LmlzTmVnID0gIXkuaXNOZWc7CgkJcmVzdWx0ID0gYmlBZGQoeCwgeSk7CgkJeS5pc05lZyA9ICF5LmlzTmVnOwoJfSBlbHNlIHsKCQlyZXN1bHQgPSBuZXcgQmlnSW50KCk7CgkJdmFyIG4sIGM7CgkJYyA9IDA7CgkJZm9yICh2YXIgaSA9IDA7IGkgPCB4LmRpZ2l0cy5sZW5ndGg7ICsraSkgewoJCQluID0geC5kaWdpdHNbaV0gLSB5LmRpZ2l0c1tpXSArIGM7CgkJCXJlc3VsdC5kaWdpdHNbaV0gPSBuICUgYmlSYWRpeDsKCQkJaWYgKHJlc3VsdC5kaWdpdHNbaV0gPCAwKSByZXN1bHQuZGlnaXRzW2ldICs9IGJpUmFkaXg7CgkJCWMgPSAwIC0gTnVtYmVyKG4gPCAwKTsKCQl9CgkJaWYgKGMgPT0gLTEpIHsKCQkJYyA9IDA7CgkJCWZvciAodmFyIGkgPSAwOyBpIDwgeC5kaWdpdHMubGVuZ3RoOyArK2kpIHsKCQkJCW4gPSAwIC0gcmVzdWx0LmRpZ2l0c1tpXSArIGM7CgkJCQlyZXN1bHQuZGlnaXRzW2ldID0gbiAlIGJpUmFkaXg7CgkJCQlpZiAocmVzdWx0LmRpZ2l0c1tpXSA8IDApIHJlc3VsdC5kaWdpdHNbaV0gKz0gYmlSYWRpeDsKCQkJCWMgPSAwIC0gTnVtYmVyKG4gPCAwKTsKCQkJfQoJCQlyZXN1bHQuaXNOZWcgPSAheC5pc05lZzsKCQl9IGVsc2UgewoJCQlyZXN1bHQuaXNOZWcgPSB4LmlzTmVnOwoJCX0KCX0KCXJldHVybiByZXN1bHQ7Cn0KCgpmdW5jdGlvbiBiaUhpZ2hJbmRleCh4KQp7Cgl2YXIgcmVzdWx0ID0geC5kaWdpdHMubGVuZ3RoIC0gMTsKCXdoaWxlIChyZXN1bHQgPiAwICYmIHguZGlnaXRzW3Jlc3VsdF0gPT0gMCkgLS1yZXN1bHQ7CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaU51bUJpdHMoeCkKewoJdmFyIG4gPSBiaUhpZ2hJbmRleCh4KTsKCXZhciBkID0geC5kaWdpdHNbbl07Cgl2YXIgbSA9IChuICsgMSkgKiBiaXRzUGVyRGlnaXQ7Cgl2YXIgcmVzdWx0OwoJZm9yIChyZXN1bHQgPSBtOyByZXN1bHQgPiBtIC0gYml0c1BlckRpZ2l0OyAtLXJlc3VsdCkgewoJCWlmICgoZCAmIDB4ODAwMCkgIT0gMCkgYnJlYWs7CgkJZCA8PD0gMTsKCX0KCXJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGJpTXVsdGlwbHkoeCwgeSkKewoJdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKCXZhciBjOwoJdmFyIG4gPSBiaUhpZ2hJbmRleCh4KTsKCXZhciB0ID0gYmlIaWdoSW5kZXgoeSk7Cgl2YXIgdSwgdXYsIGs7CgoJZm9yICh2YXIgaSA9IDA7IGkgPD0gdDsgKytpKSB7CgkJYyA9IDA7CgkJayA9IGk7CgkJZm9yIChqID0gMDsgaiA8PSBuOyArK2osICsraykgewoJCQl1diA9IHJlc3VsdC5kaWdpdHNba10gKyB4LmRpZ2l0c1tqXSAqIHkuZGlnaXRzW2ldICsgYzsKCQkJcmVzdWx0LmRpZ2l0c1trXSA9IHV2ICYgbWF4RGlnaXRWYWw7CgkJCWMgPSB1diA+Pj4gYmlSYWRpeEJpdHM7CgkJfQoJCXJlc3VsdC5kaWdpdHNbaSArIG4gKyAxXSA9IGM7Cgl9CglyZXN1bHQuaXNOZWcgPSB4LmlzTmVnICE9IHkuaXNOZWc7CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaU11bHRpcGx5RGlnaXQoeCwgeSkKewoJdmFyIG4sIGMsIHV2OwoKCXJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKCW4gPSBiaUhpZ2hJbmRleCh4KTsKCWMgPSAwOwoJZm9yICh2YXIgaiA9IDA7IGogPD0gbjsgKytqKSB7CgkJdXYgPSByZXN1bHQuZGlnaXRzW2pdICsgeC5kaWdpdHNbal0gKiB5ICsgYzsKCQlyZXN1bHQuZGlnaXRzW2pdID0gdXYgJiBtYXhEaWdpdFZhbDsKCQljID0gdXYgPj4+IGJpUmFkaXhCaXRzOwoJCS8vYyA9IE1hdGguZmxvb3IodXYgLyBiaVJhZGl4KTsKCX0KCXJlc3VsdC5kaWdpdHNbMSArIG5dID0gYzsKCXJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGFycmF5Q29weShzcmMsIHNyY1N0YXJ0LCBkZXN0LCBkZXN0U3RhcnQsIG4pCnsKCXZhciBtID0gTWF0aC5taW4oc3JjU3RhcnQgKyBuLCBzcmMubGVuZ3RoKTsKCWZvciAodmFyIGkgPSBzcmNTdGFydCwgaiA9IGRlc3RTdGFydDsgaSA8IG07ICsraSwgKytqKSB7CgkJZGVzdFtqXSA9IHNyY1tpXTsKCX0KfQoKdmFyIGhpZ2hCaXRNYXNrcyA9IG5ldyBBcnJheSgweDAwMDAsIDB4ODAwMCwgMHhDMDAwLCAweEUwMDAsIDB4RjAwMCwgMHhGODAwLAogICAgICAgICAgICAgICAgICAgICAgICAgICAgIDB4RkMwMCwgMHhGRTAwLCAweEZGMDAsIDB4RkY4MCwgMHhGRkMwLCAweEZGRTAsCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgMHhGRkYwLCAweEZGRjgsIDB4RkZGQywgMHhGRkZFLCAweEZGRkYpOwoKZnVuY3Rpb24gYmlTaGlmdExlZnQoeCwgbikKewoJdmFyIGRpZ2l0Q291bnQgPSBNYXRoLmZsb29yKG4gLyBiaXRzUGVyRGlnaXQpOwoJdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKCWFycmF5Q29weSh4LmRpZ2l0cywgMCwgcmVzdWx0LmRpZ2l0cywgZGlnaXRDb3VudCwKCSAgICAgICAgICByZXN1bHQuZGlnaXRzLmxlbmd0aCAtIGRpZ2l0Q291bnQpOwoJdmFyIGJpdHMgPSBuICUgYml0c1BlckRpZ2l0OwoJdmFyIHJpZ2h0Qml0cyA9IGJpdHNQZXJEaWdpdCAtIGJpdHM7Cglmb3IgKHZhciBpID0gcmVzdWx0LmRpZ2l0cy5sZW5ndGggLSAxLCBpMSA9IGkgLSAxOyBpID4gMDsgLS1pLCAtLWkxKSB7CgkJcmVzdWx0LmRpZ2l0c1tpXSA9ICgocmVzdWx0LmRpZ2l0c1tpXSA8PCBiaXRzKSAmIG1heERpZ2l0VmFsKSB8CgkJICAgICAgICAgICAgICAgICAgICgocmVzdWx0LmRpZ2l0c1tpMV0gJiBoaWdoQml0TWFza3NbYml0c10pID4+PgoJCSAgICAgICAgICAgICAgICAgICAgKHJpZ2h0Qml0cykpOwoJfQoJcmVzdWx0LmRpZ2l0c1swXSA9ICgocmVzdWx0LmRpZ2l0c1tpXSA8PCBiaXRzKSAmIG1heERpZ2l0VmFsKTsKCXJlc3VsdC5pc05lZyA9IHguaXNOZWc7CglyZXR1cm4gcmVzdWx0Owp9Cgp2YXIgbG93Qml0TWFza3MgPSBuZXcgQXJyYXkoMHgwMDAwLCAweDAwMDEsIDB4MDAwMywgMHgwMDA3LCAweDAwMEYsIDB4MDAxRiwKICAgICAgICAgICAgICAgICAgICAgICAgICAgIDB4MDAzRiwgMHgwMDdGLCAweDAwRkYsIDB4MDFGRiwgMHgwM0ZGLCAweDA3RkYsCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAweDBGRkYsIDB4MUZGRiwgMHgzRkZGLCAweDdGRkYsIDB4RkZGRik7CgpmdW5jdGlvbiBiaVNoaWZ0UmlnaHQoeCwgbikKewoJdmFyIGRpZ2l0Q291bnQgPSBNYXRoLmZsb29yKG4gLyBiaXRzUGVyRGlnaXQpOwoJdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKCWFycmF5Q29weSh4LmRpZ2l0cywgZGlnaXRDb3VudCwgcmVzdWx0LmRpZ2l0cywgMCwKCSAgICAgICAgICB4LmRpZ2l0cy5sZW5ndGggLSBkaWdpdENvdW50KTsKCXZhciBiaXRzID0gbiAlIGJpdHNQZXJEaWdpdDsKCXZhciBsZWZ0Qml0cyA9IGJpdHNQZXJEaWdpdCAtIGJpdHM7Cglmb3IgKHZhciBpID0gMCwgaTEgPSBpICsgMTsgaSA8IHJlc3VsdC5kaWdpdHMubGVuZ3RoIC0gMTsgKytpLCArK2kxKSB7CgkJcmVzdWx0LmRpZ2l0c1tpXSA9IChyZXN1bHQuZGlnaXRzW2ldID4+PiBiaXRzKSB8CgkJICAgICAgICAgICAgICAgICAgICgocmVzdWx0LmRpZ2l0c1tpMV0gJiBsb3dCaXRNYXNrc1tiaXRzXSkgPDwgbGVmdEJpdHMpOwoJfQoJcmVzdWx0LmRpZ2l0c1tyZXN1bHQuZGlnaXRzLmxlbmd0aCAtIDFdID4+Pj0gYml0czsKCXJlc3VsdC5pc05lZyA9IHguaXNOZWc7CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaU11bHRpcGx5QnlSYWRpeFBvd2VyKHgsIG4pCnsKCXZhciByZXN1bHQgPSBuZXcgQmlnSW50KCk7CglhcnJheUNvcHkoeC5kaWdpdHMsIDAsIHJlc3VsdC5kaWdpdHMsIG4sIHJlc3VsdC5kaWdpdHMubGVuZ3RoIC0gbik7CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaURpdmlkZUJ5UmFkaXhQb3dlcih4LCBuKQp7Cgl2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOwoJYXJyYXlDb3B5KHguZGlnaXRzLCBuLCByZXN1bHQuZGlnaXRzLCAwLCByZXN1bHQuZGlnaXRzLmxlbmd0aCAtIG4pOwoJcmV0dXJuIHJlc3VsdDsKfQoKZnVuY3Rpb24gYmlNb2R1bG9CeVJhZGl4UG93ZXIoeCwgbikKewoJdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKCWFycmF5Q29weSh4LmRpZ2l0cywgMCwgcmVzdWx0LmRpZ2l0cywgMCwgbik7CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaUNvbXBhcmUoeCwgeSkKewoJaWYgKHguaXNOZWcgIT0geS5pc05lZykgewoJCXJldHVybiAxIC0gMiAqIE51bWJlcih4LmlzTmVnKTsKCX0KCWZvciAodmFyIGkgPSB4LmRpZ2l0cy5sZW5ndGggLSAxOyBpID49IDA7IC0taSkgewoJCWlmICh4LmRpZ2l0c1tpXSAhPSB5LmRpZ2l0c1tpXSkgewoJCQlpZiAoeC5pc05lZykgewoJCQkJcmV0dXJuIDEgLSAyICogTnVtYmVyKHguZGlnaXRzW2ldID4geS5kaWdpdHNbaV0pOwoJCQl9IGVsc2UgewoJCQkJcmV0dXJuIDEgLSAyICogTnVtYmVyKHguZGlnaXRzW2ldIDwgeS5kaWdpdHNbaV0pOwoJCQl9CgkJfQoJfQoJcmV0dXJuIDA7Cn0KCmZ1bmN0aW9uIGJpRGl2aWRlTW9kdWxvKHgsIHkpCnsKCXZhciBuYiA9IGJpTnVtQml0cyh4KTsKCXZhciB0YiA9IGJpTnVtQml0cyh5KTsKCXZhciBvcmlnWUlzTmVnID0geS5pc05lZzsKCXZhciBxLCByOwoJaWYgKG5iIDwgdGIpIHsKCQkvLyB8eHwgPCB8eXwKCQlpZiAoeC5pc05lZykgewoJCQlxID0gYmlDb3B5KGJpZ09uZSk7CgkJCXEuaXNOZWcgPSAheS5pc05lZzsKCQkJeC5pc05lZyA9IGZhbHNlOwoJCQl5LmlzTmVnID0gZmFsc2U7CgkJCXIgPSBiaVN1YnRyYWN0KHksIHgpOwoJCQkvLyBSZXN0b3JlIHNpZ25zLCAnY2F1c2UgdGhleSdyZSByZWZlcmVuY2VzLgoJCQl4LmlzTmVnID0gdHJ1ZTsKCQkJeS5pc05lZyA9IG9yaWdZSXNOZWc7CgkJfSBlbHNlIHsKCQkJcSA9IG5ldyBCaWdJbnQoKTsKCQkJciA9IGJpQ29weSh4KTsKCQl9CgkJcmV0dXJuIG5ldyBBcnJheShxLCByKTsKCX0KCglxID0gbmV3IEJpZ0ludCgpOwoJciA9IHg7CgoJdmFyIHQgPSBNYXRoLmNlaWwodGIgLyBiaXRzUGVyRGlnaXQpIC0gMTsKCXZhciBsYW1iZGEgPSAwOwoJd2hpbGUgKHkuZGlnaXRzW3RdIDwgYmlIYWxmUmFkaXgpIHsKCQl5ID0gYmlTaGlmdExlZnQoeSwgMSk7CgkJKytsYW1iZGE7CgkJKyt0YjsKCQl0ID0gTWF0aC5jZWlsKHRiIC8gYml0c1BlckRpZ2l0KSAtIDE7Cgl9CglyID0gYmlTaGlmdExlZnQociwgbGFtYmRhKTsKCW5iICs9IGxhbWJkYTsgLy8gVXBkYXRlIHRoZSBiaXQgY291bnQgZm9yIHguCgl2YXIgbiA9IE1hdGguY2VpbChuYiAvIGJpdHNQZXJEaWdpdCkgLSAxOwoKCXZhciBiID0gYmlNdWx0aXBseUJ5UmFkaXhQb3dlcih5LCBuIC0gdCk7Cgl3aGlsZSAoYmlDb21wYXJlKHIsIGIpICE9IC0xKSB7CgkJKytxLmRpZ2l0c1tuIC0gdF07CgkJciA9IGJpU3VidHJhY3QociwgYik7Cgl9Cglmb3IgKHZhciBpID0gbjsgaSA+IHQ7IC0taSkgewogICAgdmFyIHJpID0gKGkgPj0gci5kaWdpdHMubGVuZ3RoKSA/IDAgOiByLmRpZ2l0c1tpXTsKICAgIHZhciByaTEgPSAoaSAtIDEgPj0gci5kaWdpdHMubGVuZ3RoKSA/IDAgOiByLmRpZ2l0c1tpIC0gMV07CiAgICB2YXIgcmkyID0gKGkgLSAyID49IHIuZGlnaXRzLmxlbmd0aCkgPyAwIDogci5kaWdpdHNbaSAtIDJdOwogICAgdmFyIHl0ID0gKHQgPj0geS5kaWdpdHMubGVuZ3RoKSA/IDAgOiB5LmRpZ2l0c1t0XTsKICAgIHZhciB5dDEgPSAodCAtIDEgPj0geS5kaWdpdHMubGVuZ3RoKSA/IDAgOiB5LmRpZ2l0c1t0IC0gMV07CgkJaWYgKHJpID09IHl0KSB7CgkJCXEuZGlnaXRzW2kgLSB0IC0gMV0gPSBtYXhEaWdpdFZhbDsKCQl9IGVsc2UgewoJCQlxLmRpZ2l0c1tpIC0gdCAtIDFdID0gTWF0aC5mbG9vcigocmkgKiBiaVJhZGl4ICsgcmkxKSAvIHl0KTsKCQl9CgoJCXZhciBjMSA9IHEuZGlnaXRzW2kgLSB0IC0gMV0gKiAoKHl0ICogYmlSYWRpeCkgKyB5dDEpOwoJCXZhciBjMiA9IChyaSAqIGJpUmFkaXhTcXVhcmVkKSArICgocmkxICogYmlSYWRpeCkgKyByaTIpOwoJCXdoaWxlIChjMSA+IGMyKSB7CgkJCS0tcS5kaWdpdHNbaSAtIHQgLSAxXTsKCQkJYzEgPSBxLmRpZ2l0c1tpIC0gdCAtIDFdICogKCh5dCAqIGJpUmFkaXgpIHwgeXQxKTsKCQkJYzIgPSAocmkgKiBiaVJhZGl4ICogYmlSYWRpeCkgKyAoKHJpMSAqIGJpUmFkaXgpICsgcmkyKTsKCQl9CgoJCWIgPSBiaU11bHRpcGx5QnlSYWRpeFBvd2VyKHksIGkgLSB0IC0gMSk7CgkJciA9IGJpU3VidHJhY3QociwgYmlNdWx0aXBseURpZ2l0KGIsIHEuZGlnaXRzW2kgLSB0IC0gMV0pKTsKCQlpZiAoci5pc05lZykgewoJCQlyID0gYmlBZGQociwgYik7CgkJCS0tcS5kaWdpdHNbaSAtIHQgLSAxXTsKCQl9Cgl9CglyID0gYmlTaGlmdFJpZ2h0KHIsIGxhbWJkYSk7CgkvLyBGaWRkbGUgd2l0aCB0aGUgc2lnbnMgYW5kIHN0dWZmIHRvIG1ha2Ugc3VyZSB0aGF0IDAgPD0gciA8IHkuCglxLmlzTmVnID0geC5pc05lZyAhPSBvcmlnWUlzTmVnOwoJaWYgKHguaXNOZWcpIHsKCQlpZiAob3JpZ1lJc05lZykgewoJCQlxID0gYmlBZGQocSwgYmlnT25lKTsKCQl9IGVsc2UgewoJCQlxID0gYmlTdWJ0cmFjdChxLCBiaWdPbmUpOwoJCX0KCQl5ID0gYmlTaGlmdFJpZ2h0KHksIGxhbWJkYSk7CgkJciA9IGJpU3VidHJhY3QoeSwgcik7Cgl9CgkvLyBDaGVjayBmb3IgdGhlIHVuYmVsaWV2YWJseSBzdHVwaWQgZGVnZW5lcmF0ZSBjYXNlIG9mIHIgPT0gLTAuCglpZiAoci5kaWdpdHNbMF0gPT0gMCAmJiBiaUhpZ2hJbmRleChyKSA9PSAwKSByLmlzTmVnID0gZmFsc2U7CgoJcmV0dXJuIG5ldyBBcnJheShxLCByKTsKfQoKZnVuY3Rpb24gYmlEaXZpZGUoeCwgeSkKewoJcmV0dXJuIGJpRGl2aWRlTW9kdWxvKHgsIHkpWzBdOwp9CgpmdW5jdGlvbiBiaU1vZHVsbyh4LCB5KQp7CglyZXR1cm4gYmlEaXZpZGVNb2R1bG8oeCwgeSlbMV07Cn0KCmZ1bmN0aW9uIGJpTXVsdGlwbHlNb2QoeCwgeSwgbSkKewoJcmV0dXJuIGJpTW9kdWxvKGJpTXVsdGlwbHkoeCwgeSksIG0pOwp9CgpmdW5jdGlvbiBiaVBvdyh4LCB5KQp7Cgl2YXIgcmVzdWx0ID0gYmlnT25lOwoJdmFyIGEgPSB4OwoJd2hpbGUgKHRydWUpIHsKCQlpZiAoKHkgJiAxKSAhPSAwKSByZXN1bHQgPSBiaU11bHRpcGx5KHJlc3VsdCwgYSk7CgkJeSA+Pj0gMTsKCQlpZiAoeSA9PSAwKSBicmVhazsKCQlhID0gYmlNdWx0aXBseShhLCBhKTsKCX0KCXJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGJpUG93TW9kKHgsIHksIG0pCnsKCXZhciByZXN1bHQgPSBiaWdPbmU7Cgl2YXIgYSA9IHg7Cgl2YXIgayA9IHk7Cgl3aGlsZSAodHJ1ZSkgewoJCWlmICgoay5kaWdpdHNbMF0gJiAxKSAhPSAwKSByZXN1bHQgPSBiaU11bHRpcGx5TW9kKHJlc3VsdCwgYSwgbSk7CgkJayA9IGJpU2hpZnRSaWdodChrLCAxKTsKCQlpZiAoay5kaWdpdHNbMF0gPT0gMCAmJiBiaUhpZ2hJbmRleChrKSA9PSAwKSBicmVhazsKCQlhID0gYmlNdWx0aXBseU1vZChhLCBhLCBtKTsKCX0KCXJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIEJhcnJldHRNdShtKQp7Cgl0aGlzLm1vZHVsdXMgPSBiaUNvcHkobSk7Cgl0aGlzLmsgPSBiaUhpZ2hJbmRleCh0aGlzLm1vZHVsdXMpICsgMTsKCXZhciBiMmsgPSBuZXcgQmlnSW50KCk7CgliMmsuZGlnaXRzWzIgKiB0aGlzLmtdID0gMTsgLy8gYjJrID0gYl4oMmspCgl0aGlzLm11ID0gYmlEaXZpZGUoYjJrLCB0aGlzLm1vZHVsdXMpOwoJdGhpcy5ia3BsdXMxID0gbmV3IEJpZ0ludCgpOwoJdGhpcy5ia3BsdXMxLmRpZ2l0c1t0aGlzLmsgKyAxXSA9IDE7IC8vIGJrcGx1czEgPSBiXihrKzEpCgl0aGlzLm1vZHVsbyA9IEJhcnJldHRNdV9tb2R1bG87Cgl0aGlzLm11bHRpcGx5TW9kID0gQmFycmV0dE11X211bHRpcGx5TW9kOwoJdGhpcy5wb3dNb2QgPSBCYXJyZXR0TXVfcG93TW9kOwp9CgpmdW5jdGlvbiBCYXJyZXR0TXVfbW9kdWxvKHgpCnsKCXZhciBxMSA9IGJpRGl2aWRlQnlSYWRpeFBvd2VyKHgsIHRoaXMuayAtIDEpOwoJdmFyIHEyID0gYmlNdWx0aXBseShxMSwgdGhpcy5tdSk7Cgl2YXIgcTMgPSBiaURpdmlkZUJ5UmFkaXhQb3dlcihxMiwgdGhpcy5rICsgMSk7Cgl2YXIgcjEgPSBiaU1vZHVsb0J5UmFkaXhQb3dlcih4LCB0aGlzLmsgKyAxKTsKCXZhciByMnRlcm0gPSBiaU11bHRpcGx5KHEzLCB0aGlzLm1vZHVsdXMpOwoJdmFyIHIyID0gYmlNb2R1bG9CeVJhZGl4UG93ZXIocjJ0ZXJtLCB0aGlzLmsgKyAxKTsKCXZhciByID0gYmlTdWJ0cmFjdChyMSwgcjIpOwoJaWYgKHIuaXNOZWcpIHsKCQlyID0gYmlBZGQociwgdGhpcy5ia3BsdXMxKTsKCX0KCXZhciByZ3RlbSA9IGJpQ29tcGFyZShyLCB0aGlzLm1vZHVsdXMpID49IDA7Cgl3aGlsZSAocmd0ZW0pIHsKCQlyID0gYmlTdWJ0cmFjdChyLCB0aGlzLm1vZHVsdXMpOwoJCXJndGVtID0gYmlDb21wYXJlKHIsIHRoaXMubW9kdWx1cykgPj0gMDsKCX0KCXJldHVybiByOwp9CgpmdW5jdGlvbiBCYXJyZXR0TXVfbXVsdGlwbHlNb2QoeCwgeSkKewoJdmFyIHh5ID0gYmlNdWx0aXBseSh4LCB5KTsKCXJldHVybiB0aGlzLm1vZHVsbyh4eSk7Cn0KCmZ1bmN0aW9uIEJhcnJldHRNdV9wb3dNb2QoeCwgeSkKewoJdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKCXJlc3VsdC5kaWdpdHNbMF0gPSAxOwoJdmFyIGEgPSB4OwoJdmFyIGsgPSB5OwoJd2hpbGUgKHRydWUpIHsKCQlpZiAoKGsuZGlnaXRzWzBdICYgMSkgIT0gMCkgcmVzdWx0ID0gdGhpcy5tdWx0aXBseU1vZChyZXN1bHQsIGEpOwoJCWsgPSBiaVNoaWZ0UmlnaHQoaywgMSk7CgkJaWYgKGsuZGlnaXRzWzBdID09IDAgJiYgYmlIaWdoSW5kZXgoaykgPT0gMCkgYnJlYWs7CgkJYSA9IHRoaXMubXVsdGlwbHlNb2QoYSwgYSk7Cgl9CglyZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBSU0FLZXlQYWlyKGVuY3J5cHRpb25FeHBvbmVudCwgZGVjcnlwdGlvbkV4cG9uZW50LCBtb2R1bHVzKQp7Cgl0aGlzLmUgPSBiaUZyb21IZXgoZW5jcnlwdGlvbkV4cG9uZW50KTsKCXRoaXMuZCA9IGJpRnJvbUhleChkZWNyeXB0aW9uRXhwb25lbnQpOwoJdGhpcy5tID0gYmlGcm9tSGV4KG1vZHVsdXMpOwoJdGhpcy5jaHVua1NpemUgPSAyICogYmlIaWdoSW5kZXgodGhpcy5tKTsKCXRoaXMucmFkaXggPSAxNjsKCXRoaXMuYmFycmV0dCA9IG5ldyBCYXJyZXR0TXUodGhpcy5tKTsKfQoKZnVuY3Rpb24gdHdvRGlnaXQobikKewoJcmV0dXJuIChuIDwgMTAgPyAiMCIgOiAiIikgKyBTdHJpbmcobik7Cn0KCmZ1bmN0aW9uIGJvZHlSU0EoKSAgIAp7ICAgCgkgICAgc2V0TWF4RGlnaXRzKDEzMCk7ICAgCgkgICAgcmV0dXJuIG5ldyBSU0FLZXlQYWlyKCIxMDAwMSIsIiIsImE1YWViOGM2MzZlZjFmZGE1YTdhMTdhMjgxOWU1MWUxZWE2ZTBjY2ViMjRiOTU1NzRhZTAyNjUzNjI0MzUyNGYzMjI4MDdkZjI1MzFhNDIxMzkzODk2NzQ1NDVmNGM1OTZkYjE2MmY2ZTZiYmIyNjQ5OGJhYWIwNzRjMDM2Nzc3Iik7Cn0KCmZ1bmN0aW9uIGVuY3J5cHRlZFN0cmluZyhtb2R1bHVzLHMpCnsKICAgIHZhciBrZXkgPSAgbmV3IFJTQUtleVBhaXIoIjAxMDAwMSIsIiIsbW9kdWx1cyk7IAoKCXZhciBhID0gbmV3IEFycmF5KCk7Cgl2YXIgc2wgPSBzLmxlbmd0aDsKCXZhciBpID0gMDsKCXdoaWxlIChpIDwgc2wpIHsKCQlhW2ldID0gcy5jaGFyQ29kZUF0KGkpOwoJCWkrKzsKCX0KCgl3aGlsZSAoYS5sZW5ndGggJSBrZXkuY2h1bmtTaXplICE9IDApIHsKCQlhW2krK10gPSAwOwoJfQoKCXZhciBhbCA9IGEubGVuZ3RoOwoJdmFyIHJlc3VsdCA9ICIiOwoJdmFyIGosIGssIGJsb2NrOwoJZm9yIChpID0gMDsgaSA8IGFsOyBpICs9IGtleS5jaHVua1NpemUpIHsKCQlibG9jayA9IG5ldyBCaWdJbnQoKTsKCQlqID0gMDsKCQlmb3IgKGsgPSBpOyBrIDwgaSArIGtleS5jaHVua1NpemU7ICsraikgewoJCQlibG9jay5kaWdpdHNbal0gPSBhW2srK107CgkJCWJsb2NrLmRpZ2l0c1tqXSArPSBhW2srK10gPDwgODsKCQl9CgkJdmFyIGNyeXB0ID0ga2V5LmJhcnJldHQucG93TW9kKGJsb2NrLCBrZXkuZSk7CgkJdmFyIHRleHQgPSBrZXkucmFkaXggPT0gMTYgPyBiaVRvSGV4KGNyeXB0KSA6IGJpVG9TdHJpbmcoY3J5cHQsIGtleS5yYWRpeCk7CgkJcmVzdWx0ICs9IHRleHQgKyAiICI7Cgl9CglyZXR1cm4gcmVzdWx0LnN1YnN0cmluZygwLCByZXN1bHQubGVuZ3RoIC0gMSk7IC8vIFJlbW92ZSBsYXN0IHNwYWNlLgp9CgpmdW5jdGlvbiBkZWNyeXB0ZWRTdHJpbmcocykKewoJZnVuY3Rpb24gYm9keVJTQSgpICAgCgl7ICAgCgkgICAgc2V0TWF4RGlnaXRzKDEzMCk7ICAgCgkgICAgcmV0dXJuIG5ldyBSU0FLZXlQYWlyKCIxMDAwMSIsIiIsImE1YWViOGM2MzZlZjFmZGE1YTdhMTdhMjgxOWU1MWUxZWE2ZTBjY2ViMjRiOTU1NzRhZTAyNjUzNjI0MzUyNGYzMjI4MDdkZjI1MzFhNDIxMzkzODk2NzQ1NDVmNGM1OTZkYjE2MmY2ZTZiYmIyNjQ5OGJhYWIwNzRjMDM2Nzc3Iik7ICAgIAoJfQoJdmFyIGtleSA9ICBib2R5UlNBKCk7IAoJdmFyIGJsb2NrcyA9IHMuc3BsaXQoIiAiKTsKCXZhciByZXN1bHQgPSAiIjsKCXZhciBpLCBqLCBibG9jazsKCWZvciAoaSA9IDA7IGkgPCBibG9ja3MubGVuZ3RoOyArK2kpIHsKCQl2YXIgYmk7CgkJaWYgKGtleS5yYWRpeCA9PSAxNikgewoJCQliaSA9IGJpRnJvbUhleChibG9ja3NbaV0pOwoJCX0KCQllbHNlIHsKCQkJYmkgPSBiaUZyb21TdHJpbmcoYmxvY2tzW2ldLCBrZXkucmFkaXgpOwoJCX0KCQlibG9jayA9IGtleS5iYXJyZXR0LnBvd01vZChiaSwga2V5LmQpOwoJCWZvciAoaiA9IDA7IGogPD0gYmlIaWdoSW5kZXgoYmxvY2spOyArK2opIHsKCQkJcmVzdWx0ICs9IFN0cmluZy5mcm9tQ2hhckNvZGUoYmxvY2suZGlnaXRzW2pdICYgMjU1LAoJCQkgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBibG9jay5kaWdpdHNbal0gPj4gOCk7CgkJfQoJfQoJLy8gUmVtb3ZlIHRyYWlsaW5nIG51bGwsIGlmIGFueS4KCWlmIChyZXN1bHQuY2hhckNvZGVBdChyZXN1bHQubGVuZ3RoIC0gMSkgPT0gMCkgewoJCXJlc3VsdCA9IHJlc3VsdC5zdWJzdHJpbmcoMCwgcmVzdWx0Lmxlbmd0aCAtIDEpOwoJfQoJcmV0dXJuIHJlc3VsdDsKfQo=";
    private              LoginUtilsForChina10000Web loginUtils     = new LoginUtilsForChina10000Web();

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        return loginUtils.init(param);
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return loginUtils.refeshPicCode(param);
            case "QUERY_BASEINFO":
                return refeshPicCodeForBaseinfo(param);
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.VALIDATE_BILL_DETAIL:
                return refeshSmsCodeForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return submitForLogin(param);
            case "QUERY_BASEINFO":
                return submitForBaseinfo(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return submitForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<String> refeshPicCodeForBaseinfo(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://wapfj.189.cn/login/otherlogin.shtml";
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10000_web_002").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登录-->初始化失败,param={},response={}", param, response);
                return result.failure(ErrorCode.TASK_INIT_ERROR);
            }
            String servType = "50";
            String modulus
                    = "9417c1a4cfb44407cf42c3b7edf50260c89a7a2bc9fdef0c66da925c86bd5bc8787f02f91528cab222ba8b5d34eafd0bd4bd7a5f1dc49a828635a8a0add8d67b";
            String loginType = "2";
            String empoent = "10001";
            String barkUrl = StringUtils.EMPTY;
            List<String> servTypeList = XPathUtil.getXpath("//input[@name='wapInfo.servType']/@value", pageContent);
            if (!CollectionUtils.isEmpty(servTypeList)) {
                servType = servTypeList.get(0);
            }
            List<String> loginTypeList = XPathUtil.getXpath("//input[@name='wapInfo.loginType']/@value", pageContent);
            if (!CollectionUtils.isEmpty(loginTypeList)) {
                loginType = loginTypeList.get(0);
            }
            List<String> moduleList = XPathUtil.getXpath("//input[@name='module']/@value", pageContent);
            if (!CollectionUtils.isEmpty(moduleList)) {
                modulus = moduleList.get(0);
            }
            List<String> empoentList = XPathUtil.getXpath("//input[@name='empoent']/@value", pageContent);
            if (!CollectionUtils.isEmpty(empoentList)) {
                empoent = empoentList.get(0);
            }
            List<String> backURLList = XPathUtil.getXpath("//input[@name='backURL']/@value", pageContent);
            if (!CollectionUtils.isEmpty(backURLList)) {
                barkUrl = backURLList.get(0);
            }
            TaskUtils.addTaskShare(param.getTaskId(), "servType", servType);
            TaskUtils.addTaskShare(param.getTaskId(), "modulus", modulus);
            TaskUtils.addTaskShare(param.getTaskId(), "loginType", loginType);
            TaskUtils.addTaskShare(param.getTaskId(), "empoent", empoent);
            TaskUtils.addTaskShare(param.getTaskId(), "barkUrl", barkUrl);

            String referer = "http://wapfj.189.cn/login/otherlogin.shtml";
            templateUrl = "http://wapfj.189.cn/wapimagecode";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "fu_jian_10000_web_003")
                    .setFullUrl(templateUrl).setReferer(referer).invoke();
            logger.info("个人信息-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("个人信息-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBaseinfo(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            /**
             * 通过登录wap版获取 姓名、身份证、入网时间
             */
            String servType = TaskUtils.getTaskShare(param.getTaskId(), "servType");
            String loginType = TaskUtils.getTaskShare(param.getTaskId(), "loginType");
            String modulus = TaskUtils.getTaskShare(param.getTaskId(), "modulus");
            String empoent = TaskUtils.getTaskShare(param.getTaskId(), "empoent");
            String barkUrl = TaskUtils.getTaskShare(param.getTaskId(), "barkUrl");

            Invocable invocable = ScriptEngineUtil.createInvocableFromBase64(javaScript_wap);
            String encryptMobile = invocable.invokeFunction("encryptedString", modulus, param.getMobile().toString()).toString();
            String encryptPassword = invocable.invokeFunction("encryptedString", modulus, param.getPassword()).toString();

            String referer = "http://wapfj.189.cn/login/otherlogin.shtml";
            String templateUrl = "http://wapfj.189.cn/login/changenbrlogin.shtml";
            String templateData = "unGet=&wapInfo.servType={}&wapInfo.loginType={}&module={}&empoent={}&backURL={}&accnbr={}&mm={}&wapInfo" +
                    ".validationCode={}";
            String data = TemplateUtils
                    .format(templateData, servType, loginType, modulus, empoent, barkUrl, encryptMobile, encryptPassword, param.getPicCode());
            response = TaskHttpClient.create(param, RequestType.POST, "fu_jian_10000_web_004").setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).invoke();
            referer = "http://wapfj.189.cn/";
            templateUrl = "http://wapfj.189.cn/service/queryWapInfo.shtml";
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10000_web_005").setFullUrl(templateUrl).setReferer(referer).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, param.getMobile().toString())) {
                String customerName = PatternUtils.group(pageContent, "客户姓名：</strong>([^：]+)<br/", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "customerName", customerName);
                String idCardNo = PatternUtils.group(pageContent, "证件号码：</strong>([^：]+)<br/", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "idCardNo", idCardNo);
                String joinDate = PatternUtils.group(pageContent, "开通时间：</strong>([^：]+)<br/", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "joinDate", joinDate);
            } else {
                logger.error("个人信息-->校验失败,wap版登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
            logger.info("个人信息-->校验成功,param={}", param);
            return result.success();
        } catch (Exception e) {
            logger.error("个人信息-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            /**
             * 登录web版
             */
            result = loginUtils.submit(param);
            if (!result.getStatus()) {
                return result;
            }

            String referer = "http://www.189.cn/fj/";
            String templateUrl = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=01420648";
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10000_web_007").setFullUrl(templateUrl).setReferer(referer).invoke();

            referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=01420648";
            templateUrl = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10014&toStUrl=http://fj.189.cn/newcmsweb/commonIframe" +
                    ".jsp?URLPATH=/service/bill/realtime.jsp&fastcode=01420648&cityCode=fj";
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10000_web_008").setFullUrl(templateUrl).setReferer(referer).invoke();

            referer = "http://fj.189.cn/service/bill/realtime.jsp";
            templateUrl = "http://fj.189.cn/BillAjaxServlet.do?method=realtime&PRODNO={}&PRODTYPE=50";
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10000_web_009").setFullUrl(templateUrl, param.getMobile())
                    .setReferer(referer).invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.contains(pageContent, "获取话费成功") || StringUtils.contains(pageContent, "暂不提供数据")) {
                logger.info("登陆成功,param={}", param);
                return result.success();
            } else {
                logger.error("登陆失败,param={},pageContent={}", param, pageContent);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String cityCode = TaskUtils.getTaskContext(param.getTaskId(), "cityCode");
            String templateUrl = "http://fj.189.cn/service/bill/detail.jsp";
            String referer = "http://fj.189.cn/service/smdj/checkSmdj.jsp";
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10000_web_010").setFullUrl(templateUrl).setReferer(referer).invoke();

            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            String billMonth = sf.format(c.getTime());

            referer = "http://fj.189.cn/service/bill/detail.jsp";
            templateUrl = "http://fj.189.cn/service/bill/tanChu.jsp?PRODNO={}&PRODTYPE=50&CITYCODE={}&MONTH={}&SELTYPE=1";
            response = TaskHttpClient.create(param, RequestType.GET, "fu_jian_10000_web_011")
                    .setFullUrl(templateUrl, param.getMobile(), cityCode, billMonth).setReferer(referer).invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.isNotBlank(pageContent)) {
                cityCode = PatternUtils.group(pageContent, "id=\"CITYCODE\" value=\"([^\"]+)\"", 1);
            } else {
                cityCode = "0" + cityCode;
            }

            TaskUtils.addTaskShare(param.getTaskId(), "fullcityCode", cityCode);

            referer = templateUrl;
            templateUrl = "http://fj.189.cn/BUFFALO/buffalo/QueryAllAjax";
            String templateData = "<buffalo-call><method>getCDMASmsCode</method><map><type>java.util" +
                    ".HashMap</type><string>PHONENUM</string><string>{}</string><string>PRODUCTID</string><string>50</string><string>CITYCODE" +
                    "</string><string>{}</string><string>I_ISLIMIT</string><string>1</string><string>QUERYTYPE</string><string>BILL</string></map" +
                    "></buffalo-call>";
            String data = TemplateUtils.format(templateData, param.getMobile(), cityCode);
            response = TaskHttpClient.create(param, RequestType.POST, "fu_jian_10000_web_012").setFullUrl(templateUrl)
                    .setRequestBody(data, ContentType.TEXT_XML).setReferer(referer).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "短信随机密码已经发到您的手机")) {
                logger.info("详单-->短信验证码-->刷新成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            String billMonth = sf.format(c.getTime());

            String fullcityCode = TaskUtils.getTaskShare(param.getTaskId(), "fullcityCode");
            String puridID = "0";
            String emailEmpoent = "10001";
            String emailModule
                    = "863581c5892cdfe8a67b95c7abb47ead8b102e9620994ae95637f637fa22acac173b91015574507362816b30a884632d8562bf20de621d31d745291aaec7ca6f";
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("fu_jian_10000_web/des_wap.js");
            Invocable invocable = ScriptEngineUtil.createInvocableFromBase64(javaScript_wap);
            String encryptPassword = invocable.invokeFunction("encryptedString", emailModule, param.getPassword()).toString();

            String templateUrl = "http://fj.189.cn/service/bill/trans.jsp";
            String templateData = "PRODNO={}&PRODTYPE=50&CITYCODE={}&SELTYPE=1&MONTH={}&PURID={}&email_empoent={}&email_module={}&serPwd50" +
                    "={}&randomPwd={}";
            String data = TemplateUtils
                    .format(templateData, param.getMobile(), fullcityCode, billMonth, puridID, emailEmpoent, emailModule, encryptPassword,
                            param.getSmsCode());
            response = TaskHttpClient.create(param, RequestType.POST, "fu_jian_10000_web_013").setFullUrl(templateUrl).setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "暂无您所查询的数据清单") || StringUtils.contains(pageContent, "客户姓名")) {
                String encryptMobile = PatternUtils.group(pageContent, "PRODNO=([^\"=]+)=", 1);
                TaskUtils.addTaskShare(param.getTaskId(), "encryptMobile", encryptMobile);
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.error("详单-->校验失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }
}
