package com.datatrees.spider.share.service.plugin.operator.china_10086_app.utils;

import java.util.HashMap;

/**
 * 中国移动app部分代码对应关系参考
 * Created by guimeichao on 18/3/28.
 */
public class DefaultData {

    private static HashMap<String, String> payType  = null;

    private static HashMap<String, String> payType1 = null;

    public static HashMap<String, String> getRealNameRegType() {
        HashMap<String, String> realNameType = new HashMap();
        realNameType.put("N", "未登记");
        realNameType.put("W", "登记中");
        realNameType.put("P", "登记通过");
        realNameType.put("F", "登记失败");
        realNameType.put("O", "登记失败");
        return realNameType;
    }

    public static HashMap<String, String> getUserType() {
        HashMap<String, String> userTypeMessage = new HashMap();
        userTypeMessage.put("01", "全球通");
        userTypeMessage.put("02", "神州行");
        userTypeMessage.put("02", "动感地带");
        userTypeMessage.put("09", "其它品牌");
        return userTypeMessage;
    }

    public static HashMap<String, String> getUtil() {
        HashMap<String, String> userTypeMessage = new HashMap();
        userTypeMessage.put("01", "分钟");
        userTypeMessage.put("02", "条");
        userTypeMessage.put("03", "KB");
        userTypeMessage.put("04", "MB");
        userTypeMessage.put("05", "GB");
        userTypeMessage.put("97", "TB");
        userTypeMessage.put("96", "PB");
        userTypeMessage.put("11", "小时");
        userTypeMessage.put("Z", "");
        return userTypeMessage;
    }

    public static HashMap<String, String> getMealName() {
        HashMap<String, String> userTypeMessage = new HashMap();
        userTypeMessage.put("01", "语音");
        userTypeMessage.put("02", "短信");
        userTypeMessage.put("03", "彩信");
        userTypeMessage.put("04", "流量");
        userTypeMessage.put("05", "WLAN");
        return userTypeMessage;
    }

    public static HashMap<String, String> getUserLevel_VIP() {
        HashMap<String, String> userTypeMessage = new HashMap();
        userTypeMessage.put("00", "普通用户");
        userTypeMessage.put("01", "VIP用户");
        return userTypeMessage;
    }

    public static HashMap<String, String> getOpType() {
        HashMap<String, String> userTypeMessage = new HashMap();
        userTypeMessage.put("0", "积分兑换");
        userTypeMessage.put("1", "积分转赠转入");
        userTypeMessage.put("2", "积分转赠转出");
        userTypeMessage.put("3", "积分捐赠转入");
        userTypeMessage.put("4", "积分捐赠转出");
        userTypeMessage.put("5", "转品牌清零");
        userTypeMessage.put("6", "积分罚奖");
        userTypeMessage.put("7", "积分生成");
        return userTypeMessage;
    }

    public static HashMap<String, String> getPonitType() {
        HashMap<String, String> userTypeMessage = new HashMap();
        userTypeMessage.put("0", "消费积分");
        userTypeMessage.put("1", "奖励积分");
        return userTypeMessage;
    }

    public static HashMap<String, String> getPersionInfo() {
        HashMap<String, String> persionMessage = new HashMap();
        persionMessage.put("2", "归  属  地:");
        persionMessage.put("3", "客户姓名:");
        persionMessage.put("8", "用户品牌:");
        persionMessage.put("12", "用户等级:");
        persionMessage.put("14", "客户等级:");
        persionMessage.put("16", "邮政编码:");
        persionMessage.put("17", "联系地址:");
        persionMessage.put("18", "电子邮箱:");
        persionMessage.put("19", "联系电话:");
        persionMessage.put("20", "入网时间:");
        persionMessage.put("36", "VIP等级:");
        persionMessage.put("37", "VIP卡号:");
        persionMessage.put("38", "机场贵宾区服务免费次数:");
        return persionMessage;
    }

