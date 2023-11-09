package backend.filesystem

import backend.filesystem.events.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import java.io.IOException
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


class FilesystemMonitor(private val changesQueue: ConcurrentLinkedQueue<FilesystemChangeEvent>) {
    @Volatile private var dir: Path? = null
    @Volatile private var watchService: WatchService? = null
    @Volatile private var watchKey: WatchKey? = null
    @Volatile private var updateJob: Job? = null

    private suspend fun pathObserverWorkflow() {
        while (true) {

            // wait for key to be signaled
            val key = try {
                withContext(Dispatchers.IO) {
                    watchService?.take()
                }
            } catch (x: InterruptedException) {
                return
            }

            key?.let {
                for (event in key.pollEvents()) {
                    val kind = event.kind()

                    val ev = event as WatchEvent<Path>
                    val filename = ev.context()

                    // Verify that the new
                    //  file is a text file.
                    val child = try {
                        // Resolve the filename against the directory.
                        // If the filename is "test" and the directory is "foo",
                        // the resolved name is "test/foo".
                        dir?. let {
                            it.resolve(filename)
                        }

                    } catch (x: IOException) {
                        System.err.println(x)
                        continue
                    }

                    // This key is registered only
                    // for ENTRY_CREATE events,
                    // but an OVERFLOW event can
                    // occur regardless if events
                    // are lost or discarded.
                    child?. let {
                        when(kind) {
                            ENTRY_CREATE -> changesQueue.add(CreateEvent(it.toString()))
                            ENTRY_DELETE -> changesQueue.add(RemoveEvent(it.toString()))
                            ENTRY_MODIFY -> changesQueue.add(EditEvent(it.toString()))
                            else -> {}
                        }
                    }
                    if (kind == OVERFLOW) {
                        continue
                    }

                    System.out.format("Emailing file %s%s%n", kind, child)
                }
            }


            val valid = key?.reset() ?: false
            if (!valid) {
                break
            }
        }
    }

    /*
     * Call only with existing path
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun register(path: Path) {
        reset()

        dir = path
        try {
            watchKey = path.register(
                watchService!!,
                arrayOf(
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY
                )
            )
        } catch (e: IOException) {
            System.err.println(e)
        } catch (e: NullPointerException) {
            System.err.println("No watch service found in FilesystemMonitor: $e")
        }
        watchKey?.let {
            val limitedIO = Dispatchers.IO.limitedParallelism(1)
            updateJob = CoroutineScope(limitedIO). launch {
                pathObserverWorkflow()
            }
            updateJob?.cancel()
        }
    }

    fun reset() {
        watchService?.close()
        watchKey = null
        watchService = FileSystems.getDefault().newWatchService()
        dir = null
    }
}