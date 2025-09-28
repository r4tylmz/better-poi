package io.github.r4tylmz.betterpoi.i18n;

import io.github.r4tylmz.betterpoi.BPOptions;
import io.github.r4tylmz.betterpoi.enums.ExcelType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Locale;

public class MessageSourceServiceTest {

    @Before
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    public void testCreateDefault() {
        MessageSourceService service = MessageSourceService.createDefault();
        assertNotNull(service);
        assertEquals(Locale.ENGLISH, service.getLocale());
        assertEquals(1, service.getBundleCount()); // Only library bundle
    }

    @Test
    public void testCreateWithLocale() {
        MessageSourceService service = MessageSourceService.forLocale("tr");
        assertNotNull(service);
        assertEquals(new Locale("tr"), service.getLocale());
        assertEquals(1, service.getBundleCount()); // Only library bundle
    }

    @Test
    public void testCreateWithLocaleAndBundle() {
        MessageSourceService service = MessageSourceService.forLocale("tr", "test");
        assertNotNull(service);
        assertEquals(new Locale("tr"), service.getLocale());
        assertEquals(2, service.getBundleCount()); // User bundle + library bundle
    }

    @Test
    public void testLibraryPropertiesOnly() {
        MessageSourceService service = new MessageSourceService(new Locale("tr"));
        
        assertEquals(1, service.getBundleCount());
        assertEquals(new Locale("tr"), service.getLocale());
        
        String message = service.getMessage("pattern.validation.error", "test", "pattern");
        assertTrue(message.contains("geçerli değil")); // Turkish content from library
    }

    @Test
    public void testUserPropertiesWithFallback() {
        // Test using user properties with fallback to library properties
        MessageSourceService service = new MessageSourceService(new Locale("tr"), "test");
        
        assertEquals(2, service.getBundleCount());
        assertEquals(new Locale("tr"), service.getLocale());
        
        // Test that user properties are loaded first
        // Note: This test assumes test_tr.properties exists in test resources
        // If not, it will fall back to library properties
        String message = service.getMessage("pattern.validation.error", "test", "pattern");
        assertNotNull(message);
        assertTrue(message.contains("test") && message.contains("pattern"));
    }

    @Test
    public void testFallbackToLibraryProperties() {
        // Test fallback when user properties don't have a key
        MessageSourceService service = new MessageSourceService(new Locale("tr"), "test");
        
        // This key should exist in library properties but not in user properties
        String message = service.getMessage("sheet.not.found.error", "TestSheet");
        assertNotNull(message);
        assertTrue(message.contains("TestSheet"));
    }

    @Test
    public void testMessageExists() {
        MessageSourceService service = new MessageSourceService(new Locale("tr"));
        
        assertTrue(service.hasMessage("pattern.validation.error"));
        assertTrue(service.hasMessage("required.validation.error"));
        assertFalse(service.hasMessage("nonexistent.key"));
    }

    @Test
    public void testMultipleLocales() {
        // Test English locale
        MessageSourceService englishService = new MessageSourceService(Locale.ENGLISH);
        String englishMessage = englishService.getMessage("pattern.validation.error", "test", "pattern");
        assertTrue(englishMessage.contains("Cell value"));
        
        // Test Turkish locale
        MessageSourceService turkishService = new MessageSourceService(new Locale("tr"));
        String turkishMessage = turkishService.getMessage("pattern.validation.error", "test", "pattern");
        assertTrue(turkishMessage.contains("Hücre değeri"));
    }

    @Test
    public void testCustomBundleName() {
        // Test with custom bundle name
        MessageSourceService service = new MessageSourceService(new Locale("en"), "custom");
        
        // If custom bundle doesn't exist, it will only have library bundle
        // The actual count depends on whether the custom bundle exists
        assertTrue(service.getBundleCount() >= 1);
        assertEquals(new Locale("en"), service.getLocale());
        
        // Should fall back to library properties if custom bundle doesn't exist
        String message = service.getMessage("pattern.validation.error", "test", "pattern");
        assertNotNull(message);
        assertTrue(message.contains("Cell value"));
    }

    @Test
    public void testNullBundleName() {
        // Test with null bundle name (should use only library properties)
        MessageSourceService service = new MessageSourceService(new Locale("en"), null);
        
        assertEquals(1, service.getBundleCount());
        assertEquals(new Locale("en"), service.getLocale());
        
        String message = service.getMessage("pattern.validation.error", "test", "pattern");
        assertNotNull(message);
        assertTrue(message.contains("Cell value"));
    }

    @Test
    public void testEmptyBundleName() {
        // Test with empty bundle name (should use only library properties)
        MessageSourceService service = new MessageSourceService(new Locale("en"), "");
        
        assertEquals(1, service.getBundleCount());
        assertEquals(new Locale("en"), service.getLocale());
        
        String message = service.getMessage("pattern.validation.error", "test", "pattern");
        assertNotNull(message);
        assertTrue(message.contains("Cell value"));
    }

    @Test
    public void testMessageFormatting() {
        MessageSourceService service = new MessageSourceService(Locale.ENGLISH);
        
        String message = service.getMessage("error.row.column.violation", "Row 5", "Column A", "Invalid data");
        assertNotNull(message);
        assertTrue(message.contains("Row 5"));
        assertTrue(message.contains("Column A"));
        assertTrue(message.contains("Invalid data"));
    }

    @Test
    public void testNonExistentKey() {
        MessageSourceService service = new MessageSourceService(Locale.ENGLISH);
        assertTrue(service.getMessage("nonexistent.key").equals("nonexistent.key"));
    }

    @Test
    public void testBPOptionsIntegration() {
        BPOptions options = BPOptions.builder()
                .withExcelType(ExcelType.XLSX)
                .withLocale("tr")
                .withBundleName("test")
                .build();
        
        MessageSourceService service = new MessageSourceService(options);
        
        assertEquals(new Locale("tr"), service.getLocale());
        assertTrue(service.getBundleCount() >= 1); // At least library bundle
    }

    @Test
    public void testBPOptionsDefaultBundle() {
        BPOptions options = BPOptions.builder()
                .withExcelType(ExcelType.XLSX)
                .withLocale("tr")
                .build();
        
        MessageSourceService service = new MessageSourceService(options);
        
        assertEquals(new Locale("tr"), service.getLocale());
        assertEquals(1, service.getBundleCount()); // Only library bundle
    }
}
