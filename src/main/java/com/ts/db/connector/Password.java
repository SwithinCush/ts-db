/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.db.connector;

/**
 *
 * @author daibheid
 */
public interface Password
{

    /**
     * 
     * @return 
     */
    String getTransformedString();
    
    /**
     * 
     * @param transformedString 
     */
    void setTransformedString(String transformedString);
    
    /**
     * 
     * @return 
     */
    String getRowString();
    
    /**
     * 
     * @param rowString 
     */
    void setRowString(String rowString);
    
    /**
     * 
     * @return 
     */
    boolean hasPassword();
}
