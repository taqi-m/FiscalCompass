package com.fiscal.compass.presentation.navigation

// ── Search graph routes ───────────────────────────────────────────────────────

object SearchGraph : MainScreens("search_graph")
object Search : MainScreens("search_screen")
object SearchFilters : MainScreens("search_filters_screen")

/** Inline category selection — lives inside SearchGraph, reads state from shared SearchViewModel */
object SearchCategorySelection : MainScreens("search_category_selection")

/** Inline person selection — lives inside SearchGraph, reads state from shared SearchViewModel */
object SearchPersonSelection : MainScreens("search_person_selection")

