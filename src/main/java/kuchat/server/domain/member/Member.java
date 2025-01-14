package kuchat.server.domain.member;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.block.Block;
import kuchat.server.domain.chatroom.ChatroomMember;
import kuchat.server.domain.enums.*;
import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@ToString
@Slf4j
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Email(message = "이메일 형식이 아닙니다.")
    @NotEmpty
    @Valid
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "department")
    private String department;

    @Size(min = 9, max = 9, message = "학번은 9자리 숫자 형태여야 합니다.")
    @Column(name = "student_id")
    private String studentId;

    @Setter
    @Column(name = "plus_id")
    private String plusId;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthday;

    @Column(name = "setting_language")
    @Enumerated(EnumType.STRING)
    private SettingLanguage setLanguage;

    @Column(name = "learn_language1")
    @Enumerated(EnumType.STRING)
    private LearnLanguage firstLanguage;

    @Column(name = "learn_language2")
    @Enumerated(EnumType.STRING)
    private LearnLanguage secondLanguage;

    private String hometown;

    private String profileImage;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    // member:roomMember = 1:다 -> member는 oneToMany     // 얘는 연관관계 종속됨 (주인은 RoomMember 클래스의 member필드)
    private Set<ChatroomMember> chatroomMembers = new HashSet<>();           // 채팅방-사용자 테이블과 member 테이블을 이어주는 칼럼

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)               // Member가 follower인 Friend 객체들의 집합
    private Set<Friend> friends = new HashSet<>();      // 내가 팔로우한 사용자들 (= 내 친구들)

    @OneToMany(mappedBy = "blocker", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // Member가 blocker인 Block 객체들의 집합
    private Set<Block> blocks = new HashSet<>();  // 내가 차단한 사람들의 목록 (sender가 나 자신인 Block 객체들의 모음)

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

//    @OneToMany(mappedBy = "member")
//    private List<Notification> notifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String providerId;       // 플랫폼에서 제공하는 id

    @Setter
    private String aboutMe;         // 한줄 자기소개

    @Builder
    public Member(String email, Platform platform, String providerId, String profileImage) {
        this.email = email;
        this.platform = platform;
        this.providerId = providerId;
        this.profileImage = profileImage;
        status = Status.PENDING;
        role = Role.GUEST;
    }

    public void updateInfo(SignupRequest request) {
        this.setLanguage = SettingLanguage.of(request.getSetLanguage());
        this.firstLanguage = LearnLanguage.of(request.getFirstLanguage());
        this.secondLanguage = LearnLanguage.of(request.getSecondLanguage());
        this.hometown = request.getHometown();
        this.name = request.getName();
        this.birthday = request.getBirthday();
        this.department = request.getDepartment();
        this.studentId = request.getStudentId();
        this.gender = Gender.of(request.getGender());
        this.plusId = generatePlusId(10);

        this.status = Status.ACTIVE;
        this.role = Role.STUDENT;           // 추가정보 받은 후 처리
    }

    public void addChatroom(ChatroomMember chatroomMember) {
        chatroomMembers.add(chatroomMember);
    }

    public void deleteChatroom(ChatroomMember chatroomMember) {
        chatroomMembers.remove(chatroomMember);
    }

    public String generatePlusId(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return stringBuilder.toString();
    }

    public int getAge() {
        LocalDate currentDate = LocalDate.now();                    // 현재 날짜
        Period age = Period.between(birthday, currentDate);        // 생일과 현재 날짜를 비교하여 나이를 계산
        return age.getYears();                                      // 현재 연도를 기준으로 나이를 반환
    }

    public void updateProfile(ProfileUpdateRequest request) {
        name = request.getName();
        department = request.getDepartment();
        firstLanguage = LearnLanguage.of(request.getFirstLanguage());
        secondLanguage = LearnLanguage.of(request.getSecondLanguage());
        profileImage = request.getProfileImage();
        aboutMe = request.getAboutMe();
    }

    public boolean addFriend(Friend friend) {
        if (containsFriend(friend.getFollowed())) {
            throw new KuchatException(BaseResponseStatus.ALREADY_FRIEND);
        }
        friends.add(friend);
        return true;
    }

    public boolean block(Member friend) {
        return blocks.contains(friend);
    }

    public void deleteFriend(Friend friend) {
        friends.remove(friend);
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public boolean containsFriend(Member friend) {
        return friends.stream()
                .anyMatch(f -> Objects.equals(f.getFollowed(), friend));
    }

    public void deleteBlock(Block block) {
        blocks.remove(block);
    }

    public void withdraw(){
        this.status = Status.WITHDRAWN;
    }
}
