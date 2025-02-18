Branch coverage of the method:
```java
    private void handleUnknownToken(final String token) throws ParseException {
        if (token.startsWith("-") && token.length() > 1 && !stopAtNonOption) { // [[false condition not covered]]
            throw new UnrecognizedOptionException("Unrecognized option: " + token, token);
        }
        cmd.addArg(token);
        if (stopAtNonOption) { // [[unreached branch]]
            skipParsing = true;
        }
```
The row coverage of the method:
```java
    private void handleUnknownToken(final String token) throws ParseException {
        if (token.startsWith("-") && token.length() > 1 && !stopAtNonOption) {
            throw new UnrecognizedOptionException("Unrecognized option: " + token, token);
        }
        cmd.addArg(token); // [[line not covered]]
        if (stopAtNonOption) { // [[line not covered]]
            skipParsing = true; // [[line not covered]]
        }
```
Output pruned code in a Java code cell.
```java
private void handleUnknownToken(final String token) throws ParseException {
        if (token.startsWith("-") && token.length() > 1 && !stopAtNonOption) {
            throw new UnrecognizedOptionException("Unrecognized option: " + token, token);
        }
    }
```
