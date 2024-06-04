package sar.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import program.ProgramBuilder;
import program.TesterBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class ConfigBuilder {
    private Config config;
    public Config getConfig() {
        return config;
    }

    public void buildConfig(CommandLine commandLine) throws IOException {
        Properties properties;
        properties = getPropertiesFromCommandLine(commandLine);
        config = buildConfigFromProperties(properties);
    }

    private Properties getPropertiesFromCommandLine(CommandLine commandLine) {
        Properties properties = new Properties();
        for (Option opt : commandLine.getOptions()) {
            String propertyID = opt.getArgName();
            properties.setProperty(propertyID, commandLine.getOptionValue(propertyID));
        }

        return properties;
    }

    private Config buildConfigFromProperties(Properties properties) throws IOException {
        Config config = new Config();
        initMethodToFix(config, properties);
        initPrograms(config, properties);
        initTestProgram(config, properties);
        return config;
    }

    private String getProperty(Properties properties, String name) {
        if (properties.containsKey(name))
            return properties.getProperty(name);
        else
            throw new IllegalStateException("Error: Property not specified in configuration (" + name + ").");
    }

    private void initMethodToFix(Config config, Properties properties){
        config.setMethodToFix(getProperty(properties, CmdOptions.METHOD_TO_FIX_OPT));
    }

    public ProgramBuilder getProgram(Config config, String sourceDir) throws IOException {
        String className = config.getClassName();
        return new ProgramBuilder(sourceDir, className);
    }


    private void initPrograms(Config config, Properties properties) throws IOException {
        String buggySourceDir = getProperty(properties, CmdOptions.BUGGY_PROGRAM_SOURCE_DIR);
        config.setBuggyProgram(getProgram(config, buggySourceDir));
        if(properties.containsKey(CmdOptions.CORRECT_PROGRAMS_SOURCE_DIR)) {
            String correctSourceDir = getProperty(properties, CmdOptions.CORRECT_PROGRAMS_SOURCE_DIR);
            config.setCorrectPrograms(getPrograms(config, correctSourceDir));
        }else if (properties.containsKey(CmdOptions.REFERENCE_PROGRAM_SOURCE_DIR)){
            String referenceSourceDir = getProperty(properties, CmdOptions.REFERENCE_PROGRAM_SOURCE_DIR);
            config.setReferenceProgram(getProgram(config, referenceSourceDir));
        }else{
            throw new IllegalStateException("Error: Property not specified in configuration (" + CmdOptions.CORRECT_PROGRAMS_SOURCE_DIR + ") or ("+ CmdOptions.REFERENCE_PROGRAM_SOURCE_DIR + ").");
        }
    }

    private ArrayList<ProgramBuilder> getPrograms(Config config, String sourceDir) throws IOException {
        String className = config.getClassName();
        return ProgramBuilder.getProgramBuilders(sourceDir, className);
    }

    private void initTestProgram(Config config, Properties properties) throws FileNotFoundException {
        String testSourceDir = getProperty(properties, CmdOptions.PROGRAM_TEST_SOURCE_DIR_OPT);
        String className = getProperty(properties, CmdOptions.PROGRAM_TEST_CLASS);
        config.setTesterProgram(new TesterBuilder(testSourceDir, className));
    }
}
