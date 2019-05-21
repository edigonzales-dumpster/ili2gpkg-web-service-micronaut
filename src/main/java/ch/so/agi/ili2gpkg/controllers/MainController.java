package ch.so.agi.ili2gpkg.controllers;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.so.agi.ili2gpkg.controllers.MainController;
import io.micronaut.http.HttpStatus;

@Controller("/ili2gpkg")
public class MainController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private static String FOLDER_PREFIX = "ili2gpkg_";

    @Get("/")
    @View("upload")
    public HttpStatus index() {
        return HttpStatus.OK;
    }

}