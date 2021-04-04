package io.github.thefrsh.stratus.transfer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MessageRequest {

    @NotNull(message = "Sender id property is missing")
    private Long senderId;

    @NotNull(message = "Message content is missing", groups = First.class)
    @NotEmpty(message = "Message content cannot be empty")
    @NotBlank(message = "Message cannot contain only white characters")
    @Size(min = 1, max = 200, message = "Message length must be between {min} and {max} characters long")
    private String content;

    public interface First {

    }

    @GroupSequence(value = {First.class, Default.class})
    public interface ValidationOrder {

    }
}
