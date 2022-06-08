/*
 * Copyright (c) 2009--2014 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */

package org.cobbler;

import com.redhat.rhn.common.util.StringUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * Base class has attributes common to distros, profiles, system records.
 *
 * @author paji
 * @see <a href="https://cobbler.readthedocs.io/en/v3.3.3/code-autodoc/cobbler.items.html#module-cobbler.items.item">RTFD - Cobbler - 3.3.3 - Item</a>
 */
public abstract class CobblerObject {
    /**
     * Constant to define the field name for the comment
     */
    protected static final String COMMENT = "comment";
    /**
     * Constant to define the field name for the owners
     */
    protected static final String OWNERS = "owners";
    /**
     * Constant to define the field name for the creation time of the object
     */
    protected static final String CTIME = "ctime";
    /**
     * Constant to define the field name for the getter of kernel post options
     */
    protected static final String KERNEL_OPTIONS_POST = "kernel_options_post";
    /**
     * Constant to define the field name for the setter of the kernel post options
     */
    protected static final String SET_KERNEL_OPTIONS_POST = "kernel_options_post";
    /**
     * Constant to define the field name for the logical object depth in the inheritance
     */
    protected static final String DEPTH = "depth";
    /**
     * Constant to define the field name for the getter of the kernel options
     */
    protected static final String KERNEL_OPTIONS = "kernel_options";
    /**
     * Constant to define the field name for the setter of the kernel options
     */
    protected static final String SET_KERNEL_OPTIONS = "kernel_options";
    /**
     * Constant to define the field name for name of an object
     */
    protected static final String NAME = "name";
    /**
     * Constant to define the field name for the getter of the autoinstallation metadata
     */
    protected static final String KS_META = "autoinstall_meta";
    /**
     * Constant to define the field name for the setter of the autoinstallation metadata
     */
    protected static final String SET_KS_META = "autoinstall_meta";
    /**
     * TODO
     */
    protected static final String PARENT = "parent";
    /**
     * TODO
     */
    protected static final String MTIME = "mtime";
    /**
     * TODO
     */
    protected static final String MGMT_CLASSES = "mgmt_classes";
    /**
     * TODO
     */
    protected static final String TEMPLATE_FILES = "template_files";
    /**
     * TODO
     */
    protected static final String UID = "uid";
    /**
     * TODO
     */
    private static final String REDHAT_KEY = "redhat_management_key";
    /**
     * TODO
     */
    public static final String INHERIT_KEY = "<<inherit>>";

    /**
     * Holds the identifier for the XML-RPC API
     */
    protected String handle;
    /**
     * The map with the raw data that an object has assigned to itself
     */
    protected Map<String, Object> dataMap = new HashMap<>();
    /**
     * The map with the resolved data that is combined from all objects down the inheritance chain
     */
    protected Map<String, Object> dataMapResolved = new HashMap<>();
    /**
     * The connection to the Cobbler server
     */
    protected CobblerConnection client;

    /**
     * Helper method used by all cobbler objects to
     * return a version of themselves by UID
     *
     * @param client     the Cobbler Connection
     * @param id         the UID of the distro/profile/system record
     * @param findMethod the find XML-RPC method, eg: find_distro
     * @return true if the cobbler object was found.
     * @see org.cobbler.Distro#lookupById for example usage.
     */
    protected static Map<String, Object> lookupDataMapById(CobblerConnection client,
                                                           String id, String findMethod) {
        if (id == null) {
            return null;
        }
        List<Map<String, Object>> objects = lookupDataMapsByCriteria(client,
                UID, id, findMethod);
        if (!objects.isEmpty()) {
            return objects.get(0);
        }
        return null;

    }

    /**
     * look up data maps by a certain criteria
     *
     * @param client     the XML-RPC client
     * @param critera    (i.e. uid profile, etc..)
     * @param value      the value of the criteria
     * @param findMethod the find method to use (find_system, find_profile)
     * @return List of maps
     */
    @SuppressWarnings("unchecked")
    protected static List<Map<String, Object>> lookupDataMapsByCriteria(
            CobblerConnection client, String critera, String value, String findMethod) {
        if (value == null) {
            return null;
        }

        Map<String, String> criteria = new HashMap<>();
        criteria.put(critera, value);
        return (List<Map<String, Object>>)
                client.invokeTokenMethod(findMethod, criteria);

    }


