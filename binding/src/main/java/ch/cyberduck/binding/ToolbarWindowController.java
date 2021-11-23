package ch.cyberduck.binding;

/*
 * Copyright (c) 2002-2016 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import ch.cyberduck.binding.application.NSTabView;
import ch.cyberduck.binding.application.NSTabViewItem;
import ch.cyberduck.binding.application.NSToolbar;
import ch.cyberduck.binding.application.NSToolbarItem;
import ch.cyberduck.binding.application.NSView;
import ch.cyberduck.binding.application.NSWindow;
import ch.cyberduck.binding.foundation.FoundationKitFunctions;
import ch.cyberduck.binding.foundation.NSArray;
import ch.cyberduck.binding.foundation.NSEnumerator;
import ch.cyberduck.binding.foundation.NSMutableArray;
import ch.cyberduck.binding.foundation.NSNotification;
import ch.cyberduck.binding.foundation.NSObject;
import ch.cyberduck.core.preferences.Preferences;
import ch.cyberduck.core.preferences.PreferencesFactory;

import org.apache.log4j.Logger;
import org.rococoa.Foundation;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSPoint;
import org.rococoa.cocoa.foundation.NSRect;
import org.rococoa.cocoa.foundation.NSSize;
import org.rococoa.cocoa.foundation.NSUInteger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A window controller with a toolbar populated from a tabbed view.
 */
public abstract class ToolbarWindowController extends WindowController implements NSToolbar.Delegate, NSTabView.Delegate {
    private static final Logger log = Logger.getLogger(ToolbarWindowController.class);

    private final Preferences preferences = PreferencesFactory.get();

    /**
     * Static window title
     */
    private String title;

    protected NSTabView tabView;
    private final NSToolbar toolbar = NSToolbar.toolbarWithIdentifier(this.getToolbarName());

    /**
     * @return Content for the tabs.
     */
    protected abstract Map<Label, NSView> getPanels();

    @Override
    public void windowDidBecomeKey(NSNotification notification) {
        this.resize();
        super.windowDidBecomeKey(notification);
    }

    protected static class Label {
        public String identifier;
        public String label;

        public Label(final String identifier, final String label) {
            this.identifier = identifier;
            this.label = label;
        }

        @Override
        public boolean equals(final Object o) {
            if(this == o) {
                return true;
            }
            if(!(o instanceof Label)) {
                return false;
            }
            final Label label = (Label) o;
            return Objects.equals(identifier, label.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }
    }

    public void setTabView(NSTabView view) {
        this.tabView = view;
        this.tabView.setAutoresizingMask(new NSUInteger(NSView.NSViewWidthSizable | NSView.NSViewHeightSizable));
        this.tabView.setDelegate(this.id());
    }

    @Override
    public void awakeFromNib() {
        // Reset
        NSEnumerator items = this.tabView.tabViewItems().objectEnumerator();
        NSObject object;
        while((object = items.nextObject()) != null) {
            this.tabView.removeTabViewItem(Rococoa.cast(object, NSTabViewItem.class));
        }
        // Insert all panels into tab view
        for(Map.Entry<Label, NSView> tab : this.getPanels().entrySet()) {
            final NSTabViewItem item = NSTabViewItem.itemWithIdentifier(tab.getKey().identifier);
            item.setView(tab.getValue());
            item.setLabel(tab.getKey().label);
            this.tabView.addTabViewItem(item);
        }
        // Set up toolbar properties: Allow customization, give a default display mode, and remember state in user defaults
        toolbar.setAllowsUserCustomization(false);
        toolbar.setSizeMode(this.getToolbarSize());
        toolbar.setDisplayMode(this.getToolbarMode());
        toolbar.setDelegate(this.id());
        window.setToolbar(toolbar);
        // Change selection to last selected item in preferences
        final int index = preferences.getInteger(String.format("%s.selected", this.getToolbarName()));
        this.setSelectedPanel(index < this.getPanels().size() ? index : 0);
        this.setTitle(this.getTitle(tabView.selectedTabViewItem()));
        super.awakeFromNib();
    }

    /**
     * Change the toolbar selection and display the tab index.
     *
     * @param selected The index of the tab to be selected
     */
    protected void setSelectedPanel(final int selected) {
        int tab = selected;
        if(-1 == tab) {
            tab = 0;
        }
        String identifier = tabView.tabViewItemAtIndex(tab).identifier();
        if(!this.validateTabWithIdentifier(identifier)) {
            tab = 0;
            identifier = tabView.tabViewItemAtIndex(tab).identifier();
        }
        tabView.selectTabViewItemAtIndex(tab);
        NSTabViewItem page = tabView.selectedTabViewItem();
        if(page == null) {
            page = tabView.tabViewItemAtIndex(0);
        }
        toolbar.setSelectedItemIdentifier(page.identifier());
        this.initializePanel(identifier);
    }

    protected abstract void initializePanel(final String identifier);

    /**
     * @return The item identifier of the tab selected.
     */
    protected String getSelectedTab() {
        return toolbar.selectedItemIdentifier();
    }

    @Override
    public void invalidate() {
        toolbar.setDelegate(null);
        tabView.setDelegate(null);
        super.invalidate();
    }

    @Override
    public void setWindow(final NSWindow window) {
        this.title = window.title();
        window.setShowsToolbarButton(false);
        super.setWindow(window);
    }

    protected NSUInteger getToolbarSize() {
        return NSToolbar.NSToolbarSizeModeRegular;
    }

