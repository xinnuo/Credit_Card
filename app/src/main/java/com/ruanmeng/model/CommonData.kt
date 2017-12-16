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
        var createDate: String = "",
        var sliderId: String = "",
        var sliderTitle: String = "",
        var content: String = "",
        var sliderImg: String = "",
        var sliderType: String = "",
        var sendStatus: String = "",
        var jumpType: String = "",
        var moduleType: String = "",

        //首页公告
        var coverImg: String = "",
        var messageType: String = "",
        var messageId: String = "",
        var messageInfoId: String = "",
        var readStatus: String = "",
        var recAccountInfoId: String = "",
        var title: String = "",

        //金融圈
        var financialId: String = "",
        var financialStatus: String = "",
        var financialTitle: String = "",
        var financialImage: String = "",

        //行业类别
        var industryId: String = "",
        var industryName: String = "",
        var position: Int = -1,
        var childs: List<CommonData> ?= ArrayList(),

        //猜你喜欢
        var compLogo: String = "",
        var compName: String = "",
        var companyId: String = "",

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
        var type: String = "",

        //兴趣爱好
        var chatRoomImage: String = "",
        var chatRoomId: String = "",
        var chatRoomName: String = "",

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