package com.github.cfogrady.dim.modifier.controls;

import lombok.Getter;

@Getter
public class LabelValuePair<VALUE, LABEL> {
    LABEL label;
    VALUE value;
}
