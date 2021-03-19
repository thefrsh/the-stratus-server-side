package io.github.thefrsh.stratus.transfer.response;

import io.github.thefrsh.stratus.model.MessageState;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String content;
    private LocalDateTime sendTime;
    private MessageState messageState;
}
