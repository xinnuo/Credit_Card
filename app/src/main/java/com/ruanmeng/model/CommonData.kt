/**
 * created by 小卷毛, 2017/10/26
 * Copyright (c) 2017, 416143467@qq.com All Rights Reserved.
 * #                   *********                            #
 * #                  ************                          #
 * #                  *************                         #
 * #                 **  ***********                        #
 * #                ***  ****** *****                       #
 * #                *** *******   ****                      #
 * #               ***  ********** ****                     #
 * #              ****  *********** ****                    #
 * #            *****   ***********  *****                  #
 * #           ******   *** ********   *****                #
 * #           *****   ***   ********   ******              #
 * #          ******   ***  ***********   ******            #
 * #         ******   **** **************  ******           #
 * #        *******  ********************* *******          #
 * #        *******  ******************************         #
 * #       *******  ****** ***************** *******        #
 * #       *******  ****** ****** *********   ******        #
 * #       *******    **  ******   ******     ******        #
 * #       *******        ******    *****     *****         #
 * #        ******        *****     *****     ****          #
 * #         *****        ****      *****     ***           #
 * #          *****       ***        ***      *             #
 * #            **       ****        ****                   #
 */
package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Credit_Card
 * 创建人：小卷毛
 * 创建时间：2017-12-05 16:35
 */
data class CommonData(
        //首页轮播图
        var href: String = "",
        var sliderId: String = "",
        var sliderImg: String = "",
        var title: String = "",

        //首页公告
        var createDate: String = "",
        var newsnoticeId: String = "",

        //账单
        var cardId: String = "",
        var cardNo: String = "",
        var cardType: String = "",
        var payRecordId: String = "",
        var paySum: String = "",
        var payTime: String = "",
        var payType: String = "",
        var rate: String = "",
        var status: String = "",
        var type: String = "",
        var userInfoId: String = "",

        //我的关注+关注我的
        var accountInfoId: String = "",
        var colleconId: String = "",
        var userName: String = "",
        var userhead: String = "",

        //我的收藏
        var address: String = "",
        var productId: String = "",
        var productName: String = "",
        var productPic: String = "",
        var productPrice: String = "",

        //我的产品
        var retailPrice: String = "",
        var supplyDescribe: String = "",
        var supplyId: String = "",
        var supplyPic: String = "",

        var managerId: String = "",
        var id: String = "",
        var image: String = "",

        //全部计划
        var repaymentId: String = "",
        var repaymentSum: String = "",
        var repaymentTime: String = "",
        var repaymentType: String = "",

        //信用卡列表
        var billDay: String = "",
        var creditcard: String = "",
        var creditcardId: String = "",
        var cvn2: String = "",
        var invaridDate: String = "",
        var quota: String = "",
        var repaymentDay: String = "",

        //储蓄卡列表
        var bank: String = "",
        var depositcard: String = "",
        var depositcardId: String = "",
        var identityCard: String = "",
        var name: String = "",
        var tel: String = "",

        var isChecked: Boolean = false
) : Serializable