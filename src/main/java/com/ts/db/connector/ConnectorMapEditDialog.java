/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.db.connector;

import com.ts.db.Databases;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ChangeListener;

/**
 * Class ConnectorMapEditDialog
 *
 * @author daibheid
 */
public class ConnectorMapEditDialog extends JDialog implements ChangeListener {

    private final ConnectorMap connectorMap;
    private final JList idList;
    private final DefaultListModel listModel;
    
    ConnectorMapEditDialog(JFrame owner, Databases env) {
        super(owner);
        final DefaultListModel listModel = new DefaultListModel();
        this.connectorMap = new ConnectorMap(env.getConnectorMap());
        this.idList = new JList(listModel);
        this.listModel = listModel;
        setTitle(getMessage("title"));
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        FlexiblePanel p = new FlexiblePanel();
        
    }
    
    public String getMessage(String key) {
        
    }
} 

