package com.demo.company.service;

import com.demo.company.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonService {

    void create(Person person) throws Exception;

    void update(String personCode, Person person) throws Exception;

    Page<Person> index(Pageable pageable) throws Exception;

    Person findByPersonCode(String personCode) throws Exception;

    void delete(String personCode) throws Exception;
}
