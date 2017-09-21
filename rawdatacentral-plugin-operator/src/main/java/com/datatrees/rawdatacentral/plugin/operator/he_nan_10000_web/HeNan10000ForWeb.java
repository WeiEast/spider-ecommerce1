package com.datatrees.rawdatacentral.plugin.operator.he_nan_10000_web;

import com.datatrees.common.util.PatternUtils;
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
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * User: yand
 * Date: 2017/9/21
 */
public class HeNan10000ForWeb implements OperatorPluginService {
    private static final Logger logger = LoggerFactory.getLogger(HeNan10000ForWeb.class);
    private static final String javaScript = "ZnVuY3Rpb24gZ2V0VXVpZCgpew0KCXZhciBDSEFSUyA9ICYjMzk7MDEyMzQ1Njc4OUFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXomIzM5Oy5zcGxpdCgmIzM5OyYjMzk7KTsNCgl2YXIgY2hhcnMgPSBDSEFSUywgdXVpZCA9IFtdLCBpLGxlbixyYWRpeDsNCiAgICByYWRpeCA9IHJhZGl4IHx8IGNoYXJzLmxlbmd0aDsNCiANCiAgICBpZiAobGVuKSB7DQogICAgICAvLyBDb21wYWN0IGZvcm0NCiAgICAgIGZvciAoaSA9IDA7IGkgJmx0OyBsZW47IGkrKykgdXVpZFtpXSA9IGNoYXJzWzAgfCBNYXRoLnJhbmRvbSgpKnJhZGl4XTsNCiAgICB9IGVsc2Ugew0KICAgICAgLy8gcmZjNDEyMiwgdmVyc2lvbiA0IGZvcm0NCiAgICAgIHZhciByOw0KIA0KICAgICAgLy8gcmZjNDEyMiByZXF1aXJlcyB0aGVzZSBjaGFyYWN0ZXJzDQogICAgICB1dWlkWzhdID0gdXVpZFsxM10gPSB1dWlkWzE4XSA9IHV1aWRbMjNdID0gJiMzOTstJiMzOTs7DQogICAgICB1dWlkWzE0XSA9ICYjMzk7NCYjMzk7Ow0KIA0KICAgICAgLy8gRmlsbCBpbiByYW5kb20gZGF0YS4gIEF0IGk9PTE5IHNldCB0aGUgaGlnaCBiaXRzIG9mIGNsb2NrIHNlcXVlbmNlIGFzDQogICAgICAvLyBwZXIgcmZjNDEyMiwgc2VjLiA0LjEuNQ0KICAgICAgZm9yIChpID0gMDsgaSAmbHQ7IDM2OyBpKyspIHsNCiAgICAgICAgaWYgKCF1dWlkW2ldKSB7DQogICAgICAgICAgciA9IDAgfCBNYXRoLnJhbmRvbSgpKjE2Ow0KICAgICAgICAgIHV1aWRbaV0gPSBjaGFyc1soaSA9PSAxOSkgPyAociAmIDB4MykgfCAweDggOiByXTsNCiAgICAgICAgfQ0KICAgICAgfQ0KICAgIH0NCiAgICByZXR1cm4gdXVpZC5qb2luKCYjMzk7JiMzOTspLnJlcGxhY2UobmV3IFJlZ0V4cCgvKC0pL2cpLCYjMzk7JiMzOTspOw0KfQ0KDQovKg0KKiBCaWdJbnQsIGEgc3VpdGUgb2Ygcm91dGluZXMgZm9yIHBlcmZvcm1pbmcgbXVsdGlwbGUtcHJlY2lzaW9uIGFyaXRobWV0aWMgaW4NCiogSmF2YVNjcmlwdC4NCiogQmFycmV0dE11LCBhIGNsYXNzIGZvciBwZXJmb3JtaW5nIEJhcnJldHQgbW9kdWxhciByZWR1Y3Rpb24gY29tcHV0YXRpb25zIGluDQoqIEphdmFTY3JpcHQuDQoqDQoqDQoqIENvcHlyaWdodCAxOTk4LTIwMDUgRGF2aWQgU2hhcGlyby4NCiogZGF2ZUBvaGRhdmUuY29tDQoqDQoqIGNoYW5nZWQgYW5kIGltcHJvdmVkIGJ5IERhbmllbCBHcmllc3Nlcg0KKiBodHRwOi8vd3d3LmpjcnlwdGlvbi5vcmcvDQoqIERhbmllbCBHcmllc3NlciAmbHQ7ZGFuaWVsLmdyaWVzc2VyQGpjcnlwdGlvbi5vcmcmZ3Q7DQoqLw0KdmFyIGJpUmFkaXhCYXNlID0gMjsNCnZhciBiaVJhZGl4Qml0cyA9IDE2Ow0KdmFyIGJpdHNQZXJEaWdpdCA9IGJpUmFkaXhCaXRzOw0KdmFyIGJpUmFkaXggPSAxICZsdDsmbHQ7IDE2Ow0KdmFyIGJpSGFsZlJhZGl4ID0gYmlSYWRpeCAmZ3Q7Jmd0OyZndDsgMTsNCnZhciBiaVJhZGl4U3F1YXJlZCA9IGJpUmFkaXggKiBiaVJhZGl4Ow0KdmFyIG1heERpZ2l0VmFsID0gYmlSYWRpeCAtIDE7DQp2YXIgbWF4SW50ZWdlciA9IDk5OTk5OTk5OTk5OTk5OTg7DQp2YXIgbWF4RGlnaXRzOw0KdmFyIFpFUk9fQVJSQVk7DQp2YXIgYmlnWmVybywgYmlnT25lOw0KdmFyIGRwbDEwID0gMTU7DQp2YXIgaGlnaEJpdE1hc2tzID0gbmV3IEFycmF5KDB4MDAwMCwgMHg4MDAwLCAweEMwMDAsIDB4RTAwMCwgMHhGMDAwLCAweEY4MDAsDQoweEZDMDAsIDB4RkUwMCwgMHhGRjAwLCAweEZGODAsIDB4RkZDMCwgMHhGRkUwLA0KMHhGRkYwLCAweEZGRjgsIDB4RkZGQywgMHhGRkZFLCAweEZGRkYpOw0KDQp2YXIgaGV4YXRyaWdlc2ltYWxUb0NoYXIgPSBuZXcgQXJyYXkoDQomIzM5OzAmIzM5OywgJiMzOTsxJiMzOTssICYjMzk7MiYjMzk7LCAmIzM5OzMmIzM5OywgJiMzOTs0JiMzOTssICYjMzk7NSYjMzk7LCAmIzM5OzYmIzM5OywgJiMzOTs3JiMzOTssICYjMzk7OCYjMzk7LCAmIzM5OzkmIzM5OywNCiYjMzk7YSYjMzk7LCAmIzM5O2ImIzM5OywgJiMzOTtjJiMzOTssICYjMzk7ZCYjMzk7LCAmIzM5O2UmIzM5OywgJiMzOTtmJiMzOTssICYjMzk7ZyYjMzk7LCAmIzM5O2gmIzM5OywgJiMzOTtpJiMzOTssICYjMzk7aiYjMzk7LA0KJiMzOTtrJiMzOTssICYjMzk7bCYjMzk7LCAmIzM5O20mIzM5OywgJiMzOTtuJiMzOTssICYjMzk7byYjMzk7LCAmIzM5O3AmIzM5OywgJiMzOTtxJiMzOTssICYjMzk7ciYjMzk7LCAmIzM5O3MmIzM5OywgJiMzOTt0JiMzOTssDQomIzM5O3UmIzM5OywgJiMzOTt2JiMzOTssICYjMzk7dyYjMzk7LCAmIzM5O3gmIzM5OywgJiMzOTt5JiMzOTssICYjMzk7eiYjMzk7DQopOw0KDQp2YXIgaGV4VG9DaGFyID0gbmV3IEFycmF5KCYjMzk7MCYjMzk7LCAmIzM5OzEmIzM5OywgJiMzOTsyJiMzOTssICYjMzk7MyYjMzk7LCAmIzM5OzQmIzM5OywgJiMzOTs1JiMzOTssICYjMzk7NiYjMzk7LCAmIzM5OzcmIzM5OywgJiMzOTs4JiMzOTssICYjMzk7OSYjMzk7LA0KJiMzOTthJiMzOTssICYjMzk7YiYjMzk7LCAmIzM5O2MmIzM5OywgJiMzOTtkJiMzOTssICYjMzk7ZSYjMzk7LCAmIzM5O2YmIzM5Oyk7DQoNCnZhciBsb3dCaXRNYXNrcyA9IG5ldyBBcnJheSgweDAwMDAsIDB4MDAwMSwgMHgwMDAzLCAweDAwMDcsIDB4MDAwRiwgMHgwMDFGLA0KMHgwMDNGLCAweDAwN0YsIDB4MDBGRiwgMHgwMUZGLCAweDAzRkYsIDB4MDdGRiwNCjB4MEZGRiwgMHgxRkZGLCAweDNGRkYsIDB4N0ZGRiwgMHhGRkZGKTsNCg0KZnVuY3Rpb24gc2V0TWF4RGlnaXRzKHZhbHVlKSB7DQoJbWF4RGlnaXRzID0gdmFsdWU7DQoJWkVST19BUlJBWSA9IG5ldyBBcnJheShtYXhEaWdpdHMpOw0KCWZvciAodmFyIGl6YSA9IDA7IGl6YSAmbHQ7IFpFUk9fQVJSQVkubGVuZ3RoOyBpemErKykgWkVST19BUlJBWVtpemFdID0gMDsNCgliaWdaZXJvID0gbmV3IEJpZ0ludCgpOw0KCWJpZ09uZSA9IG5ldyBCaWdJbnQoKTsNCgliaWdPbmUuZGlnaXRzWzBdID0gMTsNCn0NCg0KZnVuY3Rpb24gQmlnSW50KGZsYWcpIHsNCglpZiAodHlwZW9mIGZsYWcgPT0gImJvb2xlYW4iICYmIGZsYWcgPT0gdHJ1ZSkgew0KCQl0aGlzLmRpZ2l0cyA9IG51bGw7DQoJfQ0KCWVsc2Ugew0KCQl0aGlzLmRpZ2l0cyA9IFpFUk9fQVJSQVkuc2xpY2UoMCk7DQoJfQ0KCXRoaXMuaXNOZWcgPSBmYWxzZTsNCn0NCg0KZnVuY3Rpb24gYmlGcm9tRGVjaW1hbChzKSB7DQoJdmFyIGlzTmVnID0gcy5jaGFyQXQoMCkgPT0gJiMzOTstJiMzOTs7DQoJdmFyIGkgPSBpc05lZyA/IDEgOiAwOw0KCXZhciByZXN1bHQ7DQoJd2hpbGUgKGkgJmx0OyBzLmxlbmd0aCAmJiBzLmNoYXJBdChpKSA9PSAmIzM5OzAmIzM5OykgKytpOw0KCWlmIChpID09IHMubGVuZ3RoKSB7DQoJCXJlc3VsdCA9IG5ldyBCaWdJbnQoKTsNCgl9DQoJZWxzZSB7DQoJCXZhciBkaWdpdENvdW50ID0gcy5sZW5ndGggLSBpOw0KCQl2YXIgZmdsID0gZGlnaXRDb3VudCAlIGRwbDEwOw0KCQlpZiAoZmdsID09IDApIGZnbCA9IGRwbDEwOw0KCQlyZXN1bHQgPSBiaUZyb21OdW1iZXIoTnVtYmVyKHMuc3Vic3RyKGksIGZnbCkpKTsNCgkJaSArPSBmZ2w7DQoJCXdoaWxlIChpICZsdDsgcy5sZW5ndGgpIHsNCgkJCXJlc3VsdCA9IGJpQWRkKGJpTXVsdGlwbHkocmVzdWx0LCBiaUZyb21OdW1iZXIoMTAwMDAwMDAwMDAwMDAwMCkpLA0KCQkJYmlGcm9tTnVtYmVyKE51bWJlcihzLnN1YnN0cihpLCBkcGwxMCkpKSk7DQoJCQlpICs9IGRwbDEwOw0KCQl9DQoJCXJlc3VsdC5pc05lZyA9IGlzTmVnOw0KCX0NCglyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiBiaUNvcHkoYmkpIHsNCgl2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCh0cnVlKTsNCglyZXN1bHQuZGlnaXRzID0gYmkuZGlnaXRzLnNsaWNlKDApOw0KCXJlc3VsdC5pc05lZyA9IGJpLmlzTmVnOw0KCXJldHVybiByZXN1bHQ7DQp9DQoNCmZ1bmN0aW9uIGJpRnJvbU51bWJlcihpKSB7DQoJdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsNCglyZXN1bHQuaXNOZWcgPSBpICZsdDsgMDsNCglpID0gTWF0aC5hYnMoaSk7DQoJdmFyIGogPSAwOw0KCXdoaWxlIChpICZndDsgMCkgew0KCQlyZXN1bHQuZGlnaXRzW2orK10gPSBpICYgbWF4RGlnaXRWYWw7DQoJCWkgJmd0OyZndDs9IGJpUmFkaXhCaXRzOw0KCX0NCglyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiByZXZlcnNlU3RyKHMpIHsNCgl2YXIgcmVzdWx0ID0gIiI7DQoJZm9yICh2YXIgaSA9IHMubGVuZ3RoIC0gMTsgaSAmZ3Q7IC0xOyAtLWkpIHsNCgkJcmVzdWx0ICs9IHMuY2hhckF0KGkpOw0KCX0NCglyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiBiaVRvU3RyaW5nKHgsIHJhZGl4KSB7DQoJdmFyIGIgPSBuZXcgQmlnSW50KCk7DQoJYi5kaWdpdHNbMF0gPSByYWRpeDsNCgl2YXIgcXIgPSBiaURpdmlkZU1vZHVsbyh4LCBiKTsNCgl2YXIgcmVzdWx0ID0gaGV4YXRyaWdlc2ltYWxUb0NoYXJbcXJbMV0uZGlnaXRzWzBdXTsNCgl3aGlsZSAoYmlDb21wYXJlKHFyWzBdLCBiaWdaZXJvKSA9PSAxKSB7DQoJCXFyID0gYmlEaXZpZGVNb2R1bG8ocXJbMF0sIGIpOw0KCQlkaWdpdCA9IHFyWzFdLmRpZ2l0c1swXTsNCgkJcmVzdWx0ICs9IGhleGF0cmlnZXNpbWFsVG9DaGFyW3FyWzFdLmRpZ2l0c1swXV07DQoJfQ0KCXJldHVybiAoeC5pc05lZyA/ICItIiA6ICIiKSArIHJldmVyc2VTdHIocmVzdWx0KTsNCn0NCg0KZnVuY3Rpb24gYmlUb0RlY2ltYWwoeCkgew0KCXZhciBiID0gbmV3IEJpZ0ludCgpOw0KCWIuZGlnaXRzWzBdID0gMTA7DQoJdmFyIHFyID0gYmlEaXZpZGVNb2R1bG8oeCwgYik7DQoJdmFyIHJlc3VsdCA9IFN0cmluZyhxclsxXS5kaWdpdHNbMF0pOw0KCXdoaWxlIChiaUNvbXBhcmUocXJbMF0sIGJpZ1plcm8pID09IDEpIHsNCgkJcXIgPSBiaURpdmlkZU1vZHVsbyhxclswXSwgYik7DQoJCXJlc3VsdCArPSBTdHJpbmcocXJbMV0uZGlnaXRzWzBdKTsNCgl9DQoJcmV0dXJuICh4LmlzTmVnID8gIi0iIDogIiIpICsgcmV2ZXJzZVN0cihyZXN1bHQpOw0KfQ0KDQpmdW5jdGlvbiBkaWdpdFRvSGV4KG4pIHsNCgl2YXIgbWFzayA9IDB4ZjsNCgl2YXIgcmVzdWx0ID0gIiI7DQoJZm9yIChpID0gMDsgaSAmbHQ7IDQ7ICsraSkgew0KCQlyZXN1bHQgKz0gaGV4VG9DaGFyW24gJiBtYXNrXTsNCgkJbiAmZ3Q7Jmd0OyZndDs9IDQ7DQoJfQ0KCXJldHVybiByZXZlcnNlU3RyKHJlc3VsdCk7DQp9DQoNCmZ1bmN0aW9uIGJpVG9IZXgoeCkgew0KCXZhciByZXN1bHQgPSAiIjsNCgl2YXIgbiA9IGJpSGlnaEluZGV4KHgpOw0KCWZvciAodmFyIGkgPSBiaUhpZ2hJbmRleCh4KTsgaSAmZ3Q7IC0xOyAtLWkpIHsNCgkJcmVzdWx0ICs9IGRpZ2l0VG9IZXgoeC5kaWdpdHNbaV0pOw0KCX0NCglyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiBjaGFyVG9IZXgoYykgew0KCXZhciBaRVJPID0gNDg7DQoJdmFyIE5JTkUgPSBaRVJPICsgOTsNCgl2YXIgbGl0dGxlQSA9IDk3Ow0KCXZhciBsaXR0bGVaID0gbGl0dGxlQSArIDI1Ow0KCXZhciBiaWdBID0gNjU7DQoJdmFyIGJpZ1ogPSA2NSArIDI1Ow0KCXZhciByZXN1bHQ7DQoNCglpZiAoYyAmZ3Q7PSBaRVJPICYmIGMgJmx0Oz0gTklORSkgew0KCQlyZXN1bHQgPSBjIC0gWkVSTzsNCgl9IGVsc2UgaWYgKGMgJmd0Oz0gYmlnQSAmJiBjICZsdDs9IGJpZ1opIHsNCgkJcmVzdWx0ID0gMTAgKyBjIC0gYmlnQTsNCgl9IGVsc2UgaWYgKGMgJmd0Oz0gbGl0dGxlQSAmJiBjICZsdDs9IGxpdHRsZVopIHsNCgkJcmVzdWx0ID0gMTAgKyBjIC0gbGl0dGxlQTsNCgl9IGVsc2Ugew0KCQlyZXN1bHQgPSAwOw0KCX0NCglyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiBoZXhUb0RpZ2l0KHMpIHsNCgl2YXIgcmVzdWx0ID0gMDsNCgl2YXIgc2wgPSBNYXRoLm1pbihzLmxlbmd0aCwgNCk7DQoJZm9yICh2YXIgaSA9IDA7IGkgJmx0OyBzbDsgKytpKSB7DQoJCXJlc3VsdCAmbHQ7Jmx0Oz0gNDsNCgkJcmVzdWx0IHw9IGNoYXJUb0hleChzLmNoYXJDb2RlQXQoaSkpDQoJfQ0KCXJldHVybiByZXN1bHQ7DQp9DQoNCmZ1bmN0aW9uIGJpRnJvbUhleChzKSB7DQoJdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsNCgl2YXIgc2wgPSBzLmxlbmd0aDsNCglmb3IgKHZhciBpID0gc2wsIGogPSAwOyBpICZndDsgMDsgaSAtPSA0LCArK2opIHsNCgkJcmVzdWx0LmRpZ2l0c1tqXSA9IGhleFRvRGlnaXQocy5zdWJzdHIoTWF0aC5tYXgoaSAtIDQsIDApLCBNYXRoLm1pbihpLCA0KSkpOw0KCX0NCglyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiBiaUZyb21TdHJpbmcocywgcmFkaXgpIHsNCgl2YXIgaXNOZWcgPSBzLmNoYXJBdCgwKSA9PSAmIzM5Oy0mIzM5OzsNCgl2YXIgaXN0b3AgPSBpc05lZyA/IDEgOiAwOw0KCXZhciByZXN1bHQgPSBuZXcgQmlnSW50KCk7DQoJdmFyIHBsYWNlID0gbmV3IEJpZ0ludCgpOw0KCXBsYWNlLmRpZ2l0c1swXSA9IDE7IC8vIHJhZGl4XjANCglmb3IgKHZhciBpID0gcy5sZW5ndGggLSAxOyBpICZndDs9IGlzdG9wOyBpLS0pIHsNCgkJdmFyIGMgPSBzLmNoYXJDb2RlQXQoaSk7DQoJCXZhciBkaWdpdCA9IGNoYXJUb0hleChjKTsNCgkJdmFyIGJpRGlnaXQgPSBiaU11bHRpcGx5RGlnaXQocGxhY2UsIGRpZ2l0KTsNCgkJcmVzdWx0ID0gYmlBZGQocmVzdWx0LCBiaURpZ2l0KTsNCgkJcGxhY2UgPSBiaU11bHRpcGx5RGlnaXQocGxhY2UsIHJhZGl4KTsNCgl9DQoJcmVzdWx0LmlzTmVnID0gaXNOZWc7DQoJcmV0dXJuIHJlc3VsdDsNCn0NCg0KZnVuY3Rpb24gYmlEdW1wKGIpIHsNCglyZXR1cm4gKGIuaXNOZWcgPyAiLSIgOiAiIikgKyBiLmRpZ2l0cy5qb2luKCIgIik7DQp9DQoNCmZ1bmN0aW9uIGJpQWRkKHgsIHkpIHsNCgl2YXIgcmVzdWx0Ow0KDQoJaWYgKHguaXNOZWcgIT0geS5pc05lZykgew0KCQl5LmlzTmVnID0gIXkuaXNOZWc7DQoJCXJlc3VsdCA9IGJpU3VidHJhY3QoeCwgeSk7DQoJCXkuaXNOZWcgPSAheS5pc05lZzsNCgl9DQoJZWxzZSB7DQoJCXJlc3VsdCA9IG5ldyBCaWdJbnQoKTsNCgkJdmFyIGMgPSAwOw0KCQl2YXIgbjsNCgkJZm9yICh2YXIgaSA9IDA7IGkgJmx0OyB4LmRpZ2l0cy5sZW5ndGg7ICsraSkgew0KCQkJbiA9IHguZGlnaXRzW2ldICsgeS5kaWdpdHNbaV0gKyBjOw0KCQkJcmVzdWx0LmRpZ2l0c1tpXSA9IG4gJiAweGZmZmY7DQoJCQljID0gTnVtYmVyKG4gJmd0Oz0gYmlSYWRpeCk7DQoJCX0NCgkJcmVzdWx0LmlzTmVnID0geC5pc05lZzsNCgl9DQoJcmV0dXJuIHJlc3VsdDsNCn0NCg0KZnVuY3Rpb24gYmlTdWJ0cmFjdCh4LCB5KSB7DQoJdmFyIHJlc3VsdDsNCglpZiAoeC5pc05lZyAhPSB5LmlzTmVnKSB7DQoJCXkuaXNOZWcgPSAheS5pc05lZzsNCgkJcmVzdWx0ID0gYmlBZGQoeCwgeSk7DQoJCXkuaXNOZWcgPSAheS5pc05lZzsNCgl9IGVsc2Ugew0KCQlyZXN1bHQgPSBuZXcgQmlnSW50KCk7DQoJCXZhciBuLCBjOw0KCQljID0gMDsNCgkJZm9yICh2YXIgaSA9IDA7IGkgJmx0OyB4LmRpZ2l0cy5sZW5ndGg7ICsraSkgew0KCQkJbiA9IHguZGlnaXRzW2ldIC0geS5kaWdpdHNbaV0gKyBjOw0KCQkJcmVzdWx0LmRpZ2l0c1tpXSA9IG4gJiAweGZmZmY7DQoJCQlpZiAocmVzdWx0LmRpZ2l0c1tpXSAmbHQ7IDApIHJlc3VsdC5kaWdpdHNbaV0gKz0gYmlSYWRpeDsNCgkJCWMgPSAwIC0gTnVtYmVyKG4gJmx0OyAwKTsNCgkJfQ0KCQlpZiAoYyA9PSAtMSkgew0KCQkJYyA9IDA7DQoJCQlmb3IgKHZhciBpID0gMDsgaSAmbHQ7IHguZGlnaXRzLmxlbmd0aDsgKytpKSB7DQoJCQkJbiA9IDAgLSByZXN1bHQuZGlnaXRzW2ldICsgYzsNCgkJCQlyZXN1bHQuZGlnaXRzW2ldID0gbiAmIDB4ZmZmZjsNCgkJCQlpZiAocmVzdWx0LmRpZ2l0c1tpXSAmbHQ7IDApIHJlc3VsdC5kaWdpdHNbaV0gKz0gYmlSYWRpeDsNCgkJCQljID0gMCAtIE51bWJlcihuICZsdDsgMCk7DQoJCQl9DQoJCQlyZXN1bHQuaXNOZWcgPSAheC5pc05lZzsNCgkJfSBlbHNlIHsNCgkJCXJlc3VsdC5pc05lZyA9IHguaXNOZWc7DQoJCX0NCgl9DQoJcmV0dXJuIHJlc3VsdDsNCn0NCg0KZnVuY3Rpb24gYmlIaWdoSW5kZXgoeCkgew0KCXZhciByZXN1bHQgPSB4LmRpZ2l0cy5sZW5ndGggLSAxOw0KCXdoaWxlIChyZXN1bHQgJmd0OyAwICYmIHguZGlnaXRzW3Jlc3VsdF0gPT0gMCkgLS1yZXN1bHQ7DQoJcmV0dXJuIHJlc3VsdDsNCn0NCg0KZnVuY3Rpb24gYmlOdW1CaXRzKHgpIHsNCgl2YXIgbiA9IGJpSGlnaEluZGV4KHgpOw0KCXZhciBkID0geC5kaWdpdHNbbl07DQoJdmFyIG0gPSAobiArIDEpICogYml0c1BlckRpZ2l0Ow0KCXZhciByZXN1bHQ7DQoJZm9yIChyZXN1bHQgPSBtOyByZXN1bHQgJmd0OyBtIC0gYml0c1BlckRpZ2l0OyAtLXJlc3VsdCkgew0KCQlpZiAoKGQgJiAweDgwMDApICE9IDApIGJyZWFrOw0KCQlkICZsdDsmbHQ7PSAxOw0KCX0NCglyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiBiaU11bHRpcGx5KHgsIHkpIHsNCgl2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOw0KCXZhciBjOw0KCXZhciBuID0gYmlIaWdoSW5kZXgoeCk7DQoJdmFyIHQgPSBiaUhpZ2hJbmRleCh5KTsNCgl2YXIgdSwgdXYsIGs7DQoNCglmb3IgKHZhciBpID0gMDsgaSAmbHQ7PSB0OyArK2kpIHsNCgkJYyA9IDA7DQoJCWsgPSBpOw0KCQlmb3IgKGogPSAwOyBqICZsdDs9IG47ICsraiwgKytrKSB7DQoJCQl1diA9IHJlc3VsdC5kaWdpdHNba10gKyB4LmRpZ2l0c1tqXSAqIHkuZGlnaXRzW2ldICsgYzsNCgkJCXJlc3VsdC5kaWdpdHNba10gPSB1diAmIG1heERpZ2l0VmFsOw0KCQkJYyA9IHV2ICZndDsmZ3Q7Jmd0OyBiaVJhZGl4Qml0czsNCgkJfQ0KCQlyZXN1bHQuZGlnaXRzW2kgKyBuICsgMV0gPSBjOw0KCX0NCglyZXN1bHQuaXNOZWcgPSB4LmlzTmVnICE9IHkuaXNOZWc7DQoJcmV0dXJuIHJlc3VsdDsNCn0NCg0KZnVuY3Rpb24gYmlNdWx0aXBseURpZ2l0KHgsIHkpIHsNCgl2YXIgbiwgYywgdXY7DQoNCglyZXN1bHQgPSBuZXcgQmlnSW50KCk7DQoJbiA9IGJpSGlnaEluZGV4KHgpOw0KCWMgPSAwOw0KCWZvciAodmFyIGogPSAwOyBqICZsdDs9IG47ICsraikgew0KCQl1diA9IHJlc3VsdC5kaWdpdHNbal0gKyB4LmRpZ2l0c1tqXSAqIHkgKyBjOw0KCQlyZXN1bHQuZGlnaXRzW2pdID0gdXYgJiBtYXhEaWdpdFZhbDsNCgkJYyA9IHV2ICZndDsmZ3Q7Jmd0OyBiaVJhZGl4Qml0czsNCgl9DQoJcmVzdWx0LmRpZ2l0c1sxICsgbl0gPSBjOw0KCXJldHVybiByZXN1bHQ7DQp9DQoNCmZ1bmN0aW9uIGFycmF5Q29weShzcmMsIHNyY1N0YXJ0LCBkZXN0LCBkZXN0U3RhcnQsIG4pIHsNCgl2YXIgbSA9IE1hdGgubWluKHNyY1N0YXJ0ICsgbiwgc3JjLmxlbmd0aCk7DQoJZm9yICh2YXIgaSA9IHNyY1N0YXJ0LCBqID0gZGVzdFN0YXJ0OyBpICZsdDsgbTsgKytpLCArK2opIHsNCgkJZGVzdFtqXSA9IHNyY1tpXTsNCgl9DQp9DQoNCg0KDQpmdW5jdGlvbiBiaVNoaWZ0TGVmdCh4LCBuKSB7DQoJdmFyIGRpZ2l0Q291bnQgPSBNYXRoLmZsb29yKG4gLyBiaXRzUGVyRGlnaXQpOw0KCXZhciByZXN1bHQgPSBuZXcgQmlnSW50KCk7DQoJYXJyYXlDb3B5KHguZGlnaXRzLCAwLCByZXN1bHQuZGlnaXRzLCBkaWdpdENvdW50LHJlc3VsdC5kaWdpdHMubGVuZ3RoIC0gZGlnaXRDb3VudCk7DQoJdmFyIGJpdHMgPSBuICUgYml0c1BlckRpZ2l0Ow0KCXZhciByaWdodEJpdHMgPSBiaXRzUGVyRGlnaXQgLSBiaXRzOw0KCWZvciAodmFyIGkgPSByZXN1bHQuZGlnaXRzLmxlbmd0aCAtIDEsIGkxID0gaSAtIDE7IGkgJmd0OyAwOyAtLWksIC0taTEpIHsNCgkJcmVzdWx0LmRpZ2l0c1tpXSA9ICgocmVzdWx0LmRpZ2l0c1tpXSAmbHQ7Jmx0OyBiaXRzKSAmIG1heERpZ2l0VmFsKSB8DQoJCSgocmVzdWx0LmRpZ2l0c1tpMV0gJiBoaWdoQml0TWFza3NbYml0c10pICZndDsmZ3Q7Jmd0Ow0KCQkocmlnaHRCaXRzKSk7DQoJfQ0KCXJlc3VsdC5kaWdpdHNbMF0gPSAoKHJlc3VsdC5kaWdpdHNbaV0gJmx0OyZsdDsgYml0cykgJiBtYXhEaWdpdFZhbCk7DQoJcmVzdWx0LmlzTmVnID0geC5pc05lZzsNCglyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiBiaVNoaWZ0UmlnaHQoeCwgbikgew0KCXZhciBkaWdpdENvdW50ID0gTWF0aC5mbG9vcihuIC8gYml0c1BlckRpZ2l0KTsNCgl2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOw0KCWFycmF5Q29weSh4LmRpZ2l0cywgZGlnaXRDb3VudCwgcmVzdWx0LmRpZ2l0cywgMCx4LmRpZ2l0cy5sZW5ndGggLSBkaWdpdENvdW50KTsNCgl2YXIgYml0cyA9IG4gJSBiaXRzUGVyRGlnaXQ7DQoJdmFyIGxlZnRCaXRzID0gYml0c1BlckRpZ2l0IC0gYml0czsNCglmb3IgKHZhciBpID0gMCwgaTEgPSBpICsgMTsgaSAmbHQ7IHJlc3VsdC5kaWdpdHMubGVuZ3RoIC0gMTsgKytpLCArK2kxKSB7DQoJCXJlc3VsdC5kaWdpdHNbaV0gPSAocmVzdWx0LmRpZ2l0c1tpXSAmZ3Q7Jmd0OyZndDsgYml0cykgfA0KCQkoKHJlc3VsdC5kaWdpdHNbaTFdICYgbG93Qml0TWFza3NbYml0c10pICZsdDsmbHQ7IGxlZnRCaXRzKTsNCgl9DQoJcmVzdWx0LmRpZ2l0c1tyZXN1bHQuZGlnaXRzLmxlbmd0aCAtIDFdICZndDsmZ3Q7Jmd0Oz0gYml0czsNCglyZXN1bHQuaXNOZWcgPSB4LmlzTmVnOw0KCXJldHVybiByZXN1bHQ7DQp9DQoNCmZ1bmN0aW9uIGJpTXVsdGlwbHlCeVJhZGl4UG93ZXIoeCwgbikgew0KCXZhciByZXN1bHQgPSBuZXcgQmlnSW50KCk7DQoJYXJyYXlDb3B5KHguZGlnaXRzLCAwLCByZXN1bHQuZGlnaXRzLCBuLCByZXN1bHQuZGlnaXRzLmxlbmd0aCAtIG4pOw0KCXJldHVybiByZXN1bHQ7DQp9DQoNCmZ1bmN0aW9uIGJpRGl2aWRlQnlSYWRpeFBvd2VyKHgsIG4pDQp7DQoJdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsNCglhcnJheUNvcHkoeC5kaWdpdHMsIG4sIHJlc3VsdC5kaWdpdHMsIDAsIHJlc3VsdC5kaWdpdHMubGVuZ3RoIC0gbik7DQoJcmV0dXJuIHJlc3VsdDsNCn0NCg0KZnVuY3Rpb24gYmlNb2R1bG9CeVJhZGl4UG93ZXIoeCwgbikNCnsNCgl2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOw0KCWFycmF5Q29weSh4LmRpZ2l0cywgMCwgcmVzdWx0LmRpZ2l0cywgMCwgbik7DQoJcmV0dXJuIHJlc3VsdDsNCn0NCg0KZnVuY3Rpb24gYmlDb21wYXJlKHgsIHkpIHsNCglpZiAoeC5pc05lZyAhPSB5LmlzTmVnKSB7DQoJCXJldHVybiAxIC0gMiAqIE51bWJlcih4LmlzTmVnKTsNCgl9DQoJZm9yICh2YXIgaSA9IHguZGlnaXRzLmxlbmd0aCAtIDE7IGkgJmd0Oz0gMDsgLS1pKSB7DQoJCWlmICh4LmRpZ2l0c1tpXSAhPSB5LmRpZ2l0c1tpXSkgew0KCQkJaWYgKHguaXNOZWcpIHsNCgkJCQlyZXR1cm4gMSAtIDIgKiBOdW1iZXIoeC5kaWdpdHNbaV0gJmd0OyB5LmRpZ2l0c1tpXSk7DQoJCQl9IGVsc2Ugew0KCQkJCXJldHVybiAxIC0gMiAqIE51bWJlcih4LmRpZ2l0c1tpXSAmbHQ7IHkuZGlnaXRzW2ldKTsNCgkJCX0NCgkJfQ0KCX0NCglyZXR1cm4gMDsNCn0NCg0KZnVuY3Rpb24gYmlEaXZpZGVNb2R1bG8oeCwgeSkgew0KCXZhciBuYiA9IGJpTnVtQml0cyh4KTsNCgl2YXIgdGIgPSBiaU51bUJpdHMoeSk7DQoJdmFyIG9yaWdZSXNOZWcgPSB5LmlzTmVnOw0KCXZhciBxLCByOw0KCWlmIChuYiAmbHQ7IHRiKSB7DQoJCWlmICh4LmlzTmVnKSB7DQoJCQlxID0gYmlDb3B5KGJpZ09uZSk7DQoJCQlxLmlzTmVnID0gIXkuaXNOZWc7DQoJCQl4LmlzTmVnID0gZmFsc2U7DQoJCQl5LmlzTmVnID0gZmFsc2U7DQoJCQlyID0gYmlTdWJ0cmFjdCh5LCB4KTsNCgkJCXguaXNOZWcgPSB0cnVlOw0KCQkJeS5pc05lZyA9IG9yaWdZSXNOZWc7DQoJCX0gZWxzZSB7DQoJCQlxID0gbmV3IEJpZ0ludCgpOw0KCQkJciA9IGJpQ29weSh4KTsNCgkJfQ0KCQlyZXR1cm4gbmV3IEFycmF5KHEsIHIpOw0KCX0NCg0KCXEgPSBuZXcgQmlnSW50KCk7DQoJciA9IHg7DQoNCgl2YXIgdCA9IE1hdGguY2VpbCh0YiAvIGJpdHNQZXJEaWdpdCkgLSAxOw0KCXZhciBsYW1iZGEgPSAwOw0KCXdoaWxlICh5LmRpZ2l0c1t0XSAmbHQ7IGJpSGFsZlJhZGl4KSB7DQoJCXkgPSBiaVNoaWZ0TGVmdCh5LCAxKTsNCgkJKytsYW1iZGE7DQoJCSsrdGI7DQoJCXQgPSBNYXRoLmNlaWwodGIgLyBiaXRzUGVyRGlnaXQpIC0gMTsNCgl9DQoNCglyID0gYmlTaGlmdExlZnQociwgbGFtYmRhKTsNCgluYiArPSBsYW1iZGE7DQoJdmFyIG4gPSBNYXRoLmNlaWwobmIgLyBiaXRzUGVyRGlnaXQpIC0gMTsNCg0KCXZhciBiID0gYmlNdWx0aXBseUJ5UmFkaXhQb3dlcih5LCBuIC0gdCk7DQoJd2hpbGUgKGJpQ29tcGFyZShyLCBiKSAhPSAtMSkgew0KCQkrK3EuZGlnaXRzW24gLSB0XTsNCgkJciA9IGJpU3VidHJhY3QociwgYik7DQoJfQ0KCWZvciAodmFyIGkgPSBuOyBpICZndDsgdDsgLS1pKSB7DQoJCXZhciByaSA9IChpICZndDs9IHIuZGlnaXRzLmxlbmd0aCkgPyAwIDogci5kaWdpdHNbaV07DQoJCXZhciByaTEgPSAoaSAtIDEgJmd0Oz0gci5kaWdpdHMubGVuZ3RoKSA/IDAgOiByLmRpZ2l0c1tpIC0gMV07DQoJCXZhciByaTIgPSAoaSAtIDIgJmd0Oz0gci5kaWdpdHMubGVuZ3RoKSA/IDAgOiByLmRpZ2l0c1tpIC0gMl07DQoJCXZhciB5dCA9ICh0ICZndDs9IHkuZGlnaXRzLmxlbmd0aCkgPyAwIDogeS5kaWdpdHNbdF07DQoJCXZhciB5dDEgPSAodCAtIDEgJmd0Oz0geS5kaWdpdHMubGVuZ3RoKSA/IDAgOiB5LmRpZ2l0c1t0IC0gMV07DQoJCWlmIChyaSA9PSB5dCkgew0KCQkJcS5kaWdpdHNbaSAtIHQgLSAxXSA9IG1heERpZ2l0VmFsOw0KCQl9IGVsc2Ugew0KCQkJcS5kaWdpdHNbaSAtIHQgLSAxXSA9IE1hdGguZmxvb3IoKHJpICogYmlSYWRpeCArIHJpMSkgLyB5dCk7DQoJCX0NCg0KCQl2YXIgYzEgPSBxLmRpZ2l0c1tpIC0gdCAtIDFdICogKCh5dCAqIGJpUmFkaXgpICsgeXQxKTsNCgkJdmFyIGMyID0gKHJpICogYmlSYWRpeFNxdWFyZWQpICsgKChyaTEgKiBiaVJhZGl4KSArIHJpMik7DQoJCXdoaWxlIChjMSAmZ3Q7IGMyKSB7DQoJCQktLXEuZGlnaXRzW2kgLSB0IC0gMV07DQoJCQljMSA9IHEuZGlnaXRzW2kgLSB0IC0gMV0gKiAoKHl0ICogYmlSYWRpeCkgfCB5dDEpOw0KCQkJYzIgPSAocmkgKiBiaVJhZGl4ICogYmlSYWRpeCkgKyAoKHJpMSAqIGJpUmFkaXgpICsgcmkyKTsNCgkJfQ0KDQoJCWIgPSBiaU11bHRpcGx5QnlSYWRpeFBvd2VyKHksIGkgLSB0IC0gMSk7DQoJCXIgPSBiaVN1YnRyYWN0KHIsIGJpTXVsdGlwbHlEaWdpdChiLCBxLmRpZ2l0c1tpIC0gdCAtIDFdKSk7DQoJCWlmIChyLmlzTmVnKSB7DQoJCQlyID0gYmlBZGQociwgYik7DQoJCQktLXEuZGlnaXRzW2kgLSB0IC0gMV07DQoJCX0NCgl9DQoJciA9IGJpU2hpZnRSaWdodChyLCBsYW1iZGEpOw0KDQoJcS5pc05lZyA9IHguaXNOZWcgIT0gb3JpZ1lJc05lZzsNCglpZiAoeC5pc05lZykgew0KCQlpZiAob3JpZ1lJc05lZykgew0KCQkJcSA9IGJpQWRkKHEsIGJpZ09uZSk7DQoJCX0gZWxzZSB7DQoJCQlxID0gYmlTdWJ0cmFjdChxLCBiaWdPbmUpOw0KCQl9DQoJCXkgPSBiaVNoaWZ0UmlnaHQoeSwgbGFtYmRhKTsNCgkJciA9IGJpU3VidHJhY3QoeSwgcik7DQoJfQ0KDQoJaWYgKHIuZGlnaXRzWzBdID09IDAgJiYgYmlIaWdoSW5kZXgocikgPT0gMCkgci5pc05lZyA9IGZhbHNlOw0KDQoJcmV0dXJuIG5ldyBBcnJheShxLCByKTsNCn0NCg0KZnVuY3Rpb24gYmlEaXZpZGUoeCwgeSkgew0KCXJldHVybiBiaURpdmlkZU1vZHVsbyh4LCB5KVswXTsNCn0NCg0KZnVuY3Rpb24gYmlNb2R1bG8oeCwgeSkgew0KCXJldHVybiBiaURpdmlkZU1vZHVsbyh4LCB5KVsxXTsNCn0NCg0KZnVuY3Rpb24gYmlNdWx0aXBseU1vZCh4LCB5LCBtKSB7DQoJcmV0dXJuIGJpTW9kdWxvKGJpTXVsdGlwbHkoeCwgeSksIG0pOw0KfQ0KDQpmdW5jdGlvbiBiaVBvdyh4LCB5KSB7DQoJdmFyIHJlc3VsdCA9IGJpZ09uZTsNCgl2YXIgYSA9IHg7DQoJd2hpbGUgKHRydWUpIHsNCgkJaWYgKCh5ICYgMSkgIT0gMCkgcmVzdWx0ID0gYmlNdWx0aXBseShyZXN1bHQsIGEpOw0KCQl5ICZndDsmZ3Q7PSAxOw0KCQlpZiAoeSA9PSAwKSBicmVhazsNCgkJYSA9IGJpTXVsdGlwbHkoYSwgYSk7DQoJfQ0KCXJldHVybiByZXN1bHQ7DQp9DQoNCmZ1bmN0aW9uIGJpUG93TW9kKHgsIHksIG0pIHsNCgl2YXIgcmVzdWx0ID0gYmlnT25lOw0KCXZhciBhID0geDsNCgl2YXIgayA9IHk7DQoJd2hpbGUgKHRydWUpIHsNCgkJaWYgKChrLmRpZ2l0c1swXSAmIDEpICE9IDApIHJlc3VsdCA9IGJpTXVsdGlwbHlNb2QocmVzdWx0LCBhLCBtKTsNCgkJayA9IGJpU2hpZnRSaWdodChrLCAxKTsNCgkJaWYgKGsuZGlnaXRzWzBdID09IDAgJiYgYmlIaWdoSW5kZXgoaykgPT0gMCkgYnJlYWs7DQoJCWEgPSBiaU11bHRpcGx5TW9kKGEsIGEsIG0pOw0KCX0NCglyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiBCYXJyZXR0TXUobSkgew0KCXRoaXMubW9kdWx1cyA9IGJpQ29weShtKTsNCgl0aGlzLmsgPSBiaUhpZ2hJbmRleCh0aGlzLm1vZHVsdXMpICsgMTsNCgl2YXIgYjJrID0gbmV3IEJpZ0ludCgpOw0KCWIyay5kaWdpdHNbMiAqIHRoaXMua10gPSAxOw0KCXRoaXMubXUgPSBiaURpdmlkZShiMmssIHRoaXMubW9kdWx1cyk7DQoJdGhpcy5ia3BsdXMxID0gbmV3IEJpZ0ludCgpOw0KCXRoaXMuYmtwbHVzMS5kaWdpdHNbdGhpcy5rICsgMV0gPSAxOw0KCXRoaXMubW9kdWxvID0gQmFycmV0dE11X21vZHVsbzsNCgl0aGlzLm11bHRpcGx5TW9kID0gQmFycmV0dE11X211bHRpcGx5TW9kOw0KCXRoaXMucG93TW9kID0gQmFycmV0dE11X3Bvd01vZDsNCn0NCg0KZnVuY3Rpb24gQmFycmV0dE11X21vZHVsbyh4KSB7DQoJdmFyIHExID0gYmlEaXZpZGVCeVJhZGl4UG93ZXIoeCwgdGhpcy5rIC0gMSk7DQoJdmFyIHEyID0gYmlNdWx0aXBseShxMSwgdGhpcy5tdSk7DQoJdmFyIHEzID0gYmlEaXZpZGVCeVJhZGl4UG93ZXIocTIsIHRoaXMuayArIDEpOw0KCXZhciByMSA9IGJpTW9kdWxvQnlSYWRpeFBvd2VyKHgsIHRoaXMuayArIDEpOw0KCXZhciByMnRlcm0gPSBiaU11bHRpcGx5KHEzLCB0aGlzLm1vZHVsdXMpOw0KCXZhciByMiA9IGJpTW9kdWxvQnlSYWRpeFBvd2VyKHIydGVybSwgdGhpcy5rICsgMSk7DQoJdmFyIHIgPSBiaVN1YnRyYWN0KHIxLCByMik7DQoJaWYgKHIuaXNOZWcpIHsNCgkJciA9IGJpQWRkKHIsIHRoaXMuYmtwbHVzMSk7DQoJfQ0KCXZhciByZ3RlbSA9IGJpQ29tcGFyZShyLCB0aGlzLm1vZHVsdXMpICZndDs9IDA7DQoJd2hpbGUgKHJndGVtKSB7DQoJCXIgPSBiaVN1YnRyYWN0KHIsIHRoaXMubW9kdWx1cyk7DQoJCXJndGVtID0gYmlDb21wYXJlKHIsIHRoaXMubW9kdWx1cykgJmd0Oz0gMDsNCgl9DQoJcmV0dXJuIHI7DQp9DQoNCmZ1bmN0aW9uIEJhcnJldHRNdV9tdWx0aXBseU1vZCh4LCB5KSB7DQoJdmFyIHh5ID0gYmlNdWx0aXBseSh4LCB5KTsNCglyZXR1cm4gdGhpcy5tb2R1bG8oeHkpOw0KfQ0KDQpmdW5jdGlvbiBCYXJyZXR0TXVfcG93TW9kKHgsIHkpIHsNCgl2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOw0KCXJlc3VsdC5kaWdpdHNbMF0gPSAxOw0KCXdoaWxlICh0cnVlKSB7DQoJCWlmICgoeS5kaWdpdHNbMF0gJiAxKSAhPSAwKSByZXN1bHQgPSB0aGlzLm11bHRpcGx5TW9kKHJlc3VsdCwgeCk7DQoJCXkgPSBiaVNoaWZ0UmlnaHQoeSwgMSk7DQoJCWlmICh5LmRpZ2l0c1swXSA9PSAwICYmIGJpSGlnaEluZGV4KHkpID09IDApIGJyZWFrOw0KCQl4ID0gdGhpcy5tdWx0aXBseU1vZCh4LCB4KTsNCgl9DQoJcmV0dXJuIHJlc3VsdDsNCn0NCg0KZnVuY3Rpb24gZW5jcnlwdFN0cmluZyhlbmNyeXB0aW9uRXhwb25lbnQsbW9kdWx1cyxtYXhkaWdpdHMsc3RyaW5nKXsNCglmdW5jdGlvbiBnZXRCYXNlUGFyYW0oZW5jcnlwdGlvbkV4cG9uZW50LG1vZHVsdXMsbWF4ZGlnaXRzKXsNCgkJdmFyIGJhc2UgPSB0aGlzOw0KCQlzZXRNYXhEaWdpdHMocGFyc2VJbnQobWF4ZGlnaXRzLDEwKSk7DQoJCWJhc2UuZSA9IGJpRnJvbUhleChlbmNyeXB0aW9uRXhwb25lbnQpOw0KCQliYXNlLm0gPSBiaUZyb21IZXgobW9kdWx1cyk7DQoJCWJhc2UuY2h1bmtTaXplID0gMiAqIGJpSGlnaEluZGV4KGJhc2UubSk7DQoJCWJhc2UucmFkaXggPSAxNjsNCgkJYmFzZS5iYXJyZXR0ID0gbmV3IEJhcnJldHRNdShiYXNlLm0pOw0KCQlyZXR1cm4gYmFzZTsNCgl9DQoJDQoJdmFyIGtleVBhaXIgPSBnZXRCYXNlUGFyYW0oZW5jcnlwdGlvbkV4cG9uZW50LG1vZHVsdXMsbWF4ZGlnaXRzKTsNCgkNCgl2YXIgY2hhclN1bSA9IDA7DQoJCWZvcih2YXIgaSA9IDA7IGkgJmx0OyBzdHJpbmcubGVuZ3RoOyBpKyspew0KCQkJY2hhclN1bSArPSBzdHJpbmcuY2hhckNvZGVBdChpKTsNCgkJfQ0KCQl2YXIgdGFnID0gJiMzOTswMTIzNDU2Nzg5YWJjZGVmJiMzOTs7DQoJCXZhciBoZXggPSAmIzM5OyYjMzk7Ow0KCQloZXggKz0gdGFnLmNoYXJBdCgoY2hhclN1bSAmIDB4RjApICZndDsmZ3Q7IDQpICsgdGFnLmNoYXJBdChjaGFyU3VtICYgMHgwRik7DQoNCgkJdmFyIHRhZ2dlZFN0cmluZyA9IGhleCArIHN0cmluZzsNCg0KCQl2YXIgZW5jcnlwdCA9IFtdOw0KCQl2YXIgaiA9IDA7DQoNCgkJd2hpbGUgKGogJmx0OyB0YWdnZWRTdHJpbmcubGVuZ3RoKSB7DQoJCQllbmNyeXB0W2pdID0gdGFnZ2VkU3RyaW5nLmNoYXJDb2RlQXQoaik7DQoJCQlqKys7DQoJCX0NCg0KCQl3aGlsZSAoZW5jcnlwdC5sZW5ndGggJSBrZXlQYWlyLmNodW5rU2l6ZSAhPT0gMCkgew0KCQkJZW5jcnlwdFtqKytdID0gMDsNCgkJfQ0KDQoJCWZ1bmN0aW9uIGVuY3J5cHRpb24oZW5jcnlwdE9iamVjdCkgew0KCQkJdmFyIGNoYXJDb3VudGVyID0gMDsNCgkJCXZhciBqLCBibG9jazsNCgkJCXZhciBlbmNyeXB0ZWQgPSAiIjsNCgkJCWZ1bmN0aW9uIGVuY3J5cHRDaGFyKCkgew0KCQkJCWJsb2NrID0gbmV3IEJpZ0ludCgpOw0KCQkJCWogPSAwOw0KCQkJCWZvciAodmFyIGsgPSBjaGFyQ291bnRlcjsgayAmbHQ7IGNoYXJDb3VudGVyK2tleVBhaXIuY2h1bmtTaXplOyArK2opIHsNCgkJCQkJYmxvY2suZGlnaXRzW2pdID0gZW5jcnlwdE9iamVjdFtrKytdOw0KCQkJCQlibG9jay5kaWdpdHNbal0gKz0gZW5jcnlwdE9iamVjdFtrKytdICZsdDsmbHQ7IDg7DQoJCQkJfQ0KCQkJCXZhciBjcnlwdCA9IGtleVBhaXIuYmFycmV0dC5wb3dNb2QoYmxvY2ssIGtleVBhaXIuZSk7DQoJCQkJdmFyIHRleHQgPSBrZXlQYWlyLnJhZGl4ID09IDE2ID8gYmlUb0hleChjcnlwdCkgOiBiaVRvU3RyaW5nKGNyeXB0LCBrZXlQYWlyLnJhZGl4KTsNCgkJCQllbmNyeXB0ZWQgKz0gdGV4dCArICIgIjsNCgkJCQljaGFyQ291bnRlciArPSBrZXlQYWlyLmNodW5rU2l6ZTsNCgkJCQlpZiAoY2hhckNvdW50ZXIgJmx0OyBlbmNyeXB0T2JqZWN0Lmxlbmd0aCkgew0KCQkJCQllbmNyeXB0Q2hhcigpOw0KCQkJCQkvL3NldFRpbWVvdXQoZW5jcnlwdENoYXIsIDEpDQoJCQkJfSBlbHNlIHsNCgkJCQkJdmFyIGVuY3J5cHRlZFN0cmluZyA9IGVuY3J5cHRlZC5zdWJzdHJpbmcoMCwgZW5jcnlwdGVkLmxlbmd0aCAtIDEpOw0KDQoJCQkJCSAJcmV0dXJuIGVuY3J5cHRlZFN0cmluZzsNCgkJCQl9DQoJCQl9DQoJCQlyZXR1cm4gZW5jcnlwdENoYXIoKTsNCgkJCS8vc2V0VGltZW91dChlbmNyeXB0Q2hhciwgMSk7DQoJCX0NCg0KCQl2YXIgcmVzdWx0PWVuY3J5cHRpb24oZW5jcnlwdCk7DQoJCQ0KCQlyZXR1cm4gcmVzdWx0Ow0KfQ0KDQpmdW5jdGlvbiB1dGYxNnRvOChzdHIpIHsNCuOAgOOAgHZhciBvdXQsIGksIGxlbiwgYzsNCuOAgOOAgG91dCA9ICIiOw0K44CA44CAbGVuID0gc3RyLmxlbmd0aDsNCuOAgOOAgGZvcihpID0gMDsgaSAmbHQ7IGxlbjsgaSsrKSB7DQoJCQkgYyA9IHN0ci5jaGFyQ29kZUF0KGkpOw0KCQkJIGlmICgoYyAmZ3Q7PSAweDAwMDEpICYmIChjICZsdDs9IDB4MDA3RikpIHsNCgkJCeOAgOOAgCBvdXQgKz0gc3RyLmNoYXJBdChpKTsNCgkJCSB9IGVsc2UgaWYgKGMgJmd0OyAweDA3RkYpIHsNCgkJCeOAgOOAgCBvdXQgKz0gU3RyaW5nLmZyb21DaGFyQ29kZSgweEUwIHwgKChjICZndDsmZ3Q7IDEyKSAmIDB4MEYpKTsNCgkJCeOAgOOAgCBvdXQgKz0gU3RyaW5nLmZyb21DaGFyQ29kZSgweDgwIHwgKChjICZndDsmZ3Q744CANikgJiAweDNGKSk7DQoJCQnjgIDjgIAgb3V0ICs9IFN0cmluZy5mcm9tQ2hhckNvZGUoMHg4MCB8ICgoYyAmZ3Q7Jmd0O+OAgDApICYgMHgzRikpOw0KCQkJIH0gZWxzZSB7DQoJCQnjgIDjgIAgb3V0ICs9IFN0cmluZy5mcm9tQ2hhckNvZGUoMHhDMCB8ICgoYyAmZ3Q7Jmd0O+OAgDYpICYgMHgxRikpOw0KCQkJ44CA44CAIG91dCArPSBTdHJpbmcuZnJvbUNoYXJDb2RlKDB4ODAgfCAoKGMgJmd0OyZndDvjgIAwKSAmIDB4M0YpKTsNCgkJCSB9DQrjgIDjgIB9DQrjgIDjgIByZXR1cm4gb3V0Ow0KfQ0KDQp2YXIgYmFzZTY0RW5jb2RlQ2hhcnMgPSAiQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0NTY3ODkrLyI7DQp2YXIgYmFzZTY0RGVjb2RlQ2hhcnMgPSBuZXcgQXJyYXkoDQrjgIDjgIAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwNCuOAgOOAgC0xLCAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLA0K44CA44CALTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwgLTEsIC0xLCA2MiwgLTEsIC0xLCAtMSwgNjMsDQrjgIDjgIA1MiwgNTMsIDU0LCA1NSwgNTYsIDU3LCA1OCwgNTksIDYwLCA2MSwgLTEsIC0xLCAtMSwgLTEsIC0xLCAtMSwNCuOAgOOAgC0xLOOAgDAs44CAMSzjgIAyLOOAgDMsICA0LOOAgDUs44CANizjgIA3LOOAgDgs44CAOSwgMTAsIDExLCAxMiwgMTMsIDE0LA0K44CA44CAMTUsIDE2LCAxNywgMTgsIDE5LCAyMCwgMjEsIDIyLCAyMywgMjQsIDI1LCAtMSwgLTEsIC0xLCAtMSwgLTEsDQrjgIDjgIAtMSwgMjYsIDI3LCAyOCwgMjksIDMwLCAzMSwgMzIsIDMzLCAzNCwgMzUsIDM2LCAzNywgMzgsIDM5LCA0MCwNCuOAgOOAgDQxLCA0MiwgNDMsIDQ0LCA0NSwgNDYsIDQ3LCA0OCwgNDksIDUwLCA1MSwgLTEsIC0xLCAtMSwgLTEsIC0xKTsNCg0KZnVuY3Rpb24gYmFzZTY0ZW5jb2RlKHN0cikgew0K44CA44CAdmFyIG91dCwgaSwgbGVuOw0K44CA44CAdmFyIGMxLCBjMiwgYzM7DQrjgIDjgIBsZW4gPSBzdHIubGVuZ3RoOw0K44CA44CAaSA9IDA7DQrjgIDjgIBvdXQgPSAiIjsNCuOAgOOAgHdoaWxlKGkgJmx0OyBsZW4pIHsNCgkJCSBjMSA9IHN0ci5jaGFyQ29kZUF0KGkrKykgJiAweGZmOw0KCQkJIGlmKGkgPT0gbGVuKQ0KCQkJIHsNCgkJCeOAgOOAgCBvdXQgKz0gYmFzZTY0RW5jb2RlQ2hhcnMuY2hhckF0KGMxICZndDsmZ3Q7IDIpOw0KCQkJ44CA44CAIG91dCArPSBiYXNlNjRFbmNvZGVDaGFycy5jaGFyQXQoKGMxICYgMHgzKSAmbHQ7Jmx0OyA0KTsNCgkJCeOAgOOAgCBvdXQgKz0gIj09IjsNCgkJCeOAgOOAgCBicmVhazsNCgkJCSB9DQoJCQkgYzIgPSBzdHIuY2hhckNvZGVBdChpKyspOw0KCQkJIGlmKGkgPT0gbGVuKQ0KCQkJIHsNCgkJCeOAgOOAgCBvdXQgKz0gYmFzZTY0RW5jb2RlQ2hhcnMuY2hhckF0KGMxICZndDsmZ3Q7IDIpOw0KCQkJ44CA44CAIG91dCArPSBiYXNlNjRFbmNvZGVDaGFycy5jaGFyQXQoKChjMSAmIDB4MykmbHQ7Jmx0OyA0KSB8ICgoYzIgJiAweEYwKSAmZ3Q7Jmd0OyA0KSk7DQoJCQnjgIDjgIAgb3V0ICs9IGJhc2U2NEVuY29kZUNoYXJzLmNoYXJBdCgoYzIgJiAweEYpICZsdDsmbHQ7IDIpOw0KCQkJ44CA44CAIG91dCArPSAiPSI7DQoJCQnjgIDjgIAgYnJlYWs7DQoJCQkgfQ0KCQkJIGMzID0gc3RyLmNoYXJDb2RlQXQoaSsrKTsNCgkJCSBvdXQgKz0gYmFzZTY0RW5jb2RlQ2hhcnMuY2hhckF0KGMxICZndDsmZ3Q7IDIpOw0KCQkJIG91dCArPSBiYXNlNjRFbmNvZGVDaGFycy5jaGFyQXQoKChjMSAmIDB4MykmbHQ7Jmx0OyA0KSB8ICgoYzIgJiAweEYwKSAmZ3Q7Jmd0OyA0KSk7DQoJCQkgb3V0ICs9IGJhc2U2NEVuY29kZUNoYXJzLmNoYXJBdCgoKGMyICYgMHhGKSAmbHQ7Jmx0OyAyKSB8ICgoYzMgJiAweEMwKSAmZ3Q7Jmd0OzYpKTsNCgkJCSBvdXQgKz0gYmFzZTY0RW5jb2RlQ2hhcnMuY2hhckF0KGMzICYgMHgzRik7DQrjgIB9DQrjgIByZXR1cm4gb3V0Ow0KfQ0KDQoNCg0KDQoNCg0KDQoNCg0KDQoNCg0KDQoNCg0KDQo=";

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={}", param, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(OperatorParam param) {
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
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
    public HttpResult<Map<String, Object>> validatePicCode(OperatorParam param) {
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return submitForLogin(param);
            case FormType.VALIDATE_BILL_DETAIL:
                return submitForBillDetail(param);
            default:
                return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }


    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;

