package com.rpg.gmtool.models.items;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Weapon extends Item {

    private String damage;
    private String range;
    private String weaponType;

    public Weapon(String name, String type, String price, String availability, String description, String damage, String range, String weaponType) {
        super(name, type, price, availability, description);
        super.setType("weapon");
        this.damage = damage;
        this.range = range;
        this.weaponType = weaponType;
    }
}
