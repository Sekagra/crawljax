package com.crawljax.cli;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.InputSpecification;
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
            String portNumber = "";
            try {
                domainName = new URL(url).getHost();
                portNumber = String.valueOf(new URL(url).getPort());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            }

            File dstDir = new File(outputDirectory + "/" + domainName + ":" + portNumber);
            deleteFolder(dstDir, false);

            CrawljaxConfiguration.CrawljaxConfigurationBuilder builder = CrawljaxConfiguration
                    .builderFor(url)
                    .setProxyConfig(ProxyConfiguration.manualProxyOn(proxyUrl, proxyPort))
                    .setMaximumStates(maxNumberOfStates)
                    .setMaximumDepth(maxDepth)
                    .setMaximumRunTime(60, TimeUnit.MINUTES)
                    .setOutputDirectory(dstDir);


            // Add additonal click rules
            builder.crawlRules().clickDefaultElements();
            builder.crawlRules().clickElementsInRandomOrder(true);

            // Add input rules like email etc
            builder.crawlRules().setInputSpec(getCustomInputSpecification());

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

    public static InputSpecification getCustomInputSpecification() {
        InputSpecification input = new InputSpecification();
        input.fields("mail", "email", "subscribe", "email_address", "register[personal][email]", "login-email-address", "login", "product-quantity-spin", "newsletter", "spree_user_email", "email_create", "edit-name", "login-email", "thelia_newsletter[email]", "user_login", "edit-mail", "AccountFrm_email", "register_personal_email", "newlettersubscription-email", "newlettersubscription_email").setValue("christoph.rudolf@tum.de");
        input.fields("edit-quantity", "product_quantity", "input-quantity", "quantity_wanted", "qty", "quantity", "cart_quantity", "amount.*", "quantity.*").setValue("1");
        input.fields("postcode").setValue("10001");
        input.fields("phone").setValue("0123456789");
        input.fields("password", "confirmed_password").setValue("Foo1234%");
        return input;
    }
}
