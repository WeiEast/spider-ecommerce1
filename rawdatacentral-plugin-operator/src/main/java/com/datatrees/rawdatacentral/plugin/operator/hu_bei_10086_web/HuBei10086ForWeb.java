package com.datatrees.rawdatacentral.plugin.operator.hu_bei_10086_web;

import javax.script.Invocable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.xpath.XPathUtil;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import com.google.gson.reflect.TypeToken;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/8/30.
 */
public class HuBei10086ForWeb implements OperatorPluginService {

    private static final String javaScript
                                       = "Ly8gUlNBLCBhIHN1aXRlIG9mIHJvdXRpbmVzIGZvciBwZXJmb3JtaW5nIFJTQSBwdWJsaWMta2V5IGNvbXB1dGF0aW9ucyBpbgovLyBKYXZhU2NyaXB0LgovLwovLyBSZXF1aXJlcyBCaWdJbnQuanMgYW5kIEJhcnJldHQuanMuCi8vCi8vIENvcHlyaWdodCAxOTk4LTIwMDUgRGF2aWQgU2hhcGlyby4KLy8KLy8gWW91IG1heSB1c2UsIHJlLXVzZSwgYWJ1c2UsIGNvcHksIGFuZCBtb2RpZnkgdGhpcyBjb2RlIHRvIHlvdXIgbGlraW5nLCBidXQKLy8gcGxlYXNlIGtlZXAgdGhpcyBoZWFkZXIuCi8vCi8vIFRoYW5rcyEKLy8KLy8gRGF2ZSBTaGFwaXJvCi8vIGRhdmVAb2hkYXZlLmNvbQp2YXIga2V5OwpmdW5jdGlvbiBzZXRLZXkgKGVuY3J5cHRpb25FeHBvbmVudCwgZGVjcnlwdGlvbkV4cG9uZW50LCBtb2R1bHVzKXsKICAgIHNldE1heERpZ2l0cygxMzApOwogICAga2V5ID0gbmV3IFJTQUtleVBhaXIoZW5jcnlwdGlvbkV4cG9uZW50LCBkZWNyeXB0aW9uRXhwb25lbnQsIG1vZHVsdXMpOwp9CmZ1bmN0aW9uIFJTQUtleVBhaXIoZW5jcnlwdGlvbkV4cG9uZW50LCBkZWNyeXB0aW9uRXhwb25lbnQsIG1vZHVsdXMpCnsKICAgIHRoaXMuZSA9IGJpRnJvbUhleChlbmNyeXB0aW9uRXhwb25lbnQpOwogICAgdGhpcy5kID0gYmlGcm9tSGV4KGRlY3J5cHRpb25FeHBvbmVudCk7CiAgICB0aGlzLm0gPSBiaUZyb21IZXgobW9kdWx1cyk7CiAgICAvLyBXZSBjYW4gZG8gdHdvIGJ5dGVzIHBlciBkaWdpdCwgc28KICAgIC8vIGNodW5rU2l6ZSA9IDIgKiAobnVtYmVyIG9mIGRpZ2l0cyBpbiBtb2R1bHVzIC0gMSkuCiAgICAvLyBTaW5jZSBiaUhpZ2hJbmRleCByZXR1cm5zIHRoZSBoaWdoIGluZGV4LCBub3QgdGhlIG51bWJlciBvZiBkaWdpdHMsIDEgaGFzCiAgICAvLyBhbHJlYWR5IGJlZW4gc3VidHJhY3RlZC4KICAgIHRoaXMuY2h1bmtTaXplID0gMiAqIGJpSGlnaEluZGV4KHRoaXMubSk7CiAgICB0aGlzLnJhZGl4ID0gMTY7CiAgICB0aGlzLmJhcnJldHQgPSBuZXcgQmFycmV0dE11KHRoaXMubSk7Cn0KCmZ1bmN0aW9uIHR3b0RpZ2l0KG4pCnsKICAgIHJldHVybiAobiA8IDEwID8gIjAiIDogIiIpICsgU3RyaW5nKG4pOwp9CgoKZnVuY3Rpb24gZW5jcnlwdGVkU3RyaW5nKHMpCi8vIEFsdGVyZWQgYnkgUm9iIFNhdW5kZXJzIChyb2JAcm9ic2F1bmRlcnMubmV0KS4gTmV3IHJvdXRpbmUgcGFkcyB0aGUKLy8gc3RyaW5nIGFmdGVyIGl0IGhhcyBiZWVuIGNvbnZlcnRlZCB0byBhbiBhcnJheS4gVGhpcyBmaXhlcyBhbgovLyBpbmNvbXBhdGliaWxpdHkgd2l0aCBGbGFzaCBNWCdzIEFjdGlvblNjcmlwdC4KewogICAgdmFyIGEgPSBuZXcgQXJyYXkoKTsKICAgIHZhciBzbCA9IHMubGVuZ3RoOwogICAgdmFyIGkgPSAwOwogICAgd2hpbGUgKGkgPCBzbCkgewogICAgICAgIGFbaV0gPSBzLmNoYXJDb2RlQXQoaSk7CiAgICAgICAgaSsrOwogICAgfQoKICAgIHdoaWxlIChhLmxlbmd0aCAlIGtleS5jaHVua1NpemUgIT0gMCkgewogICAgICAgIGFbaSsrXSA9IDA7CiAgICB9CgogICAgdmFyIGFsID0gYS5sZW5ndGg7CiAgICB2YXIgcmVzdWx0ID0gIiI7CiAgICB2YXIgaiwgaywgYmxvY2s7CiAgICBmb3IgKGkgPSAwOyBpIDwgYWw7IGkgKz0ga2V5LmNodW5rU2l6ZSkgewogICAgICAgIGJsb2NrID0gbmV3IEJpZ0ludCgpOwogICAgICAgIGogPSAwOwogICAgICAgIGZvciAoayA9IGk7IGsgPCBpICsga2V5LmNodW5rU2l6ZTsgKytqKSB7CiAgICAgICAgICAgIGJsb2NrLmRpZ2l0c1tqXSA9IGFbaysrXTsKICAgICAgICAgICAgYmxvY2suZGlnaXRzW2pdICs9IGFbaysrXSA8PCA4OwogICAgICAgIH0KICAgICAgICB2YXIgY3J5cHQgPSBrZXkuYmFycmV0dC5wb3dNb2QoYmxvY2ssIGtleS5lKTsKICAgICAgICB2YXIgdGV4dCA9IGtleS5yYWRpeCA9PSAxNiA/IGJpVG9IZXgoY3J5cHQpIDogYmlUb1N0cmluZyhjcnlwdCwga2V5LnJhZGl4KTsKICAgICAgICByZXN1bHQgKz0gdGV4dCArICIgIjsKICAgIH0KICAgIHJldHVybiByZXN1bHQuc3Vic3RyaW5nKDAsIHJlc3VsdC5sZW5ndGggLSAxKTsgLy8gUmVtb3ZlIGxhc3Qgc3BhY2UuCn0KCmZ1bmN0aW9uIGRlY3J5cHRlZFN0cmluZyhrZXksIHMpCnsKICAgIHZhciBibG9ja3MgPSBzLnNwbGl0KCIgIik7CiAgICB2YXIgcmVzdWx0ID0gIiI7CiAgICB2YXIgaSwgaiwgYmxvY2s7CiAgICBmb3IgKGkgPSAwOyBpIDwgYmxvY2tzLmxlbmd0aDsgKytpKSB7CiAgICAgICAgdmFyIGJpOwogICAgICAgIGlmIChrZXkucmFkaXggPT0gMTYpIHsKICAgICAgICAgICAgYmkgPSBiaUZyb21IZXgoYmxvY2tzW2ldKTsKICAgICAgICB9CiAgICAgICAgZWxzZSB7CiAgICAgICAgICAgIGJpID0gYmlGcm9tU3RyaW5nKGJsb2Nrc1tpXSwga2V5LnJhZGl4KTsKICAgICAgICB9CiAgICAgICAgYmxvY2sgPSBrZXkuYmFycmV0dC5wb3dNb2QoYmksIGtleS5kKTsKICAgICAgICBmb3IgKGogPSAwOyBqIDw9IGJpSGlnaEluZGV4KGJsb2NrKTsgKytqKSB7CiAgICAgICAgICAgIHJlc3VsdCArPSBTdHJpbmcuZnJvbUNoYXJDb2RlKGJsb2NrLmRpZ2l0c1tqXSAmIDI1NSwKICAgICAgICAgICAgICAgIGJsb2NrLmRpZ2l0c1tqXSA+PiA4KTsKICAgICAgICB9CiAgICB9CiAgICAvLyBSZW1vdmUgdHJhaWxpbmcgbnVsbCwgaWYgYW55LgogICAgaWYgKHJlc3VsdC5jaGFyQ29kZUF0KHJlc3VsdC5sZW5ndGggLSAxKSA9PSAwKSB7CiAgICAgICAgcmVzdWx0ID0gcmVzdWx0LnN1YnN0cmluZygwLCByZXN1bHQubGVuZ3RoIC0gMSk7CiAgICB9CiAgICByZXR1cm4gcmVzdWx0Owp9CgovL0JhcnJldHRNdSwgYSBjbGFzcyBmb3IgcGVyZm9ybWluZyBCYXJyZXR0IG1vZHVsYXIgcmVkdWN0aW9uIGNvbXB1dGF0aW9ucyBpbgovL0phdmFTY3JpcHQuCi8vCi8vUmVxdWlyZXMgQmlnSW50LmpzLgovLwovL0NvcHlyaWdodCAyMDA0LTIwMDUgRGF2aWQgU2hhcGlyby4KLy8KLy9Zb3UgbWF5IHVzZSwgcmUtdXNlLCBhYnVzZSwgY29weSwgYW5kIG1vZGlmeSB0aGlzIGNvZGUgdG8geW91ciBsaWtpbmcsIGJ1dAovL3BsZWFzZSBrZWVwIHRoaXMgaGVhZGVyLgovLwovL1RoYW5rcyEKLy8KLy9EYXZlIFNoYXBpcm8KLy9kYXZlQG9oZGF2ZS5jb20KCmZ1bmN0aW9uIEJhcnJldHRNdShtKQp7CiAgICB0aGlzLm1vZHVsdXMgPSBiaUNvcHkobSk7CiAgICB0aGlzLmsgPSBiaUhpZ2hJbmRleCh0aGlzLm1vZHVsdXMpICsgMTsKICAgIHZhciBiMmsgPSBuZXcgQmlnSW50KCk7CiAgICBiMmsuZGlnaXRzWzIgKiB0aGlzLmtdID0gMTsgLy8gYjJrID0gYl4oMmspCiAgICB0aGlzLm11ID0gYmlEaXZpZGUoYjJrLCB0aGlzLm1vZHVsdXMpOwogICAgdGhpcy5ia3BsdXMxID0gbmV3IEJpZ0ludCgpOwogICAgdGhpcy5ia3BsdXMxLmRpZ2l0c1t0aGlzLmsgKyAxXSA9IDE7IC8vIGJrcGx1czEgPSBiXihrKzEpCiAgICB0aGlzLm1vZHVsbyA9IEJhcnJldHRNdV9tb2R1bG87CiAgICB0aGlzLm11bHRpcGx5TW9kID0gQmFycmV0dE11X211bHRpcGx5TW9kOwogICAgdGhpcy5wb3dNb2QgPSBCYXJyZXR0TXVfcG93TW9kOwp9CgpmdW5jdGlvbiBCYXJyZXR0TXVfbW9kdWxvKHgpCnsKICAgIHZhciBxMSA9IGJpRGl2aWRlQnlSYWRpeFBvd2VyKHgsIHRoaXMuayAtIDEpOwogICAgdmFyIHEyID0gYmlNdWx0aXBseShxMSwgdGhpcy5tdSk7CiAgICB2YXIgcTMgPSBiaURpdmlkZUJ5UmFkaXhQb3dlcihxMiwgdGhpcy5rICsgMSk7CiAgICB2YXIgcjEgPSBiaU1vZHVsb0J5UmFkaXhQb3dlcih4LCB0aGlzLmsgKyAxKTsKICAgIHZhciByMnRlcm0gPSBiaU11bHRpcGx5KHEzLCB0aGlzLm1vZHVsdXMpOwogICAgdmFyIHIyID0gYmlNb2R1bG9CeVJhZGl4UG93ZXIocjJ0ZXJtLCB0aGlzLmsgKyAxKTsKICAgIHZhciByID0gYmlTdWJ0cmFjdChyMSwgcjIpOwogICAgaWYgKHIuaXNOZWcpIHsKICAgICAgICByID0gYmlBZGQociwgdGhpcy5ia3BsdXMxKTsKICAgIH0KICAgIHZhciByZ3RlbSA9IGJpQ29tcGFyZShyLCB0aGlzLm1vZHVsdXMpID49IDA7CiAgICB3aGlsZSAocmd0ZW0pIHsKICAgICAgICByID0gYmlTdWJ0cmFjdChyLCB0aGlzLm1vZHVsdXMpOwogICAgICAgIHJndGVtID0gYmlDb21wYXJlKHIsIHRoaXMubW9kdWx1cykgPj0gMDsKICAgIH0KICAgIHJldHVybiByOwp9CgpmdW5jdGlvbiBCYXJyZXR0TXVfbXVsdGlwbHlNb2QoeCwgeSkKewogICAgLyoKICAgICB4ID0gdGhpcy5tb2R1bG8oeCk7CiAgICAgeSA9IHRoaXMubW9kdWxvKHkpOwogICAgICovCiAgICB2YXIgeHkgPSBiaU11bHRpcGx5KHgsIHkpOwogICAgcmV0dXJuIHRoaXMubW9kdWxvKHh5KTsKfQoKZnVuY3Rpb24gQmFycmV0dE11X3Bvd01vZCh4LCB5KQp7CiAgICB2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOwogICAgcmVzdWx0LmRpZ2l0c1swXSA9IDE7CiAgICB2YXIgYSA9IHg7CiAgICB2YXIgayA9IHk7CiAgICB3aGlsZSAodHJ1ZSkgewogICAgICAgIGlmICgoay5kaWdpdHNbMF0gJiAxKSAhPSAwKSByZXN1bHQgPSB0aGlzLm11bHRpcGx5TW9kKHJlc3VsdCwgYSk7CiAgICAgICAgayA9IGJpU2hpZnRSaWdodChrLCAxKTsKICAgICAgICBpZiAoay5kaWdpdHNbMF0gPT0gMCAmJiBiaUhpZ2hJbmRleChrKSA9PSAwKSBicmVhazsKICAgICAgICBhID0gdGhpcy5tdWx0aXBseU1vZChhLCBhKTsKICAgIH0KICAgIHJldHVybiByZXN1bHQ7Cn0KCi8vQmlnSW50LCBhIHN1aXRlIG9mIHJvdXRpbmVzIGZvciBwZXJmb3JtaW5nIG11bHRpcGxlLXByZWNpc2lvbiBhcml0aG1ldGljIGluCi8vSmF2YVNjcmlwdC4KLy8KLy9Db3B5cmlnaHQgMTk5OC0yMDA1IERhdmlkIFNoYXBpcm8uCi8vCi8vWW91IG1heSB1c2UsIHJlLXVzZSwgYWJ1c2UsCi8vY29weSwgYW5kIG1vZGlmeSB0aGlzIGNvZGUgdG8geW91ciBsaWtpbmcsIGJ1dCBwbGVhc2Uga2VlcCB0aGlzIGhlYWRlci4KLy9UaGFua3MhCi8vCi8vRGF2ZSBTaGFwaXJvCi8vZGF2ZUBvaGRhdmUuY29tCgovL0lNUE9SVEFOVCBUSElORzogQmUgc3VyZSB0byBzZXQgbWF4RGlnaXRzIGFjY29yZGluZyB0byB5b3VyIHByZWNpc2lvbgovL25lZWRzLiBVc2UgdGhlIHNldE1heERpZ2l0cygpIGZ1bmN0aW9uIHRvIGRvIHRoaXMuIFNlZSBjb21tZW50cyBiZWxvdy4KLy8KLy9Ud2Vha2VkIGJ5IElhbiBCdW5uaW5nCi8vQWx0ZXJhdGlvbnM6Ci8vRml4IGJ1ZyBpbiBmdW5jdGlvbiBiaUZyb21IZXgocykgdG8gYWxsb3cKLy9wYXJzaW5nIG9mIHN0cmluZ3Mgb2YgbGVuZ3RoICE9IDAgKG1vZCA0KQoKLy9DaGFuZ2VzIG1hZGUgYnkgRGF2ZSBTaGFwaXJvIGFzIG9mIDEyLzMwLzIwMDQ6Ci8vCi8vVGhlIEJpZ0ludCgpIGNvbnN0cnVjdG9yIGRvZXNuJ3QgdGFrZSBhIHN0cmluZyBhbnltb3JlLiBJZiB5b3Ugd2FudCB0bwovL2NyZWF0ZSBhIEJpZ0ludCBmcm9tIGEgc3RyaW5nLCB1c2UgYmlGcm9tRGVjaW1hbCgpIGZvciBiYXNlLTEwCi8vcmVwcmVzZW50YXRpb25zLCBiaUZyb21IZXgoKSBmb3IgYmFzZS0xNiByZXByZXNlbnRhdGlvbnMsIG9yCi8vYmlGcm9tU3RyaW5nKCkgZm9yIGJhc2UtMi10by0zNiByZXByZXNlbnRhdGlvbnMuCi8vCi8vYmlGcm9tQXJyYXkoKSBoYXMgYmVlbiByZW1vdmVkLiBVc2UgYmlDb3B5KCkgaW5zdGVhZCwgcGFzc2luZyBhIEJpZ0ludAovL2luc3RlYWQgb2YgYW4gYXJyYXkuCi8vCi8vVGhlIEJpZ0ludCgpIGNvbnN0cnVjdG9yIG5vdyBvbmx5IGNvbnN0cnVjdHMgYSB6ZXJvZWQtb3V0IGFycmF5LgovL0FsdGVybmF0aXZlbHksIGlmIHlvdSBwYXNzIDx0cnVlPiwgaXQgd29uJ3QgY29uc3RydWN0IGFueSBhcnJheS4gU2VlIHRoZQovL2JpQ29weSgpIG1ldGhvZCBmb3IgYW4gZXhhbXBsZSBvZiB0aGlzLgovLwovL0JlIHN1cmUgdG8gc2V0IG1heERpZ2l0cyBkZXBlbmRpbmcgb24geW91ciBwcmVjaXNpb24gbmVlZHMuIFRoZSBkZWZhdWx0Ci8vemVyb2VkLW91dCBhcnJheSBaRVJPX0FSUkFZIGlzIGNvbnN0cnVjdGVkIGluc2lkZSB0aGUgc2V0TWF4RGlnaXRzKCkKLy9mdW5jdGlvbi4gU28gdXNlIHRoaXMgZnVuY3Rpb24gdG8gc2V0IHRoZSB2YXJpYWJsZS4gRE9OJ1QgSlVTVCBTRVQgVEhFCi8vVkFMVUUuIFVTRSBUSEUgRlVOQ1RJT04uCi8vCi8vWkVST19BUlJBWSBleGlzdHMgdG8gaG9wZWZ1bGx5IHNwZWVkIHVwIGNvbnN0cnVjdGlvbiBvZiBCaWdJbnRzKCkuIEJ5Ci8vcHJlY2FsY3VsYXRpbmcgdGhlIHplcm8gYXJyYXksIHdlIGNhbiBqdXN0IHVzZSBzbGljZSgwKSB0byBtYWtlIGNvcGllcyBvZgovL2l0LiBQcmVzdW1hYmx5IHRoaXMgY2FsbHMgZmFzdGVyIG5hdGl2ZSBjb2RlLCBhcyBvcHBvc2VkIHRvIHNldHRpbmcgdGhlCi8vZWxlbWVudHMgb25lIGF0IGEgdGltZS4gSSBoYXZlIG5vdCBkb25lIGFueSB0aW1pbmcgdGVzdHMgdG8gdmVyaWZ5IHRoaXMKLy9jbGFpbS4KCi8vTWF4IG51bWJlciA9IDEwXjE2IC0gMiA9IDk5OTk5OTk5OTk5OTk5OTg7Ci8vICAgICAgICAgICAgMl41MyAgICAgPSA5MDA3MTk5MjU0NzQwOTkyOwoKdmFyIGJpUmFkaXhCYXNlID0gMjsKdmFyIGJpUmFkaXhCaXRzID0gMTY7CnZhciBiaXRzUGVyRGlnaXQgPSBiaVJhZGl4Qml0czsKdmFyIGJpUmFkaXggPSAxIDw8IDE2OyAvLyA9IDJeMTYgPSA2NTUzNgp2YXIgYmlIYWxmUmFkaXggPSBiaVJhZGl4ID4+PiAxOwp2YXIgYmlSYWRpeFNxdWFyZWQgPSBiaVJhZGl4ICogYmlSYWRpeDsKdmFyIG1heERpZ2l0VmFsID0gYmlSYWRpeCAtIDE7CnZhciBtYXhJbnRlZ2VyID0gOTk5OTk5OTk5OTk5OTk5ODsKCi8vbWF4RGlnaXRzOgovL0NoYW5nZSB0aGlzIHRvIGFjY29tbW9kYXRlIHlvdXIgbGFyZ2VzdCBudW1iZXIgc2l6ZS4gVXNlIHNldE1heERpZ2l0cygpCi8vdG8gY2hhbmdlIGl0IQovLwovL0luIGdlbmVyYWwsIGlmIHlvdSdyZSB3b3JraW5nIHdpdGggbnVtYmVycyBvZiBzaXplIE4gYml0cywgeW91J2xsIG5lZWQgMipOCi8vYml0cyBvZiBzdG9yYWdlLiBFYWNoIGRpZ2l0IGhvbGRzIDE2IGJpdHMuIFNvLCBhIDEwMjQtYml0IGtleSB3aWxsIG5lZWQKLy8KLy8xMDI0ICogMiAvIDE2ID0gMTI4IGRpZ2l0cyBvZiBzdG9yYWdlLgovLwoKdmFyIG1heERpZ2l0czsKdmFyIFpFUk9fQVJSQVk7CnZhciBiaWdaZXJvLCBiaWdPbmU7CgpmdW5jdGlvbiBzZXRNYXhEaWdpdHModmFsdWUpCnsKICAgIG1heERpZ2l0cyA9IHZhbHVlOwogICAgWkVST19BUlJBWSA9IG5ldyBBcnJheShtYXhEaWdpdHMpOwogICAgZm9yICh2YXIgaXphID0gMDsgaXphIDwgWkVST19BUlJBWS5sZW5ndGg7IGl6YSsrKSBaRVJPX0FSUkFZW2l6YV0gPSAwOwogICAgYmlnWmVybyA9IG5ldyBCaWdJbnQoKTsKICAgIGJpZ09uZSA9IG5ldyBCaWdJbnQoKTsKICAgIGJpZ09uZS5kaWdpdHNbMF0gPSAxOwp9CgpzZXRNYXhEaWdpdHMoMjApOwoKLy9UaGUgbWF4aW11bSBudW1iZXIgb2YgZGlnaXRzIGluIGJhc2UgMTAgeW91IGNhbiBjb252ZXJ0IHRvIGFuCi8vaW50ZWdlciB3aXRob3V0IEphdmFTY3JpcHQgdGhyb3dpbmcgdXAgb24geW91Lgp2YXIgZHBsMTAgPSAxNTsKLy9scjEwID0gMTAgXiBkcGwxMAp2YXIgbHIxMCA9IGJpRnJvbU51bWJlcigxMDAwMDAwMDAwMDAwMDAwKTsKCmZ1bmN0aW9uIEJpZ0ludChmbGFnKQp7CiAgICBpZiAodHlwZW9mIGZsYWcgPT0gImJvb2xlYW4iICYmIGZsYWcgPT0gdHJ1ZSkgewogICAgICAgIHRoaXMuZGlnaXRzID0gbnVsbDsKICAgIH0KICAgIGVsc2UgewogICAgICAgIHRoaXMuZGlnaXRzID0gWkVST19BUlJBWS5zbGljZSgwKTsKICAgIH0KICAgIHRoaXMuaXNOZWcgPSBmYWxzZTsKfQoKZnVuY3Rpb24gYmlGcm9tRGVjaW1hbChzKQp7CiAgICB2YXIgaXNOZWcgPSBzLmNoYXJBdCgwKSA9PSAnLSc7CiAgICB2YXIgaSA9IGlzTmVnID8gMSA6IDA7CiAgICB2YXIgcmVzdWx0OwogICAgLy8gU2tpcCBsZWFkaW5nIHplcm9zLgogICAgd2hpbGUgKGkgPCBzLmxlbmd0aCAmJiBzLmNoYXJBdChpKSA9PSAnMCcpICsraTsKICAgIGlmIChpID09IHMubGVuZ3RoKSB7CiAgICAgICAgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOwogICAgfQogICAgZWxzZSB7CiAgICAgICAgdmFyIGRpZ2l0Q291bnQgPSBzLmxlbmd0aCAtIGk7CiAgICAgICAgdmFyIGZnbCA9IGRpZ2l0Q291bnQgJSBkcGwxMDsKICAgICAgICBpZiAoZmdsID09IDApIGZnbCA9IGRwbDEwOwogICAgICAgIHJlc3VsdCA9IGJpRnJvbU51bWJlcihOdW1iZXIocy5zdWJzdHIoaSwgZmdsKSkpOwogICAgICAgIGkgKz0gZmdsOwogICAgICAgIHdoaWxlIChpIDwgcy5sZW5ndGgpIHsKICAgICAgICAgICAgcmVzdWx0ID0gYmlBZGQoYmlNdWx0aXBseShyZXN1bHQsIGxyMTApLAogICAgICAgICAgICAgICAgYmlGcm9tTnVtYmVyKE51bWJlcihzLnN1YnN0cihpLCBkcGwxMCkpKSk7CiAgICAgICAgICAgIGkgKz0gZHBsMTA7CiAgICAgICAgfQogICAgICAgIHJlc3VsdC5pc05lZyA9IGlzTmVnOwogICAgfQogICAgcmV0dXJuIHJlc3VsdDsKfQoKZnVuY3Rpb24gYmlDb3B5KGJpKQp7CiAgICB2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCh0cnVlKTsKICAgIHJlc3VsdC5kaWdpdHMgPSBiaS5kaWdpdHMuc2xpY2UoMCk7CiAgICByZXN1bHQuaXNOZWcgPSBiaS5pc05lZzsKICAgIHJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGJpRnJvbU51bWJlcihpKQp7CiAgICB2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOwogICAgcmVzdWx0LmlzTmVnID0gaSA8IDA7CiAgICBpID0gTWF0aC5hYnMoaSk7CiAgICB2YXIgaiA9IDA7CiAgICB3aGlsZSAoaSA+IDApIHsKICAgICAgICByZXN1bHQuZGlnaXRzW2orK10gPSBpICYgbWF4RGlnaXRWYWw7CiAgICAgICAgaSA9IE1hdGguZmxvb3IoaSAvIGJpUmFkaXgpOwogICAgfQogICAgcmV0dXJuIHJlc3VsdDsKfQoKZnVuY3Rpb24gcmV2ZXJzZVN0cihzKQp7CiAgICB2YXIgcmVzdWx0ID0gIiI7CiAgICBmb3IgKHZhciBpID0gcy5sZW5ndGggLSAxOyBpID4gLTE7IC0taSkgewogICAgICAgIHJlc3VsdCArPSBzLmNoYXJBdChpKTsKICAgIH0KICAgIHJldHVybiByZXN1bHQ7Cn0KCnZhciBoZXhhdHJpZ2VzaW1hbFRvQ2hhciA9IG5ldyBBcnJheSgKICAgICcwJywgJzEnLCAnMicsICczJywgJzQnLCAnNScsICc2JywgJzcnLCAnOCcsICc5JywKICAgICdhJywgJ2InLCAnYycsICdkJywgJ2UnLCAnZicsICdnJywgJ2gnLCAnaScsICdqJywKICAgICdrJywgJ2wnLCAnbScsICduJywgJ28nLCAncCcsICdxJywgJ3InLCAncycsICd0JywKICAgICd1JywgJ3YnLCAndycsICd4JywgJ3knLCAneicKKTsKCmZ1bmN0aW9uIGJpVG9TdHJpbmcoeCwgcmFkaXgpCi8vIDIgPD0gcmFkaXggPD0gMzYKewogICAgdmFyIGIgPSBuZXcgQmlnSW50KCk7CiAgICBiLmRpZ2l0c1swXSA9IHJhZGl4OwogICAgdmFyIHFyID0gYmlEaXZpZGVNb2R1bG8oeCwgYik7CiAgICB2YXIgcmVzdWx0ID0gaGV4YXRyaWdlc2ltYWxUb0NoYXJbcXJbMV0uZGlnaXRzWzBdXTsKICAgIHdoaWxlIChiaUNvbXBhcmUocXJbMF0sIGJpZ1plcm8pID09IDEpIHsKICAgICAgICBxciA9IGJpRGl2aWRlTW9kdWxvKHFyWzBdLCBiKTsKICAgICAgICBkaWdpdCA9IHFyWzFdLmRpZ2l0c1swXTsKICAgICAgICByZXN1bHQgKz0gaGV4YXRyaWdlc2ltYWxUb0NoYXJbcXJbMV0uZGlnaXRzWzBdXTsKICAgIH0KICAgIHJldHVybiAoeC5pc05lZyA/ICItIiA6ICIiKSArIHJldmVyc2VTdHIocmVzdWx0KTsKfQoKZnVuY3Rpb24gYmlUb0RlY2ltYWwoeCkKewogICAgdmFyIGIgPSBuZXcgQmlnSW50KCk7CiAgICBiLmRpZ2l0c1swXSA9IDEwOwogICAgdmFyIHFyID0gYmlEaXZpZGVNb2R1bG8oeCwgYik7CiAgICB2YXIgcmVzdWx0ID0gU3RyaW5nKHFyWzFdLmRpZ2l0c1swXSk7CiAgICB3aGlsZSAoYmlDb21wYXJlKHFyWzBdLCBiaWdaZXJvKSA9PSAxKSB7CiAgICAgICAgcXIgPSBiaURpdmlkZU1vZHVsbyhxclswXSwgYik7CiAgICAgICAgcmVzdWx0ICs9IFN0cmluZyhxclsxXS5kaWdpdHNbMF0pOwogICAgfQogICAgcmV0dXJuICh4LmlzTmVnID8gIi0iIDogIiIpICsgcmV2ZXJzZVN0cihyZXN1bHQpOwp9Cgp2YXIgaGV4VG9DaGFyID0gbmV3IEFycmF5KCcwJywgJzEnLCAnMicsICczJywgJzQnLCAnNScsICc2JywgJzcnLCAnOCcsICc5JywKICAgICdhJywgJ2InLCAnYycsICdkJywgJ2UnLCAnZicpOwoKZnVuY3Rpb24gZGlnaXRUb0hleChuKQp7CiAgICB2YXIgbWFzayA9IDB4ZjsKICAgIHZhciByZXN1bHQgPSAiIjsKICAgIGZvciAoaSA9IDA7IGkgPCA0OyArK2kpIHsKICAgICAgICByZXN1bHQgKz0gaGV4VG9DaGFyW24gJiBtYXNrXTsKICAgICAgICBuID4+Pj0gNDsKICAgIH0KICAgIHJldHVybiByZXZlcnNlU3RyKHJlc3VsdCk7Cn0KCmZ1bmN0aW9uIGJpVG9IZXgoeCkKewogICAgdmFyIHJlc3VsdCA9ICIiOwogICAgdmFyIG4gPSBiaUhpZ2hJbmRleCh4KTsKICAgIGZvciAodmFyIGkgPSBiaUhpZ2hJbmRleCh4KTsgaSA+IC0xOyAtLWkpIHsKICAgICAgICByZXN1bHQgKz0gZGlnaXRUb0hleCh4LmRpZ2l0c1tpXSk7CiAgICB9CiAgICByZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBjaGFyVG9IZXgoYykKewogICAgdmFyIFpFUk8gPSA0ODsKICAgIHZhciBOSU5FID0gWkVSTyArIDk7CiAgICB2YXIgbGl0dGxlQSA9IDk3OwogICAgdmFyIGxpdHRsZVogPSBsaXR0bGVBICsgMjU7CiAgICB2YXIgYmlnQSA9IDY1OwogICAgdmFyIGJpZ1ogPSA2NSArIDI1OwogICAgdmFyIHJlc3VsdDsKCiAgICBpZiAoYyA+PSBaRVJPICYmIGMgPD0gTklORSkgewogICAgICAgIHJlc3VsdCA9IGMgLSBaRVJPOwogICAgfSBlbHNlIGlmIChjID49IGJpZ0EgJiYgYyA8PSBiaWdaKSB7CiAgICAgICAgcmVzdWx0ID0gMTAgKyBjIC0gYmlnQTsKICAgIH0gZWxzZSBpZiAoYyA+PSBsaXR0bGVBICYmIGMgPD0gbGl0dGxlWikgewogICAgICAgIHJlc3VsdCA9IDEwICsgYyAtIGxpdHRsZUE7CiAgICB9IGVsc2UgewogICAgICAgIHJlc3VsdCA9IDA7CiAgICB9CiAgICByZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBoZXhUb0RpZ2l0KHMpCnsKICAgIHZhciByZXN1bHQgPSAwOwogICAgdmFyIHNsID0gTWF0aC5taW4ocy5sZW5ndGgsIDQpOwogICAgZm9yICh2YXIgaSA9IDA7IGkgPCBzbDsgKytpKSB7CiAgICAgICAgcmVzdWx0IDw8PSA0OwogICAgICAgIHJlc3VsdCB8PSBjaGFyVG9IZXgocy5jaGFyQ29kZUF0KGkpKQogICAgfQogICAgcmV0dXJuIHJlc3VsdDsKfQoKZnVuY3Rpb24gYmlGcm9tSGV4KHMpCnsKICAgIHZhciByZXN1bHQgPSBuZXcgQmlnSW50KCk7CiAgICB2YXIgc2wgPSBzLmxlbmd0aDsKICAgIGZvciAodmFyIGkgPSBzbCwgaiA9IDA7IGkgPiAwOyBpIC09IDQsICsraikgewogICAgICAgIHJlc3VsdC5kaWdpdHNbal0gPSBoZXhUb0RpZ2l0KHMuc3Vic3RyKE1hdGgubWF4KGkgLSA0LCAwKSwgTWF0aC5taW4oaSwgNCkpKTsKICAgIH0KICAgIHJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGJpRnJvbVN0cmluZyhzLCByYWRpeCkKewogICAgdmFyIGlzTmVnID0gcy5jaGFyQXQoMCkgPT0gJy0nOwogICAgdmFyIGlzdG9wID0gaXNOZWcgPyAxIDogMDsKICAgIHZhciByZXN1bHQgPSBuZXcgQmlnSW50KCk7CiAgICB2YXIgcGxhY2UgPSBuZXcgQmlnSW50KCk7CiAgICBwbGFjZS5kaWdpdHNbMF0gPSAxOyAvLyByYWRpeF4wCiAgICBmb3IgKHZhciBpID0gcy5sZW5ndGggLSAxOyBpID49IGlzdG9wOyBpLS0pIHsKICAgICAgICB2YXIgYyA9IHMuY2hhckNvZGVBdChpKTsKICAgICAgICB2YXIgZGlnaXQgPSBjaGFyVG9IZXgoYyk7CiAgICAgICAgdmFyIGJpRGlnaXQgPSBiaU11bHRpcGx5RGlnaXQocGxhY2UsIGRpZ2l0KTsKICAgICAgICByZXN1bHQgPSBiaUFkZChyZXN1bHQsIGJpRGlnaXQpOwogICAgICAgIHBsYWNlID0gYmlNdWx0aXBseURpZ2l0KHBsYWNlLCByYWRpeCk7CiAgICB9CiAgICByZXN1bHQuaXNOZWcgPSBpc05lZzsKICAgIHJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGJpRHVtcChiKQp7CiAgICByZXR1cm4gKGIuaXNOZWcgPyAiLSIgOiAiIikgKyBiLmRpZ2l0cy5qb2luKCIgIik7Cn0KCmZ1bmN0aW9uIGJpQWRkKHgsIHkpCnsKICAgIHZhciByZXN1bHQ7CgogICAgaWYgKHguaXNOZWcgIT0geS5pc05lZykgewogICAgICAgIHkuaXNOZWcgPSAheS5pc05lZzsKICAgICAgICByZXN1bHQgPSBiaVN1YnRyYWN0KHgsIHkpOwogICAgICAgIHkuaXNOZWcgPSAheS5pc05lZzsKICAgIH0KICAgIGVsc2UgewogICAgICAgIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKICAgICAgICB2YXIgYyA9IDA7CiAgICAgICAgdmFyIG47CiAgICAgICAgZm9yICh2YXIgaSA9IDA7IGkgPCB4LmRpZ2l0cy5sZW5ndGg7ICsraSkgewogICAgICAgICAgICBuID0geC5kaWdpdHNbaV0gKyB5LmRpZ2l0c1tpXSArIGM7CiAgICAgICAgICAgIHJlc3VsdC5kaWdpdHNbaV0gPSBuICUgYmlSYWRpeDsKICAgICAgICAgICAgYyA9IE51bWJlcihuID49IGJpUmFkaXgpOwogICAgICAgIH0KICAgICAgICByZXN1bHQuaXNOZWcgPSB4LmlzTmVnOwogICAgfQogICAgcmV0dXJuIHJlc3VsdDsKfQoKZnVuY3Rpb24gYmlTdWJ0cmFjdCh4LCB5KQp7CiAgICB2YXIgcmVzdWx0OwogICAgaWYgKHguaXNOZWcgIT0geS5pc05lZykgewogICAgICAgIHkuaXNOZWcgPSAheS5pc05lZzsKICAgICAgICByZXN1bHQgPSBiaUFkZCh4LCB5KTsKICAgICAgICB5LmlzTmVnID0gIXkuaXNOZWc7CiAgICB9IGVsc2UgewogICAgICAgIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKICAgICAgICB2YXIgbiwgYzsKICAgICAgICBjID0gMDsKICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IHguZGlnaXRzLmxlbmd0aDsgKytpKSB7CiAgICAgICAgICAgIG4gPSB4LmRpZ2l0c1tpXSAtIHkuZGlnaXRzW2ldICsgYzsKICAgICAgICAgICAgcmVzdWx0LmRpZ2l0c1tpXSA9IG4gJSBiaVJhZGl4OwogICAgICAgICAgICAvLyBTdHVwaWQgbm9uLWNvbmZvcm1pbmcgbW9kdWx1cyBvcGVyYXRpb24uCiAgICAgICAgICAgIGlmIChyZXN1bHQuZGlnaXRzW2ldIDwgMCkgcmVzdWx0LmRpZ2l0c1tpXSArPSBiaVJhZGl4OwogICAgICAgICAgICBjID0gMCAtIE51bWJlcihuIDwgMCk7CiAgICAgICAgfQogICAgICAgIC8vIEZpeCB1cCB0aGUgbmVnYXRpdmUgc2lnbiwgaWYgYW55LgogICAgICAgIGlmIChjID09IC0xKSB7CiAgICAgICAgICAgIGMgPSAwOwogICAgICAgICAgICBmb3IgKHZhciBpID0gMDsgaSA8IHguZGlnaXRzLmxlbmd0aDsgKytpKSB7CiAgICAgICAgICAgICAgICBuID0gMCAtIHJlc3VsdC5kaWdpdHNbaV0gKyBjOwogICAgICAgICAgICAgICAgcmVzdWx0LmRpZ2l0c1tpXSA9IG4gJSBiaVJhZGl4OwogICAgICAgICAgICAgICAgLy8gU3R1cGlkIG5vbi1jb25mb3JtaW5nIG1vZHVsdXMgb3BlcmF0aW9uLgogICAgICAgICAgICAgICAgaWYgKHJlc3VsdC5kaWdpdHNbaV0gPCAwKSByZXN1bHQuZGlnaXRzW2ldICs9IGJpUmFkaXg7CiAgICAgICAgICAgICAgICBjID0gMCAtIE51bWJlcihuIDwgMCk7CiAgICAgICAgICAgIH0KICAgICAgICAgICAgLy8gUmVzdWx0IGlzIG9wcG9zaXRlIHNpZ24gb2YgYXJndW1lbnRzLgogICAgICAgICAgICByZXN1bHQuaXNOZWcgPSAheC5pc05lZzsKICAgICAgICB9IGVsc2UgewogICAgICAgICAgICAvLyBSZXN1bHQgaXMgc2FtZSBzaWduLgogICAgICAgICAgICByZXN1bHQuaXNOZWcgPSB4LmlzTmVnOwogICAgICAgIH0KICAgIH0KICAgIHJldHVybiByZXN1bHQ7Cn0KCgpmdW5jdGlvbiBiaUhpZ2hJbmRleCh4KQp7CiAgICB2YXIgcmVzdWx0ID0geC5kaWdpdHMubGVuZ3RoIC0gMTsKICAgIHdoaWxlIChyZXN1bHQgPiAwICYmIHguZGlnaXRzW3Jlc3VsdF0gPT0gMCkgLS1yZXN1bHQ7CiAgICByZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaU51bUJpdHMoeCkKewogICAgdmFyIG4gPSBiaUhpZ2hJbmRleCh4KTsKICAgIHZhciBkID0geC5kaWdpdHNbbl07CiAgICB2YXIgbSA9IChuICsgMSkgKiBiaXRzUGVyRGlnaXQ7CiAgICB2YXIgcmVzdWx0OwogICAgZm9yIChyZXN1bHQgPSBtOyByZXN1bHQgPiBtIC0gYml0c1BlckRpZ2l0OyAtLXJlc3VsdCkgewogICAgICAgIGlmICgoZCAmIDB4ODAwMCkgIT0gMCkgYnJlYWs7CiAgICAgICAgZCA8PD0gMTsKICAgIH0KICAgIHJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGJpTXVsdGlwbHkoeCwgeSkKewogICAgdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKICAgIHZhciBjOwogICAgdmFyIG4gPSBiaUhpZ2hJbmRleCh4KTsKICAgIHZhciB0ID0gYmlIaWdoSW5kZXgoeSk7CiAgICB2YXIgdSwgdXYsIGs7CgogICAgZm9yICh2YXIgaSA9IDA7IGkgPD0gdDsgKytpKSB7CiAgICAgICAgYyA9IDA7CiAgICAgICAgayA9IGk7CiAgICAgICAgZm9yIChqID0gMDsgaiA8PSBuOyArK2osICsraykgewogICAgICAgICAgICB1diA9IHJlc3VsdC5kaWdpdHNba10gKyB4LmRpZ2l0c1tqXSAqIHkuZGlnaXRzW2ldICsgYzsKICAgICAgICAgICAgcmVzdWx0LmRpZ2l0c1trXSA9IHV2ICYgbWF4RGlnaXRWYWw7CiAgICAgICAgICAgIGMgPSB1diA+Pj4gYmlSYWRpeEJpdHM7CiAgICAgICAgICAgIC8vYyA9IE1hdGguZmxvb3IodXYgLyBiaVJhZGl4KTsKICAgICAgICB9CiAgICAgICAgcmVzdWx0LmRpZ2l0c1tpICsgbiArIDFdID0gYzsKICAgIH0KICAgIC8vIFNvbWVvbmUgZ2l2ZSBtZSBhIGxvZ2ljYWwgeG9yLCBwbGVhc2UuCiAgICByZXN1bHQuaXNOZWcgPSB4LmlzTmVnICE9IHkuaXNOZWc7CiAgICByZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaU11bHRpcGx5RGlnaXQoeCwgeSkKewogICAgdmFyIG4sIGMsIHV2OwoKICAgIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKICAgIG4gPSBiaUhpZ2hJbmRleCh4KTsKICAgIGMgPSAwOwogICAgZm9yICh2YXIgaiA9IDA7IGogPD0gbjsgKytqKSB7CiAgICAgICAgdXYgPSByZXN1bHQuZGlnaXRzW2pdICsgeC5kaWdpdHNbal0gKiB5ICsgYzsKICAgICAgICByZXN1bHQuZGlnaXRzW2pdID0gdXYgJiBtYXhEaWdpdFZhbDsKICAgICAgICBjID0gdXYgPj4+IGJpUmFkaXhCaXRzOwogICAgICAgIC8vYyA9IE1hdGguZmxvb3IodXYgLyBiaVJhZGl4KTsKICAgIH0KICAgIHJlc3VsdC5kaWdpdHNbMSArIG5dID0gYzsKICAgIHJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGFycmF5Q29weShzcmMsIHNyY1N0YXJ0LCBkZXN0LCBkZXN0U3RhcnQsIG4pCnsKICAgIHZhciBtID0gTWF0aC5taW4oc3JjU3RhcnQgKyBuLCBzcmMubGVuZ3RoKTsKICAgIGZvciAodmFyIGkgPSBzcmNTdGFydCwgaiA9IGRlc3RTdGFydDsgaSA8IG07ICsraSwgKytqKSB7CiAgICAgICAgZGVzdFtqXSA9IHNyY1tpXTsKICAgIH0KfQoKdmFyIGhpZ2hCaXRNYXNrcyA9IG5ldyBBcnJheSgweDAwMDAsIDB4ODAwMCwgMHhDMDAwLCAweEUwMDAsIDB4RjAwMCwgMHhGODAwLAogICAgMHhGQzAwLCAweEZFMDAsIDB4RkYwMCwgMHhGRjgwLCAweEZGQzAsIDB4RkZFMCwKICAgIDB4RkZGMCwgMHhGRkY4LCAweEZGRkMsIDB4RkZGRSwgMHhGRkZGKTsKCmZ1bmN0aW9uIGJpU2hpZnRMZWZ0KHgsIG4pCnsKICAgIHZhciBkaWdpdENvdW50ID0gTWF0aC5mbG9vcihuIC8gYml0c1BlckRpZ2l0KTsKICAgIHZhciByZXN1bHQgPSBuZXcgQmlnSW50KCk7CiAgICBhcnJheUNvcHkoeC5kaWdpdHMsIDAsIHJlc3VsdC5kaWdpdHMsIGRpZ2l0Q291bnQsCiAgICAgICAgcmVzdWx0LmRpZ2l0cy5sZW5ndGggLSBkaWdpdENvdW50KTsKICAgIHZhciBiaXRzID0gbiAlIGJpdHNQZXJEaWdpdDsKICAgIHZhciByaWdodEJpdHMgPSBiaXRzUGVyRGlnaXQgLSBiaXRzOwogICAgZm9yICh2YXIgaSA9IHJlc3VsdC5kaWdpdHMubGVuZ3RoIC0gMSwgaTEgPSBpIC0gMTsgaSA+IDA7IC0taSwgLS1pMSkgewogICAgICAgIHJlc3VsdC5kaWdpdHNbaV0gPSAoKHJlc3VsdC5kaWdpdHNbaV0gPDwgYml0cykgJiBtYXhEaWdpdFZhbCkgfAogICAgICAgICAgICAoKHJlc3VsdC5kaWdpdHNbaTFdICYgaGlnaEJpdE1hc2tzW2JpdHNdKSA+Pj4KICAgICAgICAgICAgKHJpZ2h0Qml0cykpOwogICAgfQogICAgcmVzdWx0LmRpZ2l0c1swXSA9ICgocmVzdWx0LmRpZ2l0c1tpXSA8PCBiaXRzKSAmIG1heERpZ2l0VmFsKTsKICAgIHJlc3VsdC5pc05lZyA9IHguaXNOZWc7CiAgICByZXR1cm4gcmVzdWx0Owp9Cgp2YXIgbG93Qml0TWFza3MgPSBuZXcgQXJyYXkoMHgwMDAwLCAweDAwMDEsIDB4MDAwMywgMHgwMDA3LCAweDAwMEYsIDB4MDAxRiwKICAgIDB4MDAzRiwgMHgwMDdGLCAweDAwRkYsIDB4MDFGRiwgMHgwM0ZGLCAweDA3RkYsCiAgICAweDBGRkYsIDB4MUZGRiwgMHgzRkZGLCAweDdGRkYsIDB4RkZGRik7CgpmdW5jdGlvbiBiaVNoaWZ0UmlnaHQoeCwgbikKewogICAgdmFyIGRpZ2l0Q291bnQgPSBNYXRoLmZsb29yKG4gLyBiaXRzUGVyRGlnaXQpOwogICAgdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKICAgIGFycmF5Q29weSh4LmRpZ2l0cywgZGlnaXRDb3VudCwgcmVzdWx0LmRpZ2l0cywgMCwKICAgICAgICB4LmRpZ2l0cy5sZW5ndGggLSBkaWdpdENvdW50KTsKICAgIHZhciBiaXRzID0gbiAlIGJpdHNQZXJEaWdpdDsKICAgIHZhciBsZWZ0Qml0cyA9IGJpdHNQZXJEaWdpdCAtIGJpdHM7CiAgICBmb3IgKHZhciBpID0gMCwgaTEgPSBpICsgMTsgaSA8IHJlc3VsdC5kaWdpdHMubGVuZ3RoIC0gMTsgKytpLCArK2kxKSB7CiAgICAgICAgcmVzdWx0LmRpZ2l0c1tpXSA9IChyZXN1bHQuZGlnaXRzW2ldID4+PiBiaXRzKSB8CiAgICAgICAgICAgICgocmVzdWx0LmRpZ2l0c1tpMV0gJiBsb3dCaXRNYXNrc1tiaXRzXSkgPDwgbGVmdEJpdHMpOwogICAgfQogICAgcmVzdWx0LmRpZ2l0c1tyZXN1bHQuZGlnaXRzLmxlbmd0aCAtIDFdID4+Pj0gYml0czsKICAgIHJlc3VsdC5pc05lZyA9IHguaXNOZWc7CiAgICByZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaU11bHRpcGx5QnlSYWRpeFBvd2VyKHgsIG4pCnsKICAgIHZhciByZXN1bHQgPSBuZXcgQmlnSW50KCk7CiAgICBhcnJheUNvcHkoeC5kaWdpdHMsIDAsIHJlc3VsdC5kaWdpdHMsIG4sIHJlc3VsdC5kaWdpdHMubGVuZ3RoIC0gbik7CiAgICByZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaURpdmlkZUJ5UmFkaXhQb3dlcih4LCBuKQp7CiAgICB2YXIgcmVzdWx0ID0gbmV3IEJpZ0ludCgpOwogICAgYXJyYXlDb3B5KHguZGlnaXRzLCBuLCByZXN1bHQuZGlnaXRzLCAwLCByZXN1bHQuZGlnaXRzLmxlbmd0aCAtIG4pOwogICAgcmV0dXJuIHJlc3VsdDsKfQoKZnVuY3Rpb24gYmlNb2R1bG9CeVJhZGl4UG93ZXIoeCwgbikKewogICAgdmFyIHJlc3VsdCA9IG5ldyBCaWdJbnQoKTsKICAgIGFycmF5Q29weSh4LmRpZ2l0cywgMCwgcmVzdWx0LmRpZ2l0cywgMCwgbik7CiAgICByZXR1cm4gcmVzdWx0Owp9CgpmdW5jdGlvbiBiaUNvbXBhcmUoeCwgeSkKewogICAgaWYgKHguaXNOZWcgIT0geS5pc05lZykgewogICAgICAgIHJldHVybiAxIC0gMiAqIE51bWJlcih4LmlzTmVnKTsKICAgIH0KICAgIGZvciAodmFyIGkgPSB4LmRpZ2l0cy5sZW5ndGggLSAxOyBpID49IDA7IC0taSkgewogICAgICAgIGlmICh4LmRpZ2l0c1tpXSAhPSB5LmRpZ2l0c1tpXSkgewogICAgICAgICAgICBpZiAoeC5pc05lZykgewogICAgICAgICAgICAgICAgcmV0dXJuIDEgLSAyICogTnVtYmVyKHguZGlnaXRzW2ldID4geS5kaWdpdHNbaV0pOwogICAgICAgICAgICB9IGVsc2UgewogICAgICAgICAgICAgICAgcmV0dXJuIDEgLSAyICogTnVtYmVyKHguZGlnaXRzW2ldIDwgeS5kaWdpdHNbaV0pOwogICAgICAgICAgICB9CiAgICAgICAgfQogICAgfQogICAgcmV0dXJuIDA7Cn0KCmZ1bmN0aW9uIGJpRGl2aWRlTW9kdWxvKHgsIHkpCnsKICAgIHZhciBuYiA9IGJpTnVtQml0cyh4KTsKICAgIHZhciB0YiA9IGJpTnVtQml0cyh5KTsKICAgIHZhciBvcmlnWUlzTmVnID0geS5pc05lZzsKICAgIHZhciBxLCByOwogICAgaWYgKG5iIDwgdGIpIHsKICAgICAgICAvLyB8eHwgPCB8eXwKICAgICAgICBpZiAoeC5pc05lZykgewogICAgICAgICAgICBxID0gYmlDb3B5KGJpZ09uZSk7CiAgICAgICAgICAgIHEuaXNOZWcgPSAheS5pc05lZzsKICAgICAgICAgICAgeC5pc05lZyA9IGZhbHNlOwogICAgICAgICAgICB5LmlzTmVnID0gZmFsc2U7CiAgICAgICAgICAgIHIgPSBiaVN1YnRyYWN0KHksIHgpOwogICAgICAgICAgICAvLyBSZXN0b3JlIHNpZ25zLCAnY2F1c2UgdGhleSdyZSByZWZlcmVuY2VzLgogICAgICAgICAgICB4LmlzTmVnID0gdHJ1ZTsKICAgICAgICAgICAgeS5pc05lZyA9IG9yaWdZSXNOZWc7CiAgICAgICAgfSBlbHNlIHsKICAgICAgICAgICAgcSA9IG5ldyBCaWdJbnQoKTsKICAgICAgICAgICAgciA9IGJpQ29weSh4KTsKICAgICAgICB9CiAgICAgICAgcmV0dXJuIG5ldyBBcnJheShxLCByKTsKICAgIH0KCiAgICBxID0gbmV3IEJpZ0ludCgpOwogICAgciA9IHg7CgogICAgLy8gTm9ybWFsaXplIFkuCiAgICB2YXIgdCA9IE1hdGguY2VpbCh0YiAvIGJpdHNQZXJEaWdpdCkgLSAxOwogICAgdmFyIGxhbWJkYSA9IDA7CiAgICB3aGlsZSAoeS5kaWdpdHNbdF0gPCBiaUhhbGZSYWRpeCkgewogICAgICAgIHkgPSBiaVNoaWZ0TGVmdCh5LCAxKTsKICAgICAgICArK2xhbWJkYTsKICAgICAgICArK3RiOwogICAgICAgIHQgPSBNYXRoLmNlaWwodGIgLyBiaXRzUGVyRGlnaXQpIC0gMTsKICAgIH0KICAgIC8vIFNoaWZ0IHIgb3ZlciB0byBrZWVwIHRoZSBxdW90aWVudCBjb25zdGFudC4gV2UnbGwgc2hpZnQgdGhlCiAgICAvLyByZW1haW5kZXIgYmFjayBhdCB0aGUgZW5kLgogICAgciA9IGJpU2hpZnRMZWZ0KHIsIGxhbWJkYSk7CiAgICBuYiArPSBsYW1iZGE7IC8vIFVwZGF0ZSB0aGUgYml0IGNvdW50IGZvciB4LgogICAgdmFyIG4gPSBNYXRoLmNlaWwobmIgLyBiaXRzUGVyRGlnaXQpIC0gMTsKCiAgICB2YXIgYiA9IGJpTXVsdGlwbHlCeVJhZGl4UG93ZXIoeSwgbiAtIHQpOwogICAgd2hpbGUgKGJpQ29tcGFyZShyLCBiKSAhPSAtMSkgewogICAgICAgICsrcS5kaWdpdHNbbiAtIHRdOwogICAgICAgIHIgPSBiaVN1YnRyYWN0KHIsIGIpOwogICAgfQogICAgZm9yICh2YXIgaSA9IG47IGkgPiB0OyAtLWkpIHsKICAgICAgICB2YXIgcmkgPSAoaSA+PSByLmRpZ2l0cy5sZW5ndGgpID8gMCA6IHIuZGlnaXRzW2ldOwogICAgICAgIHZhciByaTEgPSAoaSAtIDEgPj0gci5kaWdpdHMubGVuZ3RoKSA/IDAgOiByLmRpZ2l0c1tpIC0gMV07CiAgICAgICAgdmFyIHJpMiA9IChpIC0gMiA+PSByLmRpZ2l0cy5sZW5ndGgpID8gMCA6IHIuZGlnaXRzW2kgLSAyXTsKICAgICAgICB2YXIgeXQgPSAodCA+PSB5LmRpZ2l0cy5sZW5ndGgpID8gMCA6IHkuZGlnaXRzW3RdOwogICAgICAgIHZhciB5dDEgPSAodCAtIDEgPj0geS5kaWdpdHMubGVuZ3RoKSA/IDAgOiB5LmRpZ2l0c1t0IC0gMV07CiAgICAgICAgaWYgKHJpID09IHl0KSB7CiAgICAgICAgICAgIHEuZGlnaXRzW2kgLSB0IC0gMV0gPSBtYXhEaWdpdFZhbDsKICAgICAgICB9IGVsc2UgewogICAgICAgICAgICBxLmRpZ2l0c1tpIC0gdCAtIDFdID0gTWF0aC5mbG9vcigocmkgKiBiaVJhZGl4ICsgcmkxKSAvIHl0KTsKICAgICAgICB9CgogICAgICAgIHZhciBjMSA9IHEuZGlnaXRzW2kgLSB0IC0gMV0gKiAoKHl0ICogYmlSYWRpeCkgKyB5dDEpOwogICAgICAgIHZhciBjMiA9IChyaSAqIGJpUmFkaXhTcXVhcmVkKSArICgocmkxICogYmlSYWRpeCkgKyByaTIpOwogICAgICAgIHdoaWxlIChjMSA+IGMyKSB7CiAgICAgICAgICAgIC0tcS5kaWdpdHNbaSAtIHQgLSAxXTsKICAgICAgICAgICAgYzEgPSBxLmRpZ2l0c1tpIC0gdCAtIDFdICogKCh5dCAqIGJpUmFkaXgpIHwgeXQxKTsKICAgICAgICAgICAgYzIgPSAocmkgKiBiaVJhZGl4ICogYmlSYWRpeCkgKyAoKHJpMSAqIGJpUmFkaXgpICsgcmkyKTsKICAgICAgICB9CgogICAgICAgIGIgPSBiaU11bHRpcGx5QnlSYWRpeFBvd2VyKHksIGkgLSB0IC0gMSk7CiAgICAgICAgciA9IGJpU3VidHJhY3QociwgYmlNdWx0aXBseURpZ2l0KGIsIHEuZGlnaXRzW2kgLSB0IC0gMV0pKTsKICAgICAgICBpZiAoci5pc05lZykgewogICAgICAgICAgICByID0gYmlBZGQociwgYik7CiAgICAgICAgICAgIC0tcS5kaWdpdHNbaSAtIHQgLSAxXTsKICAgICAgICB9CiAgICB9CiAgICByID0gYmlTaGlmdFJpZ2h0KHIsIGxhbWJkYSk7CiAgICAvLyBGaWRkbGUgd2l0aCB0aGUgc2lnbnMgYW5kIHN0dWZmIHRvIG1ha2Ugc3VyZSB0aGF0IDAgPD0gciA8IHkuCiAgICBxLmlzTmVnID0geC5pc05lZyAhPSBvcmlnWUlzTmVnOwogICAgaWYgKHguaXNOZWcpIHsKICAgICAgICBpZiAob3JpZ1lJc05lZykgewogICAgICAgICAgICBxID0gYmlBZGQocSwgYmlnT25lKTsKICAgICAgICB9IGVsc2UgewogICAgICAgICAgICBxID0gYmlTdWJ0cmFjdChxLCBiaWdPbmUpOwogICAgICAgIH0KICAgICAgICB5ID0gYmlTaGlmdFJpZ2h0KHksIGxhbWJkYSk7CiAgICAgICAgciA9IGJpU3VidHJhY3QoeSwgcik7CiAgICB9CiAgICAvLyBDaGVjayBmb3IgdGhlIHVuYmVsaWV2YWJseSBzdHVwaWQgZGVnZW5lcmF0ZSBjYXNlIG9mIHIgPT0gLTAuCiAgICBpZiAoci5kaWdpdHNbMF0gPT0gMCAmJiBiaUhpZ2hJbmRleChyKSA9PSAwKSByLmlzTmVnID0gZmFsc2U7CgogICAgcmV0dXJuIG5ldyBBcnJheShxLCByKTsKfQoKZnVuY3Rpb24gYmlEaXZpZGUoeCwgeSkKewogICAgcmV0dXJuIGJpRGl2aWRlTW9kdWxvKHgsIHkpWzBdOwp9CgpmdW5jdGlvbiBiaU1vZHVsbyh4LCB5KQp7CiAgICByZXR1cm4gYmlEaXZpZGVNb2R1bG8oeCwgeSlbMV07Cn0KCmZ1bmN0aW9uIGJpTXVsdGlwbHlNb2QoeCwgeSwgbSkKewogICAgcmV0dXJuIGJpTW9kdWxvKGJpTXVsdGlwbHkoeCwgeSksIG0pOwp9CgpmdW5jdGlvbiBiaVBvdyh4LCB5KQp7CiAgICB2YXIgcmVzdWx0ID0gYmlnT25lOwogICAgdmFyIGEgPSB4OwogICAgd2hpbGUgKHRydWUpIHsKICAgICAgICBpZiAoKHkgJiAxKSAhPSAwKSByZXN1bHQgPSBiaU11bHRpcGx5KHJlc3VsdCwgYSk7CiAgICAgICAgeSA+Pj0gMTsKICAgICAgICBpZiAoeSA9PSAwKSBicmVhazsKICAgICAgICBhID0gYmlNdWx0aXBseShhLCBhKTsKICAgIH0KICAgIHJldHVybiByZXN1bHQ7Cn0KCmZ1bmN0aW9uIGJpUG93TW9kKHgsIHksIG0pCnsKICAgIHZhciByZXN1bHQgPSBiaWdPbmU7CiAgICB2YXIgYSA9IHg7CiAgICB2YXIgayA9IHk7CiAgICB3aGlsZSAodHJ1ZSkgewogICAgICAgIGlmICgoay5kaWdpdHNbMF0gJiAxKSAhPSAwKSByZXN1bHQgPSBiaU11bHRpcGx5TW9kKHJlc3VsdCwgYSwgbSk7CiAgICAgICAgayA9IGJpU2hpZnRSaWdodChrLCAxKTsKICAgICAgICBpZiAoay5kaWdpdHNbMF0gPT0gMCAmJiBiaUhpZ2hJbmRleChrKSA9PSAwKSBicmVhazsKICAgICAgICBhID0gYmlNdWx0aXBseU1vZChhLCBhLCBtKTsKICAgIH0KICAgIHJldHVybiByZXN1bHQ7Cn0K";
    private static final Logger logger = LoggerFactory.getLogger(HuBei10086ForWeb.class);

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        try {
            //登陆页没有获取任何cookie,不用登陆
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={}", param, e);
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
        switch (param.getFormType()) {
            case "CALL_DETAILS":
                return processForDetails(param, "GSM");
            case "SMS_DETAILS":
                return processForDetails(param, "SMS");
            case "NET_DETAILS":
                return processForDetails(param, "GPRSWLAN");
            default:
                return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
        }
    }

