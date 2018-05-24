package com.crawljax.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BatchParameterParser {
    static final String SCANLIST = "scanlist";
    static final String OUTDIR = "outdir";
    static final String DEPTH = "depth";
    static final String MAXSTATES = "states";

    private static final String DEFAULT_URL = "http://www.computerbase.de";
    private static final String DEFAULT_OUT = "out";
    private static final String DEFAULT_PROXY_URL = "localhost";
    private static final int DEFAULT_PROXY_PORT = 8888;
    private static final int DEFAULT_MAX_DEPTH = 5;
    private static final int DEFAULT_MAX_NUMBER_STATES = 20;

    private List<String> scanList;

    private Options options;
    private CommandLine parameters = null;

    public BatchParameterParser(String[] args) {
        // parse all arguments
        this.options = getOptions();
        try {
            this.parameters = new DefaultParser().parse(options, args);

            // read scanlist
            if(this.parameters.hasOption(SCANLIST)) {
                File file = new File(this.parameters.getOptionValue(SCANLIST));
                Scanner input = new Scanner(file);
                this.scanList = new ArrayList<>();
                while (input.hasNextLine()) {
                    scanList.add(input.nextLine());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Options to define the arguments the parser wants to extract.
     * @return Options to use for parsing the command line arguments via the existing DefaultParser
     */
    public Options getOptions() {
        Options options = new Options();
        options.addOption("l", SCANLIST, true, "Text file with urls to scan (line by line)");
        options.addOption("o", OUTDIR, true, "Path to the output directory (for each URL there will be a new subdirectory)");
        options.addOption("d", DEPTH, true, "Maximum depth during crawling");
        options.addOption("s", MAXSTATES, true, "Maximum number of states during crawling.");
        return options;
    }

    public List<String> getScanList() {
        return this.scanList != null ? this.scanList : Arrays.asList(DEFAULT_URL);
    }

    public String getOutputDir() {
        return this.parameters.hasOption(OUTDIR) ? this.parameters.getOptionValue(OUTDIR) : DEFAULT_OUT;
    }

    public String getProxyUrl() {
        return DEFAULT_PROXY_URL;
    }

    public int getProxyPort() {
        return DEFAULT_PROXY_PORT;
    }

    public int getMaxDepth() {
        return this.parameters.hasOption(DEPTH) ? Integer.parseInt(this.parameters.getOptionValue(DEPTH)) : DEFAULT_MAX_DEPTH;
    }

    public int getNumberOfStates() {
        return this.parameters.hasOption(MAXSTATES) ? Integer.parseInt(this.parameters.getOptionValue(MAXSTATES)) : DEFAULT_MAX_NUMBER_STATES;
    }
}
