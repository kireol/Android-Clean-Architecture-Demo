package com.example.restraurantfinderapp.restaurants.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.restraurantfinderapp.restaurants.mvvm.models.GPSLocation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var location: GPSLocation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ConstraintLayoutDemo()
            }
        }
    }
    /********************************************************************************************
     *   Compose is not ready for production use as of this time.  You can't inspect the layout.
     *   The preview takes too long to render.  many bugs
     */

    @Preview
    @Composable
    fun ConstraintLayoutDemo() {
        ConstraintLayout(modifier = Modifier.size(400.dp)) {
            val (redBox, checkboxText, blueBox, yellowBox, text) = createRefs()
            val checkedState = remember { mutableStateOf(true) }

            val checked = false
            Checkbox(modifier = Modifier
                .size(50.dp)
                .padding(5.dp)
                .constrainAs(redBox) {},
                checked = checked,
                onCheckedChange = { checkedState.value = it })
            Box(modifier = Modifier
                .size(50.dp)
                .background(Color.Blue)
                .constrainAs(blueBox) {
                    top.linkTo(redBox.bottom)
                    start.linkTo(redBox.end)
                })

            Box(modifier = Modifier
                .size(50.dp)
                .background(Color.Yellow)
                .constrainAs(yellowBox) {
                    bottom.linkTo(blueBox.bottom)
                    start.linkTo(blueBox.end, 20.dp)
                })

            Text("Hello World", modifier = Modifier.constrainAs(text) {
                top.linkTo(parent.top)
                start.linkTo(yellowBox.start)
            })
        }
    }
}

