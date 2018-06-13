package com.crawljax.util;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;

import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import org.apache.commons.codec.binary.Base64;

public class HarHelper {
    public static Har encodeHarData(Har har) {

        int MEGABYTE = (1024*1024);

        for(HarEntry entry : har.getLog().getEntries()) {
            String value = entry.getResponse().getContent().getText();
            if(value != null) {
                // Replace the text of the content of a response with its base64 encoded representation
                try {
                    System.out.println(value.getBytes().length);
                    System.out.println("Value size :" + value.getBytes().length + " bytes.");
                    byte[] encodedValue = Base64.encodeBase64(value.getBytes("UTF-8"));
                    System.out.println(" and Encoded Size " + encodedValue.length + " bytes");
                    entry.getResponse().getContent().setText(new String(encodedValue));
                } catch (OutOfMemoryError e) {
                    MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
                    MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
                    long maxMemory = heapUsage.getMax() / MEGABYTE;
                    long usedMemory = heapUsage.getUsed() / MEGABYTE;
                    System.out.println("Memory Use :" + usedMemory + "M/" + maxMemory + "M");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return har;
    }
}
