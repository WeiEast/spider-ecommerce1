/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2017
 */
package com.datatrees.rawdatacentral.domain.operator;

/**
 * 运营商
 * Created by zhouxinghai on 2017/6/22
 */
public enum GroupEnum {

    CHINA_10010("CHINA_10010","中国联通","china_10010_app"),
    ZHE_JIANG_10086("ZHE_JAING_10086","浙江移动","zhe_jiang_10086_web"),
    ZHE_JIANG_10000("ZHE_JAING_10000","浙江电信","zhe_jiang_10000_web"),
    GUANG_DONG_10086("GUANG_DONG_10086","广东移动","guang_dong_10086_web"),
    GUANG_DONG_10000("GUANG_DONG_10000","广东电信","guang_dong_10000_web"),
//    JIANG_SU_10086("JIANG_SU_10086","江苏移动","jiang_su_10086_wap"),
//    JIANG_SU_10000("JIANG_SU_10000","江苏电信","jiang_su_10000_web"),
//    SHAN_DONG_10086("SHAN_DONG_10086","山东移动","shan_dong_10086_web"),
//    SHAN_DONG_10000("SHAN_DONG_10000","山东电信","shan_dong_10000_web"),
//    HU_NAN_10086("HU_NAN_10086","湖南移动","hu_nan_10086_web"),
//    HU_NAN_10000("HU_NAN_10000","湖南电信","hu_nan_10000_wap"),
//    BEI_JING_10086("BEI_JING_10086","北京移动","bei_jing_10086_web"),
//    BEI_JING_10000("BEI_JING_10000","北京电信","bei_jing_10000_web"),
//    TIAN_JIN_10086("TIAN_JING_10086","天津移动","tian_jin_10086_web"),
//    TIAN_JIN_10000("TIAN_JING_10000","天津电信","tian_jin_10000_wap"),
//    SHANG_HAI_10086("SHANG_HAI_10086","上海移动","shang_hai_10086_web"),
//    SHANG_HAI_10000("SHANG_HAI_10000","上海电信","shang_hai_10000_web"),
//    CHONG_QING_10086("CHONG_QING_10086","重庆移动","chong_qing_10086_web"),
//    CHONG_QING_10000("CHONG_QING_10000","重庆电信","chong_qing_10000_web"),
//    HU_BEI_10086("HU_BEI_10086","湖北移动","hu_bei_10086_web"),
//    HU_BEI_10000("HU_BEI_10000","湖北电信","hu_bei_10000_wap"),
//    SI_CHUAN_10086("SI_CHUAN_10086","四川移动","si_chuan_10086_shop"),
//    SI_CHUAN_10000("SI_CHUAN_10000","四川电信","si_chuan_10000_web"),
//    AN_HUI_10086("AN_HUI_10086","安徽移动","an_hui_10086_web"),
//    AN_HUI_10000("AN_HUI_10000","安徽电信","an_hui_10000_web"),
//    HE_NAN_10086("HE_NAN_10086","河南移动","he_nan_10086_shop"),
//    HE_NAN_10000("HE_NAN_10000","河南电信","he_nan_10000_web"),
//    JIANG_XI_10086("JIANG_XI_10086","江西移动","jiang_xi_10086_wap"),
//    JIANG_XI_10000("JIANG_XI_10000","江西电信","jiang_xi_10000_web"),
//    HE_BEI_10086("HE_BEI_10086","河北移动","he_bei_10086_web"),
//    HE_BEI_10000("HE_BEI_10000","河北电信","he_bei_10000_web"),
//    FU_JIAN_10086("FU_JIAN_10086","福建移动","fu_jian_10086_web"),
//    FU_JIAN_10000("FU_JIAN_10000","福建电信","fu_jian_10000_web"),
//    SHAN_XI_TY_10086("SHAN_XI_TY_10086","山西移动","shan_xi_ty_10086_web"),
//    SHAN_XI_TY_10000("SHAN_XI_TY_10000","山西电信","shan_xi_ty_10000_web"),
//    JI_LIN_10086("JI_LIN_10086","吉林移动","ji_lin_10086_shop"),
//    JI_LIN_10000("JI_LIN_10000","吉林电信","ji_lin_10000_web"),
//    LIAO_NING_10086("LIAO_NING_10086","辽宁移动","liao_ning_10086_web"),
//    LIAO_NING_10000("LIAO_NING_10000","辽宁电信","liao_ning_10000_web"),
//    HEI_LONG_JIANG_10086("HEI_LONG_JIANG_10086","黑龙江移动","hei_long_jiang_10086_web"),
//    HEI_LONG_JIANG_10000("HEI_LONG_JIANG_10000","黑龙江电信","hei_long_jiang_10000_web"),
//    YUN_NAN_10086("YUN_NAN_10086","云南移动","yun_nan_10086_app"),
//    YUN_NAN_10000("YUN_NAN_10000","云南电信","yun_nan_10000_wap"),
//    GUANG_XI_10086("GUANG_XI_10086","广西移动","guang_xi_10086_web"),
//    GUANG_XI_10000("GUANG_XI_10000","广西电信","guang_xi_10000_web"),
//    SHAN_XI_XA_10086("SHAN_XI_XA_10086","陕西移动","shan_xi_xa_10086_wap"),
//    SHAN_XI_XA_10000("SHAN_XI_XA_10000","陕西电信","shan_xi_xa_10000_web"),
//    GAN_SU_10086("GAN_SU_10086","甘肃移动","gan_su_10086_wap"),
//    GAN_SU_10000("GAN_SU_10000","甘肃电信","gan_su_10000_app"),
//    NEI_MENG_GU_10086("NEI_MENG_GU_10086","内蒙古移动","nei_meng_gu_10086_shop"),
//    NEI_MENG_GU_10000("NEI_MENG_GU_10000","内蒙古电信","nei_meng_gu_10000_web"),
//    HAI_NAN_10086("HAI_NAN_10086","海南移动","hai_nan_10086_web"),
//    HAI_NAN_10000("HAI_NAN_10000","海南电信","hai_nan_10000_web"),
//    GUI_ZHOU_10086("GUI_ZHOU_10086","贵州移动","gui_zhou_10086_shop"),
//    QING_HAI_10086("QING_HAI_10086","青海移动","qing_hai_10086_shop"),
//    NING_XIA_10086("NING_XIA_10086","宁夏移动","ning_xia_10086_shop");



    /**
     * 运营商类别代码
     */
    private final String groopCode;

    /**
     * 运营商类别名称
     */
    private final String groupName;

    /**
     * 默认站点名称
     */
    private final String websiteName;

    GroupEnum(String groopCode, String groupName, String websiteName) {
        this.groopCode = groopCode;
        this.groupName = groupName;
        this.websiteName = websiteName;
    }
}
