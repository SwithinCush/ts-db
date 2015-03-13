/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.db.connector;

import com.ts.utils.DynamicLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Connector
 *
 * @author daibheid
 */
public final class Connector {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);
    
    private final String id;
    private final Properties props;
    private final Password password;
    
    private transient Driver driver;
    
    public Connector(String id, Properties props) {
        assert id != null;
        if (!id.matches("[A-Za-z0-9]+")) {
            throw new IllegalArgumentException("illegal id : " + id);
        }
        Properties p = new Properties();
        p.putAll(props);
        Password password = createPasswordInstance(props.getProperty("password.class"));
        password.setTransformedString(props.getProperty("password"));
        this.id = id;
        this.props = p;
        this.password = password;
    }
    
    private static Password createPasswordInstance(String className) {
        if(className != null) {
            try {
                return (Password)DynamicLoader.newInstance(className);
            }
            catch (Exception ex) {
                log.warn("", ex);
            }
        }
        return new PlainTextPassword();
    }
    
    public Connector(String id, Connector src) {
        this(id, (Properties)src.props.clone());
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return props.getProperty("name");
    }
    
    public String getClasspath() {
        return props.getProperty("classpath", "");
    }
    
    public String getDriver() {
        final String driver = props.getProperty("driver");
        if(log.isDebugEnabled()) {
            log.debug(String.format("driver=[%s]", driver));
        }
        return driver;
    }
    
    public String getUrl() {
        return props.getProperty("url");
    }
    
    public String getUser() {
        return props.getProperty("user");
    }
    
    public Password getPassword() {
        return password;
    }
    
    public boolean isReadOnly() {
        String s = props.getProperty("readonly");
        return Boolean.valueOf(s).booleanValue();
    }
    
    public boolean usesAutoRollback() {
        String s= props.getProperty("rollback");
        return Boolean.valueOf(s).booleanValue();
    }
    
    public Properties toProperties() {
        return (Properties)props.clone();
    }
    
    public Connection getConnection() throws SQLException {
        if(driver == null) {
            driver = ConnectorDriverManager.getDriver(getUrl(), getDriver(), getClasspath());
            if(driver == null) {
                throw new SQLException("failed to load driver");
            }
            if(log.isDebugEnabled()) {
                log.debug(driver.toString());
            }
        }
        Properties p = new Properties();
        p.setProperty("user", getUser());
        p.setProperty("password", getPassword().getRowString());
        if(!driver.acceptsURL(getUrl())) {
            throw new SQLException("invalid url: " + getUrl());
        }
        if(log.isInfoEnabled()) {
            log.info("driver.connect start");
        }
        Connection conn = driver.connect(getUrl(), p);
        if(log.isInfoEnabled()) {
            log.info("driver.connect end");
        }
        if(conn == null) {
            throw new IllegalStateException("driver returned null");
        }
        return conn;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((props == null) ? 0 : props.hashCode());
        return result;
    }
    
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        
        if (!(obj instanceof Connector)) {
            return false;
        }
        Connector other = (Connector)obj;
        return props.equals(other.props);
    }
    
    public String toString() {
        return "Connector: " + id;
    }
} 

