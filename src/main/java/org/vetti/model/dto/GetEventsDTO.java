package org.vetti.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetEventsDTO {

    private String eventName;
    private String schedulingUrl;
    private String vetName;
}
