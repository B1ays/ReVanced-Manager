package ru.blays.simpledocument.Utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract

internal fun queryForString(
    context: Context, self: Uri,
    column: String,
    defaultValue: String?
): String? {
    val resolver = context.contentResolver
    var c: Cursor? = null
    return try {
        c = resolver.query(self, arrayOf(column), null, null, null)
        if (c!!.moveToFirst() && !c.isNull(0)) {
            c.getString(0)
        } else {
            /*defaultValue*/ "Can't find type"
        }
    } catch (e: Exception) {
        /*defaultValue*/ "Exception: $e"
    } finally {
        closeQuietly(c)
    }
}

internal fun queryForInt(
    context: Context,
    self: Uri,
    column: String,
    defaultValue: Int
): Int {
    return queryForLong(context, self, column, defaultValue.toLong()).toInt()
}

internal fun queryForLong(
    context: Context, self: Uri, column: String,
    defaultValue: Long
): Long {
    val resolver = context.contentResolver
    var cursor: Cursor? = null
    return try {
        cursor = resolver.query(self, arrayOf(column), null, null, null)
        if (cursor?.moveToFirst() == true && !cursor.isNull(0)) {
            cursor.getLong(0)
        } else {
            defaultValue
        }
    } catch (e: java.lang.Exception) {
        defaultValue
    } finally {
        closeQuietly(cursor)
    }
}

internal fun closeQuietly(closeable: AutoCloseable?) {
    if (closeable != null) {
        try {
            closeable.close()
        } catch (rethrown: RuntimeException) {
            throw rethrown
        } catch (ignored: java.lang.Exception) {
        }
    }
}

internal fun exists(context: Context, uri: Uri?): Boolean {
    if (uri == null) return false
    val resolver = context.contentResolver
    var cursor: Cursor? = null
    return try {
        cursor = resolver.query(
            uri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID
            ),
            null,
            null,
            null
        )
        cursor?.let { it.count > 0 } ?: false
    } catch (e: Exception) {
        false
    } finally {
        closeQuietly(cursor)
    }
}

internal fun getFlags(context: Context, uri: Uri): Int {
    return queryForInt(
        context,
        uri,
        DocumentsContract.Document.COLUMN_FLAGS,
        0
    )
}

internal fun canRead(context: Context, uri: Uri): Boolean {
    // Ignore if grant doesn't allow read
    if (context.checkCallingOrSelfUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        return false
    }

    // Ignore documents without MIME
    return getRawType(context, uri)?.isNotEmpty() == true
}

private fun getRawType(context: Context, self: Uri): String? {
    return queryForString(
        context,
        self,
        DocumentsContract.Document.COLUMN_MIME_TYPE,
        null
    )
}

internal fun canWrite(context: Context, uri: Uri): Boolean {
    // Ignore if grant doesn't allow write
    if (
        context.checkCallingOrSelfUriPermission(
            uri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return false
    }
    val type: String? = getRawType(context, uri)
    val flags: Int = queryForInt(
        context,
        uri,
        DocumentsContract.Document.COLUMN_FLAGS,
        0
    )

    // Ignore documents without MIME
    if (type?.isEmpty() == true) {
        return false
    }

    // Deletable documents considered writable
    if (flags and DocumentsContract.Document.FLAG_SUPPORTS_DELETE != 0) {
        return true
    }
    if (
        DocumentsContract.Document.MIME_TYPE_DIR == type &&
        flags and DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE != 0
    ) {
        // Directories that allow create considered writable
        return true
    } else if (type?.isEmpty() != true
        && flags
        and DocumentsContract.Document.FLAG_SUPPORTS_WRITE != 0
    ) {
        // Writable normal files considered writable
        return true
    }
    return false
}

internal fun documentType(context: Context, uri: Uri): String? {
    val rawType = queryForString(
        context,
        uri,
        DocumentsContract.Document.COLUMN_MIME_TYPE,
        null
    )
    return if (DocumentsContract.Document.MIME_TYPE_DIR == rawType) {
        "Document"
    } else {
        rawType
    }
}

internal fun length(context: Context, self: Uri): Long {
    return queryForLong(
        context,
        self,
        DocumentsContract.Document.COLUMN_SIZE,
        0
    )
}