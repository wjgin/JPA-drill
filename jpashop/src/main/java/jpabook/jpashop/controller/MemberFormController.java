package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberFormController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createMemberForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        // 오류가 생기면, 오류가 result에 담겨서 아래 코드가 실행
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";
    }

    @GetMapping("/members")
    public String members(Model model) {
        /*
        실제 서비스에서 entity(Member 같은)을 직접 넘기는 것을 권장하지 않음
        API에서는 절대 entity를 넘겨서는 안됨 -> API 스펙에 맞아야함
        --> DTO를 생성해서 뷰나 스펙에 맞는 데이터를 담은 객체를 넘겨주는 것을 권장
        */
        List<Member> members = memberService.findAll();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
