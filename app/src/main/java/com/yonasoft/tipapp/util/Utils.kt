package com.yonasoft.tipapp.util
//This file is for helper functions

//Function for calculating tip. Takes the total bill and gets the percentage of the amount
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

//Calculates tip per person. Gets total bill, adds teh tip an divides by the number of people to split
fun calculateTotalPerPerson(
    totalBill:Double,
    splitBy:Int,
    tipPercentage:Int
):Double{
    val bill = totalBill + calculateTip(totalBill, tipPercentage)
    return bill/splitBy
}