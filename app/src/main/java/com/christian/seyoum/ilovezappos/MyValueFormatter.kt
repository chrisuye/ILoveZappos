package com.christian.seyoum.ilovezappos

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class MyValueFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return ("").toString()


    }
}

/*
used in MainActivity to remove the x lable
 */
