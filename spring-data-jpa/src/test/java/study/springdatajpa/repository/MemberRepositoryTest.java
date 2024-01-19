package study.springdatajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import study.springdatajpa.dto.MemberDto;
import study.springdatajpa.entity.Member;
import study.springdatajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

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

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age =10;
        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"username"));
        //when
        Page<Member> page = memberRepository.findByAge(age,pageRequest); // 반환타입을 Page로 선언하면 토탈 카운트 쿼리까지 같이 실ㅂ
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        // Slice<Member> slicePage = memberRepository.findSliceByAge(age,pageRequest); // slice는 count 쿼리를 날리지 않는다.

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for(Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        Assertions.assertEquals(content.size(),3);
        Assertions.assertEquals(page.getTotalElements(),5);
        Assertions.assertEquals(page.getNumber(),0);
        Assertions.assertEquals(page.getTotalPages(),2);
        Assertions.assertEquals(page.isFirst(),true);
        Assertions.assertEquals(page.hasNext(),true);

//        Assertions.assertEquals(content.size(),3);
//        Assertions.assertEquals(slicePage.getNumber(),0);
//        Assertions.assertEquals(slicePage.isFirst(),true);
//        Assertions.assertEquals(slicePage.hasNext(),true);
    }

    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));

        int count = memberRepository.bulkAgePlus(20); // 20살이나 이상인 경우 모두 나이에서 +1 업데이트
        em.flush(); // 남아있는 변경되지 않은 내용이 DB에 반영
        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member 5 = " + member5); // 40

        Assertions.assertEquals(count,3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1",10, teamA);
        Member member2 = new Member("member2",10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("members = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = new Member("member1",10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {
        //given
        Member member1 = new Member("member1",10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");

    }

}