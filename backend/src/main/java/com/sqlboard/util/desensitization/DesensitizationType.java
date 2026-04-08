package com.sqlboard.util.desensitization;

/**
 * 脱敏类型枚举
 */
public enum DesensitizationType {
    /**
     * 不脱敏
     */
    NONE,
    /**
     * 中文姓名
     */
    CHINESE_NAME,
    /**
     * 身份证号
     */
    ID_CARD,
    /**
     * 手机号
     */
    PHONE,
    /**
     * 邮箱
     */
    EMAIL,
    /**
     * 银行卡
     */
    BANK_CARD,
    /**
     * 地址
     */
    ADDRESS,
    /**
     * 完全脱敏
     */
    FULL
}
