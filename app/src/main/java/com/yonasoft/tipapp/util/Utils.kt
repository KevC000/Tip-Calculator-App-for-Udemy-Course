package com.yonasoft.tipapp.util

fun calculateTip(
    totalBill: Double,
    percent: Int
):Double {
    return if (totalBill > 1 &&
        totalBill.toString().isNotEmpty()
    ) {
        (totalBill * percent)/100
    } else {
        0.0
    }
}

fun calculateTotalPerPerson(
    totalBill:Double,
    splitBy:Int,
    tipPercentage:Int
):Double{
    val bill = totalBill + calculateTip(totalBill, tipPercentage)
    return bill/splitBy
}