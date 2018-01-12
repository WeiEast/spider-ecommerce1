package com.datatrees.rawdatacentral.api;

public interface ConfigServiceApi {

    String getProperty(String name);

    String getProperty(String prefix, String name);

    String getPropertyOrDefaultValue(String name, String defaultValue);

    String getPropertyOrDefaultValue(String prefix, String name, String defaultValue);

}
