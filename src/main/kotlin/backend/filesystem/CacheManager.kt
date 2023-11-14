package backend.filesystem

import java.io.File

interface CacheManager {
    fun updateCache(file: File, content: Any)
}