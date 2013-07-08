package be.crydust.uploader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import net.schmizz.sshj.sftp.FileAttributes;

/**
 *
 * @author kristof
 */
class FileStats {
    public long mtime = 0L;
    public long size = 0L;

    public FileStats() {
    }

    public FileStats(long mtime, long size) {
        this.mtime = mtime;
        this.size = size;
    }

    public FileStats(FileTime mtime, long size) {
        this(mtime.to(TimeUnit.SECONDS), size);
    }

    public FileStats(BasicFileAttributes attrs) {
        this(attrs.lastModifiedTime(), attrs.size());
    }

    public FileStats(Path file) throws IOException {
        this(Files.getLastModifiedTime(file), Files.size(file));
    }

    public FileStats(FileAttributes stat) {
        if (stat != null) {
            mtime = stat.getMtime();
            size = stat.getSize();
        }
    }

    public boolean isNewerThan(FileStats other) {
        return this.mtime > other.mtime || this.size != other.size;
    }

    public long getAtime() {
        return mtime;
    }

    public void setAtime(long mtime) {
        this.mtime = mtime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FileStats{" + "mtime=" + mtime + ", size=" + size + '}';
    }
    
}
