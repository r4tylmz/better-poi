package io.github.r4tylmz.betterpoi.i18n;

import io.github.r4tylmz.betterpoi.BPOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Service for handling internationalization (i18n) of messages.
 * This class is compatible with Java 1.8 and uses standard Java libraries.
 */
public class MessageSourceService {
    private static final String BUNDLE_NAME = "messages";
    private final ResourceBundle bundle;

    /**
     * Creates a new MessageSourceService for the specified locale.
     *
     * @param locale the locale to use for messages
     */
    public MessageSourceService(Locale locale) {
        this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, new Utf8Control());
    }

    /**
     * Creates a new MessageSourceService for the specified locale and bundle name.
     *
     * @param locale     the locale to use for messages
     * @param bundleName the name of the resource bundle
     */
    public MessageSourceService(Locale locale, String bundleName) {
        this.bundle = ResourceBundle.getBundle(bundleName, locale, new Utf8Control());
    }

    /**
     * Creates a new MessageSourceService using the provided BPImporterOptions.
     * If the bundle name or locale is not specified in the options, defaults are used.
     *
     * @param options the BPImporterOptions containing bundle name and locale
     */
    public MessageSourceService(BPOptions options) {
        this.bundle = ResourceBundle.getBundle(
                options.getBundleName() != null ? options.getBundleName() : BUNDLE_NAME,
                options.getLocale() != null ? options.getLocale() : Locale.getDefault(),
                new Utf8Control()
        );
    }

    /**
     * Creates a MessageSourceService for the specified locale and bundle name.
     *
     * @param locale     the locale code (e.g., "en", "tr")
     * @param bundleName the name of the resource bundle
     * @return a new MessageSourceService instance
     */
    public static MessageSourceService forLocale(String locale, String bundleName) {
        Locale localeObject = new Locale(locale);
        return new MessageSourceService(localeObject, bundleName);
    }

    /**
     * Creates a MessageSourceService using the system default locale.
     *
     * @return a new MessageSourceService instance
     */
    public static MessageSourceService createDefault() {
        return new MessageSourceService(Locale.getDefault());
    }

    /**
     * Creates a MessageSourceService for the specified language.
     *
     * @param language the language code (e.g., "en" for English, "tr" for Turkish)
     * @return a new MessageSourceService instance
     */
    public static MessageSourceService forLocale(String language) {
        return new MessageSourceService(new Locale(language));
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
     *
     * @param key  the message key in the resource bundle
     * @param args the arguments to format the message with
     * @return the formatted localized message
     */
    public String getMessage(String key, Object... args) {
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, args);
    }
}