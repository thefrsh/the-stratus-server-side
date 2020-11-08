package io.github.thefrsh.stratus.troubleshooting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ApiError
{
    private final String message;
    private final Integer status;
}
