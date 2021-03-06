package com.heartmarket.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.heartmarket.model.dto.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	User findByEmail(String email);
	//	@Query(value = "select * from user where email=:email", nativeQuery = true)
	//User findByEmail(@Param("email") String email);
	User findTop1ByOrderByUserNoDesc();
	User findByUserNo(int userNo);
	User findByNickname(String nickname);
	
	@Query(value = "alter table user auto_increment=:number",nativeQuery = true)
	public void resortUserNo(@Param("number") int number);
}
