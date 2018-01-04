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

    public static String guide_info = baseIp + "/guide_info.rm";                 //引导页√
    public static String index_alldata = baseIp + "/index_alldata.rm";           //首页√
    public static String agent_userInfo = baseIp + "/agent_userInfo.rm";         //代理商信息√
    public static String profit_data = baseIp + "/profit_data.rm";               //收益列表√
    public static String user_children_data = baseIp + "/user_children_data.rm"; //下级列表√
    public static String payrecord_data = baseIp + "/payrecord_data.rm";         //账单列表√
    public static String appYee_repayment = baseIp + "/appYee_repayment.rm";     //收款√
    public static String paypawd_check = baseIp + "/paypawd_check.rm";           //检验支付密码√
    public static String add_withdraw = baseIp + "/add_withdraw.rm";             //提现√
    public static String newsnotice_Info = baseIp + "/newsnotice_Info.rm";       //公告详情√
    public static String get_MsgInfo = baseIp + "/get_MsgInfo.rm";               //获取消息√
    public static String msgInfo_details = baseIp + "/msgInfo_details.rm";       //消息详细√
    public static String get_msgCount = baseIp + "/get_msgCount.rm";             //未读消息数量√

    public static String up_vip_list = baseIp + "/up_vip_list.rm"; //升级列表√
    public static String card_Info_list = baseIp + "/card_Info_list.rm"; //银行卡列表√
    public static String add_upvip_sub = baseIp + "/add_upvip_sub.rm"; //升级会员√

    public static String user_msg_data = baseIp + "/user_msg_data.rm";                     //用户个人资料√
    public static String userinfo_uploadhead_sub = baseIp + "/userinfo_uploadhead_sub.rm"; //修改头像√
    public static String nickName_change_sub = baseIp + "/nickName_change_sub.rm";         //修改昵称√
    public static String sex_change_sub = baseIp + "/sex_change_sub.rm";                   //修改性别√
    public static String password_change_sub = baseIp + "/password_change_sub.rm";         //修改密码√
    public static String certification_sub = baseIp + "/certification_sub.rm";             //实名认证√
    public static String depositcard_data = baseIp + "/depositcard_data.rm";               //储蓄卡列表√
    public static String creditcard_data = baseIp + "/creditcard_data.rm";                 //信用卡列表√
    public static String identity_Info = baseIp + "/identity_Info.rm";                     //是否实名认证√
    public static String get_userQrcode = baseIp + "/get_userQrcode.rm";                   //获取推荐二维码√
    public static String help_center = baseIp + "/help_center.rm";                         //帮助中心√
    public static String recommend_user = baseIp + "/recommend_user.rm";                   //推荐人信息√
    public static String add_recommenduser = baseIp + "/add_recommenduser.rm";             //绑定推荐人√
    public static String city1_data = baseIp + "/city1_data.rm";                           //获取所有省份√
    public static String city2_data = baseIp + "/city2_data.rm";                           //获取某省份所有市√
    public static String appChanpay_backOpenCard = baseIp + "/appChanpay_backOpenCard.rm"; //绑卡√

    public static String identify_othercode = baseIp + "/identify_othercode.rm";     //获取其他验证码√
    public static String depositcard_add_sub = baseIp + "/depositcard_add_sub.rm";   //新增储蓄卡√
    public static String creditcard_add_sub = baseIp + "/creditcard_add_sub.rm";     //新增信用卡√
    public static String creditcard_details = baseIp + "/creditcard_details.rm";     //信用卡详情√
    public static String creditcard_del_sub = baseIp + "/creditcard_del_sub.rm";     //删除信用卡√
    public static String repayment_data = baseIp + "/repayment_data.rm";             //全部计划√
    public static String add_consumeplan = baseIp + "/add_consumeplan.rm";           //新增消费计划√
    public static String add_repaymentplan = baseIp + "/add_repaymentplan.rm";       //新增还款计划√
    public static String before_repaymentplan = baseIp + "/before_repaymentplan.rm"; //预览还款计划√
    public static String base_rate_data = baseIp + "/base_rate_data.rm";             //基本费率√
}
