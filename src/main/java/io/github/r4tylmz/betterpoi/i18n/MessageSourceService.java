package io.github.r4tylmz.betterpoi.i18n;

import java.text.MessageFormat;
import java.util.Locale;
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
        this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
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
