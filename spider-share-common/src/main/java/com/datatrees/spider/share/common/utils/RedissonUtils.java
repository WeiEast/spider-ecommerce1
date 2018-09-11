/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.share.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedissonUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedissonUtils.class);

    /**
     * 使用ip地址和端口创建Redisson
     * @param ip
     * @param port
     * @return
     */
    public static RedissonClient getRedisson(String ip, String port) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + ip + ":" + port);
        RedissonClient redisson = Redisson.create(config);
        logger.info("成功连接Redis Server,{}:{}", ip, port);
        return redisson;
    }

    /**
     * 使用ip地址和端口创建Redisson
     * @param ip
     * @param port
     * @return
     */
    public static RedissonClient getRedisson(String ip, String port, String password) {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress("redis://" + ip + ":" + port);
        if (StringUtils.isNoneBlank(password)) {
            serverConfig.setPassword(password);
        }
        RedissonClient redisson = Redisson.create(config);
        logger.info("成功连接Redis Server,{}:{}", ip, port);
        return redisson;
    }

    /**
     * 关闭Redisson客户端连接
     * @param redisson
     */
    public static void closeRedisson(RedissonClient redisson) {
        redisson.shutdown();
        System.out.println("成功关闭Redis Client连接");
    }

}
