package com.yonasoft.tipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yonasoft.tipapp.components.InputField
import com.yonasoft.tipapp.ui.theme.TipAppTheme
import com.yonasoft.tipapp.ui.widgets.RoundIconButton
import com.yonasoft.tipapp.util.calculateTip
import com.yonasoft.tipapp.util.calculateTotalPerPerson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipAppTheme {
                MyApp {
                    MainContent()
                }
            }
        }
    }
}
//Composable for the container of the whole app.
@Composable
fun MyApp(content: @Composable () -> Unit) {
    Surface(color = MaterialTheme.colors.background) {
        content()
    }
}
//Preview for the app
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipAppTheme {
        MainContent()
    }
}
//Composable for header the that shows the bill per person
@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
            .clip(
                shape = CircleShape.copy(
                    all = CornerSize(12.dp)
                )
            ), color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            //Text description of the dollar value below this
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5,
                color = Color(0xFF000000)
            )
            //Text that shows value of total bill per person
            Text(
                text = "$${total}", style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000)
            )
        }
    }
}
//Container for the main part of the app, and its variables.
@Composable
fun MainContent() {
    val splitByState = remember {
        mutableStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 100)
    val tipAmount = remember {
        mutableStateOf("0.0")
    }
    val totalPerPerson = remember {
        mutableStateOf(0.0)
    }
    //Bill form of the app that takes in parameters
    BillForm(splitByState = remember {
        mutableStateOf(1)
    }, tipAmountState = remember {
        mutableStateOf(0.0)
    }, totalPerPerson = remember {
        mutableStateOf(0.0)
    })

}
//Composable that combines header and the rest of the app
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPerson: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    Column {
        //Header which shows the total per person
        TopHeader(totalPerPerson.value)

        Surface(
            modifier = modifier
                .padding(14.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray),
            ) {
            Column(
                modifier = modifier.padding(
                    6.dp,
                ), verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        keyboardController!!.hide()
                        onValChange(totalBillState.value.trim())

                        keyboardController.hide()
                    }
                )

                //If input is valid it will show additional UI for adding additional info for calculating tip amount and total per person, otherwise it will show an empty box
                if (validState) {
                    tipAmountState.value =
                        calculateTip(
                            totalBillState.value.toDouble(),
                            tipPercentage
                        )
                    totalPerPerson.value = calculateTotalPerPerson(
                        totalBillState.value.toDouble(),
                        splitBy = splitByState.value,
                        tipPercentage = tipPercentage
                    )
                    Row(
                        modifier = modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Split",
                            modifier = modifier.align(
                                alignment = Alignment.CenterVertically,
                            )
                        )
                        Spacer(
                            modifier = modifier.width(120.dp)
                        )
                        RoundIconButton(
                            modifier = modifier,
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1) splitByState.value - 1 else 1
                            }
                        )

                        Text(
                            text = "${splitByState.value}",
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(
                            modifier = modifier,
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value = splitByState.value + 1
                                }
                            })
                    }
                    //Tip amount based the slider percent and the current bill
                    Row(
                        modifier = modifier
                            .padding(3.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = "Tip",
                            modifier = modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = modifier.width(200.dp))
                        Text(text = "%.2f".format(tipAmountState.value))
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                } else {
                    Box {
                    }
                }
                //Slider for tip % and text of the % amount
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage%")
                    Spacer(modifier = modifier.height(14.dp))
                    Slider(value = sliderPositionState.value, onValueChange = {
                        sliderPositionState.value = it
                    }, modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp
                    ),
                        steps = 5,
                        onValueChangeFinished = {

                        }
                    )
                }
            }
        }
    }
}





