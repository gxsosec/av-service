package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * File and anti-virus REST service.
 */
@Validated
public interface AvRestService {

    /**
     * Returns a message status.
     *
     * @param id the message ID
     * @return the status
     */
    MessageStatus messageStatus(String id);

    /**
     * Returns a message status.
     *
     * @param id        the message ID
     * @param serviceId the service ID
     * @return the status
     */
    MessageStatus messageStatus(String id, String serviceId);

    /**
     * Returns a service ID for the given message.
     *
     * @param id the message ID
     * @return the service ID
     */
    String messageServiceId(String id);

    /**
     * Checks a file inside the message.
     *
     * @param message the AV message
     */
    void checkMessage(@Valid AvMessage message);

    /**
     * Saves a file inside the message.
     *
     * @param message the message.
     */
    void saveMessage(@Valid AvMessage message);

    /**
     * Loads an AV message with a given description.
     *
     * @param message the message
     * @return the loaded message
     */
    AvMessage loadMessage(AvMessage message);

    /**
     * Updates a file.
     *
     * @param message the update message
     */
    void updateMessage(AvMessage message);

    /**
     * Deletes a file.
     *
     * @param message the delete message
     */
    void deleteMessage(AvMessage message);

    /**
     * Returns a response AV message.
     *
     * @param id the request message ID
     * @return the response AV message or null if message is not available
     */
    AvMessage getResponse(String id);

    /**
     * Starts the service.
     */
    void start();

    /**
     * Stops the service.
     */
    void stop();
}
