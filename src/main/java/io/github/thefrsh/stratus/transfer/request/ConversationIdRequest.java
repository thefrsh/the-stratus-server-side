package io.github.thefrsh.stratus.transfer.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationIdRequest {

    @NotNull(message = "Conversation id property is missing")
    private Long id;
}