    /**
     * Helper method used by all cobbler objects to return a Map of themselves
     * by name.
     *
     * @param client       the Cobbler Connection
     * @param name         the name of the cobbler object
     * @param lookupMethod the name of the XML-RPC
     *                     method to lookup: eg get_profile for profile
     * @return the Cobbler Object Data Map or null
     * @see org.cobbler.Distro#lookupByName for example usage..
     */
    @SuppressWarnings("unchecked")
    protected static Map<String, Object> lookupDataMapByName(CobblerConnection client,
                                                             String name, String lookupMethod) {
        Object obj = client.invokeMethod(lookupMethod, name);
        if ("~".equals(obj)) {
            return null;
        }
        Map<String, Object> map = (Map<String, Object>) obj;
        if (map == null || map.isEmpty()) {
            return null;
        }
        return map;
    }

    /**
     * TODO
     *
     * @param key   TODO
     * @param value TODO
     */
    protected abstract void invokeModify(String key, Object value);

    /**
     * TODO
     *
     * @param key   TODO
     * @param value TODO
     */
    protected abstract void invokeModifyResolved(String key, Object value);

    /**
     * TODO
     */
    protected abstract void invokeSave();

    /**
     * TODO
     *
     * @return TODO
     */
    protected abstract boolean invokeRemove();

    /**
     * TODO
     *
     * @return TODO
     */
    protected abstract String invokeGetHandle();

    /**
     * TODO
     */
    protected abstract void reload();

    /**
     * TODO
     *
     * @param newName TODO
     */
    protected abstract void invokeRename(String newName);

    /**
     * TODO
     *
     * @param key TODO
     * @return TODO
     */
    protected final Object getResolvedValue(String key) {
        return client.invokeTokenMethod("get_item_resolved_value", getUid(), key);
    }

    /**
     * Gets the XML-RPC handle internal to Cobbler
     *
     * @return The handle for Cobbler. If the Item is not saved to disk it will be prefixed with {@code ___NEW___}.
     */
    protected String getHandle() {
        if (isBlank(handle)) {
            handle = invokeGetHandle();
        }
        return handle;
    }

    /**
     * TODO
     *
     * @param key   TODO
     * @param value TODO
     */
    protected void modify(String key, Object value) {
        invokeModify(key, value);
        dataMap.put(key, value);
    }

    /**
     * TODO
     *
     * @param key   TODO
     * @param value TODO
     */
    protected void modifyResolved(String key, Object value) {
        invokeModifyResolved(key, value);
        dataMapResolved.put(key, value);
    }

    /**
     * Calls save object to complete the commit
     */
    public void save() {
        invokeSave();
    }

    /**
     * Removes the kickstart object from cobbler.
     *
     * @return true if successful
     */
    public boolean remove() {
        return invokeRemove();
    }


    /**
     * TODO
     *
     * @return the comment
     */
    public String getComment() {
        return (String) dataMap.get(COMMENT);
    }


    /**
     * TODO
     *
     * @param commentIn the comment to set
     */
    public void setComment(String commentIn) {
        modify(COMMENT, commentIn);
    }

    /**
     * TODO
     *
     * @return the managementClasses
     */
    @SuppressWarnings("unchecked")
    public Optional<List<String>> getManagementClasses() {
        if (String.valueOf(dataMap.get(MGMT_CLASSES)).equals(INHERIT_KEY)) {
            return Optional.empty();
        }
        return Optional.of((List<String>) dataMap.get(MGMT_CLASSES));
    }

    /**
     * TODO
     *
     * @return the managementClasses
     */
    @SuppressWarnings("unchecked")
    public List<String> getResolvedManagementClasses() {
        return (List<String>) dataMapResolved.get(MGMT_CLASSES);
    }

    /**
     * TODO
     *
     * @param managementClassesIn the managementClasses to set
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void setManagementClasses(Optional<List<String>> managementClassesIn) {
        if (managementClassesIn.isEmpty()) {
            modify(MGMT_CLASSES, INHERIT_KEY);
            return;
        }
        modify(MGMT_CLASSES, managementClassesIn);
    }

    /**
     * TODO
     *
     * @param managementClassesIn TODO
     */
    public void setResolvedManagementClasses(List<String> managementClassesIn) {
        modifyResolved(MGMT_CLASSES, managementClassesIn);
    }


    /**
     * TODO
     *
     * @return the templateFiles
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getTemplateFiles() {
        return (Map<String, String>) dataMap.get(TEMPLATE_FILES);
    }


    /**
     * TODO
     *
     * @param templateFilesIn the templateFiles to set
     */
    public void setTemplateFiles(Map<String, String> templateFilesIn) {
        modify(TEMPLATE_FILES, templateFilesIn);
    }


    /**
     * TODO
     *
     * @return the uid
     */
    public String getUid() {
        return (String) dataMap.get(UID);
    }

    /**
     * TODO
     *
     * @return the uid
     */
    public String getId() {
        return getUid();
    }

    /**
     * TODO
     *
     * @param uidIn the uid to set
     */
    public void setUid(String uidIn) {
        modify(UID, uidIn);
    }


