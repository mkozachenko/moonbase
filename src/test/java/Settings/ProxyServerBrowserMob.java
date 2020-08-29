package Settings;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;

import java.util.concurrent.TimeUnit;

public class ProxyServerBrowserMob {

    public BrowserMobProxyServer startProxy(){
        BrowserMobProxyServer proxyServer;
        proxyServer = new BrowserMobProxyServer();
        proxyServer.setTrustAllServers(true);   //без этого на дев-площадках нет соединения (ERR_TUNNEL_CONNECTION_FAILED)
        proxyServer.waitForQuiescence(1, 120, TimeUnit.SECONDS);
        proxyServer.setHarCaptureTypes(CaptureType.RESPONSE_CONTENT, CaptureType.RESPONSE_HEADERS);
        proxyServer.enableHarCaptureTypes(CaptureType.RESPONSE_CONTENT, CaptureType.RESPONSE_HEADERS);
        proxyServer.start();
        System.out.println(">>>PROXY STARTED AT "+proxyServer.getPort());
        return proxyServer;
    }

    public Proxy setSeleniumProxy(BrowserMobProxyServer proxyServer){
        Proxy seleniumProxy;
        seleniumProxy = ClientUtil.createSeleniumProxy(proxyServer);
        seleniumProxy.setHttpProxy("localhost:"+proxyServer.getPort());
        seleniumProxy.setSslProxy("localhost:"+proxyServer.getPort());
        return seleniumProxy;
    }

    public void stopBrowserMobProxy(BrowserMobProxyServer proxyServer){
        System.out.println("STOPPING SERVER ON PORT: "+proxyServer.getPort());
        if (!proxyServer.isStopped()){
            proxyServer.abort();
        }
        System.out.println("PROXY SERVER STOPPED: "+proxyServer.isStopped());
    }
}
