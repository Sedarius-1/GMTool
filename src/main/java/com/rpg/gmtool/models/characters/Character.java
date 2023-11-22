package com.rpg.gmtool.models.characters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Character {
    private String name;
    private String rpgRole;
    private String description;
    private List<Statistic> stats;
    private List<String> equipment;
}