    /**
     * TODO
     *
     * @return the parent
     */
    public String getParent() {
        return (String) dataMap.get(PARENT);
    }


    /**
     * TODO
     *
     * @param parentIn the parent to set
     */
    public void setParent(String parentIn) {
        modify(PARENT, parentIn);
    }

    /**
     * TODO
     *
     * @return the owners
     */
    @SuppressWarnings("unchecked")
    public List<String> getOwners() {
        return (List<String>) dataMap.get(OWNERS);
    }

    /**
     * TODO
     *
     * @return the owners
     */
    @SuppressWarnings("unchecked")
    public List<String> getResolvedOwners() {
        return (List<String>) dataMapResolved.get(OWNERS);
    }

    /**
     * TODO
     *
     * @param ownersIn the owners to set
     */
    public void setOwners(List<String> ownersIn) {
        modify(OWNERS, ownersIn);
    }

    /**
     * TODO
     *
     * @param ownersIn the owners to set
     */
    public void setResolvedOwners(List<String> ownersIn) {
        modifyResolved(OWNERS, ownersIn);
    }

    /**
     * TODO
     *
     * @return the created
     */
    public Date getCreated() {
        Double time = (Double) dataMap.get(CTIME);
        // cobbler deals with seconds since epoch, Date expects milliseconds. Convert.
        return new Date(time.longValue() * 1000);
    }

    /**
     * TODO
     *
     * @param createdIn the created to set
     */
    public void setCreated(Date createdIn) {
        // cobbler deals with seconds since epoch, Date returns milliseconds. Convert.
        modify(CTIME, createdIn.getTime() / 1000);
    }

    /**
     * TODO
     *
     * @return the modified
     */
    public Date getModified() {
        Double time = (Double) dataMap.get(MTIME);
        // cobbler deals with seconds since epoch, Date expects milliseconds. Convert.
        return new Date(time.longValue() * 1000);
    }

    /**
     * TODO
     *
     * @param modifiedIn the modified to set
     */
    public void setModified(Date modifiedIn) {
        // cobbler deals with seconds since epoch, Date returns milliseconds. Convert.
        modify(MTIME, modifiedIn.getTime() / 1000);
    }

    /**
     * TODO
     *
     * @return the depth
     */
    public int getDepth() {
        return (Integer) dataMap.get(DEPTH);
    }

    /**
     * TODO
     *
     * @param depthIn the depth to set
     */
    public void setDepth(int depthIn) {
        modify(DEPTH, depthIn);
    }


    /**
     * TODO
     *
     * @return the kernelOptions
     */
    @SuppressWarnings("unchecked")
    public String getKernelOptions() {
        Object kernelOpts = dataMap.get(KERNEL_OPTIONS);
        if (kernelOpts instanceof Map) {
            return convertOptionsMap((Map<String, Object>) kernelOpts);
        }
        return (String) kernelOpts;
    }

    /**
     * Gets resolved kernel options as a dictionary
     * <p>
     * The resolved value includes all the options inherited from above.
     *
     * @return the kernel option map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getResolvedKernelOptions() {
        return (Map<String, Object>) getResolvedValue(KERNEL_OPTIONS);
    }

    /**
     * TODO
     *
     * @return the kernelOptionsPost
     */
    @SuppressWarnings("unchecked")
    public String getKernelOptionsPost() {
        Object kernelOptsPost = dataMap.get(KERNEL_OPTIONS_POST);
        if (kernelOptsPost instanceof Map) {
            return convertOptionsMap((Map<String, Object>) kernelOptsPost);
        }
        return (String) kernelOptsPost;
    }

    /**
     * Gets resolved kernel post options as a dictionary
     * <p>
     * The resolved value includes all the options inherited from above.
     *
     * @return the kernel post option map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getResolvedKernelOptionsPost() {
        return (Map<String, Object>) getResolvedValue(KERNEL_OPTIONS_POST);
    }

    /**
     * TODO
     *
     * @param map TODO
     * @return TODO
     */
    @SuppressWarnings("unchecked")
    private String convertOptionsMap(Map<String, Object> map) {
        StringBuilder string = new StringBuilder();
        for (String key : map.keySet()) {
            List<String> keyList;
            try {
                keyList = (List<String>) map.get(key);
            }
            catch (ClassCastException e) {
                keyList = new ArrayList<>();
                keyList.add((String) map.get(key));
            }
            if (keyList.isEmpty()) {
                string.append(key + " ");
            }
            else {
                for (String value : keyList) {
                    string.append(key + "=" + value + " ");
                }
            }
        }
        return string.toString();
    }


    /**
     * TODO
     *
     * @param kernelOptionsIn the kernelOptions to set
     */
    public void setKernelOptions(String kernelOptionsIn) {
        modify(SET_KERNEL_OPTIONS, kernelOptionsIn);
    }

