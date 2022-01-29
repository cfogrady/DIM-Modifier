package com.github.cfogrady.dim.modifier.data;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class Fusions {
    private final UUID type1FusionResult;
    private final UUID type2FusionResult;
    private final UUID type3FusionResult;
    private final UUID type4FusionResult;
}
