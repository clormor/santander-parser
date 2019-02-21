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

    @Override
    public boolean equals(Object o) {
        // treat all expenses as unique - it is possible albeit unusual to legitimately spend the same amount
        // at the same place twice on the same day (in general dates will be rounded to the nearest day).
        return false;
    }
}
