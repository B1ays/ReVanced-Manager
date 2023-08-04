package ru.blays.simpledocument

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import ru.blays.simpledocument.Utils.buildDocumentUriUsingTree
import ru.blays.simpledocument.Utils.getDocumentId
import ru.blays.simpledocument.Utils.getTreeDocumentId
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date

abstract class SimpleDocument {

    internal abstract var parent: SimpleDocument?
    abstract val uri: Uri
    abstract val name: String?
    abstract val type: String?
    abstract val path: String?
    abstract val length: Long?

    abstract val parentDocument: SimpleDocument?

    abstract val isExists: Boolean
    abstract val isDocument: Boolean
    abstract val isFolder: Boolean
    abstract val isVirtual: Boolean

    abstract val lastModifiedLong: Long
    abstract val lastModifiedDate: Date

    abstract val canRead: Boolean
    abstract val canWrite: Boolean

    abstract val inputStream: FileInputStream?
    abstract val outputStream: FileOutputStream?
    abstract val parcelFileDescriptor: ParcelFileDescriptor?

    abstract val documents: Array<SimpleDocument?>

    abstract fun delete(): Boolean
    abstract fun rename(newName: String, extension: String? = null): Boolean
    abstract fun createFolder(name: String): SimpleDocument?
    abstract fun createDocument(name: String, extension: String, mimeType: String): SimpleDocument?
    abstract fun findFile(fullName: String): SimpleDocument?
    abstract fun getOrCreateDocument(name: String, extension: String, mimeType: String): SimpleDocument?
    abstract fun copyTo(file: File): Boolean
    abstract fun copyTo(document: SimpleDocument): Boolean

    abstract fun open()

    companion object {

        fun isDocumentUri(uri: Uri, context: Context): Boolean {
            return DocumentsContract.isDocumentUri(context, uri)
        }

        fun fromTreeUri(uri: Uri, context: Context): SimpleDocumentTree? {
            var documentId: String = getTreeDocumentId(uri)
            if (isDocumentUri(uri, context)) {
                documentId = getDocumentId(uri)
            }
            val treeDocumentUri: Uri = buildDocumentUriUsingTree(uri, documentId)
            return try {
                SimpleDocumentTree(treeDocumentUri, context)
            } catch (e: Exception) {
                null
            }
        }

        fun fromSingleUri(uri: Uri, context: Context): SimpleDocumentSingle {
            return SimpleDocumentSingle(uri, context)
        }

        fun Uri.toTreeSimpleDocument(context: Context): SimpleDocument {
            return SimpleDocumentTree(this, context)
        }

        fun Uri.toSingleDocument(context: Context): SimpleDocument {
            return SimpleDocumentSingle(this, context)
        }
    }
}