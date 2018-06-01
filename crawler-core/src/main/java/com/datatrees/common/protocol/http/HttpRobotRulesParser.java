/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol.http;

import java.net.URL;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.Protocol;
import com.datatrees.common.protocol.Response;
import com.datatrees.common.protocol.RobotRulesParser;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used for parsing robots for urls belonging to HTTP protocol. It extends the generic
 * {@link RobotRulesParser} class and contains Http protocol specific implementation for obtaining
 * the robots file.
 */
public class HttpRobotRulesParser extends RobotRulesParser {

    public static final Logger  LOG            = LoggerFactory.getLogger(HttpRobotRulesParser.class);
    protected           boolean allowForbidden = false;
    
    HttpRobotRulesParser() {}

    public HttpRobotRulesParser(Configuration conf) {
        super(conf);
        allowForbidden = conf.getBoolean("http.robots.403.allow", false);
    }

    /**
     * The hosts for which the caching of robots rules is yet to be done, it sends a Http request to
     * the host corresponding to the {@link URL} passed, gets robots file, parses the rules and
     * caches the rules object to avoid re-work in future.
     * 
     * @param http The {@link Protocol} object
     * @param url URL
     * 
     * @return robotRules A {@link BaseRobotRules} object for the rules
     */
    public BaseRobotRules getRobotRulesSet(Protocol http, URL url) {

        String protocol = url.getProtocol().toLowerCase(); // normalize to lower case
        String host = url.getHost().toLowerCase(); // normalize to lower case

        BaseRobotRules robotRules = (SimpleRobotRules) CACHE.get(protocol + ":" + host);

        boolean cacheRule = true;

        if (robotRules == null) { // cache miss
            URL redir = null;

            LOG.trace("cache miss " + url);

            try {
                Response response =
                        ((HttpBase) http).getResponse(new URL(url, "/robots.txt"), 0,
                                true);
                // try one level of redirection ?
                if (response.getCode() == 301 || response.getCode() == 302) {
                    String redirection = response.getHeader("Location");
                    if (redirection == null) {
                        // some versions of MS IIS are known to mangle this header
                        redirection = response.getHeader("location");
                    }
                    if (redirection != null) {
                        if (!redirection.startsWith("http")) {
                            // RFC says it should be absolute, but apparently it isn't
                            redir = new URL(url, redirection);
                        } else {
                            redir = new URL(redirection);
                        }

                        response = ((HttpBase) http).getResponse(redir, 0, true);
                    }
                }

                if (response.getCode() == 200) // found rules: parse them
                    robotRules =
                            parseRules(url.toString(), response.getContent(),
                                    response.getHeader("Content-Type"), agentNames);

                else if ((response.getCode() == 403) && (!allowForbidden))
                    robotRules = FORBID_ALL_RULES; // use forbid all
                else if (response.getCode() >= 500) {
                    cacheRule = false;
                    robotRules = EMPTY_RULES;
                } else
                    robotRules = EMPTY_RULES; // use default rules
            } catch (Throwable t) {

                LOG.info("Couldn't get robots.txt for " + url + ": " + t.toString());

                cacheRule = false;
                robotRules = EMPTY_RULES;
            }

            if (cacheRule) {
                CACHE.put(protocol + ":" + host, robotRules); // cache rules for host
                if (redir != null && !redir.getHost().equals(host)) {
                    // cache also for the redirected host
                    CACHE.put(protocol + ":" + redir.getHost(), robotRules);
                }
            }
        }
        return robotRules;
    }
}
