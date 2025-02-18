Branch coverage of the method:
```java
    boolean requiresArg() {
        if (optionalArg) { // [[true condition not covered]]
            return false;
        }
        if (argCount == UNLIMITED_VALUES) {
            return values.isEmpty();
        }
        return acceptsArg();
    }
```
The row coverage of the method:
```java
    boolean requiresArg() {
        if (optionalArg) {
            return false; // [[line not covered]]
        }
        if (argCount == UNLIMITED_VALUES) {
            return values.isEmpty();
        }
        return acceptsArg();
    }
```
Output pruned code in a Java code cell.
```java
boolean requiresArg() {
        if (argCount == UNLIMITED_VALUES) {
            return values.isEmpty();
        }
        return acceptsArg();
    }
```
Branch coverage of the method:
```java
    public void setType(final Object type) {
        setType((Class<?>) type);
    }
```
The row coverage of the method:
```java
    public void setType(final Object type) {
        setType((Class<?>) type); // [[line not covered]]
    }
```
Output pruned code in a Java code cell.
```java
public void setType(final Object type) {
        setType((Class<?>) type);
    }
```