    public static HashMap<String, String> getHttpCode() {
        HashMap<String, String> httpmessage = new HashMap();
        httpmessage.put("200", "请求服务器成功，返回正常数据");
        httpmessage.put("220", "对不起，由于系统或网络原因，当前操作暂不可用，对您造成的不便敬请谅解。");
        httpmessage.put("1", "对不起，由于系统或网络原因，当前操作暂不可用，对您造成的不便敬请谅解。");
        httpmessage.put("2", "调用接口入参有误");
        httpmessage.put("3", "接口内部内存出错");
        httpmessage.put("4", "http请求服务器超时");
        httpmessage.put("5", "服务器返回其他错误");
        httpmessage.put("6", "读取XML文件错误或者读取文件为空");
        httpmessage.put("7", "读取本地文件失败");
        httpmessage.put("8", "写本地文件失败");
        httpmessage.put("9", "调用接口被终止，快速返回");
        return httpmessage;
    }

    public static HashMap<String, String> getAccounts() {
        HashMap<String, String> accounts = new HashMap();
        accounts.put("01", "固定费用");
        accounts.put("02", "语音通信费");
        accounts.put("03", "上网费");
        accounts.put("04", "短彩信");
        accounts.put("05", "增值业务费");
        accounts.put("06", "代收费");
        accounts.put("09", "其他费用");
        accounts.put("11", "业务费用减免");
        accounts.put("12", "本月通信费优惠");
        accounts.put("13", "单位已代付");
        return accounts;
    }

    public static HashMap<String, String> getPayType() {
        if (payType != null) {
            synchronized (DefaultData.class) {
                if (payType != null) {
                    HashMap<String, String> hashMap = payType;
                    return hashMap;
                }
            }
        }
        payType = new HashMap();
        payType.put("01", "营业厅");
        payType.put("02", "网上营业厅");
        payType.put("03", "掌上营业厅");
        payType.put("04", "短信营业厅");
        payType.put("05", "手机营业厅");
        payType.put("06", "自助终端");
        payType.put("07", "银行");
        payType.put("08", "空中充值");
        payType.put("09", "移动商城");
        payType.put("99", "其他");
        return payType;
    }

    public static HashMap<String, String> getPayType1() {
        if (payType1 != null) {
            synchronized (DefaultData.class) {
                if (payType1 != null) {
                    HashMap<String, String> hashMap = payType1;
                    return hashMap;
                }
            }
        }
        payType1 = new HashMap();
        payType1.put("01", "现金交费");
        payType1.put("02", "充值卡充值");
        payType1.put("03", "银行托收");
        payType1.put("04", "营销活动预存受理");
        payType1.put("05", "积分换话费业务受理");
        payType1.put("06", "第三方支付");
        payType1.put("07", "手机钱包");
        payType1.put("08", "空中充值");
        payType1.put("09", "代理商渠道办理");
        payType1.put("10", "批量冲销");
        payType1.put("11", "调帐");
        payType1.put("12", "其他");
        return payType1;
    }

    public static HashMap<String, String> getCityString() {
        HashMap<String, String> cityMessage = new HashMap();
        cityMessage.put("", "");
        return cityMessage;
    }

    public static HashMap<String, String> getProvinceString() {
        return new HashMap();
    }

