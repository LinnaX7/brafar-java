package sar;
import org.apache.commons.cli.*;
import sar.config.CmdOptions;
import sar.config.ConfigBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Main {

    public static void main(String[] args) {
        try {
            CommandLine commandLine = parseCommandLine(CmdOptions.getCmdOptions(), args);
            if (commandLine.getOptions().length == 0 || commandLine.hasOption(CmdOptions.HELP_OPT)) {
                System.out.println(helpInfo());
                return;
            }
            ConfigBuilder configBuilder = new ConfigBuilder();
            configBuilder.buildConfig(commandLine);
            SBrafar sBrafar = new SBrafar();
            sBrafar.execute(configBuilder.getConfig());
        }catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static CommandLine parseCommandLine(Options options, String[] args){
        try{
            CommandLineParser cmdLineParser = new DefaultParser();
            return cmdLineParser.parse(options, args);
        }
        catch(ParseException e){
            throw new IllegalStateException();
        }
    }

    protected static String helpInfo() {
        String result = "";

        try (StringWriter sWriter = new StringWriter();
             PrintWriter pWriter = new PrintWriter(sWriter)) {
            HelpFormatter usageFormatter = new HelpFormatter();
            usageFormatter.printHelp(pWriter, 80, "java Main", "", CmdOptions.getCmdOptions(), 3, 5, "", true);
            result = sWriter.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }
}
