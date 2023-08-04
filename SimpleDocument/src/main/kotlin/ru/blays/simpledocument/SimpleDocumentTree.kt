package ru.blays.simpledocument

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.provider.DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT
import ru.blays.simpledocument.Utils.canRead
import ru.blays.simpledocument.Utils.canWrite
import ru.blays.simpledocument.Utils.closeQuietly
import ru.blays.simpledocument.Utils.documentType
import ru.blays.simpledocument.Utils.exists
import ru.blays.simpledocument.Utils.getFlags
import ru.blays.simpledocument.Utils.length
import ru.blays.simpledocument.Utils.queryForLong
import ru.blays.simpledocument.Utils.queryForString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date

class SimpleDocumentTree internal constructor(
    override var uri: Uri,
    private val context: Context
): SimpleDocument() {

    private val contentResolver = context.contentResolver

    internal constructor(parent: SimpleDocument, uri: Uri, context: Context): this(uri, context) {
        this.parent = parent
    }

    override var parent: SimpleDocument? = null

    override val name: String?
        get() = queryForString(
            context,
            uri,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            null
        )
    override val type: String?
        get() = documentType(context, uri)
    override val path: String?
        get() = uri.lastPathSegment
    override val length: Long
        get() = length(context, uri)
    override val parentDocument: SimpleDocument?
        get() = parent
    override val isExists: Boolean
        get() = exists(context, uri)
    override val isDocument: Boolean
        get() = !(DocumentsContract.Document.MIME_TYPE_DIR == type || type?.isEmpty() == true)
    override val isFolder: Boolean
        get() = DocumentsContract.Document.MIME_TYPE_DIR == type
    override val isVirtual: Boolean
        get() = isDocument && getFlags(
            context,
            uri
        ) and FLAG_VIRTUAL_DOCUMENT != 0
    override val lastModifiedLong: Long
        get() = queryForLong(
            context,
            uri,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
            0
        )
    override val lastModifiedDate: Date
        get() = TODO("Not yet implemented")
    override val canRead: Boolean
        get() = canRead(context, uri)
    override val canWrite: Boolean
        get() = canWrite(context, uri)
    override val inputStream: FileInputStream?
        get() = try {
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "rw")
            ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor)
        } catch (e: Exception) {
            null
        }
    override val outputStream: FileOutputStream?
        get() = try {
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "rw")
            ParcelFileDescriptor.AutoCloseOutputStream(fileDescriptor)
        } catch (e: Exception) {
            null
        }
    override val parcelFileDescriptor: ParcelFileDescriptor
        get() = throw RuntimeException("Can't read parcel file descriptor")

    override val documents: Array<SimpleDocument?>
        get() {
            val resolver: ContentResolver = context.contentResolver
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                uri,
                DocumentsContract.getDocumentId(uri)
            )
            val results = ArrayList<Uri>()
            var cursor: Cursor? = null
            try {
                cursor = resolver.query(
                    childrenUri,
                    arrayOf(
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID
                    ),
                    null,
                    null,
                    null
                )
                while (cursor?.moveToNext() == true) {
                    val documentId = cursor.getString(0)
                    val documentUri = DocumentsContract.buildDocumentUriUsingTree(
                        uri,
                        documentId
                    )
                    results.add(documentUri)
                }
            } catch (_: Exception) {
            } finally {
                closeQuietly(cursor)
            }
            val result = results.toTypedArray<Uri>()
            val resultFiles: Array<SimpleDocument?> = arrayOfNulls(result.size)
            result.forEachIndexed { index, uri ->
                resultFiles[index] = SimpleDocumentTree(this, uri, context)
            }
            return resultFiles
        }

    override fun delete(): Boolean {
        return try {
            DocumentsContract.deleteDocument(
                context.contentResolver,
                uri
            )
        } catch (e: Exception) {
            false
        }
    }

    override fun rename(newName: String, extension: String?): Boolean {
        return try {
            val result = DocumentsContract.renameDocument(
                context.contentResolver,uri, newName + (extension ?: "")
            )
            if (result != null) {
                uri = result
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun createFolder(name: String): SimpleDocument? {
        val result = createFile(
            context,
            uri,
            DocumentsContract.Document.MIME_TYPE_DIR,
            name
        )
        return if (result != null) SimpleDocumentTree(this, result, context) else null
    }

    override fun createDocument(name: String, extension: String, mimeType: String): SimpleDocument? {
        val result = createFile(
            context,
            uri,
            mimeType,
            name + extension
        )
        return if (result != null) SimpleDocumentSingle(this, result, context) else null
    }

    override fun findFile(fullName: String): SimpleDocument? {
        return documents.firstOrNull {
            it?.name?.contains(fullName) == true
        }
    }

    override fun getOrCreateDocument(
        name: String,
        extension: String,
        mimeType: String
    ): SimpleDocument? {
        return findFile(name)
            ?: createDocument(name, extension, mimeType)
    }

    override fun copyTo(file: File): Boolean {
        val inputStream = inputStream ?: return false
        return if (file.canWrite()) {
            val outputStream = file.outputStream()
            inputStream.copyTo(outputStream)
            true
        } else false
    }

    override fun copyTo(document: SimpleDocument): Boolean {
        val inputStream = inputStream ?: return false
        val outputStream = document.outputStream ?: return false
        return if (document.canWrite) {
            inputStream.copyTo(outputStream)
            true
        } else false
    }

    override fun open() {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun createFile(
        context: Context,
        uri: Uri,
        mimeType: String,
        displayName: String
    ): Uri? {
        return try {
            DocumentsContract.createDocument(
                context.contentResolver,
                uri,
                mimeType,
                displayName
            )
        } catch (e: Exception) {
            null
        }
    }
}