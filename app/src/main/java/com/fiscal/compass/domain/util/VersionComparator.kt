package com.fiscal.compass.domain.util

/**
 * Compares semantic version strings (e.g. "1.2.3" or "v1.2.3").
 * Supports arbitrary segment depth (major, major.minor, major.minor.patch, etc.).
 */
object VersionComparator {

    /**
     * Returns true if [remote] is a newer version than [current].
     *
     * - Strips leading "v" or "V" prefix.
     * - Strips any trailing suffix after a hyphen (e.g. "1.0-dev" → "1.0").
     * - Compares each numeric segment left-to-right; missing segments are treated as 0.
     */
    fun isNewer(current: String, remote: String): Boolean {
        val currentParts = parseVersion(current)
        val remoteParts = parseVersion(remote)

        val maxLength = maxOf(currentParts.size, remoteParts.size)
        for (i in 0 until maxLength) {
            val c = currentParts.getOrElse(i) { 0 }
            val r = remoteParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }

    private fun parseVersion(version: String): List<Int> {
        return version
            .trimStart('v', 'V')
            .substringBefore('-')
            .split('.')
            .mapNotNull { it.toIntOrNull() }
    }
}

