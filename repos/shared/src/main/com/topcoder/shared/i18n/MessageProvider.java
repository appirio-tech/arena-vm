/*
 * MessageProvider
 * 
 * Created 07/18/2007
 */
package com.topcoder.shared.i18n;

import java.lang.reflect.Constructor;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.topcoder.shared.util.logging.Logger;

/**
 * The MessageProvider class provides an entry point for Text localization. 
 * 
 * It provides the means for obtaining localized texts from different bundles
 * and using specific formatters for different message arguments.<p>
 * 
 * When keys are not found on the given bundle, the text message returned is
 * a converted version of the string key. Underscores are translated into spaces.<p>
 * 
 * In addition a {@link MessageFormat} can be obtained that allows complex message 
 * localization.  A helper method is provided to localize {@link Message} objects 
 * using the {@link MessageFormat} class defined for each {@link Message}.<p>
 * To provide a more flexible way for formatting  messages custom formatters can be defined
 * in addition to the ones provided by the {@link MessageFormat} class.
 * 
 * The bundle format follows the standard bundle formats but special keys can be 
 * used to specify custom formatters for a specific message format.<p>
 * 
 * Example:<p>
 * #Simple key Example<br>
 * simple_key=Any simple text to use with getText(String)<br>
 * #Simple message format<br>
 * simple_message_format=This is {0} example of {1} ones.<br>
 * #Custom formatters in MessageFormat<br>
 * custom_format_msg={0} are remaining before the end of the contest {1}<br>
 * custom_format_msg.formats=0,1<br>
 * custom_format_msg.formats.0=ElapsedTimeFormat<br>
 * custom_format_msg.formats.1=com.topcoder.shared.i18n.format.ContestFormat<br>
 * <p>
 * The [key].formats indicates the indexes of the custom formats defined in the file.<p>
 * the [key].formats.[index] indicates the full class name of the format class. In addition a non full name can be given,
 *  this name will be resolved obtaining a localized string from the bundle "formatters" 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MessageProvider {
    private static final Logger log = Logger.getLogger(MessageProvider.class);
    /**
     * The default locale to use
     */
    private static Locale defaultLocale = Locale.getDefault();
    
    /**
     * Sets the default locale for the MessageProvider
     * 
     * @param defaultLocale The default locale to set
     */
    public static void setDefaultLocale(Locale defaultLocale) {
        MessageProvider.defaultLocale = defaultLocale;
    }

    /**
     * Returns the localized text for the given bundle and key, using the 
     * default locale 
     * 
     * @param bundleName The bundle name
     * @param key The key.
     * @return The localized text or the key with all underscores removed if it was not found.
     */
    public static String getText(String bundleName, String key) {
        return getText(bundleName, key, defaultLocale);
    }
    
    
    /**
     * Returns the localized text for the given bundle, key and locale
     * 
     * @param bundleName The bundle name
     * @param key The key.
     * @param locale The locale to use
     * @return The localized text or the key with all underscores removed if it was not found.
     */
    public static String getText(String bundleName, String key, Locale locale) {
        return getValue(bundleName, key, locale);
    }

    private static String getValue(String bundleName, String key, Locale locale) {
        String value = null;
        ResourceBundle bundle = getBundle(bundleName, locale);
        if (bundle !=null) {
            try {
                value = bundle.getString(key);
            } catch (MissingResourceException e) {
                log.warn("Message not found. Bundle:"+bundleName+" key="+key);
            }
        }
        if (value == null) {
            value = generateMessageFromKey(key);
        }
        return value;
    }

    private static String getPlainValue(String bundleName, String key, Locale locale) {
        String value = null;
        ResourceBundle bundle = getBundle(bundleName, locale);
        if (bundle !=null) {
            try {
                value = bundle.getString(key);
            } catch (MissingResourceException e) {
            }
        }
        return value;
    }

    
    private static ResourceBundle getBundle(String bundleName, Locale locale) {
        try {
            return ResourceBundle.getBundle(bundleName, locale);
        } catch (MissingResourceException e) {
            log.warn("Bundle not found. Bundle:"+bundleName);
        }
        return null;
    }
    
    /**
     * Returns a Message Format to use for the given bundle and key using the default locale
     * 
     * @param bundleName The bundle name
     * @param key The key.
     * @return The message format.
     */
    public static MessageFormat getFormat(String bundleName, String key) {
        return getFormat(bundleName, key, defaultLocale);
    }
    
    /**
     * Returns a Message Format to use for the given bundle, key and locale
     * 
     * @param bundleName The bundle name
     * @param key The key.
     * @param locale The locale to use
     * @return The message format.
     */
    public static MessageFormat getFormat(String bundleName, String key, Locale locale) {
        MessageFormat format = new MessageFormat(getValue(bundleName, key, locale), locale);
        applySpecificFormat(bundleName, key, format, locale);
        return format;
    }
    
    /**
     * Returns the localized text message for the give Message object, 
     * using the default locale
     * 
     * @param message The message to localize
     * @return The localized text message
     */
    public static String getText(Message message) {
        return getText(message, defaultLocale);
    }
    
    /**
     * Returns the localized text message for the give Message object, 
     * using the given locale
     * 
     * @param message The message to localize
     * @param locale The locale to use.
     * @return The localized text message
     */
    public static String getText(Message message, Locale locale) {
        return getFormat(message.getBundleName(), message.getKey(), locale).format(message.getValues());
    }


    private static String generateMessageFromKey(String key) {
        boolean spcs = key.indexOf(' ') > -1;
        if (spcs) {
            log.info("Invalid message key, it contains spaces: "+key);
        }
        return key.replace('_', ' ').replace("\\ ", "_");
    }
    
    
    private static void applySpecificFormat(String bundleName, String key, MessageFormat msgFormat, Locale locale) {
        String formats = key+".formats";
        String value = getPlainValue(bundleName, formats, locale);
        if (value == null) {
            return;
        }
        String[] indexes = value.split(",");
        for (int i = 0; i < indexes.length; i++) {
            int index = Integer.parseInt(indexes[i]);
            String formatName = getPlainValue(bundleName, formats+"."+index, locale);
            Format format = createFormat(formatName, locale);
            if (format != null) {
                msgFormat.setFormatByArgumentIndex(index, format);
            }
        }
    }

    private static Format createFormat(String value, Locale locale) {
        int pos = value.indexOf(' ');
        String formatName = value;
        String pattern = null;
        if (pos > -1) {
            formatName = value.substring(0, pos);
            pattern = value.substring(pos+1);
        }
        return createFormatInstance(formatName, pattern, locale);
    }

    private static Format createFormatInstance(String formatName, String pattern, Locale locale) {
        String formatClassname = getFormatClassName(formatName, locale);
        Format format = null;
        try {
            Class clazz = (Class) Class.forName(formatClassname);
            Constructor constructor;
            if (pattern == null || pattern.length() == 0) {
                constructor = clazz.getConstructor(new Class[]{Locale.class});
                if (constructor != null) {
                    format = (Format) constructor.newInstance(new Object[] {locale});
                } 
            } else {
                constructor = clazz.getConstructor(new Class[]{String.class, Locale.class});
                if (constructor != null) {
                    format = (Format) constructor.newInstance(new Object[] {pattern, locale});
                } else {
                    constructor = clazz.getConstructor(new Class[]{String.class});
                    if (constructor != null) {
                        format = (Format) constructor.newInstance(new Object[] {pattern});
                    } else {
                        log.info("Discarding pattern for format: "+formatName+" pattern:"+pattern);
                    }
                }
            } 
            if (format == null) {
                format = (Format) clazz.newInstance();
            }
        } catch (Exception e) {
            log.error("Failed to created format",e);
            throw new IllegalStateException("MessageProvider is not configured properly");
        }        
        return format;
    }

    private static String getFormatClassName(String formatName, Locale locale) {
        String formatClassname = null;
        try {
            formatClassname = getPlainValue("formatters", formatName, locale);
        } catch (MissingResourceException e) {
            log.info("formatters is not defined");
        }
        if (formatClassname == null) {
            formatClassname = formatName;
        }
        return formatClassname;
    }
}
