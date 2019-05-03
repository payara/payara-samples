package fish.payara.test.ejb.httpclient.model;

import java.io.Serializable;

import javax.json.bind.annotation.JsonbProperty;

public class CustomValue implements Serializable {

    @JsonbProperty
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value == null ? "" : value.toString();
    }
}
