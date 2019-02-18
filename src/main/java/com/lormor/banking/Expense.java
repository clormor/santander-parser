package com.lormor.banking;

import org.immutables.value.Value;
import org.joda.time.DateTime;

@Value.Immutable
public interface Expense {

    DateTime getDate();

    String getDescription();

    Double getAmount();
}
