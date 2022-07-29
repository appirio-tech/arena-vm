package com.topcoder.client.contestApplet.widgets;

import javax.swing.Icon;
import javax.swing.JButton;

public class FlashingIconButton extends JButton {
    private Icon flashingIcon;
    private Icon nonFlashingIcon;
    private Icon disabledFlashingIcon;
    private Icon disabledNonFlashingIcon;
    private boolean flashing = false;

    public FlashingIconButton(Icon flashingIcon, Icon nonFlashingIcon, Icon disabledFlashingIcon, Icon disabledNonFlashingIcon) {
        this.flashingIcon = flashingIcon;
        this.nonFlashingIcon = nonFlashingIcon;
        this.disabledFlashingIcon = disabledFlashingIcon;
        this.disabledNonFlashingIcon = disabledNonFlashingIcon;
        setIcon(nonFlashingIcon);
        setDisabledIcon(disabledNonFlashingIcon);
    }

    public void setFlashing(boolean flashing) {
        if (flashing == this.flashing) {
            return;
        }

        this.flashing = flashing;
        if (flashing) {
            setIcon(flashingIcon);
            setDisabledIcon(disabledFlashingIcon);
        } else {
            setIcon(nonFlashingIcon);
            setDisabledIcon(disabledNonFlashingIcon);
        }

        repaint();
    }

    public boolean isFlashing() {
        return flashing;
    }
}
