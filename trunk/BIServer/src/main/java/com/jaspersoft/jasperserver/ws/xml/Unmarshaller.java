/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed WITHOUT ANY WARRANTY; and without the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt 
 * or write to:
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 */
package com.jaspersoft.jasperserver.ws.xml;

import java.io.StringReader;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ListItem;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.OperationResult;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Request;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceProperty;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;


/**
 *
 * @author gtoffoli
 */
public class Unmarshaller {
    
    protected static final Log log = LogFactory.getLog(Unmarshaller.class);
    
    private String encoding = "UTF-8";
    
    /** Creates a new instance of XMLMarchaller */
    public Unmarshaller() {
    }
    
    static public String readPCDATA(Node textNode) {
        return readPCDATA(textNode,true);
    }

    static public String readPCDATA(Node textNode, boolean trim) {
        NodeList list_child = textNode.getChildNodes();
        for (int ck=0; ck< list_child.getLength(); ck++) {
            Node child_child = (Node)list_child.item(ck);

            // --- start solution: if there is another node this should be the PCDATA-node
            Node ns = child_child.getNextSibling();
            if (ns != null)
            child_child = ns;
            // --- end solution

            final short nt = child_child.getNodeType();
            
            // 1. look for a CDATA first...
            if (nt == Node.CDATA_SECTION_NODE) {
               if (trim) return ((String)child_child.getNodeValue()).trim();
                return (String)child_child.getNodeValue();
            }
        }
        
        for (int ck=0; ck< list_child.getLength(); ck++) {
            Node child_child = (Node)list_child.item(ck);
            
            // --- start solution: if there is another node this should be the PCDATA-node
            Node ns = child_child.getNextSibling();
            if (ns != null)
            child_child = ns;
            // --- end solution

            final short nt = child_child.getNodeType();
            // 1. look for a CDATA first...
            if (nt == Node.TEXT_NODE) {
               if (trim) return ((String)child_child.getNodeValue()).trim();
                return (String)child_child.getNodeValue();
            }
        }
        
        return "";
    }

    /**
     * Class c is not used, the method firm is done to be compatible
     * with old code.
     *
     */
    public static Object unmarshal(Class c, StringReader sr) throws Exception
    {
        Unmarshaller u = new Unmarshaller();
        return  u.unmarshal(sr);
    }
    
    public static Object unmarshalXml(String xmlString) throws Exception
    {
        Unmarshaller u = new Unmarshaller();
        return u.unmarshal(xmlString);
    }
    
