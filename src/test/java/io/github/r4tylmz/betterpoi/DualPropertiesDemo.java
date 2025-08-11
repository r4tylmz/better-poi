package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.enums.ExcelType;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;

import java.util.Locale;

/**
 * Demonstration program showing the dual properties functionality.
 * This demo shows how users can use both their own properties files
 * and library properties files with intelligent fallback.
 */
public class DualPropertiesDemo {

    public static void main(String[] args) {
        System.out.println("=== Better POI Dual Properties Demo ===\n");

        // Demo 1: Using library properties only (default behavior)
        demoLibraryPropertiesOnly();

        // Demo 2: Using custom properties with fallback to library properties
        demoCustomPropertiesWithFallback();

        // Demo 3: Multiple locales with custom properties
        demoMultipleLocales();

        // Demo 4: BPOptions integration
        demoBPOptionsIntegration();

        System.out.println("\n=== Demo Complete ===");
    }

    /**
     * Demonstrates using only library properties (default behavior).
     */
    private static void demoLibraryPropertiesOnly() {
        System.out.println("1. LIBRARY PROPERTIES ONLY (Default Behavior)");
        System.out.println("=============================================");

        // Create service using only library properties
        MessageSourceService service = new MessageSourceService(new Locale("tr"));
        
        System.out.println("Locale: " + service.getLocale());
        System.out.println("Bundles loaded: " + service.getBundleCount());
        System.out.println("Using library properties: messages_tr.properties");

        // Test some library messages
        String patternError = service.getMessage("pattern.validation.error", "test@email", "email pattern");
        String requiredError = service.getMessage("required.validation.error", "Employee Name");
        String duplicateError = service.getMessage("duplicate.row.error");

        System.out.println("\nLibrary Messages (Turkish):");
        System.out.println("  Pattern Error: " + patternError);
        System.out.println("  Required Error: " + requiredError);
        System.out.println("  Duplicate Error: " + duplicateError);
        System.out.println();
    }

    /**
     * Demonstrates using custom properties with fallback to library properties.
     */
    private static void demoCustomPropertiesWithFallback() {
        System.out.println("2. CUSTOM PROPERTIES WITH FALLBACK");
        System.out.println("==================================");

        // Create service using custom properties with fallback
        MessageSourceService service = new MessageSourceService(new Locale("tr"), "test");
        
        System.out.println("Locale: " + service.getLocale());
        System.out.println("Bundles loaded: " + service.getBundleCount());
        System.out.println("Using custom properties: test_tr.properties + fallback to messages_tr.properties");

        // Test custom messages (should come from test_tr.properties)
        String customPatternError = service.getMessage("pattern.validation.error", "test@email", "email pattern");
        String customRequiredError = service.getMessage("required.validation.error", "Employee Name");
        String customDuplicateError = service.getMessage("duplicate.row.error");

        // Test custom business rule messages (only in test properties)
        String businessRule = service.getMessage("custom.business.rule", "Invalid employee data");
        String dataIntegrity = service.getMessage("data.integrity.error", "Missing required fields");

        // Test library-only messages (should fallback to library properties)
        String sheetError = service.getMessage("sheet.not.found.error", "TestSheet");

        System.out.println("\nCustom Messages (from test_tr.properties):");
        System.out.println("  Pattern Error: " + customPatternError);
        System.out.println("  Required Error: " + customRequiredError);
        System.out.println("  Duplicate Error: " + customDuplicateError);
        System.out.println("  Business Rule: " + businessRule);
        System.out.println("  Data Integrity: " + dataIntegrity);

        System.out.println("\nFallback Messages (from library messages_tr.properties):");
        System.out.println("  Sheet Error: " + sheetError);
        System.out.println();
    }

