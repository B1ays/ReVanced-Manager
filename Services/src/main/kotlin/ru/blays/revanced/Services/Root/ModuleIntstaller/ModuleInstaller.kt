package ru.blays.revanced.Services.Root.ModuleIntstaller

import android.os.Environment
import android.util.Log
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import ru.blays.revanced.Services.Root.ModuleIntstaller.LogAdapter.LogAdapter
import ru.blays.revanced.Services.Root.ModuleIntstaller.LogAdapter.LogAdapterDefault
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

private const val DL_FOLDER_NAME = "ReVanced Manager"

@Suppress("MemberVisibilityCanBePrivate")
class ModuleInstaller(
    private val logAdapter: LogAdapter = LogAdapterDefault()
) {

    val statusFlow = MutableStateFlow<Status>(Status.STARTING)

    suspend fun install(module: Module, file: File) = coroutineScope {

        val logPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), DL_FOLDER_NAME)
        val logFilePath = File(logPath, "InstallLog_${LocalDate.now()}_${LocalTime.now()}.txt").absolutePath

        val apkPath = file.absolutePath

        // post statusFlow & write to log
        postStatusAndWriteToLog(Status.STARTING, logFilePath)

        // Create path to app module
        val pathToModule = pathToModule(module.moduleId)

        // post statusFlow & write to log
        postStatusAndWriteToLog(Status.CHECK_MODULE_EXISTS, logFilePath)

        // check module exists
        val isModuleExist = checkModuleExist(pathToModule)

        val isModuleFileExist = checkModuleFilesExist(pathToModule)

        // if module exists - update files
        if (isModuleExist && isModuleFileExist) {

            // post statusFlow & write to log
            postStatusAndWriteToLog(Status.GENERATE_SERVICE_SH, logFilePath)

            // generate new service
            val serviceSh = generateServicesSh(pathToModule, module.packageName)

            // abort install in case of error
            if (serviceSh.isEmpty()) {
                postStatusAndWriteToLog(Status.ERROR.addError("Unable to generate new service.sh for update"), logFilePath)
                return@coroutineScope
            }

            // post statusFlow & write to log
            postStatusAndWriteToLog(Status.UPDATE_MODULE, logFilePath)

            // run module update function
            val updateModule = updateModule(path = pathToModule, newApkPath = apkPath, newServicesSh = serviceSh)

            // abort install in case of error
            if (!updateModule) {
                postStatusAndWriteToLog(
                    Status.ERROR.addError("Unable to update module"),
                    logFilePath
                )
                return@coroutineScope
            }

            postStatusAndWriteToLog(Status.COMPLETE, logFilePath)

        }
        // create new module
        else {

            // post statusFlow & write to log
            postStatusAndWriteToLog(Status.CREATE_MODULE_FOLDER, logFilePath)

            // create module folder
            val createModuleFolder = createModuleFolder(pathToModule)

            // abort install in case of error
            if (!createModuleFolder) {
                postStatusAndWriteToLog(
                    Status.ERROR.addError("Can't create module folder"),
                    logFilePath
                )
                return@coroutineScope
            }

            // post statusFlow & write to log
            postStatusAndWriteToLog(Status.COPY_APK, logFilePath)

            // copy mod apk to module folder as base.apk
            val copyApkToModuleFolder = moveApkToModuleFolder(modulePath = pathToModule, apkPath = apkPath)

            // abort install in case of error
            if (!copyApkToModuleFolder) {
                postStatusAndWriteToLog(
                    Status.ERROR.addError("Can't copy apk to module folder"),
                    logFilePath
                )
                return@coroutineScope
            }

            // post statusFlow & write to log
            postStatusAndWriteToLog(Status.GENERATE_MODULE_PROP, logFilePath)

            // generate service.sh & module.prop
            val moduleProp = generateModuleProp(moduleId = module.moduleId, moduleName = module.moduleName)

            // post statusFlow & write to log
            postStatusAndWriteToLog(Status.GENERATE_SERVICE_SH, logFilePath)

            val serviceSh = generateServicesSh(path = pathToModule, packageName = module.packageName)

            // abort install in case of error
            if (serviceSh.isEmpty()) {
                postStatusAndWriteToLog(
                    Status.ERROR.addError("Can't generate service.sh"),
                    logFilePath
                )
                return@coroutineScope
            }

            // post statusFlow & write to log
            postStatusAndWriteToLog(Status.WRITE_MODULE_FILES, logFilePath)

            // write service.sh & module.prop to module folder
            val writeServiceSh = writeServiceSh(modulePath = pathToModule, serviceSh = serviceSh)
            val writeModuleProp = writeModuleProp(modulePath = pathToModule, moduleProp = moduleProp)

            // abort install in case of error
            if (!writeServiceSh || !writeModuleProp) {
                postStatusAndWriteToLog(
                    Status.ERROR.addError("Unable to write service.sh & module.prop"),
                    logFilePath
                )
                return@coroutineScope
            }

            postStatusAndWriteToLog(Status.COMPLETE, logFilePath)
        }
    }

    fun delete(module: Module): Boolean {

        // Create path to app module
        val pathToModule = pathToModule(module.moduleId)

        return deleteFile(pathToModule)
    }

    private fun updateModule(path: String, newApkPath: String, newServicesSh: String): Boolean {

        val replaceBaseApk = moveApkToModuleFolder(modulePath = path, apkPath = newApkPath)

        if (!replaceBaseApk) return false

        val writeServiceSh = writeServiceSh(modulePath = path, serviceSh = newServicesSh)

        if (!writeServiceSh) return false

        return true
    }

    // Get full path to module folder
    // Module.moduleId -> path
    private val pathToModule: (moduleID: String) -> String
        get() = { moduleID ->  "/data/adb/modules/$moduleID/" }

    private fun checkModuleExist(path: String): Boolean {
        val checkModuleDirExists = Shell.cmd("test -e $path").exec()
        val checkModuleFileExists = checkModuleFilesExist(path)
        return checkModuleDirExists.isSuccess && checkModuleFileExists
    }

    fun checkModuleExist(module: Module): Boolean {
        val path = pathToModule(module.moduleId)
        val checkModuleDirExists = Shell.cmd("test -e $path").exec()
        val checkModuleFileExists = checkModuleFilesExist(path)
        return checkModuleDirExists.isSuccess && checkModuleFileExists
    }


    private fun checkModuleFilesExist(path: String): Boolean {
        val checkBaseApk = Shell.cmd("test -e ${path}base.apk").exec()
        val checkServiceSh = Shell.cmd("test -e ${path}service.sh").exec()
        val checkModuleProp = Shell.cmd("test -e ${path}module.prop").exec()
        return checkBaseApk.isSuccess && checkServiceSh.isSuccess && checkModuleProp.isSuccess
    }

    private fun createModuleFolder(path: String): Boolean {
        val create = Shell.cmd("mkdir -p $path").exec()
        return create.isSuccess
    }

    private fun generateServicesSh(path: String, packageName: String): String {

        val pathToOrigApp =
            Shell.cmd("pm path $packageName | cut -d \":\" -f2- | grep \"base.apk\"").exec()

        Log.d("ModuleInstaller", pathToOrigApp.out.firstOrNull().toString())

        return """#!/system/bin/sh
while [ "'"$(getprop sys.boot_completed)"'" != "1" ];
do sleep 1;
done;

# Mount app
mount -o bind ${path}base.apk ${pathToOrigApp.out.firstOrNull()}"""
    }

    private fun generateModuleProp(moduleId: String, moduleName: String): String {
        return """id=$moduleId
name=$moduleName
version=1
versionCode=1
author=Blays
description=ReVanced Manager module."""
    }

    private fun writeServiceSh(modulePath: String, serviceSh: String): Boolean {
        val filePath = "${modulePath}service.sh"
        val writeServiceSh = Shell.cmd("echo \"$serviceSh\" > $filePath").exec()
        val applyChmod = applyChmod(filePath = filePath, 644)
        return writeServiceSh.isSuccess && applyChmod
    }

        private fun writeModuleProp(moduleProp: String, modulePath: String) : Boolean {
        val filePath = "${modulePath}module.prop"
        val writeModuleProp = Shell.cmd("echo \"$moduleProp\" > $filePath").exec()
        val applyChmod = applyChmod(filePath = filePath, 644)
        return writeModuleProp.isSuccess && applyChmod
    }

    private fun moveApkToModuleFolder(modulePath: String, apkPath: String): Boolean {
        val filePath = "${modulePath}base.apk"
        val moveBaseApk = Shell.cmd("cp -f '${apkPath}' $filePath").exec()
        val applyChmod = applyChmod(filePath = filePath, 644)
        return moveBaseApk.isSuccess && applyChmod
    }

    @Suppress("SameParameterValue")
    private fun applyChmod(filePath: String, chmod: Int): Boolean {
        val apply = Shell.cmd("chmod $chmod $filePath").exec()
        return apply.isSuccess
    }

    private fun deleteFile(filePath: String): Boolean {
        val delete = Shell.cmd("rm -rf $filePath").exec()
        return delete.isSuccess
    }

    private fun postStatusAndWriteToLog(status: Status, logPath: String) {
        val fullMessage = with(status) {
            if (error.isEmpty()) message
            else "$message : error $error"
        }
        logAdapter.i(fullMessage)
        this.statusFlow.tryEmit(status)
        Shell.cmd(" echo \"${fullMessage}\" >> $logPath").exec()
    }

    enum class Module {

        YOUTUBE {
            override val moduleName = "YouTube ReVanced from manager"
            override val moduleId = "Manager_YouTube"
            override val packageName = "com.google.android.youtube"
        },

        YOUTUBE_MUSIC {
            override val moduleName = "YouTube Music from manager"
            override val moduleId = "Manager_YouTube_Music"
            override val packageName = "com.google.android.apps.youtube.music"
        };

        abstract val moduleName: String
        abstract val moduleId: String
        abstract val packageName: String
    }

    enum class Status(val message: String, var error: String = "") {
        STARTING("Start install"),
        CHECK_MODULE_EXISTS("Check module exists"),
        CREATE_MODULE("Create module"),
        UPDATE_MODULE("Update module"),
        CREATE_MODULE_FOLDER("Create module folder"),
        GENERATE_MODULE_PROP("Generate module.prop"),
        GENERATE_SERVICE_SH("Generate service.sh"),
        COPY_APK("Copy APK"),
        WRITE_MODULE_FILES("Write module files"),
        COMPLETE("Install complete"),
        ERROR("Error");

        fun addError(error: String) = this.apply {
            this.error = error
        }
    }

}