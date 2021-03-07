package io.github.thefrsh.stratus.model;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @EqualsAndHashCode.Include
    private String username;
    private String password;

    @NaturalId
    @EqualsAndHashCode.Include
    private String email;

    @ManyToMany
    @JoinTable(name = "table_friends",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<User> friends;

    @ManyToMany
    @JoinTable(name = "table_friends",
               joinColumns = @JoinColumn(name = "friend_id"),
               inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> friendsOf;

    @ManyToMany(mappedBy = "participants")
    private List<Conversation> conversations;

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private List<ChatMessage> messages;

    @OneToMany(mappedBy = "receiver")
    private List<FriendInvitation> receivedInvitations;

    @OneToMany(mappedBy = "sender")
    private List<FriendInvitation> sentInvitations;
}
