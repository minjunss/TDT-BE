package TDT.backend.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @OneToOne
    private Image image;
    @OneToMany(mappedBy = "member")
    private List<TeamMember> teamMembers = new ArrayList<>();
    private String email;
    private String name;
    private String nickname;
    private String introduction;
    private Tag tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public Member(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
    public Member update(String name, String email) {
        this.name = name;
        this.email = email;

        return this;
    }
}
