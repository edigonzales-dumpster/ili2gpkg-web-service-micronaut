package ch.so.agi.ili2gpkg.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractZipFileVisitor implements FileVisitor<Path> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Path destRoot;
    
    private ArrayList<String> files = new ArrayList<String>();

    public ExtractZipFileVisitor(Path destRoot) {
        this.destRoot = destRoot;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path destDir = Paths.get(destRoot.toString(), dir.toString()); 
        Files.createDirectories(destDir);
                
        if (dir.toString().contains("__MACOSX")) {
            log.info("Ignore __MACOSX directory.");
            return FileVisitResult.SKIP_SUBTREE;
        }
                
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path dest = Paths.get(destRoot.toString(), file.toString());  
        Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
        files.add(file.getFileName().toString());
        
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          return FileVisitResult.CONTINUE;
    }
    
    public ArrayList<String> getFileList() {
        return files;
    }
}

