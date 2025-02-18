Branch coverage of the method:
```java
    public void setSelected(final Option option) throws AlreadySelectedException {
        if (option == null) { // [[true condition not covered]]
            // reset the option previously selected
            selected = null;
            return;
        }

        // if no option has already been selected or the
        // same option is being reselected then set the
        // selected member variable
        if (selected != null && !selected.equals(option.getKey())) {
            throw new AlreadySelectedException(this, option);
        }
        selected = option.getKey();
    }
```
The row coverage of the method:
```java
    public void setSelected(final Option option) throws AlreadySelectedException {
        if (option == null) {
            // reset the option previously selected
            selected = null; // [[line not covered]]
            return; // [[line not covered]]
        }

        // if no option has already been selected or the
        // same option is being reselected then set the
        // selected member variable
        if (selected != null && !selected.equals(option.getKey())) {
            throw new AlreadySelectedException(this, option);
        }
        selected = option.getKey();
    }
```
Output pruned code in a Java code cell.
```java
public void setSelected(final Option option) throws AlreadySelectedException {
    if (option == null) {
        return;
    }

    if (selected != null && !selected.equals(option.getKey())) {
        throw new AlreadySelectedException(this, option);
    }
    selected = option.getKey();
}
```
