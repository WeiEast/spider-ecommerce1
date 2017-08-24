package com.datatrees.rawdatacentral.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtils {


    public static String findFirst(String source,String regex){
        CheckUtils.checkNotBlank(source,"source is blank");
        CheckUtils.checkNotBlank(regex,"regex is blank");
        Matcher matcher = Pattern.compile(regex).matcher(source);
        if(matcher.find()){
            return matcher.group(0);
        }
        return null;
    }

    public static void main(String[] args) {
        String source = "<script language=\"javascript\">window.history.forward();</script><form name=\"authnresponseform\" method=\"post\" action=\"http://www.zj.10086.cn/my/sso\"><input type=\"hidden\" name=\"SAMLart\" value=\"147ac7bc10674e838d5f7bfa749753c5\"/><input type=\"hidden\" name=\"RelayState\" value=\"http://www.zj.10086.cn/my/index.jsp?ul_loginclient=my\"/><input type=\"submit\" name=\"submit\" style=\"display:none\"></form><script>document.authnresponseform.submit.click()</script>";
        String regex = "<form.+form>";
        String form = findFirst(source,regex);
        System.out.println(form);


    }

}