    public static HashMap<String, String> getMessageforUser() {
        HashMap<String, String> messageForUser = new HashMap();
        messageForUser.put("MSG5001", "尊敬的用户，您好，本软件为免费下载，在中国移动网络接入产生的数据流量，短信或彩信通信费，按现有标准执行");
        messageForUser.put("MSG5002", "请检查您的网络连接或网络设置");
        messageForUser.put("MSG5003", "检测到有新版本，当前版本仍可用，是否立即更新？");
        messageForUser.put("MSG5004", "检测到有新版本，建议您更新");
        messageForUser.put("MSG5005", "请输入11位移动手机号码");
        messageForUser.put("MSG5006", "对不起，您输入的号码不是移动手机号码");
        messageForUser.put("MSG5007", "对不起，请输入正确的服务密码，谢谢");
        messageForUser.put("MSG5008", "手机号码或服务密码不正确，请检查后再试");
        messageForUser.put("MSG5009", "尊敬的用户，您好，您已连续2次服务密码有误，为了安全，3次错误将锁定本号码。建议您拨打10086重置您的服务密码，或者通过短信验证码登录");
        messageForUser.put("MSG5010", "尊敬的用户，您好，您今天已经连续三次输入密码有误，导致账号锁定，您可以通过短信验证码登录");
        messageForUser.put("MSG5011", "尊敬的用户，您好，您可以拨打10086，根据语音提示进行密码重置，谢谢");
        messageForUser.put("MSG5012", "请您输入正确11位手机号码");
        messageForUser.put("MSG5013", "邮寄地址不得超过50个字，请您重新输入");
        messageForUser.put("MSG5014", "请您输入正确6位邮政编码");
        messageForUser.put("MSG5015", "Email邮箱不得超过20个字，请您重新输入");
        messageForUser.put("MSG5016", "您已经成功更新个人信息");
        messageForUser.put("MSG5017", "对不起，原服务密码不正确");
        messageForUser.put("MSG5018", "对不起，新密码应为6位或8位");
        messageForUser.put("MSG5019", "对不起,两次输入的新密码不一致，请您重新输入，谢谢");
        messageForUser.put("MSG5020", "尊敬的用户，您好，每月1号到3号是系统账单日，暂时无法查询，给您带来的不便，敬请谅解");
        messageForUser.put("MSG5021", "尊敬的用户，您好，当前月份无历史账单");
        messageForUser.put("MSG5022", "尊敬的用户，您好，您当前无交费历史");
        messageForUser.put("MSG5023", "尊敬的用户，您好，您暂未订购套餐。推荐您开通WLAN套餐");
        messageForUser.put("MSG5024", "尊敬的用户，您好，您暂无订购业务");
        messageForUser.put("MSG5025", "尊敬的用户，您好，您确定要退订*业务吗？");
        messageForUser.put("MSG5026", "尊敬的用户，您好，您的*退订已成功受理，您可以稍后刷新查看");
        messageForUser.put("MSG5027", "尊敬的用户，您好，@业务退订失败。原因：*");
        messageForUser.put("MSG5028", "尊敬的用户，您确定要办理*业务吗？");
        messageForUser.put("MSG5029", "您的*业务已经成功受理，部分业务会再给您下发二次确认的短信，请注意查收并进行回复确认，您也可以通过已订业务查询办理状态");
        messageForUser.put("MSG5030", "抱歉，当前手机号码*不能充值，详情请致电10086或前往当地移动营业厅查询");
        messageForUser.put("MSG5031", "尊敬的用户您好，为保证您的账号安全，会话超时请您重新登录！");
        messageForUser.put("MSG5032", "尊敬的用户，您确定清除所有的缓存数据吗？");
        messageForUser.put("MSG5033", "尊敬的用户，关闭通知后将无法收到主动为您推送的月末套餐余量提醒信息，您确定要关闭吗？");
        messageForUser.put("MSG5034", "尊敬的用户，您好，您现在是登录状态，确定要更换其他账号登录吗？");
        messageForUser.put("MSG5035", "尊敬的用户，您好，您的意见已提交成功。我们将尽快回复，感谢您的支持！");
        messageForUser.put("MSG5036", "正在建设,敬请期待!");
        messageForUser.put("MSG5037", "对不起，原密码应为6位或8位，谢谢");
        messageForUser.put("MSG5038", "对不起，新密码应和原密码位数保持一致，谢谢");
        messageForUser.put("MSG5039", "尊敬的用户，您好，为了账户安全，不建议设置简单重复的密码");
        messageForUser.put("MSG5040", "尊敬的用户，您好，当前版本为最新，感谢您的关注");
        messageForUser.put("MSG5041", "对不起，您的网络连接存在问题，建议您查看网络设置，谢谢");
        messageForUser.put("MSG5042", "尊敬的用户,您确定呼叫10086客服吗?");
        messageForUser.put("MSG5043", "收藏成功");
        messageForUser.put("MSG5044", "已取消收藏");
        messageForUser.put("MSG5045", "当前无收藏");
        messageForUser.put("MSG5046", "尊敬的用户，您好，关闭通知后将无法收到主动为您推送的优惠信息，您确定要关闭吗?");
        messageForUser.put("MSG5047", "尊敬的用户，您好，您确认要退出吗？");
        messageForUser.put("MSG5048", "尊敬的用户，您好，您已超过30分钟未进行任何操作，为保证账号安全，建议您重新登录");
        messageForUser.put("MSG5049", "对不起，当前时间段无效，请重新设置。");
        messageForUser.put("MSG5050", "已有收藏记录");
        messageForUser.put("MSG5051", "对不起，流水号获取失败，请您重新获取。");
        messageForUser.put("MSG5052", "尊敬的用户，您好，@业务办理失败。原因：*");
        messageForUser.put("MSG5053", "尊敬的用户，您好，您的意见未发送成功，请稍后再试");
        messageForUser.put("MSG5054", "您确认要开启记住密码和自动登录功能吗？这将会导致其他人使用您的手机开启本应用时自动登录到手机营业厅");
        messageForUser.put("MSG5055", "请输入18位充值卡密码");
        messageForUser.put("MSG5056", "尊敬的用户，您好，该功能目前只支持安卓2.2以上系统");
        messageForUser.put("MSG5057", "充值卡充值业务会有10分钟左右的延时，请您耐心等待。充值成功后，会给被充值手机号下发充值成功短信。如果充值不成功，建议您拨打13800138000进行语音充值");
        messageForUser.put("MSG5058", "尊敬的用户，您好，暂无数据信息!");
        messageForUser.put("MSG5059", "对不起，您的品牌不支持登录");
        messageForUser.put("MSG5060", "对不起，您的随机短信密码获取太过频繁，请1小时后再次获取");
        messageForUser.put("MSG5061", "对不起，您输入的随机短信密码为空，请检查后重新输入");
        messageForUser.put("MSG5062", "您确定要删除吗？");
        messageForUser.put("MSG5063", "请上传.jpg或.png格式的图片");
        messageForUser.put("MSG5064", "请上传大小为2M以内的图片");
        messageForUser.put("MSG5065", "对不起，新密码不能和原密码一致");
        messageForUser.put("MSG5066", "图片上传失败，请重新上传");
        messageForUser.put("MSG5067", "清除成功");
        messageForUser.put("MSG5068", "尊敬的用户，您好，受理结果会以短信形式下发至您的手机上，您也可通过已定业务查询来确认变更套餐信息");
        return messageForUser;
    }

