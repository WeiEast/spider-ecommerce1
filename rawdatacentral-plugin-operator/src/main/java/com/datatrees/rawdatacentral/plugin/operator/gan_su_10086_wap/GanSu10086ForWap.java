package com.datatrees.rawdatacentral.plugin.operator.gan_su_10086_wap;

import javax.script.Invocable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.RegexpUtils;
import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import com.datatrees.rawdatacentral.service.OperatorPluginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by guimeichao on 17/9/15.
 */
public class GanSu10086ForWap implements OperatorPluginService {

    private static final Logger logger     = LoggerFactory.getLogger(GanSu10086ForWap.class);
    private static final String javaScript
                                           = "LyoKICogQSBKYXZhU2NyaXB0IGltcGxlbWVudGF0aW9uIG9mIHRoZSBSU0EgRGF0YSBTZWN1cml0eSwgSW5jLiBNRDUgTWVzc2FnZQogKiBEaWdlc3QgQWxnb3JpdGhtLCBhcyBkZWZpbmVkIGluIFJGQyAxMzIxLgogKiBWZXJzaW9uIDIuMSBDb3B5cmlnaHQgKEMpIFBhdWwgSm9obnN0b24gMTk5OSAtIDIwMDIuCiAqIE90aGVyIGNvbnRyaWJ1dG9yczogR3JlZyBIb2x0LCBBbmRyZXcgS2VwZXJ0LCBZZG5hciwgTG9zdGluZXQKICogRGlzdHJpYnV0ZWQgdW5kZXIgdGhlIEJTRCBMaWNlbnNlCiAqIFNlZSBodHRwOi8vcGFqaG9tZS5vcmcudWsvY3J5cHQvbWQ1IGZvciBtb3JlIGluZm8uCiAqLwoKLyoKICogQ29uZmlndXJhYmxlIHZhcmlhYmxlcy4gWW91IG1heSBuZWVkIHRvIHR3ZWFrIHRoZXNlIHRvIGJlIGNvbXBhdGlibGUgd2l0aAogKiB0aGUgc2VydmVyLXNpZGUsIGJ1dCB0aGUgZGVmYXVsdHMgd29yayBpbiBtb3N0IGNhc2VzLgogKi8KdmFyIGhleGNhc2UgPSAwOyAgLyogaGV4IG91dHB1dCBmb3JtYXQuIDAgLSBsb3dlcmNhc2U7IDEgLSB1cHBlcmNhc2UgICAgICAgICovCnZhciBiNjRwYWQgID0gIiI7IC8qIGJhc2UtNjQgcGFkIGNoYXJhY3Rlci4gIj0iIGZvciBzdHJpY3QgUkZDIGNvbXBsaWFuY2UgICAqLwp2YXIgY2hyc3ogICA9IDg7ICAvKiBiaXRzIHBlciBpbnB1dCBjaGFyYWN0ZXIuIDggLSBBU0NJSTsgMTYgLSBVbmljb2RlICAgICAgKi8KCi8qCiAqIFRoZXNlIGFyZSB0aGUgZnVuY3Rpb25zIHlvdSdsbCB1c3VhbGx5IHdhbnQgdG8gY2FsbAogKiBUaGV5IHRha2Ugc3RyaW5nIGFyZ3VtZW50cyBhbmQgcmV0dXJuIGVpdGhlciBoZXggb3IgYmFzZS02NCBlbmNvZGVkIHN0cmluZ3MKICovCmZ1bmN0aW9uIGhleF9tZDUocyl7IHJldHVybiBiaW5sMmhleChjb3JlX21kNShzdHIyYmlubChzKSwgcy5sZW5ndGggKiBjaHJzeikpO30KZnVuY3Rpb24gYjY0X21kNShzKXsgcmV0dXJuIGJpbmwyYjY0KGNvcmVfbWQ1KHN0cjJiaW5sKHMpLCBzLmxlbmd0aCAqIGNocnN6KSk7fQpmdW5jdGlvbiBzdHJfbWQ1KHMpeyByZXR1cm4gYmlubDJzdHIoY29yZV9tZDUoc3RyMmJpbmwocyksIHMubGVuZ3RoICogY2hyc3opKTt9CmZ1bmN0aW9uIGhleF9obWFjX21kNShrZXksIGRhdGEpIHsgcmV0dXJuIGJpbmwyaGV4KGNvcmVfaG1hY19tZDUoa2V5LCBkYXRhKSk7IH0KZnVuY3Rpb24gYjY0X2htYWNfbWQ1KGtleSwgZGF0YSkgeyByZXR1cm4gYmlubDJiNjQoY29yZV9obWFjX21kNShrZXksIGRhdGEpKTsgfQpmdW5jdGlvbiBzdHJfaG1hY19tZDUoa2V5LCBkYXRhKSB7IHJldHVybiBiaW5sMnN0cihjb3JlX2htYWNfbWQ1KGtleSwgZGF0YSkpOyB9CgovKgogKiBQZXJmb3JtIGEgc2ltcGxlIHNlbGYtdGVzdCB0byBzZWUgaWYgdGhlIFZNIGlzIHdvcmtpbmcKICovCmZ1bmN0aW9uIG1kNV92bV90ZXN0KCkKewogIHJldHVybiBoZXhfbWQ1KCJhYmMiKSA9PSAiOTAwMTUwOTgzY2QyNGZiMGQ2OTYzZjdkMjhlMTdmNzIiOwp9CgovKgogKiBDYWxjdWxhdGUgdGhlIE1ENSBvZiBhbiBhcnJheSBvZiBsaXR0bGUtZW5kaWFuIHdvcmRzLCBhbmQgYSBiaXQgbGVuZ3RoCiAqLwpmdW5jdGlvbiBjb3JlX21kNSh4LCBsZW4pCnsKICAvKiBhcHBlbmQgcGFkZGluZyAqLwogIHhbbGVuID4+IDVdIHw9IDB4ODAgPDwgKChsZW4pICUgMzIpOwogIHhbKCgobGVuICsgNjQpID4+PiA5KSA8PCA0KSArIDE0XSA9IGxlbjsKCiAgdmFyIGEgPSAgMTczMjU4NDE5MzsKICB2YXIgYiA9IC0yNzE3MzM4Nzk7CiAgdmFyIGMgPSAtMTczMjU4NDE5NDsKICB2YXIgZCA9ICAyNzE3MzM4Nzg7CgogIGZvcih2YXIgaSA9IDA7IGkgPCB4Lmxlbmd0aDsgaSArPSAxNikKICB7CiAgICB2YXIgb2xkYSA9IGE7CiAgICB2YXIgb2xkYiA9IGI7CiAgICB2YXIgb2xkYyA9IGM7CiAgICB2YXIgb2xkZCA9IGQ7CgogICAgYSA9IG1kNV9mZihhLCBiLCBjLCBkLCB4W2krIDBdLCA3ICwgLTY4MDg3NjkzNik7CiAgICBkID0gbWQ1X2ZmKGQsIGEsIGIsIGMsIHhbaSsgMV0sIDEyLCAtMzg5NTY0NTg2KTsKICAgIGMgPSBtZDVfZmYoYywgZCwgYSwgYiwgeFtpKyAyXSwgMTcsICA2MDYxMDU4MTkpOwogICAgYiA9IG1kNV9mZihiLCBjLCBkLCBhLCB4W2krIDNdLCAyMiwgLTEwNDQ1MjUzMzApOwogICAgYSA9IG1kNV9mZihhLCBiLCBjLCBkLCB4W2krIDRdLCA3ICwgLTE3NjQxODg5Nyk7CiAgICBkID0gbWQ1X2ZmKGQsIGEsIGIsIGMsIHhbaSsgNV0sIDEyLCAgMTIwMDA4MDQyNik7CiAgICBjID0gbWQ1X2ZmKGMsIGQsIGEsIGIsIHhbaSsgNl0sIDE3LCAtMTQ3MzIzMTM0MSk7CiAgICBiID0gbWQ1X2ZmKGIsIGMsIGQsIGEsIHhbaSsgN10sIDIyLCAtNDU3MDU5ODMpOwogICAgYSA9IG1kNV9mZihhLCBiLCBjLCBkLCB4W2krIDhdLCA3ICwgIDE3NzAwMzU0MTYpOwogICAgZCA9IG1kNV9mZihkLCBhLCBiLCBjLCB4W2krIDldLCAxMiwgLTE5NTg0MTQ0MTcpOwogICAgYyA9IG1kNV9mZihjLCBkLCBhLCBiLCB4W2krMTBdLCAxNywgLTQyMDYzKTsKICAgIGIgPSBtZDVfZmYoYiwgYywgZCwgYSwgeFtpKzExXSwgMjIsIC0xOTkwNDA0MTYyKTsKICAgIGEgPSBtZDVfZmYoYSwgYiwgYywgZCwgeFtpKzEyXSwgNyAsICAxODA0NjAzNjgyKTsKICAgIGQgPSBtZDVfZmYoZCwgYSwgYiwgYywgeFtpKzEzXSwgMTIsIC00MDM0MTEwMSk7CiAgICBjID0gbWQ1X2ZmKGMsIGQsIGEsIGIsIHhbaSsxNF0sIDE3LCAtMTUwMjAwMjI5MCk7CiAgICBiID0gbWQ1X2ZmKGIsIGMsIGQsIGEsIHhbaSsxNV0sIDIyLCAgMTIzNjUzNTMyOSk7CgogICAgYSA9IG1kNV9nZyhhLCBiLCBjLCBkLCB4W2krIDFdLCA1ICwgLTE2NTc5NjUxMCk7CiAgICBkID0gbWQ1X2dnKGQsIGEsIGIsIGMsIHhbaSsgNl0sIDkgLCAtMTA2OTUwMTYzMik7CiAgICBjID0gbWQ1X2dnKGMsIGQsIGEsIGIsIHhbaSsxMV0sIDE0LCAgNjQzNzE3NzEzKTsKICAgIGIgPSBtZDVfZ2coYiwgYywgZCwgYSwgeFtpKyAwXSwgMjAsIC0zNzM4OTczMDIpOwogICAgYSA9IG1kNV9nZyhhLCBiLCBjLCBkLCB4W2krIDVdLCA1ICwgLTcwMTU1ODY5MSk7CiAgICBkID0gbWQ1X2dnKGQsIGEsIGIsIGMsIHhbaSsxMF0sIDkgLCAgMzgwMTYwODMpOwogICAgYyA9IG1kNV9nZyhjLCBkLCBhLCBiLCB4W2krMTVdLCAxNCwgLTY2MDQ3ODMzNSk7CiAgICBiID0gbWQ1X2dnKGIsIGMsIGQsIGEsIHhbaSsgNF0sIDIwLCAtNDA1NTM3ODQ4KTsKICAgIGEgPSBtZDVfZ2coYSwgYiwgYywgZCwgeFtpKyA5XSwgNSAsICA1Njg0NDY0MzgpOwogICAgZCA9IG1kNV9nZyhkLCBhLCBiLCBjLCB4W2krMTRdLCA5ICwgLTEwMTk4MDM2OTApOwogICAgYyA9IG1kNV9nZyhjLCBkLCBhLCBiLCB4W2krIDNdLCAxNCwgLTE4NzM2Mzk2MSk7CiAgICBiID0gbWQ1X2dnKGIsIGMsIGQsIGEsIHhbaSsgOF0sIDIwLCAgMTE2MzUzMTUwMSk7CiAgICBhID0gbWQ1X2dnKGEsIGIsIGMsIGQsIHhbaSsxM10sIDUgLCAtMTQ0NDY4MTQ2Nyk7CiAgICBkID0gbWQ1X2dnKGQsIGEsIGIsIGMsIHhbaSsgMl0sIDkgLCAtNTE0MDM3ODQpOwogICAgYyA9IG1kNV9nZyhjLCBkLCBhLCBiLCB4W2krIDddLCAxNCwgIDE3MzUzMjg0NzMpOwogICAgYiA9IG1kNV9nZyhiLCBjLCBkLCBhLCB4W2krMTJdLCAyMCwgLTE5MjY2MDc3MzQpOwoKICAgIGEgPSBtZDVfaGgoYSwgYiwgYywgZCwgeFtpKyA1XSwgNCAsIC0zNzg1NTgpOwogICAgZCA9IG1kNV9oaChkLCBhLCBiLCBjLCB4W2krIDhdLCAxMSwgLTIwMjI1NzQ0NjMpOwogICAgYyA9IG1kNV9oaChjLCBkLCBhLCBiLCB4W2krMTFdLCAxNiwgIDE4MzkwMzA1NjIpOwogICAgYiA9IG1kNV9oaChiLCBjLCBkLCBhLCB4W2krMTRdLCAyMywgLTM1MzA5NTU2KTsKICAgIGEgPSBtZDVfaGgoYSwgYiwgYywgZCwgeFtpKyAxXSwgNCAsIC0xNTMwOTkyMDYwKTsKICAgIGQgPSBtZDVfaGgoZCwgYSwgYiwgYywgeFtpKyA0XSwgMTEsICAxMjcyODkzMzUzKTsKICAgIGMgPSBtZDVfaGgoYywgZCwgYSwgYiwgeFtpKyA3XSwgMTYsIC0xNTU0OTc2MzIpOwogICAgYiA9IG1kNV9oaChiLCBjLCBkLCBhLCB4W2krMTBdLCAyMywgLTEwOTQ3MzA2NDApOwogICAgYSA9IG1kNV9oaChhLCBiLCBjLCBkLCB4W2krMTNdLCA0ICwgIDY4MTI3OTE3NCk7CiAgICBkID0gbWQ1X2hoKGQsIGEsIGIsIGMsIHhbaSsgMF0sIDExLCAtMzU4NTM3MjIyKTsKICAgIGMgPSBtZDVfaGgoYywgZCwgYSwgYiwgeFtpKyAzXSwgMTYsIC03MjI1MjE5NzkpOwogICAgYiA9IG1kNV9oaChiLCBjLCBkLCBhLCB4W2krIDZdLCAyMywgIDc2MDI5MTg5KTsKICAgIGEgPSBtZDVfaGgoYSwgYiwgYywgZCwgeFtpKyA5XSwgNCAsIC02NDAzNjQ0ODcpOwogICAgZCA9IG1kNV9oaChkLCBhLCBiLCBjLCB4W2krMTJdLCAxMSwgLTQyMTgxNTgzNSk7CiAgICBjID0gbWQ1X2hoKGMsIGQsIGEsIGIsIHhbaSsxNV0sIDE2LCAgNTMwNzQyNTIwKTsKICAgIGIgPSBtZDVfaGgoYiwgYywgZCwgYSwgeFtpKyAyXSwgMjMsIC05OTUzMzg2NTEpOwoKICAgIGEgPSBtZDVfaWkoYSwgYiwgYywgZCwgeFtpKyAwXSwgNiAsIC0xOTg2MzA4NDQpOwogICAgZCA9IG1kNV9paShkLCBhLCBiLCBjLCB4W2krIDddLCAxMCwgIDExMjY4OTE0MTUpOwogICAgYyA9IG1kNV9paShjLCBkLCBhLCBiLCB4W2krMTRdLCAxNSwgLTE0MTYzNTQ5MDUpOwogICAgYiA9IG1kNV9paShiLCBjLCBkLCBhLCB4W2krIDVdLCAyMSwgLTU3NDM0MDU1KTsKICAgIGEgPSBtZDVfaWkoYSwgYiwgYywgZCwgeFtpKzEyXSwgNiAsICAxNzAwNDg1NTcxKTsKICAgIGQgPSBtZDVfaWkoZCwgYSwgYiwgYywgeFtpKyAzXSwgMTAsIC0xODk0OTg2NjA2KTsKICAgIGMgPSBtZDVfaWkoYywgZCwgYSwgYiwgeFtpKzEwXSwgMTUsIC0xMDUxNTIzKTsKICAgIGIgPSBtZDVfaWkoYiwgYywgZCwgYSwgeFtpKyAxXSwgMjEsIC0yMDU0OTIyNzk5KTsKICAgIGEgPSBtZDVfaWkoYSwgYiwgYywgZCwgeFtpKyA4XSwgNiAsICAxODczMzEzMzU5KTsKICAgIGQgPSBtZDVfaWkoZCwgYSwgYiwgYywgeFtpKzE1XSwgMTAsIC0zMDYxMTc0NCk7CiAgICBjID0gbWQ1X2lpKGMsIGQsIGEsIGIsIHhbaSsgNl0sIDE1LCAtMTU2MDE5ODM4MCk7CiAgICBiID0gbWQ1X2lpKGIsIGMsIGQsIGEsIHhbaSsxM10sIDIxLCAgMTMwOTE1MTY0OSk7CiAgICBhID0gbWQ1X2lpKGEsIGIsIGMsIGQsIHhbaSsgNF0sIDYgLCAtMTQ1NTIzMDcwKTsKICAgIGQgPSBtZDVfaWkoZCwgYSwgYiwgYywgeFtpKzExXSwgMTAsIC0xMTIwMjEwMzc5KTsKICAgIGMgPSBtZDVfaWkoYywgZCwgYSwgYiwgeFtpKyAyXSwgMTUsICA3MTg3ODcyNTkpOwogICAgYiA9IG1kNV9paShiLCBjLCBkLCBhLCB4W2krIDldLCAyMSwgLTM0MzQ4NTU1MSk7CgogICAgYSA9IHNhZmVfYWRkKGEsIG9sZGEpOwogICAgYiA9IHNhZmVfYWRkKGIsIG9sZGIpOwogICAgYyA9IHNhZmVfYWRkKGMsIG9sZGMpOwogICAgZCA9IHNhZmVfYWRkKGQsIG9sZGQpOwogIH0KICByZXR1cm4gQXJyYXkoYSwgYiwgYywgZCk7Cgp9CgovKgogKiBUaGVzZSBmdW5jdGlvbnMgaW1wbGVtZW50IHRoZSBmb3VyIGJhc2ljIG9wZXJhdGlvbnMgdGhlIGFsZ29yaXRobSB1c2VzLgogKi8KZnVuY3Rpb24gbWQ1X2NtbihxLCBhLCBiLCB4LCBzLCB0KQp7CiAgcmV0dXJuIHNhZmVfYWRkKGJpdF9yb2woc2FmZV9hZGQoc2FmZV9hZGQoYSwgcSksIHNhZmVfYWRkKHgsIHQpKSwgcyksYik7Cn0KZnVuY3Rpb24gbWQ1X2ZmKGEsIGIsIGMsIGQsIHgsIHMsIHQpCnsKICByZXR1cm4gbWQ1X2NtbigoYiAmIGMpIHwgKCh+YikgJiBkKSwgYSwgYiwgeCwgcywgdCk7Cn0KZnVuY3Rpb24gbWQ1X2dnKGEsIGIsIGMsIGQsIHgsIHMsIHQpCnsKICByZXR1cm4gbWQ1X2NtbigoYiAmIGQpIHwgKGMgJiAofmQpKSwgYSwgYiwgeCwgcywgdCk7Cn0KZnVuY3Rpb24gbWQ1X2hoKGEsIGIsIGMsIGQsIHgsIHMsIHQpCnsKICByZXR1cm4gbWQ1X2NtbihiIF4gYyBeIGQsIGEsIGIsIHgsIHMsIHQpOwp9CmZ1bmN0aW9uIG1kNV9paShhLCBiLCBjLCBkLCB4LCBzLCB0KQp7CiAgcmV0dXJuIG1kNV9jbW4oYyBeIChiIHwgKH5kKSksIGEsIGIsIHgsIHMsIHQpOwp9CgovKgogKiBDYWxjdWxhdGUgdGhlIEhNQUMtTUQ1LCBvZiBhIGtleSBhbmQgc29tZSBkYXRhCiAqLwpmdW5jdGlvbiBjb3JlX2htYWNfbWQ1KGtleSwgZGF0YSkKewogIHZhciBia2V5ID0gc3RyMmJpbmwoa2V5KTsKICBpZihia2V5Lmxlbmd0aCA+IDE2KSBia2V5ID0gY29yZV9tZDUoYmtleSwga2V5Lmxlbmd0aCAqIGNocnN6KTsKCiAgdmFyIGlwYWQgPSBBcnJheSgxNiksIG9wYWQgPSBBcnJheSgxNik7CiAgZm9yKHZhciBpID0gMDsgaSA8IDE2OyBpKyspCiAgewogICAgaXBhZFtpXSA9IGJrZXlbaV0gXiAweDM2MzYzNjM2OwogICAgb3BhZFtpXSA9IGJrZXlbaV0gXiAweDVDNUM1QzVDOwogIH0KCiAgdmFyIGhhc2ggPSBjb3JlX21kNShpcGFkLmNvbmNhdChzdHIyYmlubChkYXRhKSksIDUxMiArIGRhdGEubGVuZ3RoICogY2hyc3opOwogIHJldHVybiBjb3JlX21kNShvcGFkLmNvbmNhdChoYXNoKSwgNTEyICsgMTI4KTsKfQoKLyoKICogQWRkIGludGVnZXJzLCB3cmFwcGluZyBhdCAyXjMyLiBUaGlzIHVzZXMgMTYtYml0IG9wZXJhdGlvbnMgaW50ZXJuYWxseQogKiB0byB3b3JrIGFyb3VuZCBidWdzIGluIHNvbWUgSlMgaW50ZXJwcmV0ZXJzLgogKi8KZnVuY3Rpb24gc2FmZV9hZGQoeCwgeSkKewogIHZhciBsc3cgPSAoeCAmIDB4RkZGRikgKyAoeSAmIDB4RkZGRik7CiAgdmFyIG1zdyA9ICh4ID4+IDE2KSArICh5ID4+IDE2KSArIChsc3cgPj4gMTYpOwogIHJldHVybiAobXN3IDw8IDE2KSB8IChsc3cgJiAweEZGRkYpOwp9CgovKgogKiBCaXR3aXNlIHJvdGF0ZSBhIDMyLWJpdCBudW1iZXIgdG8gdGhlIGxlZnQuCiAqLwpmdW5jdGlvbiBiaXRfcm9sKG51bSwgY250KQp7CiAgcmV0dXJuIChudW0gPDwgY250KSB8IChudW0gPj4+ICgzMiAtIGNudCkpOwp9CgovKgogKiBDb252ZXJ0IGEgc3RyaW5nIHRvIGFuIGFycmF5IG9mIGxpdHRsZS1lbmRpYW4gd29yZHMKICogSWYgY2hyc3ogaXMgQVNDSUksIGNoYXJhY3RlcnMgPjI1NSBoYXZlIHRoZWlyIGhpLWJ5dGUgc2lsZW50bHkgaWdub3JlZC4KICovCmZ1bmN0aW9uIHN0cjJiaW5sKHN0cikKewogIHZhciBiaW4gPSBBcnJheSgpOwogIHZhciBtYXNrID0gKDEgPDwgY2hyc3opIC0gMTsKICBmb3IodmFyIGkgPSAwOyBpIDwgc3RyLmxlbmd0aCAqIGNocnN6OyBpICs9IGNocnN6KQogICAgYmluW2k+PjVdIHw9IChzdHIuY2hhckNvZGVBdChpIC8gY2hyc3opICYgbWFzaykgPDwgKGklMzIpOwogIHJldHVybiBiaW47Cn0KCi8qCiAqIENvbnZlcnQgYW4gYXJyYXkgb2YgbGl0dGxlLWVuZGlhbiB3b3JkcyB0byBhIHN0cmluZwogKi8KZnVuY3Rpb24gYmlubDJzdHIoYmluKQp7CiAgdmFyIHN0ciA9ICIiOwogIHZhciBtYXNrID0gKDEgPDwgY2hyc3opIC0gMTsKICBmb3IodmFyIGkgPSAwOyBpIDwgYmluLmxlbmd0aCAqIDMyOyBpICs9IGNocnN6KQogICAgc3RyICs9IFN0cmluZy5mcm9tQ2hhckNvZGUoKGJpbltpPj41XSA+Pj4gKGkgJSAzMikpICYgbWFzayk7CiAgcmV0dXJuIHN0cjsKfQoKLyoKICogQ29udmVydCBhbiBhcnJheSBvZiBsaXR0bGUtZW5kaWFuIHdvcmRzIHRvIGEgaGV4IHN0cmluZy4KICovCmZ1bmN0aW9uIGJpbmwyaGV4KGJpbmFycmF5KQp7CiAgdmFyIGhleF90YWIgPSBoZXhjYXNlID8gIjAxMjM0NTY3ODlBQkNERUYiIDogIjAxMjM0NTY3ODlhYmNkZWYiOwogIHZhciBzdHIgPSAiIjsKICBmb3IodmFyIGkgPSAwOyBpIDwgYmluYXJyYXkubGVuZ3RoICogNDsgaSsrKQogIHsKICAgIHN0ciArPSBoZXhfdGFiLmNoYXJBdCgoYmluYXJyYXlbaT4+Ml0gPj4gKChpJTQpKjgrNCkpICYgMHhGKSArCiAgICAgICAgICAgaGV4X3RhYi5jaGFyQXQoKGJpbmFycmF5W2k+PjJdID4+ICgoaSU0KSo4ICApKSAmIDB4Rik7CiAgfQogIHJldHVybiBzdHI7Cn0KCi8qCiAqIENvbnZlcnQgYW4gYXJyYXkgb2YgbGl0dGxlLWVuZGlhbiB3b3JkcyB0byBhIGJhc2UtNjQgc3RyaW5nCiAqLwpmdW5jdGlvbiBiaW5sMmI2NChiaW5hcnJheSkKewogIHZhciB0YWIgPSAiQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0NTY3ODkrLyI7CiAgdmFyIHN0ciA9ICIiOwogIGZvcih2YXIgaSA9IDA7IGkgPCBiaW5hcnJheS5sZW5ndGggKiA0OyBpICs9IDMpCiAgewogICAgdmFyIHRyaXBsZXQgPSAoKChiaW5hcnJheVtpICAgPj4gMl0gPj4gOCAqICggaSAgICU0KSkgJiAweEZGKSA8PCAxNikKICAgICAgICAgICAgICAgIHwgKCgoYmluYXJyYXlbaSsxID4+IDJdID4+IDggKiAoKGkrMSklNCkpICYgMHhGRikgPDwgOCApCiAgICAgICAgICAgICAgICB8ICAoKGJpbmFycmF5W2krMiA+PiAyXSA+PiA4ICogKChpKzIpJTQpKSAmIDB4RkYpOwogICAgZm9yKHZhciBqID0gMDsgaiA8IDQ7IGorKykKICAgIHsKICAgICAgaWYoaSAqIDggKyBqICogNiA+IGJpbmFycmF5Lmxlbmd0aCAqIDMyKSBzdHIgKz0gYjY0cGFkOwogICAgICBlbHNlIHN0ciArPSB0YWIuY2hhckF0KCh0cmlwbGV0ID4+IDYqKDMtaikpICYgMHgzRik7CiAgICB9CiAgfQogIHJldHVybiBzdHI7Cn0K";

