package com.datatrees.rawdatacentral.common.utils;

import java.io.File;
import java.io.IOException;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class JsonPathTest {

    private String json = FileUtils.readFileToString(new File("/data/json.txt"));

    public JsonPathTest() throws IOException {}

    @Test
    public void test() {

        ReadContext ctx = JsonPath.parse(json);

        Object read = ctx.read("$.callDetails");
        System.out.println(read);
        read = ctx.read("$");
        System.out.println(read);


    }

}
