package com.ruanmeng.share;

import com.ruanmeng.credit_card.BuildConfig;

/**
 * 项目名称：Credit_Card
 * 创建人：小卷毛
 * 创建时间：2017-12-05 17:22
 */

public class BaseHttp {

    private static String baseUrl = BuildConfig.API_HOST;
    private static String baseIp = baseUrl + "/api";

    public static String baseImg = baseUrl + "/";

    public static String register_sub = baseIp + "/register_sub.rm";                 //注册√
    public static String identify_get = baseIp + "/identify_get.rm";                 //注册获取验证码√
    public static String login_sub = baseIp + "/login_sub.rm";                       //登录√
    public static String identify_getbyforget = baseIp + "/identify_getbyforget.rm"; //忘记密码获取验证码√
    public static String pwd_forget_sub = baseIp + "/pwd_forget_sub.rm";             //忘记密码找回提交√
    public static String logout_sub = baseIp + "/logout_sub.rm";                     //退出登录√

}
