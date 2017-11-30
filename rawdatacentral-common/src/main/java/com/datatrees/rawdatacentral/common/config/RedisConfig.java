package com.datatrees.rawdatacentral.common.config;

public class RedisConfig {

    //Redis服务器IP
    protected static String host;
    //Redis的端口号
    protected static int port     = 6379;
    protected static int database = 0;
    //访问密码
    protected static String password;
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    protected static int  maxTotal = 1024;
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    protected static int  maxIdle  = 200;
    protected static int  minIdle  = 8;
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    protected static long maxWait  = 10000;
    protected static int  timeout  = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    public void setHost(String host) {
        RedisConfig.host = host;
    }

    public void setPort(int port) {
        RedisConfig.port = port;
    }

    public void setPassword(String password) {
        RedisConfig.password = password;
    }
}
