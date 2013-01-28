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

package org.openehealth.coms.cc.web_frontend.consentcreator.service;

import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.openehealth.coms.cc.web_frontend.consentcreator.model.OIDObject;
import org.openehealth.coms.cc.web_frontend.org.json.JSONObject;


/**
 * This class contains various helper-methods.
 * 
 * @author Lennart Koester	
 * @version 1.0 14.02.2011
 *
 */
public class ConsentCreatorUtilities {
	
	private static ConsentCreatorUtilities instance = new ConsentCreatorUtilities();
	
	private ConsentCreatorUtilities() {
		
	}
	
	/**
	 * Returns an instance of the ConsentCreatorService
	 * @return
	 */
	public static ConsentCreatorUtilities getInstance() {
		return instance;
	}

	
	/**
	 * Searches all children of the currentNode and adds the node to it.
	 * 
	 * @param addNode
	 * @param currentNode
	 * @return
	 */
	public int addNodeToModel(TreeNode addNode, TreeNode currentNode) {
		
	
		int chInt = currentNode.getChildCount();
		int i = 0;
		
		
		for (; i < chInt; i++) {
			
			String currentNodeIdentifier = ((OIDObject) ((DefaultMutableTreeNode) currentNode.getChildAt(i)).getUserObject()).getIdentifier();
		
			String pathNodeIdentifer = ((OIDObject) ((DefaultMutableTreeNode) addNode).getUserObject()).getIdentifier();
			
			if (currentNodeIdentifier.equals(pathNodeIdentifer)) {
				return -1; //Already added, scenario never occurs.
			}
			else if (containsOID(currentNodeIdentifier, pathNodeIdentifer)) {
				i = addNodeToModel(addNode, currentNode.getChildAt(i));
				//currentNode and pathNode share OID, continue search one level below
			}
		}
		//None of the children of the current node have a child sharing the OID, add node to the current Node
		if (i <= chInt) {
			((DefaultMutableTreeNode) currentNode).add((DefaultMutableTreeNode) addNode);
			return chInt+2;
		}
		return i+chInt;
	}
	
	
	/**
	 * Searches all children of the currentNode and adds the path to it.
	 * 
	 * @param path
	 * @param currentNode
	 * @param depth
	 * @return
	 */
	public int addNodeToModel(TreeNode[] path, TreeNode currentNode, int depth) {
		
		int chInt = currentNode.getChildCount();
		int i = 0;
		
		
		for (; i < chInt; i++) {
			
			String currentNodeIdentifier = ((OIDObject) ((DefaultMutableTreeNode) currentNode.getChildAt(i)).getUserObject()).getIdentifier();
		
			String pathNodeIdentifer = ((OIDObject)((DefaultMutableTreeNode) path[depth]).getUserObject()).getIdentifier();
			
			if (currentNodeIdentifier.equals(pathNodeIdentifer)) {
				if (depth == path.length) {
					return -1; //Already added, scenario never occurs.
				}
				else {
					addNodeToModel(path, currentNode.getChildAt(i), depth+1);
					return -1;
				}
			}
			else if (containsOID(currentNodeIdentifier, pathNodeIdentifer)) {
				i = addNodeToModel(path, currentNode.getChildAt(i), depth+1);
				//currentNode and pathNode share OID, continue search one level below
			}
		}
		//None of the children of the current node have a child sharing the OID, add the remaining path to the current Node
		if (i <= chInt) {
			int k = depth;
			DefaultMutableTreeNode nodeToAddTo = (DefaultMutableTreeNode) currentNode;
			
			for (;k < path.length; k++) {
				DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) path[k]).clone();
				nodeToAddTo.add(newNode);
				nodeToAddTo = newNode;
			}
			return chInt+2;
		}
		return i+chInt;
		
	}
	
	
	
	/**
	 * Determines whether or not the given String s2 is contained in String s1
	 * 
	 * @param s1 - Parameter being searched
	 * @param s2 - Search parameter
	 * @return
	 */
	public boolean containsOID(String s1, String s2) {
		
		for (int i = 0; i < s1.length(); i++) {
			
			if (!(s1.charAt(i) == s2.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Formats the given URI to be an escaped URL
	 * 
	 * @param oURI
	 * @return
	 */
	public URL formatURI(URI oURI) {
		
		try {
			String uri = oURI.toString();
			
			uri = uri.replaceAll("&", "%26");
			uri = uri.replaceFirst("%26", "&");
			uri = uri.replaceFirst("%26", "&");
			
			URL url = new URL(uri);
			return url;
		}
		catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return null;
	}
	
	
	
	
	/**
	 * Sorts all Nodes and children of these Nodes by the name of their OIDObjects
	 * 
	 * @param root
	 */
	public void sortTree(DefaultMutableTreeNode root) {
		
		Vector<DefaultMutableTreeNode> childrenV = new Vector<DefaultMutableTreeNode>();
		
		if (root.getChildCount() > 1) {
			
			for (int i = 0; i < root.getChildCount(); i++) {
				
				childrenV.add((DefaultMutableTreeNode) root.getChildAt(i));
				
			}
			
			root.removeAllChildren();
			
			boolean swapped = true;

			while (swapped) {
				swapped = false;

				for(int i=0; i < childrenV.size() - 1; i++) {
					
					if (childrenV.elementAt(i).getChildCount() > 0) {
						sortTree(childrenV.elementAt(i));
					}
					
					OIDObject oo1 = (OIDObject) childrenV.elementAt(i).getUserObject();
					OIDObject oo2 = (OIDObject) childrenV.elementAt(i+1).getUserObject();
					
					DefaultMutableTreeNode node1 = childrenV.elementAt(i);
					DefaultMutableTreeNode node2 = childrenV.elementAt(i+1);
					
					if(oo1.getName().compareTo(oo2.getName()) > 0) {
						
						childrenV.setElementAt(node2,i);
						childrenV.setElementAt(node1,i+1);

						swapped = true;
					}
				}
			}
			for (int i = 0; i < childrenV.size(); i++) {
				
				root.add(childrenV.elementAt(i));
				
			}
		}
	}
	
	
	/**
	 * Constructs a Vector from the given Node and it's sub-Nodes
	 * 
	 * @param node
	 * @return
	 */
	public Vector<JSONObject> traverseTreeForExpand(DefaultMutableTreeNode node) {
		
		try {
			
			Vector<JSONObject> vjbo = new Vector<JSONObject>();
			
			for (Enumeration e = node.children() ; e.hasMoreElements() ;) {
		         
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.nextElement();
				JSONObject jso = new JSONObject();
				jso.put("name", ((OIDObject) n.getUserObject()).getName());
				jso.put("identifier", ((OIDObject) n.getUserObject()).getIdentifier());
				jso.put("hasChildren", !n.isLeaf());
				jso.put("isActive", ((OIDObject) n.getUserObject()).isActive());
				
				if (n.getChildCount() != 0) {
					Vector<JSONObject> retvjo = traverseTreeForExpand(n);
					jso.put("children", retvjo);
				}
				vjbo.add(jso);
		    }
			return vjbo;
			
		}
		catch (Exception e) {
			Logger.getLogger(this.getClass()).error(e);
		}
		return null;
		
	}
	
	
	
	/**
	 * Searches for the given oid in the given node and it's children.
	 * If the node is found, it is returned.
	 * 
	 * @param oid
	 * @param node
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public DefaultMutableTreeNode traverseTreeOID(String oid, DefaultMutableTreeNode node) {
		DefaultMutableTreeNode ret = null;
		
		Enumeration e = node.children();
		while( e.hasMoreElements() && ret == null) {
			DefaultMutableTreeNode n =  (DefaultMutableTreeNode) e.nextElement();
			if (((OIDObject) n.getUserObject()).getIdentifier().equalsIgnoreCase(oid)) {
				ret = n;
			}
			else if (containsOID(oid, ((OIDObject) n.getUserObject()).getIdentifier())) {
				ret = traverseTreeOID(oid, n);
			}
	     }
		return ret;
	}
	
	
	
	/**
	 * Searches for the given oid in the given node and it's children.
	 * If the node is found, it is returned.
	 * 
	 * @param oid
	 * @param node
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public DefaultMutableTreeNode traverseTreeString(String query, DefaultMutableTreeNode searchNode, DefaultMutableTreeNode realNode) {
		DefaultMutableTreeNode ret = null;
		
		Enumeration e = realNode.children();
		while( e.hasMoreElements()) {
			DefaultMutableTreeNode n =  (DefaultMutableTreeNode) e.nextElement();
			if (((OIDObject) n.getUserObject()).getName().toLowerCase().contains(query)) {
				ret = n;
				TreeNode[] path = n.getPath();
				addNodeToModel(path, searchNode, 1);
				
			}
			if (n.getChildCount() != 0) {
				traverseTreeString(query, searchNode, n);
			}
	     }
		return ret;
	}
	
}
