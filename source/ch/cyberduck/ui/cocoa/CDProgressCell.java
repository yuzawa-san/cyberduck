package ch.cyberduck.ui.cocoa;

/*
 *  Copyright (c) 2003 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import com.apple.cocoa.application.*;
import com.apple.cocoa.foundation.NSAttributedString;
import com.apple.cocoa.foundation.NSPoint;
import com.apple.cocoa.foundation.NSRect;
import com.apple.cocoa.foundation.NSSize;

import org.apache.log4j.Logger;

import ch.cyberduck.core.Queue;

public class CDProgressCell extends CDTableCell {
    private static Logger log = Logger.getLogger(CDProgressCell.class);

    private Queue queue;

    public void setObjectValue(Object queue) {
        this.queue = (Queue) queue;
    }

    private static final NSImage stripeGrayIcon = NSImage.imageNamed("stripeGray.tiff");
    private static final NSImage stripeWhiteIcon = NSImage.imageNamed("stripeWhite.tiff");

    public void drawInteriorWithFrameInView(NSRect cellFrame, NSView controlView) {
        super.drawInteriorWithFrameInView(cellFrame, controlView);
        if (queue != null) {

            NSPoint cellPoint = cellFrame.origin();
            NSSize cellSize = cellFrame.size();

            final float SPACE = 5;
            final float PROGRESS_HEIGHT = 10;
            float progress;
            if (queue.getSize() > 0) {
                progress = (float) ((float) queue.getCurrent() / (float) queue.getSize());
            }
            else {
                progress = 0;
            }
            float PROGRESS_WIDTH = progress * (cellSize.width() - SPACE * 2);
            if (PROGRESS_WIDTH < 0) {
                PROGRESS_WIDTH = 0;
            }

            NSRect barRect = new NSRect(cellPoint.x() + SPACE,
                    cellPoint.y() + cellSize.height() / 2 - PROGRESS_HEIGHT / 2,
                    cellSize.width() - SPACE * 2,
                    PROGRESS_HEIGHT);
            NSRect barRectFilled = new NSRect(cellPoint.x() + SPACE,
                    cellPoint.y() + cellSize.height() / 2 - PROGRESS_HEIGHT / 2,
                    PROGRESS_WIDTH,
                    PROGRESS_HEIGHT);

            // drawing current of size string
            NSGraphics.drawAttributedString(new NSAttributedString((int) (progress * 100) + "%"
                    + " - " +
                    queue.getProgress(),
                    normalFont),
                    new NSRect(cellPoint.x() + SPACE,
                            cellPoint.y() + cellSize.height() / 2 - PROGRESS_HEIGHT / 2 - 10 - SPACE,
                            cellSize.width() - SPACE,
                            cellSize.height()));

            // drawing percentage and speed
            NSGraphics.drawAttributedString(new NSAttributedString(queue.getSpeedAsString()
                    + " - " +
                    queue.getTimeLeft(),
                    tinyFont),
                    new NSRect(cellPoint.x() + SPACE,
                            cellPoint.y() + cellSize.height() / 2 + PROGRESS_HEIGHT / 2 + SPACE,
                            cellSize.width() - SPACE,
                            cellSize.height()));

            // drawing progress bar
            if (highlighted) {
                NSColor.whiteColor().set();
            }
            else {
                NSColor.lightGrayColor().set();
            }
            NSBezierPath.strokeRect(barRect);
            if (highlighted) {
				NSColor.colorWithPatternImage(stripeWhiteIcon).set();
            }
            else {
                NSColor.colorWithPatternImage(stripeGrayIcon).set();
            }
            NSBezierPath.fillRect(barRectFilled);
        }
    }
}