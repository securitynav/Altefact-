package com.securitynav.security.ui

import androidx.compose.runtime.Composable
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

@Composable
fun TestChart() {
    val producer = ChartEntryModelProducer()
    Chart(
        chart = lineChart(),
        chartModelProducer = producer
    )
}
