package com.github.cfogrady.dim.modifier.data;

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
}
