/*
* Copyright 2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.openehealth.coms.cc.consent_applet.model;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.openehealth.coms.cc.consent_applet.applet.ConsentApplet.OIDTreeSelectionListener;



public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener{ 
    private CheckTreeSelectionModel selectionModel; 
    private JTree tree = new JTree(); 
    private OIDTreeSelectionListener otsl = null;
    int hotspot = new JCheckBox().getPreferredSize().width; 
 
    public CheckTreeManager(JTree tree, OIDTreeSelectionListener otsl){ 
        this.tree = tree; 
        this.otsl = otsl;
        selectionModel = new CheckTreeSelectionModel(tree.getModel()); 
        tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel)); 
        tree.addMouseListener(this); 
        selectionModel.addTreeSelectionListener(this); 
    } 
 
    public void mouseClicked(MouseEvent me){ 
        TreePath path = tree.getPathForLocation(me.getX(), me.getY()); 
        if(path==null) 
            return; 
        if(me.getX()>tree.getPathBounds(path).x+hotspot) 
            return; 
        
        
        OIDObject oo = null;
        
        if (path.getLastPathComponent() instanceof LazyOIDTreeNode) {
        	oo = (OIDObject) ((LazyOIDTreeNode) path.getLastPathComponent()).getUserObject();
        }
        else if (path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
        	oo = (OIDObject) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        }
        
        if (!oo.isActive()) {
        	return;
        }
        
        
        tree.clearSelection();
        boolean selected = selectionModel.isPathSelected(path, true); 
        selectionModel.removeTreeSelectionListener(this); 
 
        try{ 
            if(selected) 
                selectionModel.removeSelectionPath(path); 
            else 
                selectionModel.addSelectionPath(path); 
        } finally{ 
            selectionModel.addTreeSelectionListener(this);
            tree.treeDidChange();
            otsl.valueChanged();
            
        } 
    } 
 
    
    public CheckTreeSelectionModel getSelectionModel(){ 
        return selectionModel; 
    } 
 
    
    public void valueChanged(TreeSelectionEvent e){ 
        tree.treeDidChange();
        
    } 
    
    
    public void deselectAll() {
    	
    	TreePath[] paths = selectionModel.getSelectionPaths();
    	
    	selectionModel.removeSelectionPaths(paths);
    	
    }
    
    
    public void setModel(DefaultTreeModel model) {
    	selectionModel = new CheckTreeSelectionModel(model);
    }
    
}