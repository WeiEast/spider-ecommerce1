package com.datatrees.rawdatacentral.common.utils;

import com.datatrees.rawdatacentral.domain.vo.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/7/19.
 */
public class CookieUtils {

    public static List<Cookie> getCookies(BasicCookieStore cookieStore) {
        CheckUtils.checkNotNull(cookieStore, "cookieStore is null");
        List<Cookie> list = new ArrayList<>();
        for (org.apache.http.cookie.Cookie cookie : cookieStore.getCookies()) {
            BasicClientCookie basicClientCookie = (BasicClientCookie) cookie;
            Cookie myCookie = new Cookie();
            myCookie.setDomain(basicClientCookie.getDomain());
            myCookie.setPath(basicClientCookie.getPath());
            myCookie.setName(basicClientCookie.getName());
            myCookie.setValue(basicClientCookie.getValue());
            myCookie.setSecure(basicClientCookie.isSecure());
            myCookie.setVersion(basicClientCookie.getVersion());
            myCookie.setExpiryDate(basicClientCookie.getExpiryDate());
            if (basicClientCookie.containsAttribute("domain")) {
                myCookie.getAttribs().put("domain", basicClientCookie.getAttribute("domain"));
            }
            if (basicClientCookie.containsAttribute("path")) {
                myCookie.getAttribs().put("path", basicClientCookie.getAttribute("path"));
            }
            if (basicClientCookie.containsAttribute("expires")) {
                myCookie.getAttribs().put("expires", basicClientCookie.getAttribute("expires"));
            }
            list.add(myCookie);
        }
        return list;
    }

    public static BasicClientCookie getBasicClientCookie(Cookie myCookie) {
        BasicClientCookie cookie = new BasicClientCookie(myCookie.getName(), myCookie.getValue());
        cookie.setDomain(myCookie.getDomain());
        cookie.setPath(myCookie.getPath());
        cookie.setSecure(myCookie.isSecure());
        cookie.setVersion(myCookie.getVersion());
        cookie.setExpiryDate(myCookie.getExpiryDate());
        for (Map.Entry<String, String> entry : myCookie.getAttribs().entrySet()) {
            cookie.setAttribute(entry.getKey(), entry.getValue());
        }
        return cookie;
    }
}
