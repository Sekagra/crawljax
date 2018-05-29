package com.crawljax.cli;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.ProxyConfiguration;
import com.crawljax.plugins.crawloverview.CrawlOverview;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.proxy.CaptureType;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BatchRunner {

    /**
     * Entry point
     */
    public static void main(String[] args) {
        // read command line args: --scanlist=... --outdir=... --depth=d --states=n

        BatchParameterParser parameters = new BatchParameterParser(args);

        List<String> scanUrlList = parameters.getScanList();
        String outputDirectory = parameters.getOutputDir();
        String outputFilename = "result.json";
        String proxyUrl = parameters.getProxyUrl();
        int proxyPort = parameters.getProxyPort();
        int maxDepth = parameters.getMaxDepth();
        int maxNumberOfStates = parameters.getNumberOfStates();

        for(String url : scanUrlList) {
            String domainName = "";
            try {
                domainName = new URL(url).getHost();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            }

            File dstDir = new File(outputDirectory + "/" + domainName);
            deleteFolder(dstDir, false);

            CrawljaxConfiguration.CrawljaxConfigurationBuilder builder = CrawljaxConfiguration
                    .builderFor(url)
                    .setProxyConfig(ProxyConfiguration.manualProxyOn(proxyUrl, proxyPort))
                    .setMaximumStates(maxNumberOfStates)
                    .setMaximumDepth(maxDepth)
                    .setMaximumRunTime(60, TimeUnit.MINUTES)
                    .setOutputDirectory(dstDir);

            BrowserMobProxyServer proxy = new BrowserMobProxyServer();
            proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_CONTENT);
            proxy.newHar("0");

            builder.addPlugin(new CrawlOverview(null, proxy));
            proxy.start(proxyPort);
            builder.setBrowserConfig(new BrowserConfiguration(EmbeddedBrowser.BrowserType.CHROME)); //fixed to chrome for now
            CrawljaxConfiguration config = builder.build();
            CrawljaxRunner crawljax = new CrawljaxRunner(config);
            crawljax.call();
            proxy.stop();
        }
    }

    public static void deleteFolder(File folder, boolean deleteOwnFolderWhenFinished) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f, true);
                } else {
                    f.delete();
                }
            }
        }
        if (deleteOwnFolderWhenFinished) {
            folder.delete();
        }
    }
}
