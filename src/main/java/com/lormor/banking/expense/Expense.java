package com.lormor.banking.expense;

import org.immutables.value.Value;
import org.immutables.value.Value.Default;
import org.joda.time.DateTime;

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
