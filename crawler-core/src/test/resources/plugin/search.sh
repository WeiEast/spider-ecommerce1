#!/bin/bash

tmpFile="$0.cookie"
wget -t 3 -T 60 --user-agent "Mozilla/5.0 (X11; Linux i686; rv:5.0) Gecko/20100101 Firefox/5.0" \
     --save-cookies "$tmpFile" \
     --post-data "vb_login_username=xbkaishui&vb_login_password=&vb_login_password_hint=Password&cookieuser=1&s=&securitytoken=1373253523-812bb4e896d07ef835ae282eb79fd48f486c2d0a&do=login&vb_login_md5password=e10adc3949ba59abbe56e057f20f883e&vb_login_md5password_utf=e10adc3949ba59abbe56e057f20f883e" \
    "http://www.majaa.net/login.php?do=login" \
     -O /dev/null -d

cat $tmpFile| grep 'majaa.net'|awk '{printf("%s=%s;",$6,$7); }'
echo

rm -f $tmpFile;




	"{\"cookie\":\"\"}"
printf "{\"template\":\"http://kenitra.biz/page/\${page,1,50,1+}/?s=\${keyword}&searchsubmit=\"}";
