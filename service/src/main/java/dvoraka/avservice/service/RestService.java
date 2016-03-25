package dvoraka.avservice.service;

import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.MessageStatus;

/**
 * REST service.
 */
public interface RestService {

    MessageStatus messageStatus(String id);

    MessageStatus messageStatus(String id, String serviceId);

    String messageServiceId(String id);

    void messageCheck(AVMessage message);

    AVMessage getResponse(String id);

    void stop();
}