    /**
     * Demonstrates multiple locales with custom properties.
     */
    private static void demoMultipleLocales() {
        System.out.println("3. MULTIPLE LOCALES WITH CUSTOM PROPERTIES");
        System.out.println("==========================================");

        // English locale with custom properties
        MessageSourceService englishService = new MessageSourceService(Locale.ENGLISH, "test");
        System.out.println("English Locale:");
        System.out.println("  Bundles: " + englishService.getBundleCount());
        
        String englishPattern = englishService.getMessage("pattern.validation.error", "test@email", "email pattern");
        String englishBusiness = englishService.getMessage("custom.business.rule", "Invalid data");
        System.out.println("  Pattern: " + englishPattern);
        System.out.println("  Business: " + englishBusiness);

        // Turkish locale with custom properties
        MessageSourceService turkishService = new MessageSourceService(new Locale("tr"), "test");
        System.out.println("\nTurkish Locale:");
        System.out.println("  Bundles: " + turkishService.getBundleCount());
        
        String turkishPattern = turkishService.getMessage("pattern.validation.error", "test@email", "email pattern");
        String turkishBusiness = turkishService.getMessage("custom.business.rule", "Invalid data");
        System.out.println("  Pattern: " + turkishPattern);
        System.out.println("  Business: " + turkishBusiness);
        System.out.println();
    }

    /**
     * Demonstrates BPOptions integration.
     */
    private static void demoBPOptionsIntegration() {
        System.out.println("4. BPOPTIONS INTEGRATION");
        System.out.println("=========================");

        // Create options with custom bundle
        BPOptions customOptions = BPOptions.builder()
                .withExcelType(ExcelType.XLSX)
                .withLocale("tr")
                .withBundleName("test")
                .build();

        System.out.println("Custom Bundle Options:");
        System.out.println("  Excel Type: " + customOptions.getExcelType());
        System.out.println("  Locale: " + customOptions.getLocale());
        System.out.println("  Bundle Name: " + customOptions.getBundleName());
        System.out.println("  Has Custom Bundle: " + customOptions.hasCustomBundle());

        // Create service using options
        MessageSourceService service = new MessageSourceService(customOptions);
        System.out.println("  Service Bundles: " + service.getBundleCount());

        // Test message retrieval
        String message = service.getMessage("custom.business.rule", "Test violation");
        System.out.println("  Custom Message: " + message);

        // Create options without custom bundle (library properties only)
        BPOptions libraryOptions = BPOptions.builder()
                .withExcelType(ExcelType.XLSX)
                .withLocale("tr")
                .build();

        System.out.println("\nLibrary Bundle Options:");
        System.out.println("  Excel Type: " + libraryOptions.getExcelType());
        System.out.println("  Locale: " + libraryOptions.getLocale());
        System.out.println("  Bundle Name: " + libraryOptions.getBundleName());
        System.out.println("  Has Custom Bundle: " + libraryOptions.hasCustomBundle());

        // Create service using library options
        MessageSourceService libraryService = new MessageSourceService(libraryOptions);
        System.out.println("  Service Bundles: " + libraryService.getBundleCount());

        // Test message retrieval
        String libraryMessage = libraryService.getMessage("pattern.validation.error", "test", "pattern");
        System.out.println("  Library Message: " + libraryMessage);
        System.out.println();
    }

    /**
     * Demonstrates the fallback mechanism in detail.
     */
    private static void demoFallbackMechanism() {
        System.out.println("5. FALLBACK MECHANISM DETAILS");
        System.out.println("===============================");

        // Create service with custom bundle
        MessageSourceService service = new MessageSourceService(new Locale("tr"), "test");
        
        System.out.println("Fallback Order:");
        System.out.println("  1. test_tr.properties (your Turkish properties)");
        System.out.println("  2. test.properties (your default properties)");
        System.out.println("  3. messages_tr.properties (library Turkish properties)");
        System.out.println("  4. messages.properties (library default properties)");

        System.out.println("\nTesting Fallback:");
        
        // This key exists in test_tr.properties
        if (service.hasMessage("custom.business.rule")) {
            String message = service.getMessage("custom.business.rule", "Test");
            System.out.println("  ✓ custom.business.rule: " + message);
        }

        // This key exists in library properties but not in test properties
        if (service.hasMessage("sheet.not.found.error")) {
            String message = service.getMessage("sheet.not.found.error", "TestSheet");
            System.out.println("  ✓ sheet.not.found.error: " + message + " (from library)");
        }

        // This key doesn't exist anywhere
        if (!service.hasMessage("nonexistent.key")) {
            System.out.println("  ✗ nonexistent.key: Not found in any bundle");
        }
    }
}
