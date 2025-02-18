Branch coverage of the method:
```java
    public CommandLine parse(final Options options, final String[] arguments, final Properties properties, final boolean stopAtNonOption)
            throws ParseException {
        // clear out the data in options in case it's been used before (CLI-71)
        for (final Option opt : options.helpOptions()) {
            opt.clearValues();
        }

        // clear the data from the groups
        for (final OptionGroup group : options.getOptionGroups()) {
            group.setSelected(null);
        }

        // initialize members
        setOptions(options);

        cmd = new CommandLine();

        boolean eatTheRest = false;

        final List<String> tokenList = Arrays.asList(flatten(getOptions(), arguments == null ? new String[0] : arguments, stopAtNonOption)); // [[true condition not covered]]

        final ListIterator<String> iterator = tokenList.listIterator();

        // process each flattened token
        while (iterator.hasNext()) { // [[true condition not covered]]
            final String t = iterator.next();

            // the value is the double-dash
            if ("--".equals(t)) { // [[unreached branch]]
                eatTheRest = true;
            }

            // the value is a single dash
            else if ("-".equals(t)) { // [[unreached branch]]
                if (stopAtNonOption) { // [[unreached branch]]
                    eatTheRest = true;
                } else {
                    cmd.addArg(t);
                }
            }

            // the value is an option
            else if (t.startsWith("-")) { // [[unreached branch]]
                if (stopAtNonOption && !getOptions().hasOption(t)) { // [[unreached branch]]
                    eatTheRest = true;
                    cmd.addArg(t);
                } else {
                    processOption(t, iterator);
                }
            }

            // the value is an argument
            else {
                cmd.addArg(t);

                if (stopAtNonOption) { // [[unreached branch]]
                    eatTheRest = true;
                }
            }

            // eat the remaining tokens
            if (eatTheRest) { // [[unreached branch]]
                while (iterator.hasNext()) { // [[unreached branch]]
                    final String str = iterator.next();

                    // ensure only one double-dash is added
                    if (!"--".equals(str)) { // [[unreached branch]]
                        cmd.addArg(str);
                    }
                }
            }
        }

        processProperties(properties);
        checkRequiredOptions();

        return cmd;
    }
```
The row coverage of the method:
```java
    public CommandLine parse(final Options options, final String[] arguments, final Properties properties, final boolean stopAtNonOption)
            throws ParseException {
        // clear out the data in options in case it's been used before (CLI-71)
        for (final Option opt : options.helpOptions()) {
            opt.clearValues();
        }

        // clear the data from the groups
        for (final OptionGroup group : options.getOptionGroups()) {
            group.setSelected(null); // [[line not covered]]
        }

        // initialize members
        setOptions(options);

        cmd = new CommandLine();

        boolean eatTheRest = false;

        final List<String> tokenList = Arrays.asList(flatten(getOptions(), arguments == null ? new String[0] : arguments, stopAtNonOption));

        final ListIterator<String> iterator = tokenList.listIterator();

        // process each flattened token
        while (iterator.hasNext()) {
            final String t = iterator.next(); // [[line not covered]]

            // the value is the double-dash
            if ("--".equals(t)) { // [[line not covered]]
                eatTheRest = true; // [[line not covered]]
            }

            // the value is a single dash
            else if ("-".equals(t)) { // [[line not covered]]
                if (stopAtNonOption) { // [[line not covered]]
                    eatTheRest = true; // [[line not covered]]
                } else {
                    cmd.addArg(t); // [[line not covered]]
                }
            }

            // the value is an option
            else if (t.startsWith("-")) { // [[line not covered]]
                if (stopAtNonOption && !getOptions().hasOption(t)) { // [[line not covered]]
                    eatTheRest = true; // [[line not covered]]
                    cmd.addArg(t); // [[line not covered]]
                } else {
                    processOption(t, iterator); // [[line not covered]]
                }
            }

            // the value is an argument
            else {
                cmd.addArg(t); // [[line not covered]]

                if (stopAtNonOption) { // [[line not covered]]
                    eatTheRest = true; // [[line not covered]]
                }
            }

            // eat the remaining tokens
            if (eatTheRest) { // [[line not covered]]
                while (iterator.hasNext()) { // [[line not covered]]
                    final String str = iterator.next(); // [[line not covered]]

                    // ensure only one double-dash is added
                    if (!"--".equals(str)) { // [[line not covered]]
                        cmd.addArg(str); // [[line not covered]]
                    }
                }
            }
        }

        processProperties(properties);
        checkRequiredOptions();

        return cmd;
    }
```
Output pruned code in a Java code cell.
```java
public CommandLine parse(final Options options, final String[] arguments, final Properties properties, final boolean stopAtNonOption)
            throws ParseException {
        // clear out the data in options in case it's been used before (CLI-71)
        for (final Option opt : options.helpOptions()) {
            opt.clearValues();
        }

        // initialize members
        setOptions(options);

        cmd = new CommandLine();

        boolean eatTheRest = false;

        final List<String> tokenList = Arrays.asList(flatten(getOptions(), arguments == null ? new String[0] : arguments, stopAtNonOption));

        final ListIterator<String> iterator = tokenList.listIterator();

        // process each flattened token
        while (iterator.hasNext()) {
            final String t = iterator.next();

            // the value is a single dash
            else if ("-".equals(t)) {
                if (!stopAtNonOption) {
                    cmd.addArg(t);
                }
            }

            // the value is an option
            else if (t.startsWith("-")) {
                if (!stopAtNonOption || getOptions().hasOption(t)) {
                    processOption(t, iterator);
                }
            }

            // the value is an argument
            else {
                cmd.addArg(t);
            }
        }

        processProperties(properties);
        checkRequiredOptions();

        return cmd;
    }
```
