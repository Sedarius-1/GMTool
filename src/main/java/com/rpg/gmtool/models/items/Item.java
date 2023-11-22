package com.rpg.gmtool.models.items;

import lombok.Data;

@Data
public class Item {
    private String name;
    private Long price;
    private String availability;
    private String description;
}
