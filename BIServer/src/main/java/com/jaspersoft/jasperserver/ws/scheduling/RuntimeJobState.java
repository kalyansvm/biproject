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

package com.jaspersoft.jasperserver.ws.scheduling;

/**
 * RuntimeJobState.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */
public class RuntimeJobState implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected RuntimeJobState(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _UNKNOWN = "UNKNOWN";
    public static final java.lang.String _NORMAL = "NORMAL";
    public static final java.lang.String _EXECUTING = "EXECUTING";
    public static final java.lang.String _PAUSED = "PAUSED";
    public static final java.lang.String _COMPLETE = "COMPLETE";
    public static final java.lang.String _ERROR = "ERROR";
    public static final RuntimeJobState UNKNOWN = new RuntimeJobState(_UNKNOWN);
    public static final RuntimeJobState NORMAL = new RuntimeJobState(_NORMAL);
    public static final RuntimeJobState EXECUTING = new RuntimeJobState(_EXECUTING);
    public static final RuntimeJobState PAUSED = new RuntimeJobState(_PAUSED);
    public static final RuntimeJobState COMPLETE = new RuntimeJobState(_COMPLETE);
    public static final RuntimeJobState ERROR = new RuntimeJobState(_ERROR);
    public java.lang.String getValue() { return _value_;}
    public static RuntimeJobState fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        RuntimeJobState enumeration = (RuntimeJobState)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static RuntimeJobState fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RuntimeJobState.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.jasperforge.org/jasperserver/ws", "RuntimeJobState"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
