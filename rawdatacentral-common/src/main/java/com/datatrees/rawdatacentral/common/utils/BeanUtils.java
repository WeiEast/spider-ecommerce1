package com.datatrees.rawdatacentral.common.utils;

import java.lang.reflect.Field;

import com.datatrees.spider.share.common.utils.CheckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * bean工具类
 * Created by zhouxinghai on 16/3/28.
 */
public class BeanUtils {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    /**
     * 将源数据复制到目标对象的父类上
     * @param source 源目标
     * @param dist   目标对象
     */
    public static <T> T copyToSuper(Object source, T dist) {
        CheckUtils.checkNotNull(source, "source is null");
        CheckUtils.checkNotNull(dist, "dist is null");
        try {
            Field[] sourceFields = source.getClass().getDeclaredFields();
            for (Field sourceField : sourceFields) {
                if (sourceField.getName().endsWith("serialVersionUID")) {
                    continue;
                }
                Field distField = dist.getClass().getSuperclass().getDeclaredField(sourceField.getName());
                boolean sourceFieldAccessible = sourceField.isAccessible();
                boolean distFieldAccessible = distField.isAccessible();
                if (!sourceFieldAccessible) {
                    sourceField.setAccessible(true);
                }
                if (!distFieldAccessible) {
                    distField.setAccessible(true);
                }
                distField.set(dist, sourceField.get(source));
                sourceField.setAccessible(sourceFieldAccessible);
                distField.setAccessible(distFieldAccessible);
            }
            return dist;
        } catch (NoSuchFieldException e) {
            logger.error("no such method", e);
            throw new RuntimeException("no such method");
        } catch (Exception e) {
            logger.error("error {}", e);
            throw new RuntimeException("copyToSuper error");
        }
    }

    /**
     * 将源数据复制到目标对象的上
     * @param source 源目标
     * @param dist   目标对象
     */
    public static <T> T copy(Object source, T dist) {
        CheckUtils.checkNotNull(source, "source is null");
        CheckUtils.checkNotNull(dist, "dist is null");
        try {
            Field[] sourceFields = source.getClass().getDeclaredFields();
            for (Field sourceField : sourceFields) {
                if (sourceField.getName().endsWith("serialVersionUID")) {
                    continue;
                }
                Field distField = dist.getClass().getDeclaredField(sourceField.getName());
                boolean sourceFieldAccessible = sourceField.isAccessible();
                boolean distFieldAccessible = distField.isAccessible();
                if (!sourceFieldAccessible) {
                    sourceField.setAccessible(true);
                }
                if (!distFieldAccessible) {
                    distField.setAccessible(true);
                }
                distField.set(dist, sourceField.get(source));
                sourceField.setAccessible(sourceFieldAccessible);
                distField.setAccessible(distFieldAccessible);
            }
            return dist;
        } catch (NoSuchFieldException e) {
            logger.error("no such method ", e);
            throw new RuntimeException("no such method");
        } catch (Exception e) {
            logger.error("error {}", e);
            throw new RuntimeException("copy error");
        }
    }

    public static String getString(Object source, String property, boolean isSuper) {
        CheckUtils.checkNotNull(source, "source is null");
        String value = null;
        try {
            Field sourceField = null;
            if (isSuper) {
                sourceField = source.getClass().getSuperclass().getDeclaredField(property);
            } else {
                sourceField = source.getClass().getDeclaredField(property);
            }
            boolean sourceFieldAccessible = sourceField.isAccessible();
            if (!sourceFieldAccessible) {
                sourceField.setAccessible(true);
            }
            Object o = sourceField.get(source);
            if (null != o) {
                value = String.valueOf(o);
            }
            sourceField.setAccessible(sourceFieldAccessible);
        } catch (NoSuchFieldException e) {
            logger.error("obj {} no such property {}", source, property, e);
            throw new RuntimeException("no such field " + property);
        } catch (Exception e) {
            logger.error("getString error source={},property={}", source, property, e);
            throw new RuntimeException("getString error property=" + property);
        }
        return value;
    }

    public static void setValue(Object source, String property, Object value, boolean isSuper) {
        CheckUtils.checkNotNull(source, "source is null");
        try {
            Field sourceField = null;
            if (isSuper) {
                sourceField = source.getClass().getSuperclass().getDeclaredField(property);
            } else {
                sourceField = source.getClass().getDeclaredField(property);
            }
            boolean sourceFieldAccessible = sourceField.isAccessible();
            if (!sourceFieldAccessible) {
                sourceField.setAccessible(true);
            }
            sourceField.set(source, value);
            sourceField.setAccessible(sourceFieldAccessible);
        } catch (NoSuchFieldException e) {
            logger.error("obj {} no such property {}", source, property, e);
            throw new RuntimeException("no such property:" + property);
        } catch (Exception e) {
            logger.error("error {}", e);
            throw new RuntimeException("getString error property:" + property);
        }
    }

}
