/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fidloo.flux.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fidloo.flux.domain.base.Result
import com.fidloo.flux.domain.model.DayWeather
import com.fidloo.flux.presentation.R
import com.fidloo.flux.presentation.ui.component.GenericErrorMessage
import com.fidloo.flux.presentation.ui.component.SectionHeader
import com.fidloo.flux.presentation.ui.component.SectionProgressBar
import com.fidloo.flux.presentation.ui.theme.BottomSheetShape
import com.fidloo.flux.presentation.ui.theme.FluxTheme
import com.fidloo.flux.presentation.ui.theme.blue
import com.fidloo.flux.presentation.ui.theme.darkBlue
import com.fidloo.flux.presentation.ui.theme.purple
import com.fidloo.flux.presentation.ui.theme.skyBlue

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val viewState by viewModel.state.collectAsState()
    BackdropScaffold(
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
        frontLayerScrimColor = Color.Transparent,
        backLayerBackgroundColor = skyBlue,
        frontLayerElevation = FluxTheme.elevations.Backdrop,
        backLayerContent = {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
            )
            {
                // Create references for the composables to constrain
                val (button, text, background) = createRefs()

                Image(
                    painter = painterResource(R.drawable.night2),
                    contentDescription = "Weather satellite images",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().constrainAs(background) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                )
            }
        },
        frontLayerContent = {
            DetailedWeather(viewState)
        },
        appBar = {}
    )
}

@Composable
fun DetailedWeather(viewState: HomeViewState) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
        shape = BottomSheetShape
    ) {
        val scrollState = rememberLazyListState()
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {
            item { CurrentWeather(viewState.currentWeather) }
            item { HourlyWeather(viewState.hourlyWeather) }
            item { WeatherRadar() }
            item { SectionHeader(title = "This week", subtitle = "7-day forecast") }
            item { Spacer(Modifier.height(8.dp)) }
            when (viewState.weekWeather) {
                is Result.Error -> item { GenericErrorMessage() }
                Result.Loading -> item { SectionProgressBar() }
                is Result.Success -> {
                    items(viewState.weekWeather.data) { item ->
                        DayWeather(item)
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    HomeScreen()
}
