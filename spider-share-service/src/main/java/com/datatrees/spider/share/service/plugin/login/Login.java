package com.datatrees.spider.share.service.plugin.login;

import java.util.Map;

public interface Login {

    public Map<String, Object> preLogin(Map<String, String> paramMap) throws Exception;

    public Map<String, Object> doLogin(Map<String, Object> loginParams) throws Exception;

    public Map<String, Object> postLogin(Map<String, Object> postLoginParams) throws Exception;

}
