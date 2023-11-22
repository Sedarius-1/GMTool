package com.rpg.gmtool.models.systems;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatistic {
    private String name;
    private String shorthand;

    @Override
    public String toString(){
        return name+" ("+shorthand+")";
    }
}
