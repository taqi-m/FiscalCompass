package com.fiscal.compass.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fiscal.compass.presentation.utilities.CurrencyFormater
import io.github.dautovicharis.charts.PieChart
import io.github.dautovicharis.charts.model.toChartDataSet
import io.github.dautovicharis.charts.style.ChartViewDefaults
import io.github.dautovicharis.charts.style.PieChartDefaults


@Composable
fun PieTable(
    data: Map<String, Double>,
    header: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard (
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp).padding(horizontal = 8.dp).padding(top = 8.dp)
        ) {

            //Header Row
            val headingStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )

            Text(
                text = header,
                style = MaterialTheme.typography.headlineSmall
            )

            ShowPie(
                title = header,
                values = data
            )

            Spacer(modifier = Modifier.height(16.dp))
            /*Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category",
                    style = headingStyle
                )
                Text(
                    text = header,
                    style = headingStyle
                )
            }*/

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            //Data Rows
            val dataRowStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.W500
            )
            data.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = row.key,
                        style = dataRowStyle
                    )
                    Text(
                        text = CurrencyFormater.formatCurrency(row.value),
                        style = dataRowStyle
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }

            //Footer Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Total",
                    style = headingStyle
                )
                Text(
                    text = CurrencyFormater.formatCurrency(
                        data.values.sum()
                    ),
                    style = headingStyle
                )
            }
        }

    }
}



@Composable
private fun ShowPie(
    title: String = "",
    values : Map<String, Double> = emptyMap()
) {
    if (values.isEmpty() or (values.size < 2)) {
        return
    }

    var dataValues = values.map {
        (it.value / values.values.sum() * 100).toFloat()
    }
    var dataLabels = values.keys.toList()

    val dataSet = dataValues.toChartDataSet(
        title = "",
        postfix = "%",
        labels = dataLabels
    )

    PieChart(
        dataSet = dataSet,
        style = PieChartDefaults.style(
            pieColor = MaterialTheme.colorScheme.tertiary,
            innerPadding = 30.dp,
            borderWidth = 2f,
            borderColor = MaterialTheme.colorScheme.onTertiary,
            chartViewStyle = ChartViewDefaults.style(
                outerPadding = 0.dp,
                innerPadding = 0.dp,
                shadow = 0.dp,
                backgroundColor = Color.Transparent
            )
        )
    )
}


@Preview
@Composable
fun PieTablePreview() {
    val rows = mapOf(
        "Groceries" to 150.75,
        "Utilities" to 85.50,
        "Rent" to 1200.00,
        "Transport" to 50.00
    )
    PieTable(
        data = rows,
        header = "Amount"
    )
}
