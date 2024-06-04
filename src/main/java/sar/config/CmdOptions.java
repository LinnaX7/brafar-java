package sar.config;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CmdOptions {
    public final static String HELP_OPT = "Help";
    public final static String HELP_DESC = "Print the help message.";

    public final static String BUGGY_PROGRAM_SOURCE_DIR = "BuggyProgramSourceDir";
    public final static String BUGGY_PROGRAM_SOURCE_DIR_DESC = "Path to the buggy program source directory.";

    public final static String CORRECT_PROGRAMS_SOURCE_DIR = "CorrectProgramsSourceDir";
    public final static String CORRECT_PROGRAMS_SOURCE_DIR_DESC = "Path to the correct programs source directory.";

    public final static String REFERENCE_PROGRAM_SOURCE_DIR = "CorrectProgramsSourceDir";
    public final static String REFERENCE_PROGRAM_SOURCE_DIR_DESC = "Path to the reference correct program source directory.";
    public final static String METHOD_TO_FIX_OPT = "MethodToFix";
    public final static String METHOD_TO_FIX_DESC = "Method to fix. Format: MethodName@PackageName.ClassName .";

    public final static String PROGRAM_TEST_SOURCE_DIR_OPT = "ProgramTestSourceDir";
    public final static String PROGRAM_TEST_SOURCE_DIR_DESC = "Path to the program test source directory, separated by semicolon (;).";

    public final static String PROGRAM_TEST_CLASS = "ProgramTestClass";
    public final static String PROGRAM_TEST_CLASS_DESC = "Class to the program test. Format: PackageName.ClassName .";


    private static Options cmdOptions;

    public static Options getCmdOptions() {
        if (cmdOptions == null) {
            cmdOptions = new Options();
            prepareCmdOptions();
        }
        return cmdOptions;
    }

    public final static String[][] commandLineOptions = {
            {BUGGY_PROGRAM_SOURCE_DIR, BUGGY_PROGRAM_SOURCE_DIR_DESC},
            {CORRECT_PROGRAMS_SOURCE_DIR, CORRECT_PROGRAMS_SOURCE_DIR_DESC},
            {REFERENCE_PROGRAM_SOURCE_DIR, REFERENCE_PROGRAM_SOURCE_DIR_DESC},
            {METHOD_TO_FIX_OPT, METHOD_TO_FIX_DESC},
            {PROGRAM_TEST_SOURCE_DIR_OPT, PROGRAM_TEST_SOURCE_DIR_DESC},
            {PROGRAM_TEST_CLASS, PROGRAM_TEST_CLASS_DESC}
    };

    public final static String[][] commandLineFlags = {
            {HELP_OPT, HELP_DESC}
    };

    private static void prepareCmdOptions() {
        for (String[] pair : commandLineOptions) {
            cmdOptions.addOption(Option.builder().argName(pair[0]).longOpt(pair[0]).hasArg(true).desc(pair[1]).build());
        }

        for (String[] pair : commandLineFlags) {
            cmdOptions.addOption(Option.builder().argName(pair[0]).longOpt(pair[0]).hasArg(false).desc(pair[1]).build());
        }
    }
}
