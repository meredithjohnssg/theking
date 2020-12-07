package com.arthur.family;

import com.arthur.util.DoubleKey;
import com.arthur.util.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dob;
    private Set<Person> children;
    private Set<LifeEvent> lifeEvents;
    HashMap<DoubleKey, Person> relatives = new HashMap<>();

}
