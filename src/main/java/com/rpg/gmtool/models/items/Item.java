package com.rpg.gmtool.models.items;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String name;
    private String type;
    private String price;
    private String availability;
    private String description;

    @Override
    public String toString() {
        return name;
    }
}
