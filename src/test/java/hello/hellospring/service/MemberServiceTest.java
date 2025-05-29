package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberServiceTest {

    // memberService 에서도 MemoryMemberRepository 를 사용한다.
    // `store` 가 static 영역에 있어서 인스턴스 간에 공유를 할 수 있지만, 서로 다른 인스턴스르 참조하고 있다.
    // 그래서, MemberService 가 생성될때 MemoryMemberRepository 를 주입하도록 하는 방식으로 변경한다.

    MemoryMemberRepository repository;
    MemberService memberService;

    @BeforeEach
    void beforeEach() {
        // 테스트 실행할때마다 repository, service 를 새로 생성한다.
        repository = new MemoryMemberRepository();
        memberService = new MemberService(repository);
    }

    @AfterEach
    void afterEach() {
        // 테스트가 종료 할때마다, store 를 초기화 시킨다.
        repository.clearStore();
    }

    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("spring");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    void 중복_회원_예외() {
        // given
        Member member1 = new Member();
        member1.setName("string");

        Member member2 = new Member();
        member2.setName("string");

        // when
        memberService.join(member1);
        // 동일한 이름의 회원이 있기 때문에 `java.lang.IllegalStateException: 이미 존재하는 회원입니다.` 으로 테스트가 실패 한다.
        /*
        // 방법.1 try ~ catch 으로 예외를 처리한다.
        try {
            memberService.join(member2);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
        */
        // 방법.2 assertThrows 사용
        // 여기에서는 () -> .. 같은 람다 문법을 사용한다. 지금은 이렇게 사용한다.. 정도로 알고 있으면 된다.
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

        // then
    }

}
