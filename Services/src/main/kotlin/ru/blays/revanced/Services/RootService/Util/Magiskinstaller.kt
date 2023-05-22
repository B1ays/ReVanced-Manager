package ru.blays.revanced.Services.RootService.Util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

object MagiskInstaller {

    val status = MutableStateFlow<Status>(Status.STARTING)

    fun install(module: Module, file: File, context: Context) {

        val logPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val logFilePath = File(logPath, "InstallLog_${LocalDate.now()}_${LocalTime.now()}.txt").absolutePath
        Log.d("Masisk Installer", "logFilePath: $logFilePath")

        val apkPath = file.absolutePath

        Log.d("Masisk Installer", "ApkPath: $apkPath")

        // post status & write to log
        postStatusAndWriteToLog(Status.STARTING, logFilePath)

        // Create path to app module
        val pathToModule = getPathToModule(module.moduleId)

        // post status & write to log
        postStatusAndWriteToLog(Status.CHECK_MODULE_EXISTS, logFilePath)

        // check module exists
        val isModuleExist = checkModuleExist(pathToModule)

        val isModuleFileExist = checkModuleFilesExist(pathToModule)

        // if module exists - update files
        if (isModuleExist && isModuleFileExist) {

            // post status & write to log
            postStatusAndWriteToLog(Status.GENERATE_SERVICE_SH, logFilePath)

            // generate new service
            val serviceSh = generateServicesSh(pathToModule, module.packageName)

            // abort install in case of error
            if (serviceSh.isEmpty()) {
                status.tryEmit(Status.ERROR)
                return
            }

            // post status & write to log
            postStatusAndWriteToLog(Status.UPDATE_MODULE, logFilePath)

            // run module update function
            val updateModule = updateModule(path = pathToModule, newApkPath = apkPath, newServicesSh = serviceSh)

            // abort install in case of error
            if (!updateModule) {
                status.tryEmit(Status.ERROR)
                return
            }

        }
        // create new module
        else {

            // post status & write to log
            postStatusAndWriteToLog(Status.CREATE_MODULE_FOLDER, logFilePath)

            // create module folder
            val createModuleFolder = createModuleFolder(pathToModule)

            // abort install in case of error
            if (!createModuleFolder) {
                status.tryEmit(Status.ERROR)
                return
            }

            // post status & write to log
            postStatusAndWriteToLog(Status.COPY_APK, logFilePath)

            // copy mod apk to module folder as base.apk
            val copyApkToModuleFolder = moveApkToModuleFolder(modulePath = pathToModule, apkPath = apkPath)

            // abort install in case of error
            if (!copyApkToModuleFolder) {
                status.tryEmit(Status.ERROR)
                return
            }

            // post status & write to log
            postStatusAndWriteToLog(Status.GENERATE_MODULE_PROP, logFilePath)

            // generate service.sh & module.prop
            val moduleProp = generateModuleProp(moduleId = module.moduleId, moduleName = module.moduleName)

            Log.d("MagiskInstaller", moduleProp)

            // post status & write to log
            postStatusAndWriteToLog(Status.GENERATE_SERVICE_SH, logFilePath)

            val serviceSh = generateServicesSh(path = pathToModule, packageName = module.packageName)

            Log.d("MagiskInstaller", serviceSh)

            // abort install in case of error
            if (serviceSh.isEmpty()) {
                status.tryEmit(Status.ERROR)
                return
            }

            // post status & write to log
            postStatusAndWriteToLog(Status.WRITE_MODULE_FILES, logFilePath)

            // write service.sh & module.prop to module folder
            val writeServiceSh = writeServiceSh(modulePath = pathToModule, serviceSh = serviceSh)
            val writeModuleProp = writeModuleProp(modulePath = pathToModule, moduleProp = moduleProp)

            // abort install in case of error
            if (!writeServiceSh || !writeModuleProp) {
                status.tryEmit(Status.ERROR)
                return
            }
        }
    }

    private fun updateModule(path: String, newApkPath: String, newServicesSh: String): Boolean {

        val replaceBaseApk = moveApkToModuleFolder(modulePath = path, apkPath = newApkPath)

        if (!replaceBaseApk) return false

        val writeServiceSh = writeServiceSh(modulePath = path, serviceSh = newServicesSh)

        if (!writeServiceSh) return false

        return true
    }

    // Get full path to module folder
    private val getPathToModule: (String) -> String = { "/data/adb/modules/$it/" }

    private fun checkModuleExist(path: String): Boolean {
        val check = Shell.cmd("test -e $path").exec()
        return check.isSuccess
    }

    private fun checkModuleFilesExist(path: String): Boolean {
        val checkBaseApk = Shell.cmd("test -e ${path}base.apk").exec()
        val checkServiceSh = Shell.cmd("test -e ${path}service.sh").exec()
        val checkModuleProp = Shell.cmd("test -e ${path}module.prop").exec()
        return checkBaseApk.isSuccess && checkServiceSh.isSuccess && checkModuleProp.isSuccess
    }

    private fun createModuleFolder(path: String): Boolean {
        val create = Shell.cmd("mkdir $path").exec()
        return create.isSuccess
    }

    private fun generateServicesSh(path: String, packageName: String): String {

        val pathToOrigApp =
            Shell.cmd("pm path $packageName | cut -d \":\" -f2- | grep \"base.apk\"").exec()

        Log.d("MagiskInstaller", pathToOrigApp.out.firstOrNull().toString())

        /*if (!pathToOrigApp.isSuccess) return ""*/

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
        val writeServiceSh = Shell.cmd("echo \"$serviceSh\" > ${modulePath}service.sh").exec()
        return writeServiceSh.isSuccess
    }

    private fun writeModuleProp(moduleProp: String, modulePath: String) : Boolean {
        val writeModuleProp = Shell.cmd("echo \"$moduleProp\" > ${modulePath}module.prop").exec()
        return writeModuleProp.isSuccess
    }

    private fun moveApkToModuleFolder(modulePath: String, apkPath: String): Boolean {
        val moveBaseApk = Shell.cmd("cp -f $apkPath ${modulePath}base.apk").exec()
        val applyChmod = Shell.cmd("chmod 644 ${modulePath}base.apk").exec()
        Log.d("MagiskInstaller", moveBaseApk.out.toString())
        return moveBaseApk.isSuccess
    }


    private fun postStatusAndWriteToLog(status: Status, logPath: String) {
        Log.i("MagiskInstaller", status.message)
        this.status.tryEmit(status)
        Shell.cmd(" echo \"${status.message}\" >> $logPath").exec()
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

    enum class Status(val message: String) {
        STARTING("Start install"),
        CHECK_MODULE_EXISTS("Check module exists"),
        CREATE_MODULE("Create module"),
        UPDATE_MODULE("Update module"),
        CREATE_MODULE_FOLDER("Create module folder"),
        GENERATE_MODULE_PROP("Generate module.prop"),
        GENERATE_SERVICE_SH("Generate service.sh"),
        COPY_APK("Copy APK"),
        WRITE_MODULE_FILES("Write module files"),
        ERROR("Error")
    }

}