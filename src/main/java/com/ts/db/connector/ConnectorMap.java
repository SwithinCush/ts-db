/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.db.connector;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 * Class ConnectorMap
 *
 * @author daibheid
 */
public class ConnectorMap extends LinkedHashMap<String, Connector> {

    public ConnectorMap() {
        // empty
    }
    
    public ConnectorMap(List<String> idList, Properties props) {
        for(String id : idList) {
            Properties p = new Properties();
            copyPropertyById(id, "name", props, p);
            copyPropertyById(id, "driver", props, p);
            copyPropertyById(id, "classpath", props, p);
            copyPropertyById(id, "url", props, p);
            copyPropertyById(id, "user", props, p);
            copyPropertyById(id, "password", props, p);
            copyPropertyById(id, "password.class", props, p);
            copyPropertyById(id, "readonly", props, p);
            copyPropertyById(id, "rollback", props, p);
            Connector connector = new Connector(id, p);
            put(id, connector);
        }
    }
    
    public ConnectorMap(ConnectorMap src) {
        putAll(src);
    }
    
    private void copyPropertyById(String id, String key, Properties src ,Properties dst) {
        String fullKey = id + '.' + key;
        String value = src.getProperty(fullKey, StringUtils.EMPTY);
        dst.setProperty(key, value);
    }
    
    public Connector getConnector(String id) {
        return get(id);
    }
    
    public void setConnector(String id, Connector connector) {
        put(id, connector);
    }
    
    public Properties toProperties() {
        Properties props = new Properties();
        for(String id : keySet()) {
            Connector connector = getConnector(id);
            Password password = connector.getPassword();
            props.setProperty(id + ".name", connector.getName());
            props.setProperty(id + ".driver", connector.getDriver());
            props.setProperty(id + ".classpath", connector.getClasspath());
            props.setProperty(id + ".url", connector.getUrl());
            props.setProperty(id + ".user", connector.getUser());
            props.setProperty(id + ".password", password.getTransformedString());
            props.setProperty(id + ".password.class", password.getClass().getName());
            props.setProperty(id + ".readonly", Boolean.toString(connector.isReadOnly()));
            props.setProperty(id + ".rollback", Boolean.toString(connector.usesAutoRollback()));
        }
        return props;
    }
} 

