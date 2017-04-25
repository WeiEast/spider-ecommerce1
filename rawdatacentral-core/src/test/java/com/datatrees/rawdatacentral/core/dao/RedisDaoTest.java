package com.datatrees.rawdatacentral.core.dao;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.datatrees.rawdatacentral.core.AbstractTest;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.datatrees.common.util.GsonUtils;

public class RedisDaoTest extends AbstractTest {
    @Autowired
    private RedisDao redisDao;

    @Test
    public void doTest() {
        List<String> valueList = new ArrayList<String>();
        valueList.add("test");
        valueList.add("test1");
        valueList.add("test2");

        // String key = "test";
        // System.out.println(redisDao.save2List(key, valueList));
        redisDao.saveListString("test", valueList);
        System.out.println(redisDao.getRedisTemplate().opsForList().range("test", 0, -1));
        valueList = new ArrayList<String>();
        valueList.add("test");
        System.out.println(redisDao.saveListString("test", valueList));
        System.out.println(redisDao.getRedisTemplate().opsForList().range("test", 0, -1));

    }


    @Test
    public void pushTest() {
        boolean flag = redisDao.pushMessage("test", "message for test");
        System.out.println(flag);
        String result = redisDao.pullResult("test");
        System.out.println(result);

    }

    @Test
    public void pushTest1() {
        System.out.println(redisDao.getRedisTemplate().opsForList().range("rawdata_1000000146_545779_trades", 0, -1));

    }

    class Field {
        String title;
        String key;

        /**
         * @param title
         * @param key
         */
        public Field(String title, String key) {
            super();
            this.title = title;
            this.key = key;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title the title to set
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * @param key the key to set
         */
        public void setKey(String key) {
            this.key = key;
        }

    }



    @Test
    public void setToExcel() throws IOException {
        List<Field> excelList = new ArrayList<RedisDaoTest.Field>();
        excelList.add(new Field("平台名", "name"));
        excelList.add(new Field("标签", "tags"));
        excelList.add(new Field("主页", "website"));
        excelList.add(new Field("注册资本", "registeredCapital"));
        excelList.add(new Field("地区", "area"));
        excelList.add(new Field("上线时间", "createAt"));
        excelList.add(new Field("资金托管", "zjtg"));
        excelList.add(new Field("保障模式", "bzms"));
        excelList.add(new Field("担保机构", "dbjg"));
        excelList.add(new Field("业务类型", "busType"));
        excelList.add(new Field("从事贷款", "debit"));
        excelList.add(new Field("发展指数", "index"));
        excelList.add(new Field("综合排名", "rank"));
        excelList.add(new Field("联系地址", "address"));
        excelList.add(new Field("服务邮箱", "mail"));
        excelList.add(new Field("公司电话", "phone"));

        List<String> list = redisDao.getRedisTemplate().opsForList().range("rawdata_123212_549725_p2pData", 0, -1);
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("p2p网贷公司");
        String fileName = "p2p网贷公司";
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < excelList.size(); i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(excelList.get(i).title);
        }
        int rowNum = 1;
        for (String str : list) {
            Map data = (Map) GsonUtils.fromJson(str, Map.class);
            HSSFRow row = sheet.createRow(rowNum);
            for (int i = 0; i < excelList.size(); i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(data.get(excelList.get(i).key) + "");
            }
            rowNum++;
        }

        FileOutputStream os = new FileOutputStream(fileName + "_result.xls");
        wb.write(os);
        os.close();

    }

    @Test
    public void pushTest2() {
        String key = "verify_result_zj.189.cn_123";
        String value = "c6h4ra";
        redisDao.saveString2List(key, value);

    }


    @Test
    public void pushTest3() {
        String key = "verify_result_gd.10086.cn_v1_123";
        // String value =
        // "{\"websiteName\": \"gd.10086.cn_v1\",\"userId\": 123,\"status\": \"REFRESH_LOGIN_CODE\",\"body\":{\"username\":\"18219491713\",\"password\":\"44332211\",\"randomPassword\":\"\",\"code\":\"ygtk\"}}";
        String value = "{\"websiteName\": \"gd.10086.cn_v1\",\"userId\": 123,\"status\": \"SEND_CAPTCHA\",\"body\":{\"code\":\"27\"}}";
        // String value =
        // "{\"websiteName\": \"gd.10086.cn_v1\",\"userId\": 123,\"status\": \"START_LOGIN\",\"body\":{\"username\":\"18219491713\",\"password\":\"44332211\",\"randomPassword\":\"\",\"code\":\"6p8f\"}}";
        redisDao.saveString2List(key, value);
    }
}
