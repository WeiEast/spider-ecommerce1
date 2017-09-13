package com.datatrees.rawdatacentral.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HessianUtils {

    private static final Logger logger = LoggerFactory.getLogger(HessianUtils.class);

    public static byte[] serialize(Object obj) {
        if (obj == null) return null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            HessianOutput ho = new HessianOutput(os);
            ho.writeObject(obj);
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
            HessianInput hi = new HessianInput(is);
            return hi.readObject();
        } catch (Exception e) {
            logger.error("deserialize error", e);
            return null;
        }
    }

}