        try {
            Invocable invocable = ScriptEngineUtil.createInvocableFromBase64(javaScript);
            String encryptPassword = invocable.invokeFunction("aesEncrypt", param.getPassword()).toString();

            String referer = "http://login.189.cn/login";
            String templateUrl = "http://login.189.cn/login";
            String templateData = "Account={}&UType=201&ProvinceID=17&AreaCode=&CityNo=&RandomFlag=0&Password={}&Captcha=";
            String data = TemplateUtils.format(templateData, param.getMobile(), URLEncoder.encode(encryptPassword, "UTF-8"));
            response = TaskHttpClient.create(param, RequestType.POST, "he_nan_10000_web_001").setFullUrl(templateUrl).setReferer(referer)
                    .setRequestBody(data).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            String resultCode = PatternUtils.group(pageContent, "data-resultcode=\"(\\d+)\"", 1);
            if (resultCode != null) {
                if (resultCode.equals("9103") || resultCode.equals("9999")) {
                    logger.error("登陆失败,账户名与密码不匹配,param={},response={}", param, response);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                } else if (resultCode.equals("8105")) {
                    logger.error("登陆失败,密码过于简单,请重置,param={},response={}", param, response);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                } else if (resultCode.equals("9111")) {
                    logger.error("登陆失败,登录失败过多，帐号已被锁定,param={},response={}", param, response);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                } else if (resultCode.equals("9100")) {
                    logger.error("登陆失败,该账户不存在,param={},response={}", param, response);
                    return result.failure(ErrorCode.VALIDATE_PHONE_FAIL);
                } else if (resultCode.equals("6113")) {
                    logger.error("登陆失败,系统繁忙，稍后重试,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                } else if (StringUtils.isNotBlank(resultCode)) {
                    logger.error("登陆失败,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }

            templateUrl = "http://www.189.cn/ha/";
            response = TaskHttpClient.create(param, RequestType.GET, "he_nan_10000_web_002").setFullUrl(templateUrl).invoke();

            referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=20000354";
            templateUrl = "http://www.189.cn/login/sso/ecs.do?method=linkTo&platNo=10017&toStUrl=http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-1&fastcode=20000354&cityCode=ha";
            response = TaskHttpClient.create(param, RequestType.GET, "he_nan_10000_web_003").setFullUrl(templateUrl).setReferer(referer).invoke();

            referer = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-1&fastcode=20000354&cityCode=ha";
            templateUrl = "http://ha.189.cn/service/iframe/bill/iframe_ye.jsp\"ACC_NBR=" + param.getMobile() + "&PROD_TYPE=713058010165&ACCTNBR97=";
            response = TaskHttpClient.create(param, RequestType.GET, "he_nan_10000_web_004").setFullUrl(templateUrl).setReferer(referer).invoke();
            pageContent = response.getPageContent();

            if (StringUtils.contains(pageContent, String.valueOf(param.getMobile())) && StringUtils.contains(pageContent, "可用余额")) {
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
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        try {
            String referer = "http://www.189.cn/dqmh/my189/initMy189home.do?fastcode=20000354";
            String templateUrl = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha";
            response = TaskHttpClient.create(param, RequestType.GET, "he_nan_10000_web_005").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            String pageContent = response.getPageContent();
            String PRODTYPE = PatternUtils.group(pageContent, "doQuery\\('(\\d+)','(\\d+)',''\\)", 2);
            TaskUtils.addTaskShare(param.getTaskId(), "PRODTYPE", PRODTYPE);

            referer = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha";
            templateUrl = "http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp\"ACC_NBR=" + param.getMobile() + "&PROD_TYPE="
                    + PRODTYPE + "&BEGIN_DATE=&END_DATE=&SERV_NO=&ValueType=1&REFRESH_FLAG=1&FIND_TYPE=1&radioQryType=on&QRY_FLAG=1&ACCT_DATE="
                    + sf.format(c.getTime()) + "&ACCT_DATE_1=" + sf.format(c.getTime());
            response = TaskHttpClient.create(param, RequestType.GET, "he_nan_10000_web_006").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            if (StringUtils.isBlank(response.getPageContent())) {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
            String RAND_TYPE = PatternUtils.group(pageContent, "name=\"RAND_TYPE\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "RAND_TYPE", RAND_TYPE);
            String BureauCode = PatternUtils.group(pageContent, "name=\"BureauCode\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "BureauCode", BureauCode);
            String REFRESH_FLAG = PatternUtils.group(pageContent, "name=\"REFRESH_FLAG\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "REFRESH_FLAG", REFRESH_FLAG);
            String ACCT_DATE = PatternUtils.group(pageContent, "name=\"ACCT_DATE\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "ACCT_DATE", ACCT_DATE);
            String QRY_FLAG = PatternUtils.group(pageContent, "name=\"QRY_FLAG\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "QRY_FLAG", QRY_FLAG);
            String ValueType = PatternUtils.group(pageContent, "name=\"ValueType\" value=\"(\\d+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "ValueType", ValueType);
            String OPER_TYPE = PatternUtils.group(pageContent, "name=\"OPER_TYPE\" value=\"(\\w+)\"", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "OPER_TYPE", OPER_TYPE);
            if (StringUtils.isBlank(PRODTYPE) || StringUtils.isBlank(RAND_TYPE) || StringUtils.isBlank(BureauCode)) {
                logger.error("详单-->短信验证码-->刷新失败,param={},pateContent={}", param, "param_PRODTYPE is null or param_RAND_TYPE is null or param_BureauCode is null");
                return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
            referer = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha";
            templateUrl = "http://ha.189.cn/service/bill/getRand.jsp\"PRODTYPE=" + PRODTYPE + "&RAND_TYPE=" + RAND_TYPE
                    + "&BureauCode=" + BureauCode + "&ACC_NBR=" + param.getMobile() + "&PROD_TYPE=" + PRODTYPE + "&PROD_PWD=&REFRESH_FLAG="
                    + REFRESH_FLAG + "&BEGIN_DATE=&END_DATE=&ACCT_DATE=" + ACCT_DATE + "&FIND_TYPE=1&SERV_NO=&QRY_FLAG=" + QRY_FLAG
                    + "&ValueType=" + ValueType + "&MOBILE_NAME=" + param.getMobile() + "&OPER_TYPE=" + OPER_TYPE + "&PASSWORD=";
            response = TaskHttpClient.create(param, RequestType.GET, "he_nan_10000_web_007").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "<flag>0</flag>")) {
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
            String PRODTYPE = TaskUtils.getTaskShare(param.getTaskId(), "PRODTYPE");
            String RAND_TYPE = TaskUtils.getTaskShare(param.getTaskId(), "RAND_TYPE");
            String BureauCode = TaskUtils.getTaskShare(param.getTaskId(), "BureauCode");
            String REFRESH_FLAG = TaskUtils.getTaskShare(param.getTaskId(), "REFRESH_FLAG");
            String ACCT_DATE = TaskUtils.getTaskShare(param.getTaskId(), "ACCT_DATE");
            String QRY_FLAG = TaskUtils.getTaskShare(param.getTaskId(), "QRY_FLAG");
            String ValueType = TaskUtils.getTaskShare(param.getTaskId(), "ValueType");
            String OPER_TYPE = TaskUtils.getTaskShare(param.getTaskId(), "OPER_TYPE");

            String referer = "http://ha.189.cn/service/iframe/feeQuery_iframe.jsp?SERV_NO=FSE-2-2&fastcode=20000356&cityCode=ha";
            String templateUrl = "http://ha.189.cn/service/iframe/bill/iframe_inxxall.jsp\"PRODTYPE=" + PRODTYPE + "&RAND_TYPE=" + RAND_TYPE
                    + "&BureauCode=" + BureauCode + "&ACC_NBR=" + param.getMobile() + "&PROD_TYPE=" + PRODTYPE + "&PROD_PWD=&REFRESH_FLAG="
                    + REFRESH_FLAG + "&BEGIN_DATE=&END_DATE=&ACCT_DATE=" + ACCT_DATE + "&FIND_TYPE=1&SERV_NO=&QRY_FLAG=" + QRY_FLAG
                    + "&ValueType=" + ValueType + "&MOBILE_NAME=" + param.getMobile() + "&OPER_TYPE=" + OPER_TYPE + "&PASSWORD=" + param.getSmsCode();
            response = TaskHttpClient.create(param, RequestType.GET, "he_nan_10000_web_008").setFullUrl(templateUrl).setReferer(referer)
                    .invoke();
            if (StringUtils.contains(response.getPageContent(), "开始时间")) {
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
