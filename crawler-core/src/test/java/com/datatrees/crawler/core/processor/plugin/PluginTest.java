package com.datatrees.crawler.core.processor.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.plugin.impl.JavaPlugin;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.classloader.ClassLoaderFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

// How to build ,please see : http://seals.vobile.cn/trac/Colander/wiki/howToBuildPluginForJava
public class PluginTest {

    @Test
    public void testSearchJavaPlugin() {

        try {

            PluginWrapper wrapper = new PluginWrapper();
            File pluginFile = new File("./src/test/resources/plugin/simpleSearch.jar");

            JavaPlugin pluginDesc = new JavaPlugin();
            pluginDesc.setPhase("search");
            pluginDesc.setType("jar");
            pluginDesc.setExtraConfig("ttttttttttt");
            pluginDesc.setMainClass("com.datatrees.crawler.core.plugin.SimpleSearchPlugin");

            wrapper.setFile(pluginFile);
            wrapper.setPlugin(pluginDesc);
            Plugin fieldPlugin = PluginFactory.getPlugin(wrapper);
            Map<String, String> params = new LinkedHashMap<>();
            params.put(PluginConstants.PAGE_CONTENT, "page Content");
            params.put(PluginConstants.FIELD, "xx");
            Request req = new Request();
            RequestUtil.setPluginRuntimeConf(req, params);
            Response resp = new Response();
            fieldPlugin.invoke(req, resp);
            String result = (String) resp.getOutPut();
            // get plugin json result
            Map<String, Object> pluginResultMap = PluginUtil.checkPluginResult(result);
            Object template = pluginResultMap.get(PluginConstants.TEMPLATE);
            System.out.println(template);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFieldJavaPlugin() {
        try {
            PluginWrapper wrapper = new PluginWrapper();
            File pluginFile = new File("./src/test/resources/plugin/ff7.jar");

            JavaPlugin pluginDesc = new JavaPlugin();
            pluginDesc.setPhase("field");
            pluginDesc.setType("jar");
            pluginDesc.setMainClass("com.datatrees.ff7.cc.main.Ff7Main");

            wrapper.setFile(pluginFile);
            wrapper.setPlugin(pluginDesc);
            Plugin fieldPlugin = PluginFactory.getPlugin(wrapper);
            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put(PluginConstants.PAGE_CONTENT, "'vod_name=\"我爱你 大陆版\";var list_name=\"<a href=/vodlist/1/>电影</a>\";var server_name=\"$$$$$$\";var player_name=\"yuku$$$tudou$$$qiyi\";var url_list=\"%E7%AC%AC1%E9%9B%86%2B%2BXMTk2OTc0OTgw%24%24%24%E7%AC%AC1%E9%9B%86%2B%2B150184954%24%24%24%E7%AC%AC1%E9%9B%86%2B%2B157e9968fe8511dfaa6aa4badb2c35a1\";</script>" + "" + " <iframe border=\"0\" src=\"/Public/player/play.html\" width=\"710\" height=\"486\" marginWidth=\"0\" frameSpacing=\"0\" marginHeight=\"0\" frameBorder=\"0\" scrolling=\"no\" vspale=\"0\" style=\"z-index:99;\" noResize></iframe></div></div>");
            Request req = new Request();
            RequestUtil.setPluginRuntimeConf(req, params);
            Response resp = new Response();
            fieldPlugin.invoke(req, resp);
            String result = (String) resp.getOutPut();
            // get plugin json result
            Map<String, Object> pluginResultMap = PluginUtil.checkPluginResult(result);
            Object field = pluginResultMap.get(PluginConstants.FIELD);
            System.out.println(field);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFieldJavaQingRenPlugin() {
        try {
            PluginWrapper wrapper = new PluginWrapper();
            File pluginFile = new File("./src/test/resources/plugin/qingren.jar");

            JavaPlugin pluginDesc = new JavaPlugin();
            pluginDesc.setPhase("field");
            pluginDesc.setType("jar");
            pluginDesc.setMainClass("com.datatrees.qingren.main.QingRenMain");

            wrapper.setFile(pluginFile);
            wrapper.setPlugin(pluginDesc);
            Plugin fieldPlugin = PluginFactory.getPlugin(wrapper);
            Map<String, String> params = new LinkedHashMap<String, String>();
            String argsStr = "{'pagecontent':'<div id=\"play\" style=\"display:none;\"><script language=\'javascript\' src=\'/image/movPic/qiyi.js\' type=\'text/javascript\'></script><script language=\'javascript\' type=\'text/javascript\'>addFlash(\'44b2fcb2ef054368dd5cb91770aa69a8\',\'550\',\'475\');</script></div>','keyid':'','hostingurl':'','field':'','extraConfig':''}";
            params.put(PluginConstants.PAGE_CONTENT, argsStr);
            Request req = new Request();
            RequestUtil.setPluginRuntimeConf(req, params);
            Response resp = new Response();
            fieldPlugin.invoke(req, resp);
            String result = (String) resp.getOutPut();
            // get plugin json result
            Map<String, Object> pluginResultMap = PluginUtil.checkPluginResult(result);
            Object field = pluginResultMap.get(PluginConstants.FIELD);
            System.out.println(field);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFieldZhiBoAZPlugin() {
        try {
            PluginWrapper wrapper = new PluginWrapper();
            File pluginFile = new File("./src/test/resources/plugin/zhibo.bz.py");

            JavaPlugin pluginDesc = new JavaPlugin();
            pluginDesc.setPhase("field");
            pluginDesc.setType("py");

            wrapper.setFile(pluginFile);
            wrapper.setPlugin(pluginDesc);
            Plugin fieldPlugin = PluginFactory.getPlugin(wrapper);
            Map<String, String> params = new LinkedHashMap<String, String>();
            File pageFile = new File("./src/test/resources/page_content.html");
            String content = IOUtils.toString(new FileInputStream(pageFile));
            params.put(PluginConstants.PAGE_CONTENT, content);
            Request req = new Request();
            RequestUtil.setPluginRuntimeConf(req, params);
            Response resp = new Response();
            fieldPlugin.invoke(req, resp);
            String result = (String) resp.getOutPut();
            // get plugin json result
            Map<String, Object> pluginResultMap = PluginUtil.checkPluginResult(result);
            Object field = pluginResultMap.get(PluginConstants.FIELD);
            System.out.println(field);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testFieldJavaHave8Plugin() {
        try {
            PluginWrapper wrapper = new PluginWrapper();
            File pluginFile = new File("./src/test/resources/plugin/have8.jar");

            JavaPlugin pluginDesc = new JavaPlugin();
            pluginDesc.setPhase("field");
            pluginDesc.setType("jar");
            pluginDesc.setMainClass("com.datatrees.have8.main.Have8Main");

            wrapper.setFile(pluginFile);
            wrapper.setPlugin(pluginDesc);
            Plugin fieldPlugin = PluginFactory.getPlugin(wrapper);
            Map<String, String> params = new LinkedHashMap<String, String>();
            String vsource = "qiyi";
            String adrss = "0j5970W-uR9x-p70tfBX5XTDx6-muG2gV-q4pEeO3vonu-FAud04xCm04urV9qL0R0BIEz52C5M6CbYe2O7fS4uStP3qJFZ-iRb5g1y4fmy7SrpYjcUzvfqovBTVjn7QhDrcYimrWWvd9Axsv1H9_t-sIy_edTON0hl-d75KDC5_wSveJVvp8x5becgTE60czG58YwJ6c4m2H7Iz6Wb90U9388aK9k_QYe1CmXcF8Kj6ikEO4WuE83tbEE4aBy982xxq9cY-GbL6aJ9xTp198SOna9U9T2TOvicr7jYd3Zlt99TY80-crIaPwy8BlwNU21xdW%KEs5B30qJ2pr8Q%9SXe2c9ME2gKenBLpO72XVvj%mDLTBm-7j2CkO7%vHBYBxKP22f3zc%yvfZ0GV8w0a41Q0dxYY0iML_5fFEK6HBkw26YG04Jesp3hHeS-jmCb0IpUs0EqzP0cQBr0RGCR5uu446GLQA2xwCv4zapN3LWOJ-3mEKs7ixscXnb7nJcyhzatvTvlF_fn0YADrxC_SrP-f69o57B1Q114-sjur9UJQWdOBRp6KEfs46NuOa6bvY69OZ51DjF49fDkG42FEqak80p364pF82vge0ObXD5_JtTennoj1MQAfaiU500sDDK9lP4Mc0ISPe2dJp9IhQl2rhwn7ZuKz3AjkSci8-_5UxZR4Dgwf6jNF62eeO481b-J3EsGoBBarB2P5bo%JbqlB95S72eN5o%XI5b1jbC32w0UDBvcuV26MVn%ZkNSBpyxM2TETh%oiUhBLE6u23fJm%0byu0lxMv0n_-V3p-_r9xfI23ilzf512SU2C3my4pO2b3zm1R-L7f61EJmE0LGIr5uUUt3013n9NmO52dh9Z2uMCL0YXCu2TwPI-8e5Y0RekW6_MNDzx3LTxTGttvBjuknTukIrY4WTrkvIi9H3LG1u66j-CsVN5Zg3s8U4oE8zS9486B7H9EKKI7MN_02zMin6Ih29aEEe310H4Td9sFw3RZld14WiU5akNtb3ak15q1NG77QMn7gPC3as4INduYPn46jw01Esgl5fnmY0zYgv5xvuG5kuNj71uIF6ofeOb01tl65iAqfo1e24upEgBYDy52_pnq%islGBBN_N2ly_Z%3kDk0ksx72RbvSB3WI62g_Mi%TRK_BYm3A2nS1a%5FQXBKAvw2kKco%X9Vj0GoYf01bEi2566l6-OxZ3kpdQ5y0fA2TD_Z4tPeP34WrO-Ykeu1jWJk0XDKo5kicw3pV__9i-Jd27Opm2cbiU0ak5b28JdY-ABh7cwjaweqF5EyJC89x2GVuvSz_dnVx45rGBxgrINUF9cnkC1b0Lr-UZqmfGmx9b6zMl4BmhHem4eSfLrU7d4cCJ2HdnmfvCgsfjIlZ0sJ4ycYT30cqr411-tVQfkTmXffdHx9ZwJ6aqR8t0S3wB7FgRu9vqMm7R8HY0t2hx7eGSCfsOer2FfL05P3eo1BmoN9uTx-38Nm7akcjNdsfq01SCx1BspeS2H9V5%znXGBjnOu2N-mj%Vl4l9-5e71EYndBY4ud2ESdq%N_TKB2HQx2anKi%RXLiBKZP92_DEN%YqT307Q8A0X3Zg8viMB3RaoB63MLA0w1Dp2Q_1o4BY2D3MPp2-wqSv13rl902q7j5rzqM3tst79LUd02CcDh2CZOR0Nsw82e0cA-vUyUsXUtSmMs-Ea_0GwkxlXavR9l9nF-PKrSmE5rGf9D9kjGi1oCzT-xOmx2_5xb7OaNI9_KHP70l4jeKj5U09y0TajoUha7_Tr2Uugtauxd49CeH-5FeB48VuED3-ktH9B8aa1Rmbf3CX6ea7ECf87NdK5abfecSz8450PC7eEyCTe3gSrdUQsdeNsNU2HK0ba3ZQM3V86A6oyxO0Y8wu7fq06BIxLq2sEOa%aKb1BKzIR2Imsc%2ZKt8-YeA1b1A0BExfX24MvM%LBHkBigfK2Cq5N%_xdTB0Ul02KogH%5Aqm0RT_g05mGI6zHYP8DGCJ5ou3k0gIHw2YEth4tYZs3ycEw-NSgb1Tn-q0PWRG5UTxI3HwmQ9eACd2F5-C2yQav06_fK2ICRj-UvduwT_7zxT-MY4mID-k_sO6vFFEonIjwxr-uFqr1YSN9-1AG1uQTw-Fwmr1BEGq8VQfqdYiXb3i4Vx3v1bW6AP411I3EG5hKQE7To9X1hgW912DHn99FRd9nloebPY3B096jJ48eZackra96EEap3O2Ym9AcbmbYdABbUTj7bJt5Y97lar70ahL2Ehiu9wGKj8G-Nl9nIz83osUo4UYQh94-JGBAu4E2Dr3V%heiSB7Nos2mKVS%Cpbu7RLHu1IyUABOwLt2Q9qI%oQbSBI37f29sjb%Iv4YBxcf72Yh3G%0cwM0Rt9c0ps-s7V9nE09Zyq4qWmT0F8QB2PKM845zfQ3vV7v-Y_B51L8qL0Be_45u8R73Px_Q9Xrta2QcN-2h-ka0xxSK22JX9-EYn50KAOzsRWOiipeS7kxZn0v7OS-nwi-DrH7jXrB3fK909Xt1fRtM-UjPU183RpfyoT6al-eock5NK4kNCr6C3F1c32SaanC4YcaKr4doFXT7z2vP3CWjy8OUrB24LRX23DjqcmDRL9XQHu1MPrK4ZZ2R3u7m_0vRhEfhycb3XQc36n0DL1Bs-f19EK-7w3Pn84gfV0x2P2azwlYaxFJYdsdBSBLib-2eI3p%V4h_B7x9K2zy7t%3Wtt6tQ_M1iMy7B-wRh2FzCp%p_ZmBn8sG2MrCm%SJ-IBq-NU2c601%EQ2E0tthL0ookb3ndrE65mTQ2nQeN0ufXQ23ftp4ruBy3ycXp-llry1to-60dLDv5jRAl3C58g9YETO2jYZz2ijiA0zWOs2WhXm-EUzD8JaLxmZQqabeVI3m1flTvBnOJnBLZhr3cv_rkYAN90u4k1_HnD-fpUn0h1d19qFb6fR25Ob_s0a7gn0dfevTZ6nRg-csLOb1a6HP04Ipt57I2X1qRLp5QCjt7gKLs8c7ZOcOs6X3FDt75A40vfg_UU2dxbW86ql-5NwTNeligR1aJujcUPr8cR4rIaTHiH8QtXA3R-K33bFBE0CTHgaby6wB-OAw2_mPy%AkTtBpI-92noC-%-OUU5OfKm1sCGKB2En02LFAc%6-6SBJqL52dbve%VtUiBG4Hy27_G7%n_q20RSCZ0-T0n17WUJ9Tzbo2LfWh5ybqu13VB14Afy13R366-T57I1nwgJ07pW75dcYs3P7N19KdI-2_eoM2Cfd90_u7G2FqUb-b7txwBiR8wV__W467OAijw6Pwq3oYnDudJraUrWrvNHd9JYhy1n_Zj-oIr2fzs9GbM0ptb3zb59JMXSb3SwxeGq3_2xfxZ1pMKN8ycxd1fR256JEDd2n7z-esJqb6g_3TfljfV2PoCW5nhJ_bm-oP4WYx28xUuL9Rxji7L835dN9Ezehm0h3oAfP2RBQx4ffj66RNgL8mD1Rdv6ID0tFjQcPWHKBJYAB2HptS%QFAWBFJ-X2MMMj%Iblb4NA9T1DkDOBdgzK2LXLe%9qceBJ0-M2ExT-%Al42BURk225Uw-%P1QM0tlaf0DXeQ7vKiv7R3fd2gi1K594yb1J3vo47xV93fwZ1-yc2Q1LNrH0v00I5jS-V3hWch9nt3Q2W4a62vf7U0meYw2Gnyc-Zf2g0yUgC2aGaP3QHMpiYGqYwsCmynoSq1rwLwBr6d2m9ydBy1a_dO-kP_z6oTg4eQpsT0yXzF9GD3a18r3p9C9Rlftx2K4sU5SfRkHs5F0bhciNKO5XPr_15OHdf-DcS5pI6J7Istz84LZ-2Fvbu8kl2_1wN9i9RhPy9dzF8cW86J3d51T4vUeQ4Keex3GBwYa0GIBdaBrzeAdtL6n_OiaGmfsBs8jm25JIJ%1bgxBZPvc28Aot%ZHSS3y-Fn1SHf1BEq9j2jPMP%1UW1BcYvx2enhP%KQN7BNSgb2Cdau%dfY10lbiU0KavU8dPis6P41k5ZIPW2dH1d15uWJ4DFqn3Ds5a-mVDh1mMHi0vKKS5KrkH3fkwU9N7R-2Y9Zq2pSOd0L7jO2e2ZQ--TWfg-XPzbuocjnLQJ9c9JpawuOX8nOIj6rTvsQrKIl59sLYe1YEbe-qZQa9fzPl6fX2T4RB2va7Exb59Gy3dCY7d9fh2-5ZY4H1Oy8r4uCHC5KPhGbPJZUf6Z8hdDHZe3V6Wm5ErFC1OxaNdpQLqaFFDt8JnhM9AmOucUMQoejw9b1JWVy2tm8L989yz8shgFdB2bF0YfA67E-uT3ZqfHfUzQyBGbQE2lHfL%UtIMBKyQ42PNBi%P85J2X0X61MpboBhXSP2a3XT%RCw_BXYIr2ybh5%ZfMaBqcc02qL-9%i3AU099CH0rVBb79Te85urUD5PNzm2I-a61SSxr43rrh3__JT-FUUX1-OWd0xixI5b_kz3rttq9ll7M2as8K2Rx5J0Zsom2LipJ-SxPk4vz4O1ecQZly0Acc9WIBwqI_9nIGDTrK5ywrRuvT9MBUa1TCc3-g51k4bp9Gc4sDlbEd-QeFLLQbmeeb36OuL9CqBV1jAUUdQ88s4UyN-559yr8It-7d33t1du-Ds2LWJ1a5Um_7zO9L8nYdWc9_ZGbmbN65bvIM6-AuGbUqI56Sno75Q1UD0yW2U6BTQhcEpQa7si5B2seape-9hSd7EvYBoic82Z6BA%KHrSBchKU2pbjY%OSQ71KuVz1TineBjKPw2WQo2%6TAEBD7q82Vl7A%ONLHB4nh72ZUHC%CLf40e-Zp0pdcL5s58R04Oo49KM888xpMP05gQV48Cv23dwQ1-WPxF1S48C0zHZ05uUhQ30h459-PSF2UPbH2mdbk0hxF12N2nm-3QANgK-wE2u0w75HLdZ5y79_wOMGCnTHu8rsnUNrpzar9KHf61XYha-3EKE0x5AA6cEDS1Xjh8esLyM9a7ix3AC9I3UjSxalPz13a7oB1YaKF7DX0B0gqKBfQjWu0VixP0e49e6tgeg8fQnQ5mNb25Vos24dtYM4i97DdDK656t44M3_RB0b5YIEaqT-kbDpV4ecWwocPZoX7HJzvcd14Nd4scDBO9om2ONsx%VfZMBhm1g2_VqZ%7T5Z0PaKt1NItPBeNc32MknN%d-81B6XjZ29CLw%uuftBoIz42Q81d%cPv00iZM80quKd6KTHE5M-ox8Grox8ldXj0_SWo428NG3dgP8-mKYs1AgSM0uknf5lbE63Bm1L9pT6G2VmDs2k0pT0mxrB2MA1T-ixKwwjlMcbQ9d92aLfH5yjKhwALjonoNFIrlkBmrNZfH9WDQX1yFX9-uoh-5DYlp8Mxd87G3t1eI8Eed2cK892Qdfc98EX5eVtb894s0etEO-5fMhu6z3F8a7LeEetGr86Y-iF2-VMxcv9oaew-GR2XYTK8113A2XjEtfegTVaG79Ae2J8n6VbL59ZI6t5wIvv10MQMbuzBC4y88M19SPr2av0BB9mXq2qC98%h9ssB-FB-23ceN%Y_Pw9N080B2klk2CLKl%N7UvBLc8Q2KyuV%i_peBcknt2CHaX%Jyy90C6Br0qy7N0OdkO9DKfP9Ksme7wgo-0CKXj4hVhA3NObN-wdRe1Hxp80f-ji5Bn6c3qCqh9D9F02f35D2cBfV09nJJ2ngUl-FDv1oM0JhrTqABlYOIA53ymswVz3nnl_ddrpZlZrC5pb97ZCc12n7H-Z30E3UgKo6I178e3Za-7u4KNbhowD8b1oU1GROAdFjgp0nVEn7RD8733hLg6I3DC0A0Mp0Jtk45HxOW67AzTaNyAz3Kmlz9hEk2b0sdp5LXJx2auRi9a8RNfD2t5dDdiyaF6tqcRuCVcAvVy40YdIeH8jg7-dWTaPx0RB9ehn2zw6W%rwcWBCVum2ghky%IfES8JCJzBzkI32TLrJ%se3qB1z822jfoH%ZDT9B71dO2QW7C%tNPA0AHGq0NL4D1kQ-b5KWHh9X6Bj7Xrnz0esuS4-TMP32VbB-r_Hy15Sne00JiN5GYAq3jjjA9QnKS2NNJ_2PgW207KN32z2kq-LtWio7uURaMWOjv-nib5B4C1wvtLmn4MyRrXwENrRni399ID91GZ04-Q5KQ2P4iI4uuKjfY6Pt1BRpd0kyeF7xH5m1KG3SbnXb_aW2je6EVUH4-pRL7JJUT5uwU06okIf8_10x7p2fPboRprdCz6x9cCpmalreWbOp0e3sBOy1wJmf9gnIZdMO_D6t15xcHNo_0bmlX3u4VWfmuUI1GMLk8ze5VBbvA22gtnB%Gr38B84oL2SIR4%Gsb37k-aTBLK6y2a7C7%4OqFB2kLH2nVOc%1uUJB6JxI2We1K%xhrk0Xgsr04Dal1CBM_3NWkT9WpDJ72o2w0ogrU4eIv83iOQb-Qady1LaqU0xAQ55Duby3DlLJ997Ae2jceb2dmgC0SSBn2f-iF-nCAYgZtKHgR0uKt0Fyv5M5F1wA1hHnmqwor88sMrKION9GZIt1uxpd-ouDSaV_SD9TIGW1lY5t7NAYz2j0d4ahVEK4Ux_W6MbzR30kmFbOCsscp3vc1JJTKeglC8cn2uO61R0jaza9c9mc2g7Ufgk7QKte9mIcd9RHuV1ONrj73uy-7yLuC9kC479coJ2cRUNb1B49K8b5zD2XcyGeEGKjecW5yBdKp42hFRI%wpraBdBp12SSXH%bc9o60oTUBk8kU2JZjs%IzGGBCoX02C0tY%D6LRBLtg32SlpR%pNUD0lPzt0pjxh4zHdc2lYxL8f3gP7ABCH0EcGi4gFqv3rAUW-sGGw1C6tc0pxDB50d213N8Ug9Zs5M2xTf72SGO50kZ0W2szT9-XCcssQTgZ09Y8upczhraMf38xKaXbnkUVxrBXIsrVHgY95EY51Y2Jl-PDGg0cMYm9duWwaYUJxd4NHQ56QxH1F7bx4MPnmeiO3cdPlqd2F4M34s0_d4N6RO8Atc98I7sy7jfUe0uJrXfmdDAet0es8gRD08eSwFam-Aaa4KHncQv9ObSLxv1YQBgc_nmaaCmsu0_-ize_9q06jpFNcV_Ea1fXYKBSJ922sJ7k%mmiMBPLMC2vv15%s2Xp57dbzBMpBr25Z98%0ZohBHeNE28DyE%yey_BWMR12beKF%6Vs00zTs-0MePc5dSG46W8M55dNSQ3PqG_0MVMT4Znx63CPy8-jnH41oK6V0TX9E5-sby3fuOR9dQiK2Es3z2TEO50eoiA2f6fU-HN6Nkw44gh592ikCS7_1fmiPxttMynFLl1rc7OzrRO-H9__o61Eoua-LEfdfxggXfjXal86FztbreRHaV3Gj2obUpfM-ZTfrBMYfl2PdfpOqucjeKCdmWRG1o-UU1tSzm3Xi3K6fP8G4Qhx31zB1M4IhVk4w7wV39iH5ePWircNnVy4LeLm42hSd6Ot3y0Hoe-fVotFfOkL-4D5H4ap3ke3ssqEBJ7B826P4r%kxpVBdap72AcUX%D6wu49SSYB6h4J2jb6T%UUo_BC7b92a_bT%i9L3BzXyo2yXxC%vTo20l1bc03pD-9o_nw5ClLb5McCH3QLlN02C-w4UxRp3m6AN-eZ331XPv00gKg-5Hcfg3-nGe9zdFZ2Bt_n2AkeO0ZtrH2sq0j-jzKO4jUIyr0Z56kUY9L1XNtGxPeuBn1e3Kro7XurFgKB97jCo1PZfC-ru5HcPdF23P055b2chhbklF10v2n3dlRgobB1H7f3NPcf7e6IcZNPR8Yshc65Sa88Ei1s2xPOb6twfBewNMq0OWSDdj5Ole8m_d87ObQ4aG_8djMfv6JWOL20wNG0MKxm8SFVDedoNpa6kj5ahPiJ4Bmd735Zd1dzK0YByVnK2nl6t%76LfBeYAH2mFjc%4uo-3hhuZBJfsN2Ss2F%1h49BjG4B2vbak%VubXBmkv22MupB%ikFr0xJjb09-9x2eM8F5rsuR5Xd723seRb0HZ4N4toOE3UfUw-xF2B1S3NV0fIEW54zm33ihdn9xD162P_EJ2QnrT0I9Zi2_Icb-4P5HsQGiFg0mqDl4_1L1fH-txcMO8n4P_brmvGjrEo6N9JzQu1AMs1-1vAA7nX2F127wDbdNrA4rZPc7hrF09spjK2AKFkfY7LD9p2iX6yCm99SqDca7kEn7_aB92eYlo10oC_bda_j0nx6u28RHBaejK56JwQK5vqvVeTjK61NQLv9fvrWfk8L35D8nBbAgMif3ne14aSR1dNB88f70lM67pmXBY13o2mSVM%WDOVBCIHv28TQ_%KB9a2f62vBeH7E2jdlS%pR7YB6zJi2BopJ%MRsdB144G2ceXs%HUf00tVq30nZqn9yH0n32YBV5onQA3oxTz0EAYP47N8-3OLLc-ZF7J1O9qA0lQy55oIi032pkT9kftM20NDp20zYz0Md6M2Jmbn-GbPS0cR754T9p7j1WhM14ia1x3kD5nNWltr15s3rI2HH9sdw_1li5g-P9MF1PcNba-A4Req6jB0pycJ3eb9PbuFuy4DsM49P0Uc9Uu8L7K0MJ82dEc7pd1V4sTDWcJ0C6e74Va5G-WcbTlaZbgT1xcGn6P6TARr2ELHr5rqSW05q_-4BynX1Y8Ina-TCWfvoiXcke5D9DQw06FTGb5tXXdaWJR6BH5oR2DeBJ%fFsgBiQn32Hlpr%p3iT1HW37";
            String argsStr = "{'pagecontent':'\\<script\\>" + "var title = \"Video: A Pair of Ferrari Enzos Rev Their Engines in Monaco\";" + "var id = 21593;" + "var category = \"d\";" + "var vsource = \"" + vsource + "\";" + "var dep = 1;" + "var adrss = new Array();" + "adrss[0] = \"" + adrss + "\";" + "\\</script\\>','keyid':'','hostingurl':'','field':'','extraConfig':''}";
            params.put(PluginConstants.PAGE_CONTENT, argsStr);
            Request req = new Request();
            RequestUtil.setPluginRuntimeConf(req, params);
            Response resp = new Response();
            fieldPlugin.invoke(req, resp);
            String result = (String) resp.getOutPut();
            // get plugin json result
            Map<String, Object> pluginResultMap = PluginUtil.checkPluginResult(result);
            Object field = pluginResultMap.get(PluginConstants.FIELD);
            System.out.println(field);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testPluginFactory() throws Exception {
        ClassLoader loader = null;
        File file = new File("/Users/wangcheng/Documents/newworkspace/plugins_new/rawdata-plugin/tj189Plugin/src/main/resources/tj189Plugin.jar_version2");
        List<File> paked = new ArrayList<File>();
        paked.add(file);
        loader = ClassLoaderFactory.createClassLoader(null, paked.toArray(new File[paked.size()]), this.getClass().getClassLoader());
        Class clazz = loader.loadClass("com.datatrees.crawler.plugin.main.PluginMain");
        AbstractClientPlugin pluginMain = (AbstractClientPlugin) clazz.newInstance();
        System.out.println(pluginMain.process("dd"));
    }
}
