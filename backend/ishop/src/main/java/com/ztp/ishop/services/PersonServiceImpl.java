package com.ztp.ishop.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ztp.ishop.entity.Person;
import com.ztp.ishop.repository.PersonDao;

import jakarta.transaction.Transactional;

@Service
public class PersonServiceImpl implements PersonService{

	@Autowired
	private PersonDao personDao;
	
	@Override
	@Transactional
	public List<Person> findAll() {
		return (List<Person>) personDao.findAll();
	}

	@Override
	@Transactional
	public Person save(Person person) {
		return personDao.save(person);
	}

	@Override 
	public Person findById(Long id) {
		return personDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Person person) {
		personDao.delete(person);
		
	}

}
