/**
 * RenderCommand.java
 *
 * Description:		Message to start/stop rendering
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.messages;


public class RenderCommand {

    /** True to render/false to stop */
    public boolean render;

    /**
     * RenderCommand constructor
     *
     * @param render true to start rendering/false to stop
     */
    public RenderCommand(boolean render) {
        this.render = render;
    }

    /** Whether to start rendering or not */
    public boolean startRendering() {
        return render;
    }

    public String toString() {
        return new StringBuffer().append("(RenderCommand)[").append(render).append("]").toString();
    }


}


/* @(#)RenderCommand.java */
