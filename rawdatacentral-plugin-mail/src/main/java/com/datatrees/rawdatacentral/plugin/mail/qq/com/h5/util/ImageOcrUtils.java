package com.datatrees.rawdatacentral.plugin.mail.qq.com.h5.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.plugin.mail.qq.com.h5.util.domain.Color;
import com.datatrees.rawdatacentral.plugin.mail.qq.com.h5.util.domain.ColorPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageOcrUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImageOcrUtils.class);

    public static ColorPoint ocr(byte[] fullImageDate, byte[] bagImageDate, byte[] searchImageDate, int y) {
        try {
            long start = System.currentTimeMillis();
            BufferedImage searchImage = ImageIO.read(new ByteArrayInputStream(searchImageDate));
            Color[][] searchRgbs = ImageUtils.getRgb(searchImage);
            Map<String, Object> config = getConfig(25, 75, 25, 75, 10);
            ColorPoint searchColorPoint = ColorPointBuilder.getSearchColorPoint(searchRgbs, JSON.toJSONString(config));
            logger.info("hello width={},height={}", searchRgbs.length, searchRgbs[1].length);
            searchRgbs = null;
            searchImageDate = null;

            BufferedImage fullImage = ImageIO.read(new ByteArrayInputStream(fullImageDate));
            Color[][] fullRgbs = ImageUtils.getRgb(fullImage);
            int with = fullRgbs.length - searchColorPoint.getRightPotion();
            int count1 = 0;
            int count2 = 0;
            int precision = 150;
            int root_precision = 60;
            ColorPoint best = null;
            int min = Integer.MAX_VALUE;
            for (int x = searchColorPoint.getLeftPotion() + 1; x <= with; x++) {
                ColorPoint root = ColorPointBuilder.getColorPoint(fullRgbs, x, y);
                if (searchColorPoint.getColor().isNear(root.getColor(), root_precision)) {
                    count1++;
                    ColorPointBuilder.fillChildren(fullRgbs, root, searchColorPoint.getChildren());
                    if (!searchColorPoint.isNear(root, precision)) {
                        continue;
                    }
                    count2++;
                    int value = searchColorPoint.gap(root);
                    //logger.info("x={},y={},color={}", x, y, root.getColor().toString());
                    if (value < min) {
                        best = root;
                        min = value;
                    }
                }
            }
            if (null == best) {
                return null;
            }
            logger.info("find,x={},y={},value={},mix={},us={},pt={},count={},{},searchColor={}", best.getAbsoluteX(), best.getAbsoluteY(),
                    best.getColor(), min, System.currentTimeMillis() - start, searchColorPoint.getChildren().size(), count1, count2,
                    searchColorPoint.getColor());
            return best;
        } catch (Exception e) {
            logger.error("orc error", e);
            return null;
        }

    }

    public static Map<String, Object> getConfig(int minX, int maxX, int minY, int maxY, int interval) {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> root = new HashMap<>();
        root.put("percentageX", new BigDecimal(50).setScale(2).divide(new BigDecimal(100)));
        root.put("percentageY", new BigDecimal(50).setScale(2).divide(new BigDecimal(100)));
        config.put("root", root);
        List<Map<String, Object>> children = new ArrayList<>();
        for (int x = minX; x <= maxX; x += interval) {
            for (int y = minY; y <= maxY; y += interval) {
                Map<String, Object> map = new HashMap<>();
                map.put("percentageX", new BigDecimal(x).setScale(2).divide(new BigDecimal(100)));
                map.put("percentageY", new BigDecimal(y).setScale(2).divide(new BigDecimal(100)));
                children.add(map);
            }
        }
        config.put("children", children);
        //System.out.println(JSON.toJSONString(children.size()));
        //System.out.println(JSON.toJSONString(children));
        return config;
    }

}
