package ch.so.agi.ili2gpkg.services;

import java.util.ArrayList;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2gpkg.GpkgMain;
import ch.so.agi.ili2gpkg.utils.ExtractZipFileVisitor;

@Singleton
public class Ili2gpkgService {
    private static final Logger log = LoggerFactory.getLogger(Ili2gpkgService.class);

    public String convert(String uploadFileName, String strokeArcs) throws Exception {
        // for graalvm only
        org.sqlite.JDBC.class.getName();

       
        String dataFileName = null;
        
        // prepare ili2db settings
        Config settings = createConfig();
        settings.setValidation(false);
        settings.setFunction(Config.FC_IMPORT);
        settings.setDoImplicitSchemaImport(true);
        settings.setNameOptimization(settings.NAME_OPTIMIZATION_TOPIC);
        settings.setDefaultSrsCode("2056");
        
        if (strokeArcs != null) {
            settings.setStrokeArcs(settings, settings.STROKE_ARCS_ENABLE);
        }

        // handle (zip) file
        String dest = new File(uploadFileName).getAbsoluteFile().getParent();
        ArrayList<String> fileNames = new ArrayList<String>();

        if (uploadFileName.toLowerCase().endsWith(".zip")) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "false");
            URI uri = URI.create("jar:file://" + new File(uploadFileName).getAbsolutePath());

            try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
                Iterable<Path> roots = zipfs.getRootDirectories();
                Path root = roots.iterator().next();

                ExtractZipFileVisitor ezfv = new ExtractZipFileVisitor(Paths.get(dest));
                Files.walkFileTree(root, ezfv);
                fileNames = ezfv.getFileList();
            }  
            
            // try to figure out data file to convert
            for (String fileName : fileNames) {
                if (fileName.toLowerCase().endsWith("xtf") || fileName.toLowerCase().endsWith("itf") 
                        || fileName.toLowerCase().endsWith("xml")) {
                    dataFileName = Paths.get(dest, fileName).toFile().getAbsolutePath();
                    break;
                }
            }
            
            // additional ili files
            String modelDir = settings.getModeldir() + ";" + dest;
            settings.setModeldir(modelDir);            
        } else {
            dataFileName = uploadFileName;
        }

        if (dataFileName == null) {
            return null;
        }

        // convert transfer file
        if (Ili2db.isItfFilename(dataFileName)) {
            settings.setItfTransferfile(true);
        }

        String gpkgFileName = dataFileName.substring(0, uploadFileName.length()-4) + ".gpkg";
        settings.setDbfile(gpkgFileName);

        settings.setDburl("jdbc:sqlite:" + settings.getDbfile());
        settings.setXtffile(dataFileName);

        Ili2db.run(settings, null);
        return gpkgFileName;
    }
   
    private Config createConfig() {
        Config settings = new Config();
        new GpkgMain().initConfig(settings);
        return settings;
    }

}


