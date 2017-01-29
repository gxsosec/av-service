package dvoraka.avservice.common.data;

/**
 * Interface for file message.
 */
public interface FileMessage extends Message {

    byte[] getData();

    String getFilename();

    String getOwner();
}
