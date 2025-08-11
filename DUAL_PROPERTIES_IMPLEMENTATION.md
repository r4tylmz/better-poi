# Better POI Dual Properties Implementation

## Overview

The Better POI library now supports **dual properties functionality**, allowing users to use both their own custom properties files AND the library's built-in properties files with intelligent fallback.

## Problem Solved

**Before**: Users could only use library properties or completely override them, requiring them to duplicate all messages.

**After**: Users can customize only the messages they want while automatically falling back to library properties for everything else.

## Key Features

✅ **User-defined properties** - Your custom messages and terminology  
✅ **Library properties** - Built-in messages in multiple languages  
✅ **Intelligent fallback** - Automatic fallback from user to library properties  
✅ **Multiple locales** - Support for any number of languages  
✅ **Backward compatibility** - Existing code continues to work  
✅ **UTF-8 support** - Full internationalization support  

## Implementation Details

### 1. Enhanced MessageSourceService

The `MessageSourceService` class now supports multiple resource bundles:

```java
public class MessageSourceService {
    private static final String LIBRARY_BUNDLE_NAME = "messages";
    private final List<ResourceBundle> bundles = new ArrayList<>();
    private final Locale locale;
    
    // ... implementation details
}
```

**Key Methods:**
- `getMessage(String key, Object... args)` - Get localized message with fallback
- `hasMessage(String key)` - Check if key exists in any bundle
- `getBundleCount()` - Get number of loaded bundles
- `getLocale()` - Get current locale

### 2. Enhanced BPOptions

The `BPOptions` class now provides better control over properties:

```java
public final class BPOptions {
    // ... existing fields
    
    /**
     * Checks if custom bundle name is specified.
     */
    public boolean hasCustomBundle() {
        return bundleName != null && !bundleName.trim().isEmpty();
    }
}
```

**Key Changes:**
- `withBundleName(null)` - Use only library properties (default)
- `withBundleName("myapp")` - Use custom properties with fallback
- `hasCustomBundle()` - Check if custom bundle is specified

### 3. Fallback Mechanism

The library implements a sophisticated fallback system:

```
Properties Resolution Order:
1. Your localized properties (e.g., project_tr.properties)
2. Your default properties (e.g., project.properties)  
3. Library localized properties (e.g., messages_tr.properties)
4. Library default properties (e.g., messages.properties)
```

## Usage Examples

### Example 1: Use Library Properties Only (Default)

```java
// Uses library's built-in messages.properties and messages_tr.properties
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .build();

BPImporter<EmployeeWorkbook> importer = new BPImporter<>(EmployeeWorkbook.class, options);
```

### Example 2: Use Custom Properties with Fallback

```java
// First tries your properties, then falls back to library properties
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .withBundleName("project")
        .build();

BPImporter<EmployeeWorkbook> importer = new BPImporter<>(EmployeeWorkbook.class, options);
```

### Example 3: Multiple Locales

```java
// Turkish locale
BPOptions turkishOptions = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .withBundleName("myapp")
        .build();

// French locale
BPOptions frenchOptions = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("fr")
        .withBundleName("myapp")
        .build();
```

## Properties File Structure

### Library Properties (Built-in)
```
src/main/resources/
├── messages.properties          # English (default)
└── messages_tr.properties      # Turkish
```

### Your Custom Properties
```
src/main/resources/
├── project.properties          # Your default messages
├── project_tr.properties      # Your Turkish messages
├── project_fr.properties      # Your French messages
└── project_de.properties      # Your German messages
```

## Example Custom Properties Files

### project.properties (Default)
```properties
# Custom validation messages
pattern.validation.error=Custom pattern validation failed: {0} does not match {1}
required.validation.error=Field {0} is mandatory and cannot be empty
duplicate.row.error=Duplicate entry detected in the data

# Custom business rule messages
custom.business.rule=Business rule violation: {0}
data.integrity.error=Data integrity check failed: {0}
```

### project_tr.properties (Turkish)
```properties
# Custom Turkish validation messages
pattern.validation.error=Özel desen doğrulaması başarısız: {0}, {1} ile eşleşmiyor
required.validation.error=Alan {0} zorunludur ve boş olamaz
duplicate.row.error=Verilerde yinelenen giriş tespit edildi

# Custom business rule messages in Turkish
custom.business.rule=İş kuralı ihlali: {0}
data.integrity.error=Veri bütünlüğü kontrolü başarısız: {0}
```

## Benefits

### For Users
✅ **Customize only what you need** - Define just the messages you want to change  
✅ **Automatic fallback** - Library properties fill in any missing messages  
✅ **Multiple language support** - Easy to add new locales  
✅ **Professional appearance** - Your app uses your terminology and branding  
✅ **Easy maintenance** - Update messages without touching library code  

### For Library Maintainers
✅ **Backward compatibility** - Existing users continue to work  
✅ **Reduced support** - Users can customize messages themselves  
✅ **Better adoption** - More flexible for different use cases  
✅ **Professional library** - Enterprise-grade internationalization support  

## Migration Guide

### From Old Approach

**Before (Library properties only):**
```java
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .withBundleName("messages")  // Always used library properties
        .build();
```

**After (Your properties with fallback):**
```java
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .withBundleName("myapp")     // Uses your properties first
        .build();
```

**Or keep using library properties:**
```java
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        // No withBundleName() - uses library properties
        .build();
```

## Testing

The implementation includes comprehensive tests:

- **MessageSourceServiceTest** - Tests dual properties functionality
- **DualPropertiesDemo** - Live demonstration of features
- **Test properties files** - Sample custom properties for testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=MessageSourceServiceTest

# Run demo
mvn exec:java -Dexec.mainClass="io.github.r4tylmz.betterpoi.DualPropertiesDemo" -Dexec.classpathScope=test
```

## Technical Implementation

### Resource Bundle Loading
```java
private void initializeBundles(String userBundleName) {
    // First, try to load user-defined bundle if specified
    if (userBundleName != null && !userBundleName.trim().isEmpty()) {
        try {
            ResourceBundle userBundle = ResourceBundle.getBundle(userBundleName, locale, new Utf8Control());
            if (userBundle != null) {
                bundles.add(userBundle);
            }
        } catch (Exception e) {
            // User bundle not found or invalid, will fallback to library bundle
        }
    }

    // Always add library bundle as fallback
    try {
        ResourceBundle libraryBundle = ResourceBundle.getBundle(LIBRARY_BUNDLE_NAME, locale, new Utf8Control());
        if (libraryBundle != null) {
            bundles.add(libraryBundle);
        }
    } catch (Exception e) {
        // Library bundle not found, this is a critical error
        throw new RuntimeException("Failed to load library properties file: " + LIBRARY_BUNDLE_NAME, e);
    }
}
```

### Message Resolution
```java
private String getMessagePattern(String key) {
    for (ResourceBundle bundle : bundles) {
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }
    }
    
    // Key not found in any bundle
    throw new RuntimeException("Message key '" + key + "' not found in any resource bundle for locale: " + locale);
}
```

## Conclusion

The dual properties implementation provides a powerful, flexible, and user-friendly way to handle internationalization in the Better POI library. Users can now:

1. **Use library properties only** (default behavior)
2. **Use custom properties with fallback** (recommended for customization)
3. **Support multiple locales** easily
4. **Maintain backward compatibility**

This implementation follows best practices for library design and provides enterprise-grade internationalization support while maintaining simplicity for basic use cases.
