package com.boot.shopdemo.repository;


import com.boot.shopdemo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

    Member findByEmail(String email);
}
