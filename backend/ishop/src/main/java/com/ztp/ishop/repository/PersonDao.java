package com.ztp.ishop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ztp.ishop.entity.Person;

public interface PersonDao extends JpaRepository<Person, Long> {

}
