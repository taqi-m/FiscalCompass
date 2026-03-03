package com.fiscal.compass.domain.interfaces

import com.fiscal.compass.domain.model.Resource
import com.fiscal.compass.domain.model.update.ReleaseInfo

/**
 * Abstraction for fetching the latest release information.
 *
 * Implement this interface for each distribution source:
 * - GitHubUpdateSource (GitHub Releases)
 * - Future: Firebase Remote Config, custom server, etc.
 */
interface UpdateSource {
    suspend fun getLatestRelease(): Resource<ReleaseInfo?>
}


