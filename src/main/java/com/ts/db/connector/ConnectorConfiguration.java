/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.db.connector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.modules.Places;

/**
 * Class ConnectorConfiguration
 *
 * @author daibheid
 */
public final class ConnectorConfiguration {

    private static final Pattern idPattern = Pattern.compile("^([^\\.]+)\\.name*=");
    
    public static ConnectorMap load() throws IOException {
        InputStream is = new FileInputStream(getPath());
        try {
            return load(is);
        }
        finally {
            is.close();
        }
    }
    
    public static ConnectorMap load(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        for(int c; (c = is.read(buffer)) >= 0;) {
            bos.write(buffer, 0, c);
        }
        bos.flush();
        
        List<String> idList = new ArrayList<String>();
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        BufferedReader reader = new BufferedReader(new InputStreamReader(bis));
        try {
            for(String line; (line = reader.readLine()) != null;) {
                Matcher matcher = idPattern.matcher(line);
                if(matcher.find()) {
                    idList.add(matcher.group(1));
                }
            }
        }
        finally {
            reader.close();
        }
        
        Properties props = new Properties();
        props.load(new ByteArrayInputStream(bos.toByteArray()));
        
        return new ConnectorMap(idList, props);
    }
    
    public static void save(ConnectorMap map) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        save(bos, map);
        byte[] bytes = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        FileOutputStream fos = new FileOutputStream(getPath());
        try {
            fos.getChannel().transferFrom(Channels.newChannel(bis), 0, bytes.length);
        }
        finally {
            fos.close();
        }
    }
    
    public static void save(OutputStream os, ConnectorMap map) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        map.toProperties().store(bos, "");
        
        List<String> lines = new ArrayList<String>();
        Scanner scanner = new Scanner(new ByteArrayInputStream(bos.toByteArray()));
        try {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(!line.trim().startsWith("#")) {
                    lines.add(line);
                }
            }
        }
        finally {
            scanner.close();
        }
        
        Comparator<String> c = new ConnectorPropertyComparator(new ArrayList<String>(map.keySet()));
        Collections.sort(lines, c);
        PrintWriter out = new PrintWriter(os);
        try {
            for (String line : lines) {
                out.println(line);
            }
            out.flush();
        }
        finally {
            out.close();
        }
    }
    
    private static File getPath() {
        return new File(Places.getUserDirectory(), "connector.properties");
    }
    
    private static final class ConnectorPropertyComparator implements Comparator<String>, Serializable {
        private static final long serialVersionUID = 1803584843826843857L;
        
        private final List<String> idList;
        
        ConnectorPropertyComparator(List<String> idList) {
            this.idList = idList;
        }
        
        public int compare(String s1, String s2) {
            int index1 = getIdIndex(s1);
            int index2 = getIdIndex(s2);
            if(index1 == index2) {
                return s1.compareTo(s2);
            }
            return index1 - index2;
        }
        
        private int getIdIndex(String s) {
            String[] sa = s.split("\\.", 2);
            if(sa.length >= 2) {
                String id = sa[0];
                return idList.indexOf(id);
            }
            return -1;
        }
    }
} 

