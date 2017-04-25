package com.datatrees.rawdatacentral.core.service;

import java.util.List;

public interface RedisService {
    public boolean saveString(final String key, final String value);

    public boolean saveListString(final String key, final List<String> value);
}
