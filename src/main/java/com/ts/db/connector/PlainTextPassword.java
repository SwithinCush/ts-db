/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.db.connector;

import org.apache.commons.lang3.StringUtils;

/**
 * Class PlainTextPassword
 *
 * @author daibheid
 */
public class PlainTextPassword implements Password {

    private String rowString;
    
    @Override
    public String getTransformedString() {
        return getRowString();
    }
    
    @Override
    public void setTransformedString(String transformedString) {
        setRowString(transformedString);
    }
    
    @Override
    public String getRowString() {
        return (hasPassword()) ? rowString : StringUtils.EMPTY;
    }
    
    @Override
    public void setRowString(String rowString) {
        if (rowString != null) 
            this.rowString = rowString;
    }
    
    @Override
    public boolean hasPassword() {
        return rowString != null;
    }
} 

