package io.github.thefrsh.stratus.troubleshooting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiError
{
    private final String message;
    private final Integer status;
}