    @Override
    public HttpResult<Map<String, Object>> init(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            //获取cookie:JSESSIONID
            String loginUrl = "http://wap.gs.10086.cn/jsbo_oauth/login?redirectURL=http://wap.gs.10086.cn/index.html";
            String pageContent = TaskHttpClient.create(param, RequestType.GET, "gan_su_10086_wap_001").setFullUrl(loginUrl).invoke().getPageContent();
            //获取时间戳timestamp,这个很重要,没有的话后面刷新短信验证码不行
            String timestamp = RegexpUtils.select(pageContent, "jstimestamp = (\\d+)", 1);
            TaskUtils.addTaskShare(param.getTaskId(), "timestamp", timestamp);
            return result.success();
        } catch (Exception e) {
            logger.error("登录-->初始化失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.TASK_INIT_ERROR);
        }
    }

    @Override
    public HttpResult<String> refeshPicCode(OperatorParam param) {
        return new HttpResult<String>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    @Override
    public HttpResult<Map<String, Object>> refeshSmsCode(OperatorParam param) {
        switch (param.getFormType()) {
            case FormType.LOGIN:
                return refeshSmsCodeForLogin(param);
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
        return new HttpResult<Object>().failure(ErrorCode.NOT_SUPORT_METHOD);
    }

    private HttpResult<Map<String, Object>> refeshSmsCodeForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            //http://wap.gs.10086.cn/jsbo_oauth/getNumMsg?mobile={}可以省掉

            Invocable invocable = ScriptEngineUtil.createInvocableFromBase64(javaScript);
            String encryptPassword = invocable.invokeFunction("hex_md5", param.getPassword()).toString();

            //获取时间戳timestamp,这个很重要,不能用System.currentTimeMillis(),否则-1014
            String timestamp = TaskUtils.getTaskShare(param.getTaskId(), "timestamp");
            String loginUrl = "http://wap.gs.10086.cn/jsbo_oauth/popDoorPopLogonNew";
            Map<String, Object> params = new HashMap<>();
            params.put("mobile", param.getMobile());
            params.put("password", encryptPassword);
            params.put("loginType", 1);
            params.put("icode", null);
            params.put("fromFlag", "doorPage");
            params.put("isHasV", false);
            params.put("redirectUrl", "http://wap.gs.10086.cn/index.html");
            params.put("dxYzm", null);
            params.put("timestamp", timestamp);
            params.put("clickType", 1);
            response = TaskHttpClient.create(param, RequestType.POST, "gan_su_10086_wap_003").setUrl(loginUrl).setParams(params)
                    .addHeader(HttpHeadKey.X_REQUESTED_WITH, "XMLHttpRequest").invoke();
            JSONObject json = response.getPageContentForJSON();
            Integer rcode = json.getInteger("rcode");
            switch (rcode) {
                case 200:
                    logger.info("登录-->短信验证码-->刷新成功,param={}", param);
                    return result.success();
                case 1020:
                    logger.warn("登录-->短信验证码-->刷新失败,手机号码或密码错误,param={},pageContent={}", param, response.getPageContent());
                    return result.failure(ErrorCode.VALIDATE_PASSWORD_FAIL);
                default:
                    logger.error("登录-->短信验证码-->刷新失败,手机号码或密码错误,param={},pageContent={}", param, response);
                    return result.failure(ErrorCode.REFESH_SMS_UNEXPECTED_RESULT);
            }
        } catch (Exception e) {
            logger.error("登录-->短信验证码-->刷新失败,param={},response={}", param, response, e);
            return result.failure(ErrorCode.REFESH_SMS_ERROR);
        }
    }

