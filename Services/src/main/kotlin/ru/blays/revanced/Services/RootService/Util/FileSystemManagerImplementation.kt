package ru.blays.revanced.Services.RootService.Util

import com.topjohnwu.superuser.nio.ExtendedFile
import com.topjohnwu.superuser.nio.FileSystemManager
import java.io.File
import java.nio.channels.FileChannel

internal class FileSystemManagerImplementation : FileSystemManager() {


    private val local = getLocal()
    override fun getFile(pathname: String): ExtendedFile {
        return local.getFile(pathname)
    }

    override fun getFile(parent: String?, child: String): ExtendedFile {
        return local.getFile(parent, child)
    }

    override fun openChannel(file: File, mode: Int): FileChannel {
        return local.openChannel(file, mode)
    }
}