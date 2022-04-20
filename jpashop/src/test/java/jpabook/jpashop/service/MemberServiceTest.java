package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // test case의 경우 Rollback 시킴 -> commit이 안됨 -> insert문 생성 x
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    // @Rollback(false) // insert 문 확인을 원한다면 Rollback을 false 시킴
    void 회원가입() throws Exception{
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long memberId = memberService.join(member);

        // then
        // em.flush(); // persisContext 의 변경이나 등록을 db에 반영해줌 -> insert 문 확인가능 -> @Transactional에 의해서 rollback 됨
       assertEquals(member, memberRepository.findOne(memberId));

    }

    @Test
    void 중복_회원_예외() throws Exception{
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");
        // when
        memberService.join(member1);
        // memberService.join(member2);    // IllegalStatException 예외 발생

        // try / catch 문을 이용한 예외 확인
        /*
        try{
            memberService.join(member2);    // 예외 발생!
        } catch (IllegalStateException e){  // 예외 발생 한다면 정상 작동
            return;
        }ㅎ
        */

        // then
        assertThrows(IllegalStateException.class, () -> {   // Juni5의 기대 예외 값 확인
           memberService.join(member2);
        });
        // fail("예외가 발생해야 합니다.");  // 여기가지 와서는 안됨. 중간에 예외 발생 처리
    }
}