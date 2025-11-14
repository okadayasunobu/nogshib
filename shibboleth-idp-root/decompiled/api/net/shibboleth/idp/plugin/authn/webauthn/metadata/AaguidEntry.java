/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  com.fasterxml.jackson.annotation.JsonPropertyDescription
 *  com.fasterxml.jackson.annotation.JsonPropertyOrder
 */
package net.shibboleth.idp.plugin.authn.webauthn.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value={"name", "icon_dark", "icon_light", "type"})
public class AaguidEntry {
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="icon_dark")
    @JsonPropertyDescription(value="")
    private String iconDark;
    @JsonProperty(value="icon_light")
    @JsonPropertyDescription(value="")
    private String iconLight;
    @JsonProperty(value="type")
    @JsonPropertyDescription(value="")
    private String type;

    @JsonProperty(value="name")
    public String getName() {
        return this.name;
    }

    @JsonProperty(value="name")
    public void setName(String string) {
        this.name = string;
    }

    @JsonProperty(value="icon_dark")
    public String getIconDark() {
        return this.iconDark;
    }

    @JsonProperty(value="icon_dark")
    public void setIconDark(String string) {
        this.iconDark = string;
    }

    @JsonProperty(value="icon_light")
    public String getIconLight() {
        return this.iconLight;
    }

    @JsonProperty(value="icon_light")
    public void setIconLight(String string) {
        this.iconLight = string;
    }

    @JsonProperty(value="type")
    public String getType() {
        return this.type;
    }

    @JsonProperty(value="type")
    public void setType(String string) {
        this.type = string;
    }
}

