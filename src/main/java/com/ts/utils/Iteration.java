/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Class Iteration
 *
 * @author daibheid
 */
public final class Iteration {

    private Iteration() {
        // empty
    }
    
    public static <T1, T2> List<T2> map(Iterable<T1> it, Correspondence<T1, T2> c) {
        List<T2> a = new ArrayList<T2>();
        for(T1 o : it) {
            a.add(c.f(o));
        }
        return a;
    }
    
    public static <T1, T2> List<T2> map(T1[] oa, Correspondence<T1, T2> c) {
        List<T2> a = new ArrayList<T2>();
        for(T1 o : oa) {
            a.add(c.f(o));
        }
        return a;
    }
    
    public static <T1, T2> List<T2> map(Correspondence<T1, T2> c, T1... oa) {
        List<T2> a = new ArrayList<T2>();
        for(T1 o : oa) {
            a.add(c.f(o));
        }
        return a;
    }
    
    public static <T> List<T> narrow(Iterable<T> it, Predicate<T> p) {
        List<T> a = new ArrayList<T>();
        for(T o : it) {
            if(p.f(o)) {
                a.add(o);
            }
        }
        return a;
    }
    
    public static <T> List<T> narrow(T[] oa, Predicate<T> p) {
        List<T> a = new ArrayList<T>();
        for(T o : oa) {
            if(p.f(o)) {
                a.add(o);
            }
        }
        return a;
    }
    
    public static <T> List<T> narrow(Predicate<T> p, T... oa) {
        List<T> a = new ArrayList<T>();
        for(T o : oa) {
            if(p.f(o)) {
                a.add(o);
            }
        }
        return a;
    }
    
    public static String join(Iterable<?> it, String delimiter) {
        StringBuilder buffer = new StringBuilder();
        for(Object o : it) {
            buffer.append(o);
            buffer.append(delimiter);
        }
        if(buffer.length() == 0) {
            return "";
        }
        return buffer.substring(0, buffer.length() - delimiter.length());
    }
    
    public static <T> String join(T[] oa, String delimiter) {
        StringBuilder buffer = new StringBuilder();
        for(T o : oa) {
            buffer.append(o);
            buffer.append(delimiter);
        }
        if(buffer.length() == 0) {
            return "";
        }
        return buffer.substring(0, buffer.length() - delimiter.length());
    }
    
    public static <T> String join(String delimiter, T... oa) {
        StringBuilder buffer = new StringBuilder();
        for(T o : oa) {
            buffer.append(o);
            buffer.append(delimiter);
        }
        if(buffer.length() == 0) {
            return "";
        }
        return buffer.substring(0, buffer.length() - delimiter.length());
    }
    
    public static <T> Iterable<T> asIterable(final Enumeration<T> enumeration) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    
                    public boolean hasNext() {
                        return enumeration.hasMoreElements();
                    }
                    
                    public T next() {
                        return enumeration.nextElement();
                    }
                    
                    public void remove() {
                        throw new UnsupportedOperationException("Iterable#remove");
                    }
                };
            }
        };
    }
    
    public interface Correspondence<T1, T2> {
        T2 f(T1 preimage);
    }
    
    public interface Predicate<T> {
        boolean f(T element);
    }
} 

