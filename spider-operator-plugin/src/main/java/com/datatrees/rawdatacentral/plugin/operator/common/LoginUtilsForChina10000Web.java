package com.datatrees.rawdatacentral.plugin.operator.common;

import javax.script.Invocable;
import java.net.URLEncoder;
import java.util.Map;

import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.json.JsonPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.operator.service.OperatorPluginService;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/10/18.
 */
public class LoginUtilsForChina10000Web implements OperatorPluginService {

    private static final Logger logger     = LoggerFactory.getLogger(LoginUtilsForChina10000Web.class);

    private static final String javaScript
                                           = "dmFyIENyeXB0b0pTID0gQ3J5cHRvSlMgfHwKZnVuY3Rpb24obiwgdCkgewogICAgdmFyIHUgPSB7fSwKICAgIGYgPSB1LmxpYiA9IHt9LAogICAgbyA9IGZ1bmN0aW9uKCkge30sCiAgICBpID0gZi5CYXNlID0gewogICAgICAgIGV4dGVuZDogZnVuY3Rpb24obikgewogICAgICAgICAgICBvLnByb3RvdHlwZSA9IHRoaXM7CiAgICAgICAgICAgIHZhciB0ID0gbmV3IG87CiAgICAgICAgICAgIHJldHVybiBuICYmIHQubWl4SW4obiksCiAgICAgICAgICAgIHQuaGFzT3duUHJvcGVydHkoImluaXQiKSB8fCAodC5pbml0ID0gZnVuY3Rpb24oKSB7CiAgICAgICAgICAgICAgICB0LiRzdXBlci5pbml0LmFwcGx5KHRoaXMsIGFyZ3VtZW50cykKICAgICAgICAgICAgfSksCiAgICAgICAgICAgIHQuaW5pdC5wcm90b3R5cGUgPSB0LAogICAgICAgICAgICB0LiRzdXBlciA9IHRoaXMsCiAgICAgICAgICAgIHQKICAgICAgICB9LAogICAgICAgIGNyZWF0ZTogZnVuY3Rpb24oKSB7CiAgICAgICAgICAgIHZhciBuID0gdGhpcy5leHRlbmQoKTsKICAgICAgICAgICAgcmV0dXJuIG4uaW5pdC5hcHBseShuLCBhcmd1bWVudHMpLAogICAgICAgICAgICBuCiAgICAgICAgfSwKICAgICAgICBpbml0OiBmdW5jdGlvbigpIHt9LAogICAgICAgIG1peEluOiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIGZvciAodmFyIHQgaW4gbikgbi5oYXNPd25Qcm9wZXJ0eSh0KSAmJiAodGhpc1t0XSA9IG5bdF0pOwogICAgICAgICAgICBuLmhhc093blByb3BlcnR5KCJ0b1N0cmluZyIpICYmICh0aGlzLnRvU3RyaW5nID0gbi50b1N0cmluZykKICAgICAgICB9LAogICAgICAgIGNsb25lOiBmdW5jdGlvbigpIHsKICAgICAgICAgICAgcmV0dXJuIHRoaXMuaW5pdC5wcm90b3R5cGUuZXh0ZW5kKHRoaXMpCiAgICAgICAgfQogICAgfSwKICAgIHIgPSBmLldvcmRBcnJheSA9IGkuZXh0ZW5kKHsKICAgICAgICBpbml0OiBmdW5jdGlvbihuLCBpKSB7CiAgICAgICAgICAgIG4gPSB0aGlzLndvcmRzID0gbiB8fCBbXTsKICAgICAgICAgICAgdGhpcy5zaWdCeXRlcyA9IGkgIT0gdCA/IGk6IDQgKiBuLmxlbmd0aAogICAgICAgIH0sCiAgICAgICAgdG9TdHJpbmc6IGZ1bmN0aW9uKG4pIHsKICAgICAgICAgICAgcmV0dXJuIChuIHx8IGwpLnN0cmluZ2lmeSh0aGlzKQogICAgICAgIH0sCiAgICAgICAgY29uY2F0OiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIHZhciBpID0gdGhpcy53b3JkcywKICAgICAgICAgICAgciA9IG4ud29yZHMsCiAgICAgICAgICAgIHUgPSB0aGlzLnNpZ0J5dGVzLAogICAgICAgICAgICB0OwogICAgICAgICAgICBpZiAobiA9IG4uc2lnQnl0ZXMsIHRoaXMuY2xhbXAoKSwgdSAlIDQpIGZvciAodCA9IDA7IHQgPCBuOyB0KyspIGlbdSArIHQgPj4+IDJdIHw9IChyW3QgPj4+IDJdID4+PiAyNCAtIDggKiAodCAlIDQpICYgMjU1KSA8PCAyNCAtIDggKiAoKHUgKyB0KSAlIDQpOwogICAgICAgICAgICBlbHNlIGlmICg2NTUzNSA8IHIubGVuZ3RoKSBmb3IgKHQgPSAwOyB0IDwgbjsgdCArPSA0KSBpW3UgKyB0ID4+PiAyXSA9IHJbdCA+Pj4gMl07CiAgICAgICAgICAgIGVsc2UgaS5wdXNoLmFwcGx5KGksIHIpOwogICAgICAgICAgICByZXR1cm4gdGhpcy5zaWdCeXRlcyArPSBuLAogICAgICAgICAgICB0aGlzCiAgICAgICAgfSwKICAgICAgICBjbGFtcDogZnVuY3Rpb24oKSB7CiAgICAgICAgICAgIHZhciBpID0gdGhpcy53b3JkcywKICAgICAgICAgICAgdCA9IHRoaXMuc2lnQnl0ZXM7CiAgICAgICAgICAgIGlbdCA+Pj4gMl0gJj0gNDI5NDk2NzI5NSA8PCAzMiAtIDggKiAodCAlIDQpOwogICAgICAgICAgICBpLmxlbmd0aCA9IG4uY2VpbCh0IC8gNCkKICAgICAgICB9LAogICAgICAgIGNsb25lOiBmdW5jdGlvbigpIHsKICAgICAgICAgICAgdmFyIG4gPSBpLmNsb25lLmNhbGwodGhpcyk7CiAgICAgICAgICAgIHJldHVybiBuLndvcmRzID0gdGhpcy53b3Jkcy5zbGljZSgwKSwKICAgICAgICAgICAgbgogICAgICAgIH0sCiAgICAgICAgcmFuZG9tOiBmdW5jdGlvbih0KSB7CiAgICAgICAgICAgIGZvciAodmFyIGkgPSBbXSwgdSA9IDA7IHUgPCB0OyB1ICs9IDQpIGkucHVzaCg0Mjk0OTY3Mjk2ICogbi5yYW5kb20oKSB8IDApOwogICAgICAgICAgICByZXR1cm4gbmV3IHIuaW5pdChpLCB0KQogICAgICAgIH0KICAgIH0pLAogICAgZSA9IHUuZW5jID0ge30sCiAgICBsID0gZS5IZXggPSB7CiAgICAgICAgc3RyaW5naWZ5OiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIHZhciB1ID0gbi53b3JkcywKICAgICAgICAgICAgaSwgdCwgcjsKICAgICAgICAgICAgZm9yIChuID0gbi5zaWdCeXRlcywgaSA9IFtdLCB0ID0gMDsgdCA8IG47IHQrKykgciA9IHVbdCA+Pj4gMl0gPj4+IDI0IC0gOCAqICh0ICUgNCkgJiAyNTUsCiAgICAgICAgICAgIGkucHVzaCgociA+Pj4gNCkudG9TdHJpbmcoMTYpKSwKICAgICAgICAgICAgaS5wdXNoKChyICYgMTUpLnRvU3RyaW5nKDE2KSk7CiAgICAgICAgICAgIHJldHVybiBpLmpvaW4oIiIpCiAgICAgICAgfSwKICAgICAgICBwYXJzZTogZnVuY3Rpb24obikgewogICAgICAgICAgICBmb3IgKHZhciBpID0gbi5sZW5ndGgsCiAgICAgICAgICAgIHUgPSBbXSwgdCA9IDA7IHQgPCBpOyB0ICs9IDIpIHVbdCA+Pj4gM10gfD0gcGFyc2VJbnQobi5zdWJzdHIodCwgMiksIDE2KSA8PCAyNCAtIDQgKiAodCAlIDgpOwogICAgICAgICAgICByZXR1cm4gbmV3IHIuaW5pdCh1LCBpIC8gMikKICAgICAgICB9CiAgICB9LAogICAgcyA9IGUuTGF0aW4xID0gewogICAgICAgIHN0cmluZ2lmeTogZnVuY3Rpb24obikgewogICAgICAgICAgICB2YXIgciA9IG4ud29yZHMsCiAgICAgICAgICAgIGksIHQ7CiAgICAgICAgICAgIGZvciAobiA9IG4uc2lnQnl0ZXMsIGkgPSBbXSwgdCA9IDA7IHQgPCBuOyB0KyspIGkucHVzaChTdHJpbmcuZnJvbUNoYXJDb2RlKHJbdCA+Pj4gMl0gPj4+IDI0IC0gOCAqICh0ICUgNCkgJiAyNTUpKTsKICAgICAgICAgICAgcmV0dXJuIGkuam9pbigiIikKICAgICAgICB9LAogICAgICAgIHBhcnNlOiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIGZvciAodmFyIGkgPSBuLmxlbmd0aCwKICAgICAgICAgICAgdSA9IFtdLCB0ID0gMDsgdCA8IGk7IHQrKykgdVt0ID4+PiAyXSB8PSAobi5jaGFyQ29kZUF0KHQpICYgMjU1KSA8PCAyNCAtIDggKiAodCAlIDQpOwogICAgICAgICAgICByZXR1cm4gbmV3IHIuaW5pdCh1LCBpKQogICAgICAgIH0KICAgIH0sCiAgICBhID0gZS5VdGY4ID0gewogICAgICAgIHN0cmluZ2lmeTogZnVuY3Rpb24obikgewogICAgICAgICAgICB0cnkgewogICAgICAgICAgICAgICAgcmV0dXJuIGRlY29kZVVSSUNvbXBvbmVudChlc2NhcGUocy5zdHJpbmdpZnkobikpKQogICAgICAgICAgICB9IGNhdGNoKHQpIHsKICAgICAgICAgICAgICAgIHRocm93IEVycm9yKCJNYWxmb3JtZWQgVVRGLTggZGF0YSIpOwogICAgICAgICAgICB9CiAgICAgICAgfSwKICAgICAgICBwYXJzZTogZnVuY3Rpb24obikgewogICAgICAgICAgICByZXR1cm4gcy5wYXJzZSh1bmVzY2FwZShlbmNvZGVVUklDb21wb25lbnQobikpKQogICAgICAgIH0KICAgIH0sCiAgICBoID0gZi5CdWZmZXJlZEJsb2NrQWxnb3JpdGhtID0gaS5leHRlbmQoewogICAgICAgIHJlc2V0OiBmdW5jdGlvbigpIHsKICAgICAgICAgICAgdGhpcy5fZGF0YSA9IG5ldyByLmluaXQ7CiAgICAgICAgICAgIHRoaXMuX25EYXRhQnl0ZXMgPSAwCiAgICAgICAgfSwKICAgICAgICBfYXBwZW5kOiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgICJzdHJpbmciID09IHR5cGVvZiBuICYmIChuID0gYS5wYXJzZShuKSk7CiAgICAgICAgICAgIHRoaXMuX2RhdGEuY29uY2F0KG4pOwogICAgICAgICAgICB0aGlzLl9uRGF0YUJ5dGVzICs9IG4uc2lnQnl0ZXMKICAgICAgICB9LAogICAgICAgIF9wcm9jZXNzOiBmdW5jdGlvbih0KSB7CiAgICAgICAgICAgIHZhciBmID0gdGhpcy5fZGF0YSwKICAgICAgICAgICAgcyA9IGYud29yZHMsCiAgICAgICAgICAgIHUgPSBmLnNpZ0J5dGVzLAogICAgICAgICAgICBlID0gdGhpcy5ibG9ja1NpemUsCiAgICAgICAgICAgIG8gPSB1IC8gKDQgKiBlKSwKICAgICAgICAgICAgbyA9IHQgPyBuLmNlaWwobykgOiBuLm1heCgobyB8IDApIC0gdGhpcy5fbWluQnVmZmVyU2l6ZSwgMCksCiAgICAgICAgICAgIGk7CiAgICAgICAgICAgIGlmICh0ID0gbyAqIGUsIHUgPSBuLm1pbig0ICogdCwgdSksIHQpIHsKICAgICAgICAgICAgICAgIGZvciAoaSA9IDA7IGkgPCB0OyBpICs9IGUpIHRoaXMuX2RvUHJvY2Vzc0Jsb2NrKHMsIGkpOwogICAgICAgICAgICAgICAgaSA9IHMuc3BsaWNlKDAsIHQpOwogICAgICAgICAgICAgICAgZi5zaWdCeXRlcyAtPSB1CiAgICAgICAgICAgIH0KICAgICAgICAgICAgcmV0dXJuIG5ldyByLmluaXQoaSwgdSkKICAgICAgICB9LAogICAgICAgIGNsb25lOiBmdW5jdGlvbigpIHsKICAgICAgICAgICAgdmFyIG4gPSBpLmNsb25lLmNhbGwodGhpcyk7CiAgICAgICAgICAgIHJldHVybiBuLl9kYXRhID0gdGhpcy5fZGF0YS5jbG9uZSgpLAogICAgICAgICAgICBuCiAgICAgICAgfSwKICAgICAgICBfbWluQnVmZmVyU2l6ZTogMAogICAgfSksCiAgICBjOwogICAgcmV0dXJuIGYuSGFzaGVyID0gaC5leHRlbmQoewogICAgICAgIGNmZzogaS5leHRlbmQoKSwKICAgICAgICBpbml0OiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIHRoaXMuY2ZnID0gdGhpcy5jZmcuZXh0ZW5kKG4pOwogICAgICAgICAgICB0aGlzLnJlc2V0KCkKICAgICAgICB9LAogICAgICAgIHJlc2V0OiBmdW5jdGlvbigpIHsKICAgICAgICAgICAgaC5yZXNldC5jYWxsKHRoaXMpOwogICAgICAgICAgICB0aGlzLl9kb1Jlc2V0KCkKICAgICAgICB9LAogICAgICAgIHVwZGF0ZTogZnVuY3Rpb24obikgewogICAgICAgICAgICByZXR1cm4gdGhpcy5fYXBwZW5kKG4pLAogICAgICAgICAgICB0aGlzLl9wcm9jZXNzKCksCiAgICAgICAgICAgIHRoaXMKICAgICAgICB9LAogICAgICAgIGZpbmFsaXplOiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIHJldHVybiBuICYmIHRoaXMuX2FwcGVuZChuKSwKICAgICAgICAgICAgdGhpcy5fZG9GaW5hbGl6ZSgpCiAgICAgICAgfSwKICAgICAgICBibG9ja1NpemU6IDE2LAogICAgICAgIF9jcmVhdGVIZWxwZXI6IGZ1bmN0aW9uKG4pIHsKICAgICAgICAgICAgcmV0dXJuIGZ1bmN0aW9uKHQsIGkpIHsKICAgICAgICAgICAgICAgIHJldHVybiBuZXcgbi5pbml0KGkpLmZpbmFsaXplKHQpCiAgICAgICAgICAgIH0KICAgICAgICB9LAogICAgICAgIF9jcmVhdGVIbWFjSGVscGVyOiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIHJldHVybiBmdW5jdGlvbih0LCBpKSB7CiAgICAgICAgICAgICAgICByZXR1cm4gbmV3IGMuSE1BQy5pbml0KG4sIGkpLmZpbmFsaXplKHQpCiAgICAgICAgICAgIH0KICAgICAgICB9CiAgICB9KSwKICAgIGMgPSB1LmFsZ28gPSB7fSwKICAgIHUKfSAoTWF0aCk7IChmdW5jdGlvbigpIHsKICAgIHZhciBuID0gQ3J5cHRvSlMsCiAgICB0ID0gbi5saWIuV29yZEFycmF5OwogICAgbi5lbmMuQmFzZTY0ID0gewogICAgICAgIHN0cmluZ2lmeTogZnVuY3Rpb24obikgewogICAgICAgICAgICB2YXIgaSA9IG4ud29yZHMsCiAgICAgICAgICAgIHUgPSBuLnNpZ0J5dGVzLAogICAgICAgICAgICBmID0gdGhpcy5fbWFwLAogICAgICAgICAgICB0LCBlLCByOwogICAgICAgICAgICBmb3IgKG4uY2xhbXAoKSwgbiA9IFtdLCB0ID0gMDsgdCA8IHU7IHQgKz0gMykgZm9yIChlID0gKGlbdCA+Pj4gMl0gPj4+IDI0IC0gOCAqICh0ICUgNCkgJiAyNTUpIDw8IDE2IHwgKGlbdCArIDEgPj4+IDJdID4+PiAyNCAtIDggKiAoKHQgKyAxKSAlIDQpICYgMjU1KSA8PCA4IHwgaVt0ICsgMiA+Pj4gMl0gPj4+IDI0IC0gOCAqICgodCArIDIpICUgNCkgJiAyNTUsIHIgPSAwOyA0ID4gciAmJiB0ICsgLjc1ICogciA8IHU7IHIrKykgbi5wdXNoKGYuY2hhckF0KGUgPj4+IDYgKiAoMyAtIHIpICYgNjMpKTsKICAgICAgICAgICAgaWYgKGkgPSBmLmNoYXJBdCg2NCkpIGZvciAoOyBuLmxlbmd0aCAlIDQ7KSBuLnB1c2goaSk7CiAgICAgICAgICAgIHJldHVybiBuLmpvaW4oIiIpCiAgICAgICAgfSwKICAgICAgICBwYXJzZTogZnVuY3Rpb24obikgewogICAgICAgICAgICB2YXIgZSA9IG4ubGVuZ3RoLAogICAgICAgICAgICBmID0gdGhpcy5fbWFwLAogICAgICAgICAgICBpID0gZi5jaGFyQXQoNjQpLAogICAgICAgICAgICBvLAogICAgICAgICAgICBzOwogICAgICAgICAgICBpICYmIChpID0gbi5pbmRleE9mKGkpLCAtMSAhPSBpICYmIChlID0gaSkpOwogICAgICAgICAgICBmb3IgKHZhciBpID0gW10sIHUgPSAwLCByID0gMDsgciA8IGU7IHIrKykgciAlIDQgJiYgKG8gPSBmLmluZGV4T2Yobi5jaGFyQXQociAtIDEpKSA8PCAyICogKHIgJSA0KSwgcyA9IGYuaW5kZXhPZihuLmNoYXJBdChyKSkgPj4+IDYgLSAyICogKHIgJSA0KSwgaVt1ID4+PiAyXSB8PSAobyB8IHMpIDw8IDI0IC0gOCAqICh1ICUgNCksIHUrKyk7CiAgICAgICAgICAgIHJldHVybiB0LmNyZWF0ZShpLCB1KQogICAgICAgIH0sCiAgICAgICAgX21hcDogIkFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXowMTIzNDU2Nzg5Ky89IgogICAgfQp9KSgpLApmdW5jdGlvbihuKSB7CiAgICBmdW5jdGlvbiBpKG4sIHQsIGksIHIsIHUsIGYsIGUpIHsKICAgICAgICByZXR1cm4gbiA9IG4gKyAodCAmIGkgfCB+dCAmIHIpICsgdSArIGUsCiAgICAgICAgKG4gPDwgZiB8IG4gPj4+IDMyIC0gZikgKyB0CiAgICB9CiAgICBmdW5jdGlvbiByKG4sIHQsIGksIHIsIHUsIGYsIGUpIHsKICAgICAgICByZXR1cm4gbiA9IG4gKyAodCAmIHIgfCBpICYgfnIpICsgdSArIGUsCiAgICAgICAgKG4gPDwgZiB8IG4gPj4+IDMyIC0gZikgKyB0CiAgICB9CiAgICBmdW5jdGlvbiB1KG4sIHQsIGksIHIsIHUsIGYsIGUpIHsKICAgICAgICByZXR1cm4gbiA9IG4gKyAodCBeIGkgXiByKSArIHUgKyBlLAogICAgICAgIChuIDw8IGYgfCBuID4+PiAzMiAtIGYpICsgdAogICAgfQogICAgZnVuY3Rpb24gZihuLCB0LCBpLCByLCB1LCBmLCBlKSB7CiAgICAgICAgcmV0dXJuIG4gPSBuICsgKGkgXiAodCB8IH5yKSkgKyB1ICsgZSwKICAgICAgICAobiA8PCBmIHwgbiA+Pj4gMzIgLSBmKSArIHQKICAgIH0KICAgIGZvciAodmFyIG8gPSBDcnlwdG9KUywKICAgIGUgPSBvLmxpYiwKICAgIGMgPSBlLldvcmRBcnJheSwKICAgIHMgPSBlLkhhc2hlciwKICAgIGUgPSBvLmFsZ28sCiAgICB0ID0gW10sIGggPSAwOyA2NCA+IGg7IGgrKykgdFtoXSA9IDQyOTQ5NjcyOTYgKiBuLmFicyhuLnNpbihoICsgMSkpIHwgMDsKICAgIGUgPSBlLk1ENSA9IHMuZXh0ZW5kKHsKICAgICAgICBfZG9SZXNldDogZnVuY3Rpb24oKSB7CiAgICAgICAgICAgIHRoaXMuX2hhc2ggPSBuZXcgYy5pbml0KFsxNzMyNTg0MTkzLCA0MDIzMjMzNDE3LCAyNTYyMzgzMTAyLCAyNzE3MzM4NzhdKQogICAgICAgIH0sCiAgICAgICAgX2RvUHJvY2Vzc0Jsb2NrOiBmdW5jdGlvbihuLCBlKSB7CiAgICAgICAgICAgIGZvciAodmFyIHYsIGEsIGwgPSAwOyAxNiA+IGw7IGwrKykgdiA9IGUgKyBsLAogICAgICAgICAgICBhID0gblt2XSwKICAgICAgICAgICAgblt2XSA9IChhIDw8IDggfCBhID4+PiAyNCkgJiAxNjcxMTkzNSB8IChhIDw8IDI0IHwgYSA+Pj4gOCkgJiA0Mjc4MjU1MzYwOwogICAgICAgICAgICB2YXIgbCA9IHRoaXMuX2hhc2gud29yZHMsCiAgICAgICAgICAgIHYgPSBuW2UgKyAwXSwKICAgICAgICAgICAgYSA9IG5bZSArIDFdLAogICAgICAgICAgICB5ID0gbltlICsgMl0sCiAgICAgICAgICAgIHAgPSBuW2UgKyAzXSwKICAgICAgICAgICAgdyA9IG5bZSArIDRdLAogICAgICAgICAgICBiID0gbltlICsgNV0sCiAgICAgICAgICAgIGsgPSBuW2UgKyA2XSwKICAgICAgICAgICAgZCA9IG5bZSArIDddLAogICAgICAgICAgICBnID0gbltlICsgOF0sCiAgICAgICAgICAgIG50ID0gbltlICsgOV0sCiAgICAgICAgICAgIHR0ID0gbltlICsgMTBdLAogICAgICAgICAgICBpdCA9IG5bZSArIDExXSwKICAgICAgICAgICAgcnQgPSBuW2UgKyAxMl0sCiAgICAgICAgICAgIHV0ID0gbltlICsgMTNdLAogICAgICAgICAgICBmdCA9IG5bZSArIDE0XSwKICAgICAgICAgICAgZXQgPSBuW2UgKyAxNV0sCiAgICAgICAgICAgIG8gPSBsWzBdLAogICAgICAgICAgICBzID0gbFsxXSwKICAgICAgICAgICAgaCA9IGxbMl0sCiAgICAgICAgICAgIGMgPSBsWzNdLAogICAgICAgICAgICBvID0gaShvLCBzLCBoLCBjLCB2LCA3LCB0WzBdKSwKICAgICAgICAgICAgYyA9IGkoYywgbywgcywgaCwgYSwgMTIsIHRbMV0pLAogICAgICAgICAgICBoID0gaShoLCBjLCBvLCBzLCB5LCAxNywgdFsyXSksCiAgICAgICAgICAgIHMgPSBpKHMsIGgsIGMsIG8sIHAsIDIyLCB0WzNdKSwKICAgICAgICAgICAgbyA9IGkobywgcywgaCwgYywgdywgNywgdFs0XSksCiAgICAgICAgICAgIGMgPSBpKGMsIG8sIHMsIGgsIGIsIDEyLCB0WzVdKSwKICAgICAgICAgICAgaCA9IGkoaCwgYywgbywgcywgaywgMTcsIHRbNl0pLAogICAgICAgICAgICBzID0gaShzLCBoLCBjLCBvLCBkLCAyMiwgdFs3XSksCiAgICAgICAgICAgIG8gPSBpKG8sIHMsIGgsIGMsIGcsIDcsIHRbOF0pLAogICAgICAgICAgICBjID0gaShjLCBvLCBzLCBoLCBudCwgMTIsIHRbOV0pLAogICAgICAgICAgICBoID0gaShoLCBjLCBvLCBzLCB0dCwgMTcsIHRbMTBdKSwKICAgICAgICAgICAgcyA9IGkocywgaCwgYywgbywgaXQsIDIyLCB0WzExXSksCiAgICAgICAgICAgIG8gPSBpKG8sIHMsIGgsIGMsIHJ0LCA3LCB0WzEyXSksCiAgICAgICAgICAgIGMgPSBpKGMsIG8sIHMsIGgsIHV0LCAxMiwgdFsxM10pLAogICAgICAgICAgICBoID0gaShoLCBjLCBvLCBzLCBmdCwgMTcsIHRbMTRdKSwKICAgICAgICAgICAgcyA9IGkocywgaCwgYywgbywgZXQsIDIyLCB0WzE1XSksCiAgICAgICAgICAgIG8gPSByKG8sIHMsIGgsIGMsIGEsIDUsIHRbMTZdKSwKICAgICAgICAgICAgYyA9IHIoYywgbywgcywgaCwgaywgOSwgdFsxN10pLAogICAgICAgICAgICBoID0gcihoLCBjLCBvLCBzLCBpdCwgMTQsIHRbMThdKSwKICAgICAgICAgICAgcyA9IHIocywgaCwgYywgbywgdiwgMjAsIHRbMTldKSwKICAgICAgICAgICAgbyA9IHIobywgcywgaCwgYywgYiwgNSwgdFsyMF0pLAogICAgICAgICAgICBjID0gcihjLCBvLCBzLCBoLCB0dCwgOSwgdFsyMV0pLAogICAgICAgICAgICBoID0gcihoLCBjLCBvLCBzLCBldCwgMTQsIHRbMjJdKSwKICAgICAgICAgICAgcyA9IHIocywgaCwgYywgbywgdywgMjAsIHRbMjNdKSwKICAgICAgICAgICAgbyA9IHIobywgcywgaCwgYywgbnQsIDUsIHRbMjRdKSwKICAgICAgICAgICAgYyA9IHIoYywgbywgcywgaCwgZnQsIDksIHRbMjVdKSwKICAgICAgICAgICAgaCA9IHIoaCwgYywgbywgcywgcCwgMTQsIHRbMjZdKSwKICAgICAgICAgICAgcyA9IHIocywgaCwgYywgbywgZywgMjAsIHRbMjddKSwKICAgICAgICAgICAgbyA9IHIobywgcywgaCwgYywgdXQsIDUsIHRbMjhdKSwKICAgICAgICAgICAgYyA9IHIoYywgbywgcywgaCwgeSwgOSwgdFsyOV0pLAogICAgICAgICAgICBoID0gcihoLCBjLCBvLCBzLCBkLCAxNCwgdFszMF0pLAogICAgICAgICAgICBzID0gcihzLCBoLCBjLCBvLCBydCwgMjAsIHRbMzFdKSwKICAgICAgICAgICAgbyA9IHUobywgcywgaCwgYywgYiwgNCwgdFszMl0pLAogICAgICAgICAgICBjID0gdShjLCBvLCBzLCBoLCBnLCAxMSwgdFszM10pLAogICAgICAgICAgICBoID0gdShoLCBjLCBvLCBzLCBpdCwgMTYsIHRbMzRdKSwKICAgICAgICAgICAgcyA9IHUocywgaCwgYywgbywgZnQsIDIzLCB0WzM1XSksCiAgICAgICAgICAgIG8gPSB1KG8sIHMsIGgsIGMsIGEsIDQsIHRbMzZdKSwKICAgICAgICAgICAgYyA9IHUoYywgbywgcywgaCwgdywgMTEsIHRbMzddKSwKICAgICAgICAgICAgaCA9IHUoaCwgYywgbywgcywgZCwgMTYsIHRbMzhdKSwKICAgICAgICAgICAgcyA9IHUocywgaCwgYywgbywgdHQsIDIzLCB0WzM5XSksCiAgICAgICAgICAgIG8gPSB1KG8sIHMsIGgsIGMsIHV0LCA0LCB0WzQwXSksCiAgICAgICAgICAgIGMgPSB1KGMsIG8sIHMsIGgsIHYsIDExLCB0WzQxXSksCiAgICAgICAgICAgIGggPSB1KGgsIGMsIG8sIHMsIHAsIDE2LCB0WzQyXSksCiAgICAgICAgICAgIHMgPSB1KHMsIGgsIGMsIG8sIGssIDIzLCB0WzQzXSksCiAgICAgICAgICAgIG8gPSB1KG8sIHMsIGgsIGMsIG50LCA0LCB0WzQ0XSksCiAgICAgICAgICAgIGMgPSB1KGMsIG8sIHMsIGgsIHJ0LCAxMSwgdFs0NV0pLAogICAgICAgICAgICBoID0gdShoLCBjLCBvLCBzLCBldCwgMTYsIHRbNDZdKSwKICAgICAgICAgICAgcyA9IHUocywgaCwgYywgbywgeSwgMjMsIHRbNDddKSwKICAgICAgICAgICAgbyA9IGYobywgcywgaCwgYywgdiwgNiwgdFs0OF0pLAogICAgICAgICAgICBjID0gZihjLCBvLCBzLCBoLCBkLCAxMCwgdFs0OV0pLAogICAgICAgICAgICBoID0gZihoLCBjLCBvLCBzLCBmdCwgMTUsIHRbNTBdKSwKICAgICAgICAgICAgcyA9IGYocywgaCwgYywgbywgYiwgMjEsIHRbNTFdKSwKICAgICAgICAgICAgbyA9IGYobywgcywgaCwgYywgcnQsIDYsIHRbNTJdKSwKICAgICAgICAgICAgYyA9IGYoYywgbywgcywgaCwgcCwgMTAsIHRbNTNdKSwKICAgICAgICAgICAgaCA9IGYoaCwgYywgbywgcywgdHQsIDE1LCB0WzU0XSksCiAgICAgICAgICAgIHMgPSBmKHMsIGgsIGMsIG8sIGEsIDIxLCB0WzU1XSksCiAgICAgICAgICAgIG8gPSBmKG8sIHMsIGgsIGMsIGcsIDYsIHRbNTZdKSwKICAgICAgICAgICAgYyA9IGYoYywgbywgcywgaCwgZXQsIDEwLCB0WzU3XSksCiAgICAgICAgICAgIGggPSBmKGgsIGMsIG8sIHMsIGssIDE1LCB0WzU4XSksCiAgICAgICAgICAgIHMgPSBmKHMsIGgsIGMsIG8sIHV0LCAyMSwgdFs1OV0pLAogICAgICAgICAgICBvID0gZihvLCBzLCBoLCBjLCB3LCA2LCB0WzYwXSksCiAgICAgICAgICAgIGMgPSBmKGMsIG8sIHMsIGgsIGl0LCAxMCwgdFs2MV0pLAogICAgICAgICAgICBoID0gZihoLCBjLCBvLCBzLCB5LCAxNSwgdFs2Ml0pLAogICAgICAgICAgICBzID0gZihzLCBoLCBjLCBvLCBudCwgMjEsIHRbNjNdKTsKICAgICAgICAgICAgbFswXSA9IGxbMF0gKyBvIHwgMDsKICAgICAgICAgICAgbFsxXSA9IGxbMV0gKyBzIHwgMDsKICAgICAgICAgICAgbFsyXSA9IGxbMl0gKyBoIHwgMDsKICAgICAgICAgICAgbFszXSA9IGxbM10gKyBjIHwgMAogICAgICAgIH0sCiAgICAgICAgX2RvRmluYWxpemU6IGZ1bmN0aW9uKCkgewogICAgICAgICAgICB2YXIgdSA9IHRoaXMuX2RhdGEsCiAgICAgICAgICAgIHIgPSB1LndvcmRzLAogICAgICAgICAgICB0ID0gOCAqIHRoaXMuX25EYXRhQnl0ZXMsCiAgICAgICAgICAgIGkgPSA4ICogdS5zaWdCeXRlcywKICAgICAgICAgICAgZjsKICAgICAgICAgICAgZm9yIChyW2kgPj4+IDVdIHw9IDEyOCA8PCAyNCAtIGkgJSAzMiwgZiA9IG4uZmxvb3IodCAvIDQyOTQ5NjcyOTYpLCByWyhpICsgNjQgPj4+IDkgPDwgNCkgKyAxNV0gPSAoZiA8PCA4IHwgZiA+Pj4gMjQpICYgMTY3MTE5MzUgfCAoZiA8PCAyNCB8IGYgPj4+IDgpICYgNDI3ODI1NTM2MCwgclsoaSArIDY0ID4+PiA5IDw8IDQpICsgMTRdID0gKHQgPDwgOCB8IHQgPj4+IDI0KSAmIDE2NzExOTM1IHwgKHQgPDwgMjQgfCB0ID4+PiA4KSAmIDQyNzgyNTUzNjAsIHUuc2lnQnl0ZXMgPSA0ICogKHIubGVuZ3RoICsgMSksIHRoaXMuX3Byb2Nlc3MoKSwgdSA9IHRoaXMuX2hhc2gsIHIgPSB1LndvcmRzLCB0ID0gMDsgNCA+IHQ7IHQrKykgaSA9IHJbdF0sCiAgICAgICAgICAgIHJbdF0gPSAoaSA8PCA4IHwgaSA+Pj4gMjQpICYgMTY3MTE5MzUgfCAoaSA8PCAyNCB8IGkgPj4+IDgpICYgNDI3ODI1NTM2MDsKICAgICAgICAgICAgcmV0dXJuIHUKICAgICAgICB9LAogICAgICAgIGNsb25lOiBmdW5jdGlvbigpIHsKICAgICAgICAgICAgdmFyIG4gPSBzLmNsb25lLmNhbGwodGhpcyk7CiAgICAgICAgICAgIHJldHVybiBuLl9oYXNoID0gdGhpcy5faGFzaC5jbG9uZSgpLAogICAgICAgICAgICBuCiAgICAgICAgfQogICAgfSk7CiAgICBvLk1ENSA9IHMuX2NyZWF0ZUhlbHBlcihlKTsKICAgIG8uSG1hY01ENSA9IHMuX2NyZWF0ZUhtYWNIZWxwZXIoZSkKfSAoTWF0aCksCmZ1bmN0aW9uKCkgewogICAgdmFyIHQgPSBDcnlwdG9KUywKICAgIG4gPSB0LmxpYiwKICAgIGkgPSBuLkJhc2UsCiAgICByID0gbi5Xb3JkQXJyYXksCiAgICBuID0gdC5hbGdvLAogICAgdSA9IG4uRXZwS0RGID0gaS5leHRlbmQoewogICAgICAgIGNmZzogaS5leHRlbmQoewogICAgICAgICAgICBrZXlTaXplOiA0LAogICAgICAgICAgICBoYXNoZXI6IG4uTUQ1LAogICAgICAgICAgICBpdGVyYXRpb25zOiAxCiAgICAgICAgfSksCiAgICAgICAgaW5pdDogZnVuY3Rpb24obikgewogICAgICAgICAgICB0aGlzLmNmZyA9IHRoaXMuY2ZnLmV4dGVuZChuKQogICAgICAgIH0sCiAgICAgICAgY29tcHV0ZTogZnVuY3Rpb24obiwgdCkgewogICAgICAgICAgICBmb3IgKHZhciBpLCBvLCBmID0gdGhpcy5jZmcsCiAgICAgICAgICAgIHUgPSBmLmhhc2hlci5jcmVhdGUoKSwgZSA9IHIuY3JlYXRlKCksIGggPSBlLndvcmRzLCBzID0gZi5rZXlTaXplLCBmID0gZi5pdGVyYXRpb25zOyBoLmxlbmd0aCA8IHM7KSB7CiAgICAgICAgICAgICAgICBmb3IgKGkgJiYgdS51cGRhdGUoaSksIGkgPSB1LnVwZGF0ZShuKS5maW5hbGl6ZSh0KSwgdS5yZXNldCgpLCBvID0gMTsgbyA8IGY7IG8rKykgaSA9IHUuZmluYWxpemUoaSksCiAgICAgICAgICAgICAgICB1LnJlc2V0KCk7CiAgICAgICAgICAgICAgICBlLmNvbmNhdChpKQogICAgICAgICAgICB9CiAgICAgICAgICAgIHJldHVybiBlLnNpZ0J5dGVzID0gNCAqIHMsCiAgICAgICAgICAgIGUKICAgICAgICB9CiAgICB9KTsKICAgIHQuRXZwS0RGID0gZnVuY3Rpb24obiwgdCwgaSkgewogICAgICAgIHJldHVybiB1LmNyZWF0ZShpKS5jb21wdXRlKG4sIHQpCiAgICB9Cn0gKCk7CkNyeXB0b0pTLmxpYi5DaXBoZXIgfHwKZnVuY3Rpb24obikgewogICAgdmFyIGkgPSBDcnlwdG9KUywKICAgIHQgPSBpLmxpYiwKICAgIGYgPSB0LkJhc2UsCiAgICBlID0gdC5Xb3JkQXJyYXksCiAgICBjID0gdC5CdWZmZXJlZEJsb2NrQWxnb3JpdGhtLAogICAgbCA9IGkuZW5jLkJhc2U2NCwKICAgIHkgPSBpLmFsZ28uRXZwS0RGLAogICAgbyA9IHQuQ2lwaGVyID0gYy5leHRlbmQoewogICAgICAgIGNmZzogZi5leHRlbmQoKSwKICAgICAgICBjcmVhdGVFbmNyeXB0b3I6IGZ1bmN0aW9uKG4sIHQpIHsKICAgICAgICAgICAgcmV0dXJuIHRoaXMuY3JlYXRlKHRoaXMuX0VOQ19YRk9STV9NT0RFLCBuLCB0KQogICAgICAgIH0sCiAgICAgICAgY3JlYXRlRGVjcnlwdG9yOiBmdW5jdGlvbihuLCB0KSB7CiAgICAgICAgICAgIHJldHVybiB0aGlzLmNyZWF0ZSh0aGlzLl9ERUNfWEZPUk1fTU9ERSwgbiwgdCkKICAgICAgICB9LAogICAgICAgIGluaXQ6IGZ1bmN0aW9uKG4sIHQsIGkpIHsKICAgICAgICAgICAgdGhpcy5jZmcgPSB0aGlzLmNmZy5leHRlbmQoaSk7CiAgICAgICAgICAgIHRoaXMuX3hmb3JtTW9kZSA9IG47CiAgICAgICAgICAgIHRoaXMuX2tleSA9IHQ7CiAgICAgICAgICAgIHRoaXMucmVzZXQoKQogICAgICAgIH0sCiAgICAgICAgcmVzZXQ6IGZ1bmN0aW9uKCkgewogICAgICAgICAgICBjLnJlc2V0LmNhbGwodGhpcyk7CiAgICAgICAgICAgIHRoaXMuX2RvUmVzZXQoKQogICAgICAgIH0sCiAgICAgICAgcHJvY2VzczogZnVuY3Rpb24obikgewogICAgICAgICAgICByZXR1cm4gdGhpcy5fYXBwZW5kKG4pLAogICAgICAgICAgICB0aGlzLl9wcm9jZXNzKCkKICAgICAgICB9LAogICAgICAgIGZpbmFsaXplOiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIHJldHVybiBuICYmIHRoaXMuX2FwcGVuZChuKSwKICAgICAgICAgICAgdGhpcy5fZG9GaW5hbGl6ZSgpCiAgICAgICAgfSwKICAgICAgICBrZXlTaXplOiA0LAogICAgICAgIGl2U2l6ZTogNCwKICAgICAgICBfRU5DX1hGT1JNX01PREU6IDEsCiAgICAgICAgX0RFQ19YRk9STV9NT0RFOiAyLAogICAgICAgIF9jcmVhdGVIZWxwZXI6IGZ1bmN0aW9uKG4pIHsKICAgICAgICAgICAgcmV0dXJuIHsKICAgICAgICAgICAgICAgIGVuY3J5cHQ6IGZ1bmN0aW9uKHQsIGksIHIpIHsKICAgICAgICAgICAgICAgICAgICByZXR1cm4gKCJzdHJpbmciID09IHR5cGVvZiBpID8gdjogdSkuZW5jcnlwdChuLCB0LCBpLCByKQogICAgICAgICAgICAgICAgfSwKICAgICAgICAgICAgICAgIGRlY3J5cHQ6IGZ1bmN0aW9uKHQsIGksIHIpIHsKICAgICAgICAgICAgICAgICAgICByZXR1cm4gKCJzdHJpbmciID09IHR5cGVvZiBpID8gdjogdSkuZGVjcnlwdChuLCB0LCBpLCByKQogICAgICAgICAgICAgICAgfQogICAgICAgICAgICB9CiAgICAgICAgfQogICAgfSk7CiAgICB0LlN0cmVhbUNpcGhlciA9IG8uZXh0ZW5kKHsKICAgICAgICBfZG9GaW5hbGl6ZTogZnVuY3Rpb24oKSB7CiAgICAgICAgICAgIHJldHVybiB0aGlzLl9wcm9jZXNzKCEwKQogICAgICAgIH0sCiAgICAgICAgYmxvY2tTaXplOiAxCiAgICB9KTsKICAgIHZhciBzID0gaS5tb2RlID0ge30sCiAgICBhID0gZnVuY3Rpb24odCwgaSwgcikgewogICAgICAgIHZhciBmID0gdGhpcy5faXYsCiAgICAgICAgdTsKICAgICAgICBmb3IgKGYgPyB0aGlzLl9pdiA9IG46IGYgPSB0aGlzLl9wcmV2QmxvY2ssIHUgPSAwOyB1IDwgcjsgdSsrKSB0W2kgKyB1XSBePSBmW3VdCiAgICB9LAogICAgciA9ICh0LkJsb2NrQ2lwaGVyTW9kZSA9IGYuZXh0ZW5kKHsKICAgICAgICBjcmVhdGVFbmNyeXB0b3I6IGZ1bmN0aW9uKG4sIHQpIHsKICAgICAgICAgICAgcmV0dXJuIHRoaXMuRW5jcnlwdG9yLmNyZWF0ZShuLCB0KQogICAgICAgIH0sCiAgICAgICAgY3JlYXRlRGVjcnlwdG9yOiBmdW5jdGlvbihuLCB0KSB7CiAgICAgICAgICAgIHJldHVybiB0aGlzLkRlY3J5cHRvci5jcmVhdGUobiwgdCkKICAgICAgICB9LAogICAgICAgIGluaXQ6IGZ1bmN0aW9uKG4sIHQpIHsKICAgICAgICAgICAgdGhpcy5fY2lwaGVyID0gbjsKICAgICAgICAgICAgdGhpcy5faXYgPSB0CiAgICAgICAgfQogICAgfSkpLmV4dGVuZCgpOwogICAgci5FbmNyeXB0b3IgPSByLmV4dGVuZCh7CiAgICAgICAgcHJvY2Vzc0Jsb2NrOiBmdW5jdGlvbihuLCB0KSB7CiAgICAgICAgICAgIHZhciBpID0gdGhpcy5fY2lwaGVyLAogICAgICAgICAgICByID0gaS5ibG9ja1NpemU7CiAgICAgICAgICAgIGEuY2FsbCh0aGlzLCBuLCB0LCByKTsKICAgICAgICAgICAgaS5lbmNyeXB0QmxvY2sobiwgdCk7CiAgICAgICAgICAgIHRoaXMuX3ByZXZCbG9jayA9IG4uc2xpY2UodCwgdCArIHIpCiAgICAgICAgfQogICAgfSk7CiAgICByLkRlY3J5cHRvciA9IHIuZXh0ZW5kKHsKICAgICAgICBwcm9jZXNzQmxvY2s6IGZ1bmN0aW9uKG4sIHQpIHsKICAgICAgICAgICAgdmFyIGkgPSB0aGlzLl9jaXBoZXIsCiAgICAgICAgICAgIHIgPSBpLmJsb2NrU2l6ZSwKICAgICAgICAgICAgdSA9IG4uc2xpY2UodCwgdCArIHIpOwogICAgICAgICAgICBpLmRlY3J5cHRCbG9jayhuLCB0KTsKICAgICAgICAgICAgYS5jYWxsKHRoaXMsIG4sIHQsIHIpOwogICAgICAgICAgICB0aGlzLl9wcmV2QmxvY2sgPSB1CiAgICAgICAgfQogICAgfSk7CiAgICBzID0gcy5DQkMgPSByOwogICAgciA9IChpLnBhZCA9IHt9KS5Qa2NzNyA9IHsKICAgICAgICBwYWQ6IGZ1bmN0aW9uKG4sIHQpIHsKICAgICAgICAgICAgZm9yICh2YXIgaSA9IDQgKiB0LAogICAgICAgICAgICBpID0gaSAtIG4uc2lnQnl0ZXMgJSBpLAogICAgICAgICAgICBmID0gaSA8PCAyNCB8IGkgPDwgMTYgfCBpIDw8IDggfCBpLAogICAgICAgICAgICByID0gW10sIHUgPSAwOyB1IDwgaTsgdSArPSA0KSByLnB1c2goZik7CiAgICAgICAgICAgIGkgPSBlLmNyZWF0ZShyLCBpKTsKICAgICAgICAgICAgbi5jb25jYXQoaSkKICAgICAgICB9LAogICAgICAgIHVucGFkOiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIG4uc2lnQnl0ZXMgLT0gbi53b3Jkc1tuLnNpZ0J5dGVzIC0gMSA+Pj4gMl0gJiAyNTUKICAgICAgICB9CiAgICB9OwogICAgdC5CbG9ja0NpcGhlciA9IG8uZXh0ZW5kKHsKICAgICAgICBjZmc6IG8uY2ZnLmV4dGVuZCh7CiAgICAgICAgICAgIG1vZGU6IHMsCiAgICAgICAgICAgIHBhZGRpbmc6IHIKICAgICAgICB9KSwKICAgICAgICByZXNldDogZnVuY3Rpb24oKSB7CiAgICAgICAgICAgIHZhciB0OwogICAgICAgICAgICBvLnJlc2V0LmNhbGwodGhpcyk7CiAgICAgICAgICAgIHZhciBuID0gdGhpcy5jZmcsCiAgICAgICAgICAgIGkgPSBuLml2LAogICAgICAgICAgICBuID0gbi5tb2RlOwogICAgICAgICAgICB0aGlzLl94Zm9ybU1vZGUgPT0gdGhpcy5fRU5DX1hGT1JNX01PREUgPyB0ID0gbi5jcmVhdGVFbmNyeXB0b3I6ICh0ID0gbi5jcmVhdGVEZWNyeXB0b3IsIHRoaXMuX21pbkJ1ZmZlclNpemUgPSAxKTsKICAgICAgICAgICAgdGhpcy5fbW9kZSA9IHQuY2FsbChuLCB0aGlzLCBpICYmIGkud29yZHMpCiAgICAgICAgfSwKICAgICAgICBfZG9Qcm9jZXNzQmxvY2s6IGZ1bmN0aW9uKG4sIHQpIHsKICAgICAgICAgICAgdGhpcy5fbW9kZS5wcm9jZXNzQmxvY2sobiwgdCkKICAgICAgICB9LAogICAgICAgIF9kb0ZpbmFsaXplOiBmdW5jdGlvbigpIHsKICAgICAgICAgICAgdmFyIHQgPSB0aGlzLmNmZy5wYWRkaW5nLAogICAgICAgICAgICBuOwogICAgICAgICAgICByZXR1cm4gdGhpcy5feGZvcm1Nb2RlID09IHRoaXMuX0VOQ19YRk9STV9NT0RFID8gKHQucGFkKHRoaXMuX2RhdGEsIHRoaXMuYmxvY2tTaXplKSwgbiA9IHRoaXMuX3Byb2Nlc3MoITApKSA6IChuID0gdGhpcy5fcHJvY2VzcyghMCksIHQudW5wYWQobikpLAogICAgICAgICAgICBuCiAgICAgICAgfSwKICAgICAgICBibG9ja1NpemU6IDQKICAgIH0pOwogICAgdmFyIGggPSB0LkNpcGhlclBhcmFtcyA9IGYuZXh0ZW5kKHsKICAgICAgICBpbml0OiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIHRoaXMubWl4SW4obikKICAgICAgICB9LAogICAgICAgIHRvU3RyaW5nOiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIHJldHVybiAobiB8fCB0aGlzLmZvcm1hdHRlcikuc3RyaW5naWZ5KHRoaXMpCiAgICAgICAgfQogICAgfSksCiAgICBzID0gKGkuZm9ybWF0ID0ge30pLk9wZW5TU0wgPSB7CiAgICAgICAgc3RyaW5naWZ5OiBmdW5jdGlvbihuKSB7CiAgICAgICAgICAgIHZhciB0ID0gbi5jaXBoZXJ0ZXh0OwogICAgICAgICAgICByZXR1cm4gbiA9IG4uc2FsdCwKICAgICAgICAgICAgKG4gPyBlLmNyZWF0ZShbMTM5ODg5MzY4NCwgMTcwMTA3NjgzMV0pLmNvbmNhdChuKS5jb25jYXQodCkgOiB0KS50b1N0cmluZyhsKQogICAgICAgIH0sCiAgICAgICAgcGFyc2U6IGZ1bmN0aW9uKG4pIHsKICAgICAgICAgICAgdmFyIHQsIGk7CiAgICAgICAgICAgIHJldHVybiBuID0gbC5wYXJzZShuKSwKICAgICAgICAgICAgdCA9IG4ud29yZHMsCiAgICAgICAgICAgIDEzOTg4OTM2ODQgPT0gdFswXSAmJiAxNzAxMDc2ODMxID09IHRbMV0gJiYgKGkgPSBlLmNyZWF0ZSh0LnNsaWNlKDIsIDQpKSwgdC5zcGxpY2UoMCwgNCksIG4uc2lnQnl0ZXMgLT0gMTYpLAogICAgICAgICAgICBoLmNyZWF0ZSh7CiAgICAgICAgICAgICAgICBjaXBoZXJ0ZXh0OiBuLAogICAgICAgICAgICAgICAgc2FsdDogaQogICAgICAgICAgICB9KQogICAgICAgIH0KICAgIH0sCiAgICB1ID0gdC5TZXJpYWxpemFibGVDaXBoZXIgPSBmLmV4dGVuZCh7CiAgICAgICAgY2ZnOiBmLmV4dGVuZCh7CiAgICAgICAgICAgIGZvcm1hdDogcwogICAgICAgIH0pLAogICAgICAgIGVuY3J5cHQ6IGZ1bmN0aW9uKG4sIHQsIGksIHIpIHsKICAgICAgICAgICAgciA9IHRoaXMuY2ZnLmV4dGVuZChyKTsKICAgICAgICAgICAgdmFyIHUgPSBuLmNyZWF0ZUVuY3J5cHRvcihpLCByKTsKICAgICAgICAgICAgcmV0dXJuIHQgPSB1LmZpbmFsaXplKHQpLAogICAgICAgICAgICB1ID0gdS5jZmcsCiAgICAgICAgICAgIGguY3JlYXRlKHsKICAgICAgICAgICAgICAgIGNpcGhlcnRleHQ6IHQsCiAgICAgICAgICAgICAgICBrZXk6IGksCiAgICAgICAgICAgICAgICBpdjogdS5pdiwKICAgICAgICAgICAgICAgIGFsZ29yaXRobTogbiwKICAgICAgICAgICAgICAgIG1vZGU6IHUubW9kZSwKICAgICAgICAgICAgICAgIHBhZGRpbmc6IHUucGFkZGluZywKICAgICAgICAgICAgICAgIGJsb2NrU2l6ZTogbi5ibG9ja1NpemUsCiAgICAgICAgICAgICAgICBmb3JtYXR0ZXI6IHIuZm9ybWF0CiAgICAgICAgICAgIH0pCiAgICAgICAgfSwKICAgICAgICBkZWNyeXB0OiBmdW5jdGlvbihuLCB0LCBpLCByKSB7CiAgICAgICAgICAgIHJldHVybiByID0gdGhpcy5jZmcuZXh0ZW5kKHIpLAogICAgICAgICAgICB0ID0gdGhpcy5fcGFyc2UodCwgci5mb3JtYXQpLAogICAgICAgICAgICBuLmNyZWF0ZURlY3J5cHRvcihpLCByKS5maW5hbGl6ZSh0LmNpcGhlcnRleHQpCiAgICAgICAgfSwKICAgICAgICBfcGFyc2U6IGZ1bmN0aW9uKG4sIHQpIHsKICAgICAgICAgICAgcmV0dXJuICJzdHJpbmciID09IHR5cGVvZiBuID8gdC5wYXJzZShuLCB0aGlzKSA6IG4KICAgICAgICB9CiAgICB9KSwKICAgIGkgPSAoaS5rZGYgPSB7fSkuT3BlblNTTCA9IHsKICAgICAgICBleGVjdXRlOiBmdW5jdGlvbihuLCB0LCBpLCByKSB7CiAgICAgICAgICAgIHJldHVybiByIHx8IChyID0gZS5yYW5kb20oOCkpLAogICAgICAgICAgICBuID0geS5jcmVhdGUoewogICAgICAgICAgICAgICAga2V5U2l6ZTogdCArIGkKICAgICAgICAgICAgfSkuY29tcHV0ZShuLCByKSwKICAgICAgICAgICAgaSA9IGUuY3JlYXRlKG4ud29yZHMuc2xpY2UodCksIDQgKiBpKSwKICAgICAgICAgICAgbi5zaWdCeXRlcyA9IDQgKiB0LAogICAgICAgICAgICBoLmNyZWF0ZSh7CiAgICAgICAgICAgICAgICBrZXk6IG4sCiAgICAgICAgICAgICAgICBpdjogaSwKICAgICAgICAgICAgICAgIHNhbHQ6IHIKICAgICAgICAgICAgfSkKICAgICAgICB9CiAgICB9LAogICAgdiA9IHQuUGFzc3dvcmRCYXNlZENpcGhlciA9IHUuZXh0ZW5kKHsKICAgICAgICBjZmc6IHUuY2ZnLmV4dGVuZCh7CiAgICAgICAgICAgIGtkZjogaQogICAgICAgIH0pLAogICAgICAgIGVuY3J5cHQ6IGZ1bmN0aW9uKG4sIHQsIGksIHIpIHsKICAgICAgICAgICAgcmV0dXJuIHIgPSB0aGlzLmNmZy5leHRlbmQociksCiAgICAgICAgICAgIGkgPSByLmtkZi5leGVjdXRlKGksIG4ua2V5U2l6ZSwgbi5pdlNpemUpLAogICAgICAgICAgICByLml2ID0gaS5pdiwKICAgICAgICAgICAgbiA9IHUuZW5jcnlwdC5jYWxsKHRoaXMsIG4sIHQsIGkua2V5LCByKSwKICAgICAgICAgICAgbi5taXhJbihpKSwKICAgICAgICAgICAgbgogICAgICAgIH0sCiAgICAgICAgZGVjcnlwdDogZnVuY3Rpb24obiwgdCwgaSwgcikgewogICAgICAgICAgICByZXR1cm4gciA9IHRoaXMuY2ZnLmV4dGVuZChyKSwKICAgICAgICAgICAgdCA9IHRoaXMuX3BhcnNlKHQsIHIuZm9ybWF0KSwKICAgICAgICAgICAgaSA9IHIua2RmLmV4ZWN1dGUoaSwgbi5rZXlTaXplLCBuLml2U2l6ZSwgdC5zYWx0KSwKICAgICAgICAgICAgci5pdiA9IGkuaXYsCiAgICAgICAgICAgIHUuZGVjcnlwdC5jYWxsKHRoaXMsIG4sIHQsIGkua2V5LCByKQogICAgICAgIH0KICAgIH0pCn0gKCksCmZ1bmN0aW9uKCkgewogICAgZm9yICh2YXIgaSwgdHQsIHMgPSBDcnlwdG9KUywKICAgIHkgPSBzLmxpYi5CbG9ja0NpcGhlciwKICAgIGggPSBzLmFsZ28sCiAgICB0ID0gW10sIHAgPSBbXSwgdyA9IFtdLCBiID0gW10sIGsgPSBbXSwgZCA9IFtdLCBjID0gW10sIGwgPSBbXSwgYSA9IFtdLCB2ID0gW10sIHUgPSBbXSwgZiA9IDA7IDI1NiA+IGY7IGYrKykgdVtmXSA9IDEyOCA+IGYgPyBmIDw8IDEgOiBmIDw8IDEgXiAyODM7CiAgICBmb3IgKHZhciByID0gMCwKICAgIGUgPSAwLAogICAgZiA9IDA7IDI1NiA+IGY7IGYrKykgewogICAgICAgIGkgPSBlIF4gZSA8PCAxIF4gZSA8PCAyIF4gZSA8PCAzIF4gZSA8PCA0OwogICAgICAgIGkgPSBpID4+PiA4IF4gaSAmIDI1NSBeIDk5OwogICAgICAgIHRbcl0gPSBpOwogICAgICAgIHBbaV0gPSByOwogICAgICAgIHZhciBvID0gdVtyXSwKICAgICAgICBnID0gdVtvXSwKICAgICAgICBudCA9IHVbZ10sCiAgICAgICAgbiA9IDI1NyAqIHVbaV0gXiAxNjg0MzAwOCAqIGk7CiAgICAgICAgd1tyXSA9IG4gPDwgMjQgfCBuID4+PiA4OwogICAgICAgIGJbcl0gPSBuIDw8IDE2IHwgbiA+Pj4gMTY7CiAgICAgICAga1tyXSA9IG4gPDwgOCB8IG4gPj4+IDI0OwogICAgICAgIGRbcl0gPSBuOwogICAgICAgIG4gPSAxNjg0MzAwOSAqIG50IF4gNjU1MzcgKiBnIF4gMjU3ICogbyBeIDE2ODQzMDA4ICogcjsKICAgICAgICBjW2ldID0gbiA8PCAyNCB8IG4gPj4+IDg7CiAgICAgICAgbFtpXSA9IG4gPDwgMTYgfCBuID4+PiAxNjsKICAgICAgICBhW2ldID0gbiA8PCA4IHwgbiA+Pj4gMjQ7CiAgICAgICAgdltpXSA9IG47CiAgICAgICAgciA/IChyID0gbyBeIHVbdVt1W250IF4gb11dXSwgZSBePSB1W3VbZV1dKSA6IHIgPSBlID0gMQogICAgfQogICAgdHQgPSBbMCwgMSwgMiwgNCwgOCwgMTYsIDMyLCA2NCwgMTI4LCAyNywgNTRdOwogICAgaCA9IGguQUVTID0geS5leHRlbmQoewogICAgICAgIF9kb1Jlc2V0OiBmdW5jdGlvbigpIHsKICAgICAgICAgICAgZm9yICh2YXIgbiwgZiA9IHRoaXMuX2tleSwKICAgICAgICAgICAgZSA9IGYud29yZHMsCiAgICAgICAgICAgIHIgPSBmLnNpZ0J5dGVzIC8gNCwKICAgICAgICAgICAgZiA9IDQgKiAoKHRoaXMuX25Sb3VuZHMgPSByICsgNikgKyAxKSwgdSA9IHRoaXMuX2tleVNjaGVkdWxlID0gW10sIGkgPSAwOyBpIDwgZjsgaSsrKSBpIDwgciA/IHVbaV0gPSBlW2ldIDogKG4gPSB1W2kgLSAxXSwgaSAlIHIgPyA2IDwgciAmJiA0ID09IGkgJSByICYmIChuID0gdFtuID4+PiAyNF0gPDwgMjQgfCB0W24gPj4+IDE2ICYgMjU1XSA8PCAxNiB8IHRbbiA+Pj4gOCAmIDI1NV0gPDwgOCB8IHRbbiAmIDI1NV0pIDogKG4gPSBuIDw8IDggfCBuID4+PiAyNCwgbiA9IHRbbiA+Pj4gMjRdIDw8IDI0IHwgdFtuID4+PiAxNiAmIDI1NV0gPDwgMTYgfCB0W24gPj4+IDggJiAyNTVdIDw8IDggfCB0W24gJiAyNTVdLCBuIF49IHR0W2kgLyByIHwgMF0gPDwgMjQpLCB1W2ldID0gdVtpIC0gcl0gXiBuKTsKICAgICAgICAgICAgZm9yIChlID0gdGhpcy5faW52S2V5U2NoZWR1bGUgPSBbXSwgciA9IDA7IHIgPCBmOyByKyspIGkgPSBmIC0gciwKICAgICAgICAgICAgbiA9IHIgJSA0ID8gdVtpXSA6IHVbaSAtIDRdLAogICAgICAgICAgICBlW3JdID0gNCA+IHIgfHwgNCA+PSBpID8gbjogY1t0W24gPj4+IDI0XV0gXiBsW3RbbiA+Pj4gMTYgJiAyNTVdXSBeIGFbdFtuID4+PiA4ICYgMjU1XV0gXiB2W3RbbiAmIDI1NV1dCiAgICAgICAgfSwKICAgICAgICBlbmNyeXB0QmxvY2s6IGZ1bmN0aW9uKG4sIGkpIHsKICAgICAgICAgICAgdGhpcy5fZG9DcnlwdEJsb2NrKG4sIGksIHRoaXMuX2tleVNjaGVkdWxlLCB3LCBiLCBrLCBkLCB0KQogICAgICAgIH0sCiAgICAgICAgZGVjcnlwdEJsb2NrOiBmdW5jdGlvbihuLCB0KSB7CiAgICAgICAgICAgIHZhciBpID0gblt0ICsgMV07CiAgICAgICAgICAgIG5bdCArIDFdID0gblt0ICsgM107CiAgICAgICAgICAgIG5bdCArIDNdID0gaTsKICAgICAgICAgICAgdGhpcy5fZG9DcnlwdEJsb2NrKG4sIHQsIHRoaXMuX2ludktleVNjaGVkdWxlLCBjLCBsLCBhLCB2LCBwKTsKICAgICAgICAgICAgaSA9IG5bdCArIDFdOwogICAgICAgICAgICBuW3QgKyAxXSA9IG5bdCArIDNdOwogICAgICAgICAgICBuW3QgKyAzXSA9IGkKICAgICAgICB9LAogICAgICAgIF9kb0NyeXB0QmxvY2s6IGZ1bmN0aW9uKG4sIHQsIGksIHIsIHUsIGYsIGUsIG8pIHsKICAgICAgICAgICAgZm9yICh2YXIgYiA9IHRoaXMuX25Sb3VuZHMsCiAgICAgICAgICAgIGggPSBuW3RdIF4gaVswXSwgYyA9IG5bdCArIDFdIF4gaVsxXSwgbCA9IG5bdCArIDJdIF4gaVsyXSwgcyA9IG5bdCArIDNdIF4gaVszXSwgYSA9IDQsIHcgPSAxOyB3IDwgYjsgdysrKSB2YXIgdiA9IHJbaCA+Pj4gMjRdIF4gdVtjID4+PiAxNiAmIDI1NV0gXiBmW2wgPj4+IDggJiAyNTVdIF4gZVtzICYgMjU1XSBeIGlbYSsrXSwKICAgICAgICAgICAgeSA9IHJbYyA+Pj4gMjRdIF4gdVtsID4+PiAxNiAmIDI1NV0gXiBmW3MgPj4+IDggJiAyNTVdIF4gZVtoICYgMjU1XSBeIGlbYSsrXSwKICAgICAgICAgICAgcCA9IHJbbCA+Pj4gMjRdIF4gdVtzID4+PiAxNiAmIDI1NV0gXiBmW2ggPj4+IDggJiAyNTVdIF4gZVtjICYgMjU1XSBeIGlbYSsrXSwKICAgICAgICAgICAgcyA9IHJbcyA+Pj4gMjRdIF4gdVtoID4+PiAxNiAmIDI1NV0gXiBmW2MgPj4+IDggJiAyNTVdIF4gZVtsICYgMjU1XSBeIGlbYSsrXSwKICAgICAgICAgICAgaCA9IHYsCiAgICAgICAgICAgIGMgPSB5LAogICAgICAgICAgICBsID0gcDsKICAgICAgICAgICAgdiA9IChvW2ggPj4+IDI0XSA8PCAyNCB8IG9bYyA+Pj4gMTYgJiAyNTVdIDw8IDE2IHwgb1tsID4+PiA4ICYgMjU1XSA8PCA4IHwgb1tzICYgMjU1XSkgXiBpW2ErK107CiAgICAgICAgICAgIHkgPSAob1tjID4+PiAyNF0gPDwgMjQgfCBvW2wgPj4+IDE2ICYgMjU1XSA8PCAxNiB8IG9bcyA+Pj4gOCAmIDI1NV0gPDwgOCB8IG9baCAmIDI1NV0pIF4gaVthKytdOwogICAgICAgICAgICBwID0gKG9bbCA+Pj4gMjRdIDw8IDI0IHwgb1tzID4+PiAxNiAmIDI1NV0gPDwgMTYgfCBvW2ggPj4+IDggJiAyNTVdIDw8IDggfCBvW2MgJiAyNTVdKSBeIGlbYSsrXTsKICAgICAgICAgICAgcyA9IChvW3MgPj4+IDI0XSA8PCAyNCB8IG9baCA+Pj4gMTYgJiAyNTVdIDw8IDE2IHwgb1tjID4+PiA4ICYgMjU1XSA8PCA4IHwgb1tsICYgMjU1XSkgXiBpW2ErK107CiAgICAgICAgICAgIG5bdF0gPSB2OwogICAgICAgICAgICBuW3QgKyAxXSA9IHk7CiAgICAgICAgICAgIG5bdCArIDJdID0gcDsKICAgICAgICAgICAgblt0ICsgM10gPSBzCiAgICAgICAgfSwKICAgICAgICBrZXlTaXplOiA4CiAgICB9KTsKICAgIHMuQUVTID0geS5fY3JlYXRlSGVscGVyKGgpCn0gKCk7CmZ1bmN0aW9uIHZhbEFlc0VuY3J5cHRTZXQoaSkgewogICAgdmFyIG4sdCxyOwogICAgdHJ5IHsKICAgICAgICBuID0gYWVzRGVjcnlwdChpKTsKICAgICAgICBuICE9ICIiICYmICh0ID0gYWVzRW5jcnlwdChuKSwgdCAhPSBpICYmIChuID0gIiIpKQogICAgfSBjYXRjaChyKSB7CiAgICAgICAgbiA9ICIiCiAgICB9CiAgICByZXR1cm4gbiA9PSAiIiAmJiAodCA9IGFlc0VuY3J5cHQoaSkpOwp9OwpmdW5jdGlvbiBhZXNEZWNyeXB0KG4pIHsKICAgIHZhciB0ID0gQ3J5cHRvSlMuTUQ1KCJsb2dpbi4xODkuY24iKSwKICAgIGkgPSBDcnlwdG9KUy5lbmMuVXRmOC5wYXJzZSh0KSwKICAgIHIgPSBDcnlwdG9KUy5lbmMuVXRmOC5wYXJzZSgiMTIzNDU2NzgxMjM0NTY3OCIpOwogICAgcmV0dXJuIENyeXB0b0pTLkFFUy5kZWNyeXB0KG4sIGksIHsKICAgICAgICBpdjogcgogICAgfSkudG9TdHJpbmcoQ3J5cHRvSlMuZW5jLlV0ZjgpCn07CmZ1bmN0aW9uIGFlc0VuY3J5cHQobikgewogICAgdmFyIHQgPSBDcnlwdG9KUy5NRDUoImxvZ2luLjE4OS5jbiIpLAogICAgaSA9IENyeXB0b0pTLmVuYy5VdGY4LnBhcnNlKHQpLAogICAgciA9IENyeXB0b0pTLmVuYy5VdGY4LnBhcnNlKCIxMjM0NTY3ODEyMzQ1Njc4IiksCiAgICB1ID0gQ3J5cHRvSlMuQUVTLmVuY3J5cHQobiwgaSwgewogICAgICAgIGl2OiByCiAgICB9KTsKICAgIHJldHVybiB1ICsgIiIKfTs=";

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.189.cn/login/index/ecs.do";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl).invoke();
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return refeshPicCodeForLogin(param);
            default:
                return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        return new HttpResult<Map<String, Object>>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Map<String, Object>> submit(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return submitForLogin(param);
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

    public HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://login.189.cn/web/captcha?undefined&source=login&{}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET).setFullUrl(templateUrl, Math.random())
                    .invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    public HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocableFromBase64(javaScript);
            String encryptPassword = invocable.invokeFunction("aesEncrypt", param.getPassword()).toString();

            String templateUrl = "http://login.189.cn/web/login/ajax";
            String templateData = "m=checkphone&phone={}";
            String data = TemplateUtils.format(templateData, param.getMobile());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            String pageContent = response.getPageContent();
            String provinceId = "01";
            if (pageContent.contains("provinceId")) {
                provinceId = JsonPathUtil.readAsString(pageContent, "$.provinceId");
            } else {
                logger.error("未查到省份ID！");
            }

            templateUrl = "http://login.189.cn/web/login";
            templateData = "Account={}&UType=201&ProvinceID={}&AreaCode=&CityNo=&RandomFlag=0&Password={}&Captcha={}";
            data = TemplateUtils.format(templateData, param.getMobile(), provinceId, URLEncoder.encode(encryptPassword, "UTF-8"),
                    param.getPicCode().toLowerCase());
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.POST).setFullUrl(templateUrl).setRequestBody(data)
                    .invoke();
            pageContent = response.getPageContent();
            String resultCode = PatternUtils.group(pageContent, "data-resultcode=\"(\\d+)\"", 1);
            if (resultCode != null) {
                if (resultCode.equals("9103") || resultCode.equals("9999")) {
                    logger.error("登陆失败,账户名与密码不匹配,param={}", param);
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                } else if (resultCode.equals("8105")) {
                    logger.error("登陆失败,密码过于简单,请重置,param={}", param);
                    return result.failure("密码过于简单,请重置");
                } else if (resultCode.equals("9111")) {
                    logger.error("登陆失败,登录失败过多，帐号已被锁定,param={}", param);
                    return result.failure("登录失败过多，帐号已被锁定");
                } else if (resultCode.equals("9100")) {
                    logger.error("登陆失败,该账户不存在,param={}", param);
                    return result.failure("该账户不存在");
                } else if (resultCode.equals("6113")) {
                    logger.error("登陆失败,系统繁忙，稍后重试,param={}", param);
                    return result.failure("系统繁忙，稍后重试");
                } else if (resultCode.equals("9115")) {
                    logger.error("登陆失败,验证码不正确,param={}", param);
                    return result.failure("验证码不正确");
                } else if (StringUtils.isNotBlank(resultCode)) {
                    logger.error("登陆失败,param={},response={}", param, response);
                    return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
                }
            }
            logger.info("电信统一登录入口----登录成功！");
            return result.success();
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }
}
