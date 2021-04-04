package io.github.thefrsh.stratus.aspect;

import io.github.thefrsh.stratus.model.Conversation;
import io.github.thefrsh.stratus.service.UserService;
import io.github.thefrsh.stratus.troubleshooting.exception.NotAssociatedConversationException;
import io.github.thefrsh.stratus.troubleshooting.exception.UserMessageException;
import io.vavr.collection.List;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ChatMessageServiceAspect {

    private final UserService userService;

    @Autowired
    public ChatMessageServiceAspect(UserService userService) {

        this.userService = userService;
    }

    @Before(value = "execution(* io.github.thefrsh.stratus.service.implementation.ChatMessageServiceImpl." +
            "*(Long, Long)) && args(conversationId, messageId)", argNames = "conversationId, messageId")
    public void validateUpdateMessageState(Long conversationId, Long messageId) {

        var id = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();

        var user = userService.findUser(id);

        List.ofAll(user.getConversations())
                .map(Conversation::getId)
                .find(convId -> convId.equals(conversationId))
                .onEmpty(() -> {
                    throw new NotAssociatedConversationException("User with id " + id + " do not belong to " +
                            "conversation with id " + conversationId);
                });

        List.ofAll(user.getMessages())
                .find(chatMessage -> chatMessage.getId().equals(messageId))
                .peek(chatMessage -> {
                    throw new UserMessageException("You cannot change message status if you are its author");
                });
    }
}
