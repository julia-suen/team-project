
import view.MapView;

public class Main {
    public static void main(String[] args) {
        // Fix for Java + HTTPS issues
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2,TLSv1.3");
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("http.agent", "Mozilla/5.0");

        new MapView();
    }
}
