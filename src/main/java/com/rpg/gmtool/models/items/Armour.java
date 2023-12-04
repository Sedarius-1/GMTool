package com.rpg.gmtool.models.items;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Armour extends Item {

    private String bodypart;
    private String protection;
    private String isWorn;

    public Armour(String name, String type, String price, String availability, String description, String bodypart, String protection, String isWorn) {
        super(name, type, price, availability, description);
        super.setType("armour");
        this.bodypart = bodypart;
        this.protection = protection;
        this.isWorn = isWorn;
    }

}
