package com.kenzie.capstone.service.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvironmentVariableUtility {


    /*
     * Returns input string with environment variable references expanded, e.g. $SOME_VAR or ${SOME_VAR}
     *
     * Gratefully adapted from Tim Lewis's answer on
     * https://stackoverflow.com/questions/2263929/regarding-application-properties-file-and-environment-variable
     */
    private static String resolveEnvVars(Map<String,String> envVars, String input, boolean failSilently) {
        if (null == input) {
            return null;
        }
        // match ${ENV_VAR_NAME} or $ENV_VAR_NAME
        Pattern p = Pattern.compile("\\$\\{(\\w+)\\}|\\$(\\w+)");
        Matcher m = p.matcher(input); // get a matcher object
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
            String envVarValue = envVars.get(envVarName);

            if(failSilently) {
                //how the shell works natively
                m.appendReplacement(sb, null == envVarValue ? "" : envVarValue);
            }else {
                //making it throw errors, so we know when one is not found
                if (envVarValue == null) {
                    throw new RuntimeException("Environment variable " + envVarName +
                            " expected but not found or is null. Please set variable to a non-null value.");
                }
                m.appendReplacement(sb, envVarValue);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /*
     * Reads a line expecting an environment variable and resolves it if found.
     * If no environment variable is found, the line is skipped
     */
    private static Map<String,String> computeEnvVar(Map<String,String> envVars, String line, boolean failSilently){
        String[] parts = line.trim().split("\\s+");

        Map<String,String> newVars = new HashMap<>(envVars);

        if (parts.length > 1) {
            String[] var = parts[1].split("=");

            if (var.length > 1) {
                String varName = var[0];
                String initVarValue = var[1];

                String varValue = resolveEnvVars(envVars, initVarValue, failSilently);
                newVars.put(varName, varValue);
            }
        }

        return newVars;
    }


    /**
     * Parses the given file for environment variables to return for use and returns a
     *  copy of the map of environment variables with the ones found in the file added.
     * Uses the envVars map as pre-existing variables to resolve parsing along with
     *  any variables in the file in found order.
     *
     * @param pathFromProjectRoot the list of the directories that form the relative path from
     *                            the execution location to the directory where the file to
     *                            parse is found
     *
     * @param filename the name of the file to parse for environment variables
     *
     * @param envVars the current environment variable map to be considered when constructing
     *                new environment variables in the file to parse
     *
     * @param failSilently if true, when parsing will replace any unfound variable references
     *                     with the empty string (as most terminals do). If false, will throw
     *                     a RuntimeException when a variable reference is not found.
     *
     * @throws IllegalArgumentException if the given file is not found.
     */
    public static Map<String,String> getEnvVariablesFromFile(List<String> pathFromProjectRoot,
                                                             String filename,
                                                             Map<String, String> envVars,
                                                             boolean failSilently){

        Path rootDir = Paths.get(".").normalize().toAbsolutePath().getParent();

        Path path = rootDir;
        for (String pathPart : pathFromProjectRoot) {
            path = path.resolve(pathPart);
        }
        path = path.normalize().resolve(filename);


        BufferedReader reader;

        Map<String,String> newEnvVars = new HashMap<>(envVars);

        try {
            reader = new BufferedReader(new FileReader(path.toFile()));
            String line = reader.readLine();

            while (line != null) {

                if (line.startsWith("export ")) {
                    newEnvVars = computeEnvVar(newEnvVars, line, failSilently);
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + path.toString() + " not found.", e);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return newEnvVars;
    }


    /**
     * Parses the given file for environment variables to return for use.
     * Uses the envVars map as pre-existing variables to resolve parsing
     * along with any variables in the file in found order.
     *
     * @param pathToFile the list of the directories that form the path from the execution
     *                   location to the directory where the file to parse is found
     *
     * @param filename the name of the file to parse for environment variables
     *
     * @param envVars the current environment variable map to be considered when constructing
     *                new environment variables in the file to parse
     *
     * @param envVarName name of the environment variable to find
     *
     * @param failSilently if true, when parsing will replace any unfound variable references
     *                     with the empty string (as most terminals do). If false, will throw
     *                     a RuntimeException when a variable reference is not found.
     */
    public static String getEnvVariableFromFile(List<String> pathToFile,
                                                String filename,
                                                Map<String, String> envVars,
                                                String envVarName,
                                                boolean failSilently){

        Map<String, String> newEnvVars = getEnvVariablesFromFile(pathToFile, filename, envVars, failSilently);

        return newEnvVars.get(envVarName);
    }

    /**
     * Parses the setupEnvironment.sh file for all environment variables set within. Resolves using
     * the environment variables set in System.getenv() and any variables in the file in found order.
     * Since the file is at the root of the project, the path from the root is empty
     *
     * @param envVarName the name of the environment variable to locate
     */
    public static String getEnvVarFromSetupEnvironment(String envVarName){
        Map<String,String> envVars = System.getenv();
        List<String> pathToFile = new ArrayList<>();
        String filename = "setupEnvironment.sh";

        return getEnvVariableFromFile(pathToFile,filename,envVars,envVarName,false);
    }

}
