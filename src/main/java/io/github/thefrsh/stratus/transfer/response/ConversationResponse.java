package io.github.thefrsh.stratus.transfer.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConversationResponse
{
    private Long id;
    private List<String> participants;
}
