package pro.akvel.spring.converter;

import java.io.File;
import java.util.List;

/**
 * Xml configuration searcher
 *
 * @author akvel
 * @since 12.08.2020
 */
public class XmlScanner {
    //FIXME add logs


    List<File> getConfigurations(String path){
        return List.of(new File(path));
    }
}