    public static HashMap<String, String> gethallType() {
        HashMap<String, String> halltypeMessage = new HashMap();
        halltypeMessage.put("0", "自营厅");
        halltypeMessage.put("1", "合作厅");
        return halltypeMessage;
    }

    public static HashMap<String, String> getWifiType() {
        HashMap<String, String> wifitypeMessage = new HashMap();
        wifitypeMessage.put("1", "高校、中职");
        wifitypeMessage.put("2", "医院");
        wifitypeMessage.put("3", "机场");
        wifitypeMessage.put("4", "火车站、汽车站");
        wifitypeMessage.put("5", "自有营业厅");
        wifitypeMessage.put("6", "行业服务窗口");
        wifitypeMessage.put("7", "大卖场");
        wifitypeMessage.put("8", "电脑城");
        wifitypeMessage.put("9", "小商品批发中心");
        wifitypeMessage.put("10", "垂直行业");
        wifitypeMessage.put("11", "政府机关");
        wifitypeMessage.put("12", "中小企业等集团客户点");
        wifitypeMessage.put("13", "咖啡馆");
        wifitypeMessage.put("14", "连锁酒店");
        wifitypeMessage.put("15", "住宅小区");
        wifitypeMessage.put("16", "乡镇农村");
        wifitypeMessage.put("17", "蚁居/城中村等居民点");
        wifitypeMessage.put("18", "大型会展中心");
        wifitypeMessage.put("19", "商务写字楼");
        wifitypeMessage.put("20", "商业街区");
        wifitypeMessage.put("21", "地铁");
        wifitypeMessage.put("22", "产业园区");
        wifitypeMessage.put("23", "旅游景点 ");
        wifitypeMessage.put("99", "其它");
        return wifitypeMessage;
    }

