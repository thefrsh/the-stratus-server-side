package io.github.thefrsh.stratus.transfer.websocket;

import lombok.Getter;

@Getter
public class InformationTransfer
{
    private final TransferType type;

    private final String content;

    public InformationTransfer(String content)
    {
        this.type = TransferType.INFORMATION;
        this.content = content;
    }
}
