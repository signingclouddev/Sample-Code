/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author rahmat
 */
public class Config {

    private static final String PROJECT_NAME = "demoapp";

    static String CONFIG_PATH = null;
    static String CONFIG_FILENAME = "";

    protected static final String CONFIG_FOLDER = "demoapp";

    static final String ENC_ALGORITHM = "AES";

    protected static final String DEFAULT_JBOSS_FOLDER = "/usr/local/jboss";

    /* system properties */
    public static final String BASE_URL = "baseurl";
    public static final String SMS_AUTH = "smsAuth";
    public static final String CROTP_AUTH = "crOtpAuth";
    public static final String AUTH_OPTION = "authOption";
    public static final String API_URL = "apiurl";
    public static final String AUTH_URL = "authurl";
    public static final String LOGIN_URL = "login-url-no-client-auth";
    public static final String LOGIN_URL_CLIENT = "login-url-with-client-auth";
    public static final String ADMIN_USERNAME = "adminUsername";
    public static final String ADMIN_CERT = "adminCertFp";
    public static final String INTEGRATION_KEY = "integrationkey";
    public static final String SECRET_KEY = "secretkey";
    public static final String PASSWORD_MIN = "passwordMinLength";
    public static final String PASSWORD_COMPLEX = "passwordComplexity";
    public static final String PROXY_HOST = "proxyHost";
    public static final String PROXY_PORT = "proxyPort";
    public static final String PROXY_USER = "proxyUser";
    public static final String PROXY_PASS = "proxyPass";
    public static final String BYPASS_SSL = "bypassSslChecking";

    /* default config */
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String COMPANY_NAME = "companyname";
    public static final String USER_GROUP = "usergroup";
    public static final String DEFAULT_COUNTRY = "defaultcountry";

    //db config
    public static final String IP = "ip";
    public static final String PORT = "port";
    public static final String SCHEMA = "schema";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final String SYSTEM_CONFIG = "system.config";
    public static final String DEFAULT_CONFIG = "default.config";
    public static final String DB_CONFIG = "db.config";

    public static final int TYPE_SYSTEM = 1;
    public static final int TYPE_DEFAULT = 2;
    public static final int TYPE_DB = 3;
    public static final int TYPE_OTP = 4;
    public Config() {
        if (CONFIG_PATH == null) {
            CONFIG_PATH = System.getProperty("user.dir") + "/" + CONFIG_FOLDER + "/";
        }
    }

    /**
     * specify the path of TMS's configuration
     *
     * @param path
     */
    public Config(String path) {
        if (CONFIG_PATH == null) {
            if (!path.endsWith("/") || !path.endsWith("\\")) {
                CONFIG_PATH = path.concat("/");
            } else {
                CONFIG_PATH = path;
            }
        }
    }

    public Config(String path, String confName) {
        if (CONFIG_PATH == null) {
            CONFIG_PATH = path;
            CONFIG_FILENAME = confName;
        }
    }

    protected java.io.File getConfigFile(int pIntConfigType) {
        java.io.File myFile = null;
        switch (pIntConfigType) {
            case TYPE_SYSTEM:
                myFile = new java.io.File(CONFIG_PATH + SYSTEM_CONFIG);
                break;
            case TYPE_DEFAULT:
                myFile = new java.io.File(CONFIG_PATH + DEFAULT_CONFIG);
                break;
            case TYPE_DB:
                myFile = new java.io.File(CONFIG_PATH + DB_CONFIG);
                break;
            case TYPE_OTP:
                myFile = new java.io.File(CONFIG_PATH + DB_CONFIG);
                break;    
        }
        return myFile;
    }

    public java.util.Properties getConfig(int pIntConfigType) {
        //this will ensure the properties is sorted alphabetic
        //then this is save to properties file, it will be better look
        java.util.Properties p = new java.util.Properties() {
            @Override
            public Set<Object> keySet() {
                return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
            }

            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
        java.io.File configFile = null;
        java.io.FileInputStream in = null;
        try {
            configFile = getConfigFile(pIntConfigType);

            if (configFile == null) {
                //    System.out.println ( "Config File Type: " + pIntConfigType + " not supported." );
                return p;
            } else if (!configFile.exists()) {
                //     System.out.println( configFile.getAbsolutePath () + " NOT FOUND" );
                return p;
            }
            in = new java.io.FileInputStream(configFile); // myURL.openStream();

            p.load(in);
        } catch (Exception ex) {
            //  System.out.println ( "Unable to get " + PROJECT_NAME + " Configuration"  );
            p = null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (Exception eex) {
                }
            }
            configFile = null;
        }
        return p;
    }
}
