package org.sebastiandev.productservice.dto;

import jdk.jshell.Snippet;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(String id, String name, String description, BigDecimal price) {
}
