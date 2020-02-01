package org.mhdvsolutions.zipties.api;

public enum ReleaseType {

    /**
     * When the prisoner is released from custody via command or restrainer
     */
    RELEASE,

    /**
     * When the prisoner is broken out of their restraints
     */
    ESCAPE,

    /**
     * When other factors cause the release, such as restarts
     */
    OTHER

}
