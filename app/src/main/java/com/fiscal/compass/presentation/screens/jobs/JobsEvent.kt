package com.fiscal.compass.presentation.screens.jobs

sealed class JobsEvent {
    data class SampleEventWithParameter(val value: String) : JobsEvent()
    object SampleEvent : JobsEvent()
}