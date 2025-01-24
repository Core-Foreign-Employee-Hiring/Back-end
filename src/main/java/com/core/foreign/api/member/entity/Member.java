package com.core.foreign.api.member.entity;

import com.core.foreign.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "member_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;             // 엔티티 식별번호

    private String userId;       // 아이디
    private String password;     // 비밀번호
    private String name;         // 이름[대표자명]
    private String email;        // 이메일
    private String phoneNumber;  // 전화번호
    private String refreshToken; // 리프레시토큰

    @Enumerated(EnumType.STRING)
    private Role role;           // 회원 Role (EMPLOYEE, EMPLOYER 등)

    @Embedded
    private Address address;     // 주소[회사/점포 주소]

    protected Member(String userId,
                     String password,
                     String name,
                     String email,
                     String phoneNumber,
                     Address address,
                     Role role) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }

    // 리프레시토큰 업데이트
    public Member updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

}
