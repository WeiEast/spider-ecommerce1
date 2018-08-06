package com.datatrees.spider.share.service.extra;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.*;

import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.spider.share.service.constants.Constants;
import com.datatrees.spider.share.service.constants.SubmitConstant;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.domain.SubmitFile;
import com.datatrees.spider.share.service.oss.OssService;
import com.datatrees.spider.share.service.oss.OssServiceProvider;
import com.datatrees.spider.share.service.oss.OssUtils;
import com.datatrees.spider.share.service.util.ZipCompressUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadTask implements Runnable {

    private static final Logger         LOGGER = LoggerFactory.getLogger(UploadTask.class);

    private              ExtractMessage extractMessage;

    private              List<String>   fieldList;

    private              String         ossKey;

    public UploadTask(ExtractMessage extractMessage, List<String> fieldList, String ossKey) {
        this.extractMessage = extractMessage;
        this.fieldList = fieldList;
        this.ossKey = OssUtils.getObjectKey(ossKey);
        LOGGER.debug("OSS:ObjectKey: {}", this.ossKey);
    }

    @Override
    public void run() {
        LOGGER.info("start upload task! id={},taskId={}", extractMessage.getTaskLogId(), extractMessage.getTaskId());
        try {
            Map<String, SubmitFile> uploadMap = this.getSubmitFiles(extractMessage.getMessageObject());
            // after upload complete remove extract message object
            if (MapUtils.isNotEmpty(uploadMap)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipCompressUtils.compress(baos, uploadMap);
                OssService service = OssServiceProvider.getDefaultService();
                service.putObject(SubmitConstant.ALIYUN_OSS_DEFAULTBUCKET, this.ossKey, baos.toByteArray());
                LOGGER.debug("upload task completed! id: {}, ossKey: {}", extractMessage.getTaskLogId(), ossKey);
            } else {
                LOGGER.info("no need to upload file for message: {}", extractMessage);
            }
        } catch (Exception e) {
            LOGGER.error("upload task run failed! taskId:" + extractMessage.getTaskLogId(), e);
        }
    }

    private Map<String, SubmitFile> getSubmitFiles(Object result) {
        Map<String, SubmitFile> uploadFieldMap = new HashMap<>();
        if (result instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) result).entrySet()) {
                try {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null && (fieldList.contains(key) || key.endsWith("File"))) {
                        List<SubmitFile> fileBytesList = valueFormat(value);
                        this.setUploadFieldMap(uploadFieldMap, fileBytesList, entry.getKey());
                        // remove entry value from map
                        entry.setValue(null);
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        } else {
            LOGGER.warn("Error input only support map for upload.Input:" + result);
        }
        return uploadFieldMap;
    }

    private byte[] readFileBytes(FileWapper fileWapper) {
        byte[] fileBytes = null;
        if (fileWapper != null) {
            FileInputStream stream = null;
            try {
                stream = fileWapper.getFileInputStream();
                if (stream != null) {
                    fileBytes = IOUtils.toByteArray(stream); // remove file
                    fileWapper.remove();
                }
            } catch (Exception e) {
                LOGGER.error("read fileWapper error " + fileWapper, e);
            } finally {
                IOUtils.closeQuietly(stream);
            }
        }
        return fileBytes;
    }

    @SuppressWarnings("unused")
    private void setUploadFieldMap(Map<String, SubmitFile> uploadMap, List<SubmitFile> fileBytesList, String field) {
        if (CollectionUtils.isNotEmpty(fileBytesList)) {
            int size = fileBytesList.size();
            if (size == 1) {
                uploadMap.put(field, fileBytesList.get(0));
            } else {
                for (int i = 0; i < size; i++) {
                    uploadMap.put(field + (i + 1), fileBytesList.get(i));
                }
            }
        }

    }

    private List<SubmitFile> valueFormat(Object obj) throws Exception {
        List<SubmitFile> result = new ArrayList<>();
        if (obj instanceof String) {
            SubmitFile file = new SubmitFile(null, ((String) obj).getBytes(Constants.DEFAULT_ENCODE_CHARSETNAME));
            result.add(file);
        } else if (obj instanceof FileWapper) {
            FileWapper fileWapper = (FileWapper) obj;
            byte[] resultArray = readFileBytes(fileWapper);

            if (resultArray != null) {
                SubmitFile file = new SubmitFile(fileWapper.getName(), resultArray);
                result.add(file);
            }
        } else if (obj instanceof Collection) {
            for (Object sub : (Collection) obj) {
                result.addAll(valueFormat(sub));
            }
        } else {
            SubmitFile file = new SubmitFile(null, obj.toString().getBytes(Constants.DEFAULT_ENCODE_CHARSETNAME));
            result.add(file);
        }
        return result;
    }
}