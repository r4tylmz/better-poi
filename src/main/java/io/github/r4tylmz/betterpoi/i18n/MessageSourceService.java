package io.github.r4tylmz.betterpoi.i18n;

import io.github.r4tylmz.betterpoi.BPOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Service for handling internationalization (i18n) of messages.
 * This class is compatible with Java 1.8 and uses standard Java libraries.
 */
public class MessageSourceService {
    private static final String LIBRARY_BUNDLE_NAME = "messages";
    private final List<ResourceBundle> bundles = new ArrayList<>();
    private final Locale locale;

    /**
     * Creates a new MessageSourceService for the specified locale.
     * Uses library's built-in properties file.
     *
     * @param locale the locale to use for messages
     */
    public MessageSourceService(Locale locale) {
        this(locale, null);
    }

    /**
     * Creates a new MessageSourceService for the specified locale and bundle name.
     * If bundleName is provided, it will be tried first, then fallback to library properties.
     *
     * @param locale     the locale to use for messages
     * @param bundleName the name of the user-defined resource bundle (can be null)
     */
    public MessageSourceService(Locale locale, String bundleName) {
        this.locale = locale;
        initializeBundles(bundleName);
    }

    /**
     * Creates a new MessageSourceService using the provided BPOptions.
     * If the bundle name or locale is not specified in the options, defaults are used.
     * Supports fallback from user properties to library properties.
     *
     * @param options the BPOptions containing bundle name and locale
     */
    public MessageSourceService(BPOptions options) {
        this.locale = options.getLocale() != null ? options.getLocale() : Locale.getDefault();
        initializeBundles(options.getBundleName());
    }

    /**
     * Initializes the resource bundles with fallback support.
     * First tries user-defined bundle, then falls back to library bundle.
     *
     * @param userBundleName the user-defined bundle name (can be null)
     */
    private void initializeBundles(String userBundleName) {
        if (userBundleName != null && !userBundleName.trim().isEmpty()) {
            try {
                ResourceBundle userBundle = ResourceBundle.getBundle(userBundleName, locale, new Utf8Control());
                if (userBundle != null) {
                    bundles.add(userBundle);
                }
            } catch (Exception e) {
            }
        }

        try {
            ResourceBundle libraryBundle = ResourceBundle.getBundle(LIBRARY_BUNDLE_NAME, locale, new Utf8Control());
            if (libraryBundle != null) {
                bundles.add(libraryBundle);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load library properties file: " + LIBRARY_BUNDLE_NAME, e);
        }

        if (bundles.isEmpty()) {
            throw new RuntimeException("No resource bundles could be loaded");
        }
    }

    /**
     * Creates a MessageSourceService for the specified locale and bundle name.
     *
     * @param locale     the locale code (e.g., "en", "tr")
     * @param bundleName the name of the user-defined resource bundle
     * @return a new MessageSourceService instance
     */
    public static MessageSourceService forLocale(String locale, String bundleName) {
        Locale localeObject = new Locale(locale);
        return new MessageSourceService(localeObject, bundleName);
    }

    /**
     * Creates a MessageSourceService using the system default locale.
     * Uses library's built-in properties file.
     *
     * @return a new MessageSourceService instance
     */
    public static MessageSourceService createDefault() {
        return new MessageSourceService(Locale.getDefault());
    }

    /**
     * Creates a MessageSourceService for the specified language.
     * Uses library's built-in properties file.
     *
     * @param language the language code (e.g., "en" for English, "tr" for Turkish)
     * @return a new MessageSourceService instance
     */
    public static MessageSourceService forLocale(String language) {
        return new MessageSourceService(new Locale(language));
    }

    /**
     * Gets the locale being used by this service.
     *
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets the number of resource bundles loaded.
     *
     * @return the number of bundles
     */
    public int getBundleCount() {
        return bundles.size();
    }

    /**
     * Custom ResourceBundle.Control implementation to handle UTF-8 encoded properties files.
     * This is necessary because the default ResourceBundle does not support UTF-8 encoding.
     */
    private static class Utf8Control extends ResourceBundle.Control {
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

    /**
     * Gets a localized message with the given key and formats it with the provided arguments.
     * Searches through bundles in order: user-defined first, then library properties.
     *
     * @param key  the message key in the resource bundle
     * @param args the arguments to format the message with
     * @return the formatted localized message
     * @throws RuntimeException if the key is not found in any bundle
     */
    public String getMessage(String key, Object... args) {
        String pattern = getMessagePattern(key);
        return MessageFormat.format(pattern, args);
    }

    /**
     * Gets a message pattern from the resource bundles.
     * Searches through bundles in order until the key is found.
     *
     * @param key the message key
     * @return the message pattern
     * @throws RuntimeException if the key is not found in any bundle
     */
    private String getMessagePattern(String key) {
        for (ResourceBundle bundle : bundles) {
            if (bundle.containsKey(key)) {
                return bundle.getString(key);
            }
        }
        
        // Fallback to key itself if not found
        return key;
    }

    /**
     * Checks if a message key exists in any of the loaded bundles.
     *
     * @param key the message key to check
     * @return true if the key exists, false otherwise
     */
    public boolean hasMessage(String key) {
        for (ResourceBundle bundle : bundles) {
            if (bundle.containsKey(key)) {
                return true;
            }
        }
        return false;
    }
}