package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 기본은 false
@RequiredArgsConstructor    // final인 MemberRepository 생성을 위한 생성자를 만들어줌
public class MemberService {

    private final MemberRepository memberRepository;
/*

    @Autowired  // 생성자 injection 권장 -> spring이 자동 injection, 어노테이션 생략 가능
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
*/

    // 회원가입
    @Transactional  // 쓰기의 경우 readOnl y가 적용되면 안됨 .
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원 입니다.");
        }
    }

    @Transactional  // 변경 감지 업데이트
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }

    // 회원전체 조회
    public List<Member> findAll(){
        return memberRepository.findAll();
    }

    // 단건 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