    public static HashMap<String, String> getUserLevel() {
        HashMap<String, String> userLevelMessage = new HashMap();
        userLevelMessage.put("000", "保留");
        userLevelMessage.put("100", "普通客户");
        userLevelMessage.put("200", "重要客户");
        userLevelMessage.put("201", "党政机关客户");
        userLevelMessage.put("202", "军、警、安全机关客户");
        userLevelMessage.put("203", "联通合作伙伴客户");
        userLevelMessage.put("204", "英雄、模范、名星类客户");
        userLevelMessage.put("300", "普通大客户");
        userLevelMessage.put("301", "钻石卡大客户");
        userLevelMessage.put("302", "金卡大客户");
        userLevelMessage.put("303", "银卡大客户");
        userLevelMessage.put("304", "贵宾卡大客户");
        return userLevelMessage;
    }

    public static HashMap<String, String> getPaymentType() {
        HashMap<String, String> paymentTypeMessage = new HashMap();
        paymentTypeMessage.put("01", "预付费用户");
        paymentTypeMessage.put("02", "后付费用户");
        return paymentTypeMessage;
    }

    public static HashMap<String, String> getEffectiveWay() {
        HashMap<String, String> effectiveWayMessage = new HashMap();
        effectiveWayMessage.put("01", "立即生效收取半月月租");
        effectiveWayMessage.put("02", "立即生效收取全月月租");
        effectiveWayMessage.put("03", "立即生效套餐包外资费");
        effectiveWayMessage.put("04", "下账单生效");
        return effectiveWayMessage;
    }

    public static HashMap<String, String> getVipGrade() {
        HashMap<String, String> vipGrade = new HashMap();
        vipGrade.put("00", "钻卡");
        vipGrade.put("01", "金卡");
        vipGrade.put("02", "银卡");
        return vipGrade;
    }

    public static HashMap<String, String> getSinaWeiBoMessage() {
        HashMap<String, String> message = new HashMap();
        message.put("01", "请先Oauth认证");
        message.put("02", "分享失败");
        message.put("03", "分享成功");
        message.put("04", "请输入分享内容");
        return message;
    }

