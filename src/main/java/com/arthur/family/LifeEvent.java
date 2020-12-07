package com.arthur.family;

import com.arthur.util.LifeEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LifeEvent {

    private LifeEventType type;
    private LocalDate date;

}
