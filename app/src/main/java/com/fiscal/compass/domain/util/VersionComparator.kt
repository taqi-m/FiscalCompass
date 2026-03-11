package com.fiscal.compass.domain.util

/**
 * Compares semantic version strings (e.g. "1.0.0", "v1.2.3", "1.0.0-dev").
 *
 * Follows Semantic Versioning 2.0.0 (https://semver.org) precedence rules:
 *  - Numeric segments are compared left-to-right; missing segments are treated as 0.
 *  - A pre-release version (e.g. "1.0.0-dev") has LOWER precedence than the
 *    same version without a pre-release suffix (e.g. "1.0.0").
 */
object VersionComparator {

    /**
     * Returns `true` if [remote] is a newer version than [current].
     *
     * Examples:
     * ```
     *  isNewer("1.0.0",     "1.0.1")      → true
     *  isNewer("1.0.0",     "1.0.0")      → false
     *  isNewer("1.0.0-dev", "1.0.0")      → true   // stable > pre-release
     *  isNewer("1.0.0-dev", "1.0.0-dev")  → false
     *  isNewer("1.0.0",     "1.0.0-beta") → false   // pre-release < stable
     * ```
     */
    fun isNewer(current: String, remote: String): Boolean {
        val currentParsed = parseVersion(current)
        val remoteParsed = parseVersion(remote)

        val maxLength = maxOf(currentParsed.segments.size, remoteParsed.segments.size)
        for (i in 0 until maxLength) {
            val c = currentParsed.segments.getOrElse(i) { 0 }
            val r = remoteParsed.segments.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }

        // Numeric segments are equal — compare pre-release presence.
        // Per SemVer: a version WITHOUT a pre-release suffix has higher precedence
        // than the same version WITH one (e.g. 1.0.0 > 1.0.0-dev).
        if (currentParsed.isPreRelease && !remoteParsed.isPreRelease) return true
        if (!currentParsed.isPreRelease && remoteParsed.isPreRelease) return false

        return false
    }

    private data class ParsedVersion(
        val segments: List<Int>,
        val isPreRelease: Boolean
    )

    private fun parseVersion(version: String): ParsedVersion {
        val stripped = version.trimStart('v', 'V')
        val isPreRelease = stripped.contains('-')
        val segments = stripped
            .substringBefore('-')
            .split('.')
            .mapNotNull { it.toIntOrNull() }
        return ParsedVersion(segments, isPreRelease)
    }
}