    private HttpResult<String> refeshPicCodeForLogin(OperatorParam param) {
        /**
         * 这里不一定有图片验证码,随机出现
         */
        HttpResult<String> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "https://hb.ac.10086.cn/SSO/img?codeType=0&rand={}";
            response = TaskHttpClient.create(param.getTaskId(), param.getWebsiteName(), RequestType.GET, "hu_bei_10086_web_001")
                    .setFullUrl(templateUrl, System.currentTimeMillis()).invoke();
            logger.info("登录-->图片验证码-->刷新成功,param={}", param);
            return result.success(response.getPageContentForBase64());
        } catch (Exception e) {
            logger.error("登录-->图片验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_PIC_CODE_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getPicCode(), ErrorCode.EMPTY_PIC_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String referer = "https://hb.ac.10086.cn/SSO/loginbox?service=servicenew&style=mymobile&continue=http://www" +
                    ".hb.10086.cn/servicenew/index.action";
            String templateUrl = "https://hb.ac.10086.cn/SSO/loginbox?accountType=0&username={}&passwordType=1&password={}" +
                    "&smsRandomCode=&emailusername=请输入登录帐号&emailpassword=&validateCode={}&action=/SSO/loginbox&style=mymobile&service=servicenew" +
                    "&continue=http://www.hb.10086.cn/servicenew/index.action&submitMode=login&guestIP=";
            response = TaskHttpClient.create(param, RequestType.POST, "hu_bei_10086_web_002")
                    .setFullUrl(templateUrl, param.getMobile(), param.getPassword(), param.getPicCode()).setReferer(referer).invoke();
            String pageContent = processSSOLogin(param, response.getPageContent());
            if (StringUtils.isBlank(pageContent)) {
                logger.error("登陆失败,param={},response={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
            templateUrl = PatternUtils.group(pageContent, "window\\.parent\\.location\\.href='([^']+)'", 1);
            if (StringUtils.isNotEmpty(templateUrl)) {
                response = TaskHttpClient.create(param, RequestType.GET, "hu_bei_10086_web_004").setFullUrl(templateUrl).invoke();
            }

            templateUrl = "http://www.hb.10086.cn/my/balance/QryBal.action";
            response = TaskHttpClient.create(param, RequestType.POST, "hu_bei_10086_web_005").setFullUrl(templateUrl).invoke();
            templateUrl = "http://www.hb.10086.cn/my/balance/QryBal.action";
            response = TaskHttpClient.create(param, RequestType.GET, "hu_bei_10086_web_006").setFullUrl(templateUrl).invoke();
            pageContent = response.getPageContent();
            if (StringUtils.isNotBlank(pageContent) && pageContent.contains("success")) {
                logger.info("登陆成功,param={}", param);

                String rsaModule
                        = "8a4928b7e4ce5943230539120cb6ee7a64000034b11b923a91faf8c381dd09b4a9a9a6fa02ca0bd3b90576ac1498983f7c78d8f8f5126a24a30f75eac86815c3430fe3e77f81a326d0d2f7ffbfe285bb368175d66c29777ec031c0c75f64da92aa43866fdfa2597cfb4ce614f450e95670be7cc27e4b05b7a48ca876305e5d51";
                String rsaEmpoent = "10001";
                templateUrl = "http://www.hb.10086.cn/my/index.action";
                response = TaskHttpClient.create(param, RequestType.GET, "hu_bei_10086_web_007").setFullUrl(templateUrl).invoke();
                pageContent = response.getPageContent();
                if (StringUtils.isNotBlank(pageContent)) {
                    List<String> rsaModuleList = XPathUtil.getXpath("//input[@id='rsaModule']/@value", pageContent);
                    for (String string : rsaModuleList) {
                        if (StringUtils.isNotBlank(string)) {
                            rsaModule = string;
                        }
                    }
                    List<String> rsaEmpoentList = XPathUtil.getXpath("//input[@id='rsaEmpoent']/@value", pageContent);
                    for (String string : rsaEmpoentList) {
                        if (StringUtils.isNotBlank(string)) {
                            rsaEmpoent = string;
                        }
                    }
                }
                TaskUtils.addTaskShare(param.getTaskId(), "rsaModule", rsaModule);
                TaskUtils.addTaskShare(param.getTaskId(), "rsaEmpoent", rsaEmpoent);

                return result.success();
            } else {
                logger.error("登陆失败,param={},pageContent={}", param, response.getPageContent());
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登陆失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.LOGIN_ERROR);
        }
    }

    private String processSSOLogin(OperatorParam param, String pageContent) throws UnsupportedEncodingException {
        String action = "";
        String relayState = "";
        String samLart = "";
        String passwordType = "";
        String accountType = "";
        String errorMsg = "";
        String errFlag = "";
        String telNum = "";
        String artifact = "";

        List<String> actionList = XPathUtil.getXpath("//form[@id='sso']/@action", pageContent);
        for (String string : actionList) {
            action = string;
        }
        List<String> relayStateList = XPathUtil.getXpath("//input[@name='RelayState']/@value", pageContent);
        for (String string : relayStateList) {
            relayState = string;
        }
        List<String> samLartList = XPathUtil.getXpath("//input[@name='SAMLart']/@value", pageContent);
        for (String string : samLartList) {
            samLart = string;
        }
        List<String> artifactList = XPathUtil.getXpath("//input[@name='artifact']/@value", pageContent);
        for (String string : artifactList) {
            artifact = string;
        }
        List<String> passwordTypeList = XPathUtil.getXpath("//input[@name='PasswordType']/@value", pageContent);
        for (String string : passwordTypeList) {
            passwordType = string;
        }
        List<String> accountTypeList = XPathUtil.getXpath("//input[@name='accountType']/@value", pageContent);
        for (String string : accountTypeList) {
            accountType = string;
        }
        List<String> errorMsgList = XPathUtil.getXpath("//input[@name='errorMsg']/@value", pageContent);
        for (String string : errorMsgList) {
            errorMsg = string;
        }
        List<String> errFlagList = XPathUtil.getXpath("//input[@name='errFlag']/@value", pageContent);
        for (String string : errFlagList) {
            errFlag = string;
        }
        List<String> telNumList = XPathUtil.getXpath("//input[@name='telNum']/@value", pageContent);
        for (String string : telNumList) {
            telNum = string;
        }
        if (StringUtils.isEmpty(samLart)) {
            logger.info("request sso login url error! samLart is empty! pageContent: " + pageContent);
            return null;
        }
        String templateUrl = "{}?timeStamp={}&RelayState={}&SAMLart={}&artifact={}&accountType={}&PasswordType={}&errorMsg={}&errFlag={}&telNum={}";
        Response response = TaskHttpClient.create(param, RequestType.POST, "hu_bei_10086_web_003")
                .setFullUrl(templateUrl, action, System.currentTimeMillis(), relayState, samLart, artifact, accountType, passwordType, errorMsg,
                        errFlag, telNum).invoke();

        return response.getPageContent();
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://www.hb.10086.cn/my/balance/smsRandomPass!sendSmsCheckCode.action?menuid=myDetailBill";
            response = TaskHttpClient.create(param, RequestType.POST, "hu_bei_10086_web_003").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();
            if (pageContent.contains("\"result\":\"1\"")) {
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
        String rsaEmpoent = TaskUtils.getTaskShare(param.getTaskId(), "rsaEmpoent");
        String rsaModule = TaskUtils.getTaskShare(param.getTaskId(), "rsaModule");
        if (StringUtils.isBlank(rsaModule) || StringUtils.isBlank(rsaEmpoent)) {
            logger.error("详单-->校验失败,没有rsaModule或rsaEmpoent,param={},pageContent={}", param, response.getPageContent());
            return result.failure(ErrorCode.VALIDATE_UNEXPECTED_RESULT);
        }
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("hu_bei_10086_web/des.js");
            Invocable invocable = ScriptEngineUtil.createInvocableFromBase64(javaScript);
            invocable.invokeFunction("setKey", rsaEmpoent, "", rsaModule);
            String encryptPwd = (String) invocable.invokeFunction("encryptedString", param.getPassword());
            String encryptSmsCode = (String) invocable.invokeFunction("encryptedString", param.getSmsCode());
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
            String templateUrl
                    = "http://www.hb.10086.cn/my/detailbill/detailBillQry.action?postion=outer&detailBean.billcycle={}&detailBean.selecttype=0&detailBean.flag=GSM&selecttype=%E5%85%A8%E9%83%A8%E6%9F%A5%E8%AF%A2&flag=%E9%80%9A%E8%AF%9D%E8%AF%A6%E5%8D%95&detailBean.password={}&detailBean.chkey={}";
            response = TaskHttpClient.create(param, RequestType.POST, "hu_bei_10086_web_003")
                    .setFullUrl(templateUrl, sf.format(new Date()), encryptPwd, encryptSmsCode).invoke();
            String pageContent = response.getPageContent();
            if (!pageContent.contains("暂时无法为您提供服务")) {
                logger.info("详单-->校验成功,param={}", param);
                return result.success();
            } else {
                logger.warn("详单-->短信验证码错误,param={}", param);
                return result.failure(ErrorCode.VALIDATE_SMS_FAIL);
            }
        } catch (Exception e) {
            logger.error("详单-->校验失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.VALIDATE_ERROR);
        }
    }

    private HttpResult<Object> processForDetails(OperatorParam param, String queryType) {
        HttpResult<Object> result = new HttpResult<>();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> paramMap = (LinkedHashMap<String, String>) GsonUtils
                .fromJson(param.getArgs()[0], new TypeToken<LinkedHashMap<String, String>>() {}.getType());
        String[] params = paramMap.get("page_content").split(":");
        Response response = null;
        try {
            String content = "{\"data\":[";
            StringBuilder stringBuilder = new StringBuilder(content);
            String smsCode = TaskUtils.getTaskShare(param.getTaskId(), RedisKeyPrefixEnum.TASK_SMS.getRedisKey(FormType.VALIDATE_BILL_DETAIL));

            String templateUrl = "http://www.hb.10086.cn/my/detailbill/generateNewDetailExcel.action?menuid=myDetailBill&detailBean" +
                    ".billcycle={}&detailBean.password={}&detailBean.chkey={}&detailBean.startdate={}&detailBean" +
                    ".enddate={}&detailBean.flag={}&detailBean.selecttype=0";
            response = TaskHttpClient.create(param, RequestType.POST, "hu_bei_10086_web_003")
                    .setFullUrl(templateUrl, params[0], param.getPassword(), smsCode, params[1], params[2], queryType).invoke();
            byte[] bytes = response.getResponse();
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            String str = getJsonBody(in, queryType);
            if (StringUtils.isNotBlank(str)) {
                stringBuilder.append(str + ",");
            }
            if (stringBuilder.length() > 9) {
                stringBuilder.substring(0, stringBuilder.length() - 1);
            }
            stringBuilder.append("]}");
            map.put("pageContent", stringBuilder.toString());
            map.put("pageContentFile", bytes);
            return result.success(map);
        } catch (Exception e) {
            logger.error("解析详单PDF失败,param={},response={}", param, e);
            return result.failure(ErrorCode.UNKNOWN_REASON);
        }
    }

    private String getJsonBody(InputStream inputStream, String queryType) throws IOException {
        try {
            // 设置读文件编码
            WorkbookSettings setEncode = new WorkbookSettings();
            // 从文件流中获取Excel工作区对象（WorkBook）
            Workbook wb = Workbook.getWorkbook(inputStream, setEncode);
            Sheet sheet = wb.getSheet(0);
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < sheet.getRows(); j++) {
                if (StringUtils.equals("GSM", queryType)) {
                    if (sheet.getCell(0, j).getContents().trim().matches("\\d{4}-\\d+-\\d+\\s*\\d{2}:\\d{2}:\\d{2}")) {
                        stringBuilder.append("{\"callStartDateTime\":\"" + sheet.getCell(0, j).getContents().trim() + "\",\"callLocation\":\"" +
                                sheet.getCell(1, j).getContents().trim() + "\",\"callType\":\"" + sheet.getCell(2, j).getContents().trim() +
                                "\",\"otherTelNum\":\"" + sheet.getCell(3, j).getContents().trim() + "\",\"callDuration\":\"" +
                                sheet.getCell(4, j).getContents().trim() + "\",\"callTypeDetail\":\"" + sheet.getCell(5, j).getContents().trim() +
                                "\",\"totalFee\":\"" + sheet.getCell(6, j).getContents().trim() + "\"},");
                    }
                } else if (StringUtils.equals("SMS", queryType)) {
                    if (sheet.getCell(0, j).getContents().trim().matches("\\d{4}-\\d+-\\d+\\s*\\d{2}:\\d{2}:\\d{2}")) {
                        stringBuilder.append("{\"smsDateTime\":\"" + sheet.getCell(0, j).getContents().trim() + "\",\"otherNum\":\"" +
                                sheet.getCell(2, j).getContents().trim() + "\",\"smsType\":\"" + sheet.getCell(3, j).getContents().trim() +
                                "\",\"businessType\":\"" + sheet.getCell(4, j).getContents().trim() + "\",\"fee\":\"" +
                                sheet.getCell(6, j).getContents().trim() + "\"},");
                    }
                } else if (StringUtils.equals("GPRSWLAN", queryType)) {
                    if (sheet.getCell(0, j).getContents().trim().matches("\\d{4}-\\d+-\\d+\\s*\\d{2}:\\d{2}:\\d{2}")) {
                        stringBuilder.append("{\"netStartDateTime\":\"" + sheet.getCell(0, j).getContents().trim() + "\",\"netLocation\":\"" +
                                sheet.getCell(1, j).getContents().trim() + "\",\"businessType\":\"" + sheet.getCell(2, j).getContents().trim() +
                                "\",\"netDuration\":\"" + sheet.getCell(3, j).getContents().trim() + "\",\"totalFlow\":\"" +
                                sheet.getCell(4, j).getContents().trim() + "\",\"totalFee\":\"" + sheet.getCell(6, j).getContents().trim() + "\"},");
                    }
                }
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.substring(0, stringBuilder.length() - 1);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) inputStream.close();
        }
        return null;
    }
}
