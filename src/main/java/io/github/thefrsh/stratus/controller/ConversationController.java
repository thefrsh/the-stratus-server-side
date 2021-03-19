package io.github.thefrsh.stratus.controller;

import io.github.thefrsh.stratus.service.ChatMessageService;
import io.github.thefrsh.stratus.service.ConversationService;
import io.github.thefrsh.stratus.transfer.request.MessageRequest;
import io.github.thefrsh.stratus.transfer.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final ChatMessageService chatMessageService;

    @Autowired
    public ConversationController(ConversationService conversationService, ChatMessageService chatMessageService) {

        this.conversationService = conversationService;
        this.chatMessageService = chatMessageService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{id}/messages", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MessageResponse sendMessage(@Validated(MessageRequest.ValidationOrder.class)
                                       @RequestBody MessageRequest messageRequest, @PathVariable Long id) {

        return conversationService.sendMessage(id, messageRequest.getSenderId(), messageRequest.getContent());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "/{id}/messages/{messageId}")
    public void updateMessageStatus(@PathVariable Long id, @PathVariable Long messageId) {

        chatMessageService.updateStatus(id, messageId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MessageResponse> getMessages(@PathVariable Long id,
                                             @Min(value = 1, message = "Page size must be greater than {value}")
                                             @RequestParam Integer size,
                                             @Min(value = 0, message = "Page number must be greater than {value}")
                                             @RequestParam Integer page) {

        return chatMessageService.getConversationMessages(id, PageRequest.of(page, size));
    }
}
