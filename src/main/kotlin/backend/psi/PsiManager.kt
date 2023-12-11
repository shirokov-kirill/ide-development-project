package backend.psi

import backend.psi.lexer.Lexer
import backend.psi.lexer.idelang.IDELangLexer
import backend.vfs.Vfs
import backend.vfs.descriptors.FileDescriptor

class PsiManager(private val virtualFileSystem: Vfs) {
    private val lexer: Lexer = IDELangLexer(this)
    // TODO implement parser

    public val inputState: MutableMap<FileDescriptor, String> = mutableMapOf()

    /*
     * Only call from a new Thread
     */
    fun updateState(): Boolean {
        val readLock = virtualFileSystem.readWriteLock.readLock()
        var changesExist = false
        readLock.lock()
        val descriptors = virtualFileSystem.watches
        if(inputState.size < descriptors.size) {
            changesExist = true
        }

        // TODO add changes in text
        for(descriptor in descriptors) {
            inputState[descriptor] = descriptor.getFile().getFileContent() ?: ""
        }
        readLock.unlock()
        return changesExist
    }
}