    public static HashMap<Integer, String> getActionID() {
        HashMap<Integer, String> effectiveWayMessage = new HashMap();
        effectiveWayMessage.put(Integer.valueOf(1), "1001");
        effectiveWayMessage.put(Integer.valueOf(2), "2001");
        effectiveWayMessage.put(Integer.valueOf(2091), "2001");
        effectiveWayMessage.put(Integer.valueOf(3), "8001");
        effectiveWayMessage.put(Integer.valueOf(4), "8003,8004");
        effectiveWayMessage.put(Integer.valueOf(5), "8002");
        effectiveWayMessage.put(Integer.valueOf(7), "3001");
        effectiveWayMessage.put(Integer.valueOf(701), "3001");
        effectiveWayMessage.put(Integer.valueOf(8), "3002");
        effectiveWayMessage.put(Integer.valueOf(801), "3002");
        effectiveWayMessage.put(Integer.valueOf(11), "3003");
        effectiveWayMessage.put(Integer.valueOf(1011), "3003");
        effectiveWayMessage.put(Integer.valueOf(13), "3004");
        effectiveWayMessage.put(Integer.valueOf(1301), "3004");
        effectiveWayMessage.put(Integer.valueOf(14), "3005");
        effectiveWayMessage.put(Integer.valueOf(1401), "3005");
        effectiveWayMessage.put(Integer.valueOf(15), "9001");
        effectiveWayMessage.put(Integer.valueOf(16), "9002");
        effectiveWayMessage.put(Integer.valueOf(20), "4003");
        effectiveWayMessage.put(Integer.valueOf(2001), "4003");
        effectiveWayMessage.put(Integer.valueOf(23), "4004");
        effectiveWayMessage.put(Integer.valueOf(2301), "4004");
        effectiveWayMessage.put(Integer.valueOf(25), "4005");
        effectiveWayMessage.put(Integer.valueOf(2501), "4005");
        effectiveWayMessage.put(Integer.valueOf(26), "7001");
        effectiveWayMessage.put(Integer.valueOf(27), "3006");
        effectiveWayMessage.put(Integer.valueOf(2701), "3006");
        effectiveWayMessage.put(Integer.valueOf(28), "3007");
        effectiveWayMessage.put(Integer.valueOf(29), "8008");
        effectiveWayMessage.put(Integer.valueOf(30), "4001");
        effectiveWayMessage.put(Integer.valueOf(31), "6001");
        effectiveWayMessage.put(Integer.valueOf(32), "6002");
        effectiveWayMessage.put(Integer.valueOf(33), "9003");
        effectiveWayMessage.put(Integer.valueOf(34), "4002");
        effectiveWayMessage.put(Integer.valueOf(35), "6003");
        effectiveWayMessage.put(Integer.valueOf(36), "6004");
        effectiveWayMessage.put(Integer.valueOf(47), "3008");
        effectiveWayMessage.put(Integer.valueOf(4701), "3008");
        effectiveWayMessage.put(Integer.valueOf(48), "6005");
        effectiveWayMessage.put(Integer.valueOf(49), "8007");
        effectiveWayMessage.put(Integer.valueOf(50), "8005");
        effectiveWayMessage.put(Integer.valueOf(51), "8006");
        effectiveWayMessage.put(Integer.valueOf(52), "5001");
        effectiveWayMessage.put(Integer.valueOf(54), "2001");
        effectiveWayMessage.put(Integer.valueOf(5401), "2001");
        effectiveWayMessage.put(Integer.valueOf(58), "7002");
        effectiveWayMessage.put(Integer.valueOf(59), "8009");
        effectiveWayMessage.put(Integer.valueOf(75), "8009");
        effectiveWayMessage.put(Integer.valueOf(60), "8010");
        effectiveWayMessage.put(Integer.valueOf(61), "8011");
        effectiveWayMessage.put(Integer.valueOf(62), "5002");
        effectiveWayMessage.put(Integer.valueOf(63), "3009");
        effectiveWayMessage.put(Integer.valueOf(64), "3010");
        effectiveWayMessage.put(Integer.valueOf(65), "3011");
        effectiveWayMessage.put(Integer.valueOf(66), "3012");
        effectiveWayMessage.put(Integer.valueOf(67), "3013");
        effectiveWayMessage.put(Integer.valueOf(68), "3014");
        return effectiveWayMessage;
    }

    public static HashMap<String, String> getErrorCode() {
        HashMap<String, String> errorCode = new HashMap();
        errorCode.put("7205", "0");
        errorCode.put("7206", "0");
        errorCode.put("7207", "0");
        errorCode.put("7208", "--");
        errorCode.put("7209", "--");
        errorCode.put("7210", "0");
        errorCode.put("7211", "--.--");
        errorCode.put("7212", "--.--");
        errorCode.put("7213", "--.--");
        errorCode.put("7214", "--.--");
        errorCode.put("7295", "--");
        return errorCode;
    }

    public static HashMap<String, String> getSCity() {
        HashMap<String, String> errorCode = new HashMap();
        errorCode.put("0", "维修中心");
        errorCode.put("1", "接机点");
        return errorCode;
    }

    public static HashMap<String, String> getDetailWay() {
        HashMap<String, String> msg = new HashMap();
        msg.put("01", "接收");
        msg.put("02", "发送");
        return msg;
    }

