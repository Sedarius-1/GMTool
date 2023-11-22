package com.rpg.gmtool.models.characters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Talent {
    private String name;
    private String stat;
    private String proficiency;
}
