package com.demo.company.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = Address.COLLECTION_NAME)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address implements Serializable {

    public static final String COLLECTION_NAME = "address";

    public static final String FIELD_ADDRESS_NAME = "addressName";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_CITY = "city";

    @Field(Address.FIELD_ADDRESS_NAME)
    private String addressName;

    @Field(Address.FIELD_ADDRESS)
    private String address;

    @Field(Address.FIELD_CITY)
    private String city;
}
