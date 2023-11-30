package backend.cache

import kotlinx.coroutines.*
import java.io.File
import java.io.IOException

interface CacheWriter {

    fun register(item: Cacheable)

    companion object {
        val SimpleCacheWriter = object: CacheWriter {
            private val sources = mutableListOf<Cacheable>()

            init {
                cacheWorkflow()
            }

            private suspend fun writeFile(file: File, text: String) {
                try {
                    withContext(Dispatchers.IO) {
                        file.writer().write(text)
                    }
                } catch (e: IOException) {
                    println("Error to write to file: ${file.path}")
                }
            }

            @OptIn(ExperimentalCoroutinesApi::class)
            private fun cacheWorkflow() = runBlocking {
                withContext(Dispatchers.IO.limitedParallelism(1)){
                    while (true) {
                        delay(10000)
                        for (source in sources){
                            writeFile(source.cacheFile, source.toString())
                        }
                    }
                }

            }

            override fun register(item: Cacheable) {
                sources.add(item)
            }
        }
    }
}