package github.fatalcatharsis

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

open class FlatcFetchHelper {
    companion object {
        private const val FLATC_ZIP_URL : String = "https://github.com/google/flatbuffers/releases/download/v1.10.0/flatc_windows_exe.zip"
    }

    open fun fetch(outputDir : Path) {
        val url = URL(FLATC_ZIP_URL)
        val buffer = ByteArray(1024)

        Files.createDirectories(outputDir)

        ZipInputStream(url.openStream()).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val file = newFile(outputDir, entry)
                FileOutputStream(file.toFile()).use { fos ->
                    var len = zis.read(buffer)
                    while(len > 0) {
                        fos.write(buffer, 0, len)
                        len = zis.read(buffer)
                    }
                }
                entry = zis.nextEntry
            }
        }
    }

    private fun newFile(destinationDir: Path, zipEntry: ZipEntry): Path {
        val destFile = destinationDir.resolve(zipEntry.name).normalize()
        val destDirPath = destinationDir.normalize()

        if (!destFile.startsWith(destDirPath)) {
            throw IOException("Entry is outside of the target dir: " + zipEntry.name)
        }

        return destFile
    }
}
