package com.app.uniqueplant.presentation.screens.jobs

sealed class JobsEvent {
    data class SampleEventWithParameter(val value: String) : JobsEvent()
    object SampleEvent : JobsEvent()
}