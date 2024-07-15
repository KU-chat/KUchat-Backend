package kuchat.server.domain.member;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import kuchat.server.domain.BaseTime;
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
import java.util.*;

@Slf4j
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
@ToString
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

    @Size(min = 6, max = 6, message = "생일은 6자리 숫자 형태여야 합니다.")
    private String birthday;

    @Column(name = "setting_langugage")
    @Enumerated(EnumType.STRING)
    private SettingLanguage setLanguage;

    @Column(name = "learn_language1")
    @Enumerated(EnumType.STRING)
    private LearnLanguage firstLanguage;

    @Column(name = "learn_language2")
    @Enumerated(EnumType.STRING)
    private LearnLanguage secondLanguage;

    private String hometown;

//    private String fcmToken;

    private String profileImage;

    @OneToMany(mappedBy = "member")
    // member:roomMember = 1:다 -> member는 oneToMany     // 얘는 연관관계 종속됨 (주인은 RoomMember 클래스의 member필드)
    private Set<ChatroomMember> chatroomMembers = new HashSet<>();           // 채팅방-사용자 테이블과 member 테이블을 이어주는 칼럼

//    @OneToMany(mappedBy = "member")
//    private List<BlockMember> blockMembers = new ArrayList<>();         // 차단한 사람 목록

    @OneToMany(mappedBy = "friend")
    private Set<Friend> friends = new HashSet<>();        // 친구목록 : PENDING, FRIEND, BLOCKED, BLOCKED_BY 모든 관계의 친구를 포함한다.


    @Enumerated(EnumType.STRING)
    private Role role;

//    @OneToMany(mappedBy = "member")
//    private List<Notification> notifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String attributeName;       // 플랫폼에서 제공하는 id

    private String refreshToken;

    private String aboutMe;         // 한줄 자기소개

    @Builder
    public Member(String email, Platform platform, String attributeName, String profileImage) {
        this.email = email;
        this.platform = platform;
        this.attributeName = attributeName;
        this.profileImage = profileImage;
        role = Role.GUEST;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateInfo(@Valid SignupRequest request) {
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

        this.role = Role.STUDENT;           // 추가정보 받은 후 처리
    }

    public void addChatroom(ChatroomMember chatroomMember) {
        chatroomMembers.add(chatroomMember);
    }

    public void deleteChatroom(ChatroomMember chatroomMember) {
        chatroomMembers.remove(chatroomMember);
    }

    public void setAboutMe(String aboutMe){
        this.aboutMe = aboutMe;
    }

    public String generatePlusId(int length){
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return stringBuilder.toString();
    }

    public int getAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate birthDate = LocalDate.parse(birthday, formatter);         // 입력받은 생일 문자열을 LocalDate로 변환합니다
        LocalDate currentDate = LocalDate.now();                    // 현재 날짜
        Period age = Period.between(birthDate, currentDate);        // 생일과 현재 날짜를 비교하여 나이를 계산
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

    public void addFriend(Friend friend) {
        Optional<Friend> optionalFriend = friends.stream()
                .filter(foundFriend -> foundFriend.equals(friend))
                .findFirst();
        if (optionalFriend.isPresent()) {
            Friend foundFriend = optionalFriend.get();
            friends.remove(foundFriend);         // 기존 객체 제거
            foundFriend.setFriendType(friend.getFriendType());  // friendType 수정
            friends.add(foundFriend);            // 수정된 객체 추가
        } else {
            friends.add(friend);
        }
    }
}