    public static HashMap<String, String> getShareWay() {
        HashMap<String, String> msg = new HashMap();
        msg.put("more", "DF000");
        msg.put("bill", "BF001");
        msg.put("meal", "BF002");
        msg.put("business", "BF003");
        msg.put("payment", "BF004");
        msg.put("account_mgr", "BF005");
        msg.put("message", "DF006");
        msg.put("business_shall", "DF007");
        msg.put("wlan", "DF008");
        msg.put("roam", "DF009");
        msg.put("traffic", "BF010");
        msg.put("after_sale", "DF011");
        msg.put("luck_draw", "DF012");
        return msg;
    }

    public static HashMap<String, String> getRoamTariffsType() {
        HashMap<String, String> tariffsType = new HashMap();
        tariffsType.put("0", "免费");
        tariffsType.put("-1", "未开通");
        return tariffsType;
    }

    public static String getFuncationSort(int type) {
        String funcationType = "";
        switch (type) {
            case 1:
                return "商城";
            case 2:
                return "活动";
            default:
                return funcationType;
        }
    }

    public static HashMap<String, String> getActivityName() {
        HashMap<String, String> tariffsType = new HashMap();
        tariffsType.put("DF00403", "RoamPageActivity");
        tariffsType.put("DF00501", "HallTabActivity");
        tariffsType.put("DF00601", "WlanTabActivity");
        tariffsType.put("DF00701", "UserfulSmsActicity");
        tariffsType.put("DF00801", "UserfulNumActivity");
        tariffsType.put("DN00101", "SmsspanActivity");
        tariffsType.put("DF00901", "NumberAddressActivity");
        tariffsType.put("DF01801", "CaptureActivity");
        tariffsType.put("DF00101", "FeedbackActivity");
        tariffsType.put("DF01201", "SalePointActivity");
        tariffsType.put("CF00300", "CommonHtml5Activity");
        tariffsType.put("DF01001", "CommonHtml5Activity");
        tariffsType.put("DF00301", "CommonHtml5Activity");
        tariffsType.put("DF00201", "CommonHtml5Activity");
        tariffsType.put("BF00201", "CommonHtml5Activity");
        tariffsType.put("BF00101", "CommonHtml5Activity");
        tariffsType.put("PF00101", "CommonHtml5Activity");
        tariffsType.put("PF00201", "RealnameRegisterActivity");
        tariffsType.put("TF00201", "CommonHtml5Activity");
        tariffsType.put("BF00801", "QueryBalanceActivity");
        tariffsType.put("PF00301", "CommonHtml5Activity");
        tariffsType.put("BF01000", "DetailTypeActivity");
        tariffsType.put("CF01901", "CommonHtml5Activity");
        tariffsType.put("BF00702", "TrafficCheckTonghuaActivity");
        tariffsType.put("BF00701", "TrafficCheckActivity");
        tariffsType.put("BF00501", "BillActivity");
        tariffsType.put("BF00901", "QueryBusinessActivity");
        tariffsType.put("BF01201", "CommonHtml5Activity");
        tariffsType.put("BF01301", "CommonHtml5Activity");
        tariffsType.put("90003", "CommonHtml5Activity");
        tariffsType.put("90004", "CommonHtml5Activity");
        tariffsType.put("90005", "CommonHtml5Activity");
        tariffsType.put("90006", "CommonHtml5Activity");
        tariffsType.put("BF0050102", "BillActivity");
        tariffsType.put("BF0080100", "QueryBalanceActivity");
        tariffsType.put("BF0090102", "QueryBusinessActivity");
        tariffsType.put("PF0030102", "CommonHtml5Activity");
        tariffsType.put("PF0030300", "PayHistoryActivity");
        tariffsType.put("BF0100000", "DetailTypeActivity");
        tariffsType.put("CF01901", "CommonHtml5Activity");
        tariffsType.put("CF01902", "CommonHtml5Activity");
        tariffsType.put("CF01903", "CommonHtml5Activity");
        tariffsType.put("CF01904", "CommonHtml5Activity");
        tariffsType.put("BF0060102", "AccountManagementActivity");
        tariffsType.put("CF0050002", "UserLoginWindow");
        tariffsType.put("BF0050103", "BillActivity");
        tariffsType.put("PF0030103", "CommonHtml5Activity");
        tariffsType.put("BF01101", "IntegralFragment");
        tariffsType.put("BF0070202", "TrafficCheckTonghuaActivity");
        tariffsType.put("BF0070102", "TrafficCheckActivity");
        tariffsType.put("DF02100", "CommonHtml5Activity");
        tariffsType.put("CF01101", "CommonHtml5Activity");
        return tariffsType;
    }

