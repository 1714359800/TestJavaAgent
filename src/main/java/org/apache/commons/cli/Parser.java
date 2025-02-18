

package org.apache.commons.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;


@Deprecated
public abstract class Parser implements CommandLineParser {
    
    protected CommandLine cmd;

    
    private Options options;

    
    private List requiredOptions;

    
    protected void checkRequiredOptions() throws MissingOptionException {
        // if there are required options that have not been processed
        if (!getRequiredOptions().isEmpty()) {
            throw new MissingOptionException(getRequiredOptions());
        }
    }

    
    protected abstract String[] flatten(Options opts, String[] arguments, boolean stopAtNonOption) throws ParseException;

    
    protected Options getOptions() {
        return options;
    }

    
    protected List getRequiredOptions() {
        return requiredOptions;
    }

    
    @Override
    public CommandLine parse(final Options options, final String[] arguments) throws ParseException {
        return parse(options, arguments, null, false);
    }

    
    @Override
    public CommandLine parse(final Options options, final String[] arguments, final boolean stopAtNonOption) throws ParseException {
        return parse(options, arguments, null, stopAtNonOption);
    }

    
    public CommandLine parse(final Options options, final String[] arguments, final Properties properties) throws ParseException {
        return parse(options, arguments, properties, false);
    }

    
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

        final List<String> tokenList = Arrays.asList(flatten(getOptions(), arguments == null ? new String[0] : arguments, stopAtNonOption));

        final ListIterator<String> iterator = tokenList.listIterator();

        // process each flattened token
        while (iterator.hasNext()) {
            final String t = iterator.next();

            // the value is the double-dash
            if ("--".equals(t)) {
                eatTheRest = true;
            }

            // the value is a single dash
            else if ("-".equals(t)) {
                if (stopAtNonOption) {
                    eatTheRest = true;
                } else {
                    cmd.addArg(t);
                }
            }

            // the value is an option
            else if (t.startsWith("-")) {
                if (stopAtNonOption && !getOptions().hasOption(t)) {
                    eatTheRest = true;
                    cmd.addArg(t);
                } else {
                    processOption(t, iterator);
                }
            }

            // the value is an argument
            else {
                cmd.addArg(t);

                if (stopAtNonOption) {
                    eatTheRest = true;
                }
            }

            // eat the remaining tokens
            if (eatTheRest) {
                while (iterator.hasNext()) {
                    final String str = iterator.next();

                    // ensure only one double-dash is added
                    if (!"--".equals(str)) {
                        cmd.addArg(str);
                    }
                }
            }
        }

        processProperties(properties);
        checkRequiredOptions();

        return cmd;
    }

    
    public void processArgs(final Option opt, final ListIterator<String> iter) throws ParseException {
        // loop until an option is found
        while (iter.hasNext()) {
            final String str = iter.next();

            // found an Option, not an argument
            if (getOptions().hasOption(str) && str.startsWith("-")) {
                iter.previous();
                break;
            }

            // found a value
            try {
                opt.addValueForProcessing(Util.stripLeadingAndTrailingQuotes(str));
            } catch (final RuntimeException exp) {
                iter.previous();
                break;
            }
        }

        if (opt.getValues() == null && !opt.hasOptionalArg()) {
            throw new MissingArgumentException(opt);
        }
    }

    
    protected void processOption(final String arg, final ListIterator<String> iter) throws ParseException {
        final boolean hasOption = getOptions().hasOption(arg);

        // if there is no option throw an UnrecognizedOptionException
        if (!hasOption) {
            throw new UnrecognizedOptionException("Unrecognized option: " + arg, arg);
        }

        // get the option represented by arg
        final Option opt = (Option) getOptions().getOption(arg).clone();

        // update the required options and groups
        updateRequiredOptions(opt);

        // if the option takes an argument value
        if (opt.hasArg()) {
            processArgs(opt, iter);
        }

        // set the option on the command line
        cmd.addOption(opt);
    }

    
    protected void processProperties(final Properties properties) throws ParseException {
        if (properties == null) {
            return;
        }

        for (final Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
            final String option = e.nextElement().toString();

            final Option opt = options.getOption(option);
            if (opt == null) {
                throw new UnrecognizedOptionException("Default option wasn't defined", option);
            }

            // if the option is part of a group, check if another option of the group has been selected
            final OptionGroup group = options.getOptionGroup(opt);
            final boolean selected = group != null && group.getSelected() != null;

            if (!cmd.hasOption(option) && !selected) {
                // get the value from the properties instance
                final String value = properties.getProperty(option);

                if (opt.hasArg()) {
                    if (opt.getValues() == null || opt.getValues().length == 0) {
                        try {
                            opt.addValueForProcessing(value);
                        } catch (final RuntimeException exp) { // NOPMD
                            // if we cannot add the value don't worry about it
                        }
                    }
                } else if (!("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value))) {
                    // if the value is not yes, true or 1 then don't add the
                    // option to the CommandLine
                    continue;
                }

                cmd.addOption(opt);
                updateRequiredOptions(opt);
            }
        }
    }

    
    protected void setOptions(final Options options) {
        this.options = options;
        this.requiredOptions = new ArrayList<>(options.getRequiredOptions());
    }

    
    private void updateRequiredOptions(final Option opt) throws ParseException {
        // if the option is a required option remove the option from
        // the requiredOptions list
        if (opt.isRequired()) {
            getRequiredOptions().remove(opt.getKey());
        }

        // if the option is in an OptionGroup make that option the selected
        // option of the group
        if (getOptions().getOptionGroup(opt) != null) {
            final OptionGroup group = getOptions().getOptionGroup(opt);

            if (group.isRequired()) {
                getRequiredOptions().remove(group);
            }

            group.setSelected(opt);
        }
    }
}
