package study.springdatajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import study.springdatajpa.dto.MemberDto;
import study.springdatajpa.entity.Member;
import study.springdatajpa.entity.Team;

import javax.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertEquals(findMember.getId(),member.getId());
        Assertions.assertEquals(findMember.getUsername(),member.getUsername());
        Assertions.assertEquals(findMember,member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        Assertions.assertEquals(findMember1,member1);
        Assertions.assertEquals(findMember2,member2);

        // findMember1.setUsername("member!!!!"); // 변경감지(dirty checking)를 통해 변경된다!

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertEquals(all.size(),2);

        // 카운트 검증
        long count = memberRepository.count();
        Assertions.assertEquals(count,2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long delCount = memberRepository.count();
        Assertions.assertEquals(delCount,0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("AAA",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA",15);

        Assertions.assertEquals(result.get(0).getUsername(),"AAA");
        Assertions.assertEquals(result.get(0).getAge(),20);
        Assertions.assertEquals(result.size(),1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        Assertions.assertEquals(findMember,m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA",10);
        Assertions.assertEquals(result.get(0),m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for(String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findUserMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA",10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for(MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberRepository.save(m1);
        memberRepository.save(m2);

         List<Member> aaa = memberRepository.findListByUsername("AAA");
         Member findMember = memberRepository.findMemberByUsername("AAA");
         Optional<Member> optionalMember = memberRepository.findOptionalByUsername("AAA");

         List<Member> result = memberRepository.findListByUsername("abcdefg");
         System.out.println("result = " + result.size()); // 0
    }
}