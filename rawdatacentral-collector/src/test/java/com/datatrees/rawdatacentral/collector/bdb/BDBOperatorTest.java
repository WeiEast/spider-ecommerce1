package com.datatrees.rawdatacentral.collector.bdb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.collector.bdb.operator.BDBOperator;
import org.junit.Before;
import org.junit.Test;

import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.classloader.ClassLoaderFactory;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午10:45:23
 */
public class BDBOperatorTest {

    BDBOperator bdb = null;

    @Before
    public void init() {
        try {
            bdb = new BDBOperator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addLinkTest() {
        LinkNode link = new LinkNode("http://tunemovie.so/movies/the-grand-budapest-hotel-11529.html");
        link.setDepth(3);
        LinkNode link2 = new LinkNode("http://tunemovie.so/cinema-movies.html");
        link2.setDepth(1);
        Map header = new HashMap();
        header.put("cookie", "ewnfiownefiowenfioewiofnil");
        header.put("3f32", 2);
        link2.addHeaders(header);
        System.out.println(bdb.getQueueSize());
        bdb.addLink(link);
        bdb.addLink(link2);
        System.out.println(bdb.getQueueSize());

        LinkedList<LinkNode> linkNodeList = bdb.fetchNewLinks(2);
        for (LinkNode node : linkNodeList) {
            System.out.println(node);
            System.out.println(node.getHeaders());

        }
    }

    @Test
    public void fetchNewLinks() {
        // query test
        bdb.setCurrentId(0);
        LinkedList<LinkNode> linkNodeList = bdb.fetchNewLinks(2);
        for (LinkNode node : linkNodeList) {
            System.out.println(node.getUrl());
        }
    }

    @Test
    public void testPluginFactory() throws Exception {
        ClassLoader loader = null;
        for (int i = 0; i < 3; i++) {
            File file = new File(
                    "/Users/wangcheng/Documents/newworkspace/plugins_new/rawdatacentral-plugin/tj189Plugin/target/plugin-tj189Plugin-1.0.10-SNAPSHOT.jar");
            List<File> paked = new ArrayList<File>();
            paked.add(file);
            loader = ClassLoaderFactory.createClassLoader(null, paked.toArray(new File[paked.size()]), this.getClass().getClassLoader());
            Class clazz = loader.loadClass("com.datatrees.crawler.plugin.main.PluginMain");
            AbstractClientPlugin pluginMain = (AbstractClientPlugin) clazz.newInstance();
            System.out.println(pluginMain.process("dd"));

            file = new File(
                    "/Users/wangcheng/Documents/newworkspace/plugins_new/rawdatacentral-plugin/tj189Plugin/src/main/resources/tj189Plugin.jar_version2");
            paked = new ArrayList<File>();
            paked.add(file);
            loader = ClassLoaderFactory.createClassLoader(null, paked.toArray(new File[paked.size()]), this.getClass().getClassLoader());
            clazz = loader.loadClass("com.datatrees.crawler.plugin.main.PluginMain");
            pluginMain = (AbstractClientPlugin) clazz.newInstance();
            System.out.println(pluginMain.process("ddewfwe"));
            Thread.sleep(5000);
        }

        File file = new File("/Users/wangcheng/Documents/newworkspace/plugins_new/rawdatacentral-plugin/tj189Plugin/plugin-tj189Plugin.jar3");
        List<File> paked = new ArrayList<File>();
        paked.add(file);
        loader = ClassLoaderFactory.createClassLoader(null, paked.toArray(new File[paked.size()]), this.getClass().getClassLoader());
        Class clazz = loader.loadClass("com.datatrees.crawler.plugin.main.PluginMain");
        AbstractClientPlugin pluginMain = (AbstractClientPlugin) clazz.newInstance();
        System.out.println(pluginMain.process("dd"));
        System.out.println("waitng for jar replace...");

        Thread.sleep(15000);

        file = new File("/Users/wangcheng/Documents/newworkspace/plugins_new/rawdatacentral-plugin/tj189Plugin/target/plugin-tj189Plugin-1.0.10-SNAPSHOT.jar");
        paked = new ArrayList<File>();
        paked.add(file);
        loader = ClassLoaderFactory.createClassLoader(null, paked.toArray(new File[paked.size()]), this.getClass().getClassLoader());
        clazz = loader.loadClass("com.datatrees.crawler.plugin.main.PluginMain");
        pluginMain = (AbstractClientPlugin) clazz.newInstance();
        System.out.println(pluginMain.process("dd"));
    }
}