    public static HashMap<Integer, String> getCodeDesc() {
        HashMap<Integer, String> codeDesc = new HashMap();
        codeDesc.put(Integer.valueOf(0), "初始化");
        codeDesc.put(Integer.valueOf(1), "获取应用软件版本");
        codeDesc.put(Integer.valueOf(2), "获取手机硬件相关信息");
        codeDesc.put(Integer.valueOf(3), "获取用户信息");
        codeDesc.put(Integer.valueOf(4), "获取会话信息");
        codeDesc.put(Integer.valueOf(5), "session会话失效");
        codeDesc.put(Integer.valueOf(6), "强制返回客户端");
        codeDesc.put(Integer.valueOf(7), "打开－交费历史");
        codeDesc.put(Integer.valueOf(8), "设置标题");
        codeDesc.put(Integer.valueOf(9), "提示对话框");
        codeDesc.put(Integer.valueOf(10), "弹出Toast");
        codeDesc.put(Integer.valueOf(11), "充值卡充值");
        codeDesc.put(Integer.valueOf(12), "打开OCR");
        codeDesc.put(Integer.valueOf(13), "传递OCR密码");
        codeDesc.put(Integer.valueOf(14), "和包充值");
        codeDesc.put(Integer.valueOf(15), "实体营业厅");
        codeDesc.put(Integer.valueOf(16), "分享是否显示");
        codeDesc.put(Integer.valueOf(17), "跳转附近营业厅");
        codeDesc.put(Integer.valueOf(18), "扫一扫是否显示");
        codeDesc.put(Integer.valueOf(19), "分享调用接口");
        codeDesc.put(Integer.valueOf(20), "业务办理");
        codeDesc.put(Integer.valueOf(21), "打开新页面");
        codeDesc.put(Integer.valueOf(22), "拉起登录页面");
        codeDesc.put(Integer.valueOf(23), "判断是否登录");
        codeDesc.put(Integer.valueOf(24), "分享接口：517专用");
        codeDesc.put(Integer.valueOf(25), "短信接口");
        codeDesc.put(Integer.valueOf(27), "获取会话");
        codeDesc.put(Integer.valueOf(28), "重新鉴权");
        codeDesc.put(Integer.valueOf(29), "调用手机通讯录");
        codeDesc.put(Integer.valueOf(30), "手机号码查询通讯录姓名");
        codeDesc.put(Integer.valueOf(41), "振动接口");
        codeDesc.put(Integer.valueOf(42), "网络状态接口");
        codeDesc.put(Integer.valueOf(43), "调用照相接口");
        codeDesc.put(Integer.valueOf(53), "跳转扫一扫");
        codeDesc.put(Integer.valueOf(54), "拨打电话");
        codeDesc.put(Integer.valueOf(55), "新手引导");
        codeDesc.put(Integer.valueOf(56), "版本检测");
        codeDesc.put(Integer.valueOf(57), "给我评分");
        codeDesc.put(Integer.valueOf(58), "获取版本号");
        codeDesc.put(Integer.valueOf(59), "保存图片或者识别二维码的接口");
        codeDesc.put(Integer.valueOf(60), "跳转客户端内置功能");
        codeDesc.put(Integer.valueOf(61), "查询新增用户");
        codeDesc.put(Integer.valueOf(62), "获取IMEI,IMSI");
        codeDesc.put(Integer.valueOf(63), "加密");
        codeDesc.put(Integer.valueOf(100), "获取用户信息");
        return codeDesc;
    }
}
