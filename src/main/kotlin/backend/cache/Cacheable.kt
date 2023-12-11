package backend.cache

import java.io.File

interface Cacheable {
    var cacheableData: Any
    var cacheFile: File
}