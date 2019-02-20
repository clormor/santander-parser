package com.lormor.banking.expense;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.immutables.value.Value;
import org.joda.time.DateTime;

@SuppressFBWarnings
@Value.Immutable
public abstract class Expense {

    @Value.Default
    public DateTime getDate() {
        return DateTime.now();
    }

    @Value.Default
    public String getDescription() {
        return "";
    }

    public abstract Double getAmount();
}
