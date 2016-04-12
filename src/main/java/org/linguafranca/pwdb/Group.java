/*
 * Copyright 2015 Jo Rabin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.linguafranca.pwdb;

import java.util.List;
import java.util.UUID;

/**
 * Interface for a Database Group. Databases have exactly one Root Group.
 * Further Groups of the database are help as sub-Groups of other Groups.
 * A Database may contain Groups to an indefinite level.
 *
 * <p>Any Group may contain {@link Entry} items.
 *
 * <p>Groups have a name, a UUID and an Icon.
 *
 * @author Jo
 */
public interface Group {
    /**
     * Returns true if this is the root group of a database
     */
    boolean isRootGroup();

    /**
     * Returns the parent of this group, or null if either this
     * group is the root group, or if the group does not have
     * a parent - e.g. if it is newly created or if it has
     * been removed from a previous parent.
      */
    Group getParent();

    /**
     * Set parent is equivalent to <code>parent.addGroup(this)</code>
     *
     * @param parent a prospective parent
     */
    void setParent(Group parent);

    /**
     * Returns a list of groups that are the children of this group.
     *
     * <p>The list returned is modifiable by the caller without affecting the status of the database.
     *
     * @return a modifiable list of groups
     */
    List<Group> getGroups();

    /**
     * Returns the number of groups that are direct children of this group
     */
    int getGroupsCount();

    /**
     * Remove the group from its current parent, if any and add it to this gorup.
     *
     * <p>The root group cannot be added to another group.
     *
     * @param group the group to add
     * @return the group added
     * @throws IllegalArgumentException if the group is not part of this database
     */
    Group addGroup(Group group);

    /**
     * Returns a list of child Groups whose name exactly matches that supplied.
     *
     * <p>The returned list may be modified without affecting the underlying database.
     *
     * @param groupName the name of the groups sought
     * @return a modifiable list
     */
    List<Group> findGroups(String groupName);

    /**
     * Removes the group supplied from this group. The group removed
     * no longer has a parent and so in not part of the database any more unless
     * it is re-added to another group.
     *
     * @param group the group to remove
     * @return the group removed
     * @throws IllegalArgumentException if the group is not a child of this group
     */
    Group removeGroup(Group group);

    /**
     * Returns a modifiable by the caller list of entries contained in this group.
     */
    List<Entry> getEntries();

    /**
     * Returns the number of entries in this group
     */
    int getEntriesCount();

    /**
     * Finds all entries in this group that match the string supplied.
     * Optionally in subgroups as well.
     *
     * <p>Entry match is described under {@link Entry#match(String)}
     *
     * @param match the text to match
     * @param recursive whether to include sub groups in the process
     * @return a modifiable-by-caller list
     * @see Entry#match(String)
     */
    List<Entry> findEntries(String match, boolean recursive);

    /**
     * Finds all entries in this group that match using the matcher supplied.
     * Optionally in subgroups as well.
     *
     * <p>Entry match is described under {@link Entry#match(String)}
     *
     * @param matcher the mathcher to use
     * @param recursive whether to include sub groups in the process
     * @return a modifiable-by-caller list
     * @see Entry#match(String)
     */
    List<Entry> findEntries(Entry.Matcher matcher, boolean recursive);

    /**
     * Adds an entry to this group removing it from another group
     * if it was part of one.
     * @param entry the entry to add
     * @return the entry added
     */
    Entry addEntry(Entry entry);

    /**
     * Remove an entry from this group and hence from the database.
     * @param entry the entry to remove
     * @return the entry removed
     * @throws IllegalArgumentException if the Entry is not in the group
     */
    Entry removeEntry(Entry entry);

    /**
     * Returns an XPath-like string of the names of Groups from the Root
     * to this Group.
     */
    String getPath();

    String getName();

    void setName(String name);

    UUID getUuid();

    Icon getIcon();

    void setIcon(Icon icon);
}
