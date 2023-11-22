package com.rpg.gmtool.models.campaigns;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    private String campaignName;
    private String systemCodeName;
    private String systemName;
    private String partyName;
}
