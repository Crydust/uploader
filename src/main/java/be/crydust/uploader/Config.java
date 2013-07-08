package be.crydust.uploader;

/**
 *
 * @author kristof
 */
public class Config {
    public String server = "example.com";
    public String hostKeyVerifier = "00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00";
    public String username = "alice";
    public String password = "123456";
    public String src = "/tmp";
    public String dest = "/home/alice/default_www";
    public int port = 22;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getHostKeyVerifier() {
        return hostKeyVerifier;
    }

    public void setHostKeyVerifier(String hostKeyVerifier) {
        this.hostKeyVerifier = hostKeyVerifier;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }
    
}
