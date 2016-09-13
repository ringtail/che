/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.machine.client.machine;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.core.model.machine.MachineConfig;
import org.eclipse.che.api.core.model.machine.MachineRuntimeInfo;
import org.eclipse.che.api.core.model.machine.MachineSource;
import org.eclipse.che.api.core.model.machine.MachineStatus;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.api.machine.shared.dto.MachineDto;
import org.eclipse.che.api.machine.shared.dto.MachineRuntimeInfoDto;
import org.eclipse.che.api.machine.shared.dto.ServerDto;
import org.eclipse.che.ide.api.machine.MachineEntity;
import org.eclipse.che.ide.extension.machine.client.MachineLocalizationConstant;
import org.eclipse.che.ide.extension.machine.client.inject.factories.EntityFactory;
import org.eclipse.che.ide.extension.machine.client.perspective.widgets.machine.appliance.server.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.eclipse.che.api.machine.shared.Constants.TERMINAL_REFERENCE;

/**
 * The class which describes machine entity. The class is wrapper of MachineDescriptor.
 *
 * @author Dmitry Shnurenko
 */
public class Machine implements MachineEntity {

    private final MachineDto    descriptor;
    private final EntityFactory entityFactory;
    private final List<Link>    machineLinks;
    private final MachineConfig machineConfig;

    private String activeTabName;

    @Inject
    public Machine(MachineLocalizationConstant locale,
                   EntityFactory entityFactory,
                   @Assisted MachineDto descriptor) {
        this.entityFactory = entityFactory;
        this.descriptor = descriptor;
        this.machineLinks = descriptor.getLinks();
        this.activeTabName = locale.tabInfo();
        this.machineConfig = descriptor.getConfig();
    }

    @Override
    public MachineConfig getConfig() {
        return machineConfig;
    }

    /** @return id of current machine */
    @Override
    public String getId() {
        return descriptor.getId();
    }

    @Override
    public String getWorkspaceId() {
        return descriptor.getWorkspaceId();
    }

    @Override
    public String getEnvName() {
        return descriptor.getEnvName();
    }

    @Override
    public String getOwner() {
        return descriptor.getOwner();
    }

    @Override
    public MachineStatus getStatus() {
        return descriptor.getStatus();
    }

    @Override
    public MachineRuntimeInfo getRuntime() {
        return descriptor.getRuntime();
    }

    /**
     * Returns boolean which defines bounding workspace to current machine
     *
     * @return <code>true</code> machine is bounded to workspace,<code>false</code> machine isn't bounded to workspace
     */
    public boolean isDev() {
        return machineConfig.isDev();
    }

    /** @return type of current machine */
    public String getType() {
        return machineConfig.getType();
    }

    /** @return current machine's display name */
    public String getDisplayName() {
        return machineConfig.getName();
    }

    /** Returns information about machine. */
    public Map<String, String> getProperties() {
        MachineRuntimeInfo machineRuntime = descriptor.getRuntime();
        return machineRuntime != null ? machineRuntime.getProperties() : null;
    }

    public String getTerminalUrl() {
        for (Link link : machineLinks) {
            if (TERMINAL_REFERENCE.equals(link.getRel())) {
                return link.getHref();
            }
        }
        return "";
    }

    public List<Server> getServersList() {
        List<Server> serversList = new ArrayList<>();

        MachineRuntimeInfoDto machineRuntime = descriptor.getRuntime();
        if (machineRuntime == null) {
            return serversList;
        }

        Map<String, ServerDto> servers = machineRuntime.getServers();
        for (Map.Entry<String, ServerDto> entry : servers.entrySet()) {
            String exposedPort = entry.getKey();
            ServerDto descriptor = entry.getValue();

            Server server = entityFactory.createServer(exposedPort, descriptor);

            serversList.add(server);
        }

        return serversList;
    }

    /** @return location of machine recipe */
    public String getRecipeLocation() {
        MachineSource machineSource = machineConfig.getSource();
        return machineSource.getLocation();
    }

    /** @return content of machine recipe */
    public String getRecipeContent() {
        MachineSource machineSource = machineConfig.getSource();
        return machineSource.getContent();
    }

    /** @return type of machine recipe */
    public String getRecipeType() {
        MachineSource machineSource = machineConfig.getSource();
        return machineSource.getType();
    }

    public void setActiveTabName(String activeTabName) {
        this.activeTabName = activeTabName;
    }

    public String getActiveTabName() {
        return activeTabName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Machine otherMachine = (Machine)other;

        return Objects.equals(getId(), otherMachine.getId());

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
