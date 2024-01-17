package study.springdatajpa.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import study.springdatajpa.entity.Member;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberJpaRepositoryTest {
    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        Assertions.assertEquals(findMember.getId(),member.getId());
        Assertions.assertEquals(findMember.getUsername(),member.getUsername());
        Assertions.assertEquals(findMember,member);
    }
    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        Assertions.assertEquals(findMember1,member1);
        Assertions.assertEquals(findMember2,member2);

        // findMember1.setUsername("member!!!!"); // 변경감지(dirty checking)를 통해 변경된다!

        // 리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        Assertions.assertEquals(all.size(),2);

        // 카운트 검증
        long count = memberJpaRepository.count();
        Assertions.assertEquals(count,2);

        // 삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long delCount = memberJpaRepository.count();
        Assertions.assertEquals(delCount,0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("AAA",20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA",15);
        Assertions.assertEquals(result.get(0).getUsername(),"AAA");
        Assertions.assertEquals(result.get(0).getAge(),20);
        Assertions.assertEquals(result.size(),1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA",10);
        Member m2 = new Member("BBB",20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        Assertions.assertEquals(findMember,m1);
    }
}
