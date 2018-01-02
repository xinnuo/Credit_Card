package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Credit_Card
 * 创建人：小卷毛
 * 创建时间：2017-12-16 10:38
 */
data class CommonModel(
        var depositcards: List<CommonData>? = ArrayList(),
        var creditcards: List<CardData>? = ArrayList(),
        var cards: List<CommonData>? = ArrayList(),

        var repayments: List<CommonData>? = ArrayList(),
        var news: List<CommonData>? = ArrayList(),
        var payrecords: List<CommonData>? = ArrayList(),
        var slider: List<CommonData>? = ArrayList(),

        var profits: List<IncomeData>? = ArrayList(),
        var ls: List<CommonData>? = ArrayList(),

        var las: List<CommonData>? = ArrayList(),
        var levelName: String = "",

        var msgs: List<CommonData>? = ArrayList(),
        var `object`: List<CommonData>? = ArrayList(),

        var areas: List<CommonData>? = ArrayList(),

        var msg: String = "",
        var msgcode: String = "",
        var success: String = ""
) : Serializable