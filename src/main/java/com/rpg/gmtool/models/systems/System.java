package com.rpg.gmtool.models.systems;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class System {
    private String name;
    private String codeName;
    private List<SystemStatistic> availableStats;
    private List<String> availableProficiencies;

    @Override
    public String toString() {
        return name;
    }
}
