package io.github.thefrsh.stratus.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String content;
    private LocalDateTime sendTime;

    @Enumerated(value = EnumType.STRING)
    private MessageState messageState;

    @ManyToOne
    private User sender;

    @ManyToOne
    private Conversation conversation;
}
