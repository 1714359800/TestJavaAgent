Branch coverage of the method:
```java
    public Options addOption(final Option opt) {
        final String key = opt.getKey();
        // add it to the long option list
        if (opt.hasLongOpt()) { // [[false condition not covered]]
            longOpts.put(opt.getLongOpt(), opt);
        }
        // if the option is required add it to the required list
        if (opt.isRequired()) { // [[true condition not covered]]
            if (requiredOpts.contains(key)) { // [[unreached branch]]
                requiredOpts.remove(requiredOpts.indexOf(key));
            }
            requiredOpts.add(key);
        }
        shortOpts.put(key, opt);
        return this;
    }
```
The row coverage of the method:
```java
    public Options addOption(final Option opt) {
        final String key = opt.getKey();
        // add it to the long option list
        if (opt.hasLongOpt()) {
            longOpts.put(opt.getLongOpt(), opt);
        }
        // if the option is required add it to the required list
        if (opt.isRequired()) {
            if (requiredOpts.contains(key)) { // [[line not covered]]
                requiredOpts.remove(requiredOpts.indexOf(key)); // [[line not covered]]
            }
            requiredOpts.add(key); // [[line not covered]]
        }
        shortOpts.put(key, opt);
        return this;
    }
```
Output pruned code in a Java code cell.
```java
public Options addOption(final Option opt) {
        final String key = opt.getKey();
        // add it to the long option list
        if (opt.hasLongOpt()) {
            longOpts.put(opt.getLongOpt(), opt);
        }
        shortOpts.put(key, opt);
        return this;
    }
```
