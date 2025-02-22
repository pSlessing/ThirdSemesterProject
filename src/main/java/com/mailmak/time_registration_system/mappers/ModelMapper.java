package com.mailmak.time_registration_system.mappers;

public interface ModelMapper<A,B> {
    B mapTo(A a);
}
