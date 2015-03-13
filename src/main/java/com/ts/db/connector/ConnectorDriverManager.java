/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.db.connector;

import com.ts.utils.DynamicLoader;
import com.ts.utils.DynamicLoadingException;
import com.ts.utils.Iteration;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.io.File.pathSeparator;

/**
 * Class ConnectorDriverManager
 *
 * @author daibheid
 */
final class ConnectorDriverManager {

    private static final Logger log = LoggerFactory.getLogger(ConnectorDriverManager.class);
    
    static final Set<File> driverFiles = Collections.synchronizedSet(new LinkedHashSet<File>());
    static final Set<Driver> drivers = Collections.synchronizedSet(new LinkedHashSet<Driver>());
    
    private ConnectorDriverManager() {
        
    }
    
    static Driver getDriver(String url, String driverClassName, String classpath) throws SQLException {
        assert !StringUtils.isBlank(url);
        final boolean hasClasspath = !StringUtils.isBlank(classpath);
        if (!hasClasspath) {
            for (Driver driver : new ArrayList<Driver>(drivers)) {
                if(driver.acceptsURL(url)) {
                    return driver;
                }
            }
        }
        List<File> jars = new ArrayList<File>();
        ClassLoader cl;
        if(hasClasspath) {
            List<URL> urls = new ArrayList<URL>();
            for(String path : classpath.split(pathSeparator)) {
                final File file = new File(path);
                if(isJarFile(file)) {
                    jars.add(file);
                }
                try {
                    urls.add(file.toURI().toURL());
                }
                catch (MalformedURLException ex) {
                    log.warn(ex.toString());
                }
            }
            cl = new URLClassLoader(urls.toArray(new URL[urls.size()]));
        }
        else {
            jars.addAll(getJarFiles("."));
            jars.addAll(driverFiles);
            List<URL> urls = new ArrayList<URL>();
            for (File file : jars) {
                try {
                    urls.add(file.toURI().toURL());
                }
                catch (MalformedURLException ex) {
                    log.warn(ex.toString());
                }
            }
            cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), ClassLoader.getSystemClassLoader());
        }
        driverFiles.addAll(jars);
        final boolean hasDriverClassName = !StringUtils.isBlank(driverClassName);
        if(hasDriverClassName) {
            try {
                Driver driver = DynamicLoader.newInstance(driverClassName, cl);
                assert driver != null;
                return driver;
            }
            catch (DynamicLoadingException ex) {
                Throwable cause = (ex.getCause() != ex) ? ex.getCause() : ex;
                SQLException exception = new SQLException(cause.toString());
                exception.initCause(cause);
                throw exception;
            }
        }
        final String jdbcDrivers = System.getProperty("jdbc.drivers");
        if(!StringUtils.isBlank(jdbcDrivers)) {
            for(String jdbcDriver : jdbcDrivers.split(":")) {
                try {
                    Driver driver = DynamicLoader.newInstance(jdbcDriver, cl);
                    if(driver != null) {
                        if(!hasClasspath) {
                            drivers.add(driver);
                        }
                        return driver;
                    }
                }
                catch (DynamicLoadingException ex) {
                    log.warn(ex.toString());
                }
            }
        }
        for(File jar : jars) {
            try {
                Driver driver = getDriver(jar, url, cl);
                if(driver != null) {
                    if(!hasClasspath) {
                        drivers.add(driver);
                    }
                    return driver;
                }
            }
            catch (IOException ex) {
                log.warn(ex.toString());
            }
        }
        for(String path : System.getProperty("java.class.path", "").split(pathSeparator)) {
            if(isJarFile(path)) {
                Driver driver;
                try {
                    driver = getDriver(new File(path), url, cl);
                    if(driver != null) {
                        drivers.add(driver);
                        return driver;
                    }
                }
                catch (IOException ex) {
                    log.warn(ex.toString());
                }
            }
        }
        throw new SQLException("driver not found");
    }
    
    private static Driver getDriver(File jar, String url, ClassLoader cl) throws IOException {
        ZipFile zipFile = new ZipFile(jar);
        try {
            for(ZipEntry entry : Iteration.asIterable(zipFile.entries())) {
                final String name = entry.getName();
                if(name.endsWith(".class")) {
                    final String fqcn = name.replaceFirst("\\.class", "").replace('/', '.');
                    try {
                        Class<?> c = DynamicLoader.loadClass(fqcn, cl);
                        if(Driver.class.isAssignableFrom(c)) {
                            Driver driver = (Driver)c.newInstance();
                            if(driver.acceptsURL(url)) {
                                return driver;
                            }
                        }
                    }
                    catch (Exception ex) {
                        if(log.isTraceEnabled()) {
                            log.trace(ex.toString());
                        }
                    }
                }
            }
        } 
        finally {
            zipFile.close();
        }
        return null;
    }
    
    private static List<File> getJarFiles(String path) {
        File root = new File(path);
        File[] files = root.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        List<File> jars = new ArrayList<File>();
        for(File file : files) {
            if(isJarFile(file)) {
                jars.add(file);
            }
        }
        return jars;
    }
    
    private static boolean isJarFile(File file) {
        return isJarFile(file.getPath());
    }
    
    private static boolean isJarFile(String path) {
        return path.matches("(?i).|\\.(jar|zip)");
    }
    
} 

