package ru.blays.simpledocument.Utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract

    fun isDocumentUri(context: Context?, uri: Uri?): Boolean {
        return DocumentsContract.isDocumentUri(context, uri)
    }

    fun getDocumentId(documentUri: Uri?): String {
        return DocumentsContract.getDocumentId(documentUri)
    }

    fun getTreeDocumentId(documentUri: Uri?): String {
        return DocumentsContract.getTreeDocumentId(documentUri)
    }

    fun buildDocumentUriUsingTree(treeUri: Uri?, documentId: String?): Uri {
        return DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)
    }