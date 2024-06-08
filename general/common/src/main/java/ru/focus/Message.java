package ru.focus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class Message implements Serializable {

    @JsonProperty("message_type")
    private MessageType messageType;
    @JsonProperty("message")
    private String message;
    @JsonProperty("client_name")
    private String clientName;
    @JsonProperty("clients_list")
    private Set<String> clientsList;
    @JsonProperty("current_date_time")
    private String currentDateTime;

}