    /**
     * TODO
     *
     * @param kernelOptionsIn the kernelOptions to set in the form of a map
     */
    public void setKernelOptions(Map<String, Object> kernelOptionsIn) {
        setKernelOptions(convertOptionsMap(kernelOptionsIn));
    }

    /**
     * TODO
     *
     * @param kernelOptionsPostIn the kernelOptionsPost to set
     */
    public void setKernelOptionsPost(String kernelOptionsPostIn) {
        modify(SET_KERNEL_OPTIONS_POST, kernelOptionsPostIn);
    }

    /**
     * TODO
     *
     * @param kernelOptionsPostIn the kernelOptionsPost to set in the form of a map
     */
    public void setKernelOptionsPost(Map<String, Object> kernelOptionsPostIn) {
        setKernelOptionsPost(convertOptionsMap(kernelOptionsPostIn));
    }

    /**
     * TODO
     *
     * @return the kernelMeta
     * @cobbler.inheritable P
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getKsMeta() {
        return (Map<String, Object>) dataMap.get(KS_META);
    }

    /**
     * TODO
     *
     * @return the kernelMeta
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getResolvedAutoinstallMeta() {
        return (Map<String, Object>) dataMap.get(KS_META);
    }

    /**
     * Setter that modifies the autoinstall meta field for the object with a raw value
     *
     * @param kernelMetaIn the kernelMeta to set
     */
    public void setKsMeta(Map<String, ?> kernelMetaIn) {
        modify(SET_KS_META, kernelMetaIn);
    }

    /**
     * Setter that modifies the autoinstall meta field for the object with a resolved value
     *
     * @param kernelMetaIn the kernelMeta to set
     */
    public void setResolvedAutoinstallMeta(Map<String, ?> kernelMetaIn) {
        modify(SET_KS_META, kernelMetaIn);
    }

    /**
     * Getter for the name property of a Cobbler object
     *
     * @return the name
     */
    public String getName() {
        return (String) dataMap.get(NAME);
    }

    /**
     * Setter for the name property of a Cobbler object
     *
     * @param nameIn sets the new name
     */
    public void setName(String nameIn) {
        invokeRename(nameIn);
        dataMap.put(NAME, nameIn);
        handle = null;
        handle = getHandle();
        reload();
    }

    /**
     * Helper method to check if a string is blank or not
     *
     * @param str The String to check.
     * @return True if after trimming the String is of zero length. If instead of a String null was passed this method
     * will also return True. All other cases return False.
     */
    protected boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DataMap = " + dataMap;
    }

    /**
     * Setter for the Red Hat management key with a String
     *
     * @param key the Red Hat activation key
     * @see #getRedHatManagementKey()
     */
    public void setRedHatManagementKey(String key) {
        modify(REDHAT_KEY, key);
    }

    /**
     * Setter for the Red Hat management key with a Set of Strings that are comma delimited
     *
     * @param keys the Red Hat activation keys in a set
     * @see #getRedHatManagementKey()
     */
    public void setRedHatManagementKey(Set<String> keys) {
        modify(REDHAT_KEY, StringUtils.defaultString(StringUtil.join(",", keys)));
    }

    /**
     * Get the Red Hat management key
     * <p>
     * This is used in the context of a
     * {@link com.redhat.rhn.domain.kickstart.KickstartSession} to represent
     * the currently attempted installation. The data is stored as a comma
     * separated string in Cobbler.
     *
     * @return returns the red hat key(s) as a string
     */
    public String getRedHatManagementKey() {
        return (String) dataMap.get(REDHAT_KEY);
    }

    /**
     * Get the Red Hat management key as a Set of keys
     *
     * @return returns the red hat key as a string
     * @see #getRedHatManagementKey()
     */
    public Set<String> getRedHatManagementKeySet() {
        String keys = StringUtils.defaultString(getRedHatManagementKey());
        String[] sets = (keys).split(",");
        return new HashSet<>(Arrays.asList(sets));
    }

    /**
     * Remove the specified keys from the key set and add the specified set
     *
     * @param keysToRemove list of tokens to remove
     * @param keysToAdd    list of tokens to add
     * @see #getRedHatManagementKey()
     */
    public void syncRedHatManagementKeys(Collection<String> keysToRemove,
                                         Collection<String> keysToAdd) {
        Set<String> keySet = getRedHatManagementKeySet();
        keySet.removeAll(keysToRemove);
        keySet.addAll(keysToAdd);
        if (keySet.size() > 1 && keySet.contains(INHERIT_KEY)) {
            keySet.remove(INHERIT_KEY);
        }
        else if (keySet.isEmpty()) {
            keySet.add(INHERIT_KEY);
        }
        setRedHatManagementKey(keySet);
    }

}
