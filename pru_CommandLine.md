Branch coverage of the method:
```java
    public String getOptionValue(final Option option) {
        if (option == null) {
            return null;
        }
        final String[] values = getOptionValues(option);
        return values == null ? null : values[0]; // [[false condition not covered]]
    }
```
The row coverage of the method:
```java
    public String getOptionValue(final Option option) {
        if (option == null) {
            return null;
        }
        final String[] values = getOptionValues(option);
        return values == null ? null : values[0];
    }
```
Output pruned code in a Java code cell.
```java
public String getOptionValue(final Option option) {
    if (option == null) {
        return null;
    }
    final String[] values = getOptionValues(option);
    return values == null ? null : values[0];
}
```
