package backend.psi

import backend.filesystem.CacheManager
import backend.psi.lexer.Lexer
import backend.psi.lexer.idelang.IDELangLexer
import backend.psi.parser.Parser
import backend.psi.parser.idelang.IDELangParser
import backend.vfs.Vfs
import backend.vfs.descriptors.FileDescriptor
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PsiManager(private val virtualFileSystem: Vfs, private val cacheManager: CacheManager?) {
    private val lock = virtualFileSystem.readWriteLock.readLock()
    private val psiManagerCoroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName("PsiManager"))

    init {
        psiManagerCoroutineScope.launch {
            psiManagerWorkflow()
        }
    }

    suspend fun psiManagerWorkflow() {
        delay(1000)
        lock.lock()
        val descriptors = virtualFileSystem.watches
        lock.unlock()
        return
    }

    public val inputState: MutableMap<FileDescriptor, String> = mutableMapOf()
}