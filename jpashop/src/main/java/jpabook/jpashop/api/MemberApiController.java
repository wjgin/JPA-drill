package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    // 엔티티를 외부에서 바인딩 받아서 오는 스타일 -> 권장되지 않음 / 엔티티는 여러 곳에서 사용되는 존재 -> 장애 발생 확률이 높음
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    // 별도의 DTO를 만들어서 데이터를 바인딩 -> API 스펙에 맞게 구성할 수 있음
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        // update 메소드에서 findMember를 리턴 안함(return이 void): update인 command와 find인 query를 분리하기 위함(유지 보수성)
        memberService.update(id, request.getName()); // 영속성 컨테이너에서 변화 감지로 업데이트
        Member findMember = memberService.findOne(id);  // 업데이트 된 멤버 객체
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty   // API 스펙이 맞추어 설정 가능 -> 엔티티를 건드리지 않아도 됨
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id){
            this.id = id;
        }

    }

}
