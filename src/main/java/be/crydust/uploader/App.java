package be.crydust.uploader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author kristof
 */
public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final boolean DEBUG = false;
    private static final String CONFIG_FILE = "/uploader.properties";

    public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        logger.info("greetings");
        Config config = loadConfig();
        //storeConfig(config);
        final Path src = Paths.get(config.src);
        final Path dest = Paths.get(config.dest);
        final Map<Path, FileStats> localfiles = getLocalFileStats(src);
        uploadFiles(config, localfiles, src, dest);
    }

    private static Map<Path, FileStats> getLocalFileStats(final Path src) throws IOException {
        final Map<Path, FileStats> localfiles = new LinkedHashMap<>();
        Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                String filename = file.getFileName().toString();
                if (filename.startsWith(".") || filename.endsWith("~")) {
                    //ignore
                } else {
                    //System.out.println("filename = " + filename);
                    localfiles.put(src.relativize(file), new FileStats(attrs));
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return localfiles;
    }

    private static Config loadConfig() throws IOException, IllegalAccessException, InvocationTargetException {
        Config config = new Config();
        Properties props = new Properties();
        try (InputStream input = App.class.getResourceAsStream(CONFIG_FILE)) {
            props.load(input);
            if (DEBUG) {
                props.list(System.out);
            }
            BeanUtils.populate(config, props);
        }
        return config;
    }

//    private static void storeConfig(Config config) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
//        Properties props = new Properties();
//        props.putAll(BeanUtils.describe(config));
//        URL resourceUrl = App.class.getResource(CONFIG_FILE);
//        File file = new File(resourceUrl.toURI());
//        try (OutputStream output = new FileOutputStream(file)) {
//            if (DEBUG) {
//                props.list(System.out);
//            }
//            props.store(output, null);
//        }
//    }

    private static void uploadFiles(Config config, final Map<Path, FileStats> localfiles, final Path src, final Path dest) throws IOException {
        final SSHClient ssh = new SSHClient();
        //ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.addHostKeyVerifier(config.hostKeyVerifier);
        ssh.connect(config.server, config.port);
        try {
            ssh.authPassword(config.username, config.password);
            try (SFTPClient sftp = ssh.newSFTPClient()) {
                for (Map.Entry<Path, FileStats> entry : localfiles.entrySet()) {
                    Path relativePath = entry.getKey();
                    FileStats localStats = entry.getValue();
                    Path localPath = src.resolve(relativePath);
                    String remotePathString = dest.resolve(relativePath).toString().replace('\\', '/');
                    FileAttributes attributes = sftp.statExistence(remotePathString);
                    FileStats remoteStats = new FileStats(attributes);
                    if (localStats.isNewerThan(remoteStats)) {
                        System.out.printf("uploading %s ...%n", relativePath);
                        sftp.put(new FileSystemFile(localPath.toFile()), remotePathString);
                        FileAttributes fileTime = new FileAttributes.Builder()
                                .withAtimeMtime(localStats.mtime, localStats.mtime)
                                .build();
                        sftp.setattr(remotePathString, fileTime);
                    } else {
                        System.out.printf("skipping  %s%n", relativePath);
                    }
                }
            }
        } finally {
            ssh.disconnect();
        }
    }
}
