package org.example.cab_management_portal.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@Api(value = "cab management portal Base URL's", description = "Contains Api end point to keep an eye of system's health / system details.")
@RequestMapping(value = "")
@Slf4j
public class BaseController {

    @ApiOperation(value = "Checks server health status, to be used by HAProxy", notes = "Returns App server's health", response = BaseController.class)
    @GetMapping(value = "/ping")
    @ResponseBody
    public ResponseEntity<String> ping() {
        log.debug("ping-pong");
        return ResponseEntity.ok("pong");
    }

    @ApiOperation(value = "Base call for the project", notes = "Base call for the project", response = BaseController.class)
    @GetMapping(value = "/")
    @ResponseBody
    public ResponseEntity<String> baseCall() {
        log.debug("ping-pong");
        return ResponseEntity.ok("Welcome to the native cab management portal.");
    }
}
