

package org.apache.commons.cli;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;


public class DefaultParser implements CommandLineParser {

    
    public static final class Builder {

        
        private boolean allowPartialMatching = true;

        
        private Boolean stripLeadingAndTrailingQuotes;

        
        private Builder() {
        }

        
        public DefaultParser build() {
            return new DefaultParser(allowPartialMatching, stripLeadingAndTrailingQuotes);
        }

        
        public Builder setAllowPartialMatching(final boolean allowPartialMatching) {
            this.allowPartialMatching = allowPartialMatching;
            return this;
        }

        
        public Builder setStripLeadingAndTrailingQuotes(final Boolean stripLeadingAndTrailingQuotes) {
            this.stripLeadingAndTrailingQuotes = stripLeadingAndTrailingQuotes;
            return this;
        }
    }

    
    public static Builder builder() {
        return new Builder();
    }

    
    protected CommandLine cmd;

    
    protected Options options;

    
    protected boolean stopAtNonOption;

    
    protected String currentToken;

    
    protected Option currentOption;

    
    protected boolean skipParsing;

    
    protected List expectedOpts;

    
    private final boolean allowPartialMatching;

    
    private final Boolean stripLeadingAndTrailingQuotes;

    
    public DefaultParser() {
        this.allowPartialMatching = true;
        this.stripLeadingAndTrailingQuotes = null;
    }

    
    public DefaultParser(final boolean allowPartialMatching) {
        this.allowPartialMatching = allowPartialMatching;
        this.stripLeadingAndTrailingQuotes = null;
    }

    
    private DefaultParser(final boolean allowPartialMatching,
            final Boolean stripLeadingAndTrailingQuotes) {
        this.allowPartialMatching = allowPartialMatching;
        this.stripLeadingAndTrailingQuotes = stripLeadingAndTrailingQuotes;
    }

    
    private void checkRequiredArgs() throws ParseException {
        if (currentOption != null && currentOption.requiresArg()) {
            if (isJavaProperty(currentOption.getKey()) && currentOption.getValuesList().size() == 1) {
                return;
            }
            throw new MissingArgumentException(currentOption);
        }
    }

    
    protected void checkRequiredOptions() throws MissingOptionException {
        // if there are required options that have not been processed
        if (!expectedOpts.isEmpty()) {
            throw new MissingOptionException(expectedOpts);
        }
    }

    
    private String getLongPrefix(final String token) {
        final String t = Util.stripLeadingHyphens(token);
        int i;
        String opt = null;
        for (i = t.length() - 2; i > 1; i--) {
            final String prefix = t.substring(0, i);
            if (options.hasLongOption(prefix)) {
                opt = prefix;
                break;
            }
        }
        return opt;
    }

    
    private List<String> getMatchingLongOptions(final String token) {
        if (allowPartialMatching) {
            return options.getMatchingOptions(token);
        }
        final List<String> matches = new ArrayList<>(1);
        if (options.hasLongOption(token)) {
            final Option option = options.getOption(token);
            matches.add(option.getLongOpt());
        }
        return matches;
    }

    
    protected void handleConcatenatedOptions(final String token) throws ParseException {
        for (int i = 1; i < token.length(); i++) {
            final String ch = String.valueOf(token.charAt(i));
            if (!options.hasOption(ch)) {
                handleUnknownToken(stopAtNonOption && i > 1 ? token.substring(i) : token);
                break;
            }
            handleOption(options.getOption(ch));

            if (currentOption != null && token.length() != i + 1) {
                // add the trail as an argument of the option
                currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(token.substring(i + 1)));
                break;
            }
        }
    }

    
    private void handleLongOption(final String token) throws ParseException {
        if (token.indexOf('=') == -1) {
            handleLongOptionWithoutEqual(token);
        } else {
            handleLongOptionWithEqual(token);
        }
    }

    
    private void handleLongOptionWithEqual(final String token) throws ParseException {
        final int pos = token.indexOf('=');
        final String value = token.substring(pos + 1);
        final String opt = token.substring(0, pos);
        final List<String> matchingOpts = getMatchingLongOptions(opt);
        if (matchingOpts.isEmpty()) {
            handleUnknownToken(currentToken);
        } else if (matchingOpts.size() > 1 && !options.hasLongOption(opt)) {
            throw new AmbiguousOptionException(opt, matchingOpts);
        } else {
            final String key = options.hasLongOption(opt) ? opt : matchingOpts.get(0);
            final Option option = options.getOption(key);
            if (option.acceptsArg()) {
                handleOption(option);
                currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(value));
                currentOption = null;
            } else {
                handleUnknownToken(currentToken);
            }
        }
    }

    
    private void handleLongOptionWithoutEqual(final String token) throws ParseException {
        final List<String> matchingOpts = getMatchingLongOptions(token);
        if (matchingOpts.isEmpty()) {
            handleUnknownToken(currentToken);
        } else if (matchingOpts.size() > 1 && !options.hasLongOption(token)) {
            throw new AmbiguousOptionException(token, matchingOpts);
        } else {
            final String key = options.hasLongOption(token) ? token : matchingOpts.get(0);
            handleOption(options.getOption(key));
        }
    }

    private void handleOption(final Option option) throws ParseException {
        // check the previous option before handling the next one
        checkRequiredArgs();
        final Option copy = (Option) option.clone();
        updateRequiredOptions(copy);
        cmd.addOption(copy);
        currentOption = copy.hasArg() ? copy : null;
    }

    
    private void handleProperties(final Properties properties) throws ParseException {
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
                // get the value from the properties
                final String value = properties.getProperty(option);

                if (opt.hasArg()) {
                    if (opt.getValues() == null || opt.getValues().length == 0) {
                        opt.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(value));
                    }
                } else if (!("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value))) {
                    // if the value is not yes, true or 1 then don't add the option to the CommandLine
                    continue;
                }
                handleOption(opt);
                currentOption = null;
            }
        }
    }

    
    private void handleShortAndLongOption(final String token) throws ParseException {
        final String t = Util.stripLeadingHyphens(token);
        final int pos = t.indexOf('=');
        if (t.length() == 1) {
            // -S
            if (options.hasShortOption(t)) {
                handleOption(options.getOption(t));
            } else {
                handleUnknownToken(token);
            }
        } else if (pos == -1) {
            // no equal sign found (-xxx)
            if (options.hasShortOption(t)) {
                handleOption(options.getOption(t));
            } else if (!getMatchingLongOptions(t).isEmpty()) {
                // -L or -l
                handleLongOptionWithoutEqual(token);
            } else {
                // look for a long prefix (-Xmx512m)
                final String opt = getLongPrefix(t);

                if (opt != null && options.getOption(opt).acceptsArg()) {
                    handleOption(options.getOption(opt));
                    currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(t.substring(opt.length())));
                    currentOption = null;
                } else if (isJavaProperty(t)) {
                    // -SV1 (-Dflag)
                    handleOption(options.getOption(t.substring(0, 1)));
                    currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOff(t.substring(1)));
                    currentOption = null;
                } else {
                    // -S1S2S3 or -S1S2V
                    handleConcatenatedOptions(token);
                }
            }
        } else {
            // equal sign found (-xxx=yyy)
            final String opt = t.substring(0, pos);
            final String value = t.substring(pos + 1);

            if (opt.length() == 1) {
                // -S=V
                final Option option = options.getOption(opt);
                if (option != null && option.acceptsArg()) {
                    handleOption(option);
                    currentOption.addValueForProcessing(value);
                    currentOption = null;
                } else {
                    handleUnknownToken(token);
                }
            } else if (isJavaProperty(opt)) {
                // -SV1=V2 (-Dkey=value)
                handleOption(options.getOption(opt.substring(0, 1)));
                currentOption.addValueForProcessing(opt.substring(1));
                currentOption.addValueForProcessing(value);
                currentOption = null;
            } else {
                // -L=V or -l=V
                handleLongOptionWithEqual(token);
            }
        }
    }

    
    private void handleToken(final String token) throws ParseException {
        currentToken = token;
        if (skipParsing) {
            cmd.addArg(token);
        } else if ("--".equals(token)) {
            skipParsing = true;
        } else if (currentOption != null && currentOption.acceptsArg() && isArgument(token)) {
            currentOption.addValueForProcessing(stripLeadingAndTrailingQuotesDefaultOn(token));
        } else if (token.startsWith("--")) {
            handleLongOption(token);
        } else if (token.startsWith("-") && !"-".equals(token)) {
            handleShortAndLongOption(token);
        } else {
            handleUnknownToken(token);
        }
        if (currentOption != null && !currentOption.acceptsArg()) {
            currentOption = null;
        }
    }

    
    private void handleUnknownToken(final String token) throws ParseException {
        if (token.startsWith("-") && token.length() > 1 && !stopAtNonOption) {
            throw new UnrecognizedOptionException("Unrecognized option: " + token, token);
        }
        cmd.addArg(token);
        if (stopAtNonOption) {
            skipParsing = true;
        }
    }

    
    private boolean isArgument(final String token) {
        return !isOption(token) || isNegativeNumber(token);
    }

    
    private boolean isJavaProperty(final String token) {
        final String opt = token.isEmpty() ? null : token.substring(0, 1);
        final Option option = options.getOption(opt);
        return option != null && (option.getArgs() >= 2 || option.getArgs() == Option.UNLIMITED_VALUES);
    }

    
    private boolean isLongOption(final String token) {
        if (token == null || !token.startsWith("-") || token.length() == 1) {
            return false;
        }
        final int pos = token.indexOf("=");
        final String t = pos == -1 ? token : token.substring(0, pos);
        if (!getMatchingLongOptions(t).isEmpty()) {
            // long or partial long options (--L, -L, --L=V, -L=V, --l, --l=V)
            return true;
        }
        if (getLongPrefix(token) != null && !token.startsWith("--")) {
            // -LV
            return true;
        }
        return false;
    }

    
    private boolean isNegativeNumber(final String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    
    private boolean isOption(final String token) {
        return isLongOption(token) || isShortOption(token);
    }

    
    private boolean isShortOption(final String token) {
        // short options (-S, -SV, -S=V, -SV1=V2, -S1S2)
        if (token == null || !token.startsWith("-") || token.length() == 1) {
            return false;
        }
        // remove leading "-" and "=value"
        final int pos = token.indexOf("=");
        final String optName = pos == -1 ? token.substring(1) : token.substring(1, pos);
        if (options.hasShortOption(optName)) {
            return true;
        }
        // check for several concatenated short options
        return !optName.isEmpty() && options.hasShortOption(String.valueOf(optName.charAt(0)));
    }

    @Override
    public CommandLine parse(final Options options, final String[] arguments) throws ParseException {
        return parse(options, arguments, null);
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
        this.options = options;
        this.stopAtNonOption = stopAtNonOption;
        skipParsing = false;
        currentOption = null;
        expectedOpts = new ArrayList<>(options.getRequiredOptions());

        // clear the data from the groups
        for (final OptionGroup group : options.getOptionGroups()) {
            group.setSelected(null);
        }

        cmd = new CommandLine();

        if (arguments != null) {
            for (final String argument : arguments) {
                handleToken(argument);
            }
        }

        // check the arguments of the last option
        checkRequiredArgs();

        // add the default options
        handleProperties(properties);

        checkRequiredOptions();

        return cmd;
    }

    
    private String stripLeadingAndTrailingQuotesDefaultOff(final String token) {
        if (stripLeadingAndTrailingQuotes != null && stripLeadingAndTrailingQuotes) {
            return Util.stripLeadingAndTrailingQuotes(token);
        }
        return token;
    }

    
    private String stripLeadingAndTrailingQuotesDefaultOn(final String token) {
        if (stripLeadingAndTrailingQuotes == null || stripLeadingAndTrailingQuotes) {
            return Util.stripLeadingAndTrailingQuotes(token);
        }
        return token;
    }

    
    private void updateRequiredOptions(final Option option) throws AlreadySelectedException {
        if (option.isRequired()) {
            expectedOpts.remove(option.getKey());
        }

        // if the option is in an OptionGroup make that option the selected option of the group
        if (options.getOptionGroup(option) != null) {
            final OptionGroup group = options.getOptionGroup(option);

            if (group.isRequired()) {
                expectedOpts.remove(group);
            }

            group.setSelected(option);
        }
    }
}
