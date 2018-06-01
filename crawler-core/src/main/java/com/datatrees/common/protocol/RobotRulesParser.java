/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol;

// JDK imports

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.datatrees.common.conf.Configurable;
import com.datatrees.common.conf.Configuration;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRules.RobotRulesMode;
import crawlercommons.robots.SimpleRobotRulesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class uses crawler-commons for handling the parsing of {@code robots.txt} files. It emits
 * SimpleRobotRules objects, which describe the download permissions as described in
 * SimpleRobotRulesParser.
 */
public abstract class RobotRulesParser implements Configurable {

    public static final    Logger                            LOG              = LoggerFactory.getLogger(RobotRulesParser.class);

    protected static final Hashtable<String, BaseRobotRules> CACHE            =
            new Hashtable<String, BaseRobotRules>();

    /**
     * A {@link BaseRobotRules} object appropriate for use when the {@code robots.txt} file is empty
     * or missing; all requests are allowed.
     */
    public static final    BaseRobotRules                    EMPTY_RULES      = new SimpleRobotRules(RobotRulesMode.ALLOW_ALL);

    /**
     * A {@link BaseRobotRules} object appropriate for use when the {@code robots.txt} file is not
     * fetched due to a {@code 403/Forbidden} response; all requests are disallowed.
     */
    public static          BaseRobotRules                    FORBID_ALL_RULES = new SimpleRobotRules(RobotRulesMode.ALLOW_NONE);

    private static         SimpleRobotRulesParser            robotParser      = new SimpleRobotRulesParser();
    private   Configuration conf;
    protected String        agentNames;

    public RobotRulesParser() {}

    public RobotRulesParser(Configuration conf) {
        setConf(conf);
    }

    /**
     * Set the {@link Configuration} object
     */
    public void setConf(Configuration conf) {
        this.conf = conf;
        
        String agentName = "Mozilla/5.0 (Ubuntu; X11; Linux i686; rv:8.0) Gecko/20100101 Firefox/8.0";
        String agentNames = conf.get("http.robots.agents", "Mozilla/5.0 (Ubuntu; X11; Linux i686; rv:8.0) Gecko/20100101 Firefox/8.0");
        StringTokenizer tok = new StringTokenizer(agentNames, ",");
        ArrayList<String> agents = new ArrayList<String>();
        while (tok.hasMoreTokens()) {
            agents.add(tok.nextToken().trim());
        }

        /**
         * If there are no agents for robots-parsing, use the default agent-string. If both are
         * present, our agent-string should be the first one we advertise to robots-parsing.
         */
        if (agents.size() == 0) {
            LOG.error("No agents listed in 'http.robots.agents' property!");
        } else {
            StringBuffer combinedAgentsString = new StringBuffer(agentName);
            int index = 0;

            if ((agents.get(0)).equalsIgnoreCase(agentName))
                index++;
            else {
                LOG.error("Agent we advertise (" + agentName
                        + ") not listed first in 'http.robots.agents' property!");
            }

            // append all the agents from the http.robots.agents property
            for (; index < agents.size(); index++) {
                combinedAgentsString.append(", " + agents.get(index));
            }

            // always make sure "*" is included in the end
            combinedAgentsString.append(", *");
            this.agentNames = combinedAgentsString.toString();
        }
    }

    /**
     * Get the {@link Configuration} object
     */
    public Configuration getConf() {
        return conf;
    }

    /**
     * Parses the robots content using the {@link SimpleRobotRulesParser} from crawler commons
     * 
     * @param url A string containing url
     * @param content Contents of the robots file in a byte array
     * @param contentType The
     * @param robotName A string containing value of
     * @return BaseRobotRules object
     */
    public BaseRobotRules parseRules(String url, byte[] content, String contentType,
            String robotName) {
        return robotParser.parseContent(url, content, contentType, robotName);
    }

    public BaseRobotRules getRobotRulesSet(Protocol protocol, String url) {
        URL u = null;
        try {
            u = new URL(url);
        } catch (Exception e) {
            return EMPTY_RULES;
        }
        return getRobotRulesSet(protocol, u);
    }

    public abstract BaseRobotRules getRobotRulesSet(Protocol protocol, URL url);

}
