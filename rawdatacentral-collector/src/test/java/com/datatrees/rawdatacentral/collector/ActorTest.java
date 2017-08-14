package com.datatrees.rawdatacentral.collector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.crawler.core.processor.common.CalculateUtil;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.api.MessageService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 下午4:44:13
 */
public class ActorTest extends BaseTest {

    @Resource
    private Collector           collector;
    @Resource
    private CrawlerService      cs;

    @Resource
    private MessageService      messageService;
    private static final Logger logger = LoggerFactory.getLogger(ActorTest.class);

    @Test
    public void testName() throws Exception {
        System.out.println(CalculateUtil.calculate("936.25 -(-57.61 )+468.85999999999996", 1));
    }

    @Test
    public void testCrawlerService() throws Exception {
        System.out.println(PropertiesConfiguration.getInstance());
        //        cs.verifyQr(333, "");
    }

    @Test
    public void testQQMail() throws Exception {
        while (true) {
            Thread.sleep(1000);
        }
    }

    @Test
    public void testQQ1() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setAccountNo("33");
        message.setTaskId(5);
        message.setCookie(
            "ptisp=cnc;new_mail_num=1148614215&0;username=1148614215&1148614215;pcache=412fe88a31ef1c4MTUwMDQ0NTI5NA@1148614215@4;qm_flag=0;qm_lg=qm_lg;qm_sk=1148614215&UcTT5vNw;p_lskey=00040000a3b1cbe419e199ca993ed6b1c1a556225198e91b46850d508cd7b3f01e963aa85a542bb0d1f45705;pt2gguin=o1148614215;qm_ssum=1148614215&72482bd110f9f6389ea431c2e0a3dbb4;mpwd=412fe88a31ef1c4MTUwMDQ0NTI5NA@1148614215@4;skey=@QP6LtRzf6;p_uin=o1148614215;edition=mail.qq.com;qm_username=1148614215;sid=1148614215&e4d908960ac1b3f5a0f729278cbae429,qbTJSS0hBeXVTSy1LNzhuSDJDUlBBbSpJTmVmMmR6Um5JcFZWZkEqUkVxWV8.;ssl_edition=sail.qq.com;pgv_pvi=9979547648;luin=o1148614215;p_luin=o1148614215;RK=gk9T1imaTR;pt4_token=5kWPGvh7bOm*hZ*gQIQyyplXL3FgEW86Ufblw79qrw8_;pgv_si=s2961527808;qqmail_alias=1148614215@qq.com;uin=o1148614215;p_skey=m2RKHAyuSK-K78nH2CRPAm*INef2dzRnIpVVfA*REqY_;lskey=0001000061842c55944d600c64469c06851fb488c5ae98b065fd1695189984928d9a333dc7860b0b2b87e034;device=;msid=wIfp4pT5aR9YA0ceXcLT5vNw,4,qbTJSS0hBeXVTSy1LNzhuSDJDUlBBbSpJTmVmMmR6Um5JcFZWZkEqUkVxWV8.;");
        message.setWebsiteName("qq.com");
        message.setEndURL(
            "https://w.mail.qq.com/cgi-bin/today?sid=wIfp4pT5aR9YA0ceXcLT5vNw,4,qbTJSS0hBeXVTSy1LNzhuSDJDUlBBbSpJTmVmMmR6Um5JcFZWZkEqUkVxWV8.&first=1&mcookie=disabled");
        collector.processMessage(message);

    }

    @Test
    public void test163() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "MAIL_SESS=U3uBQg9koV20ORM9Zkv1tAsHwcJe6s2RrbMHKsp3Bw9N15ST1ltocJUuxOiIwQR3DL.mxWxI8JKD5a.NYTv2YEf03hYdioCZIF9IhySjTtwlZHXK20XFbO_yb9gUzltkPm_LKLdmoqsJBAAYaTlx25Jd7IPtNJ16qIxFn4BNevOpr; MAIL_MISC=tangbaiyuan223#biujqugss; S_INFO=1479107027|0|2&90##|tangbaiyuan223#biujqugss; NTES_PASSPORT=.bioTecdtV0vEXH4f3dWjumfk7PSRrNU3nW65tkkLwB3mVb8mPEkn1.RuMpGdJSKOF0SP6vO3CZPKw8yQ8YoECAjmQwL8N6X1ilWVQqHWeSpt; mail_idc=\"\"; JSESSIONID=4530AC22E4B1EC49014D0C8143BCDE1D; starttime=; locale=; cm_last_info=dT10YW5nYmFpeXVhbjIyMyU0MDE2My5jb20mZD1odHRwJTNBJTJGJTJGbWFpbC4xNjMuY29tJTJGbSUyRm1haW4uanNwJTNGc2lkJTNEekFFYm9YeHh2RU1XZlJrc0VreHhUbWlWbER6cGpKbVQmcz16QUVib1h4eHZFTVdmUmtzRWt4eFRtaVZsRHpwakptVCZoPWh0dHAlM0ElMkYlMkZtYWlsLjE2My5jb20lMkZtJTJGbWFpbi5qc3AlM0ZzaWQlM0R6QUVib1h4eHZFTVdmUmtzRWt4eFRtaVZsRHpwakptVCZ3PW1haWwuMTYzLmNvbSZsPTAmdD0xMQ==; SID=6b351085-3fbb-4a83-bb1c-01c1bc681ca6; MAIL_SINFO=1479107027|0|2&90##|tangbaiyuan223#biujqugss; NTES_SESS=U3uBQg9koV20ORM9Zkv1tAsHwcJe6s2RrbMHKsp3Bw9N15ST1ltocJUuxOiIwQR3DL.mxWxI8JKD5a.NYTv2YEf03hYdioCZIF9IhySjTtwlZHXK20XFbO_yb9gUzltkPm_LKLdmoqsJBAAYaTlx25Jd7IPtNJ16qIxFn4BNevOpr; mail_entry_sess=9a02e7dcb6638dfc6c9232ba65d8b10923455789d3b1d9f2e21f6c56b00641f020122db0356302bf519432d9e6db719c16c76e5b26666ce27a184459fdf270c8ed0ea397f398e4cf0f2ab706599ab79384457fe991ca99dfe431e925809df03e62cbb219c831f874dbb0311c2e4943bde61706a8359aa4a7fd675b70c77dfe5296c8eaeed245f22a48f52040e8de692b725ca5d7c7fc073f5b2fb1f832cbd8ea69fe9abf66bcb27540a4aa4ea2dfbb38f06a39fd955c7cdaf9846507b7406b63; secu_info=1; MAIL_PINFO=tangbaiyuan223@163.com|1479107027|1|mail163|11&21|zhj&1479106655&mail163#zhj&330100#10#0|&0|mail163|tangbaiyuan223@163.com; Coremail=1479107027742%zAEboXxxvEMWfRksEkxxTmiVlDzpjJmT%g6a48.mail.163.com; mail_upx=t6hz.mail.163.com|t7hz.mail.163.com|t8hz.mail.163.com|t10hz.mail.163.com|t11hz.mail.163.com|t12hz.mail.163.com|t13hz.mail.163.com|t1hz.mail.163.com|t2hz.mail.163.com|t4hz.mail.163.com|t5hz.mail.163.com|c2bj.mail.163.com|c3bj.mail.163.com|c4bj.mail.163.com|c5bj.mail.163.com|c6bj.mail.163.com|c7bj.mail.163.com|c1bj.mail.163.com; P_INFO=tangbaiyuan223@163.com|1479107027|1|mail163|11&21|zhj&1479106655&mail163#zhj&330100#10#0|&0|mail163|tangbaiyuan223@163.com; mail_upx_nf=");
        message.setTaskId(1000000174);
        message.setWebsiteName("163.com");
        message.setEndURL("http://mail.163.com/m/web.jsp?sid=zAEboXxxvEMWfRksEkxxTmiVlDzpjJmT&df=smart_ios");
        collector.processMessage(message);
    }

    @Test
    public void test126() throws InterruptedException {
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void test1262() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "mail_idc=; Coremail=1446176398205%tAyocgKazXcoowtzMRaaThPZHHcUfbMe%g1a48.mail.126.com; secu_info=1; JSESSIONID=acefXKejO7Zz0bKEj12cv; MAIL_MISC=chentengteng2008@126.com; MAIL_PINFO=chentengteng2008@126.com|1446176397|1|mail126|11&17|shd&1446176338&mail126#shd&371100#10#0#0|&0|mail126|chentengteng2008@126.com; starttime=; mail_upx=t3bj.mail.126.com|t4bj.mail.126.com|t1bj.mail.126.com|t2bj.mail.126.com; NTES_SESS=A9cZaCRtZUXD0btp.YH1bH3VJm3VMIF3tVBCQvMc4b87qC5iqgFBt6PlT0rCsUUrLbC0WxlvCvBuYXWzA93wvKtQIK5YnOlUZ1R4iEiTwC7UN5rNUuQoB3.GfuZhVAsSss2dxeiX.9GoucuefboItM.g.aAsszf4KwJB8bn65obOywOIwjcjx6KzYPPKVGhhB; mail_upx_nf=; ANTICSRF=946a87b67e056f08c41ee6204e831d9f; SID=2886bc62-b117-4b55-ae15-2bdbd12dc512; mail_entry_sess=88bca22e15b439851c33c87d43ab1de9879bfec62d4fef976269c73aba8b748674a61d76919e205755290f0de2a241c9cac99a2ab562d26e1c8fcf2092fa70b7427552110888e44572840141d1b416a439d3bf8665a4f53ea3735ae98c0cdc2f673f72ef15736d0a720798c47b7d7e15c0f7fc1cb4bb4e6eaf21e645d8577bc7bf0c3813f2f91629231874d630e52d808c52a866230fe6fc50df2ea65c71c7bf7104f87ace6c561b53d80cefd093ba248783cd38c0626e3c3438b1921ea67bd5; P_INFO=chentengteng2008@126.com|1446176397|1|mail126|11&17|shd&1446176338&mail126#shd&371100#10#0#0|&0|mail126|chentengteng2008@126.com; S_INFO=1446176397|0|#3&100#|chentengteng2008@126.com; MAIL_SESS=A9cZaCRtZUXD0btp.YH1bH3VJm3VMIF3tVBCQvMc4b87qC5iqgFBt6PlT0rCsUUrLbC0WxlvCvBuYXWzA93wvKtQIK5YnOlUZ1R4iEiTwC7UN5rNUuQoB3.GfuZhVAsSss2dxeiX.9GoucuefboItM.g.aAsszf4KwJB8bn65obOywOIwjcjx6KzYPPKVGhhB; NTES_PASSPORT=9qZkHiSaZoV9XJZkHIHqPzvuO3dQSMtwBYryBuX86aCX0UVp0AITEqFP8ltU3..tz7UlcuPsUsTR5LcyvgKds2XV4RhEceDzvFMwv1vqvmRj_I52vJw5FHp1T; cm_last_info=dT1jaGVudGVuZ3RlbmcyMDA4JTQwMTI2LmNvbSZkPWh0dHAlM0ElMkYlMkZtYWlsLjEyNi5jb20lMkZtJTJGbWFpbi5qc3AlM0ZzaWQlM0R0QXlvY2dLYXpYY29vd3R6TVJhYVRoUFpISGNVZmJNZSZzPXRBeW9jZ0thelhjb293dHpNUmFhVGhQWkhIY1VmYk1lJmg9aHR0cCUzQSUyRiUyRm1haWwuMTI2LmNvbSUyRm0lMkZtYWluLmpzcCUzRnNpZCUzRHRBeW9jZ0thelhjb293dHpNUmFhVGhQWkhIY1VmYk1lJnc9bWFpbC4xMjYuY29tJmw9MCZ0PTEx; T_INFO=69793583E957DB6659080111B8F413AFE0BE584CD5B6A6563F891509F6EF28D0; MAIL_SINFO=1446176397|0|#3&100#|chentengteng2008@126.com");

        message.setTaskId(126);
        message.setWebsiteName("126.com");
        message.setNeedDuplicate(true);

        message.setEndURL("http://mail.126.com/m/web.jsp?sid=tATgmGjRbxndudDCLjRRnYlfoLvKGRHd&df=smart_ios");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void testChinaunicom() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "mallcity=36|360; gipgeo=36|360; WT.mc_id=zhejiang_zhejiang_guanjianci_0120_baidu_5067; _n3fa_cid=e610599e9095444c80af77251ddebb0f; _n3fa_ext=ft=1439191305; _n3fa_lvt_a9e72dfe4a54a20c3d6e671b3bad01d9=1439195056,1439199402,1439205401,1439273618; _n3fa_lpvt_a9e72dfe4a54a20c3d6e671b3bad01d9=1439273618; WT_FPC=id=2895b320db52291398b1439191307462:lv=1439368180339:ss=1439368145539; Hm_lvt_9208c8c641bfb0560ce7884c36938d9d=1439191308; Hm_lpvt_9208c8c641bfb0560ce7884c36938d9d=1439368181; __utma=231252639.970684594.1439191312.1439359030.1439368148.9; __utmc=231252639; __utmz=231252639.1439368148.9.5.utmcsr=iservice.10010.com|utmccn=(referral)|utmcmd=referral|utmcct=/e3/query/account_balance.html; __utmv=231252639.Zhejiang; unisecid=C567ECA1658C414D7262479694B4F216; piw=%7B%22login_name%22%3A%22186****9890%22%2C%22nickName%22%3A%22%E6%9D%8E%E5%BF%97%E5%9B%BD%22%2C%22rme%22%3A%7B%22ac%22%3A%22%22%2C%22at%22%3A%22%22%2C%22pt%22%3A%2201%22%2C%22u%22%3A%2218658139890%22%7D%2C%22verifyState%22%3A%22%22%7D; MENUURL=%2Fe3%2Fnavhtml3%2FWT3%2FWT_MENU_3_001%2F036%2F022.html%3F_%3D1439368179817; cien=; _uop_id=5424429b7f932b8b525e1a729230c32b; JUT=3MF7t6FWHv2GhJO25hlgA3VGAI7x7yMyTZPyhIzNKA7NUFlS/WwZmjPQCwIZBgcL2YE1sFBRidAlXhetrZqEBl9XT9HkpODM2BiPo/FusX7C4BYOkdqz+NP6B7ernhJEI5bUBRGU7Seb4hqwqnhJB7D9dxnmQK/5lw8uz9Xtyawy5yLdb0bElLuo2pAmBRAwylc+nCqYEqVDQDbqkFPL2YJNgV2kOTeKqXLBVTG28JODGOlTcwRizbrmp4VlW9g05xvCP0i+81MqevK/snbT7S5/39TRdaj1veKm555yI431ca4uxHFDniPHyOC7m+egcQb0oJRhI0JlcwGhy7pepAUXJRIYcCdo5AuQ4LUBFvqMhVMul6d8kR7id1WCWaDd4fVOLilYeexWzMdEnrY42QEhCyXK+My6Teb7UjWTxmeipWhAKVVYnq43JVA3BVmnn4ujXSOdxLlA22vI1h0xfUJRVQSAIkyOjh5gbRycB1c8BybNEqlkTqlOyv7HkNWgYCKmQlk0ZDwam0R3E1zkAumzkrh0glN8dNv8XuFtQEs=HXO4HzhhsaHu9Gg1mnqJpw==; MII=000100030002; MIE=00090001");
        message.setTaskId(123);
        message.setWebsiteName("10010.com");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void gd189() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "isLogin=logined; sduam=E10ADC3949BA59ABBE56E057F20F883E; .ybtj.189.cn=0812EE1C47F645D44E4DFD2FD1B85890; aactgsh111220=17705423957; BIGipServerwangting_tongyi_2_dynamic=2399143946.38947.0000; JSESSIONID=vh3LXq6JHp4zpyvM4YqlLGhWGHQ75SWvccBPy00d9PnhdhTGxrGb!-59035512!NONE; ECSLoginToken=90000SpgrmKiqRxnaF077RPBAosK70k3OWRTCuLwkrO3wRx+yY6HXKxtFKi0/H6Jx5JWLZBRe8w8g0W8MtO2XaiPK2YsfLMQVThwX38ePdAzQPtX4AyHmBNIs0/qVQis8hYMb39ag9yKT+zb7IvCUnpydJaIPCgtt/rvZgN/ioon8r2SiS343B+BU2s80uu39QbZO6QAwbqDU7HjvwNx/0Q9PO4O9VI9bfb594x369PhS67rmHNI+g2Aw3LJe4ZI1yrJ4YW+5nyj0s4aCdIL3iFCu3XtOycfP2uzFhXF1H0fHNr/JvcoVWDI3kGwHdAcaa6YmukGgNUXR+CR7iOUUl8bxeeAF/inIEtlPaE9Qat6PcWUB2xjZU1MUiLX0nV1FPGHTBteuLhJ96Snv6F3l+qw4Dms+N632Z39dS1Q6WEflm16NVikXLY6IpGJ+h/prE6AsnyAB35juRySU7orGp+xgoNF0b5/jLXgLsJbUE14cR9+U8bHEABlRSjy6FNxjsQ8VgwvrngpR8b9i/RGQ1daDkJUArGxzIF+W86UzK3466Ow=; JSESSIONID-JT=416DC0CF5375716F33290BE3F71A4DCC-n2; ECSLoginReq=ReqPath=WKTUcaKxENIPN9YZ15CSxnHlCOOcAJo2tDiTJUlpXfU=&ReqQuery=7cq6GDrJwEJ4a/icxOuKTs7cHv0iSjzBfuEqJihP+EPKJl118jOHziNzGIldhd7G8y6JBCGI+Sz1xjOJV35YD9Iu07s5QllrG14WOh1b1xVcDqj3p+ujS37NRtlc8mpBl8DHG2epMuwIdXBcHi0RIT8dU/KjrlZKVsYHSPzTJK4=&ResPath=; userId=201%7C20160000000000797984");
        message.setTaskId(1000000143);
        message.setWebsiteName("sd.189.cn");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void zj189() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "accessTokenId=bd42452a8765590420a55f6ccaffb8af90caf087a49e131c0b4e65e4f5b07a140df36ba160921ccddb4a0a9725aa8d134c0e912cc8a1679dc29204ef297cfd1b; .ybtj.189.cn=C19E373F5FB3B01C8F70FEB443A245F3; aactgsh111220=15381078908; city_id_choose=571001; JSESSIONID=2B413F122DFB8A1E31E6CDCC2A54CF17.node1; area_id_ip=571; JSESSIONID-JT=21AEACDDCAF83F1ECB47F69AB809A95E-n3; userId=201%7C20150000000039723718; sessionIdWitchIsSna=9939e2be4d3cc3165c92951af994b28e88767c86fa866f402200742712c6c647b71526c05b57269aded48e4f6af803b8; access_ip=122.224.99.210; isLogin=logined; city_id_ip=571001; ECSLoginToken=90000Vd8oGbacDwkDxkB6uZUY3Lr9hU78CvtUpjavkaWeSAA3YNzvenXD0Cf9Po+C4NUZyHcE8A6WpfkYQUUiSkLW34PqRceQICW+HcmoB3iyd9/TD5xgBl/eqgmNTJltXKEnEBndPfoP9MpCjb192t5/zV+K4CnyHnHwESkubsN5z4KU8ftvYLbz+IwIe5LZiMLg7RqqjpKrQvQZG9KskPf6euoX9VI8/HfLeW9BwrzTLa0H+LD3Hn/Nq0suacnK+HoFjiM7PjV/qecvly4aZD3czO2ROCoho5i5QBQNoJW1iz622ANfiNXg7iUvmK5+sveG6/PZOvStYUJV9XuYHbefgvaBm/rNmfDHFHMQEBRx3dTS7bsg53C6sQjCjgvJBbDkNi4tCDyiKbUkyElknjHkcuHurWJ3l8MxXUllivMDoZXc0m8dRlmth7u+mjAjRr0tJm9/ktoAb1dEqibj6pFRGUDbr9BRh7D9ws8lOvZLisc/JBTzHj0f9wHcQSDgFh33tafTtu+/V1LLWT+2ET4/4H8TQWO6dNhDKTp/DXTvDMM=; area_id_choose=571; ECSLoginReq=ReqPath=WKTUcaKxENIPN9YZ15CSxnHlCOOcAJo2tDiTJUlpXfU=&ReqQuery=7cq6GDrJwEJ4a/icxOuKTs7cHv0iSjzBfuEqJihP+EPKJl118jOHziNzGIldhd7GjyekV0+IUFjull+6T63n8thejGYqsBYWrgzOrjR+vlVYh8AAftCgXMkST4wy2D7LvdG1QA1GsXeXhO+hfJFKtyt9QW94inH0k6pRHohD9q7zalqnFJB+K5A6NzPG6I38BQ8e0AO4ABA=&ResPath=");
        message.setTaskId(123);
        message.setWebsiteName("zj.189.cn");

        // message.setEndURL("http://zj.189.cn/zjpr/activities/hd_20121029_download/index.html?channel%3Dwt/");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void sc189() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "isLogin=logined; .ybtj.189.cn=FE41468C6D179DF22C2DF91D1A76A3E8; aactgsh111220=17711083630; scIsLogin=yes; JSESSIONID=y2cQXLhQ8MwJhW2pg2lGwj88gTWlRr4klx4mfnMm6gJTrTXDKMSG!-1197773817; ECSLoginToken=100363QpCjmbnWf6EECF3IEArtUzdk9r2OzjxFL6bpvVjVCL6TXU8mIGVSGrizThdMNMtqxCzOiI9c0ZS1b55S/qKpoXPVZ5NWWya3YwgamIrV9MfPGo7hzQqG9MvCumTDFaT8Bdv8bimpd3/k3Vs3AkztRn5EXojVbL8i/q4CPozmTHCkuB1AtMtcryoUb1awDVWCng/jWUuMw+DL7jHsggCFhKL3reK7pKbYiEfqpXh6LWNFF/yUL9fDX489+G82bXBUDeo9xgtdukk0kpStPr8+kvUgKNPMMdNhM5aCYDNVESv+N/+Au0Q7Bvn0V+GtuqXSP1JtgI+rw9xbmnQeO7MqJzWbgqTMwbaWRqiN5WSwje7HECRo47BstLFimjDoFE2xXoQBsnTGSwN3cqYIHYJA6CodGdgrwGCM0mqHJcmq6alwCSCiRzatMV4eUp9TrvyOsRw5gvwUjDVc8itrmHQ2mybyYWBfISWycUVUcXbjUd1RwB5lxzVrPS4RRkOXoNZ3x1Ws5KNRKz0G+/BecBq5QbqDA2SOCqZv9LqliAWA40=; JSESSIONID-JT=759D975756200E3C53411D1982BB1BA3-n3; ECSLoginReq=ReqPath=WKTUcaKxENIPN9YZ15CSxnHlCOOcAJo2NUtTAeUzrXBQFsDYCOdS8LTLYyyF1+Q4KvDgvUs0/pWBR7i5kwoegb82HRebWaFN/PIu+BhTgzwWtjpP00K7oJORB8w95FRJz4wfdP2UmxAapWFqP9md7S3+vx6Yf5Vz/WN1P82qPoI0UcNyGZ1Eq2wxuwBjJAdT1+/PNWn1iGUGdvVIXECIHUZi4FF/TrGAAhhGSLMSQt5ZHgzvwb9kYmKnpXonGqGW&ReqQuery=Rlo7Xn+UMWQPHgJHKN5dE5aspXvQrshwzcRMuVqwFPSU4mFDMoAc9teaqz8scoC2osXuJL8JU1SUtTUb23gaiYse8kYDOfxGRzWXG4afZmtTCoek7MAEiQLaUzHIEGNAecfjUUSPx8cBGeVYXQV8kPs7qszDKWPDr2lndDOzAJMYKcAUA2PvXGKv9LWH44jAY9AM392zn+8=&ResPath=; userId=201%7C20150000000045182558; SHOPID_COOKIEID=10023; scIsBiz=no");
        message.setTaskId(123);
        message.setWebsiteName("sc.189.cn");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("desuserInfo",
            "695F8CB9B03821E5197683F0044E991127AF3C478071A40198CA676430E4103E597D91C4C62FBDFBBDEE7F64E689BCE29C66D236FB2F55CAABBBAF547A645FC7F4D49AEC4DE51E9A91510AB01DC9C524F7F7CE09ACE04ED04A084BBC06BD791658EB8FAAF5F29EF6");
        message.setProperty(properties);
        // message.setEndURL("http://zj.189.cn/zjpr/activities/hd_20121029_download/index.html?channel%3Dwt/");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void jl10086() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "v_server_ip=10.163.41.154; G_WEB10084=wwwformSrv0301-20084; v_server_ip=10.163.41.154; G_WEB10084=wwwformSrv0301-20084; v_server_ip=10.163.41.154; v_mobile=13844034615; v_add=2201; v_name=%E6%9D%A8**; v_brand=02; v_level=100; v_vcreditname=%E6%9C%AA%E8%AF%84%E7%BA%A7; G_WEB10084=wwwformSrv0301-20084; v_server_ip=10.163.41.154; v_mobile=13844034615; v_add=2201; v_name=%E6%9D%A8**; v_brand=02; v_level=100; v_vcreditname=%E6%9C%AA%E8%AF%84%E7%BA%A7; G_WEB10084=wwwformSrv0301-20084; G_SSO10080=ssoformSrv0101-10080; G_SSO10080=ssoformSrv0101-10080; v_server_ip=10.163.41.154; G_WEB10084=wwwformSrv0301-20084; v_server_ip=10.163.41.154; G_WEB10084=wwwformSrv0301-20084; v_server_ip=10.163.41.154; G_WEB10084=wwwformSrv0301-20084; v_server_ip=10.163.41.154; G_WEB10084=wwwformSrv0301-20084; v_server_ip=10.163.41.154; JSESSIONID=b22f929761d62ce3c48e372e223c; G_WEB10084=wwwformSrv0301-20084; v_server_ip=10.163.41.154; JSESSIONID=b22f929761d62ce3c48e372e223c; G_WEB10084=wwwformSrv0301-20084; G_SSO10080=ssoformSrv0101-10080; G_SSO10080=ssoformSrv0101-10080; v_server_ip=10.163.41.154; G_WEB10085=wwwformSrv0301-10085; v_server_ip=10.163.41.154; G_WEB10085=wwwformSrv0301-10085; v_server_ip=10.163.41.154; JSESSIONID=b22eead85f1429f73a4f60b6270e; v_mobile=13844034615; v_add=2201; v_name=%E6%9D%A8**; v_brand=02; v_level=100; v_vcreditname=%E6%9C%AA%E8%AF%84%E7%BA%A7; G_WEB10085=wwwformSrv0301-10085; v_server_ip=10.163.41.154; JSESSIONID=b22eead85f1429f73a4f60b6270e; v_mobile=13844034615; v_add=2201; v_name=%E6%9D%A8**; v_brand=02; v_level=100; v_vcreditname=%E6%9C%AA%E8%AF%84%E7%BA%A7; G_WEB10085=wwwformSrv0301-10085; cmtokenid=d89f5b4c0b904325b133d0399394a72e@jl.ac.10086.cn; CmProvid=jl; CmWebtokenid=13844034615,jl; G_SSO10080=ssoformSrv0101-10080; cmtokenid=d89f5b4c0b904325b133d0399394a72e@jl.ac.10086.cn; CmProvid=jl; CmWebtokenid=13844034615,jl; G_SSO10080=ssoformSrv0101-10080; G_SSO10080=ssoformSrv0101-10080; G_SSO10080=ssoformSrv0101-10080; JSESSIONID=b258fd2c6a811ad6f15209ed6c76; G_SSO10080=ssoformSrv0101-10080; JSESSIONID=b258fd2c6a811ad6f15209ed6c76; G_SSO10080=ssoformSrv0101-10080");
        message.setTaskId(123);
        message.setWebsiteName("jl.10086.cn");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("password", "716253");
        message.setProperty(properties);
        // message.setEndURL("http://zj.189.cn/zjpr/activities/hd_20121029_download/index.html?channel%3Dwt/");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void hb10086() throws InterruptedException, MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("QuickStartProducer");
        producer.setNamesrvAddr("192.168.0.243:9876");
        producer.setInstanceName("QuickStartProducer");
        producer.start();

        for (int i = 0; i < 1; i++) {
            try {
                Message msg = new Message("rawdata_output", // topic
                    "detectResults", // tag
                    ("Hello RocketMQ ,QuickStart" + i).getBytes()// body
                );
                SendResult sendResult = producer.send(msg);
                System.out.println(sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }

        producer.shutdown();
    }

    @Test
    public void sc10086() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "cmccssotoken=21aed58aaacf458f81036c43f3f240b3@.10086.cn; jsessionid-echd-cpt-cmcc-jt=860F86644D2CB383D3FAF5CB2D83BF5D; c=21aed58aaacf458f81036c43f3f240b3; verifyCode=45e25a001c7f829b4aa2a83966dba14d8e952f49; ssologinprovince=280; loginName=13730802972; CaptchaCode=SJDYZG; userinfokey=%7b%22loginType%22%3a%2201%22%2c%22provinceName%22%3a%22280%22%2c%22pwdType%22%3a%2201%22%2c%22userName%22%3a%2213730802972%22%7d; is_login=true; rdmdmd5=051C423A68EEFA9A4103EB1B3D6A622D");
        message.setTaskId(123);
        message.setWebsiteName("sc.10086.cn");
        message.getProperty().put("telphone", "13730802972");
        message.getProperty().put("password", "996247");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void bj10086() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "charset=f2c8fcb0228391e693e7ef756f75fa96; SSOTime=2016-01-20 17:42:41; mobileNo1=86ded9e73b60dacc1848df9597fc0832646621a2@@f09302c9d29952f6c10533c1d1506b014ad160c8@@1453282961676; bj=; Webtrends=122.224.99.210.1453282944220772; JSESSIONID=0000vN7bF7DzPoeT0mLZC5Edrw_:17oq0c3h6; CmWebtokenid=15001285176; cmtokenid=2c9d82fa5083af5001525e6a28b3221f@bj.ac.10086.cn; realname=true");
        message.setTaskId(123);
        message.setWebsiteName("bj.10086.cn");
        // Map<String, Object> properties = new HashMap<String, Object>();
        // properties.put("password", "716253");
        // message.setProperty(properties);
        // message.setEndURL("http://zj.189.cn/zjpr/activities/hd_20121029_download/index.html?channel%3Dwt/");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void ali() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "ubn=p; thw=cn; spanner=Jhue7xCq0DB1LjfjizZbnkQVjCyBGQUKXt2T4qEYgj0=; lid=%E5%8F%B2%E4%BA%8C%E5%B9%B3%E7%88%B1%E4%BD%A0; _l_g_=Ug%3D%3D; JSESSIONID=RZ12NpXJSA3Dh0bbb9aFAtwVNaNiFyauthRZ24GZ00; mobileSendTime=-1; cookie1=ACuySV7VjQIsk2ARHq0fVrI0hN8QYxz1EeWmj0guTdI%3D; cookie2=12fc15055de4d8073688e8728af1d3e5; uss=U7Y7QZFjC7Gn%2Fd2%2FIdxv84fRd7Z1N5JpsR%2FlJugTG%2FTy1hhVPtUS4XwvjsM%3D; zone=RZ24B; CHAIR_SESS=JWYmdXvINYrjfJhNfnAOApEy7drxxpERpaBXObg17RbIKNEAtyvEOZ4QbekehwNGEWdFdrMdTEgxjpmpPqgglJnsKWqD6lZlgjfoqn6-oIN8soNRHZaPtzNaUKvVSWuT9luPvs5q2AS1dA-sMem-Xg==; CLUB_ALIPAY_COM=2088022954760902; ALIPAYJSESSIONID.sig=CnhJNsKPCIyGlk_YNsBW34Jb81-MUaD_NKbci8FgTrI; skt=e699962941112ee9; iw.userid=K1iSL1mnW5gJwO2aWx2lPA==; ucn=unsz; riskCredibleMobileSendTime=-1; _umdata=ED82BDCEC1AA6EB9864C872E17D6058993F875F279997C08B9DB68EB3BD5D59843943117D81447F3CD43AD3E795C914C8B6F457ED96A4AD595BA5BD2F1AC4547; tracknick=%5Cu53F2%5Cu4E8C%5Cu5E73%5Cu7231%5Cu4F60; mt=np=; riskMobileAccoutSendTime=-1; _nk_=%5Cu53F2%5Cu4E8C%5Cu5E73%5Cu7231%5Cu4F60; log=lty=Tmc%3D; lgc=%5Cu53F2%5Cu4E8C%5Cu5E73%5Cu7231%5Cu4F60; existShop=MTQ5NTAyNjUwNg%3D%3D; credibleMobileSendTime=-1; sg=%E4%BD%A096; ALIPAYJSESSIONID=RZ12NpXJSA3Dh0bbb9aFAtwVNaNiFyauthRZ24GZ00; cna=BTmjEcril2ICAXWIVkG7mFAK; session.cookieNameId=ALIPAYJSESSIONID; LoginForm=alipay_login; umt=HBb535b50a63a17bf2637f16a4ecd1a19e; _tb_token_=eede7d1ee0310; riskMobileCreditSendTime=-1; uc1=cbu=1&cookie14=UoW%2BvfoR2k74RA%3D%3D&lng=zh_CN&cookie16=Vq8l%2BKCLySLZMFWHxqs8fwqnEw%3D%3D&existShop=false&cookie21=UtASsssmeW6khGmdJha8&tag=8&cookie15=VT5L2FSpMGV7TQ%3D%3D&pas=0; alipay=K1iSL1mnW5gJwO2aWx2lPMcmJ+8kTs4kAtvGzDV+SA==; uc3=sg2=ACJc9qbL5F5J1clhBSR%2BT8Gvti4QlYaJ%2Bk%2Brta3P6oA%3D&nk2=qSWTmKtGAzh38A%3D%3D&id2=UU6m39NZmsUJ6Q%3D%3D&vt3=F8dARVDUWC%2Fz6EsIkN8%3D&lg2=VFC%2FuZ9ayeYq2g%3D%3D; riskMobileBankSendTime=-1; unb=2670856439; _cc_=VT5L2FSpdA%3D%3D; riskOriginalAccountMobileSendTime=-1; cookie17=UU6m39NZmsUJ6Q%3D%3D; ctuMobileSendTime=-1; ctoken=Ggwi7a2CFN-lzgXA; ali_apache_tracktmp=uid=2088022954760902; tg=0; t=6f6e2513e1794d2f86e48f709d48eb22; v=0; lc=Vy%2BWeXGTwf6sqdtweWIz");
        message.setTaskId(1000000146);
        message.setWebsiteName("alipay.com");
        message.setEndURL("https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm");
        collector.processMessage(message);
    }

    @Test
    // w593237554@sina.com/pa$$w0rd
    public void sina() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "SWEBAPPSESSID=5187a86c27aafc1a5ecfa75b36bca445f; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW2p2_AhArnHC4MFh2Si7aQ5JpX5K2hUgL.Fo-fe0M41KqXeK22dJLoIp7LxKML1KBLBKnLxKqL1hnLBoMfSKeN1K.cSh2p; WEB2_APACHE2_TC=0bbcc32d0103a537a570e89cb4d0c612; SWMHA=usrmdinst_3; SUB=_2A256-PvADeTxGeNL6FUY-SjIyj2IHXVWAoWIrDV9PUNbktBeLWH5kW0zQDE1o85H8-QuFNfrDknT3fQ2eQ..; gsid_CTandWM=4udve7771tkZIOi48bm46neGw6D; cnmail:username=chen442337474@sina.com; SCF=AhLn_czUp5S3734RL_40m0V3ylRnL_CrXOVmqy-0_jc13FSFrNNWecBTlB5ZR4zEb8cEc079TtH_gcs2AmUIl4c.; ustat=__122.224.99.210_1476251858_0.87618200; genTime=1476251858; SINAGLOBAL=; Apache=8718840363575.187.1476251859979; ULV=1476251859981:1:1:1:8718840363575.187.1476251859979:; historyRecord={\"href\":\"https://sina.cn/?vt=4&HTTPS=1\",\"refer\":\"http://m0.mail.sina.cn/mobile/index.php?html5&ver=4.5.13\"}; dfz_loc=zj-hangzhou; vt=4");
        message.setTaskId(1000000146);
        message.setWebsiteName("sina.com");
        message.setEndURL("http://m0.mail.sina.cn/mobile/index.php?html5&ver=4.5.10");
        collector.processMessage(message);
    }

    @Test
    public void cmbSearch() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "WEBTRENDS_ID=122.224.99.210-2022324368.30474617::499C4F3F959CF53A64A8A22B2A7; WTFPC=id=29192862da56c3906291444812358098:lv=1444814410574:ss=1444812358098; ASP.NET_SessionId=nfoho3lvl0ijafz2ll31czx2; $CLientIP$=122.224.99.210; LoginType=C; DeviceType=H; _MobileAppVersion=1.0.0; Version=1.0.0; LoginMode=3UFntqbNDa8_; clientNo=15A386CA9B526F35A2192BF81F5F6CAD127767698660204100253407");
        message.setTaskId(123212);
        message.setWebsiteName("cmbchina.com");
        message.setEndURL("https://mobile.cmbchina.com/MobileHtml/Login/RecentUpdatePage.aspx");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void hotmailSearch() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "wlidperf=latency=1190&throughput=; mkt=zh-CN; MSFPC=ID=ad22bcae4325b6479312a1210fa9d039&CS=1&LV=201510&V=1; MUID=2C892F3A35106C6D2588270431106AFB; UIC=tyckO0HDIIw2fi7Uk+rl7/vojnOtvhG9WqRKyyGFBYwnag62/kfV8rf/1pI531QYPTza7MTLHD3E4wwTElLOmVflSisCYKCXaYOUd4k/jdo2TsFqfdD7ZUcMQknsGVLX; wlp=A|pNLD-t:h*9yGfC.Color_Blue:a*liDKD._|yT7M-t:h*xlifDg.Color_Blue:a*huHAD._; pd-12532470855233035220=FoldersExcoState=0&FoldersModelHash=m81h4g2&excofl=0&excoqv=0&excowebim=0; LDH=6; wla42=; HIC=9447960cf27fc0d8|0|0|blu174|4896|blu174; pd-17153393188802571888=FoldersExcoState=0&FoldersModelHash=okwADA2&excofl=0&excoqv=0&excowebim=0; KVC=17.4.9407.6000; MH=MSFT; NAP=V=1.9&E=1131&C=DOHzsMcrn8dY1Vd8riJK_7UwGMx-s7RMk3HeTAHJOJgiS5N0LBUSAA&W=1; ANON=A=9FB2D759144034BC41F0D5F6FFFFFFFF&E=118b&W=1; xid=dde5b7bf-4e0b-4df5-8bd3-12db1a090651&bhtCs&BLU174-W47&343; xidseq=425; E=P:XgcYc1jb0og=:l3uOzZgxT/SFdn6PHjpTGvmSSdElp1nf9YfF7Ee1s2A=:F; upcb=0; wls=A|pNLD-t:h*m:a*n; BP=FR=&ST=&l=WC.Hotmail&p=0; wlxS=wpc=1; LN=ZYbMy1445512446302%265c59%2611; widecontext=X; wlv=A|pNLD-d:s*mretDg.2+1+0+3; wl_optintopcexperience=false; PPLState=1; MSPAuth=39TFF0*jSFOvCrvSmfsh5PK5tmrCSwc!gN8m2L9wyNHADnIvOK657WQhARSyPPWmECUooygqpqsXhAPY0GToEnhjkVwQ6MnZsOJAi!umTGuv7cdIPrGMp*KA!dWItj3cnK; MSPProf=3EkzAYMq0A8CaU3WeWz63iKhmagJ3nrK4C5ESq6VoRwYurJiU6bgHXLavABWqpDYGUsB9cz2E0n8MSwAoVgHVE65pLjWoyJFoiSpKOXOlivj0CmmNM!KLEB7IC81hssn2Jc*WWYDLm0cFCRiz8eX1KYHob*ilw2VwidCZ7smersOo$; WLSSC=EgCCAQMAAAAEgAAAC4AAGEcNoa03F63Yqzbw+swtTl5Npwpb9b+dWduvNBhtrXXCSNmb6uadn9h5wrCnJ6zv1CCMp+y8SXBPoAby6py7x1t8x4cyMS2+t3thxHFw2gjcTk7TaBTtq6LZhy97dmi1WZWDiKSBo7CLHgddjQDauCL/XBRApwbJOcmQ84hBe7nxAGcA8QD9vwYAW6aJg6imKVaqOydWECcAAAoQoAAwFQBodW9kYTE5MUBob3RtYWlsLmNvbQBFAAAUaHVvZGExOTFAaG90bWFpbC5jb20AAADrQ04AAACZnWYIBAIAAHY2bUAABEMAA+i+vgAD6ZyNBMgAAUQAAAAAAAAAAAAA8n/A2JRHlgwAAKimKVaq4p1WAAAAAAAAAAAAAAAADwAxMjIuMjI0Ljk5LjIxMAAFAQAAAAAAAAAAAAAAAAEEAAAAAAAAAAAAzVY/UwAAJ8nYyPh2+pMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=; mobilecr=cr=1; RVC=m=1&v=17.5.9413.1000&t=10/22/2015 07:29:47; AVC=v=1.8.3.0&t=10/22/2015 11:50:55; DID=6797; mt=01_b88ed35491a9400b7687e5bc515ccedaee74ea8d41c863a09dc49a68a266b857|4c3ee2043d8bfb38; KSC=19WEPAgQyq/gYcwQE+V73NWV9iPuOB/X7rboetdZvuRJl+DqCSzWm8t+SXVG0wqtcY3WKTSd7SA/NuoJ5kvwWjPHgNt32KZNuFShHw021Vz2PlnRqgtKKm48oup4SGx2qwwavS1D+IrEkGALedzBWvWXIusPhD2G2aFNfWDte8hZxU0RchNMT2eTyde2vF2v9fM1CGLkzYfy8PuoCaSgoMJJpIyS9pSVq0mauScHWxC7XwybbFwIafsNgNonbH7+m4C3UfwJsXyGlt2rxZ9qSoe6VXI0ECGYvBjcbkktiIgW6/0loonp1m12o9VdvDF4OGxEDZKL9oOTjTotT8cKxOIZAwbSe8LhnMtguWiVKKtYq+K9p8lh/4qdD5vGLzvL");
        message.setTaskId(123212);
        message.setWebsiteName("outlook.com");
        message.setEndURL("https://i.taobao.com/my_taobao.htm?nekot=d2M1OTMyMzc1NTQ=1441364937994");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void outLookSearch() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "MicrosoftApplicationsTelemetryDeviceId=4e9a2ef4-7d05-1dcd-9469-43620e308490; MicrosoftApplicationsTelemetryFirstLaunchTime=1459410938339; MUID=1FFAF9D1FAB567001558F11DFEB56381; CookieVersion=2; ClientId=670FCE6B54E6465395A325851CC1E3E8; MSFPC=ID=50783541b151b640ab5b4159a24067b9&CS=3&LV=201604&V=1; RoutingKeyCookie=v1:00014c40-e12a-9274-0000-000000000000@hotmail.com; wlp=A|yj+n-t:h*maQTDg.Color_Blue:a*boLNDQ._|9LCj-t:h*L7kwCg.Color_Blue:a*DeqgDQ._|xVhn-t:h*UtTjCQ.Color_Blue:a*99V7DQ._|yT7M-t:h*xlifDg.Color_Blue:a*m116DQ._; RVC=m=1&v=17.5.10018.1000&t=05/04/2016 06:34:52; exchangecookie=6974d28cd2714ddcb1e116d77b47ef6e; orgName=hotmail.com; UC=ef7a2bb7fdc7478f986b0cad0630605d; mkt=zh-CN; wls=A|9LCj-t:h*m:a*n|yj+n-t:h*m:a*n; HIC=4cbcb33d218be440|0|79|blu176|2938|blu176; wlxS=wpc=1; wla42=; LN=GOfEI1462349963212%261302%2611; BP=FR=&ST=&l=WC.Hotmail&p=0; xid=59a727ed-f8f3-4ca0-b1af-96b50bfe24c1&&BLU176-W1&164; xidseq=71; LDH=8; E=P:qKZ61vRz04g=:HByGFshx/pkGJVcfhLEtMzDnGW/569xxVs9FO2syYI0=:F; MSPAuth=32dLYFDzpCu1jfcj9D4knekisuVAu!kKrzstgqwXy1Y!fMTTrscYTQmPu8YAJwHYeS3Ze4btG4iS0xCTOUQH43RlPpC60lRyA!rrKVTRJy8Og!!cNVxPPPHAgAcPpbCIUO; MSPProf=3MNP03kb!A8nsv1NBZh4bilXm!3uTgw1zG23SkbjaSF0paR!iDw60rkTBj6FN0IzQLdfJi5bWnZgtPdiMRTVPTwjKKjwBJqAr0becnOpL8KcG76kbETrITROhFGVIJRs*CRhv*!FarFUD6RMhILYGye3V4dywrJLwqSO97LmDVmApeKV0Ds1gwyvPIjV53LkgP; MH=MSFT; NAP=V=1.9&E=11f5&C=wG8nM--d6sA4PCaqJZcA0LFOkMp9mFGDuuGqtMGVLDA0gslhZXTzEQ&W=3; ANON=A=10707D3A11C421912F44833BFFFFFFFF&E=124f&W=3; WLSSC=EgCWAQMAAAAEgAAAC4AADfo5ug1CH/P6895WcIibfW2LV/5B4zW1YiHJxmwqmkGafF+anhythiYu5pWAS4soNQblynyyZgqNg64F2JNSOZ+ntvNYcc5xVR9Ianx2QXV8HRira+O2SWZt/zmkX0IcqoDdMOgKuoMEE4YPIbnvidh+wN0VkaLeQ3JAfP/oQhYFAXEABQFATAEAdJIq4ceyKVfHsilXECcAAAoQoAAQGABmYW5ndGlhbmx1bkBob3RtYWlsLmNvbQBOAAAXZmFuZ3RpYW5sdW5AaG90bWFpbC5jb20AAAAKVVMABTk4MDUyAACMAQgEAgAAdVptQAAEQwAHdGlhbmx1bgAAAAAAAUQAAAAAAAAAAAAAo6vQqw+AkbMAAMeyKVfHWaBXAAAAAAAAAAAAAAAADwAxMjIuMjI0Ljk5LjIxMAAFAQAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAf4XY5RzzAKkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==; DefaultAnchorMailbox=fangtianlun@hotmail.com; SuiteServiceProxyKey=+aTDNPBCh1blxoJZTDnwHWeI+JN1C8uDdEBDAIeSE88=&Ackoh6ap5AVan399MWhyJw==; domainName=hotmail.com; AppcacheVer=16.1216.7.1986465:zh-cnbase; PPLState=1; X-OWA-CANARY=pKPSM9v5c0-ygBdNypTo38B0p7r9c9MYuY3ALC3LQaE1uQ85MFqPI4RWxeGkqhYCs6gL_u-kAFU.");
        message.setTaskId(1000000375);
        message.setWebsiteName("outlook.com");
        message.setEndURL("https://i.taobao.com/my_taobao.htm?nekot=d2M1OTMyMzc1NTQ=1441364937994");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void Search139() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "Login_UserNumber=13923017393; cookiepartid1877=12; html5SkinPath1877=; a_l=1474085482000|4266630640; cookiepartid=12; a_l2=1474085482000|12|MTM5MjMwMTczOTN8MjAxNi0wOS0xNyAxMjoxMToyMnw5N2NlMjczY2VkZWU2NTFkNGVlYzQwMmI3MDFiNDg4Zg==; RMKEY=1bd1ed9257ca4d6a; fromhtml5=1; provCode1877=1; Os_SSo_Sid=MTQ3Mjg3NTg4MjAwMDUxNTQ1001A9923000005");
        message.setTaskId(3333333);
        message.setWebsiteName("139.com");
        message.setEndURL(
            "http://html5.mail.10086.cn/html/mailList.html?source=1&sid=MTQ3Mjg3NTg4MjAwMDUxNTQ1001A9923000005&rnd=804&comefrom=2066&useInnerWeb=0&k=1877&cguid=1211000222685&mtime=822");
        collector.processMessage(message);
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void spdTest() throws InterruptedException, IOException {
        CollectorMessage message = new CollectorMessage();
        message.setTaskId(1111111);
        message.setEndURL(
            "https://ebill.spdbccc.com.cn/cloudbank-portal/loginController/toLoGwxxhgKshkz9X30Gi28FUoxFSvEIqexT6lbFpZVcNCA8xDh$PQzQNV9TX8FDfkU5ZrC0xcD6LOH0fup2EDRei0a8$V3baSLisjCZlA==");
        message.setWebsiteName("spdb.com.cn");
        message.getProperty().put("seedurl",
            "https://ebill.spdbccc.com.cn/cloudbank-portal/loginController/toLogin.action?zrz4ofa8h5IFb1udd/JAcjoWQd1OZbTumNYxL2UdZrb4uQ4thGA5DGWaIVJK2YX8Z1/5NNDAOkquZRzQb/NywFfXQAEMsqEUZUqxDbRxXvVt9KC30uyoWbGOBAOX7QuRPmiARNAWAfzg37VWDUz5iA==");
        collector.processMessage(message);
    }

    @Test
    public void wdzjSearch() throws InterruptedException {
        while (true) {
            Thread.sleep(5000);
        }
    }

    @Test
    public void alEnterpriseSearch() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "JSESSIONID=GZ00F7KeUvi26IB8s2x3EqqjTqn0qnmbillexprodGZ00; JSESSIONID=17ECE16BF503A5DDF78F3FEF5D522A9A; mobileSendTime=-1; credibleMobileSendTime=-1; ctuMobileSendTime=-1; riskMobileBankSendTime=-1; riskMobileAccoutSendTime=-1; riskMobileCreditSendTime=-1; riskCredibleMobileSendTime=-1; riskOriginalAccountMobileSendTime=-1; cna=xy6WEFpt7BYCAXrgY9JVKzcP; ctoken=60MrkB1axXiCY1JV; LoginForm=alipay_login_auth; alipay=\"K1iSL1gljTGwaCm8I5oCGzUMk+OtFmXRcPnulv6nyZ0mZpNLkvVvx/XWt+98lw==\"; CLUB_ALIPAY_COM=2088121127562577; iw.userid=\"K1iSL1gljTGwaCm8I5oCGw==\"; ali_apache_tracktmp=\"uid=2088121127562577\"; session.cookieNameId=ALIPAYJSESSIONID; zone=RZ12B; ALIPAYJSESSIONID=RZ13Wy1IHj3BORGQhK1kowhx1EuGAtauthRZ12GZ00; spanner=XbBeA3l/SWgvbBj9MC1CSlVL+FKpWpY+");
        message.setTaskId(123212);
        message.setWebsiteName("alipay.com_enterprise");
        message.setEndURL("https://mbillexprod.alipay.com/enterprise/accountDetail.htm");
        collector.processMessage(message);
    }

    @Test
    public void testFromFile() throws InterruptedException, IOException {
        CollectorMessage message = readFromFile();
        collector.processMessage(message);

    }

    @Test
    public void testStart() {
        while (true) {
            //            logger.info("哈哈哈");
            //            messageService.sendTaskLog(RandomUtils.nextLong(),"测试信息","不告诉你");
            String hostname = PropertiesConfiguration.getInstance().get("trade.remark");
            TimeUnit.SECONDS.toSeconds(60);

        }
    }

    @Test
    public void ali1() throws InterruptedException {
        CollectorMessage message = new CollectorMessage();
        message.setCookie(
            "ubn=p; thw=cn; spanner=Jhue7xCq0DB1LjfjizZbnkQVjCyBGQUKXt2T4qEYgj0=; lid=%E5%8F%B2%E4%BA%8C%E5%B9%B3%E7%88%B1%E4%BD%A0; _l_g_=Ug%3D%3D; JSESSIONID=RZ12NpXJSA3Dh0bbb9aFAtwVNaNiFyauthRZ24GZ00; mobileSendTime=-1; cookie1=ACuySV7VjQIsk2ARHq0fVrI0hN8QYxz1EeWmj0guTdI%3D; cookie2=12fc15055de4d8073688e8728af1d3e5; uss=U7Y7QZFjC7Gn%2Fd2%2FIdxv84fRd7Z1N5JpsR%2FlJugTG%2FTy1hhVPtUS4XwvjsM%3D; zone=RZ24B; CHAIR_SESS=JWYmdXvINYrjfJhNfnAOApEy7drxxpERpaBXObg17RbIKNEAtyvEOZ4QbekehwNGEWdFdrMdTEgxjpmpPqgglJnsKWqD6lZlgjfoqn6-oIN8soNRHZaPtzNaUKvVSWuT9luPvs5q2AS1dA-sMem-Xg==; CLUB_ALIPAY_COM=2088022954760902; ALIPAYJSESSIONID.sig=CnhJNsKPCIyGlk_YNsBW34Jb81-MUaD_NKbci8FgTrI; skt=e699962941112ee9; iw.userid=K1iSL1mnW5gJwO2aWx2lPA==; ucn=unsz; riskCredibleMobileSendTime=-1; _umdata=ED82BDCEC1AA6EB9864C872E17D6058993F875F279997C08B9DB68EB3BD5D59843943117D81447F3CD43AD3E795C914C8B6F457ED96A4AD595BA5BD2F1AC4547; tracknick=%5Cu53F2%5Cu4E8C%5Cu5E73%5Cu7231%5Cu4F60; mt=np=; riskMobileAccoutSendTime=-1; _nk_=%5Cu53F2%5Cu4E8C%5Cu5E73%5Cu7231%5Cu4F60; log=lty=Tmc%3D; lgc=%5Cu53F2%5Cu4E8C%5Cu5E73%5Cu7231%5Cu4F60; existShop=MTQ5NTAyNjUwNg%3D%3D; credibleMobileSendTime=-1; sg=%E4%BD%A096; ALIPAYJSESSIONID=RZ12NpXJSA3Dh0bbb9aFAtwVNaNiFyauthRZ24GZ00; cna=BTmjEcril2ICAXWIVkG7mFAK; session.cookieNameId=ALIPAYJSESSIONID; LoginForm=alipay_login; umt=HBb535b50a63a17bf2637f16a4ecd1a19e; _tb_token_=eede7d1ee0310; riskMobileCreditSendTime=-1; uc1=cbu=1&cookie14=UoW%2BvfoR2k74RA%3D%3D&lng=zh_CN&cookie16=Vq8l%2BKCLySLZMFWHxqs8fwqnEw%3D%3D&existShop=false&cookie21=UtASsssmeW6khGmdJha8&tag=8&cookie15=VT5L2FSpMGV7TQ%3D%3D&pas=0; alipay=K1iSL1mnW5gJwO2aWx2lPMcmJ+8kTs4kAtvGzDV+SA==; uc3=sg2=ACJc9qbL5F5J1clhBSR%2BT8Gvti4QlYaJ%2Bk%2Brta3P6oA%3D&nk2=qSWTmKtGAzh38A%3D%3D&id2=UU6m39NZmsUJ6Q%3D%3D&vt3=F8dARVDUWC%2Fz6EsIkN8%3D&lg2=VFC%2FuZ9ayeYq2g%3D%3D; riskMobileBankSendTime=-1; unb=2670856439; _cc_=VT5L2FSpdA%3D%3D; riskOriginalAccountMobileSendTime=-1; cookie17=UU6m39NZmsUJ6Q%3D%3D; ctuMobileSendTime=-1; ctoken=Ggwi7a2CFN-lzgXA; ali_apache_tracktmp=uid=2088022954760902; tg=0; t=6f6e2513e1794d2f86e48f709d48eb22; v=0; lc=Vy%2BWeXGTwf6sqdtweWIz");
        message.setTaskId(1000000146);
        message.setWebsiteName("alipay.com");
        message.setEndURL("https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm");
        collector.processMessage(message);
    }

    private CollectorMessage readFromFile() throws IOException {
        String content = FileUtils.readFileToString(new File("/data/json.txt"));
        JSONObject json = JSON.parseObject(content);
        String websiteName = json.getJSONObject("resultMsg").getString("websiteName");
        String endURL = json.getJSONObject("startMsg").getString("endURL");
        String cookie = json.getJSONObject("startMsg").getString("cookie");
        CollectorMessage message = new CollectorMessage();
        message.setCookie(cookie);
        message.setTaskId(RandomUtils.nextLong());
        message.setWebsiteName(websiteName);
        message.setEndURL(endURL);
        return message;
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println(RandomUtils.nextInt(2));
        }
    }

}
