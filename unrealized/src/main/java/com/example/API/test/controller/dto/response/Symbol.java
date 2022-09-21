package com.example.API.test.controller.dto.response;

import lombok.*;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor


public class Symbol {

    @XmlAttribute
    String id;
    @XmlAttribute
    String dealprice;
    @XmlAttribute
    String shortname;
    @XmlAttribute
    String mtype;

    String responseCode;
    String message;
}
