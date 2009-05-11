package com.tonbeller.tbutils.res;

import java.io.File;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import com.tonbeller.tbutils.res.PersistentResourceProvider;
import com.tonbeller.tbutils.res.ReplacingResourceProvider;
import com.jaspersoft.jasperserver.api.common.util.StaticCharacterEncodingProvider;

/**
 * Facade to access key/value pairs and application home directory.
 * 
 * @author av
 */
public class Resources {
  private ReplacingResourceProvider provider;
  private Locale locale;
  private File home;
  private PersistentResourceProvider persistentProvider;
  private CompositeResourceProvider compositeProvider;
  
  static final String PERSISTENT_PROPERTIES = "persistent.properties";

  /**
   * returns a <code>Resources</code> instance in the default locale
   * of the VM. This is NOT the locale of the users browser etc.
   */
  public static Resources instance() {
    return ResourcesFactory.instance().getResources(null, null);
  }

  /**
   * returns a <code>Resources</code> instance in the configured locale, or if
   * no locale was configured, in the browsers locale.
   */
  public static Resources instance(Locale browserLocale) {
    return ResourcesFactory.instance().getResources(browserLocale, null);
  }

  /**
   * returns a <code>Resources</code> instance in the configured locale, or if
   * no locale was configured, in the browsers locale. 
   * <p>
   * If the package of <code>clazz</code>
   * contains a ResourceBundle named
   * <code>resources.properties</code> 
   * that bundle will be added to the returned <code>Resources</code>.
   */
  public static Resources instance(Locale browserLocale, Class clazz) {
    return ResourcesFactory.instance().getResources(browserLocale, clazz.getPackage().getName() + ".resources");
  }

  /**
   * returns a <code>Resources</code> instance in the default locale. 
   * <p>
   * If the package of <code>clazz</code>
   * contains a ResourceBundle named
   * <code>resources.properties</code> 
   * that bundle will be added to the returned <code>Resources</code>.
   */
  public static Resources instance(Class clazz) {
    return ResourcesFactory.instance().getResources(Locale.getDefault(), clazz.getPackage().getName() + ".resources");
  }
  
  /**
   * returns a <code>Resources</code> instance in the configured locale, or if
   * no locale was configured, in the browsers locale. 
   * <p>
   * The named <code>ResourceBundle</code> will be added to the returned <code>Resources</code>.
   */
  public static Resources instance(Locale browserLocale, String bundleName) {
    return ResourcesFactory.instance().getResources(browserLocale, bundleName);
  }
  
  Resources(CompositeResourceProvider compositeProvider, Locale locale, File home) {
    // js:i18n use spring LocaleContextHolder
    this.locale = LocaleContextHolder.getLocale();
  	if (this.locale == null) {
  	    this.locale = locale;
  	}
    this.home = home;
    this.compositeProvider = compositeProvider;
    File persistentProperties = new File(home, PERSISTENT_PROPERTIES);
    this.persistentProvider = new FilePersistentResourceProvider(persistentProperties);
    // make persistentProvider the first one to look into
    compositeProvider.add(0, persistentProvider);
    this.provider = new ReplacingResourceProvider(compositeProvider);
	// js:i18n
    collator = Collator.getInstance(locale);
  }

  /**
   * looksup a string value. 
   * @param key key in resourcebundle
   * @param defaultValue returnvalue if key was not found
   * @return resource value or defaultValue
   */
  public String getOptionalString(String key, String defaultValue) {
	// js:i18n use spring resource bundle
	String s; 
	if (getResourceBundle() == null) {
		s = provider.getString(key);
	}
	else {
		s = getResourceBundle().getMessage(key, null, locale);
	}
	
	if (s == null) {
		s = provider.getString(key);
	}

    if (s == null) {
      s = defaultValue;
      return s;
    }
    else {
      s = replace(s);
      return s.trim();
    }
  }
  
  /**
   * looks up a string value. 
   * @throws MissingResourceException if key was not found
   */
  public String getString(String key) throws MissingResourceException {
    // js:i18n use spring resource bundle
	String s = null; 
	if (getResourceBundle() != null) {
		s = getResourceBundle().getMessage(key, null, locale);
	}
	
	if (s == null && provider != null) {
	    s = provider.getString(key);
	}
	
    if (s == null)
      //throw new MissingResourceException("missing resource for " + key, this.getClass().getName(), key);
      s = key;
    
    return s.trim();
  }

  /**
   * looks up a string value and formats via MessageFormat
   */
  public String getString(String key, Object arg) throws MissingResourceException {
    String fmt = getString(key);
    return MessageFormat.format(fmt, new Object[]{arg});
  }

  /**
   * looks up a string value and formats via MessageFormat
   */
  public String getString(String key, Object arg0, Object arg1) throws MissingResourceException {
    String fmt = getString(key);
    return MessageFormat.format(fmt, new Object[]{arg0, arg1});
  }

  /**
   * looks up a string value and formats via MessageFormat
   */
  public String getString(String key, Object[] args) throws MissingResourceException {
    String fmt = getString(key);
    return MessageFormat.format(fmt, args);
  }

  /**
   * looks up a boolean value 
   */
  public boolean getBoolean(String key) throws MissingResourceException {
    String s = getString(key);
    return "true".equals(s) || "on".equals(s) || "yes".equals(s);
  }

  public boolean getOptionalBoolean(String key, boolean defaultValue) {
    try {
      return getBoolean(key);
    } catch (MissingResourceException e) {
      return defaultValue;
    }
  }

  /**
   * looks up an integervalue 
   */
  public int getInteger(String key) throws MissingResourceException {
    String s = getString(key);
    return Integer.parseInt(s);
  }

  public int getOptionalInteger(String key, int defaultValue) {
    try {
      return getInteger(key);
    } catch (MissingResourceException e) {
      return defaultValue;
    }
  }
  
