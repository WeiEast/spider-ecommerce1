package com.datatrees.rawdatacentral.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializeUtils {

    private static final Logger logger = LoggerFactory.getLogger(SerializeUtils.class);

    public static byte[] serialize(Object obj) {
        if (obj == null) return null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(obj);
            return os.toByteArray();
        } catch (Exception e) {
            logger.error("serialize error", e);
            return null;
        }
    }

    public static Object deserialize(byte[] by) {
        if (by == null) throw new NullPointerException();
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(by);
            ObjectInputStream in = new ObjectInputStream(is);
            return in.readObject();
        } catch (Exception e) {
            logger.error("deserialize error", e);
            return null;
        }
    }
}