    protected NSUInteger getToolbarMode() {
        return NSToolbar.NSToolbarDisplayModeIconAndLabel;
    }

    private String getToolbarName() {
        return String.format("%s.toolbar", this.getBundleName().toLowerCase(Locale.ROOT));
    }

    /**
     * Keep reference to weak toolbar items. A toolbar may ask again for a kind of toolbar item already supplied to it,
     * in which case this method may return the same toolbar item it returned before
     */
    private final Map<String, NSToolbarItem> cache = new HashMap<>();

    @Override
    public NSToolbarItem toolbar_itemForItemIdentifier_willBeInsertedIntoToolbar(final NSToolbar toolbar,
                                                                                 final String itemIdentifier,
                                                                                 final boolean flag) {
        if(!cache.containsKey(itemIdentifier)) {
            cache.put(itemIdentifier, NSToolbarItem.itemWithIdentifier(itemIdentifier));
        }
        final NSToolbarItem toolbarItem = cache.get(itemIdentifier);
        final NSTabViewItem tab = tabView.tabViewItemAtIndex(tabView.indexOfTabViewItemWithIdentifier(itemIdentifier));
        if(null == tab) {
            log.warn(String.format("No tab for toolbar item %s", itemIdentifier));
            return null;
        }
        toolbarItem.setLabel(tab.label());
        toolbarItem.setPaletteLabel(tab.label());
        toolbarItem.setToolTip(tab.label());
        toolbarItem.setTarget(this.id());
        toolbarItem.setAction(Foundation.selector("toolbarItemSelected:"));
        return toolbarItem;
    }

    @Override
    public NSArray toolbarAllowedItemIdentifiers(final NSToolbar toolbar) {
        final NSMutableArray identifiers = NSMutableArray.array();
        for(Label label : this.getPanels().keySet()) {
            identifiers.addObject(label.identifier);
        }
        return identifiers;
    }

    @Override
    public NSArray toolbarDefaultItemIdentifiers(final NSToolbar toolbar) {
        return this.toolbarAllowedItemIdentifiers(toolbar);
    }

    @Override
    public NSArray toolbarSelectableItemIdentifiers(final NSToolbar toolbar) {
        return this.toolbarAllowedItemIdentifiers(toolbar);
    }

    @Override
    public boolean validateToolbarItem(final NSToolbarItem item) {
        return this.validateTabWithIdentifier(item.itemIdentifier());
    }

    protected boolean validateTabWithIdentifier(final String itemIdentifier) {
        return true;
    }

    protected String getTitle(final NSTabViewItem item) {
        return item.label();
    }

    public void toolbarItemSelected(final NSToolbarItem sender) {
        this.setSelectedPanel(tabView.indexOfTabViewItemWithIdentifier(sender.itemIdentifier()));
    }

    /**
     * Resize window frame to fit the content view of the currently selected tab.
     */
    private void resize() {
        final NSRect windowFrame = NSWindow.contentRectForFrameRect_styleMask(window.frame(), window.styleMask());
        final double height = this.getMinWindowHeight();
        final NSRect frameRect = new NSRect(
            new NSPoint(windowFrame.origin.x.doubleValue(), windowFrame.origin.y.doubleValue() + windowFrame.size.height.doubleValue() - height),
            new NSSize(windowFrame.size.width.doubleValue(), height)
        );
        window.setFrame_display_animate(NSWindow.frameRectForContentRect_styleMask(frameRect, window.styleMask()),
            true, window.isVisible());
    }

    public NSSize windowWillResize_toSize(final NSWindow window, final NSSize newSize) {
        // Only allow horizontal sizing
        return new NSSize(newSize.width.doubleValue(), window.frame().size.height.doubleValue());
    }

    private double toolbarHeightForWindow(final NSWindow window) {
        NSRect windowFrame = NSWindow.contentRectForFrameRect_styleMask(window.frame(), window.styleMask());
        return windowFrame.size.height.doubleValue() - window.contentView().frame().size.height.doubleValue();
    }

    @Override
    public void tabView_didSelectTabViewItem(final NSTabView view, final NSTabViewItem item) {
        if(awaked) {
            this.setTitle(this.getTitle(item));
            this.resize();
            preferences.setProperty(String.format("%s.selected", this.getToolbarName()), view.indexOfTabViewItem(item));
        }
    }

    protected void setTitle(final String title) {
        if(window.respondsToSelector(Foundation.selector("setSubtitle:"))) {
            window.setTitle(this.title);
            window.setSubtitle(title);
        }
        else {
            window.setTitle(String.format("%s – %s", this.title, title));
        }
    }

    protected double getMinWindowHeight() {
        NSRect contentRect = this.getContentRect();
        //Border top + toolbar
        return contentRect.size.height.doubleValue()
            + 40 + this.toolbarHeightForWindow(window);
    }

    protected double getMinWindowWidth() {
        NSRect contentRect = this.getContentRect();
        return contentRect.size.width.doubleValue();
    }

    /**
     * @return Minimum size to fit content view of currently selected tab.
     */
    private NSRect getContentRect() {
        NSRect contentRect = new NSRect(0, 0);
        final NSView view = tabView.selectedTabViewItem().view();
        final NSEnumerator enumerator = view.subviews().objectEnumerator();
        NSObject next;
        while(null != (next = enumerator.nextObject())) {
            final NSView subview = Rococoa.cast(next, NSView.class);
            contentRect = FoundationKitFunctions.library.NSUnionRect(contentRect, subview.frame());
        }
        return contentRect;
    }
}
