package com.github.cfogrady.dim.modifier.data.card;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MetaData {
    private final int id;
    private final int revision;
    private final int year;
    private final int month;
    private final int day;
    private final int originalChecksum;
}
