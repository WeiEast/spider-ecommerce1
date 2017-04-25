package com.datatrees.rawdatacentral.submitter.filestore;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.submitter.common.SubmitConstant;
import com.datatrees.rawdatacentral.submitter.common.ZipCompressUtils;
import com.datatrees.rawdatacentral.submitter.filestore.oss.OssService;
import com.datatrees.rawdatacentral.submitter.filestore.oss.OssServiceProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.rawdatacentral.core.common.Constants;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.submitter.common.SubmitFile;

public class UploadTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(UploadTask.class);

    private ExtractMessage extractMessage;
    private List<String> fieldList;
    private String ossKey;

    public UploadTask(ExtractMessage extractMessage, List<String> fieldList, String ossKey) {
        this.extractMessage = extractMessage;
        this.fieldList = fieldList;
        this.ossKey = ossKey;
    }

    @Override
    public void run() {
        logger.debug("start upload task! id: " + extractMessage.getTaskId());
        try {
            Map<String, SubmitFile> uploadMap = this.getSubmitFiles(extractMessage.getMessageObject());
            // after upload complete remove extractmessage object
            if (MapUtils.isNotEmpty(uploadMap)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipCompressUtils.compress(baos, uploadMap);
                OssService service = OssServiceProvider.getDefaultService();
                service.putObject(SubmitConstant.ALIYUN_OSS_DEFAULTBUCKET, this.ossKey, baos.toByteArray());
                logger.debug("upload task completed! id: " + extractMessage.getTaskId() + ",osskey:" + ossKey);
            } else {
                logger.info("no need to upload file for message:" + extractMessage);
            }
        } catch (Exception e) {
            logger.error("upload task run failed! taskId:" + extractMessage.getTaskId(), e);
        }
    }

    private Map<String, SubmitFile> getSubmitFiles(Object result) {
        Map<String, SubmitFile> uploadFieldMap = new HashMap<String, SubmitFile>();
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
                    logger.error(e.getMessage(), e);
                }
            }
        } else {
            logger.warn("Error inpurt only support map for upload.Input:" + result);
        }
        return uploadFieldMap;
    }

    private byte[] readFileBytes(FileWapper inputObject) {
        byte[] fileBytes = null;
        if (inputObject != null) {
            FileWapper fileWapper = (FileWapper) inputObject;
            FileInputStream stream = null;
            try {
                stream = fileWapper.getFileInputStream();
                if (stream != null) {
                    fileBytes = IOUtils.toByteArray(stream); // remove file
                    fileWapper.remove();
                }
            } catch (Exception e) {
                logger.error("read fileWapper error " + inputObject, e);
            } finally {
                IOUtils.closeQuietly(stream);
            }
        }
        return fileBytes;
    }

    @SuppressWarnings("unused")
    private void setUploadFieldMap(Map<String, SubmitFile> uploadMap, List<SubmitFile> fileBytesList, String field) {
        if (CollectionUtils.isNotEmpty(fileBytesList) && fileBytesList.size() > 1) {
            int i = 1;
            for (SubmitFile file : fileBytesList) {
                uploadMap.put(field + i, file);
                i++;
            }
        } else if (CollectionUtils.isNotEmpty(fileBytesList) && fileBytesList.size() == 1) {
            uploadMap.put(field, fileBytesList.get(0));
        }
    }

    private List<SubmitFile> valueFormat(Object obj) throws Exception {
        List<SubmitFile> result = new ArrayList<SubmitFile>();
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
