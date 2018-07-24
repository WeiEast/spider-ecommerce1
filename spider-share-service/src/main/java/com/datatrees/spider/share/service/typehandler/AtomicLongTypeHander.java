package com.datatrees.spider.share.service.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by zhouxinghai on 2017/7/4.
 */
public class AtomicLongTypeHander implements TypeHandler {

    private static final Logger logger = LoggerFactory.getLogger(AtomicLongTypeHander.class);

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        Long v = null;
        if (null != parameter) {
            v = ((Number) parameter).longValue();
        }
        ps.setLong(i, v);

    }

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        String v = rs.getString(columnName);
        if (!StringUtils.isEmpty(v)) {
            return new AtomicLong(Long.valueOf(v));
        }
        return null;
    }

    @Override
    public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
        String v = rs.getString(columnIndex);
        if (!StringUtils.isEmpty(v)) {
            return new AtomicLong(Long.valueOf(v));
        }
        return null;
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }
}
