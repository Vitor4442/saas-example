package com.vtr.saas.requests;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  CategoryRequest {
    private String name;
    private String description;
}