  /**
   * looks up a long value 
   */
  public long getLong(String key) throws MissingResourceException {
    String s = getString(key);
    return Long.parseLong(s);
  }

  public long getOptionalLong(String key, long defaultValue) {
    try {
      return getLong(key);
    } catch (MissingResourceException e) {
      return defaultValue;
    }
  }
  
  /**
   * looks up a double value 
   */
  public double getDouble(String key) throws MissingResourceException {
    String s = getString(key);
    return Double.parseDouble(s);
  }

  public double getOptionalDouble(String key, double defaultValue) {
    try {
      return getDouble(key);
    } catch (MissingResourceException e) {
      return defaultValue;
    }
  }
  
  /**
   * returns a collection of all known keys. The list maybe incomplete
   * eg. JNDI keys are not returned.
   */
  public Collection keySet() {
    return provider.keySet();
  }

  /**
   * returns the locale for this instance
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * returns the applications "home" directory which may contain 
   * properties files and other resources. 
   */
  public File getHomeDir() {
    return home;
  }
  
  /**
   * stores a key/value pair that will be persisted in the filesystem.
   * @see #flush()
   */
  public void setPersistentString(String key, String value) {
    persistentProvider.store(key, value);
  }
  
  /**
   * stores a key/value pair that will be persisted in the filesystem.
   * @see #flush()
   */
  public void setPersistentBoolean(String key, boolean value) {
    persistentProvider.store(key, value ? "true" : "false");
  }

  /**
   * stores a key/value pair that will be persisted in the filesystem.
   * @see #flush()
   */
  public void setPersistentInteger(String key, int value) {
    persistentProvider.store(key, Integer.toString(value));
  }

  /**
   * stores a key/value pair that will be persisted in the filesystem.
   * @see #flush()
   */
  public void setPersistentLong(String key, long value) {
    persistentProvider.store(key, Long.toString(value));
  }

  /**
   * stores a key/value pair that will be persisted in the filesystem.
   * @see #flush()
   */
  public void setPersistentDouble(String key, double value) {
    persistentProvider.store(key, Double.toString(value));
  }
  
  public void removePersistent(String key) {
    persistentProvider.remove(key);
  }
  
  /**
   * commits changes into the storage.
   */
  public void flush() {
    persistentProvider.flush();
  }
  
  /**
   * returns all key/value pairs for logging / debug
   * @see StringDumper
   */
  public void dump(Dumper d) {
    provider.dump(d);
  }
  
  /**
   * scans <code>s</code> for resource keys and replaces the keys by their values.
   * <p />
   * <code>s</code> may contain resource keys in $-notation like ant, for example 
   * <code>${java.io.tmpdir}</code>.
   * If the property exists, its replaced by its value, e.g. <code>/tmp</code>.
   * If the property does not exsist, the string is not changed, e.g. it remains
   * <code>${java.io.tmpdir}</code>. A literal <code>${</code> 
   * is written as <code>$${</code> (like ant), i.e. <code>$${</code> is
   * replaced by <code>${</code>
   * <p />
   * Example:
   * <pre>
   *   Resources res = Resources.instance();
   *   String s = res.replace("${java.io.tmpdir}/mylogs");
   *   // s = "c:\\temp/mylogs" for example
   *   File myLogs = new File(s);
   * </pre>
   * <p />
   * Property values are replaced automatically, e.g. if the properties file contains
   * <pre>
   * mylogs = ${java.io.tmpdir}/mylogs
   * </pre>
   * then <code>Resources.instance().getString("mylogs")</code> will return "c:\\temp/mylogs".
   */
  public String replace(String s) {
    return provider.replace(s);
  }
  
  /**
   * returns the list of {@link ResourceProvider}'s that this instance
   * searches for properties. The list may be modified.
   * @see ResourceProvider
   */
  public List getProviders() {
    return compositeProvider.getProviders();
  }

	// js:i18n spring/jasperserver
  /**
   * for spring IoC
   */
  public Resources() {}

  /**
   * spring message connection
   */
  static private ReloadableResourceBundleMessageSource resourceBundle;
  
  /**
   * returns resourceBundle provided by spring IoC
   * @return
   */
  public ReloadableResourceBundleMessageSource getResourceBundle() {
      return resourceBundle;
  }

  /**
   * sets resourceBundle provided by spring IoC
   * @param resourceBundleIn
   */
  public void setResourceBundle(ReloadableResourceBundleMessageSource resourceBundleIn) {
      resourceBundle = resourceBundleIn;
  }
  
  /**
   * spring message connection
   */
  static private StaticCharacterEncodingProvider encodingProvider;

  /**
   * returns character encoding provided by jaspersoft
   * @return
   */
  public StaticCharacterEncodingProvider getEncodingProvider() {
	  return encodingProvider;
  }

  /**
   * sets character encoding provided by jaspersoft
   * @param encodingProvider
   */
  public void setEncodingProvider(StaticCharacterEncodingProvider encodingProviderIn) {
	  encodingProvider = encodingProviderIn;
  }
  
  /**
   * helper function for jaspersoft character encoding
   * @return
   */
  public String getCharacterEncoding() {
	  if (getEncodingProvider() != null) {
		  return getEncodingProvider().getCharacterEncoding();
	  }
	  return getString("wcf.Resources.message.defaultCharacterEncoding"); // e.g., utf-8
  }
  
  /**
   * helper function for spring locale
   * @return
   */
  public Locale getLocaleContextHolderLocale() {
	  return LocaleContextHolder.getLocale();
  }
  
  static private Collator collator;
  
  public Collator getCollator() {
	  return collator;
  }
  
  public void setCollator(Collator collatorIn) {
	  collator = collatorIn;
  }
  
}