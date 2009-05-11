/*
 * Copyright (C) 2005 - 2007 JasperSoft Corporation.  All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from JasperSoft,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
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
package com.jaspersoft.jasperserver.api.common.domain.impl;

import java.util.Date;
import java.io.Serializable;

import com.jaspersoft.jasperserver.api.common.domain.LogEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import org.hibernate.Hibernate;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LogEventImpl.java 13766 2008-05-23 07:35:01Z swood $
 */
public class LogEventImpl implements LogEvent, Serializable {

	private long id;
	private Date occurrenceDate;
	private byte type;
	private String username;
	private String component;
	private String messageCode;
	private String resourceURI;
	private String text;
	private byte[] data;
	private byte state;
        private transient Blob dataBlob;
	
	public LogEventImpl() {
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
                if (data == null) {
                   this.dataBlob = null; 
                } else {
                    this.dataBlob = Hibernate.createBlob(data);
                }
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public Date getOccurrenceDate() {
		return occurrenceDate;
	}

	public void setOccurrenceDate(Date occurrenceDate) {
		this.occurrenceDate = occurrenceDate;
	}

	public String getResourceURI() {
		return resourceURI;
	}

	public void setResourceURI(String resourceURI) {
		this.resourceURI = resourceURI;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public byte getState()
	{
		return state;
	}

	public void setState(byte state)
	{
		this.state = state;
	}

        public Blob getDataBlob() {
            return dataBlob;
        }

        public void setDataBlob(Blob dataBlob) {
            this.dataBlob = dataBlob;
            if (dataBlob == null) {
                    this.data = null;
            } else {
                    this.data = toByteArray(dataBlob);
            }
        }

        private byte[] toByteArray(Blob fromBlob) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                return toByteArrayImpl(fromBlob, baos);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }

        private byte[] toByteArrayImpl(Blob fromBlob, ByteArrayOutputStream baos)
                throws SQLException, IOException {
            byte[] buf = new byte[4000];
            InputStream is = fromBlob.getBinaryStream();
            try {
                for (;;) {
                    int dataSize = is.read(buf);

                    if (dataSize == -1) {
                        break;
                    }
                    baos.write(buf, 0, dataSize);
                }
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                    }
                }
            }
            return baos.toByteArray();
        }
}
