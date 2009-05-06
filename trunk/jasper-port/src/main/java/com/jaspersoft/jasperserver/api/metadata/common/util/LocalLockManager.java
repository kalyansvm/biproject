/*
 * Copyright (C) 2006 JasperSoft http://www.jaspersoft.com
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

package com.jaspersoft.jasperserver.api.metadata.common.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

import net.sf.jasperreports.engine.util.Pair;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: LocalLockManager.java 13142 2008-04-23 15:47:20Z lucian $
 */
public class LocalLockManager implements LockManager {
	
	private final static Log log = LogFactory.getLog(LocalLockManager.class);
	
	private Set locks = new HashSet();

	public LockHandle lock(String lockName, String key) {
		Pair lockKey = getLockKey(lockName, key);
		
		if (log.isDebugEnabled()) {
			log.debug("Acquiring lock for " + lockKey);
		}
		
		synchronized (locks) {
			while (locks.contains(lockKey)) {
				try {
					locks.wait();
				} catch (InterruptedException e) {
					throw new JSExceptionWrapper(e);
				}
			}
			locks.add(lockKey);
			
			if (log.isDebugEnabled()) {
				log.debug("Acquired lock for " + lockKey);
			}
		}
		return new LockKey(lockKey);
	}

	public void unlock(LockHandle lock) {
		if (log.isDebugEnabled()) {
			log.debug("Releasing lock for " + lock.getLockKey());
		}
		
		synchronized (locks) {
			locks.remove(lock.getLockKey());
			locks.notifyAll();
		}
	}

	protected Pair getLockKey(String lockName, String key) {
		return new Pair(lockName, key);
	}

}
