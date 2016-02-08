package dvoraka.avservice;

import java.io.File;

/**
 * AV service interface.
 */
public interface AVService {

    boolean scanStream(byte[] bytes);

    boolean scanFile(File file);
}