    private HttpResult<Map<String, Object>> submitForLogin(OperatorParam param) {
        CheckUtils.checkNotBlank(param.getPassword(), ErrorCode.EMPTY_PASSWORD);
        CheckUtils.checkNotBlank(param.getSmsCode(), ErrorCode.EMPTY_SMS_CODE);
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            Invocable invocable = ScriptEngineUtil.createInvocableFromBase64(javaScript);
            String encryptPassword = invocable.invokeFunction("hex_md5", param.getPassword()).toString();

            //获取时间戳timestamp,这个很重要,不能用System.currentTimeMillis(),否则-1014
            String timestamp = TaskUtils.getTaskShare(param.getTaskId(), "timestamp");
            String loginUrl = "http://wap.gs.10086.cn/jsbo_oauth/popDoorPopLogonNew";
            String referer = "http://wap.gs.10086.cn/jsbo_oauth/login?redirectURL=http://wap.gs.10086.cn/index.html";
            Map<String, Object> params = new HashMap<>();
            params.put("mobile", param.getMobile());
            params.put("password", encryptPassword);
            params.put("loginType", 1);
            params.put("icode", null);
            params.put("fromFlag", "doorPage");
            params.put("isHasV", false);
            params.put("redirectUrl", "http://wap.gs.10086.cn/index.html");
            params.put("dxYzm", param.getSmsCode());
            params.put("timestamp", timestamp);
            params.put("clickType", 2);

            response = TaskHttpClient.create(param, RequestType.POST, "").setUrl(loginUrl).setParams(params).setReferer(referer)
                    .addHeader(HttpHeadKey.X_REQUESTED_WITH, "XMLHttpRequest").invoke();
            JSONObject json = response.getPageContentForJSON();
            Integer rcode = json.getInteger("rcode");
            if (rcode != 1000) {
                logger.error("登陆失败,param={},pageContent={}", param, response);
                return result.failure(ErrorCode.LOGIN_UNEXPECTED_RESULT);
            }

            response = TaskHttpClient.create(param, RequestType.GET, "gan_su_10086_wap_006").addHeader(HttpHeadKey.X_REQUESTED_WITH, "XMLHttpRequest")
                    .setFullUrl("http://wap.gs.10086.cn/actionDispatcher.do?reqUrl=MessageInfo").setReferer("http://wap.gs.10086.cn/index.html")
                    .invoke();
            json = response.getPageContentForJSON();
            String resultMsg = json.getString("resultMsg");

            if (!StringUtils.contains(resultMsg, "您尚未登录或已超时，请重新登录")) {
                logger.info("登陆成功,param={}", param);
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

    private HttpResult<Map<String, Object>> refeshSmsCodeForBillDetail(OperatorParam param) {
        HttpResult<Map<String, Object>> result = new HttpResult<>();
        Response response = null;
        try {
            String templateUrl = "http://wap.gs.10086.cn/actionDispatcher.do?reqUrl=sendSmsCode&busiNum=XDCX";
            response = TaskHttpClient.create(param, RequestType.POST, "gan_su_10086_wap_008").setFullUrl(templateUrl).invoke();
            String pageContent = response.getPageContent();

            if (StringUtils.contains(pageContent, "短信下发成功")) {
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
            String templateUrl
                    = "http://wap.gs.10086.cn/actionDispatcher.do?reqUrl=XDCX_YY_Query&busiNum=XDCX&operType=3&confirm_smsPassword={}&confirmFlg=1";
            response = TaskHttpClient.create(param, RequestType.POST, "gan_su_10086_wap_009").setFullUrl(templateUrl, param.getSmsCode()).invoke();
            String pageContent = response.getPageContent();
            if (StringUtils.contains(pageContent, "系统流程处理正常")) {
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

    private String getErrorMsg(String rcode) {
        String errorMsg = StringUtils.EMPTY;
        switch (rcode) {
            case "-4":
                errorMsg = "手机号码格式不正确";
                break;
            case "-5":
                errorMsg = "密码位数不正确";
                break;
            case "-6":
                errorMsg = "验证码位数不正确";
                break;
            case "-7":
                errorMsg = "验证码失效,请重新登录";
                break;
            case "-8":
                errorMsg = "验证码错误";
                break;
            case "-9":
                errorMsg = "用户信息为空";
                break;
            case "-12":
                errorMsg = "用户IP在登录黑名单中，不允许登录！";
                break;
            case "-13":
                errorMsg = "用户手机号码在登录黑名单中，不允许登录！";
                break;
            case "-2203":
                errorMsg = "您输入的号码非甘肃省归属，请切换至手机号码归属省登录！";
                break;
            case "-2230":
                errorMsg = "对不起,密码长度错误，请输入6位有效密码！";
                break;
            case "-2231":
                errorMsg = "对不起,密码包含非法字符，请重新输入！";
                break;
            case "-1020":
                errorMsg = "对不起,登录密码错误，请重新输入！";
                break;
            case "-1030":
                errorMsg = "您的密码错误次数已达到上限，为保障您的信息安全，服务密码登陆方式已锁定，请明天再试！";
                break;
            case "-1040":
                errorMsg = "对不起，您的IP已受限，请改天再试！";
                break;
            case "-2303":
                errorMsg = "对不起,您的号码已锁！";
                break;
            case "-1010":
                errorMsg = "对不起,登录手机号码或者密码错误，请重新输入！";
                break;
            case "-1016":
                errorMsg = "对不起，验证码已失效，请重新获取验证码";
                break;
        }
        return errorMsg;
    }
}
