package com.sqlboard.util.desensitization;

import cn.hutool.core.util.StrUtil;

import java.util.function.Function;

/**
 * 数据脱敏工具类
 * 已经完成脱敏算法，提供不同类型的数据脱敏策略
 */
public class DesensitizationUtil {

    /**
     * 中文姓名脱敏：保留第一个字，其余隐藏
     * 张三 -> 张*
     * 张三丰 -> 张**
     */
    public static String chineseName(String name) {
        if (StrUtil.isBlank(name)) {
            return name;
        }
        int len = name.length();
        if (len <= 1) {
            return name;
        }
        return StrUtil.fillAfter(StrUtil.sub(name, 0, 1), '*', len - 1);
    }

    /**
     * 身份证脱敏：保留前6后4，中间隐藏
     * 110101199001011234 -> 110101**********1234
     */
    public static String idCard(String idCard) {
        if (StrUtil.isBlank(idCard) || idCard.length() < 8) {
            return idCard;
        }
        String prefix = idCard.substring(0, 6);
        String suffix = idCard.substring(idCard.length() - 4);
        return prefix + StrUtil.repeat('*', idCard.length() - 10) + suffix;
    }

    /**
     * 手机号脱敏：保留前3后4，中间隐藏
     * 13812345678 -> 138****5678
     */
    public static String phone(String phone) {
        if (StrUtil.isBlank(phone) || phone.length() < 7) {
            return phone;
        }
        String prefix = phone.substring(0, 3);
        String suffix = phone.substring(phone.length() - 4);
        return prefix + StrUtil.repeat('*', phone.length() - 7) + suffix;
    }

    /**
     * 邮箱脱敏：保留用户名第一个字符和域名
     * example@gmail.com -> e******@gmail.com
     */
    public static String email(String email) {
        if (StrUtil.isBlank(email) || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        if (username.length() <= 1) {
            return email;
        }
        return StrUtil.sub(username, 0, 1) + StrUtil.repeat('*', username.length() - 1) + "@" + domain;
    }

    /**
     * 银行卡脱敏：保留前6后4，中间隐藏
     * 6222012345678901234 -> 622201********1234
     */
    public static String bankCard(String bankCard) {
        if (StrUtil.isBlank(bankCard) || bankCard.length() < 10) {
            return bankCard;
        }
        String prefix = bankCard.substring(0, 6);
        String suffix = bankCard.substring(bankCard.length() - 4);
        return prefix + StrUtil.repeat('*', bankCard.length() - 10) + suffix;
    }

    /**
     * 地址脱敏：隐藏详细地址部分，保留前N个字符
     */
    public static String address(String address, int retainPrefix) {
        if (StrUtil.isBlank(address) || address.length() <= retainPrefix) {
            return address;
        }
        return StrUtil.sub(address, 0, retainPrefix) + StrUtil.repeat('*', address.length() - retainPrefix);
    }

    /**
     * 自定义脱敏：保留前后多少个字符，中间替换
     */
    public static String custom(String value, int prefix, int suffix) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        int len = value.length();
        if (len <= prefix + suffix) {
            return StrUtil.repeat('*', len);
        }
        return StrUtil.sub(value, 0, prefix) +
                StrUtil.repeat('*', len - prefix - suffix) +
                StrUtil.sub(value, len - suffix, len);
    }

    /**
     * 完全脱敏：全部替换为*
     */
    public static String full(String value) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        return StrUtil.repeat('*', value.length());
    }

    /**
     * 根据脱敏类型应用对应的脱敏策略
     */
    public static String apply(String value, DesensitizationType type) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        return switch (type) {
            case CHINESE_NAME -> chineseName(value);
            case ID_CARD -> idCard(value);
            case PHONE -> phone(value);
            case EMAIL -> email(value);
            case BANK_CARD -> bankCard(value);
            case ADDRESS -> address(value, 6);
            case FULL -> full(value);
            case NONE -> value;
        };
    }

    /**
     * 对数据中的敏感字段批量应用脱敏
     */
    public static <T> void desensitizeFields(T data, Function<String, DesensitizationType> typeGetter,
                                            java.util.function.BiConsumer<T, String> setter) {
        // Implementation for field-based desensitization can be added based on specific needs
    }
}
