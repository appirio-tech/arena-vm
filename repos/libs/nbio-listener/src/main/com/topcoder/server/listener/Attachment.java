package com.topcoder.server.listener;

/*javanio*
import java.nio.channels.SelectableChannel;
/**/

/*niowrapper*/

import com.topcoder.server.listener.nio.channels.SelectableChannel;

/**/

/**
 * The class that represent attachments, allows to get the associated channel for the given attachment.
 *
 * @author  Timur Zambalayev
 */
public interface Attachment {

    /**
     * Returns the associated selectable channel.
     *
     * @return  the associated selectable channel.
     */
    SelectableChannel channel();

}