    /*
     * This method unmarshall the xml. If the xml rapresents an OperationResult, an OperationResult
     * will be retun, otherwise it will return a ResourceDescritor...
     * The default encoding used is UTF-8.
     */
    public Object unmarshal(String xml) throws Exception
    {
        StringReader sreader = new java.io.StringReader(xml);
        return unmarshal(sreader);
    }
    /*
     * This method unmarshall the xml. If the xml rapresents an OperationResult, an OperationResult
     * will be retun, otherwise it will return a ResourceDescritor...
     * The default encoding used is UTF-8.
     */
    public Object unmarshal(StringReader sreader) throws Exception
    {
        try {
                // Use parser defined at the Java level, not a specific parser
            
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                
                InputSource input_source  = new InputSource( sreader );
                Document document = docBuilder.parse(input_source);

                Node rootNode = document.getDocumentElement();
                
                if (rootNode.getNodeName().equals("request"))
                {
                    return readRequest( rootNode );
                }
                else if (rootNode.getNodeName().equals("operationResult"))
                {
                    return readOperationResult( rootNode );
                }
                
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    
        return null;
    }
  
    
    private Request readRequest( Node requestNode)
    {
        
        Request request = new Request();
        
        NamedNodeMap nodeAttributes = requestNode.getAttributes();

        if (nodeAttributes.getNamedItem("operationName") != null)
            request.setOperationName( nodeAttributes.getNamedItem("operationName").getNodeValue() );
        
        if (nodeAttributes.getNamedItem("locale") != null)
            request.setLocale( nodeAttributes.getNamedItem("locale").getNodeValue() );
        

        NodeList childsOfChild = requestNode.getChildNodes();
        for (int c_count=0; c_count< childsOfChild.getLength(); c_count++) {
            Node child_child = (Node)childsOfChild.item(c_count);
            
            if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("argument")) {
                request.getArguments().add( readArgument( child_child ) );
            }
            
            if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("resourceDescriptor")) {
                request.setResourceDescriptor(  readResourceDescriptor( child_child ) );
            }

        }
        return request;
    }
    
    private Argument readArgument( Node argumentNode)
    {
        Argument argument = new Argument();
        NamedNodeMap nodeAttributes = argumentNode.getAttributes();

        if (nodeAttributes.getNamedItem("name") != null)
            argument.setName( nodeAttributes.getNamedItem("name").getNodeValue() );
        
        argument.setValue( readPCDATA( argumentNode ) );
        
        return argument;
    }
    
    private ResourceDescriptor readResourceDescriptor(Node rpNode) {
      
       ResourceDescriptor rd = new ResourceDescriptor();
        
       NamedNodeMap nodeAttributes = rpNode.getAttributes();

       if (nodeAttributes.getNamedItem("name") != null)
            rd.setName( nodeAttributes.getNamedItem("name").getNodeValue() );
       if (nodeAttributes.getNamedItem("wsType") != null)
            rd.setWsType( nodeAttributes.getNamedItem("wsType").getNodeValue() );
       if (nodeAttributes.getNamedItem("uriString") != null)
            rd.setUriString( nodeAttributes.getNamedItem("uriString").getNodeValue() );
       if (nodeAttributes.getNamedItem("isNew") != null)
            rd.setIsNew( nodeAttributes.getNamedItem("isNew").getNodeValue().equals("true") );
        
       NodeList childsOfChild = rpNode.getChildNodes();
        for (int c_count=0; c_count< childsOfChild.getLength(); c_count++) {
            Node child_child = (Node)childsOfChild.item(c_count);
            
            if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("label")) {
                rd.setLabel( readPCDATA( child_child ) );
            }
            else if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("description")) {
                rd.setDescription( readPCDATA( child_child ) );
            }
            else if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("resourceProperty")) {
                rd.setResourceProperty( readResourceProperty(child_child)  );
            }
            else if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("resourceDescriptor")) {
                rd.getChildren().add( readResourceDescriptor(child_child)  );
            }
            else if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("parameter")) {
                rd.getParameters().add( readResourceParameter(child_child)  );
            }
        }
       
       return rd;
    }

    private ResourceProperty readResourceProperty(Node rpNode) {
       
       
       ResourceProperty rp = new ResourceProperty(null);
       NamedNodeMap nodeAttributes = rpNode.getAttributes();

       if (nodeAttributes.getNamedItem("name") != null)
            rp.setName( nodeAttributes.getNamedItem("name").getNodeValue() );
        
       NodeList childsOfChild = rpNode.getChildNodes();
        for (int c_count=0; c_count< childsOfChild.getLength(); c_count++) {
            Node child_child = (Node)childsOfChild.item(c_count);
            
            if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("value")) {
                rp.setValue( readPCDATA( child_child ) );
            }
            else if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("resourceProperty")) {
                rp.getProperties().add( readResourceProperty(child_child)  );
            }
        }       
       return rp;
    }
    
    private ListItem readResourceParameter(Node rpNode) {
       
       ListItem rp = new ListItem();
       NamedNodeMap nodeAttributes = rpNode.getAttributes();

       if (nodeAttributes.getNamedItem("name") != null)
            rp.setLabel( nodeAttributes.getNamedItem("name").getNodeValue() );
       
       if (nodeAttributes.getNamedItem("isListItem") != null)
            rp.setIsListItem( nodeAttributes.getNamedItem("isListItem").getNodeValue().equals("true") );
        
       rp.setValue( readPCDATA( rpNode ) );
       
       return rp;
    }
    
    
    private OperationResult readOperationResult( Node operationResultNode)
    {
       OperationResult or = new OperationResult();
        
       NamedNodeMap nodeAttributes = operationResultNode.getAttributes();

       if (nodeAttributes.getNamedItem("version") != null)
            or.setVersion( nodeAttributes.getNamedItem("version").getNodeValue() );
        
       NodeList childsOfChild = operationResultNode.getChildNodes();
        for (int c_count=0; c_count< childsOfChild.getLength(); c_count++) {
            Node child_child = (Node)childsOfChild.item(c_count);
            
            if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("returnCode")) {
                or.setReturnCode( Integer.parseInt( readPCDATA( child_child ) ) );
            }
            else if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("returnMessage")) {
                or.setMessage( readPCDATA( child_child ) );
            }
            else if (child_child.getNodeType() == Node.ELEMENT_NODE && child_child.getNodeName().equals("resourceDescriptor")) {
                or.getResourceDescriptors().add( readResourceDescriptor(child_child)  );
            }
        }
       
       return or;
    }
    
    
}
