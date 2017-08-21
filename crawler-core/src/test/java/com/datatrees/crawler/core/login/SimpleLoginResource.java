package com.datatrees.crawler.core.login;

import com.datatrees.crawler.core.domain.Cookie;
import com.datatrees.crawler.core.domain.WebsiteAccount;
import com.datatrees.crawler.core.processor.common.resource.LoginResource;

public class SimpleLoginResource implements LoginResource {

	@Override
	public WebsiteAccount getAccount(String accountKey) {
		WebsiteAccount websiteAccount = new WebsiteAccount();
		websiteAccount.setUserName("mail51bill@qq.com");
		websiteAccount.setPassword("51test.com.cn");
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
