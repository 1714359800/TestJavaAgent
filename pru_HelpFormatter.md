Branch coverage of the method:
```java
    private void appendOptionGroup(final StringBuffer buff, final OptionGroup group) {
        if (!group.isRequired()) { // [[false condition not covered]]
            buff.append("[");
        }

        final List<Option> optList = new ArrayList<>(group.getOptions());
        if (getOptionComparator() != null) { // [[false condition not covered]]
            Collections.sort(optList, getOptionComparator());
        }
        // for each option in the OptionGroup
        for (final Iterator<Option> it = optList.iterator(); it.hasNext();) {
            // whether the option is required or not is handled at group level
            appendOption(buff, it.next(), true);

            if (it.hasNext()) {
                buff.append(" | ");
            }
        }

        if (!group.isRequired()) { // [[false condition not covered]]
            buff.append("]");
        }
```
The row coverage of the method:
```java
    private void appendOptionGroup(final StringBuffer buff, final OptionGroup group) {
        if (!group.isRequired()) {
            buff.append("[");
        }

        final List<Option> optList = new ArrayList<>(group.getOptions());
        if (getOptionComparator() != null) {
            Collections.sort(optList, getOptionComparator());
        }
        // for each option in the OptionGroup
        for (final Iterator<Option> it = optList.iterator(); it.hasNext();) {
            // whether the option is required or not is handled at group level
            appendOption(buff, it.next(), true);

            if (it.hasNext()) {
                buff.append(" | ");
            }
        }

        if (!group.isRequired()) {
            buff.append("]");
        }
```
Output pruned code in a Java code cell.
```java
private void appendOptionGroup(final StringBuffer buff, final OptionGroup group) {
        final List<Option> optList = new ArrayList<>(group.getOptions());
        if (getOptionComparator() != null) {
            Collections.sort(optList, getOptionComparator());
        }
        // for each option in the OptionGroup
        for (final Iterator<Option> it = optList.iterator(); it.hasNext();) {
            // whether the option is required or not is handled at group level
            appendOption(buff, it.next(), true);

            if (it.hasNext()) {
                buff.append(" | ");
            }
        }
    }
```
