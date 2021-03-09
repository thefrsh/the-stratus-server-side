package io.github.thefrsh.stratus.transfer.websocket;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class InformationTransfer
{
    private final TransferType type;

    private final String content;

    @JsonCreator
    public InformationTransfer(String content)
    {
        this.type = TransferType.INFORMATION;
        this.content = content;
    }
}
