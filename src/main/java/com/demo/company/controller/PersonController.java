package com.demo.company.controller;

import com.demo.base.BaseResponse;
import com.demo.base.ListBaseResponse;
import com.demo.base.Metadata;
import com.demo.base.SingleBaseResponse;
import com.demo.company.entity.Person;
import com.demo.company.service.PersonService;
import com.demo.dto.PersonCreateRequest;
import com.demo.dto.PersonResponse;
import com.demo.dto.PersonUpdateRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(PersonControllerPath.BASE_PATH)
public class PersonController implements PersonControllerPath {

    @Autowired
    private PersonService personService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse create(
            @RequestParam String storeId,
            @RequestParam String channelId,
            @RequestParam String clientId,
            @RequestParam String requestId,
            @RequestParam String username,
            @RequestBody PersonCreateRequest request
    ) throws Exception {
        this.personService.create(toPerson(request));
        return new BaseResponse(null, null, true, requestId);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListBaseResponse<PersonResponse> index(
            @RequestParam String storeId,
            @RequestParam String channelId,
            @RequestParam String clientId,
            @RequestParam String requestId,
            @RequestParam String username,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) throws Exception {
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> persons = personService.index(pageable);
        List<PersonResponse> personResponses = persons.getContent().stream().map(this::toPersonResponse).collect(Collectors.toList());
        return new ListBaseResponse<>(null, null, true, requestId, personResponses,
                new Metadata(page, size, persons.getTotalElements()));
    }

    @RequestMapping(value = PersonControllerPath.FIND_BY_PERSON_CODE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleBaseResponse<Person> find(
            @RequestParam String storeId,
            @RequestParam String channelId,
            @RequestParam String clientId,
            @RequestParam String requestId,
            @RequestParam String username,
            @PathVariable String personCode
    ) throws Exception {
        Person person = personService.findByPersonCode(personCode);
        return new SingleBaseResponse<>(null, null, true, requestId, person);
    }

    @RequestMapping(value = PersonControllerPath.UPDATE_BY_PERSON_CODE, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse update(
            @RequestParam String storeId,
            @RequestParam String channelId,
            @RequestParam String clientId,
            @RequestParam String requestId,
            @RequestParam String username,
            @PathVariable String personCode,
            @RequestBody PersonUpdateRequest request
    ) throws Exception {
        personService.update(personCode, toPerson(request));
        return new BaseResponse(null, null, true, requestId);
    }

    @RequestMapping(value = PersonControllerPath.DELETE_BY_PERSON_CODE, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse delete(
            @RequestParam String storeId,
            @RequestParam String channelId,
            @RequestParam String clientId,
            @RequestParam String requestId,
            @RequestParam String username,
            @PathVariable String personCode
    ) throws Exception {
        personService.delete(personCode);
        return new BaseResponse(null, null, true, requestId);
    }

    private PersonResponse toPersonResponse(Person person) {
        return Optional.ofNullable(person).map(e -> {
            PersonResponse personResponse = PersonResponse.builder().build();
            BeanUtils.copyProperties(e, personResponse);
            return personResponse;
        }).orElse(null);
    }

    private Person toPerson(PersonCreateRequest request) {
        return Optional.ofNullable(request).map(e -> {
            Person person = Person.builder().build();
            BeanUtils.copyProperties(e, person);
            return person;
        }).orElse(null);
    }

    private Person toPerson(PersonUpdateRequest request) {
        return Optional.ofNullable(request).map(e -> {
            Person person = Person.builder().build();
            BeanUtils.copyProperties(e, person);
            return person;
        }).orElse(null);
    }
}
