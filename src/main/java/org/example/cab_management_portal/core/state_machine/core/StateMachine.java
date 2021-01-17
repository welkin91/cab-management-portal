package org.example.cab_management_portal.core.state_machine.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
@ToString
public class StateMachine {

    StateMachineType type;

    List<State> stateSequence;
}
