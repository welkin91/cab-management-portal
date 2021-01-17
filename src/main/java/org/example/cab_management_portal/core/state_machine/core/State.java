package org.example.cab_management_portal.core.state_machine.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.example.cab_management_portal.models.CabState;

import java.util.Set;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class State {

    CabState state;

    Set<ActionType> actions;

    CabState nextState;
}
