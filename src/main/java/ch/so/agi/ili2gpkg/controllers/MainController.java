package ch.so.agi.ili2gpkg.controllers;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.views.View;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ehi.ili2db.base.Ili2dbException;
import ch.so.agi.ili2gpkg.controllers.MainController;
import ch.so.agi.ili2gpkg.services.Ili2gpkgService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;

@Controller("/ili2gpkg")
public class MainController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    private static String FOLDER_PREFIX = "ili2gpkg_";

    @Inject
    Ili2gpkgService ili2gpkgService;

    @Get("/")
    @View("upload")
    public HttpStatus index() {
        return HttpStatus.OK;
    }
    
    @Post(value = "/", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.APPLICATION_OCTET_STREAM) 
    @View("upload")
    public HttpResponse<?> validate(CompletedFileUpload file, Optional<String> doStrokeArcs) {
        try {           
            if (file.getSize() == 0 || file.getFilename().trim().equalsIgnoreCase("") || file.getName() == null) {
                log.warn("No file was uploaded. Redirecting to starting page.");
                return HttpResponse.seeOther(URI.create("/ili2gpkg"));
            }

            String strokeArcs = doStrokeArcs.orElse(null);
            
            Path tmpDirectory = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), FOLDER_PREFIX);
            Path uploadFilePath = Paths.get(tmpDirectory.toString(), file.getFilename());

            byte[] bytes = file.getBytes();
            Files.write(uploadFilePath, bytes);
            String uploadFileName = uploadFilePath.toFile().getAbsolutePath();
            log.info(uploadFileName);
            
            String gpkgFileName = ili2gpkgService.convert(uploadFileName, strokeArcs);
            
            return HttpResponse.ok().header("content-disposition", "attachment; filename=" + new File(gpkgFileName).getName())
                    .contentLength(new File(gpkgFileName).length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new File(gpkgFileName));      
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return HttpResponse.badRequest("Something went wrong:\n\n" + e.getMessage());
        }
    }
}