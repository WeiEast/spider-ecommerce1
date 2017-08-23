package com.datatrees.crawler.core.processor.operation.impl;

import java.io.*;
import java.util.regex.Matcher;

import com.datatrees.common.util.PatternUtils;
import org.junit.Test;

public class RegexOperationTest {

    private String readFile(String path) throws FileNotFoundException {
        System.out.println(new File(path).getAbsolutePath());
        String content = "";
        InputStream input = new FileInputStream(new File(path));
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String data;
            while ((data = reader.readLine()) != null) content = content + data;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }

    @Test
    public void regexTest() throws FileNotFoundException {
        String orginal = readFile("src/test/resources/regex/input");
        String regex = readFile("src/test/resources/regex/regex");
        System.out.println(orginal);
        System.out.println(regex);
        try {
            Matcher matcher = PatternUtils.matcher(regex, orginal);
            if (matcher.find()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    System.out.println("group" + i + " " + matcher.group(i));
                }
            } else {
                System.out.println("not match");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
