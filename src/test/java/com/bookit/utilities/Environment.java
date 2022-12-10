package com.bookit.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Environment {

        public static final String URL;
        public static final String BASE_URL;
        public static final String DB_USERNAME;
        public static final String DB_PASSWORD;
        public static final String DB_URL;
        public static final String TEACHER_EMAIL;
        public static final String TEACHER_PASSWORD;
        public static final String MEMBER_EMAIL;
        public static final String MEMBER_PASSWORD;
        public static final String LEADER_EMAIL;
        public static final String LEADER_PASSWORD;

    public static Properties properties;

    //Where to get the env? From configuration.properties with ConfigurationReader
    //This is layer 1. Layer 2 is to load another .properties file with the dynamic path below to get the details of the chosen environment
    //The class Environment is like ConfigurationReader, it is dynamically managing to read another properties file
    static {
            //If the environment is not null, whatever I put in the terminal (System.getProperty("environment") will be the env
            //It is gonna get whatever I pass in the terminal, but if the env is null, it will get it from ConfigReader
            //Basically, if I am using env from the command line, get that one. If not, get from ConfigReader
            String environment = System.getProperty("environment") != null ? environment = System.getProperty("environment") : ConfigurationReader.get("environment");
            //String environment = ConfigurationReader.get("environment");

            try {
                //Create a dynamic path to reach the Environments folder under Resources directory
                String path = System.getProperty("user.dir") + "/src/test/resources/Environments/" + environment + ".properties";
                // Yoy get your project locator with this, then add where this file is. You get full path with it.
                // System.getProperty("user.dir") generates location of your folder
                ///src/test/resources/Environments/ - project path
                // This will give us the FULL path
                // C:\Users\Zulpikar\IdeaProjects\EU9-BookIT\src\test\resources\Environments\qa1.properties

                FileInputStream input = new FileInputStream(path);
                properties = new Properties();
                properties.load(input);
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //not using ConfigurationReader anymore as we get the property and  put it inside a constant variable
            URL = properties.getProperty("url");
            BASE_URL = properties.getProperty("base_url");
            DB_USERNAME = properties.getProperty("dbUsername");
            DB_PASSWORD = properties.getProperty("dbPassword");
            DB_URL = properties.getProperty("dbUrl");
            TEACHER_EMAIL = properties.getProperty("teacher_email");
            TEACHER_PASSWORD = properties.getProperty("teacher_password");
            MEMBER_EMAIL = properties.getProperty("team_member_email");
            MEMBER_PASSWORD = properties.getProperty("team_member_password");
            LEADER_EMAIL = properties.getProperty("team_leader_email");
            LEADER_PASSWORD = properties.getProperty("team_leader_password");


        }

}
