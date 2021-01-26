package com.blibli.demo;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.demo.base.BaseResponse;
import com.demo.company.entity.Person;
import com.demo.company.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.DemoApplication;
import com.demo.company.controller.PersonControllerPath;
import com.demo.company.entity.Address;
import com.demo.dto.PersonCreateRequest;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerIntegrationTest {

  /* ===== Request Params ===== */
  public static final String STORE_ID_KEY = "storeId";
  public static final String CHANNEL_ID_KEY = "channelId";
  public static final String CLIENT_ID_KEY = "clientId";
  public static final String REQUEST_ID_KEY = "requestId";
  public static final String USERNAME_KEY = "username";

  public static final String STORE_ID = "10001";
  public static final String CHANNEL_ID = "1";
  public static final String CLIENT_ID = "1";
  public static final String REQUEST_ID = "1";
  public static final String USERNAME = "emmanuel";

  /* ===== Request Data ===== */
  public static final String PERSON_CODE = "123";
  public static final String PERSON_NAME = "Emmanuel";
  public static final int ADDRESSES = 3;
  public static final String ADDRESS_PREFIX = "Jalan ";
  public static final String ADDRESS_NAME_PREFIX = "Alamat ";
  public static final String CITY = "Surabaya";

  /* ========== */

  @Value("${local.server.port}")
  private int port;
  @Value("${server.servlet.context-path}")
  private String contextPath;
  private ObjectMapper objectMapper;
  @Autowired
  private MongoTemplate mongoTemplate;

  private PersonCreateRequest personCreateRequest;
  @Autowired
  private PersonRepository personRepository;

  @Before
  public void setUp() throws Exception {
    RestAssured.port = port;
    objectMapper = new ObjectMapper();
    mongoTemplate.indexOps(Person.class).ensureIndex(new Index("personCode", Sort.Direction.ASC).unique());

    personCreateRequest =
        PersonCreateRequest.builder().personCode(PERSON_CODE).personName(PERSON_NAME)
            .addresses(IntStream.range(0, ADDRESSES)
                .mapToObj(o -> Address.builder().address(ADDRESS_PREFIX + o)
                    .addressName(ADDRESS_NAME_PREFIX + o).city(CITY).build())
                .collect(Collectors.toList()))
            .build();
  }

  @Test
  public void createPerson_success_returnBaseResponse() throws IOException {
    ValidatableResponse response =
        RestAssured.given().contentType(ContentType.JSON).queryParam(STORE_ID_KEY, STORE_ID)
            .queryParam(CHANNEL_ID_KEY, CHANNEL_ID).queryParam(CLIENT_ID_KEY, CLIENT_ID)
            .queryParam(REQUEST_ID_KEY, REQUEST_ID).queryParam(USERNAME_KEY, USERNAME)
            .body(personCreateRequest).post(contextPath + PersonControllerPath.BASE_PATH).then();

    BaseResponse baseResponse =
        objectMapper.readValue(response.extract().asString(), BaseResponse.class);

    Assert.assertTrue(baseResponse.isSuccess());
    Person person = personRepository.findFirstByPersonCodeAndMarkForDeleteFalse(PERSON_CODE);
    Assert.assertNotNull(person);
    Assert.assertEquals(person.getPersonCode(), PERSON_CODE);
    Assert.assertEquals(person.getPersonName(), PERSON_NAME);
    IntStream.range(0, ADDRESSES).forEach(e -> {
      Address address = person.getAddresses().get(e);
      Assert.assertEquals(address.getCity(), CITY);
      Assert.assertEquals(address.getAddress(), ADDRESS_PREFIX + e);
      Assert.assertEquals(address.getAddressName(), ADDRESS_NAME_PREFIX + e);
    });
  }

  @Test
  public void createPerson_failed_returnBaseResponse() {

  }
}
