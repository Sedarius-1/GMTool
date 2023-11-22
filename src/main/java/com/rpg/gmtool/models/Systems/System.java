package com.rpg.gmtool.models.Systems;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class System {
    private String name;

    private List<String> availableStats;
}
