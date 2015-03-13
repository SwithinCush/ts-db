/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Class DynamicLoader
 *
 * @author daibheid
 */
public final class DynamicLoader {

    private DynamicLoader() {
        // empty
    }
    
    public static <T> Class<T> loadClass(String className) throws DynamicLoadingException {
        return loadClass(className, ClassLoader.getSystemClassLoader());
    }
    
    public static <T> Class<T> loadClass(String className, ClassLoader classLoader) throws DynamicLoadingException {
        try {
            @SuppressWarnings("unchecked")
            Class<T> c = (Class<T>)classLoader.loadClass(className);
            return c;
        }
        catch (Throwable th) {
            throw new DynamicLoadingException("class loading error", th);
        }
    }
    
    public static <T> T newInstance(String className) throws DynamicLoadingException {
        @SuppressWarnings("unchecked")
        T o = (T)newInstance(className, DynamicLoader.class.getClassLoader());
        return o;
    }
    
    public static <T> T newInstancE(String className, URL... urls) throws DynamicLoadingException {
        @SuppressWarnings("unchecked")
        T o = (T)newInstance(className, getURLClassLoader(urls));
        return o;
    }
    
    public static <T> T newInstance(String className, ClassLoader classLoader) throws DynamicLoadingException {
        try {
            Class<T> c = loadClass(className, classLoader);
            return c.newInstance();
        }
        catch (DynamicLoadingException ex) {
            throw ex;
        }
        catch (Throwable th) {
            throw new DynamicLoadingException("load error: " + className, th);
        }
    }
    
    public static <T> T newInstance(Class<T> classObject) throws DynamicLoadingException {
        try {
            return classObject.newInstance();
        }
        catch (Throwable th) {
            throw new DynamicLoadingException("load error: " + classObject, th);
        }
    }
    
    private static URLClassLoader getURLClassLoader(final URL... urls) {
        return (URLClassLoader)AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                return new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
            }
        });
    }
} 

