package com.andriyklus.dota2.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tournament {

    private String name;
    private String prizePool;
    private String link;
    private String year;

}
