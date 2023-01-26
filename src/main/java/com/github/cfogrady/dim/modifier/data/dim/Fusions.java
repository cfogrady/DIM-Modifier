package com.github.cfogrady.dim.modifier.data.dim;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class Fusions {
    private UUID type1FusionResult;
    private UUID type2FusionResult;
    private UUID type3FusionResult;
    private UUID type4FusionResult;

    public boolean isEmpty() {
        return type1FusionResult == null &&
                type2FusionResult == null &&
                type3FusionResult == null &&
                type4FusionResult == null;
    }
}
