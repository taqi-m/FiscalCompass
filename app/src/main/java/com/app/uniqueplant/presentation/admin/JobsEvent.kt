package com.app.uniqueplant.presentation.admin

sealed class JobsEvent {
    data class SampleEventWithParameter(val value: String) : JobsEvent()
    object SampleEvent : JobsEvent()
}