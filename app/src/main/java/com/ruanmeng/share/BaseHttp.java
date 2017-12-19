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

    public static String register_sub = baseIp + "/register_sub.rm";                   //注册√
    public static String identify_get = baseIp + "/identify_get.rm";                   //注册获取验证码√
    public static String login_sub = baseIp + "/login_sub.rm";                         //登录√
    public static String identify_getbyforget = baseIp + "/identify_getbyforget.rm";   //忘记密码获取验证码√
    public static String pwd_forget_sub = baseIp + "/pwd_forget_sub.rm";               //忘记密码找回提交√
    public static String paypawd_add_sub = baseIp + "/paypawd_add_sub.rm";             //新增支付密码√
    public static String paypawd_change_mobile = baseIp + "/paypawd_change_mobile.rm"; //修改支付密码√

    public static String index_alldata = baseIp + "/index_alldata.rm"; //首页√
    public static String payrecord_data = baseIp + "/payrecord_data.rm"; //账单列表√
    public static String appYee_repayment = baseIp + "/appYee_repayment.rm"; //收款√
    public static String paypawd_check = baseIp + "/paypawd_check.rm"; //检验支付密码√

    public static String user_msg_data = baseIp + "/user_msg_data.rm";                     //用户个人资料√
    public static String userinfo_uploadhead_sub = baseIp + "/userinfo_uploadhead_sub.rm"; //修改头像√
    public static String nickName_change_sub = baseIp + "/nickName_change_sub.rm";         //修改昵称√
    public static String sex_change_sub = baseIp + "/sex_change_sub.rm";                   //修改性别√
    public static String password_change_sub = baseIp + "/password_change_sub.rm";         //修改密码√
    public static String certification_sub = baseIp + "/certification_sub.rm";             //实名认证√
    public static String depositcard_data = baseIp + "/depositcard_data.rm";               //储蓄卡列表√
    public static String creditcard_data = baseIp + "/creditcard_data.rm";                 //信用卡列表√
    public static String identity_Info = baseIp + "/identity_Info.rm";                     //是否实名认证√

    public static String identify_othercode = baseIp + "/identify_othercode.rm";   //获取其他验证码√
    public static String depositcard_add_sub = baseIp + "/depositcard_add_sub.rm"; //新增储蓄卡√
    public static String creditcard_add_sub = baseIp + "/creditcard_add_sub.rm";   //新增信用卡√
    public static String creditcard_details = baseIp + "/creditcard_details.rm";   //信用卡详情√
    public static String creditcard_del_sub = baseIp + "/creditcard_del_sub.rm";   //删除信用卡√
    public static String repayment_data = baseIp + "/repayment_data.rm";           //全部计划√
}
