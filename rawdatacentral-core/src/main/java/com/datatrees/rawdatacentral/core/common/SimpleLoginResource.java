package com.datatrees.rawdatacentral.core.common;

import com.datatrees.crawler.core.domain.Cookie;
import com.datatrees.crawler.core.domain.WebsiteAccount;
import com.datatrees.crawler.core.processor.common.resource.LoginResource;

public class SimpleLoginResource implements LoginResource {

    @Override
    public WebsiteAccount getAccount(String accountKey) {
        //use empty for websiteAccount
        WebsiteAccount websiteAccount = new WebsiteAccount();
        return websiteAccount;
    }

    @Override
    public Cookie getCookie(String accountKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putCookie(String accountKey, String cookie) {
        // TODO Auto-generated method stub

    }

}
